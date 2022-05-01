/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.command;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.types.Chest;
import net.dmulloy2.shadowmagic.types.PlayerData;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdGiveChest extends ShadowMagicCommand
{
	public CmdGiveChest(ShadowMagic plugin)
	{
		super(plugin);
		this.name = "givechest";
		this.addRequiredArg("player");
		this.addRequiredArg("amount");
		this.description = "Gives a player chests";
		this.usesPrefix = true;
	}

	@Override
	public void perform()
	{
		if (sender instanceof Player || ! sender.isOp())
		{
			err("You must be console to do this!");
			return;
		}

		OfflinePlayer player = Util.matchOfflinePlayer(args[0]);
		if (player == null)
		{
			err("Player not found!");
			return;
		}

		int amount = NumberUtil.toInt(args[1]);
		if (amount < 1)
		{
			err("Invalid number!");
			return;
		}

		PlayerData data = plugin.getPlayerDataCache().getData(player);
		int nextKey = 1;
		for (int key : data.getChests().keySet())
			if (key > nextKey)
				nextKey = key;

		nextKey += amount;

		for (int i = 0; i < nextKey; i++)
		{
			if (! data.getChests().containsKey(i))
				data.getChests().put(i, new Chest(i));
		}

		sendpMessage("&b{0} &echests given to &b{1}", amount, player.getName());
	}
}
