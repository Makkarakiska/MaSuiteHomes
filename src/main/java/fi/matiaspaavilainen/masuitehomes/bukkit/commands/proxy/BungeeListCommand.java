package fi.matiaspaavilainen.masuitehomes.bukkit.commands.proxy;

import fi.matiaspaavilainen.masuitecore.bukkit.MaSuiteCore;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BungeeListCommand implements CommandExecutor {

    private MaSuiteHomes plugin;

    public BungeeListCommand(MaSuiteHomes p) {
        plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (!(cs instanceof Player)) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (plugin.in_command.contains(cs)) {
                plugin.formator.sendMessage(cs, plugin.config.load(null, "messages.yml").getString("on-active-command"));
                return;
            }

            plugin.in_command.add(cs);

            Player p = (Player) cs;
            if (args.length == 0) {
                if(MaSuiteCore.bungee){
                    new BukkitPluginChannel(plugin, p, new Object[]{"ListHomeCommand", p.getName()}).send();
                }

            } else if (args.length == 1) {
                if (p.hasPermission("masuitehomes.home.list.other")) {
                    new BukkitPluginChannel(plugin, p, new Object[]{"ListHomeOtherCommand", p.getName(), args[0]}).send();
                } else {
                    plugin.formator.sendMessage(p, plugin.config.load(null, "messages.yml").getString("no-permission"));
                }

            } else {
                plugin.formator.sendMessage(p, plugin.config.load("homes", "syntax.yml").getString("home.list"));
            }

            plugin.in_command.remove(cs);
        });

        return true;
    }
}
