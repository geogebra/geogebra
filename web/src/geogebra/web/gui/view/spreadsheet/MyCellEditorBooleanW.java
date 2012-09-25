package geogebra.web.gui.view.spreadsheet;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Cell editor for GeoBoolean.
 * 
 * @author G.Sturr 2010-6-4
 *
 */
public class MyCellEditorBooleanW implements BaseCellEditor {
	private static final long serialVersionUID = 1L;
	private Kernel kernel;
	private AppW app;
	private GeoBoolean editGeo;
	private CheckBox checkBox;	
	boolean editing = false;
	protected MyTableW table;

	public MyCellEditorBooleanW(Kernel kernel) {

		checkBox = new CheckBox();
		app = (AppW)kernel.getApplication();
	}


	public Widget getTableCellEditorWidget(MyTableW table0, Object value,
			boolean isSelected, int row, int column) {

		table = table0;
		checkBox = new CheckBox();

		editGeo = (GeoBoolean)value;
		checkBox.setValue(editGeo.getBoolean());
		editing = true;
		checkBox.getElement().getStyle().setBackgroundColor(GColor.white.toString());
		 /*table0.getBackground()*/
		//?//checkBox.setHorizontalAlignment(SwingConstants.CENTER);

		//enabled == isIndependent
		checkBox.setEnabled(editGeo.isIndependent());

		if(editGeo.isLabelVisible()){
			//checkBox.setText(editGeo.getCaption());
		}
		return checkBox;
	}


	public Object getCellEditorValue() {
		
		return editGeo;
	}

	public boolean stopCellEditing() {

		try {
			if(editGeo.isIndependent()){
				editGeo.setValue(checkBox.getValue());
				editGeo.updateCascade();
			}
			//app.storeUndoInfo();

		} catch (Exception ex) {
			ex.printStackTrace();
			//?//super.stopCellEditing();
			editing = false;
			return false;
		}

		editing = false;
		table.finishEditing();
		return true;//?//super.stopCellEditing();
	}

	public void cancelCellEditing() {
		editing = false;
		table.finishEditing();
	}

	public boolean isEditing(){
		return false;
	}
	

}
