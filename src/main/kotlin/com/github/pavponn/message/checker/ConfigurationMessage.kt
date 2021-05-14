package com.github.pavponn.message.checker

import com.github.pavponn.configuration.ConfigurationLattice

/**
 * @author pavponn
 */
data class ConfigurationMessage(val configuration: ConfigurationLattice): CheckerMessage
