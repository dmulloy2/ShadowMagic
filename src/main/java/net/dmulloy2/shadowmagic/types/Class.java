/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.types;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.powers.Power;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ItemUtil;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dmulloy2
 */

@Data
@AllArgsConstructor
public class Class implements ConfigurationSerializable
{
	private int cost;
	private String name;
	private String command;
	private ItemStack item;
	private String display;
	private String gmGroup;
	private List<String> lore;
	private List<Power> powers;

	public Class(ShadowMagic plugin, MemorySection section)
	{
		this.item = loadItem(section);
		this.name = section.getName();
		this.cost = section.getInt("cost");
		this.display = section.getString("display");
		this.lore = section.getStringList("lore");
		this.powers = loadPowers(plugin, section);
		this.gmGroup = section.getString("gmGroup", name);
		this.command = section.getString("command", "manuadd %p %g");
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

	private final List<Power> loadPowers(ShadowMagic plugin, MemorySection section)
	{
		List<Power> ret = new ArrayList<Power>();

		for (String name : section.getStringList("powers"))
		{
			Power power = plugin.getPowerHandler().getPower(name);
			if (power != null)
				ret.add(power);
		}

		return ret;
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> ret = new LinkedHashMap<>();

		ret.put("item", ItemUtil.serialize(item));
		ret.put("display", display);
		ret.put("lore", lore);
		ret.put("cost", cost);

		List<String> names = new ArrayList<String>();
		for (Power power : powers)
			names.add(power.getName());
		ret.put("powers", names);

		return ret;
	}

	@Override
	public String toString()
	{
		return "Class[name=" + name + ", cost=" + cost + "]";
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Class)
		{
			Class that = (Class) obj;
			return this.name.equals(that.name) && this.cost == that.cost;
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name, cost);
	}
}
