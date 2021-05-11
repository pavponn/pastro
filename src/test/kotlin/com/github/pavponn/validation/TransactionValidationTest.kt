package com.github.pavponn.validation

import com.github.pavponn.configuration.PastroConfiguration
import com.github.pavponn.environment.Environment
import com.github.pavponn.history.PastroHistory
import com.github.pavponn.message.Message
import com.github.pavponn.message.ValidateRequest
import com.github.pavponn.pastro.PastroHistoryHolder
import com.github.pavponn.transaction.Transaction
import com.github.pavponn.transaction.signTransaction
import com.github.pavponn.utils.DefaultValues
import com.github.pavponn.utils.DefaultValues.DEFAULT_CERTIFICATE
import com.github.pavponn.utils.ProcessId
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * @author pavponn
 */
class TransactionValidationTest {

    companion object {
        private const val TOTAL_STAKE = 45
        private val TR_INIT = Transaction(
            DefaultValues.INIT_TRANSACTION_ID,
            DefaultValues.INIT_SENDER,
            mapOf(
                1 to 10,
                2 to 22,
                3 to 13
            ),
            emptySet()
        )

        private val TR_1_BY_1 = Transaction(
            1,
            1,
            mapOf(
                1 to 5,
                2 to 5,
                3 to 0
            ),
            setOf(Pair(DefaultValues.INIT_SENDER, DefaultValues.INIT_TRANSACTION_ID))
        )

        private val TR_1_BY_2 = Transaction(
            1,
            2,
            mapOf(
                1 to 3,
                2 to 7,
                3 to 12
            ),
            setOf(Pair(DefaultValues.INIT_SENDER, DefaultValues.INIT_TRANSACTION_ID))
        )

        private val TR_1_BY_3 = Transaction(
            1,
            3,
            mapOf(
                1 to 15,
                2 to 8,
                3 to 2
            ),
            setOf(
                Pair(DefaultValues.INIT_SENDER, DefaultValues.INIT_TRANSACTION_ID),
                Pair(2, 1)
            )
        )

        private val TR_2_BY_2 = Transaction(
            2,
            2,
            mapOf(
                1 to 1,
                2 to 3,
                3 to 1
            ),
            setOf(Pair(1, 1))
        )
        val CONFIGURATION = PastroConfiguration(setOf(TR_INIT, TR_1_BY_1, TR_1_BY_2, TR_1_BY_3, TR_2_BY_2))
        val STAKE_1 = CONFIGURATION.getProcessStake(1)
        val STAKE_2 = CONFIGURATION.getProcessStake(2)
        val STAKE_3 = CONFIGURATION.getProcessStake(3)
    }

    class TestEnvironment(override val processId: ProcessId, override val nProcesses: Int) : Environment {

        val messagesSentTo: MutableMap<ProcessId, MutableSet<Message>> = mutableMapOf()

        override fun send(message: Message, toId: ProcessId) {
            if (messagesSentTo[toId] == null) {
                messagesSentTo[toId] = mutableSetOf()
            }
            messagesSentTo[toId]!!.add(message)
        }

        override fun broadcast(message: Message) {
            IntRange(1, nProcesses).forEach {
                send(message, it)
            }
        }


    }

    private lateinit var testEnvironment: TestEnvironment
    private lateinit var transactionValidation: TransactionValidation

    @Before
    fun setUp() {
        val history = PastroHistory(setOf(CONFIGURATION))
        val pastroHistoryHolder = PastroHistoryHolder(history, DEFAULT_CERTIFICATE)
        testEnvironment = TestEnvironment(1, 3)
        transactionValidation =
            PastroTransactionValidation(
                pastroHistoryHolder,
                testEnvironment,
                CONFIGURATION.getTransactions()
            )

    }

    @Test
    fun `should have correct initial distribution`() {
        Assert.assertEquals(TOTAL_STAKE, STAKE_1 + STAKE_2 + STAKE_3)
    }

    @Test
    fun `should send messages to all on validate`() {
        val transaction = Transaction(
            2,
            1,
            mapOf(2 to 5),
            setOf(1 to 1),
        )
        val certificate = signTransaction(transaction)
        val signedTransaction = Pair(transaction, certificate)
        transactionValidation.validate(transaction, certificate)
        Assert.assertTrue(
            IntRange(1, testEnvironment.nProcesses).all {
                testEnvironment.messagesSentTo[it]!!.contains(
                    ValidateRequest(
                        signedTransaction,
                        CONFIGURATION.getSize()
                    )
                )
            }
        )
    }
}