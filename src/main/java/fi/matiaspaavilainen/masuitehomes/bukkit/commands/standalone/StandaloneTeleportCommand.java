package fi.matiaspaavilainen.masuitehomes.bukkit.commands.standalone;

import fi.matiaspaavilainen.masuitecore.bukkit.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.adapters.BukkitAdapter;
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

public class StandaloneTeleportCommand implements CommandExecutor {

    private MaSuiteHomes plugin;
    private Formator formator = new Formator();
    private BukkitConfiguration config = new BukkitConfiguration();

    public StandaloneTeleportCommand(MaSuiteHomes p) {
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
                            teleport("home", p, p.getUniqueId());
                        }
                        break;
                    case (1):
                        if (checkCooldown(p)) {
                            teleport(args[0], p, p.getUniqueId());
                        }
                        break;
                    case (2):
                        if (p.hasPermission("masuitehomes.home.teleport.other")) {
                            MaSuitePlayer msp = new MaSuitePlayer().find(args[0]);
                            if (msp.getUniqueId() != null) {
                                teleport(args[1], p, msp.getUniqueId());
                            } else {
                                formator.sendMessage(p, config.load("homes", "messages.yml").getString("player-not-found"));
                            }
                        } else {
                            formator.sendMessage(p, config.load(null, "messages.yml").getString("no-permission"));
                        }
                        break;
                    default:
                        formator.sendMessage(p, config.load("homes", "syntax.yml").getString("home.teleport"));
                        break;
                }
        });
        plugin.in_command.remove(cs);
        return true;
    }

    private void teleport(String name, Player p, UUID uniqueId) {
        Home home = new Home().findLike(name, uniqueId);
        if (home != null) {
            p.teleport(BukkitAdapter.adapt(home.getLocation()));
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.teleported").replace("%home%", home.getName()));
        } else {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-not-found"));
        }
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
}
