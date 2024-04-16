package cc.fyre.shard.util.bukkit

import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent

val EntityDamageByEntityEvent.player: Player?
    get() = this.entity as? Player

val EntityDamageByEntityEvent.attacker: Pair<Player,Projectile?>?
    get() = if (this.damager is Player) {
        this.damager as Player to null
    } else if (this.damager is Projectile && ((this.damager as Projectile).shooter) is Player) {
        (this.damager as Projectile).shooter as Player to this.damager as Projectile
    } else {
        null
    }