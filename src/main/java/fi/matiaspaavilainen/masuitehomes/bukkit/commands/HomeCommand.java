package fi.matiaspaavilainen.masuitehomes.bukkit.commands;

import fi.matiaspaavilainen.masuitecore.acf.BaseCommand;
import fi.matiaspaavilainen.masuitecore.acf.annotation.*;
import fi.matiaspaavilainen.masuitecore.core.adapters.BukkitAdapter;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.entity.Player;

public class HomeCommand extends BaseCommand {

    private MaSuiteHomes plugin;

    public HomeCommand(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    // TODO: Add last location
    @CommandAlias("home")
    @CommandPermission("masuitehomes.home.teleport")
    @Description("Teleports to home")
    @CommandCompletion("@homes @masuite_players *")
    @Conditions("cooldown:type=homes,bypass=masuitehomes.cooldown.override")
    public void teleportHomeCommand(Player player, @Default("home") String home, @Optional @CommandPermission("masuitehomes.home.teleport.other") String searchPlayer) {
        if (searchPlayer == null) {
            new BukkitPluginChannel(plugin, player, "HomeCommand", player.getName(), home).send();
            return;
        }
        new BukkitPluginChannel(plugin, player, "HomeOtherCommand", player.getName(), home, searchPlayer).send();
    }

    @CommandAlias("sethome|createhome|homeset")
    @CommandPermission("masuitehomes.home.set")
    @Description("Sets home point")
    @CommandCompletion("@homes @masuite_players *")
    public void setHomeCommand(Player player, @Default("home") String home, @Optional @CommandPermission("masuitehomes.home.set.other") String searchPlayer) {
        String location = BukkitAdapter.adapt(player.getLocation()).serialize();
        int max = plugin.getMaxHomes(player);

        if (searchPlayer == null) {
            new BukkitPluginChannel(plugin, player, "SetHomeCommand", player.getName(), location, home, max).send();
            return;
        }
        new BukkitPluginChannel(plugin, player, "SetHomeOtherCommand", player.getName(), home, location, searchPlayer, -1).send();
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
}
