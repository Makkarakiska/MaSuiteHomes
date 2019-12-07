package fi.matiaspaavilainen.masuitehomes.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HomeCommand extends BaseCommand {

    private MaSuiteHomes plugin;

    public HomeCommand(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    @CommandAlias("home")
    @CommandPermission("masuitehomes.home.teleport")
    @Description("Teleports to home")
    public void teleportHomeCommand(Player player, String[] args) {
        if (args.length == 0) {
            new BukkitPluginChannel(plugin, player, new Object[]{"HomeCommand", player.getName(), "home"}).send();
            return;
        }

        if (args.length == 1) {
            new BukkitPluginChannel(plugin, player, new Object[]{"HomeCommand", player.getName(), args[0]}).send();
            return;
        }

        if (args.length == 2) {
            new BukkitPluginChannel(plugin, player, new Object[]{"HomeOtherCommand", player.getName(), args[0], args[1]}).send();
            return;
        }

        plugin.formator.sendMessage(player, plugin.config.load("homes", "syntax.yml").getString("home.teleport"));
    }

    @CommandAlias("sethome|createhome|homeset")
    @CommandPermission("masuitehomes.home.set")
    @Description("Sets home point")
    public void setHomeCommand(Player player, String[] args) {
        Location loc = player.getLocation();
        int max = plugin.getMaxHomes(player);

        String location = loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
        if (args.length == 0) {
            new BukkitPluginChannel(plugin, player, new Object[]{"SetHomeCommand", player.getName(), location, "home", max}).send();
            return;
        }

        if (args.length == 1) {
            new BukkitPluginChannel(plugin, player, new Object[]{"SetHomeCommand", player.getName(), location, args[0], max}).send();
            return;
        }

        if (args.length == 2) {
            if (player.hasPermission("masuitehomes.home.set.other")) {
                new BukkitPluginChannel(plugin, player, new Object[]{"SetHomeOtherCommand", player.getName(), args[0], location, args[1], -1}).send();
                return;
            }
            plugin.formator.sendMessage(player, plugin.config.load(null, "messages.yml").getString("no-permission"));
            return;
        }

        plugin.formator.sendMessage(player, plugin.config.load("homes", "syntax.yml").getString("home.set"));
    }

    @CommandAlias("delhome|deletehome|homedel")
    @CommandPermission("masuitehomes.home.delete")
    @Description("Deletes home point")
    public void delHomeCommand(Player player, String[] args) {
        if (args.length == 0) {
            new BukkitPluginChannel(plugin, player, new Object[]{"DelHomeCommand", player.getName(), "home"}).send();
            return;
        }

        if (args.length == 1) {
            new BukkitPluginChannel(plugin, player, new Object[]{"DelHomeCommand", player.getName(), args[0]}).send();
            return;
        }

        if (args.length == 2) {
            if (player.hasPermission("masuitehomes.home.delete.other")) {
                new BukkitPluginChannel(plugin, player, new Object[]{"DelHomeOtherCommand", player.getName(), args[0], args[1]}).send();
                return;
            }
            plugin.formator.sendMessage(player, plugin.config.load(null, "messages.yml").getString("no-permission"));
            return;
        }

        plugin.formator.sendMessage(player, plugin.config.load("homes", "syntax.yml").getString("home.delete"));
    }

    @CommandAlias("homes|listhomes|homelist")
    @CommandPermission("masuitehomes.home.list")
    @Description("List homes")
    public void listHomeCommand(Player player, String[] args) {
        if (args.length == 0) {
            new BukkitPluginChannel(plugin, player, new Object[]{"ListHomeCommand", player.getName()}).send();
            return;
        }

        if (args.length == 1) {
            if (player.hasPermission("masuitehomes.home.list.other")) {
                new BukkitPluginChannel(plugin, player, new Object[]{"ListHomeOtherCommand", player.getName(), args[0]}).send();
                return;
            }
            plugin.formator.sendMessage(player, plugin.config.load(null, "messages.yml").getString("no-permission"));
            return;
        }
        plugin.formator.sendMessage(player, plugin.config.load("homes", "syntax.yml").getString("home.list"));
    }
}
