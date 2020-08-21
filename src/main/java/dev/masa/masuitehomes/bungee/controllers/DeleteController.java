package dev.masa.masuitehomes.bungee.controllers;

import dev.masa.masuitecore.bungee.chat.Formator;
import dev.masa.masuitecore.core.configuration.BungeeConfiguration;
import dev.masa.masuitehomes.bungee.MaSuiteHomes;
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

    public void delete(ProxiedPlayer proxiedPlayer, String name, String homeName) {
        plugin.getApi().getPlayerService().getPlayer(name, playerQuery -> {
            if (!playerQuery.isPresent()) {
                plugin.formator.sendMessage(proxiedPlayer, plugin.config.load("homes", "messages.yml").getString("player-not-found"));
                return;
            }

            deleteHome(proxiedPlayer, homeName, playerQuery.get().getUniqueId());
        });
    }

    private void deleteHome(ProxiedPlayer proxiedPlayer, String homeName, UUID uniqueId) {
        plugin.getHomeService().getHomeExact(uniqueId, homeName, home -> {
            if (!home.isPresent()) {
                formator.sendMessage(proxiedPlayer, config.load("homes", "messages.yml").getString("home-not-found"));
                return;
            }
            plugin.getHomeService().delete(home.get(), success -> {
                if (success) {
                    formator.sendMessage(proxiedPlayer, config.load("homes", "messages.yml").getString("home.deleted").replace("%home%", home.get().getName()));
                    plugin.listHomes(proxiedPlayer);
                }
            });
        });
    }
}
