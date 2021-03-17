package org.geogebra.common.kernel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.SetOrientation;
import org.geogebra.common.gui.dialog.options.OptionsCAS;
import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.io.MyXMLHandler;
import org.geogebra.common.kernel.algos.AlgoCasBase;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.algos.AlgoDependentFunctionNVar;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoIf;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.algos.AlgoPointVector;
import org.geogebra.common.kernel.algos.AlgoVectorPoint;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.algos.DependentAlgo;
import org.geogebra.common.kernel.arithmetic.ArithmeticFactory;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyDoubleDegreesMinutesSeconds;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.cas.AlgoUsingTempCASalgo;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAxis;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSymbolicI;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.optimization.ExtremumFinder;
import org.geogebra.common.kernel.optimization.ExtremumFinderI;
import org.geogebra.common.kernel.parser.GParser;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.SpecialPointsListener;
import org.geogebra.common.main.SpecialPointsManager;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.LRUMap;
import org.geogebra.common.util.MaxSizeHashMap;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Provides methods for computation
 * 
 * @author Markus
 */
public class Kernel implements SpecialPointsListener, ConstructionStepper {

	/**
	 * Maximal number of spreadsheet columns if these are increased above 32000,
	 * you need to change traceRow to an int[]
	 */
	final public static int MAX_SPREADSHEET_COLUMNS_DESKTOP = 9999;
	/** Maximal number of spreadsheet rows */
	final public static int MAX_SPREADSHEET_ROWS_DESKTOP = 9999;

	/**
	 * Maximal number of spreadsheet columns if these are increased above 32000,
	 * you need to change traceRow to an int[]
	 */
	final public static int MAX_SPREADSHEET_COLUMNS_WEB = 200;
	/** Maximal number of spreadsheet rows */
	final public static int MAX_SPREADSHEET_ROWS_WEB = 350;

	/** string for +- */
	final public static String STRING_PLUS_MINUS = "\u00B1 ";
	/** string for -+ */
	final public static String STRING_MINUS_PLUS = "\u2213 ";

	// G.Sturr 2009-10-18
	// algebra style
	/**
	 * @deprecated AlgebraStyle.Value should be used instead.
	 * Algebra view style: value */
	@Deprecated
	final public static int ALGEBRA_STYLE_VALUE = 0;

	/**
	 * @deprecated AlgebraStyle.Description should be used instead.
	 * Algebra view style: description */
	@Deprecated
	final public static int ALGEBRA_STYLE_DESCRIPTION = 1;

	/**
	 * @deprecated AlgebraStyle.Definition should be used instead.
	 * Algebra view style: definition */
	@Deprecated
	final public static int ALGEBRA_STYLE_DEFINITION = 2;

	/**
	 * @deprecated AlgebraStyle.DefinitionAndValue should be used instead.
	 * Algebra view style: definition and value */
	@Deprecated
	final public static int ALGEBRA_STYLE_DEFINITION_AND_VALUE = 3;
	// critical for exam mode
	// must use getter
	private int algebraStyleSpreadsheet = Kernel.ALGEBRA_STYLE_VALUE;

	// end G.Sturr
	private MacroManager macroManager;

	/**
	 * Specifies whether possible line breaks are to be marked in the String
	 * representation of {@link ExpressionNode ExpressionNodes}.
	 */
	protected boolean insertLineBreaks = false;

	// angle unit: degree, radians
	private int angleUnit = Kernel.ANGLE_DEGREE;

	private boolean viewReiniting = false;
	private boolean undoActive = false;

	// Views may register to be informed about
	// changes to the Kernel
	// (add, remove, update)
	/** List of attached views */
	protected ArrayList<View> views = new ArrayList<>();
	private boolean addingPolygon = false;
	private GeoElement newPolygon;
	private final ArrayList<GeoElement> deleteList;
	/** Construction */
	protected Construction cons;
	/** Algebra processor */
	protected AlgebraProcessor algProcessor;
	/** Evaluator for ExpressionNode */
	protected ExpressionNodeEvaluator expressionNodeEvaluator;

	/**
	 * CAS variable handling
	 * 
	 * so ggb variable "a" is sent to Giac as "ggbtmpvara" which is then
	 * converted back to "a" when the result comes back from Giac
	 * 
	 * must start with a letter before 'x' so that variable ordering works in
	 * Giac
	 * 
	 */
	public static final String TMP_VARIABLE_PREFIX = "ggbtmpvar";
	/**
	 * used in the Prover
	 */
	public static final String TMP_VARIABLE_PREFIX2 = TMP_VARIABLE_PREFIX + "2";

	// Continuity on or off, default: false since V3.0
	private boolean continuous = false;
	/** Whether to move point on path together with path */
	public PathRegionHandling usePathAndRegionParameters = PathRegionHandling.ON;
	private GeoGebraCasInterface ggbCAS;
	/** Angle unit: radians */
	final public static int ANGLE_RADIANT = 1;
	/** Angle unit: degrees */
	final public static int ANGLE_DEGREE = 2;
	/** Coord system: cartesian */
	final public static int COORD_CARTESIAN = 3;
	/** Coord system: polar */
	final public static int COORD_POLAR = 4;
	/** Coord system: complex numbers */
	final public static int COORD_COMPLEX = 5;
	/** Coord system: 3D cartesian */
	final public static int COORD_CARTESIAN_3D = 6;
	/** Coord system: 3D spherical polar */
	final public static int COORD_SPHERICAL = 7;
	/** Angle type: degrees/minutes/Seconds */
	final public static int ANGLE_DEGREES_MINUTES_SECONDS = 8;

	/** 2*Pi */
	final public static double PI_2 = 2.0 * Math.PI;
	/** Pi/2 */
	final public static double PI_HALF = Math.PI / 2.0;
	/** sqrt(1/2) */
	final public static double SQRT_2_HALF = Math.sqrt(2.0) / 2.0;
	/** One degree (Pi/180) */
	final public static double PI_180 = Math.PI / 180;
	/** Radian to degree ratio (180/Pi) */
	final public static double CONST_180_PI = 180 / Math.PI;

	/** maximum precision of double numbers */
	public final static double MAX_DOUBLE_PRECISION = 1E-15;
	/** reciprocal of maximum precision of double numbers */
	public final static double INV_MAX_DOUBLE_PRECISION = 1E15;

	/** maximum CAS results cached */
	final public static int GEOGEBRA_CAS_CACHE_SIZE = 500;
	private MySpecialDouble eulerConstant;

	/** print precision */
	public static final int STANDARD_PRINT_DECIMALS = 10;
	/** print precision for Graphing app */
	public static final int STANDARD_PRINT_DECIMALS_GRAPHING = 13;
	/** print precision for Geometry app */
	public static final int STANDARD_PRINT_DECIMALS_GEOMETRY = 1;

	/** print precision for AppConfigDefault */
	public static final int STANDARD_PRINT_DECIMALS_SHORT = 2;
	// private double PRINT_PRECISION = 1E-2;
	private NumberFormatAdapter nf;
	private final ScientificFormatAdapter sf;
	/** whether to use significant figures for output */
	public boolean useSignificantFigures = false;

	// style of point/vector coordinates
	/** A = (3, 2) and B = (3; 90^o) */
	public static final int COORD_STYLE_DEFAULT = 0;
	/** A(3|2) and B(3; 90^o) */
	public static final int COORD_STYLE_AUSTRIAN = 1;
	/** A: (3, 2) and B: (3; 90^o) */
	public static final int COORD_STYLE_FRENCH = 2;
	private int coordStyle = 0;

	/** standard precision */
	public final static double STANDARD_PRECISION = 1E-8;
	/** square root of standard precision */
	public final static double STANDARD_PRECISION_SQRT = 1E-4;
	/** square of standard precision */
	public final static double STANDARD_PRECISION_SQUARE = 1E-16;
	/** cube of standard precision */
	public final static double STANDARD_PRECISION_CUBE = 1E-24;

	/** minimum precision */
	public final static double MIN_PRECISION = 1E-5;
	/** 1 / (min precision) */
	public final static double INV_MIN_PRECISION = 1E5;

	/** maximum reasonable precision */
	public final static double MAX_PRECISION = 1E-12;

	/** maximum axes can zoom to */
	private final static double AXES_PRECISION = 1E-14;

	// private String stringTemplate.getPi(); // for pi

	// before May 23, 2005 the function acos(), asin() and atan()
	// had an angle as result. Now the result is a number.
	// this flag is used to distinguish the different behaviour
	// depending on the the age of saved construction files
	/**
	 * if true, cyclometric functions return GeoAngle, if false, they return
	 * GeoNumeric
	 **/

	private boolean useInternalCommandNames = false;

	private boolean notifyConstructionProtocolViewAboutAddRemoveActive = true;

	private boolean allowVisibilitySideEffects = true;

	// this flag was introduced for Copy & Paste
	private boolean saveScriptsToXML = true;

	private boolean elementDefaultAllowed = false;

	// silentMode is used to create helper objects without any side effects
	// i.e. in silentMode no labels are created and no objects are added to
	// views
	private boolean silentMode = false;

	private boolean wantAnimationStarted = false;

	// setResolveUnkownVarsAsDummyGeos
	private SymbolicMode symbolicMode = SymbolicMode.NONE;

	private boolean updateEVAgain = false; // used for DrawEquationWeb and
											// DrawText in GGW
	private boolean forceUpdatingBoundingBox = false; // used for
														// DrawEquationWeb and
														// DrawText in GGW

	private final StringBuilder sbBuildExplicitLineEquation = new StringBuilder(
			50);
	/** Application */
	@Weak
	protected App app;

	private EquationSolver eqnSolver;
	private SystemOfEquationsSolver sysEqSolv;
	private ExtremumFinderI extrFinder;
	/** Parser */
	protected Parser parser;

	/** 3D manager */
	private Manager3DInterface manager3D;
	private AlgoDispatcher algoDispatcher;
	private final ArithmeticFactory arithmeticFactory;
	private final GeoFactory geoFactory;

	private GeoVec2D imaginaryUnit;

	private final Object concurrentModificationLock = new Object();

	private boolean showAnimationButton = true;
	private boolean loadingMode;

	private final StringBuilder sbFormatAngle = new StringBuilder(40);
	private MyDoubleDegreesMinutesSeconds.Value valueDegreesMinutesSeconds;

	private boolean arcusFunctionCreatesAngle;
	private ArrayList<AlgoElement> renameListenerAlgos;
	private boolean spreadsheetBatchRunning;
	private StringBuilder stateForModeStarting;
	private final GeoElementSpreadsheet ges = new GeoElementSpreadsheet();
	private final ScheduledPreviewFromInputBar scheduledPreviewFromInputBar;
	private boolean userStopsLoading = false;
	private AnimationManager animationManager;

	private StringBuilder sbFormat;
	private StringBuilder formatSB;
	private final StringBuilder sbBuildImplicitEquation = new StringBuilder(80);

	private final StringBuilder sbBuildLHS = new StringBuilder(80);
	private final StringBuilder sbBuildExplicitConicEquation = new StringBuilder(
			80);
	private StringBuilder sbFormatSF;
	/** default global JavaScript */
	final public static String defaultLibraryJavaScript = "function ggbOnInit() {}";

	private String libraryJavaScript = defaultLibraryJavaScript;

	private boolean isSaving;
	private MaxSizeHashMap<String, String> ggbCasCache;
	/** min real world x for all views */
	protected double[] xmin = new double[1];
	/** max real world x for all views */
	protected double[] xmax = new double[1];
	/** min real world y for all views */
	protected double[] ymin = new double[1];
	/** max real world y for all views */
	protected double[] ymax = new double[1];
	/** x-scale for all views */
	protected double[] xscale = new double[1];
	/** y-scale for all views */
	protected double[] yscale = new double[1];
	private boolean graphicsView2showing = false;
	private boolean notifyRepaint = true;
	private EuclidianView lastAttachedEV = null;
	private boolean notifyViewsActive = true;

	// MOB-1304 cache axes numbers
	private final HashMap<StringTemplate, LRUMap<Double, String>> formatterMaps = new HashMap<>();

	/**
	 * @param app
	 *            Application
	 * @param factory
	 *            element factory
	 */
	public Kernel(App app, GeoFactory factory) {
		this(factory);
		this.app = app;

		newConstruction();
		getExpressionNodeEvaluator();

		setManager3D(newManager3D(this));
	}

	/**
	 * Creates kernel and initializes number formats and CAS prefix
	 * 
	 * @param factory
	 *            factory for new elements
	 */
	protected Kernel(GeoFactory factory) {
		nf = FormatFactory.getPrototype().getNumberFormat(2);
		sf = FormatFactory.getPrototype().getScientificFormat(5, 16, false);
		deleteList = new ArrayList<>();
		geoFactory = factory;
		arithmeticFactory = new ArithmeticFactory();
		scheduledPreviewFromInputBar = new ScheduledPreviewFromInputBar(this);
	}

	/**
	 * Returns this kernel's algebra processor that handles all input and
	 * commands.
	 *
	 * @return Algebra processor
	 */
	public AlgebraProcessor getAlgebraProcessor() {
		if (algProcessor == null) {
			algProcessor = newAlgebraProcessor(this);
		}
		return algProcessor;
	}

	/**
	 * @param kernel
	 *            kernel
	 * @return a new algebra processor (used for 3D)
	 */
	public AlgebraProcessor newAlgebraProcessor(Kernel kernel) {
		return new AlgebraProcessor(kernel, app.newCommandDispatcher(kernel));
	}

	/**
	 * Centers the first element of a GeoElementND array, if that element is a GeoText
	 * @param geoElements The array of which the first element will be centered
	 */
	public void checkGeoTexts(GeoElementND[] geoElements) {
		if (geoElements == null) {
			// no GeoElements were created
			return;
		}
		// create texts in the middle of the visible view
		// we must check that size of geos is not 0 (ZoomIn,
		// ZoomOut, ...)
		if (geoElements.length > 0 && geoElements[0] != null
				&& geoElements[0].isGeoText()) {
			InputHelper.centerText((GeoText) geoElements[0],
					getApplication().getActiveEuclidianView());

		}
	}

	/**
	 * @param kernel
	 *            kernel
	 * @return a new 3D manager
	 */
	protected Manager3DInterface newManager3D(Kernel kernel) {
		return null;
	}

	/**
	 * Synchronized means that no two Threads can simultaneously enter any
	 * blocks locked by the same lock object, but they can only wait for the
	 * active Thread to exit from these blocks... as there is only one lock
	 * object and these methods probably do not call other synchronized code
	 * blocks, it probably does not cause any problem
	 * 
	 * @return Object unique to the Application instance
	 */
	public Object getConcurrentModificationLock() {
		return concurrentModificationLock;
	}

	/**
	 * sets the 3D manager
	 * 
	 * @param manager
	 *            3d interface manager
	 */
	public void setManager3D(Manager3DInterface manager) {
		this.manager3D = manager;
	}

	/**
	 * 
	 * @return default plane (null for 2D implementation, xOy plane for 3D)
	 */
	public GeoPlaneND getDefaultPlane() {
		return null;
	}

	/**
	 * @return the 3D manager of this
	 */
	public Manager3DInterface getManager3D() {
		return manager3D;
	}

	/**
	 * creates the construction cons
	 */
	protected void newConstruction() {
		cons = new Construction(this);
	}

	/**
	 * creates a new MyXMLHandler (used for 3D)
	 * 
	 * @param cons1
	 *            construction used in MyXMLHandler constructor
	 * @return a new MyXMLHandler
	 */
	public MyXMLHandler newMyXMLHandler(Construction cons1) {
		return newMyXMLHandler(this, cons1);
	}

	/**
	 * creates a new MyXMLHandler (used for 3D)
	 * 
	 * @param kernel
	 *            kernel
	 * @param cons1
	 *            construction
	 * @return a new MyXMLHandler
	 */
	public MyXMLHandler newMyXMLHandler(Kernel kernel, Construction cons1) {
		return new MyXMLHandler(kernel, cons1);
	}

	/**
	 * @return app
	 */
	final public App getApplication() {
		return app;
	}

	/**
	 * @return (polynomial) equation solver
	 */
	final public EquationSolver getEquationSolver() {
		if (eqnSolver == null) {
			eqnSolver = new EquationSolver();
		}
		return eqnSolver;
	}

	/**
	 * @param eSolver
	 *            single equation solver
	 * @return system solver
	 */
	final public SystemOfEquationsSolver getSystemOfEquationsSolver(
			EquationSolverInterface eSolver) {
		if (sysEqSolv == null) {
			sysEqSolv = new SystemOfEquationsSolver(eSolver);
		}
		return sysEqSolv;
	}

	/**
	 * @return extremum finding utility
	 */
	final public ExtremumFinderI getExtremumFinder() {
		if (extrFinder == null) {

			extrFinder = new ExtremumFinder();
		}
		return extrFinder;
	}

	/**
	 * @return parser for GGB and CAS expressions
	 */
	final public Parser getParser() {
		if (parser == null) {
			parser = new GParser(this, cons);
		}
		return parser;
	}

	/**
	 * creates the Evaluator for ExpressionNode
	 * 
	 * @param kernel
	 *            kernel to be used for new expression
	 * 
	 * @return the Evaluator for ExpressionNode
	 */
	public ExpressionNodeEvaluator newExpressionNodeEvaluator(Kernel kernel) {
		return new ExpressionNodeEvaluator(app.getLocalization(), kernel,
				app.getConfig().createOperationArgumentFilter());
	}

