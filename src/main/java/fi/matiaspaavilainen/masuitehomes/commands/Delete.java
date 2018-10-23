package fi.matiaspaavilainen.masuitehomes.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitehomes.Home;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Delete {

    public void delete(ProxiedPlayer p, String hs) {
        Formator formator = new Formator();
        Configuration config = new Configuration();
        Home home = new Home();
        home = home.findExact(hs, p.getUniqueId());
        if (home == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-not-found"));
            return;
        }
        if(home.delete()){
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.deleted").replace("%home%", home.getName()));
        } else{
            System.out.println("[MaSuite] [Homes] There was an error during saving home.");
        }

    }
}
