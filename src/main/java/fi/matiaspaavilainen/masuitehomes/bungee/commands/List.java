package fi.matiaspaavilainen.masuitehomes.bungee.commands;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.bungee.Home;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;


public class List {

    private BungeeConfiguration config = new BungeeConfiguration();
    private Formator formator = new Formator();

    public void list(ProxiedPlayer p) {
        Home h = new Home();
        TextComponent homes = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.title")));

        int i = 0;
        String split = formator.colorize(config.load("homes", "messages.yml").getString("homes.split"));
        for (Home home : h.getHomes(p.getUniqueId())) {
            if (i++ == h.getHomes(p.getUniqueId()).size() - 1) {
                list(homes, home, p.getName(), p.getName());
            } else {
                list(homes, home, p.getName(), p.getName());
                homes.addExtra(split);
            }
        }
        p.sendMessage(homes);

    }

    public void list(ProxiedPlayer p, String name) {
        MaSuitePlayer msp = new MaSuitePlayer().find(name);
        if (msp.getUniqueId() == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("player-not-found"));
            return;
        }
        Home h = new Home();
        TextComponent homes = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.title")));

        int i = 0;
        String split = formator.colorize(config.load("homes", "messages.yml").getString("homes.split"));
        for (Home home : h.getHomes(msp.getUniqueId())) {
            if (i++ == h.getHomes(msp.getUniqueId()).size() - 1) {
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
