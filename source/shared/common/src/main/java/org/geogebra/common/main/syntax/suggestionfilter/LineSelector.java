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

import java.util.Set;

public final class LineSelector {

	/**
	 * Select a subset of lines from a multi-line string.
	 * @param lines a string with multiple lines (separated by \n)
	 * @param indices the indices of the lines to select.
	 * @return the lines from the multiline string corresponding to the given indices.
	 */
	public static String select(String lines, Integer... indices) {
		return select(lines.split("\n"), indices);
	}

	/**
	 * Select a subset of lines from an array.
	 * @param lines an array of lines
	 * @param indices the indices of the lines to select.
	 * @return the lines from the array corresponding to the given indices.
	 */
	public static String select(String[] lines, Integer... indices) {
		Set<Integer> indexSet = Set.of(indices);
		StringBuilder linesBuilder = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			if (indexSet.contains(i)) {
				if (linesBuilder.length() != 0) {
					linesBuilder.append('\n');
				}
				linesBuilder.append(lines[i]);
			}
		}
		return linesBuilder.toString();
	}
}
