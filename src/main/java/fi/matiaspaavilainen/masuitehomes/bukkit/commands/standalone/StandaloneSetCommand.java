package fi.matiaspaavilainen.masuitehomes.bukkit.commands.standalone;

import fi.matiaspaavilainen.masuitecore.bukkit.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.adapters.BukkitAdapter;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import fi.matiaspaavilainen.masuitehomes.bungee.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.UUID;

public class StandaloneSetCommand implements CommandExecutor {

    private MaSuiteHomes plugin;
    private Formator formator = new Formator();
    private BukkitConfiguration config = new BukkitConfiguration();

    public StandaloneSetCommand(MaSuiteHomes p) {
        plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String s, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (plugin.in_command.contains(cs)) {
                formator.sendMessage((Player) cs, config.load(null, "messages.yml").getString("on-active-command"));
                return;
            }

            plugin.in_command.add(cs);

            Player p = (Player) cs;
            int max = 0;
            for (PermissionAttachmentInfo permInfo : p.getEffectivePermissions()) {
                String perm = permInfo.getPermission();
                if (perm.startsWith("masuitehomes.home.limit.")) {
                    String amount = perm.replace("masuitehomes.home.limit.", "");
                    if (amount.equalsIgnoreCase("*")) {
                        max = -1;
                        break;
                    }
                    try {
                        if (Integer.parseInt(amount) > max) {
                            max = Integer.parseInt(amount);
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("[MaSuite] [Homes] Please check your home limit permissions (Not an integer or *) ");
                    }
                }
            }
            switch (args.length) {
                case (0):
                    set(p.getUniqueId(), p,"home", max, p.getLocation());
                    break;
                case (1):
                    set(p.getUniqueId(), p, args[0], max, p.getLocation());
                    break;
                case (2):
                    if (p.hasPermission("masuitehomes.home.set.other")) {
                        MaSuitePlayer msp = new MaSuitePlayer().find(args[0]);
                        if(msp.getUniqueId() != null){
                            set(msp.getUniqueId(), p, args[1], max, p.getLocation());
                        } else {
                            formator.sendMessage(p, config.load("homes", "messages.yml").getString("player-not-found"));
                        }
                    } else {
                        formator.sendMessage(p, config.load(null, "messages.yml").getString("no-permission"));
                    }
                    break;
                default:
                    formator.sendMessage(p, config.load("homes", "syntax.yml").getString("home.set"));
                    break;
            }

            plugin.in_command.remove(cs);
        });
        return false;
    }

    private void set(UUID owner, Player creator, String hs, int max, Location loc) {
        Home h = new Home().findExact(hs, owner);
        java.util.Set<Home> homes = new Home().getHomes(owner);
        if (h != null) {
            Home home = new Home(hs, "standalone", owner, BukkitAdapter.adapt(loc)).update();
            formator.sendMessage(creator, config.load("homes", "messages.yml").getString("home.updated").replace("%home%", home.getName()));
        } else {
            if (homes.size() < max || max == -1) {
                Home home = new Home(hs, "standalone", owner, BukkitAdapter.adapt(loc)).create();
                formator.sendMessage(creator, config.load("homes", "messages.yml").getString("home.set").replace("%home%", home.getName()));
            } else {
                formator.sendMessage(creator, config.load("homes", "messages.yml").getString("home-limit-reached"));
            }

        }
    }
}
