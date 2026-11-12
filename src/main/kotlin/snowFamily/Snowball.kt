package snowFamily

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.aggregate.api.Aggregate

/**
 * Operates like the Snowflake function, but changes opinion upon observing a higher majority than any observed before.
 * @param collektiveDevice the node computing the Snowball function.
 */
fun Aggregate<Int>.snowball(collektiveDevice: CollektiveDevice<*>): List<Int> {
    val terminated = collektiveDevice.get<Boolean>("terminated")
    val oOpinion = collektiveDevice.get<Int>("opinion")
    if (!terminated) {
        val oMaxVotes = collektiveDevice.get<Int>("max_votes")
        // a causa del parametro B che viene cambiato ad ogni maggioranza riuso slush
        val details = slush(collektiveDevice)
        val nOpinion = details[0]
        val nMaxVotes = details[1]
        val observedTurns = collektiveDevice.get<Int>("observed_turns")
        val newRecord = (oMaxVotes <= nMaxVotes)

        // Comportamento di snowflake
        if (newRecord && oOpinion != nOpinion) {
            collektiveDevice["observed_turns"] = 1
        } else {
            if (observedTurns + 1 >= B) {
                collektiveDevice["terminated"] = true
            } else {
                collektiveDevice["observed_turns"] = 1 + observedTurns
            }
        }

        return if (newRecord) {
            listOf(collektiveDevice.set("opinion", nOpinion), collektiveDevice.set("max_votes", nMaxVotes))
        } else {
            listOf(collektiveDevice.set("opinion", oOpinion), collektiveDevice.set("max_votes", oMaxVotes))
        }
    } else {
        return listOf(oOpinion, 0) // teoricamente non può interrogare la rete
    }
}
