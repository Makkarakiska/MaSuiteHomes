package dev.masa.masuitehomes.bukkit;

import dev.masa.masuitehomes.common.models.Home;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@AllArgsConstructor
public class HomeMessageListener implements PluginMessageListener {

    private final MaSuiteHomes plugin;

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

        String subchannel = null;
        try {
            subchannel = in.readUTF();
            if (subchannel.equals("AddHome")) {
                Player p = Bukkit.getPlayer(UUID.fromString(in.readUTF()));
                if (p == null) return;
                if (!plugin.homes.containsKey(p.getUniqueId())) {
                    plugin.homes.put(p.getUniqueId(), new ArrayList<>());
                }
                Home home = new Home().deserialize(in.readUTF());
                plugin.homes.get(p.getUniqueId()).add(home);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
