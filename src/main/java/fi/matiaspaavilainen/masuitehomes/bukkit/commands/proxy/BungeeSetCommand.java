package fi.matiaspaavilainen.masuitehomes.bukkit.commands.proxy;

import fi.matiaspaavilainen.masuitecore.bukkit.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.PluginChannel;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class BungeeSetCommand implements CommandExecutor {

    private MaSuiteHomes plugin;
    private Formator formator = new Formator();
    private BukkitConfiguration config = new BukkitConfiguration();

    public BungeeSetCommand(MaSuiteHomes p) {
        plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (!(cs instanceof Player)) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (plugin.in_command.contains(cs)) {
                formator.sendMessage((Player) cs, config.load(null, "messages.yml").getString("on-active-command"));
                return;
            }

            plugin.in_command.add(cs);

            Player p = (Player) cs;
            Location loc = p.getLocation();
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
            String l = loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
            switch (args.length) {
                case (0):
                    new PluginChannel(plugin, p, new Object[]{"SetHomeCommand", p.getName(), l, "home", max}).send();
                    break;
                case (1):
                    new PluginChannel(plugin, p, new Object[]{"SetHomeCommand", p.getName(), l, args[0], max}).send();
                    break;
                case (2):
                    if (p.hasPermission("masuitehomes.home.set.other")) {
                        new PluginChannel(plugin, p, new Object[]{"SetHomeOtherCommand", p.getName(), args[0], l, args[1], -1}).send();
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

        return true;
    }
}