package fi.matiaspaavilainen.masuitehomes.bukkit.events;

import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveEvent implements Listener {

    private MaSuiteHomes plugin;

    public MoveEvent(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (plugin.config.load("homes", "config.yml").getInt("warmup") > 0) {
            if (MaSuiteHomes.warmups.contains(e.getPlayer().getUniqueId())) {
                if (e.getFrom() != e.getTo()) {
                    plugin.formator.sendMessage(e.getPlayer(), plugin.config.load("homes", "messages.yml").getString("teleportation-cancelled"));
                    MaSuiteHomes.warmups.remove(e.getPlayer().getUniqueId());
                }
            }
        }
    }
}
