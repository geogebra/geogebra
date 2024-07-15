package org.geogebra.common.main.syntax.suggestionfilter;

import java.util.Set;

public final class LineSelector {

	public static String select(String lines, Integer... indices) {
		return select(lines.split("\n"), indices);
	}

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
