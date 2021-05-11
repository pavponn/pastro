package com.github.pavponn.message

import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.Certificate
import com.github.pavponn.utils.SequenceNumber

/**
 * @author pavponn
 */
data class ValidateRequest(
    val signedTransaction: Pair<Transaction, Certificate>,
    val sn: SequenceNumber,
    val configSize: Int
) : TVMessage
