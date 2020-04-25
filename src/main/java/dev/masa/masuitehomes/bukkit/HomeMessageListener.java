package dev.masa.masuitehomes.bukkit;

import dev.masa.masuitecore.core.adapters.BukkitAdapter;
import dev.masa.masuitecore.core.objects.Location;
import dev.masa.masuitehomes.core.models.Home;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class HomeMessageListener implements PluginMessageListener {

    private MaSuiteHomes plugin;

    public HomeMessageListener(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

        String subchannel = null;
        try {
            subchannel = in.readUTF();
            if (subchannel.equals("HomePlayer")) {
                Player p = Bukkit.getPlayer(UUID.fromString(in.readUTF()));

                if (p == null) return;

                Location loc = new Location().deserialize(in.readUTF());

                org.bukkit.Location bukkitLocation = BukkitAdapter.adapt(loc);
                if (bukkitLocation.getWorld() == null) {
                    System.out.println("[MaSuite] [Homes] [World=" + loc.getWorld() + "] World could not be found!");
                    return;
                }

                p.teleport(bukkitLocation);
            }
            if (subchannel.equals("AddHome")) {
                Player p = Bukkit.getPlayer(UUID.fromString(in.readUTF()));
                if (p != null) {
                    if (!plugin.homes.containsKey(p.getUniqueId())) {
                        plugin.homes.put(p.getUniqueId(), new ArrayList<>());
                    }
                    Home home = new Home().deserialize(in.readUTF());
                    plugin.homes.get(p.getUniqueId()).add(home);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
