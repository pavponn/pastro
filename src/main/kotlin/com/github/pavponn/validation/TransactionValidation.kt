package com.github.pavponn.validation

import com.github.pavponn.message.validation.TVMessage
import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.Certificate
import com.github.pavponn.utils.ProcessId
import com.github.pavponn.utils.SignedTransaction

/**
 * @author pavponn
 */
interface TransactionValidation {

    /**
     * Initiate validation of a given transaction in the system.
     */
    fun validate(transaction: Transaction, certificate: Certificate)

    /**
     * Handler that should be executed when message is received.
     */
    fun onMessage(message: TVMessage, from: ProcessId)

    /**
     * Adds a callback to the list of the callbacks that are executed when the validation result is obtained.
     */
    fun onResult(listener: (Transaction, ValidationCertificate) -> Unit)

    /**
     * Verifies senders of a given collection of verifiable transactions.
     */
    fun verifySenders(signedTransactions: Collection<SignedTransaction>): Boolean
}

typealias ValidationCertificate = String
