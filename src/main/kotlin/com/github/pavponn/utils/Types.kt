package com.github.pavponn.utils

/**
 * @author pavponn
 */
typealias TransactionId = Int
typealias ProcessId = Int
typealias TransferMap = Map<ProcessId, Int>
typealias DependencySet = Set<Pair<ProcessId, TransactionId>>

typealias Timestamp = Int

typealias Signature = String