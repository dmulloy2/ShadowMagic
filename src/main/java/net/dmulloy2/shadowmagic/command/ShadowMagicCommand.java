/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.command;

import net.dmulloy2.commands.Command;
import net.dmulloy2.shadowmagic.ShadowMagic;

/**
 * @author dmulloy2
 */

public abstract class ShadowMagicCommand extends Command
{
	protected final ShadowMagic plugin;

	public ShadowMagicCommand(ShadowMagic plugin)
	{
		super(plugin);
		this.plugin = plugin;
	}
}
