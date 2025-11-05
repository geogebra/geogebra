/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.util;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

	/**
	 * Parse the command syntax strings to a list
	 * @param command command syntax string, e.g. Midpoint( &lt;Point&gt;, &lt;Point&gt; )
	 * @return list containing command name and parameters
	 */
	public static List<String> parseCommand(String command) {
		List<String> parts = new ArrayList<>();

		int nameEnd = command.indexOf('(');
		if (nameEnd == -1) {
			parts.add(command);
			return parts;
		}

		parts.add(command.substring(0, nameEnd));

		for (int i = nameEnd; i < command.length(); i++) {
			char ch = command.charAt(i);
			if (ch == '<') {
				StringBuilder parameter = new StringBuilder();
				i++;
				while (i < command.length() && command.charAt(i) != '>') {
					parameter.append(command.charAt(i));
					i++;
				}

				parts.add(parameter.toString());
				continue;
			}

			if (!skipChar(ch)) {
				StringBuilder parameter = new StringBuilder();
				while (i < command.length() && !skipChar(command.charAt(i))) {
					parameter.append(command.charAt(i));
					i++;
				}

				parts.add(parameter.toString());
			}
		}

		return parts;
	}

	private static boolean skipChar(char ch) {
		return ch == '(' || ch == ')' || ch == ' ' || ch == ',' || ch == '>';
	}
}
