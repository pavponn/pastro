package com.github.pavponn.configuration

import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.DefaultValues
import com.github.pavponn.utils.ProcessId
import com.github.pavponn.utils.TransactionId

/**
 * @author pavponn
 *
 * Implementation of configuration used in Pastro protocol.
 */
class PastroConfiguration(private val transactions: Set<Transaction>) : ConfigurationLattice {

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
        var processesStake = 0.0
        stakes.forEach {
            if (processes.contains(it.key)) {
                processesStake += it.value
            }
        }
        val neededStake: Double = (2.0 / 3.0) * totalStake
        return processesStake > neededStake
    }

    override fun getProcessStake(processId: ProcessId): Int {
        val stakes = getStakeDistribution()
        return stakes.getOrDefault(processId, 0)
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

    override fun getSize(): Int {
        return transactions.size
    }

    override fun leq(other: ConfigurationLattice): Boolean {
        return other.getTransactions().containsAll(getTransactions())
    }

    override fun less(other: ConfigurationLattice): Boolean {
        return this leq other && this noteq other
    }

    override fun eq(other: ConfigurationLattice): Boolean {
        return getTransactions() == other.getTransactions()
    }

    override fun noteq(other: ConfigurationLattice): Boolean {
        return (this eq other).not()
    }

    override fun merge(other: ConfigurationLattice): ConfigurationLattice {
        val newTransactionSet = mutableSetOf<Transaction>()
        newTransactionSet.addAll(getTransactions())
        newTransactionSet.addAll(other.getTransactions())
        return PastroConfiguration(newTransactionSet)
    }

}
