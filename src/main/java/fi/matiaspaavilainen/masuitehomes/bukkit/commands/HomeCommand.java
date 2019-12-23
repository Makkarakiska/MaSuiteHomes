package fi.matiaspaavilainen.masuitehomes.bukkit.commands;

import fi.matiaspaavilainen.masuitecore.acf.BaseCommand;
import fi.matiaspaavilainen.masuitecore.acf.annotation.*;
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
    @CommandCompletion("@homes *")
    public void teleportHomeCommand(Player player, @Default("home") String home, @Optional @CommandPermission("masuitehomes.home.teleport.other") String searchPlayer) {
        if (searchPlayer == null) {
            new BukkitPluginChannel(plugin, player, new Object[]{"HomeCommand", player.getName(), home}).send();
            return;
        }
        new BukkitPluginChannel(plugin, player, new Object[]{"HomeOtherCommand", player.getName(), home, searchPlayer}).send();
    }

    @CommandAlias("sethome|createhome|homeset")
    @CommandPermission("masuitehomes.home.set")
    @Description("Sets home point")
    @CommandCompletion("@homes *")
    public void setHomeCommand(Player player, @Default("home") String home, @Optional @CommandPermission("masuitehomes.home.set.other") String searchPlayer) {
        Location loc = player.getLocation();
        int max = plugin.getMaxHomes(player);

        String location = loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();

        if (searchPlayer == null) {
            new BukkitPluginChannel(plugin, player, new Object[]{"SetHomeCommand", player.getName(), location, home, max}).send();
            return;
        }
        new BukkitPluginChannel(plugin, player, new Object[]{"SetHomeOtherCommand", player.getName(), home, location, searchPlayer, -1}).send();
    }

    @CommandAlias("delhome|deletehome|homedel")
    @CommandPermission("masuitehomes.home.delete")
    @Description("Deletes home point")
    @CommandCompletion("@homes *")
    public void delHomeCommand(Player player, @Default("home") String home, @Optional @CommandPermission("masuitehomes.home.delete.other") String searchPlayer) {
        if (searchPlayer == null) {
            new BukkitPluginChannel(plugin, player, new Object[]{"DelHomeCommand", player.getName(), home}).send();
            return;
        }
        new BukkitPluginChannel(plugin, player, new Object[]{"DelHomeOtherCommand", player.getName(), home, searchPlayer}).send();
    }

    @CommandAlias("homes|listhomes|homelist")
    @CommandPermission("masuitehomes.home.list")
    @Description("List homes")
    public void listHomeCommand(Player player, @Optional @CommandPermission("masuitehomes.home.list.other") String searchPlayer) {
        if (searchPlayer == null) {
            new BukkitPluginChannel(plugin, player, new Object[]{"ListHomeCommand", player.getName()}).send();
            return;
        }
        new BukkitPluginChannel(plugin, player, new Object[]{"ListHomeOtherCommand", player.getName(), searchPlayer}).send();
    }
}
