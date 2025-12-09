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

package org.geogebra.desktop.cas.view;

import javax.swing.AbstractListModel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * List model for row headers
 */
public class RowHeaderListModel extends AbstractListModel
		implements TableModelListener {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private int size;

	/**
	 * @param table
	 *            creates new model
	 */
	public RowHeaderListModel(JTable table) {
		this.table = table;
		table.getModel().addTableModelListener(this);
		size = table.getRowCount();
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public Object getElementAt(int index) {
		return Integer.toString(index + 1);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		int firstRow = e.getFirstRow();
		int lastRow = e.getLastRow();

		int oldSize = size;
		int rowCount = table.getRowCount();
		size = rowCount;

		if (rowCount > oldSize) {
			fireIntervalAdded(this, firstRow, lastRow);
		} else if (rowCount < oldSize) {
			fireIntervalRemoved(this, firstRow, lastRow);
		} else {
			fireContentsChanged(this, firstRow, lastRow);
		}
	}
}