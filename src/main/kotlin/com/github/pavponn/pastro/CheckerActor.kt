package com.github.pavponn.pastro

import akka.actor.AbstractActor
import akka.actor.Props
import com.github.pavponn.configuration.ConfigurationLattice
import com.github.pavponn.history.HistoryLattice
import com.github.pavponn.message.Message
import com.github.pavponn.message.checker.*
import com.github.pavponn.utils.ProcessId

/**
 * @author pavponn
 * Class that implements checking logic.
 * It receives all the configurations and histories produced by the algorithm and
 * checks whether they are all comparable.
 */
class CheckerActor : AbstractActor() {

    companion object {
        @JvmStatic
        fun createActor(): Props {
            return Props.create(CheckerActor::class.java, ::CheckerActor)
        }
    }

    private val configurations: MutableSet<Pair<ConfigurationLattice, ProcessId>> = mutableSetOf()
    private val histories: MutableSet<Pair<HistoryLattice, ProcessId>> = mutableSetOf()

    override fun createReceive(): Receive {
        return receiveBuilder()
            .match(Message::class.java, ::onReceiveMessage)
            .build()
    }

    private fun onReceiveMessage(message: Message) {
        when (message) {
            is CheckerMessage -> {
                handleCheckerMessage(message)
            }
            else -> {
                // ignore
            }
        }
    }

    private fun handleCheckerMessage(message: CheckerMessage) {
        when (message) {
            is ConfigurationMessage -> {
                val from = getSenderId()
                configurations.add(message.configuration to from)
            }
            is HistoryMessage -> {
                val from = getSenderId()
                histories.add(message.history to from)
            }
            is CheckConfigurationsMessage -> {
                checkConfigurations()
            }
            is CheckHistoriesMessage -> {
                checkHistories()
            }
        }
    }

    private fun checkConfigurations() {
        var comparable = true
        configurations.forEach { fst ->
            configurations.forEach { snd ->
                val fstConfig = fst.first
                val fstFrom = fst.second
                val sndConfig = snd.first
                val sndFrom = snd.second
                if (!fstConfig.leq(sndConfig) && sndConfig.leq(fstConfig).not()) {
                    comparable = false
                    println("======")
                    println("Processes: p$fstFrom and p$sndFrom")
                    println("Configurations ${fstConfig.getTransactions()} and ${sndConfig.getTransactions()} are not comparable!")
                    println("======")
                }
            }
        }
        if (comparable) {
            println("All configurations are comparable")
        }
        // TODO check transactions
    }

    private fun checkHistories() {
        var comparable = true
        histories.forEach { fst ->
            histories.forEach { snd ->
                val fstHistory = fst.first
                val fstFrom = fst.second
                val sndHistory = snd.first
                val sndFrom = snd.second
                if (fstHistory.leq(sndHistory).not() && sndHistory.leq(fstHistory).not()) {
                    comparable = false
                    println("======")
                    println("Processes: p$fstFrom and p$sndFrom")
                    println("Histories $fstHistory and $sndHistory are not comparable!")
                    println("======")
                }
            }
        }
        if (comparable) {
            println("All histories are comparable")
        }
    }

    private fun getSenderId(): Int {
        return Integer.parseInt(sender.path().name().substring(1))
    }
}