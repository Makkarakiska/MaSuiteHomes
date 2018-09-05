package fi.matiaspaavilainen.masuitehomes.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitehomes.Home;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.stream.Collectors;

public class List extends Command {
    public List() {
        super("homes", "masuitehomes.home.list", "homelist", "listhomes");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        if(!(cs instanceof ProxiedPlayer)){
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) cs;
        Formator formator = new Formator();
        Configuration config = new Configuration();

        if(args.length == 1) {
            Home home = new Home();
            String homes = config.load("homes", "messages.yml").getString("homes.title");
            homes = homes + home.homes(p.getUniqueId()).stream().map(Home::getName).collect(Collectors.joining(config.load("homes", "messages.yml").getString("homes.split")));
            formator.sendMessage(p, homes);
        }else{
            formator.sendMessage(p, config.load("homes", "syntax.yml").getString("home.list"));
        }
    }
}