	/**
	 * return the Evaluator for ExpressionNode
	 * 
	 * @return the Evaluator for ExpressionNode
	 */
	public ExpressionNodeEvaluator getExpressionNodeEvaluator() {
		if (expressionNodeEvaluator == null) {
			expressionNodeEvaluator = newExpressionNodeEvaluator(this);
		}
		return expressionNodeEvaluator;
	}

	/**
	 * 
	 * @param precision
	 *            max absolute value of difference
	 * @return a double comparator which says doubles are equal if their diff is
	 *         less than precision
	 */
	final static public Comparator<Double> doubleComparator(double precision) {

		final double eps = precision;

		Comparator<Double> ret = new Comparator<Double>() {

			@Override
			public int compare(Double d1, Double d2) {
				if (Math.abs(d1 - d2) < eps) {
					return 0;
				} else if (d1 < d2) {
					return -1;
				} else {
					return 1;
				}
			}
		};

		return ret;
	}

	/**
	 * If the data-param-showAnimationButton parameter for applet is false, be
	 * sure not to show the animation button. In this case the value of
	 * showAnimationButton is false, otherwise true.
	 * 
	 * @param showAB
	 *            animation button parameter
	 */
	public void setShowAnimationButton(boolean showAB) {
		showAnimationButton = showAB;
	}

	/**
	 * @return whether animation is running
	 */
	final public boolean isAnimationRunning() {
		return animationManager != null && animationManager.isRunning();
	}

	/**
	 * @return whether animation is paused
	 */
	final public boolean isAnimationPaused() {
		return animationManager != null && animationManager.isPaused();
	}

	/**
	 * @return current frame rate
	 */
	final public double getFrameRate() {
		return animationManager.getFrameRate();
	}

	/**
	 * @return whether animation button is needed
	 */
	final public boolean needToShowAnimationButton() {
		if (!showAnimationButton) {
			return false;
		}
		return animationManager != null
				&& animationManager.needToShowAnimationButton();
	}

	/*
	 * ******************************************* Methods for MyXMLHandler
	 * *******************************************
	 */
	/**
	 * @param geo
	 *            element
	 * @param attrs
	 *            coordinates from XML
	 * @return whether this worked without exception
	 */
	public boolean handleCoords(GeoElement geo,
			LinkedHashMap<String, String> attrs) {

		if (!(geo instanceof GeoVec3D)) {
			Log.debug("wrong element type for <coords>: " + geo.getClass());
			return false;
		}
		GeoVec3D v = (GeoVec3D) geo;

		try {
			double x = StringUtil.parseDouble(attrs.get("x"));
			double y = StringUtil.parseDouble(attrs.get("y"));
			double z = StringUtil.parseDouble(attrs.get("z"));
			v.hasUpdatePrevilege = true;
			v.setCoords(x, y, z);
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * ******************************************* Construction specific methods
	 * *******************************************
	 */

	/**
	 * Returns the ConstructionElement for the given GeoElement. If geo is
	 * independent geo itself is returned. If geo is dependent it's parent
	 * algorithm is returned.
	 * 
	 * @param geo
	 *            geo
	 * @return geo or parent algo
	 */
	public static ConstructionElement getConstructionElement(GeoElement geo) {
		AlgoElement algo = geo.getParentAlgorithm();
		if (algo == null) {
			return geo;
		}
		return algo;
	}

	/**
	 * Returns the Construction object of this kernel.
	 * 
	 * @return construction
	 */
	public Construction getConstruction() {
		return cons;
	}

	/**
	 * Returns the ConstructionElement for the given construction index.
	 * 
	 * @param index
	 *            construction index
	 * @return corresponding element
	 */
	public ConstructionElement getConstructionElement(int index) {
		return cons.getConstructionElement(index);
	}

	/**
	 * 
	 * @return first geo if exists
	 */
	public GeoElement getFirstGeo() {
		return cons.getFirstGeo();
	}

	@Override
	public void setConstructionStep(int step) {
		if (cons.getStep() != step) {
			cons.setStep(step);
			getApplication().setUnsaved();
		}
	}

	/**
	 * @return construction step being done now
	 */
	public int getConstructionStep() {
		return cons.getStep();
	}

	/**
	 * @return last construction step
	 */
	public int getLastConstructionStep() {
		return cons.steps() - 1;
	}

	@Override
	public void firstStep() {
		int step = 0;

		if (cons.showOnlyBreakpoints()) {
			setConstructionStep(getNextBreakpoint(step));
		} else {
			setConstructionStep(step);
		}
	}

	@Override
	public void lastStep() {
		int step = getLastConstructionStep();

		if (cons.showOnlyBreakpoints()) {
			setConstructionStep(getPreviousBreakpoint(step));
		} else {
			setConstructionStep(step);
		}
	}

	@Override
	public void nextStep() {
		int step = cons.getStep() + 1;

		if (cons.showOnlyBreakpoints()) {
			setConstructionStep(getNextBreakpoint(step));
		} else {
			ConstructionElement next = cons.getConstructionElement(step);

			if (next instanceof GeoElement
					&& ((GeoElement) next).getCorrespondingCasCell() != null) {
				step++;
			}

			setConstructionStep(step);
		}
	}

	/**
	 * 
	 * @param breakpoint
	 *            breakpoint number
	 * @return actual construction step for breakpoint
	 */
	public int getBreakpointStep(int breakpoint) {

		if (breakpoint <= 0) {
			return 0;
		}
		int count = 0;
		int step = 0;
		int lastStep = getLastConstructionStep();

		while (step <= lastStep) {
			if (cons.getConstructionElement(step).isConsProtocolBreakpoint()) {
				count++;
				if (count == breakpoint) {
					return step;
				}
			}

			step++;
		}

		return lastStep;

	}

	/**
	 * 
	 * @return number of breakpoints
	 */
	public int getBreakpointSteps() {

		int count = 0;
		int step = 0;
		int lastStep = getLastConstructionStep();

		while (step <= lastStep) {
			if (cons.getConstructionElement(step).isConsProtocolBreakpoint()) {
				count++;
			}

			step++;
		}

		return count;
	}

	private int getNextBreakpoint(int initStep) {
		int step = initStep;
		int lastStep = getLastConstructionStep();
		// go to next breakpoint
		while (step <= lastStep) {
			if (cons.getConstructionElement(step).isConsProtocolBreakpoint()) {
				return step;
			}

			step++;
		}

		return lastStep;
	}

	@Override
	public void previousStep() {
		int step = cons.getStep() - 1;

		cons.setStep(getClosestStep(step));
	}

	/**
	 * @param step
	 *            raw step number
	 * @return closest step number showable in construction protocol
	 */
	public int getClosestStep(int step) {
		if (cons.showOnlyBreakpoints()) {
			return getPreviousBreakpoint(step);
		}
		ConstructionElement prev = cons.getConstructionElement(step);
		/*
		 * if (prev instanceof GeoElement && ((GeoElement)
		 * prev).getCorrespondingCasCell() != null) { step--; }
		 */
		if (prev instanceof GeoCasCell
				&& ((GeoCasCell) prev).getTwinGeo() != null
				&& ((GeoCasCell) prev).getTwinGeo().isAlgebraVisible()) {
			return step - 1;
		}
		return step;
	}

	private int getPreviousBreakpoint(int initStep) {
		int step = initStep;
		// go to previous breakpoint
		while (step >= 0) {
			if (cons.getConstructionElement(step).isConsProtocolBreakpoint()) {
				return step;
			}
			step--;
		}
		return -1;
	}

	/**
	 * Move object at position from to position to in current construction.
	 * 
	 * @param from
	 *            original position
	 * @param to
	 *            target position
	 * @return true if succesful
	 */
	public boolean moveInConstructionList(int from, int to) {
		return cons.moveInConstructionList(from, to);
	}

	/**
	 * @param flag
	 *            switches on or off putting scripts into XML
	 */
	public void setSaveScriptsToXML(boolean flag) {
		saveScriptsToXML = flag;
	}

	/**
	 * 
	 * @return whether scripts should be put into XML or not
	 */
	public boolean getSaveScriptsToXML() {
		return saveScriptsToXML;
	}

	/**
	 * @param flag
	 *            whether we can load default elements
	 */
	public void setElementDefaultAllowed(boolean flag) {
		elementDefaultAllowed = flag;
	}

	/**
	 * @return whether we can load default elements
	 */
	public boolean getElementDefaultAllowed() {
		return elementDefaultAllowed;
	}

	/**
	 * States whether the continuity heuristic is active.
	 * 
	 * @return whether continuous mode is on
	 */
	final public boolean isContinuous() {
		return continuous;
	}

	/**
	 * Turns the continuity heuristic on or off. Note: the macro kernel always
	 * turns continuity off.
	 * 
	 * @param continuous
	 *            true if continuous
	 */
	public void setContinuous(boolean continuous) {
		this.continuous = continuous;
	}

	/**
	 * States whether path/region parameters are used. Also test if point is
	 * defined (if not, use parameters).
	 * 
	 * @param point
	 *            point
	 * @return true if given point should use path/region parameter
	 */
	final public boolean usePathAndRegionParameters(GeoPointND point) {
		return usePathAndRegionParameters == PathRegionHandling.ON
				|| (!point.isDefined());
	}

	/**
	 * Turns the using of path/region parameters on or off.
	 * 
	 * @param flag
	 *            new flag for using path/region parameters
	 */
	public void setUsePathAndRegionParameters(PathRegionHandling flag) {
		this.usePathAndRegionParameters = flag;
	}

	// loading mode: true when a ggb file is being loaded. Devised for backward
	// compatibility.
	/**
	 * 
	 * @param b
	 *            true to indicate that file is being loaded
	 */
	public void setLoadingMode(boolean b) {
		loadingMode = b;
	}

	/**
	 * @return whether file is being loaded
	 */
	public boolean getLoadingMode() {
		return loadingMode;
	}

	final private static char sign(double x) {
		if (x > 0) {
			return '+';
		}
		return '-';
	}

	/**
	 * @param flag
	 *            whether add/remove should be sent to construction protocol
	 */
	public void setNotifyConstructionProtocolViewAboutAddRemoveActive(
			boolean flag) {
		notifyConstructionProtocolViewAboutAddRemoveActive = flag;
	}

	/**
	 * @return whether add/remove should be sent to construction protocol
	 */
	public boolean isNotifyConstructionProtocolViewAboutAddRemoveActive() {
		return notifyConstructionProtocolViewAboutAddRemoveActive;
	}

	// see http://code.google.com/p/google-web-toolkit/issues/detail?id=4097
	/**
	 * @param numbers
	 *            coefficients
	 * @param vars
	 *            variables
	 * @param cancelDown
	 *            whether to cancel GCDs
	 * @param needsZ
	 *            whether term in z is needed
	 * @param tpl
	 *            string template
	 * @param implicit
	 *            whether last var should be in LHS
	 * @return serialized equation
	 */
	public final StringBuilder buildImplicitEquation(double[] numbers,
			String[] vars, boolean cancelDown,
			boolean needsZ, StringTemplate tpl, boolean implicit) {

		sbBuildImplicitEquation.setLength(0);
		double lastCoeff = buildImplicitVarPart(sbBuildImplicitEquation, numbers,
				vars, cancelDown, needsZ,
				tpl);

		if (!implicit && !isZeroFigure(lastCoeff, tpl)) {
			sbBuildImplicitEquation.append(' ');
			formatSigned(lastCoeff, sbBuildImplicitEquation, tpl);
		}
		sbBuildImplicitEquation.append(tpl.getEqualsWithSpace());
		if (implicit) {
			// temp is set by buildImplicitVarPart
			sbBuildImplicitEquation.append(format(-lastCoeff, tpl));
		} else {
			sbBuildImplicitEquation.append(format(0.0, tpl));
		}
		return sbBuildImplicitEquation;
	}

	/**
	 * @param x
	 *            number
	 * @param sb
	 *            output buffer
	 * @param tpl
	 *            formated number with leading + or -. Skips 1 and -1.
	 */
	final public void formatSignedCoefficient(double x, StringBuilder sb,
			StringTemplate tpl) {
		if (x == -1.0) {
			sb.append("- ");
			return;
		}
		if (x == 1.0) {
			sb.append("+ ");
			return;
		}

		formatSigned(x, sb, tpl);
	}

	/**
	 * @param x
	 *            number
	 * @param sb
	 *            output buffer
	 * @param tpl
	 *            formated number with leading + or -
	 */
	final public void formatSigned(double x, StringBuilder sb,
			StringTemplate tpl) {
		boolean screenReader = tpl.hasType(StringType.SCREEN_READER);
		if (x >= 0.0d) {
			sb.append(screenReader ? " plus " : "+ ");
			sb.append(format(x, tpl));
			return;
		}
		sb.append(screenReader ? " minus " : "- ");
		sb.append(format(-x, tpl));
	}

	/**
	 * @param x
	 *            number
	 * @param sb
	 *            output buffer
	 * @param tpl
	 *            formated number with leading +- or -+. Skips 1 and -1.
	 */
	final public void formatSignedCoefficientPlusMinus(double x,
			StringBuilder sb, StringTemplate tpl) {
		if (x == -1.0) {
			sb.append(STRING_MINUS_PLUS);
			return;
		}
		if (x == 1.0) {
			sb.append(STRING_PLUS_MINUS);
			return;
		}

		formatSignedPlusMinus(x, sb, tpl);
	}

	/**
	 * @param x
	 *            number
	 * @param sb
	 *            output buffer
	 * @param tpl
	 *            formated number with leading + or -
	 */
	final public void formatSignedPlusMinus(double x, StringBuilder sb,
			StringTemplate tpl) {

		if (x >= 0.0d) {
			sb.append(STRING_PLUS_MINUS);
			sb.append(format(x, tpl));
			return;
		}
		sb.append(STRING_MINUS_PLUS);
		sb.append(format(-x, tpl));
	}

	final private String formatPiERaw(double x, NumberFormatAdapter numF,
			StringTemplate tpl) {

		LRUMap<Double, String> formatterMap = formatterMaps.get(tpl);
		if (formatterMap == null) {
			formatterMap = new LRUMap<>();
			formatterMaps.put(tpl, formatterMap);
		} else {
			String ret = formatterMap.get(x);
			if (ret != null) {
				return ret;
			}
		}

		// PI
		if (x == Math.PI && tpl.allowPiHack()) {
			formatterMap.put(x, tpl.getPi());
			return tpl.getPi();
		}

		// MULTIPLES OF PI/2
		// i.e. x = a * pi/2
		double a = (2 * x) / Math.PI;
		int aint = (int) Math.round(a);
		if (sbFormat == null) {
			sbFormat = new StringBuilder();
		}
		sbFormat.setLength(0);
		if (DoubleUtil.isEqual(a, aint, AXES_PRECISION)) {
			switch (aint) {
			case 0:
				formatterMap.put(x, "0");
				return "0";

			case 1: // pi/2
				sbFormat.append(tpl.getPi());
				sbFormat.append("/2");
				formatterMap.put(x, sbFormat.toString());
				return sbFormat.toString();

			case -1: // -pi/2
				sbFormat.append('-');
				sbFormat.append(tpl.getPi());
				sbFormat.append("/2");
				formatterMap.put(x, sbFormat.toString());
				return sbFormat.toString();

			case 2: // 2pi/2 = pi
				formatterMap.put(x, tpl.getPi());
				return tpl.getPi();

			case -2: // -2pi/2 = -pi
				sbFormat.append('-');
				sbFormat.append(tpl.getPi());
				formatterMap.put(x, sbFormat.toString());
				return sbFormat.toString();

			default:
				// even
				long half = aint / 2;
				if (aint == (2 * half)) {
					// half * pi
					sbFormat.append(half);
					if (!tpl.hasType(StringType.GEOGEBRA)) {
						sbFormat.append("*");
					}
					sbFormat.append(tpl.getPi());
					formatterMap.put(x, sbFormat.toString());
					return sbFormat.toString();
				}
				// odd
				// aint * pi/2
				sbFormat.append(aint);
				if (!tpl.hasType(StringType.GEOGEBRA)) {
					sbFormat.append("*");
				}
				sbFormat.append(tpl.getPi());
				sbFormat.append("/2");
				formatterMap.put(x, sbFormat.toString());
				return sbFormat.toString();
			}
		}
		// STANDARD CASE
		// use numberformat to get number string
		// checkDecimalFraction() added to avoid 2.19999999999999 when set to
		// 15dp
		String str = numF.format(DoubleUtil.checkDecimalFraction(x));
		sbFormat.append(str);
		// if number is in scientific notation and ends with "E0", remove this
		if (str.endsWith("E0")) {
			sbFormat.setLength(sbFormat.length() - 2);
		}
		formatterMap.put(x, sbFormat.toString());
		return sbFormat.toString();
	}

	/**
	 * Converts the double into a fraction based on the current kernel rounding
	 * precision.
	 * 
	 * @param x
	 *            input number to be rationalized
	 * @return numerator and denominator
	 */
	public long[] doubleToRational(double x) {
		long[] ret = new long[2];
		ret[1] = precision();
		ret[0] = Math.round(x * precision());

		long gcd = gcd(ret[0], ret[1]);
		ret[0] /= gcd;
		ret[1] /= gcd;
		return ret;
	}

	/**
	 * Formats the value of x using the currently set NumberFormat or
	 * ScientificFormat.
	 * 
	 * @param number
	 *            number
	 * @param tpl
	 *            string template
	 * @return formated number as string
	 */
	final public String formatRaw(double number, StringTemplate tpl) {
		double x = number;
		// format integers without significant figures
		boolean isLongInteger = false;
		long rounded = Math.round(x);
		if ((x == rounded) && (x >= Long.MIN_VALUE) && (x < Long.MAX_VALUE)) {
			isLongInteger = true;
		}
		StringType casPrintForm = tpl.getStringType();
		switch (casPrintForm) {

		// to avoid 1/3 = 0
		case PSTRICKS:
		case PGF:
			return MyDouble.toString(x);

		// number formatting for XML string output
		case GEOGEBRA_XML:
			if (isLongInteger) {
				return Long.toString(rounded);
			} else if (Double.isNaN(number) && tpl.hasQuestionMarkForNaN()) {
				return "?";
			}
			// #5149
			return MyDouble.toString(x);

		// number formatting for CAS
		case GIAC:
			if (Double.isNaN(x)) {
				return "?";
			} else if (Double.isInfinite(x)) {
				return (x < 0) ? "-inf" : "inf";
			} else if (isLongInteger) {
				return Long.toString(rounded);
			} else if (DoubleUtil.isZero(x, Kernel.MAX_PRECISION)) {
				// #4802
				return "0";
			} else {
				double abs = Math.abs(x);
				// number small enough that Double.toString() won't create E
				// notation
				if ((abs >= 10E-3) && (abs < 10E7)) {
					String ret = MyDouble.toString(x);

					// convert 0.125 to 1/8 so Giac treats it as an exact number
					// Note: exact(0.3333333333333) gives 1/3
					if (ret.indexOf('.') > -1) {
						return StringUtil.wrapInExact(x, ret, tpl, this);
					}

					return ret;
				}
				// convert scientific notation 1.0E-20 to 1*10^(-20)
				String scientificStr = MyDouble.toString(x);

				return tpl.convertScientificNotation(scientificStr);
			}

			// number formatting for screen output
		default:
			if (Double.isNaN(x)) {
				return "?";
			} else if (Double.isInfinite(x)) {
				return (x > 0) ? "\u221e" : "-\u221e"; // infinity
			} else if (x == Math.PI && tpl.allowPiHack()) {
				return tpl.getPi();
			}

			boolean useSF = tpl.useScientific(useSignificantFigures);

			// ROUNDING hack
			// NumberFormat and SignificantFigures use ROUND_HALF_EVEN as
			// default which is not changeable, so we need to hack this
			// to get ROUND_HALF_UP like in schools: increase abs(x) slightly
			// x = x * ROUND_HALF_UP_FACTOR;
			// We don't do this for large numbers as
			if (!isLongInteger && tpl.getPrecision(nf) > 1E-6) {
				double abs = Math.abs(x);
				// increase abs(x) slightly to round up
				x = x * tpl.getRoundHalfUpFactor(abs, nf, sf, useSF);
			}

			if (useSF) {
				return formatSF(x, tpl);
			}
			return formatNF(x, tpl);
		}
	}

	/**
	 * Uses current NumberFormat nf to format a number.
	 */
	final private String formatNF(double x, StringTemplate tpl) {
		// "<=" catches -0.0000000000000005
		// should be rounded to -0.000000000000001 (15 d.p.)
		// but nf.format(x) returns "-0"
		double printPrecision = tpl.getPrecision(nf);
		if (((-printPrecision / 2) < x) && (x < (printPrecision / 2))) {
			// avoid output of "-0" for eg -0.0004
			return "0";
		}
		// standard case

		// nf = FormatFactory.prototype.getNumberFormat(2);
		NumberFormatAdapter nfa = tpl.getNF(nf);
		return nfa.format(x);
	}

	/**
	 * Formats the value of x using the currently set NumberFormat or
	 * ScientificFormat.
	 * 
	 * converts to localised digits if appropriate
	 * 
	 * @param x
	 *            number
	 * @param tpl
	 *            string template
	 * @return formated string
	 */

	final public String format(double x, StringTemplate tpl) {
		// Log.printStacktrace(x + "");
		String ret = formatRaw(x, tpl);

		if (app.getLocalization().getZero() != '0') {
			ret = internationalizeDigits(ret, tpl);
		}

		return ret;
	}

	/**
	 * swaps the digits in num to the current locale's
	 * 
	 * @param num
	 *            english number
	 * @param tpl
	 *            template
	 * @return localized number
	 */
	public String internationalizeDigits(String num, StringTemplate tpl) {

		if (!tpl.internationalizeDigits()
				|| !getLocalization().isUsingLocalizedDigits()) {
			return num;
		}

		if (formatSB == null) {
			formatSB = new StringBuilder(17);
		} else {
			formatSB.setLength(0);
		}

		boolean negative = num.charAt(0) == '-';

		int start = 0;

		// make sure minus sign works in Arabic
		boolean RTL = getLocalization().isRightToLeftDigits(tpl);

		if (RTL) {
			formatSB.append(Unicode.RIGHT_TO_LEFT_MARK);
			if (negative) {
				formatSB.append(Unicode.RIGHT_TO_LEFT_UNARY_MINUS_SIGN);
				start = 1;
			}
		}

		for (int i = start; i < num.length(); i++) {

			char c = RTL ? num.charAt(num.length() - (negative ? 0 : 1) - i)
					: num.charAt(i);
			if (c == '.') {
				c = getLocalization().getDecimalPoint();
			} else if ((c >= '0') && (c <= '9')) {

				// convert to eg Arabic Numeral
				c += app.getLocalization().getZero() - '0';
			}

			formatSB.append(c);
		}

		if (RTL) {
			formatSB.append(Unicode.RIGHT_TO_LEFT_MARK);
		}

		return formatSB.toString();
	}

	/**
	 * calls formatPiERaw() and converts to localised digits if appropriate
	 * 
	 * @param x
	 *            number
	 * @param numF
	 *            number format
	 * @param tpl
	 *            string template
	 * @return formated number with e's and pi's replaced by suitable symbols
	 */
	final public String formatPiE(double x, NumberFormatAdapter numF,
			StringTemplate tpl) {
		if (app.getLocalization().getZero() != '0') {

			String num = formatPiERaw(x, numF, tpl);

			return internationalizeDigits(num, tpl);
		}
		return formatPiERaw(x, numF, tpl);
	}

	/**
	 * copy array a to array b
	 * 
	 * @param a
	 *            input array
	 * @param b
	 *            output array
	 */
	final static void copy(double[] a, double[] b) {
		for (int i = 0; i < a.length; i++) {
			b[i] = a[i];
		}
	}

	/**
	 * Computes c[] = a[] / b
	 * 
	 * @param a
	 *            array of dividends
	 * @param b
	 *            divisor
	 * @param c
	 *            array for results
	 */
	final static void divide(double[] a, double b, double[] c) {
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] / b;
		}
	}

