/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.command;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.types.Chest;
import net.dmulloy2.shadowmagic.types.Permission;
import net.dmulloy2.shadowmagic.types.PlayerData;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdChest extends ShadowMagicCommand
{
	public CmdChest(ShadowMagic plugin)
	{
		super(plugin);
		this.name = "chest";
		this.addOptionalArg("player");
		this.addRequiredArg("number");
		this.description = "Open a virtual chest";
		this.permission = Permission.CMD_CHEST;
		this.mustBePlayer = true;
		this.usesPrefix = true;
	}

	@Override
	public void perform()
	{
		Player player = this.player;
		if (args.length == 2 && hasPermission(Permission.CMD_CHEST_SEE))
		{
			player = Util.matchPlayer(args[0]);
			if (player == null)
			{
				err("Invalid player: &c{0}", args[0]);
				return;
			}
		}
		
		int number = NumberUtil.toInt(args[args.length - 1]);
		if (number < 1 || number > 99)
		{
			err("Invalid number: &c{0}", args[args.length - 1]);
			return;
		}

		Chest chest = null;
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.getChests().containsKey(number))
			chest = data.getChests().get(number);

		if (chest == null)
		{
			err("You don''t have access to chest #{0}", number);
			return;
		}

		data.setOpenChest(chest);
		chest.open((Player) sender, data);
	}
}
