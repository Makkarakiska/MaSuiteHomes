package dev.masa.masuitehomes.common.config.server;

import lombok.Getter;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.serialize.SerializationException;

@ConfigSerializable
public class HomeServerConfig {

    @Getter
    @Setting("warmup")
    @Comment("In seconds, change 0 to remove warmup")
    private final Integer warmup = 3;

    @Getter
    @Setting("cooldown")
    @Comment("In seconds, change 0 to remove cooldown")
    private final Integer cooldown = 3;

    private static final ObjectMapper<HomeServerConfig> MAPPER;

    static {
        try {
            MAPPER = ObjectMapper.factory().get(HomeServerConfig.class);
        } catch (final SerializationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static HomeServerConfig loadFrom(final ConfigurationNode node) throws SerializationException {
        return MAPPER.load(node);
    }

}
