package com.github.pavponn.message.checker

import com.github.pavponn.transaction.Transaction

data class TransactionMessage(val transaction: Transaction): CheckerMessage