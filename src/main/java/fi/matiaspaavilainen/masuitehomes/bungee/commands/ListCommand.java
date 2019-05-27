package fi.matiaspaavilainen.masuitehomes.bungee.commands;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.core.Home;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ListCommand {

    private BungeeConfiguration config = new BungeeConfiguration();
    private Formator formator = new Formator();

    private HashMap<String, List<Home>> loadHomes(UUID uuid){
        Home h = new Home();

        HashMap<String, List<Home>> homeList = new HashMap<>();
        for (Home home : h.getHomes(uuid)) {
            if (!homeList.containsKey(home.getServer())) {
                homeList.put(home.getServer(), new ArrayList<>());
            }
            homeList.get(home.getServer()).add(home);
        }
        return homeList;
    }

    public void list(ProxiedPlayer p) {


        BaseComponent baseHome = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.title")));
        p.sendMessage(baseHome);

        loadHomes(p.getUniqueId()).forEach((server, homes) -> {

            TextComponent message = new TextComponent();
            TextComponent serverTitle = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.server-name").replace("%server%", server)));

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
        MaSuitePlayer msp = new MaSuitePlayer().find(name);
        if (msp.getUniqueId() == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("player-not-found"));
            return;
        }

        BaseComponent baseHome = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.title-others").replace("%player%", msp.getUsername())));
        p.sendMessage(baseHome);

        loadHomes(msp.getUniqueId()).forEach((server, homes) -> {

            TextComponent message = new TextComponent();
            TextComponent serverTitle = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.server-name").replace("%server%", server)));

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
