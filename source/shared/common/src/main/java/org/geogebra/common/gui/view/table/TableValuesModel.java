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
	 * @return true if this model allows users to add columns (by entering values) to the right
	 * of existing columns. Defaults to true.
	 */
	boolean allowsAddingColumns();

	/**
	 * Enable or disable whether users can add columns (by entering values) to the right of
	 * existing columns.
	 * @param allowsAddingColumns Pass false to prevent new columns from being added
	 *                               (e.g., in SciCalc).
	 */
	void setAllowsAddingColumns(boolean allowsAddingColumns);

	/**
	 * @return True if there are any editable columns in the table of values view.
	 */
	boolean hasEditableColumns();

	/**
	 * @param column The column index (0-based).
	 * @return True if the given column is editable (i.e., is not a function).
	 */
	boolean isColumnEditable(int column);

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
	 * Set the header of the first column
	 * @param valuesHeader name
	 */
	void setValuesHeader(String valuesHeader);

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

	/**
	 * Sets up the GeoList object so that it can become the x values column.
	 * @param xValues The GeoList to become the x values column.
	 * @return The x values column with all the necessary settings set.
	 */
	GeoList setupXValues(GeoList xValues);

	/**
	 * This flag is set to true at the start of data import, and set back to false
	 * after any change notifications caused by the data import have been sent out.
	 * Registered TableValuesListeners can query this flag to detect if a change
	 * notification was caused by a data import.
	 * @return true during import.
	 */
	boolean isImportingData();

	/**
	 * @param onDataImported - when data imported, hide loading snackbar
	 */
	void setOnDataImportedRunnable(Runnable onDataImported);

	/**
	 * Remove all columns from construction
	 */
	void removeAllColumns();
}
