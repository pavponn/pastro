package com.github.pavponn.pastro

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.Props
import com.github.pavponn.environment.Environment
import com.github.pavponn.holder.validation.PastroTransactionValidation
import com.github.pavponn.message.Message
import com.github.pavponn.message.settings.SettingsMessage
import com.github.pavponn.message.validation.TVMessage
import com.github.pavponn.message.TransferMessage
import com.github.pavponn.utils.ProcessId
import com.github.pavponn.holder.validation.TransactionValidation
import com.github.pavponn.message.UnknownMessageException
import com.github.pavponn.message.settings.EnvironmentMessage
import com.github.pavponn.message.settings.HolderMessage

/**
 * @author pavponn
 * Class that implements main Pastro pipeline.
 */
class PastroProcess : AbstractActor() {

    private lateinit var environment: PastroEnvironment
    private lateinit var transactionValidation: TransactionValidation

    companion object {
        @JvmStatic
        fun createActor(): Props {
            return Props.create(PastroProcess::class.java, ::PastroProcess)
        }
    }

    override fun createReceive(): Receive {
        return receiveBuilder()
            .match(Message::class.java, this::onReceiveMessage)
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
            else -> {
                throw UnknownMessageException(message)
            }
        }
    }

    private fun handleTVMessage(message: TVMessage) {
        val from = getSenderId()
        transactionValidation.onMessage(message, from)
    }

    private fun handleSettingsMessage(message: SettingsMessage) {
        when (message) {
            is EnvironmentMessage -> handleEnvironmentMessage(message)
            is HolderMessage -> handleHistoryHolderMessage(message)
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
        transactionValidation =
            PastroTransactionValidation(
                message.historyHolder,
                environment,
                message.historyHolder.getHistory().greatestConfig().getTransactions()
            )
        transactionValidation.onResult { tx, certificate ->
            println("${self.path().name()} validated transaction $tx with certificate $certificate")
        }
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

}