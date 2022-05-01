/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.enchantments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.types.Numeral;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.TimeUtil;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class SoulBind extends Enchantment
{
	private transient Map<String, ItemStack[]> items;
	private String message;

	public SoulBind(ShadowMagic plugin, String name, MemorySection section)
	{
		super(plugin, name, section);
		this.items = new HashMap<>();
	}

	@Override
	protected void load()
	{
		this.message = section.getString("message");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		List<ItemStack> keep = new ArrayList<>();
		
		Iterator<ItemStack> dropIter = event.getDrops().iterator();
		while (dropIter.hasNext())
		{
			ItemStack item = dropIter.next();
			if (item.hasItemMeta())
			{
				ItemMeta meta = item.getItemMeta();
				if (meta.hasLore())
				{
					int index = 0;
					boolean changes = false;

					List<String> lore = meta.getLore();
					Iterator<String> loreIter = lore.iterator();
					while (loreIter.hasNext())
					{
						String line = loreIter.next();
						if (line.matches(getDisplay() + ".*"))
						{
							changes = true;

							String[] split = line.split(" ");
							int level = Numeral.toNumber(split[split.length - 1]);
							if (level > 0)
							{
								// Remove drop
								dropIter.remove();

								// Update lore
								line = getDisplay() + " " + Numeral.getNumeral(level - 1);
								lore.set(index, line);

								// Add to list
								keep.add(item);
							}
							else
							{
								// Remove lore
								loreIter.remove();
								continue;
							}
						}

						index++;
					}

					if (changes)
					{
						meta.setLore(lore);
						item.setItemMeta(meta);
					}
				}
			}
		}

		if (! keep.isEmpty())
		{
			items.put(player.getName(), keep.toArray(new ItemStack[0]));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		final Player player = event.getPlayer();
		if (items.containsKey(player.getName()))
		{
			final ItemStack[] keep = items.get(player.getName());
			items.remove(player.getName());

			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					InventoryUtil.giveItems(player, keep);
					player.sendMessage(formatMessage(message));
				}
			}.runTaskLater(plugin, TimeUtil.toTicks(3));
		}
	}

	private final String formatMessage(String message)
	{
		return FormatUtil.format(message);
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> ret = new LinkedHashMap<String, Object>();

		ret.put("class", ".SoulBind");
		ret.put("id", id);
		ret.put("item", ItemUtil.serialize(item));
		ret.put("display", display);
		ret.put("lore", lore);
		ret.put("message", message);

		return ret;
	}
}
