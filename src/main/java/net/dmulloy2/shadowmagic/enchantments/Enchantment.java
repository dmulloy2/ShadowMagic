/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.enchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ItemUtil;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;

/**
 * @author dmulloy2
 */

public abstract class Enchantment implements ConfigurationSerializable, Listener, Cloneable
{
	protected @Getter int id;
	protected @Getter String name;
	protected String display;
	protected @Getter ItemStack item;
	protected @Getter List<String> lore;
	protected MemorySection section;

	protected final ShadowMagic plugin;
	public Enchantment(ShadowMagic plugin, String name, MemorySection section)
	{
		this.plugin = plugin;
		this.name = name;
		this.section = section;
		this.id = section.getInt("id");
		this.display = section.getString("display");
		this.lore = section.getStringList("lore");
		this.item = loadItem(section);
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

	public final String getDisplay()
	{
		return FormatUtil.format(display);
	}

	protected abstract void load();

	public abstract Map<String, Object> serialize();

	@Override
	public String toString()
	{
		return "Enchantment[name=" + name + ", id=" + id + "]";
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Enchantment)
		{
			Enchantment that = (Enchantment) obj;
			return this.name.equals(that.name) && this.id == that.id;
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name, id);
	}
}
