package com.github.pavponn.message.settings

import akka.actor.ActorRef

/**
 * @author pavponn
 */
data class CheckerRefMessage(val checker: ActorRef): SettingsMessage
