/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.handlers;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.types.Class;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import lombok.NonNull;

/**
 * @author dmulloy2
 */

public class ClassHandler implements Reloadable
{
	private File file;
	private FileConfiguration config;
	private Map<String, Class> classes;
	
	private final ShadowMagic plugin;
	public ClassHandler(ShadowMagic plugin)
	{
		this.plugin = plugin;
		this.reload();
	}

	private final void load()
	{
		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Loading classes...");
		
		this.file = new File(plugin.getDataFolder(), "classes.yml");
		if (! file.exists())
			plugin.saveResource("classes.yml", false);

		this.config = YamlConfiguration.loadConfiguration(file);

		if (! config.isSet("classes"))
		{
			plugin.getLogHandler().log(Level.WARNING, "No classes found!");
			return;
		}

		Map<String, Object> values = config.getConfigurationSection("classes").getValues(false);
		for (Entry<String, Object> entry : values.entrySet())
		{
			String name = entry.getKey();

			try
			{
				MemorySection section = (MemorySection) entry.getValue();

				Class clazz = new Class(plugin, section);
				classes.put(name.toLowerCase(), clazz);
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading class " + name));
			}
		}

		plugin.getLogHandler().log("{0} classes loaded! Took {1} ms!", classes.size(), System.currentTimeMillis() - start);
	}

	public final void save()
	{
		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Saving classes...");

		try
		{
			this.file = new File(plugin.getDataFolder(), "classes.yml");
			if (file.exists())
				file.delete();

			file.createNewFile();

			this.config = YamlConfiguration.loadConfiguration(file);

			for (Class clazz : classes.values())
			{
				config.createSection("classes." + clazz.getName(), clazz.serialize());
			}

			config.save(file);

			plugin.getLogHandler().log("Classes saved! Took {0} ms!", System.currentTimeMillis() - start);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving classes"));
		}
	}

	public final Class getClass(@NonNull String name)
	{
		return classes.get(name.toLowerCase());
	}

	public final Class getClass(ItemStack item)
	{
		for (Class clazz : classes.values())
		{
			if (clazz.getItem().equals(item))
				return clazz;
		}

		return null;
	}

	public Collection<Class> getClasses()
	{
		return classes.values();
	}

	@Override
	public void reload()
	{
		// ---- Clear Variables
		this.config = null;
		this.file = null;

		// ---- Init Variables
		this.classes = new LinkedHashMap<>();

		// ---- Load
		this.load();
	}
}
