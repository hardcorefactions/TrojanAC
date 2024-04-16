package net.valorhcf.trojan.flag;

import org.bukkit.event.Listener;

public class FlagListener implements Listener {

    /*
        Requires a Spigot with a phase check which I don't have any atm.

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
    */

}