	/**
	 * greatest common divisor
	 * 
	 * @param m
	 *            firs number
	 * @param n
	 *            second number
	 * @return GCD of given numbers
	 */
	final public static long gcd(long m, long n) {
		// Return the GCD of positive integers m and n.
		if ((m == 0) || (n == 0)) {
			return Math.max(Math.abs(m), Math.abs(n));
		}

		long p = m, q = n;
		while ((p % q) != 0) {
			long r = p % q;
			p = q;
			q = r;
		}
		return q;
	}

	/**
	 * Compute greatest common divisor of given doubles. Note: all double values
	 * are cast to long.
	 * 
	 * @param numbers
	 *            array of numbers
	 * @return GCD of given numbers
	 */
	final public static double gcd(double[] numbers) {
		long gcd = (long) numbers[0];
		for (int i = 0; i < numbers.length; i++) {
			gcd = gcd((long) numbers[i], gcd);
		}
		return Math.abs(gcd);
	}

	/**
	 * Round a double to the given scale e.g. roundToScale(5.32, 1) = 5.0,
	 * roundToScale(5.32, 0.5) = 5.5, roundToScale(5.32, 0.25) = 5.25,
	 * roundToScale(5.32, 0.1) = 5.3
	 * 
	 * @param x
	 *            number
	 * @param scale
	 *            rounding step
	 * @return rounded number
	 */
	final public static double roundToScale(double x, double scale) {
		if (scale == 1.0) {
			return Math.round(x);
		}
		return Math.round(x / scale) * scale;
	}

	/**
	 * @param numbers
	 *            coefficients
	 * @param vars
	 *            variables
	 * @param cancelDown
	 *            whether we want to cancel GCD
	 * @param needsZ
	 *            whether explicit Z is necessary
	 * @param tpl
	 *            template
	 * @return LHS string
	 */
	final public StringBuilder buildLHS(double[] numbers, String[] vars, boolean cancelDown,
			boolean needsZ,	StringTemplate tpl) {

		return buildLHS(numbers, vars, cancelDown, needsZ,
				false, tpl);
	}

	/**
	 * Builds lhs of lhs = 0
	 * 
	 * @param numbers
	 *            coefficients
	 * @param vars
	 *            variable names
	 * @param cancelDown
	 *            true to allow canceling 2x+4y -&gt; x+2y
	 * @param needsZ
	 *            whether "+0z" is needed when z is not present
	 * @param setConstantIfNoLeading
	 *            whether constant or 0 should be added if all var coeff are 0
	 * @param tpl
	 *            string template
	 * @return string representing LHS
	 */
	final public StringBuilder buildLHS(double[] numbers, String[] vars,
			boolean cancelDown, boolean needsZ,
			boolean setConstantIfNoLeading, StringTemplate tpl) {
		sbBuildLHS.setLength(0);
		double lastCoeff = buildImplicitVarPart(sbBuildLHS, numbers, vars,
				cancelDown, needsZ, setConstantIfNoLeading,
				tpl);

		// add constant coeff
		appendConstant(sbBuildLHS, lastCoeff, tpl);
		return sbBuildLHS;
	}

	/**
	 * append +/- constant
	 * 
	 * @param sb
	 *            string builder to append to
	 * @param coeff
	 *            constant
	 * @param tpl
	 *            string template
	 */
	public final void appendConstant(StringBuilder sb, double coeff,
			StringTemplate tpl) {
		if ((Math.abs(coeff) >= tpl.getPrecision(nf))
				|| useSignificantFigures) {
			sb.append(' ');
			sb.append(sign(coeff));
			sb.append(' ');
			sb.append(format(Math.abs(coeff), tpl));
		}
	}

	// lhs of implicit equation without constant coeff
	final private double buildImplicitVarPart(
			StringBuilder sbBuildImplicitVarPart, double[] numbers,
			String[] vars, boolean CANCEL_DOWN,
			boolean needsZ, StringTemplate tpl) {
		return buildImplicitVarPart(sbBuildImplicitVarPart, numbers, vars,
				CANCEL_DOWN, needsZ, false, tpl);
	}

	// lhs of implicit equation without constant coeff
	final private double buildImplicitVarPart(
			StringBuilder sbBuildImplicitVarPart, double[] numbers,
			String[] vars, boolean CANCEL_DOWN,
			boolean needsZ, boolean setConstantIfNoLeading,
			StringTemplate tpl) {
		int leadingNonZero = -1;
		sbBuildImplicitVarPart.setLength(0);

		for (int i = 0; i < vars.length; i++) {
			if (!DoubleUtil.isZero(numbers[i])) {
				leadingNonZero = i;
				break;
			}
		}

		if (CANCEL_DOWN) {
			// check if integers and divide through gcd
			boolean allIntegers = true;
			for (int i = 0; i < numbers.length; i++) {
				allIntegers = allIntegers && DoubleUtil.isInteger(numbers[i]);
			}
			if (allIntegers) {
				// divide by greates common divisor
				divide(numbers, gcd(numbers), numbers);
			}
		}

		// no left hand side
		if (leadingNonZero == -1) {
			if (setConstantIfNoLeading) {
				double coeff = numbers[vars.length];
				if ((Math.abs(coeff) >= tpl.getPrecision(nf))
						|| useSignificantFigures) {
					sbBuildImplicitVarPart.append(format(coeff, tpl));
				} else {
					sbBuildImplicitVarPart.append("0");
				}
				return 0;
			}
			sbBuildImplicitVarPart.append("0");
			return 0;
		}

		// BUILD EQUATION STRING
		// valid left hand side
		// leading coefficient
		String strCoeff = formatCoeff(numbers[leadingNonZero], tpl);
		sbBuildImplicitVarPart.append(strCoeff);
		sbBuildImplicitVarPart.append(vars[leadingNonZero]);

		// other coefficients on lhs
		double abs;
		for (int i = leadingNonZero + 1; i < vars.length; i++) {
			abs = Math.abs(numbers[i]);
			if ((abs >= tpl.getPrecision(nf)) || useSignificantFigures
					|| (needsZ && i == 2)) {
				sbBuildImplicitVarPart.append(' ');
				formatSignedCoefficient(numbers[i], sbBuildImplicitVarPart, tpl);
				sbBuildImplicitVarPart.append(vars[i]);
			}
		}
		return numbers[vars.length];
	}

	/**
	 * 
	 * @param x
	 *            value
	 * @param tpl
	 *            strin template
	 * @return true if x is built as "0"
	 */
	private boolean isZeroFigure(double x, StringTemplate tpl) {
		return !useSignificantFigures && (Math.abs(x) <= tpl.getPrecision(nf));
	}

	/**
	 * form: y^2 = f(x) (coeff of y = 0)
	 * 
	 * @param numbers
	 *            coefficients
	 * @param vars
	 *            variables
	 * @param pos
	 *            position of y^2 coefficient
	 * @param tpl
	 *            string template
	 * @return explicit equation of conic
	 */
	public final StringBuilder buildExplicitConicEquation(double[] numbers,
			String[] vars, int pos, StringTemplate tpl) {
		// y^2-coeff is 0
		double d, dabs, q = numbers[pos];
		// coeff of y^2 is 0 or coeff of y is not 0
		if (DoubleUtil.isZero(q)) {
			return buildImplicitEquation(numbers, vars, true,
					false, tpl, true);
		}

		int i, leadingNonZero = numbers.length;
		for (i = 0; i < numbers.length; i++) {
			if ((i != pos) && // except y^2 coefficient
					((Math.abs(numbers[i]) >= tpl.getPrecision(nf))
							|| useSignificantFigures)) {
				leadingNonZero = i;
				break;
			}
		}

		// BUILD EQUATION STRING
		sbBuildExplicitConicEquation.setLength(0);
		sbBuildExplicitConicEquation.append(vars[pos]);
		sbBuildExplicitConicEquation.append(" = ");

		if (leadingNonZero == numbers.length) {
			sbBuildExplicitConicEquation.append("0");
			return sbBuildExplicitConicEquation;
		} else if (leadingNonZero == (numbers.length - 1)) {
			// only constant coeff
			d = -numbers[leadingNonZero] / q;
			sbBuildExplicitConicEquation.append(format(d, tpl));
			return sbBuildExplicitConicEquation;
		} else {
			// leading coeff
			d = -numbers[leadingNonZero] / q;
			sbBuildExplicitConicEquation.append(formatCoeff(d, tpl));
			sbBuildExplicitConicEquation.append(vars[leadingNonZero]);

			// other coeffs
			for (i = leadingNonZero + 1; i < vars.length; i++) {
				if (i != pos) {
					d = -numbers[i] / q;
					dabs = Math.abs(d);
					if ((dabs >= tpl.getPrecision(nf))
							|| useSignificantFigures) {
						sbBuildExplicitConicEquation.append(' ');
						sbBuildExplicitConicEquation.append(sign(d));
						sbBuildExplicitConicEquation.append(' ');
						sbBuildExplicitConicEquation
								.append(formatCoeff(dabs, tpl));
						sbBuildExplicitConicEquation.append(vars[i]);
					}
				}
			}

			// constant coeff
			d = -numbers[i] / q;
			dabs = Math.abs(d);
			if ((dabs >= tpl.getPrecision(nf)) || useSignificantFigures) {
				sbBuildExplicitConicEquation.append(' ');
				sbBuildExplicitConicEquation.append(sign(d));
				sbBuildExplicitConicEquation.append(' ');
				sbBuildExplicitConicEquation.append(format(dabs, tpl));
			}

			// Application.debug(sbBuildExplicitConicEquation.toString());

			return sbBuildExplicitConicEquation;
		}
	}

