package com.github.pavponn.agreement

import com.github.pavponn.lattice.Lattice
import com.github.pavponn.message.agreement.ABLAMessage

import com.github.pavponn.utils.Certificate
import com.github.pavponn.utils.ProcessId

/**
 * @author pavponn
 */
interface AdjustableByzantineLatticeAgreement<E : Lattice<E>> {

    /**
     * Propose an input value from the lattice.
     */
    fun propose(input: E, certificate: Certificate)

    /**
     * Verify input value of a lattice agreement instance.
     */
    fun verifyInput(element: VerifiableObject<E>): Boolean

    /**
     * Verify a collection of given input values of a lattice agreement instance.
     */
    fun verifyInputs(elements: Collection<VerifiableObject<E>>): Boolean =
        elements.all { verifyInput(it) }

    /**
     * On message receiving handler.
     */
    fun onMessage(message: ABLAMessage, from: ProcessId)

    /**
     * Adds a callback that should be performed when a result of lattice agreement is added.
     */
    fun onResult(listener: (E, Certificate) -> Unit)
}

typealias VerifiableObject<E> = Pair<E, Certificate>
