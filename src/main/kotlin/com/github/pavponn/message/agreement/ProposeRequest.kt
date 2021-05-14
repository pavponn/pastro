package com.github.pavponn.message.agreement

import com.github.pavponn.agreement.VerifiableObject
import com.github.pavponn.lattice.Lattice

/**
 * @author pavponn
 */
data class ProposeRequest<E : Lattice<E>>(
    val values: Set<VerifiableObject<E>>,
    val sn: Int,
    val configSize: Int,
    override val objectName: String = ""
) : ABLAMessage
