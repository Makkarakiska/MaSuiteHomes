package fi.matiaspaavilainen.masuitehomes.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitecore.managers.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.Home;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Delete {

    private Formator formator = new Formator();
    private Configuration config = new Configuration();

    public void delete(ProxiedPlayer p, String hs) {
        Home home = new Home();
        home = home.findExact(hs, p.getUniqueId());
        if (home == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-not-found"));
            return;
        }
        if (home.delete()) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.deleted").replace("%home%", home.getName()));
        } else {
            System.out.println("[MaSuite] [Homes] There was an error during removing home.");
        }

    }

    public void delete(ProxiedPlayer p, String name, String hs) {
        MaSuitePlayer msp = new MaSuitePlayer();
        msp = msp.find(name);
        if (msp.getUUID() == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("player-not-found"));
            return;
        }

        Home home = new Home();
        home = home.findExact(hs, msp.getUUID());
        if (home == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-not-found"));
            return;
        }
        if (home.delete()) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.deleted").replace("%home%", home.getName()));
        } else {
            System.out.println("[MaSuite] [Homes] There was an error during saving home.");
        }

    }
}
