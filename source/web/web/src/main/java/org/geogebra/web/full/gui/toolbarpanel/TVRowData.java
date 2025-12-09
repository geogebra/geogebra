/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

