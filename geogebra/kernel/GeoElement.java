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

package geogebra.kernel;

import geogebra.cas.CASgeneric;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.gui.view.spreadsheet.TraceSettings;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.plugin.CallJavaScript;
import geogebra.util.GeoLaTeXCache;
import geogebra.util.ImageManager;
import geogebra.util.Unicode;
import geogebra.util.Util;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author  Markus
 * @version
 */
public abstract class GeoElement
	extends ConstructionElement
	implements ExpressionValue {

	public boolean isVector3DValue() {
			return false;
	}
	/**
	 * @return the updateJavaScript
	 */
	public boolean isUpdateJavaScript() {
		return updateJavaScript;
	}

	/**
	 * @param updateJavaScript the updateJavaScript to set
	 */
	public void setUpdateJavaScript(boolean updateJavaScript) {
		this.updateJavaScript = updateJavaScript;
	}

	/**
	 * @return the clickJavaScript
	 */
	public boolean isClickJavaScript() {
		return clickJavaScript;
	}

	/**
	 * @param clickJavaScript the clickJavaScript to set
	 */
	public void setClickJavaScript(boolean clickJavaScript) {
		this.clickJavaScript = clickJavaScript;
	}

	/** min decimals or significant figures to use in editing string */
	public static final int MIN_EDITING_PRINT_PRECISION = 5;

	// maximum label offset distance
	private static final int MAX_LABEL_OFFSET = 80;

	// private static int geoElementID = Integer.MIN_VALUE;

	private static final char[] complexLabels =
	{ 'z', 'w' };

	private static final char[] pointLabels =
		{
			'A',
			'B',
			'C',
			'D',
			'E',
			'F',
			'G',
			'H',
			'I',
			'J',
			'K',
			'L',
			'M',
			'N',
			'O',
			'P',
			'Q',
			'R',
			'S',
			'T',
			'U',
			'V',
			'W',
			'Z' };

	private static final char[] functionLabels =
	{
		'f',
		'g',
		'h',
		'p',
		'q',
		'r',
		's',
		't'
	};

	private static final char[] lineLabels =
		{
			'a',
			'b',
			'c',
			'd',
			'e',
			'f',
			'g',
			'h',
			'i',
			'j',
			'k',
			'l',
			'm',
			'n',
			'p',
			'q',
			'r',
			's',
			't' };

	private static final char[] vectorLabels =
		{
			'u',
			'v',
			'w',
			'z',
			'a',
			'b',
			'c',
			'd',
			'e',
			'f',
			'g',
			'h',
			'i',
			'j',
			'k',
			'l',
			'm',
			'n',
			'p',
			'q',
			'r',
			's',
			't' };

	private static final char[] conicLabels =
		{ 'c', 'd', 'e', 'f', 'g', 'h', 'k', 'p', 'q', 'r', 's', 't' };

	private static final char[] lowerCaseLabels =
	{
		'a',
		'b',
		'c',
		'd',
		'e',
		'f',
		'g',
		'h',
		'i',
		'j',
		'k',
		'l',
		'm',
		'n',
		'o',
		'p',
		'q',
		'r',
		's',
		't',
		'u',
		'v',
		'w',
		'z'
		};

	private static final char[] integerLabels =
	{
		'n',
		'i',
		'j',
		'k',
		'l',
		'm',
		};

	private static final char[] greekLowerCase =
		{
			'\u03b1',
			'\u03b2',
			'\u03b3',
			'\u03b4',
			'\u03b5',
			'\u03b6',
			'\u03b7',
			'\u03b8',
			'\u03b9',
			'\u03ba',
			'\u03bb',
			'\u03bc',
			'\u03bd',
			'\u03be',
			'\u03bf',
			'\u03c1',
			'\u03c3',
			'\u03c4',
			'\u03c5',
			'\u03d5',
			'\u03c7',
			'\u03c8',
			'\u03c9' };

	private static final char[] arabic =
	{'\u0623',
		'\u0628',
		'\u062a',
		'\u062b',
		'\u062c',
		'\u062d',
		'\u062e',
		'\u062f',
		'\u0630',
		'\u0631',
		'\u0632',
		'\u0633',
		'\u0634',
		'\u0635',
		'\u0636',
		'\u0637',
		'\u0638',
		'\u0639',
		'\u063a',
		'\u0641',
		'\u0642',
		'\u0643',
		'\u0644',
		'\u0645',
		'\u0646',
		'\u0647', // needs this too '\u0640' (see later on)
		'\u0648',
		'\u064a'};

	private static final char[] greekUpperCase =
	{ // Michael Borcherds 2008-02-23
	'\u0391',
	'\u0392',
	'\u0393',
	'\u0394',
	'\u0395',
	'\u0396',
	'\u0397',
	'\u0398',
	'\u0399',
	'\u039a',
	'\u039b',
	'\u039c',
	'\u039d',
	'\u039e',
	'\u039f',
	'\u03a0',
	'\u03a1',
	'\u03a3',
	'\u03a4',
	'\u03a5',
	'\u03a6',
	'\u03a7',
	'\u03a8',
	'\u03a9'};


	// GeoElement types
	public static final int GEO_CLASS_ANGLE = 1;
	public static final int GEO_CLASS_AXIS = 2;
	public static final int GEO_CLASS_BOOLEAN = 3;
	public static final int GEO_CLASS_BUTTON = 4;
	public static final int GEO_CLASS_TEXTFIELD = 5;
	public static final int GEO_CLASS_CONIC = 6;
	public static final int GEO_CLASS_CONICPART = 7;
	public static final int GEO_CLASS_FUNCTION = 8;
	public static final int GEO_CLASS_INTERVAL = 9;
	public static final int GEO_CLASS_FUNCTIONCONDITIONAL = 10;
	public static final int GEO_CLASS_IMAGE = 11;
	public static final int GEO_CLASS_LINE = 12;
	public static final int GEO_CLASS_LIST = 13;
	public static final int GEO_CLASS_LOCUS = 14;
	public static final int GEO_CLASS_NUMERIC = 15;
	public static final int GEO_CLASS_POINT = 16;
	public static final int GEO_CLASS_POLYGON = 17;
	public static final int GEO_CLASS_RAY = 18;
	public static final int GEO_CLASS_SEGMENT = 19;
	public static final int GEO_CLASS_TEXT = 20;
	public static final int GEO_CLASS_VECTOR = 21;
	public static final int GEO_CLASS_CURVE_CARTESIAN = 22;
	public static final int GEO_CLASS_CURVE_POLAR = 23;
	public static final int GEO_CLASS_IMPLICIT_POLY = 24;
	public static final int GEO_CLASS_FUNCTION_NVAR = 25;
	public static final int GEO_CLASS_POLYLINE = 26;
	public static final int GEO_CLASS_LINEAR_INEQUALITY = 27;
	
	// Call cell type
	public static final int GEO_CLASS_CAS_CELL = 1001;

	// 3D types
	public static final int GEO_CLASS_POINT3D = 3010;
	public static final int GEO_CLASS_VECTOR3D = 3011;
	public static final int GEO_CLASS_SEGMENT3D = 3110;
	public static final int GEO_CLASS_LINE3D = 3120;
	public static final int GEO_CLASS_RAY3D = 3121;
	public static final int GEO_CLASS_CONIC3D = 3122;
	public static final int GEO_CLASS_AXIS3D = 3123;
	public static final int GEO_CLASS_CURVECARTESIAN3D = 3124;

	public static final int GEO_CLASS_POLYGON3D = 3211;
	public static final int GEO_CLASS_PLANE3D = 3220;
	public static final int GEO_CLASS_QUADRIC = 3230;
	public static final int GEO_CLASS_QUADRIC_PART = 3231;
	public static final int GEO_CLASS_QUADRIC_LIMITED = 3232;
	public static final int GEO_CLASS_POLYLINE3D = 3260;
	public static final int GEO_CLASS_POLYHEDRON = 3310;
	
	public static final int GEO_CLASS_SURFACECARTESIAN3D = 3320;






	public static final int LABEL_NAME = 0;
	public static final int LABEL_NAME_VALUE = 1;
	public static final int LABEL_VALUE = 2;
	public static final int LABEL_CAPTION = 3; // Michael Borcherds 2008-02-18

	public static final int TOOLTIP_ALGEBRAVIEW_SHOWING = 	0;
	public static final int TOOLTIP_ON = 					1;
	public static final int TOOLTIP_OFF = 					2;
	public static final int TOOLTIP_CAPTION = 				3;
	public static final int TOOLTIP_NEXTCELL = 				4;
	private int tooltipMode = TOOLTIP_ALGEBRAVIEW_SHOWING;

	public String label; // should only be used directly in subclasses
	private String realLabel; //for macro constructions, see setRealLabel() for details
	private String oldLabel; // see doRenameLabel
	private String caption;
	boolean labelWanted = false, labelSet = false, localVarLabelSet = false;
	private boolean euclidianVisible = true;
	private boolean forceEuclidianVisible = false;
	protected boolean algebraVisible = true;
	private boolean labelVisible = true;
	private boolean isConsProtBreakpoint; // in construction protocol
	private boolean isAlgoMacroOutput; // is an output object of a macro construction
	protected boolean fixed = false;
	public int labelMode = LABEL_NAME;
	public int toStringMode = Kernel.COORD_CARTESIAN; // cartesian or polar
	protected Color objColor = Color.black;
	protected Color bgColor = null; // none by default
	protected Color selColor = objColor;
	protected Color labelColor = objColor;
	protected Color fillColor = objColor;
	public int layer=0; 	// Michael Borcherds 2008-02-23
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
	private int hatchingAngle = 45; // in degrees
	private int hatchingDistance = 10;
	private boolean inverseFill = false;

	//=================================
	// G.Sturr new fill options
	protected String imageFileName = "";
	protected BufferedImage image;
	public static final int FILL_STANDARD = 0;
	public static final int FILL_HATCH = 1;
	public static final int FILL_IMAGE = 2;
	protected int fillType = FILL_STANDARD;
	public  int numberOfFillTypes = 3;

	//=================================

	public final static int COLORSPACE_RGB = 0;
	public final static int COLORSPACE_HSB = 1;
	public final static int COLORSPACE_HSL = 2;
	private int colorSpace = COLORSPACE_RGB;
	
	private List<Integer> viewFlags;
	
	public int getColorSpace() {
		return colorSpace;
	}

	public void setColorSpace(int colorSpace) {
		this.colorSpace = colorSpace;
	}
	
	private int defaultGeoType = -1;
	
	public int getDefaultGeoType() {
		return defaultGeoType;
	}
	
	public void setDefaultGeoType(int defaultGT) {
		defaultGeoType = defaultGT;
	}

	public int labelOffsetX = 0, labelOffsetY = 0;
	private boolean auxiliaryObject = false;
	private boolean selectionAllowed = true;
	// on change: see setVisualValues()

	// spreadsheet specific properties
	private Point spreadsheetCoords, oldSpreadsheetCoords;
	// number of AlgoCellRange using this cell: don't allow renaming when greater 0
	private int cellRangeUsers = 0; 
	// number of AlgoDependentCasCell using this cell: send updates to CAS
	private int casAlgoUsers = 0; 
	
	// condition to show object
	protected GeoBoolean condShowObject;

	// function to determine color
	private GeoList colFunction; // { GeoNumeric red, GeoNumeric Green, GeoNumeric Blue }

	private boolean useVisualDefaults = true;
	protected boolean isColorSet = false;
	protected boolean highlighted = false;
	private boolean selected = false;
	private String strAlgebraDescription, strAlgebraDescTextOrHTML, strAlgebraDescriptionHTML,
		strLabelTextOrHTML, strCaptionDescriptionHTML;

	protected String strLaTeX;
	private boolean strAlgebraDescriptionNeedsUpdate = true;
	private boolean strAlgebraDescTextOrHTMLneedsUpdate = true;
	private boolean strAlgebraDescriptionHTMLneedsUpdate = true;
	private boolean strLabelTextOrHTMLUpdate = true;
	private boolean strCaptionDescriptionHTMLneedsUpdate = true;
	protected boolean strLaTeXneedsUpdate = true;

	// line thickness and line type: s
	/** note: line thickness in Drawable is calculated as lineThickness / 2.0f*/
	public int lineThickness = EuclidianView.DEFAULT_LINE_THICKNESS;
	/** line type (full, dashed, ...) see EuclidianView.LINE_TYPE*/
	public int lineType = EuclidianView.DEFAULT_LINE_TYPE;
	/** line type for hidden parts (for 3D) */
	public int lineTypeHidden = EuclidianView.DEFAULT_LINE_TYPE_HIDDEN;

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
//	 Michael Borcherds 2007-10-06
	// angle decorations
	public static final int DECORATION_ANGLE_TWO_ARCS = 1;
	public static final int DECORATION_ANGLE_THREE_ARCS = 2;
	public static final int DECORATION_ANGLE_ONE_TICK = 3;
	public static final int DECORATION_ANGLE_TWO_TICKS = 4;
	public static final int DECORATION_ANGLE_THREE_TICKS = 5;
//	 Michael Borcherds START 2007-11-19
	public static final int DECORATION_ANGLE_ARROW_ANTICLOCKWISE = 6; //	 Michael Borcherds 2007-10-22
	public static final int DECORATION_ANGLE_ARROW_CLOCKWISE = 7; //	 Michael Borcherds 2007-10-22
//	 Michael Borcherds END 2007-11-19

	// public int geoID;
	//  static private int geoCounter = 0;
	/** parent algorithm */
	protected AlgoElement algoParent = null;
	/** draw algorithm */
	protected AlgoElement algoDraw = null;
	private ArrayList<AlgoElement> algorithmList; 	// directly dependent algos

	/**	set of all dependent algos sorted in topological order */
	protected AlgorithmSet algoUpdateSet;

	/********************************************************/

	/** Creates new GeoElement for given construction
	 * @param c Construction
	 */
	public GeoElement(Construction c) {
		super(c);
		
		// this.geoID = geoCounter++;
		
		// moved to subclasses, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		//setConstructionDefaults(); // init visual settings

		// new elements become breakpoints if only breakpoints are shown
		//isConsProtBreakpoint = cons.showOnlyBreakpoints();

		// ensure all new objects are in the top layer
		Application app = c.getApplication();
		if (app != null) {
			EuclidianView ev = app.getEuclidianView();
			if (ev != null)
				layer = ev.getMaxLayerUsed();
		}
		
		viewFlags=new ArrayList<Integer>();
		EuclidianViewInterface ev;
		if (app!=null && (ev = app.getActiveEuclidianView())!=null){
			viewFlags.add(ev.getViewID());
			// if ev isn't Graphics or Graphics 2, then also add 1st 2D euclidian view
			if (!(ev.isDefault2D()))
				viewFlags.add(Application.VIEW_EUCLIDIAN);
		}else{
			viewFlags.add(Application.VIEW_EUCLIDIAN);
		}
	}

	/* *******************************************************/

	/**
	 * We may need a simple method to get the label, as in the CopyPaste class.
	 * @return label if it is set
	 */
	public String getLabelSimple() {
		return label;
	}
	
	/**
	 * We may need a simple method to set the label, as in the CopyPaste class.
	 * @param lab the label to set
	 */
	public void setLabelSimple(String lab) {
		label = lab;
	}

	/**
	 * Returns label of GeoElement. If the label is null then
	 * algoParent.getCommandDescription() or  toValueString() is returned.
	 * @return geo's label if set, command description otherwise
	 */
	public String getLabel() {
		if (!labelSet && !localVarLabelSet) {
			if (algoParent == null)
				return toOutputValueString();
			else
				return algoParent.getCommandDescription();
		} else {
			return kernel.printVariableName(label);
		}
	}

	public void copyLabel(GeoElement c) {
		label = c.label;
	}

	/**
	 * Switch label mode among value, name, value+name and caption
	 * @param mode LABEL_ mode
	 */
	public void setLabelMode(int mode) {
		switch (mode) {
			case LABEL_NAME_VALUE :
				labelMode = LABEL_NAME_VALUE;
				break;

			case LABEL_VALUE :
				labelMode = LABEL_VALUE;
				break;

			case LABEL_CAPTION : // Michael Borcherds 2008-02-18
				labelMode = LABEL_CAPTION;
				break;

			default :
				labelMode = LABEL_NAME;
		}
	}

	/**
	 * Returns how  should label look like in Euclidian view
	 * @return label mode (name, value, name + value, caption)
	 */
	public int getLabelMode() {
		return labelMode;
	}

	/**
	 * return the label position (2D or 3D vector)
	 * @return the label position (2D or 3D vector)
	 */
	public Coords getLabelPosition(){
		return new Coords(0, 0, 0, 1);
	}

	/**
	 * Returns the GEO_CLASS_ type integer
	 * @return GEO_CLASS_ type integer
	 */
	public abstract int getGeoClassType();

	/**
	 * every subclass implements it's own copy method
	 *  this is needed for assignment copies like:
	 *  a = 2.7
	 *  b = a   (here copy() is needed)
	 *  @return copy of current element
	 * */
	public abstract GeoElement copy();

	/**
	 * This method always returns a GeoElement of the
	 * SAME CLASS as this GeoElement. Furthermore the resulting geo
	 * is in construction cons.
	 * @param cons construction
	 * @return copy in given construction
	 */
	public GeoElement copyInternal(Construction cons) {
		// default implementation: changed in some subclasses
		GeoElement geoCopy = copy();
		geoCopy.setConstruction(cons);
		return geoCopy;
	}

	/**
	 * Copies the given points array. The resulting points are part of the given construction.
	 * @param cons
	 * @param points
	 * @return copy of points in construction cons
	 */
	public static GeoPoint [] copyPoints(Construction cons, GeoPointND [] points) {
		GeoPoint [] pointsCopy = new GeoPoint[points.length];
		for (int i=0; i < points.length; i++) {
			pointsCopy[i] = (GeoPoint) ((GeoPoint) points[i]).copyInternal(cons);
			pointsCopy[i].set((GeoElement) points[i]);
		}
		return pointsCopy;
	}

	/**
	 * Copies the given points array. The resulting points are part of the given construction.
	 * @param cons
	 * @param points
	 * @return copy of points in construction cons
	 */
	public static GeoPointND [] copyPointsND(
			Construction cons,
			GeoPointND [] points) {
		GeoPointND [] pointsCopy = new GeoPointND[points.length];
		for (int i=0; i < points.length; i++) {
			pointsCopy[i] = (GeoPointND) ((GeoElement) points[i]).copyInternal(cons);
			((GeoElement) pointsCopy[i]).set((GeoElement) points[i]);
		}

		return pointsCopy;
	}

	/**
	 * Copies the given segments array. The resulting segments are part of the given construction.
	 *
	public static GeoSegment [] copySegments(Construction cons, GeoSegment [] segments) {
		GeoSegment [] segmentsCopy = new GeoSegment[segments.length];
		for (int i=0; i < segments.length; i++) {
			segmentsCopy[i] = (GeoSegment) segments[i].copyInternal(cons);

		}
		return segmentsCopy;
	}*/


	public ExpressionValue deepCopy(Kernel kernel) {
		//default implementation: changed in some subclasses
		return copy();
	}

	public void resolveVariables() {
    }

	public boolean isInfinite() {
		return false;
	}

	/** every subclass implements it's own set method
	 * @param geo geo to copy */
	public abstract void set(GeoElement geo);

	public abstract boolean isDefined();
	public abstract void setUndefined();
	public abstract String toValueString();

	private EuclidianViewInterface viewForValueString;

	/**
	 * sets a view for building the value string
	 * @param view
	 */
	public void setViewForValueString(EuclidianViewInterface view){
		viewForValueString = view;
	}

	/**
	 *
	 * @return the view used for building the value string
	 */
	public EuclidianViewInterface getViewForValueString(){
		return viewForValueString;
	}

	/**
	 *
	 * @return true if the value string can be changed regarding a view
	 */
	public boolean hasValueStringChangeableRegardingView(){
		return false;
	}

	/**
	 * Returns definition or value string of this object.
	 * Automatically increases decimals to at least 5, e.g.
	 *  FractionText[4/3] -> FractionText[1.333333333333333]
	 * @param useChangeable if false, point on path is ignored
	 * @param useOutputValueString  if true, use outputValueString rather than valueString
	 * @return definition or value string of this object
	 */
	public String getRedefineString(boolean useChangeable, boolean useOutputValueString) {
		boolean increasePrecision = kernel.ensureTemporaryPrintAccuracy(MIN_EDITING_PRINT_PRECISION);

		String ret = null;
		boolean isIndependent = !isPointOnPath() && useChangeable ? isChangeable() : isIndependent();
		if (isIndependent) {
			ret = useOutputValueString ? toOutputValueString() : toValueString();
		} else {
			ret = getCommandDescription();
		}

		if (increasePrecision)
			kernel.restorePrintAccuracy();
		return ret;
	}
	/**
	 * Returns the character which is used between label and definition
	 * @return : for conics, implicit polynomials and inequalities, = otherwise
	 */
	protected char getLabelDelimiter(){
		return '=';
	}
	/**
	 * Returns the definition of this GeoElement for the
	 * input field, e.g. A1 = 5, B1 = A1 + 2
	 * @return definition for input field
	 */
	public String getDefinitionForInputBar() {
    	// for expressions like "3 = 2 A2 - A1"
    	// getAlgebraDescription() returns "3 = 5"
    	// so we need to use getCommandDescription() in those cases
		boolean increasePrecision = kernel.ensureTemporaryPrintAccuracy(MIN_EDITING_PRINT_PRECISION);

    	String inputBarStr = getCommandDescription();
    	if (!inputBarStr.equals("")) {

    		// check needed for eg f(x) = g(x) + h(x), f(x) = sin(x)
    		char delimiter = getLabelDelimiter();
    		if (inputBarStr.indexOf(delimiter) < 0){
    			inputBarStr = getLabel() + (delimiter=='='?" =":delimiter) + " "+inputBarStr;
    		}
    	} else {
    		inputBarStr = getAlgebraDescription();
    	}

    	if (increasePrecision)
			kernel.restorePrintAccuracy();

		return inputBarStr;
	}

	/**
	 * Returns the value of this GeoElement for the
	 * input field, e.g. A1 = 5, B1 = A1 + 2
	 * @return value for input field
	 */
	public String getValueForInputBar() {
		boolean increasePrecision = kernel.ensureTemporaryPrintAccuracy(MIN_EDITING_PRINT_PRECISION);

		// copy into text field
		String ret = toOutputValueString();

		if (increasePrecision)
			kernel.restorePrintAccuracy();

		return ret;
	}

	/**
	 * Sets this object to zero (number = 0, points = (0,0), etc.)
	 */
	public void setZero() {

	}

	/**
	 * Returns a value string that is saveable in an XML file.
	 * Note: this is needed for texts that need to be quoted
	 * in lists and as command arguments.
	 */
	public String toOutputValueString() {
		if (isLocalVariable())
			return label;
		else
			return toValueString();
	}

	public void setConstructionDefaults() {
		if (useVisualDefaults) {
			ConstructionDefaults consDef = cons.getConstructionDefaults();
			if (consDef != null) {
				consDef.setDefaultVisualStyles(this, false);
			}
		}
	}

	public void setObjColor(Color color) {
		isColorSet = true;

		objColor = color;
		labelColor = color;
		fillColor = color;
		setAlphaValue(alphaValue);

		//selColor = getInverseColor(objColor);
		selColor =
			new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
	}
	/**
	 * Returns true if color was explicitly set
	 * @return true if color was explicitly set
	 */
	public boolean isColorSet() {
		return isColorSet;
	}

	// Michael Borcherds 2008-04-02
	private Color getRGBFromList(float alpha2)
	{
		if (alpha2 > 1f) alpha2 = 1f;
		if (alpha2 < 0f) alpha2 = 0f;

		int alpha = (int)(alpha2*255f);
		return getRGBFromList(alpha);
	}

	// Michael Borcherds 2008-04-02
	private Color getRGBFromList(int alpha)	{
		if (alpha > 255) alpha = 255;
		else if (alpha < 0) alpha = 0;

		// get rgb values from color list
		double redD = 0, greenD = 0, blueD = 0;
		for (int i=0; i < 3; i++) {
			GeoElement geo = colFunction.get(i);
			if (geo.isDefined()) {
				double val = ((NumberValue) geo).getDouble();
				switch (i) {
					case 0: redD = val; break;
					case 1: greenD = val; break;
					case 2: blueD = val; break;
				}
			}
		}

		//double epsilon = 0.000001; // 1 - floor(1) = 0 but we want 1.

		// make sure the colors are between 0 and 1
		redD = redD/2 - Math.floor(redD/2);
		greenD = greenD/2 - Math.floor(greenD/2);
		blueD = blueD/2 - Math.floor(blueD/2);


		// step function so
		// [0,1] -> [0,1]
		// [1,2] -> [1,0]
		// [2,3] -> [0,1]
		// [3,4] -> [1,0]
		// [4,5] -> [0,1] etc
		if (redD>0.5) redD=2*(1-redD); else redD=2*redD;
		if (greenD>0.5) greenD=2*(1-greenD); else greenD=2*greenD;
		if (blueD>0.5) blueD=2*(1-blueD); else blueD=2*blueD;

		//Application.debug("red"+redD+"green"+greenD+"blue"+blueD);


		// adjust color triple to alternate color spaces, default to RGB
		switch(colorSpace){

		case GeoElement.COLORSPACE_HSB:

			int rgb = Color.HSBtoRGB((float)redD, (float)greenD, (float)blueD);
			redD = (rgb>>16)&0xFF;
			greenD = (rgb>>8)&0xFF;
			blueD = rgb&0xFF;
			return new Color((int)redD, (int)greenD, (int)blueD, alpha);

		case GeoElement.COLORSPACE_HSL:

			// algorithm taken from wikipedia article:
			// http://en.wikipedia.org/wiki/HSL_and_HSV

			double H = redD *6;
			double S = greenD;
			double L = blueD;

			double C = (1 - Math.abs(2*L-1))*S;
			double X = C*(1- Math.abs(H % 2 - 1));

			double R1 = 0, G1 = 0, B1 = 0;

			if(H < 1){
				R1 = C; G1 = X; B1 = 0;
			}else if(H < 2){
				R1 = X; G1 = C; B1 = 0;
			}else if(H < 3){
				R1 = 0; G1 = C; B1 = X;
			}else if(H < 4){
				R1 = 0; G1 = X; B1 = C;
			}else if(H < 5){
				R1 = X; G1 = 0; B1 = C;
			}else if(H < 6){
				R1 = C; G1 = 0; B1 = X;
			}

			double m = L-.5*C;

			Color c = new Color((int)((R1+m)*255.0), (int)((G1+m)*255.0), (int)((B1+m)*255.0), alpha);

			if(c == null){
				Application.printStacktrace("error converting HSL to RGB");
			}
			return c;


		case GeoElement.COLORSPACE_RGB:
		default:
			return new Color((int)(redD*255.0), (int)(greenD*255.0), (int)(blueD*255.0), alpha);

		}

	}

	// Michael Borcherds 2008-04-02
	public Color getSelColor() {
		if (colFunction == null) return selColor;
		//else return RGBtoColor((int)colFunction.getValue(),100);
		else return getRGBFromList(100);
	}

	// Michael Borcherds 2008-04-02
	public Color getFillColor() {
		if (colFunction == null) return fillColor;
		//else return RGBtoColor((int)colFunction.getValue(),alphaValue);
		else return getRGBFromList(getAlphaValue());
	}

	/**
	 * return black if the color is white, so it can be seen
	 * @return color for algebra view (same as label or black)
	 */
	public Color getAlgebraColor() {
		Color col = getLabelColor();
		return col.equals(Color.white) ? Color.black : col;
	}


	// Michael Borcherds 2008-04-01
	public Color getLabelColor() {
		if (colFunction == null) return labelColor;
		else return getObjectColor();
	}

	// Michael Borcherds 2008-04-01
	public void setLabelColor(Color color) {
		labelColor = color;
	}

	public Color getBackgroundColor() {
		return bgColor;
	}

	public void setBackgroundColor(Color bgCol) {
		bgColor = bgCol;
	}

	// Michael Borcherds 2008-04-02
	public Color getObjectColor() {
		Color col = objColor;

		try {
			if (colFunction != null)
				col = getRGBFromList(255);
		}
		catch (Exception e) {
			removeColorFunction();
		}

		return col;
	}

	// Michael Borcherds 2008-03-01
	/**
	 * Sets layer
	 * @param layer layer from 0 to 9
	 */
	public void setLayer(int layer){
		
		//Application.printStacktrace("layer="+layer);
		
		if (layer == this.layer
				// layer valid only for Drawable objects
				// DON'T check this: eg angles on file load are not yet isDrawable()
				//	|| !isDrawable()
			) return;
		if (layer > EuclidianView.MAX_LAYERS) layer = EuclidianView.MAX_LAYERS;
		else if (layer < 0) layer = 0;
	
		EuclidianViewInterface ev = app.getActiveEuclidianView();//app.getEuclidianView();
		if (ev != null)
			ev.changeLayer(this,this.layer,layer);
		
		this.layer=layer;
	}

	// Michael Borcherds 2008-02-23
	/**
	 * @return layer of this geo (0 to 9)
	 */
	public int getLayer(){
		return layer;
	}

	// Michael Borcherds 2008-02-23
	public long getDrawingPriority()
	{

		long typePriority;

		switch (getGeoClassType())
		{
		case  GEO_CLASS_AXIS:
			typePriority = 10; break;
		case  GEO_CLASS_IMAGE:
		case GEO_CLASS_TEXTFIELD:
		case GEO_CLASS_BUTTON:
		case  GEO_CLASS_BOOLEAN:
			typePriority = 20; break;
		case  GEO_CLASS_LIST:
			typePriority = 40; break;
		case  GEO_CLASS_POLYGON :
		case  GEO_CLASS_POLYGON3D :
			typePriority = 50; break;
		case  GEO_CLASS_POLYLINE :
			typePriority = 51; break;
		case  GEO_CLASS_IMPLICIT_POLY :
			typePriority = 60; break;
		case  GEO_CLASS_CONIC:
		case  GEO_CLASS_CONICPART:
			typePriority = 70; break;
		case  GEO_CLASS_ANGLE :
		case  GEO_CLASS_NUMERIC:
			typePriority = 80; break;
		case  GEO_CLASS_INTERVAL: // not drawable
		case  GEO_CLASS_FUNCTION:
		case  GEO_CLASS_FUNCTIONCONDITIONAL:
		case  GEO_CLASS_CURVE_CARTESIAN :
		case  GEO_CLASS_CURVE_POLAR:
			typePriority = 90; break;
		case  GEO_CLASS_LINE:
		case  GEO_CLASS_LINE3D:
			typePriority = 100; break;
		case  GEO_CLASS_LINEAR_INEQUALITY:
			typePriority = 101; break;
		case  GEO_CLASS_FUNCTION_NVAR:
			typePriority = 102; break;
		case  GEO_CLASS_RAY:
		case  GEO_CLASS_SEGMENT:
		case  GEO_CLASS_RAY3D:
		case  GEO_CLASS_SEGMENT3D:
			typePriority = 110; break;
		case  GEO_CLASS_VECTOR:
			typePriority = 120; break;
		case  GEO_CLASS_LOCUS:
			typePriority = 130; break;
		case  GEO_CLASS_POINT:
		case  GEO_CLASS_POINT3D:
			typePriority = 140; break;
		case  GEO_CLASS_TEXT:
			typePriority = 150; break;
		default: // shouldn't occur
			Application.debug("missing case in getDrawingPriority()");
			typePriority = 160;
		}

		// priority = 100 000 000
		 long ret = (long) (typePriority * 10E9 + getConstructionIndex());

		 //Application.debug("priority: " + ret + ", " + this);
		 return ret;
	}

	public void setAlphaValue(float alpha) {
		if (fillColor == null || alpha < 0.0f || alpha > 1.0f)
			return;
		alphaValue = alpha;

		float[] rgb = new float[3];
		fillColor.getRGBColorComponents(rgb);
		fillColor = new Color(rgb[0], rgb[1], rgb[2], alpha);
	}

	public float getAlphaValue() {
		if (colFunction == null || colFunction.size() == 3)
			return alphaValue;

		GeoElement geo = colFunction.get(3);
		if (geo.isDefined()) {
			double alpha = ((NumberValue) geo).getDouble();

			// ensure between 0 and 1
			alpha = alpha/2 - Math.floor(alpha/2);
			if (alpha>0.5) alpha=2*(1-alpha); else alpha=2*alpha;
			return (float)alpha;
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
	 * Sets all visual values from given GeoElement.
	 * This will also affect tracing, label location
	 * and the location of texts for example.
	 * @param geo 
	 * @param keepAdvanced true to skip copying color function and visibility condition
	 */
	public void setAllVisualProperties(GeoElement geo, boolean keepAdvanced) {
		if(keepAdvanced)
			setVisualStyle(geo);
		else
			setAdvancedVisualStyle(geo);

		euclidianVisible = geo.euclidianVisible;
		algebraVisible = geo.algebraVisible;
		labelOffsetX = geo.labelOffsetX;
		labelOffsetY = geo.labelOffsetY;
		caption = geo.caption;
		strCaptionDescriptionHTMLneedsUpdate = true;
		inverseFill = geo.inverseFill;
		if (isTraceable() && geo.isTraceable()) {
			((Traceable) this).setTrace(((Traceable) geo).getTrace());
		}

		//if (isGeoPoint() && geo.isGeoPoint()) {
		if (getGeoClassType()==GeoElement.GEO_CLASS_POINT && geo.getGeoClassType()==GeoElement.GEO_CLASS_POINT) {
			((GeoPoint) this).setSpreadsheetTrace(((GeoPoint) geo).getSpreadsheetTrace());
		}

		// copy color function
		if (!keepAdvanced) // done in setAdvancedVisualStyle()
		if (geo.colFunction != null) {
			setColorFunction(geo.colFunction);
		}

		// copy ShowObjectCondition, unless it generates a CirclularDefinitionException
		if (!keepAdvanced) // done in setAdvancedVisualStyle()			
		if (geo.condShowObject != null) {
			try { setShowObjectCondition(geo.getShowObjectCondition());}
			catch (Exception e) {}
		
		}
		//G.Sturr 2010-6-26
		if (isSpreadsheetTraceable() && geo.getSpreadsheetTrace()) {
			setSpreadsheetTrace(true);
			traceSettings = geo.traceSettings;
		}
		//END G.Sturr

	}

	/*
	 * In future, this can be used to turn on/off whether transformed objects
	 * have the same style as the original object
	 */
	public void setVisualStyleForTransformations(GeoElement geo) {
		setVisualStyle(geo);
		update();
	}

	/**
	 * Just changes the basic visual styles. If the style of a geo
	 * is reset this is required as we don't want to overwrite advanced
	 * settings in that case.
	 *
	 * @param geo
	 */
	public void setVisualStyle(GeoElement geo) {
		// label style
		labelVisible = geo.labelVisible;
		labelMode = geo.labelMode;
		tooltipMode = geo.tooltipMode;

		// style of equation, coordinates, ...
		if (getGeoClassType() == geo.getGeoClassType())
			toStringMode = geo.toStringMode;

		// colors
		objColor = geo.objColor;
		selColor = geo.selColor;
		labelColor = geo.labelColor;
		if(geo.isFillable()){
			fillColor = geo.fillColor;
			fillType = geo.fillType;
			hatchingAngle = geo.hatchingAngle;
			hatchingDistance = geo.hatchingDistance;
			imageFileName = geo.imageFileName;
			alphaValue = geo.alphaValue;
		}
		else{
			fillColor = geo.objColor;
			this.setAlphaValue(geo.getAlphaValue());
		}
		bgColor = geo.bgColor;
		isColorSet = geo.isColorSet();
		// line thickness and line type:
		// note: line thickness in Drawable is calculated as lineThickness / 2.0f
		setLineThickness(geo.lineThickness);
		setLineType(geo.lineType);
		setDecorationType(geo.decorationType);

		// set whether it's an auxilliary object
		setAuxiliaryObject(geo.isAuxiliaryObject());

		// if layer is not zero (eg a new object has layer set to ev.getMaxLayerUsed())
		// we don't want to set it
		if (layer == 0)
			setLayer(geo.getLayer());

	}

	/**
	 * Also copy advanced settings of this object.
	 *
	 * @param geo
	 */
	public void setAdvancedVisualStyle(GeoElement geo) {
		setVisualStyle(geo);

		// set layer
		setLayer(geo.getLayer());

		// copy color function
		setColorFunction(geo.getColorFunction());
		setColorSpace(geo.getColorSpace());
		
		// copy ShowObjectCondition, unless it generates a CirclularDefinitionException
		try { setShowObjectCondition(geo.getShowObjectCondition());}
		catch (Exception e) {}
	}

	/**
		 * @return
		 *
	private static Color getInverseColor(Color c) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		 hsb[0] += 0.40;
		 if (hsb[0] > 1)
		  hsb[0]--;
		 hsb[1] = 1;
		 hsb[2] = 0.7f;
		 return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);

	}	*/

	/**
	 * Moves label by updating label offset
	 * @param x 
	 * @param y 
	 */
	public void setLabelOffset(int x, int y) {
		double len = GeoVec2D.length(x, y);
		if (len > MAX_LABEL_OFFSET) {
			double factor = MAX_LABEL_OFFSET / len;
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

		// used by DrawPoint to draw parts of intersection objects near the point
		if (forceEuclidianVisible) return true;

		if (!showInEuclidianView()) return false;

		if (condShowObject == null)
			return euclidianVisible;
		else
			return condShowObject.getBoolean();
	}

	public void setEuclidianVisible(boolean visible) {
		euclidianVisible = visible;
	}

	public void forceEuclidianVisible(boolean visible) {
		forceEuclidianVisible = visible;
	}

	public boolean isSetEuclidianVisible() {
		return euclidianVisible;
	}

	/**
	 * Returns whether this GeoElement is visible in
	 * the construction protocol
	 */
	final public boolean isConsProtocolBreakpoint() {
		return isConsProtBreakpoint;
	}

	public void setConsProtocolBreakpoint(boolean flag) {
		/*
		// all siblings need to have same breakpoint information
		GeoElement [] siblings = getSiblings();
		if (siblings != null) {
			for (int i=0; i < siblings.length; i++) {
				siblings[i].isConsProtBreakpoint = flag;
			}
		}*/

		isConsProtBreakpoint = flag;
	}

	/**
	 * Returns the children of the parent algorithm or null.
	 * @return the children of the parent algorithm or null.
	 */
	public GeoElement [] getSiblings() {
		if (algoParent != null) {
			return algoParent.getOutput();
		}
		else
			return null;
	}

	public boolean isDrawable() {
		return true;
	}

	public boolean isFillable() {
		return false;
	}

	public boolean isTraceable() {
		return false;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean flag) {
		if (!flag)
			fixed = flag;
		else if (isFixable())
			fixed = flag;
	}

	public boolean isFixable() {
		return true; // deleting objects with fixed descendents makes them undefined
		//return isIndependent();
	}

	/*
	 * if an object has a fixed descendent, we want to set it undefined
	 */
	final public void removeOrSetUndefinedIfHasFixedDescendent() {

		// can't delete a fixed object at all
		if (isFixed())
			return;

		boolean hasFixedDescendent = false;

		Set<GeoElement> tree = getAllChildren();
		Iterator<GeoElement> it = tree.iterator();
		while (it.hasNext() && hasFixedDescendent == false)
			if (((GeoElement) it.next()).isFixed())
				hasFixedDescendent = true;

		if (hasFixedDescendent) {
			//Application.debug("hasFixedDescendent, not deleting");
			setUndefined();
			updateRepaint();
		}
		else
		{
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

	public void setAuxiliaryObject(boolean flag) {
		if (auxiliaryObject != flag) {
			auxiliaryObject = flag;
			if (labelSet)
				notifyUpdateAuxiliaryObject();
		}
	}

	/**
	 * sets whether the object's label should be drawn in
	 * an EuclidianView
	 * @param visible
	 */
	public void setLabelVisible(boolean visible) {
		labelVisible = visible;
	}

	/**
	 * Returns whether the label should be shown in
	 * Euclidian view.
	 * @return true if label should be shown
	 */
	public boolean isLabelVisible() {
		return labelVisible && isLabelSet();
	}

	/**
	 * Returns whether the label can be shown in
	 * Euclidian view.
	 * @return true if label can be shown
	 */
	final public boolean isLabelShowable() {
		return isDrawable() &&
				!(isTextValue() ||
					isGeoImage() ||
					isGeoList() ||
					(isGeoBoolean() && !isIndependent()));
	}

	/**
	 * Returns whether the value (e.g. equation) should be shown
	 * as part of the label description
	 * false for eg GeoLocus, Boolean, Button, TextField
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
		//return isAlgebraVisible();
		switch (tooltipMode) {
		default:
		//case TOOLTIP_ALGEBRAVIEW_SHOWING:
			if (!(app.useFullGui() && app.getGuiManager().showView(Application.VIEW_ALGEBRA))) {
				return false;
			} else
				return isAlgebraVisible(); // old behaviour
		case TOOLTIP_OFF:
			return false;
		case TOOLTIP_ON:
		case TOOLTIP_CAPTION:
		case TOOLTIP_NEXTCELL:
			return true;
		}

	}

	public String getTooltipText(boolean colored, boolean alwaysOn) {
		//sbToolTipDesc.append(geo.getLongDescriptionHTML(colored, false));
		switch (tooltipMode) {
		default:
		case TOOLTIP_ALGEBRAVIEW_SHOWING:
			if (!alwaysOn)
			if (!(app.useFullGui() && app.getGuiManager().showView(Application.VIEW_ALGEBRA))) {
				return "";
			}
			// else fall through:
		case TOOLTIP_ON:

			app.setTooltipFlag();
			String ret = getLongDescriptionHTML(colored, false); // old behaviour
			app.clearTooltipFlag();

			return ret;
		case TOOLTIP_OFF:
			return "";
		case TOOLTIP_CAPTION:
			return getCaption();
		case TOOLTIP_NEXTCELL: // tooltip is the next cell to the right (spreadsheet objects only)
			String label = getLabel();
			Point coords = getSpreadsheetCoordsForLabel(label);
			if (coords == null) return "";
			coords.x++;
			label = getSpreadsheetCellName(coords.x, coords.y);
			if (label == null) return "";
			GeoElement geo = kernel.lookupLabel(label);
			return (geo == null) ? "" : geo.toValueString();
		}

	}

	public int getTooltipMode() {
		return tooltipMode;
	}

	public void setTooltipMode(int mode) {
		//return isAlgebraVisible();
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

	public void setAlgebraVisible(boolean visible) {
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

	public void setParentAlgorithm(AlgoElement algorithm) {
		algoParent = algorithm;
		if (algorithm != null)
			setConstructionDefaults(); // set colors to dependent colors
	}

	final public AlgoElement getParentAlgorithm() {
		return algoParent;
	}

	public void setDrawAlgorithm(AlgoDrawInformation algorithm) {
		if(algorithm != null){
			algoDraw = (AlgoElement)algorithm;
		}
	}

	final public AlgoElement getDrawAlgorithm() {
		if(algoDraw == null)
			return algoParent;
		return algoDraw;
	}

	final public ArrayList<AlgoElement> getAlgorithmList() {
		if (algorithmList == null)
			algorithmList = new ArrayList<AlgoElement>();
		return algorithmList;
	}

	public boolean isIndependent() {
		return (algoParent == null);
	}

	/**
	 * Returns whether this GeoElement can be
	 * changed directly.
	 * Note: for points on lines this is different than isIndependent()
	 * @return whether this geo can be changed directly
	 */
	public boolean isChangeable() {
		return !fixed && isIndependent();
	}

	/**
	 * Returns whether this GeoElement is a point
	 * on a path.
	 * @return true for points on path
	 */
	public boolean isPointOnPath() {
		return false;
	}

	/**
	 * Returns whether this object may be redefined
	 * @return whether this object may be redefined
	 */
	public boolean isRedefineable() {
		return !fixed && app.letRedefine() && !(isTextValue() || isGeoImage()) &&
			(isChangeable() ||	// redefine changeable (independent and not fixed)
			 !isIndependent()); // redefine dependent object
	}

	/**
	 * Returns whether this GeoElement can be
	 * moved in Euclidian View.
	 * Note: this is needed for texts and points on path
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
	public boolean isMoveable(EuclidianViewInterface view){
		return view.isMoveable(this);
	}

	/**
	 * Returns whether this (dependent) GeoElement has input points that can be
	 * moved in Euclidian View.
	 * @param view 
	 * @return whether this geo has only moveable input points
	 */
	public boolean hasMoveableInputPoints(EuclidianViewInterface view) {
		// allow only moving of certain object types
		switch (getGeoClassType()) {
			case GEO_CLASS_CONIC:
				
				// special case for Circle[A, r]
				if (getParentAlgorithm() instanceof AlgoCirclePointRadius) {
					return containsOnlyMoveableGeos(getFreeInputPoints(view));					
				}
				
				// fall through
				
			case GEO_CLASS_CONICPART:
			case GEO_CLASS_IMAGE:
			case GEO_CLASS_LINE:
			case GEO_CLASS_LINEAR_INEQUALITY:
			case GEO_CLASS_RAY:
			case GEO_CLASS_SEGMENT:
			case GEO_CLASS_TEXT:
				return hasOnlyFreeInputPoints(view) && containsOnlyMoveableGeos(getFreeInputPoints(view));

			case GEO_CLASS_POLYGON:
			case GEO_CLASS_POLYLINE:
				return containsOnlyMoveableGeos(getFreeInputPoints(view));

			case GEO_CLASS_VECTOR:
				if (hasOnlyFreeInputPoints(view) && containsOnlyMoveableGeos(getFreeInputPoints(view))) {
					// check if first free input point is start point of vector
					ArrayList<GeoPoint> freeInputPoints = getFreeInputPoints(view);
					if (freeInputPoints.size() > 0) {
						GeoPoint firstInputPoint = freeInputPoints.get(0);
						GeoPoint startPoint = ((GeoVector) this).getStartPoint();
						return (firstInputPoint == startPoint);
					}
				}
				break;
		}

		return false;
	}

	/**
	 * Returns all free parent points of this GeoElement.
	 * @param view 
	 * @return all free parent points of this GeoElement.
	 */
	public ArrayList<GeoPoint> getFreeInputPoints(EuclidianViewInterface view) {
		if (algoParent == null)
			return null;
		else
			return view.getFreeInputPoints(algoParent);
	}

	final public boolean hasOnlyFreeInputPoints(EuclidianViewInterface view) {
		if (algoParent == null)
			return false;
		else {
			// special case for edge of polygon
			if ( algoParent instanceof AlgoJoinPointsSegment && view.getFreeInputPoints(algoParent).size() == 2) return true;

			return view.getFreeInputPoints(algoParent).size() == algoParent.input.length;
		}
	}

	private static boolean containsOnlyMoveableGeos(ArrayList<GeoPoint> geos) {
		if (geos == null || geos.size() == 0)
			return false;

		for (int i=0; i < geos.size(); i++) {
			GeoElement geo = (GeoElement) geos.get(i);
    		if (!geo.isMoveable())
    			return false;
    	}
		return true;
	}

	/**
	 * Returns whether this object's class implements the interface Translateable.
	 * @return whether this object's class implements the interface Translateable.
	 */
	public boolean isTranslateable() {
		return false;
	}

	/**
	 * Returns whether this GeoElement can be
	 * rotated in Euclidian View.
	 * Note: this is needed for images
	 * @return whether this geo can be rotated
	 */
	public boolean isRotateMoveable() {
		return isChangeable() && this instanceof PointRotateable;
	}

	/**
	 * Returns whether this GeoElement has
	 * properties that can be edited in a properties dialog.
	 * @return whether this element has editable properties
	 */
	public boolean hasProperties() {
		//return isDrawable() || isChangeable();
		return true;
	}

	public void setAnimationStep(double s) {
		setAnimationStep(new MyDouble(kernel, s));
	}

	public void setAnimationStep(NumberValue v) {
			animationIncrement = v;
	}

	public double getAnimationStep() {
		if(animationIncrement == null)
			animationIncrement = new MyDouble(kernel, GeoNumeric.DEFAULT_SLIDER_INCREMENT);
		return animationIncrement.getDouble();
	}

	public GeoElement getAnimationStepObject() {
		if(animationIncrement == null)
			return null;
		return animationIncrement.toGeoElement();
	}

	public GeoElement getAnimationSpeedObject() {
		if (animationSpeedObj == null)
			return null;
		return animationSpeedObj.toGeoElement();
	}

	/**
	 * Returns the current animation speed of this slider. Note that
	 * the speed can be negative which will change the direction of the animation.
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
		}
		else if (speed > MAX_ANIMATION_SPEED) {
			speed = MAX_ANIMATION_SPEED;
		}
		else if (speed < -MAX_ANIMATION_SPEED) {
			speed = -MAX_ANIMATION_SPEED;
		}

		return speed;
	}

	public void setAnimationSpeedObject(NumberValue speed) {
		animationSpeedObj = speed;
	}

	public void setAnimationSpeed(double speed) {
		initAnimationSpeedObject();

		GeoElement speedObj = animationSpeedObj.toGeoElement();
		if (speedObj.isGeoNumeric() && speedObj.isIndependent()) {
			((GeoNumeric)speedObj).setValue(speed);
		}
	}

	private void initAnimationSpeedObject() {
		if (animationSpeedObj == null) {
			GeoNumeric num = new GeoNumeric(cons);
			num.setValue(1);
			animationSpeedObj = num;
		}
	}

	final public int getAnimationType() {
		return animationType;
	}

	final public void setAnimationType(int type) {
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
	 * @param flag 
	 *
	 * @see Animatable interface
	 */
	public synchronized void setAnimating(boolean flag) {
		boolean oldValue = animating;
		animating = flag && isAnimatable();

		// tell animation manager
		if (oldValue != animating) {
			AnimationManager am = kernel.getAnimatonManager();
			if (animating)
				am.addAnimatedGeo(this);
			else
				am.removeAnimatedGeo(this);
		}
	}

	final public boolean isAnimating() {
		return animating;
	}

	public boolean isAnimatable() {
		// over ridden by types that implement Animateable
		return false;
	}

    public String toLaTeXString(boolean symbolic) {
    	return getFormulaString(ExpressionNode.STRING_TYPE_LATEX, !symbolic);
    	//if (symbolic)
    	//	return toString();
    	//else
    	//	return toDefinedValueString();
    }

    /**
	 * Returns a String that can be used to define geo in the currently used CAS.
	 * For example, "f(x) := a*x^2", "a := 20", "g := 3x + 4y = 7" in MathPiper
	 * or "f(x) := a*x^2", "a:20", "g: 3x + 4y == 7" in Maxima
	 *
	 * @param type ExpressionNode.STRING_TYPE_MAXIMA, STRING_TYPE_MATHPIPER
     * @return String in the format of the current CAS.
	 */
	public String toCasAssignment(int type) {
		if (!labelSet) return null;
		
		int oldType = kernel.getCASPrintForm();
		kernel.setCASPrintForm(type);
		String retval;
		
		try {
			if (type == ExpressionNode.STRING_TYPE_GEOGEBRA) {
				String body = toValueString();
				String label = getLabel();				
				
				if (this instanceof FunctionalNVar)
				{
					String params = ((FunctionalNVar) this).getFunction().getVarString();
					retval = label + "(" + params + ") := " + body;
				} else
					retval = label + " := " + body;				
			} 
			else {
				String body = getCASString(false);
				String casLabel = getLabel();
				
				CASgeneric cas = kernel.getGeoGebraCAS().getCurrentCAS();
				if (this instanceof FunctionalNVar) {
					String params = ((FunctionalNVar) this).getFunction().getVarString();
					retval = cas.translateFunctionDeclaration(casLabel, params, body);
				} else
					retval = cas.translateAssignment(casLabel, body);
			}
		}
		finally {
			kernel.setCASPrintForm(oldType);
		}				
		
		return retval;
	}


	/**
	 * Returns a representation of geo in currently used CAS syntax.
	 * For example, "a*x^2"
	 * @param symbolic 
	 * @return representation of this geo for CAS
	 */
	 String getCASString(boolean symbolic) {
		return symbolic && !isIndependent() ?  getCommandDescription() : toValueString();
	 }
	 
	/* *******************************************************
	 * GeoElementTable Management
	 * Hashtable: String (label) -> GeoElement
	 ********************************************************/

    public void addCellRangeUser() {
    	++cellRangeUsers;
    }

    public void removeCellRangeUser() {
    	if (cellRangeUsers > 0)
    		--cellRangeUsers;
    }

    public boolean isRenameable() {
    	// don't allow renaming when this object is used in
		// cell ranges, see AlgoCellRange
    	return cellRangeUsers == 0;
    }
    
    /**
     * Tells this GeoElement that one more CAS algorithm
     * is using it as input. 
     */
    public void addCasAlgoUser() {
    	++casAlgoUsers;
    }

    /**
     * Tells this GeoElement that one CAS algorithm that had been
     * using it as input has been removed. If there are no more
     * using algorithms we call unbindVariableInCAS().
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
	 * @param newLabel
	 * @return true if label was changed
	 * @throws MyError: if new label is already in use
	 */
	public boolean rename(String newLabel) {
		if (!isRenameable())
			return false;

		if (newLabel == null)
			return false;
		newLabel = newLabel.trim();
		if (newLabel.length() == 0)
			return false;
		String oldLabel = label;

		if (newLabel.equals(oldLabel))
			return false;
		else if (cons.isFreeLabel(newLabel)) {
			setLabel(newLabel); // now we rename
			return true;
		} else {
			String str[] = { "NameUsed", newLabel };
			throw new MyError(app, str);
		}
	}

	/**
	 * Returns whether this object's label has been set and is valid now.
	 * (this is needed for saving: only object's with isLabelSet() == true should
	 * be saved)
	 * @return true if this geo has valid label
	 */
	public boolean isLabelSet() {
		return labelSet;
	}

	/**
	 * Sets label of a GeoElement and updates Construction list and GeoElement
	 * tabel (String label, GeoElement geo) in Kernel.
	 * If the old label was null, a new free label is assigned starting with
	 * label as a prefix.
	 * If newLabel is not already used, this object is renamed to newLabel.
	 * Otherwise nothing is done.
	 * @param newLabel 
	 */
	public void setLabel(String newLabel) {		
		if (cons.isSuppressLabelsActive())
			return;

		// don't want any '$'s in actual labels
		if (newLabel!=null && newLabel.indexOf('$') > -1) {
			newLabel = newLabel.replaceAll("\\$", "");
		}

		labelWanted = true;

		// had no label: try to set it
		if (!labelSet) {
			// to avoid wasting of labels, new elements must wait
			// until they are shown in one of the views to get a label
			if (isVisible()) {
				// newLabel is used already: rename the using geo
				GeoElement geo = kernel.lookupLabel(newLabel);
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
				//System.out.println("setLabel DID NOT RENAME: " + this.label + " to " + newLabel + ", new label is not free: " + cons.lookupLabel(newLabel).getLongDescription());
			}
		}
	}

//	private StringBuilder sb;
//
//	private String removeDollars(String s) {
//		if (sb == null)
//			sb = new StringBuilder();
//		sb.setLength(0);
//
//		for (int i = 0; i < s.length(); i++) {
//			char c = s.charAt(i);
//			if (c != '$')
//				sb.append(c);
//		}
//
//		return sb.toString();
//	}

	/**
	 * Sets label of a GeoElement and updates GeoElement table
	 * (label, GeoElement). This method should only be used by
	 * MyXMLHandler.
	 * @param label 
	 */
	public void setLoadedLabel(String label) {
		if (labelSet) { // label was set before -> rename
			doRenameLabel(label);
		} else { // no label set so far -> set new label
			doSetLabel(getFreeLabel(label));
		}
	}

	public boolean setCaption(String caption) {
		if (caption == null
			|| caption.equals(label)) {
			caption = null;
			return false;
		}

		caption = caption.trim();

		if (caption.trim().length() == 0) {
			this.caption = null;
			strCaptionDescriptionHTMLneedsUpdate = true;
			return true;
		}

		this.caption = caption.trim();
		strCaptionDescriptionHTMLneedsUpdate = true;
		return true;
	}

	StringBuilder captionSB = null;

	public String getCaptionNoReplace() {
		return caption;
	}
	public String getCaption() {
		if (caption == null)
			return getLabel();

		// for speed, check first for a %
		if (caption.indexOf('%') < 0) return caption;

		if (captionSB == null) captionSB = new StringBuilder();
		else captionSB.setLength(0);

		// replace %v with value and %n with name
		for (int i = 0; i < caption.length(); i++) {
			char ch = caption.charAt(i);
			if (ch == '%' && i < caption.length() - 1) {
				// get number after %
				i++;
				ch = caption.charAt(i);
				switch (ch) {
				case 'v': captionSB.append(toValueString());
				break;
				case 'n' : captionSB.append(getLabel());
				break;
				case 'x' :
					if (isGeoPoint()) captionSB.append(kernel.format(((GeoPointND)this).getInhomCoords().getX()));
					else if (isGeoLine()) captionSB.append(kernel.format(((GeoLine)this).x));
					else captionSB.append("%x");

					break;
				case 'y' :
					if (isGeoPoint()) captionSB.append(kernel.format(((GeoPointND)this).getInhomCoords().getY()));
					else if (isGeoLine()) captionSB.append(kernel.format(((GeoLine)this).y));
					else captionSB.append("%y");
				break;
				case 'z' :
					if (isGeoPoint()) captionSB.append(kernel.format(((GeoPointND)this).getInhomCoords().getZ()));
					else if (isGeoLine()) captionSB.append(kernel.format(((GeoLine)this).z));
					else captionSB.append("%z");
				break;
				default : 	captionSB.append('%');
							captionSB.append(ch);
				}
			} else {
				captionSB.append(ch);
			}
		}

		return app.translationFix(captionSB.toString());
	}

	
	public String getRawCaption() {
		if (caption == null)
			return getLabel();
		else
			return caption;
	}

	public String getCaptionDescription() {
		if (caption == null)
			return "";
		else
			return getCaption();
	}


	/**
	 * Sets label of a local variable object. This method should
	 * only be used by Construction.
	 * @param label local variable name
	 */
	public void setLocalVariableLabel(String label) {
		this.oldLabel = this.label;
		this.label = label;
		localVarLabelSet = true;
	}
	
	/**
	 * Sets label of a local variable object back to its previous label. This method should
	 * only be used by Construction.
	 */
	public void undoLocalVariableLabel() {
		if (this.oldLabel != null) {
			this.label = this.oldLabel;
			localVarLabelSet = false;
		}
	}

	public boolean isLocalVariable() {
		return localVarLabelSet;
	}

	private void doSetLabel(String label) {
		// UPDATE KERNEL
		if (!labelSet && isIndependent()) {
			//	add independent object to list of all Construction Elements
			// dependent objects are represented by their parent algorithm
			cons.addToConstructionList(this, true);
		}

		/*
		if (!cons.isFreeLabel(label)) {
			try {
				throw new Exception("SET LABEL: label: " + label + ", type: " + this.getTypeString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Application.debug("SET LABEL: " + label + ", type: " + this.getTypeString());
		}
		*/

		this.label = label; // set new label
		labelSet = true;
		labelWanted = false; // got a label, no longer wanted

		cons.putLabel(this); // add new table entry
		algebraStringsNeedUpdate();
		updateSpreadsheetCoordinates();

		notifyAdd();
	}

	private void updateSpreadsheetCoordinates() {
		if (labelSet && label.length() > 0
			&& Character.isLetter(label.charAt(0)) // starts with letter
			&& Character.isDigit(label.charAt(label.length()-1)))  // ends with digit
		{

			// init old and current spreadsheet coords
			if (spreadsheetCoords == null) {
				oldSpreadsheetCoords = null;
				spreadsheetCoords = new Point();
			} else {
				if (oldSpreadsheetCoords == null)
					oldSpreadsheetCoords = new Point();
				oldSpreadsheetCoords.setLocation(spreadsheetCoords);
			}

			// we need to also support wrapped GeoElements like
			// $A4 that are implemented as dependent geos (using ExpressionNode)
			Matcher matcher = GeoElement.spreadsheetPattern.matcher(getLabel());
			int column = getSpreadsheetColumn(matcher);
			int row = getSpreadsheetRow(matcher);
			if (column >= 0 && row >= 0) {
				spreadsheetCoords.setLocation(column, row);
			} else {
				spreadsheetCoords = null;
			}
    	} else {
    		oldSpreadsheetCoords = spreadsheetCoords;
    		spreadsheetCoords = null;
    	}


		//Application.debug("update spread sheet coords: " + this + ", " +  spreadsheetCoords + ", old: " + oldSpreadsheetCoords);
	}

	/**
	 * Returns a point with the spreadsheet coordinates of the given inputLabel.
	 * Note that this can also be used for names that include $ signs like "$A1".
	 * @param inputLabel label of spredsheet cell
	 * @return null for non-spreadsheet names
	 */
	public static Point getSpreadsheetCoordsForLabel(String inputLabel) {
		// we need to also support wrapped GeoElements like
		// $A4 that are implemented as dependent geos (using ExpressionNode)
		Matcher matcher = GeoElement.spreadsheetPattern.matcher(inputLabel);
		int column = getSpreadsheetColumn(matcher);
		int row = getSpreadsheetRow(matcher);

//		System.out.println("match: " + inputLabel);
//		for (int i=0; i < matcher.groupCount(); i++) {
//			System.out.println("    group: " + i + ": " + matcher.group(i));
//		}

		if (column >= 0 && row >= 0)
			return new Point(column, row);
		else
			return null;
	}

	// Cong Liu
	public static String getSpreadsheetCellName(int column, int row) {
		++row;
		return getSpreadsheetColumnName(column) + row;
	}

    public static String getSpreadsheetColumnName(int i) {
        ++ i;
        String col = "";
        while (i > 0) {
              col = (char)('A' + (i-1) % 26)  + col;
              i = (i-1)/ 26;
        }
        return col;
  }

	public static String getSpreadsheetColumnName(String label) {
		Matcher matcher = spreadsheetPattern.matcher(label);
		if (! matcher.matches()) return null;
		return matcher.group(1);
	}


	 /**
     * Returns the spreadsheet reference name of this GeoElement using $ signs
     * for absolute spreadsheet reference names
     * like A$1 or $A$1.
	 * @param col$ 
	 * @param row$ 
	 * @return spreadsheet reference name of this GeoElement with $ signs
     */
	public String getSpreadsheetLabelWithDollars(boolean col$, boolean row$) {
		String colName = getSpreadsheetColumnName(spreadsheetCoords.x);
		String rowName = Integer.toString(spreadsheetCoords.y + 1);

		StringBuilder sb = new StringBuilder(label.length() + 2);
		if (col$) sb.append('$');
		sb.append(colName);
		if (row$) sb.append('$');
		sb.append(rowName);
		return sb.toString();
	}

	/*
	 * compares labels alphabetically, but spreadsheet labels are sorted nicely
	 * eg A1, A2, A10 not A1, A10, A2
	 */
	final public static int compareLabels(String label1, String label2) {

		if (GeoElement.isSpreadsheetLabel(label1) && GeoElement.isSpreadsheetLabel(label2)) {
			Point p1 = GeoElement.getSpreadsheetCoordsForLabel(label1);
			Point p2 = GeoElement.getSpreadsheetCoordsForLabel(label2);
			//Application.debug(label1+" "+p1.x+" "+p1.y+" "+label2+" "+p2.x+" "+p2.y);
			if (p1.x != p2.x) return p1.x - p2.x;
			return p1.y - p2.y;
		}

		return label1.compareTo(label2);

	}

	// Michael Borcherds
	public static boolean isSpreadsheetLabel(String str) {
		Matcher matcher = spreadsheetPattern.matcher(str);
		if (matcher.matches()) return true;
		else return false;
	}

	/*
	 * match A1, ABG1, A123
	 * but not A0, A000, A0001 etc
	 */
	public static final Pattern spreadsheetPattern =
		Pattern.compile("\\$?([A-Z]+)\\$?([1-9][0-9]*)");

	public static final int MAX_LINE_WIDTH = 13;

	private static volatile StringBuilder sb = null;

	/*
	 * used to set a cell to another geo
	 * used by FillCells[] etc
	 */
	public static void setSpreadsheetCell(Application app, int row, int col, GeoElement cellGeo) {
		String cellName = GeoElement.getSpreadsheetCellName(col, row);

		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);

		sb.append(cellName);
		if (cellGeo.isGeoFunction()) sb.append("(x)");

		// getLabel() returns algoParent.getCommandDescription() or  toValueString()
		// if there's no label (eg {1,2})
		String label = cellGeo.getLabel();

		// need an = for B3=B4
		// need a : for B2:x^2 + y^2 = 2
		if (label.indexOf('=') == -1) sb.append('=');
		else sb.append(':');

		sb.append(label);

		// we only sometimes need (x), eg
		// B2(x)=f(x)
		// B2(x)=x^2
		if (cellGeo.isGeoFunction() && cellGeo.isLabelSet()) sb.append("(x)");

		//Application.debug(sb.toString());

		app.getKernel().getAlgebraProcessor().processAlgebraCommand(sb.toString(), false);

			GeoElement cell = app.getKernel().lookupLabel(cellName);
			if (cell != null) {
				cell.setVisualStyle(cellGeo);
				cell.setAuxiliaryObject(true);
			}

	}

	
	public static int getSpreadsheetColumn(Matcher matcher) {
		if (! matcher.matches()) return -1;

		String s = matcher.group(1);
		int column = 0;
		while (s.length() > 0) {
			column *= 26;
			column += s.charAt(0) - 'A' + 1;
			s = s.substring(1);
		}
		//Application.debug(column);
		return column - 1;
	}

	// Cong Liu
	public static int getSpreadsheetRow(Matcher matcher) {
		if (! matcher.matches()) return -1;
		String s = matcher.group(2);
		return Integer.parseInt(s) - 1;
	}
	
	/**
	 * Determines spreadsheet row and column indices for a given cell name (e.g.
	 * "B3" sets column = 1 and row = 2. If the cell name does not match a
	 * possible spreadsheet cell then both row and column are returned as -1.
	 * 
	 * @param cellName
	 *            given cell name	
	 * @return coordinates of spreedsheet cell
	 */
	public static Point spreadsheetIndices(String cellName){
		
		Matcher matcher = spreadsheetPattern.matcher(cellName);			
		int column = GeoElement.getSpreadsheetColumn(matcher);
		int row = GeoElement.getSpreadsheetRow(matcher);
		
		return new Point(column, row);
	}
	
	

	 private void doRenameLabel(String newLabel) {
		if (newLabel == null || newLabel.equals(label))
			return;

		/*
		if (!cons.isFreeLabel(newLabel)) {
			try {
				throw new Exception("RENAME ERROR: old: " + label + ", new: " + newLabel + ", type: " + this.getTypeString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Application.debug("RENAME: old: " + label + ", new: " + newLabel + ", type: " + this.getTypeString());
		}
		*/

		//	UPDATE KERNEL
		cons.removeLabel(this); // remove old table entry
		oldLabel = label; // remember old label (for applet to javascript rename)
		label = newLabel; // set new label
		cons.putLabel(this); // add new table entry

		algebraStringsNeedUpdate();
		updateSpreadsheetCoordinates();

		kernel.notifyRename(this); // tell views
		updateCascade();
	}

	/**
	 * Returns the label of this object before rename()
	 * was called.
	 * @return label before renaming
	 */
	final public String getOldLabel() {
		return oldLabel;
	}

	/**
	 *  set labels for array of GeoElements with given label prefix.
	 * e.g. labelPrefix = "F", geos.length = 2 sets geo[0].setLabel("F_1")
	 * and geo[1].setLabel("F_2")
	 * all members in geos are assumed to be initialized.
	 * @param labelPrefix 
	 * @param geos 
	 */
	public static void setLabels(String labelPrefix, GeoElement[] geos) {
		if (geos == null) return;

		int visible = 0;
		int firstVisible = 0;
		for (int i = geos.length-1; i >=0; i--)
			if (geos[i].isVisible()) {
				firstVisible = i;
				visible++;
			}

		switch (visible) {
			case 0 : // no visible geos: they all get the labelPrefix as suggestion
				for (int i = 0; i < geos.length; i++)
					geos[i].setLabel(labelPrefix);
				break;

			case 1 : //	if there is only one visible geo, don't use indices
				geos[firstVisible].setLabel(labelPrefix);
				break;

			default :
				// is this a spreadsheet label?
				Matcher matcher = GeoElement.spreadsheetPattern.matcher(labelPrefix);
				if (matcher.matches()) {
					// more than one visible geo and it's a spreadsheet cell
					// use D1, E1, F1, etc as names
					int col = getSpreadsheetColumn(matcher);
					int row = getSpreadsheetRow(matcher);
					for (int i = 0; i < geos.length; i++)
						geos[i].setLabel(geos[i].getFreeLabel(getSpreadsheetCellName(col + i, row)));
				} else { // more than one visible geo: use indices if we got a prefix
					for (int i = 0; i < geos.length; i++)
						geos[i].setLabel(geos[i].getIndexLabel(labelPrefix));
				}
		}
	}

	/**
	 * set labels for array of GeoElements pairwise:
	 * geos[i].setLabel(labels[i])
	 * @param labels array of labels
	 * @param geos array of geos
	 */
	public static void setLabels(String[] labels, GeoElement[] geos) {
		setLabels(labels, geos, false);
	}

	static void setLabels(String[] labels, GeoElement[] geos, boolean indexedOnly) {
		int labelLen = (labels == null) ? 0 : labels.length;

		if (labelLen == 1 && labels[0] != null && !labels[0].equals("")) {
			setLabels(labels[0], geos);
			return;
		}

		String label;
		for (int i = 0; i < geos.length; i++) {
			if (i < labelLen) {
				label = labels[i];
			} else {
				label = null;
			}

			if (indexedOnly)
				label = geos[i].getIndexLabel(label);

			geos[i].setLabel(label);
		}
	}

	/** Get a free label. Try the suggestedLabel first 
	 * @param suggestedLabel 
	 * @return free label -- either suggestedLabel or suggestedLabel_index 
	 */
	public String getFreeLabel(String suggestedLabel) {
		if (suggestedLabel != null) {
			if ("x".equals(suggestedLabel) || "y".equals(suggestedLabel))
				return getDefaultLabel(false);

			if (cons.isFreeLabel(suggestedLabel))
				return suggestedLabel;
			else if (suggestedLabel.length() > 0)
				return getIndexLabel(suggestedLabel);
		}

		// standard case: get default label
		return getDefaultLabel(false);
	}

	public String getDefaultLabel(boolean isInteger) {
		return getDefaultLabel(null, isInteger);
	}

	public String getDefaultLabel() {
		return getDefaultLabel(null, false);
	}

	protected String getDefaultLabel(char[] chars, boolean isInteger) {

		if(chars==null){
			if (isGeoPoint()) {
				// Michael Borcherds 2008-02-23
				// use Greek upper case for labeling points if lenguage is Greek (el)
				if(app.isUsingLocalizedLabels()) {
					if (app.languageIs(app.getLocale(), "el")) {
						chars=greekUpperCase;
					} else if(app.languageIs(app.getLocale(), "ar")) {
						chars=arabic;
					} else {
						chars = pointLabels;
					}
				} else {
					chars = pointLabels;
				}

				GeoPointND point = (GeoPointND)this;
				if (point.getMode() == Kernel.COORD_COMPLEX)
					chars = complexLabels;

			} else if (isGeoFunction()) {
				chars = functionLabels;
			} else if (isGeoLine() || this instanceof GeoPolyLine) {
				chars = lineLabels;
			} else if (isGeoConic() || isGeoCubic()) {
				chars = conicLabels;
			} else if (isGeoVector() || isVector3DValue()) {
				chars = vectorLabels;
			}  else if (isGeoAngle()) {
				chars = greekLowerCase;
			}
			else if (isGeoPolygon()) {
				int counter = 0;
				String str;
				do {
					counter++;
					str = app.getPlain("Name.polygon") + kernel.internationalizeDigits(counter+"");;
				} while (!cons.isFreeLabel(str));
				return str;
			}
			else if (isGeoText()) {
				int counter = 0;
				String str;
				do {
					counter++;
					str = app.getPlain("Name.text") + kernel.internationalizeDigits(counter+"");
				} while (!cons.isFreeLabel(str));
				return str;
			} else if (isGeoImage()) {
				int counter = 0;
				String str;
				do {
					counter++;
					str = app.getPlain("Name.picture") + kernel.internationalizeDigits(counter+"");;
				} while (!cons.isFreeLabel(str));
				return str;
			} else if (isGeoLocus()) {
				int counter = 0;
				String str;
				do {
					counter++;
					str = app.getPlain("Name.locus") + kernel.internationalizeDigits(counter+"");;
				} while (!cons.isFreeLabel(str));
				return str;
			} else if (this instanceof GeoTextField) {
				int counter = 0;
				String str;
				do {
					counter++;
					str = app.getPlain("Name.textfield") + kernel.internationalizeDigits(counter+"");;
				} while (!cons.isFreeLabel(str));
				return str;
			} else if (isGeoButton()) {
				int counter = 0;
				String str;
				do {
					counter++;
					str = app.getPlain("Name.button") + kernel.internationalizeDigits(counter+"");;
				} while (!cons.isFreeLabel(str));
				return str;
			} else if (isGeoList()) {
				GeoList list = (GeoList) this;
				int counter = 0;
				String str;
				do {
					counter++;
					str = list.isMatrix() ? app.getPlain("Name.matrix") + kernel.internationalizeDigits(counter+"") : app.getPlain("Name.list") + kernel.internationalizeDigits(counter+"");;
				} while (!cons.isFreeLabel(str));
				return str;
			}
			else if (isInteger && isGeoNumeric()) {
				chars = integerLabels;
			}
			else {
				chars = lowerCaseLabels;
			}
		}

		int counter = 0, q, r;
		StringBuilder sbDefaultLabel = new StringBuilder();
		sbDefaultLabel.append(chars[0]);
		while (!cons.isFreeLabel(sbDefaultLabel.toString())) {
			sbDefaultLabel.setLength(0);
			q = counter / chars.length; // quotient
			r = counter % chars.length; // remainder

			char ch = chars[r];
			sbDefaultLabel.append(ch);

			// this arabic letter is two unicode chars
			if (ch == '\u0647') {
				sbDefaultLabel.append('\u0640');
			}

			if (q > 0) {
				// don't use indices
				//sbDefaultLabel.append(q);

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

	/**
	 * Returns the next free indexed label using the given prefix.
	 * @param prefix e.g. "c"
	 * @return indexed label, e.g. "c_2"
	 */
	public String getIndexLabel(String prefix) {
		if (prefix == null)
			return getFreeLabel(null) + "_1";
		else
			return cons.getIndexLabel(prefix);
	}

	/**
	 * Removes this object and all dependent objects from the Kernel.
	 * If this object is not independent, it's parent algorithm is removed too.
	 */
	public void remove() {
		// dependent object: remove parent algorithm
		if (algoParent != null) {
			algoParent.remove(this);
		} else {
			doRemove();
			if (correspondingCasCell != null)
				correspondingCasCell.doRemove();
		}
	}

	// removes this GeoElement and all its dependents
	public void doRemove() {
		// stop animation of this geo
		setAnimating(false);

		// remove this object from List
		if (isIndependent())
			cons.removeFromConstructionList(this);

		// remove Listeners
		AlgoElement algo = getParentAlgorithm();
		if (algo instanceof EuclidianViewCE) {
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
			Object[] algos = algorithmList.toArray();
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
		if(isSelected())
			app.removeSelectedGeo(this, false);

		// notify views before we change labelSet
		notifyRemove();

		labelSet = false;
		labelWanted = false;
		correspondingCasCell = null;
		
		if (latexCache != null) {
			// remove old key from cache
			//JLaTeXMathCache.removeCachedTeXFormula(keyLaTeX);
			latexCache.remove();
		}

	}
	
	GeoLaTeXCache latexCache = null;
	
	public GeoLaTeXCache getLaTeXCache() {
		if (latexCache == null) latexCache = new GeoLaTeXCache();
		return latexCache;
	}

	final public void notifyAdd() {
		kernel.notifyAdd(this);

		//		Application.debug("add " + label);
		// printUpdateSets();
	}

	final public void notifyRemove() {
		kernel.notifyRemove(this);

		//Application.debug("remove " + label);
		//printUpdateSets();
	}

	final public void notifyUpdate() {
		kernel.notifyUpdate(this);

		//	Application.debug("update " + label);
		//	printUpdateSets();
	}

	final public void notifyUpdateAuxiliaryObject() {
		kernel.notifyUpdateAuxiliaryObject(this);

		//		Application.debug("add " + label);
		//	printUpdateSets();
	}


/*
	private void printUpdateSets() {
		Iterator itList = cons.getAllGeoElementsIterator();
		while (itList.hasNext()) {
			GeoElement geo = (GeoElement) itList.next();
			Application.debug(geo.label + ": " + geo.algoUpdateSet.toString());
		}
	}
	*/

	/* *******************************************************
	 * AlgorithmList Management
	 * each GeoElement has a list of dependent algorithms
	 ********************************************************/

	/**
	 * add algorithm to dependency list of this GeoElement
	 * @param algorithm 
	 */
	public final void addAlgorithm(AlgoElement algorithm) {
		if (!(getAlgorithmList().contains(algorithm)))
			algorithmList.add(algorithm);
		addToUpdateSets(algorithm);
	}

	/**
	 * Adds the given algorithm to the dependency list of this GeoElement.
	 * The algorithm is NOT added to the updateSet of this GeoElement!
	 * I.e. when updateCascade() is called the given algorithm will
	 * not be updated.
	 * @param algorithm 
	 */
	final void addToAlgorithmListOnly(AlgoElement algorithm) {
		if (!getAlgorithmList().contains(algorithm))
			algorithmList.add(algorithm);
	}

	/**
	 * Adds the given algorithm to the update set this GeoElement.
	 * Note: the algorithm is NOT added to the algorithm list,
	 * i.e. the dependency graph of the construction.
	 * @param algorithm 
	 */
	final void addToUpdateSetOnly(AlgoElement algorithm) {
		addToUpdateSets(algorithm);
	}

	/**
	 * remove algorithm from dependency list of this GeoElement
	 * @param algorithm 
	 */
	public final void removeAlgorithm(AlgoElement algorithm) {
		algorithmList.remove(algorithm);
		removeFromUpdateSets(algorithm);
	}
	
//	/**
//	 * Removes all algorithms from algoUpdateSet that cannot be reached
//	 * through the algorithmList - input - output graph. This method should
//	 * be called when an algorithm removes its input but keeps its output,
//	 * see AlgoDependentCasCell.removeInputButKeepOutput().
//	 */
//	final public void removeUnreachableAlgorithmsFromUpdateSet() {
//		if (algorithmList == null || algorithmList.isEmpty()) return;
//		
//		// create set of reachable algorithms
//		HashSet<AlgoElement> reachableAlgos = new HashSet<AlgoElement>();
//		addReachableAlgorithms(algorithmList, reachableAlgos);
//		
//		// remove algorithms from updateSet that are not reachable
//		Iterator<AlgoElement> it = algoUpdateSet.getIterator();
//		while (it.hasNext()) {
//			AlgoElement updateAlgo = it.next();
//			if (!reachableAlgos.contains(updateAlgo)) {
//				it.remove();
//			}
//		}		
//	}
//	
//	/**
//	 * Adds all algorithms that can be reached through the output graph from startList to
//	 * reachableAlgorithmList
//	 * @param startList 
//	 * @param reachableAlgorithmList
//	 */
//	private void addReachableAlgorithms(ArrayList<AlgoElement> startList, HashSet<AlgoElement> reachableAlgorithmList) {
//		if (startList != null && !startList.isEmpty()) {
//			reachableAlgorithmList.addAll(startList);		
//			for (AlgoElement algo : startList) {
//				for (GeoElement output : algo.getOutput()) {
//					addReachableAlgorithms(output.algorithmList, reachableAlgorithmList);
//				}
//			}
//		}
//	}

	protected AlgorithmSet getAlgoUpdateSet() {
		if (algoUpdateSet == null)
			 algoUpdateSet = new AlgorithmSet();

		return algoUpdateSet;
	}


	/**
	 * add algorithm to update sets up the construction graph
	 */
	public void addToUpdateSets(AlgoElement algorithm) {
		boolean added = getAlgoUpdateSet().add(algorithm);

		if (added) {
			// propagate up the graph if we didn't do this before
			if (algoParent != null) {
				GeoElement [] input = algoParent.getInputForUpdateSetPropagation();
				for (int i = 0; i < input.length; i++) {
					input[i].addToUpdateSets(algorithm);
				}
			}
		}
	}

	/**
	 * remove algorithm from update sets  up the construction graph
	 * @param algorithm 
	 */
	public void removeFromUpdateSets(AlgoElement algorithm) {
		boolean removed = algoUpdateSet != null && algoUpdateSet.remove(algorithm);

		if (removed) {
			//	propagate up the graph
			if (algoParent != null) {
				GeoElement [] input = algoParent.getInputForUpdateSetPropagation();
				for (int i = 0; i < input.length; i++) {
					input[i].removeFromUpdateSets(algorithm);
				}
			}
		}
	}

	/**
	 * updates this object and notifies kernel.
	 * Note: no dependent objects are updated.
	 * @see #updateRepaint()
	 */
	public void update() {

		updateGeo();

		kernel.notifyUpdate(this);
	}
	
	final private void updateGeo() {

		//Application.debug(label);

		if (labelWanted && !labelSet) {
			// check if this object's label needs to be set
			if (isVisible())
				setLabel(label);
		}

		if (correspondingCasCell != null) {
			correspondingCasCell.setInputFromTwinGeo();
		}
		
		//G.Sturr 2010-6-26
		if(getSpreadsheetTrace() && app.useFullGui()){
			app.getGuiManager().traceToSpreadsheet(this);

		}
		//END G.Sturr		
		
		// texts need updates
		algebraStringsNeedUpdate();	
		
		// send update to underlying CAS if necessary
		sendValueToCAS();

	}

	
	/**
	 * Sends geo's value in the current CAS, e.g. a := 5;
	 * @param geo
	 * @return whether an assignment was evaluated
	 */
	final public boolean sendValueToCAS() {
		if (!isSendingUpdatesToCAS() || 
			!isCasEvaluableObject()  ||
			!isLabelSet()) 
		{ 
			return false;
		}
		
		try {
			GeoGebraCASInterface cas = kernel.getGeoGebraCAS();
			String geoStr = toCasAssignment(cas.getCurrentCASstringType());
			if (geoStr != null) {
				// TODO: remove
				System.out.println("sendValueToCAS: " + geoStr);
				cas.evaluateRaw(geoStr);
				return true;
			}
		} catch (Throwable e) {
			System.err.println("GeoElement.sendValueToCAS: " + this + "\n\t" + e.getMessage());
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
	 * Updates this object and all dependent ones. Note: no repainting is done afterwards!
	 * 	 synchronized for animation
	 */
	 public void updateCascade() {
		update();
		updateDependentObjects();		
	}
	 
	final private void updateDependentObjects(){
		if (correspondingCasCell != null && isIndependent()) {
			updateAlgoUpdateSetWith(correspondingCasCell);
		}				
		else if (algoUpdateSet != null) {
			// update all algorithms in the algorithm set of this GeoElement
			algoUpdateSet.updateAll();
		}
	}
	
	/**
	 * Updates algoUpdateSet and secondGeo.algoUpdateSet together efficiently.
	 */
	protected void updateAlgoUpdateSetWith(GeoElement secondGeo) {	
		if (algoUpdateSet == null) {
			if (secondGeo.algoUpdateSet == null) {
				// both null
				return;
			} else {
				// update second only
				secondGeo.algoUpdateSet.updateAll();
			}			
		} else {
			if (secondGeo.algoUpdateSet == null) {
				// update first only
				algoUpdateSet.updateAll();
			} 
			else {
				// join both algoUpdateSets and update all algorithms	
				TreeSet<AlgoElement> tempAlgoSet = getTempSet();
				tempAlgoSet.clear();
				algoUpdateSet.addAllToCollection(tempAlgoSet);
				secondGeo.algoUpdateSet.addAllToCollection(tempAlgoSet);
				for (AlgoElement algo : tempAlgoSet) {
					algo.update();
				}		
			}
		}			
	}

	/**
	 * If the flag updateCascadeAll is false, this algorithm 
	 * updates all GeoElements in the given ArrayList and all algorithms that depend on free GeoElements in that list.
	 * If the flag updateCascadeAll is true, this algorithm
	 * updates all GeoElements in the given ArrayList and all algorithms that depend on any GeoElement in that list.
	 * This flag was introduced because of Ticket #1383, description of that change is there
	 * 
	 * Note: this method is more efficient than calling updateCascade() for all individual
	 * GeoElements.
	 * 
	 * @param geos 
	 *
	 * @param tempSet a temporary set that is used to collect all algorithms that need to be updated
	 * 
	 * @param updateCascadeAll
	 */
	final static public synchronized void updateCascade(ArrayList<?> geos, TreeSet<AlgoElement> tempSet, boolean updateCascadeAll) {		
				// only one geo: call updateCascade()
		if (geos.size() == 1) {
			ConstructionElement ce = (ConstructionElement) geos.get(0);
			if (ce.isGeoElement()) {
				((GeoElement) ce).updateCascade();
			}
			return;
		}

		// build update set of all algorithms in construction element order
		// clear temp set
		tempSet.clear();

		int size = geos.size();
		for (int i=0; i < size; i++) {
			ConstructionElement ce = (ConstructionElement) geos.get(i);
			if (ce.isGeoElement()) {
				GeoElement geo = (GeoElement) geos.get(i);
				geo.update();

				if ((geo.isIndependent() || geo.isPointOnPath() || updateCascadeAll) &&
						geo.algoUpdateSet != null)
				{
					// add all dependent algos of geo to the overall algorithm set
					geo.algoUpdateSet.addAllToCollection(tempSet);
				}
			}
		}

		// now we have one nice algorithm set that we can update
		if (tempSet.size() > 0) {
			Iterator<AlgoElement> it = tempSet.iterator();
			while (it.hasNext()) {
				AlgoElement algo = (AlgoElement) it.next();
				algo.update();
			}
		}
	}

	/**
	 * Updates all GeoElements in the given ArrayList and all algorithms that depend on free GeoElements in that list.
	 * Note: this method is more efficient than calling updateCascade() for all individual
	 * GeoElements.
	 * @param geos 
	 *
	 * @param tempSet a temporary set that is used to collect all algorithms that need to be updated
	 */
	final static public void updateCascadeUntil(ArrayList<?> geos, TreeSet<AlgoElement> tempSet, AlgoElement lastAlgo) {		
				// only one geo: call updateCascade()
		if (geos.size() == 1) {
			ConstructionElement ce = (ConstructionElement) geos.get(0);
			if (ce.isGeoElement()) {
				((GeoElement) ce).updateCascade();
			}
			return;
		}

		// build update set of all algorithms in construction element order
		// clear temp set
		tempSet.clear();

		int size = geos.size();
		for (int i=0; i < size; i++) {
			ConstructionElement ce = (ConstructionElement) geos.get(i);
			if (ce.isGeoElement()) {
				GeoElement geo = (GeoElement) geos.get(i);
				geo.update();

				if ((geo.isIndependent() || geo.isPointOnPath()) &&
						geo.algoUpdateSet != null)
				{
					// add all dependent algos of geo to the overall algorithm set
					geo.algoUpdateSet.addAllToCollection(tempSet);
				}
			}
		}

		// now we have one nice algorithm set that we can update
		if (tempSet.size() > 0) {
			Iterator<AlgoElement> it = tempSet.iterator();
			while (it.hasNext()) {
				AlgoElement algo = (AlgoElement) it.next();
				
				algo.update();
				
				if (algo == lastAlgo) {
					return;
				}
					
			}
		}
	}

	
	/**
	 * Updates this object and all dependent ones.
	 * Notifies kernel to repaint views.
	 */
	final public void updateRepaint() {
		updateCascade();
		kernel.notifyRepaint();
	}
	
	/**
	 * update color
	 */
	public void updateVisualStyle() {
		updateGeo();
		kernel.notifyUpdateVisualStyle(this);
		updateDependentObjects();
		kernel.notifyRepaint();
	}
	
	
	public String toString() {
		return label;
	}


	public String toRealString() {
		return getRealLabel();
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

	public ExpressionValue evaluate() {
		return this;
	}

	public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> ret = new HashSet<GeoElement>();
		ret.add(this);
		return ret;
	}

	/**
	 * Returns all predecessors of this GeoElement that are random numbers
	 * and don't have labels.
	 * @return all random numeric unlabeled predecessors
	 */
	public ArrayList<GeoNumeric> getRandomNumberPredecessorsWithoutLabels() {
		if (isIndependent())
			return null;
		else {
			ArrayList<GeoNumeric> randNumbers = null;

			TreeSet<GeoElement> pred = getAllPredecessors();
			Iterator<GeoElement> it = pred.iterator();
			while (it.hasNext()) {
				GeoElement geo = it.next();
				if (geo.isGeoNumeric()) {
					GeoNumeric num = (GeoNumeric) geo;
					if (num.isRandomGeo() && !num.isLabelSet()) {
						if (randNumbers == null)
							randNumbers = new ArrayList<GeoNumeric>();
						randNumbers.add(num);
					}
				}
			}

			return randNumbers;
		}
	}

	/**
	 * Returns all predecessors (of type GeoElement) that this object depends on.
	 * The predecessors are sorted topologically.
	 * @return all predecessors of this geo 
	 */
	public TreeSet<GeoElement> getAllPredecessors() {
		TreeSet<GeoElement> set = new TreeSet<GeoElement>();
		addPredecessorsToSet(set, false);
		set.remove(this);
		return set;
	}

	/**
	 * Returns all independent predecessors (of type GeoElement) that this object depends on.
	 * The predecessors are sorted topologically. Note: when this method is called
	 * on an independent geo that geo is included in the TreeSet.
	 */
	public TreeSet<GeoElement> getAllIndependentPredecessors() {
		TreeSet<GeoElement> set = new TreeSet<GeoElement>();
		addPredecessorsToSet(set, true);
		return set;
	}

	// adds all predecessors of this object to the given set
	// the set is topologically sorted
	// @param onlyIndependent: whether only indpendent geos should be added
	final public void addPredecessorsToSet(TreeSet<GeoElement> set, boolean onlyIndependent) {
		if (algoParent == null) {
			set.add(this);
		}
		else { // parent algo
			algoParent.addPredecessorsToSet(set, onlyIndependent);
		}
	}

	public TreeSet<GeoElement> getAllRandomizablePredecessors() {
		TreeSet<GeoElement> set = new TreeSet<GeoElement>();
		addRandomizablePredecessorsToSet(set);
		return set;
	}
	
	final public void addRandomizablePredecessorsToSet(TreeSet<GeoElement> set) {
		if (this.isRandomizable() && !cloneInUse)
			set.add(this);
		
		if (algoParent!=null) { // parent algo
			algoParent.addRandomizablePredecessorsToSet(set);
		}
	}
	
	/**
	 * Returns whether geo depends on this object.
	 * @param geo 
	 * @return true if geo depends on this object.
	 */
	public boolean isParentOf(GeoElement geo) {
		if (algoUpdateSet != null) {
			Iterator<AlgoElement> it = algoUpdateSet.getIterator();
			while (it.hasNext()) {
				AlgoElement algo = (AlgoElement) it.next();
				for (int i = 0; i < algo.getOutputLength(); i++) {
					if (geo == algo.getOutput(i)) // child found
						return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns whether this object is parent of other geos.
	 * @return true if this object is parent of other geos.
	 */
	public boolean hasChildren() {
		return algorithmList != null && algorithmList.size() > 0;
	}

	/**
	 * Returns whether this object is dependent on geo.
	 * @param geo 
	 * @return true if this object is dependent on geo.
	 */
	public boolean isChildOf(GeoElement geo) {
		if (geo == null || isIndependent())
			return false;
		else
			return geo.isParentOf(this);
	}

	/**
	 * Returns whether this object is dependent on geo.
	 * @param geo 
	 * @return true if this object is dependent on geo.
	 */
	public boolean isChildOrEqual(GeoElement geo) {
		return this == geo || isChildOf(geo);
	}

	/**
	 * Returns all children (of type GeoElement) that depend on this object.
	 * @return  set of all children of this geo
	 */
	public TreeSet<GeoElement> getAllChildren() {
		TreeSet<GeoElement> set = new TreeSet<GeoElement>();
		if (algoUpdateSet != null) {
			Iterator<AlgoElement> it = algoUpdateSet.getIterator();
			while (it.hasNext()) {
				AlgoElement algo = (AlgoElement) it.next();
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
	public GeoElement[] getGeoElements() {
		return myGeoElements;
	}
	private GeoElement [] myGeoElements = new GeoElement[] { this };

	final public boolean isAlgoElement() {
		return false;
	}

	final public boolean isGeoElement() {
		return true;
	}



	/**
	 * Returns construction index in current construction.
	 * For a dependent object the construction index of its parent algorithm is returned.
	 */
	public int getConstructionIndex() {
		if (algoParent == null)
			return super.getConstructionIndex();
		else
			return algoParent.getConstructionIndex();
	}

	/**
	 * Returns the smallest possible construction index for this object in its construction.
	 * For an independent object 0 is returned.
	 */
	public int getMinConstructionIndex() {
		if (algoParent == null)
			return 0;
		else
			return algoParent.getMinConstructionIndex();
	}

	/**
	 * Returns the largest possible construction index for this object in its construction.
	 */
	public int getMaxConstructionIndex() {
		if (algoParent == null) {
			// independent object:
			// index must be less than every dependent algorithm's index
			int min = cons.steps();
			int size = algorithmList == null ? 0 : algorithmList.size();
			for (int i = 0; i < size; ++i) {
				int index =
					(algorithmList.get(i)).getConstructionIndex();
				if (index < min)
					min = index;
			}
			return min - 1;
		} else
			//	dependent object
			return algoParent.getMaxConstructionIndex();
	}	

	public String getDefinitionDescription() {
		if (algoParent == null)
			return "";
		else
			return algoParent.toString();
	}

	public String getDefinitionDescriptionHTML(boolean addHTMLtag) {
		if (algoParent == null)
			return "";
		else
			return indicesToHTML(app.translationFix(algoParent.toString()), addHTMLtag);
	}

	public String getCommandDescription() {
		if (algoParent == null)
			return "";
		else
			return algoParent.getCommandDescription();
	}

	public String getCommandDescriptionHTML(boolean addHTMLtag) {
		if (algoParent == null)
			return "";
		else
			return indicesToHTML(
				algoParent.getCommandDescription(),
				addHTMLtag);
	}

	public String getCommandNameHTML(boolean addHTMLtag) {
		if (algoParent == null)
			return "";
		else
			return indicesToHTML(
				algoParent.getCommandName(),
				addHTMLtag);
	}

	public int getRelatedModeID() {
		if (algoParent == null)
			return -1;
		else
			return algoParent.getRelatedModeID();
	}


	/**
	 * Converts indices to HTML <sub> tags if necessary.
	 * @param text 
	 * @return html string
	 */
	public static String convertIndicesToHTML(String text) {
		// check for index
		if (text.indexOf('_') > -1)
			return indicesToHTML(text, true);
		else
			return text;
	}

	public String addLabelTextOrHTML(String desc) {
		String ret;

		boolean includesEqual = desc.indexOf('=') >= 0;

		// check for function in desc like "f(x) = x^2"
		if (includesEqual && desc.startsWith(label + '(')) {
			ret = desc;
		}
		else {
			StringBuffer sb = new StringBuffer();
			sb.append(label);
			if (includesEqual)
				sb.append(": ");
			else
				sb.append(" = ");
			sb.append(desc);
			ret = sb.toString();
		}

		// check for index
		return convertIndicesToHTML(ret);
	}

	final public String getCaptionDescriptionTextOrHTML() {
		//TODO: Temporarly the caption in the the Construction protocol strored in html.
		// If there is no index in the caption the html-form is redundant
		// We need a function (which is similar to hasIndexLabel() ) to determine this
		// hasIndexLabel() don't checks the caption's index
		
		//if (hasIndexLabel())			
			return getCaptionDescriptionHTML();
		//else
			//return getCaptionDescription();
	}
	
	final public String getCaptionDescriptionHTML() {	
		if (strCaptionDescriptionHTMLneedsUpdate) {
			strCaptionDescriptionHTML = indicesToHTML(getCaptionDescription(), true);

			strCaptionDescriptionHTMLneedsUpdate = false;
		}

		return strCaptionDescriptionHTML;
	}
	
	/**
	 * Returns type string of GeoElement. Note: this is
	 * equal to getClassName().substring(3), but faster
	 * @return type string without "Geo" prefix
	 */
	abstract protected String getTypeString();
	/*{
		// e.g. GeoPoint -> type = Point
		//return getClassName().substring(3);
	}*/

	final public String getObjectType() {
		return getTypeString();
	}

	public String translatedTypeString() {
		return app.getPlain(getTypeString());
	}

	/**
	 * @return Type, label and definition information about this GeoElement
	 * (for tooltips and error messages)
	 */
	final public String getLongDescription() {
		if (algoParent == null)
			return getNameDescription();
		else {
			StringBuilder sbLongDesc = new StringBuilder();
			sbLongDesc.append(getNameDescription());
			// add dependency information
			sbLongDesc.append(": ");
			sbLongDesc.append(algoParent.toString());
			return sbLongDesc.toString();
		}
	}

	/**
	 * returns Type, label and definition information about this GeoElement
	 * as html string.
	 * (for tooltips and error messages)
	 * @param colored 
	 * @param addHTMLtag 
	 * @return description (type + label + definition)
	 */
	final public String getLongDescriptionHTML(
		boolean colored,
		boolean addHTMLtag) {
		if (algoParent == null || isTextValue())
			return getNameDescriptionHTML(colored, addHTMLtag);
		else {
			StringBuilder sbLongDescHTML = new StringBuilder();

			String label = getLabel();
			String typeString = translatedTypeString();

			// html string
			if (addHTMLtag)
				sbLongDescHTML.append("<html>");

			boolean reverseOrder = app.isReverseNameDescriptionLanguage();
			if (!reverseOrder) {
				//	standard order: "point A"
				sbLongDescHTML.append(typeString);
				sbLongDescHTML.append(' ');
			}

			if (colored) {
				sbLongDescHTML.append("<b><font color=\"#");
				sbLongDescHTML.append(Util.toHexString(getAlgebraColor()));
				sbLongDescHTML.append("\">");
			}
			sbLongDescHTML.append(indicesToHTML(label, false));
			if (colored)
				sbLongDescHTML.append("</font></b>");

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
				boolean rightToLeft = app.isRightToLeftReadingOrder();
				if (rightToLeft) {
					//sbLongDescHTML.append("\u200e\u200f: \u200e");
					sbLongDescHTML.append(Unicode.LeftToRightMark);
					sbLongDescHTML.append(Unicode.RightToLeftMark);
					sbLongDescHTML.append(": ");
					sbLongDescHTML.append(Unicode.LeftToRightMark);
				}
				else
					sbLongDescHTML.append(": ");
				sbLongDescHTML.append(indicesToHTML(app.translationFix(algoParent.toString()), false));
				if (rightToLeft)
					//sbLongDescHTML.append("\u200e");
					sbLongDescHTML.append(Unicode.LeftToRightMark);
			}
			if (addHTMLtag)
				sbLongDescHTML.append("</html>");
			return sbLongDescHTML.toString();
		}
	}

	static StringBuilder sbToolTipDesc = new StringBuilder();

	/**
	 * Returns long description for all GeoElements in given array.
	 * @param geos 
	 * @param colored 
	 * @param addHTMLtag true to wrap in &lt;html> ... &lt;/html>
	 * @return long description for all GeoElements in given array.
	 */
	final public static String getToolTipDescriptionHTML(
		ArrayList<GeoElement> geos,
		boolean colored,
		boolean addHTMLtag,
		boolean alwaysOn) {
		if (geos == null)
			return null;

		sbToolTipDesc.setLength(0);

		if (addHTMLtag)
			sbToolTipDesc.append("<html>");
		int count=0;
		for (int i = 0; i < geos.size(); ++i) {
			GeoElement geo = geos.get(i);
			if (geo.showToolTipText() || alwaysOn) {
				count++;
				sbToolTipDesc.append(geo.getTooltipText(colored, alwaysOn));
				if (i+1 < geos.size())
					sbToolTipDesc.append("<br>");
			}
		}
		if (count == 0) return null;
		if (addHTMLtag)
			sbToolTipDesc.append("</html>");
		return sbToolTipDesc.toString();
	}

	/**
		* Returns the label and/or value of this object for
		* showing in EuclidianView. This depends on the current
		* setting of labelMode:
		* LABEL_NAME : only label
		* LABEL_NAME_VALUE : label and value
	    * @return label, value, label+value or caption 
		*/
	public String getLabelDescription() {
		switch (labelMode) {
			case LABEL_NAME_VALUE :
				return getAlgebraDescription();

			case LABEL_VALUE :
				return toDefinedValueString();

			case LABEL_CAPTION: // Michael Borcherds 2008-02-18
				return getCaption();

			default : // case LABEL_NAME:
				//return label;
				//Mathieu Blossier - 2009-06-30
				return getLabel();
		}
	}

	/**
	 * Returns toValueString() if isDefined() ist true, else
	 * the translation of "undefined" is returned
	 * @return eithe value string or "undefined"
	 */
	final public String toDefinedValueString() {
		if (isDefined())
			return toValueString();
		else
			return app.getPlain("undefined");
	}

	/**
	* Returns algebraic representation of this GeoElement as Text. If this
	* is not possible (because there are indices in the representation)
	* a HTML string is returned.
	* @return algebraic representation of this GeoElement as Text
	*/
	final public String getAlgebraDescriptionTextOrHTML() {
		if (strAlgebraDescTextOrHTMLneedsUpdate) {
			String algDesc = getAlgebraDescription();
			// convertion to html is only needed if indices are found
			if (hasIndexLabel()) {
				strAlgebraDescTextOrHTML =
					indicesToHTML(algDesc, true);
			} else {
				strAlgebraDescTextOrHTML = algDesc;
			}

			strAlgebraDescTextOrHTMLneedsUpdate = false;
		}

		return strAlgebraDescTextOrHTML;
	}



	final public String getAlgebraDescriptionHTML(boolean addHTMLtag) {
		if (strAlgebraDescriptionHTMLneedsUpdate) {
			
			if(this instanceof GeoText){
				strAlgebraDescriptionHTML = indicesToHTML(toValueString(),false);
			} else {
				strAlgebraDescriptionHTML = indicesToHTML(getAlgebraDescription(), false);
			}
			strAlgebraDescriptionHTMLneedsUpdate = false;
		}

		return strAlgebraDescriptionHTML;
	}

	/**
	* @return type and label of a GeoElement
	* (for tooltips and error messages)
	*/
	final public String getLabelTextOrHTML() {
		if (strLabelTextOrHTMLUpdate) {
			if (hasIndexLabel())
				strLabelTextOrHTML = indicesToHTML(getLabel(), true);
			else
				strLabelTextOrHTML = getLabel();
		}

		return strLabelTextOrHTML;
	}

	/**
	 * Returns algebraic representation of this GeoElement.
	 */
	final public String getAlgebraDescription() {
		if (strAlgebraDescriptionNeedsUpdate) {
			if (isDefined()) {
				strAlgebraDescription = toString();
			} else {
				StringBuilder sbAlgebraDesc = new StringBuilder();
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
	 * Returns simplified algebraic representation of this GeoElement. 
 	 * Used by the regression test output creator.    
	 * @return sumplifiedrepresentation for regression test
	 */
	final public String getAlgebraDescriptionRegrOut() {
		if (strAlgebraDescriptionNeedsUpdate) {
			if (isDefined()) {
				strAlgebraDescription = toStringMinimal();
			} else {
				StringBuilder sbAlgebraDesc = new StringBuilder();
				sbAlgebraDesc.append(app.getPlain("undefined"));
				strAlgebraDescription = sbAlgebraDesc.toString();
			}

			strAlgebraDescriptionNeedsUpdate = false;
		}
		else {
			strAlgebraDescription = toStringMinimal();
		}

		return strAlgebraDescription;
	}

    public String toStringMinimal() {
        return toString();
    }

	
	public String getLaTeXdescription() {
		if (strLaTeXneedsUpdate) {
			if (isDefined() && !isInfinite()) {
				strLaTeX = toLaTeXString(false);
			} else {
				strLaTeX = app.getPlain("undefined");
			}
		}

		return strLaTeX;
	}

	
	/**
	 * Returns a string used to render a LaTeX form of the geo's algebra description. 
	 * @param substituteNumbers 
	 * @return string used to render a LaTeX form of the geo's algebra description.
	 *  
	 */
	public String getLaTeXAlgebraDescription(boolean substituteNumbers){
		return getLaTeXAlgebraDescription(this, substituteNumbers);
	}
	private String getLaTeXAlgebraDescription(GeoElement geo, boolean substituteNumbers){		

		String algebraDesc = geo.getAlgebraDescription();
		StringBuilder sb = new StringBuilder();
		
		if(geo.isGeoList() && ((GeoList)geo).getElementType()==GEO_CLASS_TEXT)
			return null;
		// handle undefined
		if(!geo.isDefined()){
			// we need to keep the string simple (no \mbox) so that isLatexNeeded may return true
			sb.append(label);
			sb.append("\\,");
			sb.append(app.getPlain("undefined"));			

		// handle non-GeoText prefixed with ":", e.g.  "a: x = 3"
		}else if(algebraDesc.indexOf(":") > -1 & !geo.isGeoText()){
			sb.append(algebraDesc.split(":")[0] + ": \\,");
			sb.append(geo.getFormulaString(ExpressionNode.STRING_TYPE_LATEX, substituteNumbers));
		}

		// now handle non-GeoText prefixed with "="
		else if(algebraDesc.indexOf("=") > -1 && !geo.isGeoText()){
			sb.append(algebraDesc.split("=")[0] + "\\, = \\,");
			sb.append(geo.getFormulaString(ExpressionNode.STRING_TYPE_LATEX, substituteNumbers));
		}

		// handle GeoText with LaTeX
		else if (geo.isGeoText() && ((GeoText)geo).isLaTeX()){
			sb.append(algebraDesc.split("=")[0]);
			sb.append("\\, = \\,");
			sb.append("\\text{``"); // left quote
			sb.append(((GeoText)geo).getTextString());
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
	final public Image getAlgebraImage(Image tempImage) {
		Graphics2D g2 = (Graphics2D) g;
		GraphicsConfiguration gc = app.getGraphicsConfiguration();
		if (gc != null) {
			bgImage = gc.createCompatibleImage(width, height);
		Point p = drawIndexedString(g2, labelDesc, xLabel, yLabel);

		setSize(fontSize, p.x, fontSize + p.y);
	}
	*/

	/*
	 * replaces all indices (_ and _{}) in str by <sub> tags, all and converts all
	 * special characters in str to HTML
	 * examples:
	 * "a_1" becomes "a<sub>1</sub>"
	 * "s_{AB}" becomes "s<sub>AB</sub>"
	 */
	private static String subBegin = "<sub><font size=\"-1\">";
	private static String subEnd = "</font></sub>";
	public static String indicesToHTML(String str, boolean addHTMLtag) {
		StringBuilder sbIndicesToHTML = new StringBuilder();

		if (addHTMLtag)
			sbIndicesToHTML.append("<html>");

		int depth = 0;
		int startPos = 0;
		int length = str.length();
		for (int i = 0; i < length; i++) {
			switch (str.charAt(i)) {
				case '_' :
					//	write everything before _
					if (i > startPos) {
						sbIndicesToHTML.append(
							Util.toHTMLString(str.substring(startPos, i)));
					}
					startPos = i + 1;
					depth++;

					// check if next character is a '{' (beginning of index with several chars)
					if (startPos < length && str.charAt(startPos) != '{') {
						sbIndicesToHTML.append(subBegin);
						sbIndicesToHTML.append(
							Util.toHTMLString(
								str.substring(startPos, startPos + 1)));
						sbIndicesToHTML.append(subEnd);
						depth--;
					} else {
						sbIndicesToHTML.append(subBegin);
					}
					i++;
					startPos++;
					break;

				case '}' :
					if (depth > 0) {
						if (i > startPos) {
							sbIndicesToHTML.append(
								Util.toHTMLString(str.substring(startPos, i)));
						}
						sbIndicesToHTML.append(subEnd);
						startPos = i + 1;
						depth--;
					}
					break;
			}
		}

		if (startPos < length) {
			sbIndicesToHTML.append(Util.toHTMLString(str.substring(startPos)));
		}
		if (addHTMLtag)
			sbIndicesToHTML.append("</html>");
		return sbIndicesToHTML.toString();
	}

	/**
		* returns type and label of a GeoElement
		* (for tooltips and error messages)
		*/
	public String getNameDescription() {
		StringBuilder sbNameDescription = new StringBuilder();

		String label = getLabel();
		String typeString = translatedTypeString();

		if (app.isReverseNameDescriptionLanguage()) {
			//	reverse order: "A point"
			sbNameDescription.append(label);
			sbNameDescription.append(' ');
			// For Hungarian, the standard is to lowercase the type.
			// I don't know if this is OK for Basque as well. -- Zoltan
			sbNameDescription.append(typeString.toLowerCase());
		}
		else {
			// standard order: "point A"
			sbNameDescription.append(typeString);
			sbNameDescription.append(' ');
			sbNameDescription.append(label);
		}

		return sbNameDescription.toString();
	}

	/**
		* returns type and label of a GeoElement
		* (for tooltips and error messages)
	 * @return type and label of a GeoElement
		*/
	final public String getNameDescriptionTextOrHTML() {
		if (hasIndexLabel())
			return getNameDescriptionHTML(false, true);
		else
			return getNameDescription();
	}

	/**
	 * Returns whether the str contains any indices (i.e. '_' chars).
	 * @return whether the str contains any indices (i.e. '_' chars).
	 */
	final public boolean hasIndexLabel() {
		if (strHasIndexLabel != label) {
			hasIndexLabel = (label == null || label.indexOf('_') > -1);
			strHasIndexLabel = label;
		}

		return hasIndexLabel;
	}
	private String strHasIndexLabel;
	private boolean hasIndexLabel = false;
	private boolean updateJavaScript;
	private boolean clickJavaScript;


	/**
		* returns type and label of a GeoElement as html string
		* (for tooltips and error messages)
	 * @param colored 
	 * @param addHTMLtag 
	 * @return type and label of a GeoElement as html string
		*/
	public String getNameDescriptionHTML(
		boolean colored,
		boolean addHTMLtag) {

		StringBuilder sbNameDescriptionHTML = new StringBuilder();

		if (addHTMLtag)
			sbNameDescriptionHTML.append("<html>");

		String label = getLabel();
		String typeString = translatedTypeString();

		boolean reverseOrder = app.isReverseNameDescriptionLanguage();
		if (!reverseOrder) {
			//	standard order: "point A"
			sbNameDescriptionHTML.append(typeString);
			sbNameDescriptionHTML.append(' ');
		}

		if (colored) {
			sbNameDescriptionHTML.append(" <b><font color=\"#");
			sbNameDescriptionHTML.append(Util.toHexString(getAlgebraColor()));
			sbNameDescriptionHTML.append("\">");
		}
		sbNameDescriptionHTML.append(indicesToHTML(label, false));
		if (colored)
			sbNameDescriptionHTML.append("</font></b>");

		if (reverseOrder) {
			//	reverse order: "A point"
			sbNameDescriptionHTML.append(' ');
			// For Hungarian, the standard is to lowercase the type.
			// I don't know if this is OK for Basque as well. -- Zoltan
			sbNameDescriptionHTML.append(typeString.toLowerCase());
		}

		if (addHTMLtag)
			sbNameDescriptionHTML.append("</html>");
		return sbNameDescriptionHTML.toString();
	}

	/*#******************************************************
	 * SAVING
	 *******************************************************/

	final public String getXMLtypeString() {
		return getClassName().substring(3).toLowerCase(Locale.US);
	}

	public String getI2GtypeString() {
		return getXMLtypeString();
	}

	public String getXML() {
		StringBuilder sb = new StringBuilder();
    	getXML(sb);
    	return sb.toString();
    }

	/**
	 * save object in xml format
	 * GeoGebra File Format
	 */
	public void getXML(StringBuilder sb) {
		boolean oldValue = kernel.isPrintLocalizedCommandNames();
		kernel.setPrintLocalizedCommandNames(false);

		// make sure numbers are not put in XML in eg Arabic
		boolean oldI8NValue = kernel.internationalizeDigits;
		kernel.internationalizeDigits = false;	

		getElementOpenTagXML(sb);
		
		getXMLtags(sb);
		sb.append(getCaptionXML());

		getElementCloseTagXML(sb);

		kernel.setPrintLocalizedCommandNames(oldValue);
		kernel.internationalizeDigits = oldI8NValue;
	}
	
	protected void getElementOpenTagXML(StringBuilder sb) {
		String type = getXMLtypeString();
		sb.append("<element");
		sb.append(" type=\"");
		sb.append(type);
		sb.append("\" label=\"");
		sb.append(Util.encodeXML(label));
		if (defaultGeoType >= 0) {
			sb.append("\" default=\"");
			sb.append(defaultGeoType);
		}
		sb.append("\">\n");
	}
	
	protected void getElementCloseTagXML(StringBuilder sb) {
		sb.append("</element>\n");
	}

	public void getScriptTags(StringBuilder sb) {
		// JavaScript
		if ((updateJavaScript && updateScript.length()>0)||(clickJavaScript && clickScript.length()>0)) {
			sb.append("\t<javascript ");
			if(clickJavaScript && clickScript.length()>0){
				sb.append(" val=\"");
				sb.append(getXMLClickScript());
				sb.append("\"");
			}
			if(updateJavaScript && updateScript.length()>0){
				sb.append(" onUpdate=\"");
				sb.append(getXMLUpdateScript());
				sb.append("\"");
			}
			sb.append("/>\n");
		}

		// Script
		if ((!updateJavaScript && updateScript!= null && updateScript.length()>0)||
				(!clickJavaScript && clickScript!=null && clickScript.length()>0)) {
			sb.append("\t<ggbscript ");
			if(!clickJavaScript && clickScript.length()>0){
				sb.append(" val=\"");
				sb.append(getXMLClickScript());
				sb.append("\"");
			}
			if(!updateJavaScript && updateScript.length()>0){
				sb.append(" onUpdate=\"");
				sb.append(getXMLUpdateScript());
				sb.append("\"");
			}
			sb.append("/>\n");
		}

	}

	public String getCaptionXML() {
		// caption text
		if (caption != null && caption.length() > 0 && !caption.equals(label)) {
			StringBuilder sb = new StringBuilder();
			sb.append("\t<caption val=\"");
			sb.append(Util.encodeXML(caption));
			sb.append("\"/>\n");
			return sb.toString();
		}
		else return "";
	}

	/**
	 * save object in i2g format
	 * Intergeo File Format (Yves Kreis)
	 */
	public void getI2G(StringBuilder sb, int mode) {
		boolean oldValue = kernel.isPrintLocalizedCommandNames();
		kernel.setPrintLocalizedCommandNames(false);

		String type = getI2GtypeString();

		if (mode == CONSTRAINTS) {
			if (isIndependent() || isPointOnPath()) {
				sb.append("\t\t<free_");
				sb.append(type);
				sb.append(">\n");

				sb.append("\t\t\t<");
				sb.append(type);
				sb.append(" out=\"true\">");
				sb.append(Util.encodeXML(label));
				sb.append("</");
				sb.append(type);
				sb.append(">\n");

				sb.append("\t\t</free_");
				sb.append(type);
				sb.append(">\n");
			}
		} else {
			if (mode == DISPLAY && (caption == null || caption.length() == 0 || caption.equals(label))) {
				return;
			}

			sb.append("\t\t<");
			sb.append(type);
			sb.append(" id=\"");
			sb.append(Util.encodeXML(label));
			sb.append("\">\n");

			if (mode == ELEMENTS) {
				getI2Gtags(sb);
			} else if (mode == DISPLAY) {
				// caption text
				sb.append("\t\t\t<label>");
				sb.append(Util.encodeXML(caption));
				sb.append("</label>\n");
			}

			sb.append("\t\t</");
			sb.append(type);
			sb.append(">\n");
		}

		kernel.setPrintLocalizedCommandNames(oldValue);
	}

    final void getAuxiliaryXML(StringBuilder sb) {
    	if (!isAuxiliaryObjectByDefault()) {
			if (auxiliaryObject) {
				sb.append("\t<auxiliary val=\"");
				sb.append("true");
				sb.append("\"/>\n");
			}
    	} else { // needed for eg GeoTexts (in Algebra View but Auxilliary by default from ggb 4.0)
			if (!auxiliaryObject) {
				sb.append("\t<auxiliary val=\"");
				sb.append("false");
				sb.append("\"/>\n");
			}
    	}
	}

	/**
	 * returns all visual xml tags (like show, objColor, labelOffset, ...)
	 * @param sb 
	 */
	void getXMLvisualTags(StringBuilder sb) {
		getXMLvisualTags(sb, true);
	}

	void getXMLvisualTags(StringBuilder sb, boolean withLabelOffset) {
		boolean isDrawable = isDrawable();

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
			if (!isVisibleInView(Application.VIEW_EUCLIDIAN)) {
				//Application.debug("visible in ev1");
				EVs += 1; // bit 0
			}

			if (isVisibleInView(Application.VIEW_EUCLIDIAN2)) {
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

//		if (isDrawable) removed - want to be able to color objects in AlgebraView, Spreadsheet
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
			sb.append(alphaValue);
			sb.append("\"");

			if (colFunction!=null && kernel.getSaveScriptsToXML())
			{
				sb.append(" dynamicr=\"");
				sb.append(Util.encodeXML(colFunction.get(0).getLabel()));
				sb.append('\"');
				sb.append(" dynamicg=\"");
				sb.append(Util.encodeXML(colFunction.get(1).getLabel()));
				sb.append('\"');
				sb.append(" dynamicb=\"");
				sb.append(Util.encodeXML(colFunction.get(2).getLabel()));
				sb.append('\"');
				if (colFunction.size() == 4) {
					sb.append(" dynamica=\"");
					sb.append(Util.encodeXML(colFunction.get(3).getLabel()));
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
				sb.append(imageFileName);
				sb.append('\"');
			}
			if(inverseFill){
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
			sb.append("val=\""+layer+"\"");
			sb.append("/>\n");
		}


		if (withLabelOffset &&
			(labelOffsetX != 0 || labelOffsetY != 0)) {
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
			Traceable t = (Traceable) this;
			if (t.getTrace()) {
				sb.append("\t<trace val=\"true\"/>\n");
			}
		}


		// G.Sturr 2010-5-29
		// Get spreadsheet trace XML from the trace manager

		// trace to spreadsheet
		if (app.useFullGui() && isSpreadsheetTraceable() && getSpreadsheetTrace()) {
			sb.append( ((SpreadsheetView) app.getGuiManager().getSpreadsheetView())
					.getTraceManager().getTraceXML(this));
		}

		/* --- old version
		// trace to spreadsheet on or off
		if (isGeoPoint()) {
			GeoPointInterface p = (GeoPointInterface) this;
			if (p.getSpreadsheetTrace()) {
				sb.append("\t<spreadsheetTrace val=\"true\"/>\n");
			}
		}
		*/
		//END G.Sturr




		// decoration type
		if (decorationType != DECORATION_NONE) {
			sb.append("\t<decoration");
			sb.append(" type=\"");
			sb.append(decorationType);
			sb.append("\"/>\n");
		}

	}

	void getXMLanimationTags(StringBuilder sb) {
		// animation step width
		if (isChangeable()) {
			sb.append("\t<animation");
			String animStep = animationIncrement == null ? "1" : getAnimationStepObject().getLabel();
			sb.append(" step=\""+Util.encodeXML(animStep)+"\"");
			String animSpeed = animationSpeedObj == null ? "1" : getAnimationSpeedObject().getLabel();
			sb.append(" speed=\""+Util.encodeXML(animSpeed)+"\"");
			sb.append(" type=\""+animationType+"\"");
			sb.append(" playing=\"");
			sb.append((isAnimating() ? "true" : "false"));
			sb.append("\"");
			sb.append("/>\n");
		}

	}

	void getXMLfixedTag(StringBuilder sb) {
		//		is object fixed
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
	 * returns all class-specific xml tags for getXML
	 * GeoGebra File Format
	 * @param sb 
	 */
	protected void getXMLtags(StringBuilder sb) {
		//sb.append(getLineStyleXML());
		getXMLvisualTags(sb);
		getXMLanimationTags(sb);
		getXMLfixedTag(sb);
		getAuxiliaryXML(sb);
		getBreakpointXML(sb);
		if (kernel.getSaveScriptsToXML())
			getScriptTags(sb);
	}

	/**
	 * returns some class-specific xml tags for getConstructionRegrOut
	 * (default implementation, may be overridden in certain subclasses)
	 * @param sb 
	 */
	protected void getXMLtagsMinimal(StringBuilder sb) {
		sb.append(toValueStringMinimal());
	}

	/**
	 * returns class-specific value string for getConstructionRegressionOut
	 * (default implementation, may be overridden in certain subclasses)
	 */
	protected String toValueStringMinimal() {
		return toValueString();
	}
	
    /** returns the number in rounded format to 6 decimal places,
     *  in case of the number is very close to 0, it returns the exact value
     * 
     * @param number
     * @return formatted String
     */
    protected String regrFormat (double number) {
            if (Math.abs(number) < 0.000001) {
                    Double numberD = new Double(number);
                    return numberD.toString();
            }
    DecimalFormat df = new DecimalFormat("#.######");
    DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
    dfs.setDecimalSeparator('.');
    df.setDecimalFormatSymbols(dfs);
    return df.format(number);
    }

	
	
	/**
	 * returns all class-specific i2g tags for getI2G
	 * Intergeo File Format (Yves Kreis)
	 * @param sb 
	 */
	protected void getI2Gtags(StringBuilder sb) {
	}

	/**
	 * Returns line type and line thickness as xml string.
	 * @param sb 
	 * @see #getXMLtags(StringBuilder) of GeoConic, GeoLine and GeoVector
	 */
	protected void getLineStyleXML(StringBuilder sb) {
		if (isGeoPoint()) return;

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
	 * @param sb 
	 * @see #getXMLtags(StringBuilder) of GeoConic, GeoLine and GeoVector
	 */
	void getBreakpointXML(StringBuilder sb) {
		if (isConsProtBreakpoint) {
			sb.append("\t<breakpoint val=\"");
			sb.append(isConsProtBreakpoint);
			sb.append("\"/>\n");

		}
	}

	private String getShowObjectConditionXML() {
		if (condShowObject != null && kernel.getSaveScriptsToXML()) {
			StringBuilder sb = new StringBuilder();
			sb.append("\t<condition showObject=\"");
			sb.append(Util.encodeXML(condShowObject.getLabel()));
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
	 * @return minimum line thickness (normally 1, but 0 for polygons, integrals etc)
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
	 * @param th new thickness
	 */
	public void setLineThickness(int th) {
		lineThickness = Math.max(0,th);
	}

	/**
	 * @param i new type
	 */
	public void setLineType(int i) {
		lineType = i;
	}

	/**
	 * @param i
	 */
	public void setLineTypeHidden(int i) {
		lineTypeHidden = i;
	}

	public void setDecorationType(int type) {
		decorationType = type;
	}


	/*
	 *  NOTE: change in GeoElementWrapper too!
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

	public boolean isGeoAngle() {
		return false;
	}

	public boolean isGeoBoolean() {
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
	public boolean isGeoPoint3D() {
		return false;
	}
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

	public boolean isGeoCurveable() {
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

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	/* ** hightlighting and selecting
	 * only for internal purpouses, i.e. this is not saved */

	final public void setSelected(boolean flag) {
		selected = flag;
	}

	final public void setHighlighted(boolean flag) {
		highlighted = flag;
	}

	final public boolean doHighlighting() {
		return (highlighted || selected) && (!isFixed() || isSelectionAllowed());
	}

	final public boolean isSelected(){
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

	public void setUseVisualDefaults(boolean useVisualDefaults) {
		this.useVisualDefaults = useVisualDefaults;
	}

	public boolean isAbsoluteScreenLocateable() {
		return false;
	}

	public final GeoBoolean getShowObjectCondition() {
		return condShowObject;
	}

	public void setShowObjectCondition(GeoBoolean cond)
	throws CircularDefinitionException {
		// check for circular definition
		//		if (this == cond || isParentOf(cond))
		// 		I relaxed this to allow (a parallel b) for a and b
		if (this == cond)
			throw new CircularDefinitionException();

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

	public final void removeCondition(GeoBoolean bool) {
		if (condShowObject == bool)
			condShowObject = null;
	}

	public final GeoList getColorFunction() {
		return colFunction;
	}

	public void setColorFunction(GeoList col)
	//throws CircularDefinitionException
	{
		//Application.debug("setColorFunction"+col.getValue());

		// check for circular definition (not needed)
		//if (this == col || isParentOf(col))
		//	throw new CircularDefinitionException();

		// unregister old condition
		if (colFunction != null) {
			colFunction.unregisterColorFunctionListener(this);
		}

		// set new condition
		colFunction = col;
		
		
		// register new condition
		if (colFunction != null) {
			colFunction.registerColorFunctionListener(this);
		}
	}

	public void removeColorFunction() {
		// unregister old condition
		if (colFunction != null) {
			colFunction.unregisterColorFunctionListener(this);
		}
		//Application.debug("removeColorFunction");
		//if (colFunction == col)
			colFunction = null;
	}


	/**
	 * Translates all GeoElement objects in geos by a vector in real world coordinates or by
	 * (xPixel, yPixel) in screen coordinates.
	 * @param geos 
	 * @param rwTransVec 
	 * @param endPosition may be null
	 * @param viewDirection 
	 * @return true if something was moved
	 */
	public static boolean moveObjects(ArrayList<GeoElement> geos, Coords rwTransVec, Coords endPosition, Coords viewDirection) {
		if (moveObjectsUpdateList == null)
			moveObjectsUpdateList = new ArrayList<GeoElement>();
		
		ArrayList<GeoElement> geos2 = new ArrayList<GeoElement>();
		
		// remove duplicates, eg drag Circle[A,A]
		for (int i = 0 ; i < geos.size(); i++) {
			if (!geos2.contains(geos.get(i)))
				geos2.add(geos.get(i));
		}
		
		geos = geos2;

		boolean moved = false;
		int size = geos.size();
		moveObjectsUpdateList.clear();
		moveObjectsUpdateList.ensureCapacity(size);

		for (int i=0; i < size; i++) {
			GeoElement geo = (GeoElement) geos.get(i);

			/* Michael Borcherds
			 * check for isGeoPoint() as it makes the mouse jump to the position of the point when dragging
			 * eg Image with one corner, Rigid Polygon
			 * and stops grid-lock working properly
			 * but is needed for eg dragging (a + x(A), b + x(B)) */
			Application.debug((geo.getParentAlgorithm() == null)+" "+size+" "+geo.getClassName());
			Coords position = (size == 1) && (geo.getParentAlgorithm() != null) ? endPosition : null;
			moved = geo.moveObject(rwTransVec, position, viewDirection, moveObjectsUpdateList) || moved;
		}

		// take all independent input objects and build a common updateSet
		// then update all their algos.
		// (don't do updateCascade() on them individually as this could cause
		//  multiple updates of the same algorithm)
		updateCascade(moveObjectsUpdateList, getTempSet(), false);

		return moved;
	}
	private static volatile ArrayList<GeoElement> moveObjectsUpdateList;
	private static volatile TreeSet<AlgoElement> tempSet;

	protected static TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<AlgoElement>();
		}
		return tempSet;
	}

//	/**
//	 * Moves geo by a vector in real world coordinates.
//	 * @return whether actual moving occurred
//	 */
//	final public boolean moveObject(GeoVector rwTransVec, Point2D.Double endPosition) {
//		return moveObject(rwTransVec, endPosition, null);
//	}


	protected boolean movePoint(Coords rwTransVec, Coords endPosition) {

		boolean movedGeo = false;

		GeoPoint point = (GeoPoint) this;
		if (endPosition != null) {
			point.setCoords(endPosition.getX(), endPosition.getY(), 1);
			movedGeo = true;
		}

		// translate point
		else {
			double x  = point.inhomX + rwTransVec.getX();
			double y =  point.inhomY + rwTransVec.getY();

			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			if (Math.abs(rwTransVec.getX()) > Kernel.MIN_PRECISION)
				x  = kernel.checkDecimalFraction(x);
			if (Math.abs(rwTransVec.getY()) > Kernel.MIN_PRECISION)
				y = kernel.checkDecimalFraction(y);

			// set translated point coords
			point.setCoords(x, y, 1);
			movedGeo = true;
		}

		return movedGeo;

	}

	/**
	 * Moves geo by a vector in real world coordinates.
	 * @return whether actual moving occurred
	 */
	private boolean moveObject(Coords rwTransVec, Coords endPosition, Coords viewDirection, ArrayList<GeoElement> updateGeos) {
		boolean movedGeo = false;
		GeoElement geo = this;
		// moveable geo
		if (isMoveable()) {
			// point
			if (isGeoPoint()) {

				if (getParentAlgorithm() instanceof AlgoDynamicCoordinates) {
					GeoPoint p = ((AlgoDynamicCoordinates)getParentAlgorithm()).getParentPoint();
					movedGeo = p.movePoint(rwTransVec, endPosition);
					geo = p;
				}
				else movedGeo = movePoint(rwTransVec, endPosition);

			}

			// translateable
			else if (isTranslateable()) {
				Translateable trans = (Translateable) this;
				trans.translate(rwTransVec);
				movedGeo = true;
			}

			// absolute position on screen
			else if (isAbsoluteScreenLocateable()) {
				AbsoluteScreenLocateable screenLoc = (AbsoluteScreenLocateable) this;
				if (screenLoc.isAbsoluteScreenLocActive()) {
					int vxPixel = (int) Math.round(kernel.getXscale() * rwTransVec.getX());
					int vyPixel = -(int) Math.round(kernel.getYscale() * rwTransVec.getY());
					int x = screenLoc.getAbsoluteScreenLocX() + vxPixel;
					int y = screenLoc.getAbsoluteScreenLocY() + vyPixel;
					screenLoc.setAbsoluteScreenLoc(x, y);
					movedGeo = true;
				}
				else if (isGeoNumeric()) {
					if (!((GeoNumeric)geo).isSliderFixed()) {
						// real world screen position - GeoNumeric
						((GeoNumeric)geo).setRealWorldLoc(
								((GeoNumeric)geo).getRealWorldLocX() + rwTransVec.getX(),
								((GeoNumeric)geo).getRealWorldLocY() + rwTransVec.getY());
						movedGeo = true;
					}
				}
				else if (isGeoText()) {
					// check for GeoText with unlabeled start point
					GeoText movedGeoText = (GeoText) this;
					if (movedGeoText.hasAbsoluteLocation()) {
						//	absolute location: change location
						GeoPoint loc = (GeoPoint) movedGeoText.getStartPoint();
						if (loc != null) {
							loc.translate(rwTransVec);
							movedGeo = true;
						}
					}
				}
			}

			if (movedGeo) {
				if (updateGeos != null)
					updateGeos.add(geo);
				else
					geo.updateCascade();
			}
		}

		// non-moveable geo
		else {
			movedGeo = moveFromChangeableCoordParentNumbers(rwTransVec, endPosition, viewDirection, updateGeos, tempMoveObjectList);
		}

		return movedGeo;
	}

	/**
	 * try to move the geo with coord parent numbers (e.g. point defined by sliders)
	 * @param rwTransVec
	 * @param endPosition
	 * @param viewDirection 
	 * @param updateGeos
	 * @param tempMoveObjectList
	 * @return false if not moveable this way
	 */
	public boolean moveFromChangeableCoordParentNumbers(Coords rwTransVec, Coords endPosition, Coords viewDirection,  ArrayList<GeoElement> updateGeos, ArrayList<GeoElement> tempMoveObjectList){
		return false;
	}

	/**
	 *
	 * @return true if has changeable coord parent numbers (e.g. point defined by sliders)
	 */
	public boolean hasChangeableCoordParentNumbers() {
		return false;
	}

	/**
	 * record values when mouse pressed
	 */
	public void recordChangeableCoordParentNumbers() {

	}

	/**
	 * add changeable coord parent number to update list
	 * @param number
	 * @param updateGeos
	 * @param tempMoveObjectList
	 */
	protected void addChangeableCoordParentNumberToUpdateList(GeoElement number, ArrayList<GeoElement> updateGeos, ArrayList<GeoElement> tempMoveObjectList){
		if (updateGeos != null) {
			// add number to update list
			updateGeos.add(number);
		} else {
			// update number right now
			if (tempMoveObjectList == null)
				tempMoveObjectList = new ArrayList<GeoElement>();
			tempMoveObjectList.add(number);
			updateCascade(tempMoveObjectList, getTempSet() , false );
		}
	}

	private ArrayList<GeoElement> tempMoveObjectList;

	/**
	 * Returns the position of this GeoElement in
	 * GeoGebra's spreadsheet view.
	 * The x-coordinate of the returned point specifies its
	 * column and the y-coordinate specifies its row location.
	 * Note that this method
	 * may return null if no position was specified so far.
	 * @return position of this GeoElement in GeoGebra's spreadsheet view.
	 */
	public Point getSpreadsheetCoords() {
		if (spreadsheetCoords == null)
			updateSpreadsheetCoordinates();
		return spreadsheetCoords;
	}

	/**
	 * Sets the position of this GeoElement in
	 * GeoGebra's spreadsheet. The x-coordinate specifies its
	 * column and the y-coordinate specifies its row location.
	 * @param spreadsheetCoords 
	 */
	public void setSpreadsheetCoords(Point spreadsheetCoords) {
		this.spreadsheetCoords = spreadsheetCoords;
	}

	public Point getOldSpreadsheetCoords() {
		return oldSpreadsheetCoords;
	}

	public final boolean isAlgoMacroOutput() {
		return isAlgoMacroOutput;
	}

	void setAlgoMacroOutput(boolean isAlgoMacroOutput) {
		this.isAlgoMacroOutput = isAlgoMacroOutput;
	}


	// Michael Borcherds 2008-04-30
	public abstract boolean isEqual(GeoElement Geo);
	
	/**
	 * Returns wheter this - f gives 0 in the CAS.
	 * @param f 
	 * @return wheter this - f gives 0 in the CAS.
	 */
	final public boolean isDifferenceZeroInCAS(GeoElement f) {
		// use CAS to check f - g = 0
		try {
			StringBuilder diffSb = new StringBuilder();
			diffSb.append(getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, true));
			diffSb.append("-(");
			diffSb.append(f.getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, true));
			diffSb.append(")");
			String diff = kernel.evaluateGeoGebraCAS(diffSb.toString());
			return (Double.valueOf(diff)==0d);
		}
		catch (Throwable e) { 
			return false; 
		}		
	}

	/**
	 * String getFormulaString(int, boolean substituteNumbers)
	 * substituteNumbers determines (for a function) whether you want
	 * "2*x^2" or "a*x^2"
	 * returns a string representing the formula of the GeoElement in the following formats:
	 * getFormulaString(ExpressionNode.STRING_TYPE_MathPiper) eg Sqrt(x)
	 * getFormulaString(ExpressionNode.STRING_TYPE_LATEX) eg \sqrt(x)
	 * getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA) eg sqrt(x)
	 * getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA_XML)
	 * getFormulaString(ExpressionNode.STRING_TYPE_JASYMCA)
	 * @param ExpressionNodeType 
	 * @param substituteNumbers 
	 * @return formula string
	 */
	public String getFormulaString(int ExpressionNodeType, boolean substituteNumbers)
	{

		/*
		 * maybe use this
		 * doesn't work on f=Factor[x^2-1] Expand[f]
		if (ExpressionNodeType == ExpressionNode.STRING_TYPE_MathPiper
				 || ExpressionNodeType == ExpressionNode.STRING_TYPE_JASYMCA) {

			ExpressionValue ev;
			if (!this.isExpressionNode())
	            ev = new ExpressionNode(kernel, this);
			else
				ev = this;

			String ret = ((ExpressionNode)
					ev).getCASstring(ExpressionNodeType,
					!substituteNumbers);
			Application.debug(ret);
			return ret;
		}
		*/

		int tempCASPrintForm = kernel.getCASPrintForm();
		kernel.setCASPrintForm(ExpressionNodeType);

		String ret="";
		if (this.isGeoFunctionConditional()) {
			GeoFunctionConditional geoFun = (GeoFunctionConditional)this;
			if (ExpressionNodeType == ExpressionNode.STRING_TYPE_MATH_PIPER) {

			// get in form If(x<3, etc
			ret = geoFun.toSymbolicString();
			//Application.debug(ret);
			} else if (ExpressionNodeType == ExpressionNode.STRING_TYPE_LATEX) {
				ret = geoFun.conditionalLaTeX(substituteNumbers);								
			}

		} else if (this.isGeoFunction()) {
			GeoFunction geoFun = (GeoFunction)this;

	 		if (geoFun.isIndependent()) {
	 			ret = geoFun.toValueString();
	 		} else {
	 			
	 			Function fun = geoFun.getFunction();
	 			
	 			if (fun == null) {
	 				ret = app.getPlain("undefined");
	 			} else	 			
		 			ret = substituteNumbers ?
		 					geoFun.getFunction().toValueString():
		 					geoFun.getFunction().toString();
	 		}
		}
		// matrices
		else if (this.isGeoList() && ExpressionNodeType == ExpressionNode.STRING_TYPE_LATEX && ((GeoList)this).isMatrix()) {
			ret = toLaTeXString(!substituteNumbers);
		}
		// vectors
		else if (this.isGeoVector() && ExpressionNodeType == ExpressionNode.STRING_TYPE_LATEX) {
			ret = toLaTeXString(!substituteNumbers);
		} // curves
		else if (this.isGeoCurveCartesian() && ExpressionNodeType == ExpressionNode.STRING_TYPE_LATEX) {
			ret = toLaTeXString(!substituteNumbers);
		}
		else
		{
			ret = substituteNumbers ? this.toValueString()
					: this.getCommandDescription();
		}				
		
		
		// GeoNumeric eg a=1
		if ("".equals(ret) && this.isGeoNumeric() && !substituteNumbers && isLabelSet()) {
			ret = kernel.printVariableName(label);
		}
		if ("".equals(ret) && !this.isGeoText()) {
			// eg Text[ (1,2), false]
			ret = toOutputValueString();
		}

		/* we don't want to deal with list bracess in here since  
		 * GeoList.toOutputValueString() takes care of it */
		
		kernel.setCASPrintForm(tempCASPrintForm);

		if (ExpressionNodeType == ExpressionNode.STRING_TYPE_LATEX) {
			if ("?".equals(ret)) ret = app.getPlain("undefined");
			else if ((Unicode.Infinity+"").equals(ret)) ret = "\\infty";
			else if ((Unicode.MinusInfinity+"").equals(ret)) ret = "-\\infty";
		}

		return ret;

	}

	public String getRealFormulaString(int ExpressionNodeType, boolean substituteNumbers)
	{



		String ret="";
		if (this.isGeoFunction()) {
			GeoFunction geoFun = (GeoFunction)this;

	 		if (geoFun.isIndependent()) {
	 			ret = geoFun.toValueString();
	 		} else {
	 			ret = substituteNumbers ?
	 					geoFun.getFunction().toValueString():
	 					geoFun.getFunction().toString();
	 		}
		}
		// matrices

		else
		{
			if(getParentAlgorithm()!=null)
			ret =  getParentAlgorithm().getCommandDescription(true);
		}

		// GeoNumeric eg a=1
		if ("".equals(ret) && this.isGeoNumeric() && !substituteNumbers && isLabelSet()) {
			ret = kernel.printVariableName(label);
		}
		if ("".equals(ret) && !this.isGeoText()) {
			// eg Text[ (1,2), false]
			ret = toOutputValueString();
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

	/** Set tracing flag for this geo 
	 * @param traceFlag */
	public void setSpreadsheetTrace(boolean traceFlag) {

		if(traceFlag != true)
			traceSettings = null;
		spreadsheetTrace = traceFlag;
	}

	/**
	 * Request spreadsheet trace manager to auto-reset the tracing columns.
	 * Called after mouse_release.
	 */
	public void resetTraceColumns() {
		if (app.useFullGui())
			app.getGuiManager().resetTraceColumn(this);
	}

	/** @return if geos of this type can be traced to the spreadsheet */
	public boolean isSpreadsheetTraceable() {
		if (isGeoList() || isGeoNumeric() || isGeoVector() || isGeoPoint()) {
			return true;
		}
		//TODO should all geos be traceable?
		// temporary allow all
		return true;
	}

	private geogebra.gui.view.spreadsheet.TraceSettings traceSettings;

	public TraceSettings getTraceSettings(){

		if (traceSettings == null)
			traceSettings = new TraceSettings();

		return traceSettings;

	}

	public void setTraceSettings(TraceSettings t){
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

	final public void setInTree(boolean flag) {
		inTree = flag;
	}

	// JavaScript

	private String clickScript = "";
	private String updateScript = "";

	// This method is copied from AutoCompleteTextField
	private static boolean isLetterOrDigit(char character) {
		switch (character) {
		case '_':  // allow underscore as a valid letter in an autocompletion word
			return true;

		default:
			return Character.isLetterOrDigit(character);
		}
	}

	/**
	 * This method should split a GeoGebra script into the following format:
	 * "" or "something"; "command"; "something"; "command"; "something"; ...
	 * @param st String GeoGebra script
	 * @return String [] the GeoGebra script split into and array
	 */
	private static String [] splitScriptByCommands(String st) {

		StringBuilder retone = new StringBuilder();
		ArrayList<String> ret = new ArrayList<String>();

		// as the other algorithms would be too complicated,
		// just go from the end of the string and advance character by character

		// at first count the number of "s to decide how to start the algorithm
		int countapo = 0;
		for (int j = 0; j < st.length(); j++)
			if (st.charAt(j) == '"')
				countapo++;

		boolean in_string = false;
		if (countapo % 2 == 1)
			in_string = true;

		boolean before_bracket = false;
		boolean just_before_bracket = false;
		for (int i = st.length() - 1; i >= 0; i--) {
			if (in_string) {
				if (st.charAt(i) == '"')
					in_string = false;
			} else if (just_before_bracket) {
				if (isLetterOrDigit(st.charAt(i))) {
					ret.add(0, retone.reverse().toString());
					retone = new StringBuilder();
					just_before_bracket = false;
					before_bracket = true;
				} else if (st.charAt(i) != '[' && st.charAt(i) != ' ') {
					just_before_bracket = false;
					before_bracket = false;
					if (st.charAt(i) == '"')
						in_string = true;
				}
			} else if (before_bracket) {
				if (!isLetterOrDigit(st.charAt(i))) {
					ret.add(0, retone.reverse().toString());
					retone = new StringBuilder();
					before_bracket = false;
					if (st.charAt(i) == '"')
						in_string = true;
					else if (st.charAt(i) == '[')
						just_before_bracket = true;
				}
			} else {
				if (st.charAt(i) == '"')
					in_string = true;
				else if (st.charAt(i) == '[')
					just_before_bracket = true;
			}
			retone.append(st.charAt(i));
		}
		ret.add(0, retone.reverse().toString());
		if (before_bracket) {
			ret.add(0, "");
		}
		String [] ex = {""};
		return ret.toArray(ex);
	}

	private String script2LocalizedScript(String st) {
		String [] starr = splitScriptByCommands(st);
		StringBuilder retone = new StringBuilder();
		for (int i = 0; i < starr.length; i++) {
			if (i % 2 == 0) {
				retone.append(starr[i]);
			} else {
				retone.append(app.getCommand(starr[i]));
			}
		}
		return retone.toString();
	}
	
	private String localizedScript2Script(String st) {
		String [] starr = splitScriptByCommands(st);
		StringBuilder retone = new StringBuilder();
		for (int i = 0; i < starr.length; i++) {
			if (i % 2 == 0) {
				retone.append(starr[i]);
			} else {
				// allow English language command in French scripts
				if (app.getInternalCommand(starr[i]) != null)
					retone.append(app.getInternalCommand(starr[i]));
				else
					// fallback for wrong call in English already
					// or if someone writes an English command into an
					// other language script
					retone.append(starr[i]);
			}
		}
		return retone.toString();
	}

	public void setClickScript(String script, boolean translateInternal) {
		if (!canHaveClickScript()) return;
		//Application.debug(script);
		if (clickJavaScript) {
			if(app.getScriptingLanguage()==null){
				app.setScriptingLanguage(app.getLocale().getLanguage());
			}
			this.clickScript = script;
		} else {
			if (translateInternal)
				this.clickScript = localizedScript2Script(script);
			else
				this.clickScript = script;
		}
	}

	public void setUpdateScript(String script, boolean translateInternal) {
		if (!canHaveUpdateScript()) return;
		if (updateJavaScript) {
			if(app.getScriptingLanguage()==null){
				app.setScriptingLanguage(app.getLocale().getLanguage());
			}
			this.updateScript = script;
		} else {
			if (translateInternal)
				this.updateScript = localizedScript2Script(script);
			else
				this.updateScript = script;
		}
		app.getScriptManager().initJavaScriptViewWithoutJavascript();
	}

	public boolean canHaveUpdateScript() {
		return true;
	}

	public String getUpdateScript() {
		if (!updateJavaScript)
			return script2LocalizedScript(updateScript);
		return updateScript;
	}

	public String getClickScript() {
		if (!clickJavaScript)
			return script2LocalizedScript(clickScript);
		return clickScript;
	}

	public String getXMLUpdateScript() {
		return Util.encodeXML(updateScript);
	}

	public String getXMLClickScript() {
		return Util.encodeXML(clickScript);
	}

	private void runGgbScript(String arg,boolean update) {

		String ggbScript = update ? updateScript : clickScript;

		AlgebraProcessor ab = kernel.getAlgebraProcessor();
		String script[] = (arg == null) ? ggbScript.split("\n") :
			ggbScript.replaceAll("%0", arg).split("\n");

		boolean success = false;
		int i = -1;
		try {
			for (i = 0 ; i < script.length ; i++) {
				String command = script[i].trim();

				if (!command.equals("") && command.charAt(0) != '#') {
					//System.out.println(script[i]);
					ab.processAlgebraCommandNoExceptionHandling(command, false,false,true);
					success = true;
				}
			}
			//there have been no errors
			if(update)app.setBlockUpdateScripts(false);
		} catch (Throwable e) {
			app.showError(app.getPlain("ErrorInScriptAtLineAFromObjectB",(i+1)+"", getLabel())+"\n"+e.getLocalizedMessage());
			success = false;
			if(update)app.setBlockUpdateScripts(true);
		}
		//storing undo info is expensive, so we don't want to do it on update
		if (success && !update) app.storeUndoInfo();
	}

	private void runJavaScript(String arg,boolean update) {
		//Possible TODO: make executing update scripts also possible via browser
		try {
			if (app.isApplet() && app.useBrowserForJavaScript() && !update) {
				if (arg == null) {
					Object [] args = { };
					app.getApplet().callJavaScript("ggb"+getLabel(), args);
				} else {
					Object [] args = { arg };
					app.getApplet().callJavaScript("ggb"+getLabel(), args);
				}
			} else {
				CallJavaScript.evalScript(app, update ? updateScript:clickScript, arg);
			}
			//there have been no errors
			if(update)app.setBlockUpdateScripts(false);
		} catch (Exception e) {
			e.printStackTrace();
			app.showError(app.getPlain(update ? "OnUpdate":"OnClick")+" "+getLabel()+":\n"+
					app.getPlain("ErrorInJavaScript")+"\n"+e.getLocalizedMessage());
			if(update) app.setBlockUpdateScripts(true);
		}
	}


	public void runScripts(String arg) {
		if(!canHaveClickScript()||clickScript.length()==0 || app.isScriptingDisabled())
			return;
		if(clickJavaScript)
			runJavaScript(arg,false);
		else
			runGgbScript(arg,false);
	}


	public void runUpdateScripts() {
		if(!canHaveUpdateScript() || updateScript.length()==0 || app.isBlockUpdateScripts() || app.isScriptingDisabled())
			return;
		app.setBlockUpdateScripts(true);
		if(updateJavaScript)
			runJavaScript(null,true);
		else
			runGgbScript(null,true);
	}

	boolean showTrimmedIntersectionLines = false;

	public void setShowTrimmedIntersectionLines(boolean show) {
		showTrimmedIntersectionLines = show;
	}

	public boolean getShowTrimmedIntersectionLines() {
		return showTrimmedIntersectionLines;
	}

	public boolean isPointInRegion() {
		return false;
	}

	protected void setRandomGeo(boolean flag) {
		isRandomGeo = flag;
	}

	private boolean isRandomGeo = false;

	public boolean isRandomGeo() {
		return isRandomGeo;
	}

	public void updateRandomGeo() {

		// update parent algorithm, like AlgoRandom
		AlgoElement algo = getParentAlgorithm();
		if (algo != null) {
			algo.compute(); // eg AlgoRandom etc
		} else if (this.isGeoNumeric()) {
			((GeoNumeric)this).updateRandom();
		}
	}

	public boolean isMatrixTransformable() {
		return false;
	}




	//=============================================
	// Control which views are allowed to add a geo.
	// G.Sturr, 2010-6-30
	//=============================================
	

	
	public void setVisibility(int viewId, boolean setVisible){
		if (setVisible){
			viewFlags.add(viewId);
		}else{
			viewFlags.remove(Integer.valueOf(viewId));
		}
	}
	
	public boolean isVisibleInView(int viewId){
		return viewFlags.contains(viewId);
	}

//	private Set<Integer> viewSet = new HashSet<Integer>();

	public void addView(int viewId){
		setVisibility(viewId, true);
//		viewSet.add(view);
	}
	public void removeView(int viewId){
		setVisibility(viewId, false);
//		viewSet.remove(view);
	}

//	public boolean isVisibleInView(int view){
//		// if no views are set, add geo to both  by default
//		if(viewSet.isEmpty()){
//			EuclidianViewInterface ev = app.getActiveEuclidianView();
//			viewSet.add(ev.getViewID());
//			// if ev isn't Graphics or Graphics 2, then also add 1st 2D euclidian view
//			if (!(ev.isDefault2D()))
//				viewSet.add(Application.VIEW_EUCLIDIAN);
//
//		}
//		return viewSet.contains(view);
//	}
	
	protected List<Integer> getViewSet(){
		List<Integer> list=new ArrayList<Integer>();
		list.addAll(viewFlags);
//		Collections.copy(list, viewFlags);
		return list;
	}

	/**
	 *
	 * @return true if visible in 3D view
	 */
	public boolean isVisibleInView3D(){
		return hasDrawable3D() && (isGeoElement3D() || isVisibleInView(Application.VIEW_EUCLIDIAN));
	}


	// End G.Sturr
	//===========================================

	public void setSelectionAllowed(boolean selected2) {
		selectionAllowed = selected2;
	}

	public boolean isSelectionAllowed() {
		return selectionAllowed;
	}

	/**
	 * In case this geo is part of macro construction, it
	 * keeps its own label. To get correct output of Name[geo]
	 * we need to keep the label of the real-world geo represented by this
	 * formal geo.
	 *
	 * @param realLabel Label of the real geo represented by this one
	 */
	public void setRealLabel(String realLabel){
		this.realLabel=realLabel;
	}

	/**
	 * Used for Name command. See {@link #setRealLabel(String)}
	 * @return label of this geo, or label of a real geo in case this one is formal
	 */
	public String getRealLabel(){
		if(realLabel==null || realLabel.equals(""))return label;
		return realLabel;
	}

	public boolean isHatchingEnabled() {
		return fillType == FILL_HATCH;
	}

	public void setHatchingAngle(int angle) {
		hatchingAngle = angle;
	}

	public double getHatchingAngle() {
		return hatchingAngle;
	}

	public void setHatchingDistance(int distance) {
		hatchingDistance = distance;
	}

	public int getHatchingDistance() {
		return hatchingDistance;
	}

	public BufferedImage getFillImage() {

		if (image != null) return image;

		if (imageFileName.startsWith("/geogebra")) {
			Image im = app.getImageManager().getImageResource(imageFileName);
			image = ImageManager.toBufferedImage(im);
		} else {
			image = app.getExternalImage(imageFileName);
		}

		return image;
	}

	//public void setFillImage(BufferedImage image){
	//	this.fillImage = image;
	//}

	public void setFillImage(String filename) {
		imageFileName=filename;
		image = null;
	}

	public int getFillType(){
		return fillType;
	}
	public void setFillType(int fillType){
		this.fillType = fillType;
	}

	/**
	 * Tries to load the image using the given fileName.
	 * @param fileName
	 */
	public void setImageFileName(String fileName) {
		if (fileName.equals(this.imageFileName))
			return;

		this.imageFileName = fileName;

		if (fileName.startsWith("/geogebra")) { // internal image
			Image im = app.getImageManager().getImageResource(imageFileName);
			image = ImageManager.toBufferedImage(im);

		} else {
			image = app.getExternalImage(fileName);
		}
	}

	public String getImageFileName() {
		return imageFileName;
	}

	/**
	 * @param inverseFill the inverseFill to set
	 */
	public void setInverseFill(boolean inverseFill) {
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
	 * @return "main" direction of the element, e.g. for seeing it in a "standard" view (for 3D).
	 * E.g. orthogonal to a plane, along a line, ...
	 */
	public Coords getMainDirection(){
		return mainDirection;
	}

	/**
	 * set the main direction
	 * @param direction
	 */
	public void setMainDirection(Coords direction){
		mainDirection = direction;
	}

	/*
	 * gets shortest distance to point p
	 * overridden in eg GeoPoint, GeoLine
	 * for compound paths
	 */
	public double distance(GeoPoint p) {
		return Double.POSITIVE_INFINITY;
	}

	public double distance(GeoPointND p) {
		if (p instanceof GeoPoint)
			return distance((GeoPoint) p);
		Application.debug("TODO : distance from "+getClassName()+" to ND point");
		return Double.POSITIVE_INFINITY;
	}

	/*
	 * Gets nearest point on object to p
	 * overridden in eg GeoPoint, GeoLine
	 * for compound paths
	 */
	public Point2D.Double getNearestPoint(GeoPoint p) {
		return null;
	}
	public Point2D.Double getNearestPoint(GeoPointND p) {
		return null;
	}

	public boolean canHaveClickScript() {
		return true;
	}




	///////////////////////////////
	// 3D
	///////////////////////////////


	/** says if it's a pickable object */
	private boolean isPickable = true;
	private boolean needsReplacingInExpressionNode = false;

	/** sets the pickability of the object
	 * @param v pickability
	 */
	public void setIsPickable(boolean v){
		isPickable = v;
	}

	/** says if the object is pickable
	 * @return true if the object is pickable
	 */
	public boolean isPickable(){
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
	 * Removes dependencies (conditional visibility, min, max, corner, EV bounds)
	 * from oldgeo and moves them to this
	 * @param oldGeo geo whose dependencies should be moved
	 */
	public void moveDependencies(GeoElement oldGeo) {
		//in general case do nothing; overriden in GeoPoint, GeoNumeric and GeoBoolean		
	}
	
	
	private Stack<GeoElement> tempClone;
	private boolean cloneInUse = false;
	public void storeClone() {
		if (tempClone==null)
			tempClone = new Stack<GeoElement>();
		
		tempClone.push(this.copy());
		this.cloneInUse = true;
	}
	public void recoverFromClone() {
		if (tempClone!=null)
			this.set(tempClone.pop());
		this.cloneInUse = false;
	}
	public void randomizeForProbabilisticChecking(){
		//overode by subclasses
		return;
	}
	public boolean isRandomizable() {
		return false;
	}
	
	/**
	 * Returns corresponding GeoCasCell. See GeoCasCell.setTwinGeo().
	 * @return twin GeoElement
	 */
	final public GeoCasCell getCorrespondingCasCell() {
		return correspondingCasCell;
	}
	
	/**
	 * Sets corresponding GeoCasCell for this GeoElement. See GeoCasCell.getTwinGeo().
	 */
	final public void setCorrespondingCasCell(GeoCasCell correspondingCasCell) {
		this.correspondingCasCell = correspondingCasCell;
	}
	
	/**
	 * Test method that returns true if the given GeoElement geo is to be drawn
	 * with LaTeX rendering in the spreadsheet or AV. For performance reasons
	 * LaTeX is to be avoided when not needed.
	 * 
	 * @param geo
	 * @return
	 */
	public  boolean isLaTeXDrawableGeo(String latexStr) {
		return false;
	}
	
	/*
	 * used in eg CAS and Spreadsheet Views to decide if the LaTeX renderer
	 * is neccesary by checking for eg ^2, \frac
	 */
	public static boolean isLaTeXneeded(String latex) {
		
		for (int i=0; i < latex.length(); i++) {
			char ch = latex.charAt(i);
			switch (ch) {
				case '\\':
					char ch1 = i == latex.length() - 1 ? ' ' : latex.charAt(i+1);
					if (ch1 != ';' && ch1 != ',') return true; // \; \, just spacing
					break;
				case '^':
					return true;
			}
		}
		
		// no real latex string
		return false;
	}
	
	
	
	
	
	
	


}
