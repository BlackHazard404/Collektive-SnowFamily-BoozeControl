package snowFamily

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.aggregate.values
import kotlin.random.Random

/**
 * Value K as defined in the paper. The number of randomly parsed nodes in the neighborhood
 * Note: it has to be less than the average connectivity*/
const val K = 20

/**
 * Value alpha as defined in the paper. The minimum number of randomly parsed with the same opinion needed
 * to change opinion
 * Note: it has to be > K/2*/
const val A = 13

/**
 * Through the share function, samples k neighbors for their opinion and computes the majority.
 * Nota: bottleneck in the network can lead the protocol to malfunction; this is usually applied in a fully
 * connected network.
 * @param collektiveDevice the sampling node. Each node can have an opinion in [0,1,-1], the latter being none
 */
fun Aggregate<Int>.slush(collektiveDevice: CollektiveDevice<*>): List<Int> {
    var initialOpinion = collektiveDevice.get<Int>("opinion")
    val details = emptyList<Int>().toMutableList()

    val fOpinion = share(initialOpinion) { opinionsField ->
        val allOpinions = opinionsField.neighbors.values.list
        // args check. Iteration is skipped only if nodes that do not have an opinion
        // have been parsed, or if there are no neighbors.
        if (allOpinions.isNotEmpty() && (allOpinions.contains(1) || allOpinions.contains(0))) {
            // Share considers the whole neighborhood; as such, the simulation of an asynchronous system
            // where a node parses another node without opinion is simulated with a random function.
            if (initialOpinion < 0) {
                initialOpinion = allOpinions[Random.nextInt(allOpinions.size)]
            }

            // if a node without opinion was parsed, we consider it as if the node didn't get queried
            if (initialOpinion >= 0) {
                // parsing of K random nodes
                val pickedOpinions = emptyList<Int>().toMutableList()
                repeat(K) {
                    val pick = allOpinions[Random.nextInt(allOpinions.size)]
                    if (pick < 0) {
                        pickedOpinions.add(initialOpinion)
                    } else {
                        pickedOpinions.add(pick)
                    }
                }

                val (zeroes, ones) = pickedOpinions.partition { it == 0 }
                if (zeroes.size >= A) {
                    details.add(zeroes.size)
                    collektiveDevice.set("opinion", 0)
                } else if (ones.size >= A) {
                    details.add(ones.size)
                    collektiveDevice.set("opinion", 1)
                } else {
                    // alpha wasn't met
                    details.add(if (initialOpinion == 0) zeroes.size else ones.size)
                    collektiveDevice.set("opinion", initialOpinion)
                }
            } else {
                details.add(0)
                initialOpinion
            }
        } else {
            details.add(0)
            initialOpinion
        }
    }

    details.add(0,fOpinion)
    return details
}