	/**
	 * y = a (x + h)^2 + k
	 * 
	 * @param numbers
	 *            coefficients
	 * @param vars
	 *            strings to substitute for xx,yy,xy,x,y (only indices 3 and 4
	 *            are used)
	 * @param tpl
	 *            template
	 * @return vertex equation of the parabola
	 */
	public final StringBuilder buildVertexformEquation(double[] numbers,
			String[] vars, StringTemplate tpl) {
		double a = -1 * numbers[0] / numbers[4];
		double h = numbers[3] / numbers[0] / 2;
		double k = (numbers[3] * numbers[3]) / (4 * numbers[4] * numbers[0])
				- (numbers[5] / numbers[4]);

		StringBuilder sbBuildVertexformEquation = new StringBuilder(80);
		sbBuildVertexformEquation.append(vars[4]);
		sbBuildVertexformEquation.append(" = ");
		sbBuildVertexformEquation.append(formatCoeff(a, tpl));
		if (h == 0) {
			sbBuildVertexformEquation.append(vars[3]);
			sbBuildVertexformEquation.append(tpl.squared());
		} else {
			sbBuildVertexformEquation.append("(");
			sbBuildVertexformEquation.append(vars[3]);
			sbBuildVertexformEquation.append(" ");
			sbBuildVertexformEquation.append(sign(h));
			sbBuildVertexformEquation.append(' ');
			sbBuildVertexformEquation.append(format(Math.abs(h), tpl));
			sbBuildVertexformEquation.append(")");
			sbBuildVertexformEquation.append(tpl.squared());
		}
		if (k != 0) {
			sbBuildVertexformEquation.append(" ");
			sbBuildVertexformEquation.append(sign(k));
			sbBuildVertexformEquation.append(format(Math.abs(k), tpl));
		}
		return sbBuildVertexformEquation;
	}

	/**
	 * 4p(y-k) = (x-h)^2
	 * 
	 * @param numbers
	 *            coefficients
	 * @param vars
	 *            variables
	 * @param tpl
	 *            output template
	 * @return string builder
	 */
	public final StringBuilder buildConicformEquation(double[] numbers,
			String[] vars, StringTemplate tpl) {
		StringBuilder sbBuildConicformEquation = new StringBuilder(80);
		double h, p4, k, a, b, c, d;
		String var1, var2;

		if (numbers[0] == 0) { // directrix parallel with y axis
			a = numbers[2];
			b = numbers[4];
			c = numbers[3];
			d = numbers[5];
			var1 = vars[4];
			var2 = vars[3];
		} else { // directrix parallel with x axis
			a = numbers[0];
			b = numbers[3];
			c = numbers[4];
			d = numbers[5];
			var1 = vars[3];
			var2 = vars[4];
		}

		h = -b / a / 2;
		p4 = -c / a;
		k = b * b / (4 * a * c) - d / c;

		sbBuildConicformEquation.append(formatCoeff(p4, tpl) + "(" + var2 + " "
				+ sign(-k) + " " + format(Math.abs(k), tpl) + ") = " + "("
				+ var1 + " " + sign(-h) + " " + format(Math.abs(h), tpl) + ")"
				+ tpl.squared());
		return sbBuildConicformEquation;
	}

	/**
	 * Uses current ScientificFormat sf to format a number. Makes sure ".123" is
	 * returned as "0.123".
	 */
	final private String formatSF(double x, StringTemplate tpl) {
		if (sbFormatSF == null) {
			sbFormatSF = new StringBuilder();
		} else {
			sbFormatSF.setLength(0);
		}
		ScientificFormatAdapter sfa = tpl.getSF(sf);
		// get scientific format
		StringUtil.appendFormat(sbFormatSF, x, sfa);
		return sbFormatSF.toString();
	}

	/**
	 * append "two coeffs" expression
	 * 
	 * @param plusMinusX
	 *            says if we want "+-" before x coeffs
	 * @param x
	 *            first coeff
	 * @param y
	 *            second coeff
	 * @param s1
	 *            first string
	 * @param s2
	 *            second string
	 * @param tpl
	 *            template
	 * @param sbBuildValueString
	 *            string builder
	 */
	public final void appendTwoCoeffs(boolean plusMinusX, double x, double y,
			String s1, String s2, StringTemplate tpl,
			StringBuilder sbBuildValueString) {

		if (isZeroFigure(x, tpl)) {
			if (isZeroFigure(y, tpl)) {
				sbBuildValueString.append("0");
			} else {
				String coeff = formatCoeff(y, tpl);
				sbBuildValueString.append(coeff);
				if (coeff.length() > 0) {
					sbBuildValueString.append(" "); // no need if no coeff
				}
				sbBuildValueString.append(s2);
			}
		} else {
			if (plusMinusX) {
				formatSignedCoefficientPlusMinus(x, sbBuildValueString, tpl);
				sbBuildValueString.append(" ");
			} else {
				String coeff = formatCoeff(x, tpl);
				sbBuildValueString.append(coeff);
				if (coeff.length() > 0) {
					sbBuildValueString.append(" "); // no need if no coeff
				}
			}
			sbBuildValueString.append(s1);

			if (!isZeroFigure(y, tpl)) {
				sbBuildValueString.append(" ");
				formatSignedCoefficient(y, sbBuildValueString, tpl);
				sbBuildValueString.append(" ");
				sbBuildValueString.append(s2);
			}
		}

	}

	/**
	 * Appends one of "0", "x", "y", "x + y"
	 * 
	 * @param x
	 *            first coefficient
	 * @param y
	 *            second coefficient
	 * @param tpl
	 *            template
	 * @param sbBuildValueString
	 *            string builder
	 */
	public final void appendTwoCoeffs(double x, double y, StringTemplate tpl,
			StringBuilder sbBuildValueString) {

		if (isZeroFigure(x, tpl)) {
			if (isZeroFigure(y, tpl)) {
				sbBuildValueString.append("0");
			} else {
				formatSignedPlusMinus(y, sbBuildValueString, tpl);
			}
		} else {
			sbBuildValueString.append(format(x, tpl));

			if (!isZeroFigure(y, tpl)) {
				sbBuildValueString.append(" ");
				formatSignedPlusMinus(y, sbBuildValueString, tpl);
			}
		}
	}

	/** doesn't show 1 or -1 */
	private String formatCoeff(double x, StringTemplate tpl) {
		if (Math.abs(x) == 1.0) {
			if (x > 0.0) {
				return "";
			}
			return "-";
		}
		String numberStr = format(x, tpl);
		switch (tpl.getStringType()) {
		case GIAC:
			return numberStr + "*";

		default:
			// standard case
			return numberStr;
		}
	}

	/**
	 * @param numbers
	 *            coefficients
	 * @param vars
	 *            expressions
	 * @param tpl
	 *            string template
	 * @param explicit
	 *            whether to keep all variables on RHS
	 * @return explicit equation
	 */
	public final StringBuilder buildExplicitEquation(double[] numbers,
			String[] vars, StringTemplate tpl, boolean explicit) {

		double d, dabs, q = numbers[1];
		sbBuildExplicitLineEquation.setLength(0);

		// BUILD EQUATION STRING
		// special case
		// y-coeff is 0: if explicit equation: form x = constant
		// if general eq: form x + constant = 0
		if (DoubleUtil.isZero(q)) {
			sbBuildExplicitLineEquation.append(vars[0]);
			if (!explicit) {
				double constant = numbers[2] / numbers[0];
				String sign;
				double abs;
				if (constant < 0.0) {
					sign = " - ";
					abs = -constant;
				} else {
					sign = " + ";
					abs = constant;
				}

				String absStr = format(abs, tpl);
				if (!"0".equals(absStr)) {
					sbBuildExplicitLineEquation
							.append(sign + " " + absStr);
				}
			}

			sbBuildExplicitLineEquation.append(tpl.getEqualsWithSpace());

			if (explicit) {
				sbBuildExplicitLineEquation
						.append(format(-numbers[2] / numbers[0], tpl));
			} else {
				sbBuildExplicitLineEquation.append(format(0.0, tpl));
			}

			return sbBuildExplicitLineEquation;
		}

		// standard case: y-coeff not 0
		sbBuildExplicitLineEquation.append(vars[1]);

		// general line equation, coeff of x is null
		if (!explicit) {
			sbBuildExplicitLineEquation.append(' ');
			if (useSignificantFigures) {
				sbBuildExplicitLineEquation
						.append("+ " + format(0.0, tpl) + vars[0]);
			}
			d = numbers[2] / q;
			dabs = Math.abs(d);
			sbBuildExplicitLineEquation.append(sign(d));
			sbBuildExplicitLineEquation.append(' ');
			sbBuildExplicitLineEquation.append(format(dabs, tpl));
			sbBuildExplicitLineEquation.append(tpl.getEqualsWithSpace());
			sbBuildExplicitLineEquation.append(formatCoeff(0.0, tpl));
			return sbBuildExplicitLineEquation;
		}

		sbBuildExplicitLineEquation.append(tpl.getEqualsWithSpace());

		// x coeff
		d = -numbers[0] / q;
		dabs = Math.abs(d);
		if ((dabs >= tpl.getPrecision(nf)) || useSignificantFigures) {
			sbBuildExplicitLineEquation.append(formatCoeff(d, tpl));
			if (tpl.hasType(StringType.LATEX)) {
				sbBuildExplicitLineEquation.append(' ');
			}
			sbBuildExplicitLineEquation.append(vars[0]);

			// constant
			d = -numbers[2] / q;
			dabs = Math.abs(d);
			if ((dabs >= tpl.getPrecision(nf)) || useSignificantFigures) {
				sbBuildExplicitLineEquation.append(' ');
				sbBuildExplicitLineEquation.append(sign(d));
				sbBuildExplicitLineEquation.append(' ');
				sbBuildExplicitLineEquation.append(format(dabs, tpl));
			}
		} else {
			// only constant
			sbBuildExplicitLineEquation.append(format(-numbers[2] / q, tpl));
		}
		return sbBuildExplicitLineEquation;
	}

	// //////////////////////////////////////////////
	// FORMAT FOR NUMBERS
	// //////////////////////////////////////////////

	/**
	 * Returns formated angle (in degrees if necessary)
	 * 
	 * @param phi
	 *            angle in radians
	 * @param tpl
	 *            string template
	 * @param unbounded
	 *            whether to allow angles out of [0,2pi]
	 * @return formatted angle
	 */
	final public StringBuilder formatAngle(double phi, StringTemplate tpl,
			boolean unbounded) {
		// STANDARD_PRECISION * 10 as we need a little leeway as we've converted
		// from radians
		return formatAngle(phi, 10, tpl, unbounded, false);
	}

	/**
	 * Returns formated angle (in degrees if necessary)
	 *
	 * @param phi
	 *            angle in radians
	 * @param tpl
	 *            string template
	 * @param unbounded
	 *            whether to allow angles out of [0,2pi]\
	 * @param forceDegrees
	 *            whether to keep format in degrees]
	 * @return formatted angle
	 */
	final public StringBuilder formatAngle(double phi, StringTemplate tpl,
			boolean unbounded, boolean forceDegrees) {
		// STANDARD_PRECISION * 10 as we need a little leeway as we've converted
		// from radians
		return formatAngle(phi, 10, tpl, unbounded, forceDegrees);
	}

	/**
	 * @param alpha
	 *            angle
	 * @param precision
	 *            precision for decimal fraction checking
	 * @param tpl
	 *            string template
	 * @param unbounded
	 *            whether to allow angles out of [0,2pi]
	 * @return formatted angle
	 */
	final public StringBuilder formatAngle(double alpha, double precision,
			StringTemplate tpl, boolean unbounded, boolean forceDegrees) {
		double phi = alpha;
		sbFormatAngle.setLength(0);
		switch (tpl.getStringType()) {

		default:
			// STRING_TYPE_GEOGEBRA_XML
			// STRING_TYPE_GEOGEBRA

			if (Double.isNaN(phi)) {
				sbFormatAngle.append("?");
				return sbFormatAngle;
			}

			if (forceDegrees || degreesMode()) {
				boolean rtl = getLocalization().isRightToLeftDigits(tpl);
				if (rtl) {
					if (tpl.hasCASType()) {
						sbFormatAngle.append("pi/180*");
					} else {
						sbFormatAngle.append(Unicode.DEGREE_CHAR);
					}
				}

				phi = Math.toDegrees(phi);

				// make sure 360.0000000002 -> 360
				phi = DoubleUtil.checkInteger(phi);

				if (!unbounded) {
					if (phi < 0) {
						phi += 360;
					} else if (phi > 360) {
						phi = phi % 360;
					}
				}
				// STANDARD_PRECISION * 10 as we need a little leeway as we've
				// converted from radians
				sbFormatAngle.append(
						format(DoubleUtil.checkDecimalFraction(phi, precision), tpl));

				if (tpl.hasType(StringType.GEOGEBRA_XML)) {
					sbFormatAngle.append("*");
				}

				if (!rtl) {
					if (tpl.hasCASType()) {
						sbFormatAngle.append("*pi/180");
					} else {
						sbFormatAngle.append(Unicode.DEGREE_CHAR);
					}
				}

				return sbFormatAngle;
			}

			if (getAngleUnit() == Kernel.ANGLE_DEGREES_MINUTES_SECONDS) {
				if (valueDegreesMinutesSeconds == null) {
					valueDegreesMinutesSeconds = new MyDoubleDegreesMinutesSeconds.Value();
				}
				valueDegreesMinutesSeconds.set(phi, Kernel.MAX_PRECISION,
						unbounded);
				valueDegreesMinutesSeconds.format(sbFormatAngle, tpl, this);
				return sbFormatAngle;
			}

			// RADIANS
			sbFormatAngle.append(format(phi, tpl));

			switch (tpl.getStringType()) {

			default:
				sbFormatAngle.append(" rad");
				break;

			case LATEX:
				sbFormatAngle.append(" \\; rad");
				break;

			case GEOGEBRA_XML:
			case GIAC:
				// do nothing
				break;
			}

			return sbFormatAngle;
		}
	}

	/** Resets global JavaSrcript to default value */
	public void resetLibraryJavaScript() {
		setLibraryJavaScript(defaultLibraryJavaScript);
	}

	/**
	 * @param str
	 *            global javascript
	 */
	public void setLibraryJavaScript(String str) {
		Log.debug(str);
		libraryJavaScript = str;

		if (app.getScriptManager() != null) {
			app.getScriptManager().setGlobalScript();
		}
	}

	/**
	 * @return global JavaScript
	 */
	public String getLibraryJavaScript() {
		return libraryJavaScript;
	}

	/**
	 * return all points of the current construction
	 * 
	 * @return points in construction
	 */
	public TreeSet<GeoElement> getPointSet() {
		return getConstruction().getGeoSetLabelOrder(GeoClass.POINT);
	}

	/*------------------------------------------------------
	 * SAVING
	 *******************************************************/

	/**
	 * @return whether save is being executed
	 */
	public synchronized boolean isSaving() {
		return isSaving;
	}

	/**
	 * @param saving
	 *            whether save is being executed
	 */
	public synchronized void setSaving(boolean saving) {
		isSaving = saving;
	}

	/**
	 * @param returnAngle
	 *            whether angle should be returned from asin /acos/..
	 *            (compatibility setting for v &lt; 5.0.290)
	 */
	public void setInverseTrigReturnsAngle(boolean returnAngle) {
		arcusFunctionCreatesAngle = returnAngle;
	}

	/**
	 * @return whether angle should be returned from asin / acos /...
	 */
	public boolean getInverseTrigReturnsAngle() {
		return arcusFunctionCreatesAngle && loadingMode;
	}

	/**
	 * @param unit
	 *            Kernel.ANGLE_DEGREE or Kernel.ANGLE_RADIANT or
	 *            Kernel.ANGLE_DEGREES_MINUTES_SECONDS
	 */
	final public void setAngleUnit(int unit) {
		angleUnit = unit;
	}

	/**
	 * @return Kernel.ANGLE_DEGREE or Kernel.ANGLE_RADIANT or Kernel.ANGLE_DEGREES_MINUTES_SECONDS
	 */
	final public int getAngleUnit() {
		return angleUnit;
	}

	/**
	 *
	 * @return true if angle unit wants degree symbol automatically added
	 */
	final public boolean getAngleUnitUsesDegrees() {
		return angleUnitUsesDegrees(angleUnit);
	}

	/**
	 * @param unit
	 *            angle unit
	 *
	 * @return true if angle unit wants degree symbol automatically added
	 */
	final public static boolean angleUnitUsesDegrees(int unit) {
		return unit == Kernel.ANGLE_DEGREE
				|| unit == Kernel.ANGLE_DEGREES_MINUTES_SECONDS;
	}

	/**
	 * 
	 * @return true if in degrees mode
	 */
	final public boolean degreesMode() {
		return angleUnit == Kernel.ANGLE_DEGREE;
	}

	/**
	 * Returns whether the variable name "z" may be used. Note that the 3D
	 * kernel does not allow this as it uses "z" in plane equations like 3x + 2y
	 * + z = 5.
	 * 
	 * @return whether z may be used as a variable name
	 */
	public boolean isZvarAllowed() {
		return true;
	}

	/**
	 * @param str
	 *            string, possibly containing CAS prefix several times
	 * @return string without CAS prefixes
	 */
	final public static String removeCASVariablePrefix(final String str) {
		return removeCASVariablePrefix(str, "");
	}

	/**
	 * @param str
	 *            prefixed variable
	 * @param replace
	 *            replacement prefix
	 * @return String where CAS variable prefixes are removed again, e.g.
	 *         "ggbcasvar1a" is turned into "a" and
	 */
	final public static String removeCASVariablePrefix(final String str,
			final String replace) {
		// need a space when called from GeoGebraCAS.evaluateGeoGebraCAS()
		// so that eg Derivative[1/(-x+E2)] works (want 2 E2 not 2E2) #1595,
		// #1616

		// e.g. "ggbtmpvara" needs to be changed to "a"
		return str.replace(TMP_VARIABLE_PREFIX, replace);
	}

	/**
	 * Switch to significant figures and set precision.
	 * 
	 * @param figures
	 *            significant figures for format();
	 */
	final public void setPrintFigures(int figures) {
		if (figures >= 0) {
			useSignificantFigures = true;
			sf.setSigDigits(figures);
			sf.setMaxWidth(16); // for scientific notation
		}
	}

