package dev.masa.masuitehomes.common.config.proxy;

import dev.masa.masuitehomes.common.models.Home;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.serialize.SerializationException;

@ConfigSerializable
public class HomeProxyMessageConfig {

    @Getter
    @Setting("home-limit-reached")
    private final String homeLimitReached = "&cYou have reached home limit";

    @Getter
    @Setting("home-not-found")
    private final String homeNotFound = "&cHome with that name not found";

    @Getter
    @Setting("home-hover-text")
    private final String homeHoverText = "&8Teleport to &9%home%";

    @Getter
    @Setting("home")
    private final HomeMessage home = new HomeMessage();

    @Getter
    @Setting("homes")
    private final HomesMessage homes = new HomesMessage();

    @ConfigSerializable
    public static class HomeMessage {
        @Getter
        @Setting("set")
        public final String set = "&7Created home with name &9%home%&7!";

        @Getter
        @Setting("updated")
        public final String updated = "&7Updated home with name &9%home%&7!";

        @Getter
        @Setting("deleted")
        public final String deleted = "&7Deleted home with name &9%home%&7!";

        @Getter
        @Setting("teleported")
        public final String teleported = "&7Teleported to &9%home%&7!";
    }

    @ConfigSerializable
    public static class HomesMessage {
        @Getter
        @Setting("title")
        public final String title = "&9Your &7homes: &b";

        @Getter
        @Setting("name")
        public final String name = "&7%home%";

        @Getter
        @Setting("split")
        public final String split = "&7, ";

        @Getter
        @Setting("title-others")
        public final String titleOthers = "&9%player%'s &7homes: ";

        @Getter
        @Setting("server-name")
        public final String serverName = " &9%server%&7: ";
    }

    private static final ObjectMapper<HomeProxyMessageConfig> MAPPER;

    static {
        try {
            MAPPER = ObjectMapper.factory().get(HomeProxyMessageConfig.class);
        } catch (final SerializationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static HomeProxyMessageConfig loadFrom(final ConfigurationNode node) throws SerializationException {
        return MAPPER.load(node);
    }

}
