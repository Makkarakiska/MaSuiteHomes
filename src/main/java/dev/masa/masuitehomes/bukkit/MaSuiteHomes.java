package dev.masa.masuitehomes.bukkit;

import dev.masa.masuitecore.acf.PaperCommandManager;
import dev.masa.masuitecore.bukkit.api.MaSuiteCoreServerAPI;
import dev.masa.masuitecore.bukkit.utils.CommandManagerUtil;
import dev.masa.masuitecore.common.config.ConfigLoader;
import dev.masa.masuitecore.common.utils.Updator;
import dev.masa.masuitehomes.bukkit.commands.HomeCommand;
import dev.masa.masuitehomes.bukkit.events.JoinEvent;
import dev.masa.masuitehomes.bukkit.events.LeaveEvent;
import dev.masa.masuitehomes.common.config.server.HomeServerConfig;
import dev.masa.masuitehomes.common.models.Home;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MaSuiteHomes extends JavaPlugin {
    public HashMap<UUID, List<Home>> homes = new HashMap<>();

    public MaSuiteCoreServerAPI api = new MaSuiteCoreServerAPI();

    @Getter
    private HomeServerConfig homeConfig;

    @SneakyThrows
    @Override
    public void onEnable() {
        this.homeConfig = HomeServerConfig.loadFrom(ConfigLoader.loadConfig("homes/config.yml"));
        // Register channels
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new HomeMessageListener(this));

        getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new LeaveEvent(this), this);

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new HomeCommand(this));
        manager.getCommandCompletions().registerCompletion("homes", c -> {
            List<String> homeNames = new ArrayList<>();
            if (homes.containsKey(c.getPlayer().getUniqueId())) {
                for (Home home : homes.get(c.getPlayer().getUniqueId())) {
                    homeNames.add(home.getName());
                }
            }
            return homeNames;
        });

        CommandManagerUtil.registerMaSuitePlayerCommandCompletion(manager);
        CommandManagerUtil.registerCooldownCondition(manager);

        new Updator(getDescription().getVersion(), getDescription().getName(), "60632").checkUpdates();

        api.getCooldownService().addCooldownLength("homes", this.homeConfig.getCooldown());
        api.getWarmupService().warmupTimes.put("homes", this.homeConfig.getWarmup());
    }
}
