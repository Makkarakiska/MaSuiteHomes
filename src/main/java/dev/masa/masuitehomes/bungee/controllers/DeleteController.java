package dev.masa.masuitehomes.bungee.controllers;

import dev.masa.masuitecore.bungee.chat.Formator;
import dev.masa.masuitehomes.bungee.MaSuiteHomes;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class DeleteController {

    private final Formator formator = new Formator();

    private final MaSuiteHomes plugin;

    public DeleteController(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    public void delete(ProxiedPlayer p, String homeName) {
        deleteHome(p, homeName, p.getUniqueId());
    }

    public void delete(ProxiedPlayer proxiedPlayer, String name, String homeName) {
        plugin.getApi().getPlayerService().getPlayer(name, playerQuery -> {
            if (!playerQuery.isPresent()) {
                plugin.formator.sendMessage(proxiedPlayer, this.plugin.getApi().getCore().getMessages().getPlayerNotOnline());
                return;
            }

            deleteHome(proxiedPlayer, homeName, playerQuery.get().getUniqueId());
        });
    }

    private void deleteHome(ProxiedPlayer proxiedPlayer, String homeName, UUID uniqueId) {
        plugin.getHomeService().getHomeExact(uniqueId, homeName, home -> {
            if (!home.isPresent()) {
                formator.sendMessage(proxiedPlayer, this.plugin.getMessages().getHomeNotFound());
                return;
            }
            plugin.getHomeService().delete(home.get(), success -> {
                if (success) {
                    formator.sendMessage(proxiedPlayer, this.plugin.getMessages().getHome().getDeleted().replace("%home%", home.get().getName()));
                    plugin.listHomes(proxiedPlayer);
                }
            });
        });
    }
}
