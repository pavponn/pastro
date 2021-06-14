package com.github.pavponn.message.agreement

import com.github.pavponn.agreement.VerifiableObject
import com.github.pavponn.lattice.Lattice
import com.github.pavponn.utils.SequenceNumber

/**
 * @author pavponn
 */
data class ProposeRequest<E : Lattice<E>>(
    val values: Set<VerifiableObject<E>>,
    val sn: SequenceNumber,
    val configSize: Int,
    override val objectName: String = ""
) : ABLAMessage
