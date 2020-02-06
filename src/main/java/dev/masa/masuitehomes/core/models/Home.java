package dev.masa.masuitehomes.core.models;

import com.google.gson.Gson;
import dev.masa.masuitecore.core.objects.Location;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * Name of the home
     */
    @NonNull
    @Column(name = "name")
    private String name;

    /**
     * Owner of the Home
     */
    @NonNull
    @Column(name = "owner")
    @Type(type = "uuid-char")
    private UUID owner;

    /**
     * Location
     */
    @Embedded
    @AttributeOverrides(value = {
            @AttributeOverride(name = "x", column = @Column(name = "x")),
            @AttributeOverride(name = "y", column = @Column(name = "y")),
            @AttributeOverride(name = "z", column = @Column(name = "z")),
            @AttributeOverride(name = "yaw", column = @Column(name = "yaw")),
            @AttributeOverride(name = "pitch", column = @Column(name = "pitch")),
            @AttributeOverride(name = "server", column = @Column(name = "server"))
    })
    @NonNull
    private Location location;

    public String serialize() {
        return new Gson().toJson(this);
    }

    public Home deserialize(String json) {
        return new Gson().fromJson(json, Home.class);
    }
}
