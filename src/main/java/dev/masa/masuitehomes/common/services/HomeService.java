package dev.masa.masuitehomes.common.services;

import com.j256.ormlite.stmt.SelectArg;
import dev.masa.masuitecore.common.services.AbstractDataService;
import dev.masa.masuitehomes.bungee.MaSuiteHomes;
import dev.masa.masuitehomes.bungee.events.HomeCreationEvent;
import dev.masa.masuitehomes.common.interfaces.HomeQueryType;
import dev.masa.masuitehomes.common.models.Home;
import lombok.SneakyThrows;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class HomeService extends AbstractDataService<Integer, Home, MaSuiteHomes> {

    public HomeService(MaSuiteHomes plugin) {
        super(plugin, Home.class);
    }


    /**
     * Teleport {@link ProxiedPlayer} to home
     *
     * @param player   player to teleport
     * @param home     home to teleport
     * @param callback callback to use
     */
    public void teleportToHome(ProxiedPlayer player, Home home, Consumer<Boolean> callback) {
        this.getPlugin().getApi().getTeleportService().teleportPlayerToLocation(player, home.getLocation(), callback);
    }

    /**
     * Get {@link Home} by like it's name
     *
     * @param owner    owner of the home
     * @param name     name of the home
     * @param callback callback to use
     */
    public void getHome(UUID owner, String name, Consumer<Optional<Home>> callback) {
        this.loadHome(owner, name, HomeQueryType.BY_OWNER_AND_LIKE_NAME, callback);
    }

    /**
     * Get {@link Home} by exactly like it's name
     *
     * @param owner    owner of the home
     * @param name     name of the home
     * @param callback callback to use
     */
    public void getHomeExact(UUID owner, String name, Consumer<Optional<Home>> callback) {
        this.loadHome(owner, name, HomeQueryType.BY_OWNER_AND_NAME, callback);
    }

    /**
     * Get homes of player
     *
     * @param uuid     owner of the homes
     * @param callback callback to use
     */
    @SneakyThrows
    public void getHomes(UUID uuid, Consumer<List<Home>> callback) {
        this.getPlugin().getProxy().getScheduler().runAsync(this.getPlugin(), () -> {
            try {
                callback.accept(this.getDao().queryBuilder().orderBy("name", true).where().in("owner", uuid).query());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadHome(UUID owner, String name, HomeQueryType queryType, Consumer<Optional<Home>> callback) {
        this.getPlugin().getProxy().getScheduler().runAsync(this.getPlugin(), () -> {
            try {
                Home home = null;
                if (queryType.equals(HomeQueryType.BY_OWNER_AND_NAME)) {
                    home = this.getDao().queryBuilder().orderBy("name", true).where().in("owner", owner).and().in("name", new SelectArg(name)).query().stream().findFirst().orElse(null);
                    callback.accept(Optional.ofNullable(home));
                    return;
                }
                home = this.getDao().queryBuilder().orderBy("name", true)
                        .where().in("owner", owner)
                        .and().in("name", new SelectArg(name))
                        .or().like("name", new SelectArg(name + "%"))
                        .and().in("owner", owner)
                        .query().stream().findFirst().orElse(null);
                callback.accept(Optional.ofNullable(home));
            } catch (SQLException e) {
                e.printStackTrace();
                callback.accept(Optional.empty());
            }
        });

    }

    @Override
    public void create(Home home, Consumer<Boolean> callback) {
        this.getPlugin().getProxy().getScheduler().runAsync(this.getPlugin(), () -> {
            try {
                this.getDao().create(home);
                callback.accept(true);
                this.getPlugin().getProxy().getPluginManager().callEvent(new HomeCreationEvent(home));
            } catch (SQLException e) {
                e.printStackTrace();
                callback.accept(false);
            }
        });
    }

    @Override
    public void update(Home home, Consumer<Boolean> callback) {
        this.getPlugin().getProxy().getScheduler().runAsync(this.getPlugin(), () -> {
            try {
                this.getDao().update(home);
                callback.accept(true);
            } catch (SQLException e) {
                e.printStackTrace();
                callback.accept(false);
            }
        });
    }

    @Override
    public void delete(Home home, Consumer<Boolean> callback) {
        this.getPlugin().getProxy().getScheduler().runAsync(this.getPlugin(), () -> {
            try {
                this.getDao().delete(home);
                callback.accept(true);
            } catch (SQLException e) {
                e.printStackTrace();
                callback.accept(false);
            }
        });
    }

    @Override
    public void getFromDatabase(Integer id, Consumer<Optional<Home>> callback) {
        this.getPlugin().getProxy().getScheduler().runAsync(this.getPlugin(), () -> {
            try {
                callback.accept(Optional.ofNullable(this.getDao().queryForId(id)));
            } catch (SQLException e) {
                e.printStackTrace();
                callback.accept(Optional.empty());
            }
        });
    }
}
