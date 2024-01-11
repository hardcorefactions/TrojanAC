package net.valorhcf.trojan.profile.tracker;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
import net.valorhcf.trojan.profile.Profile;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class PayloadTracker {

    private final Profile profile;

    private final Set<String> clientInformation = new HashSet<>();

    public PayloadTracker(Profile profile) {
        this.profile = profile;
    }

    public String getClientInformation() {
        if (clientInformation.isEmpty()) return "Unknown";

        StringJoiner sj = new StringJoiner(", ");
        profile.payloadTracker.clientInformation.forEach(sj::add);

        return sj.toString();
    }

    public void handleCustomPayload(PacketPlayInCustomPayload packet) {
        String tag = packet.a();

        byte[] data = new byte[packet.b().readableBytes()];
        packet.b().readBytes(data);

        String text = "";

        PayloadInformation information = new PayloadInformation(clientInformation);

        if (tag.equals("MC|Brand")) {
            if (text.contains("vanilla")) information.add("Vanilla");
            if (text.contains("LiteLoader")) information.add("LiteLoader");
            if (text.contains("forge")) information.add("Forge");
            if (text.contains("lunarclient")) information.add("Lunar Client");
            if (text.contains("PLC")) information.add("PvPLounge Client");
            if (text.contains("hyperium")) information.add("Hyperium Client");
            if (text.contains("cheatbreaker")) information.add("CheatBreaker");
        }

        if (tag.equals("REGISTER")) {
            if (text.contains("CC")) information.add("Cosmic Client");
            if (text.contains("OCMC")) information.add("OCMC");
            if (text.contains("FALCUN-CLIENT")) information.add("Falcun Client");
            if (text.toLowerCase().contains("5zig")) information.add("5zig Mod");
            if (text.contains("WDL|INIT")) information.add("World Downloader");
            if (text.toLowerCase().contains("lunar")) information.add("Lunar Client");
            if (text.contains("WECUI")) information.add("WorldEdit CUI");
            if (text.equals("Tecknix-Client")) information.add("Tecknix Client");
            if (text.equals("transfer:channel")) information.known();
            if (text.contains("RedisBungee")) information.known();
        }

        if (tag.equals("LMC")) information.add("LabyMod");
        if (tag.equals("labymod3:main")) information.add("LabyMod");
        if (tag.equals("WDL|INIT")) information.add("WorldDownloader");
        if (tag.equals("WECUI")) information.add("WorldEdit CUI");
        if (tag.equals("CB-Client")) information.add("CheatBreaker");
        if (tag.equals("Lunar-Client")) information.add("Lunar Client");
        if (tag.equals("OCMC")) information.add("OCMC");

        if (tag.equals("dba:cxzscelefp")) information.known();

//        if (!information.isKnown()) {
//            Stream.of("Rowin", "lesbiqn")
//                    .map(Bukkit::getPlayerExact)
//                    .filter(Objects::nonNull)
//                    .forEach(p -> p.sendMessage(RED + "[T] "
//                            + profile.player.getName() + " - " + tag + ": " + text));
//        }
    }

    private static class PayloadInformation {

        private final Set<String> information;

        @Getter
        private boolean known;

        public PayloadInformation(Set<String> information) {
            this.information = information;
        }

        public void add(String name) {
            information.add(name);
            known = true;
        }

        public void known() {
            known = true;
        }
    }
}
