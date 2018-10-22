package org.geogebra.common.gui.view.table;

/**
 * Interface for the table values listener.
 */
public interface TableValuesListener {

	/**
	 * Notified when a column has been removed.
	 *
	 * @param model the model
	 * @param column the index of the column
	 */
	void notifyColumnRemoved(TableValuesModel model, int column);

	/**
	 * Notified when the column has changed.
	 *
	 * @param model the model
	 * @param column the index of the column
	 */
	void notifyColumnChanged(TableValuesModel model, int column);

	/**
	 * Notified when a column has been added.
	 *
	 * @param model the model
	 * @param column the index of the column
	 */
	void notifyColumnAdded(TableValuesModel model, int column);

	/**
	 * Notified when the header of the column has changed.
	 *
	 * @param model the model
	 * @param column the index of the column
	 */
	void notifyColumnHeaderChanged(TableValuesModel model, int column);

	/**
	 * Notified when the whole dataset changed.
	 *
	 * @param model the model
	 */
	void notifyDatasetChanged(TableValuesModel model);
}
