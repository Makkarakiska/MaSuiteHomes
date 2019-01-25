package fi.matiaspaavilainen.masuitehomes.bukkit.events;

import fi.matiaspaavilainen.masuitecore.bukkit.MaSuiteCore;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import fi.matiaspaavilainen.masuitehomes.core.Home;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JoinEvent implements Listener {

    private MaSuiteHomes plugin;

    public JoinEvent(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (MaSuiteCore.bungee) {
            try (ByteArrayOutputStream b = new ByteArrayOutputStream();
                 DataOutputStream out = new DataOutputStream(b)) {
                out.writeUTF("ListHomes");
                out.writeUTF(e.getPlayer().getName());
                plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> e.getPlayer().sendPluginMessage(plugin, "BungeeCord", b.toByteArray()), 20);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                List<String> homes = new ArrayList<>();
                for (Home home : new Home().getHomes(e.getPlayer().getUniqueId())) {
                    homes.add(home.getName());
                }
                plugin.homes.put(e.getPlayer().getUniqueId(), homes);
            }, 20);
        }
    }
}
