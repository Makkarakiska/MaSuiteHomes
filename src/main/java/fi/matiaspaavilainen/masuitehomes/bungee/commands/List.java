package fi.matiaspaavilainen.masuitehomes.bungee.commands;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.bungee.Home;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class List {

    private BungeeConfiguration config = new BungeeConfiguration();
    private Formator formator = new Formator();

    public void list(ProxiedPlayer p) {
        Home h = new Home();

        HashMap<String, ArrayList<Home>> homeList = new HashMap<>();
        for (Home home : h.getHomes(p.getUniqueId())) {
            if (!homeList.containsKey(home.getServer())) {
                homeList.put(home.getServer(), new ArrayList<>());
            }
            homeList.get(home.getServer()).add(home);
        }

        BaseComponent baseHome = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.title")));
        baseHome.addExtra("\n");

        int homeTotal = h.getHomes(p.getUniqueId()).size() - 1;
        AtomicInteger count = new AtomicInteger();

        homeList.forEach((s, homes) -> {
            final int[] i = {0};
            TextComponent serverTitle = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.server-name").replace("%server%", s)));
            homes.forEach(home -> {
                if (i[0]++ == homes.size() - 1) {
                    serverTitle.addExtra(addToList(home, p.getName(), p.getName(), false));
                } else {
                    serverTitle.addExtra(addToList(home, p.getName(), p.getName(), true));
                }
            });
            if (count.getAndIncrement() != homeTotal) {
                serverTitle.addExtra("\n");
            }
            baseHome.addExtra(serverTitle);
        });

        p.sendMessage(baseHome);

    }

    public void list(ProxiedPlayer p, String name) {
        MaSuitePlayer msp = new MaSuitePlayer().find(name);
        if (msp.getUniqueId() == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("player-not-found"));
            return;
        }
        Home h = new Home();
        HashMap<String, ArrayList<Home>> homeList = new HashMap<>();
        for (Home home : h.getHomes(p.getUniqueId())) {
            if (!homeList.containsKey(home.getServer())) {
                homeList.put(home.getServer(), new ArrayList<>());
            }
            homeList.get(home.getServer()).add(home);
        }

        BaseComponent baseHome = new TextComponent(
                formator.colorize(config.load("homes", "messages.yml")
                        .getString("homes.title-others")
                        .replace("%player%", msp.getUsername())));
        baseHome.addExtra("\n");

        int homeTotal = h.getHomes(msp.getUniqueId()).size() - 1;
        AtomicInteger count = new AtomicInteger();
        homeList.forEach((s, homes) -> {
            final int[] i = {0};
            TextComponent serverTitle = new TextComponent(
                    formator.colorize(config.load("homes", "messages.yml")
                            .getString("homes.server-name").replace("%server%", s)));
            homes.forEach(home -> {
                if (i[0]++ == homes.size() - 1) {
                    serverTitle.addExtra(addToList(home, p.getName(), msp.getUsername(), false));
                } else {
                    serverTitle.addExtra(addToList(home, p.getName(), msp.getUsername(), true));
                }
            });
            if (count.getAndIncrement() != homeTotal) {
                serverTitle.addExtra("\n");
            }
            baseHome.addExtra(serverTitle);
        });
        p.sendMessage(baseHome);

    }

    private TextComponent addToList(Home home, String requester, String owner, boolean splitter) {
        TextComponent hc = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.name").replace("%home%", home.getName())));
        if (requester.equalsIgnoreCase(owner)) {
            hc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home.getName()));
        } else {
            hc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + owner + " " + home.getName()));
        }
        hc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(formator.colorize(config.load("homes", "messages.yml").getString("home-hover-text").replace("%home%", home.getName()))).create()));
        if (splitter) {
            hc.addExtra(formator.colorize(config.load("homes", "messages.yml").getString("homes.split")));
        }

        return hc;
    }
}
