/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.InputDialog;
import org.geogebra.common.gui.dialog.TextInputDialog;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.handler.RedefineInputHandler;
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
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.Unicode;

public abstract class DialogManager {

	protected String defaultAngle = Unicode.FORTY_FIVE_DEGREES;

	protected App app;

	private Object oldString;

	/**
	 * Dialog for styling text objects.
	 */
	protected TextInputDialog textInputDialog;

	protected boolean oldVal;

	public DialogManager() {
	}

	public DialogManager(App app) {
		this.app = app;

	}

	public abstract boolean showFunctionInspector(GeoFunction geoFunction);

	public abstract void showDataSourceDialog(int mode,
			boolean doAutoLoadSelectedGeos);

	public void showRedefineDialog(GeoElement geo, boolean allowTextDialog) {

		if (allowTextDialog && geo.isGeoText() && !geo.isTextCommand()) {
			showTextDialog((GeoText) geo);
			return;
		}

		String str = geo.getRedefineString(false, true);

		InputHandler handler = new RedefineInputHandler(app, geo, str);

		newInputDialog(app, geo.getNameDescription(), app.getPlain("Redefine"),
				str, true, handler, geo);

	}

	public abstract InputDialog newInputDialog(App app, String message,
			String title, String initString, boolean autoComplete,
			InputHandler handler, GeoElement geo);

	public abstract void showNumberInputDialogSegmentFixed(String menu,
			GeoPointND geoPoint2);

	public void showNumberInputDialogAngleFixed(String menu,
			GeoSegmentND[] selectedSegments, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec) {
		doAngleFixed(
				app.getKernel(),
				selectedSegments,
				selectedPoints,
				selGeos,
				getNumber(app.getKernel(), menu + " " + app.getPlain("Length"),
						""), false, ec);

	}

	public static void doAngleFixed(Kernel kernel, GeoSegmentND[] segments,
			GeoPointND[] points, GeoElement[] selGeo2s, GeoNumberValue num,
			boolean clockWise, EuclidianController ec) {
		// GeoElement circle = kernel.Circle(null, geoPoint1,
		// ((NumberInputHandler)inputHandler).getNum());
		// geogebra.gui.AngleInputDialog dialog =
		// (geogebra.gui.AngleInputDialog) ob[1];
		// String angleText = getText();

		GeoAngle angle;

		if (points.length == 2) {
			angle = ec.getCompanion().createAngle(points[0], points[1], num,
					clockWise);
			// (GeoAngle) kernel.getAlgoDispatcher().Angle(null, points[0],
			// points[1], num, !clockWise)[0];
		} else {
			angle = ec.getCompanion().createAngle(segments[0].getEndPoint(),
					segments[0].getStartPoint(), num, clockWise);
			// (GeoAngle) kernel.getAlgoDispatcher().Angle(null,
			// segments[0].getEndPoint(), segments[0].getStartPoint(), num,
			// !clockWise)[0];
		}


		kernel.getApplication().storeUndoInfo();

	}

	public boolean showSliderCreationDialog(int x, int y) {
		Kernel kernel = app.getKernel();
		boolean isAngle = !confirm("OK for number, Cancel for angle");
		GeoNumeric slider = GeoNumeric.setSliderFromDefault(
				isAngle ? new GeoAngle(kernel.getConstruction())
						: new GeoNumeric(kernel.getConstruction()), isAngle);

		StringTemplate tmpl = StringTemplate.defaultTemplate;

		// convert to degrees (angle only)
		String minStr = isAngle ? kernel.format(
				Math.toDegrees(slider.getIntervalMin()), tmpl)
				+ Unicode.degree : kernel.format(slider.getIntervalMin(), tmpl);
		String maxStr = isAngle ? kernel.format(
				Math.toDegrees(slider.getIntervalMax()), tmpl)
				+ Unicode.degree : kernel.format(slider.getIntervalMax(), tmpl);
		String incStr = isAngle ? kernel.format(
				Math.toDegrees(slider.getAnimationStep()), tmpl)
				+ Unicode.degree : kernel.format(slider.getAnimationStep(),
				tmpl);

		// get input from user
		NumberValue min = getNumber(kernel, "Enter minimum", minStr);
		NumberValue max = getNumber(kernel, "Enter maximum", maxStr);
		NumberValue increment = getNumber(kernel, "Enter increment", incStr);

		if (min != null)
			slider.setIntervalMin(min);
		if (max != null)
			slider.setIntervalMax(max);
		if (increment != null)
			slider.setAnimationStep(increment);

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

	protected abstract boolean confirm(String string);

	public void showNumberInputDialogRotate(String menu,
			GeoPolygon[] selectedPolygons, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec) {
		String inputString = prompt(menu + " " + app.getPlain("Angle"),
				defaultAngle);

		defaultAngle = rotateObject(app, inputString, false, selectedPolygons,
				selectedPoints, selGeos, ec);

	}

	public abstract void showNumberInputDialogDilate(String menu,
			GeoPolygon[] selectedPolygons, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec);

	public abstract void showNumberInputDialogRegularPolygon(String menu,
			EuclidianController ec, GeoPointND geoPoint1, GeoPointND geoPoint2);

	public abstract void showBooleanCheckboxCreationDialog(GPoint loc,
			GeoBoolean bool);

	public abstract void showNumberInputDialogCirclePointRadius(String title,
			GeoPointND geoPointND, EuclidianView view);

	public abstract void showNumberInputDialog(String title, String message,
			String initText, AsyncOperation callback);

	public abstract void showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText,
			AsyncOperation callback);

