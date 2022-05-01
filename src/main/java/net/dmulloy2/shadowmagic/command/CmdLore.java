/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.enchantments.Enchantment;
import net.dmulloy2.shadowmagic.types.Permission;
import net.dmulloy2.util.FormatUtil;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author dmulloy2
 */

public class CmdLore extends ShadowMagicCommand
{
	public CmdLore(ShadowMagic plugin)
	{
		super(plugin);
		this.name = "lore";
		this.addRequiredArg("lore");
		this.description = "Set the lore of an item";
		this.permission = Permission.CMD_LORE;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		ItemStack inHand = player.getInventory().getItemInMainHand();
		if (inHand == null || inHand.getType() == Material.AIR)
		{
			err("You must have an item in your hand to do this!");
			return;
		}

		ItemMeta meta = inHand.getItemMeta();

		// Join and fix spacing
		String str = FormatUtil.join(" ", args);
		str = str.replaceAll("_", " ");
		str = str.replaceAll("\"", "");

		// Charging
		int length = 0;
		
		String[] split = str.split("\\|");
		List<String> newLore = new ArrayList<>();
		for (String line : split)
		{
			line = FormatUtil.format(line);
			for (Enchantment ench : plugin.getEnchantmentHandler().getEnchantments())
			{
				if (line.matches(ench.getDisplay() + ".*"))
				{
					err("Line \"&r{0}&4\" conflicts with enchantment &c{1}", line, ench.getName());
					return;
				}
			}

			length += ChatColor.stripColor(line).length();

			newLore.add(line);
		}

		// Charging
		if (! hasPermission(Permission.CMD_LORE_FREE))
		{
			int perChar = plugin.getConfig().getInt("metaCharges.lore", 100);
			double cost = (double) length * perChar;
			Economy econ = plugin.getEconomy();
			if (econ != null)
			{
				if (! econ.has(player, cost))
				{
					err("&4Insufficient funds! You need &c{0} &4to do this!", econ.format(cost));
					return;
				}
		
				econ.withdrawPlayer(player, cost);
				sendpMessage("&eYou have been charged &b{0} &efor this operation!", econ.format(cost));
			}
		}

		List<String> lore = new ArrayList<>();
		if (meta.hasLore())
			lore.addAll(meta.getLore());
		lore.addAll(newLore);

		// Apply
		meta.setLore(lore);
		inHand.setItemMeta(meta);

		plugin.logToFile(Level.WARNING, player.getName() + " has set the lore of their " + inHand.getType() + " to " + lore);

		sendpMessage("&eYou have set your &b{0}&e''s lore to \"&r{1}&e\"", FormatUtil.getFriendlyName(inHand.getType()), lore);
	}
}
