/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.gui;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.types.Class;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author dmulloy2
 */

public class PaidClassesGUI extends GUI
{
	private static final String NAME = "Paid Classes";
	private static final int FREE_INDEX = 26;
	private static final int SIZE = 27;

	public PaidClassesGUI(ShadowMagic plugin, Player player)
	{
		super(plugin, player);
	}

	@Override
	public void open()
	{
		Inventory inventory = Bukkit.createInventory(player, SIZE, NAME);

		int i = 0;
		for (Class clazz : plugin.getClassHandler().getClasses())
		{
			if (clazz.getCost() > 0)
			{
				inventory.setItem(i, clazz.getItem());
				i++;
			}
		}

		ItemStack paidItem = new ItemStack(Material.PAPER);
		ItemMeta meta = paidItem.getItemMeta();
		meta.setDisplayName(FormatUtil.format("&6&lFree Classes"));
		paidItem.setItemMeta(meta);

		inventory.setItem(FREE_INDEX, paidItem);

		player.openInventory(inventory);
	}
}
