package fi.matiaspaavilainen.masuitehomes.bukkit.commands.proxy;

import fi.matiaspaavilainen.masuitecore.bukkit.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.PluginChannel;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BungeeDeleteCommand implements CommandExecutor {

    private MaSuiteHomes plugin;
    private Formator formator = new Formator();
    private BukkitConfiguration config = new BukkitConfiguration();

    public BungeeDeleteCommand(MaSuiteHomes p) {
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
                    new PluginChannel(plugin, p, new Object[]{"DelHomeCommand", p.getName(), "home"}).send();
                    break;
                case (1):
                    new PluginChannel(plugin, p, new Object[]{"DelHomeCommand", p.getName(), args[0]}).send();
                    break;
                case (2):
                    if (p.hasPermission("masuitehomes.home.delete.other")) {
                        new PluginChannel(plugin, p, new Object[]{"DelHomeOtherCommand", p.getName(), args[0], args[1]}).send();
                    } else {
                        formator.sendMessage(p, config.load(null, "messages.yml").getString("no-permission"));
                    }
                    break;
                default:
                    formator.sendMessage(p, config.load("homes", "syntax.yml").getString("home.delete"));
                    break;
            }
            plugin.in_command.remove(cs);
        });

        return true;
    }
}
