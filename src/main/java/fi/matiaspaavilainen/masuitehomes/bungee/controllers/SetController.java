package fi.matiaspaavilainen.masuitehomes.bungee.controllers;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import fi.matiaspaavilainen.masuitecore.core.objects.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.bungee.MaSuiteHomes;
import fi.matiaspaavilainen.masuitehomes.core.models.Home;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;

public class SetController {

    private MaSuiteHomes plugin;

    public SetController(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    private Formator formator = new Formator();
    private BungeeConfiguration config = new BungeeConfiguration();

    public void set(ProxiedPlayer player, String hs, int max, Location loc) {
        setHome(player, hs, max, loc, player.getUniqueId());
    }

    public void set(ProxiedPlayer player, String name, String homeName, int max, Location loc) {
        MaSuitePlayer msp = new MaSuitePlayer().find(name);
        if (msp.getUniqueId() == null) {
            formator.sendMessage(player, config.load("homes", "messages.yml").getString("player-not-found"));
            return;
        }
        setHome(player, homeName, max, loc, msp.getUniqueId());
    }

    private void setHome(ProxiedPlayer player, String homeName, int max, Location loc, UUID uniqueId) {
        Home home = plugin.homeService.getHomeExact(uniqueId, homeName);
        List<Home> homes = plugin.homeService.getHomes(uniqueId);

        if (home != null) {
            home.setServer(player.getServer().getInfo().getName());
            home.setLocation(loc);
            plugin.homeService.updateHome(home);
            formator.sendMessage(player, config.load("homes", "messages.yml").getString("home.updated").replace("%home%", home.getName()));
            plugin.listHomes(player);
            return;
        }

        if (homes.size() < max || max == -1) {
            Home h = plugin.homeService.createHome(new Home(homeName, player.getServer().getInfo().getName(), uniqueId, loc));
            formator.sendMessage(player, config.load("homes", "messages.yml").getString("home.set").replace("%home%", h.getName()));
        } else {
            formator.sendMessage(player, config.load("homes", "messages.yml").getString("home-limit-reached"));
        }

        plugin.listHomes(player);
    }
}
