package com.github.pavponn.message.checker

import com.github.pavponn.history.HistoryLattice

/**
 * @author pavponn
 */
data class HistoryMessage(val history: HistoryLattice): CheckerMessage
