/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.powers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ItemUtil;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;

/**
 * @author dmulloy2
 */

public abstract class Power implements ConfigurationSerializable, Listener
{
	protected @Getter String name;
	protected @Getter String display;
	protected @Getter ItemStack item;
	protected @Getter List<String> lore;
	protected MemorySection section;

	protected final ShadowMagic plugin;
	public Power(ShadowMagic plugin, String name, MemorySection section)
	{
		this.plugin = plugin;
		this.name = name;
		this.display = section.getString("display");
		this.lore = section.getStringList("lore");
		this.item = loadItem(section);
		this.section = section;
		this.load();
	}

	private final ItemStack loadItem(MemorySection section)
	{
		// Item
		String item = section.getString("item");
		ItemStack stack = ItemUtil.readItem(item);

		// Display Name
		String display = FormatUtil.format(section.getString("display"));

		// Lore
		List<String> lore = new ArrayList<String>();
		for (String s : section.getStringList("lore"))
			lore.add(FormatUtil.format(s));

		// Apply
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(display);
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}

	// Load
	protected abstract void load();

	public abstract void activate(Player player);

	public abstract Map<String, Object> serialize();

	@Override
	public String toString()
	{
		return "Power[name=" + name + "]";
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Power)
		{
			Power that = (Power) obj;
			return this.name.equals(that.name);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}
