package dev.masa.masuitehomes.common.models;

import com.google.gson.Gson;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import dev.masa.masuitecore.common.objects.Location;
import lombok.*;

import javax.persistence.Table;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Table(name = "masuite_homes")
public class Home {

    @DatabaseField(generatedId = true)
    private int id;
    /**
     * Name of the home
     */
    @NonNull
    @DatabaseField
    private String name;

    /**
     * Owner of the Home
     */
    @NonNull
    @DatabaseField(dataType = DataType.UUID)
    private UUID owner;

    /**
     * Location
     */
    @DatabaseField
    private String server;
    @DatabaseField
    private String world;
    @DatabaseField
    private Double x;
    @DatabaseField
    private Double y;
    @DatabaseField
    private Double z;
    @DatabaseField
    private Float yaw = 0.0F;
    @DatabaseField
    private Float pitch = 0.0F;

    public Home(String name, UUID owner, Location location) {
        this.name = name;
        this.owner = owner;
        this.setLocation(location);
    }

    public Location getLocation() {
        return new Location(server, world, x, y, z, yaw, pitch);
    }

    public void setLocation(Location loc) {
        this.server = loc.getServer();
        this.world = loc.getWorld();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    public String serialize() {
        return new Gson().toJson(this);
    }

    public Home deserialize(String json) {
        return new Gson().fromJson(json, Home.class);
    }
}
