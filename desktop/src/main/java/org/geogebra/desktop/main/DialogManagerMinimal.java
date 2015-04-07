package org.geogebra.desktop.main;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.InputDialog;
import org.geogebra.common.gui.dialog.TextInputDialog;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.desktop.gui.dialog.InputDialogD;

public class DialogManagerMinimal extends DialogManager {

	/**
	 * Minimal implementation of DialogManager Potentially can be used in
	 * applets without needing GuiManager
	 * 
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
			GeoPointND geoPoint2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showNumberInputDialogAngleFixed(String menu,
			GeoSegmentND[] selectedSegments, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showNumberInputDialogDilate(String menu,
			GeoPolygon[] selectedPolygons, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec) {
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
	public void showNumberInputDialog(String title, String message,
			String initText, AsyncOperation callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showAngleInputDialog(String title, String message,
			String initText, AsyncOperation callback) {
		// TODO Auto-generated method stub
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
	public void showPropertiesDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showToolbarConfigDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText,
			AsyncOperation callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void openToolHelp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showDataSourceDialog(int mode, boolean doAutoLoadSelectedGeos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showLogInDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showLogOutDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showNumberInputDialogRegularPolygon(String menu,
			EuclidianController ec, GeoPointND geoPoint1, GeoPointND geoPoint2) {
		String inputString = prompt(menu + " " + app.getPlain("Points"), "4");

		makeRegularPolygon(app, ec, inputString, geoPoint1, geoPoint2);

	}

	@Override
	public TextInputDialog createTextDialog(GeoText text, GeoPointND startPoint) {
		return new org.geogebra.desktop.gui.dialog.TextInputDialog(app,
				app.getPlain("Text"), text, startPoint, 30, 6,
				app.getMode() == EuclidianConstants.MODE_TEXT);
	}

	@Override
	public InputDialog newInputDialog(App app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			GeoElement geo) {
		return new InputDialogD((AppD) app, message, title, initString,
				autoComplete, handler, geo);
	}
}
