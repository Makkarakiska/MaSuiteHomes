package dev.masa.masuitehomes.bungee.controllers;

import dev.masa.masuitecore.bungee.chat.Formator;
import dev.masa.masuitecore.core.configuration.BungeeConfiguration;
import dev.masa.masuitecore.core.models.MaSuitePlayer;
import dev.masa.masuitehomes.bungee.MaSuiteHomes;
import dev.masa.masuitehomes.core.models.Home;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class DeleteController {

    private Formator formator = new Formator();
    private BungeeConfiguration config = new BungeeConfiguration();

    private MaSuiteHomes plugin;

    public DeleteController(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    public void delete(ProxiedPlayer p, String homeName) {
        deleteHome(p, homeName, p.getUniqueId());
    }

    public void delete(ProxiedPlayer p, String name, String homeName) {
        MaSuitePlayer msp = plugin.api.getPlayerService().getPlayer(name);
        if (msp == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("player-not-found"));
            return;
        }

        deleteHome(p, homeName, msp.getUniqueId());
    }

    private void deleteHome(ProxiedPlayer p, String homeName, UUID uniqueId) {
        Home home = plugin.homeService.getHomeExact(uniqueId, homeName);
        if (home == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-not-found"));
            return;
        }

        plugin.homeService.removeHome(home);
        formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.deleted").replace("%home%", home.getName()));
        plugin.listHomes(p);
    }
}
