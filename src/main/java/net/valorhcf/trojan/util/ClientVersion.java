package net.valorhcf.trojan.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClientVersion {

    VERSION_1_7_1(3, "1.7 - 1.7.1"),
    VERSION_1_7_5(4, "1.7.2 - 1.7.5"),
    VERSION_1_7_10(5, "1.7.6 - 1.7.10"),
    VERSION_1_8_9(47, "1.8 - 1.8.9"),
    UNKNOWN(-1, "Unknown");

    private static final Int2ObjectOpenHashMap<ClientVersion> PROTOCOL_ID_TO_CLIENT_VERSION_MAP = new Int2ObjectOpenHashMap<>();

    public static ClientVersion getVersionByProtocolId(int protocolId) {
        return PROTOCOL_ID_TO_CLIENT_VERSION_MAP.getOrDefault(protocolId, UNKNOWN);
    }

    private final int protocolId;
    private final String protocolName;

    public boolean isOlderOrEqual(ClientVersion other) {
        return this.protocolId <= other.protocolId;
    }

    public boolean isNewerOrEqual(ClientVersion other) {
        return this.protocolId >= other.protocolId;
    }

    static {
        for (ClientVersion version : values()) {
            PROTOCOL_ID_TO_CLIENT_VERSION_MAP.put(version.protocolId, version);
        }
    }
}