	/**
	 * Switch to fixed decimals and set precision.
	 * 
	 * @param decimals
	 *            print decimals for format()
	 */
	final public void setPrintDecimals(int decimals) {
		if (decimals >= 0) {
			useSignificantFigures = false;
			nf = FormatFactory.getPrototype().getNumberFormat(decimals);
		}
	}

	/**
	 * @return print decimals; defaults to 5
	 */
	final public int getPrintDecimals() {
		if (nf == null) {
			return 5;
		}
		return nf.getMaximumFractionDigits();
	}

	/**
	 * @return number of significant digits, or -1 if using decimal places
	 */
	final public int getPrintFigures() {
		if (!useSignificantFigures) {
			return -1;
		}
		return sf.getSigDigits();
	}

	/**
	 * Returns whether the parser should read internal command names and not
	 * translate them.
	 * 
	 * @return true if internal command names should be read
	 */
	public boolean isUsingInternalCommandNames() {
		return useInternalCommandNames;
	}

	/**
	 * Sets whether the parser should read internal command names and not
	 * translate them.
	 * 
	 * @param b
	 *            true if internal command names should be read
	 */
	public void setUseInternalCommandNames(boolean b) {
		useInternalCommandNames = b;
	}

	/**
	 * @return whether setDrawable() on numeric should make them visible as well
	 */
	public final boolean isAllowVisibilitySideEffects() {
		return allowVisibilitySideEffects;
	}

	/**
	 * @param allowVisibilitySideEffects
	 *            whether setDrawable() on numeric should make them visible as
	 *            well
	 */
	public final void setAllowVisibilitySideEffects(
			boolean allowVisibilitySideEffects) {
		this.allowVisibilitySideEffects = allowVisibilitySideEffects;
	}

	/**
	 * @return whether this is a macro kernel
	 */
	public boolean isMacroKernel() {
		return false;
	}

	/**
	 * @return whether silent mode is turned on.
	 * 
	 * @see #setSilentMode(boolean)
	 */
	public final boolean isSilentMode() {
		return silentMode;
	}

	/**
	 * @return Whether the GeoGebraCAS has been initialized before
	 */
	public synchronized boolean isGeoGebraCASready() {
		return ggbCAS != null;
	}

	/**
	 * Turns silent mode on (true) or off (false). In silent mode, commands can
	 * be used to create objects without any side effects, i.e. no labels are
	 * created, algorithms are not added to the construction list and the views
	 * are not notified about new objects.
	 *
	 * When calling this, make sure to store the suppressLabelCreation flag
	 * of the construction to be able to restore it later.
	 * 
	 * @param silentMode
	 *            silent mode
	 */
	public final void setSilentMode(boolean silentMode) {
		this.silentMode = silentMode;
		// no new labels, no adding to construction list
		getConstruction().setSuppressLabelCreation(silentMode);
	}

	/**
	 * Sets whether unknown variables should be resolved as GeoDummyVariable
	 * objects.
	 * 
	 * @param symbolicMode
	 *            whether to resolve vars as dummies
	 */
	public final void setSymbolicMode(SymbolicMode symbolicMode) {
		this.symbolicMode = symbolicMode;
	}

	/**
	 * @return whether unkown variables are resolved as GeoDummyVariable
	 *         objects.
	 * 
	 * @see #setSilentMode(boolean)
	 */
	public final SymbolicMode getSymbolicMode() {
		return symbolicMode;
	}

	/**
	 * @param casString
	 *            String to evaluate
	 * @param arbconst
	 *            arbitrary constant
	 * @return result string (null possible)
	 * @throws Throwable
	 *             on CAS error
	 */
	public String evaluateGeoGebraCAS(String casString,
			MyArbitraryConstant arbconst) throws Throwable {
		return evaluateGeoGebraCAS(casString, arbconst,
				StringTemplate.numericNoLocal);
	}

	/**
	 * Evaluates an expression in GeoGebraCAS syntax.
	 * 
	 * @param exp
	 *            input
	 * @param arbconst
	 *            arbitrary constant handler
	 * @param tpl
	 *            output template
	 * 
	 * @return result string (null possible)
	 * @throws CASException
	 *             on CAS error
	 */
	final public String evaluateGeoGebraCAS(String exp,
			MyArbitraryConstant arbconst, StringTemplate tpl)
			throws CASException {
		return evaluateGeoGebraCAS(exp, false, arbconst, tpl);
	}

	/**
	 * Evaluates an expression in GeoGebraCAS syntax where the cache or previous
	 * evaluations is used. Make sure to only use this method when exp only
	 * includes values and no (used) variable names.
	 * 
	 * @param exp
	 *            input
	 * @param arbconst
	 *            arbitrary constant handler
	 * 
	 * @return result string (null possible)
	 * @throws CASException
	 *             on CAS error
	 */
	final public String evaluateCachedGeoGebraCAS(String exp,
			MyArbitraryConstant arbconst) throws CASException {
		return evaluateGeoGebraCAS(exp, true, arbconst,
				StringTemplate.numericNoLocal);
	}

	/**
	 * Evaluates an expression in GeoGebraCAS syntax with.
	 * 
	 * @param useCaching
	 *            only set to true when exp only includes values and no (used)
	 *            variable names
	 * @return result string (null possible)
	 * @throws Throwable
	 *             when CAS failed
	 */
	private String evaluateGeoGebraCAS(String exp, boolean useCaching,
			MyArbitraryConstant arbconst, StringTemplate tpl)
			throws CASException {
		String result = null;
		if (useCaching && hasCasCache()) {
			result = getCasCache().get(exp);
			if (result != null) {
				// caching worked
				return result;
			}
		}

		// evaluate in GeoGebraCAS
		result = getGeoGebraCAS().evaluateGeoGebraCAS(exp, arbconst, tpl, this);

		if (useCaching) {
			getCasCache().put(exp, result);
		}
		return result;
	}

	/**
	 * @param exp
	 *            RAW Giac expression to evaluate
	 * @return result from Giac
	 * @throws Throwable
	 *             error
	 */
	public String evaluateRawGeoGebraCAS(String exp) throws Throwable {
		String result = null;
		if (hasCasCache()) {
			result = getCasCache().get(exp);
			if (result != null) {
				// Log.debug("result from cache " + result);
				// caching worked
				return result;
			}
		}

		// evaluate in GeoGebraCAS
		result = getGeoGebraCAS().evaluateRaw(exp);

		getCasCache().put(exp, result);

		return result;
	}

	/**
	 * @param exp
	 *            CAS expression
	 * @param result
	 *            result
	 */
	public void putToCasCache(String exp, String result) {
		getCasCache().put(exp, result);
	}

	/**
	 * @deprecated AlgebraSettings.setStyle should be used instead.
	 *
	 * G.Sturr 2009-10-18
	 * 
	 * @param style
	 *            Algebra style, see ALGEBRA_STYLE_*
	 */
	@Deprecated
	final public void setAlgebraStyle(int style) {
		getApplication().getSettings().getAlgebra().setStyle(style);
	}

	/**
	 * Change description style for spreadsheet.
	 * 
	 * @param style
	 *            description style
	 */
	final public void setAlgebraStyleSpreadsheet(int style) {
		if (style == Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE) {
			algebraStyleSpreadsheet = Kernel.ALGEBRA_STYLE_VALUE;
		} else {
			algebraStyleSpreadsheet = style;
		}
	}

	/**
	 * @deprecated AlgebraSettings.getStyle should be used instead.
	 *
	 * @return algebra style, one of ALGEBRA_STYLE_*
	 */
	@Deprecated
	final public int getAlgebraStyle() {
		return getApplication().getSettings().getAlgebra().getStyle();
	}

	/**
	 * @return algebra style for spreadsheet
	 */
	final public int getAlgebraStyleSpreadsheet() {
		return algebraStyleSpreadsheet;
	}

	/**
	 * @return Hash map for caching CAS results.
	 */
	public MaxSizeHashMap<String, String> getCasCache() {
		if (ggbCasCache == null) {
			ggbCasCache = new MaxSizeHashMap<>(
					GEOGEBRA_CAS_CACHE_SIZE);
		}
		return ggbCasCache;
	}

	/**
	 * @return Whether kernel is already using CAS caching.
	 */
	public boolean hasCasCache() {
		return ggbCasCache != null;
	}

	/**
	 * Tells this kernel about the bounds and the scales for x-Axis and y-Axis
	 * used in EudlidianView. The scale is the number of pixels per unit.
	 * (useful for some algorithms like findminimum). All
	 * 
	 * @param viewNo
	 *            view number
	 * @param xmin
	 *            left x-coord
	 * @param xmax
	 *            right x-coord
	 * @param ymin
	 *            bottom y-coord
	 * @param ymax
	 *            top y-coord
	 * @param xscale
	 *            x scale (pixels per unit)
	 * @param yscale
	 *            y scale (pixels per unit)
	 */
	final public void setEuclidianViewBounds(int viewNo, double xmin,
			double xmax, double ymin, double ymax, double xscale,
			double yscale) {
		int view = viewNo - 1;

		if (view < 0 || viewNo < 0) {
			return;
		}

		prolongGraphicsBoundArrays(viewNo);
		this.xmin[view] = xmin;
		this.xmax[view] = xmax;
		this.ymin[view] = ymin;
		this.ymax[view] = ymax;
		this.xscale[view] = xscale;
		this.yscale[view] = yscale;

		graphicsView2showing = getApplication().isShowingMultipleEVs();
		notifyEuclidianViewCE(EVProperty.ZOOM);
	}

	/**
	 * Extend arrays with EV bounds
	 * 
	 * @param length
	 *            new length of views array
	 */
	protected void prolongGraphicsBoundArrays(int length) {
		if (length > this.xmin.length) {
			this.xmin = prolong(this.xmin, length);
			this.xmax = prolong(this.xmin, length);

			this.ymin = prolong(this.ymin, length);
			this.ymax = prolong(this.ymax, length);

			this.xscale = prolong(this.xscale, length);
			this.yscale = prolong(this.yscale, length);
		}
	}

	private static double[] prolong(double[] xmin2, int viewNo) {
		double[] ret = new double[viewNo];
		System.arraycopy(xmin2, 0, ret, 0, xmin2.length);
		return ret;
	}

	/**
	 * 
	 * {@linkplain #getViewBoundsForGeo}
	 * 
	 * @see #getViewBoundsForGeo
	 * @param geo
	 *            geo
	 * @return minimal x-bound of all views displaying geo
	 */
	public double getViewsXMin(GeoElementND geo) {
		return getViewBoundsForGeo(geo)[0];
	}

	/**
	 * @see #getViewBoundsForGeo
	 * @param geo
	 *            geo
	 * @return maximal x-bound of all views displaying geo
	 */
	public double getViewsXMax(GeoElementND geo) {
		return getViewBoundsForGeo(geo)[1];
	}

	/**
	 * @see #getViewBoundsForGeo
	 * @param geo
	 *            geo
	 * @return minimal y-bound of all views displaying geo
	 */
	public double getViewsYMin(GeoElementND geo) {
		return getViewBoundsForGeo(geo)[2];
	}

	/**
	 * @see #getViewBoundsForGeo
	 * @param geo
	 *            geo
	 * @return maximal y-bound of all views displaying geo
	 */
	public double getViewsYMax(GeoElementND geo) {
		return getViewBoundsForGeo(geo)[3];
	}

	/**
	 * @param geo
	 *            construction element
	 * @return maximal x-scale of all views displaying geo
	 */
	public double getViewsXScale(GeoElementND geo) {
		return getViewBoundsForGeo(geo)[4];
	}

	/**
	 * @param geo
	 *            construction element
	 * @return maximal y-scale of all views displaying geo
	 */
	public double getViewsYScale(GeoElementND geo) {
		return getViewBoundsForGeo(geo)[5];
	}

	/**
	 * Notify all construction elements depending on certain property
	 * 
	 * @param prop
	 *            EV property that changed
	 */
	public void notifyEuclidianViewCE(EVProperty prop) {
		if (macroManager != null) {
			macroManager.notifyEuclidianViewCE(prop);
		}

		cons.notifyEuclidianViewCE(prop);
	}

	/**
	 * @param clearScripts
	 *            (true when called from File -&gt; New, false after loading a
	 *            file otherwise the GlobalJavascript is wrongly deleted)
	 */
	public synchronized void clearConstruction(boolean clearScripts) {

		if (clearScripts) {
			resetLibraryJavaScript();
		}
		if (this.ggbCAS != null) {
			this.ggbCAS.clearResult();
		}
		if (macroManager != null) {
			macroManager.setAllMacrosUnused();
		}

		// clear animations
		if (animationManager != null) {
			animationManager.stopAnimation();
			animationManager.clearAnimatedGeos();
		}
		if (clearScripts) {
			cons.getArbitraryConsTable().clear();
		}
		cons.clearConstruction();
		notifyClearView();
		notifyRepaint();
	}

	/**
	 * @return max value of xmax of shown views; EV1 considered to be shown
	 */
	public double getXmax() {
		if (graphicsView2showing) {
			return MyMath.max(xmax);
		}
		return xmax[0];
	}

	/**
	 * @return min value of xmin of shown views; EV1 considered to be shown
	 */
	public double getXmin() {
		if (graphicsView2showing) {
			return MyMath.min(xmin);
		}
		return xmin[0];
	}

	/**
	 * @return max xscale of shown views; EV1 considered to be shown
	 */
	public double getXscale() {
		if (graphicsView2showing) {
			// xscale = pixel per unit
			// higher xscale means more pixels per unit, i.e. higher precision
			return MyMath.max(xscale);
		}
		return xscale[0];
	}

	/**
	 * @return max value of ymax of shown views; EV1 considered to be shown
	 */
	public double getYmax() {
		if (graphicsView2showing) {
			return MyMath.max(ymax);
		}
		return ymax[0];
	}

	/**
	 * @return min value of ymin of shown views; EV1 considered to be shown
	 */
	public double getYmin() {
		if (graphicsView2showing) {
			return MyMath.min(ymin);
		}
		return ymin[0];
	}

	/**
	 * @return max yscale of shown views; EV1 considered to be shown
	 */
	public double getYscale() {
		if (graphicsView2showing) {
			// yscale = pixel per unit
			// higher xscale means more pixels per unit, i.e. higher precision
			return MyMath.max(yscale);
		}
		return yscale[0];
	}

	/**
	 * @param ev1
	 *            whether to consider EV1
	 * @param ev2
	 *            whether to consider EV2
	 * @return max of xmax values of used views
	 */
	public double getXmax(boolean ev1, boolean ev2) {
		if (ev2 && !ev1) {
			return xmax[1];
		} else if (ev1 && !ev2) {
			return xmax[0];
		}
		return getXmax();
	}

	/**
	 * @param i
	 *            view index
	 * @return max x-coord
	 */
	final public double getXmax(int i) {
		return xmax[i];
	}

	/**
	 * @param i
	 *            view index
	 * @return min x-coord
	 */
	final public double getXmin(int i) {
		return xmin[i];
	}

	/**
	 * @param i
	 *            view index
	 * @return max y-coord
	 */
	final public double getYmax(int i) {
		return ymax[i];
	}

	/**
	 * @param i
	 *            view index
	 * @return min y-coord
	 */
	final public double getYmin(int i) {
		return ymin[i];
	}

	/**
	 * @param i
	 *            view index
	 * @return y-scale
	 */
	final public double getYscale(int i) {
		return yscale[i];
	}

	/**
	 * @param i
	 *            view index
	 * @return x-scale
	 */
	final public double getXscale(int i) {
		return xscale[i];
	}

	/**
	 * @param i
	 *            used in 3D
	 * @return 3D view z-max for id = 2
	 */
	public double getZmax(int i) {
		return 0;
	}

	/**
	 * @param i
	 *            used in 3D
	 * @return 3D view z-min for id = 2
	 */
	public double getZmin(int i) {
		return 0;
	}

	/**
	 * 
	 * @param i
	 *            used in 3D only
	 * @return 3D view z scale
	 */
	public double getZscale(int i) {
		// use xscale since there is no z
		return getXscale();
	}

	/**
	 * @param ev1
	 *            whether to consider EV1
	 * @param ev2
	 *            whether to consider EV2
	 * @return min of xmin values of used views
	 */
	public double getXmin(boolean ev1, boolean ev2) {
		if (ev2 && !ev1) {
			return xmin[1];
		} else if (ev1 && !ev2) {
			return xmin[0];
		}
		return getXmin();
	}

	/**
	 * @param ev1
	 *            whether to consider EV1
	 * @param ev2
	 *            whether to consider EV2
	 * @return max of xscale values of used views
	 */
	public double getXscale(boolean ev1, boolean ev2) {
		if (ev2 && !ev1) {
			// xscale = pixel per unit
			// higher xscale means more pixels per unit, i.e. higher precision
			return xscale[1];
		} else if (ev1 && !ev2) {
			return xscale[0];
		}
		return getXscale();
	}

	/**
	 * @param ev1
	 *            whether to consider EV1
	 * @param ev2
	 *            whether to consider EV2
	 * @return max of ymax values of used views
	 */
	public double getYmax(boolean ev1, boolean ev2) {
		if (ev2 && !ev1) {
			return ymax[1];
		} else if (ev1 && !ev2) {
			return ymax[0];
		}
		return getYmax();
	}

