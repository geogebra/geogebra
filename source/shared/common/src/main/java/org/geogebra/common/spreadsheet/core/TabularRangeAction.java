package org.geogebra.common.spreadsheet.core;

/**
 * An action to run on each element in a {@link TabularRange}.
 */
@FunctionalInterface
public interface TabularRangeAction {
	void run(int row, int column);
}
