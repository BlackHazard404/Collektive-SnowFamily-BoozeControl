package snowFamily

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.aggregate.FieldEntry
import it.unibo.collektive.aggregate.api.Aggregate
import kotlin.math.abs
import kotlin.math.ln

/**
 * Number of nodes in the net. Hard-coded in this case, but can be obtained at run-time
 */
const val N = 450.0

/**
 * Given the number of times that a certain opinion has obtained majority in a round,
 * tau is the difference between the two of them that has to be observed for a node
 * to terminate while abiding to the Blizzard protocol.
 */
val tau = ln(N) + B

/**
 * Operates like the Slush protocol, but terminates nodes when they observe |n1 - n0| > tau, where n1 is
 * the number of times the opinion 1 has obtained majority in a round, and n0 the same for opinion 0.
 * @param collektiveDevice the node computing the Blizzard function.
 */
fun Aggregate<Int>.blizzard(collektiveDevice: CollektiveDevice<*>): Pair<Int,Boolean> {
    val terminated = collektiveDevice.get<Boolean>("terminated")
    val oOpinion = collektiveDevice.get<Int>("opinion")
    if (!terminated) {
        // a causa del parametro B che viene cambiato ad ogni maggioranza riuso slush
        val details = slush(collektiveDevice)
        val nOpinion = details[0]
        val nMajZero = collektiveDevice.get<Int>("nMajZero")
        val nMajOne = collektiveDevice.get<Int>("nMajOne")

        // Comportamento di snowflake
        if (nOpinion == 0) {
            collektiveDevice["nMajZero"] = nMajZero + 1
        } else if (nOpinion == 1) {
            collektiveDevice["nMajOne"] = nMajOne + 1
        }

        if (abs(nMajZero - nMajOne) > tau) {
            collektiveDevice["terminated"] = true
        }

        return Pair(nOpinion,collektiveDevice.getOrDefault<Boolean>("terminated", false))
    } else {
        return Pair(oOpinion, true) // non può interrogare la rete, ritorna il valore di terminazione
    }
}
