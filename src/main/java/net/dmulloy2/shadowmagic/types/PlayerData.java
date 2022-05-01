/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.types;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.gui.GUI;
import net.dmulloy2.shadowmagic.powers.Power;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import lombok.Data;

/**
 * @author dmulloy2
 */

@Data
public class PlayerData implements ConfigurationSerializable
{
	// ---- Rank
	private String clazzName;
	private transient Class clazz;

	// ---- Powers
	private transient Power activePower;
	private Map<String, Long> cooldowns = new HashMap<>();

	// ---- Chests
	private transient Chest openChest;
	private Map<Integer, Chest> chests = new HashMap<>();

	// ---- GUI
	private transient GUI openGUI;

	// ---- UUID Stuff
	private String lastKnownBy;

	public PlayerData() { }

	public PlayerData(Map<String, Object> args)
	{
		for (Entry<String, Object> entry : args.entrySet())
		{
			try
			{
				for (Field field : getClass().getDeclaredFields())
				{
					if (field.getName().equals(entry.getKey()))
					{
						boolean accessible = field.isAccessible();

						field.setAccessible(true);

						field.set(this, entry.getValue());

						field.setAccessible(accessible);
					}
				}
			} catch (Throwable ex) { }
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Map<String, Object> serialize()
	{
		Map<String, Object> data = new HashMap<String, Object>();

		for (Field field : getClass().getDeclaredFields())
		{
			if (Modifier.isTransient(field.getModifiers()))
				continue;

			try
			{
				boolean accessible = field.isAccessible();

				field.setAccessible(true);

				if (field.getType().equals(Integer.TYPE))
				{
					if (field.getInt(this) != 0)
						data.put(field.getName(), field.getInt(this));
				}
				else if (field.getType().equals(Long.TYPE))
				{
					if (field.getLong(this) != 0)
						data.put(field.getName(), field.getLong(this));
				}
				else if (field.getType().equals(Boolean.TYPE))
				{
					if (field.getBoolean(this))
						data.put(field.getName(), field.getBoolean(this));
				}
				else if (field.getType().isAssignableFrom(Collection.class))
				{
					if (! ((Collection) field.get(this)).isEmpty())
						data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(String.class))
				{
					if (((String) field.get(this)) != null)
						data.put(field.getName(), field.get(this));
				}
				else if (field.getType().isAssignableFrom(Map.class))
				{
					if (! ((Map) field.get(this)).isEmpty())
						data.put(field.getName(), field.get(this));
				}
				else
				{
					if (field.get(this) != null)
						data.put(field.getName(), field.get(this));
				}

				field.setAccessible(accessible);
			} catch (Throwable ex) { }
		}

		return data;
	}

	@Deprecated
	public final Class getClazz()
	{
		return null;
	}

	public final Class getClazz(ShadowMagic plugin)
	{
		if (clazz == null && clazzName != null)
			clazz = plugin.getClassHandler().getClass(clazzName);

		return clazz;
	}
}
