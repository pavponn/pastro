package com.github.pavponn.utils

import com.github.pavponn.transaction.Transaction

/**
 * @author pavponn
 */
typealias TransactionId = Int
typealias ProcessId = Int
typealias TransferMap = Map<ProcessId, Int>
typealias DependencySet = Set<Pair<ProcessId, TransactionId>>

typealias Timestamp = Int

typealias Signature = String

typealias Certificate = String

typealias SequenceNumber = Int

typealias SignedTransaction = Pair<Transaction, Certificate>
