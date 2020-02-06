package dev.masa.masuitehomes.bungee.controllers;

import dev.masa.masuitecore.bungee.chat.Formator;
import dev.masa.masuitecore.core.configuration.BungeeConfiguration;
import dev.masa.masuitecore.core.models.MaSuitePlayer;
import dev.masa.masuitehomes.bungee.MaSuiteHomes;
import dev.masa.masuitehomes.core.models.Home;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeleportController {

    private MaSuiteHomes plugin;
    private Formator formator = new Formator();
    private BungeeConfiguration config = new BungeeConfiguration();

    public TeleportController(MaSuiteHomes p) {
        plugin = p;
    }

    public void teleport(ProxiedPlayer player, String name) {
        Home home = plugin.homeService.getHome(player.getUniqueId(), name);
        plugin.homeService.teleportToHome(player, home);
    }

    public void teleport(ProxiedPlayer player, String homeName, String name) {
        MaSuitePlayer msp = plugin.api.getPlayerService().getPlayer(name);
        if (msp == null) {
            formator.sendMessage(player, config.load("homes", "messages.yml").getString("player-not-found"));
            return;
        }
        Home home = plugin.homeService.getHome(msp.getUniqueId(), homeName);
        plugin.homeService.teleportToHome(player, home);
    }

}
