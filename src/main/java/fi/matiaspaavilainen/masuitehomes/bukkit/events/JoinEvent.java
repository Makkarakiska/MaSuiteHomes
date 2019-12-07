package fi.matiaspaavilainen.masuitehomes.bukkit.events;

import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JoinEvent implements Listener {

    private MaSuiteHomes plugin;

    public JoinEvent(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(b)) {
            out.writeUTF("ListHomes");
            out.writeUTF(e.getPlayer().getName());
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> e.getPlayer().sendPluginMessage(plugin, "BungeeCord", b.toByteArray()), 20);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
