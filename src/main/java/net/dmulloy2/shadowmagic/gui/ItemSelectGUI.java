/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.gui;

import net.dmulloy2.shadowmagic.ShadowMagic;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class ItemSelectGUI extends GUI
{
	private static final String NAME = "Item Selection";
	private static final int SIZE = 54;
	
	public ItemSelectGUI(ShadowMagic plugin, Player player)
	{
		super(plugin, player);
	}

	@Override
	public void open()
	{
		Inventory inventory = Bukkit.createInventory(player, SIZE, NAME);

		// ---- Swords
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		inventory.setItem(1, sword);

		sword = new ItemStack(Material.IRON_SWORD);
		inventory.setItem(3, sword);

		sword = new ItemStack(Material.GOLD_SWORD);
		inventory.setItem(5, sword);

		sword = new ItemStack(Material.STONE_SWORD);
		inventory.setItem(7, sword);

		// ---- Pickaxes
		ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
		inventory.setItem(10, pick);

		pick = new ItemStack(Material.IRON_PICKAXE);
		inventory.setItem(12, pick);

		pick = new ItemStack(Material.GOLD_PICKAXE);
		inventory.setItem(14, pick);

		pick = new ItemStack(Material.STONE_PICKAXE);
		inventory.setItem(16, pick);

		// ---- Axes
		ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
		inventory.setItem(19, axe);

		axe = new ItemStack(Material.IRON_AXE);
		inventory.setItem(21, axe);

		axe = new ItemStack(Material.GOLD_AXE);
		inventory.setItem(23, axe);

		axe = new ItemStack(Material.STONE_AXE);
		inventory.setItem(25, axe);

		ItemStack bow = new ItemStack(Material.BOW);
		inventory.setItem(40, bow);

		player.openInventory(inventory);
	}
}
