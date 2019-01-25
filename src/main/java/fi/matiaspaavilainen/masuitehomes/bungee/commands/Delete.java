package fi.matiaspaavilainen.masuitehomes.bungee.commands;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.bungee.MaSuiteHomes;
import fi.matiaspaavilainen.masuitehomes.core.Home;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Delete {

    private Formator formator = new Formator();
    private BungeeConfiguration config = new BungeeConfiguration();

    private MaSuiteHomes plugin;

    public Delete(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    public void delete(ProxiedPlayer p, String hs) {
        Home home = new Home().findExact(hs, p.getUniqueId());
        if (home == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-not-found"));
            return;
        }
        if (home.delete()) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.deleted").replace("%home%", home.getName()));
        } else {
            System.out.println("[MaSuite] [Homes] There was an error during removing home.");
        }
        plugin.listHomes(p);
    }

    public void delete(ProxiedPlayer p, String name, String hs) {
        MaSuitePlayer msp = new MaSuitePlayer().find(name);
        if (msp.getUniqueId() == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("player-not-found"));
            return;
        }

        Home home = new Home();
        home = home.findExact(hs, msp.getUniqueId());
        if (home == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-not-found"));
            return;
        }
        if (home.delete()) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.deleted").replace("%home%", home.getName()));
        } else {
            System.out.println("[MaSuite] [Homes] There was an error during saving home.");
        }
        plugin.listHomes(p);
    }
}
