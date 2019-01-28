package fi.matiaspaavilainen.masuitehomes.bukkit;

import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import fi.matiaspaavilainen.masuitehomes.core.Home;
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
                if (p != null) {
                    p.teleport(new org.bukkit.Location(Bukkit.getWorld(in.readUTF()), in.readDouble(), in.readDouble(), in.readDouble(), in.readFloat(), in.readFloat()));
                }
            }
            if (subchannel.equals("HomeCooldown")) {
                Player p = Bukkit.getPlayer(UUID.fromString(in.readUTF()));
                if (p != null) {
                    plugin.cooldowns.put(p.getUniqueId(), in.readLong());
                }
            }
            if (subchannel.equals("AddHome")) {
                Player p = Bukkit.getPlayer(UUID.fromString(in.readUTF()));
                if (p != null) {
                    if (!plugin.homes.containsKey(p.getUniqueId())) {
                        plugin.homes.put(p.getUniqueId(), new ArrayList<>());
                    }
                    String[] info = in.readUTF().split(":");
                    Home home = new Home(info[0], info[1], p.getUniqueId(),
                            new Location(info[2], Double.parseDouble(info[3]), Double.parseDouble(info[4]), Double.parseDouble(info[5])));
                    plugin.homes.get(p.getUniqueId()).add(home);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
