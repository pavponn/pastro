package com.github.pavponn.message.agreement

import com.github.pavponn.message.Message

/**
 * @author pavponn
 *
 * A common interface for  messages for ABLA objects.
 */
interface ABLAMessage : Message {

    val objectName: String
}
