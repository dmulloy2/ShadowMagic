/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.types;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;

/**
 * @author dmulloy2
 */

@Getter
@SerializableAs("net.dmulloy2.Chest")
public class Chest implements ConfigurationSerializable
{
	private static final String NAME = "Virtual Chest #";
	private static final int SIZE = 54;

	private int number;
	private List<ItemStack> items;

	private transient Player owner;
	private transient PlayerData data;
	private transient Inventory inventory;

	public Chest(int number)
	{
		this.number = number;
		this.items = new ArrayList<>();
	}

	public Chest(int number, List<ItemStack> items)
	{
		this(number);
		this.items = items;
	}

	public void open(Player player, PlayerData data) 
	{
		this.owner = player;
		this.data = data;

		this.inventory = Bukkit.createInventory(player, SIZE, NAME + number);
		inventory.setContents(items.toArray(new ItemStack[0]));
		player.openInventory(inventory);
	}

	public void close()
	{
		items = new ArrayList<>();
		for (ItemStack item : inventory.getContents())
		{
			if (item != null && item.getType() != Material.AIR)
				items.add(item);
		}

		data.getChests().put(number, this);
		data.setOpenChest(null);
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> data = new LinkedHashMap<>();

		data.put("number", number);
		data.put("items", items);

		return data;
	}

	@SuppressWarnings("unchecked")
	public static Chest deserialize(Map<String, Object> args)
	{
		return new Chest((int) args.get("number"), (List<ItemStack>) args.get("items"));
	}
}
