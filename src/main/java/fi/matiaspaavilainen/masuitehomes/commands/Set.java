package fi.matiaspaavilainen.masuitehomes.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitecore.managers.Location;
import fi.matiaspaavilainen.masuitecore.managers.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.Home;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Set {

    public void set(ProxiedPlayer p, String hs, Location loc) {
        Formator formator = new Formator();
        Configuration config = new Configuration();
        MaSuitePlayer msp = new MaSuitePlayer().find(p.getUniqueId());
        msp.requestLocation();
        Home h = new Home();
        h = h.findExact(hs, p.getUniqueId());
        java.util.Set<Home> homes = new Home().homes(p.getUniqueId());
        if (h.getServer() != null) {
            Home home = new Home(hs, p.getServer().getInfo().getName(), p.getUniqueId(), loc);
            home.update(home);
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.updated").replace("%home%", home.getName()));
        } else {
            int max = 0;
            for (int i = 100; i > 0; i--) {
                if (p.hasPermission("masuitehomes.home.limit." + i) || p.hasPermission("masuitehomes.home.limit.unlimited")) {
                    max = i - 1;
                    break;
                }
            }
            if (homes.size() <= max) {
                Home home = new Home(hs, p.getServer().getInfo().getName(), p.getUniqueId(), loc);
                home.set(home);
                formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.set").replace("%home%", home.getName()));
            } else {
                formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-limit-reached"));
            }

        }
    }
}
