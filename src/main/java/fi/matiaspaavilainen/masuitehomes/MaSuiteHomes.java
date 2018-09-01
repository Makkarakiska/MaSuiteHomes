package fi.matiaspaavilainen.masuitehomes;

import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitehomes.commands.Set;
import net.md_5.bungee.api.plugin.Plugin;

public class MaSuiteHomes extends Plugin {

    @Override
    public void onEnable() {
        super.onEnable();
        Configuration config = new Configuration();
        config.create(this, "homes", "messages.yml");
        config.create(this, "homes", "syntax.yml");
        getProxy().getPluginManager().registerCommand(this, new Set());
    }
}
