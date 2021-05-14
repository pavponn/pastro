package com.github.pavponn.message.validation

import com.github.pavponn.message.Message
import com.github.pavponn.utils.Signature
import com.github.pavponn.utils.SignedTransaction

/**
 * @author pavponn
 */
data class ValidateResponse(
    val signedTransaction: SignedTransaction,
    val signature: Signature,
) : TVMessage

data class ValidateResponseSign(
    val transaction: SignedTransaction
) : Message
