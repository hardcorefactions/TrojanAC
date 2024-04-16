package cc.fyre.shard.util.bukkit

import org.bukkit.block.BlockFace
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * @project carnage
 *
 * @date 04/06/21
 * @author xanderume@gmail.com
 */
object AngleUtil {

    private val RADIAL = arrayOf(BlockFace.WEST,BlockFace.NORTH_WEST,BlockFace.NORTH,BlockFace.NORTH_EAST,BlockFace.EAST,BlockFace.SOUTH_EAST,BlockFace.SOUTH,BlockFace.SOUTH_WEST)
    private val NOTCHES: MutableMap<BlockFace, Int> = EnumMap(BlockFace::class.java)

    init {
        for (i in RADIAL.indices) {
            NOTCHES[RADIAL[i]] = i
        }
    }

    @JvmStatic
    fun yawToFace(yaw: Float): BlockFace {
        return RADIAL[(yaw / 45.0F).roundToInt() and 0x7]
    }

    @JvmStatic
    fun faceToYaw(face: BlockFace): Int {
        return wrapAngle(45 * NOTCHES.getOrDefault(face, 0))
    }

    private fun wrapAngle(angle: Int): Int {
        var wrappedAngle = angle
        while (wrappedAngle <= -180) {
            wrappedAngle += 360
        }
        while (wrappedAngle > 180) {
            wrappedAngle -= 360
        }
        return wrappedAngle
    }

    /**
     * Converts the given yaw to bytes.
     * When sending packets, a position's yaw uses the Angle data type.
     * See http://wiki.vg/index.php?title=Protocol&oldid=7368#Data_types
     *
     * @param yaw The yaw to convert to bytes.
     * @return The yaw converted to bytes.
     */
    @JvmStatic
    fun yawToBytes(yaw: Float): Byte {
        return (yaw * 256.0f / 360.0f).toInt().toByte()
    }

    @JvmStatic
    fun opposite(value: Float):Float {

        if (value <= 0) {
            return abs(value)
        }

        return -value
    }

    val BlockFace.isDirectional: Boolean
        get() = this.modX != 0 && this.modZ != 0
}