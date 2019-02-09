
package fi.matiaspaavilainen.masuitehomes.bukkit.commands;

import fi.matiaspaavilainen.masuitecore.bukkit.chat.Formator;
import fi.matiaspaavilainen.masuitecore.bukkit.gui.MaSuiteGUI;
import fi.matiaspaavilainen.masuitecore.core.adapters.BukkitAdapter;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import fi.matiaspaavilainen.masuitehomes.core.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private FileConfiguration guicfg = config.load("homes", "gui.yml");

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

            if (!plugin.homes.containsKey(p.getUniqueId())) {
                plugin.homes.put(p.getUniqueId(), new ArrayList<>());
            }

            String title = guicfg.getString("list.title");

            int homesCount = plugin.homes.get(p.getUniqueId()).size();

            MaSuiteGUI gui = new MaSuiteGUI(title, 54);
            generateBaseMenu(gui);
            final int[] page = {0};
            addHomes(gui, p, page[0]);

            int max = plugin.getMaxHomes(p);

            gui.setItem(new ItemStack(Material.getMaterial(guicfg.getString("list.controls.previous.item").toUpperCase())), guicfg.getString("list.controls.previous.title"), 47, new MaSuiteGUI.ClickRunnable() {
                @Override
                public void run(InventoryClickEvent e) {
                }
            }, guicfg.getStringList("list.controls.previous.description").toArray(new String[0]));

            List<String> infoDesc = new ArrayList<>();
            for (String string : guicfg.getStringList("list.controls.info.description")) {
                infoDesc.add(string.replace("%used%", "" + homesCount).replace("%total%", "" + (max == -1 ? guicfg.getString("list.controls.info.unlimited") : max)));
            }

            gui.setItem(new ItemStack(Material.getMaterial(guicfg.getString("list.controls.info.item").toUpperCase())), guicfg.getString("list.controls.info.title"), 49, new MaSuiteGUI.ClickRunnable() {
                @Override
                public void run(InventoryClickEvent e) {
                    p.closeInventory();
                }
            }, infoDesc.toArray(new String[0]));

            gui.setItem(new ItemStack(Material.getMaterial(guicfg.getString("list.controls.next.item").toUpperCase())), guicfg.getString("list.controls.next.title"), 51, new MaSuiteGUI.ClickRunnable() {
                @Override
                public void run(InventoryClickEvent e) {
                    if (homesCount > page[0]) {
                        /*MaSuiteGUI nextPage = new MaSuiteGUI(title, 54);
                        generateBaseMenu(nextPage);
                        page[0] += 29;
                        addHomes(nextPage, p, page[0]);
                        nextPage.openInventory(p);*/
                    }
                }
            }, guicfg.getStringList("list.controls.next.description").toArray(new String[0]));

            gui.openInventory(p);

            plugin.in_command.remove(cs);

        });

        return true;
    }


    private void generateBaseMenu(MaSuiteGUI gui) {
        List<Material> placeholders = new ArrayList<>();
        Material material = Material.getMaterial(guicfg.getString("list.item").toUpperCase());

        for (String placeholderItem : guicfg.getStringList("list.placeholders")) {
            if (Material.getMaterial(placeholderItem.toUpperCase()) == null) {
                continue;
            }
            placeholders.add(Material.valueOf(placeholderItem.toUpperCase()));
        }
        if (material == null) {
            return;
        }

        int[] slots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 35, 44, 53, 52, 51, 50, 49, 48, 47, 46, 45, 36, 27, 18, 9};
        int i = 0;
        for (int slot : slots) {
            if (i == placeholders.size()) {
                i = 0;
            }
            ItemStack is = new ItemStack(placeholders.get(i), 1);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(" ");
            is.setItemMeta(meta);
            gui.setItem(is, slot);
            i++;
        }
    }

    private void addHomes(MaSuiteGUI gui, Player p, int page) {
        int i = 10;
        String name = guicfg.getString("list.name");
        Material material = Material.getMaterial(guicfg.getString("list.item").toUpperCase());
        if (material == null) {
            return;
        }
        List<Home> homes = plugin.homes.get(p.getUniqueId());
        for (Home home : homes.subList(page, homes.size())) {
            if (gui.getSourceInventory().getItem(i) == null) {
                List<String> description = new ArrayList<>();
                for (String string : guicfg.getStringList("list.description")) {
                    Location loc = BukkitAdapter.adapt(home.getLocation());
                    description.add(
                            string.replace("%name%", home.getName())
                                    .replace("%server%", home.getServer())
                                    .replace("%world%", loc.getWorld().getName())
                                    .replace("%x%", "" + loc.getBlockX())
                                    .replace("%y%", "" + loc.getBlockX())
                                    .replace("%z%", "" + loc.getBlockZ()));
                }
                gui.setItem(new ItemStack(material), name.replace("%home%", home.getName()), i, new MaSuiteGUI.ClickRunnable() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        if (e.getClick().isLeftClick()) {
                            p.performCommand("home " + home.getName());
                        }
                        if (e.getClick().isRightClick()) {
                            openEditMenu(p, home);
                        }

                    }
                }, description.toArray(new String[0]));
                i++;
            } else {
                i++;
            }
        }
    }

    private void openEditMenu(Player player, Home home) {
        ItemStack is = new ItemStack(Material.getMaterial(guicfg.getString("edit.placeholder")), 1);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(" ");
        is.setItemMeta(meta);
        MaSuiteGUI editGui = new MaSuiteGUI(guicfg.getString("edit.title").replace("%name%", home.getName()), 9, is);

        List<String> description = new ArrayList<>();
        for (String string : guicfg.getStringList("edit.delete.description")) {
            Location loc = BukkitAdapter.adapt(home.getLocation());
            description.add(
                    string.replace("%name%", home.getName())
                            .replace("%server%", home.getServer())
                            .replace("%world%", loc.getWorld().getName())
                            .replace("%x%", "" + loc.getBlockX())
                            .replace("%y%", "" + loc.getBlockX())
                            .replace("%z%", "" + loc.getBlockZ()));
        }

        editGui.setItem(new ItemStack(Material.getMaterial(guicfg.getString("edit.delete.item"))), guicfg.getString("edit.delete.title").replace("%name%", home.getName()), 4, new MaSuiteGUI.ClickRunnable() {
            @Override
            public void run(InventoryClickEvent e) {
                player.performCommand("delhome " + home.getName());
                player.closeInventory();
            }
        }, description.toArray(new String[0]));

        editGui.openInventory(player);
    }
}