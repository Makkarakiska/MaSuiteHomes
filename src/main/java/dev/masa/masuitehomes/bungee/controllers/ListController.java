package dev.masa.masuitehomes.bungee.controllers;

import dev.masa.masuitecore.core.models.MaSuitePlayer;
import dev.masa.masuitehomes.bungee.MaSuiteHomes;
import dev.masa.masuitehomes.core.models.Home;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ListController {
    
    private MaSuiteHomes plugin;

    public ListController(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    private HashMap<String, List<Home>> loadHomes(UUID uuid) {
        HashMap<String, List<Home>> homeList = new HashMap<>();
        for (Home home : plugin.getHomeService().getHomes(uuid)) {
            if (!homeList.containsKey(home.getLocation().getServer())) {
                homeList.put(home.getLocation().getServer(), new ArrayList<>());
            }
            homeList.get(home.getLocation().getServer()).add(home);
        }
        return homeList;
    }

    public void list(ProxiedPlayer p) {
        BaseComponent baseHome = new TextComponent(plugin.formator.colorize(plugin.config.load("homes", "messages.yml").getString("homes.title")));
        p.sendMessage(baseHome);

        loadHomes(p.getUniqueId()).forEach((server, homes) -> {

            TextComponent message = new TextComponent();
            TextComponent serverTitle = new TextComponent(plugin.formator.colorize(plugin.config.load("homes", "messages.yml").getString("homes.server-name").replace("%server%", server)));

            message.addExtra(serverTitle);

            int i = 0;
            int counter = 0;
            for (Home home : homes) {
                if (i++ == homes.size() - 1) {
                    message.addExtra(addToList(home, p.getName(), p.getName(), false));
                } else {
                    message.addExtra(addToList(home, p.getName(), p.getName(), true));
                }
                counter++;
                if (counter == 20) {
                    p.sendMessage(message);
                    message = new TextComponent();
                    counter = 0;
                }

            }
            if (homes.size() - 1 < 20) {
                p.sendMessage(message);
            }

        });


    }

    public void list(ProxiedPlayer p, String name) {
        MaSuitePlayer msp = plugin.getApi().getPlayerService().getPlayer(name);
        if (msp == null) {
            plugin.formator.sendMessage(p, plugin.config.load("homes", "messages.yml").getString("player-not-found"));
            return;
        }

        BaseComponent baseHome = new TextComponent(plugin.formator.colorize(plugin.config.load("homes", "messages.yml").getString("homes.title-others").replace("%player%", msp.getUsername())));
        p.sendMessage(baseHome);

        loadHomes(msp.getUniqueId()).forEach((server, homes) -> {

            TextComponent message = new TextComponent();
            TextComponent serverTitle = new TextComponent(plugin.formator.colorize(plugin.config.load("homes", "messages.yml").getString("homes.server-name").replace("%server%", server)));

            message.addExtra(serverTitle);

            int i = 0;
            int counter = 0;
            for (Home home : homes) {
                if (i++ == homes.size() - 1) {
                    message.addExtra(addToList(home, p.getName(), msp.getUsername(), false));
                } else {
                    message.addExtra(addToList(home, p.getName(), msp.getUsername(), true));
                }
                counter++;
                if (counter == 20) {
                    p.sendMessage(message);
                    message = new TextComponent();
                    counter = 0;
                }

            }
            if (homes.size() - 1 < 20) {
                p.sendMessage(message);
            }

        });

    }

    private TextComponent addToList(Home home, String requester, String owner, boolean splitter) {
        TextComponent hc = new TextComponent(plugin.formator.colorize(plugin.config.load("homes", "messages.yml").getString("homes.name").replace("%home%", home.getName())));
        if (requester.equalsIgnoreCase(owner)) {
            hc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home.getName()));
        } else {
            hc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home.getName() + " " + owner));
        }
        hc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(plugin.formator.colorize(plugin.config.load("homes", "messages.yml").getString("home-hover-text").replace("%home%", home.getName()))).create()));
        if (splitter) {
            hc.addExtra(plugin.formator.colorize(plugin.config.load("homes", "messages.yml").getString("homes.split")));
        }

        return hc;
    }
}
