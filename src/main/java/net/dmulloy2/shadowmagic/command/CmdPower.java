/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.command;

import net.dmulloy2.shadowmagic.ShadowMagic;
import net.dmulloy2.shadowmagic.gui.PowersGUI;
import net.dmulloy2.shadowmagic.powers.Power;
import net.dmulloy2.shadowmagic.types.Class;
import net.dmulloy2.shadowmagic.types.Permission;
import net.dmulloy2.shadowmagic.types.PlayerData;

/**
 * @author dmulloy2
 */

public class CmdPower extends ShadowMagicCommand
{
	public CmdPower(ShadowMagic plugin)
	{
		super(plugin);
		this.name = "power";
		this.aliases.add("p");
		this.addOptionalArg("power");
		this.description = "Activate a power";
		this.permission = Permission.CMD_POWER;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		Class clazz = data.getClazz(plugin);
		if (clazz == null)
		{
			err("You do not have a class!");
			return;
		}

		if (data.getActivePower() != null)
		{
			err("You already have an active power!");
			return;
		}

		if (args.length == 0)
		{
			PowersGUI pGUI = new PowersGUI(plugin, player, clazz);
			data.setOpenGUI(pGUI);
			pGUI.open();
			return;
		}

		for (Power power : clazz.getPowers())
		{
			power.activate(player);
			return;
		}

		err("Power not found!");
		return;
	}
}
