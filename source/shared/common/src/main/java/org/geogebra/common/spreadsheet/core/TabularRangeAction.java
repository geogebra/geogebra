package org.geogebra.common.spreadsheet.core;

/**
 * An action to run on each element in a {@link TabularRange}.
 */
@FunctionalInterface
public interface TabularRangeAction {

	/**
	 * @param row row index
	 * @param column column index
	 */
	void run(int row, int column);
}
