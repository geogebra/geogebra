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

package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.geogebra.common.kernel.geos.GeoBoolean;

/**
 * Cell editor for GeoBoolean.
 * 
 * @author G.Sturr 2010-6-4
 *
 */
public class MyCellEditorBoolean extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;
	private GeoBoolean editGeo;
	private JCheckBox checkBox;
	boolean editing = false;

	/**
	 * Checkbox for spreadsheet
	 */
	public MyCellEditorBoolean() {
		super(new JCheckBox());
		checkBox = (JCheckBox) editorComponent;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		editGeo = (GeoBoolean) value;
		delegate.setValue(editGeo.getBoolean());
		editing = true;
		checkBox.setBackground(table.getBackground());
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);

		// enabled == isIndependent
		checkBox.setEnabled(editGeo.isIndependent());
		return editorComponent;
	}

	@Override
	public Object getCellEditorValue() {

		return editGeo;
	}

	@Override
	public boolean stopCellEditing() {

		try {
			if (editGeo.isIndependent()) {
				editGeo.setValue(checkBox.isSelected());
				editGeo.updateCascade();
			}
			// app.storeUndoInfo();

		} catch (Exception ex) {
			ex.printStackTrace();
			super.stopCellEditing();
			editing = false;
			return false;
		}

		editing = false;
		return super.stopCellEditing();
	}

	public boolean isEditing() {
		return false;
	}

}
