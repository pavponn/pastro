package com.github.pavponn.fsds

import com.github.pavponn.message.Message
import com.github.pavponn.utils.ProcessId
import com.github.pavponn.utils.Signature
import com.github.pavponn.utils.Timestamp

/**
 * @author pavponn
 *
 * This class is a stub implementation for Forward Secure Digital Signatures.
 */
class ForwardSecureDigitalSignaturesBasic: ForwardSecureDigitalSignatures {
    override fun updateFSKey(timestamp: Timestamp) {
        // intentionally left empty
    }

    override fun signFS(message: Message, timestamp: Timestamp): Signature {
        return ""
    }

    override fun verifyFS(message: Message, process: ProcessId, signature: Signature, timestamp: Timestamp): Boolean {
        return true
    }
}