	public abstract void showAngleInputDialog(String title, String message,
			String initText, AsyncOperation callback);

	public abstract boolean showButtonCreationDialog(int x, int y,
			boolean textfield);

	public static String rotateObject(App app, String inputText,
			boolean clockwise, GeoPolygon[] polys, GeoPointND[] points,
			GeoElement[] selGeos, EuclidianController ec) {
		String defaultRotateAngle = Unicode.FORTY_FIVE_DEGREES;
		String angleText = inputText;
		Kernel kernel = app.getKernel();

		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// negative orientation ?
		if (ec.getCompanion().viewOrientationForClockwise(clockwise)) {
			inputText = "-(" + inputText + ")";
		}

		GeoElement[] result = kernel.getAlgebraProcessor()
				.processAlgebraCommand(inputText, false);

		cons.setSuppressLabelCreation(oldVal);

		boolean success = result != null && result[0] instanceof GeoNumberValue;

		if (success) {
			// GeoElement circle = kernel.Circle(null, geoPoint1,
			// ((NumberInputHandler)inputHandler).getNum());
			GeoNumberValue num = (GeoNumberValue) result[0];
			// geogebra.gui.AngleInputDialog dialog =
			// (geogebra.gui.AngleInputDialog) ob[1];

			// keep angle entered if it ends with 'degrees'
			if (angleText.endsWith(Unicode.degree))
				defaultRotateAngle = angleText;

			if (polys.length == 1) {

				GeoElement[] geos = ec.getCompanion().rotateByAngle(polys[0],
						num, points[0]);
				if (geos != null) {
					app.storeUndoInfo();
					ec.memorizeJustCreatedGeos(geos);
				}
				return defaultRotateAngle;
			}

			ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
			for (int i = 0; i < selGeos.length; i++) {
				if (selGeos[i] != points[0]) {
					if (selGeos[i] instanceof Transformable) {
						ret.addAll(Arrays.asList(ec.getCompanion()
								.rotateByAngle(selGeos[i], num, points[0])));
					} else if (selGeos[i].isGeoPolygon()) {
						ret.addAll(Arrays.asList(ec.getCompanion()
								.rotateByAngle(selGeos[i], num, points[0])));
					}
				}
			}
			if (!ret.isEmpty()) {
				app.storeUndoInfo();
				ec.memorizeJustCreatedGeos(ret);
			}

		} else {
			app.showError(app.getLocalization().getError("NumberExpected"));
		}
		return success ? defaultRotateAngle : null;
	}

	public static boolean makeRegularPolygon(App app, EuclidianController ec,
			String inputString, GeoPointND geoPoint1, GeoPointND geoPoint2) {
		if (inputString == null || "".equals(inputString)) {
			return false;
		}

		Kernel kernel = app.getKernel();
		Construction cons = kernel.getConstruction();

		// avoid labeling of num
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoElement[] result = kernel.getAlgebraProcessor()
				.processAlgebraCommand(inputString, false);

		cons.setSuppressLabelCreation(oldVal);

		boolean success = result != null && result[0] instanceof GeoNumberValue;

		if (!success) {
			kernel.getAlgebraProcessor().showError("NumberExpected");
			return false;
		}

		GeoElement[] geos = ec.getCompanion().regularPolygon(geoPoint1,
				geoPoint2, (GeoNumberValue) result[0]);
		GeoElement[] onlypoly = { null };
		if (geos != null) {
			onlypoly[0] = geos[0];
			app.storeUndoInfo();
			ec.memorizeJustCreatedGeos(onlypoly);
		}

		return true;

	}

