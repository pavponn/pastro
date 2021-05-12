package com.github.pavponn.message.validation

import com.github.pavponn.message.validation.TVMessage
import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.Certificate

/**
 * @author pavponn
 */
data class ValidateRequest(
    val signedTransaction: Pair<Transaction, Certificate>,
    val configSize: Int
) : TVMessage
