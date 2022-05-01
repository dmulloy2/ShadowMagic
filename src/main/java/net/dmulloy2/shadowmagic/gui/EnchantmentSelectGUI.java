/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.gui;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.enchantments.Enchantment;
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

public class EnchantmentSelectGUI extends GUI
{
	private static final String NAME = "Enchantment Selection";
	private static final int DONE_INDEX = 53;
	private static final int SIZE = 54;
	
	private final @Getter Material material;
	public EnchantmentSelectGUI(ShadowMagic plugin, Player player, Material material)
	{
		super(plugin, player);
		this.material = material;
	}

	@Override
	public void open()
	{
		Inventory inventory = Bukkit.createInventory(player, SIZE, NAME);

		ItemStack up = new ItemStack(Material.PAPER);
		ItemMeta uMeta = up.getItemMeta();
		uMeta.setDisplayName(FormatUtil.format("&f&lUp"));
		up.setItemMeta(uMeta);

		ItemStack gold = new ItemStack(Material.GOLD_INGOT);
		ItemMeta gMeta = gold.getItemMeta();
		gMeta.setDisplayName(FormatUtil.format("&f&l0"));
		gold.setItemMeta(gMeta);

		ItemStack down = new ItemStack(Material.PAPER);
		ItemMeta dMeta = down.getItemMeta();
		dMeta.setDisplayName(FormatUtil.format("&f&lDown"));
		down.setItemMeta(dMeta);

		List<Enchantment> enchantments = new ArrayList<>(plugin.getEnchantmentHandler().getEnchantments());

		for (int i = 0; i < 10 && i < enchantments.size(); i++)
		{
			int index = Integer.valueOf(i).intValue();
			Enchantment ench = enchantments.get(i);
			inventory.setItem(index, ench.getItem());
			inventory.setItem(index + 9, up);
			inventory.setItem(index + 18, gold);
			inventory.setItem(index + 27, down);
		}

		ItemStack paidItem = new ItemStack(Material.PAPER);
		ItemMeta meta = paidItem.getItemMeta();
		meta.setDisplayName(FormatUtil.format("&6&lDone"));
		paidItem.setItemMeta(meta);

		inventory.setItem(DONE_INDEX, paidItem);

		player.openInventory(inventory);
	}
}
