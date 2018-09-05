package fi.matiaspaavilainen.masuitehomes;

import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitehomes.commands.Delete;
import fi.matiaspaavilainen.masuitehomes.commands.List;
import fi.matiaspaavilainen.masuitehomes.commands.Set;
import fi.matiaspaavilainen.masuitehomes.commands.Teleport;
import net.md_5.bungee.api.plugin.Plugin;

public class MaSuiteHomes extends Plugin {

    @Override
    public void onEnable() {
        super.onEnable();

        //Configs
        Configuration config = new Configuration();
        config.create(this, "homes", "messages.yml");
        config.create(this, "homes", "syntax.yml");

        //Commands
        getProxy().getPluginManager().registerCommand(this, new Teleport());
        getProxy().getPluginManager().registerCommand(this, new Set());
        getProxy().getPluginManager().registerCommand(this, new Delete());
        getProxy().getPluginManager().registerCommand(this, new List());
    }
}
