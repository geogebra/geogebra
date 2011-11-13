package geogebra.gui.view.spreadsheet;

import geogebra.kernel.GeoBoolean;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 * Cell editor for GeoBoolean.
 * 
 * @author G.Sturr 2010-6-4
 *
 */
public class MyCellEditorBoolean extends DefaultCellEditor {

	private Kernel kernel;
	private Application app;
	private GeoBoolean editGeo;
	private JCheckBox checkBox;	
	boolean editing = false;
	

	public MyCellEditorBoolean(Kernel kernel) {
		
		super(new JCheckBox());
		checkBox = (JCheckBox) editorComponent;
		app = kernel.getApplication();
	}

	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		editGeo = (GeoBoolean)value;
		delegate.setValue(editGeo.getBoolean());
		editing = true;
		checkBox.setBackground(table.getBackground());
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
		
		//enabled == isIndependent
		checkBox.setEnabled(editGeo.isIndependent());
		
		if(editGeo.isLabelVisible()){
			//checkBox.setText(editGeo.getCaption());
		}
		return editorComponent;
	}

	
	@Override
	public Object getCellEditorValue() {
		
		return editGeo;
	}
		
	
	@Override
	public boolean stopCellEditing() {

		try {
			if(editGeo.isIndependent()){
				editGeo.setValue(checkBox.isSelected());
				editGeo.updateCascade();
			}
			//app.storeUndoInfo();

		} catch (Exception ex) {
			ex.printStackTrace();
			super.stopCellEditing();
			editing = false;
			return false;
		}

		editing = false;
		return super.stopCellEditing();
	}
	
	public boolean isEditing(){
		return false;
	}
	

}
