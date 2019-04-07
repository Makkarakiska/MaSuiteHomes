package fi.matiaspaavilainen.masuitehomes.bukkit.commands.standalone;

import fi.matiaspaavilainen.masuitecore.core.objects.MaSuitePlayer;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import fi.matiaspaavilainen.masuitehomes.core.Home;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StandaloneListCommand implements CommandExecutor {
    private MaSuiteHomes plugin;

    public StandaloneListCommand(MaSuiteHomes p) {
        plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (!(cs instanceof Player)) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (plugin.in_command.contains(cs)) {
                plugin.formator.sendMessage(cs, plugin.config.load(null, "messages.yml").getString("on-active-command"));
                return;
            }

            plugin.in_command.add(cs);

            Player p = (Player) cs;
            if (args.length == 0) {
                Home h = new Home();
                TextComponent homes = new TextComponent(plugin.formator.colorize(plugin.config.load("homes", "messages.yml").getString("homes.title")));

                int i = 0;
                String split = plugin.formator.colorize(plugin.config.load("homes", "messages.yml").getString("homes.split"));
                for (Home home : h.getHomes(p.getUniqueId())) {
                    if (i++ == h.getHomes(p.getUniqueId()).size() - 1) {
                        list(homes, home, p.getName(), p.getName());
                    } else {
                        list(homes, home, p.getName(), p.getName());
                        homes.addExtra(split);
                    }
                }
                p.spigot().sendMessage(homes);
            } else if (args.length == 1) {
                if (p.hasPermission("masuitehomes.home.list.other")) {
                    MaSuitePlayer msp = new MaSuitePlayer().find(args[0]);
                    if (msp.getUniqueId() == null) {
                        plugin.formator.sendMessage(p, plugin.config.load("homes", "messages.yml").getString("player-not-found"));
                        return;
                    }
                    Home h = new Home();
                    TextComponent homes = new TextComponent(plugin.formator.colorize(plugin.config.load("homes", "messages.yml")
                            .getString("homes.title-others").replace("%player%", msp.getUsername())));

                    int i = 0;
                    String split = plugin.formator.colorize(plugin.config.load("homes", "messages.yml").getString("homes.split"));
                    for (Home home : h.getHomes(msp.getUniqueId())) {
                        if (i++ == h.getHomes(msp.getUniqueId()).size() - 1) {
                            list(homes, home, p.getName(), msp.getUsername());
                        } else {
                            list(homes, home, p.getName(), msp.getUsername());
                            homes.addExtra(split);
                        }
                    }
                } else {
                    plugin.formator.sendMessage(p, plugin.config.load(null, "messages.yml").getString("no-permission"));
                }

            } else {
                plugin.formator.sendMessage(p, plugin.config.load("homes", "syntax.yml").getString("home.list"));
            }

            plugin.in_command.remove(cs);
        });

        return true;
    }

    private void list(TextComponent homes, Home home, String requester, String owner) {
        TextComponent hc = new TextComponent(plugin.formator.colorize(plugin.config.load("homes", "messages.yml").getString("homes.name").replace("%home%", home.getName())));
        if (requester.equalsIgnoreCase(owner)) {
            hc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home.getName()));
        } else {
            hc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + owner + " " + home.getName()));
        }
        hc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(plugin.formator.colorize(plugin.config.load("homes", "messages.yml").getString("home-hover-text").replace("%home%", home.getName()))).create()));
        homes.addExtra(hc);
    }
}
