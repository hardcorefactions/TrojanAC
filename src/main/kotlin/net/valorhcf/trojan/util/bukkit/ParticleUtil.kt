package cc.fyre.shard.util.bukkit

import org.apache.commons.lang.WordUtils
import org.bukkit.Effect
import java.lang.IllegalArgumentException

object ParticleUtil {

    private val particles = hashMapOf<Effect,String>()
    private val particlesByName = hashMapOf<String,Effect>()

    init {

        for (effect in Effect.values().filter{it.type == Effect.Type.PARTICLE}) {
            this.particles[effect] = WordUtils.capitalizeFully(effect.name.lowercase().replace("_"," "))
            this.particlesByName[effect.name.lowercase()] = effect
        }

    }

    @JvmStatic
    fun getName(effect: Effect):String {

        if (effect.type != Effect.Type.PARTICLE) {
            throw IllegalArgumentException("Effect is not a particle!")
        }

        return this.particles[effect]!!
    }

    @JvmStatic
    fun getByName(name: String):Effect? {
        return this.particlesByName[name.lowercase()]
    }

    @JvmStatic
    fun getAllParticles():List<Effect> {
        return this.particles.keys.toList()
    }

}