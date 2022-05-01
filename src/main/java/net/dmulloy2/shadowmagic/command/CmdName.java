/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.command;

import java.util.logging.Level;

import net.dmulloy2.shadowmagic.ShadowMagic;
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

public class CmdName extends ShadowMagicCommand
{
	public CmdName(ShadowMagic plugin)
	{
		super(plugin);
		this.name = "name";
		this.addRequiredArg("name");
		this.description = "Set the name of an item";
		this.permission = Permission.CMD_NAME;
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

		// Join and fix spacing
		String name = FormatUtil.join(" ", args);
		name = name.replaceAll("_", " ");
		name = name.replaceAll("\"", "");

		// Format
		name = FormatUtil.format(name);

		// Charge
		if (! hasPermission(Permission.CMD_NAME_FREE))
		{
			String check = ChatColor.stripColor(name);
			int length = check.length();
			int perChar = plugin.getConfig().getInt("metaCharges.name", 100);
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

		// Apply
		ItemMeta meta = inHand.getItemMeta();
		meta.setDisplayName(name);
		inHand.setItemMeta(meta);

		plugin.logToFile(Level.FINE, player.getName() + " has set the name of their " + inHand.getType() + " to " + name);
		
		sendpMessage("&eYou have set your &b{0}&e''s name to \"&r{1}&e\"", FormatUtil.getFriendlyName(inHand.getType()), name);
	}
}
