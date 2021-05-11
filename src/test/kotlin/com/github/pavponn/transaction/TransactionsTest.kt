package com.github.pavponn.transaction

import com.github.pavponn.utils.DefaultValues.DEFAULT_CERTIFICATE
import org.junit.Assert
import org.junit.Test

/**
 * @author pavponn
 */
class TransactionsTest {

    @Test
    fun `should correctly sign transaction`() {
        val transaction = Transaction(
            1,
            1,
            mapOf(2 to 5),
            emptySet()
        )
        val certificate = signTransaction(transaction)

        Assert.assertTrue(verifySender(transaction, certificate))
    }

    @Test
    fun `should not accept incorrectly signed transactions`() {
        val transactionOne = Transaction(
            1,
            1,
            mapOf(3 to 4),
            emptySet()
        )

        val transactionTwo = Transaction(
            1,
            2,
            mapOf(2 to 1),
            emptySet()
        )

        val certificate = signTransaction(transactionTwo)

        Assert.assertFalse(verifySender(transactionOne, certificate))
    }

    @Test
    fun `should not accept invalid certificates`() {
        val transaction = Transaction(
            1,
            1,
            mapOf(3 to 4),
            emptySet()
        )

        Assert.assertFalse(verifySender(transaction, DEFAULT_CERTIFICATE))
    }

    @Test
    fun `should determine conflicts for two transactions with intersection dependency sets`() {
        val transactionOne = Transaction(
            1,
            1,
            mapOf(3 to 4),
            setOf(2 to 1, 3 to 1, 4 to 1)
        )
        val transactionTwo = Transaction(
            2,
            1,
            mapOf(1 to 4),
            setOf(10 to 1, 1 to 1, 3 to 1, 5 to 2)
        )

        Assert.assertTrue(conflict(transactionOne, transactionTwo))
    }

    @Test
    fun `should determine conflicts for two transactions with same sequence numbers`() {
        val transactionOne = Transaction(
            1,
            1,
            mapOf(3 to 4),
            setOf(2 to 1, 3 to 1, 4 to 1)
        )
        val transactionTwo = Transaction(
            1,
            1,
            mapOf(1 to 4),
            setOf(5 to 1, 6 to 1)
        )

        Assert.assertTrue(conflict(transactionOne, transactionTwo))
    }

    @Test
    fun `should not determine conflicts for two transaction issued by different processes`() {
        val transactionOne = Transaction(
            1,
            1,
            mapOf(3 to 4),
            setOf(2 to 1, 3 to 1, 4 to 1)
        )
        val transactionTwo = Transaction(
            1,
            2,
            mapOf(1 to 4),
            setOf(2 to 1, 3 to 1)
        )

        Assert.assertFalse(conflict(transactionOne, transactionTwo))
    }

    @Test
    fun `should confirm validness of transaction`() {
        val transaction = Transaction(
            1,
            1,
            mapOf(3 to 4, 4 to 5),
            setOf(2 to 1, 5 to 1)
        )

        val transactionByTwo = Transaction(
            1,
            2,
            mapOf(1 to 3, 2 to 5),
            setOf()
        )

        val transactionByFive = Transaction(
            1,
            5,
            mapOf(1 to 6, 7 to 4),
            setOf()
        )

        val seenTransactions = setOf(transactionByTwo, transactionByFive)

        Assert.assertTrue(isValid(transaction, seenTransactions))
        Assert.assertTrue(isValidAndWellFormed(transaction, seenTransactions))
    }

}