package com.github.pavponn.lattice

import org.junit.Assert
import org.junit.Test

class LatticeSetTest {

    @Test
    fun `should compare two lattice sets with leq`() {
        val set1 = latticeSetOf(1, 2, 3)
        val set2 = latticeSetOf(1, 2, 3, 4, 5)
        Assert.assertTrue(set1 leq set2)
        Assert.assertFalse(set2 leq set1)
    }

    @Test
    fun `should compare two equal lattice sets with leq`() {
        val set1 = latticeSetOf(1, 2)
        val set2 = latticeSetOf(1, 2)
        Assert.assertTrue(set1 leq set2)
        Assert.assertTrue(set2 leq set1)
        Assert.assertTrue(set1 leq set1)
        Assert.assertTrue(set2 leq set2)
    }

    @Test
    fun `should not compare incomparable lattice sets with leq`() {
        val set1 = latticeSetOf(1, 2)
        val set2 = latticeSetOf(2, 3, 4)
        Assert.assertFalse(set1 leq set2)
        Assert.assertFalse(set2 leq set1)
    }

    @Test
    fun `should compare two lattice sets with less`() {
        val set1 = latticeSetOf(1, 2)
        val set2 = latticeSetOf(1, 2, 3)
        Assert.assertTrue(set1 less set2)
        Assert.assertFalse(set2 less set1)
    }

    @Test
    fun `should compare two equal lattice sets with less`() {
        val set1 = latticeSetOf(1, 2)
        val set2 = latticeSetOf(1, 2)
        Assert.assertFalse(set1 less set2)
        Assert.assertFalse(set2 less set1)
        Assert.assertFalse(set1 less set1)
        Assert.assertFalse(set2 less set2)
    }

    @Test
    fun `should compare two incomparable lattice sets with less`() {
        val set1 = latticeSetOf(1, 2)
        val set2 = latticeSetOf(2, 3)
        Assert.assertFalse(set1 less set2)
        Assert.assertFalse(set2 less set1)
    }


    @Test
    fun `should compare two not equal lattice sets with eq`() {
        val set1 = latticeSetOf(1, 2)
        val set2 = latticeSetOf(1, 2, 3)
        Assert.assertFalse(set1 eq set2)
        Assert.assertFalse(set2 eq set1)
    }

    @Test
    fun `should compare two equal lattice sets with eq`() {
        val set1 = latticeSetOf(1, 2)
        val set2 = latticeSetOf(1, 2)
        Assert.assertTrue(set1 eq set2)
        Assert.assertTrue(set2 eq set1)
        Assert.assertTrue(set1 eq set1)
        Assert.assertTrue(set2 eq set2)
    }

    @Test
    fun `should compare two not equal lattice sets with noteq`() {
        val set1 = latticeSetOf(1, 2)
        val set2 = latticeSetOf(1, 2, 3)
        Assert.assertTrue(set1 noteq set2)
        Assert.assertTrue(set2 noteq set1)
    }

    @Test
    fun `should compare two equal lattice sets with noteq`() {
        val set1 = latticeSetOf(1, 2)
        val set2 = latticeSetOf(1, 2)
        Assert.assertFalse(set1 noteq set2)
        Assert.assertFalse(set2 noteq set1)
        Assert.assertFalse(set1 noteq set1)
        Assert.assertFalse(set2 noteq set2)
    }

    @Test
    fun `should merge two lattice sets`() {
        val set1 = latticeSetOf(1, 2, 3)
        val set2 = latticeSetOf(3, 4, 5)
        val mergeSet = set1 merge set2
        Assert.assertTrue(mergeSet.containsAll(setOf(1, 2, 3, 4, 5)))
        Assert.assertEquals(5, mergeSet.size)
    }


}