package org.geogebra.common.kernel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.SetOrientation;
import org.geogebra.common.io.MyXMLHandler;
import org.geogebra.common.kernel.algos.AlgoCasBase;
import org.geogebra.common.kernel.algos.AlgoDependentBoolean;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoIf;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.algos.AlgoVectorPoint;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.Variable;
import org.geogebra.common.kernel.cas.AlgoUsingTempCASalgo;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAxis;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInterval;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoTextField;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.implicit.GeoImplicitPoly;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.optimization.ExtremumFinder;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.util.Exercise;
import org.geogebra.common.util.MaxSizeHashMap;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;

/**
 * Provides methods for computation
 * 
 * @author Markus
 */
public class Kernel {

	/**
	 * Maximal number of spreadsheet columns if these are increased above 32000,
	 * you need to change traceRow to an int[]
	 */
	public static int MAX_SPREADSHEET_COLUMNS_DESKTOP = 9999;
	/** Maximal number of spreadsheet rows */
	public static int MAX_SPREADSHEET_ROWS_DESKTOP = 9999;

	/** string for +- */
	public static String STRING_PLUS_MINUS = "\u00B1 ";
	/** string for -+ */
	public static String STRING_MINUS_PLUS = "\u2213 ";

	// G.Sturr 2009-10-18
	// algebra style
	/** Algebra view style: value */
	final public static int ALGEBRA_STYLE_VALUE = 0;
	/** Algebra view style: definition */
	final public static int ALGEBRA_STYLE_DEFINITION = 1;
	/** Algebra view style: command */
	final public static int ALGEBRA_STYLE_COMMAND = 2;
	private int algebraStyle = Kernel.ALGEBRA_STYLE_VALUE;
	// end G.Sturr
	private MacroManager macroManager;

	/**
	 * Specifies whether possible line breaks are to be marked in the String
	 * representation of {@link ExpressionNode ExpressionNodes}.
	 */
	protected boolean insertLineBreaks = false;

	// angle unit: degree, radians
	// although this is initialized from the default preferences XML,
	// we need to initialize this here too for GeoGebraWeb
	private int angleUnit = Kernel.ANGLE_DEGREE;

	private boolean viewReiniting = false;
	private boolean undoActive = false;

	// Views may register to be informed about
	// changes to the Kernel
	// (add, remove, update)
	// TODO why exactly 20 views?
	/** List of attached views */
	protected ArrayList<View> views = new ArrayList<View>();
	protected boolean addingPolygon = false;
	protected GeoElement newPolygon;
	protected ArrayList<GeoElement> deleteList;
	protected ArrayList<UserAwarenessListener> userAwarenessListeners;
	/** Construction */
	protected Construction cons;
	/** Algebra processor */
	protected AlgebraProcessor algProcessor;
	/** Evaluator for ExpressionNode */
	protected ExpressionNodeEvaluator expressionNodeEvaluator;

	/**
	 * CAS variable handling
	 * 
	 * must start with a letter before 'x' so that variable ordering works in
	 * Giac
	 * 
	 * If this is changed, it also needs changing
	 * 
	 * */
	public static final String TMP_VARIABLE_PREFIX = "ggbtmpvar";

	// Continuity on or off, default: false since V3.0
	private boolean continuous = false;
	public PathRegionHandling usePathAndRegionParameters = PathRegionHandling.ON;
	private GeoGebraCasInterface ggbCAS;
	/** Angle type: radians */
	final public static int ANGLE_RADIANT = 1;
	/** Angle type: degrees */
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
	public static int GEOGEBRA_CAS_CACHE_SIZE = 500;

	// print precision
	public static final int STANDARD_PRINT_DECIMALS = 2;
	// private double PRINT_PRECISION = 1E-2;
	private NumberFormatAdapter nf;
	private final ScientificFormatAdapter sf;
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

	/** minimum precision */
	public final static double MIN_PRECISION = 1E-5;
	private final static double INV_MIN_PRECISION = 1E5;

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
	private boolean resolveUnkownVarsAsDummyGeos = false;

	private boolean updateEVAgain = false; // used for DrawEquationWeb and
											// DrawText in GGW
	private boolean forceUpdatingBoundingBox = false; // used for
														// DrawEquationWeb and
														// DrawText in GGW

	private final StringBuilder sbBuildExplicitLineEquation = new StringBuilder(
			50);
	/** Application */
	protected App app;

	private EquationSolver eqnSolver;
	private SystemOfEquationsSolver sysEqSolv;
	private ExtremumFinder extrFinder;
	/** Parser */
	protected Parser parser;

	/** 3D manager */
	private Manager3DInterface manager3D;

	private Object concurrentModificationLock = new Object();

	/**
	 * @param app
	 *            Application
	 */
	public Kernel(App app) {
		this();
		this.app = app;

		newConstruction();
		getExpressionNodeEvaluator();

		setManager3D(newManager3D(this));
	}

	/**
	 * Creates kernel and initializes number formats and CAS prefix
	 */
	protected Kernel() {
		nf = FormatFactory.prototype.getNumberFormat(2);
		sf = FormatFactory.prototype.getScientificFormat(5, 16, false);
		this.userAwarenessListeners = new ArrayList<UserAwarenessListener>();
		this.deleteList = new ArrayList<GeoElement>();
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
		return new AlgebraProcessor(kernel, app.getCommandDispatcher(kernel));
	}

