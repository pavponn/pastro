package com.github.pavponn.environment

import com.github.pavponn.message.Message
import com.github.pavponn.utils.ProcessId

/**
 * @author pavponn
 *
 * Common interface for all distributed environments.
 * It provides main functions to work with the system.
 */
interface Environment {

    /**
     * Identifier of this process (from 1 to [nProcesses]).
     */
    val processId: ProcessId

    /**
     * The total number of processes in the system.
     */
    val nProcesses: Int

    /**
     * Sends the specified [message] to the process [toId] (from 1 to [nProcesses]).
     */
    fun send(message: Message, toId: ProcessId)

    /**
     * Broadcasts message over the system.
     */
    fun broadcast(message: Message)
}
