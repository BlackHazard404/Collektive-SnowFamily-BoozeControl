package boozeControl

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.aggregate.api.Aggregate
import snowFamily.slush

/**
* Number of nodes in the network
*/
const val nodes = 1000

/**
 * Number of opinion changes allowed to a node before
 * being classified as "contended". It depends
 * directly by the var nodes; this, actually,
 * does not correctly represent the model,
 * as declared in the README
 */
const val maxChanges = nodes/25


/**
 * Through the slush protocol,tries to reach consensus, and while doing so finds "contended" nodes, that could
 * be bottlenecks.
 * One possible implementation could be to use contended nodes as a source for a gradient that
 * divides the network in a Voronoi space.
 * @param collektiveDevice the sampling node. Each node can have an opinion in [0,1,-1], the latter being none
 */
fun Aggregate<Int>.contendedFinder(collektiveDevice: CollektiveDevice<*>) {
    val isContended = collektiveDevice.getOrDefault("contended", false)
    if(!isContended){
        val oOpinion = collektiveDevice.get<Int>("opinion")
        val res = slush(collektiveDevice)
        val nOpinion = res[0]
        if (nOpinion != oOpinion) {
            val changes = collektiveDevice.getOrDefault("nChanges", 0)
            collektiveDevice["nChanges"] = changes + 1
            if(changes + 1 > maxChanges) collektiveDevice["contended"] = true
        }

    }
}

