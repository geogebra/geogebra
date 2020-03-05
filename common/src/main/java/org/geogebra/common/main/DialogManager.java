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

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.Export3dDialogInterface;
import org.geogebra.common.gui.dialog.InputDialog;
import org.geogebra.common.gui.dialog.TextInputDialog;
import org.geogebra.common.gui.dialog.handler.RedefineInputHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

import com.himamis.retex.editor.share.util.Unicode;

public abstract class DialogManager {

	protected App app;
	protected Localization loc;

	/**
	 * Dialog for styling text objects.
	 */
	protected TextInputDialog textInputDialog;

	public DialogManager() {
	}

	/**
	 * @param app
	 *            application
	 */
	public DialogManager(App app) {
		this.app = app;
		this.loc = app.getLocalization();
	}

	public abstract boolean showFunctionInspector(GeoFunction geoFunction);

	public abstract void showDataSourceDialog(int mode,
			boolean doAutoLoadSelectedGeos);

	/**
	 * @param geo
	 *            redefined element
	 * @param allowTextDialog
	 *            whether to allow text dialog instead
	 */
	public void showRedefineDialog(GeoElement geo, boolean allowTextDialog) {
		if (allowTextDialog && geo.isGeoText() && !geo.isTextCommand()) {
			showTextDialog((GeoText) geo);
			return;
		}

		String str = geo.getRedefineString(false, true);

		InputHandler handler = new RedefineInputHandler(app, geo, str);

		newInputDialog(app, geo.getNameDescription(), loc.getMenu("Redefine"),
				str, handler, geo);
	}

	public abstract InputDialog newInputDialog(App app1, String message,
			String title, String initString, InputHandler handler,
			GeoElement geo);

	public abstract void showNumberInputDialogSegmentFixed(String menu,
			GeoPointND geoPoint2);

	/**
	 * 
	 * @param menu
	 *            title
	 * @param selectedSegments
	 *            segments
	 * @param selectedPoints
	 *            points
	 * @param selGeos
	 *            selected elements
	 * @param ec
	 *            controller
	 */
	public abstract void showNumberInputDialogAngleFixed(String menu,
			GeoSegmentND[] selectedSegments, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec);

	/**
	 * @param kernel
	 *            kernel
	 * @param segments
	 *            selected segments
	 * @param points
	 *            selected points
	 * @param num
	 *            angle value
	 * @param clockWise
	 *            whether to use clockwise angle
	 * @param ec
	 *            controller
	 */
	public static void doAngleFixed(Kernel kernel, GeoSegmentND[] segments,
			GeoPointND[] points, GeoNumberValue num,
			boolean clockWise, EuclidianController ec) {
		if (points.length == 2) {
			ec.getCompanion().createAngle(points[0], points[1], num,
					clockWise);
		} else {
			ec.getCompanion().createAngle(segments[0].getEndPoint(),
					segments[0].getStartPoint(), num, clockWise);
		}

		kernel.getApplication().storeUndoInfoAndStateForModeStarting();
	}

	/**
	 * @param x
	 *            x position
	 * @param y
	 *            y position
	 * @return success
	 */
	public abstract boolean showSliderCreationDialog(int x, int y);

	/**
	 * @param menu
	 *            title
	 * @param selectedPolygons
	 *            selected polygons
	 * @param selectedPoints
	 *            selected points
	 * @param selGeos
	 *            selected geos
	 * @param ec
	 *            controller
	 */
	public abstract void showNumberInputDialogRotate(String menu,
			GeoPolygon[] selectedPolygons, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec);

	public abstract void showNumberInputDialogDilate(String menu,
			GeoPolygon[] selectedPolygons, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec);

	/**
	 * Regular polygon dialog for 2D
	 * 
	 * @param menu
	 *            title
	 * @param ec
	 *            controller
	 * @param geoPoint1
	 *            first vertex
	 * @param geoPoint2
	 *            second vertex
	 */
	final public void showNumberInputDialogRegularPolygon(String menu,
			EuclidianController ec, GeoPointND geoPoint1,
			GeoPointND geoPoint2) {
		showNumberInputDialogRegularPolygon(menu, ec, geoPoint1, geoPoint2,
				null);
	}

