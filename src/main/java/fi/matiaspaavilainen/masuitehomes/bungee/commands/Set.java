package fi.matiaspaavilainen.masuitehomes.bungee.commands;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import fi.matiaspaavilainen.masuitecore.core.objects.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.bungee.Home;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Set {

    private Formator formator = new Formator();
    private BungeeConfiguration config = new BungeeConfiguration();

    public void set(ProxiedPlayer p, String hs, int max, Location loc) {
        Home h = new Home().findExact(hs, p.getUniqueId());
        java.util.Set<Home> homes = new Home().getHomes(p.getUniqueId());
        if (h != null) {
            Home home = new Home(hs, p.getServer().getInfo().getName(), p.getUniqueId(), loc).update();
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.updated").replace("%home%", home.getName()));
        } else {
            if (homes.size() < max || max == -1) {
                Home home = new Home(hs, p.getServer().getInfo().getName(), p.getUniqueId(), loc).create();
                formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.set").replace("%home%", home.getName()));
            } else {
                formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-limit-reached"));
            }

        }
    }

    public void set(ProxiedPlayer p, String name, String hs, int max, Location loc) {
        MaSuitePlayer msp = new MaSuitePlayer().find(name);
        if (msp.getUniqueId() == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("player-not-found"));
            return;
        }
        Home h = new Home().findExact(hs, msp.getUniqueId());
        java.util.Set<Home> homes = new Home().getHomes(msp.getUniqueId());
        if (h != null) {
            Home home = new Home(hs, p.getServer().getInfo().getName(), msp.getUniqueId(), loc).update();
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.updated").replace("%home%", home.getName()));
        } else {
            if (homes.size() < max || max == -1) {
                Home home = new Home(hs, p.getServer().getInfo().getName(), msp.getUniqueId(), loc).create();
                formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.set").replace("%home%", home.getName()));
            } else {
                formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-limit-reached"));
            }

        }
    }
}
