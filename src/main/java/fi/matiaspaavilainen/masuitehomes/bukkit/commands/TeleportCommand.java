package fi.matiaspaavilainen.masuitehomes.bukkit.commands;

import fi.matiaspaavilainen.masuitecore.bukkit.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportCommand implements CommandExecutor {
    private MaSuiteHomes plugin;
    private Formator formator = new Formator();
    private BukkitConfiguration config = new BukkitConfiguration();

    public TeleportCommand(MaSuiteHomes p) {
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

            try (ByteArrayOutputStream b = new ByteArrayOutputStream();
                 DataOutputStream out = new DataOutputStream(b)) {
                switch (args.length) {
                    case (0):
                        if (checkCooldown(p)) {
                            sendLastLoc(p);
                            out.writeUTF("HomeCommand");
                            out.writeUTF(p.getName());
                            out.writeUTF("home");
                            p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
                        }
                        break;
                    case (1):
                        if (checkCooldown(p)) {
                            sendLastLoc(p);
                            out.writeUTF("HomeCommand");
                            out.writeUTF(p.getName());
                            out.writeUTF(args[0]);
                            p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
                        }
                        break;
                    case (2):
                        if (p.hasPermission("masuitehomes.home.teleport.other")) {
                            sendLastLoc(p);
                            out.writeUTF("HomeOtherCommand");
                            out.writeUTF(p.getName());
                            out.writeUTF(args[0]);
                            out.writeUTF(args[1]);
                            p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
                        } else {
                            formator.sendMessage(p, config.load(null, "messages.yml").getString("no-permission"));
                        }
                        break;
                    default:
                        formator.sendMessage(p, config.load("homes", "syntax.yml").getString("home.teleport"));
                        break;
                }

                plugin.in_command.remove(cs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return true;
    }

    private Boolean checkCooldown(Player p) {
        if (config.load("homes", "config.yml").getInt("cooldown") > 0) {
            if (MaSuiteHomes.cooldowns.containsKey(p.getUniqueId())) {
                if (System.currentTimeMillis() - MaSuiteHomes.cooldowns.get(p.getUniqueId()) > config.load("homes", "config.yml").getInt("cooldown") * 1000) {
                    MaSuiteHomes.cooldowns.remove(p.getUniqueId());
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
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("MaSuiteTeleports");
            out.writeUTF("GetLocation");
            out.writeUTF(p.getName());
            Location loc = p.getLocation();
            out.writeUTF(loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":"
                    + loc.getYaw() + ":" + loc.getPitch());
            out.writeUTF("DETECTSERVER");
            p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
