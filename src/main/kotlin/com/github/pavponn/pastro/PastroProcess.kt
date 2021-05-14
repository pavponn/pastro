package com.github.pavponn.pastro

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.Props
import com.github.pavponn.agreement.PastroAdjustableByzantineLatticeAgreement
import com.github.pavponn.agreement.VerifiableObject
import com.github.pavponn.configuration.ConfigurationLattice
import com.github.pavponn.configuration.PastroConfiguration
import com.github.pavponn.environment.Environment
import com.github.pavponn.history.PastroHistory
import com.github.pavponn.lattice.LatticeSet
import com.github.pavponn.lattice.latticeSetOf
import com.github.pavponn.message.Message
import com.github.pavponn.message.TransferMessage
import com.github.pavponn.message.UnknownMessageException
import com.github.pavponn.message.agreement.ABLAMessage
import com.github.pavponn.message.checker.ConfigurationMessage
import com.github.pavponn.message.checker.HistoryMessage
import com.github.pavponn.message.settings.CheckerRefMessage
import com.github.pavponn.message.settings.EnvironmentMessage
import com.github.pavponn.message.settings.HolderMessage
import com.github.pavponn.message.settings.SettingsMessage
import com.github.pavponn.message.validation.TVMessage
import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.DefaultValues.DEFAULT_CERTIFICATE
import com.github.pavponn.utils.ProcessId
import com.github.pavponn.validation.PastroTransactionValidation
import com.github.pavponn.validation.TransactionValidation

/**
 * @author pavponn
 * Class that implements main Pastro pipeline.
 */
class PastroProcess : AbstractActor() {

    private lateinit var environment: PastroEnvironment
    private lateinit var transactionValidation: TransactionValidation
    private lateinit var configABLA: PastroAdjustableByzantineLatticeAgreement<LatticeSet<Transaction>>
    private lateinit var historyABLA: PastroAdjustableByzantineLatticeAgreement<LatticeSet<ConfigurationLattice>>
    private lateinit var checker: ActorRef

    companion object {
        @JvmStatic
        fun createActor(): Props {
            return Props.create(PastroProcess::class.java, ::PastroProcess)
        }
    }

    override fun createReceive(): Receive {
        return receiveBuilder()
            .match(Message::class.java, ::onReceiveMessage)
            .build()
    }

    private fun onReceiveMessage(message: Message) {
        when (message) {
            is TVMessage -> {
                handleTVMessage(message)
            }
            is SettingsMessage -> {
                handleSettingsMessage(message)
            }
            is TransferMessage -> {
                handleTransferMessage(message)
            }
            is ABLAMessage -> {
                handleABLAMessage(message)
            }
            else -> {
                throw UnknownMessageException(message)
            }
        }
    }

    private fun handleTVMessage(message: TVMessage) {
        val from = getSenderId()
        transactionValidation.onMessage(message, from)
    }


    private fun handleABLAMessage(message: ABLAMessage) {
        val from = getSenderId()
        if (message.objectName == "ConfigABLA") {
            configABLA.onMessage(message, from)
        } else if (message.objectName == "HistoryABLA") {
            historyABLA.onMessage(message, from)
        }

    }

    private fun handleSettingsMessage(message: SettingsMessage) {
        when (message) {
            is EnvironmentMessage -> handleEnvironmentMessage(message)
            is HolderMessage -> handleHistoryHolderMessage(message)
            is CheckerRefMessage -> handleCheckerRefMessage(message)
            else -> throw UnknownMessageException(message)
        }
    }

    private fun handleTransferMessage(message: TransferMessage) {
        val transaction = message.transaction
        val certificate = message.certificate
        transactionValidation.validate(transaction, certificate)
    }

    private fun handleEnvironmentMessage(message: EnvironmentMessage) {
        environment =
            PastroEnvironment(
                message.processId,
                message.actorRefs,
                self
            )
    }

    private fun handleHistoryHolderMessage(message: HolderMessage) {
        val historyHolder = message.historyHolder
        val config = historyHolder.getHistory().greatestConfig()
        val transactions = config.getTransactions()

        transactionValidation =
            PastroTransactionValidation(
                historyHolder,
                environment,
                transactions
            )
        transactionValidation.onResult { tx, certificate ->
            println("${self.path().name()} validated transaction $tx with certificate $certificate")
            startConfigABLA(tx)
        }

        configABLA =
            PastroAdjustableByzantineLatticeAgreement(
                Pair(latticeSetOf(transactions), DEFAULT_CERTIFICATE),
                environment,
                historyHolder,
                ::verifyInput,
                "ConfigABLA"
            )
        configABLA.onResult { res, certificate ->
            println("${self.path().name()} returned from ConfigABLA with $certificate")
            val configuration = PastroConfiguration(res)
            checker.tell(ConfigurationMessage(configuration), self)
            startHistoryABLA(configuration)
        }

        historyABLA =
            PastroAdjustableByzantineLatticeAgreement(
                Pair(latticeSetOf(config), DEFAULT_CERTIFICATE),
                environment,
                message.historyHolder,
                ::verifyInput,
                "HistoryABLA"
            )
        historyABLA.onResult { res, certificate ->
            println("${self.path().name()} returned from HistoryABLA with $certificate")
            val history = PastroHistory(res)
            checker.tell(HistoryMessage(history), self)
        }
    }

    private fun handleCheckerRefMessage(message: CheckerRefMessage) {
        checker = message.checker
    }

    private fun startConfigABLA(transaction: Transaction) {
        val input = latticeSetOf(transaction)
        configABLA.propose(input, DEFAULT_CERTIFICATE)
    }

    private fun startHistoryABLA(config: ConfigurationLattice) {
        val input = latticeSetOf(config)
        historyABLA.propose(input, DEFAULT_CERTIFICATE)
    }

    private fun getSenderId(): Int {
        return Integer.parseInt(sender.path().name().substring(1))
    }

    class PastroEnvironment(
        override val processId: ProcessId,
        private val refs: Array<ActorRef>,
        private val self: ActorRef
    ) : Environment {

        override val nProcesses: Int
            get() = refs.size


        override fun send(message: Message, toId: ProcessId) {
            refs[toId - 1].tell(message, self)
        }

        override fun broadcast(message: Message) {
            IntRange(1, nProcesses).forEach {
                send(message, it)
            }
        }
    }

    private fun <E> verifyInput(obj: VerifiableObject<E>) = true

}
