/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

/*
 * GeoElement.java
 *
 * Created on 30. August 2001, 17:10
 */

package geogebra.common.kernel.geos;

import geogebra.common.awt.BufferedImage;
import geogebra.common.awt.Color;
import geogebra.common.awt.Point;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.factories.AwtFactory;
import geogebra.common.factories.FormatFactory;
import geogebra.common.factories.LaTeXFactory;
import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Locateable;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoCirclePointRadiusInterface;
import geogebra.common.kernel.algos.AlgoDrawInformation;
import geogebra.common.kernel.algos.AlgoDynamicCoordinatesInterface;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoElementInterface;
import geogebra.common.kernel.algos.AlgoJoinPointsSegmentInterface;
import geogebra.common.kernel.algos.AlgorithmSet;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.cas.CASGenericInterface;
import geogebra.common.kernel.cas.GeoGebraCasInterface;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.MyError;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.LaTeXCache;
import geogebra.common.util.MyMath;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.SpreadsheetTraceSettings;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

/**
 * 
 * @author Markus
 * @version 2011-12-02
 */
@SuppressWarnings("javadoc")
public abstract class GeoElement extends ConstructionElement implements
		ExpressionValue {

	public boolean isVector3DValue() {
		return false;
	}

	/**
	 * @return the ScriptType for update
	 */
	public ScriptType getUpdateScriptType() {
		return updateScriptType;
	}

	/**
	 * @param updateJavaScript
	 *            the updateJavaScript to set
	 */
	public void setUpdateScriptType(final ScriptType scriptType) {
		updateScriptType = scriptType;
	}

	/**
	 * @return the ScriptType for click
	 */
	public ScriptType getClickScriptType() {
		return clickScriptType;
	}

	public enum ScriptType {
		GGBSCRIPT, JAVASCRIPT, PYTHON
	}
	
	public enum EventType {
		CLICK, UPDATE
	}

	/**
	 * @param clickJavaScript
	 *            the clickJavaScript to set
	 */
	public void setClickScriptType(final ScriptType scriptType) {
		clickScriptType = scriptType;
	}

	protected ArrayList<GeoNumeric> spreadsheetTraceList = null;
	protected ArrayList<String> spreadsheetColumnHeadings = null;

	/** min decimals or significant figures to use in editing string */
	public static final int MIN_EDITING_PRINT_PRECISION = 5;

	// maximum label offset distance
	private static final int MAX_LABEL_OFFSET = 80;

	// private static int geoElementID = Integer.MIN_VALUE;

	private static final char[] complexLabels = { 'z', 'w' };

	private static final char[] pointLabels = { 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'Z' };

	private static final char[] functionLabels = { 'f', 'g', 'h', 'p', 'q',
			'r', 's', 't' };

	private static final char[] lineLabels = { 'a', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't' };

	private static final char[] vectorLabels = { 'u', 'v', 'w', 'z', 'a', 'b',
			'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'p',
			'q', 'r', 's', 't' };

	private static final char[] conicLabels = { 'c', 'd', 'e', 'f', 'g', 'h',
			'k', 'p', 'q', 'r', 's', 't' };

	private static final char[] lowerCaseLabels = { 'a', 'b', 'c', 'd', 'e',
			'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'u', 'v', 'w', 'z' };

	private static final char[] integerLabels = { 'n', 'i', 'j', 'k', 'l', 'm', };

	private static final char[] greekLowerCase = { '\u03b1', '\u03b2',
			'\u03b3', '\u03b4', '\u03b5', '\u03b6', '\u03b7', '\u03b8',
			'\u03b9', '\u03ba', '\u03bb', '\u03bc', '\u03bd', '\u03be',
			'\u03bf', '\u03c1', '\u03c3', '\u03c4', '\u03c5', '\u03d5',
			'\u03c7', '\u03c8', '\u03c9' };

	private static final char[] arabic = { '\u0623', '\u0628', '\u062a',
			'\u062b', '\u062c', '\u062d', '\u062e', '\u062f', '\u0630',
			'\u0631', '\u0632', '\u0633', '\u0634', '\u0635', '\u0636',
			'\u0637', '\u0638', '\u0639', '\u063a', '\u0641', '\u0642',
			'\u0643', '\u0644', '\u0645', '\u0646', '\u0647', // needs this too
																// '\u0640' (see
																// later on)
			'\u0648', '\u064a' };

	private static final char[] greekUpperCase = { // Michael Borcherds
			// 2008-02-23
			'\u0391', '\u0392', '\u0393', '\u0394', '\u0395', '\u0396',
			'\u0397', '\u0398', '\u0399', '\u039a', '\u039b', '\u039c',
			'\u039d', '\u039e', '\u039f', '\u03a0', '\u03a1', '\u03a3',
			'\u03a4', '\u03a5', '\u03a6', '\u03a7', '\u03a8', '\u03a9' };

	public static final int LABEL_NAME = 0;
	public static final int LABEL_NAME_VALUE = 1;
	public static final int LABEL_VALUE = 2;
	public static final int LABEL_CAPTION = 3; // Michael Borcherds 2008-02-18

	public static final int TOOLTIP_ALGEBRAVIEW_SHOWING = 0;
	public static final int TOOLTIP_ON = 1;
	public static final int TOOLTIP_OFF = 2;
	public static final int TOOLTIP_CAPTION = 3;
	public static final int TOOLTIP_NEXTCELL = 4;
	private int tooltipMode = TOOLTIP_ALGEBRAVIEW_SHOWING;

	public String label; // should only be used directly in subclasses
	private String realLabel; // for macro constructions, see setRealLabel() for
								// details
	private String oldLabel; // see doRenameLabel
	private String caption;
	public boolean labelWanted = false;

	public boolean labelSet = false;

	boolean localVarLabelSet = false;
	private boolean euclidianVisible = true;
	private boolean forceEuclidianVisible = false;
	protected boolean algebraVisible = true;
	private boolean labelVisible = true;
	private boolean isConsProtBreakpoint; // in construction protocol
	private boolean isAlgoMacroOutput; // is an output object of a macro
										// construction
	protected boolean fixed = false;
	public int labelMode = LABEL_NAME;
	public int toStringMode = Kernel.COORD_CARTESIAN; // cartesian or
														// polar
	protected Color objColor = Color.black;
	protected Color bgColor = null; // none by default
	protected Color selColor = objColor;
	protected Color labelColor = objColor;
	protected Color fillColor = objColor;
	public int layer = 0; // Michael Borcherds 2008-02-23
	private NumberValue animationIncrement;
	private NumberValue animationSpeedObj;
	private GeoCasCell correspondingCasCell; // used by GeoCasCell
	private boolean animating = false;

	public final static double MAX_ANIMATION_SPEED = 100;
	public final static int ANIMATION_OSCILLATING = 0;
	public final static int ANIMATION_INCREASING = 1;
	public final static int ANIMATION_DECREASING = 2;
	public final static int ANIMATION_INCREASING_ONCE = 3;
	private int animationType = ANIMATION_OSCILLATING;
	private int animationDirection = 1;

	protected float alphaValue = 0.0f;
	protected int hatchingAngle = 45; // in degrees
	protected int hatchingDistance = 10;
	private boolean inverseFill = false;

	// =================================
	// G.Sturr new fill options
	/** substitute for imageFileName and image - Arpad Fekete;
	// 2011-12-01 */
	protected GeoElementGraphicsAdapter graphicsadapter; 
	public static final int FILL_STANDARD = 0;
	public static final int FILL_HATCH = 1;
	public static final int FILL_IMAGE = 2;
	protected int fillType = FILL_STANDARD;
	public int numberOfFillTypes = 3;

	// =================================

	public final static int COLORSPACE_RGB = 0;
	public final static int COLORSPACE_HSB = 1;
	public final static int COLORSPACE_HSL = 2;
	private int colorSpace = COLORSPACE_RGB;

	private final List<Integer> viewFlags;

	public int getColorSpace() {
		return colorSpace;
	}

	public void setColorSpace(final int colorSpace) {
		this.colorSpace = colorSpace;
	}

	private int defaultGeoType = -1;

	public int getDefaultGeoType() {
		return defaultGeoType;
	}

	public void setDefaultGeoType(final int defaultGT) {
		defaultGeoType = defaultGT;
	}
	/** offset for label in EV*/ 
	public int labelOffsetX = 0; 
			/** offset for label in EV*/
	public int	labelOffsetY = 0;
	private boolean auxiliaryObject = false;
	private boolean selectionAllowed = true;
	// on change: see setVisualValues()

	// spreadsheet specific properties
	private Point spreadsheetCoords, oldSpreadsheetCoords;
	// number of AlgoCellRange using this cell: don't allow renaming when
	// greater 0
	private int cellRangeUsers = 0;
	// number of AlgoDependentCasCell using this cell: send updates to CAS
	private int casAlgoUsers = 0;

	// condition to show object
	protected GeoBoolean condShowObject;

	// function to determine color
	private GeoList colFunction; // { GeoNumeric red, GeoNumeric Green,
									// GeoNumeric Blue }

	private boolean useVisualDefaults = true;
	protected boolean isColorSet = false;
	protected boolean highlighted = false;
	private boolean selected = false;
	private String strAlgebraDescription, strAlgebraDescTextOrHTML,
			strAlgebraDescriptionHTML, strLabelTextOrHTML;

	protected String strLaTeX;
	private boolean strAlgebraDescriptionNeedsUpdate = true;
	private boolean strAlgebraDescTextOrHTMLneedsUpdate = true;
	private boolean strAlgebraDescriptionHTMLneedsUpdate = true;
	private boolean strLabelTextOrHTMLUpdate = true;
	protected boolean strLaTeXneedsUpdate = true;

	// line thickness and line type: s
	/** note: line thickness in Drawable is calculated as lineThickness / 2.0f */
	public int lineThickness = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;
	/** line type (full, dashed, ...) see EuclidianStyleConstants.LINE_TYPE */
	public int lineType = EuclidianStyleConstants.DEFAULT_LINE_TYPE;
	/** line type for hidden parts (for 3D) */
	public int lineTypeHidden = EuclidianStyleConstants.DEFAULT_LINE_TYPE_HIDDEN;

	// decoration types
	public int decorationType = DECORATION_NONE;

	// DECORATION
	public static final int DECORATION_NONE = 0;
	// segment decorations
	public static final int DECORATION_SEGMENT_ONE_TICK = 1;
	public static final int DECORATION_SEGMENT_TWO_TICKS = 2;
	public static final int DECORATION_SEGMENT_THREE_TICKS = 3;
	// Michael Borcherds 2007-10-06
	public static final int DECORATION_SEGMENT_ONE_ARROW = 4;
	public static final int DECORATION_SEGMENT_TWO_ARROWS = 5;
	public static final int DECORATION_SEGMENT_THREE_ARROWS = 6;
	// Michael Borcherds 2007-10-06
	// angle decorations
	public static final int DECORATION_ANGLE_TWO_ARCS = 1;
	public static final int DECORATION_ANGLE_THREE_ARCS = 2;
	public static final int DECORATION_ANGLE_ONE_TICK = 3;
	public static final int DECORATION_ANGLE_TWO_TICKS = 4;
	public static final int DECORATION_ANGLE_THREE_TICKS = 5;
	// Michael Borcherds START 2007-11-19
	public static final int DECORATION_ANGLE_ARROW_ANTICLOCKWISE = 6; // Michael
																		// Borcherds
																		// 2007-10-22
	public static final int DECORATION_ANGLE_ARROW_CLOCKWISE = 7; // Michael
																	// Borcherds
																	// 2007-10-22
	// Michael Borcherds END 2007-11-19

	// public int geoID;
	// static private int geoCounter = 0;
	/** parent algorithm */
	public AlgoElement algoParent = null;// protected
	/** draw algorithm */
	protected AlgoElement algoDraw = null;
	private ArrayList<AlgoElement> algorithmList; // directly dependent algos

	/** set of all dependent algos sorted in topological order */
	protected AlgorithmSet algoUpdateSet;

	private final GeoElementSpreadsheet geoElementSpreadsheet;

	/********************************************************/

	/**
	 * Creates new GeoElement for given construction
	 * 
	 * @param c
	 *            Construction
	 */
	public GeoElement(final Construction c) {
		super(c);

		graphicsadapter = app.newGeoElementGraphicsAdapter();
		geoElementSpreadsheet = kernel.getGeoElementSpreadsheet();
		// this.geoID = geoCounter++;

		// moved to subclasses, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		// setConstructionDefaults(); // init visual settings

		// new elements become breakpoints if only breakpoints are shown
		// isConsProtBreakpoint = cons.showOnlyBreakpoints();

		// ensure all new objects are in the top layer
		if (app != null) {
			layer = app.maxLayerUsed;
		}

		viewFlags = new ArrayList<Integer>();
		EuclidianViewInterfaceSlim ev;
		if ((app != null) && ((ev = app.getActiveEuclidianView()) != null)) {
			viewFlags.add(ev.getViewID());
			// if ev isn't Graphics or Graphics 2, then also add 1st 2D
			// euclidian view
			if (!(ev.isDefault2D())) {
				viewFlags.add(AbstractApplication.VIEW_EUCLIDIAN);
			}
		} else {
			viewFlags.add(AbstractApplication.VIEW_EUCLIDIAN);
		}
	}

	/* ****************************************************** */

	/**
	 * We may need a simple method to get the label, as in the CopyPaste class.
	 * 
	 * @return label if it is set
	 */
	public String getLabelSimple() {
		return label;
	}

	/**
	 * We may need a simple method to set the label, as in the CopyPaste class.
	 * 
	 * @param lab
	 *            the label to set
	 */
	public void setLabelSimple(final String lab) {
		label = lab;
	}

	/**
	 * Returns label of GeoElement. If the label is null then
	 * algoParent.getCommandDescription() or toValueString() is returned.
	 * 
	 * @return geo's label if set, command description otherwise
	 */
	@Deprecated
	public final String getLabel() {
		return getLabel(StringTemplate.defaultTemplate);
	}
	
	public String getLabel(StringTemplate tpl) {
		if (!labelSet && !localVarLabelSet) {
			if (algoParent == null) {
				return toOutputValueString(tpl);
			}
			return algoParent.getCommandDescription(tpl);
		}
		return kernel.printVariableName(label,tpl);
	}

	public void copyLabel(final GeoElement c) {
		label = c.label;
	}

	/**
	 * Switch label mode among value, name, value+name and caption
	 * 
	 * @param mode
	 *            LABEL_ mode
	 */
	public void setLabelMode(final int mode) {
		switch (mode) {
		case LABEL_NAME_VALUE:
			labelMode = LABEL_NAME_VALUE;
			break;

		case LABEL_VALUE:
			labelMode = LABEL_VALUE;
			break;

		case LABEL_CAPTION: // Michael Borcherds 2008-02-18
			labelMode = LABEL_CAPTION;
			break;

		default:
			labelMode = LABEL_NAME;
		}
	}

	/**
	 * Returns how should label look like in Euclidian view
	 * 
	 * @return label mode (name, value, name + value, caption)
	 */
	public int getLabelMode() {
		return labelMode;
	}

	/**
	 * return the label position (2D or 3D vector)
	 * 
	 * @return the label position (2D or 3D vector)
	 */
	public Coords getLabelPosition() {
		return new Coords(0, 0, 0, 1);
	}

	/**
	 * Returns the {@link GeoClass}
	 * 
	 * @return GeoClass
	 */
	public abstract GeoClass getGeoClassType();

	/**
	 * every subclass implements it's own copy method this is needed for
	 * assignment copies like: a = 2.7 b = a (here copy() is needed)
	 * 
	 * @return copy of current element
	 * */
	public abstract GeoElement copy();

	/**
	 * This method always returns a GeoElement of the SAME CLASS as this
	 * GeoElement. Furthermore the resulting geo is in construction cons.
	 * 
	 * @param consToCopy
	 *            construction
	 * @return copy in given construction
	 */
	public GeoElement copyInternal(final Construction consToCopy) {
		// default implementation: changed in some subclasses
		final GeoElement geoCopy = copy();
		geoCopy.setConstruction(consToCopy);
		return geoCopy;
	}

	/**
	 * Copies the given points array. The resulting points are part of the given
	 * construction.
	 * 
	 * @param cons
	 * @param points
	 * @return copy of points in construction cons
	 */
	public static GeoPoint2[] copyPoints(final Construction cons,
			final GeoPointND[] points) {
		final GeoPoint2[] pointsCopy = new GeoPoint2[points.length];
		for (int i = 0; i < points.length; i++) {
			pointsCopy[i] = (GeoPoint2) ((GeoPoint2) points[i])
					.copyInternal(cons);
			pointsCopy[i].set(points[i]);
		}
		return pointsCopy;
	}

	/**
	 * Copies the given points array. The resulting points are part of the given
	 * construction.
	 * 
	 * @param cons
	 * @param points
	 * @return copy of points in construction cons
	 */
	public static GeoPointND[] copyPointsND(final Construction cons,
			final GeoPointND[] points) {
		final GeoPointND[] pointsCopy = new GeoPointND[points.length];
		for (int i = 0; i < points.length; i++) {
			pointsCopy[i] = (GeoPointND) ((GeoElement) points[i])
					.copyInternal(cons);
			((GeoElement) pointsCopy[i]).set((GeoElement) points[i]);
		}

		return pointsCopy;
	}

	/**
	 * Copies the given element using given kernel
	 * @param kernel1 Kernel
	 */
	public ExpressionValue deepCopy(final Kernel kernel1) {
		// default implementation: changed in some subclasses
		return copy();
	}

	public void resolveVariables() {
		//do nothing
	}

	public boolean isInfinite() {
		return false;
	}

	/**
	 * every subclass implements it's own set method
	 * 
	 * @param geo
	 *            geo to copy
	 */
	public abstract void set(GeoElement geo);

	/**
	 * Returns false for undefined objects
	 * @return false when undefined
	 */
	public abstract boolean isDefined();

	/**
	 * Makes object undefined, some objects lose their internally stored value when this is called
	 */
	public abstract void setUndefined();

	public abstract String toValueString(StringTemplate tpl);

	private EuclidianViewInterfaceSlim viewForValueString;

	/**
	 * sets a view for building the value string
	 * 
	 * @param view
	 */
	public void setViewForValueString(final EuclidianViewInterfaceSlim view) {
		viewForValueString = view;
	}

	/**
	 * 
	 * @return the view used for building the value string
	 */
	public EuclidianViewInterfaceSlim getViewForValueString() {
		return viewForValueString;
	}

	/**
	 * 
	 * @return true if the value string can be changed regarding a view
	 */
	public boolean hasValueStringChangeableRegardingView() {
		return false;
	}

	/**
	 * Returns definition or value string of this object. Automatically
	 * increases decimals to at least 5, e.g. FractionText[4/3] ->
	 * FractionText[1.333333333333333]
	 * 
	 * @param useChangeable
	 *            if false, point on path is ignored
	 * @param useOutputValueString
	 *            if true, use outputValueString rather than valueString
	 * @return definition or value string of this object
	 */
	public String getRedefineString(final boolean useChangeable,
			final boolean useOutputValueString) {
		
				
		StringTemplate tpl = StringTemplate.editTemplate;
		String ret = null;
		final boolean isIndependent = !isPointOnPath() && useChangeable ? isChangeable()
				: isIndependent();
		if (isIndependent) {
			ret = useOutputValueString ? toOutputValueString(tpl)
					: toValueString(tpl);
		} else {
			ret = getCommandDescription(tpl);
		}

		return ret;
	}

	/**
	 * Returns the character which is used between label and definition
	 * 
	 * @return for conics, implicit polynomials and inequalities, = otherwise
	 */
	protected char getLabelDelimiter() {
		return '=';
	}

	/**
	 * Returns the definition of this GeoElement for the input field, e.g. A1 =
	 * 5, B1 = A1 + 2
	 * 
	 * @return definition for input field
	 */
	public String getDefinitionForInputBar() {
		// for expressions like "3 = 2 A2 - A1"
		// getAlgebraDescription() returns "3 = 5"
		// so we need to use getCommandDescription() in those cases

		String inputBarStr = getCommandDescription(StringTemplate.editTemplate);
		if (!inputBarStr.equals("")) {

			// check needed for eg f(x) = g(x) + h(x), f(x) = sin(x)
			final char delimiter = getLabelDelimiter();
			if (inputBarStr.indexOf(delimiter) < 0) {
				inputBarStr = getLabel(StringTemplate.editTemplate)
						+ (delimiter == '=' ? " =" : delimiter) + " "
						+ inputBarStr;
			}
		} else {
			inputBarStr = getAlgebraDescription(StringTemplate.editTemplate);
		}

		

		return inputBarStr;
	}

	/**
	 * Returns the value of this GeoElement for the input field, e.g. A1 = 5, B1
	 * = A1 + 2
	 * 
	 * @return value for input field
	 */
	public String getValueForInputBar() {
		StringTemplate tpl = StringTemplate.editTemplate;

		// copy into text field
		final String ret = toOutputValueString(tpl);

		return ret;
	}

	/**
	 * Sets this object to zero (number = 0, points = (0,0), etc.)
	 */
	public void setZero() {
		// TODO ?
	}

	/**
	 * Returns a value string that is saveable in an XML file. Note: this is
	 * needed for texts that need to be quoted in lists and as command
	 * arguments.
	 */
	public String toOutputValueString(StringTemplate tpl) {
		if (isLocalVariable()) {
			return label;
		}
		return toValueString(tpl);
	}
	/**
	 * Set visual style from defaults
	 */
	public void setConstructionDefaults() {
		if (useVisualDefaults) {
			final ConstructionDefaults consDef = cons.getConstructionDefaults();
			if (consDef != null) {
				consDef.setDefaultVisualStyles(this, false);
			}
		}
	}

	/**
	 * 
	 * @param color new color for this object
	 */
	public void setObjColor(final Color color) {
		isColorSet = true;

		objColor = color;
		labelColor = color;
		fillColor = color;
		setAlphaValue(alphaValue);

		// selColor = getInverseColor(objColor);
		if (color != null) {
			selColor = geogebra.common.factories.AwtFactory.prototype.newColor(
					color.getRed(), color.getGreen(), color.getBlue(), 100);
		}
	}

	/**
	 * Returns true if color was explicitly set
	 * 
	 * @return true if color was explicitly set
	 */
	public boolean isColorSet() {
		return isColorSet;
	}

	// Michael Borcherds 2008-04-02
	private Color getRGBFromList(float alpha1) {
		float alpha2 = alpha1;
		if (alpha2 > 1f) {
			alpha2 = 1f;
		}
		if (alpha2 < 0f) {
			alpha2 = 0f;
		}

		final int alpha = (int) (alpha2 * 255f);
		return getRGBFromList(alpha);
	}

	// Michael Borcherds 2008-04-02
	private Color getRGBFromList(int withAlpha) {
		int alpha = withAlpha;
		if (alpha > 255) {
			alpha = 255;
		} else if (alpha < 0) {
			alpha = 0;
		}

		// get rgb values from color list
		double redD = 0, greenD = 0, blueD = 0;
		for (int i = 0; i < 3; i++) {
			final GeoElement geo = colFunction.get(i);
			if (geo.isDefined()) {
				final double val = ((NumberValue) geo).getDouble();
				switch (i) {
				case 0:
					redD = val;
					break;
				case 1:
					greenD = val;
					break;
				case 2:
					blueD = val;
					break;
				}
			}
		}

		// double epsilon = 0.000001; // 1 - floor(1) = 0 but we want 1.

		// make sure the colors are between 0 and 1
		redD = (redD / 2) - Math.floor(redD / 2);
		greenD = (greenD / 2) - Math.floor(greenD / 2);
		blueD = (blueD / 2) - Math.floor(blueD / 2);

		// step function so
		// [0,1] -> [0,1]
		// [1,2] -> [1,0]
		// [2,3] -> [0,1]
		// [3,4] -> [1,0]
		// [4,5] -> [0,1] etc
		if (redD > 0.5) {
			redD = 2 * (1 - redD);
		} else {
			redD = 2 * redD;
		}
		if (greenD > 0.5) {
			greenD = 2 * (1 - greenD);
		} else {
			greenD = 2 * greenD;
		}
		if (blueD > 0.5) {
			blueD = 2 * (1 - blueD);
		} else {
			blueD = 2 * blueD;
		}

		// Application.debug("red"+redD+"green"+greenD+"blue"+blueD);

		// adjust color triple to alternate color spaces, default to RGB
		switch (colorSpace) {

		case GeoElement.COLORSPACE_HSB:

			final int rgb = Color.HSBtoRGB((float) redD, (float) greenD,
					(float) blueD);
			redD = (rgb >> 16) & 0xFF;
			greenD = (rgb >> 8) & 0xFF;
			blueD = rgb & 0xFF;
			return AwtFactory.prototype.newColor((int) redD, (int) greenD,
					(int) blueD, alpha);

		case GeoElement.COLORSPACE_HSL:

			// algorithm taken from wikipedia article:
			// http://en.wikipedia.org/wiki/HSL_and_HSV

			final double H = redD * 6;
			final double S = greenD;
			final double L = blueD;

			final double C = (1 - Math.abs((2 * L) - 1)) * S;
			final double X = C * (1 - Math.abs((H % 2) - 1));

			double R1 = 0,
			G1 = 0,
			B1 = 0;

			if (H < 1) {
				R1 = C;
				G1 = X;
				B1 = 0;
			} else if (H < 2) {
				R1 = X;
				G1 = C;
				B1 = 0;
			} else if (H < 3) {
				R1 = 0;
				G1 = C;
				B1 = X;
			} else if (H < 4) {
				R1 = 0;
				G1 = X;
				B1 = C;
			} else if (H < 5) {
				R1 = X;
				G1 = 0;
				B1 = C;
			} else {
				R1 = C;
				G1 = 0;
				B1 = X;
			}

			final double m = L - (.5 * C);

			final Color c = AwtFactory.prototype.newColor(
					(int) ((R1 + m) * 255.0), (int) ((G1 + m) * 255.0),
					(int) ((B1 + m) * 255.0), alpha);
			return c;

		case GeoElement.COLORSPACE_RGB:
		default:
			return AwtFactory.prototype.newColor((int) (redD * 255.0),
					(int) (greenD * 255.0), (int) (blueD * 255.0), alpha);

		}

	}
	/**
	 * 
	 * @return color of object for selection
	 */
	// Michael Borcherds 2008-04-02
	public Color getSelColor() {
		if (colFunction == null) {
			return selColor;
		}
		return getRGBFromList(100);
	}
	/**
	 * 
	 * @return color of fill
	 */
	// Michael Borcherds 2008-04-02
	public Color getFillColor() {
		if (colFunction == null) {
			return fillColor;
		}
		return getRGBFromList(getAlphaValue());
	}

	/**
	 * return black if the color is white, so it can be seen
	 * 
	 * @return color for algebra view (same as label or black)
	 */
	public Color getAlgebraColor() {
		final Color col = getLabelColor();
		return col.equals(Color.white) ? Color.black : col;
	}

	/**
	 * 
	 * @return color of label
	 */
	// Michael Borcherds 2008-04-01
	public Color getLabelColor() {
		if (colFunction == null) {
			return labelColor;
		}
		return getObjectColor();
	}
	/**
	 * 
	 * @param color new color for label
	 */
	// Michael Borcherds 2008-04-01
	public void setLabelColor(final Color color) {
		labelColor = color;
	}
	/**
	 * 
	 * @return color of background
	 */
	public Color getBackgroundColor() {
		return bgColor;
	}
	/**
	 * 
	 * @param bgCol new background color
	 */
	public void setBackgroundColor(final Color bgCol) {
		bgColor = bgCol;
	}
	/**
	 * 
	 * @return current color for this object
	 */
	// Michael Borcherds 2008-04-02
	public Color getObjectColor() {
		Color col = objColor;

		try {
			if (colFunction != null) {
				col = getRGBFromList(255);
			}
		} catch (final Exception e) {
			removeColorFunction();
		}

		return col;
	}

	// Michael Borcherds 2008-03-01
	/**
	 * Sets layer
	 * 
	 * @param layer
	 *            layer from 0 to 9
	 */
	public void setLayer(int layer2) {
		int newlayer = layer2;
		// Application.printStacktrace("layer="+layer);

		if (layer2 == this.layer
		// layer valid only for Drawable objects
		// DON'T check this: eg angles on file load are not yet isDrawable()
		// || !isDrawable()
		) {
			return;
		}
		if (newlayer > EuclidianStyleConstants.MAX_LAYERS) {
			newlayer = EuclidianStyleConstants.MAX_LAYERS;
		} else if (newlayer < 0) {
			newlayer = 0;
		}

		kernel.notifyChangeLayer(this, this.layer, newlayer);

		this.layer = newlayer;
	}

	// Michael Borcherds 2008-02-23
	/**
	 * @return layer of this geo (0 to 9)
	 */
	public int getLayer() {
		return layer;
	}
	/**
	 * 
	 * @return drawing priority (lower = drawn first)
	 */
	// Michael Borcherds 2008-02-23
	public long getDrawingPriority() {

		long typePriority;

		switch (getGeoClassType()) {
		case AXIS:
			typePriority = 10;
			break;
		case IMAGE:
		case TEXTFIELD:
		case BUTTON:
		case BOOLEAN:
			typePriority = 20;
			break;
		case LIST:
			typePriority = 40;
			break;
		case POLYGON:
		case POLYGON3D:
			typePriority = 50;
			break;
		case POLYLINE:
			typePriority = 51;
			break;
		case IMPLICIT_POLY:
			typePriority = 60;
			break;
		case CONIC:
		case CONICPART:
			typePriority = 70;
			break;
		case ANGLE:
		case ANGLE3D:
		case NUMERIC:
			typePriority = 80;
			break;
		case INTERVAL: // not drawable
		case FUNCTION:
		case FUNCTIONCONDITIONAL:
		case CURVE_CARTESIAN:
		case CURVE_POLAR:
			typePriority = 90;
			break;
		case LINE:
		case LINE3D:
			typePriority = 100;
			break;
		case LINEAR_INEQUALITY:
			typePriority = 101;
			break;
		case FUNCTION_NVAR:
			typePriority = 102;
			break;
		case RAY:
		case SEGMENT:
		case RAY3D:
		case SEGMENT3D:
			typePriority = 110;
			break;
		case VECTOR:
			typePriority = 120;
			break;
		case LOCUS:
			typePriority = 130;
			break;
		case POINT:
		case POINT3D:
			typePriority = 140;
			break;
		case TEXT:
			typePriority = 150;
			break;
		default: // shouldn't occur
			AbstractApplication.debug("missing case in getDrawingPriority()");
			typePriority = 160;
		}

		// priority = 100 000 000
		final long ret = (long) ((typePriority * 10E9) + getConstructionIndex());

		// Application.debug("priority: " + ret + ", " + this);
		return ret;
	}

	public void setAlphaValue(final float alpha) {
		if ((fillColor == null) || (alpha < 0.0f) || (alpha > 1.0f)) {
			return;
		}
		alphaValue = alpha;

		final float[] rgb = new float[3];
		fillColor.getRGBColorComponents(rgb);
		fillColor = AwtFactory.prototype
				.newColor(rgb[0], rgb[1], rgb[2], alpha);
	}

	public float getAlphaValue() {
		if ((colFunction == null) || (colFunction.size() == 3)) {
			return alphaValue;
		}

		final GeoElement geo = colFunction.get(3);
		if (geo.isDefined()) {
			double alpha = ((NumberValue) geo).getDouble();

			// ensure between 0 and 1
			alpha = (alpha / 2) - Math.floor(alpha / 2);
			if (alpha > 0.5) {
				alpha = 2 * (1 - alpha);
			} else {
				alpha = 2 * alpha;
			}
			return (float) alpha;
		}
		return alphaValue;
	}

	public boolean isLimitedPath() {
		return false;
	}

	public boolean isPath() {
		return false;
	}

	public boolean isRegion() {
		return false;
	}

	public boolean isGeoList() {
		return false;
	}

	/**
	 * Sets all visual values from given GeoElement. This will also affect
	 * tracing, label location and the location of texts for example.
	 * 
	 * @param geo
	 * @param keepAdvanced
	 *            true to skip copying color function and visibility condition
	 */
	public void setAllVisualProperties(final GeoElement geo,
			final boolean keepAdvanced) {
		if (keepAdvanced) {
			setVisualStyle(geo);
		} else {
			setAdvancedVisualStyle(geo);
		}

		euclidianVisible = geo.euclidianVisible;
		algebraVisible = geo.algebraVisible;
		labelOffsetX = geo.labelOffsetX;
		labelOffsetY = geo.labelOffsetY;
		caption = geo.caption;
		inverseFill = geo.inverseFill;
		if (isTraceable() && geo.isTraceable()) {
			((Traceable) this).setTrace(((Traceable) geo).getTrace());
		}

		// if (isGeoPoint() && geo.isGeoPoint()) {
		if (getGeoClassType().equals(GeoClass.POINT)
				&& geo.getGeoClassType().equals(GeoClass.POINT)) {
			((GeoPoint2) this).setSpreadsheetTrace(((GeoPoint2) geo)
					.getSpreadsheetTrace());
		}

		// copy color function
		if (!keepAdvanced) {
			if (geo.colFunction != null) {
				setColorFunction(geo.colFunction);
			}
		}

		// copy ShowObjectCondition, unless it generates a
		// CirclularDefinitionException
		if (!keepAdvanced) {
			if (geo.condShowObject != null) {
				try {
					setShowObjectCondition(geo.getShowObjectCondition());
				} catch (final Exception e) {
					//do nothing
				}

			}
		}
		// G.Sturr 2010-6-26
		if (isSpreadsheetTraceable() && geo.getSpreadsheetTrace()) {
			setSpreadsheetTrace(true);
			traceSettings = geo.traceSettings;
		}
		// END G.Sturr

	}

	/*
	 * In future, this can be used to turn on/off whether transformed objects
	 * have the same style as the original object
	 */
	public void setVisualStyleForTransformations(final GeoElement geo) {
		setVisualStyle(geo);
		update();
	}

	/**
	 * Just changes the basic visual styles. If the style of a geo is reset this
	 * is required as we don't want to overwrite advanced settings in that case.
	 * 
	 * @param geo
	 */
	public void setVisualStyle(final GeoElement geo) {

		// label style
		labelVisible = geo.labelVisible;
		labelMode = geo.labelMode;
		tooltipMode = geo.tooltipMode;

		// style of equation, coordinates, ...
		if (getGeoClassType() == geo.getGeoClassType()) {
			toStringMode = geo.toStringMode;
		}

		// colors
		objColor = geo.objColor;
		selColor = geo.selColor;
		labelColor = geo.labelColor;
		if (geo.isFillable()) {
			fillColor = geo.fillColor;
			fillType = geo.fillType;
			hatchingAngle = geo.hatchingAngle;
			hatchingDistance = geo.hatchingDistance;
			graphicsadapter.setImageFileName(geo.getGraphicsAdapter()
					.getImageFileName());
			alphaValue = geo.alphaValue;
		} else {
			fillColor = geo.objColor;
			setAlphaValue(geo.getAlphaValue());
		}
		bgColor = geo.bgColor;
		isColorSet = geo.isColorSet();
		// line thickness and line type:
		// note: line thickness in Drawable is calculated as lineThickness /
		// 2.0f
		setLineThickness(geo.lineThickness);
		setLineType(geo.lineType);
		setLineTypeHidden(geo.lineTypeHidden);
		setDecorationType(geo.decorationType);

		// set whether it's an auxilliary object
		setAuxiliaryObject(geo.isAuxiliaryObject());

		// if layer is not zero (eg a new object has layer set to
		// ev.getMaxLayerUsed())
		// we don't want to set it
		if (layer == 0) {
			setLayer(geo.getLayer());
		}

	}

	/**
	 * Also copy advanced settings of this object.
	 * 
	 * @param geo
	 */
	public void setAdvancedVisualStyle(final GeoElement geo) {
		setVisualStyle(geo);

		// set layer
		setLayer(geo.getLayer());

		// copy color function
		setColorFunction(geo.getColorFunction());
		setColorSpace(geo.getColorSpace());

		// copy ShowObjectCondition, unless it generates a
		// CirclularDefinitionException
		try {
			setShowObjectCondition(geo.getShowObjectCondition());
		} catch (final Exception e) {
			//CircularException, we ignore it
		}
	}

	public GeoElementGraphicsAdapter getGraphicsAdapter() {
		return graphicsadapter;
	}

	/**
	 * @return
	 * 
	 *         private static Color getInverseColor(Color c) { float[] hsb =
	 *         Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
	 *         hsb[0] += 0.40; if (hsb[0] > 1) hsb[0]--; hsb[1] = 1; hsb[2] =
	 *         0.7f; return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
	 * 
	 *         }
	 */

	/**
	 * Moves label by updating label offset
	 * 
	 * @param x
	 * @param y
	 */
	public void setLabelOffset(int xcoord, int ycoord) {
		int x = xcoord;
		int y = ycoord;
		final double len = MyMath.length(x, y);
		if (len > MAX_LABEL_OFFSET) {
			final double factor = MAX_LABEL_OFFSET / len;
			x = (int) Math.round(factor * x);
			y = (int) Math.round(factor * y);
		}

		labelOffsetX = x;
		labelOffsetY = y;
	}

	/**
	 * @return whether object should be visible in at least one view
	 */
	final public boolean isVisible() {
		return isEuclidianVisible() || isAlgebraVisible();
	}

	/**
	 * @return whether object should be drawn in euclidian view
	 */
	final public boolean isEuclidianVisible() {

		// used by DrawPoint to draw parts of intersection objects near the
		// point
		if (forceEuclidianVisible) {
			return true;
		}

		if (!showInEuclidianView()) {
			return false;
		}

		if (condShowObject == null) {
			return euclidianVisible;
		}
		return condShowObject.getBoolean();
	}

	public void setEuclidianVisible(final boolean visible) {
		euclidianVisible = visible;
	}

	public void forceEuclidianVisible(final boolean visible) {
		forceEuclidianVisible = visible;
	}

	public boolean isSetEuclidianVisible() {
		return euclidianVisible;
	}

	/**
	 * Returns whether this GeoElement is visible in the construction protocol
	 */
	@Override
	final public boolean isConsProtocolBreakpoint() {
		return isConsProtBreakpoint;
	}

	public void setConsProtocolBreakpoint(final boolean flag) {
		/*
		 * // all siblings need to have same breakpoint information GeoElement
		 * [] siblings = getSiblings(); if (siblings != null) { for (int i=0; i
		 * < siblings.length; i++) { siblings[i].isConsProtBreakpoint = flag; }
		 * }
		 */

		isConsProtBreakpoint = flag;
	}

	/**
	 * Returns the children of the parent algorithm or null.
	 * 
	 * @return the children of the parent algorithm or null.
	 */
	public GeoElement[] getSiblings() {
		if (algoParent != null) {
			return algoParent.getOutput();
		}
		return null;
	}

	public boolean isDrawable() {
		return true;
	}

	public boolean isFillable() {
		return false;
	}

	public boolean isInverseFillable() {
		return false;
	}

	public boolean isTraceable() {
		return false;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(final boolean flag) {
		if (!flag) {
			fixed = flag;
		} else if (isFixable()) {
			fixed = flag;
		}
	}

	public boolean isFixable() {
		return true; // deleting objects with fixed descendents makes them
						// undefined
		// return isIndependent();
	}

	/*
	 * if an object has a fixed descendent, we want to set it undefined
	 */
	final public void removeOrSetUndefinedIfHasFixedDescendent() {

		// can't delete a fixed object at all
		if (isFixed()) {
			return;
		}

		boolean hasFixedDescendent = false;

		final Set<GeoElement> tree = getAllChildren();
		final Iterator<GeoElement> it = tree.iterator();
		while (it.hasNext() && (hasFixedDescendent == false)) {
			if (it.next().isFixed()) {
				hasFixedDescendent = true;
			}
		}

		if (hasFixedDescendent) {
			// Application.debug("hasFixedDescendent, not deleting");
			setUndefined();
			updateRepaint();
		} else {
			remove();
		}

	}

	final public boolean isAuxiliaryObject() {
		return auxiliaryObject;
	}

	public boolean isAuxiliaryObjectByDefault() {
		return false;
	}

	public GeoElement toGeoElement() {
		return this;
	}

	public void setAuxiliaryObject(final boolean flag) {
		if (auxiliaryObject != flag) {
			auxiliaryObject = flag;
			if (labelSet) {
				notifyUpdateAuxiliaryObject();
			}
		}
	}

	/**
	 * sets whether the object's label should be drawn in an EuclidianView
	 * 
	 * @param visible
	 */
	public void setLabelVisible(final boolean visible) {
		labelVisible = visible;
	}

	/**
	 * Returns whether the label should be shown in Euclidian view.
	 * 
	 * @return true if label should be shown
	 */
	public boolean isLabelVisible() {
		return labelVisible && isLabelSet();
	}

	/**
	 * Returns whether the label can be shown in Euclidian view.
	 * 
	 * @return true if label can be shown
	 */
	final public boolean isLabelShowable() {
		return isDrawable()
				&& !(isTextValue() || isGeoImage() || isGeoList() || (isGeoBoolean() && !isIndependent()));
	}

	/**
	 * Returns whether the value (e.g. equation) should be shown as part of the
	 * label description false for eg GeoLocus, Boolean, Button, TextField
	 * 
	 * @return true if value should be in description
	 */
	public boolean isLabelValueShowable() {
		return true;
	}

	/**
	 * @return whether object should be printed in algebra view
	 */
	final public boolean isAlgebraVisible() {
		return algebraVisible && showInAlgebraView();
	}

	public boolean showToolTipText() {
		// return isAlgebraVisible();
		switch (tooltipMode) {
		default:
			// case TOOLTIP_ALGEBRAVIEW_SHOWING:
			if (!(app.isUsingFullGui() && app
					.showView(AbstractApplication.VIEW_ALGEBRA))) {
				return false;
			}
			return isAlgebraVisible(); // old behaviour
		case TOOLTIP_OFF:
			return false;
		case TOOLTIP_ON:
		case TOOLTIP_CAPTION:
		case TOOLTIP_NEXTCELL:
			return true;
		}

	}

	public String getTooltipText(final boolean colored, final boolean alwaysOn) {
		// sbToolTipDesc.append(geo.getLongDescriptionHTML(colored, false));
		StringTemplate tpl = StringTemplate.defaultTemplate;
		switch (tooltipMode) {
		default:
		case TOOLTIP_ALGEBRAVIEW_SHOWING:
			if (!alwaysOn) {
				if (!(app.isUsingFullGui() && app
						.showView(AbstractApplication.VIEW_ALGEBRA))) {
					return "";
				}
			}
			// else fall through:
		case TOOLTIP_ON:

			app.setTooltipFlag();
			final String ret = getLongDescriptionHTML(colored, false); // old
																		// behaviour
			app.clearTooltipFlag();

			return ret;
		case TOOLTIP_OFF:
			return "";
		case TOOLTIP_CAPTION:
			return getCaption(tpl);
		case TOOLTIP_NEXTCELL: // tooltip is the next cell to the right
								// (spreadsheet objects only)
			String cellLabel = getLabel(tpl);
			final Point coords = geoElementSpreadsheet
					.dogetSpreadsheetCoordsForLabel(cellLabel);
			if (coords == null) {
				return "";
			}
			coords.x++;
			cellLabel = geoElementSpreadsheet.dogetSpreadsheetCellName(coords.x,
					coords.y);
			if (cellLabel == null) {
				return "";
			}
			final GeoElement geo = kernel.lookupLabel(cellLabel);
			return (geo == null) ? "" : geo.toValueString(tpl);
		}

	}

	public int getTooltipMode() {
		return tooltipMode;
	}

	public void setTooltipMode(final int mode) {
		// return isAlgebraVisible();
		switch (mode) {
		default:
			tooltipMode = TOOLTIP_ALGEBRAVIEW_SHOWING;
			break;
		case TOOLTIP_OFF:
		case TOOLTIP_ON:
		case TOOLTIP_CAPTION:
		case TOOLTIP_NEXTCELL:
			tooltipMode = mode;
			break;
		}

	}

	public void setAlgebraVisible(final boolean visible) {
		algebraVisible = visible;
	}

	public boolean isSetAlgebraVisible() {
		return algebraVisible;
	}

	public abstract boolean showInAlgebraView();

	protected abstract boolean showInEuclidianView();

	public boolean isAlgebraViewEditable() {
		return true;
	}

	final public boolean isEuclidianShowable() {
		return showInEuclidianView();
	}

	public boolean isAlgebraShowable() {
		return showInAlgebraView();
	}

	public void setParentAlgorithm(final AlgoElement algorithm) {
		algoParent = algorithm;
	}

	final public AlgoElement getParentAlgorithm() {
		return algoParent;
	}

	public void setDrawAlgorithm(final AlgoDrawInformation algorithm) {
		if (algorithm != null) {
			algoDraw = (AlgoElement) algorithm;
		}
	}

	final public AlgoElement getDrawAlgorithm() {
		if (algoDraw == null) {
			return algoParent;
		}
		return algoDraw;
	}

	final public ArrayList<AlgoElement> getAlgorithmList() {
		if (algorithmList == null) {
			algorithmList = new ArrayList<AlgoElement>();
		}
		return algorithmList;
	}

	@Override
	public boolean isIndependent() {
		return (algoParent == null);
	}

	/**
	 * Returns whether this GeoElement can be changed directly. Note: for points
	 * on lines this is different than isIndependent()
	 * 
	 * @return whether this geo can be changed directly
	 */
	public boolean isChangeable() {
		return !fixed && isIndependent();
	}

	/**
	 * Returns whether this GeoElement is a point on a path.
	 * 
	 * @return true for points on path
	 */
	public boolean isPointOnPath() {
		return false;
	}

	/**
	 * Returns whether this object may be redefined
	 * 
	 * @return whether this object may be redefined
	 */
	public boolean isRedefineable() {
		return !fixed && app.letRedefine() && !(isTextValue() || isGeoImage())
				&& (isChangeable() || // redefine changeable (independent and
										// not fixed)
				!isIndependent()); // redefine dependent object
	}

	/**
	 * Returns whether this GeoElement can be moved in Euclidian View. Note:
	 * this is needed for texts and points on path
	 * 
	 * @return true for moveable objects
	 */
	public boolean isMoveable() {
		return isChangeable();
	}

	/**
	 * 
	 * @param view
	 * @return true if moveable in the view
	 */
	public boolean isMoveable(final EuclidianViewInterfaceSlim view) {
		return view.isMoveable(this);
	}

	/**
	 * Returns whether this (dependent) GeoElement has input points that can be
	 * moved in Euclidian View.
	 * 
	 * @param view
	 * @return whether this geo has only moveable input points
	 */
	public boolean hasMoveableInputPoints(final EuclidianViewInterfaceSlim view) {
		// allow only moving of certain object types
		switch (getGeoClassType()) {
		case CONIC:

			// special case for Circle[A, r]
			if (getParentAlgorithm() instanceof AlgoCirclePointRadiusInterface) {
				return containsOnlyMoveableGeos(getFreeInputPoints(view));
			}

			// fall through

		case CONICPART:
		case IMAGE:
		case LINE:
		case LINEAR_INEQUALITY:
		case RAY:
		case SEGMENT:
		case TEXT:
			return hasOnlyFreeInputPoints(view)
					&& containsOnlyMoveableGeos(getFreeInputPoints(view));

		case POLYGON:
		case POLYLINE:
			return containsOnlyMoveableGeos(getFreeInputPoints(view));

		case VECTOR:
			if (hasOnlyFreeInputPoints(view)
					&& containsOnlyMoveableGeos(getFreeInputPoints(view))) {
				// check if first free input point is start point of vector
				final ArrayList<GeoPoint2> freeInputPoints = getFreeInputPoints(view);
				if (freeInputPoints.size() > 0) {
					final GeoPointND firstInputPoint = freeInputPoints.get(0);
					final GeoPointND startPoint = ((Locateable) this)
							.getStartPoint();
					return (firstInputPoint == startPoint);
				}
			}
			break;
		}

		return false;
	}

	/**
	 * Returns all free parent points of this GeoElement.
	 * 
	 * @param view
	 * @return all free parent points of this GeoElement.
	 */
	public ArrayList<GeoPoint2> getFreeInputPoints(
			final EuclidianViewInterfaceSlim view) {
		if (algoParent == null) {
			return null;
		}
		return view.getFreeInputPoints(algoParent);
	}

	final public boolean hasOnlyFreeInputPoints(
			final EuclidianViewInterfaceSlim view) {
		if (algoParent == null) {
			return false;
		}
		// special case for edge of polygon
		if ((algoParent instanceof AlgoJoinPointsSegmentInterface)
				&& (view.getFreeInputPoints(algoParent).size() == 2)) {
			return true;
		}

		return view.getFreeInputPoints(algoParent).size() == algoParent.input.length;
	}

	private static boolean containsOnlyMoveableGeos(
			final ArrayList<GeoPoint2> geos) {
		if ((geos == null) || (geos.size() == 0)) {
			return false;
		}

		for (int i = 0; i < geos.size(); i++) {
			final GeoElement geo = geos.get(i);
			if (!geo.isMoveable()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns whether this object's class implements the interface
	 * Translateable.
	 * 
	 * @return whether this object's class implements the interface
	 *         Translateable.
	 */
	public boolean isTranslateable() {
		return false;
	}

	/**
	 * Returns whether this GeoElement can be rotated in Euclidian View. Note:
	 * this is needed for images
	 * 
	 * @return whether this geo can be rotated
	 */
	public boolean isRotateMoveable() {
		return isChangeable() && (this instanceof PointRotateable);
	}

	/**
	 * Returns whether this GeoElement has properties that can be edited in a
	 * properties dialog.
	 * 
	 * @return whether this element has editable properties
	 */
	public boolean hasProperties() {
		// return isDrawable() || isChangeable();
		return true;
	}

	public void setAnimationStep(final double s) {
		setAnimationStep(new MyDouble(kernel, s));
	}

	public void setAnimationStep(final NumberValue v) {
		animationIncrement = v;
	}

	public double getAnimationStep() {
		if (animationIncrement == null) {
			animationIncrement = new MyDouble(kernel,
					GeoNumeric.DEFAULT_SLIDER_INCREMENT);
		}
		return animationIncrement.getDouble();
	}

	public GeoElement getAnimationStepObject() {
		if (animationIncrement == null) {
			return null;
		}
		return animationIncrement.toGeoElement();
	}

	public GeoElement getAnimationSpeedObject() {
		if (animationSpeedObj == null) {
			return null;
		}
		return animationSpeedObj.toGeoElement();
	}

	/**
	 * Returns the current animation speed of this slider. Note that the speed
	 * can be negative which will change the direction of the animation.
	 * 
	 * @return current animation speed
	 */
	public double getAnimationSpeed() {
		if (animationSpeedObj == null) {
			initAnimationSpeedObject();
		}

		// get speed
		double speed = animationSpeedObj.getDouble();
		if (Double.isNaN(speed)) {
			speed = 0;
		} else if (speed > MAX_ANIMATION_SPEED) {
			speed = MAX_ANIMATION_SPEED;
		} else if (speed < -MAX_ANIMATION_SPEED) {
			speed = -MAX_ANIMATION_SPEED;
		}

		return speed;
	}

	public void setAnimationSpeedObject(final NumberValue speed) {
		animationSpeedObj = speed;
	}

	public void setAnimationSpeed(final double speed) {
		initAnimationSpeedObject();

		final GeoElement speedObj = animationSpeedObj.toGeoElement();
		if (speedObj.isGeoNumeric() && speedObj.isIndependent()) {
			((GeoNumeric) speedObj).setValue(speed);
		}
	}

	private void initAnimationSpeedObject() {
		if (animationSpeedObj == null) {
			final GeoNumeric num = new GeoNumeric(cons);
			num.setValue(1);
			animationSpeedObj = num;
		}
	}

	final public int getAnimationType() {
		return animationType;
	}

	final public void setAnimationType(final int type) {
		switch (type) {
		case ANIMATION_INCREASING_ONCE:
		case ANIMATION_INCREASING:
		case ANIMATION_OSCILLATING:
			animationType = type;
			animationDirection = 1;
			break;

		case ANIMATION_DECREASING:
			animationType = type;
			animationDirection = -1;
			break;
		}
	}

	protected int getAnimationDirection() {
		return animationDirection;
	}

	protected void changeAnimationDirection() {
		animationDirection = -animationDirection;
	}

	/**
	 * Sets the state of this object to animating on or off.
	 * 
	 * @param flag
	 * 
	 * @see Animatable interface
	 */
	public synchronized void setAnimating(final boolean flag) {
		final boolean oldValue = animating;
		animating = flag && isAnimatable();

		// tell animation manager
		if (oldValue != animating) {
			final AbstractAnimationManager am = kernel.getAnimatonManager();
			if (animating) {
				am.addAnimatedGeo(this);
			} else {
				am.removeAnimatedGeo(this);
			}
		}
	}

	final public boolean isAnimating() {
		return animating;
	}

	public boolean isAnimatable() {
		// over ridden by types that implement Animateable
		return false;
	}

	public String toLaTeXString(final boolean symbolic,StringTemplate tpl) {
		return getFormulaString(tpl, !symbolic);
		// if (symbolic)
		// return toString();
		// else
		// return toDefinedValueString();
	}

	/**
	 * Returns a String that can be used to define geo in the currently used
	 * CAS. For example, "f(x) := a*x^2", "a := 20", "g := 3x + 4y = 7" in
	 * MathPiper or "f(x) := a*x^2", "a:20", "g: 3x + 4y == 7" in Maxima
	 * 
	 * @param type
	 *            StringType.MAXIMA, STRING_TYPE_MATHPIPER
	 * @return String in the format of the current CAS.
	 */
	public String toCasAssignment(final StringTemplate tpl) {
		if (!labelSet) {
			return null;
		}
		
		String retval;

		try {
			if (tpl.hasType(StringType.GEOGEBRA)) {
				final String body = toValueString(tpl);
				retval = getAssignmentLHS(tpl) + " := " + body;
			} else {

				final CASGenericInterface cas = kernel.getGeoGebraCAS()
						.getCurrentCAS();
				retval = cas.toAssignment(this,tpl);
			}
		} finally {
			// do nothing
		}

		return retval;
	}

	public String getAssignmentLHS(StringTemplate tpl) {
		return getLabel(tpl);
	}

	/**
	 * Returns a representation of geo in currently used CAS syntax. For
	 * example, "a*x^2"
	 * 
	 * @param symbolic
	 * @return representation of this geo for CAS
	 */
	public String getCASString(StringTemplate tpl,final boolean symbolic) {
		return symbolic && !isIndependent() ? getCommandDescription(tpl)
				: toValueString(tpl);
	}

	/* *******************************************************
	 * GeoElementTable Management Hashtable: String (label) -> GeoElement
	 * ******************************************************
	 */

	public void addCellRangeUser() {
		++cellRangeUsers;
	}

	public void removeCellRangeUser() {
		if (cellRangeUsers > 0) {
			--cellRangeUsers;
		}
	}

	public boolean isRenameable() {
		// don't allow renaming when this object is used in
		// cell ranges, see AlgoCellRange
		return cellRangeUsers == 0;
	}

	/**
	 * Tells this GeoElement that one more CAS algorithm is using it as input.
	 */
	public void addCasAlgoUser() {
		++casAlgoUsers;
	}

	/**
	 * Tells this GeoElement that one CAS algorithm that had been using it as
	 * input has been removed. If there are no more using algorithms we call
	 * unbindVariableInCAS().
	 */
	public void removeCasAlgoUser() {
		if (casAlgoUsers > 1) {
			--casAlgoUsers;
		} else {
			unbindVariableInCAS();
			casAlgoUsers = 0;
		}
	}

	/**
	 * Removes label from underlying CAS.
	 */
	public void unbindVariableInCAS() {
		if (isSendingUpdatesToCAS() && isLabelSet()) {
			kernel.unbindVariableInGeoGebraCAS(label);
		}
	}

	public boolean isSendingUpdatesToCAS() {
		return casAlgoUsers > 0;
	}

	/**
	 * renames this GeoElement to newLabel.
	 * 
	 * @param newLabel
	 * @return true if label was changed
	 * @throws MyError
	 *             : if new label is already in use
	 */
	public boolean rename(String labelNew) {
		String newLabel = labelNew;
		if (!isRenameable()) {
			return false;
		}

		if (newLabel == null) {
			return false;
		}
		newLabel = newLabel.trim();
		if (newLabel.length() == 0) {
			return false;
		}
		final String labelOld = label;

		if (newLabel.equals(labelOld)) {
			return false;
		} else if (cons.isFreeLabel(newLabel)) {
			setLabel(newLabel); // now we rename
			return true;
		} else {
			final String str[] = { "NameUsed", newLabel };
			throw new MyError(app, str);
		}
	}

	/**
	 * Returns whether this object's label has been set and is valid now. (this
	 * is needed for saving: only object's with isLabelSet() == true should be
	 * saved)
	 * 
	 * @return true if this geo has valid label
	 */
	public boolean isLabelSet() {
		return labelSet;
	}

	/**
	 * Sets label of a GeoElement and updates Construction list and GeoElement
	 * tabel (String label, GeoElement geo) in Kernel. If the old label was
	 * null, a new free label is assigned starting with label as a prefix. If
	 * newLabel is not already used, this object is renamed to newLabel.
	 * Otherwise nothing is done.
	 * 
	 * @param newLabel
	 */
	public void setLabel(String labelNew) {
		String newLabel = labelNew;
		// Application.printStacktrace(newLabel);

		if (cons.isSuppressLabelsActive()) {
			return;
		}

		// don't want any '$'s in actual labels
		if ((newLabel != null) && (newLabel.indexOf('$') > -1)) {
			newLabel = newLabel.replaceAll("\\$", "");
		}

		labelWanted = true;

		// had no label: try to set it
		if (!labelSet) {
			// to avoid wasting of labels, new elements must wait
			// until they are shown in one of the views to get a label
			if (isVisible()) {
				// newLabel is used already: rename the using geo
				final GeoElement geo = kernel.lookupLabel(newLabel);
				if (geo != null) {
					geo.doRenameLabel(getFreeLabel(newLabel));
				}

				// set newLabel for this geo
				doSetLabel(getFreeLabel(newLabel));
			} else {
				// remember desired label
				label = newLabel;
			}
		}
		// try to rename
		else if (isRenameable()) {
			if (cons.isFreeLabel(newLabel)) { // rename
				doRenameLabel(newLabel);
			} else {

				// removed: null pointer exception on Intersect[a,b]
				// System.out.println("setLabel DID NOT RENAME: " + this.label +
				// " to " + newLabel + ", new label is not free: " +
				// cons.lookupLabel(newLabel).getLongDescription());
			}
		}
	}

	// private StringBuilder sb;
	//
	// private String removeDollars(String s) {
	// if (sb == null)
	// sb = new StringBuilder();
	// sb.setLength(0);
	//
	// for (int i = 0; i < s.length(); i++) {
	// char c = s.charAt(i);
	// if (c != '$')
	// sb.append(c);
	// }
	//
	// return sb.toString();
	// }

	/**
	 * Sets label of a GeoElement and updates GeoElement table (label,
	 * GeoElement). This method should only be used by MyXMLHandler.
	 * 
	 * @param label
	 */
	public void setLoadedLabel(final String label) {
		if (labelSet) { // label was set before -> rename
			doRenameLabel(label);
		} else { // no label set so far -> set new label
			doSetLabel(getFreeLabel(label));
		}
	}

	public boolean setCaption(String caption1) {
		String caption2 = caption1;
		if ((caption2 == null) || caption2.equals(label)) {
			this.caption = null;
			return false;
		}

		caption2 = caption2.trim();

		if (caption2.trim().length() == 0) {
			this.caption = null;
			return true;
		}

		this.caption = caption2.trim();
		return true;
	}

	StringBuilder captionSB = null;

	public String getCaptionNoReplace() {
		return caption;
	}
	
	public String getCaption(StringTemplate tpl) {
		if (caption == null) {
			return getLabel(tpl);
		}

		// for speed, check first for a %
		if (caption.indexOf('%') < 0) {
			return caption;
		}

		if (captionSB == null) {
			captionSB = new StringBuilder();
		} else {
			captionSB.setLength(0);
		}

		// replace %v with value and %n with name
		for (int i = 0; i < caption.length(); i++) {
			char ch = caption.charAt(i);
			if ((ch == '%') && (i < (caption.length() - 1))) {
				// get number after %
				i++;
				ch = caption.charAt(i);
				switch (ch) {
				case 'v':
					captionSB.append(toValueString(tpl));
					break;
				case 'n':
					captionSB.append(getLabel(tpl));
					break;
				case 'x':
					if (isGeoPoint()) {
						captionSB.append(kernel.format(((GeoPointND) this)
								.getInhomCoords().getX(),tpl));
					} else if (isGeoLine()) {
						captionSB
								.append(kernel.format(((GeoLine) this).getX(),tpl));
					} else {
						captionSB.append("%x");
					}

					break;
				case 'y':
					if (isGeoPoint()) {
						captionSB.append(kernel.format(((GeoPointND) this)
								.getInhomCoords().getY(),tpl));
					} else if (isGeoLine()) {
						captionSB
								.append(kernel.format(((GeoLine) this).getY(),tpl));
					} else {
						captionSB.append("%y");
					}
					break;
				case 'z':
					if (isGeoPoint()) {
						captionSB.append(kernel.format(((GeoPointND) this)
								.getInhomCoords().getZ(),tpl));
					} else if (isGeoLine()) {
						captionSB
								.append(kernel.format(((GeoLine) this).getZ(),tpl));
					} else {
						captionSB.append("%z");
					}
					break;
				default:
					captionSB.append('%');
					captionSB.append(ch);
				}
			} else {
				captionSB.append(ch);
			}
		}

		return app.translationFix(captionSB.toString());
	}

	public String getRawCaption() {
		if (caption == null) {
			return "";
		}
		return caption;
	}

	public String getCaptionDescription(StringTemplate tpl) {
		if (caption == null) {
			return "";
		}
		return getCaption(tpl);
	}

	/**
	 * Sets label of a local variable object. This method should only be used by
	 * Construction.
	 * 
	 * @param label
	 *            local variable name
	 */
	public void setLocalVariableLabel(final String label) {
		oldLabel = this.label;
		this.label = label;
		localVarLabelSet = true;
	}

	/**
	 * Sets label of a local variable object back to its previous label. This
	 * method should only be used by Construction.
	 */
	public void undoLocalVariableLabel() {
		if (oldLabel != null) {
			label = oldLabel;
			localVarLabelSet = false;
		}
	}

	public boolean isLocalVariable() {
		return localVarLabelSet;
	}

	private void doSetLabel(final String newLabel) {
		// UPDATE KERNEL
		if (!labelSet && isIndependent()) {
			// add independent object to list of all Construction Elements
			// dependent objects are represented by their parent algorithm
			cons.addToConstructionList(this, true);
		}

		/*
		 * if (!cons.isFreeLabel(label)) { try { throw new
		 * Exception("SET LABEL: label: " + label + ", type: " +
		 * this.getTypeString()); } catch (Exception e) { e.printStackTrace(); }
		 * } else { Application.debug("SET LABEL: " + label + ", type: " +
		 * this.getTypeString()); }
		 */

		this.label = newLabel; // set new label
		labelSet = true;
		labelWanted = false; // got a label, no longer wanted

		cons.putLabel(this); // add new table entry
		algebraStringsNeedUpdate();
		updateSpreadsheetCoordinates();

		notifyAdd();
	}

	private void updateSpreadsheetCoordinates() {
		if (labelSet && (label.length() > 0)
				&& Character.isLetter(label.charAt(0)) // starts with letter
				&& Character.isDigit(label.charAt(label.length() - 1))) // ends
																		// with
																		// digit
		{

			// init old and current spreadsheet coords
			if (spreadsheetCoords == null) {
				oldSpreadsheetCoords = null;
				spreadsheetCoords = new Point();
			} else {
				if (oldSpreadsheetCoords == null) {
					oldSpreadsheetCoords = new Point();
				}
				oldSpreadsheetCoords.setLocation(spreadsheetCoords);
			}

			// we need to also support wrapped GeoElements like
			// $A4 that are implemented as dependent geos (using ExpressionNode)
			final Point p = geoElementSpreadsheet
					.dospreadsheetIndices(getLabel());

			if ((p.x >= 0) && (p.y >= 0)) {
				spreadsheetCoords.setLocation(p.x, p.y);
			} else {
				spreadsheetCoords = null;
			}
		} else {
			oldSpreadsheetCoords = spreadsheetCoords;
			spreadsheetCoords = null;
		}

		// Application.debug("update spread sheet coords: " + this + ", " +
		// spreadsheetCoords + ", old: " + oldSpreadsheetCoords);
	}

	/**
	 * Returns the spreadsheet reference name of this GeoElement using $ signs
	 * for absolute spreadsheet reference names like A$1 or $A$1.
	 * 
	 * @param col$
	 * @param row$
	 * @return spreadsheet reference name of this GeoElement with $ signs
	 */
	public String getSpreadsheetLabelWithDollars(final boolean col$,
			final boolean row$) {
		final String colName = geoElementSpreadsheet
				.dogetSpreadsheetColumnName(spreadsheetCoords.x);
		final String rowName = Integer.toString(spreadsheetCoords.y + 1);

		final StringBuilder sb = new StringBuilder(label.length() + 2);
		if (col$) {
			sb.append('$');
		}
		sb.append(colName);
		if (row$) {
			sb.append('$');
		}
		sb.append(rowName);
		return sb.toString();
	}

	/*
	 * compares labels alphabetically, but spreadsheet labels are sorted nicely
	 * eg A1, A2, A10 not A1, A10, A2
	 */
	final public static int compareLabels(final String label1,
			final String label2,
			final GeoElementSpreadsheet geoElementSpreadsheet) {

		if (geoElementSpreadsheet.doisSpreadsheetLabel(label1)
				&& geoElementSpreadsheet.doisSpreadsheetLabel(label2)) {
			final Point p1 = geoElementSpreadsheet
					.dogetSpreadsheetCoordsForLabel(label1);
			final Point p2 = geoElementSpreadsheet
					.dogetSpreadsheetCoordsForLabel(label2);
			// Application.debug(label1+" "+p1.x+" "+p1.y+" "+label2+" "+p2.x+" "+p2.y);
			if (p1.x != p2.x) {
				return p1.x - p2.x;
			}
			return p1.y - p2.y;
		}

		return label1.compareTo(label2);

	}

	public static final int MAX_LINE_WIDTH = 13;

	private void doRenameLabel(final String newLabel) {
		if ((newLabel == null) || newLabel.equals(label)) {
			return;
		}

		/*
		 * if (!cons.isFreeLabel(newLabel)) { try { throw new
		 * Exception("RENAME ERROR: old: " + label + ", new: " + newLabel +
		 * ", type: " + this.getTypeString()); } catch (Exception e) {
		 * e.printStackTrace(); } } else { Application.debug("RENAME: old: " +
		 * label + ", new: " + newLabel + ", type: " + this.getTypeString()); }
		 */

		// UPDATE KERNEL
		cons.removeLabel(this); // remove old table entry
		oldLabel = label; // remember old label (for applet to javascript
							// rename)
		label = newLabel; // set new label
		cons.putLabel(this); // add new table entry

		algebraStringsNeedUpdate();
		updateSpreadsheetCoordinates();

		kernel.notifyRename(this); // tell views
		updateCascade();
	}

	/**
	 * Returns the label of this object before rename() was called.
	 * 
	 * @return label before renaming
	 */
	final public String getOldLabel() {
		return oldLabel;
	}

	/**
	 * set labels for array of GeoElements with given label prefix. e.g.
	 * labelPrefix = "F", geos.length = 2 sets geo[0].setLabel("F_1") and
	 * geo[1].setLabel("F_2") all members in geos are assumed to be initialized.
	 * 
	 * @param labelPrefix
	 * @param geos
	 */
	public static void setLabels(final String labelPrefix,
			final GeoElement[] geos,
			final GeoElementSpreadsheet geoElementSpreadsheet) {
		if (geos == null) {
			return;
		}

		int visible = 0;
		int firstVisible = 0;
		for (int i = geos.length - 1; i >= 0; i--) {
			if (geos[i].isVisible()) {
				firstVisible = i;
				visible++;
			}
		}

		switch (visible) {
		case 0: // no visible geos: they all get the labelPrefix as suggestion
			for (int i = 0; i < geos.length; i++) {
				geos[i].setLabel(labelPrefix);
			}
			break;

		case 1: // if there is only one visible geo, don't use indices
			geos[firstVisible].setLabel(labelPrefix);
			break;

		default:
			// is this a spreadsheet label?
			final Point p = geoElementSpreadsheet
					.dospreadsheetIndices(labelPrefix);
			if ((p.x >= 0) && (p.y >= 0)) {
				// more than one visible geo and it's a spreadsheet cell
				// use D1, E1, F1, etc as names
				final int col = p.x;
				final int row = p.y;
				for (int i = 0; i < geos.length; i++) {
					geos[i].setLabel(geos[i].getFreeLabel(geoElementSpreadsheet
							.dogetSpreadsheetCellName(col + i, row)));
				}
			} else { // more than one visible geo: use indices if we got a
						// prefix
				for (int i = 0; i < geos.length; i++) {
					geos[i].setLabel(geos[i].getIndexLabel(labelPrefix));
				}
			}
		}
	}

	/**
	 * set labels for array of GeoElements pairwise: geos[i].setLabel(labels[i])
	 * 
	 * @param labels
	 *            array of labels
	 * @param geos
	 *            array of geos
	 */
	public static void setLabels(final String[] labels,
			final GeoElement[] geos,
			final GeoElementSpreadsheet geoElementSpreadsheet) {
		setLabels(labels, geos, false, geoElementSpreadsheet);
	}

	static void setLabels(final String[] labels, final GeoElement[] geos,
			final boolean indexedOnly,
			final GeoElementSpreadsheet geoElementSpreadsheet) {
		final int labelLen = (labels == null) ? 0 : labels.length;

		if ((labelLen == 1) && (labels[0] != null) && !labels[0].equals("")) {
			setLabels(labels[0], geos, geoElementSpreadsheet);
			return;
		}

		String label;
		for (int i = 0; i < geos.length; i++) {
			if (i < labelLen) {
				label = labels[i];
			} else {
				label = null;
			}

			if (indexedOnly) {
				label = geos[i].getIndexLabel(label);
			}

			geos[i].setLabel(label);
		}
	}

	/**
	 * Get a free label. Try the suggestedLabel first
	 * 
	 * @param suggestedLabel
	 * @return free label -- either suggestedLabel or suggestedLabel_index
	 */
	public String getFreeLabel(final String suggestedLabel) {
		if (suggestedLabel != null) {
			if ("x".equals(suggestedLabel) || "y".equals(suggestedLabel)) {
				return getDefaultLabel(false);
			}

			if (cons.isFreeLabel(suggestedLabel)) {
				return suggestedLabel;
			} else if (suggestedLabel.length() > 0) {
				return getIndexLabel(suggestedLabel);
			}
		}

		// standard case: get default label
		return getDefaultLabel(false);
	}

	public String getDefaultLabel(final boolean isInteger) {
		return getDefaultLabel(null, isInteger);
	}

	public String getDefaultLabel() {
		return getDefaultLabel(null, false);
	}

	protected String getDefaultLabel(char[] chars2, final boolean isInteger) {
		char [] chars = chars2;
		if (chars == null) {
			if (isGeoPoint()) {
				// Michael Borcherds 2008-02-23
				// use Greek upper case for labeling points if lenguage is Greek
				// (el)
				if (app.isUsingLocalizedLabels()) {
					if (app.languageIs("el")) {
						chars = greekUpperCase;
					} else if (app.languageIs("ar")) {
						chars = arabic;
					} else {
						chars = pointLabels;
					}
				} else {
					chars = pointLabels;
				}

				final GeoPointND point = (GeoPointND) this;
				if (point.getMode() == Kernel.COORD_COMPLEX) {
					chars = complexLabels;
				}

			} else if (isGeoFunction()) {
				chars = functionLabels;
			} else if (isGeoLine()) {
				if (((GeoLineND) this).isFromPolyhedron()) {
					int counter = 0;
					String str;
					final String name = app.getPlain("Name.edge");
					do {
						counter++;
						str = name
								+ kernel.internationalizeDigits(counter + "",StringTemplate.defaultTemplate);
					} while (!cons.isFreeLabel(str));
					return str;
				}
				chars = lineLabels;
			} else if (isGeoPolyLine()) {
				chars = lineLabels;
			} else if (isGeoConic() || isGeoCubic()) {
				chars = conicLabels;
			} else if (isGeoVector() || isVector3DValue()) {
				chars = vectorLabels;
			} else if (isGeoAngle()) {
				chars = greekLowerCase;
			} else if (isGeoText()) {
				return defaultNumberedLabel("Name.text");
			} else if (isGeoImage()) {
				return defaultNumberedLabel("Name.picture");
			} else if (isGeoLocus()) {
				return defaultNumberedLabel("Name.locus");
			} else if (isGeoTextField()) {
				return defaultNumberedLabel("Name.textfield");
			} else if (isGeoButton()) {
				return defaultNumberedLabel("Name.button");
			} else if (isGeoList()) {
				final GeoList list = (GeoList) this;
				return defaultNumberedLabel(list.isMatrix() ? "Name.matrix"
						: "Name.list");
			} else if (isInteger && isGeoNumeric()) {
				chars = integerLabels;
			} else {
				chars = lowerCaseLabels;
			}
		}

		int counter = 0, q, r;
		final StringBuilder sbDefaultLabel = new StringBuilder();
		sbDefaultLabel.append(chars[0]);
		while (!cons.isFreeLabel(sbDefaultLabel.toString())) {
			sbDefaultLabel.setLength(0);
			q = counter / chars.length; // quotient
			r = counter % chars.length; // remainder

			final char ch = chars[r];
			sbDefaultLabel.append(ch);

			// this arabic letter is two unicode chars
			if (ch == '\u0647') {
				sbDefaultLabel.append('\u0640');
			}

			if (q > 0) {
				// don't use indices
				// sbDefaultLabel.append(q);

				// q as index
				if (q < 10) {
					sbDefaultLabel.append('_');
					sbDefaultLabel.append(q);
				} else {
					sbDefaultLabel.append("_{");
					sbDefaultLabel.append(q);
					sbDefaultLabel.append('}');
				}

			}
			counter++;
		}
		return sbDefaultLabel.toString();
	}

	private String defaultNumberedLabel(final String plainKey) {
		int counter = 0;
		String str;
		do {
			counter++;
			str = app.getPlain(plainKey)
					+ kernel.internationalizeDigits(counter + "",StringTemplate.defaultTemplate);
		} while (!cons.isFreeLabel(str));
		return str;
	}

	/**
	 * Returns the next free indexed label using the given prefix.
	 * 
	 * @param prefix
	 *            e.g. "c"
	 * @return indexed label, e.g. "c_2"
	 */
	public String getIndexLabel(final String prefix) {
		if (prefix == null) {
			return getFreeLabel(null) + "_1";
		}
		return cons.getIndexLabel(prefix);
	}

	public boolean isGeoTextField() {
		return false;
	}

	/**
	 * Removes this object and all dependent objects from the Kernel. If this
	 * object is not independent, it's parent algorithm is removed too.
	 */
	@Override
	public void remove() {
		// dependent object: remove parent algorithm
		if (algoParent != null) {
			algoParent.remove(this);
		} else {
			doRemove();
			if (correspondingCasCell != null) {
				correspondingCasCell.doRemove();
			}
		}
	}

	// removes this GeoElement and all its dependents
	public void doRemove() {
		// stop animation of this geo
		setAnimating(false);

		// remove this object from List
		if (isIndependent()) {
			cons.removeFromConstructionList(this);
		}

		// remove Listeners
		AlgoElement algo = getParentAlgorithm();
		if (algo != null) {
			cons.unregisterEuclidianViewCE(algo);
		}

		if (condShowObject != null) {
			condShowObject.unregisterConditionListener(this);
		}

		if (colFunction != null) {
			colFunction.unregisterColorFunctionListener(this);
		}

		// remove all dependent algorithms
		if (algorithmList != null) {
			final Object[] algos = algorithmList.toArray();
			for (int i = 0; i < algos.length; i++) {
				algo = (AlgoElement) algos[i];
				algo.remove(this);
			}
		}

		// remove this object from table
		if (isLabelSet()) {
			cons.removeLabel(this);
		}

		// remove from selection
		if (isSelected()) {
			app.removeSelectedGeo(this, false);
		}

		// notify views before we change labelSet
		notifyRemove();

		labelSet = false;
		labelWanted = false;
		correspondingCasCell = null;

		if (latexCache != null) {
			// remove old key from cache
			// JLaTeXMathCache.removeCachedTeXFormula(keyLaTeX);
			latexCache.remove();
		}

	}

	LaTeXCache latexCache = null;

	public LaTeXCache getLaTeXCache() {
		if (latexCache == null) {
			latexCache = LaTeXFactory.prototype.newLaTeXCache();
		}
		return latexCache;
	}

	@Override
	final public void notifyAdd() {
		kernel.notifyAdd(this);

		// Application.debug("add " + label);
		// printUpdateSets();
	}

	@Override
	final public void notifyRemove() {
		kernel.notifyRemove(this);

		// Application.debug("remove " + label);
		// printUpdateSets();
	}

	final public void notifyUpdate() {
		kernel.notifyUpdate(this);

		// Application.debug("update " + label);
		// printUpdateSets();
	}

	final public void notifyUpdateAuxiliaryObject() {
		kernel.notifyUpdateAuxiliaryObject(this);

		// Application.debug("add " + label);
		// printUpdateSets();
	}

	/*
	 * private void printUpdateSets() { Iterator itList =
	 * cons.getAllGeoElementsIterator(); while (itList.hasNext()) { GeoElement
	 * geo = (GeoElement) itList.next(); Application.debug(geo.label + ": " +
	 * geo.algoUpdateSet.toString()); } }
	 */

	/* *******************************************************
	 * AlgorithmList Management each GeoElement has a list of dependent
	 * algorithms******************************************************
	 */

	/**
	 * add algorithm to dependency list of this GeoElement
	 * 
	 * @param algorithm
	 */
	public final void addAlgorithm(final AlgoElement algorithm) {
		if (!(getAlgorithmList().contains(algorithm))) {
			algorithmList.add(algorithm);
		}
		addToUpdateSets(algorithm);
	}

	/**
	 * Adds the given algorithm to the dependency list of this GeoElement. The
	 * algorithm is NOT added to the updateSet of this GeoElement! I.e. when
	 * updateCascade() is called the given algorithm will not be updated.
	 * 
	 * @param algorithm
	 */
	public final void addToAlgorithmListOnly(final AlgoElement algorithm) {
		if (!getAlgorithmList().contains(algorithm)) {
			algorithmList.add(algorithm);
		}
	}

	/**
	 * Adds the given algorithm to the update set this GeoElement. Note: the
	 * algorithm is NOT added to the algorithm list, i.e. the dependency graph
	 * of the construction.
	 * 
	 * @param algorithm
	 */
	public final void addToUpdateSetOnly(final AlgoElement algorithm) {
		addToUpdateSets(algorithm);
	}

	/**
	 * remove algorithm from dependency list of this GeoElement
	 * 
	 * @param algorithm
	 */
	public final void removeAlgorithm(final AlgoElementInterface algorithm) {
		algorithmList.remove(algorithm);
		removeFromUpdateSets((AlgoElement) algorithm);
	}

	// /**
	// * Removes all algorithms from algoUpdateSet that cannot be reached
	// * through the algorithmList - input - output graph. This method should
	// * be called when an algorithm removes its input but keeps its output,
	// * see AlgoDependentCasCell.removeInputButKeepOutput().
	// */
	// final public void removeUnreachableAlgorithmsFromUpdateSet() {
	// if (algorithmList == null || algorithmList.isEmpty()) return;
	//
	// // create set of reachable algorithms
	// HashSet<AlgoElement> reachableAlgos = new HashSet<AlgoElement>();
	// addReachableAlgorithms(algorithmList, reachableAlgos);
	//
	// // remove algorithms from updateSet that are not reachable
	// Iterator<AlgoElement> it = algoUpdateSet.getIterator();
	// while (it.hasNext()) {
	// AlgoElement updateAlgo = it.next();
	// if (!reachableAlgos.contains(updateAlgo)) {
	// it.remove();
	// }
	// }
	// }
	//
	// /**
	// * Adds all algorithms that can be reached through the output graph from
	// startList to
	// * reachableAlgorithmList
	// * @param startList
	// * @param reachableAlgorithmList
	// */
	// private void addReachableAlgorithms(ArrayList<AlgoElement> startList,
	// HashSet<AlgoElement> reachableAlgorithmList) {
	// if (startList != null && !startList.isEmpty()) {
	// reachableAlgorithmList.addAll(startList);
	// for (AlgoElement algo : startList) {
	// for (GeoElement output : algo.getOutput()) {
	// addReachableAlgorithms(output.algorithmList, reachableAlgorithmList);
	// }
	// }
	// }
	// }

	public AlgorithmSet getAlgoUpdateSet() {
		if (algoUpdateSet == null) {
			algoUpdateSet = new AlgorithmSet();
		}

		return algoUpdateSet;
	}

	/**
	 * add algorithm to update sets up the construction graph
	 */
	public void addToUpdateSets(final AlgoElement algorithm) {
		final boolean added = getAlgoUpdateSet().add(algorithm);

		if (added) {
			// propagate up the graph if we didn't do this before
			if (algoParent != null) {
				final GeoElement[] input = algoParent
						.getInputForUpdateSetPropagation();
				for (int i = 0; i < input.length; i++) {
					input[i].addToUpdateSets(algorithm);
				}
			}
		}
	}

	/**
	 * remove algorithm from update sets up the construction graph
	 * 
	 * @param algorithm
	 */
	public void removeFromUpdateSets(final AlgoElement algorithm) {
		final boolean removed = (algoUpdateSet != null)
				&& algoUpdateSet.remove(algorithm);

		if (removed) {
			// propagate up the graph
			if (algoParent != null) {
				final GeoElement[] input = algoParent
						.getInputForUpdateSetPropagation();
				for (int i = 0; i < input.length; i++) {
					input[i].removeFromUpdateSets(algorithm);
				}
			}
		}
	}

	/**
	 * updates this object and notifies kernel. Note: no dependent objects are
	 * updated.
	 * 
	 * @see #updateRepaint()
	 */
	@Override
	public void update() {

		updateGeo();

		kernel.notifyUpdate(this);
	}

	final private void updateGeo() {

		if (labelWanted && !labelSet) {
			// check if this object's label needs to be set
			if (isVisible()) {
				setLabel(label);
			}
		}

		if (correspondingCasCell != null) {
			correspondingCasCell.setInputFromTwinGeo();
		}

		// G.Sturr 2010-6-26
		if (getSpreadsheetTrace() && app.isUsingFullGui()) {
			app.traceToSpreadsheet(this);

		}
		// END G.Sturr

		// texts need updates
		algebraStringsNeedUpdate();

		// send update to underlying CAS if necessary
		sendValueToCAS();

	}

	/**
	 * Sends geo's value in the current CAS, e.g. a := 5;
	 * 
	 * @return whether an assignment was evaluated
	 */
	final public boolean sendValueToCAS() {
		if (!isSendingUpdatesToCAS() || !isCasEvaluableObject()
				|| !isLabelSet()) {
			return false;
		}

		try {
			final GeoGebraCasInterface cas = kernel.getGeoGebraCAS();
			final String geoStr = toCasAssignment(StringTemplate.get(cas.getCurrentCASstringType()));
			if (geoStr != null) {
				// TODO: remove
				System.out.println("sendValueToCAS: " + geoStr);
				cas.evaluateRaw(geoStr);
				return true;
			}
		} catch (final Throwable e) {
			System.err.println("GeoElement.sendValueToCAS: " + this + "\n\t"
					+ e.getMessage());
		}
		return false;
	}

	private void algebraStringsNeedUpdate() {
		strAlgebraDescriptionNeedsUpdate = true;
		strAlgebraDescTextOrHTMLneedsUpdate = true;
		strAlgebraDescriptionHTMLneedsUpdate = true;
		strLabelTextOrHTMLUpdate = true;
		strLaTeXneedsUpdate = true;
	}

	/**
	 * Updates this object and all dependent ones. Note: no repainting is done
	 * afterwards! synchronized for animation
	 */
	public void updateCascade() {
		update();
		updateDependentObjects();
	}

	final private void updateDependentObjects() {
		if ((correspondingCasCell != null) && isIndependent()) {
			updateAlgoUpdateSetWith(correspondingCasCell);
		} else if (algoUpdateSet != null) {
			// update all algorithms in the algorithm set of this GeoElement
			algoUpdateSet.updateAll();
		}
	}

	/**
	 * Updates algoUpdateSet and secondGeo.algoUpdateSet together efficiently.
	 */
	protected void updateAlgoUpdateSetWith(final GeoElement secondGeo) {
		if (algoUpdateSet == null) {
			if (secondGeo.algoUpdateSet == null) {
				// both null
				return;
			}
			// update second only
			secondGeo.algoUpdateSet.updateAll();
		} else {
			if (secondGeo.algoUpdateSet == null) {
				// update first only
				algoUpdateSet.updateAll();
			} else {
				// join both algoUpdateSets and update all algorithms
				final TreeSet<AlgoElementInterface> tempAlgoSet = getTempSet();
				tempAlgoSet.clear();
				algoUpdateSet.addAllToCollection(tempAlgoSet);
				secondGeo.algoUpdateSet.addAllToCollection(tempAlgoSet);
				for (final AlgoElementInterface algo : tempAlgoSet) {
					algo.update();
				}
			}
		}
	}

	/**
	 * If the flag updateCascadeAll is false, this algorithm updates all
	 * GeoElements in the given ArrayList and all algorithms that depend on free
	 * GeoElements in that list. If the flag updateCascadeAll is true, this
	 * algorithm updates all GeoElements in the given ArrayList and all
	 * algorithms that depend on any GeoElement in that list. This flag was
	 * introduced because of Ticket #1383, description of that change is there
	 * 
	 * Note: this method is more efficient than calling updateCascade() for all
	 * individual GeoElements.
	 * 
	 * @param geos
	 * 
	 * @param tempSet1
	 *            a temporary set that is used to collect all algorithms that
	 *            need to be updated
	 * 
	 * @param updateCascadeAll
	 */
	final static public synchronized void updateCascade(
			final ArrayList<?> geos,
			final TreeSet<AlgoElementInterface> tempSet1,
			final boolean updateCascadeAll) {
		// only one geo: call updateCascade()
		if (geos.size() == 1) {
			final ConstructionElement ce = (ConstructionElement) geos.get(0);
			if (ce.isGeoElement()) {
				((GeoElement) ce).updateCascade();
			}
			return;
		}

		// build update set of all algorithms in construction element order
		// clear temp set
		tempSet1.clear();

		final int size = geos.size();
		for (int i = 0; i < size; i++) {
			final ConstructionElement ce = (ConstructionElement) geos.get(i);
			if (ce.isGeoElement()) {
				final GeoElement geo = (GeoElement) geos.get(i);
				geo.update();

				if ((geo.isIndependent() || geo.isPointOnPath() || updateCascadeAll)
						&& (geo.algoUpdateSet != null)) {
					// add all dependent algos of geo to the overall algorithm
					// set
					geo.algoUpdateSet.addAllToCollection(tempSet1);
				}
			}
		}

		// now we have one nice algorithm set that we can update
		if (tempSet1.size() > 0) {
			final Iterator<AlgoElementInterface> it = tempSet1.iterator();
			while (it.hasNext()) {
				final AlgoElement algo = (AlgoElement) it.next();
				algo.update();
			}
		}
	}

	/**
	 * Updates all GeoElements in the given ArrayList and all algorithms that
	 * depend on free GeoElements in that list. Note: this method is more
	 * efficient than calling updateCascade() for all individual GeoElements.
	 * 
	 * @param geos
	 * 
	 * @param tempSet2
	 *            a temporary set that is used to collect all algorithms that
	 *            need to be updated
	 */
	final static public void updateCascadeUntil(final ArrayList<?> geos,
			final TreeSet<AlgoElementInterface> tempSet2,
			final AlgoElement lastAlgo) {
		// only one geo: call updateCascade()
		if (geos.size() == 1) {
			final ConstructionElement ce = (ConstructionElement) geos.get(0);
			if (ce.isGeoElement()) {
				((GeoElement) ce).updateCascade();
			}
			return;
		}

		// build update set of all algorithms in construction element order
		// clear temp set
		tempSet2.clear();

		final int size = geos.size();
		for (int i = 0; i < size; i++) {
			final ConstructionElement ce = (ConstructionElement) geos.get(i);
			if (ce.isGeoElement()) {
				final GeoElement geo = (GeoElement) geos.get(i);
				geo.update();

				if ((geo.isIndependent() || geo.isPointOnPath())
						&& (geo.algoUpdateSet != null)) {
					// add all dependent algos of geo to the overall algorithm
					// set
					geo.algoUpdateSet.addAllToCollection(tempSet2);
				}
			}
		}

		// now we have one nice algorithm set that we can update
		if (tempSet2.size() > 0) {
			final Iterator<AlgoElementInterface> it = tempSet2.iterator();
			while (it.hasNext()) {
				final AlgoElement algo = (AlgoElement) it.next();

				algo.update();

				if (algo == lastAlgo) {
					return;
				}

			}
		}
	}

	/**
	 * Updates this object and all dependent ones. Notifies kernel to repaint
	 * views.
	 */
	final public void updateRepaint() {
		updateCascade();
		kernel.notifyRepaint();
	}

	/**
	 * update color
	 */
	public void updateVisualStyle() {
		// updateGeo();
		kernel.notifyUpdateVisualStyle(this);
		// updateDependentObjects();
		// kernel.notifyRepaint();
	}

	public void updateVisualStyleRepaint() {

		updateVisualStyle();
		kernel.notifyRepaint();
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public final String toString() {
		return toString(StringTemplate.defaultTemplate);
	}

	public String toRealString(StringTemplate tpl) {
		return getRealLabel(tpl);
	}

	/*
	 * implementation of interface ExpressionValue
	 */
	public boolean isConstant() {
		return false;
	}

	public boolean isLeaf() {
		return true;
	}

	public final ExpressionValue evaluate() {
		return this;
	}
	
	public final ExpressionValue evaluate(StringTemplate tpl) {
		return this;
	}

	public HashSet<GeoElement> getVariables() {
		final HashSet<GeoElement> ret = new HashSet<GeoElement>();
		ret.add(this);
		return ret;
	}

	/**
	 * Returns all predecessors of this GeoElement that are random numbers and
	 * don't have labels.
	 * 
	 * @return all random numeric unlabeled predecessors
	 */
	public ArrayList<GeoNumeric> getRandomNumberPredecessorsWithoutLabels() {
		if (isIndependent()) {
			return null;
		}
		ArrayList<GeoNumeric> randNumbers = null;

		final TreeSet<GeoElement> pred = getAllPredecessors();
		final Iterator<GeoElement> it = pred.iterator();
		while (it.hasNext()) {
			final GeoElement geo = it.next();
			if (geo.isGeoNumeric()) {
				final GeoNumeric num = (GeoNumeric) geo;
				if (num.isRandomGeo() && !num.isLabelSet()) {
					if (randNumbers == null) {
						randNumbers = new ArrayList<GeoNumeric>();
					}
					randNumbers.add(num);
				}
			}
		}

		return randNumbers;
	}

	/**
	 * Returns all predecessors (of type GeoElement) that this object depends
	 * on. The predecessors are sorted topologically.
	 * 
	 * @return all predecessors of this geo
	 */
	public TreeSet<GeoElement> getAllPredecessors() {
		final TreeSet<GeoElement> set = new TreeSet<GeoElement>();
		addPredecessorsToSet(set, false);
		set.remove(this);
		return set;
	}

	/**
	 * Returns all independent predecessors (of type GeoElement) that this
	 * object depends on. The predecessors are sorted topologically. Note: when
	 * this method is called on an independent geo that geo is included in the
	 * TreeSet.
	 */
	@Override
	public TreeSet<GeoElement> getAllIndependentPredecessors() {
		final TreeSet<GeoElement> set = new TreeSet<GeoElement>();
		addPredecessorsToSet(set, true);
		return set;
	}

	// adds all predecessors of this object to the given set
	// the set is topologically sorted
	// @param onlyIndependent: whether only indpendent geos should be added
	final public void addPredecessorsToSet(final TreeSet<GeoElement> set,
			final boolean onlyIndependent) {
		if (algoParent == null) {
			set.add(this);
		} else { // parent algo
			algoParent.addPredecessorsToSet(set, onlyIndependent);
		}
	}

	public TreeSet<GeoElement> getAllRandomizablePredecessors() {
		final TreeSet<GeoElement> set = new TreeSet<GeoElement>();
		addRandomizablePredecessorsToSet(set);
		return set;
	}

	final public void addRandomizablePredecessorsToSet(
			final TreeSet<GeoElement> set) {
		if (isRandomizable() && !cloneInUse) {
			set.add(this);
		}

		if (algoParent != null) { // parent algo
			algoParent.addRandomizablePredecessorsToSet(set);
		}
	}

	/**
	 * Returns whether geo depends on this object.
	 * 
	 * @param geo
	 * @return true if geo depends on this object.
	 */
	public boolean isParentOf(final GeoElement geo) {
		if (algoUpdateSet != null) {
			final Iterator<AlgoElementInterface> it = algoUpdateSet
					.getIterator();
			while (it.hasNext()) {
				final AlgoElementInterface algo = it.next();
				for (int i = 0; i < algo.getOutputLength(); i++) {
					if (geo == algo.getOutput(i)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Returns whether this object is parent of other geos.
	 * 
	 * @return true if this object is parent of other geos.
	 */
	public boolean hasChildren() {
		return (algorithmList != null) && (algorithmList.size() > 0);
	}

	/**
	 * Returns whether this object is dependent on geo.
	 * 
	 * @param geo
	 * @return true if this object is dependent on geo.
	 */
	public boolean isChildOf(final GeoElement geo) {
		if ((geo == null) || isIndependent()) {
			return false;
		}
		return geo.isParentOf(this);
	}

	/**
	 * Returns whether this object is dependent on geo.
	 * 
	 * @param geo
	 * @return true if this object is dependent on geo.
	 */
	public boolean isChildOrEqual(final GeoElement geo) {
		return (this == geo) || isChildOf(geo);
	}

	/**
	 * Returns all children (of type GeoElement) that depend on this object.
	 * 
	 * @return set of all children of this geo
	 */
	public TreeSet<GeoElement> getAllChildren() {
		final TreeSet<GeoElement> set = new TreeSet<GeoElement>();
		if (algoUpdateSet != null) {
			final Iterator<AlgoElementInterface> it = algoUpdateSet
					.getIterator();
			while (it.hasNext()) {
				final AlgoElement algo = (AlgoElement) it.next();
				for (int i = 0; i < algo.getOutputLength(); i++) {
					set.add(algo.getOutput(i));
				}
			}
		}
		return set;
	}

	/*
	 * implementation of abstract methods from ConstructionElement
	 */
	@Override
	public GeoElement[] getGeoElements() {
		return myGeoElements;
	}

	private final GeoElement[] myGeoElements = new GeoElement[] { this };

	@Override
	final public boolean isAlgoElement() {
		return false;
	}

	@Override
	final public boolean isGeoElement() {
		return true;
	}

	/**
	 * Returns construction index in current construction. For a dependent
	 * object the construction index of its parent algorithm is returned.
	 */
	@Override
	public int getConstructionIndex() {
		if (algoParent == null) {
			return super.getConstructionIndex();
		}
		return algoParent.getConstructionIndex();
	}

	/**
	 * Returns the smallest possible construction index for this object in its
	 * construction. For an independent object 0 is returned.
	 */
	@Override
	public int getMinConstructionIndex() {
		if (algoParent == null) {
			return 0;
		}
		return algoParent.getMinConstructionIndex();
	}

	/**
	 * Returns the largest possible construction index for this object in its
	 * construction.
	 */
	@Override
	public int getMaxConstructionIndex() {
		if (algoParent == null) {
			// independent object:
			// index must be less than every dependent algorithm's index
			int min = cons.steps();
			final int size = algorithmList == null ? 0 : algorithmList.size();
			for (int i = 0; i < size; ++i) {
				final int index = (algorithmList.get(i)).getConstructionIndex();
				if (index < min) {
					min = index;
				}
			}
			return min - 1;
		}
		// dependent object
		return algoParent.getMaxConstructionIndex();
	}

	@Override
	public String getDefinitionDescription(StringTemplate tpl) {
		if (algoParent == null) {
			return "";
		}
		return algoParent.toString(tpl);
	}

	public String getDefinitionDescriptionHTML(final boolean addHTMLtag) {
		if (algoParent == null) {
			return "";
		}
		return indicesToHTML(app.translationFix(algoParent.toString(StringTemplate.defaultTemplate)),
				addHTMLtag);
	}

	@Override
	public String getCommandDescription(StringTemplate tpl) {
		if (algoParent == null) {
			return "";
		}
		return algoParent.getCommandDescription(tpl);
	}

	public String getCommandDescriptionHTML(final boolean addHTMLtag) {
		if (algoParent == null) {
			return "";
		}
		return indicesToHTML(algoParent.getCommandDescription(StringTemplate.defaultTemplate), addHTMLtag);
	}

	public String getCommandNameHTML(final boolean addHTMLtag) {
		if (algoParent == null) {
			return "";
		}
		return indicesToHTML(algoParent.getCommandName(StringTemplate.defaultTemplate), addHTMLtag);
	}

	@Override
	public int getRelatedModeID() {
		if (algoParent == null) {
			return -1;
		}
		return algoParent.getRelatedModeID();
	}

	/**
	 * Converts indices to HTML <sub> tags if necessary.
	 * 
	 * @param text
	 * @return html string
	 */
	public static String convertIndicesToHTML(final String text) {
		// check for index
		if (text.indexOf('_') > -1) {
			return indicesToHTML(text, true);
		}
		return text;
	}

	public String addLabelTextOrHTML(final String desc) {
		String ret;

		final boolean includesEqual = desc.indexOf('=') >= 0;

		// check for function in desc like "f(x) = x^2"
		if (includesEqual && desc.startsWith(label + '(')) {
			ret = desc;
		} else {
			final StringBuffer sb = new StringBuffer();
			sb.append(label);
			if (includesEqual) {
				sb.append(": ");
			} else {
				sb.append(" = ");
			}
			sb.append(desc);
			ret = sb.toString();
		}

		// check for index
		return convertIndicesToHTML(ret);
	}

	final public String getCaptionDescriptionHTML(final boolean addHTMLtag,StringTemplate tpl) {

		return indicesToHTML(getCaptionDescription(tpl), addHTMLtag);
	}

	/**
	 * Returns type string of GeoElement. Note: this is equal to
	 * getClassName().substring(3), but faster
	 * 
	 * @return type string without "Geo" prefix
	 */
	public abstract String getTypeString();

	/*
	 * { // e.g. GeoPoint -> type = Point //return getClassName().substring(3);
	 * }
	 */

	final public String getObjectType() {
		return getTypeString();
	}

	public String translatedTypeString() {
		return app.getPlain(getTypeString());
	}

	/**
	 * @return Type, label and definition information about this GeoElement (for
	 *         tooltips and error messages)
	 */
	final public String getLongDescription() {
		if (algoParent == null) {
			return getNameDescription();
		}
		final StringBuilder sbLongDesc = new StringBuilder();
		sbLongDesc.append(getNameDescription());
		// add dependency information
		sbLongDesc.append(": ");
		sbLongDesc.append(algoParent.toString(StringTemplate.defaultTemplate));
		return sbLongDesc.toString();
	}

	/**
	 * returns Type, label and definition information about this GeoElement as
	 * html string. (for tooltips and error messages)
	 * 
	 * @param colored
	 * @param addHTMLtag
	 * @return description (type + label + definition)
	 */
	final public String getLongDescriptionHTML(final boolean colored,
			final boolean addHTMLtag) {
		if ((algoParent == null) || isTextValue()) {
			return getNameDescriptionHTML(colored, addHTMLtag);
		}
		final StringBuilder sbLongDescHTML = new StringBuilder();

		final String formatedLabel = getLabel(StringTemplate.defaultTemplate);
		final String typeString = translatedTypeString();

		// html string
		if (addHTMLtag) {
			sbLongDescHTML.append("<html>");
		}

		final boolean reverseOrder = app.isReverseNameDescriptionLanguage();
		if (!reverseOrder) {
			// standard order: "point A"
			sbLongDescHTML.append(typeString);
			sbLongDescHTML.append(' ');
		}

		if (colored) {
			final Color colorAdapter = AwtFactory.prototype.newColor(
					getAlgebraColor().getRed(), getAlgebraColor()
							.getGreen(), getAlgebraColor().getBlue());
			sbLongDescHTML.append("<b><font color=\"#");
			sbLongDescHTML.append(StringUtil.toHexString(colorAdapter));
			sbLongDescHTML.append("\">");
		}
		sbLongDescHTML.append(indicesToHTML(formatedLabel, false));
		if (colored) {
			sbLongDescHTML.append("</font></b>");
		}

		if (reverseOrder) {
			// reverse order: "A point"
			sbLongDescHTML.append(' ');
			// For Hungarian, the standard is to lowercase the type.
			// I don't know if this is OK for Basque as well. -- Zoltan
			sbLongDescHTML.append(typeString.toLowerCase());
		}

		// add dependency information
		if (algoParent != null) {
			// Guy Hed, 25.8.2008
			// In order to present the text correctly in Hebrew and Arabic:
			final boolean rightToLeft = app.isRightToLeftReadingOrder();
			if (rightToLeft) {
				// sbLongDescHTML.append("\u200e\u200f: \u200e");
				sbLongDescHTML.append(Unicode.LeftToRightMark);
				sbLongDescHTML.append(Unicode.RightToLeftMark);
				sbLongDescHTML.append(": ");
				sbLongDescHTML.append(Unicode.LeftToRightMark);
			} else {
				sbLongDescHTML.append(": ");
			}
			sbLongDescHTML.append(indicesToHTML(
					app.translationFix(algoParent.toString(StringTemplate.defaultTemplate)), false));
			if (rightToLeft) {
				// sbLongDescHTML.append("\u200e");
				sbLongDescHTML.append(Unicode.LeftToRightMark);
			}
		}
		if (addHTMLtag) {
			sbLongDescHTML.append("</html>");
		}
		return sbLongDescHTML.toString();
	}

	static StringBuilder sbToolTipDesc = new StringBuilder();

	/**
	 * Returns long description for all GeoElements in given array.
	 * 
	 * @param geos
	 * @param colored
	 * @param addHTMLtag
	 *            true to wrap in &lt;html> ... &lt;/html>
	 * @return long description for all GeoElements in given array.
	 */
	final public static String getToolTipDescriptionHTML(
			final ArrayList<GeoElement> geos, final boolean colored,
			final boolean addHTMLtag, final boolean alwaysOn) {
		if (geos == null) {
			return null;
		}

		sbToolTipDesc.setLength(0);

		if (addHTMLtag) {
			sbToolTipDesc.append("<html>");
		}
		int count = 0;
		for (int i = 0; i < geos.size(); ++i) {
			final GeoElement geo = geos.get(i);
			if (geo.showToolTipText() || alwaysOn) {
				count++;
				sbToolTipDesc.append(geo.getTooltipText(colored, alwaysOn));
				if ((i + 1) < geos.size()) {
					sbToolTipDesc.append("<br>");
				}
			}
		}
		if (count == 0) {
			return null;
		}
		if (addHTMLtag) {
			sbToolTipDesc.append("</html>");
		}
		return sbToolTipDesc.toString();
	}

	/**
	 * Returns the label and/or value of this object for showing in
	 * EuclidianView. This depends on the current setting of labelMode:
	 * LABEL_NAME : only label LABEL_NAME_VALUE : label and value
	 * 
	 * @return label, value, label+value or caption
	 */
	public String getLabelDescription() {
		switch (labelMode) {
		case LABEL_NAME_VALUE:
			return getAlgebraDescription(StringTemplate.defaultTemplate);

		case LABEL_VALUE:
			return toDefinedValueString(StringTemplate.defaultTemplate);

		case LABEL_CAPTION: // Michael Borcherds 2008-02-18
			return getCaption(StringTemplate.defaultTemplate);

		default: // case LABEL_NAME:
			// return label;
			// Mathieu Blossier - 2009-06-30
			return getLabel(StringTemplate.defaultTemplate);
		}
	}

	/**
	 * Returns toValueString() if isDefined() ist true, else the translation of
	 * "undefined" is returned
	 * 
	 * @return eithe value string or "undefined"
	 */
	final public String toDefinedValueString(StringTemplate tpl) {
		if (isDefined()) {
			return toValueString(tpl);
		}
		return app.getPlain("undefined");
	}

	/**
	 * Returns algebraic representation of this GeoElement as Text. If this is
	 * not possible (because there are indices in the representation) a HTML
	 * string is returned.
	 * 
	 * @return algebraic representation of this GeoElement as Text
	 */
	final public String getAlgebraDescriptionTextOrHTML(StringTemplate tpl) {
		if (strAlgebraDescTextOrHTMLneedsUpdate) {
			final String algDesc = getAlgebraDescription(tpl);
			// convertion to html is only needed if indices are found
			if (hasIndexLabel()) {
				strAlgebraDescTextOrHTML = indicesToHTML(algDesc, true);
			} else {
				strAlgebraDescTextOrHTML = algDesc;
			}

			strAlgebraDescTextOrHTMLneedsUpdate = false;
		}

		return strAlgebraDescTextOrHTML;
	}

	final public String getAlgebraDescriptionHTML(StringTemplate tpl) {
		if (strAlgebraDescriptionHTMLneedsUpdate) {

			if (isGeoText()) {
				strAlgebraDescriptionHTML = indicesToHTML(toValueString(tpl),
						false);
			} else {
				strAlgebraDescriptionHTML = indicesToHTML(
						getAlgebraDescription(tpl), false);
			}
			strAlgebraDescriptionHTMLneedsUpdate = false;
		}

		return strAlgebraDescriptionHTML;
	}

	/**
	 * @return type and label of a GeoElement (for tooltips and error messages)
	 */
	final public String getLabelTextOrHTML() {
		if (strLabelTextOrHTMLUpdate) {
			if (hasIndexLabel()) {
				strLabelTextOrHTML = indicesToHTML(getLabel(), true);
			} else {
				strLabelTextOrHTML = getLabel();
			}
		}

		return strLabelTextOrHTML;
	}

	/**
	 * Returns algebraic representation of this GeoElement.
	 */
	@Override
	final public String getAlgebraDescription(StringTemplate tpl) {
		if (strAlgebraDescriptionNeedsUpdate) {
			if (isDefined()) {
				strAlgebraDescription = toString(tpl);
			} else {
				final StringBuilder sbAlgebraDesc = new StringBuilder();
				sbAlgebraDesc.append(label);
				sbAlgebraDesc.append(' ');
				sbAlgebraDesc.append(app.getPlain("undefined"));
				strAlgebraDescription = sbAlgebraDesc.toString();
			}

			strAlgebraDescriptionNeedsUpdate = false;
		}
		return strAlgebraDescription;
	}

	/**
	 * Returns simplified algebraic representation of this GeoElement. Used by
	 * the regression test output creator.
	 * 
	 * @return sumplifiedrepresentation for regression test
	 */
	final public String getAlgebraDescriptionRegrOut(StringTemplate tpl) {
		if (strAlgebraDescriptionNeedsUpdate) {
			if (isDefined()) {
				strAlgebraDescription = toStringMinimal(tpl);
			} else {
				final StringBuilder sbAlgebraDesc = new StringBuilder();
				sbAlgebraDesc.append(app.getPlain("undefined"));
				strAlgebraDescription = sbAlgebraDesc.toString();
			}

			strAlgebraDescriptionNeedsUpdate = false;
		} else {
			strAlgebraDescription = toStringMinimal(tpl);
		}

		return strAlgebraDescription;
	}

	public String toStringMinimal(StringTemplate tpl) {
		return toString(tpl);
	}

	public String getLaTeXdescription() {
		if (strLaTeXneedsUpdate) {
			if (isDefined() && !isInfinite()) {
				strLaTeX = toLaTeXString(false,StringTemplate.latexTemplate);
			} else {
				strLaTeX = app.getPlain("undefined");
			}
		}

		return strLaTeX;
	}

	/**
	 * Returns a string used to render a LaTeX form of the geo's algebra
	 * description.
	 * 
	 * @param substituteNumbers
	 * @return string used to render a LaTeX form of the geo's algebra
	 *         description.
	 * TODO: add templatehere as well
	 */
	public String getLaTeXAlgebraDescription(final boolean substituteNumbers,StringTemplate tpl) {
		return getLaTeXAlgebraDescription(this, substituteNumbers,tpl);
	}

	private String getLaTeXAlgebraDescription(final GeoElement geo,
			final boolean substituteNumbers,StringTemplate tpl) {

		final String algebraDesc = geo.getAlgebraDescription(tpl);
		final StringBuilder sb = new StringBuilder();

		if (geo.isGeoList()
				&& ((GeoList) geo).getElementType().equals(GeoClass.TEXT)) {
			return null;
		}
		// handle undefined
		if (!geo.isDefined()) {
			// we need to keep the string simple (no \mbox) so that
			// isLatexNeeded may return true
			sb.append(label);
			sb.append("\\,");
			sb.append(app.getPlain("undefined"));

			// handle non-GeoText prefixed with ":", e.g. "a: x = 3"
		} else if ((algebraDesc.indexOf(":") > -1) & !geo.isGeoText()) {
			sb.append(algebraDesc.split(":")[0] + ": \\,");
			sb.append(geo.getFormulaString(tpl, substituteNumbers));
		}

		// now handle non-GeoText prefixed with "="
		else if ((algebraDesc.indexOf("=") > -1) && !geo.isGeoText()) {
			sb.append(algebraDesc.split("=")[0] + "\\, = \\,");
			sb.append(geo.getFormulaString(tpl, substituteNumbers));
		}

		// handle GeoText with LaTeX
		else if (geo.isGeoText() && ((GeoTextInterface) geo).isLaTeX()) {
			sb.append(algebraDesc.split("=")[0]);
			sb.append("\\, = \\,");
			sb.append("\\text{``"); // left quote
			sb.append(((GeoTextInterface) geo).getTextString());
			sb.append("''}"); // right quote
		}

		// handle regular GeoText (and anything else we may have missed)
		// by returning a null string that will force non-LaTeX rendering
		else {
			return null;
		}

		return sb.toString();
	}

	/*
	 * final public Image getAlgebraImage(Image tempImage) { Graphics2D g2 =
	 * (Graphics2D) g; GraphicsConfiguration gc =
	 * app.getGraphicsConfiguration(); if (gc != null) { bgImage =
	 * gc.createCompatibleImage(width, height); Point p = drawIndexedString(g2,
	 * labelDesc, xLabel, yLabel);
	 * 
	 * setSize(fontSize, p.x, fontSize + p.y); }
	 */

	/*
	 * replaces all indices (_ and _{}) in str by <sub> tags, all and converts
	 * all special characters in str to HTML examples: "a_1" becomes
	 * "a<sub>1</sub>" "s_{AB}" becomes "s<sub>AB</sub>"
	 */
	private static String subBegin = "<sub><font size=\"-1\">";
	private static String subEnd = "</font></sub>";

	public static String indicesToHTML(final String str,
			final boolean addHTMLtag) {
		final StringBuilder sbIndicesToHTML = new StringBuilder();

		if (addHTMLtag) {
			sbIndicesToHTML.append("<html>");
		}

		int depth = 0;
		int startPos = 0;
		final int length = str.length();
		for (int i = 0; i < length; i++) {
			switch (str.charAt(i)) {
			case '_':
				// write everything before _
				if (i > startPos) {
					sbIndicesToHTML.append(StringUtil.toHTMLString(str
							.substring(startPos, i)));
				}
				startPos = i + 1;
				depth++;

				// check if next character is a '{' (beginning of index with
				// several chars)
				if ((startPos < length) && (str.charAt(startPos) != '{')) {
					sbIndicesToHTML.append(subBegin);
					sbIndicesToHTML.append(StringUtil.toHTMLString(str
							.substring(startPos, startPos + 1)));
					sbIndicesToHTML.append(subEnd);
					depth--;
				} else {
					sbIndicesToHTML.append(subBegin);
				}
				i++;
				startPos++;
				break;

			case '}':
				if (depth > 0) {
					if (i > startPos) {
						sbIndicesToHTML.append(StringUtil.toHTMLString(str
								.substring(startPos, i)));
					}
					sbIndicesToHTML.append(subEnd);
					startPos = i + 1;
					depth--;
				}
				break;
			}
		}

		if (startPos < length) {
			sbIndicesToHTML.append(StringUtil.toHTMLString(str
					.substring(startPos)));
		}
		if (addHTMLtag) {
			sbIndicesToHTML.append("</html>");
		}
		return sbIndicesToHTML.toString();
	}

	/**
	 * returns type and label of a GeoElement (for tooltips and error messages)
	 */
	@Override
	public String getNameDescription() {
		final StringBuilder sbNameDescription = new StringBuilder();

		final String label1 = getLabel(StringTemplate.defaultTemplate);
		final String typeString = translatedTypeString();

		if (app.isReverseNameDescriptionLanguage()) {
			// reverse order: "A point"
			sbNameDescription.append(label1);
			sbNameDescription.append(' ');
			// For Hungarian, the standard is to lowercase the type.
			// I don't know if this is OK for Basque as well. -- Zoltan
			sbNameDescription.append(typeString.toLowerCase());
		} else {
			// standard order: "point A"
			sbNameDescription.append(typeString);
			sbNameDescription.append(' ');
			sbNameDescription.append(label1);
		}

		return sbNameDescription.toString();
	}

	/**
	 * returns type and label of a GeoElement (for tooltips and error messages)
	 * 
	 * @return type and label of a GeoElement
	 */
	final public String getNameDescriptionTextOrHTML() {
		if (hasIndexLabel()) {
			return getNameDescriptionHTML(false, true);
		}
		return getNameDescription();
	}

	/**
	 * Returns whether the str contains any indices (i.e. '_' chars).
	 * 
	 * @return whether the str contains any indices (i.e. '_' chars).
	 */
	final public boolean hasIndexLabel() {
		if (strHasIndexLabel != label) {
			hasIndexLabel = ((label == null) || (label.indexOf('_') > -1));
			strHasIndexLabel = label;
		}

		return hasIndexLabel;
	}

	private String strHasIndexLabel;
	private boolean hasIndexLabel = false;
	// private boolean updateJavaScript;
	// private boolean clickJavaScript;
	private ScriptType updateScriptType = ScriptType.GGBSCRIPT;
	private ScriptType clickScriptType = ScriptType.GGBSCRIPT;

	/**
	 * returns type and label of a GeoElement as html string (for tooltips and
	 * error messages)
	 * 
	 * @param colored
	 * @param addHTMLtag
	 * @return type and label of a GeoElement as html string
	 */
	public String getNameDescriptionHTML(final boolean colored,
			final boolean addHTMLtag) {

		final StringBuilder sbNameDescriptionHTML = new StringBuilder();

		if (addHTMLtag) {
			sbNameDescriptionHTML.append("<html>");
		}

		final String label1 = getLabel(StringTemplate.defaultTemplate);
		final String typeString = translatedTypeString();

		final boolean reverseOrder = app.isReverseNameDescriptionLanguage();
		if (!reverseOrder) {
			// standard order: "point A"
			sbNameDescriptionHTML.append(typeString);
			sbNameDescriptionHTML.append(' ');
		}

		if (colored) {
			final Color colorAdapter = AwtFactory.prototype.newColor(
					getAlgebraColor().getRed(), getAlgebraColor().getGreen(),
					getAlgebraColor().getBlue());
			sbNameDescriptionHTML.append(" <b><font color=\"#");
			sbNameDescriptionHTML.append(StringUtil.toHexString(colorAdapter));
			sbNameDescriptionHTML.append("\">");
		}
		sbNameDescriptionHTML.append(indicesToHTML(label1, false));
		if (colored) {
			sbNameDescriptionHTML.append("</font></b>");
		}

		if (reverseOrder) {
			// reverse order: "A point"
			sbNameDescriptionHTML.append(' ');
			// For Hungarian, the standard is to lowercase the type.
			// I don't know if this is OK for Basque as well. -- Zoltan
			sbNameDescriptionHTML.append(typeString.toLowerCase());
		}

		if (addHTMLtag) {
			sbNameDescriptionHTML.append("</html>");
		}
		return sbNameDescriptionHTML.toString();
	}

	/*
	 * #****************************************************** SAVING
	 * *****************************************************
	 */
	public abstract String getClassName();

	final public String getXMLtypeString() {
		return app.toLowerCase(getClassName().substring(3));
	}

	public String getI2GtypeString() {
		return getXMLtypeString();
	}

	public String getXML() {
		final StringBuilder sb = new StringBuilder();
		getXML(sb);
		return sb.toString();
	}

	/**
	 * save object in xml format GeoGebra File Format
	 */
	@Override
	public void getXML(final StringBuilder sb) {
		
		// make sure numbers are not put in XML in eg Arabic
		//final boolean oldI8NValue = Kernel.internationalizeDigits;
		//Kernel.internationalizeDigits = false;

		getElementOpenTagXML(sb);

		getXMLtags(sb);
		sb.append(getCaptionXML());

		getElementCloseTagXML(sb);

		
	}

	protected void getElementOpenTagXML(final StringBuilder sb) {
		final String type = getXMLtypeString();
		sb.append("<element");
		sb.append(" type=\"");
		sb.append(type);
		sb.append("\" label=\"");
		sb.append(StringUtil.encodeXML(label));
		if (defaultGeoType >= 0) {
			sb.append("\" default=\"");
			sb.append(defaultGeoType);
		}
		sb.append("\">\n");
	}

	protected void getElementCloseTagXML(final StringBuilder sb) {
		sb.append("</element>\n");
	}

	public void getScriptTags(final StringBuilder sb) {
		// JavaScript
		if ((updateJavaScript() && (updateScript.length() > 0))
				|| (clickJavaScript() && (clickScript.length() > 0))) {
			sb.append("\t<javascript ");
			if (clickJavaScript() && (clickScript.length() > 0)) {
				sb.append(" val=\"");
				sb.append(getXMLClickScript());
				sb.append("\"");
			}
			if (updateJavaScript() && (updateScript.length() > 0)) {
				sb.append(" onUpdate=\"");
				sb.append(getXMLUpdateScript());
				sb.append("\"");
			}
			sb.append("/>\n");
		}

		// GGBScript
		if ((updateGGBScript() && (updateScript != null) && (updateScript
				.length() > 0))
				|| (clickGGBScript() && (clickScript != null) && (clickScript
						.length() > 0))) {
			sb.append("\t<ggbscript ");
			if (clickGGBScript() && (clickScript.length() > 0)) {
				sb.append(" val=\"");
				sb.append(getXMLClickScript());
				sb.append("\"");
			}
			if (updateGGBScript() && (updateScript.length() > 0)) {
				sb.append(" onUpdate=\"");
				sb.append(getXMLUpdateScript());
				sb.append("\"");
			}
			sb.append("/>\n");
		}

		// Python
		if ((updatePythonScript() && (updateScript != null) && (updateScript
				.length() > 0))
				|| (clickPythonScript() && (clickScript != null) && (clickScript
						.length() > 0))) {
			sb.append("\t<python ");
			if (clickPythonScript() && (clickScript.length() > 0)) {
				sb.append(" val=\"");
				sb.append(getXMLClickScript());
				sb.append("\"");
			}
			if (updatePythonScript() && (updateScript.length() > 0)) {
				sb.append(" onUpdate=\"");
				sb.append(getXMLUpdateScript());
				sb.append("\"");
			}
			sb.append("/>\n");
		}

	}

	public boolean clickGGBScript() {
		return clickScriptType.equals(ScriptType.GGBSCRIPT);
	}

	public boolean updateGGBScript() {
		return updateScriptType.equals(ScriptType.GGBSCRIPT);
	}

	public boolean clickJavaScript() {
		return clickScriptType.equals(ScriptType.JAVASCRIPT);
	}

	public boolean updateJavaScript() {
		return updateScriptType.equals(ScriptType.JAVASCRIPT);
	}

	public boolean clickPythonScript() {
		return clickScriptType.equals(ScriptType.PYTHON);
	}

	private boolean updatePythonScript() {
		return updateScriptType.equals(ScriptType.PYTHON);
	}

	public String getCaptionXML() {
		// caption text
		if ((caption != null) && (caption.length() > 0)
				&& !caption.equals(label)) {
			final StringBuilder sb = new StringBuilder();
			sb.append("\t<caption val=\"");
			sb.append(StringUtil.encodeXML(caption));
			sb.append("\"/>\n");
			return sb.toString();
		}
		return "";
	}

	/**
	 * save object in i2g format Intergeo File Format (Yves Kreis)
	 */
	@Override
	public void getI2G(final StringBuilder sb, final int mode) {
		final String type = getI2GtypeString();

		if (mode == CONSTRAINTS) {
			if (isIndependent() || isPointOnPath()) {
				sb.append("\t\t<free_");
				sb.append(type);
				sb.append(">\n");

				sb.append("\t\t\t<");
				sb.append(type);
				sb.append(" out=\"true\">");
				sb.append(StringUtil.encodeXML(label));
				sb.append("</");
				sb.append(type);
				sb.append(">\n");

				sb.append("\t\t</free_");
				sb.append(type);
				sb.append(">\n");
			}
		} else {
			if ((mode == DISPLAY)
					&& ((caption == null) || (caption.length() == 0) || caption
							.equals(label))) {
				return;
			}

			sb.append("\t\t<");
			sb.append(type);
			sb.append(" id=\"");
			sb.append(StringUtil.encodeXML(label));
			sb.append("\">\n");

			if (mode == ELEMENTS) {
				getI2Gtags(sb);
			} else if (mode == DISPLAY) {
				// caption text
				sb.append("\t\t\t<label>");
				sb.append(StringUtil.encodeXML(caption));
				sb.append("</label>\n");
			}

			sb.append("\t\t</");
			sb.append(type);
			sb.append(">\n");
		}
	}

	public final void getAuxiliaryXML(final StringBuilder sb) {// package
																// private
		if (!isAuxiliaryObjectByDefault()) {
			if (auxiliaryObject) {
				sb.append("\t<auxiliary val=\"");
				sb.append("true");
				sb.append("\"/>\n");
			}
		} else { // needed for eg GeoTexts (in Algebra View but Auxilliary by
					// default from ggb 4.0)
			if (!auxiliaryObject) {
				sb.append("\t<auxiliary val=\"");
				sb.append("false");
				sb.append("\"/>\n");
			}
		}
	}

	/**
	 * returns all visual xml tags (like show, objColor, labelOffset, ...)
	 * 
	 * @param sb
	 */
	public void getXMLvisualTags(final StringBuilder sb) {// package private
		getXMLvisualTags(sb, true);
	}

	public void getXMLvisualTags(final StringBuilder sb,
			final boolean withLabelOffset) {// package
		// private
		final boolean isDrawable = isDrawable();

		// show object and/or label in EuclidianView
		// don't save this for simple dependent numbers (e.g. in spreadsheet)
		if (isDrawable) {
			sb.append("\t<show");
			sb.append(" object=\"");
			sb.append(euclidianVisible);
			sb.append("\"");
			sb.append(" label=\"");
			sb.append(labelVisible);
			sb.append("\"");

			int EVs = 0;
			if (!isVisibleInView(AbstractApplication.VIEW_EUCLIDIAN)) {
				// Application.debug("visible in ev1");
				EVs += 1; // bit 0
			}

			if (isVisibleInView(AbstractApplication.VIEW_EUCLIDIAN2)) {
				EVs += 2; // bit 1
			}

			if (EVs != 0) {
				sb.append(" ev=\"");
				sb.append(EVs);
				sb.append("\"");
			}

			sb.append("/>\n");
		}

		if (getShowTrimmedIntersectionLines()) {
			sb.append("\t<showTrimmed val=\"true\"/>\n");
		}

		// conditional visibility
		sb.append(getShowObjectConditionXML());

		// if (isDrawable) removed - want to be able to color objects in
		// AlgebraView, Spreadsheet
		{
			sb.append("\t<objColor");
			sb.append(" r=\"");
			sb.append(objColor.getRed());
			sb.append("\"");
			sb.append(" g=\"");
			sb.append(objColor.getGreen());
			sb.append("\"");
			sb.append(" b=\"");
			sb.append(objColor.getBlue());
			sb.append("\"");
			sb.append(" alpha=\"");

			// changed from alphavalue (don't want alpha="-1.0" in XML)
			// see GeoList
			sb.append(getAlphaValue());

			sb.append("\"");
			StringTemplate tpl =StringTemplate.xmlTemplate;
			if ((colFunction != null) && kernel.getSaveScriptsToXML()) {
				sb.append(" dynamicr=\"");
				sb.append(StringUtil.encodeXML(colFunction.get(0).getLabel(tpl)));
				sb.append('\"');
				sb.append(" dynamicg=\"");
				sb.append(StringUtil.encodeXML(colFunction.get(1).getLabel(tpl)));
				sb.append('\"');
				sb.append(" dynamicb=\"");
				sb.append(StringUtil.encodeXML(colFunction.get(2).getLabel(tpl)));
				sb.append('\"');
				if (colFunction.size() == 4) {
					sb.append(" dynamica=\"");
					sb.append(StringUtil.encodeXML(colFunction.get(3)
							.getLabel(tpl)));
					sb.append('\"');
				}
				sb.append(" colorSpace=\"");
				sb.append(colorSpace);
				sb.append('\"');
			}

			if (isHatchingEnabled()) {
				sb.append(" hatchAngle=\"");
				sb.append(hatchingAngle);
				sb.append("\" hatchDistance=\"");
				sb.append(hatchingDistance);
				sb.append("\"");
			} else if (fillType == FILL_IMAGE) {
				sb.append(" image=\"");
				sb.append(graphicsadapter.getImageFileName());
				sb.append('\"');
			}
			if (inverseFill) {
				sb.append(" inverseFill=\"true\"");
			}
			sb.append("/>\n");
		}

		if (bgColor != null) {
			sb.append("\t<bgColor");
			sb.append(" r=\"");
			sb.append(bgColor.getRed());
			sb.append("\"");
			sb.append(" g=\"");
			sb.append(bgColor.getGreen());
			sb.append("\"");
			sb.append(" b=\"");
			sb.append(bgColor.getBlue());
			sb.append("\"");
			sb.append(" alpha=\"");
			sb.append(bgColor.getAlpha());
			sb.append("\"/>\n");
		}

		// don't remove layer 0 information
		// we always need it in case an earlier element has higher layer eg 1
		if (isDrawable) {
			sb.append("\t<layer ");
			sb.append("val=\"" + layer + "\"");
			sb.append("/>\n");
		}

		if (withLabelOffset && ((labelOffsetX != 0) || (labelOffsetY != 0))) {
			sb.append("\t<labelOffset");
			sb.append(" x=\"");
			sb.append(labelOffsetX);
			sb.append("\"");
			sb.append(" y=\"");
			sb.append(labelOffsetY);
			sb.append("\"");
			sb.append("/>\n");
		}

		if (isDrawable()) {
			sb.append("\t<labelMode");
			sb.append(" val=\"");
			sb.append(labelMode);
			sb.append("\"");
			sb.append("/>\n");

			if (tooltipMode != TOOLTIP_ALGEBRAVIEW_SHOWING) {
				sb.append("\t<tooltipMode");
				sb.append(" val=\"");
				sb.append(tooltipMode);
				sb.append("\"");
				sb.append("/>\n");
			}
		}

		// trace on or off
		if (isTraceable()) {
			final Traceable t = (Traceable) this;
			if (t.getTrace()) {
				sb.append("\t<trace val=\"true\"/>\n");
			}
		}

		// G.Sturr 2010-5-29
		// Get spreadsheet trace XML from the trace manager

		// trace to spreadsheet
		if (app.isUsingFullGui() && isSpreadsheetTraceable()
				&& getSpreadsheetTrace()) {
			sb.append(app.getTraceXML(this));// sb.append(null)?
		}

		/*
		 * --- old version // trace to spreadsheet on or off if (isGeoPoint()) {
		 * GeoPoint2 p = (GeoPoint2) this; if (p.getSpreadsheetTrace()) {
		 * sb.append("\t<spreadsheetTrace val=\"true\"/>\n"); } }
		 */
		// END G.Sturr

		// decoration type
		if (decorationType != DECORATION_NONE) {
			sb.append("\t<decoration");
			sb.append(" type=\"");
			sb.append(decorationType);
			sb.append("\"/>\n");
		}

	}

	public void getXMLanimationTags(final StringBuilder sb) {// package private
		StringTemplate tpl =StringTemplate.xmlTemplate;
		// animation step width
		if (isChangeable()) {
			sb.append("\t<animation");
			final String animStep = animationIncrement == null ? "1"
					: getAnimationStepObject().getLabel(tpl);
			sb.append(" step=\"" + StringUtil.encodeXML(animStep) + "\"");
			final String animSpeed = animationSpeedObj == null ? "1"
					: getAnimationSpeedObject().getLabel(tpl);
			sb.append(" speed=\"" + StringUtil.encodeXML(animSpeed) + "\"");
			sb.append(" type=\"" + animationType + "\"");
			sb.append(" playing=\"");
			sb.append((isAnimating() ? "true" : "false"));
			sb.append("\"");
			sb.append("/>\n");
		}

	}

	public void getXMLfixedTag(final StringBuilder sb) {// package private
		// is object fixed
		if (fixed && isFixable()) {
			sb.append("\t<fixed val=\"");
			sb.append(fixed);
			sb.append("\"/>\n");
		}
		// is selection allowed
		if (!selectionAllowed) {
			sb.append("\t<selectionAllowed val=\"");
			sb.append(selectionAllowed);
			sb.append("\"/>\n");
		}
	}

	/**
	 * returns all class-specific xml tags for getXML GeoGebra File Format
	 * 
	 * @param sb
	 */
	protected void getXMLtags(final StringBuilder sb) {
		// sb.append(getLineStyleXML());
		getXMLvisualTags(sb);
		getXMLanimationTags(sb);
		getXMLfixedTag(sb);
		getAuxiliaryXML(sb);
		getBreakpointXML(sb);
		if (kernel.getSaveScriptsToXML()) {
			getScriptTags(sb);
		}
	}

	/**
	 * returns some class-specific xml tags for getConstructionRegrOut (default
	 * implementation, may be overridden in certain subclasses)
	 * 
	 * @param sb
	 */
	public void getXMLtagsMinimal(final StringBuilder sb,StringTemplate tpl) {
		sb.append(toValueStringMinimal(tpl));
	}

	/**
	 * returns class-specific value string for getConstructionRegressionOut
	 * (default implementation, may be overridden in certain subclasses)
	 */
	protected String toValueStringMinimal(StringTemplate tpl) {
		return toValueString(tpl);
	}

	/**
	 * returns the number in rounded format to 6 decimal places, in case of the
	 * number is very close to 0, it returns the exact value
	 * 
	 * @param number
	 * @return formatted String
	 */
	protected String regrFormat(final double number) {
		if (Math.abs(number) < 0.000001) {
			final Double numberD = new Double(number);
			return numberD.toString();
		}
		// this constructors uses US locale, so we don't have to worry about ","
		final NumberFormatAdapter df = FormatFactory.prototype
				.getNumberFormat("#.######", 6);
		return df.format(number);
	}

	/**
	 * returns all class-specific i2g tags for getI2G Intergeo File Format (Yves
	 * Kreis)
	 * 
	 * @param sb
	 */
	protected void getI2Gtags(final StringBuilder sb) {
		//do nothing
	}

	/**
	 * Returns line type and line thickness as xml string.
	 * 
	 * @param sb
	 * @see #getXMLtags(StringBuilder) of GeoConic, GeoLine and GeoVector
	 */
	protected void getLineStyleXML(final StringBuilder sb) {
		if (isGeoPoint()) {
			return;
		}

		sb.append("\t<lineStyle");
		sb.append(" thickness=\"");
		sb.append(lineThickness);
		sb.append("\"");
		sb.append(" type=\"");
		sb.append(lineType);
		sb.append("\"");
		sb.append(" typeHidden=\"");
		sb.append(lineTypeHidden);
		sb.append("\"");
		sb.append("/>\n");
	}

	/**
	 * Returns line type and line thickness as xml string.
	 * 
	 * @param sb
	 * @see #getXMLtags(StringBuilder) of GeoConic, GeoLine and GeoVector
	 */
	public void getBreakpointXML(final StringBuilder sb) {// package private
		if (isConsProtBreakpoint) {
			sb.append("\t<breakpoint val=\"");
			sb.append(isConsProtBreakpoint);
			sb.append("\"/>\n");

		}
	}

	private String getShowObjectConditionXML() {
		if ((condShowObject != null) && kernel.getSaveScriptsToXML()) {
			final StringBuilder sb = new StringBuilder();
			sb.append("\t<condition showObject=\"");
			sb.append(StringUtil.encodeXML(condShowObject.getLabel()));
			sb.append("\"/>\n");
			return sb.toString();
		}
		return "";
	}

	/**
	 * @return line thickness
	 */
	public int getLineThickness() {
		return lineThickness;
	}

	/**
	 * @return minimum line thickness (normally 1, but 0 for polygons, integrals
	 *         etc)
	 */
	public int getMinimumLineThickness() {
		return 1;
	}

	/**
	 * @return line type
	 */
	public int getLineType() {
		return lineType;
	}

	/**
	 * @return the line type for hidden parts
	 */
	public int getLineTypeHidden() {
		return lineTypeHidden;
	}

	/**
	 * @param th
	 *            new thickness
	 */
	public void setLineThickness(final int th) {
		lineThickness = Math.max(0, th);
	}

	/**
	 * @param i
	 *            new type
	 */
	public void setLineType(final int i) {
		lineType = i;
	}

	/**
	 * @param i
	 */
	public void setLineTypeHidden(final int i) {
		lineTypeHidden = i;
	}

	public void setDecorationType(final int type) {
		decorationType = type;
	}

	/*
	 * NOTE: change in GeoElementWrapper too!
	 */

	public boolean isGeoElement3D() {
		return false;
	}

	/**
	 * 
	 * @return true if the geo is drawable in 3D view
	 */
	public boolean hasDrawable3D() {
		return isGeoElement3D();
	}
	
	public boolean hasLevelOfDetail() {
		return false;
	}
	
	

	public boolean isGeoAngle() {
		return false;
	}

	public boolean isGeoBoolean() {
		return false;
	}

	public boolean isGeoPolyLine() {
		return false;
	}

	public boolean isGeoCubic() {
		return false;
	}

	public boolean isGeoImplicitPoly() {
		return false;
	}

	public boolean isGeoConic() {
		return false;
	}

	public boolean isGeoConicPart() {
		return false;
	}

	public boolean isGeoFunction() {
		return false;
	}

	public boolean isGeoFunctionNVar() {
		return false;
	}

	public boolean isGeoFunctionBoolean() {
		return false;
	}

	public boolean isGeoFunctionConditional() {
		return false;
	}

	public boolean isGeoFunctionable() {
		return false;
	}

	public boolean isGeoImage() {
		return false;
	}

	public boolean isGeoLine() {
		return false;
	}

	public boolean isGeoPlane() {
		return false;
	}

	public boolean isGeoLocus() {
		return false;
	}

	public boolean isGeoNumeric() {
		return false;
	}

	public boolean isGeoPoint() {
		return false;
	}

	public boolean isGeoCasCell() {
		return false;
	}

	/*
	 * public boolean isGeoPoint3D() { return false; }
	 */

	public boolean isGeoPolygon() {
		return false;
	}

	public boolean isGeoRay() {
		return false;
	}

	public boolean isGeoSegment() {
		return false;
	}

	public boolean isGeoText() {
		return false;
	}

	public boolean isGeoVector() {
		return false;
	}

	public boolean isGeoCurveCartesian() {
		return false;
	}

	public boolean isCasEvaluableObject() {
		return false;
	}

	final public boolean isExpressionNode() {
		return false;
	}

	final public boolean isVariable() {
		return false;
	}

	final public boolean contains(final ExpressionValue ev) {
		return ev == this;
	}

	/*
	 * ** hightlighting and selecting only for internal purpouses, i.e. this is
	 * not saved
	 */

	final public void setSelected(final boolean flag) {
		selected = flag;
	}

	final public void setHighlighted(final boolean flag) {
		highlighted = flag;
	}

	final public boolean doHighlighting() {
		return (highlighted || selected)
				&& (!isFixed() || isSelectionAllowed());
	}

	final public boolean isSelected() {
		return selected;
	}

	public boolean isNumberValue() {
		return false;
	}

	public boolean isAngle() {
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isBooleanValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}

	public boolean isTextValue() {
		return false;
	}

	public boolean isListValue() {
		return false;
	}

	public boolean isGeoButton() {
		return false;
	}

	public boolean isUseVisualDefaults() {
		return useVisualDefaults;
	}

	public void setUseVisualDefaults(final boolean useVisualDefaults) {
		this.useVisualDefaults = useVisualDefaults;
	}

	public boolean isAbsoluteScreenLocateable() {
		return false;
	}

	public final GeoBoolean getShowObjectCondition() {
		return condShowObject;
	}

	public void setShowObjectCondition(final GeoBoolean cond)
			throws CircularDefinitionException {
		// check for circular definition
		// if (this == cond || isParentOf(cond))
		// I relaxed this to allow (a parallel b) for a and b
		if (this == cond) {
			throw new CircularDefinitionException();
		}

		// unregister old condition
		if (condShowObject != null) {
			condShowObject.unregisterConditionListener(this);
		}

		// set new condition
		condShowObject = cond;

		// register new condition
		if (condShowObject != null) {
			condShowObject.registerConditionListener(this);
		}
	}

	public final void removeCondition(final GeoBoolean bool) {
		if (condShowObject == bool) {
			condShowObject = null;
		}
	}

	public final GeoList getColorFunction() {
		return colFunction;
	}

	public void setColorFunction(final GeoList col)
	// throws CircularDefinitionException
	{
		// Application.debug("setColorFunction"+col.getValue());

		// check for circular definition (not needed)
		// if (this == col || isParentOf(col))
		// throw new CircularDefinitionException();

		// unregister old condition
		if (colFunction != null) {
			colFunction.unregisterColorFunctionListener(this);
		}

		// set new condition
		colFunction = col;

		// register new condition
		if (colFunction != null) {
			colFunction.unregisterColorFunctionListener(this);
		}
	}

	public void removeColorFunction() {
		// unregister old condition
		if (colFunction != null) {
			colFunction.unregisterColorFunctionListener(this);
		}
		// Application.debug("removeColorFunction");
		// if (colFunction == col)
		colFunction = null;
	}

	/**
	 * Translates all GeoElement objects in geos by a vector in real world
	 * coordinates or by (xPixel, yPixel) in screen coordinates.
	 * 
	 * @param geos
	 * @param rwTransVec
	 * @param endPosition
	 *            may be null
	 * @param viewDirection
	 * @return true if something was moved
	 */
	public static boolean moveObjects(ArrayList<GeoElement> geosToMove,
			final Coords rwTransVec, final Coords endPosition,
			final Coords viewDirection) {
		if (moveObjectsUpdateList == null) {
			moveObjectsUpdateList = new ArrayList<GeoElement>();
		}
		ArrayList<GeoElement> geos = geosToMove;
		final ArrayList<GeoElement> geos2 = new ArrayList<GeoElement>();

		// remove duplicates, eg drag Circle[A,A]
		for (int i = 0; i < geos.size(); i++) {
			if (!geos2.contains(geos.get(i))) {
				geos2.add(geos.get(i));
			}
		}

		geos = geos2;

		boolean moved = false;
		final int size = geos.size();
		moveObjectsUpdateList.clear();
		moveObjectsUpdateList.ensureCapacity(size);

		for (int i = 0; i < size; i++) {
			final GeoElement geo = geos.get(i);

			/*
			 * Michael Borcherds check for isGeoPoint() as it makes the mouse
			 * jump to the position of the point when dragging eg Image with one
			 * corner, Rigid Polygon and stops grid-lock working properly but is
			 * needed for eg dragging (a + x(A), b + x(B))
			 */
			AbstractApplication.debug((geo.getParentAlgorithm() == null) + " "
					+ size + " " + geo.getClassName());
			final Coords position = (size == 1)
					&& (geo.getParentAlgorithm() != null) ? endPosition : null;
			moved = geo.moveObject(rwTransVec, position, viewDirection,
					moveObjectsUpdateList) || moved;
		}

		// take all independent input objects and build a common updateSet
		// then update all their algos.
		// (don't do updateCascade() on them individually as this could cause
		// multiple updates of the same algorithm)
		updateCascade(moveObjectsUpdateList, getTempSet(), false);

		return moved;
	}

	private static volatile ArrayList<GeoElement> moveObjectsUpdateList;
	private static volatile TreeSet<AlgoElementInterface> tempSet;

	protected static TreeSet<AlgoElementInterface> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<AlgoElementInterface>();
		}
		return tempSet;
	}

	// /**
	// * Moves geo by a vector in real world coordinates.
	// * @return whether actual moving occurred
	// */
	// final public boolean moveObject(GeoVector rwTransVec, Point2D.Double
	// endPosition) {
	// return moveObject(rwTransVec, endPosition, null);
	// }

	protected boolean movePoint(final Coords rwTransVec,
			final Coords endPosition) {

		boolean movedGeo = false;

		final GeoPoint2 point = (GeoPoint2) this;
		if (endPosition != null) {
			point.setCoords(endPosition.getX(), endPosition.getY(), 1);
			movedGeo = true;
		}

		// translate point
		else {
			double x = point.getInhomX() + rwTransVec.getX();
			double y = point.getInhomY() + rwTransVec.getY();

			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			if (Math.abs(rwTransVec.getX()) > Kernel.MIN_PRECISION) {
				x = Kernel.checkDecimalFraction(x);
			}
			if (Math.abs(rwTransVec.getY()) > Kernel.MIN_PRECISION) {
				y = Kernel.checkDecimalFraction(y);
			}

			// set translated point coords
			point.setCoords(x, y, 1);
			movedGeo = true;
		}

		return movedGeo;

	}

	/**
	 * Moves geo by a vector in real world coordinates.
	 * 
	 * @return whether actual moving occurred
	 */
	private boolean moveObject(final Coords rwTransVec,
			final Coords endPosition, final Coords viewDirection,
			final ArrayList<GeoElement> updateGeos) {
		boolean movedGeo = false;
		GeoElement geo = this;
		// moveable geo
		if (isMoveable()) {
			// point
			if (isGeoPoint()) {

				if (getParentAlgorithm() instanceof AlgoDynamicCoordinatesInterface) {
					final GeoPoint2 p = ((AlgoDynamicCoordinatesInterface) getParentAlgorithm())
							.getParentPoint();
					movedGeo = p.movePoint(rwTransVec, endPosition);
					geo = p;
				} else {
					movedGeo = movePoint(rwTransVec, endPosition);
				}
			}

			// translateable
			else if (isTranslateable()) {
				final Translateable trans = (Translateable) this;
				trans.translate(rwTransVec);
				movedGeo = true;
			}

			// absolute position on screen
			else if (isAbsoluteScreenLocateable()) {
				final AbsoluteScreenLocateable screenLoc = (AbsoluteScreenLocateable) this;
				if (screenLoc.isAbsoluteScreenLocActive()) {
					final int vxPixel = (int) Math.round(kernel.getXscale()
							* rwTransVec.getX());
					final int vyPixel = -(int) Math.round(kernel.getYscale()
							* rwTransVec.getY());
					final int x = screenLoc.getAbsoluteScreenLocX() + vxPixel;
					final int y = screenLoc.getAbsoluteScreenLocY() + vyPixel;
					screenLoc.setAbsoluteScreenLoc(x, y);
					movedGeo = true;
				} else if (isGeoNumeric()) {
					if (!((GeoNumeric) geo).isSliderFixed()) {
						// real world screen position - GeoNumeric
						((GeoNumeric) geo).setRealWorldLoc(
								((GeoNumeric) geo).getRealWorldLocX()
										+ rwTransVec.getX(),
								((GeoNumeric) geo).getRealWorldLocY()
										+ rwTransVec.getY());
						movedGeo = true;
					}
				} else if (isGeoText()) {
					// check for GeoText with unlabeled start point
					final GeoTextInterface movedGeoText = (GeoTextInterface) this;
					if (movedGeoText.hasAbsoluteLocation()) {
						// absolute location: change location
						final GeoPoint2 loc = (GeoPoint2) movedGeoText
								.getStartPoint();
						if (loc != null) {
							loc.translate(rwTransVec);
							movedGeo = true;
						}
					}
				}
			}

			if (movedGeo) {
				if (updateGeos != null) {
					updateGeos.add(geo);
				} else {
					geo.updateCascade();
				}
			}
		}

		// non-moveable geo
		else {
			movedGeo = moveFromChangeableCoordParentNumbers(rwTransVec,
					endPosition, viewDirection, updateGeos, tempMoveObjectList);
		}

		return movedGeo;
	}

	/**
	 * try to move the geo with coord parent numbers (e.g. point defined by
	 * sliders)
	 * 
	 * @param rwTransVec
	 * @param endPosition
	 * @param viewDirection
	 * @param updateGeos
	 * @param tempMoveObjectList1
	 * @return false if not moveable this way
	 */
	public boolean moveFromChangeableCoordParentNumbers(
			final Coords rwTransVec, final Coords endPosition,
			final Coords viewDirection, final ArrayList<GeoElement> updateGeos,
			final ArrayList<GeoElement> tempMoveObjectList1) {
		return false;
	}

	/**
	 * 
	 * @return true if has changeable coord parent numbers (e.g. point defined
	 *         by sliders)
	 */
	public boolean hasChangeableCoordParentNumbers() {
		return false;
	}

	/**
	 * record values when mouse pressed
	 */
	public void recordChangeableCoordParentNumbers() {
		//do nothing
	}

	/**
	 * add changeable coord parent number to update list
	 * 
	 * @param number
	 * @param updateGeos
	 * @param tempMoveObjectList1
	 */
	protected void addChangeableCoordParentNumberToUpdateList(
			final GeoElement number, final ArrayList<GeoElement> updateGeos,
			ArrayList<GeoElement> tempMoveObjectList1) {
		if (updateGeos != null) {
			// add number to update list
			updateGeos.add(number);
		} else {
			// update number right now
			ArrayList<GeoElement> tempMoveObjectList2 = tempMoveObjectList1;
			if (tempMoveObjectList1 == null) {
				tempMoveObjectList2 = new ArrayList<GeoElement>();
			}
			tempMoveObjectList2.add(number);
			updateCascade(tempMoveObjectList2, getTempSet(), false);
		}
	}

	private ArrayList<GeoElement> tempMoveObjectList;

	/**
	 * Returns the position of this GeoElement in GeoGebra's spreadsheet view.
	 * The x-coordinate of the returned point specifies its column and the
	 * y-coordinate specifies its row location. Note that this method may return
	 * null if no position was specified so far.
	 * 
	 * @return position of this GeoElement in GeoGebra's spreadsheet view.
	 */
	public Point getSpreadsheetCoords() {
		if (spreadsheetCoords == null) {
			updateSpreadsheetCoordinates();
		}
		return spreadsheetCoords;
	}

	/**
	 * Sets the position of this GeoElement in GeoGebra's spreadsheet. The
	 * x-coordinate specifies its column and the y-coordinate specifies its row
	 * location.
	 * 
	 * @param spreadsheetCoords
	 */
	public void setSpreadsheetCoords(final Point spreadsheetCoords) {
		this.spreadsheetCoords = spreadsheetCoords;
	}

	public Point getOldSpreadsheetCoords() {
		return oldSpreadsheetCoords;
	}

	public final boolean isAlgoMacroOutput() {
		return isAlgoMacroOutput;
	}

	public void setAlgoMacroOutput(final boolean isAlgoMacroOutput) {
		this.isAlgoMacroOutput = isAlgoMacroOutput;
	}

	// Michael Borcherds 2008-04-30
	public abstract boolean isEqual(GeoElement Geo);

	/**
	 * Returns wheter this - f gives 0 in the CAS.
	 * 
	 * @param f
	 * @return wheter this - f gives 0 in the CAS.
	 */
	final public boolean isDifferenceZeroInCAS(final GeoElement f) {
		// use CAS to check f - g = 0
		try {
			final StringBuilder diffSb = new StringBuilder();
			diffSb.append(getFormulaString(StringTemplate.defaultTemplate, true));
			diffSb.append("-(");
			diffSb.append(f.getFormulaString(StringTemplate.defaultTemplate, true));
			diffSb.append(")");
			final String diff = kernel.evaluateGeoGebraCAS(diffSb.toString());
			return (Double.valueOf(diff) == 0d);
		} catch (final Throwable e) {
			return false;
		}
	}

	/**
	 * String getFormulaString(int, boolean substituteNumbers) substituteNumbers
	 * determines (for a function) whether you want "2*x^2" or "a*x^2" returns a
	 * string representing the formula of the GeoElement in the following
	 * formats: getFormulaString(StringType.MathPiper) eg Sqrt(x)
	 * getFormulaString(StringType.LATEX) eg \sqrt(x)
	 * getFormulaString(StringType.GEOGEBRA) eg sqrt(x)
	 * getFormulaString(StringType.GEOGEBRA_XML)
	 * getFormulaString(StringType.JASYMCA)
	 * 
	 * @param ExpressionNodeType
	 * @param substituteNumbers
	 * @return formula string
	 */
	public String getFormulaString(final StringTemplate tpl,
			final boolean substituteNumbers) {

		String ret = "";

		// Functions override this, no need to care about them
		// only inequalities call this

		// matrices
		if (isGeoList() && tpl.hasType(StringType.LATEX)
				&& ((GeoList) this).isMatrix()) {
			ret = toLaTeXString(!substituteNumbers,tpl);
		}
		// vectors
		else if (isGeoVector() && tpl.hasType(StringType.LATEX)) {
			ret = toLaTeXString(!substituteNumbers,tpl);
		} // curves
		else if (isGeoCurveCartesian()
				&& tpl.hasType(StringType.LATEX)) {
			ret = toLaTeXString(!substituteNumbers,tpl);
		} else {
			ret = substituteNumbers ? toValueString(tpl) : getCommandDescription(tpl);
		}

		// GeoNumeric eg a=1
		if ("".equals(ret) && isGeoNumeric() && !substituteNumbers
				&& isLabelSet()) {
			ret = kernel.printVariableName(label,tpl);
		}
		if ("".equals(ret) && !isGeoText()) {
			// eg Text[ (1,2), false]
			ret = toOutputValueString(tpl);
		}

		/*
		 * we don't want to deal with list bracess in here since
		 * GeoList.toOutputValueString() takes care of it
		 */

		if (tpl.hasType(StringType.LATEX)) {
			if ("?".equals(ret)) {
				ret = app.getPlain("undefined");
			} else if ((Unicode.Infinity + "").equals(ret)) {
				ret = "\\infty";
			} else if ((Unicode.MinusInfinity + "").equals(ret)) {
				ret = "-\\infty";
			}
		}

		return ret;

	}

	public String getRealFormulaString(final StringTemplate tpl,
			final boolean substituteNumbers) {
		String ret = "";

		// matrices

		if (getParentAlgorithm() != null) {
			ret = getParentAlgorithm().getCommandDescription(tpl,true);
		}

		// GeoNumeric eg a=1
		if ("".equals(ret) && isGeoNumeric() && !substituteNumbers
				&& isLabelSet()) {
			ret = kernel.printVariableName(label,tpl);
		}
		if ("".equals(ret) && !isGeoText()) {
			// eg Text[ (1,2), false]
			ret = toOutputValueString(tpl);
		}

		return ret;

	}

	// ===================================================
	// G.Sturr 2010-5-14
	// New code for spreadsheet tracing with trace manager
	// ===================================================

	/** Spreadsheet tracing on/off flag */
	private boolean spreadsheetTrace;

	/** @return true if this geo is tracing to the spreadsheet */
	public boolean getSpreadsheetTrace() {
		return spreadsheetTrace;
	}

	/**
	 * Set tracing flag for this geo
	 * 
	 * @param traceFlag
	 */
	public void setSpreadsheetTrace(final boolean traceFlag) {

		if (traceFlag != true) {
			traceSettings = null;
		}
		spreadsheetTrace = traceFlag;
	}

	/**
	 * Request spreadsheet trace manager to auto-reset the tracing columns.
	 * Called after mouse_release.
	 */
	public void resetTraceColumns() {
		if (app.isUsingFullGui()) {
			app.resetTraceColumn(this);
		}
	}

	/** @return if geos of this type can be traced to the spreadsheet */
	public boolean isSpreadsheetTraceable() {
		return this instanceof SpreadsheetTraceable;
	}

	private geogebra.common.util.SpreadsheetTraceSettings traceSettings;

	public SpreadsheetTraceSettings getTraceSettings() {

		if (traceSettings == null) {
			traceSettings = new SpreadsheetTraceSettings();
		}

		return traceSettings;

	}

	public void setTraceSettings(final SpreadsheetTraceSettings t) {
		traceSettings = t;
	}

	/*
	 * over-ridden in GeoList
	 */
	public GeoElement getGeoElementForPropertiesDialog() {
		return this;
	}

	/*
	 * over-ridden in GeoText
	 */
	public boolean isTextCommand() {
		return false;
	}

	boolean inTree = false;

	final public boolean isInTree() {
		return inTree;
	}

	final public void setInTree(final boolean flag) {
		inTree = flag;
	}

	// JavaScript

	private String clickScript = "";
	private String updateScript = "";

	// This method is copied from AutoCompleteTextField
	private static boolean isLetterOrDigit(final char character) {
		switch (character) {
		case '_': // allow underscore as a valid letter in an autocompletion
					// word
			return true;

		default:
			return Character.isLetterOrDigit(character);
		}
	}

	/**
	 * This method should split a GeoGebra script into the following format: ""
	 * or "something"; "command"; "something"; "command"; "something"; ...
	 * 
	 * @param st
	 *            String GeoGebra script
	 * @return String [] the GeoGebra script split into and array
	 */
	private static String[] splitScriptByCommands(final String st) {

		StringBuilder retone = new StringBuilder();
		final ArrayList<String> ret = new ArrayList<String>();

		// as the other algorithms would be too complicated,
		// just go from the end of the string and advance character by character

		// at first count the number of "s to decide how to start the algorithm
		int countapo = 0;
		for (int j = 0; j < st.length(); j++) {
			if (st.charAt(j) == '"') {
				countapo++;
			}
		}

		boolean in_string = false;
		if ((countapo % 2) == 1) {
			in_string = true;
		}

		boolean before_bracket = false;
		boolean just_before_bracket = false;
		for (int i = st.length() - 1; i >= 0; i--) {
			if (in_string) {
				if (st.charAt(i) == '"') {
					in_string = false;
				}
			} else if (just_before_bracket) {
				if (isLetterOrDigit(st.charAt(i))) {
					ret.add(0, retone.toString());
					retone = new StringBuilder();
					just_before_bracket = false;
					before_bracket = true;
				} else if ((st.charAt(i) != '[') && (st.charAt(i) != ' ')) {
					just_before_bracket = false;
					before_bracket = false;
					if (st.charAt(i) == '"') {
						in_string = true;
					}
				}
			} else if (before_bracket) {
				if (!isLetterOrDigit(st.charAt(i))) {
					ret.add(0, retone.toString());
					retone = new StringBuilder();
					before_bracket = false;
					if (st.charAt(i) == '"') {
						in_string = true;
					} else if (st.charAt(i) == '[') {
						just_before_bracket = true;
					}
				}
			} else {
				if (st.charAt(i) == '"') {
					in_string = true;
				} else if (st.charAt(i) == '[') {
					just_before_bracket = true;
				}
			}
			retone.insert(0, st.charAt(i));
		}
		ret.add(0, retone.toString());
		if (before_bracket) {
			ret.add(0, "");
		}
		final String[] ex = { "" };
		return ret.toArray(ex);
	}

	private String script2LocalizedScript(final String st) {
		final String[] starr = splitScriptByCommands(st);
		final StringBuilder retone = new StringBuilder();
		for (int i = 0; i < starr.length; i++) {
			if ((i % 2) == 0) {
				retone.append(starr[i]);
			} else {
				retone.append(app.getCommand(starr[i]));
			}
		}
		return retone.toString();
	}

	private String localizedScript2Script(final String st) {
		final String[] starr = splitScriptByCommands(st);
		final StringBuilder retone = new StringBuilder();
		for (int i = 0; i < starr.length; i++) {
			if ((i % 2) == 0) {
				retone.append(starr[i]);
			} else {
				// allow English language command in French scripts
				if (app.getInternalCommand(starr[i]) != null) {
					retone.append(app.getInternalCommand(starr[i]));
				} else {
					// fallback for wrong call in English already
					// or if someone writes an English command into an
					// other language script
					retone.append(starr[i]);
				}
			}
		}
		return retone.toString();
	}
	
	private void setPythonEventHandler(ScriptType type, String evt, String code) {
		if (type == ScriptType.PYTHON) {
			app.getPythonBridge().setEventListener(this, evt, code);
		}		
	}

	public void setClickScript(final String script,
			final boolean translateInternal) {
		if (!canHaveClickScript()) {
			return;
		}
		// Application.debug(script);
		if (!clickGGBScript()) {
			if (app.getScriptingLanguage() == null) {
				app.setScriptingLanguage(app.getLanguage());
			}
			setPythonEventHandler(clickScriptType, "click", script);
			clickScript = script;
		} else {
			if (translateInternal) {
				clickScript = localizedScript2Script(script);
			} else {
				setPythonEventHandler(clickScriptType, "click", script);
				clickScript = script;
			}
		}
	}

	public void setUpdateScript(final String script,
			final boolean translateInternal) {
		if (!canHaveUpdateScript()) {
			return;
		}
		if (!updateGGBScript()) {
			if (app.getScriptingLanguage() == null) {
				app.setScriptingLanguage(app.getLanguage());
			}
			setPythonEventHandler(updateScriptType, "update", script);
			updateScript = script;
		} else {
			if (translateInternal) {
				updateScript = localizedScript2Script(script);
			} else {
				setPythonEventHandler(updateScriptType, "update", script);
				updateScript = script;
			}
		}
		app.initJavaScriptViewWithoutJavascript();
	}

	public boolean canHaveUpdateScript() {
		return true;
	}

	public String getUpdateScript() {
		if (updateGGBScript()) {
			return script2LocalizedScript(updateScript);
		}
		return updateScript;
	}

	public String getClickScript() {
		if (clickGGBScript()) {
			return script2LocalizedScript(clickScript);
		}
		return clickScript;
	}
	
	public String getScript(EventType type) {
		switch (type) {
		case CLICK:
			return getClickScript();
		case UPDATE:
			return getUpdateScript();
		}
		return "";
	}
	
	public String getXMLUpdateScript() {
		return StringUtil.encodeXML(updateScript);
	}

	public String getXMLClickScript() {
		return StringUtil.encodeXML(clickScript);
	}

	private void runGgbScript(final String arg, final boolean update) {

		final String ggbScript = update ? updateScript : clickScript;

		final AlgebraProcessor ab = kernel.getAlgebraProcessor();
		final String script[] = (arg == null) ? ggbScript.split("\n")
				: ggbScript.replaceAll("%0", arg).split("\n");

		boolean success = false;
		int i = -1;
		try {
			for (i = 0; i < script.length; i++) {
				final String command = script[i].trim();

				if (!command.equals("") && (command.charAt(0) != '#')) {
					// System.out.println(script[i]);
					ab.processAlgebraCommandNoExceptionHandling(command, false,
							false, true);
					success = true;
				}
			}
			// there have been no errors
			if (update) {
				app.setBlockUpdateScripts(false);
			}
		} catch (final Throwable e) {
			app.showError(app.getPlain("ErrorInScriptAtLineAFromObjectB",
					(i + 1) + "", getLabel()) + "\n" + e.getLocalizedMessage());
			success = false;
			if (update) {
				app.setBlockUpdateScripts(true);
			}
		}
		// storing undo info is expensive, so we don't want to do it on update
		if (success && !update) {
			app.storeUndoInfo();
		}
	}

	@SuppressWarnings("unused")
	private void runPythonScript(final String arg, final boolean update) {

		AbstractApplication.debug("running Python script: " + arg);
		app.evalPythonScript(app, update ? updateScript : clickScript, arg);
	}

	private void runJavaScript(final String arg, final boolean update) {
		// Possible TODO: make executing update scripts also possible via
		// browser
		try {
			if (app.isApplet() && app.useBrowserForJavaScript() && !update) {
				if (arg == null) {
					final Object[] args = {};
					app.callAppletJavaScript("ggb" + getLabel(StringTemplate.defaultTemplate), args);
				} else {
					final Object[] args = { arg };
					app.callAppletJavaScript("ggb" + getLabel(StringTemplate.defaultTemplate), args);
				}
			} else if (app.isHTML5Applet()) {
				String functionPrefix = update ? "ggbUpdate" : "ggb";
				if (arg == null) {
					final Object[] args = {};
					app.callAppletJavaScript(functionPrefix + getLabel(StringTemplate.defaultTemplate), args);
				} else {
					final Object[] args = { arg };
					app.callAppletJavaScript(functionPrefix + getLabel(StringTemplate.defaultTemplate), args);
				}
			}else {
				app.evalScript(app, update ? updateScript : clickScript, arg);
			}
			// there have been no errors
			if (update) {
				app.setBlockUpdateScripts(false);
			}
		} catch (final Exception e) {
			e.printStackTrace();
			app.showError(app.getPlain(update ? "OnUpdate" : "OnClick") + " "
					+ getLabel() + ":\n" + app.getPlain("ErrorInJavaScript")
					+ "\n" + e.getLocalizedMessage());
			if (update) {
				app.setBlockUpdateScripts(true);
			}
		}
	}

	public void runScripts(final String arg) {
		if (!canHaveClickScript() || (clickScript.length() == 0)
				|| app.isScriptingDisabled()) {
			return;
		}
		switch (clickScriptType) {
		case PYTHON:
			app.getPythonBridge().click(this);
			//runPythonScript(arg, false);
			break;
		case JAVASCRIPT:
			runJavaScript(arg, false);
			break;
		case GGBSCRIPT:
			runGgbScript(arg, false);
			break;
		}
	}

	public void runUpdateScripts() {
		if (!canHaveUpdateScript() || (updateScript.length() == 0)
				|| app.isBlockUpdateScripts() || app.isScriptingDisabled()) {
			return;
		}
		app.setBlockUpdateScripts(true);
		switch (updateScriptType) {
		case PYTHON:
			//runPythonScript(null, true);
			break;
		case JAVASCRIPT:
			runJavaScript(null, true);
			break;
		case GGBSCRIPT:
			runGgbScript(null, true);
			break;
		}
	}

	boolean showTrimmedIntersectionLines = false;

	public void setShowTrimmedIntersectionLines(final boolean show) {
		showTrimmedIntersectionLines = show;
	}

	public boolean getShowTrimmedIntersectionLines() {
		return showTrimmedIntersectionLines;
	}

	public boolean isPointInRegion() {
		return false;
	}

	public void setRandomGeo(final boolean flag) {
		isRandomGeo = flag;
	}

	private boolean isRandomGeo = false;

	public boolean isRandomGeo() {
		return isRandomGeo;
	}

	public void updateRandomGeo() {

		// update parent algorithm, like AlgoRandom
		final AlgoElement algo = getParentAlgorithm();
		if (algo != null) {
			algo.compute(); // eg AlgoRandom etc
		} else if (isGeoNumeric()) {
			((GeoNumeric) this).updateRandom();
		}
	}

	public boolean isMatrixTransformable() {
		return false;
	}

	// =============================================
	// Control which views are allowed to add a geo.
	// G.Sturr, 2010-6-30
	// =============================================

	public void setVisibility(final int viewId, final boolean setVisible) {
		if (setVisible) {
			if (!viewFlags.contains(viewId)) {
				viewFlags.add(viewId);
			}
		} else {
			viewFlags.remove(Integer.valueOf(viewId));
		}
	}

	public boolean isVisibleInView(final int viewId) {
		return viewFlags.contains(viewId);
	}

	// private Set<Integer> viewSet = new HashSet<Integer>();

	public void addView(final int viewId) {
		setVisibility(viewId, true);
		// viewSet.add(view);
	}

	public void removeView(final int viewId) {
		setVisibility(viewId, false);
		// viewSet.remove(view);
	}
	
	public void setViewFlags(List<Integer> flags){
		viewFlags.clear();
		viewFlags.addAll(flags);
		// Collections.copy(list, viewFlags);
	}

	// public boolean isVisibleInView(int view){
	// // if no views are set, add geo to both by default
	// if(viewSet.isEmpty()){
	// EuclidianViewInterface ev = app.getActiveEuclidianView();
	// viewSet.add(ev.getViewID());
	// // if ev isn't Graphics or Graphics 2, then also add 1st 2D euclidian
	// view
	// if (!(ev.isDefault2D()))
	// viewSet.add(Application.VIEW_EUCLIDIAN);
	//
	// }
	// return viewSet.contains(view);
	// }

	public List<Integer> getViewSet() {
		final List<Integer> list = new ArrayList<Integer>();
		list.addAll(viewFlags);
		// Collections.copy(list, viewFlags);
		return list;
	}

	/**
	 * 
	 * @return true if visible in 3D view
	 */
	public boolean isVisibleInView3D() {
		return hasDrawable3D()
				&& (isGeoElement3D() || isVisibleInView(AbstractApplication.VIEW_EUCLIDIAN));
	}

	// End G.Sturr
	// ===========================================

	public void setSelectionAllowed(final boolean selected2) {
		selectionAllowed = selected2;
	}

	public boolean isSelectionAllowed() {
		return selectionAllowed;
	}

	/**
	 * In case this geo is part of macro construction, it keeps its own label.
	 * To get correct output of Name[geo] we need to keep the label of the
	 * real-world geo represented by this formal geo.
	 * 
	 * @param realLabel
	 *            Label of the real geo represented by this one
	 */
	public void setRealLabel(final String realLabel) {
		this.realLabel = realLabel;
	}

	/**
	 * Used for Name command. See {@link #setRealLabel(String)}
	 * 
	 * @return label of this geo, or label of a real geo in case this one is
	 *         formal
	 */
	public String getRealLabel(StringTemplate tpl) {
		if ((realLabel == null) || realLabel.equals("")) {
			return getLabel(tpl);
		}
		return realLabel;
	}

	public boolean isHatchingEnabled() {
		return fillType == FILL_HATCH;
	}

	public void setHatchingAngle(final int angle) {
		hatchingAngle = angle;
	}

	public double getHatchingAngle() {
		return hatchingAngle;
	}

	public void setHatchingDistance(final int distance) {
		hatchingDistance = distance;
	}

	public int getHatchingDistance() {
		return hatchingDistance;
	}

	public BufferedImage getFillImage() {

		return graphicsadapter.getFillImage();
	}

	// public void setFillImage(BufferedImage image){
	// this.fillImage = image;
	// }

	public void setFillImage(final String filename) {
		graphicsadapter.setFillImage(filename);
	}

	public int getFillType() {
		return fillType;
	}

	public void setFillType(final int fillType) {
		this.fillType = fillType;
	}

	/**
	 * Tries to load the image using the given fileName.
	 * 
	 * @param fileName
	 */
	public void setImageFileName(final String fileName) {
		graphicsadapter.setImageFileName(fileName);
	}

	public String getImageFileName() {
		return graphicsadapter.getImageFileName();
	}

	/**
	 * @param inverseFill
	 *            the inverseFill to set
	 */
	public void setInverseFill(final boolean inverseFill) {
		this.inverseFill = inverseFill;
	}

	/**
	 * @return the inverseFill
	 */
	public boolean isInverseFill() {
		return inverseFill;
	}

	private Coords mainDirection = new Coords(0, 0, 1, 0);

	/**
	 * 
	 * @return "main" direction of the element, e.g. for seeing it in a
	 *         "standard" view (for 3D). E.g. orthogonal to a plane, along a
	 *         line, ...
	 */
	public Coords getMainDirection() {
		return mainDirection;
	}

	/**
	 * set the main direction
	 * 
	 * @param direction
	 */
	public void setMainDirection(final Coords direction) {
		mainDirection = direction;
	}

	/**
	 * gets shortest distance to point p overridden in eg GeoPoint, GeoLine for
	 * compound paths
	 * @param p
	 */
	public double distance(final GeoPoint2 p) {
		return Double.POSITIVE_INFINITY;
	}

	public double distance(final GeoPointND p) {
		if ((p instanceof GeoElement) && (p instanceof GeoPoint2)) {
			return distance((GeoPoint2) p);
		}
		AbstractApplication.debug("TODO : distance from " + getClassName()
				+ " to ND point");
		return Double.POSITIVE_INFINITY;
	}

	public boolean canHaveClickScript() {
		return true;
	}

	// /////////////////////////////
	// 3D
	// /////////////////////////////

	/** says if it's a pickable object */
	private boolean isPickable = true;
	private boolean needsReplacingInExpressionNode = false;

	/**
	 * sets the pickability of the object
	 * 
	 * @param v
	 *            pickability
	 */
	public void setIsPickable(final boolean v) {
		isPickable = v;
	}

	/**
	 * says if the object is pickable
	 * 
	 * @return true if the object is pickable
	 */
	public boolean isPickable() {
		return isPickable && isSelectionAllowed();
	}

	public boolean needsReplacingInExpressionNode() {
		return needsReplacingInExpressionNode;
	}

	public void setNeedsReplacingInExpressionNode() {
		needsReplacingInExpressionNode = true;
	}

	public boolean isGeoInterval() {
		return false;
	}

	public double getMeasure() {
		return 0;
	}

	/**
	 * Removes dependencies (conditional visibility, min, max, corner, EV
	 * bounds) from oldgeo and moves them to this
	 * 
	 * @param oldGeo
	 *            geo whose dependencies should be moved
	 */
	public void moveDependencies(final GeoElement oldGeo) {
		// in general case do nothing; overriden in GeoPoint, GeoNumeric and
		// GeoBoolean
	}

	private Stack<GeoElement> tempClone;
	private boolean cloneInUse = false;

	public void storeClone() {
		if (tempClone == null) {
			tempClone = new Stack<GeoElement>();
		}

		tempClone.push(copy());
		cloneInUse = true;
	}

	public void recoverFromClone() {
		if (tempClone != null) {
			set(tempClone.pop());
		}
		cloneInUse = false;
	}

	public void randomizeForProbabilisticChecking() {
		// overode by subclasses
		return;
	}

	public boolean isRandomizable() {
		return false;
	}

	/**
	 * Returns corresponding GeoCasCell. See GeoCasCell.setTwinGeo().
	 * 
	 * @return twin GeoElement
	 */
	final public GeoCasCell getCorrespondingCasCell() {
		return correspondingCasCell;
	}

	/**
	 * Sets corresponding GeoCasCell for this GeoElement. See
	 * GeoCasCell.getTwinGeo().
	 */
	final public void setCorrespondingCasCell(
			final GeoCasCell correspondingCasCell) {
		this.correspondingCasCell = correspondingCasCell;
	}

	/**
	 * Test method that returns true if the given GeoElement geo is to be drawn
	 * with LaTeX rendering in the spreadsheet or AV. For performance reasons
	 * LaTeX is to be avoided when not needed.
	 * 
	 * @return
	 */
	public boolean isLaTeXDrawableGeo(@SuppressWarnings("unused") final String latexStr) {
		// TODO Does not what the comment says
		return false;
	}

	/*
	 * used in eg CAS and Spreadsheet Views to decide if the LaTeX renderer is
	 * neccesary by checking for eg ^2, \frac
	 */
	public static boolean isLaTeXneeded(String latex2) {
		String latex = latex2;
		// Unicode is fine to render these:
		latex = latex.replace("\\leq", "");
		latex = latex.replace("\\geq", "");

		for (int i = 0; i < latex.length(); i++) {
			final char ch = latex.charAt(i);
			switch (ch) {
			case '\\':
				final char ch1 = i == (latex.length() - 1) ? ' ' : latex
						.charAt(i + 1);
				if ((ch1 != ';') && (ch1 != ',')) {
					return true; // \; \, just spacing
				}
				break;
			case '^':
				return true;
			}
		}

		// no real latex string
		return false;
	}

	public boolean hasBackgroundColor() {
		return false;
	}

	public boolean algoUpdateSetContains(final AlgoElementInterface i) {
		return getAlgoUpdateSet().contains(i);
	}

	/*
	 * for the SpreadsheetTraceable interface. Default: just return the label
	 */
	public ArrayList<String> getColumnHeadings() {
		if (spreadsheetColumnHeadings == null) {
			spreadsheetColumnHeadings = new ArrayList<String>();
		}

		spreadsheetColumnHeadings.clear();
		spreadsheetColumnHeadings.add(getLabel(StringTemplate.defaultTemplate));

		return spreadsheetColumnHeadings;
	}

	/*
	 * default for elements implementing NumberValue interface eg GeoSegment,
	 * GeoPolygon
	 */
	public ArrayList<GeoNumeric> getSpreadsheetTraceList() {

		if (isNumberValue()) {

			if (spreadsheetTraceList == null) {
				spreadsheetTraceList = new ArrayList<GeoNumeric>();
				final GeoNumeric xx = new GeoNumeric(cons,
						((NumberValue) this).getDouble());
				spreadsheetTraceList.add(xx);
			} else {
				spreadsheetTraceList.get(0).setValue(
						((NumberValue) this).getDouble());
			}

		} else {
			AbstractApplication
					.debug("error in getSpreadsheetTraceList(), not a NumberValue");
			return null;
		}

		return spreadsheetTraceList;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return label;
	}

}
