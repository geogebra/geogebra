package org.geogebra.common.gui.view.table;

/**
 * Interface for the table values listener.
 */
public interface TableValuesListener {

	/**
	 * Notified when a column has been removed.
	 *
	 * @param column the index of the column
	 */
	void notifyColumnRemoved(int column);

	/**
	 * Notified when the column has changed.
	 *
	 * @param column the index of the column
	 */
	void notifyColumnChanged(int column);

	/**
	 * Notified when a column has been added.
	 *
	 * @param column the index of the column
	 */
	void notifyColumnAdded(int column);

	/**
	 * Notified when the header of the column has changed.
	 *
	 * @param column the index of the column
	 */
	void notifyColumnHeaderChanged(int column);

	/**
	 * Notified when the whole dataset changed.
	 */
	void notifyDatasetChanged();
}
