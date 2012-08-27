package geogebra.main;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.gui.view.properties.PropertiesView.OptionType;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;

import java.util.ArrayList;

import javax.swing.JOptionPane;

public class DialogManagerMinimal extends DialogManager {

	/**
	 * Minimal implementation of DialogManager
	 * Potentially can be used in applets without needing GuiManager
	 * @param app
	 */
	public DialogManagerMinimal(App app) {
		this.app = app;

	}
	@Override
	protected String prompt(String message, String def) {
		return JOptionPane.showInputDialog(message);
	}

	@Override
	protected boolean confirm(String string) {
		return JOptionPane.showConfirmDialog(null, string) == JOptionPane.OK_CANCEL_OPTION;
	}

	@Override
	public boolean showFunctionInspector(GeoFunction geoFunction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showPropertiesDialog(ArrayList<GeoElement> geos) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void showPropertiesDialog(OptionType type, ArrayList<GeoElement> geos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showNumberInputDialogSegmentFixed(String menu,
			GeoPoint geoPoint2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showNumberInputDialogAngleFixed(String menu,
			GeoSegment[] selectedSegments, GeoPoint[] selectedPoints,
			GeoElement[] selGeos) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void showNumberInputDialogDilate(String menu,
			GeoPolygon[] selectedPolygons, GeoPoint[] selectedPoints,
			GeoElement[] selGeos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showBooleanCheckboxCreationDialog(GPoint loc, GeoBoolean bool) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showNumberInputDialogCirclePointRadius(String menu,
			GeoPointND geoPointND, EuclidianView view) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NumberValue showNumberInputDialog(String title, String message,
			String initText) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] showAngleInputDialog(String title, String message,
			String initText) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean showButtonCreationDialog(int x, int y, boolean textfield) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void closeAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showRenameDialog(GeoElement geo, boolean b, String label,
			boolean c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showOptionsDialog(int tabEuclidian) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showPropertiesDialog() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showToolbarConfigDialog() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public NumberValue showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void openToolHelp() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void showDataSourceDialog(int mode, boolean doAutoLoadSelectedGeos) {
		// TODO Auto-generated method stub
		
	}


}
