package com.github.pavponn.agreement

import com.github.pavponn.lattice.Lattice
import com.github.pavponn.message.agreement.ABLAMessage

import com.github.pavponn.utils.Certificate
import com.github.pavponn.utils.ProcessId

/**
 * @author pavponn
 */
interface AdjustableByzantineLatticeAgreement<E : Lattice<E>> {

    fun propose(input: E, certificate: Certificate)

    fun verifyInput(element: VerifiableObject<E>): Boolean

    fun verifyInputs(elements: Collection<VerifiableObject<E>>): Boolean =
        elements.all { verifyInput(it) }


    fun onMessage(message: ABLAMessage, from: ProcessId)

    fun onResult(listener: (E, Certificate) -> Unit)
}

typealias VerifiableObject<E> = Pair<E, Certificate>
