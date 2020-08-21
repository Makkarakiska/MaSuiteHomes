package dev.masa.masuitehomes.bungee.dataextensions;

import com.djrapitops.plan.extension.CallEvents;
import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.annotation.TableProvider;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.table.Table;
import dev.masa.masuitecore.common.objects.Location;
import dev.masa.masuitehomes.bungee.MaSuiteHomes;

import java.util.StringJoiner;
import java.util.UUID;

@PluginInfo(name = "MaSuiteHomes", iconName = "home", iconFamily = Family.SOLID, color = Color.BLUE)
public class HomeDataExtension implements DataExtension {

    private MaSuiteHomes plugin;

    public HomeDataExtension(MaSuiteHomes plugin) {
        this.plugin = plugin;
    }

    @Override
    public CallEvents[] callExtensionMethodsOn() {
        return new CallEvents[]{
                CallEvents.PLAYER_JOIN,
                CallEvents.PLAYER_LEAVE
        };
    }

    @TableProvider(tableColor = Color.NONE)
    public Table homeListing(UUID uuid) {
        Table.Factory homeTable = Table.builder()
                .columnOne("Name", new Icon(Family.SOLID, "gavel", Color.BLUE))
                .columnTwo("Location", new Icon(Family.SOLID, "map-marked-alt", Color.BLUE))
                .columnThree("Server", new Icon(Family.SOLID, "server", Color.BLUE));

        plugin.getHomeService().getHomes(uuid, homes -> homes.forEach(home -> {
            Location loc = home.getLocation();
            StringJoiner stringLocation = new StringJoiner(", ");
            stringLocation.add("X: " + loc.getX());
            stringLocation.add("Y: " + loc.getY());
            stringLocation.add("Z: " + loc.getZ());
            homeTable.addRow(home.getName(), stringLocation.toString(), loc.getServer());
        }));

        return homeTable.build();
    }

}
