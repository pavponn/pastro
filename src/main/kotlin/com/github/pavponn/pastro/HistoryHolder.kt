package com.github.pavponn.pastro

import com.github.pavponn.configuration.ConfigurationLattice
import com.github.pavponn.history.HistoryLattice
import com.github.pavponn.utils.Certificate

/**
 * @author pavponn
 */
interface HistoryHolder {

    /**
     * Returns current verifiable history. The histories returned
     * by this function should be comparable  (w.r.t. ⊆) and only can grow (also w.r.t. ⊆)
     * with time.
     */
    fun getHistory(): HistoryLattice

    /**
     * Returns certificate for current verifiable history.
     */
    fun getHistoryCertificate(): Certificate

    /**
     * Updates current verifiable history. The function should only
     * update history to a greater one.
     * What's more, the certificate should be valid.
     */
    fun updateHistory(history: HistoryLattice, certificate: Certificate)

    /**
     * Returns current installed configuration. The configurations returned
     * by this function should be comparable  (w.r.t. ⊑) and only can grow (also w.r.t. ⊑)
     * with time.
     */
    fun getConfigInstalled(): ConfigurationLattice

    /**
     * Updates current installed configuration. The function should only
     * update configuration to a greater one
     * and only if the configuration is part of current history [getHistory].
     */
    fun updateInstalledConfig(config: ConfigurationLattice)

    /**
     * Returns configuration from which next state transfer protocol should be performed.
     * The configurations returned by this function should be comparable (w.r.t. ⊆)
     * and only can grow (also w.r.t. ⊑) with time.
     */
    fun getConfigStateTransfer(): ConfigurationLattice

    /**
     * Updates configuration from which next state transfer protocol should be performed.
     * The function should only update configuration to a greater one
     * and only if the configuration is part of current history [getHistory].
     */
    fun updateConfigStateTransfer(config: ConfigurationLattice)
}