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
import net.dmulloy2.shadowmagic.enchantments.Enchantment;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class EnchantmentHandler implements Reloadable
{
	private File file;
	private FileConfiguration config;
	private Map<Integer, Enchantment> enchantments;

	private final ShadowMagic plugin;

	public EnchantmentHandler(ShadowMagic plugin)
	{
		this.plugin = plugin;
		this.reload();
	}

	private final void load()
	{
		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Loading enchantments...");

		this.file = new File(plugin.getDataFolder(), "enchantments.yml");
		if (! file.exists())
			plugin.saveResource("enchantments.yml", false);

		this.config = YamlConfiguration.loadConfiguration(file);

		if (! config.isSet("enchantments"))
		{
			plugin.getLogHandler().log(Level.WARNING, "No enchantments found!");
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

		Map<String, Object> values = config.getConfigurationSection("enchantments").getValues(false);
		for (Entry<String, Object> entry : values.entrySet())
		{
			String name = entry.getKey();

			try
			{
				MemorySection section = (MemorySection) entry.getValue();

				// Class
				if (! section.isSet("class"))
					throw new Exception("Class not defined!");

				String path = section.getString("class");
				if (path.startsWith("."))
					path = "net.dmulloy2.necessities.enchantments" + path;

				// Construct
				Class<? extends Enchantment> clazz = cl.loadClass(path).asSubclass(Enchantment.class);
				Constructor<? extends Enchantment> constructor = clazz.getConstructor(ShadowMagic.class, String.class, 
						MemorySection.class);
				constructor.setAccessible(true);

				Enchantment enchantment = constructor.newInstance(plugin, name, section);
				enchantments.put(enchantment.getId(), enchantment);

				// Register listener
				plugin.getServer().getPluginManager().registerEvents(enchantment, plugin);
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading enchantment " + name));
			}
		}

		plugin.getLogHandler().log("{0} enchantments loaded! Took {1} ms!", enchantments.size(), System.currentTimeMillis() - start);
	}

	public final void save()
	{
		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Saving enchantments...");

		try
		{
			this.file = new File(plugin.getDataFolder(), "enchantments.yml");
			if (file.exists())
				file.delete();

			file.createNewFile();

			this.config = YamlConfiguration.loadConfiguration(file);

			for (Enchantment ench : enchantments.values())
			{
				config.createSection("enchantments." + ench.getName(), ench.serialize());
			}

			config.save(file);

			plugin.getLogHandler().log("Enchantments saved! Took {0} ms!", System.currentTimeMillis() - start);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving classes"));
		}
	}

	public final Enchantment getEnchantment(int id)
	{
		return enchantments.get(id);
	}

	public final Enchantment getEnchantment(String name)
	{
		for (Enchantment ench : enchantments.values())
		{
			if (ench.getName().equalsIgnoreCase(name) || ench.getDisplay().equalsIgnoreCase(name))
				return ench;
		}

		return null;
	}

	public final Enchantment getEnchantment(ItemStack icon)
	{
		for (Enchantment ench : enchantments.values())
		{
			if (ench.getItem().equals(icon))
				return ench;
		}

		return null;
	}

	public final Collection<Enchantment> getEnchantments()
	{
		return enchantments.values();
	}

	@Override
	public void reload()
	{
		// ---- Clear Variables
		this.config = null;
		this.file = null;

		// ---- Init Variables
		this.enchantments = new LinkedHashMap<>();

		// ---- Load
		this.load();
	}
}
