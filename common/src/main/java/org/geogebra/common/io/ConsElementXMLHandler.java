package org.geogebra.common.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.MacroConstruction;
import org.geogebra.common.kernel.algos.ChartStyle;
import org.geogebra.common.kernel.algos.ChartStyleAlgo;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.kernel.geos.HasAlignment;
import org.geogebra.common.kernel.geos.HasDynamicCaption;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.kernel.geos.LimitedPath;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.geos.RectangleTransformable;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.geos.properties.Auxiliary;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.CoordStyle;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DInterface;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable.LevelOfDetail;
import org.geogebra.common.kernel.prover.AlgoProve;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.JsReference;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.util.SpreadsheetTraceSettings;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

/**
 * XML handler for GeoElement properties
 */
public class ConsElementXMLHandler {
	/**
	 * we used minimal text size of 4px until 4.0 for texts, because the font
	 * size setting was additive. Not needed with current multiplicative
	 * approach, just for opening old files.
	 */
	private static final double MIN_TEXT_SIZE = 4;
	private GeoElement geo;
	// List of LocateableExpPair objects
	// for setting the start points at the end of the construction
	// (needed for GeoText and GeoVector)
	private LinkedList<LocateableExpPair> startPointList = new LinkedList<>();

	// List of GeoExpPair objects
	// for setting the linked geos needed for GeoTextFields
	private LinkedList<GeoExpPair> linkedGeoList = new LinkedList<>();

	// List of GeoExpPair condition objects
	// for setting the conditions at the end of the construction
	// (needed for GeoText and GeoVector)
	private LinkedList<GeoExpPair> showObjectConditionList = new LinkedList<>();
	private LinkedList<GeoExpPair> dynamicColorList = new LinkedList<>();
	private LinkedList<GeoExpPair> animationSpeedList = new LinkedList<>();
	private LinkedList<GeoExpPair> animationStepList = new LinkedList<>();
	private LinkedList<GeoExpPair> dynamicCaptionList = new LinkedList<>();
	private LinkedList<GeoElement> animatingList = new LinkedList<>();
	private LinkedList<GeoNumericMinMax> minMaxList = new LinkedList<>();
	private boolean lineStyleTagProcessed;
	private boolean symbolicTagProcessed;
	private boolean sliderTagProcessed;
	private boolean fontTagProcessed;
	private boolean setEigenvectorsCalled = false;
	private double embedX;
	private double embedY;
	/**
	 * The point style of the document, for versions < 3.3
	 */
	private int docPointStyle;
	@Weak
	private App app;
	@Weak
	private MyXMLHandler xmlHandler;
	private boolean needsConstructionDefaults;

	private static class GeoExpPair {
		private GeoElement geoElement;
		String exp;

		GeoExpPair(GeoElement g, String exp) {
			setGeo(g);
			this.exp = exp;
		}

		GeoElement getGeo() {
			return geoElement;
		}

		void setGeo(GeoElement geo) {
			this.geoElement = geo;
		}
	}

	private static class GeoNumericMinMax {
		private GeoElement geoElement;
		String min;
		String max;

		GeoNumericMinMax(GeoElement g, String min, String max) {
			setGeo(g);
			this.min = min;
			this.max = max;
		}

		GeoElement getGeo() {
			return geoElement;
		}

		void setGeo(GeoElement geo) {
			this.geoElement = geo;
		}
	}

	private static class LocateableExpPair {
		Locateable locateable;
		String exp; // String with expression to create point
		GeoPointND point; // free point
		int number; // number of startPoint

		LocateableExpPair(Locateable g, String s, int n) {
			locateable = g;
			exp = s;
			number = n;
		}

		LocateableExpPair(Locateable g, GeoPointND p, int n) {
			locateable = g;
			point = p;
			number = n;
		}
	}

	/**
	 * @param myXMLHandler
	 *            XML handler
	 * @param app
	 *            app
	 */
	public ConsElementXMLHandler(MyXMLHandler myXMLHandler, App app) {
		this.xmlHandler = myXMLHandler;
		this.app = app;
	}

	public void setNeedsConstructionDefaults(boolean needsConstructionDefaults) {
		this.needsConstructionDefaults = needsConstructionDefaults;
	}

