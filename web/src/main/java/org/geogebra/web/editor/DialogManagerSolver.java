package org.geogebra.web.editor;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.TextInputDialog;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.AsyncOperation;

import com.google.gwt.user.client.Window;

/**
 * Dialog manager for solver
 */
public class DialogManagerSolver extends DialogManager {

	@Override
	public boolean showFunctionInspector(GeoFunction geoFunction) {
		return false;
	}

	@Override
	public void showDataSourceDialog(int mode, boolean doAutoLoadSelectedGeos) {
		// not needed in solver
	}

	@Override
	public void showNumberInputDialogSegmentFixed(String menu,
			GeoPointND geoPoint2) {
		// not needed in solver
	}

	@Override
	public void showNumberInputDialogAngleFixed(String menu,
			GeoSegmentND[] selectedSegments, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec) {
		// not needed in solver
	}

	@Override
	public boolean showSliderCreationDialog(int x, int y) {
		return false;
	}

	@Override
	public void showNumberInputDialogRotate(String menu,
			GeoPolygon[] selectedPolygons, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec) {
		// not needed in solver
	}

	@Override
	public void showNumberInputDialogDilate(String menu,
			GeoPolygon[] selectedPolygons, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec) {
		// not needed in solver
	}

	@Override
	public void showNumberInputDialogRegularPolygon(String menu,
			EuclidianController ec, GeoPointND geoPoint1, GeoPointND geoPoint2,
			GeoCoordSys2D direction) {
		// not needed in solver
	}

	@Override
	public void showBooleanCheckboxCreationDialog(GPoint corner,
			GeoBoolean bool) {
		// not needed in solver

	}

	@Override
	public void showNumberInputDialogCirclePointRadius(String title,
			GeoPointND geoPointND, EuclidianView view) {
		// not needed in solver

	}

	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, AsyncOperation<GeoNumberValue> callback) {
		// not needed in solver

	}

	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText,
			AsyncOperation<GeoNumberValue> callback) {
		// not needed in solver
	}

	@Override
	public void showAngleInputDialog(String title, String message,
			String initText, AsyncOperation<GeoNumberValue> callback) {
		// not needed in solver
	}

	@Override
	public boolean showButtonCreationDialog(int x, int y, boolean textfield) {
		return false;
	}

	@Override
	public void closeAll() {
		// not needed in solver
	}

	@Override
	public void showRenameDialog(GeoElement geo, boolean b, String label,
			boolean c) {
		// not needed in solver
	}

	@Override
	public void showPropertiesDialog(ArrayList<GeoElement> geos) {
		// not needed in solver
	}

	@Override
	public void showPropertiesDialog(OptionType type,
			ArrayList<GeoElement> geos) {
		// not needed in solver
	}

	@Override
	public void openToolHelp() {
		// not needed in solver
	}

	@Override
	public TextInputDialog createTextDialog(GeoText text, GeoPointND startPoint,
			boolean rw) {
		// not needed in solver
		return null;
	}

	@Override
	public void showPrintPreview() {
		Window.print();
	}
}