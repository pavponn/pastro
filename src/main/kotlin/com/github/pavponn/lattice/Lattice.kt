package com.github.pavponn.lattice

interface Lattice<T> {

    infix fun leq(other: T): Boolean

    infix fun less(other: T): Boolean

    infix fun eq(other: T): Boolean

    infix fun noteq(other: T): Boolean

    infix fun merge(other: T): T

}