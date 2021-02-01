package dev.masa.masuitehomes.bukkit.events;

import dev.masa.masuitecore.common.channels.BukkitPluginChannel;
import dev.masa.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class JoinEvent implements Listener {

    private MaSuiteHomes plugin;

    public JoinEvent(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.homes.put(e.getPlayer().getUniqueId(), new ArrayList<>());
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> new BukkitPluginChannel(plugin, e.getPlayer(), "ListHomes", e.getPlayer().getName()).send(), 20);
    }
}
