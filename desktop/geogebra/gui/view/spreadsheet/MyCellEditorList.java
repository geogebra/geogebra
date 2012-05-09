package geogebra.gui.view.spreadsheet;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;

/**
 * Cell editor for GeoBoolean.
 * 
 * @author G.Sturr 2010-6-4
 *
 */
public class MyCellEditorList extends DefaultCellEditor implements ActionListener{
	private static final long serialVersionUID = 1L;
	private GeoList editGeo;
	private JComboBox comboBox;	
	private DefaultComboBoxModel model;

	public MyCellEditorList() {
		
		super(new JComboBox());
		comboBox = (JComboBox) editorComponent;
		comboBox.setRenderer(new MyListCellRenderer());
		model = new DefaultComboBoxModel();
		comboBox.addActionListener(this);	
		
	}

	public void actionPerformed(ActionEvent e) {			
		try {
			editGeo.setSelectedIndex(comboBox.getSelectedIndex(), false);
			editGeo.updateCascade();
			editGeo.getKernel().notifyRepaint();
			editGeo.getKernel().storeUndoInfo();
		} catch (Exception ex) {
			ex.printStackTrace();			
		}
	}

		
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		comboBox.removeActionListener(this);
		editGeo = (GeoList)value;	
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
		
	
	public boolean isEditing(){
		return false;
	}
	

	//======================================================
	//         ComboBox Cell Renderer 
	//======================================================
	
	/**
	 * Custom cell renderer that displays GeoElement descriptions.
	 */
	static private class MyListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean hasFocus) {

			setBackground(Color.WHITE);
			JLabel lbl = (JLabel)super.getListCellRendererComponent(
	                list, value, index, isSelected, hasFocus);
	        lbl.setHorizontalAlignment(LEFT);

			if (value != null) {
				GeoElement geo = (GeoElement) value;
				if(geo.isGeoText())
					setText(geo.toValueString(StringTemplate.defaultTemplate));
				else
					setText(geo.getLabel(StringTemplate.defaultTemplate));
			} else
				setText(" ");
			
			return lbl;
		}

	}

}
