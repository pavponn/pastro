package com.github.pavponn.lattice

/**
 * @author pavponn
 *
 * Interface that combines lattice and set operations.
 */
interface LatticeSet<E> : Lattice<LatticeSet<E>>, MutableSet<E>