	private boolean handleCurveParam(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoVec3D)) {
			Log.debug("wrong element type for <curveParam>: " + geo.getClass());
			return false;
		}
		GeoVec3D v = (GeoVec3D) geo;

		try {
			String tAttr = attrs.get("t");

			if (tAttr != null) {
				// AlgoPointOnPath
				double t = StringUtil.parseDouble(tAttr);
				((GeoPointND) v).getPathParameter().setT(t);
			}

			return true;

		} catch (RuntimeException e) {
			Log.error("problem in <curveParam>: " + e.getMessage());
			return false;
		}
	}

	private boolean handleCoords(LinkedHashMap<String, String> attrs) {
		ExpressionNode def = geo.getDefinition();
		boolean success = xmlHandler.kernel.handleCoords(geo, attrs);
		geo.setDefinition(def);
		return success;
	}

	private boolean handleDimensions(LinkedHashMap<String, String> attrs) {
		String width = attrs.get("width");
		String height = attrs.get("height");
		String angle = attrs.get("angle");
		if (width != null && height != null) {

			double widthD = -1;
			double heightD = -1;
			double angleD = 0;
			try {
				widthD = StringUtil.parseDouble(width);
				heightD = StringUtil.parseDouble(height);
				angleD = StringUtil.parseDouble(angle);
			} catch (Exception e) {
				Log.warn(e.getMessage());
			}
			if (geo.isGeoButton()) {
				GeoButton button = (GeoButton) geo;
				if (widthD > 10 && heightD > 10) {
					button.setWidth(widthD);
					button.setHeight(heightD);
				}
				button.setFixedSize(true);
				return true;
			} else if (geo instanceof RectangleTransformable) {
				if (angle == null) {
					// we have an old GeoEmbed
					((GeoEmbed) geo).setContentWidth(widthD);
					((GeoEmbed) geo).setContentHeight(heightD);
				} else {
					((RectangleTransformable) geo).setSize(widthD, heightD);
					((RectangleTransformable) geo).setAngle(angleD);
				}
			}

			return true;
		}
		return false;
	}

	private boolean handleScript(LinkedHashMap<String, String> attrs,
			ScriptType type) {
		try {
			String text = attrs.get("val");
			if (text != null && text.length() > 0) {
				Script script = app.createScript(type, text, false);
				geo.setClickScript(script);
			}
			text = attrs.get("onUpdate");
			if (text != null && text.length() > 0) {
				Script script = app.createScript(type, text, false);
				geo.setUpdateScript(script);
			}
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleCondition(LinkedHashMap<String, String> attrs) {
		try {
			// condition for visibility of object
			String strShowObjectCond = attrs.get("showObject");
			if (strShowObjectCond != null) {
				// store (geo, epxression) values
				// they will be processed in processShowObjectConditionList()
				// later
				showObjectConditionList
						.add(new GeoExpPair(geo, strShowObjectCond));
			}

			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleCheckbox(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoBoolean())) {
			Log.error("wrong element type for <checkbox>: " + geo.getClass());
			return false;
		}

		try {
			GeoBoolean bool = (GeoBoolean) geo;
			bool.setCheckboxFixed(
					MyXMLHandler.parseBoolean(attrs.get("fixed")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private void handleContentParam(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoInline)) {
			Log.error("wrong element type for <content>: " + geo.getClass());
			return;
		}

		GeoInline inlineText = (GeoInline) geo;
		inlineText.setContent(attrs.get("val"));
	}

	private boolean handleValue(LinkedHashMap<String, String> attrs,
			ArrayList<String> errors) {
		boolean isBoolean = geo.isGeoBoolean();
		boolean isNumber = geo.isGeoNumeric();
		// GGB-244 something that was formerly just a number is now a segment:
		// hide it!
		if (geo.isNumberValue() && !isNumber && !isBoolean) {
			geo.setEuclidianVisible(false);
			return true;
		}
		// set value even when definition exists; might be needed if value
		// depends on Corner
		ExpressionNode oldDef = geo.getDefinition();
		if (!(isNumber || isBoolean || geo.isGeoButton())) {
			Log.debug("wrong element type for <value>: " + geo.getClass());
			return false;
		}

		try {
			String strVal = attrs.get("val");
			if (isNumber) {
				GeoNumeric n = (GeoNumeric) geo;
				n.setValue(StringUtil.parseDouble(strVal));

				// random
				n.setRandom("true".equals(attrs.get("random")));
				n.setDefinition(oldDef);

			} else if (isBoolean) {
				GeoBoolean bool = (GeoBoolean) geo;
				/*
				 * GGB-1372: use the recently computed value instead of the
				 * saved one for the Prove command
				 */
				if (!(geo.getParentAlgorithm() instanceof AlgoProve)) {
					bool.setValue(MyXMLHandler.parseBoolean(strVal));
				}
				bool.setDefinition(oldDef);
			} else if (geo.isGeoButton()) {
				// XXX What's this javascript doing here? (Arnaud)
				GeoButton button = (GeoButton) geo;
				Script script = app.createScript(ScriptType.JAVASCRIPT, strVal,
						false);
				button.setClickScript(script);
			}
			return true;
		} catch (RuntimeException e) {
			errors.add(e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleVariables(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoSymbolic)) {
			return false;
		}
		String variableString = attrs.get("val");
		if (variableString.isEmpty()) {
			return false;
		}
		String[] variables = variableString.split(",");
		FunctionVariable[] fVars = new FunctionVariable[variables.length];
		for (int i = 0; i < variables.length; i++) {
			fVars[i] = new FunctionVariable(xmlHandler.kernel, variables[i]);
		}
		GeoSymbolic symbolic = (GeoSymbolic) geo;
		symbolic.setVariables(fVars);
		return true;
	}

	protected void init(LinkedHashMap<String, String> attrs) {
		sliderTagProcessed = false;
		fontTagProcessed = false;
		symbolicTagProcessed = false;
		lineStyleTagProcessed = false;
		geo = getGeoElement(attrs);
		if (needsConstructionDefaults) {
			geo.setConstructionDefaults();
		}
		geo.setLineOpacity(255);
		if (geo instanceof VectorNDValue) {
			((VectorNDValue) geo)
					.setMode(((VectorNDValue) geo).getDimension() == 3
							? Kernel.COORD_CARTESIAN_3D
							: Kernel.COORD_CARTESIAN);
		} else if (geo instanceof GeoPolyLine) {
			((GeoPolyLine) geo).setVisibleInView3D(false);
		} else if (geo instanceof GeoFunction) {
			geo.setFixed(false);
		} else if (geo instanceof GeoAngle) {
			((GeoAngle) geo).setEmphasizeRightAngle(true);
		} else if (geo instanceof GeoText) {
			geo.setBackgroundColor(null);
		}
	}

	// for point or vector
	private boolean handleCoordStyle(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof CoordStyle)) {
			Log.error("wrong element type for <coordStyle>: " + geo.getClass());
			return false;
		}
		CoordStyle v = (CoordStyle) geo;
		String style = attrs.get("style");
		switch (style) {
		case "cartesian":
			v.setCartesian();
			break;
		case "polar":
			v.setPolar();
			break;
		case "complex":
			v.setComplex();
			break;
		case "cartesian3d":
			v.setCartesian3D();
			break;
		case "spherical":
			v.setSpherical();
			break;
		default:
			Log.error("unknown style in <coordStyle>: " + style);
			return false;
		}
		return true;
	}

	private boolean handleListeners(LinkedHashMap<String, String> attrs) {
		try {
			if ("objectUpdate".equals(attrs.get("type"))) {
				app.getScriptManager().getUpdateListenerMap().put(geo,
						JsReference.fromName(attrs.get("val")));
			}
			if ("objectClick".equals(attrs.get("type"))) {
				app.getScriptManager().getClickListenerMap().put(geo,
						JsReference.fromName(attrs.get("val")));
			}
			return true;
		} catch (RuntimeException e) {
			Log.error(e.getMessage());
			return false;
		}
	}

	private boolean handleCaption(LinkedHashMap<String, String> attrs) {
		try {
			geo.setCaption(attrs.get("val"));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handlePointSize(LinkedHashMap<String, String> attrs) {
		if (geo.isGeoNumeric()) {
			((GeoNumeric) geo).setSliderBlobSize(
					StringUtil.parseDouble(attrs.get("val")));
			return true;
		}
		if (!(geo instanceof PointProperties)) {
			Log.debug("wrong element type for <pointSize>: " + geo.getClass());
			return false;
		}

		try {
			PointProperties p = (PointProperties) geo;
			p.setPointSize(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handlePointStyle(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof PointProperties)) {
			Log.debug("wrong element type for <pointStyle>: " + geo.getClass());
			return false;
		}

		try {
			PointProperties p = (PointProperties) geo;

			int style = Integer.parseInt(attrs.get("val"));

			if (style == -1) {
				style = docPointStyle;
			}
			p.setPointStyle(style);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleLayer(LinkedHashMap<String, String> attrs) {

		try {
			geo.setLayer(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleAnimation(LinkedHashMap<String, String> attrs) {
		try {

			String strStep = attrs.get("step");
			if (strStep != null) {
				// store speed expression to be processed later
				animationStepList.add(new GeoExpPair(geo, strStep));
			}
			String strSpeed = attrs.get("speed");
			if (strSpeed != null) {
				// store speed expression to be processed later
				animationSpeedList.add(new GeoExpPair(geo, strSpeed));
			}

			String type = attrs.get("type");
			if (type != null) {
				geo.setAnimationType(Integer.parseInt(type));
			}

			// doesn't work for hidden sliders now that intervalMin/Max are set
			// at end of XML (dynamic slider range(
			// geo.setAnimating(MyXMLHandler.parseBoolean((String)
			// attrs.get("playing")));

			// replacement
			if (MyXMLHandler.parseBoolean(attrs.get("playing"))) {
				animatingList.add(geo);
			}

			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleFixed(LinkedHashMap<String, String> attrs) {
		try {
			geo.setFixed(MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleIsShape(LinkedHashMap<String, String> attrs) {
		try {
			geo.setIsShape(MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleIsMask(LinkedHashMap<String, String> attrs) {
		try {
			geo.setIsMask(MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleBreakpoint(LinkedHashMap<String, String> attrs) {
		try {
			geo.setConsProtocolBreakpoint(
					MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleFile(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage() || geo.isGeoButton() || geo.isGeoTurtle())) {
			Log.error("wrong element type for <file>: " + geo.getClass());
			return false;
		}

		try {
			geo.setImageFileName(attrs.get("name"));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSerifContent(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoInputBox)) {
			Log.error("wrong element type for <contentSerif>: " + geo.getClass());
			return false;
		}
		String serif = attrs.get("val");

		if (serif != null) {
			((GeoInputBox) geo).setSerifContent(MyXMLHandler.parseBoolean(serif));
		}
		return true;
	}

	// <font serif="false" size="12" style="0">
	private boolean handleTextFont(LinkedHashMap<String, String> attrs) {
		this.fontTagProcessed = true;
		if (!(geo instanceof TextProperties)) {
			Log.error("wrong element type for <font>: " + geo.getClass());
			return false;
		}
		Object serif = attrs.get("serif");
		Object style = attrs.get("style");

		try {
			TextProperties text = (TextProperties) geo;

			String oldSize = attrs.get("size");
			// multiplier, new from ggb42
			String size = attrs.get("sizeM");

			if (size == null) {
				double appSize = app.getFontSize();
				double oldSizeInt = Integer.parseInt(oldSize);
				text.setFontSizeMultiplier(
						Math.max(appSize + oldSizeInt, MIN_TEXT_SIZE)
								/ appSize);
			} else {
				text.setFontSizeMultiplier(StringUtil.parseDouble(size));
			}
			if (serif != null) {
				text.setSerifFont(MyXMLHandler.parseBoolean((String) serif));
			}
			if (style != null) {
				text.setFontStyle(Integer.parseInt((String) style));
			}
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleTextDecimals(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof TextProperties)) {
			Log.error("wrong element type for <decimals>: " + geo.getClass());
			return false;
		}

		try {
			TextProperties text = (TextProperties) geo;
			text.setPrintDecimals(Integer.parseInt(attrs.get("val")), true);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleTextFigures(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof TextProperties)) {
			Log.error("wrong element type for <decimals>: " + geo.getClass());
			return false;
		}

		try {
			TextProperties text = (TextProperties) geo;
			text.setPrintFigures(Integer.parseInt(attrs.get("val")), true);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleInBackground(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage())) {
			Log.error(
					"wrong element type for <inBackground>: " + geo.getClass());
			return false;
		}

		try {
			((GeoImage) geo).setInBackground(
					MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleCentered(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage())) {
			Log.error("wrong element type for <centered>: " + geo.getClass());
			return false;
		}

		try {
			((GeoImage) geo)
					.setCentered(MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleInterpolate(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage())) {
			Log.error(
					"wrong element type for <interpolate>: " + geo.getClass());
			return false;
		}

		try {
			((GeoImage) geo).setInterpolate(
					MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleAuxiliary(LinkedHashMap<String, String> attrs) {
		try {
			geo.setAuxiliaryObject(MyXMLHandler.parseBoolean(attrs.get("val"))
							? Auxiliary.YES_SAVE
							: Auxiliary.NO_SAVE);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleAutocolor(LinkedHashMap<String, String> attrs) {
		try {
			geo.setAutoColor(MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleIsLaTeX(LinkedHashMap<String, String> attrs) {
		try {
			((GeoText) geo).setLaTeX(
					MyXMLHandler.parseBoolean(attrs.get("val")), false);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleArcSize(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof AngleProperties)) {
			Log.error("wrong element type for <arcSize>: " + geo.getClass());
			return false;
		}

		try {
			AngleProperties angle = (AngleProperties) geo;
			angle.setArcSize(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleAbsoluteScreenLocation(
			LinkedHashMap<String, String> attrs, boolean absolute) {
		if (!(geo instanceof AbsoluteScreenLocateable)) {
			Log.error("wrong element type for <absoluteScreenLocation>: "
					+ geo.getClass());
			return false;
		}

		try {
			AbsoluteScreenLocateable absLoc = (AbsoluteScreenLocateable) geo;
			double x = Double.parseDouble(attrs.get("x"));
			double y = Double.parseDouble(attrs.get("y"));
			if (absolute) {
				if (app.isWhiteboardActive() && absLoc.isGeoImage()) {
					((GeoImage) absLoc).setAbsoluteScreenLoc((int) x, (int) y,
							0);
				} else {
					absLoc.setAbsoluteScreenLoc((int) x, (int) y);
				}
				absLoc.setAbsoluteScreenLocActive(true);
			} else {
				absLoc.setRealWorldLoc(x, y);
			}

			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleAllowReflexAngle(
			LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof AngleProperties)) {
			Log.error("wrong element type for <allowReflexAngle>: "
					+ geo.getClass());
			return false;
		}

		try {
			AngleProperties angle = (AngleProperties) geo;
			angle.setAllowReflexAngle(
					MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {

			return false;
		}
	}

	private boolean handleEmphasizeRightAngle(
			LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof AngleProperties)) {
			Log.error("wrong element type for <emphasizeRightAngle>: "
					+ geo.getClass());
			return false;
		}

		try {
			AngleProperties angle = (AngleProperties) geo;
			angle.setEmphasizeRightAngle(
					MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {

			return false;
		}
	}

	private boolean handleComboBox(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoList())) {
			Log.error("wrong element type for <comboBox>: " + geo.getClass());
			return false;
		}

		try {
			GeoList list = (GeoList) geo;
			list.setDrawAsComboBox(MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {

			return false;
		}
	}

	private boolean handleCropBox(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage())) {
			Log.error("wrong element type for <cropBox>: " + geo.getClass());
			return false;
		}

		try {
			GeoImage img = (GeoImage) geo;
			double x = Double.parseDouble(attrs.get("x"));
			double y = Double.parseDouble(attrs.get("y"));
			double w = Double.parseDouble(attrs.get("width"));
			double h = Double.parseDouble(attrs.get("height"));
			boolean cropped = MyXMLHandler.parseBoolean(attrs.get("cropped"));
			GRectangle2D rect = AwtFactory.getPrototype().newRectangle2D();
			rect.setRect(x, y, w, h);
			img.setCropBoxRelative(rect);
			img.setCropped(cropped);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleAngleStyle(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof AngleProperties)) {
			Log.error("wrong element type for <angleStyle>: " + geo.getClass());
			return false;
		}

		try {
			AngleProperties angle = (AngleProperties) geo;
			angle.setAngleStyle(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {

			return false;
		}
	}

	private boolean handleAudio(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoAudio)) {
			Log.error("wrong element type for <audio>: " + geo.getClass());
			return false;
		}
		try {
			GeoAudio audio = (GeoAudio) geo;
			audio.setSrc(attrs.get("src"));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleAlgebra(LinkedHashMap<String, String> attrs) {
		try {
			geo.setAlgebraLabelVisible(MyXMLHandler
					.parseBooleanRev(attrs.get("labelVisible")));
			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleTableView(LinkedHashMap<String, String> attrs) {
		try {
			((GeoEvaluatable) geo).setTableColumn(
					(int) MyXMLHandler.parseDoubleNaN(attrs.get("column")));
			((GeoEvaluatable) geo)
					.setPointsVisible(
							MyXMLHandler.parseBoolean(attrs.get("points")));
			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleVideo(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoVideo)) {
			Log.error("wrong element type for <video>: " + geo.getClass());
			return false;
		}
		try {
			GeoVideo video = (GeoVideo) geo;
			video.setSrc(attrs.get("src"), attrs.get("type"));
			video.setSize(Integer.parseInt(attrs.get("width")),
					Integer.parseInt(attrs.get("height")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	/*
	 * needed for old files (4.2 and earlier)
	 */
	private boolean handleForceReflexAngle(
			LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof AngleProperties)) {
			Log.error("wrong element type for <forceReflexAngle>: "
					+ geo.getClass());
			return false;
		}

		try {
			AngleProperties angle = (AngleProperties) geo;
			angle.setForceReflexAngle(
					MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {

			return false;
		}
	}

	private boolean handleOutlyingIntersections(
			LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof LimitedPath)) {
			Log.debug("wrong element type for <outlyingIntersections>: "
					+ geo.getClass());
			return false;
		}

		try {
			LimitedPath lpath = (LimitedPath) geo;
			lpath.setAllowOutlyingIntersections(
					MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleKeepTypeOnTransform(
			LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof LimitedPath)) {
			Log.debug("wrong element type for <outlyingIntersections>: "
					+ geo.getGeoClassType());
			return false;
		}

		try {
			LimitedPath lpath = (LimitedPath) geo;
			lpath.setKeepTypeOnGeometricTransform(
					MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSymbolic(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof HasSymbolicMode)) {
			Log.error("wrong element type for <symbolic>: " + geo.getClass());
			return false;
		}
		symbolicTagProcessed = true;
		try {
			HasSymbolicMode num = (HasSymbolicMode) geo;
			num.setSymbolicMode(MyXMLHandler.parseBoolean(attrs.get("val")),
					false);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSlopeTriangleSize(
			LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoNumeric())) {
			Log.error("wrong element type for <slopeTriangleSize>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoNumeric num = (GeoNumeric) geo;
			num.setSlopeTriangleSize(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleExtraTag(LinkedHashMap<String, String> attrs) {
		ChartStyle algo = ((ChartStyleAlgo) geo.getParentAlgorithm()).getStyle();
		if (!"".equals(attrs.get("key")) && !"".equals(attrs.get("value"))
				&& !"".equals(attrs.get("barNumber"))) {
			switch (attrs.get("key")) {
			case "barAlpha":
				algo.setBarAlpha(Float.parseFloat(attrs.get("value")),
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			case "barHatchDistance":
				algo.setBarHatchDistance(Integer.parseInt(attrs.get("value")),
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			case "barFillType":
				algo.setBarFillType(
						FillType.values()[Integer.parseInt(attrs.get("value"))],
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			case "barHatchAngle":
				algo.setBarHatchAngle(Integer.parseInt(attrs.get("value")),
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			case "barImage":
				algo.setBarImage(attrs.get("value"),
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			case "barSymbol":
				algo.setBarSymbol(attrs.get("value"),
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			case "barColor":
				String[] c = attrs.get("value").split(",");
				algo.setBarColor(
						GColor.newColor(Integer.parseInt(c[0].substring(5)),
								Integer.parseInt(c[1]), Integer.parseInt(c[2])),
						Integer.parseInt(attrs.get("barNumber")));
				return true;
			}
		}
		return false;
	}

	/**
	 * Start Points have to be handled at the end of the construction, because
	 * they could depend on objects that are defined after this GeoElement.
	 * 
	 * So we store all (geo, startpoint expression) pairs and process them at
	 * the end of the construction.
	 * 
	 * @see #processStartPointList()
	 */
	private void handleStartPoint(LinkedHashMap<String, String> attrs) {
		if (geo instanceof RectangleTransformable && !geo.isGeoImage()) {
			double x = 0;
			double y = 0;

			try {
				x = Double.parseDouble(attrs.get("x"));
				y = Double.parseDouble(attrs.get("y"));
			} catch (NumberFormatException e) {
				Log.error("Incorrect start point for RectangleTransformable");
			}

			// old GeoEmbeds are represented by three rw points
			String number = attrs.get("number");
			if (geo instanceof GeoEmbed && number != null) {
				GeoEmbed embed = (GeoEmbed) geo;

				if ("0".equals(number)) {
					embedY = y;
					return;
				} else if ("1".equals(number)) {
					embedX = x;
					return;
				} else if ("2".equals(number)) {
					embed.setRealWidth(embedX - x);
					embed.setRealHeight(y - embedY);
				}
			}

			GPoint2D startPoint = new GPoint2D(x, y);
			((RectangleTransformable) geo).setLocation(startPoint);
			return;
		}

		if (!(geo instanceof Locateable)) {
			Log.error("wrong element type for <startPoint>: " + geo.getClass());
			return;
		}

		Locateable locGeo = (Locateable) geo;

		// relative start point (expression or label expected)
		String exp = attrs.get("exp");
		if (exp == null) {
			exp = attrs.get("label");
		}

		// for corners a number of the startPoint is given
		int number = 0;
		try {
			number = Integer.parseInt(attrs.get("number"));
		} catch (RuntimeException e) {
			// do nothing
		}

		if (exp != null) {
			// store (geo, epxression, number) values
			// they will be processed in processStartPoints() later
			startPointList.add(new LocateableExpPair(locGeo, exp, number));
			locGeo.setWaitForStartPoint();
		} else {
			// absolute start point (coords expected)
			try {
				GeoPointND p = xmlHandler.handleAbsoluteStartPoint(attrs);

				if (number == 0) {
					// set first start point right away
					locGeo.setStartPoint(p);
				} else {
					// set other start points later
					// store (geo, point, number) values
					// they will be processed in processStartPoints() later
					startPointList
							.add(new LocateableExpPair(locGeo, p, number));
					locGeo.setWaitForStartPoint();
				}
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	private boolean handleLength(LinkedHashMap<String, String> attrs) {

		// name of linked geo
		String val = attrs.get("val");

		if (geo instanceof GeoInputBox) {
			((GeoInputBox) geo).setLength(Integer.parseInt(val));
		} else {
			Log.error("Length not supported for " + geo.getGeoClassType());
		}

		return true;
	}

	private boolean handleTempUserInput(LinkedHashMap<String, String> attrs) {

		// name of linked geo
		String eval = attrs.get("eval");
		String display = attrs.get("display");

		if (geo instanceof GeoInputBox) {
			GeoInputBox inputBox = (GeoInputBox) geo;
			inputBox.setTempUserDisplayInput(display);
			inputBox.setTempUserEvalInput(eval);
		} else {
			Log.error("temp user input not supported for " + geo.getGeoClassType());
		}

		return true;
	}

	private boolean handleTextAlign(LinkedHashMap<String, String> attrs) {
		HorizontalAlignment align = HorizontalAlignment.fromString(attrs.get("val"));

		if (align != null && geo instanceof HasAlignment) {
			((HasAlignment) geo).setAlignment(align);
		} else {
			Log.error("Text alignment not supported for " + geo.getGeoClassType());
		}

		return true;
	}

	private boolean handleListType(LinkedHashMap<String, String> attrs) {

		// name of geo type, eg "point"
		String val = attrs.get("val");

		if (geo instanceof GeoList) {
			((GeoList) geo).setTypeStringForXML(val);
		} else {
			Log.error("handleListType: expected LIST, got "
					+ geo.getGeoClassType());
		}

		return true;
	}

	/**
	 * Linked Geos have to be handled at the end of the construction, because
	 * they could depend on objects that are defined after this GeoElement.
	 * 
	 * So we store all (geo, expression) pairs and process them at the end of
	 * the construction.
	 * 
	 * @see #processLinkedGeoList()
	 */
	private boolean handleLinkedGeo(LinkedHashMap<String, String> attrs) {

		// name of linked geo
		String exp = attrs.get("exp");

		if (exp != null) {
			// store (geo, expression, number) values
			// they will be processed in processLinkedGeos() later
			linkedGeoList.add(new GeoExpPair(geo, exp));
		} else {
			return false;
		}

		return true;
	}

	private boolean handleLineStyle(LinkedHashMap<String, String> attrs) {
		try {
			lineStyleTagProcessed = true;
			geo.setLineType(Integer.parseInt(attrs.get("type")));
			geo.setLineThickness(Integer.parseInt(attrs.get("thickness")));

			// for 3D
			String typeHidden = attrs.get("typeHidden");
			if (typeHidden != null) {
				geo.setLineTypeHidden(Integer.parseInt(typeHidden));
			}
			String opacity = attrs.get("opacity");
			if (opacity != null) {
				geo.setLineOpacity(Integer.parseInt(opacity));
			}
			String drawArrows = attrs.get("drawArrow");
			if (drawArrows != null && geo instanceof GeoLocus) {
				((GeoLocus) geo).drawAsArrows(MyXMLHandler.parseBoolean(drawArrows));
			}

			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleDecoration(LinkedHashMap<String, String> attrs) {
		try {
			geo.setDecorationType(Integer.parseInt(attrs.get("type")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleEqnStyle(LinkedHashMap<String, String> attrs) {
		String style = attrs.get("style");
		String parameter = attrs.get("parameter");
		if (geo instanceof EquationValue) {
			// GeoConic handled here
			if (!((EquationValue) geo).setTypeFromXML(style, parameter, true)) {
				Log.error("unknown style for conic in <eqnStyle>: " + style);
			}
		} else if (geo instanceof GeoLineND && "parametric".equals(style)) {
			((GeoLineND) geo).setToParametric(parameter);
		} else if (geo instanceof GeoConicND) {
			// GeoConic3D handled here
			if ("parametric".equals(style)) {
				((GeoConicND) geo).setToParametric(parameter);
			}
		} else {
			Log.error("wrong element type for <eqnStyle>: " + geo.getClass());
			return false;
		}
		return true;
	}

	private void handleEmbed(LinkedHashMap<String, String> attrs) {
		if (geo instanceof GeoEmbed) {
			try {
				((GeoEmbed) geo).setEmbedId(Integer.parseInt(attrs.get("id")));
				((GeoEmbed) geo).setAppName(attrs.get("app"));
				((GeoEmbed) geo).setUrl(attrs.get("url"));
			} catch (RuntimeException e) {
				Log.error("Problem parsing embed " + e.getMessage());
			}
		} else {
			Log.error("wrong element type for <embed>: " + geo.getClass());
		}
	}

	private void handleEmbedSettings(LinkedHashMap<String, String> attrs) {
		if (geo instanceof GeoEmbed) {
			for (Map.Entry<String, String> entry: attrs.entrySet()) {
				((GeoEmbed) geo).attr(entry.getKey(), entry.getValue());
			}
		}
	}

	private boolean handleSlider(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoNumeric())) {
			Log.error("wrong element type for <slider>: " + geo.getClass());
			return false;
		}

		try {
			sliderTagProcessed = true;
			// don't create sliders in macro construction
			if (geo.getKernel().isMacroKernel()) {
				return true;
			}

			GeoNumeric num = (GeoNumeric) geo;

			// make sure
			String strMin = attrs.get("min");
			String strMax = attrs.get("max");
			if (strMin != null || strMax != null) {
				minMaxList.add(new GeoNumericMinMax(geo, strMin, strMax));
			}

			String str = attrs.get("absoluteScreenLocation");
			if (str != null) {
				num.setAbsoluteScreenLocActive(MyXMLHandler.parseBoolean(str));
			} else {
				num.setAbsoluteScreenLocActive(false);
			}

			// null in preferences
			if (attrs.get("x") != null) {
				double x = StringUtil.parseDouble(attrs.get("x"));
				double y = StringUtil.parseDouble(attrs.get("y"));
				num.setSliderLocation(x, y, true);
			}

			num.setSliderWidth(StringUtil.parseDouble(attrs.get("width")),
					true);
			num.setSliderFixed(MyXMLHandler.parseBoolean(attrs.get("fixed")));
			num.setShowExtendedAV(
					MyXMLHandler.parseBoolean(attrs.get("showAlgebra")));

			num.setSliderHorizontal(
					MyXMLHandler.parseBoolean(attrs.get("horizontal")));

			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleTrace(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof Traceable)) {
			Log.error("wrong element type for <trace>: " + geo.getClass());
			return false;
		}

		try {
			Traceable t = (Traceable) geo;
			t.setTrace(MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSpreadsheetTrace(
			LinkedHashMap<String, String> attrs) {

		// G.Sturr 2010-5-30
		// XML handling for new tracing code
		if (!geo.isSpreadsheetTraceable()) {
			Log.error("wrong element type for <trace>: " + geo.getClass());
			return false;
		}

		try {

			// set geo for tracing
			geo.setSpreadsheetTrace(
					MyXMLHandler.parseBoolean(attrs.get("val")));

			SpreadsheetTraceSettings t = geo.getTraceSettings();
			t.traceColumn1 = Integer.parseInt(attrs.get("traceColumn1"));
			t.traceColumn2 = Integer.parseInt(attrs.get("traceColumn2"));
			t.traceRow1 = Integer.parseInt(attrs.get("traceRow1"));
			t.traceRow2 = Integer.parseInt(attrs.get("traceRow2"));
			t.tracingRow = Integer.parseInt(attrs.get("tracingRow"));
			t.numRows = Integer.parseInt(attrs.get("numRows"));
			t.headerOffset = Integer.parseInt(attrs.get("headerOffset"));

			t.doColumnReset = (MyXMLHandler
					.parseBoolean(attrs.get("doColumnReset")));
			t.doRowLimit = (MyXMLHandler.parseBoolean(attrs.get("doRowLimit")));
			t.showLabel = (MyXMLHandler.parseBoolean(attrs.get("showLabel")));
			t.showTraceList = (MyXMLHandler
					.parseBoolean(attrs.get("showTraceList")));
			t.doTraceGeoCopy = (MyXMLHandler
					.parseBoolean(attrs.get("doTraceGeoCopy")));

			String stringPause = attrs.get("pause");
			if (stringPause == null) {
				t.pause = false;
			} else {
				t.pause = MyXMLHandler.parseBoolean(stringPause);
			}

			app.setNeedsSpreadsheetTableModel();

			// app.getTraceManager().loadTraceGeoCollection(); is called when
			// construction loaded to add geo to trace list

			return true;

		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleShowTrimmed(LinkedHashMap<String, String> attrs) {
		try {
			geo.setShowTrimmedIntersectionLines(
					MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSelectionAllowed(
			LinkedHashMap<String, String> attrs) {
		try {
			geo.setSelectionAllowed(
					MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleSelectedIndex(LinkedHashMap<String, String> attrs) {
		try {
			if (geo.isGeoList()) {
				((GeoList) geo).setSelectedIndex(
						Integer.parseInt(attrs.get("val")), false);
			}
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleFading(LinkedHashMap<String, String> attrs) {
		try {
			float fading = Float.parseFloat(attrs.get("val"));
			((GeoPlaneND) geo).setFading(fading);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleLevelOfDetailQuality(
			LinkedHashMap<String, String> attrs) {
		try {
			boolean lod = MyXMLHandler.parseBoolean(attrs.get("val"));
			if (lod) {
				((SurfaceEvaluable) geo)
						.setLevelOfDetail(LevelOfDetail.QUALITY);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleBgColor(LinkedHashMap<String, String> attrs) {
		GColor col = handleColorAlphaAttrs(attrs);
		if (col == null) {
			return false;
		}
		geo.setBackgroundColor(col);

		return true;
	}

	private void handleBorderColor(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoInlineText)) {
			return;
		}
		int red = Integer.parseInt(attrs.get("r"));
		int green = Integer.parseInt(attrs.get("g"));
		int blue = Integer.parseInt(attrs.get("b"));
		GColor col = GColor.newColor(red, green, blue);
		((GeoInlineText) geo).setBorderColor(col);
	}

	private void handleBoundingBox(LinkedHashMap<String, String> attrs) {
		if (geo instanceof GeoText && geo.isIndependent()) {
			try {
				GeoInlineText ret = new GeoInlineText((GeoText) geo);
				geo.getConstruction().replace(geo, ret);
				geo = ret;
				ret.setSize(Integer.parseInt(attrs.get("width")),
						Integer.parseInt(attrs.get("height")));
			} catch (Exception e) {
				Log.debug(e);
			}
		} else {
			Log.error("Unexpected type for <boundingBox>: " + geo.getClass());
		}
	}

	private boolean handleMatrix(LinkedHashMap<String, String> attrs) {
		if (!geo.isGeoConic() && !geo.isGeoQuadric()) {
			Log.error("wrong element type for <matrix>: " + geo.getClass());
			return false;
		}
		try {
			handleMatrixConicOrQuadric(attrs);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * handler matrix for a conic or a quadric
	 * 
	 * @param attrs
	 *            attributes
	 * @throws Exception
	 *             exception
	 */
	private void handleMatrixConicOrQuadric(LinkedHashMap<String, String> attrs)
			throws Exception {
		if (geo.isGeoQuadric()) {
			if (geo.isDefaultGeo()) { // avoid setting for default geo
				return;
			}
			GeoQuadric3DInterface quadric = (GeoQuadric3DInterface) geo;
			// set matrix and classify conic now
			// <eigenvectors> should have been set earlier

			if (geo.isIndependent() && geo.getDefinition() == null) {
				double[] matrix = { StringUtil.parseDouble(attrs.get("A0")),
						StringUtil.parseDouble(attrs.get("A1")),
						StringUtil.parseDouble(attrs.get("A2")),
						StringUtil.parseDouble(attrs.get("A3")),
						StringUtil.parseDouble(attrs.get("A4")),
						StringUtil.parseDouble(attrs.get("A5")),
						StringUtil.parseDouble(attrs.get("A6")),
						StringUtil.parseDouble(attrs.get("A7")),
						StringUtil.parseDouble(attrs.get("A8")),
						StringUtil.parseDouble(attrs.get("A9")) };
				quadric.setMatrixFromXML(matrix);
			} else {
				quadric.ensureClassified();
			}
			if (!setEigenvectorsCalled) {
				quadric.hideIfNotSphere();
			}
		} else if (geo.isGeoConic() && geo.getDefinition() == null) {
			GeoConicND conic = (GeoConicND) geo;
			// set matrix and classify conic now
			// <eigenvectors> should have been set earlier
			double[] matrix = { StringUtil.parseDouble(attrs.get("A0")),
					StringUtil.parseDouble(attrs.get("A1")),
					StringUtil.parseDouble(attrs.get("A2")),
					StringUtil.parseDouble(attrs.get("A3")),
					StringUtil.parseDouble(attrs.get("A4")),
					StringUtil.parseDouble(attrs.get("A5")) };
			conic.setMatrix(matrix);
		}
	}

	private boolean handleLabelOffset(LinkedHashMap<String, String> attrs) {
		try {
			geo.labelOffsetX = Integer.parseInt(attrs.get("x"));
			geo.labelOffsetY = Integer.parseInt(attrs.get("y"));

			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleLabelMode(LinkedHashMap<String, String> attrs) {
		try {
			geo.setLabelMode(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleTooltipMode(LinkedHashMap<String, String> attrs) {
		try {
			geo.setTooltipMode(Integer.parseInt(attrs.get("val")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean handleCoefficients(LinkedHashMap<String, String> attrs) {
		// Application.debug(attrs.toString());
		if (!(geo.isGeoImplicitCurve())) {
			Log.warn(
					"wrong element type for <coefficients>: " + geo.getClass());
			return false;
		}
		try {
			String rep = attrs.get("rep");
			if (rep == null) {
				return false;
			}
			if (attrs.get("rep").equals("array")) {
				String data = attrs.get("data");
				if (data == null) {
					return false;
				}
				ArrayList<ArrayList<Double>> collect = new ArrayList<>();
				ArrayList<Double> newRow = new ArrayList<>();
				int start = 0;
				for (int c = 1; c < data.length(); c++) {
					switch (data.charAt(c)) {
					default:
						// do nothing
						break;
					case '[':
						if (newRow.size() > 0) {
							return false;
						}
						start = c + 1;
						break;
					case ']':
						newRow.add(StringUtil
								.parseDouble(data.substring(start, c)));
						start = c + 1;
						collect.add(newRow);
						newRow = new ArrayList<>();
						c++; // jump over ','
						break;
					case ',':
						newRow.add(StringUtil
								.parseDouble(data.substring(start, c)));
						start = c + 1;
					}
				}
				double[][] coeff = new double[collect.size()][];
				for (int i = 0; i < collect.size(); i++) {
					ArrayList<Double> row = collect.get(i);
					coeff[i] = new double[row.size()];
					for (int j = 0; j < row.size(); j++) {
						coeff[i][j] = row.get(j);
					}
				}
				ExpressionNode def = geo.getDefinition();
				/*
				 * Only overwrite coeff from XML when we don't have definition
				 * (setting coeffs explicitly kills factorization)
				 */
				if (def == null) {
					((GeoImplicit) geo).setCoeff(coeff);
				}
				// geo.setDefinition(def);
				return true;
			}
		} catch (RuntimeException e) {
			return false;
		}
		return false;
	}

	private boolean handleUserInput(LinkedHashMap<String, String> attrs) {
		// Application.debug(attrs.toString());
		if (!(geo instanceof GeoImplicit)) {
			Log.warn("wrong element type for <userinput>: " + geo.getClass());
			return false;
		}
		try {
			boolean valid = !"false".equals(attrs.get("valid"));
			if (geo.isIndependent() && valid) {
				String value = attrs.get("value");
				if (value != null) {
					ValidExpression ve = xmlHandler.parser
							.parseGeoGebraExpression(value);
					geo.setDefinition(ve.wrap());
					if (ve.unwrap() instanceof Equation) {
						((GeoImplicit) geo).fromEquation((Equation) ve.unwrap(),
								null);
					}
				}
			}
			if (attrs.get("show") != null && attrs.get("show").equals("true")
					&& valid) {
				((GeoImplicit) geo).setToUser();
			} else {
				((GeoImplicit) geo).setToImplicit();
			}

			return true;
		} catch (Exception e) {
			Log.debug(e.getMessage());
			return false;
		}
	}

	private void handleOrdering(LinkedHashMap<String, String> attrs) {
		try {
			geo.setOrdering(Integer.parseInt(attrs.get("val")));
		} catch (RuntimeException e) {
			// no or incorrect ordering
		}
	}

	private boolean handleObjColor(LinkedHashMap<String, String> attrs) {
		GColor col = MyXMLHandler.handleColorAttrs(attrs);
		if (col == null) {
			return false;
		}
		geo.setObjColor(col);

		// Dynamic colors
		// Michael Borcherds 2008-04-02
		String red = attrs.get("dynamicr");
		String green = attrs.get("dynamicg");
		String blue = attrs.get("dynamicb");
		String alpha = attrs.get("dynamica");
		String colorSpace = attrs.get("colorSpace");

		if (red != null && green != null && blue != null) {
			try {
				if (!"".equals(red) || !"".equals(green) || !"".equals(blue)) {
					if ("".equals(red)) {
						red = "0";
					}
					if ("".equals(green)) {
						green = "0";
					}
					if ("".equals(blue)) {
						blue = "0";
					}

					StringBuilder sb = new StringBuilder();
					sb.append('{');
					sb.append(red);
					sb.append(',');
					sb.append(green);
					sb.append(',');
					sb.append(blue);
					if (alpha != null && !"".equals(alpha)) {
						sb.append(',');
						sb.append(alpha);
					}
					sb.append('}');

					// need to to this at end of construction (dependencies!)
					dynamicColorList.add(new GeoExpPair(geo, sb.toString()));
					geo.setColorSpace(
							colorSpace == null ? GeoElement.COLORSPACE_RGB
									: Integer.parseInt(colorSpace));
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
				Log.error("Error loading Dynamic Colors");
			}
		}

		String angle = attrs.get("hatchAngle");
		if (angle != null) {
			geo.setHatchingAngle(Integer.parseInt(angle));
		}

		String inverse = attrs.get("inverseFill");
		if (inverse != null) {
			geo.setInverseFill(MyXMLHandler.parseBoolean(inverse));
		}

		String distance = attrs.get("hatchDistance");
		if (angle != null) {
			geo.setHatchingDistance(Integer.parseInt(distance));
			// Old files don't store fillType, just fillDistance. New files
			// override this below.
			geo.setFillType(FillType.HATCH);
		}

		String fillType = attrs.get("fillType");
		if (fillType != null) {
			geo.setFillType(
					FillType.values()[Integer.parseInt(fillType)]);
		}
		String fillSymbol = attrs.get("fillSymbol");
		if (fillSymbol != null) {
			geo.setFillSymbol(fillSymbol);
		}
		String filename = attrs.get("image");
		if (filename != null) {
			geo.setFillImage(filename);
			geo.setFillType(FillType.IMAGE);
		}

		alpha = attrs.get("alpha");
		// ignore alpha value for lists prior to GeoGebra 3.2
		if (alpha != null
				&& (!geo.isGeoList() || xmlHandler.ggbFileFormat > 3.19)) {
			geo.setAlphaValue(Float.parseFloat(alpha));
		}
		return true;
	}

	/**
	 * @param attrs
	 *            attributes
	 * @return success
	 */
	private boolean handleEigenvectorsConic(
			LinkedHashMap<String, String> attrs) {
		if (!geo.isGeoConic()) {
			Log.error(
					"wrong element type for <eigenvectors>: " + geo.getClass());
			return false;
		}
		try {
			GeoConicND conic = (GeoConicND) geo;
			// set eigenvectors, but don't classify conic now
			// classifyConic() will be called in handleMatrix() by
			// conic.setMatrix()
			conic.setEigenvectors(StringUtil.parseDouble(attrs.get("x0")),
					StringUtil.parseDouble(attrs.get("y0")),
					StringUtil.parseDouble(attrs.get("z0")),
					StringUtil.parseDouble(attrs.get("x1")),
					StringUtil.parseDouble(attrs.get("y1")),
					StringUtil.parseDouble(attrs.get("z1")));
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private void handleEigenvectors(LinkedHashMap<String, String> attrs) {
		if (!geo.isGeoQuadric()) {
			handleEigenvectorsConic(attrs);
			return;
		}
		try {
			GeoQuadric3DInterface quadric = (GeoQuadric3DInterface) geo;
			// set eigenvectors, but don't classify conic now
			// classifyConic() will be called in handleMatrix() by
			// conic.setMatrix()
			setEigenvectorsCalled = true;
			if (geo.isIndependent() && geo.getDefinition() == null) {
				quadric.setEigenvectors(StringUtil.parseDouble(attrs.get("x0")),
						StringUtil.parseDouble(attrs.get("y0")),
						StringUtil.parseDouble(attrs.get("z0")),
						StringUtil.parseDouble(attrs.get("x1")),
						StringUtil.parseDouble(attrs.get("y1")),
						StringUtil.parseDouble(attrs.get("z1")),
						StringUtil.parseDouble(attrs.get("x2")),
						StringUtil.parseDouble(attrs.get("y2")),
						StringUtil.parseDouble(attrs.get("z2")));
			}
		} catch (Exception e) {
			Log.error("Problem parsing eigenvectors: " + e);
		}
	}

	protected void finish() {
		if (!sliderTagProcessed && geo.isGeoNumeric()) {
			((GeoNumeric) geo).setShowExtendedAV(false);
		} else if (!fontTagProcessed && geo.isGeoText()) {
			((TextProperties) geo).setFontSizeMultiplier(1);
			((TextProperties) geo).setSerifFont(false);
			((TextProperties) geo).setFontStyle(GFont.PLAIN);
		} else if (!lineStyleTagProcessed && ((geo.isGeoFunctionNVar()
				&& ((GeoFunctionNVar) geo).isFun2Var())
				|| geo.isGeoSurfaceCartesian())) {
			geo.setLineThickness(0);
		}

		if (!symbolicTagProcessed && geo.isGeoText()) {
			((GeoText) geo).setSymbolicMode(false, false);
		}
		if (xmlHandler.casMap != null && geo instanceof CasEvaluableFunction) {
			((CasEvaluableFunction) geo).updateCASEvalMap(xmlHandler.casMap);
		}

		if (geo.isGeoImage() && ((GeoImage) geo).isCentered()) {
			((GeoImage) geo).setCentered(true);
		}
	}

	private boolean handleShow(LinkedHashMap<String, String> attrs) {
		try {
			geo.setEuclidianVisible(
					MyXMLHandler.parseBoolean(attrs.get("object")));
			geo.setLabelVisible(MyXMLHandler.parseBoolean(attrs.get("label")));

			// bit 0 -> display object in EV1, 0 = true (default)
			// bit 1 -> display object in EV2, 0 = false (default)
			int EVs = 0; // default, display in just EV1
			String str = attrs.get("ev");
			if (str != null) {
				EVs = Integer.parseInt(str);
			}

			if ((EVs & 1) == 0) {
				geo.addView(App.VIEW_EUCLIDIAN);
			} else {
				geo.removeView(App.VIEW_EUCLIDIAN);
			}

			if ((EVs & 2) == 2) { // bit 1
				geo.addView(App.VIEW_EUCLIDIAN2);
			} else {
				geo.removeView(App.VIEW_EUCLIDIAN2);
			}

			if ((EVs & 4) == 4) { // bit 2
				geo.addViews3D();
			}

			if ((EVs & 8) == 8) { // bit 3
				geo.removeViews3D();
			}

			if ((EVs & 16) == 16) { // bit 4
				geo.setVisibleInViewForPlane(true);
				if (!(xmlHandler.cons instanceof MacroConstruction)) {
					app.addToViewsForPlane(geo);
				}
			}

			if ((EVs & 32) == 32) { // bit 5
				geo.setVisibleInViewForPlane(false);
				app.removeFromViewsForPlane(geo);
			}

			return true;

		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleShowOnAxis(LinkedHashMap<String, String> attrs) {
		try {
			if (!(geo instanceof GeoFunction)) {
				return false;
			}
			((GeoFunction) geo)
					.setShowOnAxis(MyXMLHandler.parseBoolean(attrs.get("val")));
			return true;

		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Handle start tag inside &lt;element>
	 * 
	 * @param eName
	 *            element name
	 * @param attrs
	 *            attributes
	 */
	protected void startGeoElement(String eName,
			LinkedHashMap<String, String> attrs, ArrayList<String> errors) {
		if (geo == null) {
			Log.error("no element set for <" + eName + ">");
			return;
		}

		ScriptType scriptType = ScriptType.getTypeWithXMLName(eName);
		if (scriptType != null) {
			handleScript(attrs, scriptType);
		} else {
			switch (eName) {
			case "auxiliary":
				handleAuxiliary(attrs);
				break;
			case "autocolor":
				handleAutocolor(attrs);
				break;
			case "animation":
				handleAnimation(attrs);
				break;
			case "arcSize":
				handleArcSize(attrs);
				break;
			case "allowReflexAngle":
				handleAllowReflexAngle(attrs);
				break;
			case "absoluteScreenLocation":
				handleAbsoluteScreenLocation(attrs, true);
				break;
			case "angleStyle":
				handleAngleStyle(attrs);
				break;
			case "audio":
				handleAudio(attrs);
				break;
			case "algebra":
				handleAlgebra(attrs);
				break;
			case "breakpoint":
				handleBreakpoint(attrs);
				break;
			case "bgColor":
				handleBgColor(attrs);
				break;
			case "borderColor":
				handleBorderColor(attrs);
				break;
			case "boundingBox":
				handleBoundingBox(attrs);
				break;
			case "coords":
				handleCoords(attrs);
				break;
			case "coordStyle":
				handleCoordStyle(attrs);
				break;
			case "caption":
				handleCaption(attrs);
				break;
			case "condition":
				handleCondition(attrs);
				break;
			case "contentSize":
				handleContentSize(attrs);
				break;
			case "checkbox":
				handleCheckbox(attrs);
				break;
			case "coefficients":
				handleCoefficients(attrs);
				break;
			case "comboBox":
				handleComboBox(attrs);
				break;
			case "contentSerif":
				handleSerifContent(attrs);
				break;
			case "cropBox":
				handleCropBox(attrs);
				break;
			case "curveParam":
				handleCurveParam(attrs);
				break;
			case "casMap":
				xmlHandler.casMapForElement();
				break;
			case "content":
				handleContentParam(attrs);
				break;
			case "decoration":
				handleDecoration(attrs);
				break;
			case "decimals":
				handleTextDecimals(attrs);
				break;
			case "dimensions":
				handleDimensions(attrs);
				break;
			case "eqnStyle":
				handleEqnStyle(attrs);
				break;
			case "eigenvectors":
				handleEigenvectors(attrs);
				break;
			case "emphasizeRightAngle":
				handleEmphasizeRightAngle(attrs);
				break;
			case "embed":
				handleEmbed(attrs);
				break;
			case "embedSettings":
				handleEmbedSettings(attrs);
				break;
			case "fixed":
				handleFixed(attrs);
				break;
			case "file":
				handleFile(attrs);
				break;
			case "font":
				handleTextFont(attrs);
				break;
			case "forceReflexAngle":
				handleForceReflexAngle(attrs);
				break;
			case "dynamicCaption":
				handleDynamicCaption(attrs);
				break;
			case "fading":
				handleFading(attrs);
				break;
			case "isLaTeX":
				handleIsLaTeX(attrs);
				break;
			case "inBackground":
				handleInBackground(attrs);
				break;
			case "interpolate":
				handleInterpolate(attrs);
				break;
			case "isMask":
				handleIsMask(attrs);
				break;
			case "isShape":
				handleIsShape(attrs);
				break;
			case "centered":
				handleCentered(attrs);
				break;
			case "keepTypeOnTransform":
				handleKeepTypeOnTransform(attrs);
				break;
			case "lineStyle":
				handleLineStyle(attrs);
				break;
			case "labelOffset":
				handleLabelOffset(attrs);
				break;
			case "labelMode":
				handleLabelMode(attrs);
				break;
			case "layer":
				handleLayer(attrs);
				break;
			case "linkedGeo":
				handleLinkedGeo(attrs);
				break;
			case "length":
				handleLength(attrs);
				break;
			case "tempUserInput":
				handleTempUserInput(attrs);
				break;
			case "listType":
				handleListType(attrs);
				break;
			case "listener":
				handleListeners(attrs);
				break;
			case "levelOfDetailQuality":
				handleLevelOfDetailQuality(attrs);
				break;
			case "matrix":
				handleMatrix(attrs);
				break;
			case "objColor":
				handleObjColor(attrs);
				break;
			case "ordering":
				handleOrdering(attrs);
				break;
			case "outlyingIntersections":
				handleOutlyingIntersections(attrs);
				break;
			case "parentLabel":
				handleParentLabel(attrs);
				break;
			case "pointSize":
				handlePointSize(attrs);
				break;
			case "pointStyle":
				handlePointStyle(attrs);
				break;
			case "show":
				handleShow(attrs);
				break;
			case "showOnAxis":
				handleShowOnAxis(attrs);
				break;
			case "startPoint":
				handleStartPoint(attrs);
				break;
			case "slider":
				handleSlider(attrs);
				break;
			case "symbolic":
				handleSymbolic(attrs);
				break;
			case "slopeTriangleSize":
				handleSlopeTriangleSize(attrs);
				break;
			case "significantfigures":
				handleTextFigures(attrs);
				break;
			case "spreadsheetTrace":
				handleSpreadsheetTrace(attrs);
				break;
			case "showTrimmed":
				handleShowTrimmed(attrs);
				break;
			case "selectionAllowed":
				handleSelectionAllowed(attrs);
				break;
			case "selectedIndex":
				handleSelectedIndex(attrs);
				break;
			case "tableview":
				handleTableView(attrs);
				break;
			case "trace":
				handleTrace(attrs);
				break;
			case "tooltipMode":
				handleTooltipMode(attrs);
				break;
			case "tag":
				handleExtraTag(attrs);
				break;
			case "tags":
				// ignore
				break;
			case "userinput":
				handleUserInput(attrs);
				break;
			case "value":
				handleValue(attrs, errors);
				break;
			case "variables":
				handleVariables(attrs);
				break;
			case "video":
				handleVideo(attrs);
				break;
			case "textAlign":
				handleTextAlign(attrs);
				break;
			default:
				Log.error("unknown tag in <element>: " + eName);
			}
		}

	}

	private void handleDynamicCaption(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoInputBox)) {
			Log.error("wrong element type for <dynamicCaption>: " + geo.getClass());
			return;
		}
		try {
			String dynamicCaption = attrs.get("val");
			if (dynamicCaption != null) {
				dynamicCaptionList
						.add(new GeoExpPair(geo, dynamicCaption));
			}
		} catch (RuntimeException e) {
			Log.error("malformed <dynamicCaption>");
		}
	}

	private void handleContentSize(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoEmbed)) {
			Log.error("wrong element type for <contentSize>: " + geo.getClass());
			return;
		}

		GeoEmbed geoEmbed = (GeoEmbed) geo;

		try {
			double width = Double.parseDouble(attrs.get("width"));
			double height = Double.parseDouble(attrs.get("height"));

			geoEmbed.setContentWidth(width);
			geoEmbed.setContentHeight(height);
		} catch (NumberFormatException e) {
			Log.error("malformed <contentSize>");
		}
	}

	private void handleParentLabel(LinkedHashMap<String, String> attrs) {
		if (geo instanceof GeoLocusStroke) {
			((GeoLocusStroke) geo).setSplitParentLabel(attrs.get("val"));
		}

	}

	protected void initDefault(LinkedHashMap<String, String> attrs) {
		geo = getGeoElement(attrs);
		geo.setLineOpacity(255);
	}

	private void processStartPointList() {
		try {
			AlgebraProcessor algProc = xmlHandler.getAlgProcessor();

			for (LocateableExpPair pair : startPointList) {
				GeoPointND P = pair.point != null ? pair.point
						: algProc.evaluateToPoint(pair.exp,
								ErrorHelper.silent(), true);
				pair.locateable.setStartPoint(P, pair.number);

			}
		} catch (Exception e) {
			startPointList.clear();
			e.printStackTrace();
			addError("Invalid start point: " + e.toString());
		}
		startPointList.clear();
	}

	private void processLinkedGeoList() {
		try {
			for (GeoExpPair pair : linkedGeoList) {
				((GeoInputBox) pair.getGeo())
						.setLinkedGeo(xmlHandler.kernel.lookupLabel(pair.exp));
			}
		} catch (RuntimeException e) {
			linkedGeoList.clear();
			e.printStackTrace();
			addError("Invalid linked geo " + e.toString());
		}
		linkedGeoList.clear();
	}

	private void processDynamicCaptionList() {
		try {
			for (GeoExpPair pair : dynamicCaptionList) {
				GeoElement caption = xmlHandler.kernel.lookupLabel(pair.exp);
				if (caption.isGeoText()) {
					HasDynamicCaption text = (HasDynamicCaption) pair.geoElement;
					text.setDynamicCaption((GeoText) caption);
				} else {
					Log.error("dynamicCaption is not a GeoText");
					break;
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			dynamicCaptionList.clear();
		}
	}

	private void processShowObjectConditionList() {
		Iterator<GeoExpPair> it = showObjectConditionList.iterator();
		AlgebraProcessor algProc = xmlHandler.getAlgProcessor();

		while (it.hasNext()) {
			try {
				GeoExpPair pair = it.next();
				GeoBoolean condition = algProc.evaluateToBoolean(pair.exp,
						ErrorHelper.silent());
				if (condition != null) {
					pair.getGeo().setShowObjectCondition(condition);
				} else {
					addError("Invalid condition to show object: " + pair.exp);
				}

			} catch (Exception e) {
				showObjectConditionList.clear();
				e.printStackTrace();
				addError("Invalid condition to show object: " + e.toString());
			}
		}
		showObjectConditionList.clear();
	}

	private void processAnimationSpeedList() {
		try {
			Iterator<GeoExpPair> it = animationSpeedList.iterator();
			AlgebraProcessor algProc = xmlHandler.getAlgProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = it.next();
				GeoNumberValue num = algProc.evaluateToNumeric(pair.exp,
						xmlHandler.handler);
				pair.getGeo().setAnimationSpeedObject(num);
			}
		} catch (RuntimeException e) {
			animationSpeedList.clear();
			e.printStackTrace();
			addError("Invalid animation speed: " + e.toString());
		}
		animationSpeedList.clear();
	}

	private void processAnimationStepList() {
		try {
			Iterator<GeoExpPair> it = animationStepList.iterator();
			AlgebraProcessor algProc = xmlHandler.getAlgProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = it.next();
				NumberValue num = algProc.evaluateToNumeric(pair.exp,
						xmlHandler.handler);
				if (pair.getGeo().isGeoNumeric()) {
					((GeoNumeric) pair.getGeo())
							.setAutoStep(Double.isNaN(num.getDouble()));
				}
				pair.getGeo().setAnimationStep(num);

			}
		} catch (RuntimeException e) {
			animationStepList.clear();
			e.printStackTrace();
			addError("Invalid animation step: " + e.toString());
		}
		animationSpeedList.clear();
	}

	private void processAnimatingList() {
		try {
			for (GeoElement animGeo : animatingList) {
				animGeo.setAnimating(true);
			}
		} catch (RuntimeException e) {
			addError("Invalid animating: " + e.toString());
		}
		animatingList.clear();
	}

	private void processMinMaxList() {
		try {
			Iterator<GeoNumericMinMax> it = minMaxList.iterator();
			AlgebraProcessor algProc = xmlHandler.getAlgProcessor();

			while (it.hasNext()) {
				GeoNumericMinMax pair = it.next();
				// the setIntervalMin and setIntervalMax methods might turn ?
				// into defined
				// this is intentional, but when loading a file we must override
				// it for 3.2 compatibility
				GeoElement geoElement = pair.getGeo();
				boolean wasDefined = geoElement.isDefined();
				boolean isDrawable = geoElement.isDrawable();
				if (pair.min != null) {
					NumberValue num = algProc.evaluateToNumeric(pair.min,
							xmlHandler.handler);
					((GeoNumeric) geoElement).setIntervalMin(num);
				}

				if (pair.max != null) {
					NumberValue num2 = algProc.evaluateToNumeric(pair.max,
							xmlHandler.handler);
					((GeoNumeric) geoElement).setIntervalMax(num2);
				}

				if (!wasDefined) {
					geoElement.setUndefined();
				}
				if (!isDrawable && geoElement instanceof GeoNumeric) {
					((GeoNumeric) geoElement).setDrawable(false);
				}
			}
		} catch (RuntimeException e) {
			minMaxList.clear();
			e.printStackTrace();
			addError("Invalid min/max: " + e.toString());
		}
		minMaxList.clear();
	}

	// Michael Borcherds 2008-05-18
	private void processDynamicColorList() {
		try {
			Iterator<GeoExpPair> it = dynamicColorList.iterator();
			AlgebraProcessor algProc = xmlHandler.getAlgProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = it.next();
				pair.getGeo()
						.setColorFunction(algProc.evaluateToList(pair.exp));
			}
		} catch (RuntimeException e) {
			dynamicColorList.clear();
			e.printStackTrace();
			addError("Invalid dynamic color: " + e.toString());
		}
		dynamicColorList.clear();
	}

	private void addError(String string) {
		xmlHandler.errors.add(string);
	}

	protected void processLists() {
		processStartPointList();
		processLinkedGeoList();
		processShowObjectConditionList();
		processDynamicColorList();
		processDynamicCaptionList();
		processAnimationSpeedList();
		processAnimationStepList();
		processMinMaxList();

		processAnimatingList(); // must be after min/maxList otherwise
								// GeoElement.setAnimating doesn't work

	}

	protected void processDefaultLists() {
		processMinMaxList();
		processAnimationStepList();
		processAnimationSpeedList();
	}

	protected void reset() {
		startPointList.clear();
		showObjectConditionList.clear();
		dynamicColorList.clear();

		linkedGeoList.clear();
		animatingList.clear();
		minMaxList.clear();
		animationStepList.clear();
		animationSpeedList.clear();
		sliderTagProcessed = false;
		fontTagProcessed = false;
		lineStyleTagProcessed = false;
		symbolicTagProcessed = false;
		setEigenvectorsCalled = false;
	}

	/*
	 * expects r, g, b, alpha attributes to build a color
	 */
	private static GColor handleColorAlphaAttrs(
			LinkedHashMap<String, String> attrs) {
		try {
			int red = Integer.parseInt(attrs.get("r"));
			int green = Integer.parseInt(attrs.get("g"));
			int blue = Integer.parseInt(attrs.get("b"));
			int alpha = Integer.parseInt(attrs.get("alpha"));
			return GColor.newColor(red, green, blue, alpha);
		} catch (RuntimeException e) {
			return null;
		}
	}

	// called when <element> is encountered
	// e.g. for <element type="point" label="P">
	private GeoElement getGeoElement(LinkedHashMap<String, String> attrs) {
		GeoElement geo1 = null;
		String label = attrs.get("label");
		String type = attrs.get("type");
		String defaultset = attrs.get("default");
		if (label == null || type == null) {
			Log.error("attributes missing in <element>");
			return geo1;
		}

		if (defaultset == null
				|| !xmlHandler.kernel.getElementDefaultAllowed()) {
			// does a geo element with this label exist?
			geo1 = xmlHandler.kernel.lookupLabel(label);

			// Application.debug(label+", geo="+geo);
			// needed for TRAC-2719
			// if geo wasn't found in construction list
			// look in cas
			if (geo1 == null) {
				geo1 = xmlHandler.kernel.lookupCasCellLabel(label);
			}
			if (geo1 == null) {

				// try to find an algo on which this label depends
				// geo = cons.resolveLabelDependency(label,
				// kernel.getClassType(type));
				// if none, create new geo
				geo1 = xmlHandler.kernel.createGeoElement(xmlHandler.cons,
						type);
				geo1.setLoadedLabel(label);

				// Application.debug(label+", "+geo.isLabelSet());

				// independent GeoElements should be hidden by default
				// (as older versions of this file format did not
				// store show/hide information for all kinds of objects,
				// e.g. GeoNumeric)
				geo1.setEuclidianVisible(false);
			}
		} else {
			int defset = Integer.parseInt(defaultset);
			geo1 = xmlHandler.kernel.getConstruction().getConstructionDefaults()
					.getDefaultGeo(defset);
			if (geo1 == null) {
				// wrong default setting, act as if there were no default set
				geo1 = xmlHandler.kernel.lookupLabel(label);
				if (geo1 == null) {
					geo1 = xmlHandler.kernel.createGeoElement(xmlHandler.cons,
							type);
					geo1.setLoadedLabel(label);
					geo1.setEuclidianVisible(false);
				}
			}
		}

		// use default point style on points
		if (geo1.getGeoClassType().equals(GeoClass.POINT)
				&& xmlHandler.ggbFileFormat < 3.3) {
			((PointProperties) geo1).setPointStyle(docPointStyle);
		}

		// for downward compatibility
		if (geo1.isLimitedPath()) {
			LimitedPath lp = (LimitedPath) geo1;
			// old default value for intersections of segments, ...
			// V2.5: default of "allow outlying intersections" is now false
			lp.setAllowOutlyingIntersections(true);

			// old default value for geometric transforms of segments, ...
			// V2.6: default of "keep type on geometric transform" is now true
			lp.setKeepTypeOnGeometricTransform(false);
		}

		return geo1;
	}

	protected void updatePointStyle(LinkedHashMap<String, String> attrs) {
		// if there is a point style given save it
		if (xmlHandler.ggbFileFormat < 3.3) {
			String strPointStyle = attrs.get("pointStyle");
			if (strPointStyle != null) {
				docPointStyle = Integer.parseInt(strPointStyle);
			} else {
				docPointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
			}

			// TODO save as default construction (F.S.)
		} else {
			docPointStyle = -1;
		}
	}

	/**
	 * parse list of geos in a group
	 * @param attrs - labels of geos in the group
	 */
	public void handleGroup(LinkedHashMap<String, String> attrs) {
		ArrayList<GeoElement> geosInGroup = new ArrayList<>();
		for (String label : attrs.values()) {
			GeoElement geo = xmlHandler.kernel.lookupLabel(label);
			if (geo != null) {
				geosInGroup.add(geo);
			}
		}
		if (!geosInGroup.isEmpty()) {
			app.getKernel().getConstruction().createGroup(geosInGroup);
		}
	}
}
