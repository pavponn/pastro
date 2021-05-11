package com.github.pavponn.validation

import com.github.pavponn.environment.Environment
import com.github.pavponn.message.*
import com.github.pavponn.pastro.HistoryHolder
import com.github.pavponn.transaction.*
import com.github.pavponn.utils.Certificate
import com.github.pavponn.utils.ProcessId
import com.github.pavponn.utils.SignedTransaction
import com.github.pavponn.fsds.ForwardSecureDigitalSignaturesBasic

/**
 * @author pavponn
 */
class PastroTransactionValidation(
    private val historyHolder: HistoryHolder,
    private val environment: Environment,
    private val initialSeenTransactions: Set<Transaction>,
) : TransactionValidation {

    enum class Status {
        Requesting,
        Inactive
    }

    private var status: Status = Status.Inactive

    private var acks: MutableMap<Transaction, MutableSet<ProcessId>> = mutableMapOf()
    private val seenTransactions: MutableSet<Pair<Transaction, Certificate>> =
        initialSeenTransactions.map { Pair(it, "") }.toMutableSet()

    private val executeOnResult: MutableList<(Transaction, ValidationCertificate) -> Unit> = mutableListOf()

    private val fsds = ForwardSecureDigitalSignaturesBasic()

    override fun validate(transaction: Transaction, certificate: Certificate) {
        val signedTransaction = Pair(transaction, certificate)
        acks[transaction] = mutableSetOf()
        seenTransactions.add(signedTransaction)
        status = Status.Requesting
        val config = historyHolder.getHistory().greatestConfig()
        environment.broadcast(ValidateRequest(signedTransaction, config.getSize()))
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

    override fun onResult(listener: (Transaction, ValidationCertificate) -> Unit) {
        executeOnResult.add(listener)
    }

    private fun handleValidateRequest(message: ValidateRequest, from: ProcessId) {
        if (
            message.configSize != historyHolder.getConfigInstalled().getSize() &&
            historyHolder.getHistory().greatestConfig().getSize() >= message.configSize
        ) {
            // intentionally left blank
        }
        val signedTransaction = message.signedTransaction
        val transaction = message.signedTransaction.first
        val seenTransactionsNotSigned = signedTransactionsToTransactions(seenTransactions)
        if (verifySenders(setOf(signedTransaction)) &&
            isValidAndWellFormed(transaction, seenTransactionsNotSigned)
        ) {
            // add transaction to the set of seen transactions
            seenTransactions.add(signedTransaction)

            // if transaction doesn't conflict, then sign it and send the response
            if (noConflictsWith(transaction, seenTransactionsNotSigned)) {
                val sig = fsds.signFS(
                    ValidateResponseSign(signedTransaction),
                    historyHolder.getHistory().greatestConfig().getSize()
                )
                environment.send(ValidateResponse(signedTransaction, sig), from)
            }
        }

    }


    private fun handleValidateResponse(message: ValidateResponse, from: ProcessId) {
        val collectedSignatures = acks[message.signedTransaction.first] ?: return
        if (historyHolder.getHistory().greatestConfig().hasQuorum(collectedSignatures)) {
            return
        }
        val config = historyHolder.getHistory().greatestConfig()
        val isValid = fsds.verifyFS(
            ValidateResponseSign(message.signedTransaction),
            from,
            message.signature,
            config.getSize()
        )
        if (!isValid) {
            return
        }
        acks[message.signedTransaction.first]!!.add(from)
        checkReturn(message.signedTransaction)
    }

    private fun checkReturn(signedTransaction: SignedTransaction) {
        val signatures = acks[signedTransaction.first] ?: emptySet()
        if (historyHolder.getHistory().greatestConfig().hasQuorum(signatures)) {
            executeOnResult.forEach {
                it(signedTransaction.first, "")
            }
        }
    }

}