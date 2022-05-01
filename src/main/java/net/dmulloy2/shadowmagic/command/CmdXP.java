/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.command;

import java.util.logging.Level;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.types.Permission;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdXP extends ShadowMagicCommand
{
	public CmdXP(ShadowMagic plugin)
	{
		super(plugin);
		this.name = "xp";
		this.addRequiredArg("player");
		this.addRequiredArg("xp");
		this.description = "Give xp to a player";
		this.permission = Permission.CMD_XP;
		this.usesPrefix = true;
	}

	@Override
	public void perform()
	{
		Player player = Util.matchPlayer(args[0]);
		if (player == null)
		{
			err("Player not found!");
			return;
		}

		int xp = NumberUtil.toInt(args[1]);
		if (xp < 0)
		{
			err("Invalid number!");
			return;
		}

		plugin.logToFile(Level.SEVERE, sender.getName() + " gave " + xp + " to " + player.getName());

		player.setLevel(player.getLevel() + xp);
		sendpMessage("&eYou have given &b{0} &exp to &b{1}", xp, player.getName());
	}
}
