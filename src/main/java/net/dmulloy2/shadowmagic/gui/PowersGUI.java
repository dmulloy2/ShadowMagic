/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.gui;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.powers.Power;
import net.dmulloy2.shadowmagic.types.Class;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * @author dmulloy2
 */

public class PowersGUI extends GUI
{
	private static final String NAME = "Powers";
	private static final int SIZE = 54;

	private Class clazz;
	public PowersGUI(ShadowMagic plugin, Player player, Class clazz)
	{
		super(plugin, player);
		this.clazz = clazz;
	}

	@Override
	public void open()
	{
		Inventory inventory = Bukkit.createInventory(player, SIZE, NAME);

		int i = 0;
		for (Power power : clazz.getPowers())
		{
			inventory.setItem(i, power.getItem());
			i++;
		}

		player.openInventory(inventory);
	}
}
