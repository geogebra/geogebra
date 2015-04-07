package org.geogebra.desktop.cas.view;

import javax.swing.table.DefaultTableModel;

/**
 * CAS Table model
 */
public class CASTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates new CAS table model
	 */
	public CASTableModel() {
		super(1, 1);
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		return true;
	}

}
