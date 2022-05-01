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
import net.dmulloy2.shadowmagic.gui.ItemSelectGUI;
import net.dmulloy2.shadowmagic.types.Numeral;
import net.dmulloy2.shadowmagic.types.Permission;
import net.dmulloy2.shadowmagic.types.PlayerData;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.NumberUtil;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author dmulloy2
 */

public class CmdEnchant extends ShadowMagicCommand
{
	public CmdEnchant(ShadowMagic plugin)
	{
		super(plugin);
		this.name = "enchant";
		this.addOptionalArg("id");
		this.description = "Enchant an item";
		this.permission = Permission.CMD_ENCHANT;
		this.mustBePlayer = true;
		this.usesPrefix = true;
	}

	@Override
	public void perform()
	{
		if (args.length == 0)
		{
			PlayerData data = plugin.getPlayerDataCache().getData(player);
			ItemSelectGUI isGUI = new ItemSelectGUI(plugin, player);
			data.setOpenGUI(isGUI);
			isGUI.open();
		}
		else
		{
			ItemStack inHand = player.getInventory().getItemInMainHand();
			if (inHand == null || inHand.getType() == Material.AIR)
			{
				err("Invalid item!");
				return;
			}

			String[] split = args[0].split("-");
			int id = NumberUtil.toInt(split[0]);
			if (id < 0)
			{
				err("Invalid id!");
				return;
			}

			Enchantment ench = plugin.getEnchantmentHandler().getEnchantment(id);
			if (ench == null)
			{
				err("Invalid enchantment!");
				return;
			}

			int level = NumberUtil.toInt(split[1]);
			if (level < 0)
			{
				err("Invalid level!");
				return;
			}

			ItemMeta meta = inHand.getItemMeta();
			List<String> lore = new ArrayList<String>();
			if (meta.hasLore())
			{
				for (String s : meta.getLore())
				{
					split = s.split(" ");
					if (split[0].equalsIgnoreCase(ench.getDisplay()))
					{
						err("Your item already has this enchantment!");
						return;
					}

					lore.add(s);
				}
			}

			lore.add(ench.getDisplay() + " " + Numeral.getNumeral(level));
			meta.setLore(lore);
			inHand.setItemMeta(meta);

			plugin.logToFile(Level.CONFIG, player.getName() + " has applied enchantment " + ench.getName() + ":" + level + " to their "
					+ inHand.getType());

			sendpMessage("You have applied enchantment &b{0} &ewith level &b{1} &eto &b{2}", ench.getName(), level,
					FormatUtil.getFriendlyName(inHand.getType()));
		}
	}
}
