package fi.matiaspaavilainen.masuitehomes.bukkit;

import fi.matiaspaavilainen.masuitecore.bukkit.MaSuiteCore;
import fi.matiaspaavilainen.masuitecore.core.Updator;
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
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MaSuiteHomes extends JavaPlugin {
    public HashMap<UUID, Long> cooldowns = new HashMap<>();
    public HashMap<UUID, List<String>> homes = new HashMap<>();
    public final java.util.List<CommandSender> in_command = new ArrayList<>();

    private BukkitConfiguration config = new BukkitConfiguration();

    @Override
    public void onEnable() {

        // Create configs
        config.create(this, "homes", "config.yml");
        config.create(this, "homes", "syntax.yml");
        loadDefaults();
        if (MaSuiteCore.bungee) {
            setupBungee();
        } else {
            setupNoBungee();
        }
        registerListeners();


        new Updator(new String[]{getDescription().getVersion(), getDescription().getName(), "60632"}).checkUpdates();
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
        getCommand("home").setTabCompleter(new HomeTabCompleter(this));

        // Events
        getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new LeaveEvent(this), this);
    }

    private void loadDefaults() {
        config.addDefault("homes/messages.yml", "gui.title", "Homes");
        config.addDefault("homes/messages.yml", "gui.name", "&5%home%");
        config.addDefault("homes/messages.yml", "gui.item", "EMERALD");
        config.addDefault("homes/messages.yml", "gui.title", "&5Homes");

        List<String> description = new ArrayList<>();
        description.add("&dClick to teleport!");
        config.addDefault("homes/messages.yml", "gui.description", description);

        // Borders
        List<String> placeholders = new ArrayList<>();
        placeholders.add("BLUE_STAINED_GLASS_PANE");
        placeholders.add("GRAY_STAINED_GLASS_PANE");
        config.addDefault("homes/messages.yml", "gui.placeholders", placeholders);

        // Previous button
        config.addDefault("homes/messages.yml", "gui.controls.previous.title", "&5Previous");
        config.addDefault("homes/messages.yml", "gui.controls.previous.item", "PAPER");

        List<String> prevDesc = new ArrayList<>();
        prevDesc.add("&dGo to the previous page!");
        config.addDefault("homes/messages.yml", "gui.controls.previous.description", prevDesc);

        // Info/center button
        config.addDefault("homes/messages.yml", "gui.controls.info.title", "&5Home info");
        config.addDefault("homes/messages.yml", "gui.controls.info.unlimited", "unlimited");
        config.addDefault("homes/messages.yml", "gui.controls.info.item", "COMPASS");

        List<String> infoDesc = new ArrayList<>();
        infoDesc.add("&dHomes used: %used%");
        infoDesc.add("&dHomes available: %total%");
        config.addDefault("homes/messages.yml", "gui.controls.info.description", infoDesc);

        // Next button
        config.addDefault("homes/messages.yml", "gui.controls.next.title", "&5Next");
        config.addDefault("homes/messages.yml", "gui.controls.next.item", "PAPER");

        List<String> nextDesc = new ArrayList<>();
        nextDesc.add("&dGo to the next page!");
        config.addDefault("homes/messages.yml", "gui.controls.next.description", nextDesc);

        config.addDefault("homes/config.yml", "use-gui", true);
    }
}
