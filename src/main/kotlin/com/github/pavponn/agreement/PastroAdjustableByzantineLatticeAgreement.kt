package com.github.pavponn.agreement

import com.github.pavponn.environment.Environment
import com.github.pavponn.fsds.ForwardSecureDigitalSignaturesBasic
import com.github.pavponn.holder.HistoryHolder
import com.github.pavponn.lattice.Lattice
import com.github.pavponn.message.UnknownMessageException
import com.github.pavponn.message.agreement.ABLAMessage
import com.github.pavponn.message.agreement.ProposeRequest
import com.github.pavponn.message.agreement.ProposeResponse
import com.github.pavponn.message.agreement.ProposeResponseSign
import com.github.pavponn.utils.Certificate
import com.github.pavponn.utils.ProcessId

/**
 * @author pavponn
 */
class PastroAdjustableByzantineLatticeAgreement<E : Lattice<E>>(
    initInput: VerifiableObject<E>,
    private val environment: Environment,
    private val historyHolder: HistoryHolder,
    private val verifyingFunction: (VerifiableObject<E>) -> Boolean,
    private val name: String = ""

    ) : AdjustableByzantineLatticeAgreement<E> {

    private val values: MutableSet<VerifiableObject<E>> = mutableSetOf(initInput)
    private var acks: MutableSet<ProcessId> = mutableSetOf()
    private var seqNum = 0
    private var isProposing = false

    private var sentValues: MutableSet<VerifiableObject<E>> = mutableSetOf()

    private val fsds = ForwardSecureDigitalSignaturesBasic()

    private val executeOnResult: MutableList<(E, Certificate) -> Unit> = mutableListOf()

    override fun propose(input: E, certificate: Certificate) {
        refine(setOf(Pair(input, certificate)))
    }

    private fun refine(vs: Set<VerifiableObject<E>>) {
        acks = mutableSetOf()
        values.addAll(vs)
        sentValues = mutableSetOf()
        sentValues.addAll(values)
        seqNum += 1
        isProposing = true
        val config = historyHolder.getHistory().greatestConfig()
        environment.broadcast(ProposeRequest(values.toSet(), seqNum, config.getSize(), name))
    }

    override fun verifyInput(element: VerifiableObject<E>): Boolean {
        return verifyingFunction(element)
    }


    override fun onMessage(message: ABLAMessage, from: ProcessId) {
        when (message) {
            is ProposeRequest<*> -> handleProposeRequest(message as ProposeRequest<E>, from)
            is ProposeResponse<*> -> handleProposeResponse(message as ProposeResponse<E>, from)
            else -> throw UnknownMessageException(message)

        }
    }

    private fun handleProposeRequest(message: ProposeRequest<E>, from: ProcessId) {
        if (
            message.configSize != historyHolder.getConfigInstalled().getSize() &&
            historyHolder.getHistory().greatestConfig().getSize() >= message.configSize
        ) {
            // intentionally left blank
        }
        val config = historyHolder.getHistory().greatestConfig()
        if (verifyInputs(message.values)) {
            values.addAll(message.values)
            if (config.getSize() == message.configSize) {
                val signature = fsds.signFS(ProposeResponseSign(values), config.getSize())
                environment.send(ProposeResponse<E>(values, signature, message.sn, name), from)
            }
        }
    }

    private fun handleProposeResponse(message: ProposeResponse<E>, from: ProcessId) {
        if (message.sn != seqNum || !isProposing) {
            return
        }

        val config = historyHolder.getHistory().greatestConfig()
        val isValid = fsds.verifyFS(
            ProposeResponseSign<E>(message.vs),
            from,
            message.signature,
            config.getSize()
        )
        if (isValid) {
            if (sentValues == message.vs) {
                acks.add(from)
                checkReturn()
            } else if (verifyInputs(message.vs)) {
                refine(message.vs)
            }
        }
    }

    private fun checkReturn() {
        val config = historyHolder.getHistory().greatestConfig()
        if (config.hasQuorum(acks)) {
            isProposing = false
            var result = values.toList()[0].first
            values.forEach {
                result = result.merge(it.first)
            }

            executeOnResult.forEach {
                it(result, "signatures from: $acks, isQuorum: ${config.hasQuorum(acks)}")
            }
            acks = mutableSetOf()
        }


    }

    override fun onResult(listener: (E, Certificate) -> Unit) {
        executeOnResult.add(listener)
    }
}