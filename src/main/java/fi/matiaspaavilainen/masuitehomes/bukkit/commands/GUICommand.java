package fi.matiaspaavilainen.masuitehomes.bukkit.commands;

import fi.matiaspaavilainen.masuitecore.bukkit.chat.Formator;
import fi.matiaspaavilainen.masuitecore.bukkit.gui.MaSuiteGUI;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUICommand implements CommandExecutor {

    private MaSuiteHomes plugin;
    private Formator formator = new Formator();
    private BukkitConfiguration config = new BukkitConfiguration();

    public GUICommand(MaSuiteHomes p) {
        plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (!(cs instanceof Player)) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (plugin.in_command.contains(cs)) {
                formator.sendMessage(cs, config.load(null, "messages.yml").getString("on-active-command"));
                return;
            }

            plugin.in_command.add(cs);

            Player p = (Player) cs;

            FileConfiguration fc = config.load("homes", "messages.yml");
            String title = fc.getString("gui.title");
            String name = fc.getString("gui.name");
            String[] description = fc.getStringList("gui.description").toArray(new String[0]);
            Material material = Material.getMaterial(fc.getString("gui.item").toUpperCase());
            if (material == null) {
                return;
            }
            int homesCount = plugin.homes.get(p.getUniqueId()).size();

            MaSuiteGUI gui = new MaSuiteGUI(title, 9);
            if (homesCount <= 9) {
                gui = new MaSuiteGUI(title, 9);
            } else if (homesCount <= 18) {
                gui = new MaSuiteGUI(title, 18);
            } else if (homesCount <= 27) {
                gui = new MaSuiteGUI(title, 27);
            } else if (homesCount <= 36) {
                gui = new MaSuiteGUI(title, 36);
            } else if (homesCount <= 45) {
                gui = new MaSuiteGUI(title, 45);
            } else if (homesCount <= 54) {
                gui = new MaSuiteGUI(title, 54);
            }
            int i = 0;
            for (String home : plugin.homes.get(p.getUniqueId())) {
                gui.setItem(new ItemStack(material), name.replace("%home%", home), i, new MaSuiteGUI.ClickRunnable() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        p.performCommand("home " + home);
                    }
                }, description);
                i++;
            }

            gui.openInventory(p);

            plugin.in_command.remove(cs);
        });

        return true;
    }

}