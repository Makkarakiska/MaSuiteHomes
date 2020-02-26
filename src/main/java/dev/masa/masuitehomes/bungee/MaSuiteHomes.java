package dev.masa.masuitehomes.bungee;

import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.ExtensionService;
import dev.masa.masuitehomes.bungee.controllers.DeleteController;
import dev.masa.masuitehomes.bungee.controllers.ListController;
import dev.masa.masuitehomes.bungee.controllers.SetController;
import dev.masa.masuitehomes.bungee.controllers.TeleportController;
import dev.masa.masuitehomes.core.dataextensions.HomeDataExtension;
import dev.masa.masuitehomes.core.services.HomeService;
import dev.masa.masuitecore.bungee.Utils;
import dev.masa.masuitecore.bungee.chat.Formator;
import dev.masa.masuitecore.core.Updator;
import dev.masa.masuitecore.core.api.MaSuiteCoreAPI;
import dev.masa.masuitecore.core.channels.BungeePluginChannel;
import dev.masa.masuitecore.core.configuration.BungeeConfiguration;
import dev.masa.masuitecore.core.objects.Location;
import dev.masa.masuitehomes.core.models.Home;
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

        try {

            DataExtension yourExtension = new HomeDataExtension(this);
            ExtensionService.getInstance().register(yourExtension);

        } catch (NoClassDefFoundError | IllegalStateException ignored) {
        } catch (IllegalArgumentException dataExtensionImplementationIsInvalid) {
            dataExtensionImplementationIsInvalid.printStackTrace();
        }
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
            ProxiedPlayer player = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(player)) {
                teleport.teleport(player, in.readUTF());
            }

        }
        if (subchannel.equals("HomeOtherCommand")) {
            TeleportController teleport = new TeleportController(this);
            ProxiedPlayer player = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(player)) {
                teleport.teleport(player, in.readUTF(), in.readUTF());
            }
        }
        if (subchannel.equals("SetHomeCommand")) {
            ProxiedPlayer player = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(player)) {
                SetController set = new SetController(this);
                Location location = new Location().deserialize(in.readUTF());
                set.set(player, in.readUTF(), location, in.readInt(), in.readInt());
            }
        }

        if (subchannel.equals("SetHomeOtherCommand")) {
            ProxiedPlayer player = getProxy().getPlayer(in.readUTF());
            String owner = in.readUTF();
            if (utils.isOnline(player)) {
                SetController set = new SetController(this);
                Location location = new Location().deserialize(in.readUTF());
                set.set(player, owner, in.readUTF(), location, in.readInt(), in.readInt());
            }
        }

        if (subchannel.equals("DelHomeCommand")) {
            ProxiedPlayer player = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(player)) {
                DeleteController delete = new DeleteController(this);
                delete.delete(player, in.readUTF());
            }
        }

        if (subchannel.equals("DelHomeOtherCommand")) {
            ProxiedPlayer player = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(player)) {
                DeleteController delete = new DeleteController(this);
                delete.delete(player, in.readUTF(), in.readUTF());
            }
        }

        if (subchannel.equals("ListHomeCommand")) {
            ProxiedPlayer player = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(player)) {
                ListController list = new ListController(this);
                list.list(player);
            }
        }

        if (subchannel.equals("ListHomeOtherCommand")) {
            ProxiedPlayer player = getProxy().getPlayer(in.readUTF());
            if (utils.isOnline(player)) {
                ListController list = new ListController(this);
                list.list(player, in.readUTF());
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
