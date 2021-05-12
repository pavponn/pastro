package com.github.pavponn

import akka.actor.ActorRef
import akka.actor.ActorSystem
import com.github.pavponn.holder.HistoryHolder
import com.github.pavponn.message.settings.EnvironmentMessage
import com.github.pavponn.message.settings.HolderMessage
import com.github.pavponn.pastro.PastroProcess

/**
 * @author pavponn
 */
fun main() {
    val nProcesses: Int = 5
    val system: ActorSystem = ActorSystem.create("system")
    val processes: MutableList<ActorRef> = mutableListOf()
    val historyHolder = createHolder(nProcesses)

    IntRange(1, nProcesses).forEach {
        processes.add(system.actorOf(PastroProcess.createActor(), "p$it"))
    }

    IntRange(1, nProcesses).forEach {
        processes[it - 1].tell(EnvironmentMessage(it, processes.toTypedArray()), ActorRef.noSender())
    }

    IntRange(1, nProcesses).forEach {
        processes[it - 1].tell(HolderMessage(historyHolder), ActorRef.noSender())
    }

}


fun createHolder(n: Int): HistoryHolder {
    TODO()
}
