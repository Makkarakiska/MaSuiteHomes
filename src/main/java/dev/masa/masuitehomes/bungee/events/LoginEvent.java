package dev.masa.masuitehomes.bungee.events;

import dev.masa.masuitehomes.bungee.MaSuiteHomes;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LoginEvent implements Listener {

    private MaSuiteHomes plugin;

    public LoginEvent(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> plugin.getHomeService().initializeHomes(event.getPlayer().getUniqueId()));
    }
}
