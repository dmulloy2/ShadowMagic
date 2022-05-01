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

import lombok.Getter;

/**
 * @author dmulloy2
 */

public class ConfirmClassGUI extends GUI
{
	private static final String NAME = "Confirm";
	private static final int CONFIRM_INDEX = 10;
	private static final int DENY_INDEX = 16;
	private static final int SIZE = 27;
	
	private final @Getter Class clazz;
	public ConfirmClassGUI(ShadowMagic plugin, Player player, Class clazz)
	{
		super(plugin, player);
		this.clazz = clazz;
	}

	@Override
	public void open()
	{
		Inventory inventory = Bukkit.createInventory(player, SIZE, NAME);

		ItemStack confirm = new ItemStack(Material.WOOL, 1, (short) 5);
		ItemMeta cMeta = confirm.getItemMeta();
		cMeta.setDisplayName(FormatUtil.format("&a&lConfirm"));
		confirm.setItemMeta(cMeta);

		inventory.setItem(CONFIRM_INDEX, confirm);

		ItemStack deny = new ItemStack(Material.WOOL, 1, (short) 14);
		ItemMeta dMeta = deny.getItemMeta();
		dMeta.setDisplayName(FormatUtil.format("&c&lDeny"));
		deny.setItemMeta(dMeta);

		inventory.setItem(DENY_INDEX, deny);

		player.openInventory(inventory);
	}
}
