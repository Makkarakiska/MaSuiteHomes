package fi.matiaspaavilainen.masuitehomes.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitehomes.Home;
import fi.matiaspaavilainen.masuitehomes.MaSuiteHomes;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Teleport {
    public void teleport(ProxiedPlayer p, String hs) {
        Formator formator = new Formator();
        Configuration config = new Configuration();
        Home home = new Home();
        home = home.findLike(hs, p.getUniqueId());

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
                ProxyServer.getInstance().getScheduler().schedule(new MaSuiteHomes(), () -> ProxyServer.getInstance().getServerInfo(h.getServer()).sendData("BungeeCord", b.toByteArray()), 350, TimeUnit.MILLISECONDS);
            }else {
                ProxyServer.getInstance().getServerInfo(home.getServer()).sendData("BungeeCord", b.toByteArray());
            }
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.teleported").replace("%home%", home.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
