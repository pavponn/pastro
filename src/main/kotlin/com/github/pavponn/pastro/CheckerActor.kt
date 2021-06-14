package com.github.pavponn.pastro

import akka.actor.AbstractActor
import akka.actor.Props
import com.github.pavponn.configuration.ConfigurationLattice
import com.github.pavponn.history.HistoryLattice
import com.github.pavponn.message.Message
import com.github.pavponn.message.checker.*
import com.github.pavponn.message.settings.HolderMessage
import com.github.pavponn.message.settings.SettingsMessage
import com.github.pavponn.transaction.Transaction
import com.github.pavponn.transaction.conflict
import com.github.pavponn.utils.ProcessId

/**
 * @author pavponn
 *
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

    private val transactions: MutableSet<Pair<Transaction, ProcessId>> = mutableSetOf()
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
            is SettingsMessage -> {
                handleSettingsMessage(message)
            }
            else -> {
                // ignore
            }
        }
    }

    private fun handleCheckerMessage(message: CheckerMessage) {
        when (message) {
            is TransactionMessage -> {
                val from = getSenderId()
                transactions.add(message.transaction to from)
            }
            is ConfigurationMessage -> {
                val from = getSenderId()
                configurations.add(message.configuration to from)
            }
            is HistoryMessage -> {
                val from = getSenderId()
                histories.add(message.history to from)
            }
            is CheckTransactionsMessage -> {
                checkTransactions()
            }
            is CheckConfigurationsMessage -> {
                checkConfigurations()
            }
            is CheckHistoriesMessage -> {
                checkHistories()
            }
        }
    }

    private fun handleSettingsMessage(message: SettingsMessage) {
        when (message) {
            is HolderMessage -> {
                val holder = message.historyHolder
                histories.add(holder.getHistory() to 0)
                configurations.add(holder.getConfigInstalled() to 0)
                holder.getConfigInstalled().getTransactions().forEach {
                    transactions.add(it to 0)
                }
            }
        }
    }

    private fun checkTransactions() {
        var nonConflicting = true
        transactions.forEach { fst ->
            transactions.forEach { snd ->
                val fstTransaction = fst.first
                val fstFrom = fst.second
                val sndTransaction = snd.first
                val sndFrom = snd.second
                if (conflict(fstTransaction, sndTransaction)) {
                    nonConflicting = false
                    println("======")
                    println("Processes: p$fstFrom and p$sndFrom")
                    println("Validated transactions $fstTransaction and $sndTransaction conflict!")
                    println("======")
                }
            }
        }
        if (nonConflicting) {
            println("All validated transaction don't conflict")
        }
    }

    private fun checkConfigurations() {
        var onlyValidatedTransaction = true
        configurations.forEach {
            it.first.getTransactions().forEach { transaction ->
                if (!transactions.map { c -> c.first }.toSet().contains(transaction)) {
                    onlyValidatedTransaction = false
                    println("No such verifiable transaction $transaction")
                }
            }
        }
        if (onlyValidatedTransaction) {
            println("Verifiable configurations contain only validated transactions")
        }
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
    }

    private fun checkHistories() {
        var onlyVerifiableConfigurations = true
        histories.forEach {
            it.first.orderedConfigs().forEach { config ->
                if (!configurations.map { c -> c.first }.toSet().contains(config)) {
                    onlyVerifiableConfigurations = false
                    println("No such verifiable configuration ${config.getTransactions()}")
                }
            }
        }

        if (onlyVerifiableConfigurations) {
            println("Verifiable histories contain only verifiable configurations")
        }

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