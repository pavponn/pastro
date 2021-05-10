package com.github.pavponn.transaction

import com.github.pavponn.utils.Certificate
import com.github.pavponn.utils.SignedTransaction

/**
 * @author pavponn
 * File contains useful functions while working with transactions.
 */

/**
 * Stub implementation for verifying sign
 */
fun verifySender(transaction: Transaction, certificate: Certificate): Boolean {
    return try {
        Integer.parseInt(certificate) == transaction.spenderId
    } catch (e: Exception) {
        false
    }

}

/**
 * Stub implementation for signing the transaction.
 */
fun signTransaction(transaction: Transaction): Certificate {
    return "${transaction.spenderId}"
}

/**
 * Checks whether all the dependencies of a transaction [tx] are in the set of [otherTransactions].
 */
fun allDependencies(tx: Transaction, otherTransactions: Collection<Transaction>): Boolean {
    return tx.dependencies.all {
        otherTransactions.any { other ->
            it.first == other.spenderId && it.second == other.transactionId
        }
    }
}

/**
 * Checks whether transaction [tx] reference itself.
 */
fun referenceItself(tx: Transaction): Boolean {
    return tx.dependencies.any { it.first == tx.spenderId && it.second == tx.spenderId }
}

fun isValid(tx: Transaction, otherTransactions: Collection<Transaction>): Boolean {
    TODO("To be implemented")
}

/**
 * Converts set of signed transactions to set of transactions.
 */
fun signedTransactionsToTransactions(signedTransactions: Collection<SignedTransaction>): Set<Transaction> {
    return signedTransactions.map { it.first }.toSet()
}

/**
 * Checks whether two transaction conflict.
 * Returns true if they conflict, false otherwise.
 */
fun conflict(tx1: Transaction, tx2: Transaction): Boolean {
    return tx1 != tx2 &&
            tx1.spenderId == tx2.spenderId && (
            tx1.transactionId == tx2.transactionId ||
                    tx1.dependencies.intersect(tx2.dependencies).isNotEmpty()
            )
}

/**
 * Checks whether transaction conflicts with the some of transaction in given collection.
 */
fun noConflictsWith(transaction: Transaction, otherTransactions: Collection<Transaction>): Boolean {
    return otherTransactions.none { conflict(it, transaction) }
}