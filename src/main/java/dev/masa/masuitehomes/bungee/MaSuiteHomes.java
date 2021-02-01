package dev.masa.masuitehomes.bungee;

import dev.masa.masuitecore.bungee.Utils;
import dev.masa.masuitecore.bungee.api.MaSuiteCoreProxyAPI;
import dev.masa.masuitecore.bungee.chat.Formator;
import dev.masa.masuitecore.common.channels.BungeePluginChannel;
import dev.masa.masuitecore.common.config.ConfigLoader;
import dev.masa.masuitecore.common.interfaces.IDatabaseServiceProvider;
import dev.masa.masuitecore.common.objects.Location;
import dev.masa.masuitecore.common.services.DatabaseService;
import dev.masa.masuitecore.common.utils.Updator;
import dev.masa.masuitehomes.bungee.controllers.DeleteController;
import dev.masa.masuitehomes.bungee.controllers.ListController;
import dev.masa.masuitehomes.bungee.controllers.SetController;
import dev.masa.masuitehomes.bungee.controllers.TeleportController;
import dev.masa.masuitehomes.bungee.dataextensions.DataExtensionRegister;
import dev.masa.masuitehomes.common.config.proxy.HomeProxyMessageConfig;
import dev.masa.masuitehomes.common.services.HomeService;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MaSuiteHomes extends Plugin implements Listener, IDatabaseServiceProvider {

    public Utils utils = new Utils();

    @Getter
    private HomeService homeService;

    @Getter
    public HomeProxyMessageConfig messages;
    public Formator formator = new Formator();

    @Getter
    private final MaSuiteCoreProxyAPI api = new MaSuiteCoreProxyAPI();

    @SneakyThrows
    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerListener(this, this);
        // Check updates
        new Updator(getDescription().getVersion(), getDescription().getName(), "60632").checkUpdates();

        this.messages = HomeProxyMessageConfig.loadFrom(ConfigLoader.loadConfig("homes/messages.yml"));

        homeService = new HomeService(this);

        try {
            DataExtensionRegister.registerHomeExtension(this);
        } catch (NoClassDefFoundError | IllegalStateException | IllegalArgumentException ignored) {
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
            homeService.getHomes(p.getUniqueId(), homes -> homes.forEach(home -> {
                new BungeePluginChannel(this, p.getServer().getInfo(),
                        "AddHome",
                        p.getUniqueId().toString(),
                        home.serialize()
                ).send();
            }));
        }
    }

    @Override
    public DatabaseService getDatabaseService() {
        return MaSuiteCoreProxyAPI.getDatabaseService();
    }
}
