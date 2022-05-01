/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.types;

import net.dmulloy2.types.IPermission;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dmulloy2
 */

@Getter
@AllArgsConstructor
public enum Permission implements IPermission
{
	CMD_CHEST("cmd.chest"),
	CMD_CHEST_SEE("cmd.chest.see"),
	CMD_ENCHANT("cmd.enchant"),
	CMD_LORE("cmd.lore"),
	CMD_LORE_FREE("cmd.lore.free"),
	CMD_NAME("cmd.name"),
	CMD_NAME_FREE("cmd.name.free"),
	CMD_POWER("cmd.power"),
	CMD_RELOAD("cmd.reload"),
	CMD_VERSION("cmd.version"),
	CMD_XP("cmd.xp"),
	;

	private String node;
}
