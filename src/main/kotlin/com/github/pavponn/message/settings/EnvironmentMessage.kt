package com.github.pavponn.message.settings

import akka.actor.ActorRef

/**
 * @author pavponn
 */
data class EnvironmentMessage(val processId: Int, val actorRefs: Array<ActorRef>) : SettingsMessage {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EnvironmentMessage

        if (processId != other.processId) return false
        if (!actorRefs.contentEquals(other.actorRefs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = processId
        result = 31 * result + actorRefs.contentHashCode()
        return result
    }
}
