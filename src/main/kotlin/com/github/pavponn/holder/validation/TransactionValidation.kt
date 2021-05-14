package com.github.pavponn.holder.validation

import com.github.pavponn.message.validation.TVMessage
import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.Certificate
import com.github.pavponn.utils.ProcessId
import com.github.pavponn.utils.SignedTransaction

/**
 * @author pavponn
 */
interface TransactionValidation {

    fun validate(transaction: Transaction, certificate: Certificate)

    fun onMessage(message: TVMessage, from: ProcessId)

    fun onResult(listener: (Transaction, ValidationCertificate) -> Unit)

    fun verifySenders(signedTransactions: Collection<SignedTransaction>): Boolean
}

typealias ValidationCertificate = String
