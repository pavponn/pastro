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
import com.github.pavponn.transaction.signTransaction
import com.github.pavponn.utils.DefaultValues.DEFAULT_CERTIFICATE
import com.github.pavponn.utils.createInitTransaction
import com.github.pavponn.utils.createTransactions
import com.github.pavponn.utils.printInitStakeDistribution

/**
 * @author pavponn
 */
fun main(args: Array<String>) {
    val nProcesses = 10
    val system: ActorSystem = ActorSystem.create("systemWithOnlyCorrectProcesses")
    val processes: MutableList<ActorRef> = mutableListOf()

    val initTransaction = createInitTransaction(nProcesses)
    val initConfiguration = PastroConfiguration(setOf(initTransaction))
    val initHistory = PastroHistory(setOf(initConfiguration))
    val historyHolder = PastroHistoryHolder(initHistory, DEFAULT_CERTIFICATE)

    val transactions = createTransactions(initTransaction)

    printInitStakeDistribution(initConfiguration)

    // create processes
    IntRange(1, nProcesses).forEach {
        processes.add(system.actorOf(PastroProcess.createActor(), "p$it"))
    }

    val checkerActor = system.actorOf(CheckerActor.createActor(), "checker")

    // send processes information about their initial environment
    IntRange(1, nProcesses).forEach {
        processes[it - 1].tell(EnvironmentMessage(it, processes.toTypedArray()), ActorRef.noSender())
    }

    // send processes history holders
    IntRange(1, nProcesses).forEach {
        processes[it - 1].tell(HolderMessage(historyHolder), ActorRef.noSender())
    }

    checkerActor.tell(HolderMessage(historyHolder), ActorRef.noSender())

    IntRange(1, nProcesses).forEach {
        processes[it - 1].tell(CheckerRefMessage(checkerActor), ActorRef.noSender())
    }

    Thread.sleep(20000)

    // start modelling
    IntRange(1, nProcesses).forEach {
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



