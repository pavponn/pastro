package com.github.pavponn.lattice

/**
 * @author pavponn
 */

fun <T> compare(l1: T, l2: T): Int where T : Lattice<T> {
    require(l1 leq l2 || l2 leq l1)
    return when {
        l1 less l2 -> -1
        l2 less l1 -> 1
        else -> 0
    }

}