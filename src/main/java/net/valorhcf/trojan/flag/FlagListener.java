package net.valorhcf.trojan.flag;

import cc.fyre.migot.event.PlayerFlagEvent;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.profile.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FlagListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerFlag(PlayerFlagEvent event) {

        Profile profile = Trojan.getInstance().getProfileManager().getProfile(event.getPlayer());

        if (profile == null || profile.kicked || profile.loggedOut) {
            return;
        }

        String name = null;

        if (event.getType() == PlayerFlagEvent.FlagType.PHASE) {
            name = "Phase";
        }

        if (name == null) {
            return;
        }

        Trojan.getInstance().getLogManager().log(profile,name,null);
        Trojan.getInstance().getFlagManager().alert(profile,name,null);
    }

}