	/**
	 * @param ev1
	 *            whether to consider EV1
	 * @param ev2
	 *            whether to consider EV2
	 * @return min of ymin values of used views
	 */
	public double getYmin(boolean ev1, boolean ev2) {
		if (ev2 && !ev1) {
			return ymin[1];
		} else if (ev1 && !ev2) {
			return ymin[0];
		}
		return getYmin();
	}

	/**
	 * @param ev1
	 *            whether to consider EV1
	 * @param ev2
	 *            whether to consider EV2
	 * @return max of yscale values of used views
	 */
	public double getYscale(boolean ev1, boolean ev2) {
		if (ev2 && !ev1) {
			return yscale[1];
		} else if (ev1 && !ev2) {
			return yscale[0];
		}
		return getYscale();
	}

	/**
	 * @return CAS
	 */
	public synchronized GeoGebraCasInterface getGeoGebraCAS() {
		if (ggbCAS == null) {
			ggbCAS = new GeoGebraCAS(this);
		}

		return ggbCAS;
	}

	/**
	 * @return coordinate style
	 */
	final public int getCoordStyle() {
		return coordStyle;
	}

	/**
	 * @param coordStlye
	 *            coordinate style
	 */
	public void setCoordStyle(int coordStlye) {
		coordStyle = coordStlye;
	}

	/**
	 * Returns a GeoElement for the given label.
	 * 
	 * @param label
	 *            CAS cell label
	 * 
	 * @return may return null
	 */
	final public GeoElement lookupLabel(String label) {
		return lookupLabel(label, false, SymbolicMode.NONE);
	}

	/**
	 * Returns a GeoCasCell for the given label.
	 * 
	 * @return may return null
	 */
	final public GeoCasCell lookupCasCellLabel(String label) {
		return cons.lookupCasCellLabel(label);
	}

	/**
	 * Returns a GeoCasCell for the given cas row.
	 * 
	 * @param label
	 *            twin geo label
	 * 
	 * @return may return null
	 * @throws CASException
	 *             thrown if one or more row references are invalid (like $x or
	 *             if the number is higher than the number of rows)
	 */
	final public GeoCasCell lookupCasRowReference(String label)
			throws CASException {
		return cons.lookupCasRowReference(label);
	}

	/**
	 * Finds element with given the label and possibly creates it
	 * 
	 * @param label
	 *            Label of element we are looking for
	 * @param autoCreate
	 *            true iff new geo should be created if missing
	 * @param resMode
	 *            variable resolution mode
	 * @return GeoElement with given label
	 */
	final public GeoElement lookupLabel(String label, boolean autoCreate,
			SymbolicMode resMode) {
		GeoElement geo = cons.lookupLabel(label, autoCreate);

		if ((geo == null) && resMode == SymbolicMode.SYMBOLIC) {
			// lookup CAS variables too
			geo = lookupCasCellLabel(label);

			// resolve unknown variable as dummy geo to keep its name and
			// avoid an "unknown variable" error message
			if (geo == null) {
				geo = new GeoDummyVariable(cons, label);
			}
		}

		return geo;
	}

	/**
	 * Finds the polynomial coefficients of the given expression and returns it
	 * in ascending order. If exp is not a polynomial null is returned.
	 * 
	 * @param exp
	 *            expression in Giac syntax, e.g. "3*a*x^2 + b*x"
	 * @param variable
	 *            e.g "x"
	 * @return array of coefficients, e.g. ["0", "b", "3*a"]
	 */
	final public String[] getPolynomialCoeffs(String exp, String variable) {
		return getGeoGebraCAS().getPolynomialCoeffs(exp, variable);
	}

	/**
	 * returns GeoElement at (row,col) in spreadsheet may return nully
	 * 
	 * @param col
	 *            Spreadsheet column
	 * @param row
	 *            Spreadsheet row
	 * @return Spreadsheet cell content (may be null)
	 */
	public GeoElement getGeoAt(int col, int row) {
		return lookupLabel(
				GeoElementSpreadsheet.getSpreadsheetCellName(col, row));
	}

	/**
	 * Lazy load animation manager.
	 * 
	 * @return animation manager
	 */
	final public AnimationManager getAnimatonManager() {
		if (animationManager == null) {
			animationManager = getApplication().newAnimationManager(this);
		}
		return animationManager;
	}

	/**
	 * @param geo
	 *            geo
	 * @return RealWorld Coordinates of the rectangle covering all euclidian
	 *         views in which <b>geo</b> is shown.<br>
	 *         Format: {xMin,xMax,yMin,yMax,xScale,yScale}
	 */
	public double[] getViewBoundsForGeo(GeoElementND geo) {
		List<Integer> viewSet = geo.getViewSet();
		double[] viewBounds = new double[6];
		for (int i = 0; i < 6; i++) {
			viewBounds[i] = Double.NEGATIVE_INFINITY;
		}
		viewBounds[0] = viewBounds[2] = Double.POSITIVE_INFINITY;
		if (geo.isVisibleInView3D() && app.isEuclidianView3Dinited()) {
			addViews(App.VIEW_EUCLIDIAN3D, viewBounds);
		}
		if (viewSet == null) {
			addViews(App.VIEW_EUCLIDIAN, viewBounds);
			return viewBounds;
		}
		// we can't use foreach here because of GWT
		for (int i = 0; i < viewSet.size(); i++) {
			addViews(viewSet.get(i), viewBounds);
		}

		// if (viewBounds[0]==Double.POSITIVE_INFINITY){
		// //standard values if no view
		// viewBounds[0]=viewBounds[2]=-10;
		// viewBounds[1]=viewBounds[3]=10;
		// viewBounds[5]=viewBounds[6]=1;
		// }
		return viewBounds;
	}

	private void addViews(Integer id, double[] viewBounds) {
		View view = getApplication().getView(id);
		if ((view != null) && (view instanceof EuclidianViewInterfaceSlim)) {

			EuclidianViewInterfaceSlim ev = (EuclidianViewInterfaceSlim) view;
			viewBounds[0] = Math.min(viewBounds[0], ev.getXmin());
			viewBounds[1] = Math.max(viewBounds[1], ev.getXmax());
			viewBounds[2] = Math.min(viewBounds[2], ev.getYmin());
			viewBounds[3] = Math.max(viewBounds[3], ev.getYmax());
			viewBounds[4] = Math.max(viewBounds[4], ev.getXscale());
			viewBounds[5] = Math.max(viewBounds[5], ev.getYscale());
		}
	}

	/**
	 * @return x-axis
	 */
	final public GeoAxis getXAxis() {
		return cons.getXAxis();
	}

	/**
	 * @return y-axis
	 */
	final public GeoAxis getYAxis() {
		return cons.getYAxis();
	}

	final public boolean isAxis(GeoElement geo) {
		return (geo == cons.getXAxis()) || (geo == cons.getYAxis());
	}

	/**
	 * Update localized axis names.
	 */
	public void updateLocalAxesNames() {
		if (cons != null) {
			cons.updateLocalAxesNames();
		}
	}

	/**
	 * Update notify repaint flag and call repaint if needed.
	 * 
	 * @param flag
	 *            whether to notify views
	 */
	public void setNotifyRepaintActive(boolean flag) {
		if (flag != notifyRepaint) {
			notifyRepaint = flag;
			if (notifyRepaint) {
				notifyRepaint();
			}
		}
	}

	final public boolean isNotifyRepaintActive() {
		return notifyRepaint;
	}

	/**
	 * Notify all views to repaint.
	 */
	public final void notifyRepaint() {
		if (notifyRepaint && notifyViewsActive) {
			for (View view : views) {
				view.repaintView();
			}
		}
	}

	/**
	 * Notify all views about zoom / pan.
	 */
	public final void notifyScreenChanged() {
		for (View view : views) {
			if (view instanceof EuclidianViewInterfaceCommon) {
				((EuclidianViewInterfaceCommon) view).screenChanged();
			}
		}
	}

	/**
	 * Dispatch all remaining move events
	 */
	public final void notifyControllersMoveIfWaiting() {
		if (notifyRepaint && notifyViewsActive) {
			for (View view : views) {
				if (view instanceof EuclidianView) {
					((EuclidianView) view).getEuclidianController()
							.moveIfWaiting();
				}
			}
		}
	}

	/**
	 * @return whether at least one view needed repaint
	 */
	public final boolean notifySuggestRepaint() {
		boolean needed = false;
		if (notifyViewsActive) {
			for (View view : views) {
				needed = view.suggestRepaint() || needed;
			}
		}
		return needed;
	}

	/**
	 * Reset all views.
	 */
	final public void notifyReset() {
		if (notifyViewsActive) {
			for (View view : views) {
				view.reset();
			}
		}
	}

	/**
	 * Clears all views, even if notifyViewsActive is false
	 */
	protected final void notifyClearView() {
		for (View view : views) {
			view.clearView();
		}

	}

	/**
	 * Clear newly created geo lists i views.
	 */
	public void clearJustCreatedGeosInViews() {
		if (notifyViewsActive) {
			for (View view : views) {
				if (view instanceof EuclidianViewInterfaceSlim) {
					((EuclidianViewInterfaceSlim) view).getEuclidianController()
							.clearJustCreatedGeos();
				}
			}
		}
	}

	/**
	 * notify only construction protocol about add
	 * 
	 * @param geo
	 *            added geo
	 */
	public void notifyConstructionProtocol(GeoElement geo) {
		for (View view : views) {
			if (view.getViewID() == App.VIEW_CONSTRUCTION_PROTOCOL) {
				view.add(geo);
			}
		}
	}

	/**
	 * Turn on or off views notifications; if turned on add all geos to all
	 * views.
	 * 
	 * @param flag
	 *            whether views should be notified
	 */
	public void setNotifyViewsActive(boolean flag) {
		// Application.debug("setNotifyViews: " + flag);

		if (flag != notifyViewsActive) {
			notifyViewsActive = flag;

			if (flag) {
				// Application.debug("Activate VIEWS");
				viewReiniting = true;

				// "attach" views again
				// add all geos to all views
				for (View view : views) {
					notifyAddAll(view);
				}

				notifyEuclidianViewCE(EVProperty.ZOOM);
				notifyReset();
				// algebra settings need to be applied after remaking tree
				if (app.getGuiManager() != null) {
					app.getGuiManager().applyAlgebraViewSettings();
				}

				viewReiniting = false;
			} else {
				// Application.debug("Deactivate VIEWS");

				// "detach" views
				notifyClearView();
			}
		}
	}

	/*
	 * ******************************************************* methods for
	 * view-Pattern (Model-View-Controller)
	 * ******************************************************
	 */

	/**
	 * @return last attached euclidian view
	 */
	final public EuclidianView getLastAttachedEV() {
		return lastAttachedEV;
	}

	/**
	 * Attach view (view will receive events)
	 * 
	 * @param view
	 *            view
	 */
	public void attach(View view) {
		if (!views.contains(view)) {
			views.add(view);
		}

		if (view instanceof EuclidianView) {
			lastAttachedEV = (EuclidianView) view;
		}

		printAttachedViews();
	}

	private void printAttachedViews() {

		// can give java.util.ConcurrentModificationException
		try {
			if (!notifyViewsActive) {
				Log.debug("Number of registered views = 0");
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("Number of registered views = ");
				sb.append(views.size());
				for (View view : views) {
					sb.append("\n * ");
					sb.append(view.getClass());
				}

				Log.debug(sb.toString());
			}
		} catch (Exception e) {
			Log.debug(e.getMessage());
		}
	}

	/**
	 * Detach a view (will stop sending events to it)
	 * 
	 * @param view
	 *            view
	 */
	public void detach(View view) {
		views.remove(view);
		printAttachedViews();

	}

	/**
	 * Notify the views that the mode changed.
	 * 
	 * @param mode
	 *            mode (see EuclidianConstants)
	 * @param m
	 *            mode change event type
	 */
	final public void notifyModeChanged(int mode, ModeSetter m) {
		if (notifyViewsActive) {
			for (View view : views) {
				view.setMode(mode, m);
			}
		}
	}

	/**
	 * Add all elements to a view
	 * 
	 * @param view
	 *            view
	 */
	final public void notifyAddAll(View view) {
		if (cons == null) {
			return;
		}
		int consStep = cons.getStep();
		notifyAddAll(view, consStep);
	}

	/**
	 * Registers an algorithm that needs to be updated when notifyRename(),
	 * notifyAdd(), or notifyRemove() is called.
	 * 
	 * @param algo
	 *            algo listening to rename events
	 */
	public void registerRenameListenerAlgo(AlgoElement algo) {
		if (renameListenerAlgos == null) {
			renameListenerAlgos = new ArrayList<>();
		}

		if (!renameListenerAlgos.contains(algo)) {
			renameListenerAlgos.add(algo);
		}
	}

	/**
	 * See {@link #registerRenameListenerAlgo(AlgoElement)}
	 * @param algo
	 *            algo listening to rename events
	 */
	void unregisterRenameListenerAlgo(AlgoElement algo) {
		if (renameListenerAlgos != null) {
			renameListenerAlgos.remove(algo);
		}
	}

	private void notifyRenameListenerAlgos() {
		// #4073 command Object[] registers rename listeners
		if (cons != null && !cons.isFileLoading()
				&& !this.isSpreadsheetBatchRunning()) {
			AlgoElement.updateCascadeAlgos(renameListenerAlgos);
		}
	}

	/**
	 * @return whether spreadsheet batch operation is running
	 */
	public boolean isSpreadsheetBatchRunning() {
		return this.spreadsheetBatchRunning;
	}

	/**
	 * @param b
	 *            whether spreadsheet batch operation is running
	 */
	public void setSpreadsheetBatchRunning(boolean b) {
		this.spreadsheetBatchRunning = b;
		if (!b) {
			notifyRenameListenerAlgos();
		}
	}

	/**
	 * Currently, this method should rename every oldLabel to newLabel in
	 * GgbScript-type objects, for use of CopyPaste and InsertFile
	 * 
	 * @param oldLabel
	 *            the label to be renamed from
	 * @param newLabel
	 *            the label to be renamed to
	 * @return whether any renaming happened
	 */
	final public boolean renameLabelInScripts(String oldLabel,
			String newLabel) {
		Script work;
		boolean somethingHappened = false;
		for (GeoElement geo : cons.getGeoSetWithCasCellsConstructionOrder()) {
			work = geo.getScript(EventType.UPDATE);
			if (work instanceof GgbScript) {
				somethingHappened |= work.renameGeo(oldLabel, newLabel);
			}
			work = geo.getScript(EventType.CLICK);
			if (work instanceof GgbScript) {
				somethingHappened |= work.renameGeo(oldLabel, newLabel);
			}
		}
		return somethingHappened;
	}

	/**
	 * Add all elements up to construction step to a view.
	 * 
	 * @param view
	 *            view
	 * @param consStep
	 *            construction step
	 */
	final public void notifyAddAll(View view, int consStep) {
		if (!notifyViewsActive) {
			return;
		}

		for (GeoElement geo : cons.getGeoSetWithCasCellsConstructionOrder()) {
			// stop when not visible for current construction step
			if (!geo.isAvailableAtConstructionStep(consStep)) {
				break;
			}
			view.add(geo);
		}

		if (getUpdateAgain()) {
			setUpdateAgain(false, null);
			app.scheduleUpdateConstruction();
		}
	}

	/**
	 * Notify views about added geo.
	 * 
	 * @param geo
	 *            added geo
	 */
	public final void notifyAdd(GeoElement geo) {
		if (notifyViewsActive) {
			if (addingPolygon && geo.isLabelSet()) {
				if (geo.getXMLtypeString().equalsIgnoreCase("Polygon")) {
					this.newPolygon = geo;
				}

			}
			for (View view : views) {
				if ((view.getViewID() != App.VIEW_CONSTRUCTION_PROTOCOL)
						|| isNotifyConstructionProtocolViewAboutAddRemoveActive()) {
					view.add(geo);
				}
			}
		}

		notifyRenameListenerAlgos();
	}

	/**
	 * Notify views about adding polygon.
	 */
	public final void addingPolygon() {
		if (notifyViewsActive) {
			this.addingPolygon = true;
			if (app.hasEventDispatcher()) {
				app.getEventDispatcher().addingPolygon();
			}
		}
	}

	/**
	 * Notify views about new polygon
	 */
	public final void notifyPolygonAdded() {
		if (notifyViewsActive && app.hasEventDispatcher()) {
			app.getEventDispatcher().addPolygonComplete(this.newPolygon);
		}
	}

	/**
	 * Notify views about mass remove
	 */
	public final void notifyRemoveGroup() {
		if (notifyViewsActive && app.hasEventDispatcher()) {
			app.getEventDispatcher().deleteGeos(deleteList);
		}
		this.deleteList.clear();
	}

	/**
	 * Notify views about removed element
	 * 
	 * @param geo
	 *            removed element
	 */
	public final void notifyRemove(GeoElement geo) {
		if (notifyViewsActive) {
			if (geo.isLabelSet()) {
				this.deleteList.add(geo);
			}
			for (View view : views) {
				if ((view.getViewID() != App.VIEW_CONSTRUCTION_PROTOCOL)
						|| isNotifyConstructionProtocolViewAboutAddRemoveActive()) {
					// needed for GGB-808
					// geoCasCell is already removed from cas view
					if (view.getViewID() == App.VIEW_CAS) {
						removeFromCAS(view, geo);
					} else {
						view.remove(geo);
					}
				}
			}
		}

		notifyRenameListenerAlgos();
	}

