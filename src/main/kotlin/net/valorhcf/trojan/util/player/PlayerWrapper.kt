package net.valorhcf.trojan.util.player

import cc.fyre.shard.util.injector.TitleType
import cc.fyre.shard.util.player.MessageUtil
import com.comphenix.protocol.ProtocolLibrary
import com.viaversion.viaversion.api.Via
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle
import net.valorhcf.trojan.Trojan
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage

import org.bukkit.entity.Player

val Player.protocol: Int
    get() = if (Trojan.getInstance().isPluginPresent("ViaVersion")) {
        Via.getAPI().getConnection(this.uniqueId)?.protocolInfo?.protocolVersion ?: 47
    } else if (Trojan.getInstance().isPluginPresent("ProtocolLib")) {
        ProtocolLibrary.getProtocolManager().getProtocolVersion(this)
    } else {
        47
    }

fun Player.sendTitle(type: TitleType, text: String?,fadeIn: Int,stay: Int,fadeOut: Int) {

    val handle = (this as CraftPlayer).handle

    handle.playerConnection.sendPacket(PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE,CraftChatMessage.fromString(text ?: "")[0]))
    handle.playerConnection.sendPacket(PacketPlayOutTitle(fadeIn,stay,fadeOut))
    //handle.playerConnection.sendPacket(ProtocolInjector.PacketPlayOutTitle(ProtocolInjector.PacketPlayOutTitle.Action.TITLE,CraftChatMessage.fromString(text ?: "")[0]))
//    handle.playerConnection.sendPacket(ProtocolInjector.PacketPlayOutTitle(ProtocolInjector.PacketPlayOutTitle.Action.TIMES,fadeIn,stay,fadeOut))

    //PlayerUtil.sendPacket(this,Shard.AI.getPacketAI().PacketPlayOutTitle(type,text ?: ""))
    //PlayerUtil.sendPacket(this,Shard.AI.getPacketAI().PacketPlayOutTitle(TitleType.TIMES,null,fadeIn,stay,fadeOut))
}

fun Player.sendActionBar(text: String) {

    if (player.protocol > 20) {
        PlayerUtil.sendPacket(this,Shard.AI.getPacketAI().PacketPlayOutChat(text,3,false))
    } //else if (Shard.instance.isLunarClientEnabled() && LunarClientAPI.getInstance().isRunningLunarClient(player)) { not the right thing
//        LunarClientAPI.getInstance().sendPacket(this,LCPacketTitle(com.lunarclient.bukkitapi.title.TitleType.SUBTITLE.name,text,0L,0L,0L))
//    }

}

fun Player.sendCenteredMessage(message: String) {
    MessageUtil.sendCenteredMessage(this, message)
}
