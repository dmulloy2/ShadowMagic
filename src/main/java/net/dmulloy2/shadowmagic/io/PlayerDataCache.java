/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import net.dmulloy2.io.FileSerialization;
import net.dmulloy2.io.IOUtil;
import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.types.PlayerData;
import net.dmulloy2.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class PlayerDataCache
{
	private final File folder;
	private final String extension = ".dat";
	private final String folderName = "players";

	private final ConcurrentMap<String, PlayerData> cache;

	private final ShadowMagic plugin;
	public PlayerDataCache(ShadowMagic plugin)
	{
		this.folder = new File(plugin.getDataFolder(), folderName);
		if (! folder.exists())
			folder.mkdirs();

		this.cache = new ConcurrentHashMap<String, PlayerData>(64, 0.75F, 64);
		this.plugin = plugin;
	}

	// ---- Data Getters

	private final PlayerData getData(String key)
	{
		// Check cache first
		PlayerData data = cache.get(key);
		if (data == null)
		{
			// Attempt to load it
			File file = new File(folder, getFileName(key));
			if (file.exists())
			{
				data = loadData(file, true);
				if (data == null)
				{
					// Corrupt data :(
					if (! file.renameTo(new File(folder, file.getName() + "_bad")))
						file.delete();
					return null;
				}

				// Cache it
				cache.put(key, data);
			}
		}

		return data;
	}

	public final PlayerData getData(Player player)
	{
		PlayerData data = getData(getKey(player));

		// Online players always have data
		if (data == null)
			data = newData(player);

		// Update last known by
		data.setLastKnownBy(player.getName());

		// Return
		return data;
	}

	public final PlayerData getData(OfflinePlayer player)
	{
		// Slightly different handling for Players
		if (player.isOnline())
			return getData(player.getPlayer());

		// Attempt to get by name
		return getData(getKey(player));
	}

	// ---- Data Management

	public final PlayerData newData(String key)
	{
		// Construct
		PlayerData data = new PlayerData();

		// Cache and return
		cache.put(key, data);
		return data;
	}

	public final PlayerData newData(Player player)
	{
		return newData(getKey(player));
	}

	private final PlayerData loadData(File file, boolean exists)
	{
		try
		{
			return FileSerialization.load(file, PlayerData.class, exists);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading data from {0}", file.getName()));
			return null;
		}
	}

	public final void save()
	{
		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Saving players to disk...");

		for (Entry<String, PlayerData> entry : getAllLoadedPlayerData().entrySet())
		{
			try
			{
				File file = new File(folder, getFileName(entry.getKey()));
				FileSerialization.save(entry.getValue(), file);
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving data for {0}", entry.getKey()));
			}
		}

		plugin.getLogHandler().log("Players saved. Took {0} ms.", System.currentTimeMillis() - start);
	}

	public final void cleanupData()
	{
		// Get all online players into a  list
		List<String> online = new ArrayList<>();
		for (Player player : Util.getOnlinePlayers())
			online.add(getKey(player));

		// Actually cleanup the data
		for (String key : getAllLoadedPlayerData().keySet())
			if (! online.contains(key))
				cache.remove(key);

		online.clear();
	}

	// ---- Mass Getters

	public final Map<String, PlayerData> getAllLoadedPlayerData()
	{
		return Collections.unmodifiableMap(cache);
	}

	public final Map<String, PlayerData> getAllPlayerData()
	{
		Map<String, PlayerData> data = new HashMap<String, PlayerData>();
		data.putAll(cache);

		File[] files = folder.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				return file.getName().endsWith(extension);
			}
		});

		for (File file : files)
		{
			String fileName = IOUtil.trimFileExtension(file, extension);
			if (isFileLoaded(fileName))
				continue;

			PlayerData loaded = loadData(file, true);
			if (loaded != null)
				data.put(fileName, loaded);
		}

		return Collections.unmodifiableMap(data);
	}

	// ---- Util

	private final String getKey(OfflinePlayer player)
	{
		return player.getUniqueId().toString();
	}

	private final String getFileName(String key)
	{
		return key + extension;
	}

	private final boolean isFileLoaded(String fileName)
	{
		return cache.keySet().contains(fileName);
	}
}
