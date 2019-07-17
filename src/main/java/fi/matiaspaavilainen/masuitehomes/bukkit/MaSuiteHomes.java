package fi.matiaspaavilainen.masuitehomes.bukkit;

import fi.matiaspaavilainen.masuitecore.bukkit.MaSuiteCore;
import fi.matiaspaavilainen.masuitecore.core.Updator;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitecore.core.database.ConnectionManager;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.GUICommand;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.HomeTabCompleter;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.proxy.BungeeDeleteCommand;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.proxy.BungeeListCommand;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.proxy.BungeeSetCommand;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.proxy.BungeeTeleportCommand;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.standalone.StandaloneDeleteCommand;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.standalone.StandaloneListCommand;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.standalone.StandaloneSetCommand;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.standalone.StandaloneTeleportCommand;
import fi.matiaspaavilainen.masuitehomes.bukkit.events.JoinEvent;
import fi.matiaspaavilainen.masuitehomes.bukkit.events.LeaveEvent;
import fi.matiaspaavilainen.masuitehomes.core.Home;
import me.lucko.helper.Commands;
import me.lucko.helper.internal.HelperImplementationPlugin;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@HelperImplementationPlugin
public class MaSuiteHomes extends ExtendedJavaPlugin {
    public HashMap<UUID, Long> cooldowns = new HashMap<>();
    public HashMap<UUID, List<Home>> homes = new HashMap<>();
    public final java.util.List<CommandSender> in_command = new ArrayList<>();

    private BukkitConfiguration config = new BukkitConfiguration();

    @Override
    public void enable() {

        // Create configs
        config.create(this, "homes", "config.yml");
        config.create(this, "homes", "syntax.yml");
        config.create(this, "homes", "gui.yml");

        loadDefaults();
        if (MaSuiteCore.bungee) {
            setupBungee();
        } else {
            setupNoBungee();
        }
        registerListeners();


        new Updator(new String[]{getDescription().getVersion(), getDescription().getName(), "60632"}).checkUpdates();

        Commands.create()
                .assertPermission("masuitehomes.home.teleport")
                .assertPlayer()
                .assertUsage("<home> [player]").handler(c -> {
                    System.out.println(c.arg(0).parse(String.class) + " " + c.sender().getUniqueId());
                    new BukkitPluginChannel(this, c.sender(), new Object[]{"HomeCommand", c.sender().getUniqueId(), c.arg(0).parse(String.class)}).send();
                }
        ).register("home");
    }

    public int getMaxHomes(Player p) {
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
        return max;
    }

    private void setupBungee() {
        config.create(this, "homes", "messages.yml");
        // Register channels
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new HomeMessageListener(this));

        // Register commands
        getCommand("sethome").setExecutor(new BungeeSetCommand(this));
        getCommand("delhome").setExecutor(new BungeeDeleteCommand(this));
        getCommand("home").setExecutor(new BungeeTeleportCommand(this));

        if (config.load("homes", "config.yml").getBoolean("use-gui")) {
            getCommand("homes").setExecutor(new GUICommand(this));
        } else {
            getCommand("homes").setExecutor(new BungeeListCommand(this));
        }
    }

    private void setupNoBungee() {
        config.copyFromBungee(this, "homes", "messages.yml");
        config.addDefault("homes/messages.yml", "in-cooldown", "&cYou can go to home after %time% seconds");
        config.addDefault("homes/messages.yml", "homes.title-others", "&9%player%''s &7homes: ");
        config.addDefault("homes/messages.yml", "homes.server-name", "&9%server%&7: ");

        ConnectionManager.db.createTable("homes", "(id INT(10) unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100) NOT NULL, owner VARCHAR(36) NOT NULL, server VARCHAR(100) NOT NULL, world VARCHAR(100) NOT NULL, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        // Register commands
        getCommand("sethome").setExecutor(new StandaloneSetCommand(this));
        getCommand("delhome").setExecutor(new StandaloneDeleteCommand(this));
        getCommand("home").setExecutor(new StandaloneTeleportCommand(this));
        if (config.load("homes", "config.yml").getBoolean("use-gui")) {
            getCommand("homes").setExecutor(new GUICommand(this));
        } else {
            getCommand("homes").setExecutor(new StandaloneListCommand(this));
        }

    }

    private void registerListeners() {
        // Tab completions
        getCommand("sethome").setTabCompleter(new HomeTabCompleter(this));
        getCommand("delhome").setTabCompleter(new HomeTabCompleter(this));
        //getCommand("home").setTabCompleter(new HomeTabCompleter(this));

        // Events
        getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new LeaveEvent(this), this);
    }

    private void loadDefaults() {
        config.addDefault("homes/config.yml", "use-gui", false);
    }
}
