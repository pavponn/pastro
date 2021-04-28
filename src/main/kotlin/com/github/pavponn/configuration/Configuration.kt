package com.github.pavponn.configuration

import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.ProcessId

/**
 * @author pavponn
 */
interface Configuration {

    fun getTransactions(): Set<Transaction>

    fun hasQuorum(processes: Set<ProcessId>): Boolean

    fun getProcessStake(process: ProcessId): Int

    fun getStakeDistribution(): Map<ProcessId, Int>

    fun getTotalStake(): Int
}