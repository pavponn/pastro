package com.github.pavponn.pastro

import com.github.pavponn.configuration.ConfigurationLattice
import com.github.pavponn.history.HistoryLattice
import com.github.pavponn.utils.Certificate

/**
 * @author pavponn
 */
class PastroHistoryHolder(historyInit: HistoryLattice, certificateInit: Certificate) : HistoryHolder {
    init {
        require(historyInit.orderedConfigs().size == 1) {
            "Can't create HistoryHolder: initial history is big"
        }
    }

    /**
     * Corresponds to 'history' in Pastro article.
     */
    private var historyCurrent = historyInit

    /**
     * Corresponds to 'σ_{hist}' in Pastro article.
     */
    private var historyCertificate = certificateInit

    /**
     * Corresponds to 'C_{inst}' in Pastro article.
     */
    private var configurationInstalled = historyInit.orderedConfigs()[0]

    /**
     * Corresponds to 'C_{cur}' in Pastro article.
     */
    private var configurationCur = historyInit.orderedConfigs()[0]


    override fun getHistory(): HistoryLattice {
        return historyCurrent
    }

    override fun getHistoryCertificate(): Certificate {
        return historyCertificate
    }

    override fun updateHistory(history: HistoryLattice, certificate: Certificate) {
        // TODO: certificate checking
        if (this.historyCurrent leq history) {
            this.historyCurrent = history
        }
    }

    override fun getConfigInstalled(): ConfigurationLattice {
        return configurationInstalled
    }

    override fun updateInstalledConfig(config: ConfigurationLattice) {
        if (configurationInstalled leq config && getHistory().contains(config)) {
            configurationInstalled = config
        }
    }

    override fun getConfigStateTransfer(): ConfigurationLattice {
        return configurationCur
    }

    override fun updateConfigStateTransfer(config: ConfigurationLattice) {
        if (configurationCur leq config && getHistory().contains(config)) {
            configurationCur = config
        }
    }

}