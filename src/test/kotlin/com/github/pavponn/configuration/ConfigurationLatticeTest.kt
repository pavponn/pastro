package com.github.pavponn.configuration

import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.DefaultValues
import org.junit.Assert
import org.junit.Test

/**
 * @author pavponn
 */
class ConfigurationLatticeTest {

    companion object {
        val TRANSACTION_1 = Transaction(
            DefaultValues.INIT_TRANSACTION_ID,
            DefaultValues.INIT_SENDER,
            mapOf(
                1 to 10,
                2 to 22,
                3 to 13
            ),
            emptySet()
        )

        val TRANSACTION_2 = Transaction(
            DefaultValues.INIT_TRANSACTION_ID,
            DefaultValues.INIT_SENDER,
            mapOf(
                2 to 15,
                3 to 32,
                4 to 23
            ),
            emptySet()
        )

        val TRANSACTION_3 = Transaction(
            DefaultValues.INIT_TRANSACTION_ID,
            DefaultValues.INIT_SENDER,
            mapOf(
                2 to 111,
                4 to 215,
                1 to 231
            ),
            emptySet()
        )

        val TRANSACTION_4 = Transaction(
            DefaultValues.INIT_TRANSACTION_ID,
            DefaultValues.INIT_SENDER,
            mapOf(
                1 to 12,
                2 to 14,
                3 to 15
            ),
            emptySet()
        )


    }

    @Test
    fun `should correctly compare comparable (non equal) configurations with leq`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3))
        Assert.assertTrue(configurationOne leq configurationTwo)
    }

    @Test
    fun `should correctly compare equal configurations with leq`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3))
        Assert.assertTrue(configurationOne leq configurationTwo)
        Assert.assertTrue(configurationOne leq configurationOne)
    }

    @Test
    fun `should correctly compare comparable configurations with less`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3))
        Assert.assertTrue(configurationOne less configurationTwo)
    }

    @Test
    fun `should correctly compare equal configurations with less`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3))
        Assert.assertFalse(configurationOne less configurationTwo)
        Assert.assertFalse(configurationTwo less configurationOne)
        Assert.assertFalse(configurationOne less configurationOne)
    }

    @Test
    fun `should correctly compare equal configurations with eq`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_3))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_3))
        Assert.assertTrue(configurationOne eq configurationTwo)
        Assert.assertTrue(configurationTwo eq configurationTwo)
        Assert.assertTrue(configurationOne eq configurationOne)
    }

    @Test
    fun `should correctly compare non equal configurations with eq`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_3))
        Assert.assertFalse(configurationOne eq configurationTwo)
    }

    @Test
    fun `should correctly compare equal configurations with noteq`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2))
        Assert.assertFalse(configurationOne noteq configurationTwo)
        Assert.assertFalse(configurationOne noteq configurationOne)
        Assert.assertFalse(configurationTwo noteq configurationTwo)
    }

    @Test
    fun `should correctly compare non equal configurations with noteq`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_3))
        Assert.assertTrue(configurationOne noteq configurationTwo)
    }

    @Test
    fun `should correctly compare incomparable configurations with leq`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_3))
        Assert.assertFalse(configurationOne leq configurationTwo)
        Assert.assertFalse(configurationTwo leq configurationOne)
    }

    @Test
    fun `should correctly compare incomparable configurations with less`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_3))
        Assert.assertFalse(configurationOne less configurationTwo)
        Assert.assertFalse(configurationTwo less configurationOne)
    }

    @Test
    fun `should correctly compare incomparable configurations with eq`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_3))
        Assert.assertFalse(configurationOne eq configurationTwo)
    }

    @Test
    fun `should correctly compare incomparable configurations with noteq`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_3))
        Assert.assertTrue(configurationOne noteq configurationTwo)
    }

    @Test
    fun `should merge two comparable configurations correctly`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_3))
        val mergeConfiguration = configurationOne merge configurationTwo
        Assert.assertTrue(
            mergeConfiguration
                .getTransactions()
                .containsAll(
                    listOf(
                        TRANSACTION_1,
                        TRANSACTION_3
                    )
                )
        )
        Assert.assertEquals(2, mergeConfiguration.getTransactions().size)
    }

    @Test
    fun `should merge two incomparable configurations correctly`() {
        val configurationOne = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2))
        val configurationTwo = PastroConfiguration(setOf(TRANSACTION_3, TRANSACTION_4))
        val mergeConfiguration = configurationOne merge configurationTwo
        Assert.assertTrue(
            mergeConfiguration
                .getTransactions()
                .containsAll(
                    listOf(
                        TRANSACTION_1,
                        TRANSACTION_2,
                        TRANSACTION_3,
                        TRANSACTION_4
                    )
                )
        )
        Assert.assertEquals(4, mergeConfiguration.getTransactions().size)
    }
}
