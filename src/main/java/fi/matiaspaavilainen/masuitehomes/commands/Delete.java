package fi.matiaspaavilainen.masuitehomes.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitehomes.Home;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Delete extends Command {
    public Delete() {
        super("delhome", "masuitehomes.home.delete", "homedel", "deletehome");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        if(!(cs instanceof ProxiedPlayer)){
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) cs;
        Formator formator = new Formator();
        Configuration config = new Configuration();
        if(args.length == 1){
            Home home = new Home();
            home = home.find(args[0], p.getUniqueId());
            if(home.getServer() == null){
                formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-not-found"));
                return;
            }
            home.delete(home);
        }else{
            formator.sendMessage(p, config.load("homes", "syntax.yml").getString("home.delete"));
        }
    }
}
