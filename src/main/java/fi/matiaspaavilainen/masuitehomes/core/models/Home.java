package fi.matiaspaavilainen.masuitehomes.core.models;

import fi.matiaspaavilainen.masuitecore.core.objects.Location;
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
    private int id;
    /**
     * Name of the home
     */
    @NonNull
    @Column(name = "name")
    private String name;

    /**
     * Server
     */
    @NonNull
    @Column(name = "server")
    private String server;

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
            @AttributeOverride(name = "pitch", column = @Column(name = "pitch"))
    })
    @NonNull
    private Location location;
}
