package com.github.pavponn.utils

import com.github.pavponn.configuration.Configuration
import com.github.pavponn.transaction.Transaction
import kotlin.random.Random

fun createInitTransaction(n: Int, malicious: Int = 0): Transaction {
    val map: MutableMap<ProcessId, Int> = mutableMapOf()
    IntRange(1, n).forEach {
        val sum = Random.nextInt(1, n) * Random.nextInt(1, it * 100)
        map[it] = sum
    }

    if (malicious > 0) {
        IntRange(n + 1, n + malicious).forEach {
            val sum = Random.nextInt(n / 2, n)
            map[it] = sum
        }
    }

    return Transaction(
        DefaultValues.INIT_TRANSACTION_ID,
        DefaultValues.INIT_SENDER,
        map,
        emptySet()
    )
}


fun createTransactions(initialTransaction: Transaction): List<Transaction> {
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


fun printInitStakeDistribution(initConfiguration: Configuration) {
    println("Init distribution")
    println("== Total stake ${initConfiguration.getTotalStake()}")
    initConfiguration.getTransactions().toList()[0].transfer.forEach {
        println("==== to: ${it.key}, amount: ${it.value}")
    }
}
