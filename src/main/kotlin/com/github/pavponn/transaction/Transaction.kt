package com.github.pavponn.transaction

import com.github.pavponn.utils.DependencySet
import com.github.pavponn.utils.ProcessId
import com.github.pavponn.utils.TransactionId
import com.github.pavponn.utils.TransferMap

/**
 * @author pavponn
 */
data class Transaction(
    val transactionId: TransactionId,
    val spenderId: ProcessId,
    val transfer: TransferMap,
    val dependencies: DependencySet
) {
}