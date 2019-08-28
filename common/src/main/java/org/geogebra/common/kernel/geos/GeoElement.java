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

package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.serialize.TeXAtomSerializer;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.euclidian.draw.CanvasDrawable;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.factories.LaTeXFactory;
import org.geogebra.common.gui.dialog.options.model.AxisModel.IAxisModelListener;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.kernel.AnimationManager;
import org.geogebra.common.kernel.AutoColor;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.GTemplate;
import org.geogebra.common.kernel.GraphAlgo;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoAttachCopyToView;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadiusInterface;
import org.geogebra.common.kernel.algos.AlgoDependentText;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoIntegralODE;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoMacroInterface;
import org.geogebra.common.kernel.algos.AlgoName;
import org.geogebra.common.kernel.algos.AlgorithmSet;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.algos.DrawInformationAlgo;
import org.geogebra.common.kernel.algos.TableAlgo;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Functional;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyDoubleDegreesMinutesSeconds;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.properties.Auxiliary;
import org.geogebra.common.kernel.geos.properties.EquationType;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.script.JsScript;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.LaTeXCache;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.SpreadsheetTraceSettings;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.lang.Language;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.util.Greek;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * 
 * @author Markus
 * @version 2011-12-02
 */

public abstract class GeoElement extends ConstructionElement
		implements GeoElementND {

	/**
	 * Column headings for spreadsheet trace
	 */
	protected ArrayList<GeoText> spreadsheetColumnHeadings = null;

	/** min decimals or significant figures to use in editing string */
	public static final int MIN_EDITING_PRINT_PRECISION = 5;

	// maximum label offset distance
	private static final int MAX_LABEL_OFFSET = 80;
	/** maximal line width */
	public static final int MAX_LINE_WIDTH = 13;

	// private static int geoElementID = Integer.MIN_VALUE;

	private int tooltipMode = TOOLTIP_ALGEBRAVIEW_SHOWING;
	/** should only be used directly in subclasses */
	protected String label;
	private String realLabel; // for macro constructions, see setRealLabel() for
								// details
	private String oldLabel; // see doRenameLabel
	private String caption; // accessible via getRawCaption
	/** true if label is wanted, but not set */
	private boolean labelWanted = false;
	/** tue if label is set */
	private boolean labelSet = false;

	private boolean localVarLabelSet = false;
	private boolean euclidianVisible = true;
	private boolean forceEuclidianVisible = false;
	private boolean algebraVisible = true;
	private boolean labelVisible = true;
	private boolean algebraLabelVisible = true;
	private boolean isConsProtBreakpoint; // in construction protocol
	private boolean algoMacroOutput; // is an output object of a macro
										// construction
	/** fixed (cannot be moved or deleted) */
	protected boolean fixed = false;
	/** label, value, caption, label+value */
	public int labelMode = LABEL_DEFAULT;
	/** cartesian, polar or complex */
	protected int toStringMode = Kernel.COORD_CARTESIAN;
	/** default (foreground) color */
	protected GColor objColor = GColor.BLACK;
	/** background color */
	protected GColor bgColor = null; // none by default
	/** color when selected */
	protected GColor selColor = objColor;
	/** color for fill */
	protected GColor fillColor = objColor;
	private int layer = 0;
	private NumberValue animationIncrement;
	private GeoNumberValue animationSpeedObj;
	private GeoCasCell correspondingCasCell; // used by GeoCasCell
	private boolean animating = false;
	/** says if it's a pickable object */
	private boolean isPickable = true;
	private boolean hasPreviewPopup = false;
	private boolean needsReplacingInExpressionNode = false;

	private Stack<GeoElement> tempClone;
	private boolean cloneInUse = false;

	private boolean descriptionNeedsUpdateInAV = true;

	private int animationType = ANIMATION_OSCILLATING;
	private int animationDirection = 1;
	/** transparency */
	protected double alphaValue = 0.0f;
	/** angle of hatching */
	protected int hatchingAngle = 45; // in degrees
	/** distance of hatching */
	protected int hatchingDistance = 10;
	private boolean inverseFill = false;

	private String fillSymbol = null;

	/** The original location if any */
	private ScreenLocation screenLocation = null;
	// =================================
	// G.Sturr new fill options
	/**
	 * substitute for imageFileName and image - Arpad Fekete; // 2011-12-01
	 */
	protected GeoElementGraphicsAdapter graphicsadapter;
	/** offset for label in EV */
	public int labelOffsetX = 0;
	/** offset for label in EV */
	public int labelOffsetY = 0;

	private Auxiliary auxiliaryObject = Auxiliary.NO_DEFAULT;
	private boolean selectionAllowed = true;
	// on change: see setVisualValues()

	// spreadsheet specific properties
	private GPoint spreadsheetCoords;
	private GPoint oldSpreadsheetCoords;
	// number of AlgoCellRange using this cell: don't allow renaming when
	// greater 0
	private int cellRangeUsers = 0;

	/** condition to show object */
	protected GeoBoolean condShowObject;
	/** whether we should send value to CAS (for false we send the name) */
	protected boolean sendValueToCas = true;

	/**
	 * List of GeoNumerics to determine color { red, green, blue, alpha
	 * (optional) }
	 */
	protected GeoList colFunction;

	private boolean useVisualDefaults = true;
	/** true if color is set */
	private boolean isColorSet = false;
	/** true if geo is highlited */
	protected boolean highlighted = false;
	private boolean selected = false;
	private String strAlgebraDescription;
	private String strAlgebraDescTextOrHTML;
	private String strAlgebraDescriptionHTML;
	private String strLabelTextOrHTML;
	/** LaTeX string for LaTeX export */
	protected String strLaTeX;
	private boolean strAlgebraDescriptionNeedsUpdate = true;
	private boolean strAlgebraDescTextOrHTMLneedsUpdate = true;
	private boolean strAlgebraDescriptionHTMLneedsUpdate = true;
	private boolean strLabelTextOrHTMLUpdate = true;
	/** true if strLaTex is out of sync */
	protected boolean strLaTeXneedsUpdate = true;

	// line thickness and line type: s
	/**
	 * note: line thickness in Drawable is calculated as lineThickness / 2.0f
	 */
	private int lineThickness = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;
	/** line type (full, dashed, ...) see EuclidianStyleConstants.LINE_TYPE */
	public int lineType = EuclidianStyleConstants.DEFAULT_LINE_TYPE;
	/** line type for hidden parts (for 3D) */
	public int lineTypeHidden = EuclidianStyleConstants.DEFAULT_LINE_TYPE_HIDDEN;
	/** line opacity */
	protected int lineOpacity = 255;

	/** decoration type */
	private int decorationType = DECORATION_NONE;
	private EuclidianViewInterfaceSlim viewForValueString;

	private boolean autoColor;
	private boolean emptySpreadsheetCell = false;
	private LaTeXCache latexCache = null;
	private SpreadsheetTraceSettings traceSettings;
	/** Spreadsheet tracing on/off flag */
	private boolean spreadsheetTrace;

	private boolean inTree = false;

	private Script[] scripts = null;

	private boolean showTrimmedIntersectionLines = false;

	private boolean isRandomGeo = false;

	/** Flag for visibility in 3D view(s) */
	protected ExtendedBoolean visibleInView3D = ExtendedBoolean.UNKNOWN;
	/** Flag for visibility in plane view(s) */
	private ExtendedBoolean visibleInViewForPlane = ExtendedBoolean.UNKNOWN;

	private boolean canBeRemovedAsInput = true;

	private ExpressionNode definition;

	private int defaultGeoType = -1;
	/** parent algorithm */
	@Weak
	protected AlgoElement algoParent = null;
	/** draw algorithm */
	protected AlgoElement algoDraw = null;
	/** directly dependent algos */
	private ArrayList<AlgoElement> algorithmList;

	/** set of all dependent algos sorted in topological order */
	protected AlgorithmSet algoUpdateSet;

	/** fill type */
	protected FillType fillType = FillType.STANDARD;

	// =================================
	/** color space: RGB */
	final public static int COLORSPACE_RGB = 0;
	/** color space: HSB */
	final public static int COLORSPACE_HSB = 1;
	/** color space: HSL */
	final public static int COLORSPACE_HSL = 2;
	private int colorSpace = COLORSPACE_RGB;

	private List<Integer> viewFlags = null;

	private NumberFormatAdapter numberFormatter6;
	private static volatile TreeSet<AlgoElement> tempSet;

	private TeXFormula tf;
	private TeXAtomSerializer texAtomSerializer;

	private static Comparator<AlgoElement> algoComparator = new Comparator<AlgoElement>() {

		@Override
		public int compare(AlgoElement o1, AlgoElement o2) {
			return o1.compareTo(o2);
		}

	};

	@Override
	public int getColorSpace() {
		return colorSpace;
	}

	@Override
	public void setColorSpace(final int colorSpace) {
		this.colorSpace = colorSpace;
	}

	/**
	 * @return index for ConstructionDefaults or -1 if not default geo
	 */
	public int getDefaultGeoType() {
		return defaultGeoType;
	}

	@Override
	public boolean hasTableOfValues() {
		return this instanceof Functional && !this.isLimitedPath();
	}

	/**
	 * 
	 * @return true if a default geo
	 */
	public boolean isDefaultGeo() {
		return defaultGeoType != -1;
	}

	/**
	 * @param defaultGT
	 *            index for ConstructionDefaults
	 */
	public void setDefaultGeoType(final int defaultGT) {
		defaultGeoType = defaultGT;
	}

	/********************************************************/

	/**
	 * Creates new GeoElement for given construction
	 * 
	 * @param c
	 *            Construction
	 */
	public GeoElement(final Construction c) {
		super(c);
		c.addUsedType(this.getGeoClassType());
		graphicsadapter = kernel.getApplication()
				.newGeoElementGraphicsAdapter();
		// this.geoID = geoCounter++;

		// moved to subclasses, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		// setConstructionDefaults(); // init visual settings

		// new elements become breakpoints if only breakpoints are shown
		// isConsProtBreakpoint = cons.showOnlyBreakpoints();

		EuclidianViewInterfaceSlim ev;
		if ((kernel.getApplication() != null)
				&& ((ev = kernel.getApplication()
						.getActiveEuclidianView()) != null)
				&& (kernel.getApplication().getActiveEuclidianView()
						.getViewID() != App.VIEW_EUCLIDIAN)) {
			viewFlags = new ArrayList<>();
			viewFlags.add(ev.getViewID());

			// if ev isn't Graphics or Graphics 2, then also add 1st 2D
			// euclidian view
			if (!(ev.isDefault2D())) {
				viewFlags.add(App.VIEW_EUCLIDIAN);
			}
		}
	}

	/* ****************************************************** */

	@Override
	public String getLabelSimple() {
		return label;
	}

	@Override
	public void setLabelSimple(final String lab) {
		label = lab;
		GeoElementSpreadsheet.setBackgroundColor(this);
	}

	@Override
	public String getLabel(StringTemplate tpl) {
		if (!tpl.isUseRealLabels() || (realLabel == null)
				|| "".equals(realLabel)) {
			if (!isLabelSet() && !localVarLabelSet) {
				if (algoParent != null) {
					return algoParent.getDefinition(tpl);
				}
				if (definition != null) {
					return definition.toString(tpl);
				}
				return toOutputValueString(tpl);
			}
			return tpl.printVariableName(label);
		}
		return tpl.printVariableName(realLabel);
	}

	/**
	 * @param c
	 *            geo to receive the label copy
	 */
	public void copyLabel(final GeoElement c) {
		setLabelSimple(c.label);
	}

	@Override
	public void setLabelMode(final int mode) {

		if (isDefaultGeo()) {
			switch (mode) {
			case LABEL_NAME:
			case LABEL_NAME_VALUE:
			case LABEL_VALUE:
			case LABEL_CAPTION:
			case LABEL_CAPTION_VALUE:
				// old values for default geos: set label to default
				labelMode = LABEL_DEFAULT;
				break;

			default:
				labelMode = mode;
			}

			if (labelMode != LABEL_DEFAULT) {
				resetLabelSetting();
			}
		} else {
			switch (mode) {
			case LABEL_NAME_VALUE:
			case LABEL_DEFAULT_NAME_VALUE:
				labelMode = LABEL_NAME_VALUE;
				break;

			case LABEL_VALUE:
			case LABEL_DEFAULT_VALUE:
				labelMode = LABEL_VALUE;
				break;

			case LABEL_CAPTION: // Michael Borcherds 2008-02-18
			case LABEL_DEFAULT_CAPTION:
				labelMode = LABEL_CAPTION;
				break;
			case LABEL_CAPTION_VALUE:
				labelMode = LABEL_CAPTION_VALUE;
				break;
			case LABEL_DEFAULT:
				setLabelModeDefault();
				break;

			default:
				labelMode = LABEL_NAME;
			}
		}
	}

	private void resetLabelSetting() {
		App app = getKernel().getApplication();
		if (app != null) {
			app.getSettingsUpdater().getLabelSettingsUpdater()
					.resetLabelVisibilityForMenu();
		}
	}

	/**
	 * Switch label mode among value, name, value+name and caption from stylebar
	 * 
	 * @param index
	 *            stylebar index
	 */
	public void setLabelModeFromStylebar(final int index) {

		// set label to not visible
		if (index == 0) {
			setLabelVisible(false);
			if (isDefaultGeo()) {
				if (labelMode == LABEL_DEFAULT) {
					// set to default label mode
					setLabelModeDefault();
					// shift for LABEL_DEFAULT_NAME_VALUE, etc.
					labelMode += 5;
				}

				// tells app that no labeling style is selected
				resetLabelSetting();
			}
			return;
		}

		// set label to visible and mode
		setLabelVisible(true);
		final int mode = index - 1;
		if (isDefaultGeo()) {
			// shift for LABEL_DEFAULT_NAME_VALUE, etc.
			labelMode = mode + 5;
			resetLabelSetting();
		} else {
			switch (mode) {
			case LABEL_NAME_VALUE:
			case LABEL_DEFAULT_NAME_VALUE:
				labelMode = LABEL_NAME_VALUE;
				break;

			case LABEL_VALUE:
			case LABEL_DEFAULT_VALUE:
				labelMode = LABEL_VALUE;
				break;

			case LABEL_CAPTION: // Michael Borcherds 2008-02-18
			case LABEL_DEFAULT_CAPTION:
				labelMode = LABEL_CAPTION;
				break;

			case LABEL_DEFAULT:
				setLabelModeDefault();
				break;

			default:
				labelMode = LABEL_NAME;
			}
		}
	}

	/**
	 * set label mode to default mode
	 */
	protected void setLabelModeDefault() {
		labelMode = LABEL_NAME;
	}

	@Override
	public final int getLabelMode() {
		return labelMode;
	}

	/**
	 * return the label position (2D or 3D vector)
	 * 
	 * @return the label position (2D or 3D vector)
	 */
	public Coords getLabelPosition() {
		return Coords.O;
	}

	@Override
	public abstract GeoClass getGeoClassType();

	@Override
	public abstract GeoElement copy();

	/**
	 * overridden in GeoList so that the list elements are copied too (needed
	 * for tracing to spreadsheet)
	 * 
	 * @return copy of this object
	 */
	public GeoElement deepCopyGeo() {
		return copy();
	}

	@Override
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
	 *            construction
	 * @param points
	 *            array of points
	 * @return copy of points in construction cons
	 */
	public static GeoPointND[] copyPointsND(final Construction cons,
			final GeoPointND[] points) {
		final GeoPointND[] pointsCopy = new GeoPointND[points.length];
		for (int i = 0; i < points.length; i++) {
			pointsCopy[i] = (GeoPointND) points[i].copyInternal(cons);
			((GeoElement) pointsCopy[i]).set(points[i]);
		}

		return pointsCopy;
	}

	@Override
	public ExpressionValue deepCopy(final Kernel kernel1) {
		// default implementation: changed in some subclasses
		return copy();
	}

	@Override
	public void resolveVariables(EvalInfo info) {
		// do nothing
	}

	@Override
	public boolean isInfinite() {
		return false;
	}

	@Override
	public abstract void set(GeoElementND geo);

	@Override
	public abstract boolean isDefined();

	@Override
	public abstract void setUndefined();

	@Override
	public abstract String toValueString(StringTemplate tpl);

	/**
	 * sets a view for building the value string
	 * 
	 * @param view
	 *            view
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

	@Override
	public String getRedefineString(final boolean useChangeable,
			final boolean useOutputValueString) {

		StringTemplate tpl = StringTemplate.editTemplate;
		String ret = "";
		final boolean isIndependent = !isPointOnPath() && useChangeable
				? isChangeable() : isIndependent();
		if (isIndependent && getDefinition() == null) {
			ret = useOutputValueString ? toOutputValueString(tpl)
					: toValueString(tpl);
		} else if (getParentAlgorithm() != null) {
			ret = getParentAlgorithm().getDefinition(tpl);
		} else if (getDefinition() != null) {
			ret = getDefinition().toString(tpl);
		}

		return ret;
	}

	/**
	 * Returns the character which is used between label and definition
	 * 
	 * @return for conics, implicit polynomials and inequalities, = otherwise
	 */
	public char getLabelDelimiter() {
		return '=';
	}

	/**
	 * 
	 * @return label and delimiter.
	 */
	protected String getLabelDelimiterWithSpace() {
		return getLabelDelimiter() == '=' ? " = " : getLabelDelimiter() + " ";
	}

	@Override
	public String getDefinitionForInputBar() {
		return getDefinitionForInputBar(StringTemplate.editTemplate);
	}

	/**
	 * @return definition for LaTeX editor
	 */
	public String getDefinitionForEditor() {
		return getDefinitionForInputBar(StringTemplate.editorTemplate);
	}

	/**
	 * @param tpl
	 *            template
	 * @return definition for LaTeX editor, no label
	 */
	public String getDefinitionNoLabel(StringTemplate tpl) {
		String ret = getDefinition(tpl);
		if ("".equals(ret)) {
			ret = toValueString(tpl);
		}
		return ret;
	}

	private String getDefinitionForInputBar(StringTemplate stringTemplate) {
		// for expressions like "3 = 2 A2 - A1"
		// getAlgebraDescription() returns "3 = 5"
		// so we need to use getCommandDescription() in those cases

		String inputBarStr = getDefinition(stringTemplate);
		if (!"".equals(inputBarStr)) {

			// check needed for eg f(x) = g(x) + h(x), f(x) = sin(x)
			// beware correct vars for f(t) = t + a
			if (isAlgebraLabelVisible()) {
				inputBarStr = getAssignmentLHS(stringTemplate)
					+ getLabelDelimiterWithSpace()
						+ inputBarStr;
			}

		} else {
			inputBarStr = getAlgebraDescription(stringTemplate);
		}

		return inputBarStr;
	}

	@Override
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

	@Override
	public String toOutputValueString(StringTemplate tpl) {
		if (isLocalVariable()) {
			return label;
		}
		return toValueString(tpl);
	}

	/**
	 * Set visual style from defaults
	 */
	final public void setConstructionDefaults() {
		setConstructionDefaults(true, true);
	}

	/**
	 * Set visual style from defaults
	 * 
	 * @param setEuclidianVisible
	 *            If eucldianVisible should be set
	 * @param setAuxiliaryProperty
	 *            if auxiliary property should be set
	 */
	final public void setConstructionDefaults(boolean setEuclidianVisible,
			boolean setAuxiliaryProperty) {

		if (useVisualDefaults) {
			final ConstructionDefaults consDef = cons.getConstructionDefaults();
			if (consDef != null) {
				consDef.setDefaultVisualStyles(this, false,
						setEuclidianVisible, setAuxiliaryProperty);
			}
		}
	}

	@Override
	public void setObjColor(final GColor color) {
		isColorSet = !isDefaultGeo() || !isGeoNumeric();
		objColor = color == null ? GColor.BLACK : color;
		fillColor = objColor;
		setAlphaValue(alphaValue);

		// selColor = getInverseColor(objColor);
		if (color != null) {
			int alpha = getGeoClassType() != GeoClass.NUMERIC ? 51 : 100;
			selColor = GColor.newColor(color.getRed(), color.getGreen(),
					color.getBlue(), alpha);
		}
	}

	@Override
	public boolean isColorSet() {
		return isColorSet;
	}

	// Michael Borcherds 2008-04-02
	private GColor getRGBFromList(double alpha1) {
		double alpha2 = alpha1;
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
				final double val = geo.evaluateDouble();
				switch (i) {
				default:
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
		switch (this.colorSpace) {

		case GeoElement.COLORSPACE_HSB:

			final int rgb = GColor.fromHSBtoRGB(redD, greenD, blueD);
			redD = (rgb >> 16) & 0xFF;
			greenD = (rgb >> 8) & 0xFF;
			blueD = rgb & 0xFF;
			return GColor.newColor((int) redD, (int) greenD, (int) blueD,
					alpha);

		case GeoElement.COLORSPACE_HSL:

			// algorithm taken from wikipedia article:
			// http://en.wikipedia.org/wiki/HSL_and_HSV

			final double H = redD * 6;
			final double S = greenD;
			final double L = blueD;

			final double C = (1 - Math.abs((2 * L) - 1)) * S;
			final double X = C * (1 - Math.abs((H % 2) - 1));

			double R1 = 0, G1 = 0, B1 = 0;

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

			final GColor c = GColor.newColor((int) ((R1 + m) * 255.0),
					(int) ((G1 + m) * 255.0), (int) ((B1 + m) * 255.0), alpha);
			return c;

		case GeoElement.COLORSPACE_RGB:
		default:
			return GColor.newColor((int) (redD * 255.0), (int) (greenD * 255.0),
					(int) (blueD * 255.0), alpha);

		}

	}

	@Override
	public GColor getSelColor() {
		if (colFunction == null) {
			return selColor;
		}
		return getRGBFromList(100);
	}

	@Override
	public GColor getFillColor() {
		if (colFunction == null) {
			return getShowHideColor(fillColor);
		}
		return getShowHideColor(getRGBFromList(getAlphaValue()));
	}

	@Override
	public GColor getAlgebraColor() {
		return GColor.updateForWhiteBackground(objColor);
	}

	@Override
	public GColor getLabelColor() {
		return getObjectColor();
	}

	@Override
	public GColor getBackgroundColor() {
		return bgColor;
	}

	/**
	 * 
	 * @param bgCol
	 *            new background color
	 */
	public void setBackgroundColor(final GColor bgCol) {
		bgColor = bgCol;
	}

	/**
	 * 
	 * @return current color for this object
	 */
	// Michael Borcherds 2008-04-02
	@Override
	public GColor getObjectColor() {
		GColor col = objColor;

		try {
			if (colFunction != null) {
				col = getRGBFromList(255);
			}
		} catch (final Exception e) {
			removeColorFunction();
		}

		return getShowHideColor(col);
	}

	/**
	 * @return true, if the geo is hidden, but currently should be shown,
	 *         because Show/Hide tool selected
	 */
	public boolean isHideShowGeo() {
		return isSelected() && (kernel.getApplication()
						.getMode() == EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
	}

	/**
	 * @param col
	 *            original color of the geo
	 * @return the original color, with more opacity if needed (because hidden
	 *         object, and show/hide tool selected
	 */
	public GColor getShowHideColor(GColor col) {
		if (isHideShowGeo()) {
			return GColor.newColor(col.getRed(), col.getGreen(), col.getBlue(),
					col.getAlpha() / 2);
		}
		return col;
	}

	@Override
	public void setLayer(int layer2) {
		int newlayer = layer2;

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

	@Override
	public final int getLayer() {
		return layer;
	}

	/**
	 * 
	 * @return drawing priority (lower = drawn first)
	 */
	private int typePriority() {
		return getGeoClassType().getPriority(isIndependent());
	}

	/**
	 * Compare drawing priority with another object
	 * 
	 * @param other
	 *            the other object
	 * @param checkLastHitType
	 *            whether hits on boundary should be preferred to hits on
	 *            filling
	 * @return whether this should be drawn fist
	 */
	public boolean drawBefore(GeoElement other, boolean checkLastHitType) {

		if (this.getLayer() < other.getLayer()) {
			return true;
		}

		if (this.getLayer() > other.getLayer()) {
			return false;
		}

		if (checkLastHitType) {
			if (this.getLastHitType() == HitType.ON_BOUNDARY
					&& other.getLastHitType() != HitType.ON_BOUNDARY) {
				return false;
			}

			if (this.getLastHitType() != HitType.ON_BOUNDARY
					&& other.getLastHitType() == HitType.ON_BOUNDARY) {
				return true;
			}
		}

		if (this.typePriority() < other.typePriority()) {
			return true;
		}

		if (this.typePriority() > other.typePriority()) {
			return false;
		}

		if (this.getConstructionIndex() < other.getConstructionIndex()) {
			return true;
		}

		if (this.getConstructionIndex() > other.getConstructionIndex()) {
			return false;
		}
		if (this.getParentAlgorithm() instanceof AlgoMacroInterface) {
			return ((AlgoMacroInterface) this.getParentAlgorithm())
					.drawBefore(this, other);
		}
		// Log.warn("Objects "+this+" and "+other+" have the same drawing
		// priority.");
		return true;
	}

	@Override
	public void setAlphaValue(final double alpha) {
		if ((fillColor == null) || (alpha < 0.0f) || (alpha > 1.0f)) {
			return;
		}
		alphaValue = alpha;
		fillColor = GColor.newColor(fillColor.getRed(), fillColor.getGreen(),
				fillColor.getBlue(), (int) (255 * alpha));
	}

	@Override
	public double getAlphaValue() {
		return getAlphaValueWhenVisible();
	}

	private double getAlphaValueWhenVisible() {
		if ((colFunction == null) || (colFunction.size() == 3)) {
			return alphaValue;
		}

		final GeoElement geo = colFunction.get(3);
		if (geo.isDefined()) {
			double alpha = geo.evaluateDouble();

			// ensure between 0 and 1
			alpha = (alpha / 2) - Math.floor(alpha / 2);
			if (alpha > 0.5) {
				alpha = 2 * (1 - alpha);
			} else {
				alpha = 2 * alpha;
			}
			return alpha;
		}
		return alphaValue;
	}

	@Override
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

	@Override
	public boolean isGeoList() {
		return false;
	}

	@Override
	final public void setAllVisualProperties(final GeoElement geo,
			final boolean keepAdvanced) {
		setAllVisualProperties(geo, keepAdvanced, true);
	}

	/**
	 * Sets all visual values from given GeoElement. This will also affect
	 * tracing, label location and the location of texts for example.
	 * 
	 * @param geo
	 *            source geo
	 * @param keepAdvanced
	 *            true to skip copying color function and visibility condition
	 * @param setAuxiliaryProperty
	 *            if sets auxiliary property
	 */
	final public void setAllVisualProperties(final GeoElement geo,
			final boolean keepAdvanced, final boolean setAuxiliaryProperty) {

		euclidianVisible = geo.euclidianVisible;
		visibleInView3D = geo.visibleInView3D;
		algebraLabelVisible = geo.algebraLabelVisible;
		setAllVisualPropertiesExceptEuclidianVisible(geo, keepAdvanced,
				setAuxiliaryProperty);
	}

	/**
	 * Sets all visual values from given GeoElement, EXCEPT euclidianVisible :
	 * needed for apply defaults on slider/angle.
	 * 
	 * This will also affect tracing, label location and the location of texts
	 * for example.
	 * 
	 * @param geo
	 *            source geo
	 * @param keepAdvanced
	 *            true to skip copying color function and visibility condition
	 * @param setAuxiliaryProperty
	 *            if sets auxiliary property
	 */
	public void setAllVisualPropertiesExceptEuclidianVisible(
			final GeoElement geo, final boolean keepAdvanced, boolean setAuxiliaryProperty) {
		if (keepAdvanced) {
			setVisualStyle(geo, setAuxiliaryProperty);
		} else {
			setAdvancedVisualStyle(geo, setAuxiliaryProperty);
		}

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
			((GeoPoint) this).setSpreadsheetTrace(
					((GeoPoint) geo).getSpreadsheetTrace());
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
					// do nothing
				}

			}
		}

		if (isSpreadsheetTraceable() && geo.getSpreadsheetTrace()) {
			setSpreadsheetTrace(true);
			traceSettings = geo.traceSettings;
		}
	}

	@Override
	public final void setVisualStyleForTransformations(final GeoElement geo) {
		setVisualStyle(geo);
		setFixed(false);
		updateVisualStyle(GProperty.COMBINED);
	}

	@Override
	final public void setVisualStyle(final GeoElement geo) {
		setVisualStyle(geo, true);
	}

	/**
	 * set visual style to geo
	 * 
	 * @param geo
	 *            geo
	 * @param setAuxiliaryProperty
	 *            if setting auxiliary property
	 */
	public void setVisualStyle(final GeoElement geo,
			boolean setAuxiliaryProperty) {

		// label style
		labelVisible = geo.getLabelVisible();
		setLabelMode(geo.getLabelMode());
		tooltipMode = geo.getTooltipMode();

		// style of equation, coordinates, ...
		if (getGeoClassType() == geo.getGeoClassType()
				&& (kernel.getApplication().getSettings() == null
						|| kernel.getApplication().getSettings()
								.getCasSettings().isEnabled())) {
			toStringMode = geo.toStringMode;
		}

		// colors
		setColorVisualStyle(geo);

		// line thickness and line type:
		// note: line thickness in Drawable is calculated as lineThickness /
		// 2.0f
		setLineThickness(geo.getLineThickness());
		setLineType(geo.getLineType());
		setLineTypeHidden(geo.getLineTypeHidden());
		setDecorationType(geo.getDecorationType());
		setLineOpacity(geo.getLineOpacity());

		if (setAuxiliaryProperty) {
			// set whether it's an auxilliary object
			setAuxiliaryObject(geo.isAuxiliaryObject());
		}
		setAnimationStep(geo.getAnimationStep());
		setAnimationType(geo.getAnimationType());
		// set fixed
		setFixedFrom(geo);

		// if layer is not zero (eg a new object has layer set to
		// ev.getMaxLayerUsed())
		// we don't want to set it
		if (layer == 0) {
			setLayer(geo.getLayer());
		}
	}

	/**
	 * set color from source geo
	 * 
	 * @param geo
	 *            source geo
	 */
	protected void setColorVisualStyle(final GeoElement geo) {
		if (geo.isAutoColor()) {
			setObjColor(geo.getAutoColorScheme()
					.getNext(!cons.getKernel().isSilentMode()));
		} else {
			objColor = geo.objColor;
			selColor = geo.selColor;
		}

		if (geo.isFillable()) {
			if (geo.isAutoColor()) {
				fillColor = objColor;
				setAlphaValue(geo.getAlphaValue());
			} else {
				fillColor = geo.fillColor;
			}
			setFillType(geo.fillType);
			hatchingAngle = geo.hatchingAngle;
			hatchingDistance = geo.hatchingDistance;
			graphicsadapter.setImageFileName(
					geo.getGraphicsAdapter().getImageFileName());
			alphaValue = geo.alphaValue;
		} else {
			fillColor = geo.objColor;
			setAlphaValue(geo.getAlphaValue());
		}
		bgColor = geo.bgColor;
		isColorSet = geo.isColorSet();
	}

	/**
	 * @return whether sequential color is used, makes sense only for default
	 *         geos
	 */
	public boolean isAutoColor() {
		return this.autoColor;
	}

	/**
	 * @param sequential
	 *            whether sequential color is used, makes sense only for default
	 *            geos
	 */
	public void setAutoColor(boolean sequential) {
		this.autoColor = sequential;
	}

	/**
	 * @return auto color scheme
	 */
	@Override
	public AutoColor getAutoColorScheme() {
		if (cons.getApplication().isUnbundled()) {
			return AutoColor.CURVES_GRAPHING;
		}
		return AutoColor.CURVES;
	}

	@Override
	public void setAdvancedVisualStyle(final GeoElement geo) {
		setAdvancedVisualStyle(geo, true);
	}

	/**
	 * Also copy advanced settings of this object.
	 *
	 * @param geo
	 *            source geo
	 * @param setAuxiliaryProperty
	 *            if setting auxiliary property
	 */
	public void setAdvancedVisualStyle(final GeoElement geo,
			boolean setAuxiliaryProperty) {
		setVisualStyle(geo, setAuxiliaryProperty);

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
			// CircularException, we ignore it
		}
	}

	@Override
	public final void setAdvancedVisualStyleCopy(final GeoElementND geo) {
		// copy color function
		if (geo.getColorFunction() != null) {
			setColorFunction(geo.getColorFunction().deepCopyGeo());
			setColorSpace(geo.getColorSpace());
		}

		// copy ShowObjectCondition, unless it generates a
		// CirclularDefinitionException
		if (geo.getShowObjectCondition() != null) {
			try {
				setShowObjectCondition(geo.getShowObjectCondition().copy());
			} catch (final Exception e) {
				// CircularException, we ignore it
			}
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
	 * 		private static Color getInverseColor(Color c) { float[] hsb =
	 *         Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
	 *         hsb[0] += 0.40; if (hsb[0] > 1) hsb[0]--; hsb[1] = 1; hsb[2] =
	 *         0.7f; return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
	 * 
	 *         }
	 */

	/**
	 * Moves label by updating label offset
	 * 
	 * @param xcoord
	 *            label x-offset
	 * @param ycoord
	 *            label y-offset
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

	@Override
	final public boolean isVisible() {
		return isEuclidianVisible() || isAlgebraVisible();
	}

	@Override
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

	@Override
	public void setEuclidianVisible(final boolean visible) {
		euclidianVisible = visible;
	}

	@Override
	public void setEuclidianVisibleIfNoConditionToShowObject(
			final boolean visible) {
		if (condShowObject == null) {
			setEuclidianVisible(visible);
		}
	}

	/**
	 * Forces drawing this in EV
	 * 
	 * @param visible
	 *            true to force drawing this in EV
	 */
	public void forceEuclidianVisible(final boolean visible) {
		forceEuclidianVisible = visible;
	}

	@Override
	public final boolean isSetEuclidianVisible() {
		return euclidianVisible;
	}

	@Override
	final public boolean isConsProtocolBreakpoint() {
		return isConsProtBreakpoint;
	}

	/**
	 * @param flag
	 *            true to make this a breakpoint
	 */
	public void setConsProtocolBreakpoint(final boolean flag) {
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

	@Override
	public boolean isDrawable() {
		return true;
	}

	@Override
	public boolean isFillable() {
		return false;
	}

	/** @return true if inverse fill is posible */
	public boolean isInverseFillable() {
		return false;
	}

	@Override
	public boolean isTraceable() {
		return false;
	}

	/** @return true if this is fixed (moving forbidden, deleting OK) */
	public boolean isLocked() {
		return fixed;
	}

	private void setFixedFrom(GeoElement geo) {
		boolean flag = geo.isLocked();
		if (geo.isDefaultGeo() && !flag) {
			fixed = false;
		} else {
			setFixed(flag);
		}
	}

	@Override
	public void setFixed(boolean flag) {
		if (!flag) {
			fixed = kernel.getApplication().isExamStarted()
					&& AlgebraItem.isFunctionOrEquationFromUser(this)
					&& !this.isDefaultGeo();
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
	 * @return true if fix/unfix button is shown in properties
	 */
	public boolean showFixUnfix() {
		return isFixable();
	}

	@Override
	final public void removeOrSetUndefinedIfHasFixedDescendent() {
		// can't delete a fixed object at all
		if (isProtected(EventType.REMOVE)) {
			return;
		}
		boolean hasFixedDescendent = false;

		final Set<GeoElement> tree = getAllChildren();
		final Iterator<GeoElement> it = tree.iterator();
		while (it.hasNext() && !hasFixedDescendent) {
			if (it.next().isProtected(EventType.REMOVE)) {
				hasFixedDescendent = true;
			}
		}

		if (hasFixedDescendent) {
			// Application.debug("hasFixedDescendent, not deleting");
			setUndefined();
			updateRepaint();
		} else {
			remove();
			kernel.notifyRemoveGroup();
		}

	}

	@Override
	final public boolean isAuxiliaryObject() {
		return auxiliaryObject.isOn();
	}

	/**
	 * @return true for loci, texts and images
	 */
	public boolean isAuxiliaryObjectByDefault() {
		return false;
	}

	@Override
	final public GeoElement toGeoElement() {
		return this;
	}

	@Override
	public void setAuxiliaryObject(final boolean flag) {
		if (auxiliaryObject.isOn() != flag) {
			auxiliaryObject = auxiliaryObject.toggle();
			if (isLabelSet()) {
				notifyUpdateAuxiliaryObject();
			}
		}
	}

	/**
	 * set auxiliary property
	 * 
	 * @param flag
	 *            flag
	 */
	public void setAuxiliaryObject(final Auxiliary flag) {
		if (auxiliaryObject != flag) {
			boolean oldIsOn = auxiliaryObject.isOn();
			auxiliaryObject = flag;
			if (isLabelSet() && oldIsOn != flag.isOn()) {
				notifyUpdateAuxiliaryObject();
			}
		}
	}

	@Override
	public void setLabelVisible(final boolean visible) {
		labelVisible = visible;
	}

	@Override
	public boolean isLabelVisible() {
		return labelVisible && isLabelSet();
	}

	/**
	 * 
	 * @return value of labelVisible
	 */
	final public boolean getLabelVisible() {
		return labelVisible;
	}

	@Override
	public boolean isAlgebraLabelVisible() {
		return algebraLabelVisible;
	}

	@Override
	public void setAlgebraLabelVisible(boolean algebraLabelVisible) {
		this.algebraLabelVisible = algebraLabelVisible;
	}

	/**
	 * Returns whether the label can be shown in Euclidian view.
	 * 
	 * @return true if label can be shown
	 */
	public boolean isLabelShowable() {
		return isDrawable() && !(this instanceof TextValue || isGeoImage()
				|| isGeoButton() || isGeoLocus()
				|| (isGeoBoolean() && !isIndependent()));
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
			if (!(kernel.getApplication().isUsingFullGui()
					&& kernel.getApplication().showView(App.VIEW_ALGEBRA))) {
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
	 * @param colored
	 *            true to use colors (HTML)
	 * @param alwaysOn
	 *            true to override default behavior
	 * @return tooltip text as HTML
	 */
	public String getTooltipText(final boolean colored,
			final boolean alwaysOn) {
		if (getParentAlgorithm() instanceof AlgoAttachCopyToView) {
			return "";
		}

		StringTemplate tpl = StringTemplate.defaultTemplate;
		switch (tooltipMode) {
		default:
		case TOOLTIP_ALGEBRAVIEW_SHOWING:
			if (!alwaysOn) {
				if (!(kernel.getApplication().isUsingFullGui() && kernel
						.getApplication().showView(App.VIEW_ALGEBRA))) {
					return "";
				}
			}
			// else fall through:
		case TOOLTIP_ON:

			getLoc().setTooltipFlag();
			// old behaviour

			String ret = getLongDescriptionHTML(colored, false);
			getLoc().clearTooltipFlag();

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
	public final int getTooltipMode() {
		return tooltipMode;
	}

	@Override
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
	 * @param visible
	 *            whether this is allowed to appear in AV
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

	@Override
	public boolean isAlgebraViewEditable() {
		return true;
	}

	@Override
	final public boolean isEuclidianShowable() {
		return showInEuclidianView();
	}

	/**
	 * @return true if user can toggle euclidian visibility
	 */
	final public boolean isEuclidianToggleable() {
		return isEuclidianShowable() && getShowObjectCondition() == null
				&& (!isGeoBoolean() || isIndependent());
	}

	/**
	 * @return true if showable in AV
	 */
	public boolean isAlgebraShowable() {
		return showInAlgebraView();
	}

	/**
	 * @param algorithm
	 *            algorithm responsible for computation of this object
	 */
	public void setParentAlgorithm(final AlgoElement algorithm) {
		algoParent = algorithm;
	}

	@Override
	final public AlgoElement getParentAlgorithm() {
		return algoParent;
	}

	@Override
	public void setDrawAlgorithm(final DrawInformationAlgo algorithm) {
		if (algorithm instanceof AlgoElement) {
			algoDraw = (AlgoElement) algorithm;
		}
	}

	@Override
	final public AlgoElement getDrawAlgorithm() {
		if (algoDraw == null) {
			return algoParent;
		}
		return algoDraw;
	}

	@Override
	final public ArrayList<AlgoElement> getAlgorithmList() {
		if (algorithmList == null) {
			algorithmList = new ArrayList<>();
		}
		return algorithmList;
	}

	@Override
	public boolean isIndependent() {
		return (algoParent == null) && (this.getCorrespondingCasCell() == null
				|| !this.getCorrespondingCasCell().hasVariablesOrCommands());
	}

	@Override
	public boolean isChangeable() {
		return !isProtected(EventType.UPDATE) && isIndependent();
	}

	/**
	 * @return whether this is changeable by dragging the pointer
	 */
	public boolean isPointerChangeable() {
		return !isLocked() && isIndependent();
	}

	@Override
	public boolean isPointOnPath() {
		return false;
	}

	/**
	 * Returns whether this object may be redefined
	 * 
	 * @return whether this object may be redefined
	 */
	public boolean isRedefineable() {
		return !isProtected(EventType.UPDATE)
				&& kernel.getApplication().letRedefine()
				&& !(this instanceof TextValue) && isAlgebraViewEditable()
				&& (isChangeable() || // redefine changeable (independent and
										// not fixed)
						!isIndependent()); // redefine dependent object
	}

	/**
	 * @param type
	 *            event type (UPDATE or REMOVE)
	 * @return whether this is protected against deleting and editing
	 */
	public boolean isProtected(EventType type) {
		return !kernel.getLoadingMode() && isLocked()
				&& this.getSpreadsheetCoords() != null
				&& (type == EventType.REMOVE || !(this instanceof GeoFunction));
	}

	@Override
	public boolean isMoveable() {
		return isPointerChangeable();
	}

	/**
	 * 
	 * @return true if we can move it with 6 degrees of freedom input device
	 */
	public boolean is6dofMoveable() {
		return false;
	}

	@Override
	public boolean isMoveable(final EuclidianViewInterfaceSlim view) {
		return view.isMoveable(this);
	}

	@Override
	public boolean hasMoveableInputPoints(
			final EuclidianViewInterfaceSlim view) {
		// allow only moving of certain object types
		switch (getGeoClassType()) {
		case CONIC:
		case CONIC3D:

			// special case for Circle[A, r]
			if (getParentAlgorithm() instanceof AlgoCirclePointRadiusInterface) {
				return containsOnlyMoveableGeos(getFreeInputPoints(view));
			}

			//$FALL-THROUGH$

		case CONICPART:
		case IMAGE:
		case LINE:
		case LINE3D:
		case RAY:
		case RAY3D:
		case SEGMENT:
		case SEGMENT3D:
		case TEXT:
			return hasOnlyFreeInputPoints(view)
					&& containsOnlyMoveableGeos(getFreeInputPoints(view));

		case POLYGON:
		case POLYGON3D:
		case POLYLINE:
		case POLYLINE3D:
		case PENSTROKE:
			return containsOnlyMoveableGeos(getFreeInputPoints(view));

		case VECTOR:
		case VECTOR3D:
			if (hasOnlyFreeInputPoints(view)
					&& containsOnlyMoveableGeos(getFreeInputPoints(view))) {
				// check if first free input point is start point of vector
				final ArrayList<GeoPointND> freeInputPoints = getFreeInputPoints(
						view);
				if (freeInputPoints.size() > 0) {
					final GeoPointND firstInputPoint = freeInputPoints.get(0);
					final GeoPointND startPoint = ((Locateable) this)
							.getStartPoint();
					return (firstInputPoint == startPoint);
				}
			}
			break;
		case ANGLE:
			break;
		case ANGLE3D:
			break;
		case AXIS:
			break;
		case AXIS3D:
			break;
		case BOOLEAN:
			break;
		case BUTTON:
			break;
		case CAS_CELL:
			break;
		case CLIPPINGCUBE3D:
			break;
		case CONICSECTION:
			// TODO make moveable?
			break;
		case CURVE_CARTESIAN:
			break;
		case CURVE_CARTESIAN3D:
			break;
		case DEFAULT:
			break;
		case FUNCTION:
			break;
		case FUNCTION_NVAR:
			break;
		case IMPLICIT_POLY:
			break;
		case INTERVAL:
			break;
		case LIST:
			break;
		case LOCUS:
			break;
		case NET:
			break;
		case NUMERIC:
			break;
		case PLANE3D:
			break;
		case POINT:
			break;
		case POINT3D:
			break;
		case POLYHEDRON:
			break;
		case QUADRIC:
			break;
		case QUADRIC_LIMITED:
			break;
		case QUADRIC_PART:
			break;
		case SPACE:
			break;
		case SURFACECARTESIAN:
		case SURFACECARTESIAN3D:
			break;
		case TEXTFIELD:
			break;
		case TURTLE:
			break;
		default:
			break;
		}

		return false;
	}

	@Override
	public ArrayList<GeoPointND> getFreeInputPoints(
			final EuclidianViewInterfaceSlim view) {
		if (algoParent == null) {
			return null;
		}
		return view.getFreeInputPoints(algoParent);
	}

	/**
	 * @param view
	 *            view
	 * @return whether all input points are free in given view
	 */
	final public boolean hasOnlyFreeInputPoints(
			final EuclidianViewInterfaceSlim view) {
		if (algoParent == null) {
			return false;
		}
		// special case for edge of polygon
		if (algoParent instanceof AlgoJoinPointsSegment
				&& (view.getFreeInputPoints(algoParent).size() == 2)) {
			return true;
		}

		return view.getFreeInputPoints(algoParent)
				.size() == algoParent.input.length;
	}

	private static boolean containsOnlyMoveableGeos(
			final ArrayList<GeoPointND> geos) {
		if ((geos == null) || (geos.size() == 0)) {
			return false;
		}

		for (int i = 0; i < geos.size(); i++) {
			final GeoElement geo = (GeoElement) geos.get(i);
			if (!geo.isMoveable()) {
				return false;
			}
		}
		return true;
	}

	@Override
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
		return isPointerChangeable() && (this instanceof PointRotateable);
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
	 * @param s
	 *            animation step
	 */
	public void setAnimationStep(final double s) {
		setAnimationStep(new MyDouble(kernel, s));
	}

	/**
	 * @param v
	 *            animation step
	 */
	public void setAnimationStep(final NumberValue v) {
		animationIncrement = v;
	}

	@Override
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
	public NumberValue getAnimationStepObject() {
		if (animationIncrement == null) {
			return null;
		}
		return animationIncrement;
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
	 * @param speed
	 *            new speed
	 */
	public void setAnimationSpeedObject(final GeoNumberValue speed) {
		animationSpeedObj = speed;
	}

	/**
	 * @param speed
	 *            new speed
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
	 * @param type
	 *            animation type (ANIMATION_*)
	 */
	final public void setAnimationType(final int type) {
		switch (type) {
		default:
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
	 * @param flag
	 *            true to make this animating
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
	 * 
	 * @return true if this can be animated
	 */
	public boolean isAnimatable() {
		return false;
	}

	@Override
	public String toLaTeXString(final boolean symbolic, StringTemplate tpl) {
		return getFormulaString(tpl, !symbolic);
		// if (symbolic)
		// return toString();
		// else
		// return toDefinedValueString();
	}

	/**
	 * Returns a String that can be used to define geo in the currently used
	 * CAS. For example, "f(x) := a*x^2", "a := 20", "g := 3x + 4y = 7"
	 * 
	 * @param tpl
	 *            StringType.Giac
	 * @return String in the format of the current CAS.
	 */
	public String toCasAssignment(final StringTemplate tpl) {
		if (!isLabelSet()) {
			return null;
		}

		final String body = toValueString(tpl);
		return getAssignmentLHS(tpl) + " := " + body;

	}

	/**
	 * @param tpl
	 *            string template
	 * @return left hand side for assignment (label or e.g. label(x))
	 */
	public String getAssignmentLHS(StringTemplate tpl) {
		return getLabel(tpl);
	}

	/**
	 * Returns a representation of geo in currently used CAS syntax. For
	 * example, "a*x^2"
	 * 
	 * @param tpl
	 *            string template
	 * 
	 * @param symbolic
	 *            true to keep variable names
	 * @return representation of this geo for CAS
	 */
	public String getCASString(StringTemplate tpl, final boolean symbolic) {
		return symbolic && !isIndependent() ? getDefinition(tpl)
				: toValueString(tpl);
	}

	/*
	 * ******************************************************* GeoElementTable
	 * Management Hashtable: String (label) -> GeoElement
	 * ******************************************************
	 */
	/** increment number of cellRange algos using this geo */
	public void addCellRangeUser() {
		++cellRangeUsers;
	}

	/** decrement number of cellRange algos using this geo */
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

	@Override
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
			throw new MyError(getLoc(), "NameUsed", newLabel);
		}
	}

	@Override
	public boolean isLabelSet() {
		return labelSet;
	}

	@Override
	public final void setLabel(String labelNew) {

		String newLabel = labelNew;
		if (cons.isSuppressLabelsActive()) {
			if (kernel.getApplication().getGuiManager() != null
					&& kernel.getApplication().getGuiManager()
					.hasSpreadsheetView()) {
				kernel.getApplication().getGuiManager().getSpreadsheetView()
						.scrollIfNeeded(this, labelNew);
			}
			return;
		}

		// don't want any '$'s in actual labels
		if ((newLabel != null) && (newLabel.indexOf('$') > -1)) {
			newLabel = newLabel.replaceAll("\\$", "");
		}

		labelWanted = true;

		// had no label: try to set it
		if (!isLabelSet()) {
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
				setLabelSimple(newLabel);

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

	@Override
	public void setLoadedLabel(final String label) {
		if (isLabelSet()) { // label was set before -> rename
			doRenameLabel(label);
		} else { // no label set so far -> set new label
			if (label.startsWith("c_") && this instanceof GeoNumeric) {
				cons.setCasCellUpdate(false);
			}
			doSetLabel(getFreeLabel(label));
			cons.setCasCellUpdate(false);
		}
	}

	@Override
	public boolean setCaption(String caption1) {
		String caption2 = caption1;
		if ((caption2 == null) || caption2.equals(label)) {
			this.caption = null;
			return false;
		}

		// workaround for unintended feature of old Input Boxes
		// &nbsp and &nbsp; both used to work
		if (caption2.indexOf("&nbsp") > -1) {
			caption2 = caption2.replaceAll("&nbsp;", Unicode.NBSP + "");
			caption2 = caption2.replaceAll("&nbsp", Unicode.NBSP + "");
		}

		caption2 = caption2.trim();

		if (caption2.isEmpty()) {
			this.caption = null;
			return true;
		}

		this.caption = caption2;
		return true;
	}

	/**
	 * 
	 * @return caption as stored in geo
	 */
	public String getCaptionSimple() {
		return caption;
	}

	@Override
	public String getCaption(StringTemplate tpl) {
		if (caption == null) {
			return getLabel(tpl);
		}

		// for speed, check first for a %
		if (caption.indexOf('%') < 0) {
			return caption;
		}
		return CaptionBuilder.getCaption(caption, this, tpl);
	}

	@Override
	public String getRawCaption() {
		if (caption == null) {
			return "";
		}
		return caption;
	}

	/**
	 * @param tpl
	 *            string template
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
		// needed for GGB-810
		boolean addToConstr = true;
		if (cons.isFileLoading() && this instanceof GeoNumeric
				&& newLabel.startsWith("c_")) {
			GeoElement geo = cons.lookupLabel(newLabel);
			// remove from construction duplicate of constant
			if (geo != null) {
				cons.removeFromConstructionList(geo);
				cons.removeLabel(geo);
			}
		}
		// hack needed for web for file loading with algebra view
		else {
			GeoElement geo = cons.lookupLabel(newLabel);
			// remove from construction duplicate of constant
			if (geo instanceof GeoNumeric
					&& this instanceof GeoNumeric) {
				try {
					cons.replace(geo, this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				geo = this;
				if (!((GeoNumeric) geo).isDependentConst()) {
					((GeoNumeric) geo).setIsDependentConst(true);
				}
				addToConstr = false;
			}
		}
		// UPDATE KERNEL
		if (!isLabelSet() && isIndependent()) {
			// add independent object to list of all Construction Elements
			// dependent objects are represented by their parent algorithm
			if (addToConstr) {
				cons.addToConstructionList(this, true);
			}
		}

		setLabelSimple(newLabel); // set new label

		setLabelSet(true);
		labelWanted = false; // got a label, no longer wanted

		if (this instanceof GeoNumeric && newLabel.startsWith("c_")) {
			GeoNumeric geoNum = cons.lookupConstantLabel(newLabel);
			if (geoNum != null) {
				((GeoNumeric) this).setIsDependentConst(true);
			}
		}

		if (addToConstr) {
			cons.putLabel(this); // add new table entry
		}
		algebraStringsNeedUpdate();
		updateSpreadsheetCoordinates();

		if (addToConstr) {
			notifyAdd();
		}
		/*
		 * if(cons.getCASdummies().contains(newLabel)){
		 * cons.moveInConstructionList(this, 0);
		 * cons.getCASdummies().remove(newLabel); for(int
		 * i=0;cons.getCasCell(i)!=null;i++){
		 * kernel.getAlgebraProcessor().processCasCell(cons.getCasCell(i)); } }
		 */
	}

	private void updateSpreadsheetCoordinates() {
		// starts with letter and ends with digit
		if (isLabelSet() && (label.length() > 0)
				&& com.himamis.retex.editor.share.input.Character
						.isLetter(label.charAt(0))
				&& StringUtil.isDigit(label.charAt(label.length() - 1))) {

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
			final GPoint p = GeoElementSpreadsheet.spreadsheetIndices(
					getLabel(StringTemplate.defaultTemplate));

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
	 * @param colDollar
	 *            true if col has $
	 * @param rowDollar
	 *            true if row has $
	 * @return spreadsheet reference name of this GeoElement with $ signs
	 */
	public String getSpreadsheetLabelWithDollars(final boolean colDollar,
			final boolean rowDollar) {
		final String colName = GeoElementSpreadsheet
				.getSpreadsheetColumnName(spreadsheetCoords.x);
		final String rowName = Integer.toString(spreadsheetCoords.y + 1);

		final StringBuilder sb = new StringBuilder(label.length() + 2);
		if (colDollar) {
			sb.append('$');
		}
		sb.append(colName);
		if (rowDollar) {
			sb.append('$');
		}
		sb.append(rowName);
		return sb.toString();
	}

	/**
	 * compares labels alphabetically, but spreadsheet labels are sorted nicely
	 * eg A1, A2, A10 not A1, A10, A2
	 * 
	 * @param label1
	 *            first label
	 * @param label2
	 *            second label
	 * @return negative/0/positive as in {@link Comparable#compareTo(Object)}
	 */
	final public static int compareLabels(final String label1,
			final String label2) {
		String prefix1 = trailingDigits(label1);
		String prefix2 = trailingDigits(label2);
		int comp = prefix1.compareTo(prefix2);
		if (comp != 0) {
			return comp;
		}
		int suffixLength1 = label1.length() - prefix1.length();
		int suffixLength2 = label2.length() - prefix2.length();
		if (suffixLength1 != suffixLength2) {
			return suffixLength1 - suffixLength2;
		}

		return label1.compareTo(label2);
	}

	private static String trailingDigits(String label1) {
		int i = label1.length() - 1;
		while (i > 0 && label1.charAt(i) >= '0' && label1.charAt(i) <= '9') {
			i--;
		}
		return label1.substring(0, i + 1);
	}

	private void doRenameLabel(final String newLabel) {
		if ((newLabel == null) || newLabel.equals(label)) {
			return;
		}

		// UPDATE KERNEL
		cons.removeLabel(this); // remove old table entry
		oldLabel = label; // remember old label (for applet to javascript
							// rename)

		label = newLabel; // set new label
		GeoElementSpreadsheet.setBackgroundColor(this);

		// rename corresponding cas cell, before the label
		// is in construction set
		if (correspondingCasCell != null) {
			correspondingCasCell.setInputFromTwinGeo(false, false);
		}
		cons.putLabel(this); // add new table entry

		algebraStringsNeedUpdate();
		updateSpreadsheetCoordinates();

		kernel.notifyRename(this); // tell views
		updateCascade();
		kernel.notifyRenameUpdatesComplete();
	}

	@Override
	final public String getOldLabel() {
		return oldLabel;
	}

	@Override
	public String getFreeLabel(final String suggestedLabel) {
		if (suggestedLabel != null) {
			if ("x".equals(suggestedLabel) || "y".equals(suggestedLabel)) {
				return getDefaultLabel();
			}

			if (cons.isFreeLabel(suggestedLabel)) {
				return suggestedLabel;
			} else if (suggestedLabel.length() > 0) {
				return getIndexLabel(suggestedLabel);
			}
		}

		// standard case: get default label
		return getDefaultLabel();
	}

	/**
	 * appends all upper case Greek letters to list
	 * 
	 * @param list
	 *            list to append Greek Upper case letters to
	 */
	public static void addAddAllGreekUpperCase(ArrayList<String> list) {

		for (Greek greek : Greek.values()) {
			if (greek.upperCase) {
				list.add(greek.unicode + "");
			}
		}

	}

	/**
	 * appends all upper case Greek letters to list
	 * 
	 * @param list
	 *            list to append Greek Upper case letters to
	 */
	public static void addAddAllGreekLowerCaseNoPi(ArrayList<String> list) {
		for (Greek greek : Greek.values()) {
			if (!greek.upperCase && greek.unicode != Unicode.pi) {

				// \u03d5 in place of \u03c6
				if (greek.unicode == Unicode.phi) {
					list.add(Unicode.phi_symbol + "");
				} else {
					list.add(greek.unicode + "");
				}
			}
		}
	}

	/**
	 * appends all upper case Greek letters to list
	 * 
	 * @param listener
	 *            listener to append Greek Upper case letters to
	 */
	public static void addAddAllGreekLowerCaseNoPi(
			IAxisModelListener listener) {

		for (Greek greek : Greek.values()) {
			if (!greek.upperCase && greek.unicode != Unicode.pi) {

				// \u03d5 in place of \u03c6
				if (greek.unicode == Unicode.phi) {
					listener.addAxisLabelItem(Unicode.phi_symbol + "");
				} else {
					listener.addAxisLabelItem(greek.unicode + "");
				}
			}
		}

	}

	/**
	 * @return default label
	 */
	@Override
	public String getDefaultLabel() {
		char[] chars = null;
		EquationType equationType = getEquationTypeForLabeling();
		if (isGeoPoint() && !(this instanceof GeoTurtle)) {
			// Michael Borcherds 2008-02-23
			// use Greek upper case for labeling points if language is Greek
			// (el)
			if (getLoc().isUsingLocalizedLabels()) {
				if (getLoc().languageIs(Language.Greek.locale)) {
					chars = Greek.getGreekUpperCase();
				} else if (getLoc().languageIs(Language.Arabic.locale)) {
					// Arabic / Arabic (Morocco)
					chars = LabelType.arabic;
				} else if (getLoc().languageIs(Language.Yiddish.locale)) {
					chars = LabelType.yiddish;
				} else {
					chars = LabelType.pointLabels;
				}
			} else {
				chars = LabelType.pointLabels;
			}

			final GeoPointND point = (GeoPointND) this;
			if (point.getToStringMode() == Kernel.COORD_COMPLEX) {

				// check through z_1, z_2, etc and return first one free
				// (also checks z_{1} to avoid clash)
				return cons.getIndexLabel("z", 1);
			}

		} else if (equationType == EquationType.IMPLICIT) {
			return defaultNumberedLabel("eq", false);
		} else if (equationType == EquationType.EXPLICIT || isGeoFunction()) {
			chars = LabelType.functionLabels;
		} else if (isGeoLine()) {
			// name "edge" for segments from polyhedron
			if (getMetasLength() == 1
					&& !((FromMeta) this).getMetas()[0].isGeoPolygon()) {
				int counter = 0;
				String str;
				final String name = getLoc().getPlainLabel("edge", "edge"); // Name.edge
				do {
					counter++;
					str = name + kernel.internationalizeDigits(counter + "",
							StringTemplate.defaultTemplate);
				} while (!cons.isFreeLabel(str));
				return str;
			}
			chars = LabelType.lineLabels;
		} else if (isGeoPlane()) {
			chars = LabelType.planeLabels;
		} else if (isPenStroke()) {
			// needs to come before PolyLine (subclass)
			return defaultNumberedLabel("penStroke", false); // Name.penStroke
		} else if (isGeoPolyLine()) {
			chars = LabelType.lineLabels;
		} else if (isGeoConic()) {
			chars = LabelType.conicLabels;
		} else if (isGeoVector() || evaluatesTo3DVector()) {
			chars = LabelType.vectorLabels;
		} else if (isGeoAngle()) {
			chars = getLabelManager().getAngleLabels();
		} else if (isGeoText()) {
			return defaultNumberedLabel("text", false); // Name.text
		} else if (isGeoImage()) {
			return defaultNumberedLabel("picture", false); // Name.picture
		} else if (isGeoLocus()) {

			if (algoParent.getClassName().equals(Commands.SolveODE)
					|| algoParent instanceof AlgoIntegralODE
					|| algoParent.getClassName().equals(Commands.NSolveODE)) {
				// Name.numericalIntegral
				return defaultNumberedLabel("numericalIntegral", false);

			} else if (algoParent.getClassName().equals(Commands.SlopeField)) {

				return defaultNumberedLabel("slopefield", false); // Name.slopefield
			} else if (algoParent instanceof GraphAlgo) {

				return defaultNumberedLabel("graph", false); // Name.graph
			}

			return defaultNumberedLabel("locus", false); // Name.locus
		} else if (isGeoInputBox()) {
			return defaultNumberedLabel("textfield", false); // Name.textfield
		} else if (isGeoButton()) {
			return defaultNumberedLabel("button", false); // Name.button
		} else if (isGeoTurtle()) {
			return defaultNumberedLabel("turtle", false); // Name.turtle
		} else if (isGeoList()) {
			final GeoList list = (GeoList) this;

			String prefix = list.isMatrix() ? "m" : "l";
			return defaultNumberedLabel(prefix, false);
		} else {
			chars = LabelType.lowerCaseLabels;
		}

		return getLabelManager().getNextIndexedLabel(chars);
	}

	private String defaultNumberedLabel(final String plainKey,
			boolean allowNoSuffix) {
		String trans = getLoc().getPlainLabel(plainKey, plainKey);
		if (allowNoSuffix && cons.isFreeLabel(trans)) {
			return trans;
		}
		int counter = 0;
		String str;
		do {
			counter++;
			str = trans + kernel.internationalizeDigits(counter + "",
					StringTemplate.defaultTemplate);
		} while (!cons.isFreeLabel(str));
		return str;
	}

	@Override
	public String getIndexLabel(final String prefix) {
		if (prefix == null) {
			return getFreeLabel(null) + "_1";
		}
		return cons.getIndexLabel(prefix);
	}

	@Override
	public boolean isGeoInputBox() {
		return false;
	}

	/**
	 * @param isEmptySpreadsheetCell
	 *            empty spreadsheet cell flag
	 */
	public void setEmptySpreadsheetCell(boolean isEmptySpreadsheetCell) {
		this.emptySpreadsheetCell = isEmptySpreadsheetCell;
	}

	/**
	 * @return empty spreadsheet cell flag
	 */
	public boolean isEmptySpreadsheetCell() {
		return emptySpreadsheetCell;
	}

	@Override
	public void remove() {
		// dependent object: remove parent algorithm
		if (algoParent != null) {
			algoParent.remove(this);
		} else {
			// must be done in this order because doRemove destroys the link
			if (correspondingCasCell != null) {
				correspondingCasCell.doRemove();
			}
			doRemove();
		}
	}

	@Override
	public void doRemove() {
		// stop animation of this geo
		setAnimating(false);

		// first remove all dependent algorithms
		removeDependentAlgos();

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

		/*
		 * // remove all dependent algorithms if (algorithmList != null) { final
		 * Object[] algos = algorithmList.toArray(); for (int i = 0; i <
		 * algos.length; i++) { algo = (AlgoElement) algos[i];
		 * algo.remove(this); cons.updateCasCellRows(); } //
		 * cons.updateCasCellRows(); }
		 */

		// remove this object from table
		if (isLabelSet()) {
			cons.removeLabel(this);
		}

		// remove from selection
		if (isSelected()) {
			// prevent update selection if construction will replace the geo
			kernel.getApplication().getSelectionManager().removeSelectedGeo(
					this, false, !cons.isRemovingGeoToReplaceIt());
		}

		// notify views before we change labelSet
		notifyRemove();

		setLabelSet(false);
		labelWanted = false;
		correspondingCasCell = null;

		if (latexCache != null) {
			// remove old key from cache
			// JLaTeXMathCache.removeCachedTeXFormula(keyLaTeX);
			latexCache.remove();
		}

		// http://dev.geogebra.org/trac/changeset/39262
		// reverted as causes infinite loop when Attach/Detach tool used
		// to *drag* a Point onto eg a circle
		// if (kernel.getApplication() != null
		// && kernel.getApplication().getActiveEuclidianView() != null
		// && kernel.getApplication().getActiveEuclidianView()
		// .getEuclidianController() != null) {
		// kernel.getApplication().getActiveEuclidianView()
		// .getEuclidianController().clearSelections();
		// }
	}

	/**
	 * Remove algos depending on this geo.
	 */
	public void removeDependentAlgos() {
		if (algorithmList != null) {
			final Object[] algos = algorithmList.toArray();
			for (int i = 0; i < algos.length; i++) {
				AlgoElement algo = (AlgoElement) algos[i];
				algo.remove(this);
			}
		}
	}

	@Override
	public LaTeXCache getLaTeXCache() {
		if (latexCache == null) {
			latexCache = LaTeXFactory.getPrototype().newLaTeXCache();
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

	/*
	 * ******************************************************* AlgorithmList
	 * Management each GeoElement has a list of dependent
	 * algorithms******************************************************
	 */

	@Override
	final public void addAlgorithm(final AlgoElement algorithm) {
		if (!(getAlgorithmList().contains(algorithm))) {
			algorithmList.add(algorithm);
		}
		addToUpdateSets(algorithm);
	}

	@Override
	final public void addToAlgorithmListOnly(final AlgoElement algorithm) {
		if (!getAlgorithmList().contains(algorithm)) {
			algorithmList.add(algorithm);
		}
	}

	@Override
	final public void addToUpdateSetOnly(final AlgoElement algorithm) {
		addToUpdateSets(algorithm);
	}

	@Override
	final public void removeAlgorithm(final AlgoElement algorithm) {
		if (algorithmList != null) {
			algorithmList.remove(algorithm);
			removeFromUpdateSets(algorithm);
		}
	}

	@Override
	public AlgorithmSet getAlgoUpdateSet() {
		if (algoUpdateSet == null) {
			algoUpdateSet = new AlgorithmSet();
		}

		return algoUpdateSet;
	}

	@Override
	public boolean addToUpdateSets(final AlgoElement algorithm) {
		final boolean added = getAlgoUpdateSet().add(algorithm);

		if (added) {
			// propagate up the graph if we didn't do this before
			if (algoParent != null) {
				final GeoElementND[] input = algoParent
						.getInputForUpdateSetPropagation();
				for (int i = 0; i < input.length; i++) {
					input[i].addToUpdateSets(algorithm);
				}
			}
		}

		return added;
	}

	@Override
	public boolean removeFromUpdateSets(final AlgoElement algorithm) {
		final boolean removed = (algoUpdateSet != null)
				&& algoUpdateSet.remove(algorithm);

		if (removed) {
			// propagate up the graph
			if (algoParent != null) {
				final GeoElementND[] input = algoParent
						.getInputForUpdateSetPropagation();
				for (int i = 0; i < input.length; i++) {
					input[i].removeFromUpdateSets(algorithm);
				}
			}
		}

		return removed;
	}

	/**
	 * updates this object and notifies kernel. Note: no dependent objects are
	 * updated.
	 * 
	 * @see #updateRepaint()
	 * @param dragging
	 *            whether this was triggered by drag
	 */
	public void update(boolean dragging) {
		updateGeo(!cons.isUpdateConstructionRunning(), dragging);
		maybeUpdateSpecialPoints();

		kernel.notifyUpdate(this);
	}

	private void maybeUpdateSpecialPoints() {
		if (canHaveSpecialPoints() && kernel.getApplication().getConfig().hasPreviewPoints()) {
			kernel.getApplication().getSpecialPointsManager().updateSpecialPoints(null);
		}
	}

	@Override
	public final void update() {
		update(false);
	}

	/**
	 * Same as update(), but do not notify kernel
	 * 
	 * @param mayUpdateCas
	 *            whether update migt be sent to CAS
	 * @param dragging
	 *            whether this was triggered by drag
	 */
	protected final void updateGeo(boolean mayUpdateCas, boolean dragging) {

		if (labelWanted && !isLabelSet()) {
			// check if this object's label needs to be set
			if (isVisible()) {
				setLabel(label);
			}
		}

		if (mayUpdateCas && correspondingCasCell != null) {
			correspondingCasCell.setInputFromTwinGeo(false, dragging);
		}

		// texts need updates
		algebraStringsNeedUpdate();
	}

	/**
	 * @param mayUpdateCas
	 *            whether CAS may need update
	 */
	protected final void updateGeo(boolean mayUpdateCas) {
		updateGeo(mayUpdateCas, false);
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
	 * 
	 * @param dragging
	 *            whether this was triggered by drag
	 */
	public void updateCascade(boolean dragging) {
		long l = System.currentTimeMillis();
		kernel.notifyBatchUpdate();
		update(dragging);
		updateDependentObjects();
		GeoGebraProfiler.addUpdateCascade(System.currentTimeMillis() - l);
		kernel.notifyEndBatchUpdate();
	}

	@Override
	public void updateCascade() {
		updateCascade(false);
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
	 * True if this element can have special points. This is used to e.g.
	 * update the special points when this element is updated.
	 *
	 * @return true if this element can have special points
	 */
	protected boolean canHaveSpecialPoints() {
		return false;
	}

	/**
	 * Updates algoUpdateSet and secondGeo.algoUpdateSet together efficiently.
	 * 
	 * @param secondGeo
	 *            other geo whose update set needs an update
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

	@Override
	public boolean hasAlgoUpdateSet() {
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
	 * @param geos
	 *            geos to be updated
	 * 
	 * @param tempSet1
	 *            a temporary set that is used to collect all algorithms that
	 *            need to be updated
	 * 
	 * @param updateCascadeAll
	 *            true to update cascade over dependent geos as well
	 */
	final static public synchronized void updateCascade(
			final ArrayList<? extends GeoElementND> geos,
			final TreeSet<AlgoElement> tempSet1,
			final boolean updateCascadeAll) {

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
	 * Updates all objects in a cascade, but only location is updated for the
	 * locatables in input array
	 * 
	 * @param geos
	 *            locateables
	 * @param cons
	 *            construction where update is done
	 */
	final static public synchronized void updateCascadeLocation(
			final ArrayList<Locateable> geos, Construction cons) {
		// build update set of all algorithms in construction element order
		// clear temp set
		final TreeSet<AlgoElement> tempSet1 = new TreeSet<>();

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
		AlgorithmSet algoSetCurrentlyUpdated = cons
				.getAlgoSetCurrentlyUpdated();
		if (algoSetCurrentlyUpdated != null) {
			algoSetCurrentlyUpdated.removeAllFromCollection(tempSet1);
		}

		// now we have one nice algorithm set that we can update
		if (tempSet1.size() > 0) {
			final Iterator<AlgoElement> it = tempSet1.iterator();
			while (it.hasNext()) {
				try {
					final AlgoElement algo = it.next();
					algo.update();
				} catch (Exception e) {
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
	 * @param geos
	 *            geos to be updated
	 * 
	 * @param tempSet2
	 *            a temporary set that is used to collect all algorithms that
	 *            need to be updated
	 * @param lastAlgo
	 *            stop cascade on this algo
	 */
	final static public void updateCascadeUntil(final ArrayList<?> geos,
			final TreeSet<AlgoElement> tempSet2, final AlgoElement lastAlgo) {
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
	 * 
	 * @param dragging
	 *            whether this was triggered by drag
	 */
	public void updateRepaint(boolean dragging) {
		updateCascade(dragging);
		kernel.notifyRepaint();
	}

	/**
	 * Updates this object and all dependent ones. Notifies kernel to repaint
	 * views.
	 */
	@Override
	public void updateRepaint() {
		updateRepaint(false);
	}

	@Override
	public void updateVisualStyle(GProperty prop) {
		// updateGeo();
		kernel.notifyUpdateVisualStyle(this, prop);
		// updateDependentObjects();
		// kernel.notifyRepaint();
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	final public String toString() {
		return toString(StringTemplate.defaultTemplate);
	}

	/**
	 * implementation of interface ExpressionValue
	 */
	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public final boolean isLeaf() {
		return true;
	}

	/**
	 * Evaluates to number (if not numeric, returns undefined MyDouble)
	 * 
	 * @return number or undefined double
	 */
	@Override
	public double evaluateDouble() {
		if (this instanceof NumberValue) {
			return ((NumberValue) this).getDouble();
		}
		return Double.NaN;
	}

	@Override
	final public ExpressionValue evaluate(StringTemplate tpl) {
		if (this instanceof GeoCasCell) {
			return ((GeoCasCell) this).getValue();
		}
		return this;
	}

	/**
	 * Returns just set with just one element (itself). Do not ever remove the
	 * final flag, it will break the update mechanism.
	 */
	@Override
	public final HashSet<GeoElement> getVariables(SymbolicMode mode) {
		final HashSet<GeoElement> ret = new HashSet<>();
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
						randNumbers = new ArrayList<>();
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
		final TreeSet<GeoElement> set = new TreeSet<>();
		addPredecessorsToSet(set, false);
		set.remove(this);
		return set;
	}

	@Override
	final public void addPredecessorsToSet(final TreeSet<GeoElement> set,
			final boolean onlyIndependent) {
		if (algoParent == null) {
			set.add(this);
		} else { // parent algo
			algoParent.addPredecessorsToSet(set, onlyIndependent);
		}
	}

	/**
	 * only add predecessors that satisfy a condition
	 * 
	 * @param set
	 *            output set
	 * @param check
	 *            condition
	 */
	final public void addPredecessorsToSet(final TreeSet<GeoElement> set,
			final Inspecting check) {
		if (algoParent == null) {
			if (check.check(this)) {
				set.add(this);
			}
		} else { // parent algo
			algoParent.addPredecessorsToSet(set, check);
		}
	}

	/**
	 * @return set of all predecessor that can be randomized
	 */
	public TreeSet<GeoElement> getAllRandomizablePredecessors() {
		final TreeSet<GeoElement> set = new TreeSet<>();
		addRandomizablePredecessorsToSet(set);
		return set;
	}

	/**
	 * @param set
	 *            set of randomizable predecessors
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

	@Override
	final public boolean isParentOf(final GeoElementND geo) {
		if (algoUpdateSet != null) {
			final Iterator<AlgoElement> it = algoUpdateSet.getIterator();
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

	@Override
	final public boolean hasChildren() {
		return (algorithmList != null) && (algorithmList.size() > 0);
	}

	@Override
	final public boolean isChildOf(final GeoElementND geo) {
		if ((geo == null) || isIndependent()) {
			return false;
		}
		return geo.isParentOf(this);
	}

	/**
	 * Returns whether this object is dependent on other geo (or equal)
	 * 
	 * @param geo
	 *            other geo
	 * @return true if this object is dependent on other geo.
	 */
	final public boolean isChildOrEqual(final GeoElementND geo) {
		return (this == geo) || isChildOf(geo);
	}

	@Override
	final public TreeSet<GeoElement> getAllChildren() {
		final TreeSet<GeoElement> set = new TreeSet<>();
		if (algoUpdateSet != null) {
			final Iterator<AlgoElement> it = algoUpdateSet.getIterator();
			while (it.hasNext()) {
				final AlgoElement algo = it.next();
				for (int i = 0; i < algo.getOutputLength(); i++) {
					set.add(algo.getOutput(i));
				}
			}
		}
		return set;
	}

	/**
	 * implementation of abstract methods from ConstructionElement Almost never
	 * called, do not cache
	 */
	@Override
	final public GeoElement[] getGeoElements() {
		return new GeoElement[] { this };
	}

	@Override
	final public boolean isAlgoElement() {
		return false;
	}

	@Override
	final public boolean isGeoElement() {
		return true;
	}

	/**
	 * @return twinGeos construction index
	 */
	final public int getAlgoDepCasCellGeoConstIndex() {
		return super.getConstructionIndex();
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
	public String getDefinitionDescription(StringTemplate tpl) {
		if (algoParent == null) {
			if (getDefinition() != null) {
				return getDefinition().toString(tpl);
			}
			return "";
		}
		return algoParent.toString(tpl);
	}

	/**
	 * @param addHTMLtag
	 *            true to wrap in &lt;html&gt;
	 * @return definition description as HTML
	 */
	final public String getDescriptionHTML(final boolean addHTMLtag) {
		if (algoParent == null) {
			return "";
		}
		return indicesToHTML(
				getDefinitionDescription(StringTemplate.defaultTemplate),
				addHTMLtag);
	}

	@Override
	final public String getDefinition(StringTemplate tpl) {
		if (algoParent != null) {
			return algoParent.getDefinition(tpl);

		}
		if (definition != null) {
			return definition.toString(tpl);
		}
		return "";
	}

	/**
	 * @param addHTMLtag
	 *            true to wrap in &lt;HTML&gt;
	 * @return HTML command description
	 */
	final public String getDefinitionHTML(final boolean addHTMLtag) {
		if (algoParent == null) {
			return "";
		}
		return indicesToHTML(
				algoParent.getDefinition(StringTemplate.defaultTemplate),
				addHTMLtag);
	}

	@Override
	public int getRelatedModeID() {
		if (algoParent == null) {
			return -1;
		}
		return algoParent.getRelatedModeID();
	}

	/**
	 * @param desc
	 *            value string
	 * @return value string prepended with label = ,label(x) = or label :
	 */
	final public String addLabelText(final String desc) {
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

		return ret;
	}

	/**
	 * @param desc
	 *            description
	 * @param builder
	 *            builder for indexed strings
	 */
	final public void addLabelTextOrHTML(final String desc, IndexHTMLBuilder builder) {
		String ret = addLabelText(desc);

		// check for index
		IndexHTMLBuilder.convertIndicesToHTML(ret, builder);
	}

	/**
	 * @param addHTMLtag
	 *            true to wrap in &lt;html&gt;
	 * @param tpl
	 *            string template
	 * @return HTML representation of caption
	 */
	final public String getCaptionDescriptionHTML(final boolean addHTMLtag,
			StringTemplate tpl) {

		return indicesToHTML(getCaptionDescription(tpl), addHTMLtag);
	}

	@Override
	final public String getXMLtypeString() {
		// don't use getTypeString() as it's overridden
		return StringUtil.toLowerCaseUS(getGeoClassType().xmlName);
	}

	/**
	 * Returns type string of GeoElement.
	 * 
	 * @return type string without "Geo" prefix in most cases, overridden in eg
	 *         GeoPoint, GeoPolygon
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
		return getLoc().getMenu(getTypeString());
	}

	/**
	 * @return localized type string for Algebra View
	 */
	public String translatedTypeStringForAlgebraView() {
		// Log.debug(getTypeStringForAlgebraView());
		// Log.debug(app.getPlain(getTypeStringForAlgebraView()));
		return getLoc().getMenu(getTypeStringForAlgebraView());
	}

	@Override
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
	 *            true to allow colors
	 * @param addHTMLtag
	 *            true to wrap in &lt;html&gt;
	 * @return description (type + label + definition)
	 */
	final public String getLongDescriptionHTML(final boolean colored,
			final boolean addHTMLtag) {
		if ((algoParent == null) || this instanceof TextValue
				|| isPenStroke() || this instanceof GeoPointND) {
			return getNameDescriptionHTML(colored, addHTMLtag);
		}
		final StringBuilder sbLongDescHTML = new StringBuilder();

		final String formatedLabel = getLabel(StringTemplate.defaultTemplate);
		final String typeString = translatedTypeString();

		// html string
		if (addHTMLtag) {
			sbLongDescHTML.append("<html>");
		}

		final boolean reverseOrder = getLoc()
				.isReverseNameDescriptionLanguage();
		if (!reverseOrder) {
			// standard order: "point A"
			sbLongDescHTML.append(typeString);
			sbLongDescHTML.append(' ');
		}

		if (colored) {
			final GColor colorAdapter = GColor.newColor(
					getAlgebraColor().getRed(), getAlgebraColor().getGreen(),
					getAlgebraColor().getBlue());
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
			final boolean rightToLeft = getLoc().isRightToLeftReadingOrder();
			if (rightToLeft) {
				// sbLongDescHTML.append("\u200e\u200f: \u200e");
				sbLongDescHTML.append(Unicode.LEFT_TO_RIGHT_MARK);
				sbLongDescHTML.append(Unicode.RIGHT_TO_LEFT_MARK);
				sbLongDescHTML.append(": ");
				sbLongDescHTML.append(Unicode.LEFT_TO_RIGHT_MARK);
			} else {
				sbLongDescHTML.append(": ");
			}
			sbLongDescHTML.append(indicesToHTML(
					algoParent.toString(StringTemplate.defaultTemplate),
					false));
			if (rightToLeft) {
				// sbLongDescHTML.append("\u200e");
				sbLongDescHTML.append(Unicode.LEFT_TO_RIGHT_MARK);
			}
		}
		if (addHTMLtag) {
			sbLongDescHTML.append("</html>");
		}
		return sbLongDescHTML.toString();
	}

	/**
	 * Colored label of the GeoElement.
	 * 
	 * @return the colored label
	 */
	final public String getColoredLabel() {
		String formatedLabel = getLabel(StringTemplate.defaultTemplate);
		StringBuilder sb = new StringBuilder();
		final GColor colorAdapter = GColor.newColor(getAlgebraColor().getRed(),
				getAlgebraColor().getGreen(), getAlgebraColor().getBlue());
		sb.append("<b><font color=\"#");
		sb.append(StringUtil.toHexString(colorAdapter));
		sb.append("\">");
		sb.append(indicesToHTML(formatedLabel, false));
		sb.append("</font></b>");
		return sb.toString();
	}

	/**
	 * Returns long description for all GeoElements in given array, each geo on
	 * one line.
	 * 
	 * @param geos
	 *            list of geos
	 * @param colored
	 *            true to use colors
	 * @param addHTMLtag
	 *            true to wrap in &lt;html&gt; ... &lt;/html&gt;
	 * @param alwaysOn
	 *            true to override default
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

	@Override
	public String getLabelDescription() {
		String labelDescription;
		switch (labelMode) {
		case LABEL_CAPTION_VALUE:
			labelDescription = getCaptionAndValue();
			break;
		case LABEL_NAME_VALUE:
			labelDescription = getAlgebraDescriptionDefault();
			break;
		case LABEL_VALUE:
			labelDescription = toDefinedValueString(
					StringTemplate.defaultTemplate);
			break;
		case LABEL_CAPTION: // Michael Borcherds 2008-02-18
			labelDescription = getCaption(StringTemplate.defaultTemplate);
			break;
		default: // case LABEL_NAME:
			// return label;
			// Mathieu Blossier - 2009-06-30
			labelDescription = getLabel(StringTemplate.defaultTemplate);
		}

		return labelDescription.startsWith(LabelManager.HIDDEN_PREFIX) ? "" : labelDescription;
	}

	/**
	 * 
	 * @return Caption + Value if defined, Name + Value otherwise.
	 */
	public String getCaptionAndValue() {
		if ("".equals(getRawCaption())) {
			return getAlgebraDescriptionDefault();
		}

		StringBuilder sb = new StringBuilder();
		sb.append(getRawCaption());
		sb.append(getLabelDelimiterWithSpace());
		sb.append(toValueString(StringTemplate.defaultTemplate));
		return sb.toString();
	}

	/**
	 * Returns toValueString() if isDefined() ist true, else the translation of
	 * "undefined" is returned
	 * 
	 * @param tpl
	 *            string template
	 * 
	 * @return either value string or ?
	 */
	final public String toDefinedValueString(StringTemplate tpl) {
		if (isDefined()) {
			return toValueString(tpl);
		}
		return "?";
	}

	/**
	 * Returns algebraic representation of this GeoElement as Text. If this is
	 * not possible (because there are indices in the representation) a HTML
	 * string is returned. Default template is used, caching is employed.
	 * 
	 * @param builder
	 *            indexed HTML builder
	 * 
	 * @return algebraic representation of this GeoElement as Text
	 */

	final public String getAlgebraDescriptionTextOrHTMLDefault(
			IndexHTMLBuilder builder) {
		if (!isAlgebraLabelVisible()) {
			String desc = getLaTeXDescriptionRHS(false,
					StringTemplate.defaultTemplate);
			Log.debug("desc = " + desc);
			if (LabelManager.isShowableLabel(desc)) {
				builder.clear();
				builder.append(desc);
				return builder.toString();
			}
		}
		if (strAlgebraDescTextOrHTMLneedsUpdate) {
			final String algDesc = getAlgebraDescriptionDefault();
			// convertion to html is only needed if indices are found
			if (hasIndexLabel()) {
				builder.indicesToHTML(algDesc);
				strAlgebraDescTextOrHTML = builder.toString();
			} else {
				builder.clear();
				builder.append(algDesc);
				strAlgebraDescTextOrHTML = algDesc;
			}

			strAlgebraDescTextOrHTMLneedsUpdate = false;
		} else {
			// TODO in some cases we don't need this
			if (!builder.canAppendRawHtml()) {
				builder.indicesToHTML(strAlgebraDescription);
			} else {
				builder.clear();
				builder.append(strAlgebraDescTextOrHTML);
			}
		}

		return strAlgebraDescTextOrHTML;
	}

	/**
	 * @param builder
	 *            index builder
	 * @return right hand side
	 */
	final public String getAlgebraDescriptionTextOrHTMLRHS(
			IndexHTMLBuilder builder) {
		String algDesc = getAlgebraDescriptionRHS();

		// conversion to html is only needed if indices are found
		builder.indicesToHTML(algDesc);
		return algDesc;
	}

	/**
	 * @return "undefined" or value string
	 */
	final public String getAlgebraDescriptionRHS() {
		String algDesc;
		if (!isDefined()) {
			algDesc = getLoc().getMenu("Undefined");
		} else {
			algDesc = toValueString(StringTemplate.algebraTemplate);
		}
		return algDesc;

	}

	/**
	 * @return algebra description
	 */
	final public String getAlgebraDescriptionHTMLDefault() {
		if (strAlgebraDescriptionHTMLneedsUpdate) {

			if (isGeoText()) {
				strAlgebraDescriptionHTML = indicesToHTML(
						toValueString(StringTemplate.defaultTemplate), false);
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
	 * @param addHTMLTag
	 *            says if html tags have to be added
	 * @return type and label of a GeoElement (for tooltips and error messages)
	 */
	final public String getLabelTextOrHTML(boolean addHTMLTag) {
		if (strLabelTextOrHTMLUpdate) {
			if (hasIndexLabel()) {
				strLabelTextOrHTML = indicesToHTML(
						getLabel(StringTemplate.defaultTemplate), addHTMLTag);
			} else {
				strLabelTextOrHTML = getLabel(StringTemplate.defaultTemplate);
			}
		}

		return strLabelTextOrHTML;
	}

	/**
	 * Returns algebraic representation (e.g. coordinates, equation) of this
	 * construction element.
	 * 
	 * For editing ? is better than undefined (because of localization).
	 * 
	 * @param tpl
	 *            string template
	 * @return algebraic representation (e.g. coordinates, equation) or a= ? for
	 *         undefined
	 */
	final public String getAlgebraDescription(StringTemplate tpl) {
		if (isDefinitionValid()) {
			return toString(tpl);
		}
		final StringBuilder sbAlgebraDesc = new StringBuilder();
		sbAlgebraDesc.append(label);
		sbAlgebraDesc.append(" = ?");
		return sbAlgebraDesc.toString();
	}

	/**
	 *
	 * @return algebraic representation for preview output
	 */
	final public String getAlgebraDescriptionForPreviewOutput() {
		return getAlgebraDescriptionRHSLaTeX();
	}

	/**
	 * Returns algebraic representation (e.g. coordinates, equation) of this
	 * construction element. Default string template is used =&gt; caching can
	 * be employed
	 * 
	 * @return algebraic representation (e.g. coordinates, equation)
	 */
	public String getAlgebraDescriptionDefault() {
		if (strAlgebraDescriptionNeedsUpdate) {
			updateAlgebraDescription();
			strAlgebraDescriptionNeedsUpdate = false;
		}
		return strAlgebraDescription;
	}

	private void updateAlgebraDescription() {
		if (isDefined()) {
			setAlgebraDescriptionForDefined();
		} else {
			setAlgebraDescriptionForUndefined();
		}
	}

	private void setAlgebraDescriptionForDefined() {
		if (!LabelManager.isShowableLabel(label)) {
			strAlgebraDescription = toValueString(StringTemplate.algebraTemplate);
		} else {
			strAlgebraDescription = toString(StringTemplate.algebraTemplate);
		}
	}

	private void setAlgebraDescriptionForUndefined() {
		final StringBuilder sbAlgebraDesc = new StringBuilder();
		sbAlgebraDesc.append(label);
		sbAlgebraDesc.append(' ');
		sbAlgebraDesc.append(getLoc().getMenu("Undefined"));
		strAlgebraDescription = sbAlgebraDesc.toString();
	}

	/**
	 * @return LaTeX description
	 */
	public String getAlgebraDescriptionLaTeX() {
		if (isDefined()) {
			return toString(StringTemplate.latexTemplate);
		}
		final StringBuilder sbAlgebraDesc = new StringBuilder();
		sbAlgebraDesc.append(label);
		sbAlgebraDesc.append("\\;");
		sbAlgebraDesc.append(getLoc().getMenu("Undefined"));
		return sbAlgebraDesc.toString();
	}

	/**
	 * @return LaTeX description RHS
	 */
	final public String getAlgebraDescriptionRHSLaTeX() {
		if (!isDefined()) {
			return getLoc().getMenu("Undefined");
		}
		return toValueString(StringTemplate.latexTemplate);
	}

	/**
	 * Returns simplified algebraic representation of this GeoElement. Used by
	 * the regression test output creator.
	 * 
	 * @param tpl
	 *            string template
	 * 
	 * @return sumplifiedrepresentation for regression test
	 */
	final public String getAlgebraDescriptionRegrOut(StringTemplate tpl) {
		if (strAlgebraDescriptionNeedsUpdate) {
			if (isDefined()) {
				strAlgebraDescription = toStringMinimal(tpl);
			} else {
				final StringBuilder sbAlgebraDesc = new StringBuilder();
				sbAlgebraDesc.append("?");
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
	 * 
	 * @param tpl
	 *            string template
	 * @return string for regression output
	 */
	public String toStringMinimal(StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	public String getLaTeXdescription() {
		if (strLaTeXneedsUpdate) {
			if (isDefined() && !isInfinite()) {
				strLaTeX = toLaTeXString(false, StringTemplate.latexTemplate);
			} else {
				strLaTeX = "?";

			}
		}

		return strLaTeX;
	}

	/**
	 * In RadioButtonTreeItem, we don't want to return null from
	 * getLaTeXAlgebraDescription in case of GeoText, but return its simple
	 * algebra description, which shall be (label="content") in theory
	 * 
	 * @param substituteNumbers
	 *            whether to substitute variables
	 * @param tpl
	 *            template
	 * @param fallback
	 *            fallback text
	 * @return LaTeX text
	 */
	public String getLaTeXAlgebraDescriptionWithFallback(
			final boolean substituteNumbers, StringTemplate tpl,
			boolean fallback) {
		String ret = null;
		if (!substituteNumbers) {
			ret = getDefinition(tpl);

		}
		if (ret != null && ret.length() > 0) {
			ret = getAssignmentLHS(tpl)
					+ getLabelDelimiterWithSpace() + ret;

			return ret;
		}
		if (!isDefined() || !isGeoText()) {
			ret = getLaTeXAlgebraDescription(substituteNumbers, tpl);
		}
		if ((ret == null || "".equals(ret)) && isGeoText() && fallback) {
			ret = getAlgebraDescription(tpl);
		}
		return ret;
	}

	/**
	 * Returns a string used to render a LaTeX form of the geo's algebra
	 * description.
	 * 
	 * @param substituteNumbers
	 *            true to replace variable names by values
	 * @param tpl
	 *            string template
	 * @return string used to render a LaTeX form of the geo's algebra
	 *         description.
	 */
	public final String getLaTeXAlgebraDescription(
			final boolean substituteNumbers,
			StringTemplate tpl) {

		return getLaTeXAlgebraDescription(this, substituteNumbers, tpl,
				isAlgebraLabelVisible());
	}

	/**
	 * @param substituteNumbers
	 *            whether to use numbers rather than variable nmes
	 * @param tpl
	 *            template
	 * @return LaTeX description without LHS
	 */
	public final String getLaTeXDescriptionRHS(final boolean substituteNumbers,
			StringTemplate tpl) {
		return getLaTeXAlgebraDescription(this, substituteNumbers, tpl, false);
	}

	private String getLaTeXAlgebraDescription(final GeoElement geo,
			final boolean substituteNumbers, StringTemplate tpl,
			boolean includeLHS) {

		final String algebraDesc = geo.getAlgebraDescription(tpl);
		final StringBuilder sb = new StringBuilder();

		if (geo.isGeoList()
				&& ((GeoList) geo).getElementType().equals(GeoClass.TEXT)) {
			return null;
		}
		// handle undefined
		if (!geo.isDefinitionValid()) {
			// we need to keep the string simple (no \mbox) so that
			// isLatexNeeded may return true
			if (includeLHS) {
				sb.append(label);
				sb.append("\\, ");
			}
			sb.append("\\text{");
			sb.append(getLoc().getMenu("Undefined"));
			sb.append("} ");

			// handle non-GeoText prefixed with ":", e.g. "a: x = 3"
		} else if ((algebraDesc.indexOf(":") > -1) && !geo.isGeoText()) {
			if (includeLHS) {
				sb.append(algebraDesc.split(":")[0] + ": \\,");
			}
			sb.append(geo.getFormulaString(tpl, substituteNumbers));
		}

		// now handle non-GeoText prefixed with "="
		else if ((algebraDesc.indexOf("=") > -1) && !geo.isGeoText()) {
			if (includeLHS) {
				sb.append(algebraDesc.split("=")[0] + "\\, = \\,");
			}
			sb.append(geo.getFormulaString(tpl, substituteNumbers));
		} else if (geo.isGeoVector()) {
			if (includeLHS) {
				sb.append(label);
				sb.append("\\, = \\,");
			}
			sb.append(geo.getFormulaString(tpl, substituteNumbers));
		}

		// handle GeoText with LaTeX
		else if (geo.isGeoText() && ((GeoText) geo).isLaTeX()) {
			if (includeLHS) {
				sb.append(algebraDesc.split("=")[0]);
				sb.append("\\, = \\,");
			}
			if (geo.getParentAlgorithm() instanceof TableAlgo) {
				sb.append(((GeoText) geo).getTextString());
			} else {

				String str = ((GeoText) geo).getTextString();

				boolean containsLaTeX = StringUtil.containsLaTeX(str);
				if (!containsLaTeX) {
					sb.append("\\text{");
				}

				sb.append(Unicode.OPEN_DOUBLE_QUOTE);
				sb.append(((GeoText) geo).getTextString());
				sb.append(Unicode.CLOSE_DOUBLE_QUOTE);
				if (!containsLaTeX) {
					sb.append("}");
				}
			}
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

	/**
	 * @return whether definition is valid (like isDefined with exception of
	 *         x=x)
	 */
	public boolean isDefinitionValid() {
		return isDefined();
	}

	/**
	 * @param str
	 *            raw string
	 * @param addHTMLtag
	 *            true to wrap in &lt;html&gt;
	 * @return str with indices in HTML notation (&lt;sub&gt;)
	 */
	public static String indicesToHTML(final String str,
			final boolean addHTMLtag) {
		final IndexHTMLBuilder sbIndicesToHTML = new IndexHTMLBuilder(
				addHTMLtag);
		sbIndicesToHTML.indicesToHTML(str);
		return sbIndicesToHTML.toString();
	}

	@Override
	public String getNameDescription() {
		final StringBuilder sbNameDescription = new StringBuilder();

		final String label1 = getLabel(StringTemplate.defaultTemplate);
		final String typeString = translatedTypeString();

		if (getLoc().isReverseNameDescriptionLanguage()) {
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

	@Override
	final public boolean hasIndexLabel() {
		return label != null && label.indexOf('_') > -1;
	}

	/**
	 * returns type and label of a GeoElement as html string (for tooltips and
	 * error messages)
	 * 
	 * @param colored
	 *            true to allow colors
	 * @param addHTMLtag
	 *            true to wrap in &lt;html&gt;
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

		final boolean reverseOrder = getLoc()
				.isReverseNameDescriptionLanguage();
		if (!reverseOrder
				// want "xAxis" not "Line xAxis"
				&& !isAxis()) {
			// standard order: "point A"
			sbNameDescriptionHTML.append(typeString);
			sbNameDescriptionHTML.append(' ');
		}

		if (colored) {
			sbNameDescriptionHTML.append(" <b><font color=\"#");
			sbNameDescriptionHTML
					.append(StringUtil.toHexString(getAlgebraColor()));
			sbNameDescriptionHTML.append("\">");
		}
		sbNameDescriptionHTML.append(indicesToHTML(label1, addHTMLtag));

		if (this instanceof GeoPointND && getKernel().getApplication()
				.getSettings().getEuclidian(1).axisShown()) {
			sbNameDescriptionHTML
					.append(toValueString(StringTemplate.defaultTemplate));
		}

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
		getXML(false, sb);
		return sb.toString();
	}

	@Override
	public void getXML(boolean getListenersToo, final StringBuilder sb) {

		// make sure numbers are not put in XML in eg Arabic
		// final boolean oldI8NValue = Kernel.internationalizeDigits;
		// Kernel.internationalizeDigits = false;
		if (isIndependent() && definition != null && getDefaultGeoType() < 0) {
			sb.append("<expression");
			sb.append(" label=\"");
			sb.append(StringUtil.encodeXML(label));
			sb.append("\" exp=\"");
			StringUtil.encodeXML(sb,
					definition.toString(StringTemplate.xmlTemplate));
			// expression
			sb.append("\"");

			// add type (e.g. for plane/line)
			if (isGeoPoint()) {
				sb.append(" type=\"point\"");
			} else if (isGeoVector()) {
				sb.append(" type=\"vector\"");
			} else if (isGeoLine()) {
				sb.append(" type=\"line\"");
			} else if (isGeoPlane()) {
				sb.append(" type=\"plane\"");
			} else if (isGeoConic()) {
				sb.append(" type=\"conic\"");
			} else if (isGeoQuadric()) {
				sb.append(" type=\"quadric\"");
			} else if (isGeoImplicitPoly()) {
				sb.append(" type=\"implicitpoly\"");
			} else if (isGeoImplicitSurface()) {
				sb.append(" type=\"implicitsurface\"");
			}
			sb.append("/>\n");
		}

		getElementOpenTagXML(sb);

		getXMLtags(sb);
		getCaptionXML(sb);
		getExtraTagsXML(sb);
		if (getListenersToo) {
			getListenerTagsXML(sb);
		}
		getElementCloseTagXML(sb);

	}

	/**
	 * Append object listener names to XML string builder
	 * 
	 * @param sb
	 *            string builder
	 */
	protected void getListenerTagsXML(StringBuilder sb) {
		ScriptManager scriptManager = kernel.getApplication()
				.getScriptManager();
		// updateListenerMap
		getListenerTagXML(sb, scriptManager.getUpdateListenerMap(),
				"objectUpdate");
		// clickListenerMap
		getListenerTagXML(sb, scriptManager.getUpdateListenerMap(),
				"objectClick");

	}

	private void getListenerTagXML(StringBuilder sb,
			HashMap<GeoElement, JsScript> map, String type) {
		if (map != null) {
			JsScript objectListener = map.get(this);
			if (objectListener != null) {
				sb.append("\t<listener type=\"" + type + "\" val=\"");
				sb.append(objectListener.getText());
				sb.append("\"/>\n");
			}
		}
	}

	/**
	 * Appends open element tag &lt;element&gt; or &lt;cascell&gt; to the
	 * builder
	 * 
	 * @param sb
	 *            string builder
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
	 * Closes the element tag -- either &lt;element&gt; or &lt;cascell&gt;
	 * 
	 * @param sb
	 *            string builder
	 */
	protected void getElementCloseTagXML(final StringBuilder sb) {
		sb.append("</element>\n");
	}

	/**
	 * Appends tags for click and update script to the builder
	 * 
	 * @param sb
	 *            string builder
	 */
	public void getScriptTags(final StringBuilder sb) {
		if (scripts == null) {
			return;
		}
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
	 * 
	 * @param sb
	 *            string builder
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
	 * 
	 * @param sb
	 *            string builder
	 */
	protected final void getAuxiliaryXML(final StringBuilder sb) {
		if (!isAuxiliaryObjectByDefault()) {
			if (auxiliaryObject.needsSaveToXML()) {
				sb.append("\t<auxiliary val=\"");
				sb.append(auxiliaryObject.isOn());
				sb.append("\"/>\n");
			} else if (getMetasLength() > 0 && !auxiliaryObject.isOn()) {
				// force save "not auxiliary" for e.g. segments created by
				// polygon algo
				sb.append("\t<auxiliary val=\"false\"/>\n");
			}
		} else if (!auxiliaryObject.isOn()) {
				// needed for eg GeoTexts (in Algebra View but Auxilliary by
				// default from ggb 4.0)
			sb.append("\t<auxiliary val=\"false\"/>\n");
		}
	}

	/**
	 * returns all visual xml tags (like show, objColor, labelOffset, ...)
	 * 
	 * @param sb
	 *            string builder
	 */
	protected void getXMLvisualTags(final StringBuilder sb) {
		XMLBuilder.getXMLvisualTags(this, sb, true);
	}

	/**
	 * @param sb
	 *            string builder
	 */
	protected void appendObjectColorXML(StringBuilder sb) {
		sb.append("\t<objColor");
		XMLBuilder.appendRGB(sb, objColor);
		sb.append(" alpha=\"");
		// changed from alphavalue (don't want alpha="-1.0" in XML)
		// see GeoList
		sb.append(getAlphaValue());
		sb.append("\"");
		StringTemplate tpl = StringTemplate.xmlTemplate;
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
				StringUtil.encodeXML(sb, colFunction.get(3).getLabel(tpl));
				sb.append('\"');
			}
			sb.append(" colorSpace=\"");
			sb.append(colorSpace);
			sb.append('\"');
		}

		if (isHatchingEnabled()) {
			sb.append(" fillType=\"");
			sb.append(fillType.ordinal());
			sb.append("\" hatchAngle=\"");
			sb.append(hatchingAngle);
			sb.append("\" hatchDistance=\"");
			sb.append(hatchingDistance);
			sb.append("\"");
		} else if (fillType == FillType.IMAGE) {
			sb.append(" image=\"");
			sb.append(graphicsadapter.getImageFileName());
			sb.append('\"');
		}
		if (fillType == FillType.SYMBOLS) {
			sb.append(" fillSymbol=\"");
			sb.append(fillSymbol);
			sb.append('\"');
		}
		if (inverseFill) {
			sb.append(" inverseFill=\"true\"");
		}
		sb.append("/>\n");
	}

	/**
	 * @param sb
	 *            string builder
	 */
	protected void getXMLanimationTags(final StringBuilder sb) {
		StringTemplate tpl = StringTemplate.xmlTemplate;
		// animation step width
		if (isPointerChangeable()) {
			sb.append("\t<animation");
			if (!isGeoNumeric() || !((GeoNumeric) this).isAutoStep()) {
				final String animStep = animationIncrement == null ? "1"
						: getAnimationStepObject().getLabel(tpl);
				sb.append(" step=\"");
				StringUtil.encodeXML(sb, animStep);
				sb.append("\"");
			}
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
	 * 
	 * @param sb
	 *            string builder
	 */
	protected void getXMLfixedTag(final StringBuilder sb) {
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
	 * Appends isShape tag to given builder
	 * 
	 * @param sb
	 *            string builder
	 */
	protected final void getXMLisShapeTag(final StringBuilder sb) {
		// was object created with shape tool
		if (isShape()) {
			sb.append("\t<isShape val=\"true\"/>\n");
		}
	}

	/**
	 * returns all class-specific xml tags for getXML GeoGebra File Format
	 * 
	 * @param sb
	 *            string builder
	 */
	protected void getXMLtags(final StringBuilder sb) {
		// sb.append(getLineStyleXML());
		getXMLvisualTags(sb);
		getXMLanimationTags(sb);
		getXMLfixedTag(sb);
		getXMLisShapeTag(sb);
		getAuxiliaryXML(sb);
		getBreakpointXML(sb);
		if (kernel.getSaveScriptsToXML()) {
			getScriptTags(sb);
		}
	}

	private void getExtraTagsXML(StringBuilder sb) {
		if (this.getParentAlgorithm() instanceof AlgoBarChart) {
			((AlgoBarChart) this.getParentAlgorithm()).barXml(sb);
		}
	}

	/**
	 * returns some class-specific xml tags for getConstructionRegrOut (default
	 * implementation, may be overridden in certain subclasses)
	 * 
	 * @param sb
	 *            string builder
	 * @param tpl
	 *            string template
	 */
	public void getXMLtagsMinimal(final StringBuilder sb, StringTemplate tpl) {
		sb.append(toValueStringMinimal(tpl));
	}

	/**
	 * returns class-specific value string for getConstructionRegressionOut
	 * (default implementation, may be overridden in certain subclasses)
	 * 
	 * @param tpl
	 *            string template
	 * @return value string
	 */
	protected String toValueStringMinimal(StringTemplate tpl) {
		return toValueString(tpl);
	}

	/**
	 * returns the number in rounded format to 6 decimal places, in case of the
	 * number is very close to 0, it returns the exact value
	 * 
	 * @param number
	 *            number to be formated
	 * @return formatted String
	 */
	protected String regrFormat(final double number) {
		if (Math.abs(number) < 0.000001) {
			final Double numberD = new Double(number);
			return numberD.toString();
		}
		// this constructors uses US locale, so we don't have to worry about ","
		if (numberFormatter6 == null) {
			numberFormatter6 = FormatFactory.getPrototype().getNumberFormat("#.######", 6);
		}
		return numberFormatter6.format(number);
	}

	/**
	 * Appends line type and line thickness as xml string to given builder.
	 * 
	 * @param sb
	 *            string builder
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
		if (hasLineOpacity() && getLineOpacity() < 255) {
			sb.append(" opacity=\"");
			sb.append(lineOpacity);
			sb.append("\"");
		}
		sb.append("/>\n");
	}

	/**
	 * Returns line type and line thickness as xml string.
	 * 
	 * @param sb
	 *            string builder
	 * @see #getXMLtags(StringBuilder) of GeoConic, GeoLine and GeoVector
	 */
	protected void getBreakpointXML(final StringBuilder sb) {
		if (isConsProtBreakpoint) {
			sb.append("\t<breakpoint val=\"");
			sb.append(isConsProtBreakpoint);
			sb.append("\"/>\n");
		}
	}

	/**
	 * Append show condition tag
	 * 
	 * @param sb
	 *            string builder for XML
	 */
	void getShowObjectConditionXML(StringBuilder sb) {
		if (condShowObject != null && kernel.getSaveScriptsToXML()) {
			sb.append("\t<condition showObject=\"");
			StringUtil.encodeXML(sb,
					condShowObject.getLabel(StringTemplate.xmlTemplate));
			sb.append("\"/>\n");
		}
	}

	@Override
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

	@Override
	final public int getLineType() {
		return lineType;
	}

	/**
	 * @return the line type for hidden parts
	 */
	final public int getLineTypeHidden() {
		return lineTypeHidden;
	}

	@Override
	public void setLineThickness(final int th) {
		lineThickness = Math.max(0, th);
	}

	/**
	 * set line thickness and/or visibility (if th == 0)
	 * 
	 * @param th
	 *            new thickness
	 */
	public void setLineThicknessOrVisibility(final int th) {

		if (isRegion()) {
			setLineThickness(th);
		} else {
			if (th > 0) {
				setEuclidianVisibleIfNoConditionToShowObject(true);
				setLineThickness(th);
			} else {
				setEuclidianVisibleIfNoConditionToShowObject(false);
			}
		}
	}

	@Override
	public void setLineType(final int i) {
		lineType = i;
	}

	@Override
	public void setLineTypeHidden(final int i) {
		lineTypeHidden = i;
	}

	/**
	 * @param type
	 *            new decoration type
	 */
	public void setDecorationType(final int type) {
		decorationType = type;
	}

	/**
	 * @param type
	 *            decoration type
	 * @param max
	 *            max for this object class
	 */
	public void setDecorationType(final int type, int max) {
		if (type >= max || type < 0) {
			decorationType = DECORATION_NONE;
		} else {
			decorationType = type;
		}
	}

	@Override
	public boolean isGeoElement3D() {
		return false;
	}

	/**
	 * @return true if can change fill type (e.g. hatching)
	 */
	public boolean hasFillType() {
		return isFillable();
	}

	@Override
	public boolean isRegion3D() {
		return false;
	}

	@Override
	public boolean hasDrawable3D() {
		return isGeoElement3D();
	}

	/**
	 * @return true for 3D geos with level of detail
	 */
	public boolean hasLevelOfDetail() {
		return false;
	}

	@Override
	public boolean isGeoAngle() {
		return false;
	}

	@Override
	public boolean isGeoBoolean() {
		return false;
	}

	@Override
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
	 * @return true for implicit surfaces
	 */
	public boolean isGeoImplicitSurface() {
		return false;
	}

	@Override
	public boolean isGeoImplicitCurve() {
		return false;
	}

	@Override
	public boolean isGeoConic() {
		return false;
	}

	@Override
	public boolean isGeoConicPart() {
		return false;
	}

	@Override
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

	@Override
	public boolean isRealValuedFunction() {
		return false;
	}

	@Override
	public boolean isGeoImage() {
		return false;
	}

	/**
	 * @return true for turtles
	 */
	public boolean isGeoTurtle() {
		return false;
	}

	@Override
	public boolean isGeoLine() {
		return false;
	}

	@Override
	public boolean isGeoPlane() {
		return false;
	}

	/**
	 * @return true for quadrics
	 */
	public boolean isGeoQuadric() {
		return false;
	}

	/**
	 * @return true for loci
	 */
	public boolean isGeoLocus() {
		return false;
	}

	@Override
	public boolean isGeoNumeric() {
		return false;
	}

	@Override
	public boolean isGeoPoint() {
		return false;
	}

	@Override
	public boolean isGeoCasCell() {
		return false;
	}

	@Override
	public boolean isGeoPolygon() {
		return false;
	}

	@Override
	public boolean isGeoPolyhedron() {
		return false;
	}

	@Override
	public boolean isGeoRay() {
		return false;
	}

	@Override
	public boolean isGeoSegment() {
		return false;
	}

	@Override
	public boolean isGeoText() {
		return false;
	}

	@Override
	public boolean isGeoVector() {
		return false;
	}

	@Override
	public boolean isGeoCurveCartesian() {
		return false;
	}

	/**
	 * @return true for cartesian surfaces
	 */
	public boolean isGeoSurfaceCartesian() {
		return false;
	}

	/**
	 * @return true for functions evaluable in CAS
	 */
	public boolean isCasEvaluableObject() {
		return false;
	}

	@Override
	final public boolean isExpressionNode() {
		return false;
	}

	@Override
	final public boolean isVariable() {
		return false;
	}

	@Override
	public boolean isShape() {
		return false;
	}

	/**
	 * @param isShape
	 *            - true, if geo was created with shape tool
	 */
	public void setIsShape(boolean isShape) {
		// overridden for conics & polygons
	}

	@Override
	final public boolean contains(final ExpressionValue ev) {
		return ev == this;
	}

	/*
	 * ** hightlighting and selecting only for internal purpouses, i.e. this is
	 * not saved
	 */
	@Override
	public boolean setSelected(final boolean flag) {
		if (flag != selected) {
			selected = flag;
			kernel.notifyUpdateHightlight(this);
			return true;
		}
		return false;
	}

	@Override
	public boolean setHighlighted(final boolean flag) {
		if (flag != highlighted) {
			highlighted = flag;
			kernel.notifyUpdateHightlight(this);
			return true;
		}
		return false;
	}

	@Override
	final public boolean doHighlighting() {
		return (highlighted || selected)
				&& (!isLocked() || isSelectionAllowed(null))
				&& (kernel.getApplication()
						.getMode() != EuclidianConstants.MODE_SHOW_HIDE_OBJECT);
	}

	/**
	 * @return true if this object is selected
	 */
	final public boolean isSelected() {
		return selected;
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	/**
	 * @return true for angles
	 */
	public int getAngleDim() {
		return 0;
	}

	@Override
	public boolean evaluatesToNonComplex2DVector() {
		return false;
	}

	@Override
	public boolean evaluatesToVectorNotPoint() {
		return false;
	}

	@Override
	public boolean evaluatesToText() {
		return false;
	}

	@Override
	public boolean evaluatesToList() {
		return false;
	}

	@Override
	public boolean isGeoButton() {
		return false;
	}

	@Override
	public boolean isGeoAudio() {
		return false;
	}

	@Override
	public boolean isGeoVideo() {
		return false;
	}

	/**
	 * @return true for strokes
	 */
	public boolean isPenStroke() {
		return false;
	}

	/**
	 * @return true if this is using visual defaults
	 */
	public final boolean isUseVisualDefaults() {
		return useVisualDefaults;
	}

	/**
	 * @param useVisualDefaults
	 *            true to use visual defaults
	 */
	public final void setUseVisualDefaults(final boolean useVisualDefaults) {
		this.useVisualDefaults = useVisualDefaults;
	}

	/**
	 * @return true if this can have absoute screen location
	 */
	public boolean isAbsoluteScreenLocateable() {
		return false;
	}

	@Override
	final public GeoBoolean getShowObjectCondition() {
		return condShowObject;
	}

	@Override
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
	 * 
	 * @param bool
	 *            condition to show object
	 */
	final public void removeCondition(final GeoBoolean bool) {
		if (condShowObject == bool) {
			condShowObject = null;
		}
	}

	@Override
	final public GeoList getColorFunction() {
		return colFunction;
	}

	@Override
	public void setColorFunction(final GeoList col) {
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
	 * @return temporary set of algoritms
	 */
	protected static TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<>(algoComparator);
		}
		return tempSet;
	}

	/**
	 * @param rwTransVec
	 *            translation vector
	 * @param endPosition
	 *            end position
	 * @return true if successful
	 */
	protected boolean movePoint(final Coords rwTransVec,
			final Coords endPosition) {
		return false;
	}

	/**
	 * @param rwTransVec
	 *            translation vector
	 * @param endPosition
	 *            end position
	 * @return true if successful
	 */
	protected boolean moveVector(final Coords rwTransVec,
			final Coords endPosition) {

		boolean movedGeo = false;

		final GeoVector vector = (GeoVector) this;
		if (endPosition != null) {
			vector.setCoords(endPosition.getX(), endPosition.getY(), 0);
			movedGeo = true;
		}

		// translate point
		else {
			double x = vector.getX() + rwTransVec.getX();
			double y = vector.getY() + rwTransVec.getY();

			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			if (Math.abs(rwTransVec.getX()) > Kernel.MIN_PRECISION) {
				x = DoubleUtil.checkDecimalFraction(x);
			}
			if (Math.abs(rwTransVec.getY()) > Kernel.MIN_PRECISION) {
				y = DoubleUtil.checkDecimalFraction(y);
			}

			// set translated point coords
			vector.setCoords(x, y, 0);
			movedGeo = true;
		}

		return movedGeo;
	}

	/**
	 * try to move the geo with coord parent numbers (e.g. point defined by
	 * sliders)
	 * 
	 * @param rwTransVec
	 *            translation vector
	 * @param endPosition
	 *            end position
	 * @param updateGeos
	 *            geos to be updated
	 * @param tempMoveObjectList1
	 *            temporary list
	 * @return false if not moveable this way
	 */
	public boolean moveFromChangeableCoordParentNumbers(final Coords rwTransVec,
			final Coords endPosition, final ArrayList<GeoElement> updateGeos,
			final ArrayList<GeoElement> tempMoveObjectList1) {
		return false;
	}

	@Override
	public boolean hasChangeableCoordParentNumbers() {
		return false;
	}

	/**
	 * 
	 * @return true if geo is child of a parent that can change e.g by dragging
	 *         in 3D
	 */
	public boolean hasChangeableParent3D() {
		return false;
	}

	/**
	 * 
	 * @return changeable parent (or null if none)
	 */
	public ChangeableParent getChangeableParent3D() {
		return null;
	}

	/**
	 * add changeable coord parent number to update list
	 * 
	 * @param number
	 *            changeable number
	 * @param updateGeos
	 *            set of geos
	 * @param tempMoveObjectList1
	 *            temporary list
	 */
	protected static final void addParentToUpdateList(final GeoElement number,
			final ArrayList<GeoElement> updateGeos,
			ArrayList<GeoElement> tempMoveObjectList1) {
		if (updateGeos != null) {
			// add number to update list
			updateGeos.add(number);
		} else {
			// update number right now
			ArrayList<GeoElement> tempMoveObjectList2 = tempMoveObjectList1;
			if (tempMoveObjectList1 == null) {
				tempMoveObjectList2 = new ArrayList<>();
			}
			tempMoveObjectList2.add(number);
			updateCascade(tempMoveObjectList2, getTempSet(), false);
		}
	}

	// private ArrayList<GeoElement> tempMoveObjectList;

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
	 * @param spreadsheetCoords
	 *            point (col,row)
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
		return algoMacroOutput;
	}

	/**
	 * @param isAlgoMacroOutput
	 *            mark/unmark this geo as macro output
	 */
	public void setAlgoMacroOutput(final boolean isAlgoMacroOutput) {
		this.algoMacroOutput = isAlgoMacroOutput;
	}

	@Override
	public abstract boolean isEqual(GeoElementND geo);

	/**
	 * Returns whether this - f gives 0 in the CAS.
	 * 
	 * @param f
	 *            other geo
	 * @return whether this - f gives 0 in the CAS.
	 */
	final public boolean isDifferenceZeroInCAS(final GeoElementND f) {
		// use CAS to check f - g = 0
		String myFormula = getFormulaString(StringTemplate.defaultTemplate,
				true);
		String otherFormula = f.getFormulaString(StringTemplate.defaultTemplate,
				true);
		if (myFormula.equals(otherFormula)) {
			return true;
		}
		try {
			final StringBuilder diffSb = new StringBuilder();
			diffSb.append("Simplify[");

			diffSb.append(myFormula);
			diffSb.append("-(");
			diffSb.append(otherFormula);
			diffSb.append(")]");
			final String diff = kernel.evaluateGeoGebraCAS(diffSb.toString(),
					null);
			return (Double.valueOf(diff) == 0d);
		} catch (final Throwable e) {
			return false;
		}
	}

	@Override
	public String getFormulaString(final StringTemplate tpl,
			final boolean substituteNumbers) {

		String ret = "";

		// GeoFunction & GeoFunctionNVar override this, no need to care about
		// them
		// only inequalities call this

		// matrices
		if (isGeoList() && tpl.hasType(StringType.LATEX)
				&& ((GeoList) this).isMatrix()) {
			ret = toLaTeXString(!substituteNumbers, tpl);
		}
		// vectors
		else if (isGeoVector() && tpl.hasType(StringType.LATEX)) {
			ret = toLaTeXString(!substituteNumbers, tpl);
		} // curves
		else if (isGeoCurveCartesian() && tpl.hasType(StringType.LATEX)) {
			ret = toLaTeXString(!substituteNumbers, tpl);
		} else if (isGeoSurfaceCartesian() && tpl.hasType(StringType.LATEX)) {
			ret = toLaTeXString(!substituteNumbers, tpl);
		} else {
			ret = substituteNumbers ? toValueString(tpl) : getDefinition(tpl);
		}

		// GeoNumeric eg a=1
		if ("".equals(ret) && isGeoNumeric() && !substituteNumbers
				&& isLabelSet()) {
			ret = tpl.printVariableName(label);
		}
		if ("".equals(ret) && isGeoCasCell()
				&& ((GeoCasCell) this).getAssignmentVariable() != null) {
			ret = getLabel(tpl);
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
				ret = "?";
			} else if ((Unicode.INFINITY + "").equals(ret)) {
				ret = "\\infty";
			} else if ((Unicode.MINUS_INFINITY_STRING).equals(ret)) {
				ret = "-\\infty";
			}
		}

		return ret;
	}

	// ===================================================
	// G.Sturr 2010-5-14
	// New code for spreadsheet tracing with trace manager
	// ===================================================

	@Override
	public boolean getSpreadsheetTrace() {
		return spreadsheetTrace;
	}

	/**
	 * Set tracing flag for this geo
	 * 
	 * @param traceFlag
	 *            true to trace to spreadsheet
	 */
	public void setSpreadsheetTrace(final boolean traceFlag) {
		if (!traceFlag) {
			traceSettings = null;
		}
		spreadsheetTrace = traceFlag;

		// #2153
		if (spreadsheetTrace) {
			cons.addTracingGeo();
		}
	}

	/**
	 * Request spreadsheet trace manager to auto-reset the tracing columns.
	 * Called after mouse_release.
	 */
	public void resetTraceColumns() {
		if (kernel.getApplication().isUsingFullGui()) {
			kernel.getApplication().resetTraceColumn(this);
		}
	}

	/** @return if geos of this type can be traced to the spreadsheet */
	public boolean isSpreadsheetTraceable() {
		return this instanceof SpreadsheetTraceable;
	}

	/**
	 * Used by list to check if geos are compatible.
	 * 
	 * @return has spreadsheet mode that is a traceable mode
	 */
	public boolean hasSpreadsheetTraceModeTraceable() {
		return isSpreadsheetTraceable();
	}

	/**
	 * @return spreadsheet trace settings
	 */
	public SpreadsheetTraceSettings getTraceSettings() {
		if (traceSettings == null) {
			traceSettings = new SpreadsheetTraceSettings();
			// if only copy is possible, set it immediately
			if (getTraceModes() == TraceModesEnum.ONLY_COPY) {
				traceSettings.doTraceGeoCopy = true;
			}
		}

		return traceSettings;
	}

	/**
	 * @param t
	 *            spreadsheet trace settings
	 */
	public void setTraceSettings(final SpreadsheetTraceSettings t) {
		traceSettings = t;
	}

	/**
	 * over-ridden in GeoList
	 * 
	 * @return element for properties dialog
	 */
	public GeoElement getGeoElementForPropertiesDialog() {
		return this;
	}

	/**
	 * over-ridden in GeoText
	 * 
	 * @return true if this was created by Text command
	 */
	public boolean isTextCommand() {
		return false;
	}

	@Override
	final public boolean isInTree() {
		return inTree;
	}

	@Override
	final public void setInTree(final boolean flag) {
		inTree = flag;
	}

	/*
	 * Scripting
	 */

	/**
	 * @return whether some script is associated with the geo
	 */
	public boolean hasScripts() {
		return scripts != null && scripts.length != 0;
	}

	/**
	 * @param script
	 *            script
	 */
	public void setClickScript(Script script) {
		setScript(script, EventType.CLICK);
	}

	/**
	 * Sets update script
	 * 
	 * @param script
	 *            script
	 */
	public void setUpdateScript(Script script) {
		setScript(script, EventType.UPDATE);
	}

	/**
	 * Set a script for this geo
	 * 
	 * @param script
	 *            source code for the new script
	 * @param evt
	 *            the event type that will trigger the script
	 */
	public void setScript(Script script, EventType evt) {
		if (evt == EventType.UPDATE && !canHaveUpdateScript()
				|| evt == EventType.CLICK && !canHaveClickScript()) {
			return;
		}
		if (this.scripts == null) {
			this.scripts = new Script[EventType.values().length];
		}

		// Make sure we're listening to events for this script
		kernel.getApplication().startGeoScriptRunner();
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

	@Override
	public Script getScript(EventType type) {
		if (scripts == null) {
			return null;
		}
		return scripts[type.ordinal()];
	}

	/**
	 * Runs the click script of this object
	 * 
	 * @param arg
	 *            argument that replaces all %0 in the script
	 */
	public void runClickScripts(final String arg) {
		// "%0" is replaced in the script by "arg"
		kernel.getApplication().dispatchEvent(
				new Event(EventType.CLICK, this, arg == null ? label : arg));
	}

	/**
	 * @param show
	 *            true to show trimmed lines
	 */
	public void setShowTrimmedIntersectionLines(final boolean show) {
		showTrimmedIntersectionLines = show;
	}

	@Override
	public boolean getShowTrimmedIntersectionLines() {
		return showTrimmedIntersectionLines;
	}

	@Override
	public boolean isPointInRegion() {
		return false;
	}

	/**
	 * @param flag
	 *            mark/unmark this geo as random
	 */
	public void setRandomGeo(final boolean flag) {
		isRandomGeo = flag;
	}

	@Override
	public boolean isRandomGeo() {
		return isRandomGeo;
	}

	@Override
	public void updateRandomGeo() {

		// update parent algorithm, like AlgoRandom
		final AlgoElement algo = getParentAlgorithm();
		if (algo != null) {
			algo.compute(); // eg AlgoRandom etc
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
	 * @param viewId
	 *            view id
	 * @param setVisible
	 *            true make this geo visible in given view
	 */
	public void setVisibility(final int viewId, final boolean setVisible) {
		if (this.viewFlags == null) {
			this.viewFlags = new ArrayList<>();
		}
		if (setVisible) {
			if (!viewFlags.contains(viewId)) {
				viewFlags.add(viewId);
			}
		} else {
			viewFlags.remove(Integer.valueOf(viewId));
		}
	}

	@Override
	public boolean isVisibleInView(final int viewId) {
		if (viewFlags == null) {
			return viewId == App.VIEW_EUCLIDIAN;
		}
		return viewFlags.contains(viewId);
	}

	@Override
	final public void addView(final int viewId) {
		if (App.isView3D(viewId)) {
			addViews3D();
		} else {
			setVisibility(viewId, true);
		}
	}

	/**
	 * set visible in 3D views
	 */
	final public void addViews3D() {
		visibleInView3D = ExtendedBoolean.TRUE;
	}

	@Override
	public void removeView(final int viewId) {
		if (App.isView3D(viewId)) {
			removeViews3D();
		} else {
			setVisibility(viewId, false);
		}
	}

	/**
	 * set not visible in 3D views
	 */
	final public void removeViews3D() {
		visibleInView3D = ExtendedBoolean.FALSE;
	}

	@Override
	public void setViewFlags(List<Integer> flags) {
		if (flags == null) {
			viewFlags = null;
			return;
		}
		if (this.viewFlags == null) {
			this.viewFlags = new ArrayList<>();
		} else {
			viewFlags.clear();
		}
		viewFlags.addAll(flags);
		// Collections.copy(list, viewFlags);
	}

	@Override
	public List<Integer> getViewSet() {
		if (viewFlags == null) {
			return null;
		}
		final List<Integer> list = new ArrayList<>();
		list.addAll(viewFlags);
		// Collections.copy(list, viewFlags);
		return list;
	}

	@Override
	public boolean isVisibleInView3D() {

		switch (visibleInView3D) {
		case UNKNOWN:
		default:
			return isVisibleInView3DNotSet();
		case TRUE:
			return hasDrawable3D();
		case FALSE:
			return false;
		}
	}

	/**
	 * decide if visible in 3D view when flag is not already set
	 * 
	 * @return true if should be visible in 3D view
	 */
	protected boolean isVisibleInView3DNotSet() {
		if (hasDrawable3D()) {
			if (isGeoElement3D() || isVisibleInView(App.VIEW_EUCLIDIAN)) {
				// visible: we set it
				visibleInView3D = ExtendedBoolean.TRUE;
				return true;
			}

			// not visible: we set it
			visibleInView3D = ExtendedBoolean.FALSE;
			return false;
		}
		return false;

	}

	/**
	 * @return whether this is visible in plane
	 */
	public ExtendedBoolean getVisibleInViewForPlane() {
		return visibleInViewForPlane;
	}

	@Override
	public boolean isVisibleInViewForPlane() {
		switch (visibleInViewForPlane) {
		case UNKNOWN:
		default:
			if (isVisibleInView3D()) {
				visibleInViewForPlane = ExtendedBoolean.TRUE;
				return true;
			}
			visibleInViewForPlane = ExtendedBoolean.FALSE;
			return false;
		case TRUE:
			return true;
		case FALSE:
			return false;
		}

	}

	/**
	 * set if this is visible in 3D view or not
	 * 
	 * @param flag
	 *            flag
	 */
	public void setVisibleInView3D(boolean flag) {
		if (flag) {
			visibleInView3D = ExtendedBoolean.TRUE;
		} else {
			visibleInView3D = ExtendedBoolean.FALSE;
		}
	}

	/**
	 * set if this is visible in view for plane or not
	 * 
	 * @param flag
	 *            flag
	 */
	public void setVisibleInViewForPlane(boolean flag) {
		if (flag) {
			visibleInViewForPlane = ExtendedBoolean.TRUE;
		} else {
			visibleInViewForPlane = ExtendedBoolean.FALSE;
		}
	}

	@Override
	public void setVisibleInView3D(GeoElement geo) {
		visibleInView3D = geo.visibleInView3D;
	}

	@Override
	public void setVisibleInViewForPlane(GeoElement geo) {
		visibleInViewForPlane = geo.visibleInViewForPlane;
	}

	@Override
	public void setSelectionAllowed(final boolean selectionAllowed) {
		this.selectionAllowed = selectionAllowed;
	}

	/**
	 * @param ev
	 *            view
	 * @return true if selection is allowed
	 */
	public boolean isSelectionAllowed(EuclidianViewInterfaceSlim ev) {
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

	@Override
	public boolean isHatchingEnabled() {
		return fillType.isHatch();
	}

	@Override
	public void setHatchingAngle(final int angle) {
		hatchingAngle = angle;
	}

	@Override
	public double getHatchingAngle() {
		return hatchingAngle;
	}

	@Override
	public void setHatchingDistance(final int distance) {
		hatchingDistance = distance;
	}

	@Override
	public int getHatchingDistance() {
		return hatchingDistance;
	}

	@Override
	public MyImage getFillImage() {
		return graphicsadapter.getFillImage();
	}

	// public void setFillImage(BufferedImage image){
	// this.fillImage = image;
	// }

	/**
	 * @param filename
	 *            filename of fill image
	 */
	public void setFillImage(final String filename) {
		graphicsadapter.setFillImage(filename);
	}

	@Override
	public FillType getFillType() {
		return fillType;
	}

	@Override
	public void setFillType(final FillType fillType) {
		this.fillType = fillType;
	}

	@Override
	public void setImageFileName(final String fileName) {
		graphicsadapter.setImageFileName(fileName);
	}

	@Override
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

	@Override
	public boolean isInverseFill() {
		return inverseFill;
	}

	// private Coords mainDirection = Coords.VZ;

	@Override
	public Coords getMainDirection() {
		return Coords.VZ;
	}

	/**
	 * gets shortest distance to point p overridden in eg GeoPoint, GeoLine for
	 * compound paths
	 * 
	 * @param p
	 *            other point
	 * @return distance
	 */
	public double distance(final GeoPoint p) {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public double distance(final GeoPointND p) {
		if ((p instanceof GeoElement) && (p instanceof GeoPoint)) {
			return distance((GeoPoint) p);
		}
		Log.debug("TODO : distance from " + getGeoClassType() + " to ND point");
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
		return isPickable && isSelectionAllowed(null);
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

	/**
	 * Srore copy of this geo in stack
	 */
	public void storeClone() {
		if (tempClone == null) {
			tempClone = new Stack<>();
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
	 * Randomize for probability chacking overriden in subclasses that allow
	 * randomization
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
	 * 
	 * @param correspondingCasCell
	 *            corresponding CAS cell
	 */
	final public void setCorrespondingCasCell(
			final GeoCasCell correspondingCasCell) {
		this.correspondingCasCell = correspondingCasCell;
	}

	@Override
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
	 * @param i
	 *            algo
	 * @return true if algo is in update set
	 */
	public boolean algoUpdateSetContains(final AlgoElement i) {
		return getAlgoUpdateSet().contains(i);
	}

	/**
	 * Makes sure that column headings are empty list of GeoTexts
	 */
	protected void resetSpreadsheetColumnHeadings() {
		if (spreadsheetColumnHeadings == null) {
			spreadsheetColumnHeadings = new ArrayList<>();
		} else {
			spreadsheetColumnHeadings.clear();
		}
	}

	/**
	 * for the SpreadsheetTraceable interface. Default: just return the label
	 * 
	 * @return list of column headings
	 */
	final public ArrayList<GeoText> getColumnHeadings() {

		// if no values / only copy
		if (getTraceSettings().doTraceGeoCopy) {
			// trace copy
			updateColumnHeadingsForTraceGeoCopy();
		} else {
			// update column headings for trace values
			updateColumnHeadingsForTraceValues();
		}

		return spreadsheetColumnHeadings;
	}

	/** update column headings for trace values */
	public void updateColumnHeadingsForTraceValues() {
		// for NumberValue
		updateColumnHeadingsForTraceGeoCopy();
	}

	/**
	 * 
	 * @return string description of values traced
	 */
	public String getTraceDialogAsValues() {
		return getLabelTextOrHTML(false); // columnHeadingsForTraceDialog.toString();
	}

	/** Used by TraceDialog for "Trace as... value of/copy of */
	public enum TraceModesEnum {
		/** no value for this geo, only copy */
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
	public TraceModesEnum getTraceModes() {
		return TraceModesEnum.ONE_VALUE_ONLY; // default for NumberValue
	}

	/**
	 * 
	 * update column headings when "trace geo copy"
	 */
	protected void updateColumnHeadingsForTraceGeoCopy() {
		resetSpreadsheetColumnHeadings();
		spreadsheetColumnHeadings.add(getNameGeo());
	}

	/**
	 * 
	 * @return geo text = Name[this]
	 */
	protected GeoText getNameGeo() {
		AlgoName algo = new AlgoName(cons, this);
		GeoText ret = algo.getGeoText();
		ret.setEuclidianVisible(false);
		return ret;
	}

	/**
	 * 
	 * @param node
	 *            expression describing the text
	 * @return GeoText linked to expression
	 */
	protected GeoText getColumnHeadingText(ExpressionNode node) {

		GeoText ret;

		if (node.getGeoElementVariables(SymbolicMode.NONE) == null) {
			// no variables in expression node : compute only once
			ret = new GeoText(cons);
			AlgoDependentText.nodeToGeoText(node, ret, ret.getStringTemplate());
		} else {
			AlgoDependentText algo = new AlgoDependentText(cons, node, false);
			algo.setProtectedInput(true);
			ret = algo.getGeoText();
		}

		ret.setEuclidianVisible(false);
		return ret;
	}

	/**
	 * default for elements implementing NumberValue interface eg GeoSegment,
	 * GeoPolygon
	 * 
	 * @param spreadsheetTraceList
	 *            list of numbers for spreadsheet
	 */
	public void addToSpreadsheetTraceList(
			ArrayList<GeoNumeric> spreadsheetTraceList) {

		if (this instanceof NumberValue) {

			final GeoNumeric xx = new GeoNumeric(cons,
					((NumberValue) this).getDouble());
			spreadsheetTraceList.add(xx);

		} else {
			Log.debug("error in getSpreadsheetTraceList(), not a NumberValue");
		}

	}

	@Override
	public String toString(StringTemplate tpl) {
		return label;
	}

	@Override
	final public ExpressionValue traverse(Traversing t) {
		return t.process(this);
	}

	@Override
	final public boolean inspect(Inspecting t) {
		return t.check(this);
	}

	/**
	 * Says if this geo has a "meta geo", e.g. a segment coming from a polygon
	 * 
	 * @return length of metas
	 */
	public int getMetasLength() {
		return 0;
	}

	@Override
	final public GeoElement unwrap() {
		return this;
	}

	@Override
	final public ExpressionNode wrap() {
		return new ExpressionNode(getKernel(), this);
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
			return ((AbsoluteScreenLocateable) this)
					.isAbsoluteScreenLocActive();
		}

		if (!isPinnable()) {
			return false;
		}

		return getParentAlgorithm() instanceof AlgoAttachCopyToView;
	}

	@Override
	public boolean hasCoords() {
		return false;
	}

	@Override
	public void setScripting(GeoElement oldGeo) {
		if (oldGeo.scripts == null) {
			this.scripts = null;
			return;
		}
		if (this.scripts == null) {
			this.scripts = new Script[EventType.values().length];
		}
		for (int i = 0; i < oldGeo.scripts.length; i++) {
			if (oldGeo.scripts[i] != null) {
				scripts[i] = oldGeo.scripts[i].copy();
			} else {
				scripts[i] = null;
			}
		}
	}

	/**
	 * Implementation for numbers, segments etc.
	 */
	@Override
	public ExpressionValue derivative(FunctionVariable fv, Kernel kernel0) {
		return new MyDouble(kernel, 0);
	}

	@Override
	public ExpressionValue integral(FunctionVariable fv, Kernel kernel0) {
		return null;
	}

	/**
	 * @return whether it's a matrix. Overridden in GeoList
	 */
	public boolean isMatrix() {
		return false;
	}

	@Override
	public String getFillSymbol() {
		return fillSymbol;
	}

	@Override
	public void setFillSymbol(String symbol) {
		fillSymbol = symbol;
	}

	@Override
	public final int getDecorationType() {
		return decorationType;
	}

	/**
	 * Sets the flag whether this objects value or label should be sent to CAS
	 * 
	 * @param var
	 *            true if the value should be sent to cas false otherwise
	 */
	public void setSendValueToCas(boolean var) {
		sendValueToCas = var;
	}

	/**
	 * @return flag wheter this objects value or label should be sent to CAS
	 */
	public boolean getSendValueToCas() {
		return sendValueToCas;
	}

	/**
	 * Adds or modifies the caption to contain the label in P(v1,v2) form
	 * (LaTeX)
	 * 
	 * @param vars
	 *            in LaTeX format
	 */
	public void setCaptionBotanaVars(String vars) {
		setLabelMode(LABEL_CAPTION);
		labelVisible = true;

		String labelWithVars = "{\\bf\\it " + label + vars + "}\\\\";

		if (caption == null) {
			caption = "$" + labelWithVars + "$";
			return;
		}

		if (caption.startsWith(labelWithVars)) {
			return;
		}

		caption = "$" + labelWithVars + "\\\\"
				+ caption.substring(1, caption.length());
	}

	/**
	 * Adds a new poly to the caption (LaTeX)
	 * 
	 * @param poly
	 *            in LaTeX format
	 */
	public void addCaptionBotanaPolynomial(String poly) {
		setLabelMode(LABEL_CAPTION);
		labelVisible = true;

		if (caption != null) {
			caption = caption.substring(0, caption.length() - 1) + poly
					+ "\\\\$";
		} else {
			caption = "$" + poly + "\\\\$";
		}

	}

	/**
	 * @return whether line properties of this object should be editable by
	 *         Prop. View
	 */
	public boolean showLineProperties() {
		return isPath();
	}

	@Override
	public boolean evaluatesTo3DVector() {
		return false;
	}

	//////////////////////////////
	// specific input protection
	/////////////////////////////

	/**
	 * set this can (not) be removed when input of algo
	 * 
	 * @param flag
	 *            flag
	 */
	public void setCanBeRemovedAsInput(boolean flag) {
		canBeRemovedAsInput = flag;
	}

	@Override
	public boolean canBeRemovedAsInput() {
		return canBeRemovedAsInput
				&& (algorithmList == null || algorithmList.size() <= 1);
	}

	@Override
	public boolean hasLineOpacity() {
		return false;
	}

	@Override
	public int getLineOpacity() {
		if (isHideShowGeo()) {
			return lineOpacity / 2;
		}
		return lineOpacity;
	}

	@Override
	public void setLineOpacity(int lineOpacity) {
		this.lineOpacity = lineOpacity;
	}

	@Override
	public boolean evaluatesToNumber(boolean def) {
		return isNumberValue();
	}

	/**
	 * @return whether it's tracing or not
	 */
	public boolean getTrace() {
		return false;
	}

	/** hit type (no/boundary/inside) */
	public enum HitType {
		/** not hit */
		NONE,
		/** boundary hit */
		ON_BOUNDARY,
		/** fill hit */
		ON_FILLING
	}

	/**
	 * @return last hit type
	 */
	abstract public HitType getLastHitType();

	/**
	 * @return whether this evaluates to angle
	 */
	public final boolean isAngle() {
		return getAngleDim() == 1;
	}

	@Override
	public String getAssignmentOperator() {
		return ":=";
	}

	@Override
	public boolean isParametric() {
		return false;
	}

	@Override
	public int getListDepth() {
		return 0;
	}

	/**
	 * @param val
	 *            number
	 * @return val as geoelement or null for MyDouble / MyBoolean
	 */
	public static GeoElement as(NumberValue val) {
		return val instanceof GeoElement ? (GeoElement) val : null;
	}

	@Override
	public String toString(GTemplate tpl) {
		return toString(tpl.getTemplate());
	}

	@Override
	public void setLabelWanted(boolean b) {
		this.labelWanted = b;
	}

	/**
	 * 
	 * @param geo
	 *            other geo
	 * @return whether this and geo are congruent
	 */
	public ExtendedBoolean isCongruent(GeoElement geo) {
		return isEqual(geo) ? ExtendedBoolean.TRUE : ExtendedBoolean.UNKNOWN;
	}

	@Override
	public final void setDefinition(ExpressionNode root) {
		if (definition != null && root == null && algoParent != null) {
			return;
		}
		this.definition = root;
	}

	/**
	 * @param geo
	 *            template element
	 */
	protected void reuseDefinition(GeoElementND geo) {
		if (geo.isIndependent() || geo.getDefinition() == null
				|| geo.getDefinition().isConstant()) {
			this.definition = geo.getDefinition();
		} else {
			this.definition = null;
		}
	}

	@Override
	public ExpressionNode getDefinition() {
		return definition;
	}

	/**
	 * @return whether value == definition
	 */
	public final boolean isSimple() {

		if (!isIndependent()) {
			return false;
		}
		if (definition == null) {
			return true;
		}
		if (definition.getOperation() == Operation.MULTIPLY) {
			if (definition.getLeft().unwrap() instanceof NumberValue
					&& definition.getRight().unwrap() instanceof MyDouble
					&& MyDouble.exactEqual(definition.getRight()
							.evaluateDouble(), MyMath.DEG)) {
				return true;
			}
		}
		ExpressionValue unwrap = definition.unwrap();
		if (unwrap instanceof ExpressionNode) {
			return false;
		}
		if (unwrap instanceof MyDoubleDegreesMinutesSeconds) {
			return false;
		}
		if (unwrap instanceof NumberValue) {
			double val = evaluateDouble();
			return MyDouble.isFinite(val) && !DoubleUtil.isEqual(val, Math.PI)
					&& !DoubleUtil.isEqual(val, Math.E);
		}
		return true;
	}

	@Override
	public ExpressionValue getUndefinedCopy(Kernel kernel1) {
		GeoElement ret = copy();
		ret.setUndefined();
		return ret;
	}

	@Override
	public ExpressionValue toValidExpression() {
		return this;
	}

	@Override
	public boolean evaluatesToNDVector() {
		ValueType vt = getValueType();
		return vt == ValueType.NONCOMPLEX2D || vt == ValueType.VECTOR3D;
	}

	@Override
	public final void updateVisualStyleRepaint(GProperty prop) {
		updateVisualStyle(prop);
		kernel.notifyRepaint();
	}

	@Override
	public DescriptionMode needToShowBothRowsInAV() {
	    if (AlgebraItem.shouldShowOnlyDefinitionForGeo(this)) {
	        return DescriptionMode.DEFINITION;
        }
		String def0 = getDefinition(StringTemplate.defaultTemplate);
		if ("".equals(def0)) {
			return DescriptionMode.VALUE;
		}
		if (getPackedIndex() > 0) {
			return DescriptionMode.VALUE;
		}
		IndexHTMLBuilder sbDef = new IndexHTMLBuilder(false);
		IndexHTMLBuilder sbVal = new IndexHTMLBuilder(false);
		addLabelTextOrHTML(def0, sbDef);
		String def = sbDef.toString();
		String val = getAlgebraDescriptionTextOrHTMLDefault(sbVal);
		return !def.equals(val) ? DescriptionMode.DEFINITION_VALUE
				: DescriptionMode.VALUE;
	}

	/**
	 * @return -1 if this is not part of packed output; 0 for pack header, >0
	 *         for packed items
	 */
	public int getPackedIndex() {
		if (getParentAlgorithm() != null
				&& getParentAlgorithm().getOutputLength() > 1
				&& getParentAlgorithm().hasSingleOutputType()
				&& kernel.getApplication().getSettings().getAlgebra()
						.getTreeMode() == SortMode.ORDER) {
			return getParentAlgorithm().getOutput(0) == this ? 0 : 1;
		}
		return -1;
	}

	/**
	 * if AV update fails (e.g. if row not visible), we can set this to true to
	 * force update later
	 * 
	 * @param flag
	 *            whether update is needed
	 */
	public void setDescriptionNeedsUpdateInAV(boolean flag) {
		descriptionNeedsUpdateInAV = flag;
	}

	/**
	 * @return whether AV update is needed
	 */
	public boolean descriptionNeedsUpdateInAV() {
		return descriptionNeedsUpdateInAV;
	}

	@Override
	public boolean isVisibleInputForMacro() {
		return isLabelSet();
	}

	/**
	 * @param cons1
	 *            construction
	 * @return reference to this
	 */
	public GeoElement toGeoElement(Construction cons1) {
		return this;
	}

	/**
	 * @return this wrapped in array
	 */
	public GeoElement[] asArray() {
		return new GeoElement[] { this };
	}

	@Override
	public void resetDefinition() {
		definition = null;
	}

	@Override
	public boolean isFilled() {
		return getAlphaValueWhenVisible() > 0 || isHatchingEnabled();
	}

	/**
	 * @param labelSet
	 *            the labelSet flag
	 */
	public void setLabelSet(boolean labelSet) {
		this.labelSet = labelSet;
	}

	// @Override
	// public NumberDerivativePair getDualNumber(Kernel kernel) {
	// return new NumberDerivativePair(kernel, evaluateDouble(), 0);
	// }

	/**
	 * @return whether this is locusable (locus or function)
	 */
	public boolean isGeoLocusable() {
		return false;
	}

	/**
	 * 
	 * @return the original screen location
	 */
	public ScreenLocation getScreenLocation() {
		return screenLocation;
	}

	/**
	 * 
	 * @param screenLocation
	 *            to set
	 */
	public void setScreenLocation(ScreenLocation screenLocation) {
		this.screenLocation = screenLocation;
	}

	/**
	 * Sets the original (x, y) location of the geo.
	 * 
	 * @param x
	 *            to set
	 * @param y
	 *            to set
	 */
	public void setScreenLocation(int x, int y) {
		this.screenLocation = new ScreenLocation(x, y);
	}

	/**
	 * 
	 * @return if this has original screen location comes from file.
	 */
	public boolean hasScreenLocation() {
		return screenLocation != null;
	}

	/**
	 * resets original location to null
	 */
	public void resetScreenLocation() {
		screenLocation = null;
	}

	/**
	 * 
	 * @return if geo can be duplicated from Algebra View.
	 */
	public boolean isAlgebraDuplicateable() {
		return true;
	}

	/**
	 *
	 * @return true if when AV has description mode, we want to show description instead of definition
	 */
	final public boolean mayShowDescriptionInsteadOfDefinition() {
		if (AlgebraItem.shouldShowOnlyDefinitionForGeo(this)
				&& getKernel()
						.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			return false;
		}
		if (algoParent == null) {
			return mayShowDescriptionInsteadOfDefinitionNoAlgoParent();
		}
		return algoParent.mayShowDescriptionInsteadOfDefinition();
	}

	/**
	 *
	 * @return true if when AV has description mode, we want to show description instead of definition
	 * (when no algoParent)
	 */
	protected boolean mayShowDescriptionInsteadOfDefinitionNoAlgoParent() {
		return true;
	}

	@Override
	public boolean addAuralCaption(ScreenReaderBuilder sb) {
		if (!StringUtil.empty(getCaptionSimple())) {
			String myCaption = getCaption(StringTemplate.defaultTemplate);
			if (CanvasDrawable.isLatexString(myCaption)) {
				getLaTeXAuralCaption(sb);
			} else {
				sb.append(myCaption);
			}
			sb.endSentence();
			return true;
		}
		return false;
	}

	private void getLaTeXAuralCaption (ScreenReaderBuilder sb) {
		tf = getTfLazy();
		tf.setLaTeX(caption);
		sb.append(getTexAtomSerializerLazy().serialize(tf.root));
	}

	private TeXAtomSerializer getTexAtomSerializerLazy() {
		if (texAtomSerializer == null) {
			texAtomSerializer = new TeXAtomSerializer(null);
		}
		return texAtomSerializer;
	}

	private TeXFormula getTfLazy() {
		if (tf == null) {
			tf = new TeXFormula(caption);
		}
		return tf;
	}

	@Override
	public void addAuralType(Localization loc, ScreenReaderBuilder sb) {
		sb.append(translatedTypeStringForAlgebraView());
		sb.appendSpace();
	}

	@Override
	public void addAuralLabel(Localization loc, ScreenReaderBuilder sb) {
		sb.append(getLabelSimple());
		sb.endSentence();
	}

	@Override
	public void addAuralName(Localization loc, ScreenReaderBuilder sb) {
		addAuralType(loc, sb);
		if (!addAuralCaption(sb)) {
			addAuralLabel(loc, sb);
		}
	}

	@Override
	public void addAuralContent(Localization loc, ScreenReaderBuilder sb) {
		// implement this if geo has a content like items.
	}

	@Override
	public void addAuralStatus(Localization loc, ScreenReaderBuilder sb) {
		// Implement this if geo has status. (pressed, checked, etc)
	}

	@Override
	public String getAuralText(ScreenReaderBuilder sb) {
		Localization loc = kernel.getLocalization();
		addAuralName(loc, sb);
		sb.appendSpace();
		addAuralStatus(loc, sb);
		sb.appendSpace();
		addAuralContent(loc, sb);
		sb.appendSpace();
		addAuralOperations(loc, sb);
		sb.endSentence();
		return sb.toString();
	}

	@Override
	public void addAuralOperations(Localization loc, ScreenReaderBuilder sb) {
		App app = kernel.getApplication();
		if (getScript(EventType.CLICK) != null
				&& getScript(EventType.CLICK).getText().length() > 0 && !sb.isMobile()) {
			sb.append(loc.getMenuDefault("PressSpaceToRunScript", "Press space to run script"));
		}

		if (isEuclidianShowable()) {
			if (app.getGuiManager() != null && app.getGuiManager().hasAlgebraView()
					&& !isGeoInputBox()) {
				if (isEuclidianVisible()) {
					sb.append(loc.getMenuDefault("PressSlashToHide", "Press / to hide object"));
				} else {
					sb.append(loc.getMenuDefault("PressSlashToShow", "Press / to show object"));
				}
			}
			sb.appendSpace();
		}
		if (app.showToolBar() && !isGeoInputBox()) {
			if (isGeoButton() || isPenStroke()) {
				sb.append(loc.getMenuDefault("PressEnterToOpenSettings",
						"Press enter to open settings"));
			} else if (!isGeoButton()) {
				sb.append(loc.getMenuDefault("PressEnterToEdit", "Press enter to edit"));
			}
		}
	}

	@Override
	public String getAuralTextForSpace() {
		return null;
	}

	@Override
	public String getAuralTextForMove() {
		return null;
	}

	@Override
	public String getAuralExpression() {
		return toValueString(StringTemplate.screenReader);
	}

	/**
	 * 
	 * @param ev
	 *            view
	 * @return if geo lies completely in view (could be false for a 3D object)
	 */
	public boolean isWhollyIn2DView(EuclidianView ev) {
		return true;
	}

	/**
	 * Convenience method to get label manager of current construction
	 * 
	 * @return label manager
	 */
	public LabelManager getLabelManager() {
		return cons.getLabelManager();
	}

	/**
	 * @return complex / polar/ cartesian for points an vectors, implicit /
	 *         explitic / parametric / ... for equations
	 */
	public final int getToStringMode() {
		return toStringMode;
	}

	/**
	 * Set this to true, if the element should have a preview popup.
	 * That is only values should be shown.
	 *
	 * @param hasPreviewPopup true to show values in EV
	 */
	public final void setHasPreviewPopup(boolean hasPreviewPopup) {
		this.hasPreviewPopup = hasPreviewPopup;
	}

	/**
	 * Check if this geo has preview popup.
	 *
	 * @return true if this geo has a preview popup
	 */
	public final boolean hasPreviewPopup() {
		return hasPreviewPopup;
	}

	/**
	 * Equation type unrelated to type for display which is set in the geo.
	 * 
	 * @return whether to prefer implicit equation label
	 */
	public EquationType getEquationTypeForLabeling() {
		if (definition == null
				|| !(definition.unwrap() instanceof EquationValue)) {
			return EquationType.NONE;
		}
		Equation eqn = (Equation) definition.unwrap();
		if (eqn.isExplicitIn("y") || eqn.isExplicitIn("z")) {
			return EquationType.EXPLICIT;
		}
		return EquationType.IMPLICIT;
	}

	/**
	 * @param i
	 *            1 for EV1, 2 for EV2, 3 for 3D
	 * @return whether the locus is visible
	 */
	public boolean isVisibleInEV(int i) {
		switch (i) {
		case 1:
			return isVisibleInView(App.VIEW_EUCLIDIAN)
					&& kernel.getApplication().showView(App.VIEW_EUCLIDIAN);

		case 2:
			return isVisibleInView(App.VIEW_EUCLIDIAN2)
					&& kernel.getApplication().hasEuclidianView2(1);

		case 3:
			return isVisibleInView3D()
					&& kernel.getApplication().isEuclidianView3Dinited();
		}
		return false;
	}

	@Override
	public GeoElementND unwrapSymbolic() {
		return this;
	}
}
