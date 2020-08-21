package dev.masa.masuitehomes.bungee.controllers;

import dev.masa.masuitecore.bungee.chat.Formator;
import dev.masa.masuitecore.core.configuration.BungeeConfiguration;
import dev.masa.masuitehomes.bungee.MaSuiteHomes;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeleportController {

    private MaSuiteHomes plugin;
    private Formator formator = new Formator();
    private BungeeConfiguration config = new BungeeConfiguration();

    public TeleportController(MaSuiteHomes p) {
        plugin = p;
    }

    public void teleport(ProxiedPlayer player, String name) {
        plugin.getHomeService().getHome(player.getUniqueId(), name, home -> {
            if (!home.isPresent()) {
                plugin.formator.sendMessage(player, plugin.config.load("homes", "messages.yml").getString("home-not-found"));
                return;
            }
            this.teleportToHome(player, name);
        });
    }

    public void teleport(ProxiedPlayer player, String name, String username) {
        plugin.getApi().getPlayerService().getPlayer(username, maSuitePlayer -> {
            if (!maSuitePlayer.isPresent()) {
                formator.sendMessage(player, config.load("homes", "messages.yml").getString("player-not-found"));
            }
            this.teleportToHome(player, name);
        });
    }

    private void teleportToHome(ProxiedPlayer player, String name) {
        plugin.getHomeService().getHome(player.getUniqueId(), name, home -> {
            if (!home.isPresent()) {
                plugin.formator.sendMessage(player, plugin.config.load("homes", "messages.yml").getString("home-not-found"));
                return;
            }
            plugin.getHomeService().teleportToHome(player, home.get(), success -> {
                plugin.formator.sendMessage(player, plugin.config.load("homes", "messages.yml").getString("home.teleported").replace("%home%", home.get().getName()));
            });
        });
    }

}
