package com.github.pavponn

import akka.actor.ActorRef
import akka.actor.ActorSystem
import com.github.pavponn.configuration.PastroConfiguration
import com.github.pavponn.history.PastroHistory
import com.github.pavponn.holder.PastroHistoryHolder
import com.github.pavponn.message.TransferMessage
import com.github.pavponn.message.settings.EnvironmentMessage
import com.github.pavponn.message.settings.HolderMessage
import com.github.pavponn.pastro.PastroProcess
import com.github.pavponn.transaction.Transaction
import com.github.pavponn.transaction.signTransaction
import com.github.pavponn.utils.DefaultValues
import com.github.pavponn.utils.DefaultValues.DEFAULT_CERTIFICATE
import com.github.pavponn.utils.ProcessId
import kotlin.random.Random

/**
 * @author pavponn
 */
fun main() {
    val nProcesses = 10
    val system: ActorSystem = ActorSystem.create("system")
    val processes: MutableList<ActorRef> = mutableListOf()

    val initTransaction = createInitTransaction(nProcesses)
    val initConfiguration = PastroConfiguration(setOf(initTransaction))
    val initHistory = PastroHistory(setOf(initConfiguration))
    val historyHolder = PastroHistoryHolder(initHistory, DEFAULT_CERTIFICATE)

    val transactions = createTransactionsForProcesses(initTransaction)

    println("Init distribution")
    println("== Total stake ${initConfiguration.getTotalStake()}")
    initTransaction.transfer.forEach {
        println("==== to: ${it.key}, amount: ${it.value}")
    }

    // create processes
    IntRange(1, nProcesses).forEach {
        processes.add(system.actorOf(PastroProcess.createActor(), "p$it"))
    }

    // send processes information about their initial environment
    IntRange(1, nProcesses).forEach {
        processes[it - 1].tell(EnvironmentMessage(it, processes.toTypedArray()), ActorRef.noSender())
    }

    // send processes history holders
    IntRange(1, nProcesses).forEach {
        processes[it - 1].tell(HolderMessage(historyHolder), ActorRef.noSender())
    }

    // start modelling
    IntRange(1, nProcesses).forEach {
        val transaction = transactions[it - 1]
        val certificate = signTransaction(transaction)
        processes[it - 1].tell(TransferMessage(transaction, certificate), ActorRef.noSender())
    }
}


fun createInitTransaction(n: Int): Transaction {
    val map: MutableMap<ProcessId, Int> = mutableMapOf()
    IntRange(1, n).forEach {
        val sum = Random.nextInt(1, n) * Random.nextInt(1, it * 100)
        map[it] = sum
    }

    return Transaction(
        DefaultValues.INIT_TRANSACTION_ID,
        DefaultValues.INIT_SENDER,
        map,
        emptySet()
    )

}

fun createTransactionsForProcesses(initialTransaction: Transaction): List<Transaction> {
    val transactions: MutableList<Transaction> = mutableListOf()
    initialTransaction.transfer.entries.forEach {
        val to = Random.nextInt(1, initialTransaction.transfer.size + 1)
        val transaction = Transaction(
            1,
            it.key,
            mapOf(to to it.value),
            setOf(initialTransaction.spenderId to initialTransaction.transactionId)
        )
        transactions.add(transaction)
    }

    return transactions
}
