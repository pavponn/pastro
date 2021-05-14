package com.github.pavponn.history

import com.github.pavponn.configuration.ConfigurationLattice

/**
 * @author pavponn
 *
 * Interface that any implementation of history (see Pastro paper)
 * should satisfy.
 */
interface History {

    /**
     * Returns greatest (w.r.t. ⊑) configuration in this history.
     */
    fun greatestConfig(): ConfigurationLattice

    /**
     * Returns all configurations in this history as an ordered (w.r.t. ⊑) list.
     */
    fun orderedConfigs(): List<ConfigurationLattice>

    /**
     * Same as [orderedConfigs], but the resulting list contains only the
     * configurations greater than or equal to [from] (w.r.t. ⊑).
     */
    fun orderedConfigs(from: ConfigurationLattice): List<ConfigurationLattice>

    /**
     * Return true if configuration [config] is part of a given history.
     * Otherwise, return false.
     */
    fun contains(config: ConfigurationLattice): Boolean
}
