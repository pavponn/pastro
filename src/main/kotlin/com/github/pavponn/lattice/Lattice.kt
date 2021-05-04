package com.github.pavponn.lattice

/**
 * @author pavponn
 *
 * Any type that forms a lattice should implement this interface.
 */
interface Lattice<T> {

    /**
     * Checks whether this element of lattice is less than or equal to (w.r.t. ⊑) [other] element of lattice.
     */
    infix fun leq(other: T): Boolean

    /**
     * Checks whether this element of lattice is less than (w.r.t. ⊑) [other] element of lattice.
     */
    infix fun less(other: T): Boolean

    /**
     * Checks whether this element of lattice is equal to (w.r.t. ⊑) [other] element of lattice.
     */
    infix fun eq(other: T): Boolean

    /**
     * Checks whether this element of lattice is not equal to (w.r.t. ⊑) [other] element of lattice.
     */
    infix fun noteq(other: T): Boolean

    /**
     * Returns an element of lattice which is a merge (⊔) of this element of lattice and [other] element of lattice.
     */
    infix fun merge(other: T): T

}
