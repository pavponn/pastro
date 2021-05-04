package com.github.pavponn.history

import com.github.pavponn.configuration.ConfigurationLattice
import com.github.pavponn.configuration.PastroConfiguration
import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.DefaultValues
import org.junit.Assert
import org.junit.Test
import java.lang.IllegalArgumentException

/**
 * @author pavponn
 */
class HistoryTest {

    companion object {
        private val TRANSACTION_1 = Transaction(
            DefaultValues.INIT_TRANSACTION_ID,
            DefaultValues.INIT_SENDER,
            mapOf(
                1 to 7,
                2 to 5,
                3 to 7,
                4 to 6
            ),
            emptySet()
        )

        private val TRANSACTION_2 = Transaction(
            DefaultValues.INIT_TRANSACTION_ID,
            DefaultValues.INIT_SENDER,
            mapOf(
                1 to 202,
                3 to 7,
                4 to 5,
                6 to 7
            ),
            emptySet()
        )

        private val TRANSACTION_3 = Transaction(
            DefaultValues.INIT_TRANSACTION_ID,
            DefaultValues.INIT_SENDER,
            mapOf(
                1 to 2,
                2 to 3,
                4 to 3
            ),
            emptySet()
        )

        private val TRANSACTION_4 = Transaction(
            DefaultValues.INIT_TRANSACTION_ID,
            DefaultValues.INIT_SENDER,
            mapOf(
                2 to 1,
                2 to 30,
                5 to 100
            ),
            emptySet()
        )

        val CONFIGURATION_1 = PastroConfiguration(setOf(TRANSACTION_1))
        val CONFIGURATION_2 = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2))
        val CONFIGURATION_3 = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3))
        val CONFIGURATION_4 = PastroConfiguration(setOf(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3, TRANSACTION_4))

        val CONFIGURATION_EXTRA = PastroConfiguration(setOf(TRANSACTION_2))
    }

    @Test
    fun `should create history from one configuration`() {
        val configuration = PastroConfiguration(setOf(TRANSACTION_1))
        PastroHistory(listOf(configuration))
        PastroHistory(setOf(configuration))
    }

    @Test
    fun `should create history from comparable configurations`() {
        PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw when creating history from not comparable configurations`() {
        PastroHistory(listOf(CONFIGURATION_1, CONFIGURATION_EXTRA, CONFIGURATION_3))
    }

    @Test
    fun `should return configurations ordered`() {
        val history = PastroHistory(listOf(CONFIGURATION_2, CONFIGURATION_3, CONFIGURATION_1))
        val orderedConfigurations = history.orderedConfigs()
        Assert.assertEquals(CONFIGURATION_1, orderedConfigurations[0])
        Assert.assertEquals(CONFIGURATION_2, orderedConfigurations[1])
        Assert.assertEquals(CONFIGURATION_3, orderedConfigurations[2])
        Assert.assertEquals(3, orderedConfigurations.size)
    }

    @Test
    fun `should return configurations ordered without duplicates`() {
        val history = PastroHistory(
            listOf(
                CONFIGURATION_2,
                CONFIGURATION_3,
                CONFIGURATION_1,
                CONFIGURATION_2
            )
        )
        val orderedConfigurations = history.orderedConfigs()
        Assert.assertEquals(CONFIGURATION_1, orderedConfigurations[0])
        Assert.assertEquals(CONFIGURATION_2, orderedConfigurations[1])
        Assert.assertEquals(CONFIGURATION_3, orderedConfigurations[2])
        Assert.assertEquals(3, orderedConfigurations.size)
    }

    @Test
    fun `should return ordered configurations starting from specified`() {
        val history = PastroHistory(
            listOf(
                CONFIGURATION_2,
                CONFIGURATION_4,
                CONFIGURATION_3,
                CONFIGURATION_1
            )
        )
        val orderedConfigurations = history.orderedConfigs(CONFIGURATION_3)
        Assert.assertEquals(CONFIGURATION_3, orderedConfigurations[0])
        Assert.assertEquals(CONFIGURATION_4, orderedConfigurations[1])
        Assert.assertEquals(2, orderedConfigurations.size)
    }

    @Test
    fun `should return ordered configurations greater than specified if no specified`() {
        val history = PastroHistory(listOf(CONFIGURATION_4, CONFIGURATION_3, CONFIGURATION_1))
        val orderedConfigurations = history.orderedConfigs(CONFIGURATION_2)
        Assert.assertEquals(CONFIGURATION_3, orderedConfigurations[0])
        Assert.assertEquals(CONFIGURATION_4, orderedConfigurations[1])
        Assert.assertEquals(2, orderedConfigurations.size)
    }

    @Test
    fun `should return empty result when all configurations are less then specified`() {
        val history = PastroHistory(listOf(CONFIGURATION_3, CONFIGURATION_1, CONFIGURATION_2))
        val orderedConfigurations = history.orderedConfigs(CONFIGURATION_4)
        Assert.assertEquals(emptyList<ConfigurationLattice>(), orderedConfigurations)
    }

    @Test
    fun `should return greatest configuration`() {
        val history = PastroHistory(listOf(CONFIGURATION_4, CONFIGURATION_1, CONFIGURATION_3))
        val greatestConfig = history.greatestConfig()
        Assert.assertEquals(CONFIGURATION_4, greatestConfig)
    }
}