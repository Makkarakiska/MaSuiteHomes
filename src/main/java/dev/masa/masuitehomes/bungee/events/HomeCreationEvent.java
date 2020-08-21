package dev.masa.masuitehomes.bungee.events;

import dev.masa.masuitehomes.common.models.Home;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

public class HomeCreationEvent extends Event {
    @Getter
    private Home home;

    public HomeCreationEvent(Home home) {
        this.home = home;
    }

}