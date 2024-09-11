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
