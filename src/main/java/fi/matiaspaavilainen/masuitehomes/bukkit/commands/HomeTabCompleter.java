package fi.matiaspaavilainen.masuitehomes.bukkit.commands;

import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeTabCompleter implements TabCompleter {
    private MaSuiteHomes plugin;

    public HomeTabCompleter(MaSuiteHomes p) {
        plugin = p;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }
        if (cmd.getName().equalsIgnoreCase("home") || cmd.getName().equalsIgnoreCase("sethome") || cmd.getName().equalsIgnoreCase("delhome")) {
            Player player = (Player) sender;
            if (plugin.homes.get(player.getUniqueId()) == null) {
                return new ArrayList<>();
            }
            List<String> homes = new ArrayList<>(plugin.homes.get(player.getUniqueId()));
            if (args.length == 1) {
                return StringUtil.copyPartialMatches(args[0], new ArrayList<>(homes), new ArrayList<>());
            }
            return new ArrayList<>(homes);
        }
        return null;
    }
}
