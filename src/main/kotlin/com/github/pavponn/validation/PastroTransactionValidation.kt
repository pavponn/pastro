package com.github.pavponn.validation

import com.github.pavponn.environment.Environment
import com.github.pavponn.message.TVMessage
import com.github.pavponn.message.UnknownMessageException
import com.github.pavponn.message.ValidateRequest
import com.github.pavponn.message.ValidateResponse
import com.github.pavponn.pastro.HistoryHolder
import com.github.pavponn.transaction.Transaction
import com.github.pavponn.transaction.verifySender
import com.github.pavponn.utils.Certificate
import com.github.pavponn.utils.ProcessId
import com.github.pavponn.utils.SignedTransaction


/**
 * @author pavponn
 */
class PastroTransactionValidation(
    private val historyHolder: HistoryHolder,
    private val environment: Environment
) : TransactionValidation {

    enum class Status {
        Requesting,
        Inactive
    }

    private var seqNum = 0
    private var status: Status = Status.Inactive

    private val acks: Set<Pair<ProcessId, Certificate>> = mutableSetOf()
    private val seenTransactions: MutableSet<Pair<Transaction, Certificate>> = mutableSetOf()

    private var curTransaction: SignedTransaction? = null

    private var result: Pair<Transaction, ValidationCertificate>? = null
    private val executeOnResult: MutableList<(Pair<Transaction, ValidationCertificate>) -> Unit> = mutableListOf()

    override fun validate(transaction: Transaction, certificate: Certificate) {
        val signedTransaction = Pair(transaction, certificate)
        curTransaction = signedTransaction
        seenTransactions.add(signedTransaction)
        seqNum += 1
        status = Status.Requesting
        val config = historyHolder.getHistory().greatestConfig()
        environment.broadcast(ValidateRequest(signedTransaction, seqNum, config.getSize()))
    }

    override fun onMessage(message: TVMessage, from: ProcessId) {
        when (message) {
            is ValidateRequest -> {
                handleValidateRequest(message, from)
            }
            is ValidateResponse -> {
                handleValidateResponse(message, from)
            }
            else -> {
                throw UnknownMessageException(message)
            }
        }

    }

    override fun verifySenders(signedTransactions: Collection<SignedTransaction>) =
        signedTransactions.all { verifySender(it.first, it.second) }

    override fun getResult(): Pair<Transaction, ValidationCertificate>? {
        return result
    }

    override fun onResult(listener: (Pair<Transaction, ValidationCertificate>) -> Unit) {
        executeOnResult.add(listener)
    }

    private fun handleValidateRequest(message: ValidateRequest, from: ProcessId) {
        if (
            message.configSize != historyHolder.getConfigInstalled().getSize() &&
            historyHolder.getHistory().greatestConfig().getSize() >= message.configSize
        ) {
            // TODO: wait
        }


    }


    private fun handleValidateResponse(message: ValidateResponse, from: ProcessId) {
        TODO("Not yet implemented")
    }




}