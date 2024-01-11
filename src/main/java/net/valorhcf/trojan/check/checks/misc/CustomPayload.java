package net.valorhcf.trojan.check.checks.misc;

import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;
import me.lucko.helper.Schedulers;

public class CustomPayload extends Check {

    public CustomPayload(Profile profile) {
        super("Custom Payload", profile);
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInCustomPayload) {
            PacketPlayInCustomPayload packet = (PacketPlayInCustomPayload) message;

            String tag = packet.a();

            boolean ban = true;

            switch (tag) {
                case "CRYSTAL|6LAKS0TRIES":
                    flag("C: Crystalware");
                    break;
                case "1946203560":
                    flag("C: Vape v3.25 Crack (Ape)");
                    break;
                case "0SO1Lk2KASxzsd":
                    flag("C: bspkrsCore Client");
                    break;
                default:
                    ban = false;
                    break;
            }

            if (ban) {
                // Give them 30 seconds to realize they joined with cheats
                Schedulers.sync().runLater(() -> {
                    if (!profile.loggedOut) {
                        ban();
                    }
                }, 30 * 20);
            }
        }
    }
}
