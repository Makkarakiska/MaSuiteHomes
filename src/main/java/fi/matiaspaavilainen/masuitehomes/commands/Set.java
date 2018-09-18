package fi.matiaspaavilainen.masuitehomes.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitecore.listeners.MaSuitePlayerLocation;
import fi.matiaspaavilainen.masuitecore.managers.Location;
import fi.matiaspaavilainen.masuitecore.managers.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.Home;
import fi.matiaspaavilainen.masuitehomes.MaSuiteHomes;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.TimeUnit;

public class Set extends Command {
    public Set() {
        super("sethome", "masuitehomes.home.set", "homeset", "createhome");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        if (!(cs instanceof ProxiedPlayer)) {
            return;
        }

        Formator formator = new Formator();
        Configuration config = new Configuration();
        ProxiedPlayer p = (ProxiedPlayer) cs;
        if (args.length == 1) {
            MaSuitePlayer msp = new MaSuitePlayer().find(p.getUniqueId());
            msp.requestLocation();
            Home h = new Home();
            h = h.findExact(args[0], p.getUniqueId());
            java.util.Set<Home> homes = new Home().homes(p.getUniqueId());
            if (h.getServer() != null) {
                ProxyServer.getInstance().getScheduler().schedule(new MaSuiteHomes(), () -> {
                    Location loc = MaSuitePlayerLocation.locations.get(p.getUniqueId());
                    Home home = new Home(args[0], p.getServer().getInfo().getName(), p.getUniqueId(), loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                    home.update(home);
                    formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.updated").replace("%home%", home.getName()));
                }, 100, TimeUnit.MILLISECONDS);
                MaSuitePlayerLocation.locations.remove(p.getUniqueId());
            } else {
                int max = 0;
                for (int i = 100; i > 0; i--) {
                    if (p.hasPermission("masuitehomes.home.limit." + i)) {
                        max = i - 1;
                        break;
                    }
                }
                if (homes.size() <= max) {
                    ProxyServer.getInstance().getScheduler().schedule(new MaSuiteHomes(), () -> {
                        Location loc = MaSuitePlayerLocation.locations.get(p.getUniqueId());
                        Home home = new Home(args[0], p.getServer().getInfo().getName(), p.getUniqueId(), loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                        home.set(home);
                        formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.set").replace("%home%", home.getName()));
                    }, 100, TimeUnit.MILLISECONDS);
                    MaSuitePlayerLocation.locations.remove(p.getUniqueId());

                } else {
                    formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-limit-reached"));
                }

            }
        } else {
            formator.sendMessage(p, config.load("homes", "syntax.yml").getString("home.set"));
        }
    }
}
