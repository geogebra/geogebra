/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
