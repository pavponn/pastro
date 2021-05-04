package com.github.pavponn.lattice

/**
 * @author pavponn
 *
 * Functions that are useful when working with lattices.
 */

/**
 * Compare two comparable elements of lattice, otherwise throws.
 */
fun <T> compare(l1: T, l2: T): Int where T : Lattice<T> {
    require(l1 leq l2 || l2 leq l1)
    return when {
        l1 less l2 -> -1
        l2 less l1 -> 1
        else -> 0
    }

}