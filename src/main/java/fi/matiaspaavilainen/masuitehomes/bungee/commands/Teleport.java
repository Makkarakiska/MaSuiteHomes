package fi.matiaspaavilainen.masuitehomes.bungee.commands;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.channels.BungeePluginChannel;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.bungee.MaSuiteHomes;
import fi.matiaspaavilainen.masuitehomes.core.Home;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class Teleport {

    private MaSuiteHomes plugin;
    private Formator formator = new Formator();
    private BungeeConfiguration config = new BungeeConfiguration();

    public Teleport(MaSuiteHomes p) {
        plugin = p;
    }

    public void teleport(ProxiedPlayer p, String hs) {
        Home home = new Home().findLike(hs, p.getUniqueId());

        send(p, home);
        if (home != null) {
            plugin.sendCooldown(p, home);
        }
    }

    public void teleport(ProxiedPlayer p, String name, String hs) {
        MaSuitePlayer msp = new MaSuitePlayer().find(name);
        if (msp.getUniqueId() == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("player-not-found"));
            return;
        }
        Home home = new Home().findLike(hs, msp.getUniqueId());
        send(p, home);
    }

    private void send(ProxiedPlayer p, Home home) {
        if (home == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-not-found"));
            return;
        }

        if (!p.getServer().getInfo().getName().equals(home.getServer())) {
            p.connect(ProxyServer.getInstance().getServerInfo(home.getServer()));
        }
        BungeePluginChannel bpc = new BungeePluginChannel(plugin, ProxyServer.getInstance().getServerInfo(home.getServer()),
                new Object[]{"HomePlayer",
                        p.getUniqueId().toString(),
                        home.getLocation().getWorld(),
                        home.getLocation().getX(),
                        home.getLocation().getY(),
                        home.getLocation().getZ(),
                        home.getLocation().getYaw(),
                        home.getLocation().getPitch()
                });
        if (!p.getServer().getInfo().getName().equals(home.getServer())) {
            plugin.getProxy().getScheduler().schedule(plugin, bpc::send, 500, TimeUnit.MILLISECONDS);
        } else {
            bpc.send();
        }
        formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.teleported").replace("%home%", home.getName()));

    }
}
