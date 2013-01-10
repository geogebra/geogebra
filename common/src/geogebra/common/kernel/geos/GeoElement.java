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

import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.factories.AwtFactory;
import geogebra.common.factories.FormatFactory;
import geogebra.common.factories.LaTeXFactory;
import geogebra.common.kernel.AnimationManager;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.GraphAlgo;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Locateable;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoAttachCopyToView;
import geogebra.common.kernel.algos.AlgoCirclePointRadiusInterface;
import geogebra.common.kernel.algos.AlgoDependentText;
import geogebra.common.kernel.algos.AlgoDynamicCoordinatesInterface;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoIntegralODE;
import geogebra.common.kernel.algos.AlgoName;
import geogebra.common.kernel.algos.AlgorithmSet;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.algos.DrawInformationAlgo;
import geogebra.common.kernel.algos.EquationElementInterface;
import geogebra.common.kernel.algos.EquationScopeInterface;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyStringBuffer;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.Traversing;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.kernelND.GeoElementND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.Event;
import geogebra.common.plugin.EventType;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.script.Script;
import geogebra.common.util.LaTeXCache;
import geogebra.common.util.Language;
import geogebra.common.util.MyMath;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.SpreadsheetTraceSettings;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;

import java.util.ArrayList;
import java.util.Comparator;
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

