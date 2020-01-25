package fi.matiaspaavilainen.masuitehomes.bungee;

import fi.matiaspaavilainen.masuitecore.bungee.Utils;
import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.Updator;
import fi.matiaspaavilainen.masuitecore.core.api.MaSuiteCoreAPI;
import fi.matiaspaavilainen.masuitecore.core.channels.BungeePluginChannel;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import fi.matiaspaavilainen.masuitehomes.bungee.controllers.DeleteController;
import fi.matiaspaavilainen.masuitehomes.bungee.controllers.ListController;
import fi.matiaspaavilainen.masuitehomes.bungee.controllers.SetController;
import fi.matiaspaavilainen.masuitehomes.bungee.controllers.TeleportController;
import fi.matiaspaavilainen.masuitehomes.core.models.Home;
import fi.matiaspaavilainen.masuitehomes.core.services.HomeService;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MaSuiteHomes extends Plugin implements Listener {

    public Utils utils = new Utils();

    public HomeService homeService;

    public BungeeConfiguration config = new BungeeConfiguration();
    public Formator formator = new Formator();

    public MaSuiteCoreAPI api = new MaSuiteCoreAPI();

    @Override
    public void onEnable() {
        //Configs
        config.create(this, "homes", "messages.yml");
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerListener(this, this);
        // Check updates
        new Updator(getDescription().getVersion(), getDescription().getName(), "60632").checkUpdates();

        config.addDefault("homes/messages.yml", "homes.title-others", "&9%player%''s &7homes: ");
        config.addDefault("homes/messages.yml", "homes.server-name", "&9%server%&7: ");

        homeService = new HomeService(this);
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
                Location location = new Location().deserialize(in.readUTF());
                set.set(p, in.readUTF(), in.readInt(), location);
            }
        }

        if (subchannel.equals("SetHomeOtherCommand")) {
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            String player = in.readUTF();
            if (utils.isOnline(p)) {
                SetController set = new SetController(this);
                Location location = new Location().deserialize(in.readUTF());
                set.set(p, player, in.readUTF(), in.readInt(), location);
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

    public void listHomes(ProxiedPlayer p) {
        if (utils.isOnline(p)) {
            for (Home home : homeService.getHomes(p.getUniqueId())) {
                new BungeePluginChannel(this, p.getServer().getInfo(),
                        "AddHome",
                        p.getUniqueId().toString(),
                        home.serialize()
                ).send();
            }
        }
    }
}
