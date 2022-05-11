package org.geogebra.common.gui.view.table;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Class to hold values of table view and handle listeners.
 */
public interface TableValuesModel {

	/**
	 * Register as a listener to this model, to get notified about changes.
	 * @param listener listener
	 */
	void registerListener(TableValuesListener listener);

	/**
	 * Unregister objects as listener.
	 * @param listener listener
	 */
	void unregisterListener(TableValuesListener listener);

	/**
	 * Sets an element.
	 * @param element element
	 * @param column column
	 * @param rowIndex row index
	 */
	void set(GeoElement element, GeoList column, int rowIndex);

	/**
	 * Get the number of rows in the table.
	 * @return the number of rows
	 */
	int getRowCount();

	/**
	 * Get the number of columns in the table.
	 * @return the number of columns
	 */
	int getColumnCount();

	/**
	 * Return the value for the specified cell at the specified location.
	 * @param row the row of the entry
	 * @param column the column of the entry
	 * @return the value of the table at the specified location
	 */
	TableValuesCell getCellAt(int row, int column);

	/**
	 * Return the header for the specified column.
	 * @param column the index of the header
	 * @return the header string
	 */
	String getHeaderAt(int column);

	/**
	 * Returns the numeric value at the specified location.
	 * @param row row of the entry
	 * @param column column of the entry
	 * @return numeric value or NaN.
	 */
	double getValueAt(int row, int column);

	/**
	 * Starts batch update. This batch update call cannot be nested.
	 */
	void startBatchUpdate();

	/**
	 * Ends the batch update. If {@code notifyDatasetChanged} is true, calls
	 * TableValuesListener.notifyDatasetChanged(TableValuesModel).
	 * @param notifyDatasetChanged whether to call notifyDatasetChanged
	 */
	void endBatchUpdate(boolean notifyDatasetChanged);

	/**
	 * @return True, if it's called between
	 * {@link TableValuesModel#startBatchUpdate()} and
	 * {@link TableValuesModel#endBatchUpdate(boolean)} calls.
	 * Otherwise, false.
	 */
	boolean isBatchUpdate();

	/**
	 * @param element element
	 * @return whether the element contains only an empty string
	 */
	boolean isEmptyValue(GeoElement element);

	/**
	 * Creates an empty value to be used in lists.
	 * @return empty value
	 */
	GeoElement createEmptyValue();

	/**
	 * Creates a numeric value to be used in lists.
	 * @param value value
	 * @return geo element
	 */
	GeoNumeric createValue(double value);
}