	protected GeoNumberValue getNumber(Kernel kernel, String message, String def) {

		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		String str = prompt(message, def);

		GeoNumberValue result = kernel.getAlgebraProcessor().evaluateToNumeric(
				str, true);

		cons.setSuppressLabelCreation(oldVal);

		return result;
	}

	protected abstract String prompt(String message, String def);

	public abstract void closeAll();

	public abstract void showRenameDialog(GeoElement geo, boolean b,
			String label, boolean c);

	public abstract void showPropertiesDialog();

	public abstract void showPropertiesDialog(ArrayList<GeoElement> geos);

	public abstract void showPropertiesDialog(OptionType type,
			ArrayList<GeoElement> geos);

	public abstract void showToolbarConfigDialog();

	public abstract void showLogInDialog();

	public abstract void showLogOutDialog();

	public static boolean doDilate(Kernel kernel, NumberValue num,
			GeoPointND[] points, GeoElement[] selGeos, EuclidianController ec) {

		if (selGeos.length > 0) {
			// mirror all selected geos
			// GeoElement [] selGeos = getSelectedGeos();
			GeoPointND point = points[0];
			ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
			for (int i = 0; i < selGeos.length; i++) {
				if (selGeos[i] != point) {
					if ((selGeos[i] instanceof Transformable)
							|| selGeos[i].isGeoList())
						ret.addAll(Arrays.asList(ec.getCompanion()
								.dilateFromPoint(selGeos[i], num, point)));
				}
			}
			if (!ret.isEmpty()) {
				ec.memorizeJustCreatedGeos(ret);
				kernel.getApplication().storeUndoInfo();
				return true;
			}
		}
		return false;
	}

	public static void doSegmentFixed(Kernel kernel, GeoPointND geoPoint1,
			NumberValue num) {


		GeoElement[] segment = kernel.getAlgoDispatcher().Segment(null,
				geoPoint1, num);
		GeoElement[] onlysegment = { null };
		if (segment != null) {
			onlysegment[0] = segment[0];
			kernel.getApplication().storeUndoInfo();
			kernel.getApplication().getActiveEuclidianView()
					.getEuclidianController()
					.memorizeJustCreatedGeos(onlysegment);
		}
	}

	/**
	 * Displays the text dialog for a given text.
	 */
	final public void showTextDialog(GeoText text) {
		showTextDialog(text, null, true);
	}

	/**
	 * Creates a new text at given startPoint
	 * 
	 * @param startPoint
	 *            start point position
	 * @param rw
	 *            true iff in real world coordinates
	 */
	final public void showTextCreationDialog(GeoPointND startPoint, boolean rw) {
		showTextDialog(null, startPoint, rw);
	}

	public abstract void openToolHelp();

	protected void showTextDialog(GeoText text, GeoPointND startPoint,
			boolean rw) {
		app.setWaitCursor();

		if (textInputDialog == null) {
			textInputDialog = createTextDialog(text, startPoint, rw);
		} else {
			textInputDialog.reInitEditor(text, startPoint, rw);
		}

		textInputDialog.setVisible(true);
		app.setDefaultCursor();
	}

	public abstract TextInputDialog createTextDialog(GeoText text,
			GeoPointND startPoint, boolean rw);

	// public abstract void showOpenFromGGTDialog();

	/**
	 * 
	 * @param title
	 * @param geoPoint
	 */
	public void showNumberInputDialogSpherePointRadius(String title,
			GeoPointND geoPoint) {
		// 3D stuff

	}

	/**
	 * for creating a cone
	 * 
	 * @param title
	 * @param a
	 *            basis center
	 * @param b
	 *            apex point
	 */
	public void showNumberInputDialogConeTwoPointsRadius(String title,
			GeoPointND a, GeoPointND b) {
		// 3D stuff

	}

	/**
	 * for creating a cylinder
	 * 
	 * @param title
	 * @param a
	 *            basis center
	 * @param b
	 *            top center
	 */
	public void showNumberInputDialogCylinderTwoPointsRadius(String title,
			GeoPointND a, GeoPointND b) {
		// 3D stuff

	}

	/**
	 * @param title
	 * @param geoPoint
	 * @param forAxis
	 * 
	 */
	public void showNumberInputDialogCirclePointDirectionRadius(String title,
			GeoPointND geoPoint, GeoDirectionND forAxis) {
		// 3D stuff

	}

	public void showNumberInputDialogRotate(String title, GeoPolygon[] polys,
			GeoLineND[] selectedLines, GeoElement[] selGeos,
			EuclidianController ec) {
		// 3D stuff

	}

	public void showColorChooserDialog(GColor originalColor,
			ColorChangeHandler handler) {
	}

}
