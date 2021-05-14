package com.github.pavponn.configuration

import com.github.pavponn.lattice.Lattice

/**
 * @author pavponn
 *
 * Combines Configuration operations with Lattice operations.
 */
interface ConfigurationLattice : Lattice<ConfigurationLattice>, Configuration
