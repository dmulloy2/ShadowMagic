/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.command;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdVersion extends ShadowMagicCommand
{
	public CmdVersion(ShadowMagic plugin)
	{
		super(plugin);
		this.name = "version";
		this.aliases.add("v");
		this.description = "Display SwornDrops version";
		this.permission = Permission.CMD_VERSION;

		this.usesPrefix = true;
	}

	@Override
	public void perform()
	{
		sendMessage("&3====[ &eSwornDrops &3]====");
		sendMessage("&eAuthor: &bdmulloy2");
		sendMessage("&eVersion: &b{0}", plugin.getDescription().getFullName());
		// sendMessage("&eDownload: {0}");
	}
}
