package net.valorhcf.trojan.listener;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import me.lucko.helper.Schedulers;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.log.LogManager;
import net.valorhcf.trojan.packet.TrojanChannelDuplexHandler;
import net.valorhcf.trojan.profile.Profile;
import net.valorhcf.trojan.profile.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BukkitListeners implements Listener {

    // This whole class makes me want to kill myself have fun fixing it

    private final Trojan main = Trojan.getInstance();
    private final ProfileManager profileManager = main.getProfileManager();
    private final LogManager logManager = main.getLogManager();

    // TODO: This map does NOT belong here
    public static final Map<Integer, Player> ENTITY_ID_TO_PLAYER_MAP = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // TODO: MOVE THIS SOMEWHERE ELSE LOOOOOOL.
        ENTITY_ID_TO_PLAYER_MAP.put(player.getEntityId(), player);

        Profile profile = profileManager.createProfile(player);

        //logManager.log(profile, "Logged in");

        PlayerConnection connection = profile.entityPlayer.playerConnection;
        Channel channel = connection.networkManager.channel;

        channel.eventLoop().execute(() -> {
            channel.pipeline().addBefore("packet_handler", "trojan_packet_handler", new TrojanChannelDuplexHandler(profile));
        });

        Schedulers.async().run(() -> {
            // TODO: More aids code I should move this into a separate method...
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeByte(0);
                dos.writeBoolean(false);
                dos.writeBoolean(true);
                dos.writeBoolean(true);
                byte[] payload = baos.toByteArray();
                connection.sendPacket(new PacketPlayOutCustomPayload("schematica", new PacketDataSerializer(Unpooled.wrappedBuffer(payload))));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        // Payloads used to detect cheats
        Schedulers.async().runLater(() ->
                connection.sendPacket(new PacketPlayOutCustomPayload("stop ddosing",new PacketDataSerializer(Unpooled.EMPTY_BUFFER))), 20);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = profileManager.removeProfile(event.getPlayer());

        if (profile != null) {
            profile.loggedOut = true;
        }

        ENTITY_ID_TO_PLAYER_MAP.remove(player.getEntityId());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Profile profile = profileManager.getProfile(event.getPlayer());

        if (profile != null && !profile.connectionTracker.hasRespondedToTransaction()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player)) return;

        Profile profile = profileManager.getProfile((Player) event.getDamager());

        if (profile != null && !profile.connectionTracker.hasRespondedToTransaction()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX()
                && from.getBlockZ() == to.getBlockZ()
                && from.getY() >= to.getY()) return;

        Profile profile = profileManager.getProfile(event.getPlayer());

        if (profile != null && !profile.connectionTracker.hasRespondedToTransaction() && profile.movementTracker.ticksSinceLogin > 40) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {

        Block main = event.getBlock();

        double minX = main.getX();
        double maxX = main.getX();

        double minY = main.getY();
        double maxY = main.getY();

        double minZ = main.getZ();
        double maxZ = main.getZ();

        for (Block block : event.getBlocks()) {
            if (block.getX() < minX) minX = block.getX();
            if (block.getX() > maxX) maxX = block.getX();

            if (block.getY() < minY) minY = block.getY();
            if (block.getY() > maxY) maxY = block.getY();

            if (block.getZ() < minZ) minZ = block.getZ();
            if (block.getZ() > maxZ) maxZ = block.getZ();
        }

        minX -= 1 + 0.3;
        maxX += 1 + 1 + 0.3;

        minY -= 1 + 0.3;
        maxY += 2.3;

        minZ -= 1 + 0.3;
        maxZ += 1 + 1 + 0.3;

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getLocation();

            if (location.getX() >= minX
                    && location.getX() <= maxX
                    && location.getY() >= minY
                    && location.getY() <= maxY
                    && location.getZ() >= minZ
                    && location.getZ() <= maxZ
            ) {
                Profile profile = profileManager.getProfile(player);
                if (profile != null) profile.movementTracker.ticksSinceFuckingPiston = 0;
            }
        }
    }
}
