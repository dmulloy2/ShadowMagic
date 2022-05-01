/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.gui;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.powers.Power;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * @author dmulloy2
 */

public class PowerSignGUI extends GUI
{
	private static final String NAME = "Powers";
	private static final int SIZE = 54;

	public PowerSignGUI(ShadowMagic plugin, Player player)
	{
		super(plugin, player);
	}

	@Override
	public void open()
	{
		Inventory inventory = Bukkit.createInventory(player, SIZE, NAME);

		int i = 0;
		for (Power power : plugin.getPowerHandler().getPowers())
		{
			inventory.setItem(i, power.getItem());
			i++;
		}

		player.openInventory(inventory);
	}
}
