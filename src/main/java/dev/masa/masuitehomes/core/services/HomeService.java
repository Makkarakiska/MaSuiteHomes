package dev.masa.masuitehomes.core.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.table.TableUtils;
import dev.masa.masuitecore.core.channels.BungeePluginChannel;
import dev.masa.masuitecore.core.models.MaSuitePlayer;
import dev.masa.masuitehomes.bungee.MaSuiteHomes;
import dev.masa.masuitehomes.core.models.Home;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HomeService {

private Dao<Home, Integer> homeDao;
private MaSuiteHomes plugin;

@SneakyThrows
public HomeService(MaSuiteHomes plugin) {
    this.plugin = plugin;
    this.homeDao = DaoManager.createDao(plugin.getApi().getDatabaseService().getConnection(), Home.class);
    this.homeDao.setObjectCache(true);
    TableUtils.createTableIfNotExists(plugin.getApi().getDatabaseService().getConnection(), Home.class);
}

    /**
     * Teleport player to home
     *
     * @param msPlayer player to teleport
     * @param home     home to teleport
     */
    public void teleportToHome(MaSuitePlayer msPlayer, Home home) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(msPlayer.getUniqueId());
        this.teleport(player, home);
    }

    /**
     * Teleport player to home
     *
     * @param player player to teleport
     * @param home   home to teleport
     */
    public void teleportToHome(ProxiedPlayer player, Home home) {
        this.teleport(player, home);
    }

    /**
     * Teleport player to home
     *
     * @param uuid uuid of the player to teleport
     * @param home home to teleport
     */
    public void teleportToHome(UUID uuid, Home home) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(uuid);
        this.teleport(player, home);
    }

    /**
     * Teleports player to home
     *
     * @param player player to teleport
     * @param home   home to teleport
     */
    private void teleport(ProxiedPlayer player, Home home) {
        if (home == null) {
            plugin.formator.sendMessage(player, plugin.config.load("homes", "messages.yml").getString("home-not-found"));
            return;
        }

        BungeePluginChannel bpc = new BungeePluginChannel(plugin, ProxyServer.getInstance().getServerInfo(home.getLocation().getServer()),
                "HomePlayer",
                player.getUniqueId().toString(),
                home.getLocation().serialize()
        );

        if (!player.getServer().getInfo().getName().equals(home.getLocation().getServer())) {
            plugin.getProxy().getScheduler().runAsync(plugin, () -> player.connect(ProxyServer.getInstance().getServerInfo(home.getLocation().getServer()), (connected, throwable) -> {
                if (connected) {
                    plugin.getProxy().getScheduler().schedule(plugin, () -> {
                        bpc.send();
                        plugin.utils.applyCooldown(plugin, player.getUniqueId(), "homes");
                    }, plugin.config.load(null, "config.yml").getInt("teleportation-delay"), TimeUnit.MILLISECONDS);
                }
            }));
        } else {
            bpc.send();
        }
        plugin.formator.sendMessage(player, plugin.config.load("homes", "messages.yml").getString("home.teleported").replace("%home%", home.getName()));
    }

    /**
     * Create a new {@link Home}
     *
     * @param home home to create
     */
    @SneakyThrows
    public Home createHome(Home home) {
        homeDao.create(home);
        return home;
    }

    /**
     * Update specific {@link Home}
     */
    @SneakyThrows
    public Home updateHome(Home home) {
        homeDao.update(home);
        return home;
    }

    /**
     * Remove home
     *
     * @param home home to remove
     */
    @SneakyThrows
    public void removeHome(Home home) {
        homeDao.delete(home);
    }

    /**
     * Gets player home points
     *
     * @param uuid owner of homes
     * @return returns a list of homes
     */
    @SneakyThrows
    public List<Home> getHomes(UUID uuid) {
        return homeDao.queryBuilder().orderBy("name", true).where().in("owner", uuid).query();
    }

    @SneakyThrows
    public void initializeHomes(UUID uuid) {
        List<Home> homesList = homeDao.queryBuilder().orderBy("name", true).where().in("owner", uuid).query();
    }

    /**
     * Get {@link MaSuitePlayer}'s home by name
     *
     * @param player owner of the home
     * @param home   name of the home
     * @return returns home or null
     */
    public Home getHome(MaSuitePlayer player, String home) {
        return this.loadHome(player.getUniqueId(), home, "findHomeByOwnerAndLikeName");
    }

    /**
     * Get username home by name
     *
     * @param username owner of the home
     * @param home     name of the home
     * @return returns home or null
     */
    public Home getHome(String username, String home) {
        MaSuitePlayer player = plugin.getApi().getPlayerService().getPlayer(username);
        if (player == null) {
            return null;
        }
        return this.loadHome(player.getUniqueId(), home, "findHomeByOwnerAndLikeName");
    }

    /**
     * Get username home by name
     *
     * @param uuid owner of the home
     * @param home name of the home
     * @return returns home or null
     */
    public Home getHome(UUID uuid, String home) {
        return this.loadHome(uuid, home, "findHomeByOwnerAndLikeName");
    }


    /**
     * Get username home by exact name
     *
     * @param uuid owner of the home
     * @param home name of the home
     * @return returns home or null
     */
    public Home getHomeExact(UUID uuid, String home) {
        return this.loadHome(uuid, home, "findHomeByOwnerAndName");
    }

    /**
     * Load home from cache or database
     *
     * @param uuid owner of the home
     * @param name name of the home
     * @return return home or null from cache or database
     */
    @SneakyThrows
    private Home loadHome(UUID uuid, String name, String type) {
        // Search home from database
        Home home = null;
        if (type.equals("findHomeByOwnerAndName")) {
            home = homeDao.queryBuilder().orderBy("name", true).where().in("owner", uuid).and().in("name", new SelectArg(name)).query().stream().findFirst().orElse(null);
        }
        if (type.equals("findHomeByOwnerAndLikeName")) {
            home = homeDao.queryBuilder().orderBy("name", true)
                    .where().in("owner", uuid)
                    .and().in("name", new SelectArg(name))
                    .or().like("name", new SelectArg(name + "%"))
                    .and().in("owner", uuid)
                    .query().stream().findFirst().orElse(null);
        }
        return home;
    }
}
