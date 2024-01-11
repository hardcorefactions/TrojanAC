package net.valorhcf.trojan.profile;

import com.viaversion.viaversion.ViaVersionPlugin;
import net.minecraft.server.v1_8_R3.*;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.check.checks.combat.*;
import net.valorhcf.trojan.check.checks.inventory.InventoryA;
import net.valorhcf.trojan.check.checks.inventory.InventoryB;
import net.valorhcf.trojan.check.checks.misc.*;
import net.valorhcf.trojan.check.checks.movement.Fly;
import net.valorhcf.trojan.check.checks.movement.NoFall;
import net.valorhcf.trojan.check.checks.movement.Speed;
import net.valorhcf.trojan.check.checks.order.*;
import net.valorhcf.trojan.profile.tracker.*;
import net.valorhcf.trojan.util.ClientVersion;
import net.valorhcf.trojan.util.TrojanLocation;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class Profile {

    private final Trojan main = Trojan.getInstance();

    public final Player player;
    public final String uuid;
    public final EntityPlayer entityPlayer;

    private final List<Check> checkList = new ArrayList<>();

    public final ClickTracker clickTracker = new ClickTracker(this);
    public final ConnectionTracker connectionTracker = new ConnectionTracker(this);
    public final LocationHistoryTracker locationHistoryTracker = new LocationHistoryTracker(this);
    public final MovementTracker movementTracker;
    public final PayloadTracker payloadTracker = new PayloadTracker(this);

    public int protocolVersion;
    public ClientVersion clientVersion;

    public boolean kicked = false;
    public boolean loggedOut = false;

    public Profile(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId().toString();

        movementTracker = new MovementTracker(this);

        CraftPlayer craftPlayer = (CraftPlayer) player;
        this.entityPlayer = craftPlayer.getHandle();

        this.protocolVersion = ViaVersionPlugin.getInstance().getApi().getPlayerVersion(player);
        this.clientVersion = ClientVersion.getVersionByProtocolId(protocolVersion);

        checkList.addAll(Arrays.asList(
                // Combat
                new AimAssist(this),

                new AutoClickerD(this),
                new AutoClickerG(this),
                new AutoClickerH(this),
                new AutoClickerK(this),
                new AutoClickerO(this),
                new AutoClickerS(this),
                new AutoClickerT(this),
                new AutoClickerZ(this),

                new KillAura(this),

                new MultiAura(this),
                new Reach(this),

                // Inventory
                new InventoryA(this),
                new InventoryB(this),

                // Misc
                new BlockPlaceA(this),
                new BlockPlaceB(this),

                new CustomPayload(this),

                new DoubleSneak(this),
                new DoubleSprint(this),

                new FrozenMinecraft(this),

                new NoPosition(this),
                new Pitch(this),

                new PostFlyingPacket(this),

                new SameSlotSwitch(this),

                new Timer(this),

                // Movement
                new Fly(this),
                new NoFall(this),
                new Speed(this),

                // Order
                new ActionAttack(this),
                new NoSwingAttack(this),
                new PlaceAttack(this),
                new PlaceRelease(this),
                new PlaceSlot(this)
        ));
    }

    // All this instanceof checking is WACK
    // TODO: Visitor pattern or some shit?
    public void handleInboundPacket(Object message, long millis, long nanos) {

        try {
            if (message instanceof PacketPlayInFlying) {
                movementTracker.handleFlying((PacketPlayInFlying) message);
                clickTracker.handleFlying();
            } else if (message instanceof PacketPlayInBlockDig) {
                clickTracker.handleBlockDig((PacketPlayInBlockDig) message);
            } else if (message instanceof PacketPlayInBlockPlace) {
                clickTracker.handleBlockPlace();
            } else if (message instanceof PacketPlayInUseEntity) {
                clickTracker.handleUseEntity((PacketPlayInUseEntity) message);
            } else if (message instanceof PacketPlayInArmAnimation) {
                clickTracker.handleArmAnimation();
            } else if (message instanceof PacketPlayInCustomPayload) {
                payloadTracker.handleCustomPayload((PacketPlayInCustomPayload) message);
            } else if (message instanceof PacketPlayInTransaction) {
                connectionTracker.handleTransaction((PacketPlayInTransaction) message, millis);
                movementTracker.handleTransaction((PacketPlayInTransaction) message);
                locationHistoryTracker.handleTransaction((PacketPlayInTransaction) message);
            } else if (message instanceof PacketPlayInKeepAlive) {
                connectionTracker.handleKeepAlive((PacketPlayInKeepAlive) message, millis);
            } else if (message instanceof PacketPlayInEntityAction) {
                movementTracker.handleEntityAction((PacketPlayInEntityAction) message);
            }
        } catch (Exception e) {
            main.getLogger().log(Level.SEVERE, "Could not handle inbound packet", e);
            e.printStackTrace();
        }

        // Fyre - still handle tracker check

        if (!(Trojan.getInstance().isHCFEnabled())) {

            for (Check c : checkList) {
                try {
                    c.handleInboundPacket(message, millis, nanos);
                } catch (Exception e) {
                    main.getLogger().log(Level.SEVERE, "Could not handle inbound packet for check " + c.getName() + ":", e);
                }
            }

        }


        if (message instanceof PacketPlayInFlying) {
            locationHistoryTracker.handleFlyingPost();
        }
    }

    public void handleOutboundPacketBeforeWrite(Object message, long millis, long nanos) {

        try {
            if (message instanceof PacketPlayOutPosition) {
                movementTracker.handlePositionBeforeWrite((PacketPlayOutPosition) message);
            }
        } catch (Exception e) {
            main.getLogger().log(Level.SEVERE, "Could not handle outbound packet before write", e);
            e.printStackTrace();
        }
    }

    public void handleOutboundPacket(Object message, long millis, long nanos) {

        try {
            if (message instanceof PacketPlayOutEntityVelocity) {
                movementTracker.handleEntityVelocity((PacketPlayOutEntityVelocity) message);
            } else if (message instanceof PacketPlayOutExplosion) {
                movementTracker.handleExplosion((PacketPlayOutExplosion) message);
            } else if (message instanceof PacketPlayOutSpawnEntity) {
                locationHistoryTracker.handleSpawnEntity((PacketPlayOutSpawnEntity) message);
            } else if (message instanceof PacketPlayOutEntity) {
                locationHistoryTracker.handleEntity((PacketPlayOutEntity) message);
            } else if (message instanceof PacketPlayOutEntityTeleport) {
                locationHistoryTracker.handleSpawnEntityTeleport((PacketPlayOutEntityTeleport) message);
            } else if (message instanceof PacketPlayOutEntityDestroy) {
                locationHistoryTracker.handleEntityDestroy((PacketPlayOutEntityDestroy) message);
            }
        } catch (Exception e) {
            main.getLogger().log(Level.SEVERE, "Could not handle outbound packet", e);
            e.printStackTrace();
        }

        if (Trojan.getInstance().isHCFEnabled()) {
            return;
        }

        for (Check c : checkList) {
            try {
                c.handleOutboundPacket(message, millis, nanos);
            } catch (Exception e) {
                main.getLogger().log(Level.SEVERE, "Could not handle outbound packet for check " + c.getName() + ":", e);
            }
        }
    }

    public void onMove(TrojanLocation from, TrojanLocation to, boolean moved, boolean rotated) {

        if (Trojan.getInstance().isHCFEnabled()) {
            return;
        }

        for (Check c : checkList) {
            try {
                c.onMove(from, to, moved, rotated);
            } catch (Exception e) {
                main.getLogger().log(Level.SEVERE, "Could not handle onMove for check " + c.getName() + ":", e);
            }
        }
    }

    public void onMouseLeftClick(int ticks) {

        if (Trojan.getInstance().isHCFEnabled()) {
            return;
        }

        checkList.forEach(c -> c.onMouseLeftClick(ticks));
    }

    public void onAsyncTick() {
        connectionTracker.onAsyncTick();
    }

    public void onTeleport() {

        if (Trojan.getInstance().isHCFEnabled()) {
            return;
        }

        checkList.forEach(Check::onTeleport);
    }

    public void onTrojanKeepAlive() {

        if (Trojan.getInstance().isHCFEnabled()) {
            return;
        }

        checkList.forEach(Check::onTrojanKeepAlive);
    }
}
