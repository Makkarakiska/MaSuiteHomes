package fi.matiaspaavilainen.masuitehomes.bungee;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.bungee.Utils;
import fi.matiaspaavilainen.masuitecore.core.Updator;
import fi.matiaspaavilainen.masuitecore.core.channels.BungeePluginChannel;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.database.ConnectionManager;
import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import fi.matiaspaavilainen.masuitehomes.bungee.controllers.DeleteController;
import fi.matiaspaavilainen.masuitehomes.bungee.controllers.ListController;
import fi.matiaspaavilainen.masuitehomes.bungee.controllers.SetController;
import fi.matiaspaavilainen.masuitehomes.bungee.controllers.TeleportController;
import fi.matiaspaavilainen.masuitehomes.core.HibernateUtil;
import fi.matiaspaavilainen.masuitehomes.core.models.Home;
import fi.matiaspaavilainen.masuitehomes.core.services.HomeService;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class MaSuiteHomes extends Plugin implements Listener {

    private Utils utils = new Utils();

    public HomeService homeService;

    public BungeeConfiguration config = new BungeeConfiguration();
    public Formator formator = new Formator();

    @Override
    public void onEnable() {
        //Configs
        config.create(this, "homes", "messages.yml");
        getProxy().getPluginManager().registerListener(this, this);

        //Commands
        ConnectionManager.db.createTable("homes", "(id INT(10) unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100) NOT NULL, owner VARCHAR(36) NOT NULL, server VARCHAR(100) NOT NULL, world VARCHAR(100) NOT NULL, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        // Check updates
        new Updator(new String[]{getDescription().getVersion(), getDescription().getName(), "60632"}).checkUpdates();

        config.addDefault("homes/messages.yml", "homes.title-others", "&9%player%''s &7homes: ");
        config.addDefault("homes/messages.yml", "homes.server-name", "&9%server%&7: ");

        homeService = new HomeService(this);
    }

    @Override
    public void onDisable() {
        HibernateUtil.shutdown();
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        if (!homeService.homes.containsKey(event.getPlayer().getUniqueId())) {
            homeService.homes.put(event.getPlayer().getUniqueId(), new ArrayList<>());
        }
        getProxy().getScheduler().runAsync(this, () -> homeService.getHomes(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) throws IOException {
        if (!e.getTag().equals("BungeeCord")) {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
        String subchannel = in.readUTF();
        if (subchannel.equals("HomeCommand")) {
            TeleportController teleport = new TeleportController(this);
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(p)) {
                teleport.teleport(p, in.readUTF());
            }

        }
        if (subchannel.equals("HomeOtherCommand")) {
            TeleportController teleport = new TeleportController(this);
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(p)) {
                teleport.teleport(p, in.readUTF(), in.readUTF());
            }
        }
        if (subchannel.equals("SetHomeCommand")) {
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(p)) {
                SetController set = new SetController(this);
                String[] location = in.readUTF().split(":");
                set.set(p, in.readUTF(), in.readInt(), new Location(location[0], Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]), Float.parseFloat(location[4]), Float.parseFloat(location[5])));
            }
        }

        if (subchannel.equals("SetHomeOtherCommand")) {
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            String player = in.readUTF();
            if (utils.isOnline(p)) {
                SetController set = new SetController(this);
                String[] location = in.readUTF().split(":");
                set.set(p, player, in.readUTF(), in.readInt(), new Location(location[0], Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]), Float.parseFloat(location[4]), Float.parseFloat(location[5])));
            }
        }

        if (subchannel.equals("DelHomeCommand")) {
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(p)) {
                DeleteController delete = new DeleteController(this);
                delete.delete(p, in.readUTF());
            }
        }

        if (subchannel.equals("DelHomeOtherCommand")) {
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(p)) {
                DeleteController delete = new DeleteController(this);
                delete.delete(p, in.readUTF(), in.readUTF());
            }
        }

        if (subchannel.equals("ListHomeCommand")) {
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(p)) {
                ListController list = new ListController(this);
                list.list(p);
            }
        }

        if (subchannel.equals("ListHomeOtherCommand")) {
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(p)) {
                ListController list = new ListController(this);
                list.list(p, in.readUTF());
            }
        }

        if (subchannel.equals("ListHomes")) {
            listHomes(getProxy().getPlayer(in.readUTF()));
        }
    }

    public void sendCooldown(ProxiedPlayer p, Home home) {
        BungeePluginChannel bpc = new BungeePluginChannel(this, p.getServer().getInfo(),
                new Object[]{"HomeCooldown", p.getUniqueId().toString(), System.currentTimeMillis()});
        if (!getProxy().getServerInfo(home.getServer()).getName().equals(p.getServer().getInfo().getName())) {
            getProxy().getScheduler().schedule(this, bpc::send, 500, TimeUnit.MILLISECONDS);
        } else {
            bpc.send();
        }

    }

    public void listHomes(ProxiedPlayer p) {
        if (utils.isOnline(p)) {
            for (Home home : homeService.getHomes(p.getUniqueId())) {
                StringJoiner info = new StringJoiner(":");
                Location loc = home.getLocation();
                info.add(home.getName())
                        .add(home.getServer())
                        .add(loc.getWorld())
                        .add(loc.getX().toString())
                        .add(loc.getY().toString())
                        .add(loc.getZ().toString());

                new BungeePluginChannel(this, p.getServer().getInfo(), new Object[]{
                        "AddHome",
                        p.getUniqueId().toString(),
                        info.toString()
                }).send();
            }
        }
    }
}
