package org.geogebra.desktop.main;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.TextInputDialog;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.desktop.gui.dialog.TextInputDialogD;

import com.himamis.retex.editor.share.util.Unicode;

public class DialogManagerMinimal extends DialogManager {

	private String defaultAngle = Unicode.FORTY_FIVE_DEGREES_STRING;

	/**
	 * Minimal implementation of DialogManager Potentially can be used in
	 * applets without needing GuiManager
	 * 
	 * @param app
	 */
	public DialogManagerMinimal(App app) {
		super(app);
	}

	/**
	 * @param message
	 *            message
	 * @param def
	 *            default
	 * @return user input
	 */
	protected String prompt(String message, String def) {
		return JOptionPane.showInputDialog(message);
	}

	/**
	 * @param string
	 *            message
	 * @return confirmation
	 */
	protected boolean confirm(String string) {
		return JOptionPane.showConfirmDialog(null,
				string) == JOptionPane.OK_CANCEL_OPTION;
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
	public void showPropertiesDialog(OptionType type,
			ArrayList<GeoElement> geos) {
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
	public void showBooleanCheckboxCreationDialog(GPoint screenLoc,
			GeoBoolean bool) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showNumberInputDialogCirclePointRadius(String menu,
			GeoPointND geoPointND, EuclidianView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, AsyncOperation<GeoNumberValue> callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showAngleInputDialog(String title, String message,
			String initText, AsyncOperation<GeoNumberValue> callback) {
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

	/** open toolbar customizer */
	public void showToolbarConfigDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText,
			AsyncOperation<GeoNumberValue> callback) {
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

	public void showLogInDialog() {
		// TODO Auto-generated method stub

	}

	public void showLogOutDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showNumberInputDialogRegularPolygon(String menu,
			EuclidianController ec, GeoPointND geoPoint1, GeoPointND geoPoint2,
			GeoCoordSys2D direction) {
		String inputString = prompt(menu + " " +
				getLocalization().getMenu("Points"), "4");

		makeRegularPolygon(app, ec, inputString, geoPoint1, geoPoint2,
				direction,
				app.getErrorHandler(), new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean obj) {
						// TODO Auto-generated method stub

					}
				});

	}

	@Override
	public TextInputDialog createTextDialog(GeoText text, GeoPointND startPoint,
			boolean rw) {
		return new TextInputDialogD(app, getLocalization().getMenu("Text"), text, startPoint,
				rw, 30, 6, app.getMode() == EuclidianConstants.MODE_TEXT);
	}

	@Override
	public boolean showSliderCreationDialog(int x, int y)		 {
				Kernel kernel = app.getKernel();
				boolean isAngle = !confirm("OK for number, Cancel for angle");
				GeoNumeric slider = GeoNumeric
						.setSliderFromDefault(
								isAngle ? new GeoAngle(kernel.getConstruction())
										: new GeoNumeric(kernel.getConstruction()),
								isAngle);

				StringTemplate tmpl = StringTemplate.defaultTemplate;

				// convert to degrees (angle only)
				String minStr = isAngle
						? kernel.format(Math.toDegrees(slider.getIntervalMin()), tmpl)
								+ Unicode.DEGREE_STRING
						: kernel.format(slider.getIntervalMin(), tmpl);
				String maxStr = isAngle
						? kernel.format(Math.toDegrees(slider.getIntervalMax()), tmpl)
								+ Unicode.DEGREE_STRING
						: kernel.format(slider.getIntervalMax(), tmpl);
				String incStr = isAngle
						? kernel.format(Math.toDegrees(slider.getAnimationStep()), tmpl)
								+ Unicode.DEGREE_STRING
						: kernel.format(slider.getAnimationStep(), tmpl);

				// get input from user
				NumberValue min = getNumber(kernel, "Enter minimum", minStr);
				NumberValue max = getNumber(kernel, "Enter maximum", maxStr);
				NumberValue increment = getNumber(kernel, "Enter increment", incStr);

				if (min != null) {
					slider.setIntervalMin(min);
				}
				if (max != null) {
					slider.setIntervalMax(max);
				}
				if (increment != null) {
					slider.setAnimationStep(increment);
				}

				slider.setLabel(null);
				slider.setValue(isAngle ? 45 * Math.PI / 180 : 1);
				slider.setSliderLocation(x, y, true);
				slider.setEuclidianVisible(true);

				slider.setLabelMode(GeoElement.LABEL_NAME_VALUE);
				slider.setLabelVisible(true);
				slider.update();
				// slider.setRandom(cbRandom.isSelected());

				app.storeUndoInfo();

				return true;

	}

	protected GeoNumberValue getNumber(Kernel kernel, String message,
			String def) {

		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		String str = prompt(message, def);

		GeoNumberValue result = kernel.getAlgebraProcessor()
				.evaluateToNumeric(str, true);

		cons.setSuppressLabelCreation(oldVal);

		return result;
	}

	@Override
	public void showNumberInputDialogRotate(String menu,
			GeoPolygon[] selectedPolygons, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec) {
		String inputString = prompt(menu + " " + getLocalization().getMenu("Angle"),
				defaultAngle);

		rotateObject(app, inputString, false, selectedPolygons,
				new CreateGeoForRotatePoint(selectedPoints[0]), selGeos, ec,
				app.getDefaultErrorHandler(), new AsyncOperation<String>() {

					@Override
					public void callback(String obj) {
						defaultAngle = obj;

					}
				});

	}
}
