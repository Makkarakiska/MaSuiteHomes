package fi.matiaspaavilainen.masuitehomes.bungee.commands;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.bungee.Home;
import fi.matiaspaavilainen.masuitehomes.bungee.MaSuiteHomes;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
        if(home != null){
            plugin.sendCooldown(p, home);
        }
    }

    public void teleport(ProxiedPlayer p, String name, String hs){
        MaSuitePlayer msp = new MaSuitePlayer().find(name);
        if(msp.getUniqueId() == null){
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
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            if (!p.getServer().getInfo().getName().equals(home.getServer())) {
                p.connect(ProxyServer.getInstance().getServerInfo(home.getServer()));
            }
            out.writeUTF("HomePlayer");
            out.writeUTF(p.getUniqueId().toString());
            out.writeUTF(home.getLocation().getWorld());
            out.writeDouble(home.getLocation().getX());
            out.writeDouble(home.getLocation().getY());
            out.writeDouble(home.getLocation().getZ());
            out.writeFloat(home.getLocation().getYaw());
            out.writeFloat(home.getLocation().getPitch());
            if (!p.getServer().getInfo().getName().equals(home.getServer())) {
                final Home h = home;
                ProxyServer.getInstance().getScheduler().schedule(plugin, () -> ProxyServer.getInstance().getServerInfo(h.getServer()).sendData("BungeeCord", b.toByteArray()), 500, TimeUnit.MILLISECONDS);
            } else {
                ProxyServer.getInstance().getServerInfo(home.getServer()).sendData("BungeeCord", b.toByteArray());
            }
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.teleported").replace("%home%", home.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
