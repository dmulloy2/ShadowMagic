/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.enchantments;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.types.Numeral;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author dmulloy2
 */

public class TriggeredPotionEffect extends Enchantment
{
	private PotionEffectType type;
	private String message;
	private int duration;
	private int potency;
	private int chance;

	public TriggeredPotionEffect(ShadowMagic plugin, String name, MemorySection section)
	{
		super(plugin, name, section);
	}

	@Override
	protected void load()
	{
		this.type = PotionEffectType.getByName(section.getString("type").toUpperCase());
		this.message = section.getString("message");
		this.duration = section.getInt("duration");
		this.potency = section.getInt("potency");
		this.chance = section.getInt("chance");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event)
	{
		Entity damager = event.getDamager();
		if (damager instanceof Player)
		{
			Player player = (Player) damager;
			Entity entity = event.getEntity();
			if (entity instanceof LivingEntity)
			{
				LivingEntity opponent = (LivingEntity) entity;
				ItemStack item = player.getInventory().getItemInMainHand();
				if (item.hasItemMeta())
				{
					ItemMeta meta = item.getItemMeta();
					if (meta.hasLore())
					{
						Iterator<String> iter = meta.getLore().iterator();
						while (iter.hasNext())
						{
							String line = iter.next();
							if (line.matches(getDisplay() + ".*"))
							{
								String[] split = line.split(" ");
								int level = Numeral.toNumber(split[split.length - 1]);
								if (Util.random(chance - level) == 0)
								{
									opponent.addPotionEffect(new PotionEffect(type, (duration + level) * 20, potency, false));
									player.sendMessage(formatMessage(message, opponent));
								}
							}
						}
					}
				}
			}
		}
	}

	private final String formatMessage(String message, LivingEntity opponent)
	{
		String name = FormatUtil.getFriendlyName(opponent.getType());
		if (opponent instanceof Player)
			name = ((Player) opponent).getName();

		message = message.replaceAll("%p", name);
		return FormatUtil.format(message);
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> ret = new LinkedHashMap<String, Object>();

		ret.put("class", ".TriggeredPotionEffect");
		ret.put("id", id);
		ret.put("item", ItemUtil.serialize(item));
		ret.put("display", display);
		ret.put("lore", lore);
		ret.put("type", type.getName());
		ret.put("message", message);
		ret.put("duration", duration);
		ret.put("potency", potency);
		ret.put("chance", chance);

		return ret;
	}
}
