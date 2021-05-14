package com.github.pavponn.history

import com.github.pavponn.configuration.ConfigurationLatticeTest
import com.github.pavponn.configuration.PastroConfiguration
import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.DefaultValues
import org.junit.Assert
import org.junit.Test

/**
 * @author pavponn
 */
class HistoryLatticeTest {

    companion object {
        private val TRANSACTION_1 = Transaction(
            DefaultValues.INIT_TRANSACTION_ID,
            DefaultValues.INIT_SENDER,
            mapOf(4 to 6),
            emptySet()
        )

        private val TRANSACTION_2 = Transaction(
            DefaultValues.INIT_TRANSACTION_ID,
            DefaultValues.INIT_SENDER,
            mapOf(3 to 7),
            emptySet()
        )

        private val TRANSACTION_3 = Transaction(
            DefaultValues.INIT_TRANSACTION_ID,
            DefaultValues.INIT_SENDER,
            mapOf(2 to 2),
            emptySet()
        )

        private val TRANSACTION_4 = Transaction(
            DefaultValues.INIT_TRANSACTION_ID,
            DefaultValues.INIT_SENDER,
            mapOf(1 to 1),
            emptySet()
        )

        val CONFIGURATION_1 = PastroConfiguration(setOf(TRANSACTION_1))
        val CONFIGURATION_2 = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2))
        val CONFIGURATION_3 = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3))
        val CONFIGURATION_4 = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3, TRANSACTION_4))
    }

    @Test
    fun `should correctly compare comparable (non equal) histories with leq`() {
        val historyOne = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        val historyTwo = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3))
        Assert.assertTrue(historyOne leq historyTwo)
    }

    @Test
    fun `should correctly compare equal histories with leq`() {
        val historyOne = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        val historyTwo = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        Assert.assertTrue(historyOne leq historyTwo)
        Assert.assertTrue(historyOne leq historyOne)
    }

    @Test
    fun `should correctly compare comparable histories with less`() {
        val historyOne = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        val historyTwo = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3))
        Assert.assertTrue(historyOne less historyTwo)
    }

    @Test
    fun `should correctly compare equal histories with less`() {
        val historyOne = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        val historyTwo = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        Assert.assertFalse(historyOne less historyTwo)
        Assert.assertFalse(historyTwo less historyOne)
        Assert.assertFalse(historyTwo less historyTwo)
        Assert.assertFalse(historyOne less historyOne)
    }

    @Test
    fun `should correctly compare equal histories with eq`() {
        val historyOne = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        val historyTwo = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        Assert.assertTrue(historyOne eq historyTwo)
        Assert.assertTrue(historyTwo eq historyOne)
        Assert.assertTrue(historyTwo eq historyTwo)
        Assert.assertTrue(historyOne eq historyOne)
    }

    @Test
    fun `should correctly compare non equal histories with eq`() {
        val historyOne = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        val historyTwo = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3))
        Assert.assertFalse(historyOne eq historyTwo)
        Assert.assertFalse(historyTwo eq historyOne)
    }

    @Test
    fun `should correctly compare equal histories with noteq`() {
        val historyOne = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        val historyTwo = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        Assert.assertFalse(historyOne noteq historyTwo)
        Assert.assertFalse(historyTwo noteq historyOne)
    }

    @Test
    fun `should correctly compare non equal histories with noteq`() {
        val historyOne = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        val historyTwo = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3))
        Assert.assertTrue(historyOne noteq historyTwo)
        Assert.assertTrue(historyTwo noteq historyOne)
    }

    @Test
    fun `should correctly compare incomparable histories with leq`() {
        val historyOne = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        val historyTwo = PastroHistory(listOf(CONFIGURATION_2, CONFIGURATION_3))
        Assert.assertFalse(historyOne leq historyTwo)
        Assert.assertFalse(historyTwo leq historyOne)
    }

    @Test
    fun `should correctly compare incomparable histories with less`() {
        val historyOne = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        val historyTwo = PastroHistory(listOf(CONFIGURATION_2, CONFIGURATION_3))
        Assert.assertFalse(historyOne less historyTwo)
        Assert.assertFalse(historyTwo less historyOne)
    }

    @Test
    fun `should correctly compare incomparable histories with eq`() {
        val historyOne = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        val historyTwo = PastroHistory(listOf(CONFIGURATION_2, CONFIGURATION_3))
        Assert.assertFalse(historyOne eq historyTwo)
        Assert.assertFalse(historyTwo eq historyOne)
    }

    @Test
    fun `should correctly compare incomparable histories with noteq`() {
        val historyOne = PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2))
        val historyTwo = PastroHistory(listOf(CONFIGURATION_2, CONFIGURATION_3))
        Assert.assertTrue(historyOne noteq historyTwo)
        Assert.assertTrue(historyTwo noteq historyOne)
    }

}
