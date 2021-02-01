package dev.masa.masuitehomes.bungee.controllers;

import dev.masa.masuitecore.common.objects.Location;
import dev.masa.masuitehomes.bungee.MaSuiteHomes;
import dev.masa.masuitehomes.common.models.Home;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class SetController {

    private final MaSuiteHomes plugin;

    public SetController(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    public void set(ProxiedPlayer player, String home, Location loc, int maxGlobalHomes, int maxServerHomes) {
        loc.setServer(player.getServer().getInfo().getName());
        setHome(player, home, loc, player.getUniqueId(), maxGlobalHomes, maxServerHomes);
    }

    public void set(ProxiedPlayer player, String name, String home, Location loc, int maxGlobalHomes, int maxServerHomes) {
        plugin.getApi().getPlayerService().getPlayer(name, playerQuery -> {
            if (!playerQuery.isPresent()) {
                this.plugin.formator.sendMessage(player, plugin.getApi().getCore().getMessages().getPlayerNotOnline());
                return;
            }
            loc.setServer(player.getServer().getInfo().getName());
            setHome(player, home, loc, playerQuery.get().getUniqueId(), maxGlobalHomes, maxServerHomes);
        });
    }

    private void setHome(ProxiedPlayer player, String name, Location loc, UUID owner, int maxGlobalHomes, int maxServerHomes) {
        plugin.getHomeService().getHomeExact(owner, name, homeQuery -> {
            plugin.getHomeService().getHomes(owner, homes -> {
                Home home;
                long serverHomeCount = homes.stream().filter(filteredHome -> filteredHome.getLocation().getServer().equalsIgnoreCase(player.getServer().getInfo().getName())).count();
                if (homeQuery.isPresent() && (homes.size() <= maxGlobalHomes || maxGlobalHomes == -1) && (serverHomeCount <= maxServerHomes || maxServerHomes == -1)) {
                    home = homeQuery.get();
                    home.setLocation(loc);
                    Home finalHome = home;
                    plugin.getHomeService().update(home, success -> {
                        if (success) {
                            this.plugin.formator.sendMessage(player, plugin.getMessages().getHome().getUpdated().replace("%home%", finalHome.getName()));
                            plugin.listHomes(player);
                        }
                    });
                } else if ((homes.size() < maxGlobalHomes || maxGlobalHomes == -1) && (serverHomeCount < maxServerHomes || maxServerHomes == -1)) {
                    home = new Home(name, owner, loc);
                    Home finalHome1 = home;
                    plugin.getHomeService().create(home, success -> {
                        if (success) {
                            this.plugin.formator.sendMessage(player, plugin.getMessages().getHome().getSet().replace("%home%", finalHome1.getName()));
                            plugin.listHomes(player);
                        }
                    });
                } else {
                    this.plugin.formator.sendMessage(player, plugin.getMessages().getHomeLimitReached());
                }
            });
        });
    }
}
