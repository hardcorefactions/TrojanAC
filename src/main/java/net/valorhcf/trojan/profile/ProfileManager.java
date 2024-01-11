package net.valorhcf.trojan.profile;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ProfileManager {

    @Getter
    private final Map<Player, Profile> profileMap = new HashMap<>();

    public Profile getProfile(Player player) {
        return profileMap.get(player);
    }

    public Profile createProfile(Player player) {
        return profileMap.computeIfAbsent(player, Profile::new);
    }

    public Profile removeProfile(Player player) {
        return profileMap.remove(player);
    }
}
