/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.handlers;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.powers.Power;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class PowerHandler implements Reloadable
{
	private File file;
	private FileConfiguration config;
	private Map<String, Power> powers;

	private final ShadowMagic plugin;

	public PowerHandler(ShadowMagic plugin)
	{
		this.plugin = plugin;
		this.reload();
	}

	private final void load()
	{
		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Loading powers...");

		this.file = new File(plugin.getDataFolder(), "powers.yml");
		if (! file.exists())
			plugin.saveResource("powers.yml", false);

		this.config = YamlConfiguration.loadConfiguration(file);

		if (! config.isSet("powers"))
		{
			plugin.getLogHandler().log(Level.WARNING, "No powers found!");
			return;
		}

		List<File> jarList = new ArrayList<File>();
		for (File file : plugin.getDataFolder().listFiles())
		{
			if (file.getName().endsWith(".jar"))
				jarList.add(file);
		}

		URL[] urls = new URL[jarList.size() + 1];
		ClassLoader cl = plugin.getClazzLoader();

		try
		{
			urls[0] = plugin.getDataFolder().toURI().toURL();
			for (int i = 1; i <= jarList.size(); i++)
			{
				urls[i] = jarList.get(i - 1).toURI().toURL();
			}

			cl = new URLClassLoader(urls, cl);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, "Failed to load jars!");
		}

		Map<String, Object> values = config.getConfigurationSection("powers").getValues(false);
		for (Entry<String, Object> entry : values.entrySet())
		{
			String name = entry.getKey();

			try
			{
				MemorySection section = (MemorySection) entry.getValue();

				if (! section.isSet("class"))
					throw new Exception("Class not defined!");

				String path = section.getString("class");
				if (path.startsWith("."))
					path = "net.dmulloy2.necessities.powers" + path;

				Class<? extends Power> clazz = cl.loadClass(path).asSubclass(Power.class);
				Constructor<? extends Power> constructor = clazz.getConstructor(ShadowMagic.class, String.class, MemorySection.class);
				constructor.setAccessible(true);

				Power power = constructor.newInstance(plugin, name, section);
				powers.put(name.toLowerCase(), power);

				// Register listener
				plugin.getServer().getPluginManager().registerEvents(power, plugin);
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading power " + name));
			}
		}

		plugin.getLogHandler().log("{0} powers loaded! Took {1} ms!", powers.size(), System.currentTimeMillis() - start);
	}

	public final void save()
	{
		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Saving powers...");

		try
		{
			this.file = new File(plugin.getDataFolder(), "powers.yml");
			if (file.exists())
				file.delete();

			file.createNewFile();

			this.config = YamlConfiguration.loadConfiguration(file);

			for (Power power : powers.values())
			{
				config.createSection("powers." + power.getName(), power.serialize());
			}

			config.save(file);

			plugin.getLogHandler().log("Powers saved! Took {0} ms!",  System.currentTimeMillis() - start);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving powers"));
		}
	}

	public final Power getPower(String name)
	{
		return powers.get(name.toLowerCase());
	}

	public Power getPower(ItemStack item)
	{
		for (Power power : powers.values())
		{
			if (power.getItem().equals(item))
				return power;
		}

		return null;
	}

	public final Collection<Power> getPowers()
	{
		return powers.values();
	}

	@Override
	public void reload()
	{
		// ---- Clear Variables
		this.config = null;
		this.file = null;

		// ---- Init Variables
		this.powers = new LinkedHashMap<>();

		// ---- Load
		this.load();
	}
}
