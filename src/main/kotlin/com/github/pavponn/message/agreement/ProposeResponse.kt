package com.github.pavponn.message.agreement

import com.github.pavponn.agreement.VerifiableObject
import com.github.pavponn.lattice.Lattice
import com.github.pavponn.message.Message
import com.github.pavponn.utils.SequenceNumber
import com.github.pavponn.utils.Signature

/**
 * @author pavponn
 */
data class ProposeResponse<E : Lattice<E>>(
    val vs: Set<VerifiableObject<E>>,
    val signature: Signature,
    val sn: SequenceNumber,
    override val objectName: String = ""
) : ABLAMessage

data class ProposeResponseSign<E>(
    val vs: Set<VerifiableObject<E>>,
) : Message


