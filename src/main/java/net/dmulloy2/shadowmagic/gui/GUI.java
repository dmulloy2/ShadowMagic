/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.gui;

import net.dmulloy2.shadowmagic.ShadowMagic;

import org.bukkit.entity.Player;

import lombok.Getter;

/**
 * @author dmulloy2
 */

public abstract class GUI
{
	protected @Getter boolean closing;

	protected final Player player;
	protected final ShadowMagic plugin;
	public GUI(ShadowMagic plugin, Player player)
	{
		this.plugin = plugin;
		this.player = player;
		this.closing = false;
	}

	public abstract void open();

	public final void close()
	{
		closing = true;
		player.closeInventory();
	}
}
