package dev.masa.masuitehomes.bungee.controllers;

import dev.masa.masuitecore.common.models.MaSuitePlayer;
import dev.masa.masuitehomes.bungee.MaSuiteHomes;
import dev.masa.masuitehomes.common.models.Home;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ListController {

    private final MaSuiteHomes plugin;

    public ListController(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    private void loadHomes(UUID uuid, Consumer<HashMap<String, List<Home>>> callback) {
        HashMap<String, List<Home>> homeList = new HashMap<>();
        plugin.getHomeService().getHomes(uuid, homes -> {
            for (Home home : homes) {
                if (!homeList.containsKey(home.getLocation().getServer())) {
                    homeList.put(home.getLocation().getServer(), new ArrayList<>());
                }
                homeList.get(home.getLocation().getServer()).add(home);
            }
            callback.accept(homeList);
        });
    }

    public void list(ProxiedPlayer p) {
        BaseComponent baseHome = new TextComponent(plugin.formator.colorize(this.plugin.getMessages().getHomes().getTitle()));
        p.sendMessage(baseHome);

        loadHomes(p.getUniqueId(), homeList -> homeList.forEach((server, homes) -> {
            TextComponent message = new TextComponent();
            TextComponent serverTitle = new TextComponent(plugin.formator.colorize(this.plugin.getMessages().getHomes().getServerName().replace("%server%", server)));

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

        }));


    }

    public void list(ProxiedPlayer proxiedPlayer, String name) {
        plugin.getApi().getPlayerService().getPlayer(name, playerQuery -> {
            if (!playerQuery.isPresent()) {
                plugin.formator.sendMessage(proxiedPlayer, this.plugin.getApi().getCore().getMessages().getPlayerNotOnline());
                return;
            }

            MaSuitePlayer player = playerQuery.get();

            BaseComponent baseHome = new TextComponent(plugin.formator.colorize(this.plugin.getMessages().getHomes().getTitleOthers().replace("%player%", player.getUsername())));
            proxiedPlayer.sendMessage(baseHome);

            loadHomes(player.getUniqueId(), homeList -> homeList.forEach((server, homes) -> {

                TextComponent message = new TextComponent();
                TextComponent serverTitle = new TextComponent(plugin.formator.colorize(this.plugin.getMessages().getHomes().getName().replace("%server%", server)));

                message.addExtra(serverTitle);

                int i = 0;
                int counter = 0;
                for (Home home : homes) {
                    if (i++ == homes.size() - 1) {
                        message.addExtra(addToList(home, proxiedPlayer.getName(), player.getUsername(), false));
                    } else {
                        message.addExtra(addToList(home, proxiedPlayer.getName(), player.getUsername(), true));
                    }
                    counter++;
                    if (counter == 20) {
                        proxiedPlayer.sendMessage(message);
                        message = new TextComponent();
                        counter = 0;
                    }

                }
                if (homes.size() - 1 < 20) {
                    proxiedPlayer.sendMessage(message);
                }
            }));
        });
    }

    private TextComponent addToList(Home home, String requester, String owner, boolean splitter) {
        TextComponent hc = new TextComponent(plugin.formator.colorize(this.plugin.getMessages().getHomes().getName().replace("%home%", home.getName())));
        if (requester.equalsIgnoreCase(owner)) {
            hc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home.getName()));
        } else {
            hc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home.getName() + " " + owner));
        }
        hc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(plugin.formator.colorize(this.plugin.getMessages().getHomeHoverText().replace("%home%", home.getName()))).create()));
        if (splitter) {
            hc.addExtra(plugin.formator.colorize(this.plugin.getMessages().getHomes().getSplit()));
        }

        return hc;
    }
}
