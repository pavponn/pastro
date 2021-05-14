package com.github.pavponn.message.settings

import akka.actor.ActorRef
import com.github.pavponn.message.Message

/**
 * @author pavponn
 */
data class CheckerRefMessage(val checker: ActorRef): SettingsMessage
