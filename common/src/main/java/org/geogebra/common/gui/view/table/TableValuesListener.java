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
	 * @param column the index of the column
	 * @param row the index of the row
	 */
	void notifyCellChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column, int row);

	/**
	 * Notified when the whole dataset changed.
	 * @param model the model
	 */
	void notifyDatasetChanged(TableValuesModel model);
}
