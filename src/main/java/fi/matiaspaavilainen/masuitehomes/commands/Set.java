package fi.matiaspaavilainen.masuitehomes.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitecore.listeners.MaSuitePlayerLocation;
import fi.matiaspaavilainen.masuitecore.managers.Location;
import fi.matiaspaavilainen.masuitecore.managers.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.Home;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

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
            System.out.println("adw");
            java.util.Set<Home> homes = new Home().homes(p.getUniqueId());
            //if (p.hasPermission("masuitehomes.home.limit." + "10")) {
                System.out.println("faeff");
                MaSuitePlayer msp = new MaSuitePlayer().find(p.getUniqueId());
                msp.requestLocation();
                Location loc = MaSuitePlayerLocation.locations.get(p.getUniqueId());
                Home home = new Home(args[0], p.getServer().getInfo().getName(), p.getUniqueId(), loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                home.set(home);
                // home = homes.stream().filter(h -> h.getName().equalsIgnoreCase(args[0]));

            /*} else {
                formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-limit-reached"));
            }*/

        } else {
            formator.sendMessage(p, config.load("homes", "syntax.yml").getString("home.set"));
        }
    }
}