	/**
	 * Remove object from CAS, ignore CAS cells with index -1 as they were
	 * removed in Construction.removeFromConstructionList
	 * 
	 * NB we can't ignore all cells so that construction protocol navigation
	 * works
	 */
	private static void removeFromCAS(View view, GeoElement geo) {
		if (geo instanceof GeoCasCell
				&& geo.getConstructionIndex() < 0) {
			return;
		}
		view.remove(geo);
	}

	/**
	 * Notify views about moving multiple geos (start)
	 */
	public final void movingGeoSet() {
		if (notifyViewsActive && app.hasEventDispatcher()) {
			app.getEventDispatcher().movingGeos();
		}
	}

	/**
	 * Notify views about moving multiple geos (end)
	 * 
	 * @param elmSet
	 *            moved geos
	 */
	public final void movedGeoSet(ArrayList<GeoElement> elmSet) {
		if (notifyViewsActive && app.hasEventDispatcher()) {
			app.getEventDispatcher().movedGeos(elmSet);
		}
	}

	/**
	 * Notify views about possible value change
	 * 
	 * @param geo
	 *            element
	 */
	public final void notifyUpdate(GeoElement geo) {
		// event dispatcher should not collect calls to stay compatible with 4.0
		if (notifyViewsActive) {
			for (View view : views) {
				view.update(geo);
			}
		}
	}

	/**
	 * Notify views about changed geo's location on screen.
	 * 
	 * @param geo
	 *            element
	 */
	public final void notifyUpdateLocation(GeoElement geo) {
		// event dispatcher should not collect calls to stay compatible with 4.0
		if (notifyViewsActive) {
			for (View view : views) {
				// we already told event dispatcher
				if (view instanceof UpdateLocationView) {
					((UpdateLocationView) view).updateLocation(geo);
				} else {
					view.update(geo);
				}
			}
		}
	}

	/**
	 * Notify views about changed visual style.
	 * 
	 * @param geo
	 *            element
	 * @param prop
	 *            property
	 */
	public final void notifyUpdateVisualStyle(GeoElement geo, GProperty prop) {
		if (notifyViewsActive) {
			for (View view : views) {
				view.updateVisualStyle(geo, prop);
			}
		}
	}

	/**
	 * Notify views about highlighting geo.
	 * 
	 * @param geo
	 *            highlighted geo
	 */
	public final void notifyUpdateHightlight(GeoElement geo) {
		if (notifyViewsActive) {
			for (View view : views) {
				view.updateHighlight(geo);
			}
		}
	}

	/**
	 * Notify views about auxiliary property change
	 * 
	 * @param geo
	 *            changed geo
	 */
	public final void notifyUpdateAuxiliaryObject(GeoElement geo) {
		if (notifyViewsActive) {
			for (View view : views) {
				view.updateAuxiliaryObject(geo);
			}
		}
	}

	/**
	 * Notify views about rename
	 * 
	 * @param geo
	 *            renamed geo
	 */
	public final void notifyRename(GeoElement geo) {
		if (notifyViewsActive) {
			for (View view : views) {
				view.rename(geo);
			}
		}

		notifyRenameListenerAlgos();
	}

	/**
	 * Notify views about geo type change
	 * 
	 * @param geo
	 *            new geo after type change
	 */
	public final void notifyTypeChanged(GeoElement geo) {
		if (notifyViewsActive) {
			for (View view : views) {
				if (view.getViewID() == App.VIEW_ALGEBRA) {
					view.rename(geo);
				}
			}
		}
	}

	/**
	 * Notify about finished rename.
	 */
	public final void notifyRenameUpdatesComplete() {
		if (notifyViewsActive && app.hasEventDispatcher()) {
			app.getEventDispatcher().renameUpdatesComplete();
		}
	}

	/**
	 * @param pasteXml
	 *            XML of pasted construction part
	 */
	public void notifyPaste(String pasteXml) {
		if (notifyViewsActive && app.hasEventDispatcher()) {
			app.getEventDispatcher().pasteElms(pasteXml);
		}
	}

	/**
	 * Notify views about finished paste.
	 */
	public void notifyPasteComplete(ArrayList<GeoElement> pastedGeos) {
		if (notifyViewsActive && app.hasEventDispatcher()) {
			app.getEventDispatcher().pasteElmsComplete(pastedGeos);

		}
	}

	/**
	 * @param geo
	 *            animated geo
	 */
	public void notifyStartAnimation(GeoElement geo) {
		if (notifyViewsActive) {
			app.getEventDispatcher().startAnimation(geo);
		}
	}

	/**
	 * @param geo
	 *            animated geo
	 */
	public void notifyStopAnimation(GeoElement geo) {
		if (notifyViewsActive) {
			app.getEventDispatcher().stopAnimation(geo);
		}
	}

	public boolean isNotifyViewsActive() {
		return notifyViewsActive && !viewReiniting;
	}

	public boolean isViewReiniting() {
		return viewReiniting;
	}

	/**
	 * Recompute all objects.
	 */
	public final void updateConstruction() {
		updateConstruction(true);
	}

	/**
	 * Recompute all objects.
	 * 
	 * @param randomize
	 *            whether to randomize random numbers
	 */
	public final void updateConstruction(boolean randomize) {

		// views are notified about update at the end of this method
		cons.updateConstruction(randomize);

		// latexes in GeoGebraWeb are rendered afterwards and set updateEVAgain
		if (getUpdateAgain()) {
			setUpdateAgain(false, null);
			app.scheduleUpdateConstruction();
		} else {
			notifyRepaint();
		}
	}

	/**
	 * Update construction from language change.
	 */
	public void updateConstructionLanguage() {
		// views are notified about update at the end of this method
		cons.updateConstructionLanguage();

		if (getUpdateAgain()) {
			setUpdateAgain(false, null);
			app.scheduleUpdateConstruction();
		} else {
			notifyRepaint();
		}
	}

	/**
	 * update construction n times
	 * 
	 * @param randomize
	 *            whether variables should be randomized
	 * @param n
	 *            number of repetitions
	 */
	public void updateConstruction(boolean randomize, int n) {

		// views are notified about update at the end of this method
		for (int i = 0; i < n; i++) {
			cons.updateConstruction(randomize);
		}

		// latexes in GeoGebraWeb are rendered afterwards and set updateEVAgain
		if (getUpdateAgain()) {
			setUpdateAgain(false, null);
			app.scheduleUpdateConstruction();
		} else {
			notifyRepaint();
		}
	}

	/**
	 * Tests if the current construction has no elements.
	 * 
	 * @return true if the current construction has no GeoElements; false
	 *         otherwise.
	 */
	public boolean isEmpty() {
		return cons.isEmpty();
	}

	/*
	 * ****************************** redo / undo for current construction
	 * *****************************
	 */

	/**
	 * @param flag
	 *            whether undo is active
	 */
	public void setUndoActive(boolean flag) {
		undoActive = flag;
	}

	/**
	 * @return whether undo is active
	 */
	public boolean isUndoActive() {
		return undoActive;
	}

	/**
	 * Store an undo point.
	 */
	public void storeUndoInfo() {
		if (undoActive && cons != null) {
			cons.storeUndoInfo();
		}
	}

	/**
	 * Restore state from last undo point.
	 */
	public void restoreCurrentUndoInfo() {
		if (undoActive) {
			cons.restoreCurrentUndoInfo();
		}
	}

	/**
	 * Initialize undo manager if possible.
	 */
	public void initUndoInfo() {
		if (undoActive && cons != null) {
			cons.initUndoInfo();
		}
	}

	/**
	 * Redo last action.
	 */
	public void redo() {
		if (undoActive && cons.getUndoManager().redoPossible()) {
			app.batchUpdateStart();
			cons.redo();
			app.batchUpdateEnd();
			storeStateForModeStarting();
			app.getEventDispatcher()
					.dispatchEvent(new Event(EventType.REDO));
			app.setUnAutoSaved();
		}
	}

	/**
	 * Restore state from mode-specific undo point.
	 */
	public void restoreStateForInitNewMode() {
		if (undoActive && getSelectionManager().isGeoToggled()) {
			restoreStateForModeStarting();
		}
	}

	/**
	 * Store mode-specific undo point.
	 */
	public void storeStateForModeStarting() {
		stateForModeStarting = cons.getCurrentUndoXML(true);
		getSelectionManager().resetGeoToggled();
	}

	/**
	 * Store both global and mode specific undo point.
	 */
	public void storeUndoInfoAndStateForModeStarting() {
		if (cons != null) {
			storeStateForModeStarting();
			if (cons.isUndoEnabled()) {
				// reuse cons.getCurrentUndoXML(true)
				cons.getUndoManager().storeUndoInfo(stateForModeStarting,
						false);
			}
		}
	}

	private SelectionManager getSelectionManager() {
		return app.getSelectionManager();
	}

	private void restoreStateForModeStarting() {
		app.batchUpdateStart();
		app.getCompanion().storeViewCreators();
		app.getScriptManager().disableListeners();
		notifyReset();
		getApplication().getActiveEuclidianView().getEuclidianController()
				.clearSelections();
		cons.processXML(stateForModeStarting);
		notifyReset();
		app.getScriptManager().enableListeners();
		app.getCompanion().recallViewCreators();
		app.batchUpdateEnd();
		app.setUnAutoSaved();
	}

	/**
	 * Undo last action.
	 */
	public void undo() {
		if (undoActive) {
			if (getApplication().getActiveEuclidianView()
					.getEuclidianController().isUndoableMode()) {
				if (getSelectionManager().isGeoToggled()
						&& !getSelectionManager().getSelectedGeos().isEmpty()) {

					restoreStateForModeStarting();
					getSelectionManager().resetGeoToggled();
					return;
				}
			}

			if (cons.getUndoManager().undoPossible()) {
				app.batchUpdateStart();
				cons.undo();

				// repaint needed for last undo in second EuclidianView (bugfix)
				if (!undoPossible()) {
					notifyRepaint();
				}
				app.batchUpdateEnd();
				storeStateForModeStarting();
				app.getEventDispatcher()
						.dispatchEvent(new Event(EventType.UNDO));
				app.setUnAutoSaved();
			}
		}
	}

	public boolean undoPossible() {
		return undoActive && cons != null && cons.undoPossible();
	}

	public boolean redoPossible() {
		return undoActive && cons != null && cons.redoPossible();
	}

	/**
	 * Get {@link Kernel#insertLineBreaks insertLineBreaks}.
	 * 
	 * @return {@link Kernel#insertLineBreaks insertLineBreaks}.
	 */
	public boolean isInsertLineBreaks() {
		return insertLineBreaks;
	}

	/**
	 * @return major version of XML
	 */
	static public String getXMLFileFormat() {
		return GeoGebraConstants.XML_FILE_FORMAT;
	}

	/**
	 * Set {@link Kernel#insertLineBreaks insertLineBreaks}.
	 * 
	 * @param insertLineBreaks
	 *            The value to set {@link Kernel#insertLineBreaks
	 *            insertLineBreaks} to.
	 */
	public void setInsertLineBreaks(boolean insertLineBreaks) {
		this.insertLineBreaks = insertLineBreaks;
	}

	/**
	 * @return imaginary unit
	 */
	public GeoVec2D getImaginaryUnit() {
		if (imaginaryUnit == null) {
			imaginaryUnit = new GeoVec2D(this, 0, 1);
			imaginaryUnit.setMode(COORD_COMPLEX);
		}

		return imaginaryUnit;
	}

	/**
	 * @return E as MySpecialDouble
	 */
	public MySpecialDouble getEulerNumber() {
		if (eulerConstant == null) {
			eulerConstant = new MySpecialDouble(this, Math.E,
					Unicode.EULER_STRING);
		}
		return eulerConstant;
	}

	/**
	 * Creates a new algorithm that uses the given macro.
	 * 
	 * @return output of macro algorithm
	 */

