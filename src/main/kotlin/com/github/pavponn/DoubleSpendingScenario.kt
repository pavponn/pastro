package com.github.pavponn

import akka.actor.ActorRef
import akka.actor.ActorSystem
import com.github.pavponn.configuration.PastroConfiguration
import com.github.pavponn.history.PastroHistory
import com.github.pavponn.holder.PastroHistoryHolder
import com.github.pavponn.message.TransferMessage
import com.github.pavponn.message.checker.CheckConfigurationsMessage
import com.github.pavponn.message.checker.CheckHistoriesMessage
import com.github.pavponn.message.checker.CheckTransactionsMessage
import com.github.pavponn.message.settings.CheckerRefMessage
import com.github.pavponn.message.settings.EnvironmentMessage
import com.github.pavponn.message.settings.HolderMessage
import com.github.pavponn.pastro.CheckerActor
import com.github.pavponn.pastro.PastroProcess
import com.github.pavponn.transaction.Transaction
import com.github.pavponn.transaction.signTransaction
import com.github.pavponn.utils.*
import com.github.pavponn.utils.DefaultValues.DEFAULT_CERTIFICATE
import kotlin.random.Random

/**
 * @author pavponn
 */
fun main(args: Array<String>) {
    var nProcesses = 5
    var maliciousProcesses = 2
    if (args.isNotEmpty()) {
        try {
            nProcesses = Integer.parseUnsignedInt(args[0])
        } catch (e: NumberFormatException) {
            println("Invalid number format: ${args[0]} is not a positive integer")
            return
        }
        if (args.size > 1) {
            try {
                maliciousProcesses = Integer.parseUnsignedInt(args[1])
            } catch (e: NumberFormatException) {
                println("Invalid number format: ${args[1]} is not a positive integer")
                return
            }
        }
    }

    val system: ActorSystem = ActorSystem.create("systemWithDoubleSpendingAttempt")
    val processes: MutableList<ActorRef> = mutableListOf()

    val initTransaction = createInitTransaction(nProcesses, maliciousProcesses)
    val initConfiguration = PastroConfiguration(setOf(initTransaction))
    val initHistory = PastroHistory(setOf(initConfiguration))
    val historyHolder = PastroHistoryHolder(initHistory, DEFAULT_CERTIFICATE)

    val allTransactions = createTransactions(initTransaction)
    val correctTransactions = allTransactions.subList(0, nProcesses)
    val conflictingTransactions = allTransactions
        .subList(nProcesses, allTransactions.size).flatMap {
            val entries = it.transfer.entries
            val map: MutableMap<ProcessId, Int> = mutableMapOf()
            entries.forEach {
                val to = Random.nextInt(1, initTransaction.transfer.size + 1)
                map[to] = it.value
            }
            val other = Transaction(
                1,
                it.spenderId,
                map,
                it.dependencies
            )
            listOf(it, other)
        }.toList()

    val transactions = mutableListOf<Transaction>()
    transactions.addAll(correctTransactions)
    transactions.addAll(conflictingTransactions)


    // emulate double spending with two actors per one malicious process
    val emulationNumberOfMaliciousProcesses = maliciousProcesses * 2

    printInitStakeDistribution(initConfiguration)

    // create correct processes
    IntRange(1, nProcesses).forEach {
        processes.add(system.actorOf(PastroProcess.createActor(), "p$it"))
    }

    // create processes that try to double spend
    IntRange(1, emulationNumberOfMaliciousProcesses).forEach {
        processes.add(system.actorOf(PastroProcess.createActor(), "p${nProcesses + it}"))
    }


    val checkerActor = system.actorOf(CheckerActor.createActor(), "checker")

    // send processes information about their initial environment
    IntRange(1, nProcesses + emulationNumberOfMaliciousProcesses).forEach {
        processes[it - 1].tell(EnvironmentMessage(it, processes.toTypedArray()), ActorRef.noSender())
    }

    // send processes history holders
    IntRange(1, nProcesses + emulationNumberOfMaliciousProcesses).forEach {
        processes[it - 1].tell(HolderMessage(historyHolder), ActorRef.noSender())
    }

    checkerActor.tell(HolderMessage(historyHolder), ActorRef.noSender())

    IntRange(1, nProcesses + emulationNumberOfMaliciousProcesses).forEach {
        processes[it - 1].tell(CheckerRefMessage(checkerActor), ActorRef.noSender())
    }

    Thread.sleep(20000)

    // start modelling
    IntRange(1, nProcesses + emulationNumberOfMaliciousProcesses).forEach {
        val transaction = transactions[it - 1]
        val certificate = signTransaction(transaction)
        processes[it - 1].tell(TransferMessage(transaction, certificate), ActorRef.noSender())
    }

    // wait before checking
    Thread.sleep(20000)

    // check produced results
    checkerActor.tell(CheckTransactionsMessage(), ActorRef.noSender())
    checkerActor.tell(CheckConfigurationsMessage(), ActorRef.noSender())
    checkerActor.tell(CheckHistoriesMessage(), ActorRef.noSender())

    Thread.sleep(10000)
    system.terminate()
}
