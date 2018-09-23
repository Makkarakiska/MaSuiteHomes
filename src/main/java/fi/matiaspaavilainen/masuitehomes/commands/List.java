package fi.matiaspaavilainen.masuitehomes.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitehomes.Home;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


public class List extends Command {
    public List() {
        super("homes", "masuitehomes.home.list", "homelist", "listhomes");
    }

    private Configuration config = new Configuration();
    private Formator formator = new Formator();

    @Override
    public void execute(CommandSender cs, String[] args) {
        if (!(cs instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) cs;

        if (args.length == 0) {
            Home h = new Home();
            TextComponent homes = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.title")));

            int i = 0;
            String split = formator.colorize(config.load("homes", "messages.yml").getString("homes.split"));
            for (Home home: h.homes(p.getUniqueId())) {
                if(i++ == h.homes(p.getUniqueId()).size() - 1){
                    TextComponent hc = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.name").replace("%home%", home.getName())));
                    hc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home.getName()));
                    hc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(formator.colorize(config.load("homes", "messages.yml").getString("home-hover-text").replace("%home%", home.getName()))).create()));
                    homes.addExtra(hc);
                }else{
                    TextComponent hc = new TextComponent(formator.colorize(config.load("homes", "messages.yml").getString("homes.name").replace("%home%", home.getName())));
                    hc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home.getName()));
                    hc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(formator.colorize(config.load("homes", "messages.yml").getString("home-hover-text").replace("%home%", home.getName()))).create()));
                    homes.addExtra(hc);
                    homes.addExtra(split);
                }
            }
            //homes = homes + h.homes(p.getUniqueId()).stream().map(Home::getName).collect(Collectors.joining();
            p.sendMessage(homes);
        } else {
            formator.sendMessage(p, config.load("homes", "syntax.yml").getString("home.list"));
        }

    }
}
