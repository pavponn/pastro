package com.github.pavponn.message

import com.github.pavponn.utils.SequenceNumber
import com.github.pavponn.utils.Signature
import com.github.pavponn.utils.SignedTransaction

/**
 * @author pavponn
 */
data class ValidateResponse(
    val signedTransaction: SignedTransaction,
    val signature: Signature,
    val sn: SequenceNumber
): TVMessage