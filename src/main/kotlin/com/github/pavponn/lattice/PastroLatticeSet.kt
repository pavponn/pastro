package com.github.pavponn.lattice

/**
 * @author pavponn
 */
class PastroLatticeSet<E>(): LatticeSet<E>, LinkedHashSet<E>() {

    override fun leq(other: LatticeSet<E>): Boolean {
        return other.containsAll(this)
    }

    override fun less(other: LatticeSet<E>): Boolean {
        return this leq other && !(other leq this)
    }

    override fun eq(other: LatticeSet<E>): Boolean {
       return this leq other && other leq this
    }

    override fun noteq(other: LatticeSet<E>): Boolean {
        return (this eq other).not()
    }

    override fun merge(other: LatticeSet<E>): LatticeSet<E> {
        val newSet = PastroLatticeSet<E>()
        newSet.addAll(this)
        newSet.addAll(other)
        return newSet
    }
}

fun <E> latticeSetOf(vararg elements: E): LatticeSet<E> {
    val newPastroLatticeSet = PastroLatticeSet<E>()
    newPastroLatticeSet.addAll(elements)
    return newPastroLatticeSet
}