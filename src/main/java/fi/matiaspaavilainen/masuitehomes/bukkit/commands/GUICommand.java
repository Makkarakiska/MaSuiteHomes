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

            MaSuiteGUI gui = new MaSuiteGUI(title, 54);
            int[] slots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 18, 26, 35, 44, 53, 52, 51, 50, 49, 48, 47, 46, 45, 36, 27, 18, 9};
            Material lastItem = Material.ORANGE_STAINED_GLASS_PANE;
            for (int i = 0; i < slots.length; i++) {
                if (lastItem.equals(Material.ORANGE_STAINED_GLASS_PANE)) {
                    gui.setItem(new ItemStack(Material.LIME_STAINED_GLASS_PANE), slots[i]);
                    lastItem = Material.LIME_STAINED_GLASS_PANE;
                } else {
                    gui.setItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), slots[i]);
                    lastItem = Material.ORANGE_STAINED_GLASS_PANE;
                }
            }

            int i = 10;
            for (String home : plugin.homes.get(p.getUniqueId())) {
                if (gui.getSourceInventory().getItem(i) == null) {
                    gui.setItem(new ItemStack(material), name.replace("%home%", home), i, new MaSuiteGUI.ClickRunnable() {
                        @Override
                        public void run(InventoryClickEvent e) {
                            p.performCommand("home " + home);
                        }
                    }, description);
                    i++;
                } else {
                    i++;
                }
            }
            int max = plugin.getMaxHomes(p);

            gui.setItem(new ItemStack(Material.PAPER), ChatColor.DARK_PURPLE + "Previous", 47, new MaSuiteGUI.ClickRunnable() {
                        @Override
                        public void run(InventoryClickEvent e) {
                        }
                    }, ChatColor.LIGHT_PURPLE + "Go to the previous page!");

            gui.setItem(new ItemStack(Material.COMPASS), ChatColor.DARK_PURPLE + "Page 1/1", 49, new MaSuiteGUI.ClickRunnable() {
                @Override
                public void run(InventoryClickEvent e) {
                    p.closeInventory();
                }
            }, ChatColor.LIGHT_PURPLE + "Click to close!");

            gui.setItem(new ItemStack(Material.PAPER), ChatColor.DARK_PURPLE + "Next", 51, new MaSuiteGUI.ClickRunnable() {
                @Override
                public void run(InventoryClickEvent e) {
                }
            }, ChatColor.LIGHT_PURPLE + "Go to the next page");

            gui.setItem(new ItemStack(Material.CLOCK), ChatColor.DARK_PURPLE + "Home info", 53, new MaSuiteGUI.ClickRunnable() {
                        @Override
                        public void run(InventoryClickEvent e) {
                        }
                    }, ChatColor.LIGHT_PURPLE + "Homes used: " + homesCount,
                    ChatColor.LIGHT_PURPLE + "Homes available: " + (max == -1 ? "unlimited" : max));

            gui.openInventory(p);

            plugin.in_command.remove(cs);

        });

        return true;
    }

}