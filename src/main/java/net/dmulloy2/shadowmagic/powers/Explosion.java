/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.powers;

import java.util.LinkedHashMap;
import java.util.Map;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.types.PlayerData;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.TimeUtil;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class Explosion extends Power
{
	private String message;
	private int powerDuration;
	private int cooldown;
	private int damage;
	private int chance;
	private int duration;

	public Explosion(ShadowMagic plugin, String name, MemorySection section)
	{
		super(plugin, name, section);
	}

	@Override
	protected void load()
	{
		this.message = section.getString("message");
		this.powerDuration = section.getInt("powerDuration");
		this.cooldown = section.getInt("cooldown");
		this.damage = section.getInt("damage");
		this.chance = section.getInt("chance");
		this.duration = section.getInt("duration");
	}

	@Override
	public void activate(final Player player)
	{
		final PlayerData data = plugin.getPlayerDataCache().getData(player);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				data.setActivePower(null);
				data.getCooldowns().put(name, System.currentTimeMillis() + TimeUtil.toTicks(cooldown));

				player.sendMessage(FormatUtil.format("&3{0} has worn off!", name));
			}
		}.runTaskLater(plugin, TimeUtil.toTicks(powerDuration));

		data.setActivePower(this);
		player.sendMessage(FormatUtil.format("&3You have activated {0}!", name));
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
				PlayerData data = plugin.getPlayerDataCache().getData(player);
				Power active = data.getActivePower();
				if (active != null && active.equals(this))
				{
					if (Util.random(chance) == 0)
					{
						opponent.getWorld().createExplosion(opponent.getLocation(), damage);
						player.sendMessage(formatMessage(message, opponent));
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

		ret.put("class", ".Explosion");
		ret.put("item", ItemUtil.serialize(item));
		ret.put("display", display);
		ret.put("lore", lore);
		ret.put("message", message);
		ret.put("powerDuration", powerDuration);
		ret.put("cooldown", cooldown);
		ret.put("damage", damage);
		ret.put("chance", chance);
		ret.put("duration", duration);

		return ret;
	}
}
