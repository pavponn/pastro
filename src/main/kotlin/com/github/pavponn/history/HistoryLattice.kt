package com.github.pavponn.history

import com.github.pavponn.lattice.Lattice

/**
 * @author pavponn
 *
 * Combines History operations with Lattice operations.
 */
interface HistoryLattice : History, Lattice<HistoryLattice>