	final public GeoElement[] useMacro(String[] labels, Macro macro,
			GeoElement[] input) {
		try {
			AlgoMacro algo = new AlgoMacro(cons, labels, macro, input, true);
			return algo.getOutput();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Localization getLocalization() {
		return getApplication().getLocalization();
	}

	/**
	 * Returns the kernel settings in XML format.
	 */
	public void getKernelXML(StringBuilder sb, boolean asPreference) {

		// kernel settings
		sb.append("<kernel>\n");

		// is 3D?
		if (cons.requires3D()) {
			// DO NOT REMOVE
			// it's important we pick up errors involving this quickly
			Log.error("file has 3D objects");
			sb.append("\t<uses3D val=\"true\"/>\n");
		}

		// continuity: true or false, since V3.0
		sb.append("\t<continuous val=\"");
		sb.append(isContinuous());
		sb.append("\"/>\n");

		if (symbolicMode == SymbolicMode.SYMBOLIC_AV) {
			sb.append("\t<symbolic val=\"true\"/>\n");
		}

		sb.append("\t<usePathAndRegionParameters val=\"");
		sb.append(usePathAndRegionParameters.getXML());
		sb.append("\"/>\n");

		if (useSignificantFigures) {
			// significant figures
			sb.append("\t<significantfigures val=\"");
			sb.append(getPrintFigures());
			sb.append("\"/>\n");
		} else {
			// decimal places
			sb.append("\t<decimals val=\"");
			sb.append(getPrintDecimals());
			sb.append("\"/>\n");
		}

		// angle unit
		sb.append("\t<angleUnit val=\"");
		switch (getAngleUnit()) {
			case Kernel.ANGLE_RADIANT:
				sb.append("radiant");
				break;
			case Kernel.ANGLE_DEGREES_MINUTES_SECONDS:
				sb.append("degreesMinutesSeconds");
				break;
			case Kernel.ANGLE_DEGREE:
			default:
				sb.append("degree");
				break;
		}
		sb.append("\"/>\n");

		// algebra style
		sb.append("\t<algebraStyle val=\"");
		sb.append(getAlgebraStyle());
		sb.append("\" spreadsheet=\"");
		sb.append(getAlgebraStyleSpreadsheet());
		sb.append("\"/>\n");

		// coord style
		sb.append("\t<coordStyle val=\"");
		sb.append(getCoordStyle());
		sb.append("\"/>\n");

		// animation
		if (isAnimationRunning()) {
			sb.append("\t<startAnimation val=\"");
			sb.append(isAnimationRunning());
			sb.append("\"/>\n");
		}

		if (asPreference) {
			sb.append("\t<localization digits=\"");
			sb.append(getLocalization().isUsingLocalizedDigits());
			sb.append("\" labels=\"");
			sb.append(getLocalization().isUsingLocalizedLabels());
			sb.append("\"/>\n");

			sb.append("\t<casSettings timeout=\"");
			sb.append(OptionsCAS.getTimeoutOption(
					app.getSettings().getCasSettings().getTimeoutMilliseconds()
							/ 1000));
			sb.append("\" expRoots=\"");
			sb.append(app.getSettings().getCasSettings().getShowExpAsRoots());
			sb.append("\"/>\n");
		}

		sb.append("</kernel>\n");
	}

	/*
	 * ********************************** MACRO handling
	 * *********************************
	 */

	/**
	 * Creates a new macro within the kernel. A macro is a user defined command
	 * in GeoGebra.
	 */
	public void addMacro(Macro macro) {
		if (macroManager == null) {
			macroManager = new MacroManager();
		}
		macroManager.addMacro(macro);

		app.dispatchEvent(
				new Event(EventType.ADD_MACRO, null, macro.getCommandName()));
	}

	/**
	 * Removes a macro from the kernel.
	 */
	public void removeMacro(Macro macro) {
		if (macroManager != null) {
			macroManager.removeMacro(macro);
		}

		app.dispatchEvent(new Event(EventType.REMOVE_MACRO, null,
				macro.getCommandName()));
	}

	/**
	 * Removes all macros from the kernel.
	 */
	public void removeAllMacros() {
		if (macroManager != null) {
			getApplication().removeMacroCommands();
			macroManager.removeAllMacros();
		}

		app.dispatchEvent(new Event(EventType.REMOVE_MACRO, null, null));
	}

	/**
	 * Sets the command name of a macro. Note: if the given name is already used
	 * nothing is done.
	 * 
	 * @param macro
	 *            macro
	 * @param cmdName
	 *            new name
	 * 
	 * @return if the command name was really set
	 */
	public boolean setMacroCommandName(Macro macro, String cmdName) {
		boolean nameUsed = macroManager.getMacro(cmdName) != null;
		if (nameUsed || cmdName == null || cmdName.length() == 0) {
			return false;
		}

		app.dispatchEvent(new Event(EventType.RENAME_MACRO, null,
				"[\"" + macro.getCommandName() + "\",\"" + cmdName + "\"]"));
		macroManager.setMacroCommandName(macro, cmdName);

		return true;
	}

	/**
	 * Returns the macro object for a given macro command name. Note: null may
	 * be returned.
	 * 
	 * @param commandName
	 *            command name
	 * @return macro
	 */
	public Macro getMacro(String commandName) {
		return (macroManager == null) ? null
				: macroManager.getMacro(commandName);
	}

	/**
	 * Returns an XML represenation of the given macros in this kernel.
	 * 
	 * @param macros
	 *            macros
	 * 
	 * @return macro construction XML
	 */
	public String getMacroXML(ArrayList<Macro> macros) {
		if (hasMacros()) {
			return MacroManager.getMacroXML(macros);
		}
		return "";
	}

	/**
	 * Returns whether any macros have been added to this kernel.
	 * 
	 * @return whether any macros have been added to this kernel.
	 */
	public boolean hasMacros() {
		return (macroManager != null && macroManager.getMacroNumber() > 0);
	}

	/**
	 * @return the number of currently registered macros
	 */
	public int getMacroNumber() {
		if (macroManager == null) {
			return 0;
		}
		return macroManager.getMacroNumber();
	}

	/**
	 * Returns a list with all currently registered macros.
	 * 
	 * @return all macros
	 */
	public ArrayList<Macro> getAllMacros() {
		if (macroManager == null) {
			return null;
		}
		return macroManager.getAllMacros();
	}

	/**
	 * @param i
	 *            macro index
	 * @return i-th registered macro
	 */
	public Macro getMacro(int i) {
		try {
			return macroManager.getMacro(i);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the ID of the given macro.
	 * 
	 * @param macro
	 *            macro
	 * @return index or -1 if not found
	 */
	public int getMacroID(Macro macro) {
		return (macroManager == null) ? -1 : macroManager.getMacroID(macro);
	}

	/**
	 * used to delay animation start until everything loaded
	 */
	public void setWantAnimationStarted(boolean want) {
		wantAnimationStarted = want;
	}

	public boolean wantAnimationStarted() {
		return wantAnimationStarted;
	}

	/**
	 * Converts a NumberValue object to an ExpressionNode object.
	 * 
	 * @param geo
	 *            construction element
	 * @return expression
	 */
	public ExpressionNode convertNumberValueToExpressionNode(GeoElement geo) {
		AlgoElement algo = geo.getParentAlgorithm();
		Traversing ifReplacer = new Traversing() {

			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev instanceof GeoElement) {
					return Kernel.this
							.convertNumberValueToExpressionNode((GeoElement) ev)
							.unwrap();
				}
				return ev;
			}

		};
		if (!geo.isLabelSet() && algo != null
				&& algo instanceof DependentAlgo) {
			DependentAlgo algoDep = (DependentAlgo) algo;

			if (algoDep.getExpression() != null) {

				return algoDep.getExpression().getCopy(this)
						.traverse(ifReplacer).wrap();
			}
		}

		if (!geo.isLabelSet() && algo != null && algo instanceof AlgoIf) {
			return ((AlgoIf) algo).toExpression();
		}
		return new ExpressionNode(this, geo);
	}

	/**
	 * @param circle
	 *            circle
	 * @param point1
	 *            close point
	 * @return point on circle close to point1
	 */
	public GeoPointND rigidPolygonPointOnCircle(GeoConicND circle,
			GeoPointND point1) {
		return getAlgoDispatcher().point(null, circle, point1.getInhomX(),
				point1.getInhomY(), true, false, true);
	}

	/**
	 * tangent to Curve f in point P: (b'(t), -a'(t), a'(t)*b(t)-a(t)*b'(t))
	 * 
	 * @param label
	 *            output label
	 * @param P
	 *            point
	 * @param f
	 *            curve
	 * @return tangent
	 */
	final public GeoLine tangent(String label, GeoPointND P,
			GeoCurveCartesian f) {
		return KernelCAS.tangent(cons, label, P, f);
	}

	/**
	 * @return spreadsheet coord handler
	 */
	public GeoElementSpreadsheet getGeoElementSpreadsheet() {
		return ges;
	}

	/**
	 * @return new kernel for a macro
	 */
	public MacroKernel newMacroKernel() {
		return new MacroKernel(this);
	}

	/**
	 * Notify views about layer change.
	 * 
	 * @param geo
	 *            element
	 * @param layer
	 *            old layer
	 * @param layer2
	 *            new layer
	 */
	public void notifyChangeLayer(GeoElement geo, int layer, int layer2) {
		app.updateMaxLayerUsed(layer2);
		if (notifyViewsActive) {
			for (View view : views) {
				if (view instanceof LayerView) {
					((LayerView) view).changeLayer(geo, layer, layer2);
				}
			}
		}
	}

	/**
	 * When function (or parabola) is transformed to curve, we need some good
	 * estimate for which part of curve should be ploted
	 * 
	 * @return lower bound for function -&gt; curve transform
	 */
	public double getXmaxForFunctions() {
		if (getXmin() == getXmax()) {
			return 10;
		}
		return (((2 * getXmax()) - getXmin()) + getYmax()) - getYmin();
	}

	/**
	 * @see #getXmaxForFunctions()
	 * @return upper bound for function -&gt; curve transform
	 */
	public double getXminForFunctions() {
		if (getXmin() == getXmax()) {
			return -10;
		}
		return (((2 * getXmin()) - getXmax()) + getYmin()) - getYmax();
	}

	/**
	 * clear cache (needed in web when CAS loaded)
	 */
	public synchronized void clearCasCache() {
		if (ggbCasCache != null) {
			ggbCasCache.clear();
		}
		if (ggbCAS != null) {
			ggbCAS.clearCache();
		}
	}

	/**
	 * Recompute CAS algos. Used by web once CAS is loaded.
	 */
	public void refreshCASCommands() {
		clearCasCache();

		ArrayList<GeoElement> geosToUpdate = new ArrayList<>();
		for (GeoElement geo : cons.getGeoSetWithCasCellsConstructionOrder()) {
			AlgoElement parent = geo.getParentAlgorithm();
			if (geo instanceof CasEvaluableFunction) {
				((CasEvaluableFunction) geo).clearCasEvalMap();
				if (parent instanceof AlgoDependentFunction
					|| parent instanceof AlgoDependentFunctionNVar) {
					geosToUpdate.add(geo);
				}
			} else if (geo instanceof GeoSymbolicI && parent == null) {
				((GeoSymbolicI) geo).computeOutput();
				geosToUpdate.add(geo);
			}
		}
		CasAlgoChecker checker = new CasAlgoChecker();
		for (AlgoElement algo : cons.getAlgoList()) {
			if (algo instanceof AlgoCasBase) {
				((AlgoCasBase) algo).clearCasEvalMap();
			}
			if (algo instanceof AlgoUsingTempCASalgo) {
				((AlgoUsingTempCASalgo) algo).refreshCASResults();
			}

			if (checker.isAlgoUsingCas(algo)) {
				// eg Limit, LimitAbove, LimitBelow, SolveODE
				// AlgoCasCellInterface: eg Solve[x^2]
				algo.compute();

				if (algo.getOutput() != null) {
					geosToUpdate.addAll(Arrays.asList(algo.getOutput()));
				}
			}
		}
		cons.setUpdateConstructionRunning(true);
		GeoElement.updateCascade(geosToUpdate, new TreeSet<AlgoElement>(), true);
		cons.setUpdateConstructionRunning(false);
	}

	public GeoElement[] polygonND(String[] labels, GeoPointND[] P) {
		return getAlgoDispatcher().polygon(labels, P);
	}

	public GeoElement[] polyLineND(String label, GeoPointND[] P) {
		return getAlgoDispatcher().polyLine(label, P);
	}

	/**
	 * over-ridden in Kernel3D
	 * 
	 * @param transformedLabel
	 *            output label
	 * @param geoPointND
	 *            start point
	 * @param geoPointND2
	 *            point on ray
	 * @return ray
	 */
	public GeoRayND rayND(String transformedLabel, GeoPointND geoPointND,
			GeoPointND geoPointND2) {
		return getAlgoDispatcher().ray(transformedLabel, (GeoPoint) geoPointND,
				(GeoPoint) geoPointND2);
	}

	/**
	 * over-ridden in Kernel3D
	 * 
	 * @param label
	 *            output label
	 * @param P
	 *            start point
	 * @param Q
	 *            end point
	 * @return segment
	 */
	public GeoSegmentND segmentND(String label, GeoPointND P, GeoPointND Q) {
		return getAlgoDispatcher().segment(label, (GeoPoint) P, (GeoPoint) Q);
	}

	/**
	 * @return factory for AlgoElements
	 */
	public AlgoDispatcher getAlgoDispatcher() {
		if (algoDispatcher == null) {
			algoDispatcher = newAlgoDispatcher(cons);
		}
		return algoDispatcher;
	}

	/**
	 * 
	 * @return new instance of AlgoDispatcher
	 */
	protected AlgoDispatcher newAlgoDispatcher(Construction cons1) {
		return new AlgoDispatcher(cons1);
	}

	public GeoRayND ray(String label, GeoPoint p, GeoPoint q) {
		return getAlgoDispatcher().ray(label, p, q);
	}

	public GeoSegmentND segment(String label, GeoPoint p, GeoPoint q) {
		return getAlgoDispatcher().segment(label, p, q);
	}

	public GeoElement[] polygon(String[] labels, GeoPointND[] p) {
		return getAlgoDispatcher().polygon(labels, p);
	}

	public GeoElement[] polyLine(String label, GeoPointND[] p) {
		return getAlgoDispatcher().polyLine(label, p);
	}

	public boolean hasAlgebraProcessor() {
		return algProcessor != null;
	}

	public void setAlgebraProcessor(AlgebraProcessor algebraProcessor) {
		algProcessor = algebraProcessor;
	}

	public AlgebraProcessor getAlgPForAsync() {
		return algProcessor;
	}

	/**
	 * used in 3D
	 * 
	 * @return xOy plane
	 */
	public GeoCoordSys2D getXOYPlane() {
		return null;
	}

	/**
	 * used in 3D
	 * 
	 * @return global space
	 */
	public GeoDirectionND getSpace() {
		return null;
	}

	/**
	 * used for DrawEquationWeb and DrawText in GeoGebraWeb
	 * 
	 * @param value
	 *            whether another update is needed
	 * @param geo
	 *            source geo
	 */
	public void setUpdateAgain(boolean value, GeoElement geo) {
		updateEVAgain = value;
		if (value) {
			this.cons.addLaTeXGeo(geo);
		}
	}

	/**
	 * used for DrawEquationWeb and DrawText in GeoGebraWeb
	 * 
	 * @return whether updateAgain flag was set
	 */
	public boolean getUpdateAgain() {
		return updateEVAgain && app.isHTML5Applet();
	}

	/**
	 * used for DrawEquationWeb and DrawText in GeoGebraWeb
	 * 
	 * @param value
	 *            whether bounding box update is needed
	 */
	public void setForceUpdatingBoundingBox(boolean value) {
		forceUpdatingBoundingBox = value;
	}

	/**
	 * used for DrawEquationWeb and DrawText in GeoGebraWeb
	 * 
	 * @return whether bounding box update is needed
	 */
	public boolean getForceUpdatingBoundingBox() {
		return forceUpdatingBoundingBox && app.isHTML5Applet();
	}

	public boolean useCASforDerivatives() {
		return !app.isScreenshotGenerator();
	}

	public boolean useCASforIntegrals() {
		return !app.isScreenshotGenerator();
	}

	/**
	 * Notify views about started update batch.
	 */
	public void notifyBatchUpdate() {
		if (notifyViewsActive) {
			for (View view : views) {
				view.startBatchUpdate();
			}
		}
	}

	/**
	 * Notify views about finished update batch.
	 */
	public void notifyEndBatchUpdate() {
		if (notifyViewsActive) {
			for (View view : views) {
				view.endBatchUpdate();
			}
		}
	}

	/**
	 * Call setLabels on all views.
	 */
	public void setViewsLabels() {
		if (notifyViewsActive) {
			for (View view : views) {
				if (view instanceof SetLabels) {
					((SetLabels) view).setLabels();
				}
			}
		}
	}

	/**
	 * Notify views about orientation
	 */
	public void setViewsOrientation() {
		if (notifyViewsActive) {
			for (View view : views) {
				if (view instanceof SetOrientation) {
					((SetOrientation) view).setOrientation();
				}
			}
		}
	}

	/**
	 * 
	 * @return true if algo (e.g. AlgoOrthoLinePointLine) doens't need to say
	 *         that we work in (or parallel to) xOy plane
	 */
	final public boolean noNeedToSpecifyXOYPlane() {
		return getXOYPlane() == null
				|| getApplication().getActiveEuclidianView().isDefault2D();
	}

	/**
	 * @param geo
	 *            2D or 3D geo
	 * @return 3D copy of the geo (if exists)
	 */
	public final GeoElement copy3D(GeoElement geo) {
		return geoFactory.copy3D(geo);
	}

	/**
	 * @param cons1
	 *            target cons
	 * @param geo
	 *            geo to copy
	 * @return 3D copy internal of the geo (if exists)
	 */
	public final GeoElement copyInternal3D(Construction cons1, GeoElement geo) {
		return geoFactory.copyInternal3D(cons1, geo);
	}

	/**
	 * set correct string mode regarding active euclidian view
	 * 
	 * @param point
	 *            point
	 */
	public void setStringMode(GeoPointND point) {
		if (cons.isFileLoading()) {
			// nothing to do : string mode will be set from the XML
			return;
		}

		if (app.getActiveEuclidianView().isEuclidianView3D()) {
			point.setCartesian3D();
		} else {
			point.setCartesian();
		}
		point.update();
	}

	/**
	 * @return whether parser should prefer 3d objects (x=y is a plane)
	 */
	public boolean isParsingFor3D() {
		if (getLoadingMode()) {
			return false;
		}
		EuclidianViewInterfaceCommon ev = getApplication()
				.getActiveEuclidianView();
		if (ev.isEuclidianView3D() || ev.isShowing()) {
			return ev.isEuclidianView3D();
		}
		return getApplication().showView(App.VIEW_EUCLIDIAN3D);

	}

	/**
	 * @param pt
	 *            point
	 * @return Vector(point)
	 */
	public GeoElement wrapInVector(GeoPointND pt) {
		AlgoVectorPoint algo = new AlgoVectorPoint(cons, pt);
		cons.removeFromConstructionList(algo);
		return (GeoElement) algo.getVector();
	}

	/**
	 * @param vec
	 *            vector
	 * @return Point(vector)
	 */
	public GeoPointND wrapInPoint(GeoVectorND vec) {
		AlgoPointVector algo = new AlgoPointVector(cons, cons.getOrigin(), vec);
		cons.removeFromConstructionList(algo);
		return algo.getQ();
	}

	/**
	 * for compatibility/interfacing with 3D
	 * 
	 * @return zAxis
	 */
	public GeoAxisND getZAxis3D() {
		return null;
	}

	/**
	 * @return number of views with xmax
	 */
	public int getXmaxLength() {
		return xmax.length;
	}

	/**
	 * Creates a new GeoElement object for the given type string.
	 * 
	 * @param cons1
	 *            construction
	 * 
	 * @param type
	 *            String as produced by GeoElement.getXMLtypeString()
	 * @return new element
	 */
	public GeoElement createGeoElement(Construction cons1, String type) {
		return geoFactory.createGeoElement(cons1, type);
	}

	public GeoImplicit newImplicitPoly(Construction cons2) {
		return geoFactory.newImplicitPoly(cons2);
	}

	public ArithmeticFactory getArithmeticFactory() {
		return arithmeticFactory;
	}

	/**
	 * @return factory for GeoElements
	 */
	public GeoFactory getGeoFactory() {
		return geoFactory;
	}

	/**
	 * try to create/update preview for input typed
	 * 
	 * @return preview update scheduler
	 */
	public ScheduledPreviewFromInputBar getInputPreviewHelper() {
		return scheduledPreviewFromInputBar;
	}

	/**
	 * Notify views about preview geos.
	 * 
	 * @param geos
	 *            preview geos
	 */
	public final void notifyUpdatePreviewFromInputBar(GeoElement[] geos) {
		// event dispatcher should not collect calls to stay compatible with 4.0
		if (notifyViewsActive) {
			for (View view : views) {
				view.updatePreviewFromInputBar(geos);
			}
		}
	}

	@Override
	public void specialPointsChanged(SpecialPointsManager manager, List<GeoElement> specialPoints) {
		if (notifyViewsActive) {
			app.getActiveEuclidianView().updateSpecPointFromInputBar(specialPoints);
		}
	}

	/**
	 * @param cons1
	 *            construction
	 * @return construction companion
	 */
	public ConstructionCompanion createConstructionCompanion(
			Construction cons1) {
		return new ConstructionCompanion(cons1);
	}

	public boolean userStopsLoading() {
		return userStopsLoading;
	}

	public void setUserStopsLoading(boolean flag) {
		userStopsLoading = flag;
	}

	/**
	 * Computes precision.
	 * 
	 * @return the size of a unit on the screen in pixels
	 */
	public long precision() {
		EuclidianView ev = this.getLastAttachedEV();
		double evXscale = ev == null ? EuclidianView.SCALE_STANDARD
				: ev.getXscale();
		double evYscale = ev == null ? EuclidianView.SCALE_STANDARD
				: ev.getYscale();
		double scale = evXscale < evYscale ? evXscale : evYscale;
		long p = (long) scale;
		if (p < GeoGebraConstants.PROVER_MIN_PRECISION) {
			p = GeoGebraConstants.PROVER_MIN_PRECISION;
		}
		return p;
	}

	@Override
	public int getCurrentStepNumber() {
		return cons.getStep();
	}

	@Override
	public int getLastStepNumber() {
		return cons.steps();
	}

}
