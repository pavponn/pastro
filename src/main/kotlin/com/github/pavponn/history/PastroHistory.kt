package com.github.pavponn.history

import com.github.pavponn.configuration.ConfigurationLattice
import com.github.pavponn.lattice.compare

/**
 * @author pavponn
 *
 * Implementation of history used in Pastro protocol.
 */
class PastroHistory(configs: Collection<ConfigurationLattice>) : HistoryLattice {
    private val configurations: List<ConfigurationLattice>

    init {
        val configurationsList = configs.toSet().toList()
        require(configurationsList.isNotEmpty()) { "$EXCEPTION_MESSAGE_INIT no configurations" }
        configurationsList.forEach { c1 ->
            configurationsList.forEach { c2 ->
                require(c1 leq c2 || c2 leq c1) {
                    "$EXCEPTION_MESSAGE_INIT configurations $c1 and $c2 and not comparable"
                }
            }
        }

        configurations = configurationsList.sortedWith { c1: ConfigurationLattice, c2: ConfigurationLattice ->
            compare(c1, c2)
        }
    }

    override fun greatestConfig(): ConfigurationLattice {
        return configurations.last()
    }

    override fun orderedConfigs(): List<ConfigurationLattice> {
        return configurations
    }

    override fun orderedConfigs(from: ConfigurationLattice): List<ConfigurationLattice> {
        val index = configurations.indexOfFirst { from leq it }
        if (index == -1) {
            return emptyList()
        }
        return configurations.subList(index, configurations.size)
    }

    override fun contains(config: ConfigurationLattice): Boolean {
        return configurations.contains(config)
    }

    override fun leq(other: HistoryLattice): Boolean {
        return other.orderedConfigs().containsAll(orderedConfigs())
    }

    override fun less(other: HistoryLattice): Boolean {
        return this leq other && this noteq other
    }

    override fun eq(other: HistoryLattice): Boolean {
        return (other leq this) && (this leq other)
    }

    override fun noteq(other: HistoryLattice): Boolean {
        return (this eq other).not()
    }

    override fun merge(other: HistoryLattice): HistoryLattice {
        val newConfigurationsSet = mutableSetOf<ConfigurationLattice>()
        newConfigurationsSet.addAll(orderedConfigs())
        newConfigurationsSet.addAll(other.orderedConfigs())
        return PastroHistory(newConfigurationsSet)
    }

    companion object {
        const val EXCEPTION_MESSAGE_INIT = "Can't create history:"
    }

}
