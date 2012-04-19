package geogebra.main;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;

public class DialogManagerMinimal extends DialogManager {

	/**
	 * Minimal implementation of DialogManager
	 * Potentially can be used in applets without needing GuiManager
	 * @param app
	 */
	public DialogManagerMinimal(AbstractApplication app) {
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
	public void showNumberInputDialogSegmentFixed(String menu,
			GeoPoint2 geoPoint2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showNumberInputDialogAngleFixed(String menu,
			GeoSegment[] selectedSegments, GeoPoint2[] selectedPoints,
			GeoElement[] selGeos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showTextCreationDialog(GeoPointND loc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showNumberInputDialogDilate(String menu,
			GeoPolygon[] selectedPolygons, GeoPoint2[] selectedPoints,
			GeoElement[] selGeos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showBooleanCheckboxCreationDialog(Point loc, GeoBoolean bool) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showNumberInputDialogCirclePointRadius(String menu,
			GeoPointND geoPointND, AbstractEuclidianView view) {
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
	public void showTextDialog(GeoText geo) {
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


}
