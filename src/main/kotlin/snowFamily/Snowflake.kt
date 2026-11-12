package snowFamily

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.aggregate.api.Aggregate

/**
 * Value B as defined in the paper. The number of turns in which the same maj is
 * observed before deciding on a value
 */
const val B = 350

/**
 * Adds to the Slush function the feature to terminate nodes dynamically.
 * @param collektiveDevice the node computing the Snowflake function.
 */
fun Aggregate<Int>.snowflake(collektiveDevice: CollektiveDevice<*>): List<Int> {
    val terminated = collektiveDevice.get<Boolean>("terminated")
    val oldOpinion = collektiveDevice.get<Int>("opinion")
    if (!terminated) {
        val details = slush(collektiveDevice)
        val opinion = details[0]
        val observedTurns = collektiveDevice.get<Int>("observed_turns")

        if (oldOpinion != opinion) {
            collektiveDevice["observed_turns"] = 1
        } else {
            if (observedTurns + 1 >= B) {
                collektiveDevice["terminated"] = true
            } else {
                collektiveDevice["observed_turns"] = 1 + observedTurns
            }
        }
        return details
    } else {
        return listOf(oldOpinion, 0)
    }
}
