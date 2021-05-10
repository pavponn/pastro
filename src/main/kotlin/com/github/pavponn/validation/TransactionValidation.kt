package com.github.pavponn.validation

import com.github.pavponn.message.TVMessage
import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.Certificate
import com.github.pavponn.utils.ProcessId
import com.github.pavponn.utils.Signature
import com.github.pavponn.utils.SignedTransaction

/**
 * @author pavponn
 */
interface TransactionValidation {

    fun validate(transaction: Transaction, certificate: Certificate)

    fun onMessage(message: TVMessage, from: ProcessId)

    fun getResult(): Pair<Transaction, ValidationCertificate>?

    fun onResult(listener: (Pair<Transaction, ValidationCertificate>) -> Unit)

    fun verifySenders(signedTransactions: Collection<SignedTransaction>): Boolean
}

typealias ValidationCertificate = Set<Pair<Signature, ProcessId>>