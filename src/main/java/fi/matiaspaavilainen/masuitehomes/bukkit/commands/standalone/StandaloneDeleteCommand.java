package fi.matiaspaavilainen.masuitehomes.bukkit.commands.standalone;

import fi.matiaspaavilainen.masuitecore.bukkit.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitecore.core.objects.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import fi.matiaspaavilainen.masuitehomes.core.Home;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StandaloneDeleteCommand implements CommandExecutor {

    private MaSuiteHomes plugin;
    private Formator formator = new Formator();
    private BukkitConfiguration config = new BukkitConfiguration();

    public StandaloneDeleteCommand(MaSuiteHomes p) {
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
            switch (args.length) {
                case (0):
                    delete("home", p, p.getUniqueId());
                    break;
                case (1):
                    delete(args[0], p, p.getUniqueId());
                    break;
                case (2):
                    if (p.hasPermission("masuitehomes.home.delete.other")) {
                        MaSuitePlayer msp = new MaSuitePlayer().find(args[0]);
                        if (msp.getUniqueId() != null) {
                            delete(args[0], p, msp.getUniqueId());
                        } else {
                            formator.sendMessage(p, config.load("homes", "messages.yml").getString("player-not-found"));
                        }
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

    private void delete(String name, Player p, UUID uuid) {
        Home home = new Home().findExact(name, uuid);
        if (home != null) {
            if (home.delete()) {
                formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.deleted").replace("%home%", home.getName()));
            } else {
                System.out.println("[MaSuite] [Homes] There was an error during removing home.");
            }
        } else {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-not-found"));
        }
    }
}
