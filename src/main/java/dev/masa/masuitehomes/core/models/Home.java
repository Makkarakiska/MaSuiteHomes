package dev.masa.masuitehomes.core.models;

import com.google.gson.Gson;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import dev.masa.masuitecore.core.objects.Location;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
@Table(name = "masuite_homes")

@NamedQuery(
        name = "findHomeByOwnerAndLikeName",
        query = "SELECT h FROM Home h WHERE h.owner = :owner AND h.name LIKE :name ORDER BY h.name"
)

@NamedQuery(
        name = "findHomeByOwnerAndName",
        query = "SELECT h FROM Home h WHERE h.owner = :owner AND h.name = :name ORDER BY h.name"
)
public class Home {

    @DatabaseField(id = true)
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
    @DatabaseField(dataType = DataType.UUID, readOnly = true)
    private UUID owner;

    /**
     * Location
     */
    @NonNull
    private Location location;

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

    public Location getLocation() {
        return new Location(server, world, x, y, z, yaw, pitch);
    }

    public void setLocation(Location loc) {
        this.location = loc;
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
