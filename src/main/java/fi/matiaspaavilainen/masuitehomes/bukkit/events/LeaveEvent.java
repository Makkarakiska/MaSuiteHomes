package fi.matiaspaavilainen.masuitehomes.bukkit.events;

import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LeaveEvent implements Listener {

    private MaSuiteHomes plugin;

    public LeaveEvent(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeave(PlayerJoinEvent e) {
        plugin.homes.remove(e.getPlayer().getUniqueId());
    }
}
