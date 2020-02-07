package dev.masa.masuitehomes.bukkit.commands;

import dev.masa.masuitecore.acf.BaseCommand;
import dev.masa.masuitecore.acf.annotation.*;
import dev.masa.masuitecore.core.adapters.BukkitAdapter;
import dev.masa.masuitecore.core.channels.BukkitPluginChannel;
import dev.masa.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class HomeCommand extends BaseCommand {

    private MaSuiteHomes plugin;

    public HomeCommand(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    @CommandAlias("home")
    @CommandPermission("masuitehomes.home.teleport")
    @Description("Teleports to home")
    @CommandCompletion("@homes @masuite_players *")
    @Conditions("cooldown:type=homes,bypass=masuitehomes.cooldown.override")
    public void teleportHomeCommand(Player player, @Default("home") String home, @Optional @CommandPermission("masuitehomes.home.teleport.other") String searchPlayer) {
        if (searchPlayer != null) {
            new BukkitPluginChannel(plugin, player, "HomeOtherCommand", player.getName(), home, searchPlayer).send();
            return;
        }

        plugin.api.getWarmupService().applyWarmup(player, "masuitehomes.warmup.override", "homes", success -> {
            if (success) {
                new BukkitPluginChannel(plugin, player, "HomeCommand", player.getName(), home).send();
            }
        });

    }

    @CommandAlias("sethome|createhome|homeset")
    @CommandPermission("masuitehomes.home.set")
    @Description("Sets home point")
    @CommandCompletion("@homes @masuite_players *")
    public void setHomeCommand(Player player, @Default("home") String home, @Optional @CommandPermission("masuitehomes.home.set.other") String searchPlayer) {
        String location = BukkitAdapter.adapt(player.getLocation()).serialize();

        if (searchPlayer == null) {
            new BukkitPluginChannel(plugin, player, "SetHomeCommand", player.getName(), location, home,
                    this.getMaxHomes(player, "global"),
                    this.getMaxHomes(player, "server")).send();
            return;
        }
        new BukkitPluginChannel(plugin, player, "SetHomeOtherCommand", player.getName(), home, location, searchPlayer, -1, -1).send();
    }

    @CommandAlias("delhome|deletehome|homedel")
    @CommandPermission("masuitehomes.home.delete")
    @Description("Deletes home point")
    @CommandCompletion("@homes @masuite_players *")
    public void delHomeCommand(Player player, @Default("home") String home, @Optional @CommandPermission("masuitehomes.home.delete.other") String searchPlayer) {
        if (searchPlayer == null) {
            new BukkitPluginChannel(plugin, player, "DelHomeCommand", player.getName(), home).send();
            return;
        }
        new BukkitPluginChannel(plugin, player, "DelHomeOtherCommand", player.getName(), home, searchPlayer).send();
    }

    @CommandAlias("homes|listhomes|homelist")
    @CommandPermission("masuitehomes.home.list")
    @Description("List homes")
    @CommandCompletion("@masuite_players *")
    public void listHomeCommand(Player player, @Optional @CommandPermission("masuitehomes.home.list.other") String searchPlayer) {
        if (searchPlayer == null) {
            new BukkitPluginChannel(plugin, player, "ListHomeCommand", player.getName()).send();
            return;
        }
        new BukkitPluginChannel(plugin, player, "ListHomeOtherCommand", player.getName(), searchPlayer).send();
    }


    private int getMaxHomes(Player player, String type) {
        int max = 0;
        for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
            String perm = permInfo.getPermission();
            if (perm.startsWith("masuitehomes.home.limit." + type)) {
                String amount = perm.replace("masuitehomes.home.limit." + type + ".", "");
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
        return max;
    }
}
