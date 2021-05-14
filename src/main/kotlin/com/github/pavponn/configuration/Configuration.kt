package com.github.pavponn.configuration

import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.ProcessId

/**
 * @author pavponn
 *
 * Interface that any implementation of configuration (see Pastro paper)
 * should satisfy.
 */
interface Configuration {

    /**
     * Returns transactions that are included in this configuration.
     */
    fun getTransactions(): Set<Transaction>

    /**
     * Checks whether given set of processes forms a quorum in this configuration.
     */
    fun hasQuorum(processes: Set<ProcessId>): Boolean

    /**
     * Returns stake of a given process (specified by its [processId]).
     */
    fun getProcessStake(processId: ProcessId): Int

    /**
     * Returns a map from [ProcessId] to [Int] that represents
     * stake distribution in given configuration.
     */
    fun getStakeDistribution(): Map<ProcessId, Int>

    /**
     * Returns total stake in the system according to this configuration.
     */
    fun getTotalStake(): Int

    /**
     * Returns size of configuration (number of transactions).
     */
    fun getSize(): Int

}
