package fi.matiaspaavilainen.masuitehomes.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                    p.teleport(new Location(Bukkit.getWorld(in.readUTF()), in.readDouble(), in.readDouble(), in.readDouble(), in.readFloat(), in.readFloat()));
                }
            }
            if (subchannel.equals("HomeCooldown")) {
                Player p = Bukkit.getPlayer(UUID.fromString(in.readUTF()));
                if (p != null) {
                    plugin.cooldowns.put(p.getUniqueId(), in.readLong());
                }
            }
            if (subchannel.equals("ListHomes")) {
                Player p = Bukkit.getPlayer(UUID.fromString(in.readUTF()));
                if (p != null) {
                    List<String> homes = new ArrayList<>(Arrays.asList(in.readUTF().split(":")));
                    plugin.homes.put(p.getUniqueId(), homes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
