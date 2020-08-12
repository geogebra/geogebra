package org.geogebra.common.main.syntax.suggestionfilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class LineSelector {

	String select(String[] lines, Integer... indices) {
		Set<Integer> indexSet = new HashSet<>(Arrays.asList(indices));
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
