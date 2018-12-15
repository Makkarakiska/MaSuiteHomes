package fi.matiaspaavilainen.masuitehomes.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitecore.managers.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.Home;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;


public class List {

    private Configuration config = new Configuration();
    private Formator formator = new Formator();

    public void list(ProxiedPlayer p) {
        Home h = new Home();
        TextComponent homes = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.title")));

        int i = 0;
        String split = formator.colorize(config.load("homes", "messages.yml").getString("homes.split"));
        for (Home home : h.homes(p.getUniqueId())) {
            if (i++ == h.homes(p.getUniqueId()).size() - 1) {
                list(homes, home, p.getName(), p.getName());
            } else {
                list(homes, home, p.getName(), p.getName());
                homes.addExtra(split);
            }
        }
        p.sendMessage(homes);

    }

    public void list(ProxiedPlayer p, String name) {
        MaSuitePlayer msp = new MaSuitePlayer();
        msp = msp.find(name);
        if (msp.getUUID() == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("player-not-found"));
            return;
        }
        Home h = new Home();
        TextComponent homes = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.title")));

        int i = 0;
        String split = formator.colorize(config.load("homes", "messages.yml").getString("homes.split"));
        for (Home home : h.homes(msp.getUUID())) {
            if (i++ == h.homes(msp.getUUID()).size() - 1) {
                list(homes, home, p.getName(), msp.getUsername());
            } else {
                list(homes, home, p.getName(), msp.getUsername());
                homes.addExtra(split);
            }
        }
        p.sendMessage(homes);

    }

    private void list(TextComponent homes, Home home, String requester, String owner) {
        TextComponent hc = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.name").replace("%home%", home.getName())));
        if (requester.equalsIgnoreCase(owner)) {
            hc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home.getName()));
        } else {
            hc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + owner + " " + home.getName()));
        }
        hc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(formator.colorize(config.load("homes", "messages.yml").getString("home-hover-text").replace("%home%", home.getName()))).create()));
        homes.addExtra(hc);
    }
}
