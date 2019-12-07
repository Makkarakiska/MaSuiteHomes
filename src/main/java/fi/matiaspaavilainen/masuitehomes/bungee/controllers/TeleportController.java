package fi.matiaspaavilainen.masuitehomes.bungee.controllers;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.bungee.MaSuiteHomes;
import fi.matiaspaavilainen.masuitehomes.core.models.Home;
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

    public void teleport(ProxiedPlayer p, String name, String hs) {
        MaSuitePlayer msp = new MaSuitePlayer().find(name);
        if (msp.getUniqueId() == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("player-not-found"));
            return;
        }
        Home home = plugin.homeService.getHome(msp.getUniqueId(), hs);
        plugin.homeService.teleportToHome(msp, home);
    }

}
