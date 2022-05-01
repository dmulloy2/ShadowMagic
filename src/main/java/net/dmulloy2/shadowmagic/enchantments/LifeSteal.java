/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.enchantments;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.types.Numeral;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author dmulloy2
 */

public class LifeSteal extends Enchantment
{
	private String message;
	private int chance;
	private int hearts;

	public LifeSteal(ShadowMagic plugin, String name, MemorySection section)
	{
		super(plugin, name, section);
	}

	@Override
	protected void load()
	{
		this.message = section.getString("message");
		this.chance = section.getInt("chance");
		this.hearts = section.getInt("hearts");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event)
	{
		Entity damager = event.getDamager();
		if (damager instanceof Player)
		{
			Player player = (Player) damager;
			Entity entity = event.getEntity();
			if (entity instanceof Player)
			{
				Player steal = (Player) entity;
				ItemStack item = player.getInventory().getItemInMainHand();
				if (item.hasItemMeta())
				{
					ItemMeta meta = item.getItemMeta();
					if (meta.hasLore())
					{
						List<String> lore = meta.getLore();
						for (int i = 0; i < lore.size(); i++)
						{
							String line = lore.get(i);
							if (line.matches(getDisplay() + ".*"))
							{
								String[] split = line.split(" ");
								int level = Numeral.toNumber(split[split.length - 1]);
								if (Util.random(chance - level) == 0)
								{
									int hearts = (this.hearts + level) * 2;
									steal.setHealth(Math.max(steal.getHealth() - hearts, 0));
									player.setHealth(Math.min(steal.getHealth() + hearts, 20));
									player.sendMessage(formatMessage(message, steal, hearts));
								}
							}
						}
					}
				}
			}
		}
	}

	private final String formatMessage(String message, Player steal, int hearts)
	{
		message = message.replaceAll("%p", steal.getName());
		message = message.replaceAll("%h", (hearts / 2) + "");
		return FormatUtil.format(message);
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> ret = new LinkedHashMap<String, Object>();

		ret.put("class", ".LifeSteal");
		ret.put("id", id);
		ret.put("item", ItemUtil.serialize(item));
		ret.put("display", display);
		ret.put("lore", lore);
		ret.put("message", message);
		ret.put("hearts", hearts);
		ret.put("chance", chance);

		return ret;
	}
}
