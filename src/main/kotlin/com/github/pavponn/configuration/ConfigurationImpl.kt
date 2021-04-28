package com.github.pavponn.configuration

import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.DefaultValues
import com.github.pavponn.utils.ProcessId
import com.github.pavponn.utils.TransactionId

/**
 * @author pavponn
 */
class ConfigurationImpl(private val transactions: Set<Transaction>) : Configuration {

    private val processes = mutableSetOf<ProcessId>()

    init {
        transactions.forEach {
            if (it.spenderId != DefaultValues.INIT_SENDER) {
                processes.add(it.spenderId)
            }

            it.transfer.keys.forEach { process ->
                processes.add(process)
            }
        }
    }

    override fun getTransactions(): Set<Transaction> {
        return transactions
    }

    override fun hasQuorum(processes: Set<ProcessId>): Boolean {
        val stakes = getStakeDistribution()
        val totalStake: Double = getTotalStake().toDouble()
        var processesStake: Double = 0.0
        stakes.forEach {
            if (processes.contains(it.key)) {
                processesStake += it.value
            }
        }
        val neededStake: Double = (2.0 / 3.0) * totalStake
        return processesStake > neededStake
    }

    override fun getProcessStake(process: ProcessId): Int {
        val stakes = getStakeDistribution()
        return stakes.getOrDefault(process, 0)
    }


    override fun getStakeDistribution(): Map<ProcessId, Int> {
        val processStakes = mutableMapOf<ProcessId, Int>()

        processes.forEach { processId ->
            val referencedTransactions = mutableSetOf<Pair<ProcessId, TransactionId>>()
            var processStake = 0

            transactions.forEach { transaction ->
                if (transaction.spenderId == processId) {
                    // find all transactions that this user
                    // uses as dependencies and store them
                    referencedTransactions.addAll(transaction.dependencies)
                }
            }

            transactions.forEach { transaction ->
                if (!referencedTransactions.contains(Pair(transaction.spenderId, transaction.transactionId))) {
                    // if this transaction is not referenced by the process,
                    // then we can count its outputs
                    processStake += transaction.transfer.getOrDefault(processId, 0)
                }
            }
            processStakes[processId] = processStake

        }

        return processStakes
    }

    override fun getTotalStake(): Int {
        return getStakeDistribution()
            .values
            .stream()
            .reduce(0, Integer::sum)
    }
}