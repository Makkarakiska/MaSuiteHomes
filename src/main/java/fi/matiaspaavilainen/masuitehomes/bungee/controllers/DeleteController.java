package fi.matiaspaavilainen.masuitehomes.bungee.controllers;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.bungee.MaSuiteHomes;
import fi.matiaspaavilainen.masuitehomes.core.models.Home;
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
        MaSuitePlayer msp = new MaSuitePlayer().find(name);
        if (msp.getUniqueId() == null) {
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
