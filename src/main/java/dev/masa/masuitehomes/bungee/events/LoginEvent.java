package dev.masa.masuitehomes.bungee.events;

import dev.masa.masuitehomes.bungee.MaSuiteHomes;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;

public class LoginEvent implements Listener {

    private MaSuiteHomes plugin;

    public LoginEvent(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        if (!plugin.homeService.homes.containsKey(event.getPlayer().getUniqueId())) {
            plugin.homeService.homes.put(event.getPlayer().getUniqueId(), new ArrayList<>());
        }
        plugin.getProxy().getScheduler().runAsync(plugin, () -> plugin.homeService.initializeHomes(event.getPlayer().getUniqueId()));
    }
}
