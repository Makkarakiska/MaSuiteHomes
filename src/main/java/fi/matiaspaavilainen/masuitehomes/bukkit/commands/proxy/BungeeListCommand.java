package fi.matiaspaavilainen.masuitehomes.bukkit.commands.proxy;

import fi.matiaspaavilainen.masuitecore.bukkit.MaSuiteCore;
import fi.matiaspaavilainen.masuitecore.bukkit.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BungeeListCommand implements CommandExecutor {

    private MaSuiteHomes plugin;
    private Formator formator = new Formator();
    private BukkitConfiguration config = new BukkitConfiguration();

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
                formator.sendMessage(cs, config.load(null, "messages.yml").getString("on-active-command"));
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
                    new BukkitPluginChannel(plugin, p, new Object[]{"ListHomeCommand", p.getName(), args[0]}).send();
                } else {
                    formator.sendMessage(p, config.load(null, "messages.yml").getString("no-permission"));
                }

            } else {
                formator.sendMessage(p, config.load("homes", "syntax.yml").getString("home.list"));
            }

            plugin.in_command.remove(cs);
        });

        return true;
    }
}
