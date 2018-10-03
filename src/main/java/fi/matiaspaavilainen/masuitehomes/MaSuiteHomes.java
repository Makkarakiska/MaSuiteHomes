package fi.matiaspaavilainen.masuitehomes;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fi.matiaspaavilainen.masuitecore.Updator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitehomes.database.Database;
import fi.matiaspaavilainen.masuitecore.managers.Location;
import fi.matiaspaavilainen.masuitehomes.commands.Delete;
import fi.matiaspaavilainen.masuitehomes.commands.List;
import fi.matiaspaavilainen.masuitehomes.commands.Set;
import fi.matiaspaavilainen.masuitehomes.commands.Teleport;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MaSuiteHomes extends Plugin implements Listener {

    static Database db = new Database();
    @Override
    public void onEnable() {
        super.onEnable();

        //Configs
        Configuration config = new Configuration();
        config.create(this, "homes", "messages.yml");
        getProxy().getPluginManager().registerListener(this, this);
        //Commands

        db.connect();
        db.createTable("homes",
                "(id INT(10) unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100) NOT NULL, owner VARCHAR(36) NOT NULL, server VARCHAR(100) NOT NULL, world VARCHAR(100) NOT NULL, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        new Updator().checkVersion(this.getDescription(), "60632");
    }

    @Override
    public void onDisable(){
        db.hikari.close();
    }
    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) throws IOException {
        if(!e.getTag().equals("BungeeCord")){
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
        String subchannel = in.readUTF();
        if(subchannel.equals("HomeCommand")){
            Teleport teleport = new Teleport();
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            if(p == null){
                return;
            }
            teleport.teleport(p, in.readUTF());
            sendCooldown(p);
        }
        if(subchannel.equals("SetHomeCommand")){
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            if(p == null){
                return;
            }
            Set set = new Set();
            String[] location = in.readUTF().split(":");
            set.set(p, in.readUTF(), new Location(location[0], Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]), Float.parseFloat(location[4]), Float.parseFloat(location[5])));
        }
        if(subchannel.equals("DelHomeCommand")){
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            if(p == null){
                return;
            }
            Delete delete = new Delete();
            delete.delete(p, in.readUTF());
        }
        if(subchannel.equals("ListHomeCommand")){
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            if(p == null){
                return;
            }
            List list = new List();
            list.list(p);
        }
    }

    private void sendCooldown(ProxiedPlayer p) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("HomeCooldown");
        out.writeUTF(String.valueOf(p.getUniqueId()));
        try {
            Thread.sleep(200);
            out.writeLong(System.currentTimeMillis());
            p.getServer().sendData("BungeeCord", out.toByteArray());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
