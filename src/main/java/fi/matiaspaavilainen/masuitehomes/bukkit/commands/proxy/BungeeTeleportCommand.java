package fi.matiaspaavilainen.masuitehomes.bukkit.commands.proxy;

import fi.matiaspaavilainen.masuitecore.bukkit.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BungeeTeleportCommand implements CommandExecutor {
    private MaSuiteHomes plugin;
    private Formator formator = new Formator();
    private BukkitConfiguration config = new BukkitConfiguration();

    public BungeeTeleportCommand(MaSuiteHomes p) {
        plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (!(cs instanceof Player)) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (plugin.in_command.contains(cs)) {
                formator.sendMessage(cs, config.load(null, "messages.yml").getString("on-active-command"));
                return;
            }

            plugin.in_command.add(cs);

            Player p = (Player) cs;
            switch (args.length) {
                case (0):
                    if (checkCooldown(p)) {
                        sendLastLoc(p);
                        new BukkitPluginChannel(plugin, p, new Object[]{"HomeCommand", p.getName(), "home"}).send();
                    }
                    break;
                case (1):
                    if (checkCooldown(p)) {
                        sendLastLoc(p);
                        new BukkitPluginChannel(plugin, p, new Object[]{"HomeCommand", p.getName(), args[0]}).send();
                    }
                    break;
                case (2):
                    if (p.hasPermission("masuitehomes.home.teleport.other")) {
                        sendLastLoc(p);
                        new BukkitPluginChannel(plugin, p, new Object[]{"HomeOtherCommand", p.getName(), args[0], args[1]}).send();
                    } else {
                        formator.sendMessage(p, config.load(null, "messages.yml").getString("no-permission"));
                    }
                    break;
                default:
                    formator.sendMessage(p, config.load("homes", "syntax.yml").getString("home.teleport"));
                    break;
            }

            plugin.in_command.remove(cs);
        });

        return true;
    }

    private Boolean checkCooldown(Player p) {
        if (config.load("homes", "config.yml").getInt("cooldown") > 0) {
            if (p.hasPermission("masuitehomes.cooldown.override")) return true;
            if (plugin.cooldowns.containsKey(p.getUniqueId())) {
                if (System.currentTimeMillis() - plugin.cooldowns.get(p.getUniqueId()) > config.load("homes", "config.yml").getInt("cooldown") * 1000) {
                    plugin.cooldowns.remove(p.getUniqueId());
                    return true;
                } else {
                    formator.sendMessage(p, config.load("homes", "messages.yml").getString("in-cooldown").replace("%time%", config.load("homes", "config.yml").getString("cooldown")));
                    return false;
                }
            } else {
                return true;
            }
        }
        return true;
    }

    private void sendLastLoc(Player p) {
        Location loc = p.getLocation();
        new BukkitPluginChannel(plugin, p, new Object[]{"MaSuiteTeleports", "GetLocation", p.getName(), loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":"
                + loc.getYaw() + ":" + loc.getPitch()}).send();
    }
}
