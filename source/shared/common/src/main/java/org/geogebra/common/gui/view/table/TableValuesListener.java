package org.geogebra.common.gui.view.table;

import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

/**
 * Interface for the table values listener.
 */
public interface TableValuesListener {

	/**
	 * Notified when a column has been removed.
	 * @param model the model
	 * @param evaluatable evaluatable
	 * @param column the index of the column
	 */
	void notifyColumnRemoved(TableValuesModel model, GeoEvaluatable evaluatable, int column);

	/**
	 * Notified when the column has changed.
	 * @param model the model
	 * @param evaluatable evaluatable
	 * @param column the index of the column
	 */
	void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column);

	/**
	 * Notified when a column has been added.
	 * @param model the model
	 * @param evaluatable evaluatable
	 * @param column the index of the column
	 */
	void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column);

	/**
	 * Notified when the header of the column has changed.
	 * @param model the model
	 * @param evaluatable evaluatable
	 * @param column the index of the column
	 */
	void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column);

	/**
	 * Notified when the cell has changed.
	 * @param model the model
	 * @param evaluatable list or function
	 * @param column the index of the column
	 * @param row the index of the row
	 */
	void notifyCellChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column, int row);

	/**
	 * Notified when one or multiple rows have been removed.
	 * If only a single row has been removed, {@code firstRow} and {@code lastRow} are equal.
	 * @param model the model
	 * @param firstRow the index of the first row that was removed
	 * @param lastRow the index of the last row that was removed
	 */
	void notifyRowsRemoved(TableValuesModel model, int firstRow, int lastRow);

	/**
	 * Notified when one or multiple rows have been added.
	 * If only a single row has been added, {@code firstRow} and {@code lastRow} are equal.
	 * @param model the model
	 * @param firstRow the index of the first row that was added
	 * @param lastRow the index of the last row that was added
	 */
	void notifyRowsAdded(TableValuesModel model, int firstRow, int lastRow);

	/**
	 * Notified when a row has been changed.
	 * @param model the model
	 * @param row the index of the row
	 */
	void notifyRowChanged(TableValuesModel model, int row);

	/**
	 * Notified when the whole dataset changed.
	 * @param model the model
	 */
	void notifyDatasetChanged(TableValuesModel model);

	/**
	 * Notifies listeners when data import has finished.
	 */
	default void notifyImportFinished(TableValuesModel model) {
		// ignore
	}
}
