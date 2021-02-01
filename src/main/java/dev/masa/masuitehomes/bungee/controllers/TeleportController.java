package dev.masa.masuitehomes.bungee.controllers;

import dev.masa.masuitecore.bungee.chat.Formator;
import dev.masa.masuitehomes.bungee.MaSuiteHomes;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeleportController {

    private final MaSuiteHomes plugin;
    private final Formator formator = new Formator();

    public TeleportController(MaSuiteHomes p) {
        plugin = p;
    }

    public void teleport(ProxiedPlayer player, String name) {
        System.out.println("Teleporting to home...");
        plugin.getHomeService().getHome(player.getUniqueId(), name, home -> {
            if (!home.isPresent()) {
                plugin.formator.sendMessage(player, this.plugin.getMessages().getHomeNotFound());
                return;
            }
            this.teleportToHome(player, name);
        });
    }

    public void teleport(ProxiedPlayer player, String name, String username) {
        plugin.getApi().getPlayerService().getPlayer(username, maSuitePlayer -> {
            if (!maSuitePlayer.isPresent()) {
                formator.sendMessage(player, this.plugin.getApi().getCore().getMessages().getPlayerNotOnline());
            }
            this.teleportToHome(player, name);
        });
    }

    private void teleportToHome(ProxiedPlayer player, String name) {
        plugin.getHomeService().getHome(player.getUniqueId(), name, home -> {
            if (!home.isPresent()) {
                plugin.formator.sendMessage(player, this.plugin.getMessages().getHomeNotFound());
                return;
            }
            plugin.getHomeService().teleportToHome(player, home.get(), success -> {
                plugin.formator.sendMessage(player, this.plugin.getMessages().getHome().getTeleported().replace("%home%", home.get().getName()));
            });
        });
    }

}
