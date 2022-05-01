/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.commands.CmdHelp;
import net.dmulloy2.commands.CmdReload;
import net.dmulloy2.handlers.CommandHandler;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.handlers.PermissionHandler;
import net.dmulloy2.shadowmagic.command.CmdChest;
import net.dmulloy2.shadowmagic.command.CmdEnchant;
import net.dmulloy2.shadowmagic.command.CmdGiveChest;
import net.dmulloy2.shadowmagic.command.CmdLore;
import net.dmulloy2.shadowmagic.command.CmdName;
import net.dmulloy2.shadowmagic.command.CmdPower;
import net.dmulloy2.shadowmagic.command.CmdVersion;
import net.dmulloy2.shadowmagic.command.CmdXP;
import net.dmulloy2.shadowmagic.handlers.ClassHandler;
import net.dmulloy2.shadowmagic.handlers.EnchantmentHandler;
import net.dmulloy2.shadowmagic.handlers.PowerHandler;
import net.dmulloy2.shadowmagic.io.PlayerDataCache;
import net.dmulloy2.shadowmagic.listeners.PlayerListener;
import net.dmulloy2.shadowmagic.types.LogFormatter;
import net.dmulloy2.shadowmagic.types.Permission;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.FormatUtil;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;

/**
 * @author dmulloy2
 */

public class ShadowMagic extends SwornPlugin
{
	private @Getter EnchantmentHandler enchantmentHandler;
	private @Getter PermissionHandler permissionHandler;
	private @Getter CommandHandler commandHandler;
	private @Getter PowerHandler powerHandler;
	private @Getter ClassHandler classHandler;
	private @Getter LogHandler logHandler;

	private @Getter PlayerDataCache playerDataCache;
	private @Getter Economy economy;

	private Logger fileLogger;
	private List<Listener> listeners;

	private @Getter String prefix = FormatUtil.format("&3[&eSwornMagic&3]&e ");

	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();

		// Configuration
		saveDefaultConfig();
		reloadConfig();

		// Handlers
		logHandler = new LogHandler(this);
		powerHandler = new PowerHandler(this);
		classHandler = new ClassHandler(this);
		commandHandler = new CommandHandler(this);
		permissionHandler = new PermissionHandler(this);
		enchantmentHandler = new EnchantmentHandler(this);

		// Logger
		setupFileLogger();

		// Integration
		setupVaultIntegration();

		props().setReloadPerm(Permission.CMD_RELOAD);

		// Prefixed Commands
		commandHandler.setCommandPrefix("necessities");
		commandHandler.registerPrefixedCommand(new CmdChest(this));
		commandHandler.registerPrefixedCommand(new CmdEnchant(this));
		commandHandler.registerPrefixedCommand(new CmdGiveChest(this));
		commandHandler.registerPrefixedCommand(new CmdHelp(this));
		commandHandler.registerPrefixedCommand(new CmdLore(this));
		commandHandler.registerPrefixedCommand(new CmdName(this));
		commandHandler.registerPrefixedCommand(new CmdReload(this));
		commandHandler.registerPrefixedCommand(new CmdVersion(this));
		commandHandler.registerPrefixedCommand(new CmdXP(this));

		// Non-Prefixed Commands
		commandHandler.registerCommand(new CmdLore(this));
		commandHandler.registerCommand(new CmdName(this));
		commandHandler.registerCommand(new CmdPower(this));

		// Listeners
		listeners = new ArrayList<Listener>();
		registerListener(new PlayerListener(this));

		// Player Data
		playerDataCache = new PlayerDataCache(this);

		if (getConfig().getBoolean("autoSave.enabled"))
		{
			int interval = 20 * 60 * getConfig().getInt("autoSave.interval");

			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					// Save and cleanup
					playerDataCache.save();
					playerDataCache.cleanupData();
				}
			}.runTaskTimerAsynchronously(this, interval, interval);
		}

		logHandler.log("{0} has been enabled ({1} ms)", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	@Override
	public void onDisable()
	{
		long start = System.currentTimeMillis();

		// Cancel tasks and services
		getServer().getServicesManager().unregisterAll(this);
		getServer().getScheduler().cancelTasks(this);

		// Save data
		classHandler.save();
		powerHandler.save();
		playerDataCache.save();
		enchantmentHandler.save();

		logHandler.log("{0} has been disabled ({1} ms)", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	private final void setupFileLogger()
	{
		fileLogger = Logger.getLogger("SwornMagic");
		
		try
		{
			Handler fileHandler = new FileHandler(new File(getDataFolder(), "log.log").getPath(), true);
			fileHandler.setFormatter(new LogFormatter());
			for (Handler h : fileLogger.getHandlers())
				fileLogger.removeHandler(h);
			fileLogger.setUseParentHandlers(false);
			fileLogger.addHandler(fileHandler);
			fileLogger.setLevel(Level.INFO);
		} catch (Throwable ex) { }
	}

	public final void logToFile(Level level, String string, Object... args)
	{
		string = FormatUtil.format(string, args);
		fileLogger.log(level, string);
		logHandler.log(string);
	}

	private final void setupVaultIntegration()
	{
		PluginManager pm = getServer().getPluginManager();
		if (pm.isPluginEnabled("Vault"))
		{
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
			if (economyProvider != null)
			{
				economy = economyProvider.getProvider();
			}
		}

		if (economy != null)
		{
			logHandler.log("Economy integration through {0}", economy.getName());
		}
	}

	public final ClassLoader getClazzLoader()
	{
		return super.getClassLoader();
	}
	
	/**
	 * Reloads the Configuration
	 */
	@Override
	public final void reload()
	{
		// Config
		reloadConfig();

		// Listeners
		reloadListeners();

		// Handlers
		powerHandler.reload();
		classHandler.reload();
		enchantmentHandler.reload();
	}

	/**
	 * Registers a {@link Listener}
	 * 
	 * @param listener Listener to register
	 */
	private final void registerListener(Listener listener)
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(listener, this);
		listeners.add(listener);
	}

	/**
	 * Reloads the configuration settings of the listeners
	 */
	private final void reloadListeners()
	{
		for (Listener listener : listeners)
		{
			if (listener instanceof Reloadable)
			{
				((Reloadable) listener).reload();
			}
		}
	}
}
