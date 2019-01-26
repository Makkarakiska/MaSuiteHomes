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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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

            List<Material> placeholders = new ArrayList<>();
            for (String placeholderItem : fc.getStringList("gui.placeholders")) {
                if (Material.getMaterial(placeholderItem.toUpperCase()) == null) {
                    continue;
                }
                placeholders.add(Material.valueOf(placeholderItem.toUpperCase()));
            }
            if (material == null) {
                return;
            }
            int homesCount = plugin.homes.get(p.getUniqueId()).size();

            MaSuiteGUI gui = new MaSuiteGUI(title, 54);
            int[] slots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 35, 44, 53, 52, 51, 50, 49, 48, 47, 46, 45, 36, 27, 18, 9};

            int i = 0;
            for (int slot : slots) {
                if (i == placeholders.size()) {
                    i = 0;
                }
                ItemStack is = new ItemStack(placeholders.get(i));
                ItemMeta meta = is.getItemMeta();
                meta.setDisplayName("");
                is.setItemMeta(meta);
                gui.setItem(new ItemStack(is), slot);
                i++;
            }

            i = 10;
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

            gui.setItem(new ItemStack(Material.getMaterial(fc.getString("gui.controls.previous.item").toUpperCase())), fc.getString("gui.controls.previous.title"), 47, new MaSuiteGUI.ClickRunnable() {
                @Override
                public void run(InventoryClickEvent e) {
                }
            }, fc.getStringList("gui.controls.previous.description").toArray(new String[0]));

            List<String> infoDesc = new ArrayList<>();
            for(String string : fc.getStringList("gui.controls.info.description")){
                infoDesc.add(string.replace("%used%", "" + homesCount).replace("%total%", "" + (max == -1 ? fc.getString("gui.controls.info.unlimited") : max)));
            }

            gui.setItem(new ItemStack(Material.getMaterial(fc.getString("gui.controls.info.item").toUpperCase())), fc.getString("gui.controls.info.title"), 49, new MaSuiteGUI.ClickRunnable() {
                @Override
                public void run(InventoryClickEvent e) {
                    p.closeInventory();
                }
            }, infoDesc.toArray(new String[0]));

            gui.setItem(new ItemStack(Material.getMaterial(fc.getString("gui.controls.next.item").toUpperCase())), fc.getString("gui.controls.next.title"), 51, new MaSuiteGUI.ClickRunnable() {
                @Override
                public void run(InventoryClickEvent e) {
                    gui.openInventory(p);
                }
            }, fc.getStringList("gui.controls.next.description").toArray(new String[0]));

            gui.openInventory(p);

            plugin.in_command.remove(cs);

        });

        return true;
    }

}