package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.view.table.TableValuesModel;

/**
 * Data for building sticky table.
 *
 * @author latzg
 *
 */
public class TVRowData {
	private int row;
	private TableValuesModel model;

	/**
	 * Constructor.
	 *
	 * @param row
	 *            to represent
	 * @param model
	 *            the data source.
	 */
	public TVRowData(int row, TableValuesModel model) {
		this.row = row;
		this.model = model;
	}

	/**
	 * @return the column header.
	 */
	public String getHeader() {
		return model.getHeaderAt(1);
	}

	/**
	 *
	 * @param col
	 *            the column
	 * @return the cell value
	 */
	public String getValue(int col) {
		if (row < model.getRowCount() && col < model.getColumnCount()) {
			return model.getCellAt(row, col).getInput();
		}
		return "";
	}

	/**
	 *
	 * @param col
	 *            the column
	 * @return if cell is erroneous
	 */
	public boolean isCellErroneous(int col) {
		if (row < model.getRowCount() && col < model.getColumnCount()
			&& hasCellAt(row, col)) {
			return model.getCellAt(row, col).isErroneous();
		}
		return false;
	}

	private boolean hasCellAt(int row, int col) {
		return model.getCellAt(row, col) != null;
	}

	/**
	 * @return the row.
	 */
	public int getRow() {
		return row;
	}
}

