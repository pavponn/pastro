package com.github.pavponn.history

import com.github.pavponn.configuration.ConfigurationLattice

/**
 * @author pavponn
 */
interface History {

    fun greatestConfig(): ConfigurationLattice

    fun orderedConfigs(): List<ConfigurationLattice>

    fun orderedConfigs(from: ConfigurationLattice): List<ConfigurationLattice>

    fun contains(config: ConfigurationLattice): Boolean
}