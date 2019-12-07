package fi.matiaspaavilainen.masuitehomes.bukkit;

import co.aikar.commands.PaperCommandManager;
import fi.matiaspaavilainen.masuitecore.bukkit.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.Updator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.HomeCommand;
import fi.matiaspaavilainen.masuitehomes.bukkit.events.JoinEvent;
import fi.matiaspaavilainen.masuitehomes.bukkit.events.LeaveEvent;
import fi.matiaspaavilainen.masuitehomes.core.models.Home;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MaSuiteHomes extends JavaPlugin {
    public HashMap<UUID, Long> cooldowns = new HashMap<>();
    public HashMap<UUID, List<Home>> homes = new HashMap<>();

    public BukkitConfiguration config = new BukkitConfiguration();

    public Formator formator = new Formator();

    @Override
    public void onEnable() {

        // Create configs
        config.create(this, "homes", "config.yml");
        config.create(this, "homes", "syntax.yml");
        config.create(this, "homes", "gui.yml");

        setupBungee();
        registerListeners();

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new HomeCommand(this));
        manager.getCommandCompletions().registerAsyncCompletion("homes", c -> {
            List<String> homeNames = new ArrayList<>();
            for (Home home : homes.get(c.getPlayer().getUniqueId())) {
                homeNames.add(home.getName());
            }
            return homeNames;
        });

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
    }

    private void registerListeners() {
        // Events
        getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new LeaveEvent(this), this);
    }
}