public abstract class GeoElement extends ConstructionElement implements
		GeoElementND {

	public boolean isVector3DValue() {
		return false;
	}

	/**
	 * Column headings for spreadsheet trace
	 */
	protected ArrayList<GeoText> spreadsheetColumnHeadings = null;


	/** min decimals or significant figures to use in editing string */
	public static final int MIN_EDITING_PRINT_PRECISION = 5;

	// maximum label offset distance
	private static final int MAX_LABEL_OFFSET = 80;

	// private static int geoElementID = Integer.MIN_VALUE;

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
	
	private static final char[] yiddish = { '\u05D0', '\u05D1', '\u05D2', '\u05D3',
			'\u05D4', '\u05D5', '\u05D6', '\u05D7', '\u05D8', '\u05DB',
			'\u05DC', '\u05DE', '\u05E0', '\u05E1', '\u05E2', '\u05E4',
			'\u05E6', '\u05E7', '\u05E8', '\u05E9', '\u05EA'
	};

	private static final char[] greekUpperCase = { // Michael Borcherds
			// 2008-02-23
			'\u0391', '\u0392', '\u0393', '\u0394', '\u0395', '\u0396',
			'\u0397', '\u0398', '\u0399', '\u039a', '\u039b', '\u039c',
			'\u039d', '\u039e', '\u039f', '\u03a0', '\u03a1', '\u03a3',
			'\u03a4', '\u03a5', '\u03a6', '\u03a7', '\u03a8', '\u03a9' };
	/** label mode: name*/
	public static final int LABEL_NAME = 0;
	/** label mode: name + value*/
	public static final int LABEL_NAME_VALUE = 1;
	/** label mode: value*/
	public static final int LABEL_VALUE = 2;
	/** label mode: caption*/
	public static final int LABEL_CAPTION = 3; // Michael Borcherds 2008-02-18

	/** tooltip mode: iff AV showing*/
	public static final int TOOLTIP_ALGEBRAVIEW_SHOWING = 0;
	/** tooltip mode: always on*/
	public static final int TOOLTIP_ON = 1;
	/** tooltip mode: always off*/
	public static final int TOOLTIP_OFF = 2;
	/** tooltip mode: caption, always on*/
	public static final int TOOLTIP_CAPTION = 3;
	/** tooltip mode: next spreadsheet cell, always on*/
	public static final int TOOLTIP_NEXTCELL = 4;
	private int tooltipMode = TOOLTIP_ALGEBRAVIEW_SHOWING;
	/** should only be used directly in subclasses */
	protected String label; 
	private String realLabel; // for macro constructions, see setRealLabel() for
								// details
	private String oldLabel; // see doRenameLabel
	private String caption; //accessible via getRawCaption
	/** true if label is wanted, but not set*/
	public boolean labelWanted = false;
	/** tue if label is set */
	public boolean labelSet = false;
	
	private boolean localVarLabelSet = false;
	private boolean euclidianVisible = true;
	private boolean forceEuclidianVisible = false;
	private boolean algebraVisible = true;
	private boolean labelVisible = true;
	private boolean isConsProtBreakpoint; // in construction protocol
	private boolean isAlgoMacroOutput; // is an output object of a macro
										// construction
	/** fixed (cannot be moved or deleted)*/
	protected boolean fixed = false;
	/** label, value, caption, label+value */
	public int labelMode = LABEL_NAME;
	/** cartesian, polar or complex */
	public int toStringMode = Kernel.COORD_CARTESIAN;
	/** default (foreground) color */
	protected GColor objColor = GColor.black;
	/** background color*/
	protected GColor bgColor = null; // none by default
	/** color when selected */
	protected GColor selColor = objColor;
	/** color for label */
	protected GColor labelColor = objColor;
	/** color for fill*/
	protected GColor fillColor = objColor;
	private int layer = 0; // Michael Borcherds 2008-02-23
	private NumberValue animationIncrement;
	private NumberValue animationSpeedObj;
	private GeoCasCell correspondingCasCell; // used by GeoCasCell
	private boolean animating = false;
	/** maximal animation speed */
	final public static double MAX_ANIMATION_SPEED = 100;
	/** animation type: oscillating */
	final public static int ANIMATION_OSCILLATING = 0;
	/** animation type: increasing */
	final public static int ANIMATION_INCREASING = 1;
	/** animation type: decreasing */
	final public static int ANIMATION_DECREASING = 2;
	/** animation type: increasing once */
	final public static int ANIMATION_INCREASING_ONCE = 3;
	private int animationType = ANIMATION_OSCILLATING;
	private int animationDirection = 1;
	/** transparency */
	protected float alphaValue = 0.0f;
	/** angle of hatching */
	protected int hatchingAngle = 45; // in degrees
	/** distance of hatching*/
	protected int hatchingDistance = 10;
	private boolean inverseFill = false;

	// =================================
	// G.Sturr new fill options
	/** substitute for imageFileName and image - Arpad Fekete;
	// 2011-12-01 */
	protected GeoElementGraphicsAdapter graphicsadapter; 
	/** fill type: standard*/
	public static final int FILL_STANDARD = 0;
	/** fill type: hatch*/
	public static final int FILL_HATCH = 1;
	/** fill type: image*/
	public static final int FILL_IMAGE = 2;
	/** fill type*/
	protected int fillType = FILL_STANDARD;
	

	// =================================
	/** color space: RGB */
	final public static int COLORSPACE_RGB = 0;
	/** color space: HSB */
	final public static int COLORSPACE_HSB = 1;
	/** color space: HSL */
	final public static int COLORSPACE_HSL = 2;
	private int colorSpace = COLORSPACE_RGB;

	private final List<Integer> viewFlags;
	/**
	 * @return used color space (GeoElement.COLORSPACE_*)
	 */
	public int getColorSpace() {
		return colorSpace;
	}

	/**
	 * @param colorSpace color space (GeoElement.COLORSPACE_*)
	 */
	public void setColorSpace(final int colorSpace) {
		this.colorSpace = colorSpace;
	}

	private int defaultGeoType = -1;

	/**
	 * @return index for ConstructionDefaults or -1 if not default geo
	 */
	public int getDefaultGeoType() {
		return defaultGeoType;
	}

	/**
	 * @param defaultGT index for ConstructionDefaults
	 */
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
	private GPoint spreadsheetCoords, oldSpreadsheetCoords;
	// number of AlgoCellRange using this cell: don't allow renaming when
	// greater 0
	private int cellRangeUsers = 0;

	/** condition to show object*/
	protected GeoBoolean condShowObject;

	// function to determine color
	private GeoList colFunction; // { GeoNumeric red, GeoNumeric Green,
									// GeoNumeric Blue }

	private boolean useVisualDefaults = true;
	/** true if color is set*/
	protected boolean isColorSet = false;
	/** true if geo is highlited */
	protected boolean highlighted = false;
	private boolean selected = false;
	private String strAlgebraDescription, strAlgebraDescTextOrHTML,
			strAlgebraDescriptionHTML, strLabelTextOrHTML;
	/** LaTeX string for LaTeX export*/
	protected String strLaTeX;
	private boolean strAlgebraDescriptionNeedsUpdate = true;
	private boolean strAlgebraDescTextOrHTMLneedsUpdate = true;
	private boolean strAlgebraDescriptionHTMLneedsUpdate = true;
	private boolean strLabelTextOrHTMLUpdate = true;
	/** true if strLaTex is out of sync*/
	protected boolean strLaTeXneedsUpdate = true;

	// line thickness and line type: s
	/** note: line thickness in Drawable is calculated as lineThickness / 2.0f */
	public int lineThickness = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;
	/** line type (full, dashed, ...) see EuclidianStyleConstants.LINE_TYPE */
	public int lineType = EuclidianStyleConstants.DEFAULT_LINE_TYPE;
	/** line type for hidden parts (for 3D) */
	public int lineTypeHidden = EuclidianStyleConstants.DEFAULT_LINE_TYPE_HIDDEN;

	/** decoration type */
	public int decorationType = DECORATION_NONE;

	// DECORATION
	
	/** Decoration type: no decoration */
	public static final int DECORATION_NONE = 0;
	// segment decorations
	/** Decoration type: one tick */
	public static final int DECORATION_SEGMENT_ONE_TICK = 1;
	/** Decoration type: two ticks */
	public static final int DECORATION_SEGMENT_TWO_TICKS = 2;
	/** Decoration type: three ticks */
	public static final int DECORATION_SEGMENT_THREE_TICKS = 3;
	// Michael Borcherds 2007-10-06
	/** Decoration type: one arow */
	public static final int DECORATION_SEGMENT_ONE_ARROW = 4;
	/** Decoration type: two arrows */
	public static final int DECORATION_SEGMENT_TWO_ARROWS = 5;
	/** Decoration type: three arrows */
	public static final int DECORATION_SEGMENT_THREE_ARROWS = 6;
	// Michael Borcherds 2007-10-06
	// angle decorations
	/** Decoration type for angles: two arcs */
	public static final int DECORATION_ANGLE_TWO_ARCS = 1;
	/** Decoration type for angles: three arcs */
	public static final int DECORATION_ANGLE_THREE_ARCS = 2;
	/** Decoration type for angles: one tick */
	public static final int DECORATION_ANGLE_ONE_TICK = 3;
	/** Decoration type for angles: two ticks */
	public static final int DECORATION_ANGLE_TWO_TICKS = 4;
	/** Decoration type for angles: three ticks */
	public static final int DECORATION_ANGLE_THREE_TICKS = 5;

	/** Decoration type for angles: anticlockwise arrow 
	 * @author Michael Borcherds, 2007-10-22*/
	public static final int DECORATION_ANGLE_ARROW_ANTICLOCKWISE = 6; 
	/** Decoration type for angles: clockwise arrow 
	 * @author Michael Borcherds, 2007-10-22*/
	public static final int DECORATION_ANGLE_ARROW_CLOCKWISE = 7; 

	/** parent algorithm */
	protected AlgoElement algoParent = null;
	/** draw algorithm */
	protected AlgoElement algoDraw = null;
	/** directly dependent algos */
	private ArrayList<AlgoElement> algorithmList; 

	/** set of all dependent algos sorted in topological order */
	protected AlgorithmSet algoUpdateSet;


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
		// this.geoID = geoCounter++;

		// moved to subclasses, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		// setConstructionDefaults(); // init visual settings

		// new elements become breakpoints if only breakpoints are shown
		// isConsProtBreakpoint = cons.showOnlyBreakpoints();

		// ensure all new objects are in the top layer
		if (app != null) {
			layer = app.getMaxLayerUsed();
		}

		viewFlags = new ArrayList<Integer>();
		EuclidianViewInterfaceSlim ev;
		if ((app != null) && ((ev = app.getActiveEuclidianView()) != null)) {
			viewFlags.add(ev.getViewID());
			// if ev isn't Graphics or Graphics 2, then also add 1st 2D
			// euclidian view
			if (!(ev.isDefault2D())) {
				viewFlags.add(App.VIEW_EUCLIDIAN);
			}
		} else {
			viewFlags.add(App.VIEW_EUCLIDIAN);
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
	 * Returns label or local variable label if set,
	 * returns output value string otherwise
	 * @param tpl string template
	 * @return label or output value string
	 */
	public String getLabel(StringTemplate tpl) {
		if (!tpl.isUseRealLabels() || (realLabel == null) || realLabel.equals("")) {
			if (!labelSet && !localVarLabelSet) {
				if (algoParent == null) {
					return toOutputValueString(tpl);
				}
				return algoParent.getCommandDescription(tpl);
			}
			return tpl.printVariableName(label);
		}
		return tpl.printVariableName(realLabel);
	}

	/**
	 * @param c geo to receive the label copy
	 */
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
	 * overridden in GeoList so that the list elements are copied too
	 * (needed for tracing to spreadsheet)
	 * @return copy of this object
	 */
	public GeoElement deepCopyGeo() {
		return copy();
	}
	
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
	 * @param cons construction
	 * @param points array of points
	 * @return copy of points in construction cons
	 */
	public static GeoPoint[] copyPoints(final Construction cons,
			final GeoPointND[] points) {
		final GeoPoint[] pointsCopy = new GeoPoint[points.length];
		for (int i = 0; i < points.length; i++) {
			pointsCopy[i] = (GeoPoint) ((GeoPoint) points[i])
					.copyInternal(cons);
			pointsCopy[i].set(points[i]);
		}
		return pointsCopy;
	}

	/**
	 * Copies the given points array. The resulting points are part of the given
	 * construction.
	 * 
	 * @param cons construction
	 * @param points array of points
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

	public void resolveVariables(boolean forEquation) {
		//do nothing
	}

	/**
	 * @return true for infinite numbers or points with infinite coords
	 */
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
	 * @param view view
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
		// overriden where needed
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
	public void setObjColor(final GColor color) {
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
	private GColor getRGBFromList(float alpha1) {
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
	private GColor getRGBFromList(int withAlpha) {
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

			final int rgb = GColor.HSBtoRGB((float) redD, (float) greenD,
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

			final GColor c = AwtFactory.prototype.newColor(
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
	public GColor getSelColor() {
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
	public GColor getFillColor() {
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
	public GColor getAlgebraColor() {
		final GColor col = getLabelColor();
		return col.equals(GColor.white) ? GColor.black : col;
	}

	/**
	 * 
	 * @return color of label
	 */
	// Michael Borcherds 2008-04-01
	public GColor getLabelColor() {
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
	public void setLabelColor(final GColor color) {
		labelColor = color;
	}
	/**
	 * 
	 * @return color of background
	 */
	public GColor getBackgroundColor() {
		return bgColor;
	}
	/**
	 * 
	 * @param bgCol new background color
	 */
	public void setBackgroundColor(final GColor bgCol) {
		bgColor = bgCol;
	}
	/**
	 * 
	 * @return current color for this object
	 */
	// Michael Borcherds 2008-04-02
	public GColor getObjectColor() {
		GColor col = objColor;

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
	 * @param layer2
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
		case PENSTROKE:
			typePriority = 15;
			break;
		case IMAGE:
		case TURTLE:
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
		case CONIC3D:
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
		case VECTOR3D:
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
			App.debug("missing case in getDrawingPriority() for "+getGeoClassType());
			typePriority = 160;
		}

		// priority = 100 000 000
		final long ret = (long) ((typePriority * 10E9) + getConstructionIndex());

		// Application.debug("priority: " + ret + ", " + this);
		return ret;
	}
	/**
	 * Changes transparency of this geo
	 * @param alpha new alpha value
	 */
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

	/**
	 * @return alpha value (transparency)
	 */
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
	/**
	 * @return true for limited paths
	 */
	public boolean isLimitedPath() {
		return false;
	}

	/**
	 * @return true for paths
	 */
	public boolean isPath() {
		return false;
	}

	/**
	 * @return true for regions
	 */
	public boolean isRegion() {
		return false;
	}

	/**
	 * @return true for GeoLists
	 */
	public boolean isGeoList() {
		return false;
	}

	/**
	 * Sets all visual values from given GeoElement. This will also affect
	 * tracing, label location and the location of texts for example.
	 * 
	 * @param geo source geo
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
			((GeoPoint) this).setSpreadsheetTrace(((GeoPoint) geo)
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

	/**
	 * In future, this can be used to turn on/off whether transformed objects
	 * have the same style as the original object
	 * @param geo source geo
	 */
	public void setVisualStyleForTransformations(final GeoElement geo) {
		setVisualStyle(geo);
		updateVisualStyle();
	}

	/**
	 * Just changes the basic visual styles. If the style of a geo is reset this
	 * is required as we don't want to overwrite advanced settings in that case.
	 * 
	 * @param geo source geo
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
	 * @param geo source geo
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

	/**
	 * @return graphics adapter (wrapper for fill image) of this element
	 */
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
	 * @param xcoord label x-offset
	 * @param ycoord label y-offset
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

	/**
	 * Allows drawing this in EV
	 * @param visible true to allow drawing this in EV
	 */
	public void setEuclidianVisible(final boolean visible) {
		euclidianVisible = visible;
	}
	/**
	 * Forces drawing this in EV
	 * @param visible true to force drawing this in EV
	 */
	public void forceEuclidianVisible(final boolean visible) {
		forceEuclidianVisible = visible;
	}

	/**
	 * @return true if this is allowed to be drawn in EV
	 */
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

	/**
	 * @param flag true to make this a breakpoint
	 */
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

	/**
	 * @return true if this can be drawn
	 */
	public boolean isDrawable() {
		return true;
	}

	/**
	 * @return true if this can be filled
	 */
	public boolean isFillable() {
		return false;
	}
	/** @return true if inverse fill is posible */
	public boolean isInverseFillable() {
		return false;
	}
	/** @return true if tracing is posible */
	public boolean isTraceable() {
		return false;
	}
	/** @return true if this is fixed (moving & deleting forbidden) */
	public boolean isFixed() {
		return fixed;
	}

	/**
	 * @param flag true to make this fixed
	 */
	public void setFixed(final boolean flag) {
		if (!flag) {
			fixed = flag;
		} else if (isFixable()) {
			fixed = flag;
		}
	}

	/**
	 * @return true if fixed property can be set
	 */
	public boolean isFixable() {
		return true; // deleting objects with fixed descendents makes them
						// undefined
		// return isIndependent();
	}

	/**
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

	/**
	 * @return true for auxiliary objects
	 */
	final public boolean isAuxiliaryObject() {
		return auxiliaryObject;
	}

	/**
	 * @return true for loci, texts and images
	 */
	public boolean isAuxiliaryObjectByDefault() {
		return false;
	}

	/**
	 * Used to convert various interfaces into GeoElement
	 * @return this
	 */
	final public GeoElement toGeoElement() {
		return this;
	}

	/**
	 * @param flag true to make this auxiliary
	 */
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
	 * @param visible true to make label visible
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

	/**
	 * @return true if tooltip should be shown
	 */
	public boolean showToolTipText() {
		// return isAlgebraVisible();
		switch (tooltipMode) {
		default:
			// case TOOLTIP_ALGEBRAVIEW_SHOWING:
			if (!(app.isUsingFullGui() && app
					.showView(App.VIEW_ALGEBRA))) {
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

	/**
	 * @param colored true to use colors (HTML)
	 * @param alwaysOn true to override default behavior
	 * @return tooltip text as HTML
	 */
	public String getTooltipText(final boolean colored, final boolean alwaysOn) {

		if (getParentAlgorithm() instanceof AlgoAttachCopyToView) {
			return "";
		}
		
		StringTemplate tpl = StringTemplate.defaultTemplate;
		switch (tooltipMode) {
		default:
		case TOOLTIP_ALGEBRAVIEW_SHOWING:
			if (!alwaysOn) {
				if (!(app.isUsingFullGui() && app
						.showView(App.VIEW_ALGEBRA))) {
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
			final GPoint coords = GeoElementSpreadsheet
					.getSpreadsheetCoordsForLabel(cellLabel);
			if (coords == null) {
				return "";
			}
			coords.x++;
			cellLabel = GeoElementSpreadsheet.getSpreadsheetCellName(coords.x,
					coords.y);
			if (cellLabel == null) {
				return "";
			}
			final GeoElement geo = kernel.lookupLabel(cellLabel);
			return (geo == null) ? "" : geo.toValueString(tpl);
		}

	}

	/**
	 * @return tooltip mode
	 */
	public int getTooltipMode() {
		return tooltipMode;
	}

	/**
	 * @param mode new tooltip mode
	 */
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

	/**
	 * @param visible whether this is allowed to appear in AV 
	 */
	public void setAlgebraVisible(final boolean visible) {
		algebraVisible = visible;
	}

	/**
	 * @return whether this is allowed to appear in AV
	 */
	public boolean isSetAlgebraVisible() {
		return algebraVisible;
	}

	/**
	 * @return whether this is shown in AV
	 */
	public abstract boolean showInAlgebraView();

	/**
	 * @return whether this is shown in EV
	 */
	protected abstract boolean showInEuclidianView();

	/**
	 * @return true if this can be edited in AV directly
	 */
	public boolean isAlgebraViewEditable() {
		return true;
	}

	/**
	 * @return true if showable in EV
	 */
	final public boolean isEuclidianShowable() {
		return showInEuclidianView();
	}

	/**
	 * @return true if showable in AV
	 */
	public boolean isAlgebraShowable() {
		return showInAlgebraView();
	}

	/**
	 * @param algorithm algorithm responsible for computation of this object
	 */
	public void setParentAlgorithm(final AlgoElement algorithm) {
		algoParent = algorithm;
	}

	/**
	 * @return algorithm responsible for computation of this object
	 */
	final public AlgoElement getParentAlgorithm() {
		return algoParent;
	}
	/**
	 * @param algorithm algorithm responsible for drawing this 
	 */
	public void setDrawAlgorithm(final DrawInformationAlgo algorithm) {
		if (algorithm instanceof AlgoElement) {
			algoDraw = (AlgoElement) algorithm;
		}
	}

	/**
	 * @return algorithm responsible for drawing this
	 */
	final public AlgoElement getDrawAlgorithm() {
		if (algoDraw == null) {
			return algoParent;
		}
		return algoDraw;
	}

	/**
	 * @return list of directly dependent algos
	 */
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
	 * @param view view
	 * @return true if moveable in the view
	 */
	public boolean isMoveable(final EuclidianViewInterfaceSlim view) {
		return view.isMoveable(this);
	}

	/**
	 * Returns whether this (dependent) GeoElement has input points that can be
	 * moved in Euclidian View.
	 * 
	 * @param view view
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
		case PENSTROKE:
			return containsOnlyMoveableGeos(getFreeInputPoints(view));

		case VECTOR:
			if (hasOnlyFreeInputPoints(view)
					&& containsOnlyMoveableGeos(getFreeInputPoints(view))) {
				// check if first free input point is start point of vector
				final ArrayList<GeoPoint> freeInputPoints = getFreeInputPoints(view);
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
	 * @param view view
	 * @return all free parent points of this GeoElement.
	 */
	public ArrayList<GeoPoint> getFreeInputPoints(
			final EuclidianViewInterfaceSlim view) {
		if (algoParent == null) {
			return null;
		}
		return view.getFreeInputPoints(algoParent);
	}

	/**
	 * @param view view
	 * @return whether all input points are free in given view
	 */
	final public boolean hasOnlyFreeInputPoints(
			final EuclidianViewInterfaceSlim view) {
		if (algoParent == null) {
			return false;
		}
		// special case for edge of polygon
		if ((algoParent.getClassName().equals(Algos.AlgoJoinPointsSegment))
				&& (view.getFreeInputPoints(algoParent).size() == 2)) {
			return true;
		}

		return view.getFreeInputPoints(algoParent).size() == algoParent.input.length;
	}

	private static boolean containsOnlyMoveableGeos(
			final ArrayList<GeoPoint> geos) {
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
	final public boolean hasProperties() {
		// return isDrawable() || isChangeable();
		return isGeoElement();
	}

	/**
	 * @param s animation step
	 */
	public void setAnimationStep(final double s) {
		setAnimationStep(new MyDouble(kernel, s));
	}

	/**
	 * @param v animation step
	 */
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

	/**
	 * @return animation step as geo
	 */
	public GeoElement getAnimationStepObject() {
		if (animationIncrement == null) {
			return null;
		}
		return animationIncrement.toGeoElement();
	}

	/**
	 * @return animation speed as geo
	 */
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

	/**
	 * @param speed new speed
	 */
	public void setAnimationSpeedObject(final NumberValue speed) {
		animationSpeedObj = speed;
	}

	/**
	 * @param speed new speed
	 */
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

	/**
	 * @return animation type (ANIMATION_*)
	 */
	final public int getAnimationType() {
		return animationType;
	}

	/**
	 * @param type animation type (ANIMATION_*)
	 */
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

	/**
	 * @return +1 or -1
	 */
	protected int getAnimationDirection() {
		return animationDirection;
	}

	/**
	 * Change direction from +1 to -1 or vice versa
	 */
	protected void changeAnimationDirection() {
		animationDirection = -animationDirection;
	}

	/**
	 * Sets the state of this object to animating on or off.
	 * 
	 * @param flag true to make this animating
	 * 
	 * @see Animatable interface
	 */
	public synchronized void setAnimating(final boolean flag) {
		final boolean oldValue = animating;
		animating = flag && isAnimatable();

		// tell animation manager
		if (oldValue != animating) {
			final AnimationManager am = kernel.getAnimatonManager();
			if (animating) {
				am.addAnimatedGeo(this);
			} else {
				am.removeAnimatedGeo(this);
			}
		}
	}

	/**
	 * @return true if animation is on
	 */
	final public boolean isAnimating() {
		return animating;
	}

	/**
	 * over ridden by types that implement Animateable
	 * @return true if this can be animated
	 */
	public boolean isAnimatable() {
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
	 * @param tpl
	 *            StringType.MAXIMA, STRING_TYPE_MATHPIPER
	 * @return String in the format of the current CAS.
	 */
	public String toCasAssignment(final StringTemplate tpl) {
		if (!labelSet) {
			return null;
		}
		
		String retval = "";

		try {
			
				final String body = toValueString(tpl);
				retval = getAssignmentLHS(tpl) + " := " + body;
			 
		} finally {
			// do nothing
		}

		return retval;
	}

	/**
	 * @param tpl string template
	 * @return left hand side for assignment (label or e.g. label(x))
	 */
	public String getAssignmentLHS(StringTemplate tpl) {
		return getLabel(tpl);
	}

	/**
	 * Returns a representation of geo in currently used CAS syntax. For
	 * example, "a*x^2"
	 * @param tpl string template
	 * 
	 * @param symbolic true to keep variable names
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
	/** increment number of cellRange algos using this geo*/
	public void addCellRangeUser() {
		++cellRangeUsers;
	}
	/** decrement number of cellRange algos using this geo*/
	public void removeCellRangeUser() {
		if (cellRangeUsers > 0) {
			--cellRangeUsers;
		}
	}
	/** @return true if this can be renamed */
	public boolean isRenameable() {
		// don't allow renaming when this object is used in
		// cell ranges, see AlgoCellRange
		return cellRangeUsers == 0;
	}

	/**
	 * renames this GeoElement to newLabel.
	 * 
	 * @param labelNew new label
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
	 * @param labelNew new label
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
	 * @param label label
	 */
	public void setLoadedLabel(final String label) {
		if (labelSet) { // label was set before -> rename
			doRenameLabel(label);
		} else { // no label set so far -> set new label
			doSetLabel(getFreeLabel(label));
		}
	}

	/**
	 * @param caption1 raw caption
	 * @return true if new caption is not null
	 */
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
	
	/**
	 * 
	 * @return caption as stored in geo
	 */
	public String getCaptionSimple(){
		return caption;
	}

	/**
	 * Caption string (with substitutions)
	 * @param tpl string template
	 * @return caption (or label if caption is null)
	 */
	public String getCaption(StringTemplate tpl) {
		if (caption == null) {
			return getLabel(tpl);
		}

		// for speed, check first for a %
		if (caption.indexOf('%') < 0) {
			return caption;
		}
		StringBuilder captionSB = new StringBuilder();
		

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
	/** @return caption without substitution; returns "" if caption is null*/
	public String getRawCaption() {
		if (caption == null) {
			return "";
		}
		return caption;
	}

	/**
	 * @param tpl string template
	 * @return caption string with substitution or "" if caption is null
	 */
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

	/**
	 * @return true for local variables
	 */
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

		this.label = newLabel; // set new label
		labelSet = true;
		labelWanted = false; // got a label, no longer wanted

		cons.putLabel(this); // add new table entry
		algebraStringsNeedUpdate();
		updateSpreadsheetCoordinates();

		notifyAdd();
		/*if(cons.getCASdummies().contains(newLabel)){
			cons.moveInConstructionList(this, 0);
			cons.getCASdummies().remove(newLabel);
			for(int i=0;cons.getCasCell(i)!=null;i++){
				kernel.getAlgebraProcessor().processCasCell(cons.getCasCell(i));
			}
		}*/
	}

	private void updateSpreadsheetCoordinates() {
		if (labelSet && (label.length() > 0)
				&& StringUtil.isLetter(label.charAt(0)) // starts with letter
				&& StringUtil.isDigit(label.charAt(label.length() - 1))) // ends
																		// with
																		// digit
		{

			// init old and current spreadsheet coords
			if (spreadsheetCoords == null) {
				oldSpreadsheetCoords = null;
				spreadsheetCoords = new GPoint();
			} else {
				if (oldSpreadsheetCoords == null) {
					oldSpreadsheetCoords = new GPoint();
				}
				oldSpreadsheetCoords.setLocation(spreadsheetCoords);
			}

			// we need to also support wrapped GeoElements like
			// $A4 that are implemented as dependent geos (using ExpressionNode)
			final GPoint p = GeoElementSpreadsheet
					.spreadsheetIndices(getLabel(StringTemplate.defaultTemplate));

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
	 * @param col$ true if col has $
	 * @param row$ true if row has $
	 * @return spreadsheet reference name of this GeoElement with $ signs
	 */
	public String getSpreadsheetLabelWithDollars(final boolean col$,
			final boolean row$) {
		final String colName = GeoElementSpreadsheet
				.getSpreadsheetColumnName(spreadsheetCoords.x);
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

	/**
	 * compares labels alphabetically, but spreadsheet labels are sorted nicely
	 * eg A1, A2, A10 not A1, A10, A2
	 * @param label1 first label
	 * @param label2 second label
	 * @return negative/0/positive as in {@link Comparable#compareTo(Object)}
	 */
	final public static int compareLabels(final String label1,
			final String label2) {

		if (GeoElementSpreadsheet.isSpreadsheetLabel(label1)
				&& GeoElementSpreadsheet.isSpreadsheetLabel(label2)) {
			final GPoint p1 = GeoElementSpreadsheet
					.getSpreadsheetCoordsForLabel(label1);
			final GPoint p2 = GeoElementSpreadsheet
					.getSpreadsheetCoordsForLabel(label2);
			// Application.debug(label1+" "+p1.x+" "+p1.y+" "+label2+" "+p2.x+" "+p2.y);
			if (p1.x != p2.x) {
				return p1.x - p2.x;
			}
			return p1.y - p2.y;
		}

		return label1.compareTo(label2);

	}
	/** maximal line width*/
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
	 * @param labelPrefix prefix
	 * @param geos array of geos to be labeled
	 */
	public static void setLabels(final String labelPrefix,
			final GeoElement[] geos) {
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
			final GPoint p = GeoElementSpreadsheet
					.spreadsheetIndices(labelPrefix);
			if ((p.x >= 0) && (p.y >= 0)) {
				// more than one visible geo and it's a spreadsheet cell
				// use D1, E1, F1, etc as names
				final int col = p.x;
				final int row = p.y;
				for (int i = 0; i < geos.length; i++) {
					geos[i].setLabel(geos[i].getFreeLabel(GeoElementSpreadsheet
							.getSpreadsheetCellName(col + i, row)));
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
			final GeoElement[] geos) {
		setLabels(labels, geos, false);
	}
	/**
	 * Sets labels for given geos
	 * @param labels labels
	 * @param geos geos
	 * @param indexedOnly true for labels a_1,a_2,a_3,...
	 */
	static void setLabels(final String[] labels, final GeoElement[] geos,
			final boolean indexedOnly) {
		final int labelLen = (labels == null) ? 0 : labels.length;

		if ((labelLen == 1) && (labels[0] != null) && !labels[0].equals("")) {
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

			if (indexedOnly) {
				label = geos[i].getIndexLabel(label);
			}

			geos[i].setLabel(label);
		}
	}

	/**
	 * Get a free label. Try the suggestedLabel first
	 * 
	 * @param suggestedLabel label to be tried first
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

	/**
	 * @param isInteger flag for integers
	 * @return default label for this geo
	 */
	public String getDefaultLabel(final boolean isInteger) {
		return getDefaultLabel(null, isInteger);
	}

	/**
	 * @return deafult label for this geo (depends on type)
	 */
	public String getDefaultLabel() {
		return getDefaultLabel(null, false);
	}

	/**
	 * @param chars2 array of one-character labels for this GeoType
	 * @param isInteger true for integer sliders
	 * @return default label
	 */
	protected String getDefaultLabel(char[] chars2, final boolean isInteger) {
		char [] chars = chars2;
		if (chars == null) {
			if (isGeoPoint() && !(this instanceof GeoTurtle)) {
				// Michael Borcherds 2008-02-23
				// use Greek upper case for labeling points if language is Greek
				// (el)
				if (app.isUsingLocalizedLabels()) {
					if (app.languageIs(Language.Greek.locale)) {
						chars = greekUpperCase;
					} else if (app.languageIs(Language.Arabic.locale)) {
						// Arabic / Arabic (Morocco)
						chars = arabic;
					} else if (app.languageIs(Language.Yiddish.locale)) {
						chars = yiddish;
					} else {
						chars = pointLabels;
					}
				} else {
					chars = pointLabels;
				}

				final GeoPointND point = (GeoPointND) this;
				if (point.getMode() == Kernel.COORD_COMPLEX) {
					String complexLabel = "z_1";
					int i = 1;
					while (!cons.isFreeLabel(complexLabel)) {
						i++;
						if (i < 9) {
							// eg z_6
							complexLabel = "z_" + i;
						} else {
							// eg z_{12}
							complexLabel = "z_{" + i + "}";
						}
					}
					return complexLabel;
				}

			} else if (isGeoFunction()) {
				chars = functionLabels;
			} else if (isGeoLine()) {
				//name "edge" for segments from polyhedron
				if (isFromMeta() && !((FromMeta) this).getMeta().isGeoPolygon()) {
					int counter = 0;
					String str;
					final String name = app.getPlainLabel("edge"); // Name.edge
					do {
						counter++;
						str = name
								+ kernel.internationalizeDigits(counter + "",StringTemplate.defaultTemplate);
					} while (!cons.isFreeLabel(str));
					return str;
				}
				chars = lineLabels;
			} else if (this instanceof GeoPenStroke) {
				// needs to come before PolyLine (subclass)
				return defaultNumberedLabel("penStroke"); // Name.penStroke
			} else if (isGeoPolyLine()) {
				chars = lineLabels;
			} else if (isGeoConic()) {
				chars = conicLabels;
			} else if (isGeoVector() || isVector3DValue()) {
				chars = vectorLabels;
			} else if (isGeoAngle()) {
				chars = greekLowerCase;
			} else if (isGeoText()) {
				return defaultNumberedLabel("text"); // Name.text
			} else if (isGeoImage()) {
				return defaultNumberedLabel("picture"); // Name.picture
			} else if (isGeoLocus()) {
				
				if (algoParent.getClassName().equals(Commands.SolveODE)
						|| algoParent instanceof AlgoIntegralODE
						|| algoParent.getClassName().equals(Commands.NSolveODE)) {
					
					return defaultNumberedLabel("numericalIntegral"); // Name.numericalIntegral
					
				} else if (algoParent.getClassName().equals(Commands.SlopeField)) {
					
					return defaultNumberedLabel("slopefield"); // Name.slopefield
				} else if (algoParent instanceof GraphAlgo) {
					
					return defaultNumberedLabel("graph"); // Name.graph
				}
				
				return defaultNumberedLabel("locus"); // Name.locus
			} else if (isGeoTextField()) {
				return defaultNumberedLabel("textfield"); // Name.textfield
			} else if (isGeoButton()) {
				return defaultNumberedLabel("button"); // Name.button
			} else if (isGeoTurtle()) {
				return defaultNumberedLabel("turtle"); // Name.turtle
			} else if (isGeoList()) {
				final GeoList list = (GeoList) this;
				return defaultNumberedLabel(list.isMatrix() ? "matrix"
						: "list"); // Name.matrix / Name.list
			} else if (isInteger && isGeoNumeric()) {
				chars = integerLabels;
			} else {
				chars = lowerCaseLabels;
			}
		}

		int counter = 0, q, r;
		final StringBuilder sbDefaultLabel = new StringBuilder();
		boolean repeat = true;
		while (repeat) {
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
			
			//is label reserved
			repeat = !cons.isFreeLabel(sbDefaultLabel.toString(),true,true);
			
		}
		return sbDefaultLabel.toString();
	}

	private String defaultNumberedLabel(final String plainKey) {
		int counter = 0;
		String str;
		do {
			counter++;
			str = app.getPlainLabel(plainKey)
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

	/**
	 * @return true for textfields (=Input Boxes)
	 */
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
			//must be done in this order because doRemove destroys the link
			if (correspondingCasCell != null) {
				correspondingCasCell.doRemove();
			}
			doRemove();
			
		}
	}

	/**
	 *  removes this GeoElement and all its dependents
	 */
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
			//prevent update selection if construction will replace the geo
			app.removeSelectedGeo(this, false, !cons.isRemovingGeoToReplaceIt());
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
	
	private LaTeXCache latexCache = null;

	/**
	 * @return latex cache
	 */
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

	/**
	 * Notify kernel (and all views) about update
	 */
	final public void notifyUpdate() {
		kernel.notifyUpdate(this);

		// Application.debug("update " + label);
		// printUpdateSets();
	}
	/**
	 * Notify kernel (and all views) about update of auxiliary object
	 */
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
	 * @param algorithm algorithm directly dependent on this
	 */
	final public void addAlgorithm(final AlgoElement algorithm) {
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
	 * @param algorithm algo to be added
	 */
	final public void addToAlgorithmListOnly(final AlgoElement algorithm) {
		if (!getAlgorithmList().contains(algorithm)) {
			algorithmList.add(algorithm);
		}
	}

	/**
	 * Adds the given algorithm to the update set this GeoElement. Note: the
	 * algorithm is NOT added to the algorithm list, i.e. the dependency graph
	 * of the construction.
	 * 
	 * @param algorithm algorithm to be added
	 */
	final public void addToUpdateSetOnly(final AlgoElement algorithm) {
		addToUpdateSets(algorithm);
	}

	/**
	 * remove algorithm from dependency list of this GeoElement
	 * 
	 * @param algorithm algorithm to be removed
	 */
	final public void removeAlgorithm(final AlgoElement algorithm) {
		if (algorithmList != null) {
			algorithmList.remove(algorithm);
			removeFromUpdateSets(algorithm);
		}
	}

	/**
	 * @return set of all dependent algos in topological order
	 */
	public AlgorithmSet getAlgoUpdateSet() {
		if (algoUpdateSet == null) {
			algoUpdateSet = new AlgorithmSet();
		}

		return algoUpdateSet;
	}

	/**
	 * add algorithm to update sets up the construction graph
	 * @param algorithm algo to be added
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
	 * @param algorithm algo to be removed
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

	/**
	 * Same as update(), but do not notify kernel
	 */
	protected final void updateGeo() {

		if (labelWanted && !labelSet) {
			// check if this object's label needs to be set
			if (isVisible()) {
				setLabel(label);
			}
		}

		if (!cons.isUpdateConstructionRunning() && correspondingCasCell != null) {
			correspondingCasCell.setInputFromTwinGeo(false);
		}



		// texts need updates
		algebraStringsNeedUpdate();
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

	private void updateDependentObjects() {
		if ((correspondingCasCell != null) && isIndependent()) {
			updateAlgoUpdateSetWith(correspondingCasCell);
		} else if (algoUpdateSet != null) {
			// update all algorithms in the algorithm set of this GeoElement
			cons.setAlgoSetCurrentlyUpdated(algoUpdateSet);
			algoUpdateSet.updateAll();
			cons.setAlgoSetCurrentlyUpdated(null);
		}
	}

	/**
	 * Updates algoUpdateSet and secondGeo.algoUpdateSet together efficiently.
	 * @param secondGeo other geo whose update set needs an update
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
				final TreeSet<AlgoElement> tempAlgoSet = getTempSet();
				tempAlgoSet.clear();
				algoUpdateSet.addAllToCollection(tempAlgoSet);
				secondGeo.algoUpdateSet.addAllToCollection(tempAlgoSet);
				for (final AlgoElement algo : tempAlgoSet) {
					algo.update();
				}
			}
		}
	}

	public boolean hasAlgoUpdateSet(){
		return algoUpdateSet != null;
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
	 * @param geos geos to be updated
	 * 
	 * @param tempSet1
	 *            a temporary set that is used to collect all algorithms that
	 *            need to be updated
	 * 
	 * @param updateCascadeAll true to update cascade over dependent geos as well
	 */
	final static public synchronized void updateCascade(
			final ArrayList<? extends GeoElementND> geos,
			final TreeSet<AlgoElement> tempSet1,
			final boolean updateCascadeAll) 
	{		
	
		// only one geo: call updateCascade()
		if (geos.size() == 1) {
			final GeoElementND ce = geos.get(0);
			ce.updateCascade();
			return;
		}

		// build update set of all algorithms in construction element order
		// clear temp set
		tempSet1.clear();
		
		
		
			final int size = geos.size();
			for (int i = 0; i < size; i++) {
				final GeoElementND geo = geos.get(i);

		
				
				geo.update();								

				if ((geo.isIndependent() || geo.isPointOnPath() || updateCascadeAll)
						&& (geo.hasAlgoUpdateSet())) {
					// add all dependent algos of geo to the overall algorithm
					// set
					geo.getAlgoUpdateSet().addAllToCollection(tempSet1);
				}
			}
	
			// now we have one nice algorithm set that we can update
			if (tempSet1.size() > 0) {
				final Iterator<AlgoElement> it = tempSet1.iterator();
				while (it.hasNext()) {
					final AlgoElement algo = it.next();
					algo.update();
				}
			}	
		
		
	}
	
	/**
	 * Updates all objects in a cascade, but only location is updated for
	 * the locatables in input array
	 * @param geos locateables
	 */
	final static public synchronized void updateCascadeLocation(
			final ArrayList<Locateable> geos, Construction cons) 
	{		
		// build update set of all algorithms in construction element order
		// clear temp set
		final TreeSet<AlgoElement> tempSet1 = new TreeSet<AlgoElement>();
		
		final int size = geos.size();
			for (int i = 0; i < size; i++) {
				final Locateable geo = geos.get(i);
				
				geo.updateLocation();								

				if ((geo.isIndependent() || geo.isGeoText())
						&& (geo.hasAlgoUpdateSet())) {
					// add all dependent algos of geo to the overall algorithm
					// set
					geo.getAlgoUpdateSet().addAllToCollection(tempSet1);
				}
			}
				
			// remove algos currently updated
			AlgorithmSet algoSetCurrentlyUpdated = cons.getAlgoSetCurrentlyUpdated();
			if (algoSetCurrentlyUpdated!=null)
				algoSetCurrentlyUpdated.removeAllFromCollection(tempSet1);

			// now we have one nice algorithm set that we can update
			if (tempSet1.size() > 0) {
				final Iterator<AlgoElement> it = tempSet1.iterator();
				while (it.hasNext()) {
					try{
						final AlgoElement algo = it.next();
						algo.update();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}	
		
	}

	/**
	 * Updates all GeoElements in the given ArrayList and all algorithms that
	 * depend on free GeoElements in that list. Note: this method is more
	 * efficient than calling updateCascade() for all individual GeoElements.
	 * 
	 * @param geos geos to be updated
	 * 
	 * @param tempSet2
	 *            a temporary set that is used to collect all algorithms that
	 *            need to be updated
	 * @param lastAlgo stop cascade on this algo
	 */
	final static public void updateCascadeUntil(final ArrayList<?> geos,
			final TreeSet<AlgoElement> tempSet2,
			final AlgoElement lastAlgo) 
	{
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
				final Iterator<AlgoElement> it = tempSet2.iterator();
				while (it.hasNext()) {
					final AlgoElement algo = it.next();
	
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
	public void updateRepaint() {
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

	/**
	 * Updates visual properties and repaints this object
	 */
	public void updateVisualStyleRepaint() {

		updateVisualStyle();
		kernel.notifyRepaint();
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	final public String toString() {
		return toString(StringTemplate.defaultTemplate);
	}

	/*
	 * implementation of interface ExpressionValue
	 */
	public boolean isConstant() {
		return false;
	}

	public final boolean isLeaf() {
		return true;
	}

	/**
	 * Evaluates to number (if not numeric, returns undefined MyDouble)
	 * @return number or undefined double
	 */
	public NumberValue evaluateNum() {
		if(this instanceof NumberValue)
			return (NumberValue)this;
		return new MyDouble(getKernel(),Double.NaN);
	}
	
	final public ExpressionValue evaluate(StringTemplate tpl) {
		return this;
	}
	
	/**
	 * Returns just set with just one element (itself).
	 * Do not ever remove the final flag, it will break the update mechanism.
	 */
	public final HashSet<GeoElement> getVariables() {
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

	/** adds all predecessors of this object to the given set
	* the set is topologically sorted
	 * @param set set of predecessors
	* @param onlyIndependent whether only indpendent geos should be added
	*/
	final public void addPredecessorsToSet(final TreeSet<GeoElement> set,
			final boolean onlyIndependent) {
		if (algoParent == null) {
			set.add(this);
		} else { // parent algo
			algoParent.addPredecessorsToSet(set, onlyIndependent);
		}
	}

	/**
	 * @return set of all predecessor that can be randomized
	 */
	public TreeSet<GeoElement> getAllRandomizablePredecessors() {
		final TreeSet<GeoElement> set = new TreeSet<GeoElement>();
		addRandomizablePredecessorsToSet(set);
		return set;
	}

	/**
	 * @param set set of randomizable predecessors
	 */
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
	 * @param geo other geo
	 * @return true if geo depends on this object.
	 */
	final public boolean isParentOf(final GeoElement geo) {
		if (algoUpdateSet != null) {
			final Iterator<AlgoElement> it = algoUpdateSet
					.getIterator();
			while (it.hasNext()) {
				final AlgoElement algo = it.next();
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
	final public boolean hasChildren() {
		return (algorithmList != null) && (algorithmList.size() > 0);
	}

	/**
	 * Returns whether this object is dependent on geo.
	 * 
	 * @param geo other geo
	 * @return true if this object is dependent on geo.
	 */
	final public boolean isChildOf(final GeoElement geo) {
		if ((geo == null) || isIndependent()) {
			return false;
		}
		return geo.isParentOf(this);
	}

	/**
	 * Returns whether this object is dependent on other geo
	 * (or equal)
	 * @param geo other geo
	 * @return true if this object is dependent on other geo.
	 */
	final public boolean isChildOrEqual(final GeoElement geo) {
		return (this == geo) || isChildOf(geo);
	}

	/**
	 * Returns all children (of type GeoElement) that depend on this object.
	 * 
	 * @return set of all children of this geo
	 */
	final public TreeSet<GeoElement> getAllChildren() {
		final TreeSet<GeoElement> set = new TreeSet<GeoElement>();
		if (algoUpdateSet != null) {
			final Iterator<AlgoElement> it = algoUpdateSet
					.getIterator();
			while (it.hasNext()) {
				final AlgoElement algo = it.next();
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
	final public GeoElement[] getGeoElements() {
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
	final public int getConstructionIndex() {
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
	final public int getMinConstructionIndex() {
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
	final public int getMaxConstructionIndex() {
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
	final public String getDefinitionDescription(StringTemplate tpl) {
		if (algoParent == null) {
			return "";
		}
		return algoParent.toString(tpl);
	}

	/**
	 * @param addHTMLtag true to wrap in &lthtml>
	 * @return definition description as HTML
	 */
	final public String getDefinitionDescriptionHTML(final boolean addHTMLtag) {
		if (algoParent == null) {
			return "";
		}
		return indicesToHTML(app.translationFix(algoParent.toString(StringTemplate.defaultTemplate)),
				addHTMLtag);
	}

	@Override
	final public String getCommandDescription(StringTemplate tpl) {
		if (algoParent == null) {
			return "";
		}
		return algoParent.getCommandDescription(tpl);
	}

	/**
	 * @param addHTMLtag tue to wrap in &lt;HTML>
	 * @return HTML command description
	 */
	final public String getCommandDescriptionHTML(final boolean addHTMLtag) {
		if (algoParent == null) {
			return "";
		}
		return indicesToHTML(algoParent.getCommandDescription(StringTemplate.defaultTemplate), addHTMLtag);
	}

	@Override
	public int getRelatedModeID() {
		if (algoParent == null) {
			return -1;
		}
		return algoParent.getRelatedModeID();
	}

	/**
	 * Converts indices to HTML &lt;sub> tags if necessary.
	 * 
	 * @param text GGB string
	 * @return html HTML string
	 */
	public static String convertIndicesToHTML(final String text) {
		// check for index
		if (text.indexOf('_') > -1) {
			return indicesToHTML(text, true);
		}
		return text;
	}

	/**
	 * @param desc description
	 * @return label = description or label: description
	 */
	final public String addLabelTextOrHTML(final String desc) {
		String ret;

		final boolean includesEqual = desc.indexOf('=') >= 0;

		// check for function in desc like "f(x) = x^2"
		if (includesEqual && desc.startsWith(label + '(')) {
			ret = desc;
		} else {
			final StringBuilder sb = new StringBuilder();
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
		return convertIndicesToHTML(app.translationFix(ret));
	}

	/**
	 * @param addHTMLtag true to wrap in &lt;html>
	 * @param tpl string template
	 * @return HTML representation of caption
	 */
	final public String getCaptionDescriptionHTML(final boolean addHTMLtag,StringTemplate tpl) {

		return indicesToHTML(getCaptionDescription(tpl), addHTMLtag);
	}

	/**
	 * @return type string for XML
	 */
	final public String getXMLtypeString() {
		// don't use getTypeString() as it's overridden
		return StringUtil.toLowerCase(getGeoClassType().name);
	}

	/**
	 * Returns type string of GeoElement. 
	 * 
	 * @return type string without "Geo" prefix in most cases, overridden in eg GeoPoint, GeoPolygon
	 */
	public String getTypeString() {
		return getGeoClassType().name;
	}

	/**
	 * overridden in GeoConicND
	 * 
	 * @return object type 
	 */
	public String getTypeStringForAlgebraView() {
		return getTypeString();
	}

	/**
	 * @return localized type string
	 */
	public String translatedTypeString() {
		return app.getPlain(getTypeString());
	}

	/**
	 * @return localized type string for Algebra View
	 */
	public String translatedTypeStringForAlgebraView() {
		//App.debug(getTypeStringForAlgebraView());
		//App.debug(app.getPlain(getTypeStringForAlgebraView()));
		return app.getPlain(getTypeStringForAlgebraView());
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
	 * @param colored true to allow colors
	 * @param addHTMLtag true to wrap in &lt;html>
	 * @return description (type + label + definition)
	 */
	final public String getLongDescriptionHTML(final boolean colored,
			final boolean addHTMLtag) {
		if ((algoParent == null) || isTextValue() || this instanceof GeoPenStroke) {
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
			final GColor colorAdapter = AwtFactory.prototype.newColor(
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

	

	/**
	 * Returns long description for all GeoElements in given array,
	 * each geo on one line.
	 * 
	 * @param geos list of geos
	 * @param colored true to use colors
	 * @param addHTMLtag
	 *            true to wrap in &lt;html> ... &lt;/html>
	 * @param alwaysOn true to override default
	 * @return long description for all GeoElements in given array.
	 */
	final public static String getToolTipDescriptionHTML(
			final ArrayList<GeoElement> geos, final boolean colored,
			final boolean addHTMLtag, final boolean alwaysOn) {
		if (geos == null) {
			return null;
		}

		StringBuilder sbToolTipDesc = new StringBuilder();

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
			return getAlgebraDescriptionDefault();

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
	 * @param tpl string template
	 * 
	 * @return either value string or "undefined"
	 */
	final public String toDefinedValueString(StringTemplate tpl) {
		if (isDefined()) {
			return toValueString(tpl);
		}
		return app.getPlain("Undefined");
	}

	/**
	 * Returns algebraic representation of this GeoElement as Text. If this is
	 * not possible (because there are indices in the representation) a HTML
	 * string is returned.
	 * Default template is used, caching is employed.
	 * 
	 * @return algebraic representation of this GeoElement as Text
	 */
	
	final public String getAlgebraDescriptionTextOrHTMLDefault() {
		if (strAlgebraDescTextOrHTMLneedsUpdate) {
			final String algDesc = getAlgebraDescriptionDefault();
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

	/**
	 * @return algebra description
	 */
	final public String getAlgebraDescriptionHTMLDefault() {
		if (strAlgebraDescriptionHTMLneedsUpdate) {

			if (isGeoText()) {
				strAlgebraDescriptionHTML = indicesToHTML(toValueString(StringTemplate.defaultTemplate),
						false);
			} else {
				strAlgebraDescriptionHTML = indicesToHTML(
						getAlgebraDescriptionDefault(), false);
			}
			strAlgebraDescriptionHTMLneedsUpdate = false;
		}

		return strAlgebraDescriptionHTML;
	}
	
	/**
	 * @return type and label of a GeoElement (for tooltips and error messages)
	 */
	final public String getLabelTextOrHTML() {

		return getLabelTextOrHTML(true);
	}

	/**
	 * @param addHTMLTag says if html tags have to be added
	 * @return type and label of a GeoElement (for tooltips and error messages)
	 */
	final public String getLabelTextOrHTML(boolean addHTMLTag) {
		if (strLabelTextOrHTMLUpdate) {
			if (hasIndexLabel()) {
				strLabelTextOrHTML = indicesToHTML(getLabel(StringTemplate.defaultTemplate), addHTMLTag);
			} else {
				strLabelTextOrHTML = getLabel(StringTemplate.defaultTemplate);
			}
		}

		return strLabelTextOrHTML;
	}
	
	

	/**
	 * Returns algebraic representation (e.g. coordinates, equation) of this
	 * construction element.
	 * @param tpl string template
	 * @return algebraic representation (e.g. coordinates, equation)
	 */
	final public String getAlgebraDescription(StringTemplate tpl) {
		
			if (isDefined()) {
				return toString(tpl);
			}
			final StringBuilder sbAlgebraDesc = new StringBuilder();
			sbAlgebraDesc.append(label);
			sbAlgebraDesc.append(' ');
			sbAlgebraDesc.append(app.getPlain("Undefined"));
			return sbAlgebraDesc.toString();
			
	}
	/**
	 * Returns algebraic representation (e.g. coordinates, equation) of this
	 * construction element.
	 * Default string template is used => caching can be employed
	 * @return algebraic representation (e.g. coordinates, equation)
	 */
	final public String getAlgebraDescriptionDefault() {
		if (strAlgebraDescriptionNeedsUpdate) {
			if (isDefined()) {
				strAlgebraDescription = toString(StringTemplate.defaultTemplate);
			} else {
				final StringBuilder sbAlgebraDesc = new StringBuilder();
				sbAlgebraDesc.append(label);
				sbAlgebraDesc.append(' ');
				sbAlgebraDesc.append(app.getPlain("Undefined"));
				strAlgebraDescription = sbAlgebraDesc.toString();
			}

			strAlgebraDescriptionNeedsUpdate = false;
		}
		return strAlgebraDescription;
	}

	/**
	 * Returns simplified algebraic representation of this GeoElement. Used by
	 * the regression test output creator.
	 * @param tpl string template
	 * 
	 * @return sumplifiedrepresentation for regression test
	 */
	final public String getAlgebraDescriptionRegrOut(StringTemplate tpl) {
		if (strAlgebraDescriptionNeedsUpdate) {
			if (isDefined()) {
				strAlgebraDescription = toStringMinimal(tpl);
			} else {
				final StringBuilder sbAlgebraDesc = new StringBuilder();
				sbAlgebraDesc.append(app.getPlain("Undefined"));
				strAlgebraDescription = sbAlgebraDesc.toString();
			}

			strAlgebraDescriptionNeedsUpdate = false;
		} else {
			strAlgebraDescription = toStringMinimal(tpl);
		}

		return strAlgebraDescription;
	}
	/**
	 * ToString(tpl) by default, but may be overriden
	 * @param tpl string template
	 * @return string for regression output
	 */
	public String toStringMinimal(StringTemplate tpl) {
		return toString(tpl);
	}
	/**
	 * @return LaTeX description
	 */
	public String getLaTeXdescription() {
		if (strLaTeXneedsUpdate) {
			if (isDefined() && !isInfinite()) {
				strLaTeX = toLaTeXString(false,StringTemplate.latexTemplate);
			} else {
				strLaTeX = app.getPlain("Undefined");
			}
		}

		return strLaTeX;
	}

	/**
	 * Returns a string used to render a LaTeX form of the geo's algebra
	 * description.
	 * 
	 * @param substituteNumbers
	 *         true to replace variable names by values
	 * @param tpl string template
	 * @return string used to render a LaTeX form of the geo's algebra
	 *         description.
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
			sb.append(app.getPlain("Undefined"));

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
		else if (geo.isGeoText() && ((GeoText) geo).isLaTeX()) {
			sb.append(algebraDesc.split("=")[0]);
			sb.append("\\, = \\,");
			sb.append("\\text{``"); // left quote
			sb.append(((GeoText) geo).getTextString());
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

	/**
	 * @param str raw string
	 * @param addHTMLtag true to wrap in &lt;html>
	 * @return str with indices in HTML notation (&lt;sub>)
	 */
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

	/**
	 * returns type and label of a GeoElement as html string (for tooltips and
	 * error messages)
	 * 
	 * @param colored true to allow colors
	 * @param addHTMLtag true to wrap in &lt;html>
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
		if (!reverseOrder
				// want "xAxis" not "Line xAxis"
				&& !isAxis()) {
			// standard order: "point A"
			sbNameDescriptionHTML.append(typeString);
			sbNameDescriptionHTML.append(' ');
		}

		if (colored) {
			final GColor colorAdapter = AwtFactory.prototype.newColor(
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

		if (reverseOrder
				// want "xAxis" not "Line xAxis"
				&& !isAxis()) {
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

	/**
	 * @return whether this is GeoAxisND
	 */
	public boolean isAxis() {
		return false;
	}

	/**
	 * @return XML of this geo as string
	 */
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
		getCaptionXML(sb);

		getElementCloseTagXML(sb);

		
	}

	/**
	 * Appends open element tag &lt;element> or &lt;cascell> to the builder 
	 * @param sb string builder
	 */
	protected void getElementOpenTagXML(final StringBuilder sb) {
		final String type = getXMLtypeString();
		sb.append("<element");
		sb.append(" type=\"");
		sb.append(type);
		sb.append("\" label=\"");
		StringUtil.encodeXML(sb, label);
		if (defaultGeoType >= 0) {
			sb.append("\" default=\"");
			sb.append(defaultGeoType);
		}
		sb.append("\">\n");
	}

	/**
	 * Closes the element tag -- either &lt;element> or &lt;cascell> 
	 * @param sb string builder
	 */
	protected void getElementCloseTagXML(final StringBuilder sb) {
		sb.append("</element>\n");
	}
	/**
	 * Appends tags for click and update script to the builder
	 * @param sb string builder
	 */
	public void getScriptTags(final StringBuilder sb) {
		Script clickScript = scripts[EventType.CLICK.ordinal()];
		Script updateScript = scripts[EventType.UPDATE.ordinal()];
		if (clickScript != null) {
			sb.append("\t<");
			sb.append(clickScript.getXMLName());
			sb.append(" val=\"");
			StringUtil.encodeXML(sb, clickScript.getInternalText());
			sb.append("\"/>\n");
		}
		if (updateScript != null) {
			sb.append("\t<");
			sb.append(updateScript.getXMLName());
			sb.append(" onUpdate=\"");
			StringUtil.encodeXML(sb, updateScript.getInternalText());
			sb.append("\"/>\n");
		}
	}

	/**
	 * Appends caption XML tag to given builder
	 * @param sb string builder
	 */
	final public void getCaptionXML(StringBuilder sb) {
		// caption text
		if ((caption != null) && (caption.length() > 0)
				&& !caption.equals(label)) {
			
			sb.append("\t<caption val=\"");
			StringUtil.encodeXML(sb, caption);
			sb.append("\"/>\n");

		}
		
	}

	/**
	 * Append auxiliary XML tag to given builder
	 * @param sb string builder
	 */
	protected final void getAuxiliaryXML(final StringBuilder sb) {
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
	 * @param sb string builder
	 */
	protected void getXMLvisualTags(final StringBuilder sb) {
		getXMLvisualTags(sb, true);
	}
	/**
	 * Appends visual tags to string builder
	 * @param sb string builder
	 * @param withLabelOffset true to include label offsets
	 */
	protected void getXMLvisualTags(final StringBuilder sb,
			final boolean withLabelOffset) {
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
			if (!isVisibleInView(App.VIEW_EUCLIDIAN)) {
				// Application.debug("visible in ev1");
				EVs += 1; // bit 0
			}

			if (isVisibleInView(App.VIEW_EUCLIDIAN2)) {
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
				StringUtil.encodeXML(sb, colFunction.get(0).getLabel(tpl));
				sb.append('\"');
				sb.append(" dynamicg=\"");
				StringUtil.encodeXML(sb, colFunction.get(1).getLabel(tpl));
				sb.append('\"');
				sb.append(" dynamicb=\"");
				StringUtil.encodeXML(sb, colFunction.get(2).getLabel(tpl));
				sb.append('\"');
				if (colFunction.size() == 4) {
					sb.append(" dynamica=\"");
					StringUtil.encodeXML(sb, colFunction.get(3)
							.getLabel(tpl));
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

	/**
	 * @param sb string builder
	 */
	protected void getXMLanimationTags(final StringBuilder sb) {
		StringTemplate tpl =StringTemplate.xmlTemplate;
		// animation step width
		if (isChangeable()) {
			sb.append("\t<animation");
			final String animStep = animationIncrement == null ? "1"
					: getAnimationStepObject().getLabel(tpl);
			sb.append(" step=\"");
			StringUtil.encodeXML(sb, animStep);
			sb.append("\"");
			final String animSpeed = animationSpeedObj == null ? "1"
					: getAnimationSpeedObject().getLabel(tpl);
			sb.append(" speed=\"");
			StringUtil.encodeXML(sb, animSpeed);
			sb.append("\"");
			sb.append(" type=\"" + animationType + "\"");
			sb.append(" playing=\"");
			sb.append((isAnimating() ? "true" : "false"));
			sb.append("\"");
			sb.append("/>\n");
		}

	}

	/**
	 * Appends fixed tag to given builder
	 * @param sb string builder
	 */
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
	 * @param sb string builder
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
	 * @param sb string builder
	 * @param tpl string template
	 */
	public void getXMLtagsMinimal(final StringBuilder sb,StringTemplate tpl) {
		sb.append(toValueStringMinimal(tpl));
	}

	/**
	 * returns class-specific value string for getConstructionRegressionOut
	 * (default implementation, may be overridden in certain subclasses)
	 * @param tpl string template
	 * @return value string
	 */
	protected String toValueStringMinimal(StringTemplate tpl) {
		return toValueString(tpl);
	}

	/**
	 * returns the number in rounded format to 6 decimal places, in case of the
	 * number is very close to 0, it returns the exact value
	 * 
	 * @param number number to be formated
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
	 * Appends line type and line thickness as xml string to given builder.
	 * 
	 * @param sb string builder
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
	 * @param sb string builder
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
			StringUtil.encodeXML(sb, condShowObject.getLabel(StringTemplate.xmlTemplate));
			sb.append("\"/>\n");
			return sb.toString();
		}
		return "";
	}

	final public int getLineThickness() {
		return lineThickness;
	}

	/**
	 * @return minimum line thickness (normally 1, but 0 for polygons, integrals
	 *         etc)
	 */
	public int getMinimumLineThickness() {
		return 1;
	}

	final public int getLineType() {
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
	 * @param i line type for hidden lines
	 */
	public void setLineTypeHidden(final int i) {
		lineTypeHidden = i;
	}
	/**
	 * @param type new decoration type
	 */
	public void setDecorationType(final int type) {
		decorationType = type;
	}

	/**
	 * @return true for 3D GeoElements
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
	/**
	 * @return true for 3D geos with level of detail
	 */
	public boolean hasLevelOfDetail() {
		return false;
	}
	
	
	/**
	 * @return true for angles
	 */
	public boolean isGeoAngle() {
		return false;
	}

	/**
	 * @return true for booleans
	 */
	public boolean isGeoBoolean() {
		return false;
	}

	/**
	 * @return true for polylines
	 */
	public boolean isGeoPolyLine() {
		return false;
	}

	/**
	 * @return true for implicit polynomials
	 */
	public boolean isGeoImplicitPoly() {
		return false;
	}

	/**
	 * @return true for conics
	 */
	public boolean isGeoConic() {
		return false;
	}

	/**
	 * @return true for conic arcs/sectors
	 */
	public boolean isGeoConicPart() {
		return false;
	}

	/**
	 * @return true for valid functions
	 */
	public boolean isGeoFunction() {
		return false;
	}

	/**
	 * @return true for valid multivariate functions
	 */
	public boolean isGeoFunctionNVar() {
		return false;
	}

	/**
	 * @return true for boolean functions
	 */
	public boolean isGeoFunctionBoolean() {
		return false;
	}
	/**
	 * @return true for conditional functions
	 */
	public boolean isGeoFunctionConditional() {
		return false;
	}
	/**
	 * @return true for functionables
	 */
	public boolean isGeoFunctionable() {
		return false;
	}
	/**
	 * @return true for images
	 */
	public boolean isGeoImage() {
		return false;
	}
	/**
	 * @return true for turtles
	 */
	public boolean isGeoTurtle() {
		return false;
	}
	/**
	 * @return true for lines
	 */
	public boolean isGeoLine() {
		return false;
	}

	/**
	 * @return true for planes
	 */
	public boolean isGeoPlane() {
		return false;
	}
	/**
	 * @return true for loci
	 */
	public boolean isGeoLocus() {
		return false;
	}
	/**
	 * @return true for numbers
	 */
	public boolean isGeoNumeric() {
		return false;
	}
	/**
	 * @return true for (ND) points
	 */
	public boolean isGeoPoint() {
		return false;
	}
	/**
	 * @return true for CAS cells
	 */
	public boolean isGeoCasCell() {
		return false;
	}

	/*
	 * public boolean isGeoPoint3D() { return false; }
	 */
	/**
	 * @return true for polygons
	 */
	public boolean isGeoPolygon() {
		return false;
	}

	/**
	 * @return true for rays
	 */
	public boolean isGeoRay() {
		return false;
	}

	/**
	 * @return true for segments
	 */
	public boolean isGeoSegment() {
		return false;
	}
	/**
	 * @return true for texts
	 */
	public boolean isGeoText() {
		return false;
	}

	/**
	 * @return true for vectors
	 */
	public boolean isGeoVector() {
		return false;
	}

	/**
	 * @return true for cartesian curves
	 */
	public boolean isGeoCurveCartesian() {
		return false;
	}

	/**
	 * @return true for functions evaluable in CAS
	 */
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
	/**
	 * @param flag true to make this selected
	 */
	public void setSelected(final boolean flag) {
		selected = flag;
	}
	/**
	 * @param flag true to make this highlighted
	 */
	final public void setHighlighted(final boolean flag) {
		highlighted = flag;
	}
	/**
	 * @return true if highlighted or selected
	 */
	final public boolean doHighlighting() {
		return (highlighted || selected)
				&& (!isFixed() || isSelectionAllowed());
	}

	/**
	 * @return true if this object is selected
	 */
	final public boolean isSelected() {
		return selected;
	}

	public boolean isNumberValue() {
		return false;
	}

	/**
	 * @return true for angles
	 */
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
	/**
	 * @return true for buttons
	 */
	public boolean isGeoButton() {
		return false;
	}
	/**
	 * @return true if this is using visual defaults
	 */
	public boolean isUseVisualDefaults() {
		return useVisualDefaults;
	}

	/**
	 * @param useVisualDefaults true to use visual defaults
	 */
	public void setUseVisualDefaults(final boolean useVisualDefaults) {
		this.useVisualDefaults = useVisualDefaults;
	}

	/**
	 * @return true if this can have absoute screen location
	 */
	public boolean isAbsoluteScreenLocateable() {
		return false;
	}

	/**
	 * @return condition to show this geo
	 */
	final public GeoBoolean getShowObjectCondition() {
		return condShowObject;
	}

	/**
	 * @param cond new condition to show this geo
	 * @throws CircularDefinitionException if this == cond
	 */
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

	/**
	 * Removes condition to show object, if it is equal to the given one
	 * @param bool condition to show object
	 */
	final public void removeCondition(final GeoBoolean bool) {
		if (condShowObject == bool) {
			condShowObject = null;
		}
	}
	/**
	 * @return dynamic color as list of numbers {R,G,B} / {H,S,L} / {H,S,B}
	 */
	final public GeoList getColorFunction() {
		return colFunction;
	}

	/**
	 * @param col dynamic color as list of numbers {R,G,B} / {H,S,L} / {H,S,B}
	 */
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
			colFunction.registerColorFunctionListener(this);
		}
	}
	/**
	 * Removes dynamic color from this geo
	 */
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
	 * @param geosToMove geos to be moved
	 * @param rwTransVec translation vector
	 * @param endPosition
	 *            end position; may be null
	 * @param viewDirection direction of view
	 * @return true if something was moved
	 */
	public static boolean moveObjects(ArrayList<GeoElement> geosToMove,
			final Coords rwTransVec, final Coords endPosition,
			final Coords viewDirection) {
		//AbstractApplication.printStacktrace("XXX");
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
			if(geo.isGeoList()){
				moveObjectsUpdateList.add(geo);
				continue;
			}
			/*
			 * Michael Borcherds check for isGeoPoint() as it makes the mouse
			 * jump to the position of the point when dragging eg Image with one
			 * corner, Rigid Polygon and stops grid-lock working properly but is
			 * needed for eg dragging (a + x(A), b + x(B))
			 */
			//AbstractApplication.debug((geo.getParentAlgorithm() == null) + " "
			//		+ size + " " + geo.getClassName()+" "+geo.getLabel(StringTemplate.defaultTemplate));
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
	private static volatile TreeSet<AlgoElement> tempSet;
	
	private static Comparator<AlgoElement> algoComparator = new Comparator<AlgoElement>(){

		public int compare(AlgoElement o1, AlgoElement o2) {
			return o1.compareTo(o2);
		}
		
	};
	/**
	 * @return temporary set of algoritms
	 */
	protected static TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<AlgoElement>(algoComparator);
		}
		return tempSet;
	}

	/**
	 * @param rwTransVec translation vector
	 * @param endPosition end position
	 * @return true if successful
	 */
	protected boolean movePoint(final Coords rwTransVec,
			final Coords endPosition) {

		boolean movedGeo = false;

		final GeoPoint point = (GeoPoint) this;
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
					final GeoPoint p = ((AlgoDynamicCoordinatesInterface) getParentAlgorithm())
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
					final GeoText movedGeoText = (GeoText) this;
					if (movedGeoText.hasAbsoluteLocation()) {
						// absolute location: change location
						final GeoPoint loc = (GeoPoint) movedGeoText
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
	 * @param rwTransVec translation vector
	 * @param endPosition end position
	 * @param viewDirection view direction
	 * @param updateGeos geos to be updated
	 * @param tempMoveObjectList1 temporary list
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
	 * @param number changeable number
	 * @param updateGeos set of geos
	 * @param tempMoveObjectList1 temporary list
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
	public GPoint getSpreadsheetCoords() {
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
	 * @param spreadsheetCoords point (col,row)
	 */
	public void setSpreadsheetCoords(final GPoint spreadsheetCoords) {
		this.spreadsheetCoords = spreadsheetCoords;
	}

	/**
	 * @return old spreadsheet coords
	 */
	public GPoint getOldSpreadsheetCoords() {
		return oldSpreadsheetCoords;
	}
	/**
	 * @return true for macro outputs
	 */
	final public boolean isAlgoMacroOutput() {
		return isAlgoMacroOutput;
	}

	/**
	 * @param isAlgoMacroOutput mark/unmark this geo as macro output
	 */
	public void setAlgoMacroOutput(final boolean isAlgoMacroOutput) {
		this.isAlgoMacroOutput = isAlgoMacroOutput;
	}

	/**
	 * @author Michael Borcherds 
	 * @version 2008-04-30
	 * @param geo other geo
	 * @return true if these elements are algebraically equal
	 */
	public abstract boolean isEqual(GeoElement geo);

	/**
	 * Returns wheter this - f gives 0 in the CAS.
	 * 
	 * @param f other geo
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
			final String diff = kernel.evaluateGeoGebraCAS(diffSb.toString(),null);
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
	 * getFormulaString(StringType.LIBRE_OFFICE) eg sqrt {x}
	 * getFormulaString(StringType.GEOGEBRA) eg sqrt(x)
	 * getFormulaString(StringType.GEOGEBRA_XML)
	 * getFormulaString(StringType.JASYMCA)
	 * 
	 * @param tpl string template
	 * @param substituteNumbers true to substitute numbers
	 * @return formula string
	 */
	public String getFormulaString(final StringTemplate tpl,
			final boolean substituteNumbers) {

		String ret = "";

		// GeoFunction & GeoFunctionNVar override this, no need to care about them
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
			ret = tpl.printVariableName(label);
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
				ret = app.getPlain("Undefined");
			} else if ((Unicode.Infinity + "").equals(ret)) {
				ret = "\\infty";
			} else if ((Unicode.MinusInfinity + "").equals(ret)) {
				ret = "-\\infty";
			}
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
	 * @param traceFlag true to trace to spreadsheet
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
	
	/**
	 * Used by list to check if geos are compatible.
	 * @return has spreadsheet mode that is a traceable mode
	 */
	public boolean hasSpreadsheetTraceModeTraceable(){
		return isSpreadsheetTraceable();
	}

	private geogebra.common.util.SpreadsheetTraceSettings traceSettings;

	/**
	 * @return spreadsheet trace settings
	 */
	public SpreadsheetTraceSettings getTraceSettings() {
		
		if (traceSettings == null) {
			traceSettings = new SpreadsheetTraceSettings();
			//if only copy is possible, set it immediately
			if (getTraceModes()==TraceModesEnum.ONLY_COPY){
				traceSettings.doTraceGeoCopy = true;
			}
		}

		return traceSettings;

	}

	/**
	 * @param t spreadsheet trace settings
	 */
	public void setTraceSettings(final SpreadsheetTraceSettings t) {
		traceSettings = t;
	}

	/**
	 * over-ridden in GeoList
	 * @return element for properties dialog
	 */
	public GeoElement getGeoElementForPropertiesDialog() {
		return this;
	}

	/**
	 * over-ridden in GeoText
	 * @return true if this was created by Text command
	 */
	public boolean isTextCommand() {
		return false;
	}

	private boolean inTree = false;

	final public boolean isInTree() {
		return inTree;
	}

	final public void setInTree(final boolean flag) {
		inTree = flag;
	}

	/*
	 * Scripting
	 */

	private Script[] scripts = new Script[EventType.values().length];

	
	/**
	 * @param script script
	 */
	public void setClickScript(Script script) {
		setScript(script, EventType.CLICK);
	}

	/**
	 * Sets update script
	 * @param script script
	 */
	public void setUpdateScript(Script script) {
		setScript(script, EventType.UPDATE);
	}
	
	/**
	 * Set a script for this geo
	 * @param script source code for the new script
	 * @param evt the event type that will trigger the script
	 */
	public void setScript(Script script, EventType evt) {
		if (evt == EventType.UPDATE && !canHaveUpdateScript()
				|| evt == EventType.CLICK && !canHaveClickScript()) {
			return;
		}
		// Make sure we're listening to events for this script
		app.startGeoScriptRunner();
		Script oldScript = scripts[evt.ordinal()];
		if (oldScript != null) {
			oldScript.unbind(this, evt);
		}
		scripts[evt.ordinal()] = script;
		script.bind(this, evt);
	}
	
	/**
	 * @return true if this can have update script
	 */
	public boolean canHaveUpdateScript() {
		return true;
	}

	/**
	 * Return script for event type (localized if ggbscript)
	 * @param type event type
	 * @return script
	 */
	public Script getScript(EventType type) {
		return scripts[type.ordinal()];
	}
	
	/**
	 * Runs the click script of this object
	 * @param arg argument that replaces all %0 in the script
	 */
	public void runClickScripts(final String arg) {
		app.dispatchEvent(new Event(EventType.CLICK, this, arg));
	}

	private boolean showTrimmedIntersectionLines = false;

	/**
	 * @param show true to show trimmed lines
	 */
	public void setShowTrimmedIntersectionLines(final boolean show) {
		showTrimmedIntersectionLines = show;
	}

	/**
	 * @return true if showing trimmed lines
	 */
	public boolean getShowTrimmedIntersectionLines() {
		return showTrimmedIntersectionLines;
	}
	/**
	 * @return true for points in region
	 */
	public boolean isPointInRegion() {
		return false;
	}

	/**
	 * @param flag mark/unmark this geo as random
	 */
	public void setRandomGeo(final boolean flag) {
		isRandomGeo = flag;
	}

	private boolean isRandomGeo = false;

	/**
	 * @return true for random geos (numbers, lists)
	 */
	public boolean isRandomGeo() {
		return isRandomGeo;
	}

	/**
	 * Randomize this geo
	 */
	public void updateRandomGeo() {

		// update parent algorithm, like AlgoRandom
		final AlgoElement algo = getParentAlgorithm();
		if (algo != null) {
			algo.compute(); // eg AlgoRandom etc
		} else if (isGeoNumeric()) {
			((GeoNumeric) this).updateRandom();
		}
	}

	/**
	 * @return true if this implemnts MatrixTransformable
	 */
	public boolean isMatrixTransformable() {
		return false;
	}

	// =============================================
	// Control which views are allowed to add a geo.
	// G.Sturr, 2010-6-30
	// =============================================

	/**
	 * @param viewId view id
	 * @param setVisible true make this geo visible in given view
	 */
	public void setVisibility(final int viewId, final boolean setVisible) {
		if (setVisible) {
			if (!viewFlags.contains(viewId)) {
				viewFlags.add(viewId);
			}
		} else {
			viewFlags.remove(Integer.valueOf(viewId));
		}
	}

	/**
	 * @param viewId view id
	 * @return whether this geo is visible in given view
	 */
	public boolean isVisibleInView(final int viewId) {
		return viewFlags.contains(viewId);
	}

	// private Set<Integer> viewSet = new HashSet<Integer>();

	/**
	 * @param viewId view id
	 */
	public void addView(final int viewId) {
		setVisibility(viewId, true);
		// viewSet.add(view);
	}

	/**
	 * Make this invisible in given view
	 * @param viewId view id
	 */
	public void removeView(final int viewId) {
		setVisibility(viewId, false);
		// viewSet.remove(view);
	}
	
	/**
	 * Make this visible in given views
	 * @param flags list of view ids
	 */
	public void setViewFlags(List<Integer> flags){
		viewFlags.clear();
		viewFlags.addAll(flags);
		// Collections.copy(list, viewFlags);
	}

	/**
	 * @return set of views in which this is visible
	 */
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
				&& (isGeoElement3D() || isVisibleInView(App.VIEW_EUCLIDIAN));
	}

	/**
	 * @param selected2 true to allow selection
	 */
	
	public void setSelectionAllowed(final boolean selected2) {
		selectionAllowed = selected2;
	}

	/**
	 * @return true if selection is allowed
	 */
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
	 * @return true if current fill style is hatch
	 */
	public boolean isHatchingEnabled() {
		return fillType == FILL_HATCH;
	}

	/**
	 * @param angle hatching angle in degrees
	 */
	public void setHatchingAngle(final int angle) {
		hatchingAngle = angle;
	}

	/**
	 * @return hatching angle in degrees
	 */
	public double getHatchingAngle() {
		return hatchingAngle;
	}

	/**
	 * @param distance hatching distance
	 */
	public void setHatchingDistance(final int distance) {
		hatchingDistance = distance;
	}

	/**
	 * @return hatching distance
	 */
	public int getHatchingDistance() {
		return hatchingDistance;
	}

	/**
	 * @return fill image
	 */
	public GBufferedImage getFillImage() {

		return graphicsadapter.getFillImage();
	}

	// public void setFillImage(BufferedImage image){
	// this.fillImage = image;
	// }

	/**
	 * @param filename filename of fill image
	 */
	public void setFillImage(final String filename) {
		graphicsadapter.setFillImage(filename);
	}

	/**
	 * @return fill  type (standard/hatch/image)
	 */
	public int getFillType() {
		return fillType;
	}

	/**
	 * @param fillType new fill type
	 */
	public void setFillType(final int fillType) {
		this.fillType = fillType;
	}

	/**
	 * Tries to load the image using the given fileName.
	 * 
	 * @param fileName filename
	 */
	public void setImageFileName(final String fileName) {
		graphicsadapter.setImageFileName(fileName);
	}

	/**
	 * @return filename of fill image
	 */
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
	 * @param direction direction
	 */
	public void setMainDirection(final Coords direction) {
		mainDirection = direction;
	}

	/**
	 * gets shortest distance to point p overridden in eg GeoPoint, GeoLine for
	 * compound paths
	 * @param p other point
	 * @return distance
	 */
	public double distance(final GeoPoint p) {
		return Double.POSITIVE_INFINITY;
	}

	/**
	 * @param p point
	 * @return distance from point
	 */
	public double distance(final GeoPointND p) {
		if ((p instanceof GeoElement) && (p instanceof GeoPoint)) {
			return distance((GeoPoint) p);
		}
		App.debug("TODO : distance from " + getGeoClassType()
				+ " to ND point");
		return Double.POSITIVE_INFINITY;
	}

	/**
	 * @return true if this can have click script
	 */
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

	/**
	 * @return true if needs replacing in expression nodes
	 */
	public boolean needsReplacingInExpressionNode() {
		return needsReplacingInExpressionNode;
	}

	/**
	 * Call this to make sure that this is replaced in expression nodes
	 */
	public void setNeedsReplacingInExpressionNode() {
		needsReplacingInExpressionNode = true;
	}

	/**
	 * @return true for intervals
	 */
	public boolean isGeoInterval() {
		return false;
	}

	/**
	 * @return length or area, overriden in subclasses
	 */
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

	/**
	 * Srore copy of this geo in stack
	 */
	public void storeClone() {
		if (tempClone == null) {
			tempClone = new Stack<GeoElement>();
		}

		tempClone.push(copy());
		cloneInUse = true;
	}

	/**
	 * 
	 */
	public void recoverFromClone() {
		if (tempClone != null) {
			set(tempClone.pop());
		}
		cloneInUse = false;
	}

	/**
	 * Randomize for probability chacking
	 * overriden in subclasses that allow randomization
	 */
	public void randomizeForProbabilisticChecking() {
		// overode by subclasses
	}

	/**
	 * @return true if this allows randomization
	 */
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
	 * @param correspondingCasCell corresponding CAS cell
	 */
	final public void setCorrespondingCasCell(
			final GeoCasCell correspondingCasCell) {
		this.correspondingCasCell = correspondingCasCell;
	}

	/**
	 * @return true if the given GeoElement geo is to be drawn
	 * with LaTeX in AV/Spreadsheet
	 */
	public boolean isLaTeXDrawableGeo() {
		return false;
	}

	/**
	 * @return true if this has a background color
	 */
	public boolean hasBackgroundColor() {
		return false;
	}

	/**
	 * @param i algo
	 * @return true if algo is in update set
	 */
	public boolean algoUpdateSetContains(final AlgoElement i) {
		return getAlgoUpdateSet().contains(i);
	}

	
	/**
	 * Makes sure that column headings are empty list of GeoTexts
	 */
	protected void resetSpreadsheetColumnHeadings(){
		if (spreadsheetColumnHeadings == null) {
			spreadsheetColumnHeadings = new ArrayList<GeoText>();
		}
		else{
			spreadsheetColumnHeadings.clear();
		}
	}
	
	/**
	 * for the SpreadsheetTraceable interface. Default: just return the label
	 * @return list of column headings
	 */
	final public ArrayList<GeoText> getColumnHeadings() {
		
		//if no values / only copy
		if (getTraceSettings().doTraceGeoCopy) //update column headings for trace copy
			updateColumnHeadingsForTraceGeoCopy();
		else //update column headings for trace values
			updateColumnHeadingsForTraceValues();

		return spreadsheetColumnHeadings;
	}
	
	/** update column headings for trace values */
	public void updateColumnHeadingsForTraceValues(){
		//for NumberValue
		updateColumnHeadingsForTraceGeoCopy();
	}
	
	/**
	 * 
	 * @return string description of values traced
	 */
	public String getTraceDialogAsValues(){
		return getLabelTextOrHTML(false);//columnHeadingsForTraceDialog.toString();
	}
	

	
	/** Used by TraceDialog for "Trace as... value of/copy of */
	static public enum TraceModesEnum {
		/** no value for this geo, only copy*/
		ONLY_COPY, 
		/** one value / copy (e.g. text) */
		ONE_VALUE_OR_COPY, 
		/** one value / no copy (e.g. segment) */
		ONE_VALUE_ONLY, 
		/** at least two values (e.g. point) */
		SEVERAL_VALUES_OR_COPY,
		/** at least two values (e.g. point) */
		SEVERAL_VALUES_ONLY,
		/** not traceable */
		NOT_TRACEABLE
	}
	
	
	/**
	 * 
	 * @return possible modes for trace to spreadsheet
	 */
	public TraceModesEnum getTraceModes(){
		return TraceModesEnum.ONE_VALUE_ONLY;//default for NumberValue
	}
		
		
	
	
	/**
	 * 
	 * update column headings when "trace geo copy"
	 */
	protected  void updateColumnHeadingsForTraceGeoCopy(){
		resetSpreadsheetColumnHeadings();
		spreadsheetColumnHeadings.add(getNameGeo());
	}
	
	/**
	 * 
	 * @return geo text = Name[this]
	 */
	protected GeoText getNameGeo(){
		AlgoName algo = new AlgoName(cons, this);
		GeoText ret = algo.getGeoText();
		ret.setEuclidianVisible(false);
		return ret;
	}
	
	/**
	 * 
	 * @param node expression describing the text
	 * @return GeoText linked to expression
	 */
	protected GeoText getColumnHeadingText(ExpressionNode node){
		
		GeoText ret;
		
		if (node.getGeoElementVariables()==null){
			//no variables in expression node : compute only once
			ret = new GeoText(cons);
			AlgoDependentText.nodeToGeoText(node, ret, ret.getStringTemplate());
		}else{	
			AlgoDependentText algo = new AlgoDependentText(cons, node);
			algo.setProtectedInput(true);
			ret = algo.getGeoText();
		}
		
		ret.setEuclidianVisible(false);
		return ret;
	}
	
	private MyStringBuffer xBracket = null, yBracket = null, zBracket = null, closeBracket = null;
	
	/**  @return "x(" */
	protected MyStringBuffer getXBracket(){
		if (xBracket == null)
			xBracket = new MyStringBuffer(kernel, "x(");
		return xBracket;
	}
	
	/**  @return "y(" */
	protected MyStringBuffer getYBracket(){
		if (yBracket == null)
			yBracket = new MyStringBuffer(kernel, "y(");
		return yBracket;
	}
	
	/**  @return "z(" */
	protected MyStringBuffer getZBracket(){
		if (zBracket == null)
			zBracket = new MyStringBuffer(kernel, "z(");
		return zBracket;
	}
	
	/**  @return ")" */
	protected MyStringBuffer getCloseBracket(){
		if (closeBracket == null)
			closeBracket = new MyStringBuffer(kernel, ")");
		return closeBracket;
	}

	/**
	 * default for elements implementing NumberValue interface eg GeoSegment,
	 * GeoPolygon
	 * @param spreadsheetTraceList list of numbers for spreadsheet
	 */
	public void addToSpreadsheetTraceList(ArrayList<GeoNumeric> spreadsheetTraceList) {

		if (isNumberValue()) {

			final GeoNumeric xx = new GeoNumeric(cons,
					((NumberValue) this).getDouble());
			spreadsheetTraceList.add(xx);

		} else {
			App.debug("error in getSpreadsheetTraceList(), not a NumberValue");
		}

	}

	@Override
	public String toString(StringTemplate tpl) {
		return label;
	}
	
	final public ExpressionValue traverse(Traversing t){
		return t.process(this);
	}
	
	
	/**
	 * Says if this geo comes from a "meta geo", e.g. a segment coming from a polygon
	 * @return true if is from meta geo
	 */
	public boolean isFromMeta(){
		return false;
	}
	
	final public GeoElement unwrap(){
		return this;
	}
	
	final public ExpressionNode wrap(){
		return new ExpressionNode(getKernel(),this);
	}

	/**
	 * Creates a new {@link EquationElement} for this GeoElement given a {@link EquationScope}.
	 * @param scope given {@link EquationScope}
	 * @return a new {@link EquationElement}
	 */
	public EquationElementInterface createEquationElement(final EquationScopeInterface scope) {
		return this.getParentAlgorithm().buildEquationElementForGeo(this, scope);
	}
	
	@Override
	public boolean isLocusEquable() {
		return this.getParentAlgorithm() != null &&
				this.getParentAlgorithm().isLocusEquable();
	}

	/**
	 * @return whether to show pin in stylebar/geo context menu
	 */
	public boolean isPinnable() {
		return false;
	}

	/**
	 * @return whether this element has fixed screen location in some view
	 */
	final public boolean isPinned() {
		if (this instanceof AbsoluteScreenLocateable) {
			return ((AbsoluteScreenLocateable) this).isAbsoluteScreenLocActive();
		}
		
		if (!isPinnable()) {
			return false;
		}
		
		return getParentAlgorithm() instanceof AlgoAttachCopyToView;
	}
	
	public boolean hasCoords() {
		return false;
	}

	/**
	 * copies the scripts from another geo. Used when redefining (so that the scripts aren't "deleted")
	 * 
	 * @param oldGeo old GeoElement
	 */
	public void setScripting(GeoElement oldGeo) {
		
		for (int i = 0 ; i < oldGeo.scripts.length ; i++) {
			if (oldGeo.scripts[i] != null) {
				scripts[i] = oldGeo.scripts[i].copy();
			} else {
				scripts[i] = null;
			}
		}
		
	}	
}
