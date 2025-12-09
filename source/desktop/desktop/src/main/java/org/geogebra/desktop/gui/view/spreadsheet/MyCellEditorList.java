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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.util.debug.Log;

/**
 * Cell editor for GeoBoolean.
 * 
 * @author G.Sturr 2010-6-4
 *
 */
public class MyCellEditorList extends DefaultCellEditor
		implements ActionListener {
	private static final long serialVersionUID = 1L;
	private GeoList editGeo;
	private JComboBox<GeoElement> comboBox;
	private DefaultComboBoxModel<GeoElement> model;

	/**
	 * Creates the editor
	 */
	public MyCellEditorList() {
		super(new JComboBox());
		comboBox = (JComboBox) editorComponent;
		comboBox.setRenderer(new SpreadsheetCellRendererD.GeoElementListCellRenderer());
		model = new DefaultComboBoxModel<>();
		comboBox.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			editGeo.setSelectedIndexUpdate(comboBox.getSelectedIndex());
		} catch (Exception ex) {
			Log.debug(ex);
		}
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		comboBox.removeActionListener(this);
		editGeo = (GeoList) value;
		model.removeAllElements();
		for (int i = 0; i < editGeo.size(); i++) {
			model.addElement(editGeo.get(i));
		}
		comboBox.setModel(model);
		comboBox.setSelectedIndex(editGeo.getSelectedIndex());
		comboBox.addActionListener(this);
		return editorComponent;

	}

	@Override
	public Object getCellEditorValue() {
		return editGeo;
	}

}