	/**
	 * @param kernel
	 *            kernel
	 * @return a new 3D manager TODO: reduce visibility after refactoring
	 */
	public Manager3DInterface newManager3D(Kernel kernel) {
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
	 * @Deprecated Please get the app elsewhere! App will be separated from the
	 *             Kernel in the future, instead a class with settings for the
	 *             Kernel will be introduced. - Matthias
	 * @return chaos
	 */
	final public App getApplication() {
		return app;
	}

	final public EquationSolver getEquationSolver() {
		if (eqnSolver == null)
			eqnSolver = new EquationSolver(this);
		return eqnSolver;
	}

	final public SystemOfEquationsSolver getSystemOfEquationsSolver(
			EquationSolverInterface eSolver) {
		if (sysEqSolv == null)
			sysEqSolv = new SystemOfEquationsSolver(eSolver);
		return sysEqSolv;
	}

	final public ExtremumFinder getExtremumFinder() {
		if (extrFinder == null)
			extrFinder = new ExtremumFinder();
		return extrFinder;
	}

	final public Parser getParser() {
		if (parser == null)
			parser = new Parser(this, cons);
		return parser;
	}

	/**
	 * creates the Evaluator for ExpressionNode
	 * 
	 * @return the Evaluator for ExpressionNode
	 */
	public ExpressionNodeEvaluator newExpressionNodeEvaluator(Kernel kernel) {
		return new ExpressionNodeEvaluator(app.getLocalization(), kernel);
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
	 * @return a double comparator which says doubles are equal if their diff is
	 *         less than precision
	 */
	final static public Comparator<Double> DoubleComparator(double precision) {

		final double eps = precision;

		Comparator<Double> ret = new Comparator<Double>() {

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

	// This is a temporary place for abstract adapter methods which will go into
	// factories later
	// Arpad Fekete, 2011-12-01
	// public abstract ColorAdapter getColorAdapter(int red, int green, int
	// blue);

	protected AnimationManager animationManager;

	/*
	 * If the data-param-showAnimationButton parameter for applet is false, be
	 * sure not to show the animation button. In this case the value of
	 * showAnimationButton is false, otherwise true.
	 */
	private boolean showAnimationButton = true;

	public void setShowAnimationButton(boolean showAB) {
		showAnimationButton = showAB;
	}

	final public boolean isAnimationRunning() {
		return animationManager != null && animationManager.isRunning();
	}

	final public boolean isAnimationPaused() {
		return animationManager != null && animationManager.isPaused();
	}

	final public boolean needToShowAnimationButton() {
		if (!showAnimationButton)
			return false;
		return animationManager != null
				&& animationManager.needToShowAnimationButton();
	}

	final public void udpateNeedToShowAnimationButton() {
		if (animationManager != null)
			animationManager.updateNeedToShowAnimationButton();

	}

	/* *******************************************
	 * Methods for MyXMLHandler *******************************************
	 */
	public boolean handleCoords(GeoElement geo,
			LinkedHashMap<String, String> attrs) {

		if (!(geo instanceof GeoVec3D)) {
			App.debug("wrong element type for <coords>: " + geo.getClass());
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

	/* *******************************************
	 * Construction specific methods *******************************************
	 */

	/**
	 * Returns the ConstructionElement for the given GeoElement. If geo is
	 * independent geo itself is returned. If geo is dependent it's parent
	 * algorithm is returned.
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

	/**
	 * @param step
	 *            new construction step
	 */
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
	 * @return
	 */
	public int getLastConstructionStep() {
		return cons.steps() - 1;
	}

	/**
	 * Sets construction step to first step of construction protocol. Note:
	 * showOnlyBreakpoints() is important here
	 */
	public void firstStep() {
		int step = 0;

		if (cons.showOnlyBreakpoints()) {
			setConstructionStep(getNextBreakpoint(step));
		} else {
			setConstructionStep(step);
		}
	}

	/**
	 * Sets construction step to last step of construction protocol. Note:
	 * showOnlyBreakpoints() is important here
	 */
	public void lastStep() {
		int step = getLastConstructionStep();

		if (cons.showOnlyBreakpoints()) {
			setConstructionStep(getPreviousBreakpoint(step));
		} else {
			setConstructionStep(step);
		}
	}

	/**
	 * Sets construction step to next step of construction protocol. Note:
	 * showOnlyBreakpoints() is important here
	 */
	public void nextStep() {
		int step = cons.getStep() + 1;

		if (cons.showOnlyBreakpoints()) {
			setConstructionStep(getNextBreakpoint(step));
		} else {
			setConstructionStep(step);
		}
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

	/**
	 * Sets construction step to previous step of construction protocol Note:
	 * showOnlyBreakpoints() is important here
	 */
	public void previousStep() {
		int step = cons.getStep() - 1;

		if (cons.showOnlyBreakpoints()) {
			cons.setStep(getPreviousBreakpoint(step));
		} else {
			cons.setStep(step);
		}
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
	 * Find geos with caption ending %style=... in this kernel, and set their
	 * visual styles to those geos in the otherKernel which have the same
	 * %style=... caption ending; as well as set %style=defaultStyle for all
	 * geos
	 * 
	 * @param otherKernel
	 */
	public void setVisualStyles(Kernel otherKernel) {
		TreeSet<GeoElement> okts = otherKernel.getConstruction()
				.getGeoSetWithCasCellsConstructionOrder();
		ArrayList<GeoElement> selected = getApplication().getSelectionManager()
				.getSelectedGeos();

		// maybe it's efficient to pre-filter this set to only contain
		// elements that have "%style=" styling

		Iterator<GeoElement> okit = okts.iterator();
		GeoElement okactual;
		String okcapt;
		int okpos;
		while (okit.hasNext()) {
			okactual = okit.next();
			okcapt = okactual.getCaptionSimple();
			if (okcapt == null) {
				okpos = -1;
			} else {
				okpos = okcapt.indexOf("%style=");
			}
			if (okpos < 0) {
				// not having "%style=" setting, can be removed
				// lucky that iterator has this method
				okit.remove();
			}
		}

		// okts is ready, now to the main loop

		Iterator<GeoElement> it;
		if (selected.isEmpty()) {
			it = cons.getGeoSetWithCasCellsConstructionOrder().iterator();
		} else {
			it = selected.iterator();
		}

		GeoElement actual;
		String capt;
		int pos;
		while (it.hasNext()) {
			actual = it.next();

			// at first, apply default styles!
			// these are applied anyway, caption is not needed;
			// however, Geo type is needed!
			GeoClass gc = actual.getGeoClassType();

			okit = okts.iterator();
			while (okit.hasNext()) {
				okactual = okit.next();
				okcapt = okactual.getCaptionSimple();
				// as okts is pre-filtered, okcapt is not null
				okpos = okcapt.indexOf("%style=defaultStyle");
				if (okpos > -1 && okactual.getGeoClassType() == gc) {
					// match!
					actual.setVisualStyle(okactual);
				}
			}
			// now, okit, okactual, okcapt, okpos can be redefined...

			capt = actual.getCaptionSimple();
			if (capt == null) {
				pos = -1;
			} else {
				pos = capt.indexOf("%style=");
			}
			if (pos > -1) {
				// capt will not be needed until the next iteration
				capt = capt.substring(pos);
				// now, it's time to search for geos in otherKernel,
				// whether any of them has the same style ending
				okit = okts.iterator();
				while (okit.hasNext()) {
					okactual = okit.next();
					okcapt = okactual.getCaptionSimple();
					// as okts is pre-filtered, okcapt is not null
					okpos = okcapt.indexOf("%style=");
					// as okts is pre-filtered, okpos is not -1
					// although we could double-check, it's not important

					// okcapt will not be needed until the next iteration
					okcapt = okcapt.substring(okpos);
					if (capt.equals(okcapt)) {
						// match!
						actual.setVisualStyle(okactual);
					}
				}
			}
		}
	}
	
	
	public void setConstructionDefaults(Kernel otherKernel){
		getConstruction().getConstructionDefaults().setConstructionDefaults(
				otherKernel.getConstruction().getConstructionDefaults());
		
		
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

	public void setElementDefaultAllowed(boolean flag) {
		elementDefaultAllowed = flag;
	}

	public boolean getElementDefaultAllowed() {
		return elementDefaultAllowed;
	}

	/**
	 * States whether the continuity heuristic is active.
	 * 
	 * @returns whether continuous mode is on
	 */
	final public boolean isContinuous() {
		return continuous;
	}

	/**
	 * Turns the continuity heuristic on or off. Note: the macro kernel always
	 * turns continuity off.
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
	private boolean loadingMode;

	/**
	 * 
	 * @param b
	 *            true to indicate that file is being loaded
	 */
	public void setLoadingMode(boolean b) {
		loadingMode = b;
	}

	public void notifyOpeningFile(String fileName) {
		for (UserAwarenessListener listener : this.userAwarenessListeners) {
			listener.fileLoading(fileName);
		}
	}

	public void notifyFileOpenComplete(boolean success) {
		for (UserAwarenessListener listener : this.userAwarenessListeners) {
			listener.fileLoadComplete(success);
		}
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

	public void setNotifyConstructionProtocolViewAboutAddRemoveActive(
			boolean flag) {
		notifyConstructionProtocolViewAboutAddRemoveActive = flag;
	}

	public boolean isNotifyConstructionProtocolViewAboutAddRemoveActive() {
		return notifyConstructionProtocolViewAboutAddRemoveActive;
	}

	// see http://code.google.com/p/google-web-toolkit/issues/detail?id=4097
	public final StringBuilder buildImplicitEquation(double[] numbers,
			String[] vars, boolean KEEP_LEADING_SIGN, boolean CANCEL_DOWN,
			boolean needsZ, char op, StringTemplate tpl) {

		sbBuildImplicitEquation.setLength(0);
		double[] temp = buildImplicitVarPart(sbBuildImplicitEquation, numbers,
				vars, KEEP_LEADING_SIGN || (op == '='), CANCEL_DOWN, needsZ,
				tpl);

		sbBuildImplicitEquation.append(' ');
		sbBuildImplicitEquation.append(op);
		sbBuildImplicitEquation.append(' ');

		// temp is set by buildImplicitVarPart
		sbBuildImplicitEquation.append(format(-temp[vars.length], tpl));

		return sbBuildImplicitEquation;
	}

	private StringBuilder sbFormat;

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

	final public void formatSigned(double x, StringBuilder sb,
			StringTemplate tpl) {

		if (x >= 0.0d) {
			sb.append("+ ");
			sb.append(format(x, tpl));
			return;
		}
		sb.append("- ");
		sb.append(format(-x, tpl));
		return;
	}

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

	final public void formatSignedPlusMinus(double x, StringBuilder sb,
			StringTemplate tpl) {

		if (x >= 0.0d) {
			sb.append(STRING_PLUS_MINUS);
			sb.append(format(x, tpl));
			return;
		}
		sb.append(STRING_MINUS_PLUS);
		sb.append(format(-x, tpl));
		return;
	}

	final private String formatPiERaw(double x, NumberFormatAdapter numF,
			StringTemplate tpl) {
		// PI
		if (x == Math.PI && tpl.allowPiHack()) {
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
		if (isEqual(a, aint, AXES_PRECISION)) {
			switch (aint) {
			case 0:
				return "0";

			case 1: // pi/2
				sbFormat.append(tpl.getPi());
				sbFormat.append("/2");
				return sbFormat.toString();

			case -1: // -pi/2
				sbFormat.append('-');
				sbFormat.append(tpl.getPi());
				sbFormat.append("/2");
				return sbFormat.toString();

			case 2: // 2pi/2 = pi
				return tpl.getPi();

			case -2: // -2pi/2 = -pi
				sbFormat.append('-');
				sbFormat.append(tpl.getPi());
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
				return sbFormat.toString();
			}
		}
		// STANDARD CASE
		// use numberformat to get number string
		// checkDecimalFraction() added to avoid 2.19999999999999 when set to
		// 15dp
		String str = numF.format(checkDecimalFraction(x));
		sbFormat.append(str);
		// if number is in scientific notation and ends with "E0", remove this
		if (str.endsWith("E0")) {
			sbFormat.setLength(sbFormat.length() - 2);
		}
		return sbFormat.toString();
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
			}
			// #5149
			return MyDouble.toString(x);

			// number formatting for CAS
		case GIAC:
			if (Double.isNaN(x)) {
				return "?";
			} else if (Double.isInfinite(x)) {
				if (casPrintForm.equals(StringType.GIAC)) {
					return (x < 0) ? "-inf" : "inf";
				}
				return Double.toString(x); // "Infinity" or "-Infinity"
			} else if (isLongInteger) {
				return Long.toString(rounded);
			} else if (Kernel.isZero(x, Kernel.MAX_PRECISION)) {
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
					if (casPrintForm.equals(StringType.GIAC)
							&& ret.indexOf('.') > -1) {
						return StringUtil.wrapInExact(ret);
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
			if (!isLongInteger) {
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
		if (((-printPrecision / 2) <= x) && (x < (printPrecision / 2))) {
			// avoid output of "-0" for eg -0.0004
			return "0";
		}
		// standard case

		// nf = FormatFactory.prototype.getNumberFormat(2);
		NumberFormatAdapter nfa = tpl.getNF(nf);
		return nfa.format(x);
	}

	private StringBuilder formatSB;

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
		// App.printStacktrace(x+"");
		String ret = formatRaw(x, tpl);

		if (app.getLocalization().unicodeZero != '0') {
			ret = internationalizeDigits(ret, tpl);
		}

		return ret;
	}

	/*
	 * swaps the digits in num to the current locale's
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
			formatSB.append(Unicode.RightToLeftMark);
			if (negative) {
				formatSB.append(Unicode.RightToLeftUnaryMinusSign);
				start = 1;
			}
		}

		for (int i = start; i < num.length(); i++) {

			char c = RTL ? num.charAt(num.length() - (negative ? 0 : 1) - i)
					: num.charAt(i);
			if (c == '.') {
				c = getLocalization().unicodeDecimalPoint;
			} else if ((c >= '0') && (c <= '9')) {

				// convert to eg Arabic Numeral
				c += app.getLocalization().unicodeZero - '0';
			}

			formatSB.append(c);
		}

		if (RTL) {
			formatSB.append(Unicode.RightToLeftMark);
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
		if (app.getLocalization().unicodeZero != '0') {

			String num = formatPiERaw(x, numF, tpl);

			return internationalizeDigits(num, tpl);
		}
		return formatPiERaw(x, numF, tpl);
	}

	private final StringBuilder sbBuildImplicitEquation = new StringBuilder(80);

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
	 * change signs of double array values, write result to array b
	 * 
	 * @param a
	 *            input array
	 * @param b
	 *            output array
	 */
	final static void negative(double[] a, double[] b) {
		for (int i = 0; i < a.length; i++) {
			b[i] = -a[i];
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
	 * compares double arrays:
	 * 
	 * @param a
	 *            first array
	 * @param b
	 *            second array
	 * @return true if (isEqual(a[i], b[i]) == true) for all i
	 */
	final static boolean isEqual(double[] a, double[] b) {
		for (int i = 0; i < a.length; ++i) {
			if (!isEqual(a[i], b[i])) {
				return false;
			}
		}
		return true;
	}

	/*
	 * // calc acos(x). returns 0 for x > 1 and pi for x < -1 final static
	 * double trimmedAcos(double x) { if (Math.abs(x) <= 1.0d) return
	 * Math.acos(x); else if (x > 1.0d) return 0.0d; else if (x < -1.0d) return
	 * Math.PI; else return Double.NaN; }
	 */

	/**
	 * Computes max of abs(a[i])
	 * 
	 * @param a
	 *            array of numbers
	 * @return max of abs(a[i])
	 */
	final static double maxAbs(double[] a) {
		double temp, max = Math.abs(a[0]);
		for (int i = 1; i < a.length; i++) {
			temp = Math.abs(a[i]);
			if (temp > max) {
				max = temp;
			}
		}
		return max;
	}

	/**
	 * Builds lhs of lhs = 0
	 * 
	 * @param numbers
	 *            coefficients
	 * @param vars
	 *            variable names
	 * @param KEEP_LEADING_SIGN
	 *            true to keep leading sign
	 * @param CANCEL_DOWN
	 *            true to allow canceling 2x+4y -> x+2y
	 * @param tpl
	 *            string template
	 * @return string representing LHS
	 */
	final public StringBuilder buildLHS(double[] numbers, String[] vars,
			boolean KEEP_LEADING_SIGN, boolean CANCEL_DOWN, boolean needsZ,
			StringTemplate tpl) {
		sbBuildLHS.setLength(0);
		double[] temp = buildImplicitVarPart(sbBuildLHS, numbers, vars,
				KEEP_LEADING_SIGN, CANCEL_DOWN, needsZ, tpl);

		// add constant coeff
		double coeff = temp[vars.length];
		if ((Math.abs(coeff) >= tpl.getPrecision(nf)) || useSignificantFigures) {
			sbBuildLHS.append(' ');
			sbBuildLHS.append(sign(coeff));
			sbBuildLHS.append(' ');
			sbBuildLHS.append(format(Math.abs(coeff), tpl));
		}
		return sbBuildLHS;
	}

	private final StringBuilder sbBuildLHS = new StringBuilder(80);
	private final StringBuilder sbBuildExplicitConicEquation = new StringBuilder(
			80);

	// y = k x + d
	/**
	 * Inverts the > or < sign
	 * 
	 * @param op
	 *            =,<,>,\u2264 or \u2265
	 * @return opposite sign
	 */
	public static char oppositeSign(char op) {
		switch (op) {
		case '=':
			return '=';
		case '<':
			return '>';
		case '>':
			return '<';
		case '\u2264':
			return '\u2265';
		case '\u2265':
			return '\u2264';
		default:
			return '?'; // should never happen
		}
	}

	// lhs of implicit equation without constant coeff
	final private double[] buildImplicitVarPart(
			StringBuilder sbBuildImplicitVarPart, double[] numbers,
			String[] vars, boolean KEEP_LEADING_SIGN, boolean CANCEL_DOWN,
			boolean needsZ, StringTemplate tpl) {

		double[] temp = new double[numbers.length];

		int leadingNonZero = -1;
		sbBuildImplicitVarPart.setLength(0);

		for (int i = 0; i < vars.length; i++) {
			if (!isZero(numbers[i])) {
				leadingNonZero = i;
				break;
			}
		}

		if (CANCEL_DOWN) {
			// check if integers and divide through gcd
			boolean allIntegers = true;
			for (int i = 0; i < numbers.length; i++) {
				allIntegers = allIntegers && isInteger(numbers[i]);
			}
			if (allIntegers) {
				// divide by greates common divisor
				divide(numbers, gcd(numbers), numbers);
			}
		}

		// no left hand side
		if (leadingNonZero == -1) {
			sbBuildImplicitVarPart.append("0");
			return temp;
		}

		// don't change leading coefficient
		if (KEEP_LEADING_SIGN) {
			copy(numbers, temp);
		} else {
			if (numbers[leadingNonZero] < 0) {
				negative(numbers, temp);
			} else {
				copy(numbers, temp);
			}
		}

		// BUILD EQUATION STRING
		// valid left hand side
		// leading coefficient
		String strCoeff = formatCoeff(temp[leadingNonZero], tpl);
		sbBuildImplicitVarPart.append(strCoeff);
		sbBuildImplicitVarPart.append(vars[leadingNonZero]);

		// other coefficients on lhs
		String sign;
		double abs;
		for (int i = leadingNonZero + 1; i < vars.length; i++) {
			if (temp[i] < 0.0) {
				sign = " - ";
				abs = -temp[i];
			} else {
				sign = " + ";
				abs = temp[i];
			}

			if ((abs >= tpl.getPrecision(nf)) || useSignificantFigures
					|| (needsZ && i == 2)) {
				sbBuildImplicitVarPart.append(sign);
				sbBuildImplicitVarPart.append(formatCoeff(abs, tpl));
				sbBuildImplicitVarPart.append(vars[i]);
			}
		}
		return temp;
	}

	// private final StringBuilder sbBuildImplicitVarPart = new
	// StringBuilder(80);

	/**
	 * 
	 * @param x
	 *            value
	 * @param tpl
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
	 * @param KEEP_LEADING_SIGN
	 *            whether leading sign should be kept
	 * @param tpl
	 *            string template
	 * @return explicit equation of conic
	 */
	public final StringBuilder buildExplicitConicEquation(double[] numbers,
			String[] vars, int pos, boolean KEEP_LEADING_SIGN,
			StringTemplate tpl) {
		// y^2-coeff is 0
		double d, dabs, q = numbers[pos];
		// coeff of y^2 is 0 or coeff of y is not 0
		if (isZero(q)) {
			return buildImplicitEquation(numbers, vars, KEEP_LEADING_SIGN,
					true, false, '=', tpl);
		}

		int i, leadingNonZero = numbers.length;
		for (i = 0; i < numbers.length; i++) {
			if ((i != pos) && // except y^2 coefficient
					((Math.abs(numbers[i]) >= tpl.getPrecision(nf)) || useSignificantFigures)) {
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
					if ((dabs >= tpl.getPrecision(nf)) || useSignificantFigures) {
						sbBuildExplicitConicEquation.append(' ');
						sbBuildExplicitConicEquation.append(sign(d));
						sbBuildExplicitConicEquation.append(' ');
						sbBuildExplicitConicEquation.append(formatCoeff(dabs,
								tpl));
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
		String absStr;
		if (x == 0) {
			// avoid output of "-0.00"
			absStr = sfa.format(0);
		} else if (x > 0) {
			absStr = sfa.format(x);
		} else {
			sbFormatSF.append('-');
			absStr = sfa.format(-x);
		}

		// make sure ".123" is returned as "0.123".
		if (absStr.charAt(0) == '.') {
			sbFormatSF.append('0');
		}
		sbFormatSF.append(absStr);
		return sbFormatSF.toString();
	}

	private StringBuilder sbFormatSF;

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
	private final String formatCoeff(double x, StringTemplate tpl) { // TODO
																		// make
																		// private
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

	public final StringBuilder buildExplicitLineEquation(double[] numbers,
			String[] vars, char opDefault, StringTemplate tpl) {
		char op = opDefault;
		double d, dabs, q = numbers[1];
		sbBuildExplicitLineEquation.setLength(0);

		// BUILD EQUATION STRING
		// special case
		// y-coeff is 0: form x = constant
		if (isZero(q)) {
			sbBuildExplicitLineEquation.append(vars[0]);

			sbBuildExplicitLineEquation.append(' ');
			if (numbers[0] < MIN_PRECISION) {
				op = oppositeSign(op);
			}
			sbBuildExplicitLineEquation.append(op);
			sbBuildExplicitLineEquation.append(' ');

			sbBuildExplicitLineEquation.append(format(-numbers[2] / numbers[0],
					tpl));
			return sbBuildExplicitLineEquation;
		}

		// standard case: y-coeff not 0
		sbBuildExplicitLineEquation.append(vars[1]);

		sbBuildExplicitLineEquation.append(' ');
		if (numbers[1] < MIN_PRECISION) {
			op = oppositeSign(op);
		}
		sbBuildExplicitLineEquation.append(op);
		sbBuildExplicitLineEquation.append(' ');

		// x coeff
		d = -numbers[0] / q;
		dabs = Math.abs(d);
		if ((dabs >= tpl.getPrecision(nf)) || useSignificantFigures) {
			sbBuildExplicitLineEquation.append(formatCoeff(d, tpl));
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

	/**
	 * if x is nearly zero, 0.0 is returned, else x is returned
	 */
	final public static double chop(double x) {
		if (isZero(x)) {
			return 0.0d;
		}
		return x;
	}

	/** is abs(x) < epsilon ? */
	final public static boolean isZero(double x) {
		return (-STANDARD_PRECISION < x) && (x < STANDARD_PRECISION);
	}

	/** is abs(x) < epsilon ? */
	final public static boolean isZero(double x, double eps) {
		return (-eps < x) && (x < eps);
	}

	/**
	 * 
	 * check if a point is zero, see #5202
	 * 
	 * @param e
	 * @param x
	 * @param y
	 * @return
	 */
	final public static boolean isEpsilon(double e, double x, double y) {

		double eAbs = Math.abs(e);

		if (eAbs > STANDARD_PRECISION) {
			return false;
		}

		if (eAbs > Math.abs(x) * STANDARD_PRECISION) {
			return false;
		}

		if (eAbs > Math.abs(y) * STANDARD_PRECISION) {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * check if a point is zero, see #5202
	 * 
	 * @param e
	 * @param x
	 * @param y
	 * @param z
	 * 
	 * @return
	 */
	final public static boolean isEpsilon(double e, double x, double y, double z) {

		double eAbs = Math.abs(e);

		if (eAbs > STANDARD_PRECISION) {
			return false;
		}

		if (eAbs > Math.abs(x) * STANDARD_PRECISION) {
			return false;
		}

		if (eAbs > Math.abs(y) * STANDARD_PRECISION) {
			return false;
		}

		if (eAbs > Math.abs(z) * STANDARD_PRECISION) {
			return false;
		}

		return true;
	}

	/**
	 * @param a
	 *            array of numbers
	 * @return whether all given numbers are zero within current precision
	 */
	final static boolean isZero(double[] a) {
		for (int i = 0; i < a.length; i++) {
			if (!isZero(a[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param x
	 *            number
	 * @return whether fractional part of the number is zero within current
	 *         precision (false for +/-Infinity, NaN
	 */
	final public static boolean isInteger(double x) {

		if (Double.isInfinite(x) || Double.isNaN(x)) {
			return false;
		}

		if (x > 1E17 || x < -1E17) {
			return true;
		}
		return isEqual(x, Math.round(x));
	}

	/**
	 * Returns whether x is equal to y infinity == infinity returns true eg 1/0
	 * -infinity == infinity returns false eg -1/0 -infinity == -infinity
	 * returns true undefined == undefined returns false eg 0/0
	 */
	final public static boolean isEqual(double x, double y) {
		if (x == y) {
			return true;
		}
		return ((x - STANDARD_PRECISION) <= y)
				&& (y <= (x + STANDARD_PRECISION));
	}

	final public static boolean isEqual(double x, double y, double eps) {
		if (x == y) {
			return true;
		}
		return ((x - eps) < y) && (y < (x + eps));
	}

	/**
	 * Returns whether x is greater than y
	 */
	final public static boolean isGreater(double x, double y) {
		return x > (y + STANDARD_PRECISION);
	}

	/**
	 * 
	 * @param x
	 *            first value
	 * @param y
	 *            second value
	 * @return 0 if x ~ y ; -1 if x < y ; 1 if x > y
	 */
	final public static int compare(double x, double y) {
		if (isGreater(x, y)) {
			return 1;
		}

		if (isGreater(y, x)) {
			return -1;
		}

		return 0;
	}

	/**
	 * Returns whether x is greater than y
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param eps
	 *            tolerance
	 * @return true if x > y + eps
	 */
	final public static boolean isGreater(double x, double y, double eps) {
		return x > (y + eps);
	}

	/**
	 * Returns whether x is greater than or equal to y
	 */
	final public static boolean isGreaterEqual(double x, double y) {
		return (x + STANDARD_PRECISION) > y;
	}

	final public static double convertToAngleValue(double val) {
		if ((val > STANDARD_PRECISION) && (val < PI_2)) {
			return val;
		}

		double value = val % PI_2;
		if (isZero(value)) {
			if (val < 1.0) {
				value = 0.0;
			} else {
				value = PI_2;
			}
		} else if (value < 0.0) {
			value += PI_2;
		}
		return value;
	}

	// //////////////////////////////////////////////
	// FORMAT FOR NUMBERS
	// //////////////////////////////////////////////

	/**
	 * Checks if x is close (Kernel.MIN_PRECISION) to a decimal fraction, eg
	 * 2.800000000000001. If it is, the decimal fraction eg 2.8 is returned,
	 * otherwise x is returned.
	 * 
	 * @param x
	 *            input number
	 * @param precision
	 *            specifies how many decimals digits are accepted in results --
	 *            e.g. 0.001 to allow three digits
	 * @return input number; rounded with given precision if the rounding error
	 *         is less than this kernel's minimal precision
	 */

	final public static double checkDecimalFraction(double x, double precision) {

		double prec = precision;
		// Application.debug(precision+" ");
		prec = Math
				.pow(10, Math.floor(Math.log(Math.abs(prec)) / Math.log(10)));

		double fracVal = x * INV_MIN_PRECISION;
		double roundVal = Math.round(fracVal);
		// Application.debug(precision+" "+x+" "+fracVal+" "+roundVal+" "+isEqual(fracVal,
		// roundVal, precision)+" "+roundVal / INV_MIN_PRECISION);
		if (isEqual(fracVal, roundVal, STANDARD_PRECISION * prec)) {
			return roundVal / INV_MIN_PRECISION;
		}
		return x;
	}

	final public static double checkDecimalFraction(double x) {
		return checkDecimalFraction(x, 1);
	}

	/**
	 * Checks if x is very close (1E-8) to an integer. If it is, the integer
	 * value is returned, otherwise x is returnd.
	 */
	final public static double checkInteger(double x) {
		double roundVal = Math.round(x);
		if (Math.abs(x - roundVal) < STANDARD_PRECISION) {
			return roundVal;
		}
		return x;
	}

	/**
	 * Returns formated angle (in degrees if necessary)
	 * 
	 * @param phi
	 *            angle in radians
	 * @param tpl
	 *            string template
	 * @return formated angle
	 */
	final public StringBuilder formatAngle(double phi, StringTemplate tpl,
			boolean unbounded) {
		// STANDARD_PRECISION * 10 as we need a little leeway as we've converted
		// from radians
		StringBuilder ret = formatAngle(phi, 10, tpl, unbounded);

		return ret;
	}

	final public StringBuilder formatAngle(double alpha, double precision,
			StringTemplate tpl, boolean unbounded) {
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

			if (getAngleUnit() == ANGLE_DEGREE) {
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
				phi = checkInteger(phi);

				if (!unbounded) {
					if (phi < 0) {
						phi += 360;
					} else if (phi > 360) {
						phi = phi % 360;
					}
				}
				// STANDARD_PRECISION * 10 as we need a little leeway as we've
				// converted from radians
				sbFormatAngle.append(format(
						checkDecimalFraction(phi, precision), tpl));

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
			// RADIANS
			sbFormatAngle.append(format(phi, tpl));

			if (!tpl.hasType(StringType.GEOGEBRA_XML)) {
				sbFormatAngle.append(" rad");
			}
			return sbFormatAngle;
		}

	}

	/** default global JavaScript */
	final public static String defaultLibraryJavaScript = "function ggbOnInit() {}";

	private String libraryJavaScript = defaultLibraryJavaScript;

	/** Resets global JavaSrcript to default value */
	public void resetLibraryJavaScript() {
		setLibraryJavaScript(defaultLibraryJavaScript);
	}

	public void setLibraryJavaScript(String str) {
		App.debug(str);
		libraryJavaScript = str;

		// libraryJavaScript =
		// "function ggbOnInit() {ggbApplet.evalCommand('A=(1,2)');ggbApplet.registerObjectUpdateListener('A','listener');}function listener() {//java.lang.System.out.println('add listener called'); var x = ggbApplet.getXcoord('A');var y = ggbApplet.getYcoord('A');var len = Math.sqrt(x*x + y*y);if (len > 5) { x=x*5/len; y=y*5/len; }ggbApplet.unregisterObjectUpdateListener('A');ggbApplet.setCoords('A',x,y);ggbApplet.registerObjectUpdateListener('A','listener');}";
		// libraryJavaScript =
		// "function ggbOnInit() {ggbApplet.evalCommand('A=(1,2)');}";

		app.getScriptManager().setGlobalScript();
	}

	// public String getLibraryJavaScriptXML() {
	// return Util.encodeXML(libraryJavaScript);
	// }

	public String getLibraryJavaScript() {
		return libraryJavaScript;
	}

	/** return all points of the current construction */
	public TreeSet<GeoElement> getPointSet() {
		return getConstruction().getGeoSetLabelOrder(GeoClass.POINT);
	}

	/*******************************************************
	 * SAVING
	 *******************************************************/

	private boolean isSaving;

	public synchronized boolean isSaving() {
		return isSaving;
	}

	public synchronized void setSaving(boolean saving) {
		isSaving = saving;
	}

	private final StringBuilder sbFormatAngle = new StringBuilder(40);

	private boolean arcusFunctionCreatesAngle;

	public void setInverseTrigReturnsAngle(boolean selected) {
		arcusFunctionCreatesAngle = selected;
	}

	public boolean getInverseTrigReturnsAngle() {
		return arcusFunctionCreatesAngle;
	}

	final public void setAngleUnit(int unit) {
		angleUnit = unit;
	}

	final public int getAngleUnit() {
		return angleUnit;
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
	 * @return String where CAS variable prefixes are removed again, e.g.
	 *         "ggbcasvar1a" is turned into "a" and
	 */
	final public static String removeCASVariablePrefix(final String str,
			final String replace) {
		// e.g. "ggbtmpvar1a" is changed to "a"
		// need a space when called from GeoGebraCAS.evaluateGeoGebraCAS()
		// so that eg Derivative[1/(-x+E2)] works (want 2 E2 not 2E2) #1595,
		// #1616

		// e.g. "ggbtmpvara" needs to be changed to "a"
		return str.replace(TMP_VARIABLE_PREFIX, replace);
	}

	final public void setPrintFigures(int figures) {
		if (figures >= 0) {
			useSignificantFigures = true;
			sf.setSigDigits(figures);
			sf.setMaxWidth(16); // for scientific notation
		}
	}

	final public void setPrintDecimals(int decimals) {
		if (decimals >= 0) {
			useSignificantFigures = false;
			nf = FormatFactory.prototype.getNumberFormat(decimals);
		}
	}

	final public int getPrintDecimals() {
		if (nf == null)
			return 5;
		return nf.getMaximumFractionDigits();
	}

	/*
	 * returns number of significant digits, or -1 if using decimal places
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

	public final boolean isAllowVisibilitySideEffects() {
		return allowVisibilitySideEffects;
	}

	public final void setAllowVisibilitySideEffects(
			boolean allowVisibilitySideEffects) {
		this.allowVisibilitySideEffects = allowVisibilitySideEffects;
	}

	public boolean isMacroKernel() {
		return false;
	}

	/**
	 * Returns whether silent mode is turned on.
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
	 */
	public final void setSilentMode(boolean silentMode) {

		this.silentMode = silentMode;

		// no new labels, no adding to construction list
		getConstruction().setSuppressLabelCreation(silentMode);

		// no notifying of views
		// ggb3D - 2009-07-17
		// removing :
		// notifyViewsActive = !silentMode;
		// (seems not to work with loading files)

		// Application.printStacktrace(""+silentMode);

	}

	/**
	 * Sets whether unknown variables should be resolved as GeoDummyVariable
	 * objects.
	 */
	public final void setResolveUnkownVarsAsDummyGeos(
			boolean resolveUnkownVarsAsDummyGeos) {
		this.resolveUnkownVarsAsDummyGeos = resolveUnkownVarsAsDummyGeos;
	}

	/**
	 * Returns whether unkown variables are resolved as GeoDummyVariable
	 * objects.
	 * 
	 * @see #setSilentMode(boolean)
	 */
	public final boolean isResolveUnkownVarsAsDummyGeos() {
		return resolveUnkownVarsAsDummyGeos;
	}

	/**
	 * @param casString
	 *            String to evaluate
	 * @param arbconst
	 *            arbitrary constant
	 * @return result string (null possible)
	 * @throws Throwable
	 */
	public String evaluateGeoGebraCAS(String casString,
			MyArbitraryConstant arbconst) throws Throwable {
		return evaluateGeoGebraCAS(casString, arbconst,
				StringTemplate.numericNoLocal);
	}

	/**
	 * Evaluates an expression in GeoGebraCAS syntax.
	 * 
	 * @return result string (null possible)
	 * @throws Throwable
	 */
	final public String evaluateGeoGebraCAS(String exp,
			MyArbitraryConstant arbconst, StringTemplate tpl) throws Throwable {
		return evaluateGeoGebraCAS(exp, false, arbconst, tpl);
	}

	/**
	 * Evaluates an expression in GeoGebraCAS syntax where the cache or previous
	 * evaluations is used. Make sure to only use this method when exp only
	 * includes values and no (used) variable names.
	 * 
	 * @return result string (null possible)
	 * @throws Throwable
	 */
	final public String evaluateCachedGeoGebraCAS(String exp,
			MyArbitraryConstant arbconst) throws Throwable {
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
	 */
	private String evaluateGeoGebraCAS(String exp, boolean useCaching,
			MyArbitraryConstant arbconst, StringTemplate tpl) throws Throwable {
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

	public void evaluateGeoGebraCASAsync(AsynchronousCommand c) {
		String result = null;
		String exp = c.getCasInput();
		if (c.useCacheing() && hasCasCache()) {
			result = getCasCache().get(exp);
			if (result != null) {
				// caching worked
				c.handleCASoutput(result, exp.hashCode());
				return;
			}
		}

		// evaluate in GeoGebraCAS
		getGeoGebraCAS().evaluateGeoGebraCASAsync(c);
	}

	public void putToCasCache(String exp, String result) {
		getCasCache().put(exp, result);
	}

	/**
	 * G.Sturr 2009-10-18
	 * 
	 * @param style
	 *            Algebra style, see ALGEBRA_STYLE_*
	 */
	final public void setAlgebraStyle(int style) {
		algebraStyle = style;
	}

	/**
	 * @return algebra style, one of ALGEBRA_STYLE_*
	 */
	final public int getAlgebraStyle() {
		return algebraStyle;
	}

	private MaxSizeHashMap<String, String> ggbCasCache;

	/**
	 * @return Hash map for caching CAS results.
	 */
	public MaxSizeHashMap<String, String> getCasCache() {
		if (ggbCasCache == null) {
			ggbCasCache = new MaxSizeHashMap<String, String>(
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

	protected double[] xmin = new double[1], xmax = new double[1],
			ymin = new double[1], ymax = new double[1], xscale = new double[1],
			yscale = new double[1];
	// for 2nd Graphics View

	private boolean graphicsView2showing = false;

	/**
	 * Tells this kernel about the bounds and the scales for x-Axis and y-Axis
	 * used in EudlidianView. The scale is the number of pixels per unit.
	 * (useful for some algorithms like findminimum). All
	 * 
	 * @param view
	 *            view
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
			double xmax, double ymin, double ymax, double xscale, double yscale) {
		int view = viewNo - 1;

		if (view < 0 || viewNo < 0) {
			return;
		}

		if (view >= this.xmin.length) {

			this.xmin = prolong(this.xmin, viewNo);
			this.xmax = prolong(this.xmin, viewNo);

			this.ymin = prolong(this.ymin, viewNo);
			this.ymax = prolong(this.ymax, viewNo);

			this.xscale = prolong(this.xscale, viewNo);
			this.yscale = prolong(this.yscale, viewNo);
		}
		this.xmin[view] = xmin;
		this.xmax[view] = xmax;
		this.ymin[view] = ymin;
		this.ymax[view] = ymax;
		this.xscale[view] = xscale;
		this.yscale[view] = yscale;

		graphicsView2showing = getApplication().isShowingMultipleEVs();
		notifyEuclidianViewCE();
	}

	protected double[] prolong(double[] xmin2, int viewNo) {

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
	public double getViewsXMin(GeoElement geo) {
		return getViewBoundsForGeo(geo)[0];
	}

	/**
	 * @see #getViewBoundsForGeo
	 * @param geo
	 *            geo
	 * @return maximal x-bound of all views displaying geo
	 */
	public double getViewsXMax(GeoElement geo) {
		return getViewBoundsForGeo(geo)[1];
	}

	/**
	 * @see #getViewBoundsForGeo
	 * @param geo
	 *            geo
	 * @return minimal y-bound of all views displaying geo
	 */
	public double getViewsYMin(GeoElement geo) {
		return getViewBoundsForGeo(geo)[2];
	}

	/**
	 * @see #getViewBoundsForGeo
	 * @param geo
	 *            geo
	 * @return maximal y-bound of all views displaying geo
	 */
	public double getViewsYMax(GeoElement geo) {
		return getViewBoundsForGeo(geo)[3];
	}

	public double getViewsXScale(GeoElement geo) {
		return getViewBoundsForGeo(geo)[4];
	}

	public double getViewsYScale(GeoElement geo) {
		return getViewBoundsForGeo(geo)[5];
	}

	public void notifyEuclidianViewCE() {
		if (macroManager != null)
			macroManager.notifyEuclidianViewCE();

		cons.notifyEuclidianViewCE(false);
	}

	/**
	 * @param clearScripts
	 *            (true when called from File -> New, false after loading a file
	 *            otherwise the GlobalJavascript is wrongly deleted)
	 */
	public void clearConstruction(boolean clearScripts) {

		if (clearScripts) {
			resetLibraryJavaScript();

			// This needs to happen *before* cons.clearConstruction() is called
			// as clearConstruction calls notifyClearView which triggers the
			// updating of the Python Script
			// resetLibraryPythonScript();
		}
		if (this.ggbCAS != null) {
			this.ggbCAS.getCurrentCAS().clearResult();
		}
		if (macroManager != null)
			macroManager.setAllMacrosUnused();

		// clear animations
		if (animationManager != null) {
			animationManager.stopAnimation();
			animationManager.clearAnimatedGeos();
		}

		cons.clearConstruction();
		notifyClearView();
		notifyRepaint();

	}

	public double getXmax() {
		if (graphicsView2showing) {
			return MyMath.max(xmax);
		}
		return xmax[0];
	}

	public double getXmin() {
		if (graphicsView2showing) {
			return MyMath.min(xmin);
		}
		return xmin[0];
	}

	public double getXscale() {
		if (graphicsView2showing) {
			// xscale = pixel per unit
			// higher xscale means more pixels per unit, i.e. higher precision
			return MyMath.max(xscale);
		}
		return xscale[0];

	}

	public double getYmax() {
		if (graphicsView2showing) {
			return MyMath.max(ymax);
		}
		return ymax[0];
	}

	public double getYmin() {
		if (graphicsView2showing) {
			return MyMath.min(ymin);
		}
		return ymin[0];
	}

	public double getYscale() {
		if (graphicsView2showing) {
			// yscale = pixel per unit
			// higher xscale means more pixels per unit, i.e. higher precision
			return MyMath.max(yscale);
		}
		return yscale[0];
	}

	public double getXmax(boolean ev1, boolean ev2) {
		if (ev2 && !ev1) {
			return xmax[1];
		} else if (ev1 && !ev2) {
			return xmax[0];
		}
		return getXmax();
	}

	final public double getXmax(int i) {
		return xmax[i];
	}

	final public double getXmin(int i) {
		return xmin[i];
	}

	final public double getYmax(int i) {
		return ymax[i];
	}

	final public double getYmin(int i) {
		return ymin[i];
	}

	final public double getYscale(int i) {
		return yscale[i];
	}

	final public double getXscale(int i) {
		return xscale[i];
	}

	/**
	 * @param i
	 *            used in 3D
	 */
	public double getZmax(int i) {
		return 0;
	}

	/**
	 * @param i
	 *            used in 3D
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

	public double getXmin(boolean ev1, boolean ev2) {
		if (ev2 && !ev1) {
			return xmin[1];
		} else if (ev1 && !ev2) {
			return xmin[0];
		}
		return getXmin();
	}

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

	public double getYmax(boolean ev1, boolean ev2) {
		if (ev2 && !ev1) {
			return ymax[1];
		} else if (ev1 && !ev2) {
			return ymax[0];
		}
		return getYmax();
	}

	public double getYmin(boolean ev1, boolean ev2) {
		if (ev2 && !ev1) {
			return ymin[1];
		} else if (ev1 && !ev2) {
			return ymin[0];
		}
		return getYmin();
	}

	public double getYscale(boolean ev1, boolean ev2) {
		if (ev2 && !ev1) {
			return yscale[1];
		} else if (ev1 && !ev2) {
			return yscale[0];
		}
		return getYscale();
	}

	public synchronized GeoGebraCasInterface getGeoGebraCAS() {
		if (ggbCAS == null) {
			ggbCAS = new GeoGebraCAS(this);
		}

		return ggbCAS;
	}

	final public int getCoordStyle() {
		return coordStyle;
	}

	public void setCoordStyle(int coordStlye) {
		coordStyle = coordStlye;
	}

	/**
	 * Returns a GeoElement for the given label.
	 * 
	 * @return may return null
	 */
	final public GeoElement lookupLabel(String label) {
		return lookupLabel(label, false, isResolveUnkownVarsAsDummyGeos());
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
	 * @return GeoElement with given label
	 */
	final public GeoElement lookupLabel(String label, boolean autoCreate, boolean useDummies) {
		GeoElement geo = cons.lookupLabel(label, autoCreate);

		if ((geo == null) && useDummies) {
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

	public GeoClass getClassType(String type) throws MyError {
		switch (type.charAt(0)) {
		case 'a': // angle
			return GeoClass.ANGLE;

		case 'b': // angle
			if (type.equals("boolean")) {
				return GeoClass.BOOLEAN;
			}
			return GeoClass.BUTTON; // "button"

		case 'c': // conic
			if (type.equals("conic")) {
				return GeoClass.CONIC;
			} else if (type.equals("conicpart")) {
				return GeoClass.CONICPART;
			} else if (type.equals("circle")) { // bug in GeoGebra 2.6c
				return GeoClass.CONIC;
			}

		case 'd': // doubleLine // bug in GeoGebra 2.6c
			return GeoClass.CONIC;

		case 'e': // ellipse, emptyset // bug in GeoGebra 2.6c
			return GeoClass.CONIC;

		case 'f': // function
			return GeoClass.FUNCTION;

		case 'h': // hyperbola // bug in GeoGebra 2.6c
			return GeoClass.CONIC;

		case 'i': // image,implicitpoly
			if (type.equals("image")) {
				return GeoClass.IMAGE;
			} else if (type.equals("intersectinglines")) {
				return GeoClass.CONIC;
			} else if (type.equals("implicitpoly")) {
				return GeoClass.IMPLICIT_POLY;
			}

		case 'l': // line, list, locus
			if (type.equals("line")) {
				return GeoClass.LINE;
			} else if (type.equals("list")) {
				return GeoClass.LIST;
			} else {
				return GeoClass.LOCUS;
			}

		case 'n': // numeric
			return GeoClass.NUMERIC;

		case 'p': // point, polygon
			if (type.equals("point")) {
				return GeoClass.POINT;
			} else if (type.equals("polygon")) {
				return GeoClass.POLYGON;
			} else if (type.equals("polyline")) {
				return GeoClass.POLYLINE;
			} else if (type.equals("penstroke")) {
				return GeoClass.PENSTROKE;
			} else {
				return GeoClass.CONIC;
			}

		case 'r': // ray
			return GeoClass.RAY;

		case 's': // segment
			return GeoClass.SEGMENT;

		case 't':
			if (type.equals("text")) {
				return GeoClass.TEXT; // text
			}
			return GeoClass.TEXTFIELD; // textfield

		case 'v': // vector
			return GeoClass.VECTOR;

		default:
			throw new MyError(cons.getApplication().getLocalization(),
					"Kernel: GeoElement of type " + type
							+ " could not be created.");
		}
	}

	/* *******************************************
	 * Methods for EuclidianView/EuclidianView3D
	 * *******************************************
	 */

	public String getModeText(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			return "Select";

		case EuclidianConstants.MODE_MOVE:
			return "Move";

		case EuclidianConstants.MODE_POINT:
			return "Point";

		case EuclidianConstants.MODE_COMPLEX_NUMBER:
			return "ComplexNumber";

		case EuclidianConstants.MODE_POINT_ON_OBJECT:
			return "PointOnObject";

		case EuclidianConstants.MODE_JOIN:
			return "Join";

		case EuclidianConstants.MODE_SEGMENT:
			return "Segment";

		case EuclidianConstants.MODE_SEGMENT_FIXED:
			return "SegmentFixed";

		case EuclidianConstants.MODE_RAY:
			return "Ray";

		case EuclidianConstants.MODE_POLYGON:
			return "Polygon";

		case EuclidianConstants.MODE_POLYLINE:
			return "PolyLine";

		case EuclidianConstants.MODE_RIGID_POLYGON:
			return "RigidPolygon";

		case EuclidianConstants.MODE_VECTOR_POLYGON:
			return "VectorPolygon";

		case EuclidianConstants.MODE_PARALLEL:
			return "Parallel";

		case EuclidianConstants.MODE_ORTHOGONAL:
			return "Orthogonal";

		case EuclidianConstants.MODE_INTERSECT:
			return "Intersect";

		case EuclidianConstants.MODE_INTERSECTION_CURVE:
			return "IntersectionCurve";

		case EuclidianConstants.MODE_LINE_BISECTOR:
			return "LineBisector";

		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			return "AngularBisector";

		case EuclidianConstants.MODE_TANGENTS:
			return "Tangent";

		case EuclidianConstants.MODE_POLAR_DIAMETER:
			return "PolarDiameter";

		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
			return "Circle2";

		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			return "Circle3";

		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
			return "Ellipse3";

		case EuclidianConstants.MODE_PARABOLA:
			return "Parabola";

		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			return "Hyperbola3";

			// Michael Borcherds 2008-03-13
		case EuclidianConstants.MODE_COMPASSES:
			return "Compasses";

		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			return "Conic5";

		case EuclidianConstants.MODE_RELATION:
			return "Relation";

		case EuclidianConstants.MODE_TRANSLATEVIEW:
			return "TranslateView";

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			return "ShowHideObject";

		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			return "ShowHideLabel";

		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			return "CopyVisualStyle";

		case EuclidianConstants.MODE_DELETE:
			return "Delete";

		case EuclidianConstants.MODE_VECTOR:
			return "Vector";

		case EuclidianConstants.MODE_TEXT:
			return "Text";

		case EuclidianConstants.MODE_IMAGE:
			return "Image";

		case EuclidianConstants.MODE_MIDPOINT:
			return "Midpoint";

		case EuclidianConstants.MODE_SEMICIRCLE:
			return "Semicircle";

		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			return "CircleArc3";

		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			return "CircleSector3";

		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			return "CircumcircleArc3";

		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			return "CircumcircleSector3";

		case EuclidianConstants.MODE_SLIDER:
			return "Slider";

		case EuclidianConstants.MODE_MIRROR_AT_POINT:
			return "MirrorAtPoint";

		case EuclidianConstants.MODE_MIRROR_AT_LINE:
			return "MirrorAtLine";

		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
			return "MirrorAtCircle";

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return "TranslateByVector";

		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			return "RotateByAngle";

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			return "DilateFromPoint";

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
			return "CirclePointRadius";

		case EuclidianConstants.MODE_ANGLE:
			return "Angle";

		case EuclidianConstants.MODE_ANGLE_FIXED:
			return "AngleFixed";

		case EuclidianConstants.MODE_VECTOR_FROM_POINT:
			return "VectorFromPoint";

		case EuclidianConstants.MODE_DISTANCE:
			return "Distance";

		case EuclidianConstants.MODE_MOVE_ROTATE:
			return "MoveRotate";

		case EuclidianConstants.MODE_ZOOM_IN:
			return "ZoomIn";

		case EuclidianConstants.MODE_ZOOM_OUT:
			return "ZoomOut";

		case EuclidianConstants.MODE_LOCUS:
			return "Locus";

		case EuclidianConstants.MODE_AREA:
			return "Area";

		case EuclidianConstants.MODE_SLOPE:
			return "Slope";

		case EuclidianConstants.MODE_REGULAR_POLYGON:
			return "RegularPolygon";

		case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
			return "ShowCheckBox";

		case EuclidianConstants.MODE_BUTTON_ACTION:
			return "ButtonAction";

		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			return "TextFieldAction";

		case EuclidianConstants.MODE_PEN:
			return "Pen";

			// case EuclidianConstants.MODE_PENCIL:
			// return "Pencil";

		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			return "FreehandShape";

			// case EuclidianConstants.MODE_VISUAL_STYLE:
			// return "VisualStyle";

		case EuclidianConstants.MODE_FREEHAND_CIRCLE:
			return "FreehandCircle";

		case EuclidianConstants.MODE_FITLINE:
			return "FitLine";

		case EuclidianConstants.MODE_CREATE_LIST:
			return "CreateListGraphicsView";

		case EuclidianConstants.MODE_PROBABILITY_CALCULATOR:
			return "ProbabilityCalculator";

		case EuclidianConstants.MODE_FUNCTION_INSPECTOR:
			return "FunctionInspector";

			// CAS
		case EuclidianConstants.MODE_CAS_EVALUATE:
			return "Evaluate";

		case EuclidianConstants.MODE_CAS_NUMERIC:
			return "Numeric";

		case EuclidianConstants.MODE_CAS_KEEP_INPUT:
			return "KeepInput";

		case EuclidianConstants.MODE_CAS_EXPAND:
			return "Expand";

		case EuclidianConstants.MODE_CAS_FACTOR:
			return "Factor";

		case EuclidianConstants.MODE_CAS_SUBSTITUTE:
			return "Substitute";

		case EuclidianConstants.MODE_CAS_SOLVE:
			return "Solve";

		case EuclidianConstants.MODE_CAS_NUMERICAL_SOLVE:
			return "NSolve";
		case EuclidianConstants.MODE_CAS_DERIVATIVE:
			return "Derivative";

		case EuclidianConstants.MODE_CAS_INTEGRAL:
			return "Integral";

		case EuclidianConstants.MODE_ATTACH_DETACH:
			return "AttachDetachPoint";

			// Spreadsheet
		case EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS:
			return "OneVarStats";

		case EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS:
			return "TwoVarStats";

		case EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS:
			return "MultiVarStats";

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:
			return "CreateList";

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			return "CreateListOfPoints";

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			return "CreateMatrix";

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
			return "CreateTable";

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			return "CreatePolyLine";

		case EuclidianConstants.MODE_SPREADSHEET_SUM:
			return "SumCells";

		case EuclidianConstants.MODE_SPREADSHEET_AVERAGE:
			return "MeanCells";

		case EuclidianConstants.MODE_SPREADSHEET_COUNT:
			return "CountCells";

		case EuclidianConstants.MODE_SPREADSHEET_MIN:
			return "MinCells";

		case EuclidianConstants.MODE_SPREADSHEET_MAX:
			return "MaxCells";

		default:
			return "";
		}
	}

	/**
	 * Finds the polynomial coefficients of the given expression and returns it
	 * in ascending order. If exp is not a polynomial null is returned.
	 * 
	 * @param exp
	 *            expression in MPreduce syntax, e.g. "3*a*x^2 + b*x"
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
		return lookupLabel(GeoElementSpreadsheet.getSpreadsheetCellName(col,
				row));
	}

	final public AnimationManager getAnimatonManager() {
		if (animationManager == null) {
			animationManager = getApplication().newAnimationManager(this);
		}
		return animationManager;
	}

	/**
	 * @param geo
	 * @return RealWorld Coordinates of the rectangle covering all euclidian
	 *         views in which <b>geo</b> is shown.<br />
	 *         Format: {xMin,xMax,yMin,yMax,xScale,yScale}
	 */
	public double[] getViewBoundsForGeo(GeoElement geo) {
		List<Integer> viewSet = geo.getViewSet();
		double[] viewBounds = new double[6];
		for (int i = 0; i < 6; i++) {
			viewBounds[i] = Double.NEGATIVE_INFINITY;
		}
		viewBounds[0] = viewBounds[2] = Double.POSITIVE_INFINITY;
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

	final public GeoAxis getXAxis() {
		return cons.getXAxis();
	}

	final public GeoAxis getYAxis() {
		return cons.getYAxis();
	}

	final public boolean isAxis(GeoElement geo) {
		return ((geo == cons.getXAxis()) || (geo == cons.getYAxis()));
	}

	public void updateLocalAxesNames() {
		cons.updateLocalAxesNames();
	}

	private boolean notifyRepaint = true;

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

	public final void notifyRepaint() {
		if (notifyRepaint && notifyViewsActive) {
			for (View view : views) {
				view.repaintView();
			}
		}
	}

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

	public final boolean notifySuggestRepaint() {
		boolean needed = false;
		if (notifyViewsActive) {
			for (View view : views) {
				needed = view.suggestRepaint() || needed;
			}
		}
		return needed;
	}

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

	public void clearJustCreatedGeosInViews() {
		if (notifyViewsActive) {
			for (View view : views) {
				if (view instanceof EuclidianViewInterfaceSlim) {
					((EuclidianViewInterfaceSlim) view)
							.getEuclidianController().clearJustCreatedGeos();
				}
			}
		}
	}

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

				notifyEuclidianViewCE();
				notifyReset();
				// algebra settings need to be applied after remaking tree
				if (app.getGuiManager() != null)
					app.getGuiManager().applyAlgebraViewSettings();

				viewReiniting = false;
			} else {
				// Application.debug("Deactivate VIEWS");

				// "detach" views
				notifyClearView();
			}
		}
	}

	/* *******************************************************
	 * methods for view-Pattern (Model-View-Controller)
	 * ******************************************************
	 */
	
	
	private EuclidianView lastAttachedEV = null;
	
	
	final public EuclidianView getLastAttachedEV(){
		return lastAttachedEV;
	}

	public void attach(View view) {

		if (!views.contains(view)) {
			views.add(view);
		}
		
		if (view instanceof EuclidianView){
			lastAttachedEV = (EuclidianView) view;
		}

		printAttachedViews();

	}

	private void printAttachedViews() {

		// can give java.util.ConcurrentModificationException
		try {
			if (!notifyViewsActive) {
				App.debug("Number of registered views = 0");
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("Number of registered views = ");
				sb.append(views.size());
				for (View view : views) {
					sb.append("\n * ");
					sb.append(view.getClass());
				}

				App.debug(sb.toString());
			}
		} catch (Exception e) {
			App.debug(e.getMessage());
		}

	}

	private boolean notifyViewsActive = true;

	public void detach(View view) {

		views.remove(view);
		printAttachedViews();

	}

	/**
	 * Notify the views that the mode changed.
	 * 
	 * @param mode
	 */
	final public void notifyModeChanged(int mode, ModeSetter m) {
		if (notifyViewsActive) {
			for (View view : views) {
				view.setMode(mode, m);
			}
		}
	}

	final public void notifyAddAll(View view) {
		int consStep = cons.getStep();
		notifyAddAll(view, consStep);
	}

	/**
	 * Registers an algorithm that needs to be updated when notifyRename(),
	 * notifyAdd(), or notifyRemove() is called.
	 */
	public void registerRenameListenerAlgo(AlgoElement algo) {
		if (renameListenerAlgos == null) {
			renameListenerAlgos = new ArrayList<AlgoElement>();
		}

		if (!renameListenerAlgos.contains(algo)) {
			renameListenerAlgos.add(algo);
		}
	}

	void unregisterRenameListenerAlgo(AlgoElement algo) {
		if (renameListenerAlgos != null) {
			renameListenerAlgos.remove(algo);
		}
	}

	private ArrayList<AlgoElement> renameListenerAlgos;
	private boolean spreadsheetBatchRunning;

	private void notifyRenameListenerAlgos() {
		// #4073 command Object[] registers rename listeners
		if (cons != null && !cons.isFileLoading()
				&& !this.isSpreadsheetBatchRunning()) {
			AlgoElement.updateCascadeAlgos(renameListenerAlgos);
		}
	}

	public boolean isSpreadsheetBatchRunning() {
		return this.spreadsheetBatchRunning;
	}

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
	final public boolean renameLabelInScripts(String oldLabel, String newLabel) {
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

	public final void notifyAdd(GeoElement geo) {
		if (notifyViewsActive) {
			if (addingPolygon && geo.labelSet) {
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

	public final void addingPolygon() {
		if (notifyViewsActive) {
			this.addingPolygon = true;
			for (View view : views) {
				if (view instanceof ClientView) {
					((ClientView) view).addingPolygon();
				}
			}
		}
	}

	public final void notifyPolygonAdded() {
		if (notifyViewsActive) {

			for (View view : views) {
				if (view instanceof ClientView) {
					((ClientView) view).addPolygonComplete(this.newPolygon);
				}
			}
		}
	}

	public final void notifyRemoveGroup() {
		if (notifyViewsActive) {
			for (View view : views) {
				if (view instanceof ClientView) {
					((ClientView) view).deleteGeos(deleteList);
				}
			}
		}
		this.deleteList.clear();
	}

	public final void notifyRemove(GeoElement geo) {
		if (notifyViewsActive) {
			if (geo.isLabelSet()) {
				this.deleteList.add(geo);
			}
			for (View view : views) {
				if ((view.getViewID() != App.VIEW_CONSTRUCTION_PROTOCOL)
						|| isNotifyConstructionProtocolViewAboutAddRemoveActive()) {
					view.remove(geo);
				}
			}

		}

		notifyRenameListenerAlgos();
	}

	public final void movingGeoSet() {
		if (notifyViewsActive) {
			for (View view : views) {
				if (view instanceof ClientView) {
					((ClientView) view).movingGeos();
				}
			}
		}
	}

	public final void movedGeoSet(ArrayList<GeoElement> elmSet) {
		if (notifyViewsActive) {
			for (View view : views) {
				if (view instanceof ClientView) {
					((ClientView) view).movedGeos(elmSet);
				}
			}
		}
	}

	public final void notifyUpdate(GeoElement geo) {
		// event dispatcher should not collect calls to stay compatible with 4.0
		if (notifyViewsActive) {
			for (View view : views) {
				view.update(geo);
			}
		}
	}

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

		// App.printStacktrace("notifyUpdate " + geo);
	}

	public final void notifyUpdateVisualStyle(GeoElement geo) {
		if (notifyViewsActive) {
			for (View view : views) {
				view.updateVisualStyle(geo);
			}
		}
	}

	public final void notifyUpdateAuxiliaryObject(GeoElement geo) {
		if (notifyViewsActive) {
			for (View view : views) {
				view.updateAuxiliaryObject(geo);
			}
		}
	}

	public final void notifyRename(GeoElement geo) {
		if (notifyViewsActive) {
			for (View view : views) {
				view.rename(geo);
			}
		}

		notifyRenameListenerAlgos();
	}

	public final void notifyRenameUpdatesComplete() {
		if (notifyViewsActive) {
			for (View view : views) {
				if (view instanceof ClientView) {
					((ClientView) view).renameUpdatesComplete();
				}
			}
		}
	}

	public void notifyPerspectiveChanged(String perspectiveId) {
		for (UserAwarenessListener listener : this.userAwarenessListeners) {
			listener.perspectiveChanged(perspectiveId);
		}
	}

	public void notifyPaste() {
		if (notifyViewsActive) {
			for (View view : views) {
				if (view instanceof ClientView) {
					((ClientView) view).pasteElms();
				}
			}
		}
	}

	public void notifyPasteComplete() {
		if (notifyViewsActive) {
			for (View view : views) {
				if (view instanceof ClientView) {
					((ClientView) view).pasteElmsComplete(app
							.getSelectionManager().getSelectedGeos());
				}
			}
		}
	}

	public boolean isNotifyViewsActive() {
		return notifyViewsActive && !viewReiniting;
	}

	public boolean isViewReiniting() {
		return viewReiniting;
	}

	/* *******************************************************
	 * methods for managing user awareness listeners
	 * ******************************************************
	 */
	public void addUserAwarenessListener(UserAwarenessListener listener) {
		this.userAwarenessListeners.add(listener);
	}

	public void removeUserAwarenessListener(UserAwarenessListener listener) {
		this.userAwarenessListeners.remove(listener);
	}

	/*
	 * /************************** Undo /Redo
	 */
	public void updateConstruction() {

		// views are notified about update at the end of this method
		cons.updateConstruction();

		// latexes in GeoGebraWeb are rendered afterwards and set updateEVAgain
		if (getUpdateAgain()) {
			setUpdateAgain(false, null);
			app.scheduleUpdateConstruction();
		} else {
			notifyRepaint();
		}
	}

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
	 * @param n
	 */
	public void updateConstruction(int n) {

		// views are notified about update at the end of this method
		for (int i = 0; i < n; i++) {
			cons.updateConstruction();
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

	/* ******************************
	 * redo / undo for current construction *****************************
	 */

	private boolean isGettingUndo;

	public synchronized boolean isGettingUndo() {
		return isGettingUndo;
	}

	public synchronized void setIsGettingUndo(boolean flag) {
		isGettingUndo = flag;
	}

	public void setUndoActive(boolean flag) {
		undoActive = flag;
	}

	public boolean isUndoActive() {
		return undoActive;
	}

	public void storeUndoInfo() {
		if (undoActive) {
			cons.storeUndoInfo();
		}
	}

	public void restoreCurrentUndoInfo() {
		if (undoActive) {
			cons.restoreCurrentUndoInfo();
		}
	}

	public void initUndoInfo() {
		if (undoActive) {
			cons.initUndoInfo();
		}
	}

	/** selected geos names just before undo/redo */
	private ArrayList<String> selectedGeosNames = new ArrayList<String>();

	/**
	 * store selected geos names
	 */
	public void storeSelectedGeosNames() {
		selectedGeosNames.clear();
		for (GeoElement geo : getApplication().getSelectionManager()
				.getSelectedGeos())
			selectedGeosNames.add(geo.getLabelSimple());
	}

	/**
	 * set geos selected from their names
	 */
	public void recallSelectedGeosNames() {
		ArrayList<GeoElement> list = new ArrayList<GeoElement>();
		for (String name : selectedGeosNames) {
			GeoElement geo = lookupLabel(name);
			if (geo != null)
				list.add(geo);
		}
		getApplication().getSelectionManager().setSelectedGeos(list);
	}

	public void redo() {
		if (undoActive && cons.getUndoManager().redoPossible()) {
			app.startCollectingRepaints();
			storeSelectedGeosNames();
			app.getCompanion().storeViewCreators();
			notifyReset();
			clearJustCreatedGeosInViews();
			cons.redo();
			notifyReset();
			app.getCompanion().recallViewCreators();
			recallSelectedGeosNames();
			app.stopCollectingRepaints();
		}
	}

	public void undo() {
		if (undoActive && cons.getUndoManager().undoPossible()) {
			app.startCollectingRepaints();
			storeSelectedGeosNames();
			app.getCompanion().storeViewCreators();
			notifyReset();
			clearJustCreatedGeosInViews();
			getApplication().getActiveEuclidianView().getEuclidianController()
					.clearSelections();
			cons.undo();
			notifyReset();
			app.getCompanion().recallViewCreators();
			recallSelectedGeosNames();

			// repaint needed for last undo in second EuclidianView (bugfix)
			if (!undoPossible()) {
				notifyRepaint();
			}
			app.stopCollectingRepaints();
		}
	}

	public boolean undoPossible() {
		return undoActive && cons.undoPossible();
	}

	public boolean redoPossible() {
		return undoActive && cons.redoPossible();
	}

	/**
	 * Get {@link Kernel#insertLineBreaks insertLineBreaks}.
	 * 
	 * @return {@link Kernel#insertLineBreaks insertLineBreaks}.
	 */
	public boolean isInsertLineBreaks() {
		return insertLineBreaks;
	}

	public String getXMLFileFormat() {
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

	final public ExpressionNode handleTrigPower(String image,
			ExpressionNode en, String operation) {

		if ("x".equals(operation) || "y".equals(operation)
				|| "z".equals(operation)) {
			return new ExpressionNode(this, new ExpressionNode(this,
					new FunctionVariable(this, operation), Operation.POWER,
					convertIndexToNumber(image)),
					Operation.MULTIPLY_OR_FUNCTION, en);
		}
		GeoElement ge = lookupLabel(operation);
		Operation type = app.getParserFunctions().get(operation, 1);
		if (ge != null || type == null) {
			return new ExpressionNode(this, new ExpressionNode(this,
					new Variable(this, operation), Operation.POWER,
					convertIndexToNumber(image)),
					Operation.MULTIPLY_OR_FUNCTION, en);
		}

		// sin^(-1)(x) -> ArcSin(x)
		if (image.indexOf(Unicode.Superscript_Minus) > -1) {
			// String check = ""+Unicode.Superscript_Minus +
			// Unicode.Superscript_1 + '(';
			if (image.substring(3, 6)
					.equals(Unicode.superscriptMinusOneBracket)) {
				return inverseTrig(type, en);
			}
			throw new Error("Bad index for trig function"); // eg sin^-2(x)
		}

		return new ExpressionNode(this,
				new ExpressionNode(this, en, type, null), Operation.POWER,
				convertIndexToNumber(image));

	}

	public ExpressionNode inverseTrig(Operation type, ExpressionValue en) {
		switch (type) {
		case SIN:
			return new ExpressionNode(this, en, Operation.ARCSIN, null);
		case COS:
			return new ExpressionNode(this, en, Operation.ARCCOS, null);
		case TAN:
			return new ExpressionNode(this, en, Operation.ARCTAN, null);
		case SINH:
			return new ExpressionNode(this, en, Operation.ASINH, null);
		case COSH:
			return new ExpressionNode(this, en, Operation.ACOSH, null);
		case TANH:
			return new ExpressionNode(this, en, Operation.ATANH, null);
		default:
			// eg csc^-1(x)
			throw new Error("Inverse not supported for trig function");

		}
	}

	final public MyDouble convertIndexToNumber(String str) {
		int i = 0;
		while ((i < str.length()) && !Unicode.isSuperscriptDigit(str.charAt(i))) {
			i++;
		}

		// Application.debug(str.substring(i, str.length() - 1));
		MyDouble md = new MyDouble(this, str.substring(i, str.length() - 1)); // strip
																				// off
																				// eg
																				// "sin"
																				// at
																				// start,
																				// "("
																				// at
																				// end

		return md;

	}

	/***********************************
	 * FACTORY METHODS FOR GeoElements
	 ***********************************/

	private GeoVec2D imaginaryUnit;

	public GeoVec2D getImaginaryUnit() {
		if (imaginaryUnit == null) {
			imaginaryUnit = new GeoVec2D(this, 0, 1);
			imaginaryUnit.setMode(COORD_COMPLEX);
		}

		return imaginaryUnit;
	}

	/**
	 * Creates a new algorithm that uses the given macro.
	 * 
	 * @return output of macro algorithm
	 */

	final public GeoElement[] useMacro(String[] labels, Macro macro,
			GeoElement[] input) {
		try {
			AlgoMacro algo = new AlgoMacro(cons, labels, macro, input);
			return algo.getOutput();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Localization getLocalization() {
		return getApplication().getLocalization();
	}

	public boolean kernelHas3DObjects() {
		Iterator<GeoClass> it = cons.usedGeos.iterator();

		boolean kernelHas3DObjects = false;

		while (it.hasNext()) {
			GeoClass geoType = it.next();

			if (geoType.is3D) {
				// App.debug("found 3D geo: " + geoType.xmlName);
				kernelHas3DObjects = true;
				break;
			}
		}

		return kernelHas3DObjects;
	}

	/**
	 * Returns the kernel settings in XML format.
	 */
	public void getKernelXML(StringBuilder sb, boolean asPreference) {

		// kernel settings
		sb.append("<kernel>\n");

		// is 3D?
		if (kernelHas3DObjects()) {
			sb.append("\t<uses3D val=\"true\"/>\n");
		}

		// continuity: true or false, since V3.0
		sb.append("\t<continuous val=\"");
		sb.append(isContinuous());
		sb.append("\"/>\n");

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
		sb.append(getAngleUnit() == Kernel.ANGLE_RADIANT ? "radiant" : "degree");
		sb.append("\"/>\n");

		// algebra style
		sb.append("\t<algebraStyle val=\"");
		sb.append(algebraStyle);
		sb.append("\"/>\n");

		// coord style
		sb.append("\t<coordStyle val=\"");
		sb.append(getCoordStyle());
		sb.append("\"/>\n");

		// whether return angle from inverse trigonometric functions
		if (!asPreference) {
			sb.append("\t<angleFromInvTrig val=\"");
			sb.append(getInverseTrigReturnsAngle());
			sb.append("\"/>\n");
		}

		// animation
		if (isAnimationRunning()) {
			sb.append("\t<startAnimation val=\"");
			sb.append(isAnimationRunning());
			sb.append("\"/>\n");
		}

		if (asPreference) {
			sb.append("\t<localization");
			sb.append(" digits=\"");
			sb.append(getLocalization().isUsingLocalizedDigits());
			sb.append("\"");
			sb.append(" labels=\"");
			sb.append(getLocalization().isUsingLocalizedLabels());
			sb.append("\"");
			sb.append("/>\n");

			sb.append("\t<casSettings");
			sb.append(" timeout=\"");
			sb.append(MyXMLHandler.getTimeoutOption(app.getSettings()
					.getCasSettings().getTimeoutMilliseconds() / 1000));
			sb.append("\"");
			sb.append(" expRoots=\"");
			sb.append(app.getSettings().getCasSettings().getShowExpAsRoots());
			sb.append("\"");
			sb.append("/>\n");
		}

		sb.append("</kernel>\n");
	}

	/* **********************************
	 * MACRO handling *********************************
	 */

	/**
	 * Creates a new macro within the kernel. A macro is a user defined command
	 * in GeoGebra.
	 */
	public void addMacro(Macro macro) {
		if (macroManager == null) {
			macroManager = new MacroManager(getApplication());
		}
		macroManager.addMacro(macro);

		for (UserAwarenessListener listener : this.userAwarenessListeners) {
			listener.addMacro(macro);
		}
	}

	/**
	 * Removes a macro from the kernel.
	 */
	public void removeMacro(Macro macro) {
		if (macroManager != null)
			macroManager.removeMacro(macro);

		for (UserAwarenessListener listener : this.userAwarenessListeners) {
			listener.removeMacro(macro);
		}
	}

	/**
	 * Removes all macros from the kernel.
	 */
	public void removeAllMacros() {
		if (macroManager != null) {
			getApplication().removeMacroCommands();
			macroManager.removeAllMacros();
		}

		for (UserAwarenessListener listener : this.userAwarenessListeners) {
			listener.removeAllMacros();
		}
	}

	/**
	 * Sets the command name of a macro. Note: if the given name is already used
	 * nothing is done.
	 * 
	 * @return if the command name was really set
	 */
	public boolean setMacroCommandName(Macro macro, String cmdName) {
		boolean nameUsed = macroManager.getMacro(cmdName) != null;
		if (nameUsed || cmdName == null || cmdName.length() == 0)
			return false;

		for (UserAwarenessListener listener : this.userAwarenessListeners) {
			listener.setMacroCommandName(macro, cmdName);
		}

		macroManager.setMacroCommandName(macro, cmdName);

		return true;
	}

	/**
	 * Returns the macro object for a given macro name. Note: null may be
	 * returned.
	 */
	public Macro getMacro(String name) {
		return (macroManager == null) ? null : macroManager.getMacro(name);
	}

	/**
	 * Returns an XML represenation of the given macros in this kernel.
	 * 
	 * @return
	 */
	public String getMacroXML(ArrayList<Macro> macros) {
		if (hasMacros())
			return MacroManager.getMacroXML(macros);
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
	 * Returns the number of currently registered macros
	 */
	public int getMacroNumber() {
		if (macroManager == null)
			return 0;
		return macroManager.getMacroNumber();
	}

	/**
	 * Returns a list with all currently registered macros.
	 */
	public ArrayList<Macro> getAllMacros() {
		if (macroManager == null) {
			return null;
		}
		return macroManager.getAllMacros();
	}

	/**
	 * Returns i-th registered macro
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
	 */
	public int getMacroID(Macro macro) {
		return (macroManager == null) ? -1 : macroManager.getMacroID(macro);
	}

	private Exercise exercise;

	public Exercise getExercise() {
		if (exercise == null) {
			exercise = new Exercise(getApplication());
		}
		return exercise;
	}

	/**
	 * Creates a new GeoElement object for the given type string.
	 * 
	 * @param type
	 *            String as produced by GeoElement.getXMLtypeString()
	 */
	public GeoElement createGeoElement(Construction cons1, String type) {
		// the type strings are the classnames in lowercase without the
		// beginning "geo"
		// due to a bug in GeoGebra 2.6c the type strings for conics
		// in XML may be "ellipse", "hyperbola", ...

		switch (type.charAt(0)) {
		case 'a': // angle
			if (type.equals("angle"))
				return new GeoAngle(cons1);
			return new GeoAxis(cons1, 1);

		case 'b': // angle
			if (type.equals("boolean")) {
				return new GeoBoolean(cons1);
			}
			return new GeoButton(cons1); // "button"

		case 'c': // conic
			if (type.equals("conic"))
				return new GeoConic(cons1);
			else if (type.equals("conicpart"))
				return new GeoConicPart(cons1, 0);
			else if (type.equals("curvecartesian"))
				return new GeoCurveCartesian(cons1);
			else if (type.equals("cascell"))
				return new GeoCasCell(cons1);
			else if (type.equals("circle")) { // bug in GeoGebra 2.6c
				return new GeoConic(cons1);
			}

		case 'd': // doubleLine // bug in GeoGebra 2.6c
			return new GeoConic(cons1);

		case 'e': // ellipse, emptyset // bug in GeoGebra 2.6c
			return new GeoConic(cons1);

		case 'f': // function
			if (type.equals("function")) {
				return new GeoFunction(cons1);
			} else if (type.equals("functionconditional")) { // had special
																// class fror v
																// <5.0
				return new GeoFunction(cons1);
			} else {
				return new GeoFunctionNVar(cons1);
			}

		case 'h': // hyperbola // bug in GeoGebra 2.6c
			return new GeoConic(cons1);

		case 'i': // image,implicitpoly
			if (type.equals("image"))
				return new GeoImage(cons1);
			else if (type.equals("intersectinglines")) // bug in GeoGebra 2.6c
				return new GeoConic(cons1);
			else if (type.equals("implicitpoly"))
				return new GeoImplicitPoly(cons1);
			else if (type.equals("interval")) {
				return new GeoInterval(cons1);
			}

		case 'l': // line, list, locus
			if (type.equals("line"))
				return new GeoLine(cons1);
			else if (type.equals("list"))
				return new GeoList(cons1);
			else
				return new GeoLocus(cons1);

		case 'n': // numeric
			return new GeoNumeric(cons1);

		case 'p': // point, polygon
			if (type.equals("point"))
				return new GeoPoint(cons1);
			else if (type.equals("polygon"))
				return new GeoPolygon(cons1, null);
			else if (type.equals("polyline"))
				return new GeoPolyLine(cons1, new GeoPointND[] {});
			else
				// parabola, parallelLines, point // bug in GeoGebra 2.6c
				return new GeoConic(cons1);

		case 'r': // ray
			return new GeoRay(cons1, null);

		case 's': // segment
			return new GeoSegment(cons1, null, null);

		case 't':
			if (type.equals("text")) {
				return new GeoText(cons1); // text
			}
			return new GeoTextField(cons1); // textfield

		case 'v': // vector
			return new GeoVector(cons1);
		default:
			App.error(
					"Kernel: GeoElement of type " + type
							+ " could not be created.");
			return new GeoNumeric(cons1);
		}
	}

	/*
	 * used to delay animation start until everything loaded
	 */
	public void setWantAnimationStarted() {
		wantAnimationStarted = true;
	}

	public boolean wantAnimationStarted() {
		return wantAnimationStarted;
	}

	/**
	 * Converts a NumberValue object to an ExpressionNode object.
	 */
	public ExpressionNode convertNumberValueToExpressionNode(GeoElement geo) {
		AlgoElement algo = geo.getParentAlgorithm();
		Traversing ifReplacer = new Traversing() {

			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev instanceof GeoElement) {
					return Kernel.this.convertNumberValueToExpressionNode(
							(GeoElement) ev).unwrap();
				}
				return ev;
			}

		};
		if (!geo.isLabelSet() && algo != null
				&& algo instanceof AlgoDependentNumber) {
			AlgoDependentNumber algoDep = (AlgoDependentNumber) algo;
			return algoDep.getExpression().getCopy(this).traverse(ifReplacer)
					.wrap();
		}
		if (!geo.isLabelSet() && algo != null
				&& algo instanceof AlgoDependentBoolean) {
			AlgoDependentBoolean algoDep = (AlgoDependentBoolean) algo;
			return algoDep.getExpression().getCopy(this);
		}
		if (!geo.isLabelSet() && algo != null && algo instanceof AlgoIf) {
			return ((AlgoIf) algo).toExpression();
		}
		return new ExpressionNode(this, geo);
	}

	final public GeoElement[] VectorPolygon(String[] labels, GeoPointND[] points) {

		/*
		 * cons.setSuppressLabelCreation(true); getAlgoDispatcher().Circle(null,
		 * (GeoPoint) points[0], new MyDouble(cons.getKernel(),
		 * points[0].distance(points[1])));
		 * cons.setSuppressLabelCreation(oldMacroMode);
		 */

		StringBuilder sb = new StringBuilder();

		double xA = points[0].getInhomX();
		double yA = points[0].getInhomY();

		for (int i = 1; i < points.length; i++) {

			double xC = points[i].getInhomX();
			double yC = points[i].getInhomY();

			GeoNumeric nx = new GeoNumeric(cons, null, xC - xA);
			GeoNumeric ny = new GeoNumeric(cons, null, yC - yA);
			StringTemplate tpl = StringTemplate.maxPrecision;
			// make string like this
			// (a+x(A),b+y(A))
			sb.setLength(0);
			sb.append('(');
			sb.append(nx.getLabel(tpl));
			sb.append("+x(");
			sb.append(points[0].getLabel(tpl));
			sb.append("),");
			sb.append(ny.getLabel(tpl));
			sb.append("+y(");
			sb.append(points[0].getLabel(tpl));
			sb.append("))");

			// Application.debug(sb.toString());

			GeoPoint pp = (GeoPoint) getAlgebraProcessor().evaluateToPoint(
					sb.toString(), true, true);

			try {
				cons.replace((GeoElement) points[i], pp);
				points[i] = pp;
				// points[i].setEuclidianVisible(false);
				points[i].update();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		points[0].update();

		return getAlgoDispatcher().Polygon(labels, points);

	}

	/**
	 * makes a copy of a polygon that can be dragged and rotated but stays
	 * congruent to original
	 * 
	 * @param poly
	 * @param offset2
	 * @param offset
	 * @return
	 */
	final public GeoElement[] RigidPolygon(GeoPolygon poly, double offsetX,
			double offsetY) {

		GeoPointND[] p = new GeoPointND[poly.getPointsLength()];

		// create free point p0
		p[0] = poly.getPoint(0).copy();
		p[0].setLabel(null);

		GeoSegmentND[] segs = poly.getSegments();
		GeoPointND[] pts = poly.getPoints();

		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		// create p1 = point on circle (so it can be dragged to rotate the whole
		// shape)
		GeoConicND circle = getAlgoDispatcher().Circle(null, p[0],
				poly.getSegments()[0]);
		cons.setSuppressLabelCreation(oldMacroMode);

		p[1] = getAlgoDispatcher().Point(null, circle, poly.getPoint(1).inhomX,
				poly.getPoint(1).inhomY, true, false, true);

		p[1].setLabel(null);

		boolean oldVal = isUsingInternalCommandNames();
		setUseInternalCommandNames(true);

		StringBuilder sb = new StringBuilder();

		for (int i = 2; i < poly.getPointsLength(); i++) {

			// build string like
			// Rotate[B_1 + (l, 0), Angle[C - B] + Angle[B_1 - A_1] - Angle[B -
			// A], B_1]
			//

			sb.setLength(0);
			sb.append("Rotate[");
			sb.append(p[i - 1].getLabel(StringTemplate.defaultTemplate));
			sb.append("+ (");
			sb.append(segs[i - 1].getLabel(StringTemplate.defaultTemplate));
			sb.append(", 0), Angle[");
			sb.append(pts[i].getLabel(StringTemplate.defaultTemplate)); // C
			sb.append("-");
			sb.append(pts[i - 1].getLabel(StringTemplate.defaultTemplate)); // B
			sb.append("] + Angle[");
			sb.append(p[i - 1].getLabel(StringTemplate.defaultTemplate));
			sb.append("-");
			sb.append(p[i - 2].getLabel(StringTemplate.defaultTemplate));
			sb.append("] - Angle[");
			sb.append(pts[i - 1].getLabel(StringTemplate.defaultTemplate)); // B
			sb.append("-");
			sb.append(pts[i - 2].getLabel(StringTemplate.defaultTemplate)); // A
			sb.append("],");
			sb.append(p[i - 1].getLabel(StringTemplate.defaultTemplate));
			sb.append("]");

			// app.debug(sb.toString());

			p[i] = getAlgebraProcessor().evaluateToPoint(sb.toString(), true,
					false);
			p[i].setLabel(null);
			p[i].setEuclidianVisible(false);
			p[i].update();

		}

		setUseInternalCommandNames(oldVal);

		AlgoPolygon algo = new AlgoPolygon(cons, null, p);
		GeoElement[] ret = { algo.getGeoElements()[0] };

		GeoPointND firstPoint = ((GeoPolygon) ret[0]).getPoints()[0];

		firstPoint.updateCoords2D();

		firstPoint.setCoords(firstPoint.getX2D() + offsetX, firstPoint.getY2D()
				+ offsetY, 1.0);
		firstPoint.updateRepaint();

		return ret;
	}

	protected GeoPointND RigidPolygonPointOnCircle(GeoConicND circle,
			GeoPointND point1) {
		return getAlgoDispatcher().Point(null, circle, point1.getInhomX(),
				point1.getInhomY(), true, false, true);
	}

	final public GeoElement[] RigidPolygon(String[] labels, GeoPointND[] points) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();

		cons.setSuppressLabelCreation(true);
		GeoConicND circle = getAlgoDispatcher().Circle(null, points[0],
				new MyDouble(this, points[0].distance(points[1])));
		cons.setSuppressLabelCreation(oldMacroMode);

		GeoPointND p = RigidPolygonPointOnCircle(circle, points[1]);

		try {
			(cons).replace((GeoElement) points[1], (GeoElement) p);
			points[1] = p;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder sb = new StringBuilder();

		double xA = points[0].getInhomX();
		double yA = points[0].getInhomY();
		double xB = points[1].getInhomX();
		double yB = points[1].getInhomY();

		GeoVec2D a = new GeoVec2D(cons.getKernel(), xB - xA, yB - yA); // vector
																		// AB
		GeoVec2D b = new GeoVec2D(cons.getKernel(), yA - yB, xB - xA); // perpendicular
																		// to
		// AB
		// changed to use this instead of Unit(Orthoganal)Vector
		// https://www.geogebra.org/forum/viewtopic.php?f=13&p=82764#p82764
		double aLength = Math.sqrt(a.inner(a));

		boolean oldVal = isUsingInternalCommandNames();
		setUseInternalCommandNames(true);

		a.makeUnitVector();
		b.makeUnitVector();
		StringTemplate tpl = StringTemplate.maxPrecision;
		boolean is3D = points[0].isGeoElement3D() || points[1].isGeoElement3D();
		for (int i = 2; i < points.length; i++) {

			double xC = points[i].getInhomX();
			double yC = points[i].getInhomY();

			GeoVec2D d = new GeoVec2D(cons.getKernel(), xC - xA, yC - yA); // vector
																			// AC

			// make string like this
			// A+3.76UnitVector[Segment[A,B]]+-1.74UnitPerpendicularVector[Segment[A,B]]
			sb.setLength(0);
			sb.append(points[0].getLabel(tpl));
			sb.append('+');
			sb.append(format(a.inner(d) / aLength, tpl));

			// use internal command name
			sb.append("Vector[");
			sb.append(points[0].getLabel(tpl));
			sb.append(',');
			sb.append(points[1].getLabel(tpl));
			sb.append("]+");
			sb.append(format(b.inner(d) / aLength, tpl));
			// use internal command name
			sb.append("OrthogonalVector[Segment[");
			sb.append(points[0].getLabel(tpl));
			sb.append(',');
			sb.append(points[1].getLabel(tpl));
			RigidPolygonAddEndOfCommand(sb, is3D);

			// Application.debug(sb.toString());

			GeoPointND pp = getAlgebraProcessor().evaluateToPoint(
					sb.toString(), true, true);

			try {
				(cons).replace((GeoElement) points[i], (GeoElement) pp);
				points[i] = pp;
				points[i].setEuclidianVisible(false);
				points[i].update();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		setUseInternalCommandNames(oldVal);

		points[0].update();

		return getAlgoDispatcher().Polygon(labels, points);

	}

	/**
	 * @param is3D
	 *            used in 3D
	 */
	protected void RigidPolygonAddEndOfCommand(StringBuilder sb, boolean is3D) {
		sb.append("]]");
	}

	/**
	 * tangent to Curve f in point P: (b'(t), -a'(t), a'(t)*b(t)-a(t)*b'(t))
	 */
	final public GeoLine Tangent(String label, GeoPointND P, GeoCurveCartesian f) {

		return KernelCAS.Tangent(cons, label, P, f);
	}

	/**
	 * Numeric search for extremum of function f in interval [left,right] Ulven
	 * 2011-2-5
	 * 
	 * final public GeoPoint[] Extremum(String label,GeoFunction f,NumberValue
	 * left,NumberValue right) { AlgoExtremumNumerical algo=new
	 * AlgoExtremumNumerical(cons,label,f,left,right); GeoPoint
	 * g=algo.getNumericalExtremum(); //All variants return array... GeoPoint[]
	 * result=new GeoPoint[1]; result[0]=g; return result;
	 * }//Extremum(label,geofunction,numbervalue,numbervalue)
	 */

	/***********************************
	 * PACKAGE STUFF
	 ***********************************/

	// temp for buildEquation

	/*
	 * final private String formatAbs(double x) { if (isZero(x)) return "0";
	 * else return formatNF(Math.abs(x)); }
	 */

	private GeoElementSpreadsheet ges = new GeoElementSpreadsheet();

	public GeoElementSpreadsheet getGeoElementSpreadsheet() {
		return ges;
	}

	public MacroKernel newMacroKernel() {
		return new MacroKernel(this);
	}

	public void notifyChangeLayer(GeoElement ge, int layer, int layer2) {
		app.updateMaxLayerUsed(layer2);
		if (notifyViewsActive) {
			for (View view : views) {
				if (view instanceof LayerView)
					((LayerView) view).changeLayer(ge, layer, layer2);
			}
		}
	}

	/**
	 * When function (or parabola) is transformed to curve, we need some good
	 * estimate for which part of curve should be ploted
	 * 
	 * @return lower bound for function -> curve transform
	 */
	public double getXmaxForFunctions() {
		return (((2 * getXmax()) - getXmin()) + getYmax()) - getYmin();
	}

	/**
	 * @see #getXmaxForFunctions()
	 * @return upper bound for function -> curve transform
	 */
	public double getXminForFunctions() {
		return (((2 * getXmin()) - getXmax()) + getYmin()) - getYmax();
	}

	/**
	 * clear cache (needed in web when CAS loaded)
	 */
	public void clearCasCache() {
		if (ggbCasCache != null) {
			ggbCasCache.clear();
		}
		if (ggbCAS != null) {
			ggbCAS.clearCache();
		}
	}

	/*
	 * used by web once CAS is loaded
	 */
	public void refreshCASCommands() {

		clearCasCache();
		cons.recomputeCASalgos();
		TreeSet<GeoElement> treeset = new TreeSet<GeoElement>(getConstruction()
				.getGeoSetWithCasCellsConstructionOrder());

		ArrayList<GeoElement> al = new ArrayList<GeoElement>();

		Iterator<GeoElement> it = treeset.iterator();

		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo instanceof FunctionalNVar) {
				FunctionNVar fun = ((FunctionalNVar) geo).getFunction();

				if (fun != null) {
					fun.clearCasEvalMap("");
				}
			} else if (geo instanceof GeoCurveCartesian) {
				GeoCurveCartesian curve = (GeoCurveCartesian) geo;
				FunctionNVar fun1 = curve.getFunX();
				FunctionNVar fun2 = curve.getFunY();
				if (fun1 != null) {
					fun1.clearCasEvalMap("");
				}
				if (fun2 != null) {
					fun2.clearCasEvalMap("");
				}
			}
			AlgoElement algo = geo.getParentAlgorithm();

			if (algo instanceof AlgoCasBase) {
				((AlgoCasBase) algo).clearCasEvalMap("");
				algo.compute();
			} else if (algo instanceof AlgoUsingTempCASalgo) {
				((AlgoUsingTempCASalgo) algo).refreshCASResults();
				algo.compute();
			} else if (algo instanceof UsesCAS
					|| algo instanceof AlgoCasCellInterface) {
				// eg Limit, LimitAbove, LimitBelow, SolveODE
				// AlgoCasCellInterface: eg Solve[x^2]
				algo.compute();
			}
			if (geo.isGeoCasCell() && algo == null) {
				((GeoCasCell) geo).computeOutput();
			}
			al.add(geo);
		}
		cons.setUpdateConstructionRunning(true);
		GeoElement.updateCascade(al, new TreeSet<AlgoElement>(), true);
		cons.setUpdateConstructionRunning(false);
	}

	public GeoElement[] PolygonND(String[] labels, GeoPointND[] P) {
		return getAlgoDispatcher().Polygon(labels, P);
	}

	public GeoElement[] PolyLineND(String[] labels, GeoPointND[] P) {
		return getAlgoDispatcher().PolyLine(labels, P, false);
	}

	/**
	 * over-ridden in Kernel3D
	 * 
	 * @param transformedLabel
	 * @param geoPointND
	 * @param geoPointND2
	 * @return
	 */
	public GeoRayND RayND(String transformedLabel, GeoPointND geoPointND,
			GeoPointND geoPointND2) {
		return getAlgoDispatcher().Ray(transformedLabel, (GeoPoint) geoPointND,
				(GeoPoint) geoPointND2);
	}

	/**
	 * over-ridden in Kernel3D
	 * 
	 * @param label
	 * @param P
	 * @param l
	 * @param direction
	 * @return
	 */
	public GeoLineND OrthogonalLine(String label, GeoPointND P, GeoLineND l,
			GeoDirectionND direction) {
		return getAlgoDispatcher().OrthogonalLine(label, (GeoPoint) P,
				(GeoLine) l);
	}

	/**
	 * over-ridden in Kernel3D
	 * 
	 * @param label
	 * @param P
	 * @param Q
	 * @return
	 */
	public GeoSegmentND SegmentND(String label, GeoPointND P, GeoPointND Q) {

		return getAlgoDispatcher().Segment(label, (GeoPoint) P, (GeoPoint) Q);
	}

	private AlgoDispatcher algoDispatcher;

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

	public GeoRayND Ray(String label, GeoPoint p, GeoPoint q) {
		return getAlgoDispatcher().Ray(label, p, q);
	}

	public GeoSegmentND Segment(String label, GeoPoint p, GeoPoint q) {
		return getAlgoDispatcher().Segment(label, p, q);
	}

	public GeoElement[] Polygon(String[] labels, GeoPointND[] p) {
		return getAlgoDispatcher().Polygon(labels, p);
	}

	public GeoElement[] PolyLine(String[] labels, GeoPointND[] p, boolean b) {
		return getAlgoDispatcher().PolyLine(labels, p, b);
	}

	public void initAfterAsync(App app1) {
		this.app = app1;

		newConstruction();
		getExpressionNodeEvaluator();

		setManager3D(newManager3D(this));
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
	 */
	public void setUpdateAgain(boolean value, GeoElement geo) {
		updateEVAgain = value;
		if (value) {
			this.cons.addLaTeXGeo(geo);
		}
	}

	/**
	 * used for DrawEquationWeb and DrawText in GeoGebraWeb
	 */
	public boolean getUpdateAgain() {
		return updateEVAgain && app.isHTML5Applet();
	}

	/**
	 * used for DrawEquationWeb and DrawText in GeoGebraWeb
	 */
	public void setForceUpdatingBoundingBox(boolean value) {
		forceUpdatingBoundingBox = value;
	}

	/**
	 * used for DrawEquationWeb and DrawText in GeoGebraWeb
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

	public void notifyBatchUpdate() {
		if (notifyViewsActive) {
			for (View view : views) {
				view.startBatchUpdate();
			}
		}
	}

	public void notifyEndBatchUpdate() {
		if (notifyViewsActive) {
			for (View view : views) {
				view.endBatchUpdate();
			}
		}
	}

	public void setViewsLabels() {
		if (notifyViewsActive) {
			for (View view : views) {
				if (view instanceof SetLabels) {
					((SetLabels) view).setLabels();
				}
			}
		}
	}

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
	 * 
	 * @param geo
	 * @return 3D copy of the geo (if exists)
	 */
	public GeoElement copy3D(GeoElement geo) {
		return geo.copy();
	}

	/**
	 * 
	 * @param cons1
	 *            target cons
	 * @param geo
	 * @return 3D copy internal of the geo (if exists)
	 */
	public GeoElement copyInternal3D(Construction cons1, GeoElement geo) {
		return geo.copyInternal(cons1);
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

	public GeoElement wrapInVector(GeoPointND pt) {
		AlgoVectorPoint algo = new AlgoVectorPoint(cons, pt);
		cons.removeFromConstructionList(algo);
		return (GeoElement) algo.getVector();
	}

	// for compatibility/interfacing with 3D
	public GeoAxisND getXAxis3D() {
		return null;
	}

	// for compatibility/interfacing with 3D
	public GeoAxisND getYAxis3D() {
		return null;
	}

	// for compatibility/interfacing with 3D
	public GeoAxisND getZAxis3D() {
		return null;
	}

	// for compatibility/interfacing with 3D
	public GeoElement getClippingCube() {
		return null;
	}
}
