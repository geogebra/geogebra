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

package org.geogebra.common.main.syntax.suggestionfilter;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Selects the specified lines from the syntax definition for the specified commands.
 */
public class LineSelectorSyntaxFilter implements SyntaxFilter {

	private final Map<Commands, Integer[]> syntaxFilterMap = new HashMap<>();

	/**
	 * Adds a syntax line selector for the specified command.
	 * {@link SyntaxFilter#getFilteredSyntax(String, String)} will then select only those lines
	 * from the syntax definition which are specified.
	 * @param command specify the command to select the lines from
	 * @param lines the lines to select from the syntax definition
	 */
	public void addSelector(Commands command, Integer... lines) {
		syntaxFilterMap.put(command, lines);
	}

	@Override
	public String getFilteredSyntax(String internalCommandName, String syntax) {
		for (Map.Entry<Commands, Integer[]> entry : syntaxFilterMap.entrySet()) {
			if (entry.getKey().name().equals(internalCommandName)) {
				return LineSelector.select(syntax, entry.getValue());
			}
		}
		return syntax;
	}
}
