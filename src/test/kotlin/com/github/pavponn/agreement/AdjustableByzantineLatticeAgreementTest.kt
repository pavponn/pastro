package com.github.pavponn.agreement

import com.github.pavponn.configuration.PastroConfiguration
import com.github.pavponn.environment.Environment
import com.github.pavponn.fsds.ForwardSecureDigitalSignaturesBasic.Companion.DEFAULT_SIGNATURE
import com.github.pavponn.history.PastroHistory
import com.github.pavponn.holder.PastroHistoryHolder
import com.github.pavponn.lattice.LatticeSet
import com.github.pavponn.lattice.latticeSetOf
import com.github.pavponn.message.Message
import com.github.pavponn.message.agreement.ProposeRequest
import com.github.pavponn.message.agreement.ProposeResponse
import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.Certificate
import com.github.pavponn.utils.DefaultValues
import com.github.pavponn.utils.DefaultValues.DEFAULT_CERTIFICATE
import com.github.pavponn.utils.ProcessId
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AdjustableByzantineLatticeAgreementTest {

    companion object {
        val ELEMENT_0 = Pair(latticeSetOf(0), DEFAULT_CERTIFICATE)
        val ELEMENT_1 = Pair(latticeSetOf(1), DEFAULT_CERTIFICATE)
        val ELEMENT_2 = Pair(latticeSetOf(2), DEFAULT_CERTIFICATE)

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

    private fun <E> verifyInput(obj: VerifiableObject<E>): Boolean {
        return true
    }

    private lateinit var testEnvironment: TestEnvironment
    private lateinit var pastroABLA: AdjustableByzantineLatticeAgreement<LatticeSet<Int>>

    @Before
    fun setUp() {
        val history = PastroHistory(setOf(CONFIGURATION))
        val pastroHistoryHolder = PastroHistoryHolder(history, DEFAULT_CERTIFICATE)
        testEnvironment = TestEnvironment(1, 3)
        pastroABLA =
            PastroAdjustableByzantineLatticeAgreement(
                ELEMENT_0,
                testEnvironment,
                pastroHistoryHolder,
                ::verifyInput
            )
    }

    @Test
    fun `should have correct initial distribution`() {
        Assert.assertEquals(TOTAL_STAKE, STAKE_1 + STAKE_2 + STAKE_3)
    }


    @Test
    fun `should send messages to all on propose`() {
        val sn = 1
        val latticeSet = latticeSetOf(1)
        pastroABLA.propose(latticeSet, DEFAULT_CERTIFICATE)
        Assert.assertTrue(
            IntRange(1, testEnvironment.nProcesses).all {
                testEnvironment.messagesSentTo[it]!!.contains(
                    ProposeRequest(
                        setOf(ELEMENT_0, ELEMENT_1),
                        sn,
                        CONFIGURATION.getSize()
                    )
                )
            }
        )
    }

    @Test
    fun `should reply with equal set`() {
        val sn = 1
        val sender = 2
        pastroABLA.onMessage(
            ProposeRequest(
                setOf(ELEMENT_0, ELEMENT_2),
                sn,
                CONFIGURATION.getSize()
            ),
            sender
        )

        Assert.assertTrue(
            testEnvironment.messagesSentTo[sender]!!.contains(
                ProposeResponse(
                    setOf(ELEMENT_0, ELEMENT_2),
                    DEFAULT_SIGNATURE,
                    sn
                )
            )
        )
    }

    @Test
    fun `should reply with greater set`() {
        val sn = 1
        val sender = 2
        pastroABLA.onMessage(
            ProposeRequest(
                setOf(ELEMENT_1, ELEMENT_2),
                sn,
                CONFIGURATION.getSize()
            ),
            sender
        )

        Assert.assertTrue(
            testEnvironment.messagesSentTo[sender]!!.contains(
                ProposeResponse(
                    setOf(ELEMENT_0, ELEMENT_1, ELEMENT_2),
                    DEFAULT_SIGNATURE,
                    sn
                )
            )
        )
    }

    @Test
    fun `should return when quorum signed`() {
        var proposeResponse = 1
        var returnedAfter = -1
        var obtainedResult: LatticeSet<Int>? = null
        val onResultListener: (LatticeSet<Int>, Certificate) -> Unit =
            { result: LatticeSet<Int>, _: Certificate ->
                returnedAfter = proposeResponse
                obtainedResult = result
            }
        val proposeResponseMessage = ProposeResponse(
            setOf(ELEMENT_0, ELEMENT_1),
            DEFAULT_SIGNATURE,
            1
        )
        pastroABLA.onResult(onResultListener)
        pastroABLA.propose(ELEMENT_1.first, DEFAULT_CERTIFICATE)
        pastroABLA.onMessage(proposeResponseMessage, 2)
        proposeResponse += 1
        pastroABLA.onMessage(proposeResponseMessage, 1)
        proposeResponse += 1
        pastroABLA.onMessage(proposeResponseMessage, 3)
        proposeResponse += 1
        Assert.assertEquals(2, returnedAfter)
        Assert.assertEquals(latticeSetOf(0, 1), obtainedResult)
    }

    @Test
    fun `should return when quorum signed `() {
        var proposeResponse = 1
        var returnedAfter = -1
        var obtainedResult: LatticeSet<Int>? = null
        val onResultListener: (LatticeSet<Int>, Certificate) -> Unit =
            { result: LatticeSet<Int>, _: Certificate ->
                returnedAfter = proposeResponse
                obtainedResult = result
            }

        val proposeResponseFirstMessage = ProposeResponse(
            setOf(ELEMENT_0, ELEMENT_1, ELEMENT_2),
            DEFAULT_SIGNATURE,
            1
        )
        val proposeResponseSecondMessage = ProposeResponse(
            setOf(ELEMENT_0, ELEMENT_1, ELEMENT_2),
            DEFAULT_SIGNATURE,
            2
        )
        pastroABLA.onResult(onResultListener)
        pastroABLA.propose(ELEMENT_1.first, DEFAULT_CERTIFICATE)
        pastroABLA.onMessage(proposeResponseFirstMessage, 2)
        proposeResponse += 1
        pastroABLA.onMessage(proposeResponseFirstMessage, 1)
        proposeResponse += 1
        pastroABLA.onMessage(proposeResponseFirstMessage, 3)
        proposeResponse += 1
        pastroABLA.onMessage(proposeResponseSecondMessage, 2)
        proposeResponse += 1
        pastroABLA.onMessage(proposeResponseSecondMessage, 1)
        proposeResponse += 1
        pastroABLA.onMessage(proposeResponseSecondMessage, 3)
        proposeResponse += 1
        Assert.assertEquals(5, returnedAfter)
        Assert.assertEquals(latticeSetOf(0, 1, 2), obtainedResult)
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
}