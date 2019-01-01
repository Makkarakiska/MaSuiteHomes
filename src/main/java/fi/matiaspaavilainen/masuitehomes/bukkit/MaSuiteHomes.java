package fi.matiaspaavilainen.masuitehomes.bukkit;

import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.DeleteCommand;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.ListCommand;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.SetCommand;
import fi.matiaspaavilainen.masuitehomes.bukkit.commands.TeleportCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MaSuiteHomes extends JavaPlugin {
    public static HashMap<UUID, Long> cooldowns = new HashMap<>();
    public final java.util.List<CommandSender> in_command = new ArrayList<>();

    private BukkitConfiguration config = new BukkitConfiguration();

    @Override
    public void onEnable() {

        // Create configs
        config.create(this, "homes", "config.yml");
        config.create(this, "homes", "messages.yml");
        config.create(this, "homes", "syntax.yml");

        // Register channels
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new HomeMessageListener());

        // Register commands
        getCommand("sethome").setExecutor(new SetCommand(this));
        getCommand("delhome").setExecutor(new DeleteCommand(this));
        getCommand("home").setExecutor(new TeleportCommand(this));
        getCommand("homes").setExecutor(new ListCommand(this));
    }
}
