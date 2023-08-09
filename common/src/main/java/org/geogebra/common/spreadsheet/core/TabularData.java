package org.geogebra.common.spreadsheet.core;

/**
 * Interacting with the structure and contents of tabular data.
 */
public interface TabularData {

	// structure
	void reset(int rows, int columns);

	int numberOfRows();

	int numberOfColumns();

	void appendRows(int rows);

	void insertRowAt(int row);

	void deleteRowAt(int row);

	void appendColumns(int columns);

	void insertColumnAt(int column);

	void deleteColumnAt(int column);

	// content
	void setContent(int row, int column, Object content);

	Object contentAt(int row, int column);

	String getColumnName(int column);
}