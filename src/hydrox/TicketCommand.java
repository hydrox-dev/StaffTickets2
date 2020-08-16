package hydrox;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class TicketCommand implements CommandExecutor, Listener
{

    public HashMap<UUID, String> allTickets = new HashMap<>();
    public HashSet<UUID> staffMembers = new HashSet<>();
    public Inventory inv = Bukkit.createInventory(null, 27, "§dStaff §eTickets");
    public HashSet<UUID> ticketTrue = new HashSet<>();
    public String displayName;
    public HashMap<UUID, UUID> lookedAt = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        if (command.getName().equalsIgnoreCase("tickets"))
        {
            Player p = (Player) commandSender;


            if (strings[0].equalsIgnoreCase("create"))
            {
                if (ticketTrue.contains(p.getUniqueId()))
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cYou already have an open ticket! Wait until staff get to it!"));
                    return false;
                }
                if (p.hasPermission("stafftickets.ticket"))
                {
                    if (strings[1] == null)
                    {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cYou need to write something in your ticket!"));
                        return false;
                    }
                    allTickets.put(p.getUniqueId(), strings[1]);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&e&7] &aYou have created a ticket! Staff will take a look!"));
                } else
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cYou lack the required permission to create a ticket!"));
                }
                return false;
            }


            if (strings[0].equalsIgnoreCase("add"))
            {
                if (p.hasPermission("stafftickets.add"))
                {
                    if (strings[1] == null)
                    {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cYou need to write a player name!"));
                        return false;
                    }
                    Player staffAdd = Bukkit.getPlayerExact(strings[1]);
                    if (staffAdd == null)
                    {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cHmmm... Did you mispell the player name?"));
                        return false;
                    }
                    staffMembers.add(staffAdd.getUniqueId());
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &aYou have added &e" + staffAdd.getDisplayName() + " &ato the staff list!"));
                } else
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cYou lack the required permission to create a ticket!"));
                    return false;
                }
                return false;
            }


            if (strings[0].equalsIgnoreCase("remove"))
            {
                if (p.hasPermission("stafftickets.remove"))
                {
                    Player staffAdd = Bukkit.getPlayerExact(strings[1]);
                    if (staffAdd == null)
                    {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cYou need to write a player name!"));
                        return false;
                    }
                    if (staffMembers.contains(staffAdd.getUniqueId()))
                    {
                        staffMembers.remove(staffAdd.getUniqueId());
                    } else
                    {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cThis player isn't staff."));
                        return false;
                    }
                } else
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cYou lack the required permission to create a ticket!"));
                    return false;
                }
            }


            if (strings[0].equalsIgnoreCase("stafflist"))
            {
                staffList(p);
                return false;
            }


            if (strings[0].equalsIgnoreCase("open"))
            {
                if (!p.hasPermission("stafftickets.open"))
                {
                     p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cYou lack the required permission to create a ticket!"));
                     return false;
                }
                getTickets(p);
                return false;
            }


            if (strings[0].equalsIgnoreCase("clear"))
            {
                if (!p.hasPermission("stafftickets.clear"))
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cYou lack the required permission to create a ticket!"));
                    return false;
                }
                allTickets.clear();
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &aYou have cleared all tickets!"));
            }
            return false;
        }
        return false;
    }



    protected ItemStack guiItem (final Material material, final String name, final String... lore)
    {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }



    public void staffList(Player p)
    {
        if (p.hasPermission("stafflist.list"))
        {
            if (staffMembers == null)
            {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cThere are no staff! To add some, use &e/tickets add <player>&c!"));
                return;
            }
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &7Staff Members are:"));
            for (UUID uuid : staffMembers)
            {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + Bukkit.getPlayer(uuid).getDisplayName()));
            }
        } else
        {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cYou lack the required permission to create a ticket!"));
            return;
        }
    }


    public void getTickets(Player p)
    {
        int i = 0;
        for (Map.Entry<UUID, String> entry : allTickets.entrySet())
        {
            UUID uuid = entry.getKey();
            if (lookedAt.containsKey(uuid))
            {
                inv.setItem(i, guiItem(Material.LIME_STAINED_GLASS_PANE, "§a" + Bukkit.getPlayer(uuid).getDisplayName() + "", "§a" + lookedAt.get(uuid) + "§c is looking at this ticket!"));
                i++;
                return;
            }
            inv.setItem(i, guiItem(Material.RED_STAINED_GLASS_PANE, "§a" + Bukkit.getPlayer(uuid).getDisplayName() + "", "§aLeft Click to see what&e " + Bukkit.getPlayer(uuid).getDisplayName() + " &asays", "§cRight Click to delete the ticket"));
            i++;
        }
        if (i > 26)
        {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cThere are an overwhelming amount of tickets!"));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cClear some tickets to see more!"));
            i = i - 26;
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cThere are &e" + i + "&c ticket(s) that couldn't fit!"));
        }
        openInv(p);
    }

    public void openInv(final HumanEntity ent)
    {
        ent.openInventory(inv);
    }

    @SuppressWarnings("deprecated")
    @EventHandler
    public void inventoryClick(final InventoryClickEvent e)
    {
        if (e.getInventory() != inv) return;
        if (e.getCurrentItem() == null) return;

        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        displayName = e.getCurrentItem().getItemMeta().getDisplayName();
        UUID uuid = Bukkit.getPlayer(displayName).getUniqueId();
        String ticket = allTickets.get(uuid);

        if (e.getClick().isLeftClick())
        {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8" + displayName + "&7 says: " + ticket));
            if (lookedAt.containsKey(uuid))
            {
                if (lookedAt.containsValue(p.getUniqueId()))
                {
                    lookedAt.remove(uuid);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &aYou have stopped looking at this ticket"));
                } else
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cAnother staff member is looking at this ticket!"));
                }
            }
            lookedAt.put(uuid, p.getUniqueId());
            inv.setItem(slot, guiItem(Material.LIME_STAINED_GLASS_PANE, "§a" + displayName, "§aLeft Click to stop looking at this ticket!", "§cRight Click to delete the ticket"));
            return;
        }
        if (e.getClick().isRightClick())
        {
            if (lookedAt.containsKey(uuid))
            {
                if(lookedAt.containsValue(p.getUniqueId()))
                {
                    lookedAt.remove(uuid);
                } else
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &cSomeone else is looking at this ticket!"));
                    return;
                }
            }
            allTickets.remove(uuid);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dS&eT&7] &aYou have deleted &e" + displayName + "&e's &aticket!"));
            p.closeInventory();
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&cYou deleted a ticket!"), ChatColor.translateAlternateColorCodes('&', "&e" + displayName + "&e's &cticket has been deleted!"), 1, 20, 5);
        }
    }
}
