package com.github.pavponn.message

/**
 * @author pavponn
 */
class UnknownMessageException(message: Message): Exception("Message $message has unknown type")
