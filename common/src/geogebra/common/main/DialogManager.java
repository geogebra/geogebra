/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.main;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.factories.Factory;
import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.InputDialog;
import geogebra.common.gui.dialog.TextInputDialog;
import geogebra.common.gui.dialog.handler.RedefineInputHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoDependentNumber;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.Transformable;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.plugin.Operation;
import geogebra.common.util.Unicode;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class DialogManager {

	private String defaultAngle = "45" + Unicode.degree;

	protected App app;

	private Object oldString;

	/**
	 * Dialog for styling text objects.
	 */
	protected TextInputDialog textInputDialog;

	public DialogManager() {
	}

	public DialogManager(App app) {
		this.app = app;

	}

	public abstract boolean showFunctionInspector(GeoFunction geoFunction);

	public abstract void showDataSourceDialog(int mode, boolean doAutoLoadSelectedGeos);
	
	public void showRedefineDialog(GeoElement geo, boolean allowTextDialog) {
		
		/* TODO
		if (allowTextDialog && geo.isGeoText() && !geo.isTextCommand()) {
			showTextDialog((GeoText) geo);
			return;
		} */

		String str = geo.getRedefineString(false, true);

		InputHandler handler = new RedefineInputHandler(app, geo, str);
	
		InputDialog inputDialog = Factory.getPrototype().newInputDialog(app,
				geo.getNameDescription(), app.getPlain("Redefine"), str, true,
				handler, geo);


	}

	public void showNumberInputDialogSegmentFixed(String menu,
			GeoPoint geoPoint2) {
		doSegmentFixed(app.getKernel(), geoPoint2, getNumber(app.getKernel(), menu + " " + app.getPlain("Length"), ""));
	}

	public void showNumberInputDialogAngleFixed(String menu,
			GeoSegment[] selectedSegments, GeoPoint[] selectedPoints,
			GeoElement[] selGeos) {
		doAngleFixed(app.getKernel(), selectedSegments, selectedPoints, selGeos, getNumber(app.getKernel(), menu + " " + app.getPlain("Length"), ""), false);
		
	}

	public static void doAngleFixed(Kernel kernel, GeoSegment[] segments,
			GeoPoint[] points, GeoElement[] selGeo2s, GeoNumberValue num, boolean clockWise) {
		//GeoElement circle = kernel.Circle(null, geoPoint1, ((NumberInputHandler)inputHandler).getNum());
		//geogebra.gui.AngleInputDialog dialog = (geogebra.gui.AngleInputDialog) ob[1];
		//String angleText = getText();

		GeoAngle angle;
		
		if (points.length == 2) {
			angle = (GeoAngle) kernel.getAlgoDispatcher().Angle(null, points[0], points[1], num, !clockWise)[0];			
		} else {
			angle = (GeoAngle) kernel.getAlgoDispatcher().Angle(null, segments[0].getEndPoint(), segments[0].getStartPoint(), num, !clockWise)[0];
		}			

		// make sure that we show angle value
		if (angle.isLabelVisible()) 
			angle.setLabelMode(GeoElement.LABEL_NAME_VALUE);
		else 
			angle.setLabelMode(GeoElement.LABEL_VALUE);
		angle.setLabelVisible(true);		
		angle.updateRepaint();
		
		kernel.getApplication().storeUndoInfo();
		
	}

    public boolean showSliderCreationDialog(int x, int y) {
		Kernel kernel = app.getKernel();
		boolean isAngle = !confirm("OK for number, Cancel for angle");
		GeoNumeric slider = setSliderFromDefault(isAngle ? new GeoAngle(kernel.getConstruction()) :  new GeoNumeric(kernel.getConstruction()), isAngle);

		StringTemplate tmpl = StringTemplate.defaultTemplate;
		
		// convert to degrees (angle only)
		String minStr = isAngle ? kernel.format(Math.toDegrees(slider.getIntervalMin()),tmpl)+Unicode.degree
				: kernel.format(slider.getIntervalMin(), tmpl);
		String maxStr = isAngle ? kernel.format(Math.toDegrees(slider.getIntervalMax()),tmpl)+Unicode.degree
				: kernel.format(slider.getIntervalMax(), tmpl);
		String incStr = isAngle ? kernel.format(Math.toDegrees(slider.getAnimationStep()),tmpl)+Unicode.degree
				: kernel.format(slider.getAnimationStep(), tmpl);
		
		// get input from user
		NumberValue min = getNumber(kernel, "Enter minimum", minStr);
		NumberValue max = getNumber(kernel, "Enter maximum", maxStr);
		NumberValue increment = getNumber(kernel, "Enter increment", incStr);
		
		if (min != null) slider.setIntervalMin(min);
		if (max != null) slider.setIntervalMax(max);
		if (increment != null) slider.setAnimationStep(increment);
		
		slider.setLabel(null);
		slider.setValue(isAngle ? 45 * Math.PI/180 : 1);
		slider.setSliderLocation(x, y, true);
		slider.setEuclidianVisible(true);
		
		slider.setLabelMode(GeoElement.LABEL_NAME_VALUE);
		slider.setLabelVisible(true);
		slider.update();
		//slider.setRandom(cbRandom.isSelected());

		app.storeUndoInfo();

		return true;
    }


    protected abstract boolean confirm(String string);

	public void showNumberInputDialogRotate(String menu,
            GeoPolygon[] selectedPolygons, GeoPointND[] selectedPoints,
            GeoElement[] selGeos) {
		String inputString = prompt(menu + " " + app.getPlain("Angle"), defaultAngle);
		
		defaultAngle = rotateObject(app, inputString, false, selectedPolygons, selectedPoints, selGeos);
	    
    }

	public  void showNumberInputDialogDilate(String menu,
			GeoPolygon[] selectedPolygons, GeoPoint[] selectedPoints,
			GeoElement[] selGeos) {
		doDilate(app.getKernel(), getNumber(app.getKernel(), menu + " " + app.getPlain("Numeric"), ""), selectedPoints, selGeos);
	}

	public void showNumberInputDialogRegularPolygon(String menu,
			GeoPoint geoPoint1, GeoPoint geoPoint2) {
		
		String inputString = prompt(menu + " " + app.getPlain("Points"), "4");
		
		makeRegularPolygon(app, inputString, geoPoint1, geoPoint2);
	}

	public abstract void showBooleanCheckboxCreationDialog(GPoint loc, GeoBoolean bool);

	public void showNumberInputDialogCirclePointRadius(String menu,
			GeoPointND geoPointND, EuclidianView view) {
		
		Kernel kernel = geoPointND.getKernel();
		
		NumberValue num = getNumber(kernel, menu, "");
		
		GeoConic circle = geoPointND.getKernel().getAlgoDispatcher().Circle(null, (GeoPoint) geoPointND, num);
		
		GeoElement[] geos = { circle };
		app.storeUndoInfo();
		app.getActiveEuclidianView().getEuclidianController().memorizeJustCreatedGeos(geos);

	}

	public abstract NumberValue showNumberInputDialog(String title, String message,
			String initText);
	
	public abstract NumberValue showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText);

	public abstract Object[] showAngleInputDialog(String title, String message,
			String initText);

	public abstract boolean showButtonCreationDialog(int x, int y, boolean textfield);

	public static String rotateObject(App app, String inputText,
			boolean clockwise, GeoPolygon[] polys, GeoPointND[] points,
			GeoElement[] selGeos) {	
		String defaultRotateAngle = "45" + "\u00b0";		String angleText = inputText;
		Kernel kernel = app.getKernel();
		

		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);


		// negative orientation ?
		if (clockwise) {
			inputText = "-(" + inputText + ")";
		}

		GeoElement[] result = kernel.getAlgebraProcessor().processAlgebraCommand(inputText, false);

		cons.setSuppressLabelCreation(oldVal);


		boolean success = result != null && result[0].isNumberValue();

		if (success) {
			// GeoElement circle = kernel.Circle(null, geoPoint1,
			// ((NumberInputHandler)inputHandler).getNum());
			GeoNumberValue num = (GeoNumberValue) result[0];
			// geogebra.gui.AngleInputDialog dialog =
			// (geogebra.gui.AngleInputDialog) ob[1];

			// keep angle entered if it ends with 'degrees'
			if (angleText.endsWith("\u00b0"))
				defaultRotateAngle = angleText;


			if (polys.length == 1) {

				GeoElement[] geos = kernel.getAlgoDispatcher().Rotate(null, polys[0], num,
						(GeoPoint) points[0]);
				if (geos != null) {
					app.storeUndoInfo();
					kernel.getApplication().getActiveEuclidianView()
					.getEuclidianController()
					.memorizeJustCreatedGeos(geos);
				}
				return defaultRotateAngle;
			}

			
			ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
			for (int i = 0; i < selGeos.length; i++) {
				if (selGeos[i] != points[0]) {
					if (selGeos[i] instanceof Transformable) {
						ret.addAll(Arrays.asList(kernel.getAlgoDispatcher().Rotate(null,
								selGeos[i], num, (GeoPoint) points[0])));
					} else if (selGeos[i].isGeoPolygon()) {
						ret.addAll(Arrays.asList(kernel.getAlgoDispatcher().Rotate(null,
								selGeos[i], num, (GeoPoint) points[0])));
					}
				}
			}
			if (!ret.isEmpty()) {
				app.storeUndoInfo();
				kernel.getApplication().getActiveEuclidianView()
				.getEuclidianController().memorizeJustCreatedGeos(ret);
			}
			
		}
		return defaultRotateAngle;
	}


	public static boolean makeRegularPolygon(App app, String inputString, GeoPoint geoPoint1, GeoPoint geoPoint2) {
		if (inputString == null || "".equals(inputString) ) {
			return false;
		}

		Kernel kernel = app.getKernel();
		Construction cons = kernel.getConstruction();

		// avoid labeling of num
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoElement[] result = kernel.getAlgebraProcessor().processAlgebraCommand(inputString, false);

		cons.setSuppressLabelCreation(oldVal);


		boolean success = result != null && result[0].isNumberValue();

		if (!success) {
			return false;
		}


		GeoElement[] geos = kernel.getAlgoDispatcher().RegularPolygon(null, geoPoint1, geoPoint2, (NumberValue) result[0]);
		GeoElement[] onlypoly = { null };
		if (geos != null) {
			onlypoly[0] = geos[0];
			app.storeUndoInfo();
			app.getActiveEuclidianView().getEuclidianController().memorizeJustCreatedGeos(onlypoly);
		}

		return true;

	}
	
	protected GeoNumberValue getNumber(Kernel kernel, String message, String def) {
		
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		
		String str = prompt(message, def);

		GeoNumberValue result = kernel.getAlgebraProcessor().evaluateToNumeric(str, true);

		cons.setSuppressLabelCreation(oldVal);
		
		return result;
    }

	protected abstract String prompt(String message, String def);

	public static GeoNumeric setSliderFromDefault(GeoNumeric num, boolean isAngle) {
		GeoNumeric defaultNum = num.getKernel().getAlgoDispatcher().getDefaultNumber(isAngle);		
		num.setSliderFixed(defaultNum.isSliderFixed());		
		num.setEuclidianVisible(true);
		num.setIntervalMin((GeoNumeric)defaultNum.getIntervalMinObject());
		num.setIntervalMax((GeoNumeric)defaultNum.getIntervalMaxObject());
		num.setAbsoluteScreenLocActive(true);
		num.setAnimationType(defaultNum.getAnimationType());
		num.setSliderWidth(defaultNum.getSliderWidth());
		num.setRandom(defaultNum.isRandom());
		return num;
	}

	public abstract void closeAll();

	public abstract void showRenameDialog(GeoElement geo, boolean b, String label,
			boolean c);


	public abstract void showPropertiesDialog();

	public abstract void showPropertiesDialog(ArrayList<GeoElement> geos);

	public abstract void showPropertiesDialog(OptionType type,ArrayList<GeoElement> geos);
	
	public abstract void showToolbarConfigDialog();

	public static boolean doDilate(Kernel kernel, NumberValue num, GeoPoint[] points, GeoElement[] selGeos) {

		if (selGeos.length > 0) {					
			// mirror all selected geos
			//GeoElement [] selGeos = getSelectedGeos();
			GeoPoint point = points[0];
			ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
			for (int i=0; i < selGeos.length; i++) {				
				if (selGeos[i] != point) {
					if ((selGeos[i] instanceof Transformable) || selGeos[i].isGeoList())
						ret.addAll(Arrays.asList(kernel.getAlgoDispatcher().Dilate(null,  selGeos[i], num, point)));
				}
			}
			if (!ret.isEmpty()) {
				kernel.getApplication().getActiveEuclidianView().getEuclidianController().memorizeJustCreatedGeos(ret);
				kernel.getApplication().storeUndoInfo();
				return true;
			}
		}
		return false;
	}
	
	public static void doSegmentFixed(Kernel kernel, GeoPoint geoPoint1, NumberValue num) {

		// apply abs() to number so that entering -3 doesn't give an undefined point
	 	ExpressionNode en = new ExpressionNode(kernel, num, Operation.ABS, null);
	 	AlgoDependentNumber algo = new AlgoDependentNumber(kernel.getConstruction(), en, false);
	 	
	 	GeoElement[] segment = kernel.getAlgoDispatcher().Segment(null, geoPoint1, algo.getNumber());
		GeoElement[] onlysegment = { null };
		if (segment != null) {
			onlysegment[0] = segment[0];
			kernel.getApplication().storeUndoInfo();
			kernel.getApplication().getActiveEuclidianView().getEuclidianController().memorizeJustCreatedGeos(onlysegment);
		}
	}

	/**
	 * Displays the text dialog for a given text.
	 */
	final public void showTextDialog(GeoText text) {
		showTextDialog(text, null);
	}

	/**
	 * Creates a new text at given startPoint
	 */
	final public void showTextCreationDialog(GeoPointND startPoint) {
		showTextDialog(null, startPoint);
	}
	
	
	public abstract void openToolHelp();

	protected void showTextDialog(GeoText text, GeoPointND startPoint) {		
		app.setWaitCursor();
	
		if (textInputDialog == null) {
			textInputDialog = createTextDialog(text, startPoint);
		} else {
			textInputDialog.reInitEditor(text, startPoint);
		}
	
		textInputDialog.setVisible(true);
		app.setDefaultCursor();
	
	}

	public TextInputDialog createTextDialog(GeoText text, GeoPointND startPoint) {
		boolean isTextMode = app.getMode() == EuclidianConstants.MODE_TEXT;
		TextInputDialog id = app.getFactory().newTextInputDialog(app,
				app.getPlain("Text"), text, startPoint, 30, 6, isTextMode);
		return id;
	}




}
