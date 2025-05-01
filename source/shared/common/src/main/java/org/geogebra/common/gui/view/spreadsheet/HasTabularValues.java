package org.geogebra.common.gui.view.spreadsheet;

/**
 * Rectangular table of elements of the same type
 * @param <T> element type
 */
public interface HasTabularValues<T> {
	/**
	 * @param row row
	 * @param column column
	 * @return table content at given coordinates
	 */
	T contentAt(int row, int column);

	/**
	 * @return number of rows
	 */
	int numberOfRows();

	/**
	 * @return number ofcolumns
	 */
	int numberOfColumns();
}