	/**
	 * Regular polygon dialog for 3D
	 * 
	 * @param title
	 *            title
	 * @param ec
	 *            controller
	 * @param geoPoint1
	 *            first vertex
	 * @param geoPoint2
	 *            second vertex
	 * @param direction
	 *            direction
	 */
	abstract public void showNumberInputDialogRegularPolygon(String title,
			EuclidianController ec, GeoPointND geoPoint1, GeoPointND geoPoint2,
			GeoCoordSys2D direction);

	public abstract void showBooleanCheckboxCreationDialog(GPoint corner,
			GeoBoolean bool);

	public abstract void showNumberInputDialogCirclePointRadius(String title,
			GeoPointND geoPointND, EuclidianView view);

	public abstract void showNumberInputDialog(String title, String message,
			String initText, AsyncOperation<GeoNumberValue> callback);

	public abstract void showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText,
			AsyncOperation<GeoNumberValue> callback);

	public abstract void showAngleInputDialog(String title, String message,
			String initText, AsyncOperation<GeoNumberValue> callback);

	public abstract boolean showButtonCreationDialog(int x, int y,
			boolean textfield);

	public interface CreateGeoForRotate {
		public GeoElement[] createGeos(EuclidianController ec, GeoElement geo,
				GeoNumberValue num);

		public GeoElementND getPivot();
	}

	public static class CreateGeoForRotatePoint implements CreateGeoForRotate {

		private GeoPointND point;

		public CreateGeoForRotatePoint(GeoPointND point) {
			this.point = point;
		}

		@Override
		public GeoElement[] createGeos(EuclidianController ec, GeoElement geo,
				GeoNumberValue num) {
			return ec.getCompanion().rotateByAngle(geo, num, point);
		}

		@Override
		public GeoElementND getPivot() {
			return point;
		}
	}

	public static class CreateGeoForRotateLine implements CreateGeoForRotate {

		private GeoLineND line;

		public CreateGeoForRotateLine(GeoLineND line) {
			this.line = line;
		}

		@Override
		public GeoElement[] createGeos(EuclidianController ec, GeoElement geo,
				GeoNumberValue num) {
			return ec.getKernel().getManager3D().rotate3D(null, geo, num, line);
		}

		@Override
		public GeoElementND getPivot() {
			return line;
		}
	}

	/**
	 * @param app
	 *            app
	 * @param angleText
	 *            angle
	 * @param clockwise
	 *            whether angle is clockwise
	 * @param polys
	 *            selected polygons
	 * @param creator
	 *            rotated geo creator
	 * @param selGeos
	 *            selected geos
	 * @param ec
	 *            controller
	 * @param eh
	 *            error handler
	 * @param callback
	 *            callback
	 */
	public static void rotateObject(final App app, final String angleText,
			boolean clockwise, final GeoPolygon[] polys,
			final CreateGeoForRotate creator, final GeoElement[] selGeos,
			final EuclidianController ec, final ErrorHandler eh,
			final AsyncOperation<String> callback) {

		String inputText = angleText;
		final Kernel kernel = app.getKernel();

		// avoid labeling of num
		final Construction cons = kernel.getConstruction();
		final boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// negative orientation ?
		if (ec.getCompanion().viewOrientationForClockwise(clockwise, creator)) {
			inputText = "-(" + inputText + ")";
		}

		kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
				inputText, false, eh, true,
				new AsyncOperation<GeoElementND[]>() {

					@Override
					public void callback(GeoElementND[] result) {
						cons.setSuppressLabelCreation(oldVal);
						String defaultRotateAngle = Unicode.FORTY_FIVE_DEGREES_STRING;
						boolean success = result != null && result.length > 0
								&& result[0] instanceof GeoNumberValue;

						if (success) {
							GeoNumberValue num = (GeoNumberValue) result[0];
							// keep angle entered if it ends with
							// 'degrees'
							if (angleText.endsWith(Unicode.DEGREE_STRING)) {
								defaultRotateAngle = angleText;
							}

							if (polys.length == 1) {

								GeoElement[] geos = creator.createGeos(ec,
										polys[0], num);
								if (geos != null) {
									app.storeUndoInfoAndStateForModeStarting();
									ec.memorizeJustCreatedGeos(geos);
									kernel.notifyRepaint();
								}
								if (callback != null) {
									callback.callback(defaultRotateAngle);
								}
								return;
							}
							ArrayList<GeoElement> ret = new ArrayList<>();
							for (int i = 0; i < selGeos.length; i++) {
								if (selGeos[i] != creator.getPivot()) {
									if (selGeos[i] instanceof Transformable) {
										ret.addAll(Arrays
												.asList(creator.createGeos(ec,
														selGeos[i], num)));
									} else if (selGeos[i].isGeoPolygon()) {
										ret.addAll(Arrays
												.asList(creator.createGeos(ec,
														selGeos[i], num)));
									}
								}
							}
							if (!ret.isEmpty()) {
								app.storeUndoInfoAndStateForModeStarting();
								ec.memorizeJustCreatedGeos(ret);
								kernel.notifyRepaint();
							}

						} else {
							if (result != null && result.length > 0) {
								numberExpectedError(eh, app);
							}
						}
						if (callback != null) {
							callback.callback(
									success ? defaultRotateAngle : null);
						}

					}
				});
	}

	public void showAudioInputDialog() {
		// nothing to do here
	}

	public void showVideoInputDialog() {
		// nothing to do here -> implemented in extended class
	}

	/**
	 * @param app
	 *            app
	 * @param ec
	 *            controller
	 * @param inputString
	 *            input
	 * @param geoPoint1
	 *            first vertex
	 * @param geoPoint2
	 *            second vertex
	 * @param direction
	 *            direction
	 * @param handler
	 *            error handler
	 * @param cb
	 *            callback
	 */
	public static void makeRegularPolygon(final App app,
			final EuclidianController ec, String inputString,
			final GeoPointND geoPoint1, final GeoPointND geoPoint2,
			final GeoCoordSys2D direction, final ErrorHandler handler,
			final AsyncOperation<Boolean> cb) {
		if (inputString == null || "".equals(inputString)) {
			if (cb != null) {
				cb.callback(false);
			}
			return;
		}

		final Kernel kernel = app.getKernel();
		final Construction cons = kernel.getConstruction();

		// avoid labeling of num
		final boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		AsyncOperation<GeoElementND[]> checkNumber = new AsyncOperation<GeoElementND[]>() {
			@Override
			public void callback(GeoElementND[] result) {

				cons.setSuppressLabelCreation(oldVal);

				boolean success = result != null
						&& result[0] instanceof GeoNumberValue;

				if (!success) {
					numberExpectedError(handler, app);
					if (cb != null) {
						cb.callback(false);
					}
					return;
				}

				GeoElement[] geos = ec.getCompanion().regularPolygon(geoPoint1,
						geoPoint2, (GeoNumberValue) result[0], direction);
				GeoElement[] onlypoly = { null };
				if (geos != null) {
					onlypoly[0] = geos[0];
					app.storeUndoInfoAndStateForModeStarting();
					ec.memorizeJustCreatedGeos(onlypoly);
				}
				if (cb != null) {
					cb.callback(success);
				}
			}
		};

		kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
				inputString, false, handler, true, checkNumber);
	}

	public abstract void closeAll();

	public abstract void showRenameDialog(GeoElement geo, boolean b,
			String label, boolean c);

	public abstract void showPropertiesDialog(ArrayList<GeoElement> geos);

	public abstract void showPropertiesDialog(OptionType type,
			ArrayList<GeoElement> geos);

	/**
	 * @param kernel
	 *            kernel
	 * @param num
	 *            dilation factor
	 * @param points
	 *            selected points
	 * @param selGeos
	 *            selected geos
	 * @param ec
	 *            controller
	 * @return success
	 */
	public static boolean doDilate(Kernel kernel, GeoNumberValue num,
			GeoPointND[] points, GeoElement[] selGeos, EuclidianController ec) {

		if (selGeos.length > 0) {
			// mirror all selected geos
			// GeoElement [] selGeos = getSelectedGeos();
			GeoPointND point = points[0];
			ArrayList<GeoElement> ret = new ArrayList<>();
			for (int i = 0; i < selGeos.length; i++) {
				if (selGeos[i] != point) {
					if ((selGeos[i] instanceof Transformable)
							|| selGeos[i].isGeoList()) {
						ret.addAll(Arrays.asList(ec.getCompanion()
								.dilateFromPoint(selGeos[i], num, point)));
					}
				}
			}
			if (!ret.isEmpty()) {
				ec.memorizeJustCreatedGeos(ret);
				kernel.getApplication().storeUndoInfoAndStateForModeStarting();
				return true;
			}
		}
		return false;
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param geoPoint1
	 *            start point
	 * @param num
	 *            length
	 */
	public static void doSegmentFixed(Kernel kernel, GeoPointND geoPoint1,
			GeoNumberValue num) {

		GeoElement[] segment = kernel.getAlgoDispatcher().segment(null,
				geoPoint1, num);
		GeoElement[] onlysegment = { null };
		if (segment != null) {
			onlysegment[0] = segment[0];
			kernel.getApplication().storeUndoInfoAndStateForModeStarting();
			kernel.getApplication().getActiveEuclidianView()
					.getEuclidianController()
					.memorizeJustCreatedGeos(onlysegment);
		}
	}

	/**
	 * Displays the text dialog for a given text.
	 * 
	 * @param text
	 *            text
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
	final public void showTextCreationDialog(GeoPointND startPoint,
			boolean rw) {
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
	 * @param ec
	 *            controller
	 * @param title
	 *            title
	 * @param geoPoint
	 *            point
	 */
	public void showNumberInputDialogSpherePointRadius(String title,
			GeoPointND geoPoint, EuclidianController ec) {
		// 3D stuff

	}

	/**
	 * for creating a cone
	 * 
	 * @param title
	 *            title
	 * @param a
	 *            basis center
	 * @param b
	 *            apex point
	 * @param ec
	 *            controller
	 */
	public void showNumberInputDialogConeTwoPointsRadius(String title,
			GeoPointND a, GeoPointND b, EuclidianController ec) {
		// 3D stuff

	}

	/**
	 * for creating a cylinder
	 * 
	 * @param title
	 *            title
	 * @param a
	 *            basis center
	 * @param b
	 *            top center
	 * @param ec
	 *            controller
	 */
	public void showNumberInputDialogCylinderTwoPointsRadius(String title,
			GeoPointND a, GeoPointND b, EuclidianController ec) {
		// 3D stuff
	}

	/**
	 * @param ec
	 *            controller
	 * @param title
	 *            title
	 * @param geoPoint
	 *            point
	 * @param forAxis
	 *            axis
	 * 
	 */
	public void showNumberInputDialogCirclePointDirectionRadius(String title,
			GeoPointND geoPoint, GeoDirectionND forAxis,
			EuclidianController ec) {
		// 3D stuff
	}

	/**
	 * @param title
	 *            title
	 * @param polys
	 *            polygons
	 * @param selectedLines
	 *            lines
	 * @param selGeos
	 *            selected geos
	 * @param ec
	 *            controller
	 */
	public void showNumberInputDialogRotate(String title, GeoPolygon[] polys,
			GeoLineND[] selectedLines, GeoElement[] selGeos,
			EuclidianController ec) {
		// 3D stuff
	}

	public boolean hasFunctionInspector() {
		return false;
	}

	public interface CreateGeoFromRadius {
		public GeoElement createGeo(Kernel kernel, GeoNumberValue num);
	}

	public static class CreateSphereFromRadius implements CreateGeoFromRadius {

		private GeoPointND point;

		public CreateSphereFromRadius(GeoPointND point) {
			this.point = point;
		}

		@Override
		public GeoElement createGeo(Kernel kernel, GeoNumberValue num) {
			return kernel.getManager3D().sphere(null, point, num);
		}
	}

	public static class CreateConeFromRadius implements CreateGeoFromRadius {

		private GeoPointND point1;
		private GeoPointND point2;

		/**
		 * @param point1
		 *            bottom center
		 * @param point2
		 *            apex
		 */
		public CreateConeFromRadius(GeoPointND point1, GeoPointND point2) {
			this.point1 = point1;
			this.point2 = point2;
		}

		@Override
		public GeoElement createGeo(Kernel kernel, GeoNumberValue num) {
			return kernel.getManager3D().coneLimited(null, point1, point2,
					num)[0];
		}
	}

	public static class CreateCylinderFromRadius
			implements CreateGeoFromRadius {

		private GeoPointND point1;
		private GeoPointND point2;

		/**
		 * @param point1
		 *            bottom center
		 * @param point2
		 *            top center
		 */
		public CreateCylinderFromRadius(GeoPointND point1, GeoPointND point2) {
			this.point1 = point1;
			this.point2 = point2;
		}

		@Override
		public GeoElement createGeo(Kernel kernel, GeoNumberValue num) {
			return kernel.getManager3D().cylinderLimited(null, point1, point2,
					num)[0];
		}
	}

	public static class CreateCircleFromDirectionRadius
			implements CreateGeoFromRadius {

		private GeoPointND point;
		private GeoDirectionND forAxis;

		/**
		 * @param point
		 *            center
		 * @param forAxis
		 *            plane direction
		 */
		public CreateCircleFromDirectionRadius(GeoPointND point,
				GeoDirectionND forAxis) {
			this.point = point;
			this.forAxis = forAxis;
		}

		@Override
		public GeoElement createGeo(Kernel kernel, GeoNumberValue num) {
			return kernel.getManager3D().circle3D(null, point, num, forAxis);
		}
	}

	public static class CreateCircleFromRadius implements CreateGeoFromRadius {

		private GeoPointND point;

		public CreateCircleFromRadius(GeoPointND point) {
			this.point = point;
		}

		@Override
		public GeoElement createGeo(Kernel kernel, GeoNumberValue num) {
			return kernel.getAlgoDispatcher().circle(null, point, num);
		}
	}

	/**
	 * @param app
	 *            app
	 * @param ec
	 *            controller
	 * @param inputString
	 *            input
	 * @param createGeoFromRadius
	 *            radius handler
	 * @param handler
	 *            error handler
	 * @param callback
	 *            success callback
	 */
	public static void makeGeoPointRadius(final App app,
			final EuclidianController ec, String inputString,
			final CreateGeoFromRadius createGeoFromRadius,
			final ErrorHandler handler,
			final AsyncOperation<Boolean> callback) {
		if (inputString == null || "".equals(inputString)) {
			if (callback != null) {
				callback.callback(false);
			}
			return;
		}

		final Kernel kernel = app.getKernel();
		final Construction cons = kernel.getConstruction();

		// avoid labeling of num
		final boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
				inputString, false, handler, true,
				new AsyncOperation<GeoElementND[]>() {

					@Override
					public void callback(GeoElementND[] result) {
						cons.setSuppressLabelCreation(oldVal);

						boolean success = result != null
								&& result[0] instanceof GeoNumberValue;
						if (!success) {
							numberExpectedError(handler, app);
							if (callback != null) {
								callback.callback(false);
							}
							return;
						}

						GeoElement geo = createGeoFromRadius.createGeo(kernel,
								(GeoNumberValue) result[0]);

						GeoElement[] onlypoly = { null };
						if (geo != null) {
							onlypoly[0] = geo;
							app.storeUndoInfoAndStateForModeStarting();
							ec.memorizeJustCreatedGeos(onlypoly);
							kernel.notifyRepaint();
						}
						if (callback != null) {
							callback.callback(geo != null);
						}

					}
				});

	}

	/**
	 * @param kernel
	 *            kernel
	 * @param userInput
	 *            input
	 * @param clockwise
	 *            clockwise?
	 * @param handler
	 *            error handler
	 * @param segments
	 *            selected segments
	 * @param points
	 *            selected points
	 * @param callback
	 *            callback
	 * @param ec
	 *            controller
	 */
	public static void createAngleFixed(final Kernel kernel,
			final String userInput,
			final boolean clockwise, final ErrorHandler handler,
			final GeoSegmentND[] segments, final GeoPointND[] points,
			final AsyncOperation<Boolean> callback,
			final EuclidianController ec) {
		String inputText = userInput;
		// avoid labeling of num
		final Construction cons = kernel.getConstruction();
		final boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// negative orientation ?
		if (clockwise) {
			inputText = "-(" + inputText + ")";
		}

		kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
				inputText, false, handler, true,
				new AsyncOperation<GeoElementND[]>() {

					@Override
					public void callback(GeoElementND[] result) {
						cons.setSuppressLabelCreation(oldVal);

						boolean success = result != null
								&& result[0] instanceof GeoNumberValue;
						if (!success) {
							numberExpectedError(handler, kernel.getApplication());
							if (callback != null) {
								callback.callback(false);
							}
							return;
						}

						DialogManager.doAngleFixed(kernel, segments, points,
								(GeoNumberValue) result[0], clockwise, ec);
						if (callback != null) {
							callback.callback(true);
						}
					}
				});
	}

	/**
	 * @param app
	 *            app
	 * @param inputString
	 *            input
	 * @param creator
	 *            input handler
	 * @param changeSign
	 *            whether to change sign of the number
	 * @param handler
	 *            error handler
	 * @param callback
	 *            success callback
	 */
	public static void makeGeoFromNumber(final App app, String inputString,
			final AsyncOperation<GeoNumberValue> creator,
			final boolean changeSign, final ErrorHandler handler,
			final AsyncOperation<Boolean> callback) {
		if (inputString == null || "".equals(inputString)) {
			if (callback != null) {
				callback.callback(false);
			}
			return;
		}

		final Kernel kernel = app.getKernel();
		final Construction cons = kernel.getConstruction();

		// avoid labeling of num
		final boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// handle change sign
		String inputWithSign;
		if (changeSign) {
			StringBuilder sb = new StringBuilder();
			sb.append("-(");
			sb.append(inputString);
			sb.append(")");
			inputWithSign = sb.toString();
		} else {
			inputWithSign = inputString;
		}

		kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
				inputWithSign, false, handler, true,
				new AsyncOperation<GeoElementND[]>() {

					@Override
					public void callback(GeoElementND[] result) {

						cons.setSuppressLabelCreation(oldVal);

						boolean success = result != null
								&& result[0] instanceof GeoNumberValue;
						if (!success) {
							numberExpectedError(handler, app);
							if (callback != null) {
								callback.callback(false);
							}
							return;
						}

						creator.callback((GeoNumberValue) result[0]);

						if (callback != null) {
							callback.callback(success);
						}

					}
				});
	}

	protected static void numberExpectedError(ErrorHandler handler, App app) {
		handler.showError(Errors.NumberExpected.getError(app.getLocalization()));
	}

	/**
	 * 
	 * @param base64Image
	 *            optional image
	 */
	public void showExportImageDialog(String base64Image) {
		// overridden in web
	}

	/**
	 * show pdf input dialog
	 */
	public void showPDFInputDialog() {
		// overridden in web
	}

	/**
	 * Show print preview
	 */
	public void showPrintPreview() {
		// overriden in web
	}

	/**
	 * Show embed dialog in web.
	 */
	public void showEmbedDialog() {
		// only needed in web
	}

	/**
	 * @param geo
	 *            function to add to able view after success
	 */
	public void openTableViewDialog(GeoElement geo) {
		// only needed in web
	}

	/**
	 * @param view
	 *            exported view
	 * @return dialog for export 3D settings
	 * 
	 */
	public Export3dDialogInterface getExport3dDialog(View view) {
		// implemented only in web
		return null;
	}

	public void showTemplateChooser() {
		// only needed in web
	}

	public void closeTemplateChooser() {
		// only needed in web
	}
}
