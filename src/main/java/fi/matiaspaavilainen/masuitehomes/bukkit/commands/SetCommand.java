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
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SetCommand implements CommandExecutor {

    private MaSuiteHomes plugin;
    private Formator formator = new Formator();
    private BukkitConfiguration config = new BukkitConfiguration();

    public SetCommand(MaSuiteHomes p) {
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
            try (ByteArrayOutputStream b = new ByteArrayOutputStream();
                 DataOutputStream out = new DataOutputStream(b)) {
                switch (args.length) {
                    case (0):
                        out.writeUTF("SetHomeCommand");
                        out.writeUTF(p.getName());
                        out.writeUTF(loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch());
                        out.writeUTF("home");
                        out.writeInt(max);
                        p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
                        break;
                    case (1):
                        out.writeUTF("SetHomeCommand");
                        out.writeUTF(p.getName());
                        out.writeUTF(loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch());
                        out.writeUTF(args[0]);
                        out.writeInt(max);
                        p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
                        break;
                    case (2):
                        if (p.hasPermission("masuitehomes.home.set.other")) {
                            out.writeUTF("SetHomeOtherCommand");
                            out.writeUTF(p.getName());
                            out.writeUTF(args[0]);
                            out.writeUTF(loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch());
                            out.writeUTF(args[1]);
                            out.writeInt(-1);
                            p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
                        } else {
                            formator.sendMessage(p, config.load(null, "messages.yml").getString("no-permission"));
                        }
                        break;
                    default:
                        formator.sendMessage(p, config.load("homes", "syntax.yml").getString("home.set"));
                        break;
                }

                plugin.in_command.remove(cs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return true;
    }
}