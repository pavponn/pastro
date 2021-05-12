package com.github.pavponn.message

import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.Certificate

data class TransferMessage(val transaction: Transaction, val certificate: Certificate): Message