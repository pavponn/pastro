package com.github.pavponn.configuration

import com.github.pavponn.transaction.Transaction
import com.github.pavponn.utils.DefaultValues.INIT_SENDER
import com.github.pavponn.utils.DefaultValues.INIT_TRANSACTION_ID
import org.junit.Assert
import org.junit.Test

/**
 * @author pavponn
 */
class ConfigurationTest {

    companion object {
        private const val TOTAL_STAKE = 45
        private val TR_INIT = Transaction(
            INIT_TRANSACTION_ID,
            INIT_SENDER,
            mapOf(
                1 to 10,
                2 to 22,
                3 to 13
            ),
            emptySet()
        )

        private val TR_1_BY_1 = Transaction(
            1,
            1,
            mapOf(
                1 to 5,
                2 to 5,
                3 to 0
            ),
            setOf(Pair(INIT_SENDER, INIT_TRANSACTION_ID))
        )

        private val TR_1_BY_2 = Transaction(
            1,
            2,
            mapOf(
                1 to 3,
                2 to 7,
                3 to 12
            ),
            setOf(Pair(INIT_SENDER, INIT_TRANSACTION_ID))
        )

        private val TR_1_BY_3 = Transaction(
            1,
            3,
            mapOf(
                1 to 15,
                2 to 8,
                3 to 2
            ),
            setOf(
                Pair(INIT_SENDER, INIT_TRANSACTION_ID),
                Pair(2, 1)
            )
        )

        private val TR_2_BY_2 = Transaction(
            2,
            2,
            mapOf(
                1 to 1,
                2 to 3,
                3 to 1
            ),
            setOf(Pair(1, 1))
        )
    }

    @Test
    fun `should calculate total stake correctly with one transaction`() {
        val configuration = PastroConfiguration(setOf(TR_INIT))
        val result = configuration.getTotalStake()
        Assert.assertEquals(TOTAL_STAKE, result)
    }

    @Test
    fun `should calculate total stake correctly with two transactions`() {
        val configuration = PastroConfiguration(setOf(TR_INIT, TR_1_BY_1))
        val result = configuration.getTotalStake()
        Assert.assertEquals(TOTAL_STAKE, result)
    }

    @Test
    fun `should calculate total stake correctly with several transactions`() {
        val configuration = PastroConfiguration(setOf(TR_INIT, TR_1_BY_1, TR_1_BY_2, TR_1_BY_3, TR_2_BY_2))
        val result = configuration.getTotalStake()
        Assert.assertEquals(TOTAL_STAKE, result)
    }

    @Test
    fun `should calculate stake for every participant correctly with one transaction`() {
        val configuration = PastroConfiguration(setOf(TR_INIT))
        val stakeDistribution = configuration.getStakeDistribution()
        Assert.assertEquals(10, stakeDistribution.getOrDefault(1, 0))
        Assert.assertEquals(22, stakeDistribution.getOrDefault(2, 0))
        Assert.assertEquals(13, stakeDistribution.getOrDefault(3, 0))

        Assert.assertEquals(10, configuration.getProcessStake(1))
        Assert.assertEquals(22, configuration.getProcessStake(2))
        Assert.assertEquals(13, configuration.getProcessStake(3))
    }

    @Test
    fun `should calculate stake for every participant correctly with two transactions`() {
        val configuration = PastroConfiguration(setOf(TR_INIT, TR_1_BY_2))
        val stakeDistribution = configuration.getStakeDistribution()
        Assert.assertEquals(13, stakeDistribution.getOrDefault(1, 0))
        Assert.assertEquals(7, stakeDistribution.getOrDefault(2, 0))
        Assert.assertEquals(25, stakeDistribution.getOrDefault(3, 0))

        Assert.assertEquals(13, configuration.getProcessStake(1))
        Assert.assertEquals(7, configuration.getProcessStake(2))
        Assert.assertEquals(25, configuration.getProcessStake(3))
    }

    @Test
    fun `should calculate stake for every participant with several transactions`() {
        val configuration = PastroConfiguration(setOf(TR_INIT, TR_1_BY_2, TR_1_BY_1, TR_1_BY_3, TR_2_BY_2))
        val stakeDistribution = configuration.getStakeDistribution()
        Assert.assertEquals(24, stakeDistribution.getOrDefault(1, 0))
        Assert.assertEquals(18, stakeDistribution.getOrDefault(2, 0))
        Assert.assertEquals(3, stakeDistribution.getOrDefault(3, 0))

        Assert.assertEquals(24, configuration.getProcessStake(1))
        Assert.assertEquals(18, configuration.getProcessStake(2))
        Assert.assertEquals(3, configuration.getProcessStake(3))
    }

    @Test
    fun `should determine whether set of process is a quorum in one transaction configuration`() {
        val configuration = PastroConfiguration(setOf(TR_INIT))

        Assert.assertEquals(false, configuration.hasQuorum(setOf(1)))
        Assert.assertEquals(false, configuration.hasQuorum(setOf(2)))
        Assert.assertEquals(false, configuration.hasQuorum(setOf(3)))

        Assert.assertEquals(true, configuration.hasQuorum(setOf(1, 2)))
        Assert.assertEquals(false, configuration.hasQuorum(setOf(1, 3)))
        Assert.assertEquals(true, configuration.hasQuorum(setOf(2, 3)))

        Assert.assertEquals(true, configuration.hasQuorum(setOf(1, 2, 3)))
    }


    @Test
    fun `should determine whether set of process is a quorum in two transactions configuration`() {
        val configuration = PastroConfiguration(setOf(TR_INIT, TR_1_BY_2))

        Assert.assertEquals(false, configuration.hasQuorum(setOf(1)))
        Assert.assertEquals(false, configuration.hasQuorum(setOf(2)))
        Assert.assertEquals(false, configuration.hasQuorum(setOf(3)))

        Assert.assertEquals(false, configuration.hasQuorum(setOf(1, 2)))
        Assert.assertEquals(true, configuration.hasQuorum(setOf(1, 3)))
        Assert.assertEquals(true, configuration.hasQuorum(setOf(2, 3)))

        Assert.assertEquals(true, configuration.hasQuorum(setOf(1, 2, 3)))
    }

    @Test
    fun `should determine whether set of process is a quorum in several transactions configuration`() {
        val configuration = PastroConfiguration(setOf(TR_INIT, TR_1_BY_2, TR_1_BY_1, TR_2_BY_2, TR_1_BY_3))

        Assert.assertEquals(false, configuration.hasQuorum(setOf(1)))
        Assert.assertEquals(false, configuration.hasQuorum(setOf(2)))
        Assert.assertEquals(false, configuration.hasQuorum(setOf(3)))

        Assert.assertEquals(true, configuration.hasQuorum(setOf(1, 2)))
        Assert.assertEquals(false, configuration.hasQuorum(setOf(1, 3)))
        Assert.assertEquals(false, configuration.hasQuorum(setOf(2, 3)))

        Assert.assertEquals(true, configuration.hasQuorum(setOf(1, 2, 3)))
    }

}
