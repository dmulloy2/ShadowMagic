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

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author dmulloy2
 */

public class ActivatedPotionEffect extends Power
{
	private PotionEffectType type;
	private String message;
	private int duration;
	private int cooldown;
	private int potency;

	public ActivatedPotionEffect(ShadowMagic plugin, String name, MemorySection section)
	{
		super(plugin, name, section);
	}

	@Override
	protected void load()
	{
		this.type = PotionEffectType.getByName(section.getString("type").toUpperCase());
		this.message = section.getString("message");
		this.duration = section.getInt("duration");
		this.cooldown = section.getInt("cooldown");
		this.potency = section.getInt("potency");
	}

	@Override
	public void activate(Player player)
	{
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.getCooldowns().containsKey(name))
		{
			if (data.getCooldowns().get(name) > System.currentTimeMillis())
				data.getCooldowns().remove(name);

			if (data.getCooldowns().containsKey(name))
			{
				long remaining = System.currentTimeMillis() - data.getCooldowns().get(name);
				player.sendMessage(formatError("Cooling down for &c{0} &4seconds!", TimeUtil.toSeconds(remaining)));
			}
		}

		player.addPotionEffect(new PotionEffect(type, duration * 20, potency, false));
		player.sendMessage(formatMessage(message));

		data.setActivePower(this);
		data.getCooldowns().put(name, System.currentTimeMillis() + TimeUtil.toTicks(cooldown));
		player.sendMessage(FormatUtil.format("&3You have activated " + name));
	}

	protected final String formatMessage(String string)
	{
		return FormatUtil.format("&3" + string);
	}

	protected final String formatError(String string, int seconds)
	{
		return FormatUtil.format("&cError: &4" + string, seconds);
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> ret = new LinkedHashMap<String, Object>();

		ret.put("class", ".ActivatedPotionEffect");
		ret.put("item", ItemUtil.serialize(item));
		ret.put("display", display);
		ret.put("lore", lore);
		ret.put("type", type.getName());
		ret.put("message", message);
		ret.put("duration", duration);
		ret.put("cooldown", cooldown);
		ret.put("potency", potency);

		return ret;
	}
}
