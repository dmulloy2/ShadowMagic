/**
 * Copyright (C) 2016 dmulloy2
 * All rights reserved
 */
package net.dmulloy2.shadowmagic.types;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author dmulloy2
 */

public class LogFormatter extends Formatter
{
	private static Map<Level, String> prefixes;

	private final SimpleDateFormat date;
	public LogFormatter()
	{
		date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	@Override
	public String format(LogRecord record)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(date.format(record.getMillis()));
		builder.append(" ");

		if (! prefixes.containsKey(record.getLevel()))
			return "";

		builder.append(prefixes.get(record.getLevel()));
		builder.append(" ");
		builder.append(formatMessage(record));
		builder.append('\n');

		return builder.toString();
	}

	static
	{
		prefixes.put(Level.ALL, "[Class]");
		prefixes.put(Level.CONFIG, "[Encant]");
		prefixes.put(Level.FINE, "[Name]");
		prefixes.put(Level.WARNING, "[Lore]");
		prefixes.put(Level.SEVERE, "[XP]");
	}
}
