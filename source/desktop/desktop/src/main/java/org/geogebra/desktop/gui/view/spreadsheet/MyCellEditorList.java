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
