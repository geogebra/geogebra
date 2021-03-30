package org.geogebra.common.geogebra3D.euclidian3D;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPointWithZ;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.euclidian.controller.MouseTouchGestureController;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.euclidian3D.Mouse3DEvent;
import org.geogebra.common.geogebra3D.euclidian3D.animator.EuclidianView3DAnimator;
import org.geogebra.common.geogebra3D.euclidian3D.animator.EuclidianView3DAnimator.AnimationType;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawAngle3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawAxis3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawClippingCube3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawConic3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawConicPart3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawConicSection3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawConify3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawCurve3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawExtrusion3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawImplicitCurve3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLine3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawList3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLocus3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPlane3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPlaneConstant3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPointDecorations;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPolyLine3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPolygon3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPolyhedron3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawQuadric3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawQuadric3DLimited;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawQuadric3DPart;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawRay3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawSegment3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawSurface3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawSurface3DElements;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawSurfaceComposite;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawSurfaceOfRevolution;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawText3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawVector3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3DListsForView;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.ScalerXYZ;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterCursor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Format;
import org.geogebra.common.geogebra3D.euclidian3D.xr.XRManagerInterface;
import org.geogebra.common.geogebra3D.kernel3D.Kernel3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicSection;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCursor3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3DConstant;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.GeoImplicitSurface;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EVProperty;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocusNDInterface;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.XMLBuilder;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.matrix.Coords3;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Class for 3D view
 *
 * @author mathieu
 *
 */
public abstract class EuclidianView3D extends EuclidianView
		implements EuclidianView3DInterface, ScalerXYZ {

	// since V3.0 this factor is 1, before it was 0.5
	final public static double DEFAULT_GRID_DIST_FACTOR = 1;

	/**
	 * number of drawables linked to this view (xOy plane, Ox, Oy, Oz axis)
	 */
	static final public int DRAWABLES_NB = 4;
	/**
	 * no point under the cursor
	 */
	public static final int PREVIEW_POINT_NONE = 0;
	/**
	 * free point under the cursor
	 */
	public static final int PREVIEW_POINT_FREE = 1;
	/**
	 * path point under the cursor
	 */
	public static final int PREVIEW_POINT_PATH = 2;
	/**
	 * region point under the cursor
	 */
	public static final int PREVIEW_POINT_REGION = 3;
	/**
	 * dependent point under the cursor
	 */
	public static final int PREVIEW_POINT_DEPENDENT = 4;
	/**
	 * already existing point under the cursor
	 */
	public static final int PREVIEW_POINT_ALREADY = 5;
	/**
	 * region as path (e.g. quadric as line) point under the cursor
	 */
	public static final int PREVIEW_POINT_REGION_AS_PATH = 6;
	public static final int CURSOR_DEFAULT = 0;

	private static final int PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT = 2500;
	// maximum angle between two line segments
	private static final double MAX_ANGLE_SPEED_SURFACE = 20; // degrees
	private static final double MAX_BEND_SPEED_SURFACE = Math
			.tan(MAX_ANGLE_SPEED_SURFACE * Kernel.PI_180);
	public static double automaticGridDistanceFactor = DEFAULT_GRID_DIST_FACTOR;

    static public final int CURSOR_DELAY_IN_MILLISECONDS = 1000;

	protected Renderer renderer;
	// viewing values
	protected double zZero;
	protected double zZeroOld = 0;
	protected double aOld;
	protected double bOld;
	// picking and hits
	protected Hits3D hits = new Hits3D(); // objects picked from openGL
	protected DrawClippingCube3D clippingCubeDrawable;
	protected GeoPoint3D cursorOnXOYPlane;
	protected CoordMatrix rotationAndScaleMatrix;
	// EuclidianViewInterface
	protected Coords pickPoint = new Coords(0, 0, 0, 1);
	protected CoordMatrix4x4 tmpMatrix4x4_3 = CoordMatrix4x4.identity();
	protected Coords tmpCoords1 = new Coords(4);
	protected Coords tmpCoords2 = new Coords(4);
	protected GColor bgColor;
	protected GColor bgApplyedColor;

	private Kernel3D kernel3D;
	// list of 3D objects
	private boolean waitForUpdate = true; // says if it waits for update...

	private Drawable3DListsForView drawable3DLists;
	/**
	 * list for drawables that will be added on next frame
	 */
	private LinkedList<Drawable3D> drawable3DListToBeAdded; // = new
	/**
	 * list for drawables that will be removed on next frame
	 */
	private LinkedList<Drawable3D> drawable3DListToBeRemoved; // = new
	/**
	 * list for Geos to that will be added on next frame
	 */
	private TreeSet<GeoElement> geosToBeAdded;
	// Map (geo, drawable) for GeoElements and Drawables
	private TreeMap<GeoElement, Drawable3D> drawable3DMap = new TreeMap<>();
	// matrix for changing coordinate system
	private CoordMatrix4x4 mWithoutScale = CoordMatrix4x4.identity();
	private CoordMatrix4x4 mWithScale = CoordMatrix4x4.identity();
    private CoordMatrix4x4 mToScene = CoordMatrix4x4.identity();
	private CoordMatrix4x4 mInvTranspose = CoordMatrix4x4.identity();
	private CoordMatrix4x4 undoRotationMatrix = CoordMatrix4x4.identity();
	private double a = ANGLE_ROT_OZ;
	private double b = ANGLE_ROT_XOY; // angles (in degrees)
	private double translationZzeroForAR = 0;
	private double arFloorZ = 0;
	private double arZZeroAtStart;

	/**
	 * direction of view
	 */
	private Coords viewDirection = new Coords(4);
	private Coords eyePosition = new Coords(4);
	// axis and xOy plane
	private GeoPlane3DConstant xOyPlane;
	private GeoAxisND[] axis;
	private GeoClippingCube3D clippingCube;
	private DrawPlane3D xOyPlaneDrawable;
	private DrawAxis3D[] axisDrawable;
	// point decorations
	private DrawPointDecorations pointDecorations;
	// preview
	private Previewable previewDrawable;
	private GeoCursor3D cursor3D;
	private int cursor3DType = PREVIEW_POINT_NONE;
	private boolean cursor3DVisible = true;
	private EuclidianCursor cursor = EuclidianCursor.DEFAULT;
	private CoordMatrix4x4 scaleMatrix = CoordMatrix4x4.identity();
	private CoordMatrix4x4 undoScaleMatrix = CoordMatrix4x4.identity();
	private CoordMatrix4x4 translationMatrixWithScale = CoordMatrix4x4
			.identity();
	private CoordMatrix4x4 translationMatrixWithoutScale = CoordMatrix4x4
			.identity();
    private CoordMatrix4x4 undoTranslationMatrix = CoordMatrix4x4.identity();
	private CoordMatrix4x4 rotationMatrix = CoordMatrix4x4.identity();
	private Coords viewDirectionPersp = new Coords(4);
	private Coords tmpCoordsLength3 = new Coords(3);
    private Coords tmpCoordsLength4 = new Coords(4);
	private int intersectionThickness;
	private GeoPointND intersectionPoint;
	private CoordMatrix4x4 tmpMatrix1 = CoordMatrix4x4.identity();
	private CoordMatrix4x4 tmpMatrix2 = CoordMatrix4x4.identity();
	private boolean defaultCursorWillBeHitCursor = false;
	private double[] parameters = new double[2];
	private boolean viewChangedByZoom = true;
	private boolean viewChangedByTranslate = true;
	private boolean viewChangedByRotate = true;
	private int pointStyle;
	private int projection = PROJECTION_ORTHOGRAPHIC;
	private double[] projectionPerspectiveEyeDistance = {
			PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT,
			PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT };
	private boolean isGlassesGrayScaled = true;
	private boolean isGlassesShutDownGreen = false;
	private double[] eyeX = { -100, 100 };
	private double[] eyeY = { 0, 0 };
	private double projectionObliqueAngle = 30;
	private double projectionObliqueFactor = 0.5;
	/** min corner for objects enclosing bounding box */
	protected Coords boundsMin;
	/** max corner for objects enclosing bounding box */
	protected Coords boundsMax;
	private double fontScale = 1;
	private EuclidianView3DCompanion companion3D;

	private CoordMatrix4x4 cursorMatrix = new CoordMatrix4x4();
	private Coords cursorNormal = new Coords(3);

	private Coords startPos;

	private CoordMatrix4x4 startTranslation = CoordMatrix4x4.identity();

	private EuclidianView3DAnimator animator;

	//Mixed Reality and Augmented Reality
	private boolean mIsXRDrawing;
	private boolean mIsXREnabled;
	private Target target;

	// AR Ratio
	final public static int RATIO_UNIT_METERS_CENTIMETERS_MILLIMETERS = 1;
	final public static int RATIO_UNIT_INCHES = 2;
	final public static float FROM_INCH_TO_CM = 2.54f;
	final public static float FROM_CM_TO_INCH = 0.393700787f;
	private boolean arRatioIsShown = true;
	private String arRatioUnit = "cm";
	private int arRatioMetricSystem;

	/** possibly dynamic z min */
	protected NumberValue zminObject;
	/** possibly dynamic z max */
	protected NumberValue zmaxObject;

	/**
	 * common constructor
	 *
	 * @param ec
	 *            controller on this
	 * @param settings
	 *            settings
	 */
	public EuclidianView3D(EuclidianController3D ec,
			EuclidianSettings settings) {

		super(ec, EVNO_3D, settings);
		logInited();
		this.kernel3D = (Kernel3D) ec.getKernel();
		euclidianController.setView(this);

		animator = new EuclidianView3DAnimator(this);

		startPos = new Coords(4);
		startPos.setW(1);

		viewDirection.set3(Coords.VZ);

		target = new Target();
		start();
	}

	protected void logInited() {
		// don't remove, it's important we pick up when this class is created by
		// mistake
		Log.error("******************************************************************************");
		Log.error("******************* 3D View being initialized ********************************");
		Log.error("******************************************************************************");
	}

	final private static void changeCoords(CoordMatrix mat, Coords vInOut) {
		Coords v1 = vInOut.getCoordsLast1();
		vInOut.set(mat.mul(v1));
	}

	/**
	 * return the intersection of intervals [minmax] and [v1,v2]
	 *
	 * @param minmax
	 *            initial interval
	 * @param v1
	 *            first value
	 * @param v2
	 *            second value
	 */
	private static void intervalUnion(double[] minmax, double v1, double v2) {

		// Log.debug(v1+","+v2);

		if (Double.isNaN(v2)) {
			return;
		}
		double vMin = v1;
		double vMax = v2;
		if (v1 > v2) {
			vMax = v1;
			vMin = v2;
		}

		if (vMin < minmax[0] && !Double.isInfinite(vMin)) {
			minmax[0] = vMin;
		}

		if (vMax > minmax[1] && !Double.isInfinite(vMax)) {
			minmax[1] = vMax;
		}

	}

	@Override
	protected void initAxesValues() {
		axesNumberFormat = new NumberFormatAdapter[3];
		showAxesNumbers = new boolean[] { true, true, true };
		axesLabels = new String[] { null, null, null };
		axesLabelsStyle = new int[] { GFont.PLAIN, GFont.PLAIN, GFont.PLAIN };
		axesUnitLabels = new String[] { null, null, null };
		setAxesTickStyles(new int[] {
				EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
				EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
				EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR });
		automaticAxesNumberingDistances = new boolean[] { true, true, true };
		axesNumberingDistances = new double[] { 2, 2, 2 };
		axesDistanceObjects = new GeoNumberValue[] { null, null, null };
		drawBorderAxes = new boolean[] { false, false, false };
		axisCross = new double[] { 0, 0, 0 };
		positiveAxes = new boolean[] { false, false, false };
		piAxisUnit = new boolean[] { false, false, false };
		gridDistances = new double[] { 2, 2, Math.PI / 6 };
		axesTickInterval = new double[] { 1, 1, 1 };
	}

	/**
	 * create the panel
	 */
	abstract protected void createPanel();

	abstract protected Renderer createRenderer();

	protected void start() {

		drawable3DLists = new Drawable3DListsForView(this);
		drawable3DListToBeAdded = new LinkedList<>();
		drawable3DListToBeRemoved = new LinkedList<>();

		geosToBeAdded = new TreeSet<>();

		renderer = createRenderer();
		if (renderer == null) {
			initAxisAndPlane();
			return;
		}

		createPanel();

		attachView();

		initAxisAndPlane();
		kernel3D.getConstruction().setIgnoringNewTypes(true);
		// previewables
		cursor3D = new GeoCursor3D(kernel3D.getConstruction());
		cursor3D.setCoords(0, 0, 0, 1);
		cursor3D.setIsPickable(false);
		// cursor3D.setLabelOffset(5, -5);
		// cursor3D.setEuclidianVisible(false);
		cursor3D.setMoveNormalDirection(Coords.VZ);

		cursorOnXOYPlane = new GeoPoint3D(kernel3D.getConstruction());
		cursorOnXOYPlane.setCoords(0, 0, 0, 1);
		cursorOnXOYPlane.setIsPickable(false);
		cursorOnXOYPlane.setMoveNormalDirection(Coords.VZ);
		cursorOnXOYPlane.setRegion(xOyPlane);
		cursorOnXOYPlane.setMoveMode(GeoPointND.MOVE_MODE_XY);
		kernel3D.getConstruction().setIgnoringNewTypes(false);
		// point decorations
		initPointDecorations();

		// tells the renderer if use clipping cube
		updateUseClippingCube();

	}

	/**
	 * init the axis and xOy plane
	 */
	final public void initAxisAndPlane() {
		// axis
		axis = new GeoAxisND[3];
		axisDrawable = new DrawAxis3D[3];
		axis[0] = kernel3D.getXAxis();
		axis[1] = kernel3D.getYAxis();
		axis[2] = kernel3D.getZAxis3D();

		for (int i = 0; i < 3; i++) {
			axis[i].setLabelVisible(true);
			axisDrawable[i] = (DrawAxis3D) createDrawable((GeoElement) axis[i]);
		}
		Construction cons = kernel3D.getConstruction();
		// clipping cube
		clippingCube = (GeoClippingCube3D) cons.getClippingCube();
		clippingCube.setEuclidianVisible(true);
		clippingCube.setObjColor(GColor.GRAY);
		clippingCube.setLineThickness(1);
		clippingCube.setIsPickable(false);
		clippingCubeDrawable = (DrawClippingCube3D) createDrawable(
				clippingCube);

		// plane
		xOyPlane = (GeoPlane3DConstant) cons.getXOYPlane();
		xOyPlane.setEuclidianVisible(true);
		xOyPlane.setGridVisible(true);
		xOyPlane.setPlateVisible(true);
		// xOyPlane.setFading(0);
		xOyPlaneDrawable = (DrawPlane3D) createDrawable(xOyPlane);

		// companion
		getCompanion().initAxisAndPlane();

	}

	// POINT_CAPTURING_STICKY_POINTS locks onto these points
	// not implemented yet in 3D
	@Override
	public ArrayList<GeoPointND> getStickyPointList() {
		return new ArrayList<>();
	}

	/**
	 * return the 3D kernel
	 *
	 * @return the 3D kernel
	 */
	@Override
	public Kernel3D getKernel() {
		return kernel3D;
	}

	/**
	 * @return gl renderer
	 */
	@Override
	public Renderer getRenderer() {
		return renderer;
	}

	/**
	 * adds a GeoElement3D to this view
	 */
	@Override
	public void add(GeoElement geo) {
		if (geo.isVisibleInView3D()) {
			createAndAddDrawable(geo);
		}
	}

	@Override
	protected boolean createAndAddDrawable(GeoElement geo) {
		setWaitForUpdate();
		geosToBeAdded.add(geo);
		return true;
	}

	@Override
	protected boolean createPreviewsForSpecsPoints() {
		// not implemented yet
		return false;
	}

	@Override
	protected void updatePreviewFromInputBar() {
		repaintForPreviewFromInputBar();
	}

	@Override
	protected boolean createAndUpdatePreviewDrawable(GeoElement geo) {
		return createAndAddDrawable(geo);
	}

	/**
	 * add the geo now
	 */
	private void addNow(GeoElement geo) {

		// check if geo has been already added
		if (getDrawableND(geo) != null) {
			return;
		}

		// create the drawable
		Drawable3D d = null;
		d = createDrawable(geo);
		if (d != null) {
			drawable3DLists.add(d);
		}
	}

	/**
	 * add the drawable to the lists of drawables
	 *
	 * @param d
	 *            drawable to add
	 */
	public void addToDrawable3DLists(Drawable3D d) {
		/*
		 * if (d.getGeoElement().getLabel().equals("a")){
		 * Application.debug("d="+d); }
		 */

		drawable3DListToBeAdded.add(d);
	}

	@Override
	public Drawable3D newDrawable(GeoElementND geo) {
		Drawable3D d = null;
		if (geo.hasDrawable3D()) {

			switch (geo.getGeoClassType()) {

			default:
				Log.debug("missing case " + geo.getGeoClassType());
				break;
			// 2D also shown in 3D
			case LIST:
				d = new DrawList3D(this, (GeoList) geo);
				break;

			// 3D stuff
			case POINT:
			case POINT3D:
				d = new DrawPoint3D(this, (GeoPointND) geo);
				break;

			case VECTOR:
			case VECTOR3D:
				d = new DrawVector3D(this, (GeoVectorND) geo);
				break;

			case SEGMENT:
			case SEGMENT3D:
				d = new DrawSegment3D(this, (GeoSegmentND) geo);
				break;

			case PLANE3D:
				if (geo instanceof GeoPlane3DConstant) {
					d = new DrawPlaneConstant3D(this, (GeoPlane3D) geo,
							axisDrawable[AXIS_X], axisDrawable[AXIS_Y]);
				} else {
					d = new DrawPlane3D(this, (GeoPlane3D) geo);
				}

				break;

			case POLYGON:
			case POLYGON3D:
				d = new DrawPolygon3D(this, (GeoPolygon) geo);
				break;

			case PENSTROKE:
			case POLYLINE:
			case POLYLINE3D:
				d = new DrawPolyLine3D(this, (GeoElement) geo);
				break;

			case LINE:
			case LINE3D:
				d = new DrawLine3D(this, (GeoLineND) geo);
				break;

			case RAY:
			case RAY3D:
				d = new DrawRay3D(this, (GeoRayND) geo);
				break;

			case CONIC:
			case CONIC3D:
				d = new DrawConic3D(this, (GeoConicND) geo);
				break;

			case CONICPART:
				d = new DrawConicPart3D(this, (GeoConicPartND) geo);
				break;

			case CONICSECTION:
				d = new DrawConicSection3D(this, (GeoConicSection) geo);
				break;

			case AXIS:
			case AXIS3D:
				d = new DrawAxis3D(this, (GeoAxisND) geo);
				break;

			case FUNCTION:
				if (((GeoFunction) geo).isBooleanFunction()) {
					d = newDrawSurface3D((SurfaceEvaluable) geo);

				} else {
					d = new DrawCurve3D(this, (CurveEvaluable) geo);
				}
				break;
			case CURVE_CARTESIAN:
			case CURVE_CARTESIAN3D:
				d = new DrawCurve3D(this, (CurveEvaluable) geo);
				break;

			case LOCUS:
				d = new DrawLocus3D(this,
						((GeoLocusNDInterface) geo).getLocus(),
						(GeoElement) geo,
						CoordSys.XOY);
				break;
			case IMPLICIT_POLY:
				d = new DrawImplicitCurve3D(this, (GeoImplicit) geo);
				break;

			case ANGLE:
			case ANGLE3D:
				if (geo.isIndependent()) {
					// TODO: slider
				} else {
					d = new DrawAngle3D(this, (GeoAngle) geo);
				}
				break;

			case QUADRIC:
				d = new DrawQuadric3D(this, (GeoQuadric3D) geo);
				break;

			case QUADRIC_PART:
				d = new DrawQuadric3DPart(this, (GeoQuadric3DPart) geo);
				break;

			case QUADRIC_LIMITED:
				if (!((GeoQuadric3DLimited) geo).getSide().isLabelSet()) {
					// create drawable when side is not explicitely created
					// (e.g. in sequence, or with transformation)
					d = new DrawQuadric3DLimited(this,
							(GeoQuadric3DLimited) geo);
				}
				break;

			case POLYHEDRON:
				d = new DrawPolyhedron3D(this, (GeoPolyhedron) geo);
				break;

			case FUNCTION_NVAR:
				GeoFunctionNVar geoFun = (GeoFunctionNVar) geo;
				switch (geoFun.getVarNumber()) {
				default:
					// do nothing
					break;
				case 2:
					d = newDrawSurface3D(geoFun);
					break;
				/*
				 * case 3: d = new DrawImplicitFunction3Var(this, geoFun);
				 * break;
				 */
				}
				break;
			case SURFACECARTESIAN:
			case SURFACECARTESIAN3D:
				d = newDrawSurface3D((GeoSurfaceCartesianND) geo);
				break;

			case TEXT:
				d = new DrawText3D(this, (GeoText) geo);
				break;

			case CLIPPINGCUBE3D:
				d = new DrawClippingCube3D(this, (GeoClippingCube3D) geo);
				break;
			case IMPLICIT_SURFACE_3D:
				d = new DrawSurfaceComposite(this, (GeoImplicitSurface) geo);
			}
		}

		return d;

	}

	final private DrawSurface3D newDrawSurface3D(SurfaceEvaluable surface) {
		if (renderer.useShaders()) {
			return new DrawSurface3DElements(this, surface);
		}
		return new DrawSurface3D(this, surface);
	}

	@Override
	protected Drawable3D createDrawable(GeoElement geo) {

		Drawable3D d = newDrawable(geo);
		if (d != null) {
			drawable3DMap.put(geo, d);
		}

		return d;
	}

	/**
	 * converts the vector to scene coords
	 *
	 * @param vInOut
	 *            vector
	 */
	final public void toSceneCoords3D(Coords vInOut) {
		changeCoords(mToScene, vInOut);
	}

	/**
	 * converts the vector to screen coords
	 *
	 * @param vInOut
	 *            vector
	 */
	final public void toScreenCoords3D(Coords vInOut) {
		changeCoords(mWithScale, vInOut);
	}

	/**
	 * return the matrix : screen coords -> scene coords.
	 *
	 * @return the matrix : screen coords -> scene coords.
	 */
	@Override
	final public CoordMatrix4x4 getToSceneMatrix() {
		return mToScene;
	}

	/**
	 * 
	 * @return transposed to scene matrix
	 */
	final public CoordMatrix4x4 getToSceneMatrixTranspose() {
		return mInvTranspose;
	}

	/**
	 * return the matrix : scene coords -> screen coords.
	 *
	 * @return the matrix : scene coords -> screen coords.
	 */
	final public CoordMatrix4x4 getToScreenMatrix() {
		return mWithScale;
	}

	/**
	 *
	 * @return the matrix : scene coords (already scaled) -> screen coords.
	 */
	final public CoordMatrix4x4 getToScreenMatrixForGL() {
		return mWithoutScale;
	}

	/**
	 * return the matrix undoing the rotation : scene coords -> screen coords.
	 *
	 * @return the matrix undoing the rotation : scene coords -> screen coords.
	 */
	final public CoordMatrix4x4 getUndoRotationMatrix() {
		return undoRotationMatrix;
	}

	/**
	 *
	 * @return true if y axis is vertical (and not z axis)
	 */
	public boolean getYAxisVertical() {
		return getSettings().getYAxisVertical();
	}

	@Override
	public void setYAxisVertical(boolean flag) {
		getSettings().setYAxisVertical(flag);

	}

	public boolean getUseLight() {
		return getSettings().getUseLight();
	}

	private void updateRotationMatrix() {
		if (mIsXRDrawing) {
            CoordMatrix.setRotation3DMatrix(CoordMatrix.X_AXIS,
                    (-90) * EuclidianController3D.ANGLE_TO_DEGREES, tmpMatrix1);
            CoordMatrix.setRotation3DMatrix(CoordMatrix.Z_AXIS,
                    (-this.a - 90) * EuclidianController3D.ANGLE_TO_DEGREES,
                    tmpMatrix2);
        } else {
            if (getYAxisVertical()) { // y axis taken for up-down direction
				CoordMatrix.setRotation3DMatrix(CoordMatrix.X_AXIS,
						(this.b) * EuclidianController3D.ANGLE_TO_DEGREES,
						tmpMatrix1);
				CoordMatrix.setRotation3DMatrix(CoordMatrix.Y_AXIS,
						(-this.a - 90) * EuclidianController3D.ANGLE_TO_DEGREES,
						tmpMatrix2);
            } else { // z axis taken for up-down direction
				CoordMatrix.setRotation3DMatrix(CoordMatrix.X_AXIS,
						(this.b - 90) * EuclidianController3D.ANGLE_TO_DEGREES,
						tmpMatrix1);
				CoordMatrix.setRotation3DMatrix(CoordMatrix.Z_AXIS,
						(-this.a - 90) * EuclidianController3D.ANGLE_TO_DEGREES,
						tmpMatrix2);
            }
        }

		rotationMatrix.setMul3x3(tmpMatrix1, tmpMatrix2);
	}

	// TODO specific scaling for each direction
	// private double scale = 50;

	private void updateScaleMatrix() {
		scaleMatrix.set(1, 1, getXscale());
		scaleMatrix.set(2, 2, getYscale());
		scaleMatrix.set(3, 3, getZscale());
	}

	public double getXTranslationUnity() {
		return 0;
	}

	public double getYTranslationUnity() {
		return 0;
	}

	public double getZTranslationUnity() {
		return 0;
	}

	protected CoordMatrix4x4 getTranslationMatrixWithScale() {
		return translationMatrixWithScale;
	}

	/**
	 * Update translation matrices (do and undo).
	 */
	public void updateTranslationMatrices() {

        double translationZzero = getZZero() + translationZzeroForAR + getZTranslationUnity();
        double translationXzero = getXZero() + getXTranslationUnity();
        double translationYzero = getYZero() + getYTranslationUnity();

		// scene to screen translation matrices
		translationMatrixWithScale.set(1, 4, translationXzero * getXscale());
		translationMatrixWithScale.set(2, 4, translationYzero * getYscale());
		translationMatrixWithScale.set(3, 4, translationZzero * getZscale());
		translationMatrixWithoutScale.set(1, 4, translationXzero);
		translationMatrixWithoutScale.set(2, 4, translationYzero);
		translationMatrixWithoutScale.set(3, 4, translationZzero);

		// screen to scene translation matrix
        undoTranslationMatrix.set(1, 4, -translationXzero);
        undoTranslationMatrix.set(2, 4, -translationYzero);
        undoTranslationMatrix.set(3, 4, -translationZzero);
    }

    @Override
	protected Map<String, Object> getCoordinates() {
		Map<String, Object> coordinates = super.getCoordinates();
		coordinates.put("zZero", getZZero());
		coordinates.put("zscale", getZscale());
		coordinates.put("xAngle", getAngleA());
		coordinates.put("zAngle", getAngleB());

		return coordinates;
	}

	private void dispatch3DViewChangeEvent() {
		app.dispatchEvent(new Event(EventType.VIEW_CHANGED_3D)
				.setJsonArgument(getCoordinates()));
	}

	/**
	 * Update scale and rotation matrices.
	 */
	public void updateRotationAndScaleMatrices() {
		// rotations
		updateRotationMatrix();

		undoRotationMatrix.set(rotationMatrix.inverse());

		// scaling
		updateScaleMatrix();
		undoScaleMatrix.set(1, 1, 1 / getXscale());
		undoScaleMatrix.set(2, 2, 1 / getYscale());
		undoScaleMatrix.set(3, 3, 1 / getZscale());

		rotationAndScaleMatrix = rotationMatrix.mul(scaleMatrix);
	}

	/**
	 * @return current rotation matrix
	 */
	public CoordMatrix getRotationMatrix() {
		return rotationMatrix;
	}

	/**
	 * Set global matrices.
	 */
	public void setGlobalMatrices() {
		mWithoutScale.setMul(rotationMatrix, translationMatrixWithScale);

		mWithScale.setMul(rotationAndScaleMatrix, translationMatrixWithoutScale);

		tmpMatrix1.setMul(undoScaleMatrix, undoRotationMatrix);
		mToScene.setMul(undoTranslationMatrix, tmpMatrix1);
		mInvTranspose.setTranspose(mToScene);

		updateEye();
	}

	@Override
	public void updateMatrix() {
		// rotations and scaling
		updateRotationAndScaleMatrices();

		// translation
		updateTranslationMatrices();

		// set global matrix and inverse, and eye position
		setGlobalMatrices();
	}

	private void updateEye() {
		// update view direction
		if (projection == PROJECTION_OBLIQUE) {
			viewDirection.set3(renderer.getObliqueOrthoDirection());
		} else {
			viewDirection.set3(Coords.VZm);
		}
		toSceneCoords3D(viewDirection);
		viewDirection.normalize();

		// update eye position
		if (projection == PROJECTION_ORTHOGRAPHIC
				|| projection == PROJECTION_OBLIQUE) {
			eyePosition = viewDirection;
		} else {
			eyePosition = renderer.getPerspEye().copyVector();
			toSceneCoords3D(eyePosition);
		}
	}

	// ////////////////////////////////////
	// update

	/**
	 *
	 * @return ortho direction of the eye
	 */
	public Coords getViewDirection() {
		if (projection == PROJECTION_ORTHOGRAPHIC
				|| projection == PROJECTION_OBLIQUE) {
			return viewDirection;
		}

		return viewDirectionPersp;
	}

	/**
	 * Gives direction vector from user input, e.g. if user clicks on screen, it
	 * will return the user-to-screen vector in ggb scene coordinate system; for
	 * orthographic projection, the vector will be orthogonal to the screen.
	 * 
	 * @param ret
	 *            returned direction
	 */
	final public void getHittingDirection(Coords ret) {
		if (mIsXREnabled) {
			renderer.getHittingDirectionAR(ret);
		} else {
			getCompanion().getHittingDirection(ret);
		}
	}

	/**
	 * @return eye position
	 */
	@Override
	public Coords getEyePosition() {
		return eyePosition;
	}

	@Override
	public void shiftRotAboutZ(double da) {
		setRotXYinDegrees(aOld + da, bOld);
		updateRotation();
	}

	@Override
	public void shiftRotAboutY(double db) {
		setRotXYinDegrees(aOld, bOld + db);
		updateRotation();
	}

	private void updateRotation() {
		updateRotationAndScaleMatrices();
		setGlobalMatrices();
		setViewChangedByRotate();
		setWaitForUpdate();
	}

	@Override
	public void setRotXYinDegrees(double a, double b) {
		// Log.debug("setRotXY: "+a+","+b);
		if (Double.isNaN(a) || Double.isNaN(b)) {
			Log.error("NaN values for setRotXYinDegrees");
			return;
		}

		this.a = a;
		this.b = b;

		if (this.b > EuclidianController3D.ANGLE_MAX) {
			this.b = EuclidianController3D.ANGLE_MAX;
		} else if (this.b < -EuclidianController3D.ANGLE_MAX) {
			this.b = -EuclidianController3D.ANGLE_MAX;
		}
		this.getSettings().setRotXYinDegreesFromView(a, b);

	}

	@Override
	public EuclidianSettings3D getSettings() {
		return (EuclidianSettings3D) super.getSettings();
	}

	/**
	 * Sets coord system from mouse move
	 */
	@Override
	final public void translateCoordSystemInPixels(int dx, int dy, int dz) {
		setXZero(xZeroOld + dx / getSettings().getXscale());
		setYZero(yZeroOld - dy / getSettings().getYscale());
		setZZero(zZeroOld + dz / getSettings().getZscale());

		getSettings().updateOriginFromView(getXZero(), getYZero(), getZZero());
		updateMatrix();
		setViewChangedByTranslate();
		setWaitForUpdate();
	}

	@Override
	final public void pageUpDownTranslateCoordSystem(int height) {
		translateCoordSystemInPixels(0, 0, height / 100);
	}

	/**
	 * Sets coord system from mouse move
	 */
	@Override
	final public void setCoordSystemFromMouseMove(int dx, int dy, int mode) {
		animator.setCoordSystemFromMouseMove(dx, dy, mode);
	}

	final public void setCoordSystemFromAxisScale(double factor,
			double scaleOld, int mode) {
		animator.setCoordSystemFromAxisScale(factor, scaleOld, mode);
	}

	/**
	 * @return the zminObject
	 */
	public GeoNumeric getZminObject() {
		return (GeoNumeric) zminObject;
	}

	/**
	 * @param zminObjectNew
	 *            the zminObject to set
	 */
	public void setZminObject(NumberValue zminObjectNew) {
		if (zminObject != null) {
			((GeoNumeric) zminObject).removeEVSizeListener(this);
		}
		if (zminObjectNew == null && kernel.getConstruction() != null) {
			this.zminObject = new GeoNumeric(kernel.getConstruction());
			updateBoundObjects();
		} else {
			this.zminObject = zminObjectNew;
		}
		setSizeListeners();
	}

	/**
	 * @return the zmaxObject
	 */
	public GeoNumeric getZmaxObject() {
		return (GeoNumeric) zmaxObject;
	}

	/**
	 * @param zmaxObjectNew
	 *            the zmaxObject to set
	 */
	public void setZmaxObject(NumberValue zmaxObjectNew) {
		if (zmaxObject != null) {
			((GeoNumeric) zmaxObject).removeEVSizeListener(this);
		}
		if (zmaxObjectNew == null && kernel.getConstruction() != null) {
			this.zmaxObject = new GeoNumeric(kernel.getConstruction());
			updateBoundObjects();
		} else {
			this.zmaxObject = zmaxObjectNew;
		}
		setSizeListeners();
	}

	/*
	 * TODO interaction - note : methods are called by
	 * EuclidianRenderer3D.viewOrtho() to re-center the scene
	 */
	@Override
	public double getXZero() {
		return xZero;
	}

	/**
	 * set the x-coord of the origin
	 *
	 * @param val
	 *            x-coord of the origin
	 */
	@Override
	public void setXZero(double val) {
		xZero = val;
	}

	@Override
	public double getYZero() {
		return yZero;
	}

	/**
	 * set the y-coord of the origin
	 *
	 * @param val
	 *            y-coord of the origin
	 */
	@Override
	public void setYZero(double val) {
		yZero = val;
	}

	/**
	 * @return the z-coord of the origin
	 */
	@Override
	public double getZZero() {
		return zZero;
	}

	/**
	 * set the z-coord of the origin
	 *
	 * @param val
	 *            z-coord of the origin
	 */
	@Override
	public void setZZero(double val) {
		zZero = val;
	}

	@Override
	public void setZeroFromXML(double x, double y, double z) {
		if (app.fileVersionBefore(new int[] { 4, 9, 14, 0 })) {
			// new matrix multiplication (since 4.9.14)
			updateRotationMatrix();
			updateScaleMatrix();
			setXZero(x);
			setYZero(y);
			setZZero(z);
			getSettings().updateOriginFromView(x, y, z);
			updateTranslationMatrices();
			CoordMatrix mRS = rotationMatrix.mul(scaleMatrix);
			CoordMatrix matrix = ((mRS.inverse())
					.mul(translationMatrixWithoutScale).mul(mRS));
			Coords origin = matrix.getOrigin();
			setXZero(origin.getX());
			setYZero(origin.getY());
			setZZero(origin.getZ());
			updateMatrix();
			return;
		}

		setXZero(x);
		setYZero(y);
		setZZero(z);
	}

	public double getXRot() {
		return a;
	}

	public double getZRot() {
		return b;
	}

	@Override
	public double getXmin() {
		return clippingCubeDrawable.getMinMax()[0][0];
	}

	@Override
	public double getXmax() {
		return clippingCubeDrawable.getMinMax()[0][1];
	}

	@Override
	public double getYmin() {
		return clippingCubeDrawable.getMinMax()[1][0];
	}

	@Override
	public double getYmax() {
		return clippingCubeDrawable.getMinMax()[1][1];
	}

	@Override
	public double getZmin() {
		return clippingCubeDrawable.getMinMax()[2][0];
	}

	// ////////////////////////////////////////////
	// EuclidianViewInterface

	@Override
	public double getZmax() {
		return clippingCubeDrawable.getMinMax()[2][1];
	}

	/**
	 *
	 * @return coords of the center point
	 */
	public Coords getCenter() {
		return clippingCubeDrawable.getCenter();
	}

	/**
	 * @return max value from center to one FRUSTUM edge
	 */
	public double getFrustumRadius() {
		return clippingCubeDrawable.getFrustumRadius();
	}

	/**
	 * @return min value from center to one FRUSTUM face
	 */
	public double getFrustumInteriorRadius() {
		return clippingCubeDrawable.getFrustumInteriorRadius();
	}

	@Override
	public double getXscale() {
		return getSettings().getXscale();
	}

	@Override
	public double getYscale() {
		return getSettings().getYscale();
	}

	/**
	 * @return the z-scale
	 */
	@Override
	public double getZscale() {
		return getSettings().getZscale();
	}

	@Override
	public double getScale(int i) {
		switch (i) {
		case 0:
		default:
			return getXscale();
		case 1:
			return getYscale();
		case 2:
			return getZscale();
		}
	}

	@Override
	protected void setAxesIntervals(double scale, int axis) {
		super.setAxesIntervals(scale, axis);
		axisDrawable[axis].setLabelWaitForUpdate();
		setWaitForUpdate();
	}

	/**
	 * @return the all-axis scale
	 */
	public double getScale() {
		return getSettings().getXscale();
	}

	public double getMaxScale() {
		return getSettings().getMaxScale();
	}

	/**
	 * @param p1
	 *            start point
	 * @param p2
	 *            end point
	 * @return size of scaled vector p1-p2
	 */
	public double getScaledDistance(Coords p1, Coords p2) {
		tmpCoordsLength3.setSub(p1, p2);
		scaleXYZ(tmpCoordsLength3);
		tmpCoordsLength3.calcNorm();
		return tmpCoordsLength3.getNorm();
	}

	/**
	 * set the all-axis scale
	 */
	final public void setScale(double xscale, double yscale, double zscale) {
		getSettings().setXscaleValue(xscale);
		getSettings().setYscaleValue(yscale);
		getSettings().setZscaleValue(zscale);
		setViewChangedByZoom();
	}

	/** remembers the origins values (xzero, ...) */
	@Override
	public void rememberOrigins() {
		super.rememberOrigins();

		aOld = a;
		bOld = b;
		zZeroOld = getZZero();

		animator.rememberOrigins();
	}

	/**
	 * Update animation.
	 */
	public void updateAnimation() {
		if (isAnimated()) {
			animator.animate();
			setWaitForUpdate();
		}
	}

	/**
	 * update the drawables for 3D view
	 */
	public void update() {
		updateAnimation();

		if (waitForUpdate || !drawable3DListToBeRemoved.isEmpty()
				|| !drawable3DListToBeAdded.isEmpty()) {
			// drawList3D.updateAll();

			// I've placed remove() before add(), otherwise when the two lists
			// contains the same element, the element will NOT be added. ---Tam,
			// 2011/7/15
			drawable3DLists.remove(drawable3DListToBeRemoved);
			drawable3DListToBeRemoved.clear();

			// add drawables (for preview)
			drawable3DLists.add(drawable3DListToBeAdded);
			drawable3DListToBeAdded.clear();

			// add geos
			for (GeoElement geo : geosToBeAdded) {
				addNow(geo);
			}
			geosToBeAdded.clear();

			viewChangedOwnDrawables();
			// setWaitForUpdateOwnDrawables();

			waitForUpdate = false;
		}

		// update decorations
		pointDecorations.update();

		getCompanion().update();
	}

	@Override
	public void setWaitForUpdate() {
		waitForUpdate = true;
	}

	// ////////////////////////////////////////////////
	// ANIMATION
	// ////////////////////////////////////////////////

	/**
	 * (x,y) 2D screen coords -> 3D physical coords
	 *
	 * @param mouse
	 *            pointer position
	 * @param ret
	 *            TODO
	 */
	public void getPickPoint(GPoint mouse, Coords ret) {

		setPickPointFromMouse(mouse);

		if (projection == PROJECTION_PERSPECTIVE
				|| projection == PROJECTION_GLASSES) {
			viewDirectionPersp.setSub3(pickPoint, renderer.getPerspEye());
			toSceneCoords3D(viewDirectionPersp);
			viewDirectionPersp.normalize();
		}

		ret.set4(pickPoint);
	}

	/**
	 * Gives origin coordinates from user input, e.g. if user clicks on screen,
	 * it will return the click coordinates in ggb scene coordinate system. The
	 * depth position is calculated to be between the user and scene objects.
	 * 
	 * @param mouse
	 *            mouse position
	 * @param ret
	 *            returned origin
	 */
	final public void getHittingOrigin(GPoint mouse, Coords ret) {
		if (isXREnabled()) {
			renderer.getHittingOriginAR(ret);
		} else {
			getCompanion().getHittingOrigin(mouse, ret);
		}
	}

	/**
	 * @param mouse
	 *            mouse position
	 * @param result
	 *            mouse position with (0,0) on window center
	 */
	public void setCenteredPosition(GPoint mouse, GPoint result) {
		result.x = mouse.getX() + renderer.getLeft();
		result.y = -mouse.getY() + renderer.getTop();
	}

	protected void setPickPointFromMouse(GPoint mouse) {
		getCompanion().setPickPointFromMouse(mouse, pickPoint);
	}

	/**
	 * p scene coords, (dx,dy) 2D mouse move -> 3D physical coords
	 *
	 * @param p
	 *            coords
	 * @param dx
	 *            mouse movement in x
	 * @param dy
	 *            mouse movement in y
	 * @param ret
	 *            3D physical coords
	 */
	public void getPickFromScenePoint(Coords p, int dx, int dy, Coords ret) {

		Coords point = getToScreenMatrix().mul(p);

		pickPoint.setX(point.get(1) + dx);
		pickPoint.setY(point.get(2) - dy);

		if (projection == PROJECTION_PERSPECTIVE
				|| projection == PROJECTION_GLASSES) {
			viewDirectionPersp.setSub3(pickPoint, renderer.getPerspEye());
			toSceneCoords3D(viewDirectionPersp);
			viewDirectionPersp.normalize();
		}

		ret.set4(pickPoint);
	}

	/**
	 * attach the view to the kernel
	 */
	@Override
	public void attachView() {
		kernel3D.notifyAddAll(this);
		kernel3D.attach(this);
	}

	@Override
	public void clearView() {
		// clear lists
		drawable3DLists.clear();
		geosToBeAdded.clear();
		drawable3DListToBeAdded.clear();
		drawable3DMap.clear();
		setRotContinueAnimation(0, 0);
		initView(false);
	}

	@Override
	protected void initView(boolean repaint) {
		super.initView(repaint);
		setBackground(GColor.WHITE);
		updateMatrix();
	}

	/**
	 * remove a GeoElement3D from this view
	 */
	@Override
	public void remove(GeoElement geo) {
		if (geo.hasDrawable3D()) {
			Drawable3D d = drawable3DMap.get(geo);
			remove(d);
		}

		drawable3DMap.remove(geo);
		geosToBeAdded.remove(geo);

		repaintView();
	}

	/**
	 * remove the drawable d
	 *
	 * @param d
	 *            drawable
	 */
	public void remove(Drawable3D d) {
		drawable3DListToBeAdded.remove(d);
		drawable3DListToBeRemoved.add(d);
	}

	@Override
	public void rename(GeoElement geo) {
		// TODO auto-generated
	}

	/**
	 * says we want a new repaint after current repaint
	 */
	public void waitForNewRepaint() {
		// nothing done here, see EuclidianView3DW
	}

	@Override
    final public void reset() {
	    reset(false);
    }

    /**
     * reset view
     * @param clearClippingEnlargement if we want to clear clipping cube enlargement
     */
	public void reset(boolean clearClippingEnlargement) {
		resetAllDrawables(clearClippingEnlargement);
		setViewChanged();
		viewChangedOwnDrawables();
		setWaitForUpdate();
	}

	@Override
	public void update(GeoElement geo) {
		if (geo.hasDrawable3D()) {
			Drawable3D d = drawable3DMap.get(geo);
			// ((GeoElement3DInterface) geo).getDrawable3D();
			// Application.debug(d);
			if (d != null) {
				update(d);
				// update(((GeoElement3DInterface) geo).getDrawable3D());
			}
		}
	}

	@Override
	public void updateVisualStyle(GeoElement geo, GProperty prop) {
		// Application.debug(geo);
		if (geo.hasDrawable3D()) {
			Drawable3D d = drawable3DMap.get(geo);
			if (d != null) {
				d.setWaitForUpdateVisualStyle(prop);
			}
		}

		if (styleBar != null) {
			styleBar.updateVisualStyle(geo);
		}
	}

	@Override
	public void updateAllDrawables() {
		for (Drawable3D d : drawable3DMap.values()) {
			update(d);
		}
		setWaitForUpdateOwnDrawables();
	}

	/**
	 * says this drawable to be updated
	 *
	 * @param d
	 *            drawable
	 */
	public void update(Drawable3D d) {
		d.setWaitForUpdate();
	}

	@Override
	public DrawableND getDrawableND(GeoElement geo) {
		if (geo.hasDrawable3D()) {

			return drawable3DMap.get(geo);
		}

		return null;
	}

	@Override
	final public GeoElement getLabelHit(GPoint p, PointerEventType type) {
		return getCompanion().getLabelHit(p, type);
	}

	@Override
	public Previewable getPreviewDrawable() {
		return previewDrawable;
	}

	@Override
	public boolean getShowMouseCoords() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setShowMouseCoords(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getShowXaxis() {
		return axis[AXIS_X].isEuclidianVisible();
	}

	@Override
	public boolean getShowYaxis() {
		return axis[AXIS_Y].isEuclidianVisible();
	}

	@Override
	public boolean setShowAxis(int axis, boolean flag, boolean update) {
		boolean old = this.axis[axis].isEuclidianVisible();
		this.axis[axis].setEuclidianVisible(flag);
		return flag != old;
	}

	@Override
	public boolean setShowAxes(boolean flag, boolean update) {
		boolean changedX = setShowAxis(AXIS_X, flag, false);
		boolean changedY = setShowAxis(AXIS_Y, flag, false);
		return setShowAxis(AXIS_Z, flag, true) || changedX || changedY;
	}

	@Override
	public void setShowPlate(boolean flag) {
		getxOyPlane().setPlateVisible(flag);
	}

	/**
	 * sets the visibility of xOy plane grid
	 *
	 * @param flag
	 *            show grid?
	 */
	@Override
	public boolean setShowGrid(boolean flag) {
		boolean changed = getxOyPlane().setGridVisible(flag);
		xOyPlaneDrawable.setWaitForUpdate();
		return changed;
	}

	@Override
	public int getViewHeight() {
		return getHeight();
	}

	@Override
	public int getViewWidth() {
		return getWidth();
	}

	@Override
	public boolean hitAnimationButton(int x, int y) {
		return false;
	}

	@Override
	public void resetMode() {
		// TODO Auto-generated method stub

	}

	/** @return whether the view is under animation */
	public boolean isAnimated() {
		return animator.getAnimationType() != AnimationType.OFF;
	}

	/**
	 * tells if the view is under rot animation
	 *
	 * @return true if there is a rotation animation
	 */
	public boolean isRotAnimated() {
		return isRotAnimatedContinue()
				|| animator.getAnimationType() == AnimationType.ROTATION;
	}

	/**
	 * @return true if there is a continue rotation animation
	 */
	public boolean isRotAnimatedContinue() {
		return animator.getAnimationType() == AnimationType.CONTINUE_ROTATION;
	}

	/**
	 *
	 * @param p
	 *            point
	 * @return true if the point is between min-max coords values
	 */
	public boolean isInside(Coords p) {
		double val = p.getX();
		if (val < getXmin() || val > getXmax()) {
			return false;
		}

		val = p.getY();
		if (val < getYmin() || val > getYmax()) {
			return false;
		}

		val = p.getZ();
		if (val < getZmin() || val > getZmax()) {
			return false;
		}

		return true;
	}

	/**
	 * @return true if use of clipping cube
	 */
	public boolean useClippingCube() {
		return getSettings().useClippingCube();
	}

	// ///////////////////////////////////////
	// previewables

	@Override
	public void setUseClippingCube(boolean flag) {
		getSettings().setUseClippingCube(flag);
		updateUseClippingCube();
	}

	private void updateUseClippingCube() {
		renderer.setEnableClipPlanes(useClippingCube());
		setViewChanged();
		setWaitForUpdate();
	}

	/**
	 * @return true if clipping cube is shown
	 */
	public boolean showClippingCube() {
		return getSettings().showClippingCube();
	}

	/**
	 * sets if the clipping cube is shown
	 *
	 * @param flag
	 *            flag
	 */
	@Override
	public void setShowClippingCube(boolean flag) {
		getSettings().setShowClippingCube(flag);
		setWaitForUpdate();
	}

	/**
	 * toggle show/hide clipping
	 */
	public void toggleShowAndUseClippingCube() {
		boolean flag = showClippingCube() || useClippingCube();
		setShowClippingCube(!flag);
		setUseClippingCube(!flag);
	}

	/**
	 * @return the reduction of the clipping box
	 */
	public int getClippingReduction() {
		return clippingCube.getReduction();
	}

	/**
	 * sets the reduction of the clipping box
	 *
	 * @param value
	 *            reduction
	 */
	@Override
	public void setClippingReduction(int value) {
		clippingCube.setReduction(value);
		setViewChanged();
		setWaitForUpdate();
	}

	@Override
	public void setAnimatedCoordSystem(double x0, double y0, int steps,
									   boolean storeUndo) {
		setAnimatedCoordSystem(steps);
	}

	private void setAnimatedCoordSystem(int steps) {
		animator.setAnimatedCoordSystem(XZERO_SCENE_STANDARD, YZERO_SCENE_STANDARD,
				ZZERO_SCENE_STANDARD, SCALE_STANDARD, steps);
	}

	@Override
	public void setAnimatedCoordSystem(double ox, double oy, double f,
			double newScale, int steps, boolean storeUndo) {

		animator.setAnimatedCoordSystem(newScale);
	}

	/**
	 * sets a continued animation for rotation if delay is too long, no
	 * animation if speed is too small, no animation
	 *
	 * @param delay
	 *            delay since last drag
	 * @param rotSpeed
	 *            speed of rotation
	 */
	public void setRotContinueAnimation(double delay, double rotSpeed) {
		animator.setRotContinueAnimation(delay, rotSpeed);
	}

    /**
     * start a rotation animation to be in the vector direction (AR)
     *
     * @param vn
     *            vector direction
     */
    public void setRotAnimationAR(Coords vn) {
        CoordMatrixUtil.sphericalCoords(vn, tmpCoordsLength3);
        double angle = tmpCoordsLength3.get(2);
        getHittingDirection(tmpCoordsLength4);
        CoordMatrixUtil.sphericalCoords(tmpCoordsLength4, tmpCoordsLength3);
        angle = (a + (angle - tmpCoordsLength3.get(2)) * 180 / Math.PI + 180) % 360;
        if (angle > 180) {
            angle -= 360;
        }
		setRotAnimation(angle, b, true, true);
    }

	@Override
	public void setRotAnimation(Coords vn, boolean checkSameValues,
			boolean animated) {
		tmpCoords1.set3(vn);
		scaleXYZ(tmpCoords1);
		CoordMatrixUtil.sphericalCoords(tmpCoords1, tmpCoordsLength3);
		setRotAnimation(tmpCoordsLength3.get(2) * 180 / Math.PI,
				tmpCoordsLength3.get(3) * 180 / Math.PI, checkSameValues,
				animated);
	}

	@Override
	public void setRotAnimation(double rotOz, boolean checkSameValues,
			boolean animated) {
		setRotAnimation(rotOz * 180 / Math.PI, this.b, checkSameValues,
				animated);
	}

	/**
	 * start a rotation animation to be in the vector direction
	 *
	 * @param vn
	 *            vector direction
	 */
	public void setRotAnimation(Coords vn) {
		setRotAnimation(vn, true, true);
	}

	@Override
	public void setClosestRotAnimation(Coords v, boolean animated) {
		if (v.dotproduct(getViewDirection()) > 0) {
			setRotAnimation(v.mul(-1), true, animated);
		} else {
			setRotAnimation(v, true, animated);
		}
	}

	/**
	 * rotate to default
	 */
	@Override
	public void setDefaultRotAnimation() {
		getCompanion().setDefaultRotAnimation();
	}

	/**
	 * start a rotation animation to go to the new values
	 *
	 * @param aN
	 *            new Oz angle
	 * @param bN
	 *            new xOy angle
	 * @param checkSameValues
	 *            if true, check new values are same than old, in this case
	 *            revert the view
	 */
	public void setRotAnimation(double aN, double bN, boolean checkSameValues) {
		setRotAnimation(aN, bN, checkSameValues, true);
	}

	/**
	 * start a rotation animation to go to the new values
	 *
	 * @param aN
	 *            new Oz angle
	 * @param bN
	 *            new xOy angle
	 * @param checkSameValues
	 *            if true, check new values are same than old, in this case
	 *            revert the view
	 * @param animated
	 *            whether to use multiple steps
	 */
	public void setRotAnimation(double aN, double bN, boolean checkSameValues,
			boolean animated) {
		animator.setRotAnimation(aN, bN, checkSameValues, animated, false);
	}

	/**
	 * stops the animations
	 */
	public void stopAnimation() {
		animator.stopAnimation();
	}

	@Override
	public boolean isZeroStandard() {
		return DoubleUtil.isEqual(xZero, XZERO_SCENE_STANDARD)
				&& DoubleUtil.isEqual(yZero, YZERO_SCENE_STANDARD)
				&& DoubleUtil.isEqual(zZero, ZZERO_SCENE_STANDARD)
				&& DoubleUtil.isEqual(a, ANGLE_ROT_OZ)
				&& DoubleUtil.isEqual(b, ANGLE_ROT_XOY);
	}

	@Override
	public void setHits(GPoint p, PointerEventType type) {
	    if (isXREnabled() && ((EuclidianController3D) euclidianController)
                .isCurrentModeForCreatingPoint()) {
            renderer.setHits(p, getCapturingThreshold(PointerEventType.MOUSE));
        } else {
            renderer.setHits(p, getCapturingThreshold(type));
            if (type == PointerEventType.TOUCH
                    && hitsEmptyOrOnlyContainsXOYPlane()) {
                renderer.setHits(p, getCapturingThreshold(type) * 3);
            }
        }

		hasMouse = true;
		updateCursor3D();
	}

	private boolean hitsEmptyOrOnlyContainsXOYPlane() {
		if (hits.size() == 0) {
			return true;
		}

		if (hits.size() == 1) {
			return hits.get(0) == getxOyPlane();
		}

		return false;
	}

	public int getCapturingThreshold(PointerEventType type) {
		return getCompanion().getCapturingThreshold(type);
	}

	public DrawAxis3D getAxisDrawable(int i) {
		return axisDrawable[i];
	}

	public DrawPlane3D getPlaneDrawable() {
		return xOyPlaneDrawable;
	}

	public Hits3D getHits3D() {
		return hits;
	}

	@Override
	public Hits getHits() {
		return hits.cloneHits();
	}

	/**
	 * init the hits for this view
	 *
	 * @param hits
	 *            hits
	 */
	public void setHits(Hits3D hits) {
		this.hits = hits;
	}

	@Override
	public void updateCursor(GeoPointND point) {
		hits.init();
		hits.add((GeoElement) point);
		updateCursor3D();
	}

	@Override
	public void setSelectionRectangle(GRectangle selectionRectangle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setShowAxesRatio(boolean b) {
		// TODO Auto-generated method stub

	}

	// ///////////////////////////////////////////////////
	//
	// POINT DECORATION
	//
	// ///////////////////////////////////////////////////

	@Override
	public void zoom(double px, double py, double zoomFactor, int steps,
			boolean storeUndo) {
		animator.zoom(zoomFactor);
	}

	@Override
	public void zoomAxesRatio(double zoomFactorY, double zoomFactorZ) {
		animator.zoomAxesRatio(zoomFactorY, zoomFactorZ);
	}

	/**
	 * return the point used for 3D cursor
	 *
	 * @return the point used for 3D cursor
	 */
	public GeoCursor3D getCursor3D() {
		return cursor3D;
	}

	/**
	 * @return the point used for cursor on xOy plane
	 */
	public GeoPoint3D getCursorOnXOYPlane() {
		return cursorOnXOYPlane;
	}

	// ///////////////////////////////////////////////////
	//
	// CURSOR
	//
	// ///////////////////////////////////////////////////

	/**
	 * @return the type of the cursor
	 */
	public int getCursor3DType() {
		return cursor3DType;
	}

	/**
	 * sets the type of the cursor
	 *
	 * @param v
	 *            cursor type
	 */
	public void setCursor3DType(int v) {
		cursor3DType = v;
		cursor3DVisible = true;
		if (euclidianController.isCreatingPointAR()) {
			target.updateType(this);
        }
	}

	@Override
	public void setCursor3DVisible(boolean flag) {
		cursor3DVisible = flag;
	}

	/**
	 * 
	 * @return true if 3D cursor is visible
	 */
	public boolean isCursor3DVisible() {
		return cursor3DVisible;
	}

	/**
	 * Update intersection thickness.
	 *
	 * @param a
	 *            intersecting object
	 * @param b
	 *            intersecting object
	 */
	public void setIntersectionThickness(GeoElement a, GeoElement b) {
		intersectionThickness = Math.max(a.getLineThickness(),
				b.getLineThickness())
				+ EuclidianStyleConstants.PREVIEW_POINT_ENLARGE_SIZE_FOR_INTERSECTION;
	}

	public int getIntersectionThickness() {
		return intersectionThickness;
	}

	public GeoPointND getIntersectionPoint() {
		return intersectionPoint;
	}

	public void setIntersectionPoint(GeoPointND point) {
		intersectionPoint = point;
	}

	/**
	 * @return the list of 3D drawables
	 */
	public Drawable3DListsForView getDrawList3D() {
		return drawable3DLists;
	}

	@Override
	public Previewable createPreviewLine(ArrayList<GeoPointND> selectedPoints) {
		return new DrawLine3D(this, selectedPoints);
	}

	@Override
	public Previewable createPreviewSegment(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawSegment3D(this, selectedPoints);
	}

	@Override
	public Previewable createPreviewRay(ArrayList<GeoPointND> selectedPoints) {
		return new DrawRay3D(this, selectedPoints);
	}

	@Override
	public Previewable createPreviewVector(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawVector3D(this, selectedPoints);
	}

	@Override
	public Previewable createPreviewPolygon(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawPolygon3D(this, selectedPoints);
	}

	/**
	 * @param selectedPoints
	 *            selected points
	 * @param selectedPolygons
	 *            selected polygons
	 * @param mode
	 *            app mode
	 * @return preview polyhedron
	 */
	public Previewable createPreviewPyramidOrPrism(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoPolygon> selectedPolygons, int mode) {
		return new DrawPolyhedron3D(this, selectedPoints, selectedPolygons,
				mode);
	}

	@Override
	public Previewable createPreviewConic(int mode,
			ArrayList<GeoPointND> selectedPoints) {
		return null;
	}

	/**
	 * @param selectedPoints
	 *            selected points
	 * @return a preview sphere (center-point)
	 */
	public Previewable createPreviewSphere(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawQuadric3D(this, selectedPoints,
				GeoQuadricNDConstants.QUADRIC_SPHERE);
	}

	/**
	 * @return a preview right prism/cylinder (basis and height)
	 */
	public Previewable createPreviewExtrusion(
			ArrayList<GeoPolygon> selectedPolygons,
			ArrayList<GeoConicND> selectedConics) {
		return new DrawExtrusion3D(this, selectedPolygons, selectedConics);
	}

	public Previewable createPreviewSurfaceOfRevolution(
			ArrayList<Path> selectedPaths) {
		return new DrawSurfaceOfRevolution(this, selectedPaths);
	}

	/**
	 * @return a preview pyramid/cone (basis and height)
	 */
	public Previewable createPreviewConify(
			ArrayList<GeoPolygon> selectedPolygons,
			ArrayList<GeoConicND> selectedConics) {
		return new DrawConify3D(this, selectedPolygons, selectedConics);
	}

	@Override
	public void updatePreviewable() {
		if (previewDrawable != null) {
			if (getCursor3DType() != PREVIEW_POINT_NONE) {
				previewDrawable.updatePreview();
			}
		}
	}

	@Override
	public void updatePreviewableForProcessMode() {
		if (previewDrawable != null) {
			updatePreviewable();
		}
	}

	/**
	 * update the 3D cursor with current hits
	 *
	 * @param hits1
	 *            hits
	 */
	public void updateCursor3D(Hits hits1) {
		if (hasMouse()) {
			getEuclidianController().updateNewPoint(true, hits1, true, true,
					!EuclidianConstants.isMoveOrSelectionMode(getMode()), // TODO
																// doSingleHighlighting
																// = false ?
					false, false);

			updateCursorOnXOYPlane();

			updateMatrixForCursor3D();
		}
	}

	private void updateCursorOnXOYPlane() {
		cursorOnXOYPlane.setWillingCoords(getCursor3D().getCoords());
		getHittingDirection(tmpCoords1);
		cursorOnXOYPlane.setWillingDirection(tmpCoords1);
		cursorOnXOYPlane.doRegion();
		cursorOnXOYPlane.getDrawingMatrix().setDiag(1);
		scaleXYZ(cursorOnXOYPlane.getDrawingMatrix().getOrigin());
	}

	/**
	 * Update cursor on xOy plane
	 */
	public void switchMoveCursor() {
		if (moveCursorIsVisible()) {
			cursorOnXOYPlane.switchMoveMode(getMode());
		}
	}

	final protected boolean moveCursorIsVisible() {
		return getCompanion().moveCursorIsVisible();
	}

	/**
	 * update the 3D cursor with current hits
	 */
	public void updateCursor3D() {
		// updateCursor3D(getHits().getTopHits());

		// we also want to see different pick orders in preview, e.g. line/plane
		// intersection
		// For now we follow the practice of EView2D: we reserve only points if
		// there are any,
		// and return the clone if there are no points.
		// TODO: define this behavior better
		if (getHits().containsGeoPoint()) {
			updateCursor3D(getHits().getTopHits());
		} else {
			updateCursor3D(getHits());
		}

	}

	private void flipCursorNormal() {
	    if (isXREnabled()) {
	        getHittingDirection(tmpCoordsLength4);
            if (cursorNormal.dotproduct3(tmpCoordsLength4) > 0) {
                cursorNormal.mulInside(-1);
            }
        } else {
            Coords direction = getViewDirection();
            if (direction != null && cursorNormal.dotproduct3(direction) > 0) {
                cursorNormal.mulInside(-1);
            }
        }
    }

	/**
	 * update cursor3D matrix
	 */
	public void updateMatrixForCursor3D() {
	    if (isXREnabled() && !isXRDrawing()) {
	        return;
        }
		double t;
		if (getEuclidianController()
				.getMode() == EuclidianConstants.MODE_VIEW_IN_FRONT_OF) {

			switch (getCursor3DType()) {

			default:
				// do nothing
				break;
			case PREVIEW_POINT_REGION:
				// use region drawing directions for the cross
				cursorNormal.set3(getCursor3D().getMoveNormalDirection());
				flipCursorNormal();
				scaleNormalXYZ(cursorNormal);
				cursorNormal.normalize();
				CoordMatrix4x4.createOrthoToDirection(
						getCursor3D().getDrawingMatrix().getOrigin(),
						cursorNormal, CoordMatrix4x4.VZ, tmpCoords1, tmpCoords2,
						cursorMatrix);
				scaleXYZ(cursorMatrix.getOrigin());
				break;
			case PREVIEW_POINT_PATH:
				// use path drawing directions for the arrow
				cursorMatrix.setOrigin(getCursor3D().getDrawingMatrix().getOrigin());
				scaleXYZ(cursorMatrix.getOrigin());
				cursorNormal.set3(((GeoElement) getCursor3D().getPath()).getMainDirection());
                flipCursorNormal();
				scaleXYZ(cursorNormal);
				cursorNormal.normalize();
				CoordMatrix4x4.createOrthoToDirection(getCursor3D().getDrawingMatrix().getOrigin(),
						cursorNormal, CoordMatrix4x4.VZ, tmpCoords1, tmpCoords2, cursorMatrix);
				scaleXYZ(cursorMatrix.getOrigin());
				break;
			}
		} else if (moveCursorIsVisible()) {
			if (cursor != EuclidianCursor.MOVE) {
				cursorMatrix.setOrigin(
						getCursor3D().getDrawingMatrix().getOrigin());
				scaleXYZ(cursorMatrix.getOrigin());
				switch (cursor) {
				default:
					// do nothing
					break;
				case RESIZE_X:
					cursorMatrix.setVx(Coords.VY);
					cursorMatrix.setVy(Coords.VZ);
					cursorMatrix.setVz(Coords.VX);
					break;
				case RESIZE_Y:
					cursorMatrix.setVx(Coords.VZ);
					cursorMatrix.setVy(Coords.VX);
					cursorMatrix.setVz(Coords.VY);
					break;
				case RESIZE_Z:
					cursorMatrix.setVx(Coords.VX);
					cursorMatrix.setVy(Coords.VY);
					cursorMatrix.setVz(Coords.VZ);
					break;
				}
			}
		} else {
			if (getEuclidianController().isCreatingPointAR()) {
				target.updateMatrices(this);
			}
			switch (getCursor3DType()) {

			default:
				// do nothing
				break;
			case PREVIEW_POINT_FREE:
				// use default directions for the cros
				cursorMatrix.setDiagonal3(1);
				cursorMatrix.setOrigin(getCursor3D().getDrawingMatrix().getOrigin());
				scaleXYZ(cursorMatrix.getOrigin());
				break;
			case PREVIEW_POINT_REGION:
				// use region drawing directions for the cross
				cursorNormal.set3(getCursor3D().getMoveNormalDirection());
				flipCursorNormal();
				scaleNormalXYZ(cursorNormal);
				cursorNormal.normalize();
				CoordMatrix4x4.createOrthoToDirection(getCursor3D().getDrawingMatrix().getOrigin(),
						cursorNormal, CoordMatrix4x4.VZ, tmpCoords1, tmpCoords2, cursorMatrix);
				scaleXYZ(cursorMatrix.getOrigin());
				break;
			case PREVIEW_POINT_PATH:
			case PREVIEW_POINT_REGION_AS_PATH:
				// use path drawing directions for the cross
				cursorMatrix.setOrigin(getCursor3D().getDrawingMatrix().getOrigin());
				scaleXYZ(cursorMatrix.getOrigin());
				GeoElement path = getCursorPath();
				cursorNormal.set3(path.getMainDirection());
				scaleXYZ(cursorNormal);
				cursorNormal.normalize();
				CoordMatrix4x4.completeOrtho(cursorNormal, tmpCoords1, tmpCoords2, cursorMatrix);
				t = 10 + path.getLineThickness();
				cursorMatrix.getVy().mulInside3(t);
				cursorMatrix.getVz().mulInside3(t);
				break;
			case PREVIEW_POINT_DEPENDENT:
				// use size of intersection
				cursorMatrix.setOrigin(
						getCursor3D().getDrawingMatrix().getOrigin());
				scaleXYZ(cursorMatrix.getOrigin());
				t = getIntersectionThickness();
				cursorMatrix.getVx().setMul(Coords.VX, t);
				cursorMatrix.getVy().setMul(Coords.VY, t);
				cursorMatrix.getVz().setMul(Coords.VZ, t);
				break;
			case PREVIEW_POINT_ALREADY:
				if (getCursor3D().isPointOnPath()) {
					cursorNormal.set3(((GeoElement) getCursor3D().getPath())
							.getMainDirection());
					scaleXYZ(cursorNormal);
					cursorNormal.normalize();

					CoordMatrix4x4.completeOrtho(cursorNormal, tmpCoords1,
							tmpCoords2, tmpMatrix1);

					cursorMatrix.setVx(tmpMatrix1.getVy());
					cursorMatrix.setVy(tmpMatrix1.getVz());
					cursorMatrix.setVz(tmpMatrix1.getVx());
					cursorMatrix.setOrigin(tmpMatrix1.getOrigin());

				} else if (getCursor3D().hasRegion()) {
					cursorNormal.set3(getCursor3D().getMoveNormalDirection());
					scaleNormalXYZ(cursorNormal);
					cursorNormal.normalize();
					CoordMatrix4x4.createOrthoToDirection(
							getCursor3D().getCoordsInD3(), cursorNormal,
							CoordMatrix4x4.VZ, tmpCoords1, tmpCoords2,
							cursorMatrix);
				} else {
					CoordMatrix4x4.identity(cursorMatrix);
				}

				cursorMatrix.setOrigin(
						getCursor3D().getDrawingMatrix().getOrigin());
				scaleXYZ(cursorMatrix.getOrigin());

				cursorMatrix.getVx().normalize();
				// use size of point
				t = Math.max(1, getCursor3D().getPointSize() / 6.0 + 0.5);
				cursorMatrix.getVx().mulInside3(t);
				cursorMatrix.getVy().mulInside3(t);
				cursorMatrix.getVz().mulInside3(t);
				break;
			}
		}
		// Application.debug("getCursor3DType()="+getCursor3DType());

	}

	public Coords getCursorNormal() {
		return cursorNormal;
	}

	// ///////////////////////////////////////////////////
	//
	// EUCLIDIANVIEW DRAWABLES (AXIS AND PLANE)
	//
	// ///////////////////////////////////////////////////

	/**
	 * 
	 * @return current cursor path
	 */
	public GeoElement getCursorPath() {
		if (getCursor3DType() == PREVIEW_POINT_PATH) {
			return (GeoElement) getCursor3D().getPath();
		}

		// PREVIEW_POINT_REGION_AS_PATH
		return (GeoElement) getCursor3D().getRegion();
	}

	@Override
	public void setPreview(Previewable previewDrawable) {
		if (this.previewDrawable == previewDrawable) {
			return;
		}

		if (this.previewDrawable != null) {
			this.previewDrawable.disposePreview();
		}

		if (previewDrawable instanceof Drawable3D) {
			if (((Drawable3D) previewDrawable).getGeoElement() != null) {
				addToDrawable3DLists((Drawable3D) previewDrawable);
			}
		}

		this.previewDrawable = previewDrawable;
	}

	private void initPointDecorations() {
		// Application.debug("hop");
		pointDecorations = new DrawPointDecorations(this);
	}

	/**
	 * update decorations for localizing point in the space
	 *
	 */
	public void updatePointDecorations() {
		pointDecorations.setWaitForUpdate();
	}

	/**
	 * set point for point decorations for localizing point in the space. If
	 * point==null, no decoration will be drawn
	 *
	 * @param point
	 *            point
	 */
	public void setPointDecorations(GeoPointND point) {
		getCompanion().setPointDecorations(point);
	}

	/**
	 * draws the mouse cursor (for glasses)
	 *
	 * @param renderer1
	 *            renderer
	 */
	final public void drawMouseCursor(Renderer renderer1) {
		getCompanion().drawMouseCursor(renderer1);
	}

	/**
	 * draw mouse cursor for location v
	 *
	 * @param renderer1
	 *            renderer
	 * @param v
	 *            location
	 */
	public void drawMouseCursor(Renderer renderer1, Coords v) {
		CoordMatrix4x4.identity(tmpMatrix4x4_3);

		tmpMatrix4x4_3.setOrigin(v);
		renderer1.setMatrix(tmpMatrix4x4_3);
		renderer1.drawMouseCursor();
	}

	protected void drawFreeCursor(Renderer renderer1) {
		getCompanion().drawFreeCursor(renderer1);
	}

	protected void drawTranslateViewCursor(Renderer renderer1) {

		getCompanion().drawTranslateViewCursor(renderer1, cursor,
				cursorOnXOYPlane, cursorMatrix);

	}

	/**
	 * draws the cursor
	 *
	 * @param renderer1
	 *            renderer
	 */
	public void drawCursor(Renderer renderer1) {
		if (companion3D != null && companion3D.shouldDrawCursor()) {
			if (shouldDrawCursorAtEnd()) {
				// draw here for hidden parts
				if (moveCursorIsVisible()) {
					drawTranslateViewCursor(renderer1);
				} else {
					drawTarget(renderer1);
				}
			} else {
				// mouse cursor
				if (moveCursorIsVisible()) {
					drawTranslateViewCursor(renderer1);
				} else if (!getEuclidianController().mouseIsOverLabel()
						&& ((EuclidianController3D) getEuclidianController())
								.cursor3DVisibleForCurrentMode(
										getCursor3DType())) {
					renderer1.setMatrix(cursorMatrix);

					switch (cursor) {
					case DEFAULT:
						switch (getCursor3DType()) {
						case PREVIEW_POINT_FREE:
							drawFreeCursor(renderer1);
							break;
						case PREVIEW_POINT_ALREADY: // showing arrows directions
							drawPointAlready(getCursor3D());
							break;
						default:
						case PREVIEW_POINT_NONE:
							// do nothing
							break;
						}
						break;
					case HIT:
						switch (getCursor3DType()) {
						default:
							// do nothing
							break;
						case PREVIEW_POINT_FREE:
							if (getCompanion().drawCrossForFreePoint()) {
								renderer1
										.drawCursor(PlotterCursor.Type.CROSS2D);
							}
							break;
						case PREVIEW_POINT_REGION:
							if (getEuclidianController()
									.getMode() == EuclidianConstants.MODE_VIEW_IN_FRONT_OF) {
								renderer1.drawViewInFrontOf();
							} else {
								renderer1
										.drawCursor(PlotterCursor.Type.CROSS2D);
							}
							break;
						case PREVIEW_POINT_PATH:
						case PREVIEW_POINT_REGION_AS_PATH:
							if (getEuclidianController()
									.getMode() == EuclidianConstants.MODE_VIEW_IN_FRONT_OF) {
								renderer1.drawViewInFrontOf();
							} else {
								renderer1.drawCursor(
										PlotterCursor.Type.CYLINDER);
							}
							break;
						case PREVIEW_POINT_DEPENDENT:
							renderer1.drawCursor(PlotterCursor.Type.DIAMOND);
							break;

						case PREVIEW_POINT_ALREADY:
							drawPointAlready(getCursor3D());
							break;
						}
						break;
					}
				}
			}
		}
	}

	/**
	 * draws the cursor at end of rendering pass
	 *
	 * @param renderer1
	 *            renderer
	 */
	public void drawCursorAtEnd(Renderer renderer1) {
		if (companion3D != null && companion3D.shouldDrawCursor()
				&& shouldDrawCursorAtEnd()) {
			drawTarget(renderer1);
		}
	}

	private void drawTarget(Renderer renderer1) {
		target.draw(renderer1, this);
	}

	/**
	 * @param point
	 *            moved point
	 * @return true if it has to draw 2D/1D arrows to move this point
	 */
	protected boolean drawCrossForPoint(GeoPoint3D point) {
		return true;
	}

	protected void drawPointAlready(GeoPoint3D point) {
		getCompanion().drawPointAlready(point);
	}

	/**
	 * Draw point cursor.
	 *
	 * @param mode
	 *            GeoPointND.MOVE_MODE_*
	 */
	public void drawPointAlready(int mode) {
		int pointMoveMode = mode;
		if (pointMoveMode == GeoPointND.MOVE_MODE_TOOL_DEFAULT) {
			pointMoveMode = ((EuclidianController3D) euclidianController)
					.getPointMoveMode();
		}

		switch (pointMoveMode) {
		case GeoPointND.MOVE_MODE_XY:
			renderer.drawCursor(PlotterCursor.Type.ALREADY_XY);
			break;
		case GeoPointND.MOVE_MODE_Z:
			renderer.drawCursor(PlotterCursor.Type.ALREADY_Z);
			break;
		case GeoPointND.MOVE_MODE_XYZ:
			renderer.drawCursor(PlotterCursor.Type.ALREADY_XYZ);
			break;
		default:
			// draw nothing
			break;
		}
	}

	/**
	 * set cursor to move
	 */
	public void setMoveCursor() {
		// 3D cursor
		cursor = EuclidianCursor.MOVE;
	}

	/**
	 * @return cursor type
	 */
	public EuclidianCursor getCursor() {
		return cursor;
	}

	final protected boolean cursorIsTranslateViewCursor() {
		return cursor == EuclidianCursor.MOVE
				|| cursor == EuclidianCursor.RESIZE_X
				|| cursor == EuclidianCursor.RESIZE_Y
				|| cursor == EuclidianCursor.RESIZE_Z;
	}

	/**
	 * @return null if cursor is view translate; proper cursor otherwise.
	 */
	public EuclidianCursor updateCursorIfNotTranslateViewCursor() {
		if (!cursorIsTranslateViewCursor()) {
			EuclidianCursor ret = cursor;
			Hits hits1 = getHits();
			if (hits1 != null && hits1.size() >= 1) {
				setCursorForTranslateView(hits1);
			} else {
				cursor = EuclidianCursor.MOVE;
			}
			return ret;
		}
		return null;
	}

	/**
	 * @param hits
	 *            hits
	 */
	public void setCursorForTranslateView(Hits hits) {
		EuclidianCursor old = cursor;
		if (hits.hasXAxis()) {
			cursor = EuclidianCursor.RESIZE_X;
		} else if (hits.hasYAxis()) {
			cursor = EuclidianCursor.RESIZE_Y;
		} else if (hits.hasZAxis()) {
			cursor = EuclidianCursor.RESIZE_Z;
		} else {
			cursor = EuclidianCursor.MOVE;
		}

		if (cursor != EuclidianCursor.MOVE && cursor != old) {
			// update may has failed since cursor was not correct type
			updateCursor3D();
		}
	}

	/**
	 * Set cursor for translating view.
	 */
	public void setCursorForTranslateViewNoHit() {
		cursor = EuclidianCursor.MOVE;
		setCursor(EuclidianCursor.DEFAULT);
	}

	@Override
	public void setCursor(EuclidianCursor cursor1) {
		switch (cursor1) {
		case HIT:
			setHitCursor();
			return;
		case DRAG:
			setDragCursor();
			return;
		case MOVE:
			setMoveCursor();
			return;
		case DEFAULT:
			setDefaultCursor();
			return;
		case RESIZE_X:
			cursor = EuclidianCursor.RESIZE_X;
			return;
		case RESIZE_Y:
			cursor = EuclidianCursor.RESIZE_Y;
			return;
		case RESIZE_Z:
			cursor = EuclidianCursor.RESIZE_Z;
			return;
		case RESIZE_NESW:
			cursor = EuclidianCursor.RESIZE_NESW;
			return;
		case RESIZE_NWSE:
			cursor = EuclidianCursor.RESIZE_NWSE;
			return;
		case RESIZE_EW:
			cursor = EuclidianCursor.RESIZE_EW;
			return;
		case RESIZE_NS:
			cursor = EuclidianCursor.RESIZE_NS;
			return;
		case TRANSPARENT:
			setTransparentCursor();
			return;
		default:
			setDefaultCursor();
			break;
		}
	}

	protected abstract void setTransparentCursor();

	/**
	 * next call to setDefaultCursor() will call setHitCursor() instead
	 */
	public void setDefaultCursorWillBeHitCursor() {
		defaultCursorWillBeHitCursor = true;
	}

	/**
	 * Set cursor to drag.
	 */
	public void setDragCursor() {
		// 2D cursor is invisible
		// setCursor(app.getTransparentCursor());
		// 3D cursor
		cursor = EuclidianCursor.DRAG;
	}

	/**
	 * @return true if shift key is down
	 */
	abstract protected boolean getShiftDown();

	/**
	 * Set cursor to default.
	 */
	public void setDefaultCursor() {
		// App.printStacktrace("setDefaultCursor:"+defaultCursorWillBeHitCursor);

		if (getShiftDown()) {
			return;
		}

		if (defaultCursorWillBeHitCursor) {
			defaultCursorWillBeHitCursor = false;
			setHitCursor();
			return;
		}

		// 2D cursor
		if (getProjection() == PROJECTION_GLASSES) {
			setCursor(EuclidianCursor.TRANSPARENT); // use own 3D cursor
													// (for depth)
			// setDefault2DCursor();
		} else {
			setDefault2DCursor();
		}

		// 3D cursor
		cursor = EuclidianCursor.DEFAULT;
	}

	/**
	 * set 2D cursor to default
	 */
	abstract protected void setDefault2DCursor();

	/**
	 * Set cursor to hit.
	 */
	public void setHitCursor() {
		if (getShiftDown()) {
			return;
		}

		cursor = EuclidianCursor.HIT;
	}

	/**
	 * returns settings in XML format, read by xml handlers
	 *
	 * @see org.geogebra.common.io.MyXMLHandler
	 * @see org.geogebra.common.geogebra3D.io.MyXMLHandler3D
	 */
	@Override
	public void getXML(StringBuilder sb, boolean asPreference) {
		StringTemplate tpl = StringTemplate.xmlTemplate;
		sb.append("<euclidianView3D>\n");

		// coord system
		if (!isZoomable() && !asPreference) {
			sb.append("\t<coordSystem");
			sb.append(" xMin=\"");
			StringUtil.encodeXML(sb, ((GeoNumeric) xminObject).getLabel(tpl));
			sb.append("\"");
			sb.append(" xMax=\"");
			StringUtil.encodeXML(sb, ((GeoNumeric) xmaxObject).getLabel(tpl));
			sb.append("\"");
			sb.append(" yMin=\"");
			StringUtil.encodeXML(sb, ((GeoNumeric) yminObject).getLabel(tpl));
			sb.append("\"");
			sb.append(" yMax=\"");
			StringUtil.encodeXML(sb, ((GeoNumeric) ymaxObject).getLabel(tpl));
			sb.append("\"");
			sb.append(" zMin=\"");
			StringUtil.encodeXML(sb, ((GeoNumeric) zminObject).getLabel(tpl));
			sb.append("\"");
			sb.append(" zMax=\"");
			StringUtil.encodeXML(sb, ((GeoNumeric) zmaxObject).getLabel(tpl));
			sb.append("\"");
		} else {
			sb.append("\t<coordSystem");
			sb.append(" xZero=\"");
			sb.append(getXZero());
			sb.append("\" yZero=\"");
			sb.append(getYZero());
			sb.append("\" zZero=\"");
			sb.append(getZZero());
			sb.append("\"");

			sb.append(" scale=\"");
			sb.append(getXscale());
			sb.append("\"");

			if (!getSettings().hasSameScales()) {
				sb.append(" yscale=\"");
				sb.append(getYscale());
				sb.append("\"");

				sb.append(" zscale=\"");
				sb.append(getZscale());
				sb.append("\"");
			}
		}
		sb.append(" xAngle=\"");
		sb.append(b);
		sb.append("\" zAngle=\"");
		sb.append(a);
		sb.append("\"/>\n");

		// ev settings
		sb.append("\t<evSettings axes=\"");
		sb.append(getShowAxis(0) || getShowAxis(1) || getShowAxis(2));

		sb.append("\" grid=\"");
		sb.append(getShowGrid());
		sb.append("\" gridIsBold=\""); //
		sb.append(gridIsBold); // Michael Borcherds 2008-04-11
		sb.append("\" pointCapturing=\"");

		// make sure POINT_CAPTURING_STICKY_POINTS isn't written to XML
		sb.append(
				getPointCapturingMode() > EuclidianStyleConstants.POINT_CAPTURING_XML_MAX
						? EuclidianStyleConstants.POINT_CAPTURING_DEFAULT
						: getPointCapturingMode());

		sb.append("\" rightAngleStyle=\"");
		sb.append(getApplication().rightAngleStyle);

		sb.append("\" gridType=\"");
		sb.append(getGridType()); // cartesian/isometric/polar

		sb.append("\"/>\n");
		// end ev settings

		// axis settings
		for (int i = 0; i < 3; i++) {
			this.getSettings().addAxisXML(i, sb);
		}

		// grid distances
		if (!automaticGridDistance) {
			sb.append("\t<grid distX=\"");
			sb.append(gridDistances[0]);
			sb.append("\" distY=\"");
			sb.append(gridDistances[1]);
			sb.append("\"/>\n");
		}

		// xOy plane settings
		sb.append("\t<plate show=\"");
		sb.append(getxOyPlane().isPlateVisible());
		sb.append("\"/>\n");
		//
		// sb.append("\t<grid show=\"");
		// sb.append(getxOyPlane().isGridVisible());
		// sb.append("\"/>\n");

		// background color
		sb.append("\t<bgColor");
		XMLBuilder.appendRGB(sb, bgColor);
		sb.append("/>\n");

		// colored axes
		if (!getSettings().getHasColoredAxes()) {
			sb.append("\t<axesColored val=\"false\"/>\n");
		}

		// y axis is up
		if (getYAxisVertical()) {
			sb.append("\t<yAxisVertical val=\"true\"/>\n");
		}

		// use light
		if (!getUseLight()) {
			sb.append("\t<light val=\"false\"/>\n");
		}

		// clipping cube
		sb.append("\t<clipping use=\"");
		sb.append(useClippingCube());
		sb.append("\" show=\"");
		sb.append(showClippingCube());
		sb.append("\" size=\"");
		sb.append(getClippingReduction());
		sb.append("\"/>\n");

		// projection
		sb.append("\t<projection type=\"");

		sb.append(getSettings().getProjection());

		getXMLForStereo(sb);
		if (!DoubleUtil.isEqual(projectionObliqueAngle,
				EuclidianSettings3D.PROJECTION_OBLIQUE_ANGLE_DEFAULT)) {
			sb.append("\" obliqueAngle=\"");
			sb.append(projectionObliqueAngle);
		}
		if (!DoubleUtil.isEqual(projectionObliqueFactor,
				EuclidianSettings3D.PROJECTION_OBLIQUE_FACTOR_DEFAULT)) {
			sb.append("\" obliqueFactor=\"");
			sb.append(projectionObliqueFactor);
		}

		sb.append("\"/>\n");

		// axes label style
		int style = getSettings().getAxisFontStyle();
		if (style == GFont.BOLD || style == GFont.ITALIC
				|| style == GFont.BOLD + GFont.ITALIC) {
			sb.append("\t<labelStyle axes=\"");
			sb.append(style);
			sb.append("\"/>\n");
		}

		// end
		sb.append("</euclidianView3D>\n");
	}

	final protected void getXMLForStereo(StringBuilder sb) {
		int eyeDistance = (int) projectionPerspectiveEyeDistance[0];
		int sep = (int) getEyeSep();
		getCompanion().getXMLForStereo(sb, eyeDistance, sep);
	}

	/**
	 * toggle the visibility of axes
	 */
	public void toggleAxis() {
		getSettings().setShowAxes(!axesAreAllVisible());
	}

	/**
	 * says if all axes are visible
	 *
	 * @return true if all axes are visible
	 */
	public boolean axesAreAllVisible() {
		boolean flag = true;

		for (int i = 0; i < 3; i++) {
			flag = (flag && axis[i].isEuclidianVisible());
		}

		return flag;
	}

	// ////////////////////////////////////////////////////
	// AXES
	// ////////////////////////////////////////////////////

	/**
	 * @return true if show xOy plane
	 */
	public boolean getShowPlane() {
		return xOyPlane.isPlateVisible();
	}

	@Override
	public void setShowPlane(boolean flag) {
		getxOyPlane().setEuclidianVisible(flag);
	}

	/**
	 * toggle the visibility of xOy grid
	 */
	public void toggleGrid() {
		getSettings().showGrid(!getShowGrid());
	}

	/**
	 * @return plane z=0
	 */
	public GeoPlane3DConstant getxOyPlane() {
		return xOyPlane;
	}

	/**
	 * says if this geo is owned by the view (xOy plane, ...)
	 *
	 * @param geo
	 *            construction element
	 * @return if this geo is owned by the view (xOy plane, ...)
	 */
	public boolean owns(GeoElement geo) {
		boolean ret = (geo == xOyPlane);

		for (int i = 0; (!ret) && (i < 3); i++) {
			ret = (geo == axis[i]);
		}

		return ret;
	}

	/**
	 * draw transparent parts of view's drawables (xOy plane)
	 *
	 * @param renderer1
	 *            renderer
	 */
	public void drawTransp(Renderer renderer1) {
		if (xOyPlane.isPlateVisible()) {
			xOyPlaneDrawable.drawTransp(renderer1);
		}
	}

	/**
	 * draw hiding parts of view's drawables (xOy plane)
	 *
	 * @param renderer1
	 *            renderer
	 */
	public void drawHiding(Renderer renderer1) {
		xOyPlaneDrawable.drawHiding(renderer1);
	}

	/**
	 * draw not hidden parts of view's drawables (axis)
	 *
	 * @param renderer1
	 *            renderer
	 */
	public void draw(Renderer renderer1) {
		for (int i = 0; i < 3; i++) {
			axisDrawable[i].drawOutline(renderer1);
		}

		if (showClippingCube()) {
			clippingCubeDrawable.drawOutline(renderer1);
		}
	}

	// ///////////////////////////
	// OPTIONS
	// //////////////////////////

	/**
	 * draw hidden parts of view's drawables (axis)
	 *
	 * @param renderer1
	 *            renderer
	 */
	public void drawHidden(Renderer renderer1) {
		for (int i = 0; i < 3; i++) {
			axisDrawable[i].drawHidden(renderer1);
		}

		xOyPlaneDrawable.drawHidden(renderer1);

		if (showClippingCube()) {
			clippingCubeDrawable.drawHidden(renderer1);
		}

		if (getCompanion().decorationVisible()) {
			pointDecorations.drawHidden(renderer1);
		}
	}

	public DrawPointDecorations getPointDecorations() {
		return pointDecorations;
	}

	/**
	 * draw ticks on axis
	 *
	 * @param renderer1
	 *            renderer
	 */
	public void drawLabel(Renderer renderer1) {
		for (int i = 0; i < 3; i++) {
			axisDrawable[i].drawLabel(renderer1);
		}
	}

	/**
	 * tell all drawables owned by the view to be updated
	 */
	private void setWaitForUpdateOwnDrawables() {
		xOyPlaneDrawable.setWaitForUpdate();

		for (int i = 0; i < 3; i++) {
			axisDrawable[i].setWaitForUpdate();
		}
		clippingCubeDrawable.setWaitForUpdate();
	}

	/**
	 * says all labels owned by the view that the view has changed
	 */
	public void resetOwnDrawables() {
        resetOwnDrawables(true);
    }

    /**
     * says all labels owned by the view that the view has changed
     * @param clearClippingEnlargement if we want to clear clipping cube enlargement
     */
    public void resetOwnDrawables(boolean clearClippingEnlargement) {
		xOyPlaneDrawable.setWaitForReset();

		for (int i = 0; i < 3; i++) {
			axisDrawable[i].setWaitForReset();
		}

		pointDecorations.setWaitForReset();

		if (clearClippingEnlargement) {
            clippingCubeDrawable.clearEnlarge();
        }
		clippingCubeDrawable.setWaitForReset();

		getCompanion().resetOwnDrawables();
	}

    /**
     * says all labels to be recomputed
     */
    public void resetAllDrawables() {
        resetAllDrawables(true);
    }

	/**
	 * says all labels to be recomputed
     * @param clearClippingEnlargement if we want to clear clipping cube enlargement
	 */
	public void resetAllDrawables(boolean clearClippingEnlargement) {
		drawable3DLists.setWaitForResetManagerBuffers();
		resetOwnDrawables(clearClippingEnlargement);
		drawable3DLists.resetAllDrawables();

	}

	/**
	 * reset all drawables visual styles
	 */
	public void resetAllVisualStyles() {
		// own drawables
		xOyPlaneDrawable.setWaitForUpdateVisualStyle(null);

		for (int i = 0; i < 3; i++) {
			axisDrawable[i].setWaitForUpdateVisualStyle(null);
		}

		pointDecorations.setWaitForUpdateVisualStyle(null);

		// other drawables
		drawable3DLists.resetAllVisualStyles();

		getCompanion().resetAllVisualStyles();
	}

	/**
	 * @param i
	 *            index
	 * @return i-th vertex of the clipping cube
	 */
	public Coords getClippingVertex(int i) {
		return clippingCubeDrawable.getVertex(i);
	}

	/**
	 * update minmax to fit minimum interval that is outside clipping, i.e. the
	 * clipping box is between the two orthogonal planes to the (o,v) line,
	 * through min and max parameters
	 *
	 * @param minmax
	 *            initial and returned min/max values
	 * @param o
	 *            line origin
	 * @param v
	 *            line direction
	 */
	public void getMinIntervalOutsideClipping(double[] minmax, Coords o,
			Coords v) {

		Coords p1, p2;

		// check 4 x opposite corners

		p1 = clippingCubeDrawable.getVertex(0);
		p2 = clippingCubeDrawable.getVertex(7);

		intervalUnionOutside(minmax, o, v, p1, p2);

		p1 = clippingCubeDrawable.getVertex(1);
		p2 = clippingCubeDrawable.getVertex(6);

		intervalUnionOutside(minmax, o, v, p1, p2);

		p1 = clippingCubeDrawable.getVertex(3);
		p2 = clippingCubeDrawable.getVertex(4);

		intervalUnionOutside(minmax, o, v, p1, p2);

		p1 = clippingCubeDrawable.getVertex(2);
		p2 = clippingCubeDrawable.getVertex(5);

		intervalUnionOutside(minmax, o, v, p1, p2);

	}

	// //////////////////////////////////////
	// ALGEBRA VIEW
	// //////////////////////////////////////

	private void intervalUnionOutside(double[] minmax, Coords o, Coords v,
			Coords p1, Coords p2) {
		p1.projectLine(o, v, tmpCoords1, parameters);
		double t1 = parameters[0];
		p2.projectLine(o, v, tmpCoords1, parameters);
		double t2 = parameters[0];
		intervalUnion(minmax, t1, t2);
	}

	/**
	 * Update bounds in kernel and recompute printing scale.
	 */
	public void updateBounds() {
		((Kernel3D) kernel).setEuclidianView3DBounds(evNo, getXmin(), getXmax(),
				getYmin(), getYmax(), getZmin(), getZmax(), getXscale(),
				getYscale(), getZscale());
		calcPrintingScale();
	}

	@Override
	public void updateBounds(boolean updateDrawables, boolean updateSettings) {
		for (int i = 0; i < axesDistanceObjects.length; i++) {
			if (axesDistanceObjects[i] != null
					&& axesDistanceObjects[i].getDouble() > 0) {
				axesNumberingDistances[i] = axesDistanceObjects[i].getDouble();
			}
		}

		if (getSettings().getXminObject() != null
				&& getSettings().getZminObject() != null
				&& getSettings().isUpdateScaleOrigin()) {
			double[][] minmax2 = new double[3][2];
			double xmin2 = getSettings().getXminObject().getDouble();
			double xmax2 = getSettings().getXmaxObject().getDouble();
			double ymin2 = getSettings().getYminObject().getDouble();
			double ymax2 = getSettings().getYmaxObject().getDouble();
			double zmin2 = getSettings().getZminObject().getDouble();
			double zmax2 = getSettings().getZmaxObject().getDouble();

			if (((xmax2 - xmin2) > Kernel.MAX_PRECISION) && ((ymax2 - ymin2) > Kernel.MAX_PRECISION)
					&& ((zmax2 - zmin2 > Kernel.MAX_PRECISION))) {
				minmax2[0][0] = xmin2;
				minmax2[0][1] = xmax2;
				minmax2[1][0] = ymin2;
				minmax2[1][1] = ymax2;
				minmax2[2][0] = zmin2;
				minmax2[2][1] = zmax2;

				double width = renderer.getWidth();
				double top = renderer.getTop();
				double bottom = renderer.getBottom();
				double rv = clippingCubeDrawable.getRV(clippingCube.getReduction());
				xZero = (xmin2 * (width / 2.0 - rv * width) - xmax2 * (-width / 2.0 + rv * width))
						/ (2 * rv * width - width);
				double xscale = (-width / 2.0 + rv * width) / (xmin2 + xZero);
				double yscale;
				double zscale;
				if (getYAxisVertical()) {
					yZero = (ymin2 * (top - rv * (top - bottom)) - ymax2 * (bottom + rv * (top
							- bottom))) / (bottom - top + 2 * rv * (top - bottom));
					zZero = (zmin2 * (width / 2.0 - rv * width) - zmax2 * (-width / 2.0
							+ rv * width)) / (
							2 * rv * width - width);
					yscale = (bottom) / (ymin2 + yZero) + rv * (top - bottom) / (ymin2 + yZero);
					zscale = (-width / 2.0 + rv * width) / (zmin2 + zZero);
				} else {
					yZero = (ymin2 * (width / 2.0 - rv * width) - ymax2 * (-width / 2.0
							+ rv * width)) / (
							2 * rv * renderer.getWidth() - width);
					zZero = (zmin2 * (top - rv * (top - bottom)) - zmax2 * (bottom + rv * (top
							- bottom))) / (bottom - top + 2 * rv * (top - bottom));
					yscale = (-width / 2.0 + rv * width) / (ymin2 + yZero);
					zscale = (bottom) / (zmin2 + zZero) + rv * (top - bottom) / (zmin2 + zZero);
				}

				if (updateSettings && getSettings() != null) {
					getSettings()
							.setCoordSystem(xZero, yZero, zZero, xscale, yscale, zscale, false);
				}
				clippingCubeDrawable.doUpdateMinMax();

				if (!isZoomable()) {
					if (!getSettings().isSetStandardCoordSystem()) {
						getSettings().setSetStandardCoordSystem(true);
					} else {
						updateMatrix();
					}
					updateAllDrawables();
				}

				updateDecorations(minmax2);
			}
		}
		updateBounds();
	}

	private void viewChangedOwnDrawables() {

		// update, but not in case where view changed by rotation
		if (viewChangedByTranslate() || viewChangedByZoom()) {
			// update clipping cube
			double[][] minMax = isXREnabled()
					? clippingCubeDrawable.updateMinMaxLarge()
					: updateClippingCubeMinMax();
			// e.g. Corner[] algos are updated by clippingCubeDrawable
			clippingCubeDrawable.setWaitForUpdate();

			// xOy plane wait for update
			xOyPlaneDrawable.setWaitForUpdate();

			// update decorations and wait for update
			updateDecorations(minMax);

			if (getOptionPanel() != null) {
				getOptionPanel().updateBounds();
			}
			updateBoundObjects();
		}

		// we need to update renderer clip planes even for rotation, since they
		// are in screen coordinates
		clippingCubeDrawable.updateRendererClipPlanes();

		if (viewChangedByRotate()) {

			updateAxesDecoration();

			// update e.g. Corner[]
			kernel.notifyEuclidianViewCE(EVProperty.ROTATION);
		}
	}

	private void updateDecorations(double[][] minMax) {
		for (int i = 0; i < 3; i++) {
			axisDrawable[i].setDrawMinMaxImmediatly(minMax);
			axisDrawable[i].updateDecorations();
			setAxesIntervals(getScale(i), i);

			axisDrawable[i].setWaitForUpdate();
		}
	}

	@Override
	public void updateBoundObjects() {
		if (isZoomable() && xminObject != null) {
			((GeoNumeric) xminObject).setValue(getXmin());
			((GeoNumeric) xmaxObject).setValue(getXmax());
			((GeoNumeric) yminObject).setValue(getYmin());
			((GeoNumeric) ymaxObject).setValue(getYmax());
			((GeoNumeric) zminObject).setValue(getZmin());
			((GeoNumeric) zmaxObject).setValue(getZmax());
		}
	}

	/**
	 * update axes values for ticks and labels
	 */
	public void updateAxesDecoration() {
		// we need to update axis numbers locations
		for (int i = 0; i < 3; i++) {
			axisDrawable[i].updateDecorations();
			axisDrawable[i].setLabelWaitForUpdate();
		}
	}

	/**
	 * update axes position for ticks and labels
	 */
	public void updateAxesDecorationPosition() {
		for (int i = 0; i < 3; i++) {
			axisDrawable[i].updateDrawPositionAxes();
		}
	}

	protected double[][] updateClippingCubeMinMax() {
		return clippingCubeDrawable.updateMinMax();
	}

	// ///////////////////////////////////////////////
	// UPDATE VIEW : ZOOM, TRANSLATE, ROTATE
	// ///////////////////////////////////////////////

	/**
	 * update all drawables now
	 */
	public void updateOwnDrawablesNow() {
		for (int i = 0; i < 3; i++) {
			axisDrawable[i].update();
		}

		// update xOyPlane
		xOyPlaneDrawable.update();

		clippingCubeDrawable.update();

		// update intersection curves in controller
		((EuclidianController3D) getEuclidianController())
				.updateOwnDrawablesNow();
	}

	/**
	 * update all drawables
	 */
	public void updateDrawables() {
		drawable3DLists.updateAll(renderer);
	}

	/**
	 * @param i
	 *            index
	 * @return i-th label
	 */
	public String getAxisLabel(int i) {
		return axesLabels[i];
	}

	public GFont getAxisLabelFont(int i) {
		return getFontLine().deriveFont(axesLabelsStyle[i]);
	}

	public String getAxisUnitLabel(int i) {
		return axesUnitLabels[i];
	}

	public boolean getPiAxisUnit(int i) {
		return piAxisUnit[i];
	}

	@Override
	public void setAxesLabels(String[] axesLabels) {
		this.axesLabels = axesLabels;
		for (int i = 0; i < 3; i++) {
			if (axesLabels[i] != null && axesLabels[i].length() == 0) {
				axesLabels[i] = null;
			}
		}
	}

	@Override
	public void setAxisLabel(int axis, String axisLabel) {
		super.setAxisLabel(axis, axisLabel);
		axisDrawable[axis].setLabelWaitForUpdate();
		setWaitForUpdate();
	}

	@Override
	public void setAxesUnitLabels(String[] axesUnitLabels) {
		super.setAxesUnitLabels(axesUnitLabels);
		setAxesIntervals(getZscale(), 2);
		for (int i = 0; i < 3; i++) {
			axisDrawable[i].setLabelWaitForUpdate();
		}
		setWaitForUpdate();
	}

	/**
	 * @param i
	 *            index
	 * @return if i-th axis shows numbers
	 */
	public boolean getShowAxisNumbers(int i) {
		return showAxesNumbers[i];
	}

	@Override
	public Previewable createPreviewParallelLine(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines,
			ArrayList<GeoFunction> selectedFunctions) {
		// not implemented in 3D
		return null;
	}

	@Override
	public Previewable createPreviewPerpendicularLine(
			ArrayList<GeoPointND> selectedPoints, 
			ArrayList<GeoLineND> selectedLines,
			ArrayList<GeoFunction> selectedFunctions) {
		// not implemented in 3D
		return null;
	}

	@Override
	public Previewable createPreviewPerpendicularBisector(
			ArrayList<GeoPointND> selectedPoints) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Previewable createPreviewAngleBisector(
			ArrayList<GeoPointND> selectedPoints) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Previewable createPreviewPolyLine(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawPolyLine3D(this, selectedPoints);
	}

	@Override
	public Previewable createPreviewParabola(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines) {
		return null;
	}

	public boolean getPositiveAxis(int i) {
		return positiveAxes[i];
	}

	@Override
	public void setPositiveAxis(int axis, boolean isPositiveAxis) {
		super.setPositiveAxis(axis, isPositiveAxis);
		axisDrawable[axis].setLabelWaitForUpdate();
		setWaitForUpdate();
	}

	@Override
	public boolean getShowGrid() {
		return xOyPlane.isGridVisible();
	}

	@Override
	public boolean showGrid(boolean selected) {
		return setShowGrid(selected);
	}

	@Override
	public void setAutomaticGridDistance(boolean flag) {
		super.setAutomaticGridDistance(flag);
		setAxesIntervals(getZscale(), 2);
	}

	@Override
	public int getMode() {
		return getEuclidianController().getMode();
	}

	// ///////////////////////////////////////////////
	// PROJECTION (ORTHO/PERSPECTIVE/...)
	// ///////////////////////////////////////////////

	public void setViewChangedByZoom() {
		viewChangedByZoom = true;
	}

	public void setViewChangedByTranslate() {
		viewChangedByTranslate = true;
	}

	public void setViewChangedByRotate() {
		viewChangedByRotate = true;
	}

	@Override
	public void setViewChanged() {
		setViewChangedByZoom();
		setViewChangedByTranslate();
		setViewChangedByRotate();
	}

	/**
	 * @return whether view was zoomed
	 */
	public boolean viewChangedByZoom() {
		return viewChangedByZoom;
	}

	/**
	 * @return whether view was panned
	 */
	public boolean viewChangedByTranslate() {
		return viewChangedByTranslate;
	}

	/**
	 * @return whether view was rotated
	 */
	public boolean viewChangedByRotate() {
		return viewChangedByRotate;
	}

	/**
	 * @return whether view was changed (zoomed, panned, rotated)
	 */
	public boolean viewChanged() {
		return viewChangedByZoom || viewChangedByTranslate
				|| viewChangedByRotate;
	}

	/**
	 * Reset all viewChanged* flags
	 */
	public void resetViewChanged() {
		if (viewChanged()) {
			dispatch3DViewChangeEvent();

			viewChangedByZoom = false;
			viewChangedByTranslate = false;
			viewChangedByRotate = false;
		}
	}

	final public int getPointStyle() {
		return pointStyle;
	}

	@Override
	public String getFromPlaneString() {
		return "space";
	}

	@Override
	public String getTranslatedFromPlaneString() {
		return app.getLocalization().getMenu("space");
	}

	@Override
	public Previewable createPreviewAngle(
			ArrayList<GeoPointND> selectedPoints) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDefault2D() {
		return false;
	}

	@Override
	public boolean isEuclidianView3D() {
		return true;
	}

	@Override
	public ArrayList<GeoPointND> getFreeInputPoints(AlgoElement algoParent) {
		return algoParent.getFreeInputPoints();
	}

	private void setProjectionValues(int projection) {
		if (this.projection != projection) {
			this.projection = projection;
			updateEye();
			setViewChanged();
			setWaitForUpdate();
			// resetAllDrawables();
			resetAllVisualStyles();
			renderer.setWaitForUpdateClearColor();
		}
	}

	public int getProjection() {
		return projection;
	}

	public boolean hasParallelProjection() {
		return getProjection() == PROJECTION_ORTHOGRAPHIC || getProjection() == PROJECTION_OBLIQUE;
	}

	@Override
	public void setProjection(int projection) {
		if (getCompanion().useOnlyProjectionGlasses()) {
			setProjectionGlasses();
			return;
		}

		switch (projection) {
		default:
		case PROJECTION_ORTHOGRAPHIC:
			setProjectionOrthographic();
			break;
		case PROJECTION_PERSPECTIVE:
			setProjectionPerspective();
			break;
		case PROJECTION_GLASSES:
			setProjectionGlasses();
			break;
		case PROJECTION_OBLIQUE:
			setProjectionOblique();
			break;
		}
	}

	/**
	 * Set projection to ortographic.
	 */
	public void setProjectionOrthographic() {
		renderer.updateOrthoValues();
		setProjectionValues(PROJECTION_ORTHOGRAPHIC);
		setDefault2DCursor();
	}

	/**
	 * Set projection to perspective.
	 */
	public void setProjectionPerspective() {
		updateProjectionPerspectiveEyeDistance();
		setProjectionValues(PROJECTION_PERSPECTIVE);
		setDefault2DCursor();
		// setTransparentCursor();
	}

	/**
	 * set the near distance regarding eye distance to the screen for
	 * perspective (in pixels). Left != right with headtracking.
	 *
	 * @param distanceLeft
	 *            left aye distance
	 * @param distanceRight
	 *            right eye distance
	 */
	public void setProjectionPerspectiveEyeDistance(double distanceLeft,
			double distanceRight) {
		projectionPerspectiveEyeDistance[0] = distanceLeft;
		projectionPerspectiveEyeDistance[1] = distanceRight;
		if (projection != PROJECTION_PERSPECTIVE
				&& projection != PROJECTION_GLASSES) {
			projection = PROJECTION_PERSPECTIVE;
		}
		updateProjectionPerspectiveEyeDistance();
		if (projection == PROJECTION_GLASSES) { // also update
			// eyes
			// separation
			renderer.updateGlassesValues();
		}
	}

	final private void updateProjectionPerspectiveEyeDistance() {

		renderer.setNear(projectionPerspectiveEyeDistance[0],
				projectionPerspectiveEyeDistance[1]);
	}

	/**
	 *
	 * @return eye distance to the screen for perspective
	 */
	public double getProjectionPerspectiveEyeDistance() {
		return projectionPerspectiveEyeDistance[0];
	}

	/**
	 * Set projection to glasses.
	 */
	public void setProjectionGlasses() {
		updateProjectionPerspectiveEyeDistance();
		renderer.updateGlassesValues();
		setProjectionValues(PROJECTION_GLASSES);
		setCursor(EuclidianCursor.TRANSPARENT);
	}

	public boolean isGlassesGrayScaled() {
		return isGlassesGrayScaled;
	}

	/**
	 * @param flag
	 *            whether to use grayscale for glasses
	 */
	public void setGlassesGrayScaled(boolean flag) {
		if (isGlassesGrayScaled == flag) {
			return;
		}

		isGlassesGrayScaled = flag;
		resetAllDrawables();
	}

	/**
	 * @return whether to render objects in grayscale
	 */
	public boolean isGrayScaled() {
		return projection == PROJECTION_GLASSES
                && !isXREnabled()
				&& !getCompanion().isStereoBuffered()
				&& isGlassesGrayScaled();
	}

	public boolean isGlassesShutDownGreen() {
		return isGlassesShutDownGreen;
	}

	/**
	 * @param flag
	 *            shutdown green flag
	 */
	public void setGlassesShutDownGreen(boolean flag) {
		if (isGlassesShutDownGreen == flag) {
			return;
		}

		isGlassesShutDownGreen = flag;
		renderer.setWaitForUpdateClearColor();
	}

	public boolean isShutDownGreen() {
		return projection == PROJECTION_GLASSES && isGlassesShutDownGreen();
	}

	/**
	 * @param leftX
	 *            left eye x
	 * @param leftY
	 *            left eye y
	 * @param rightX
	 *            right eye x
	 * @param rightY
	 *            right eye y
	 */
	public void setEyes(double leftX, double leftY, double rightX,
			double rightY) {
		eyeX[0] = leftX;
		eyeY[0] = leftY;
		eyeX[1] = rightX;
		eyeY[1] = rightY;
		renderer.updateGlassesValues();
	}

	public double getEyeSep() {
		return eyeX[1] - eyeX[0];
	}

	public double getEyeX(int i) {
		return eyeX[i];
	}

	public double getEyeY(int i) {
		return eyeY[i];
	}

	@Override
	public int getViewID() {
		return App.VIEW_EUCLIDIAN3D - evNo - 1;
	}

	/**
	 * Set projection to oblique.
	 */
	public void setProjectionOblique() {
		renderer.updateProjectionObliqueValues();
		setProjectionValues(PROJECTION_OBLIQUE);
		setDefault2DCursor();
	}

	/**
	 * @return oblique projection angle
	 */
	public double getProjectionObliqueAngle() {
		return projectionObliqueAngle;
	}

	/**
	 * @param angle
	 *            oblique projection angle
	 */
	public void setProjectionObliqueAngle(double angle) {
		projectionObliqueAngle = angle;
		renderer.updateProjectionObliqueValues();
	}

	public double getProjectionObliqueFactor() {
		return projectionObliqueFactor;
	}

	/**
	 * @param factor
	 *            oblique projection factor
	 */
	public void setProjectionObliqueFactor(double factor) {
		projectionObliqueFactor = factor;
		renderer.updateProjectionObliqueValues();
	}

	@Override
	public boolean getShowAxis(int axisNo) {
		return this.axis[axisNo].isEuclidianVisible();
	}

	@Override
	public boolean isAxesHidden() {
		for (int i = 0; i < axis.length; i++) {
			if (this.axis[i].isEuclidianVisible()) {
				return false;
			}
		}
		return true;
	}

	// ////////////////////////////////////
	// PICKING

	@Override
	public void replaceBoundObject(GeoNumeric num, GeoNumeric num2) {
		if (xmaxObject == num) {
			xmaxObject = num2;
		}
		if (xminObject == num) {
			xminObject = num2;
		}
		if (ymaxObject == num) {
			ymaxObject = num2;
		}
		if (yminObject == num) {
			yminObject = num2;
		}
		if (zmaxObject == num) {
			zmaxObject = num2;
		}
		if (zminObject == num) {
			zminObject = num2;
		}
		for (int i = 0; i < axesDistanceObjects.length; i++) {
			if (axesDistanceObjects[i] == num) {
				axesDistanceObjects[i] = num2;
			}
		}
		updateBounds(true, true);
	}

	public GColor getBackground() {
		return bgColor;
	}

	// ////////////////////////////////////
	// SOME LINKS WITH 2D VIEW

	@Override
	final public void setBackground(GColor color) {
		getCompanion().setBackground(color);
	}

	public GColor getApplyedBackground() {
		return bgApplyedColor;
	}

	// ////////////////////////////////////////
	// ABSTRACTEUCLIDIANVIEW
	// ////////////////////////////////////////

	// ////////////////////////////////////////
	// EUCLIDIANVIEWND
	// ////////////////////////////////////////

	@Override
	final public GColor getBackgroundCommon() {
		return getBackground();
	}

	@Override
	public int getFontSize() {
		return app.getFontSize();
	}

	@Override
	public int getEuclidianViewNo() {
		return getViewID();
	}

	@Override
	public void setEuclidianViewNo(int evNo) {
		this.evNo = evNo;
	}

	@Override
	protected void initCursor() {
		// no normal cursor in 3D
	}

	@Override
	public void setShowAxis(boolean show) {
		setShowAxis(0, show, false);
		setShowAxis(1, show, false);
		setShowAxis(2, show, true);
	}

	@Override
	public GGraphics2D getGraphicsForPen() {
		return null;
	}

	@Override
	protected void drawResetIcon(GGraphics2D g) {
		Log.debug("unimplemented");
	}

	public double[] getIntervalClippedLarge(double[] minmax, Coords o,
			Coords v) {
		return clippingCubeDrawable.getIntervalClippedLarge(minmax, o, v);
	}

	public double[] getIntervalClipped(double[] minmax, Coords o, Coords v) {
		return clippingCubeDrawable.getIntervalClipped(minmax, o, v);
	}

	@Override
	public boolean isOnView(double[] coords) {
		// check first x, y
		if (!super.isOnView(coords)) {
			return false;
		}

		// check z
		if (coords.length < 3) { // 2D points : z = 0
			return (0 >= getZmin()) && (0 <= getZmax());
		}

		// 3D point
		return (coords[2] >= getZmin()) && (coords[2] <= getZmax());
	}

	@Override
	public double[] getOnScreenDiff(double[] p1, double[] p2) {
		double[] ret = new double[p1.length];
		ret[0] = (p2[0] - p1[0]) * getXscale();
		ret[1] = (p2[1] - p1[1]) * getYscale();
		if (ret.length > 2) {
			ret[2] = (p2[2] - p1[2]) * getZscale();
		}
		return ret;
	}

	@Override
	public boolean isSegmentOffView(double[] p1, double[] p2) {
		if (super.isSegmentOffView(p1, p2)) {
			return true;
		}

		double tolerance = EuclidianStatic.CLIP_DISTANCE / getZscale();

		// check z
		double z1, z2;
		if (p1.length < 3) { // 2D points : z = 0
			z1 = 0;
			z2 = 0;
		} else {
			z1 = p1[2];
			z2 = p2[2];
		}

		if (DoubleUtil.isGreater(getZmin(), z1, tolerance)
				&& DoubleUtil.isGreater(getZmin(), z2, tolerance)) {
			return true;
		}

		if (DoubleUtil.isGreater(z1, getZmax(), tolerance)
				&& DoubleUtil.isGreater(z2, getZmax(), tolerance)) {
			return true;
		}

		// close to screen
		return false;
	}

	@Override
	public boolean drawPlayButtonInThisView() {
		return false;
	}

	/**
	 *
	 * @return distance between two number ticks on the x axis
	 */
	public double getNumbersDistance() {
		return getAxisNumberingDistance(AXIS_X);
	}

	/**
	 *
	 * @param idx
	 *            axis index
	 * @return distance between two number ticks on the axis
	 */
	public double getAxisNumberingDistance(int idx) {
		return axesNumberingDistances[idx];
	}

	@Override
	public double getGridDistances(int i) {
		if (i == AXIS_Z) { // no grid along z axis
			return getAxisNumberingDistance(AXIS_Z);
		}

		return super.getGridDistances(i);
	}

	@Override
	public EuclidianController getEuclidianController() {
		return euclidianController;
	}

	@Override
	public GeoDirectionND getDirection() {
		return kernel.getSpace();
	}

	@Override
	public int getDimension() {
		return 3;
	}

	@Override
	public final boolean isGridOrAxesShown() {
		for (int i = 0; i < 3; i++) {
			if (getShowAxis(i)) {
				return true;
			}
		}

		return getShowGrid();
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		companion.settingsChanged(settings);

		EuclidianSettings3D evs = (EuclidianSettings3D) settings;

		evs.updateOrigin(this);
		evs.updateRotXY(this);

		updateUseClippingCube();
		setClippingReduction(evs.getClippingReduction());

		setShowPlate(evs.getShowPlate());

		setProjectionPerspectiveEyeDistance(
				evs.getProjectionPerspectiveEyeDistance(),
				evs.getProjectionPerspectiveEyeDistance());
		eyeX[0] = -evs.getEyeSep() / 2.0;
		eyeX[1] = -eyeX[0];
		eyeY[0] = 0;
		eyeY[1] = 0;
		projectionObliqueAngle = evs.getProjectionObliqueAngle();
		projectionObliqueFactor = evs.getProjectionObliqueFactor();

		setProjection(evs.getProjection());

		boolean colored = evs.getHasColoredAxes();
		setColoredAxes(colored);

		updateMatrix();
		getEuclidianController().onCoordSystemChanged();
		setViewChanged();
		setWaitForUpdate();
		if (evs.getRotSpeed() > 0) {
			this.setRotContinueAnimation(0, evs.getRotSpeed());
		}

		if (styleBar != null) {
			styleBar.updateGUI();
		}
	}

	protected void setColoredAxes(boolean colored) {
		for (GeoAxisND ax : axis) {
			ax.setColoredFor3D(colored);
		}
	}

    /**
     * Updates the objects before showing all objects
     */
    public void showAllObjectsKeepRatioUpdate() {
        // add drawables to lists
        update();
        // update own drawables to get correct clipping
        updateOwnDrawablesNow();
        // update drawables to compute bounds
        updateDrawables();
        // show all objects, no step
        setViewShowAllObjects(false, true, 0);
    }

	@Override
	public final void setViewShowAllObjects(boolean storeUndo,
			boolean keepRatio) {
    	if (isZoomable()) {
			setViewShowAllObjects(storeUndo, keepRatio, 15);
		}
	}

	@Override
	public final void setViewShowAllObjects(boolean storeUndo,
			boolean keepRatio, int steps) {

		if (updateObjectsBounds()) {
			zoomRW(boundsMin, boundsMax, steps);
		}
	}

	/**
	 * update bounds that enclose all objects (except axes)
	 *
	 */
	private boolean updateObjectsBounds() {
		return updateObjectsBounds(false, false, false);
	}

	/**
	 * update bounds that enclose all objects
	 *
	 * @param includeXYAxesIfVisible
	 *            if x & y axes should enlarge bounds
     * @param includeZAxisIfVisible
     *            if z axis should enlarge bounds
	 * @param dontExtend
	 *            set to true if clipped curves/surfaces should not be larger
	 *            than the view itself; and when point radius should extend
	 *
	 * @return true if bounds were computed
	 */
	protected boolean updateObjectsBounds(boolean includeXYAxesIfVisible,
                                          boolean includeZAxisIfVisible,
                                          boolean dontExtend) {
		if (boundsMin == null) {
			boundsMin = new Coords(3);
			boundsMax = new Coords(3);
		}

		boundsMin.setPositiveInfinity();
		boundsMax.setNegativeInfinity();

		drawable3DLists.enlargeBounds(boundsMin, boundsMax, dontExtend);
		if (includeXYAxesIfVisible) {
            enlargeBounds(axisDrawable[AXIS_X], dontExtend);
            enlargeBounds(axisDrawable[AXIS_Y], dontExtend);
		}
		if (includeZAxisIfVisible) {
            enlargeBounds(axisDrawable[AXIS_Z], dontExtend);
        }

		return !Double.isInfinite(boundsMin.getX());
	}

	private void enlargeBounds(DrawAxis3D d, boolean dontExtend) {
        if (d.isVisible()) {
            d.enlargeBounds(boundsMin, boundsMax, dontExtend);
        }
    }

	@Override
	public void zoomRW(Coords boundsMin2, Coords boundsMax2) {
		zoomRW(boundsMin2, boundsMax2, 15);
	}

	/**
	 * @param boundsMin2
	 *            min bounds after zoom
	 * @param boundsMax2
	 *            max bounds after zoom
	 * @param steps
	 *            number of steps
	 */
	public void zoomRW(Coords boundsMin2, Coords boundsMax2, int steps) {
		double dx0 = getXmax() - getXmin();
		double dy0 = getYmax() - getYmin();
		double dz0 = getZmax() - getZmin();

		double dx = boundsMax2.getX() - boundsMin2.getX();
		double dy = boundsMax2.getY() - boundsMin2.getY();
		double dz = boundsMax2.getZ() - boundsMin2.getZ();

		double scale = Double.POSITIVE_INFINITY;
		if (!DoubleUtil.isZero(dx)) {
			scale = dx0 / dx;
		}
		if (!DoubleUtil.isZero(dy)) {
			double v = dy0 / dy;
			if (scale > v) {
				scale = v;
			}
		}
		if (!DoubleUtil.isZero(dz)) {
			double v = dz0 / dz;
			if (scale > v) {
				scale = v;
			}
		}

		if (Double.isInfinite(scale)) {
			return;
		}

		if (Double.isNaN(scale)) {
			return;
		}

		if (DoubleUtil.isZero(scale)) {
			return;
		}

		scale *= getScale();

		// let the view a bit greater than the scene
		scale *= 0.94;

		double x = -(boundsMin2.getX() + boundsMax2.getX()) / 2;
		double y = -(boundsMin2.getY() + boundsMax2.getY()) / 2;
		double z = -(boundsMin2.getZ() + boundsMax2.getZ()) / 2;

		animator.setAnimatedCoordSystem(x, y, z, scale, steps);

	}

	/**
	 * dispose current preview
	 */
	public void disposePreview() {
		if (this.previewDrawable != null) {
			this.previewDrawable.disposePreview();
		}
	}

	@Override
	protected void updateDrawableFontSize() {
		drawable3DLists.resetAllLabels();
		for (int i = 0; i < 3; i++) {
			axisDrawable[i].setWaitForUpdate();
			axisDrawable[i].setLabelWaitForReset();
		}
		repaintView();
	}

	@Override
	public void centerView(GeoPointND point) {
		animator.centerView(point);
	}

	public double getMaxBendSpeedSurface() {
		return MAX_BEND_SPEED_SURFACE;
	}

	@Override
	public int getExportWidth() {
		return getWidth();
	}

	@Override
	public int getExportHeight() {
		return getHeight();
	}

	public double getFontScale() {
		return fontScale;
	}

	/**
	 * set font scale
	 *
	 * @param scale
	 *            scale
	 */
	public void setFontScale(double scale) {
		if (!DoubleUtil.isEqual(scale, fontScale)) {
			fontScale = scale;
			updateDrawableFontSize();
		}
	}

	/**
	 * set zNear nearest value
	 *
	 * @param zNear
	 *            near z-coord
	 */
	final public void setZNearest(double zNear) {
		getCompanion().setZNearest(zNear);
	}

	/**
	 *
	 * @return true if consumes space key hitted
	 */
	public boolean handleSpaceKey() {
		return getCompanion().handleSpaceKey();
	}

	@Override
	public void closeDropdowns() {
		// no combo box in 3D for now
	}

	/**
	 * update background color and apply color to background
	 *
	 * @param updatedColor
	 *            color to update background
	 * @param applyedColor
	 *            color actually applyed
	 *
	 */
	public void setBackground(GColor updatedColor, GColor applyedColor) {
		this.bgColor = updatedColor;
		this.bgApplyedColor = applyedColor;
		if (renderer != null) {
			renderer.setWaitForUpdateClearColor();
		}
		if (isXREnabled()) {
			renderer.setBackgroundColor();
		}
	}

	@Override
	final public void paintBackground(GGraphics2D g2) {
		// not used in 3D
	}

	public void screenTranslateAndScale(double dx, double dy,
			double scaleFactor) {
		animator.screenTranslateAndScale(dx, dy, scaleFactor);
	}

	@Override
	public void endBatchUpdate() {
		this.batchUpdate = false;
	}

	@Override
	public final void drawActionObjects(GGraphics2D g) {
		// TODO Auto-generated method stub

	}

	public void stopScreenTranslateAndScale() {
		animator.stopScreenTranslateAndScale();
	}

	@Override
	public void scaleXYZ(Coords coords) {
		coords.mulInside(getXscale(), getYscale(), getZscale());
	}

	public void scaleXYZ(Coords3 coords) {
		coords.mulInside(getXscale(), getYscale(), getZscale());
	}

	/**
	 * scale coords as normal vector
	 *
	 * @param coords
	 *            normal vector
	 */
	public void scaleNormalXYZ(Coords coords) {
		EuclidianSettings3D settings = getSettings();
		if (settings.hasSameScales()) {
			return;
		}
		coords.mulInside(settings.getYZscale(), settings.getZXscale(),
				settings.getXYscale());
	}

	/**
	 * Sclae and normalize normal vector.
	 *
	 * @param coords
	 *            normal vector
	 */
	public void scaleAndNormalizeNormalXYZ(Coords3 coords) {
		EuclidianSettings3D settings = getSettings();
		if (settings.hasSameScales()) {
			return;
		}
		coords.mulInside(settings.getYZscale(), settings.getZXscale(),
				settings.getXYscale());
		coords.normalizeIfPossible();
	}

	@Override
	public boolean scaleAndNormalizeNormalXYZ(Coords coords, Coords ret) {
		EuclidianSettings3D settings = getSettings();
		if (settings.hasSameScales()) {
			return false;
		}
		ret.setMul(coords, settings.getYZscale(), settings.getZXscale(),
				settings.getXYscale());
		ret.normalize();
		return true;
	}

	@Override
	public GBufferedImage getExportImage(double scale, boolean transparency,
			ExportType exportType) {
		return getRenderer().getExportImage(scale);
	}

	@Override
	final public boolean getKeepCenter() {
		// no need in 3D
		return false;
	}

	@Override
	final public void setKeepCenter(boolean center) {
		// no need in 3D
	}

	@Override
	protected void setXYMinMaxForSetCoordSystem() {
		// no need in 3D
	}

	/**
	 * set the coord system regarding 3D mouse move
	 *
	 * @param translation
	 *            translation vector
	 */
	public void setCoordSystemFromMouse3DMove(Coords translation) {
		setXZero(xZeroOld + translation.getX());
		setYZero(yZeroOld + translation.getY());
		setZZero(zZeroOld + translation.getZ());

		// update the view
		updateTranslationMatrices();
		setGlobalMatrices();

		setViewChangedByTranslate();
		setWaitForUpdate();
	}

	/**
	 * set mouse start pos
	 *
	 * @param screenStartPos
	 *            mouse start pos (screen)
	 */
	public void setStartPos(Coords screenStartPos) {
		startPos.set(screenStartPos);
		toSceneCoords3D(startPos);
		startTranslation.setOrigin(screenStartPos.add(startPos));
	}

	/**
	 * set the coord system regarding 3D mouse move
	 *
	 * @param startPos1
	 *            start 3D position (screen)
	 * @param newPos
	 *            current 3D position (screen)
	 * @param rotX
	 *            relative mouse rotate around x (screen)
	 * @param rotZ
	 *            relative mouse rotate around z (view)
	 */
	public void setCoordSystemFromMouse3DMove(Coords startPos1, Coords newPos,
			double rotX, double rotZ) {

		// translation
		Coords v = new Coords(4);
		v.set(newPos.sub(startPos1));
		toSceneCoords3D(v);

		// rotation
		setRotXYinDegrees(aOld + rotX, bOld + rotZ);

		updateRotationAndScaleMatrices();

		// center rotation on pick point ( + v for translation)
		CoordMatrix m1 = rotationAndScaleMatrix.inverse().mul(startTranslation)
				.mul(rotationAndScaleMatrix);
		Coords t1 = m1.getOrigin();
		setXZero(t1.getX() - startPos.getX() + v.getX());
		setYZero(t1.getY() - startPos.getY() + v.getY());
		setZZero(t1.getZ() - startPos.getZ() + v.getZ());
		getSettings().updateOriginFromView(getXZero(), getYZero(), getZZero());
		// update the view
		updateTranslationMatrices();
		setGlobalMatrices();

		setViewChangedByTranslate();
		setViewChangedByRotate();
		setWaitForUpdate();

	}

	public Mouse3DEvent createMouse3DEvent(GPointWithZ mouse3DLoc) {
		return new Mouse3DEvent(mouse3DLoc);
	}

	@Override
	protected EuclidianViewCompanion newEuclidianViewCompanion() {
		companion3D = new EuclidianView3DCompanion(this);
		return companion3D;
	}

	@Override
	public EuclidianView3DCompanion getCompanion() {
		return companion3D;
	}

	public DrawClippingCube3D getClippingCubeDrawable() {
		return clippingCubeDrawable;
	}

	@Override
	public void setExport3D(final Format format) {
		renderer.setExport3D(new Runnable() {
			@Override
			public void run() {
				ExportToPrinter3D exportToPrinter = new ExportToPrinter3D(EuclidianView3D.this,
						renderer.getGeometryManager());
				StringBuilder export = exportToPrinter.export(format);
				getApplication().exportStringToFile(format.getExtension(),
						export.toString());
			}
		});
	}

	@Override
	protected boolean needsZoomerForStandardRatio() {
		return false; // not needed in 3D
	}

	@Override
	public void updateBackground() {
		// make sure axis number formats are up to date
		setAxesIntervals(getZscale(), 2);
		super.updateBackground();
		kernel.notifyRepaint();
	}

	@Override
	public double getAngleA() {
		return a;
	}

	@Override
	public double getAngleB() {
		return b;
	}

	@Override
	public void setStandardView(boolean storeUndo) {
		if (isZoomable()) {
			setAnimatedCoordSystem(STANDARD_VIEW_STEPS);
			animator.setRotAnimation(ANGLE_ROT_OZ, ANGLE_ROT_XOY, false, true, storeUndo);
		}
	}

	@Override
	public boolean isStandardView() {
		return isZeroStandard()
				&& DoubleUtil.isEqual(getXscale(), SCALE_STANDARD)
				&& DoubleUtil.isEqual(getYscale(), SCALE_STANDARD)
				&& DoubleUtil.isEqual(getZscale(), SCALE_STANDARD);
	}

	@Override
	protected void onCoordSystemChangedFromSetCoordSystem() {
		// not needed for 3D: animator handles it
	}

	@Override
	public void setSettingsToStandardView() {
		EuclidianSettings3D settings = getSettings();
		if (settings == null) {
			setStandardView(false);
		} else {
			settings.setStandardView();
			// settings should have been reset before
			settingsChanged(settings);
			// reset rendering
			reset();
		}
	}

	@Override
	public void updateHighlight(GeoElementND geo) {
		if (geo.hasDrawable3D()) {
			Drawable3D d = drawable3DMap.get(geo);
			if (d != null) {
				d.setWaitForUpdateVisualStyle(GProperty.HIGHLIGHT);
			}
		}
	}

	/**
	 *
	 * @return true if that view draws labels
	 */
	public boolean drawsLabels() {
		return true;
	}

	@Override
	public boolean hasVisibleObjects() {
		return drawable3DLists != null && !drawable3DLists.isEmpty();
	}

	/**
	 * @param exportToPrinter3D
	 *            3D printer export
	 */
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D) {
		drawable3DLists.exportToPrinter3D(exportToPrinter3D);
		for (int i = 0; i < 3; i++) {
			axisDrawable[i].exportToPrinter3D(exportToPrinter3D, false);
		}
		if (xOyPlane.isPlateVisible()) {
			xOyPlaneDrawable.exportToPrinter3D(exportToPrinter3D, true);
		}
		xOyPlaneDrawable.exportToPrinter3D(exportToPrinter3D, false);
	}

	/**
	 *
	 * @param thickness
	 *            line thickness
	 * @return thickness for lines (may be emphasized for STL export)
	 */
	public float getThicknessForLine(int thickness) {
		return thickness;
	}

	/**
	 *
	 * @return thickness for surfaces (only for 3D print export)
	 */
	public float getThicknessForSurface() {
		return 0f;
	}

	/**
	 *
	 * @param size
	 *            point size
	 * @return size for points (may be emphasized for STL export)
	 */
	public float getSizeForPoint(int size) {
		return size;
	}

	/**
	 *
	 * @return factor for axes ticks thickness
	 */
	public float getTicksThicknessFactor() {
		return 4f;
	}

	/**
	 *
	 * @return factor for axes minor ticks thickness
	 */
	public float getTicksMinorThicknessFactor() {
		return 2.5f;
	}

	/**
	 *
	 * @return factor for axes ticks delta
	 */
	public float getTicksDeltaFactor() {
		return 1f;
	}

	/**
	 * @param isXRDrawing
	 *            whether XR is active
	 */
	public void setXRDrawing(boolean isXRDrawing) {
		if (mIsXRDrawing != isXRDrawing) {
			mIsXRDrawing = isXRDrawing;
			if (isXRDrawing) {
                boolean boundsNeededUpdate = updateObjectsBounds(true,
                        false, true);
                if (boundsNeededUpdate) {
                    clippingCubeDrawable.enlargeFor(boundsMin);
                    clippingCubeDrawable.enlargeFor(boundsMax);
                }
                if (boundsNeededUpdate) {
                    translationZzeroForAR = -boundsMin.getZ();
                    // ensure showing plane if visible and not too far
                    if (translationZzeroForAR < 0
                            && (((getShowGrid() || getShowPlane()) && getZmin() < 0)
                                || isAtLeastOneAxisVisible())) {
                        translationZzeroForAR = 0;
                    }
                } else {
                    translationZzeroForAR = 0;
                }
                setARFloorZ(-translationZzeroForAR);
                translationZzeroForAR -= getZZero();
                getRenderer().setARScaleAtStart();
                updateMatrix();
                reset(false);
			} else {
                translationZzeroForAR = 0;
                updateMatrix();
                reset(true);
            }
		}
	}

	private boolean isAtLeastOneAxisVisible() {
	    for (int i = 0; i < 3; i++) {
	        if (axisDrawable[i].isVisible()) {
	            return true;
            }
        }
	    return false;
    }

	private void setARFloorZ(double z) {
	    arFloorZ = z;
        getRenderer().setARFloorZ(z);
        arZZeroAtStart = getZZero();
    }

	/**
	 * 
	 * @return shift used for AR floor
	 */
    public double getARFloorShift() {
        return arZZeroAtStart - getZZero();
    }

    /**
     *
     * @return z value which stands on the floor (AR)
     */
    public double getARMinZ() {
        return arFloorZ + getARFloorShift();
    }

	/**
	 * @return whether XR is active
	 */
	public boolean isXRDrawing() {
		return mIsXRDrawing;
	}

	/**
	 * set XR enabled/disabled
	 * 
	 * @param isXREnabled
	 *            flag
	 */
	public void setXREnabled(boolean isXREnabled) {
		mIsXREnabled = isXREnabled;
        if (euclidianController.isCreatingPointAR()) {
            target.updateType(this);
        }
		updateMatrixForCursor3D();
        ((EuclidianController3D) euclidianController).scheduleMouseExit();
	}

	@Override
	public boolean isXREnabled() {
		return mIsXREnabled;
	}

	@Override
	public boolean checkHitForStylebar() {
		return true;
	}

	public boolean showPlaneOutlineIfNeeded() {
		return !isXRDrawing();
	}

	@Override
	public boolean canMoveFunctions() {
		return false;
	}

	@Override
	public boolean canShowPointStyle() {
		return false;
	}

	/**
	 * 
	 * @return cursor matrix
	 */
	public CoordMatrix4x4 getCursorMatrix() {
		return cursorMatrix;
	}

	@Override
	public void setHasMouse(boolean flag) {
		super.setHasMouse(flag);
		setCursor3DVisible(flag);
	}

	@Override
	public void showFocusOn(GeoElement geo) {
		if (geo.isGeoPoint() && geo.isVisibleInView3D()
				&& geo.isEuclidianVisible()) {
			euclidianController.createNewPoint((GeoPointND) geo);
			if (geo.isGeoElement3D()) {
				getCursor3D().setMoveMode(GeoPointND.MOVE_MODE_XYZ);
			}
			updateMatrixForCursor3D();
		} else {
			setCursor3DVisible(false);
		}
	}

	/**
	 * enlarge clipping values regarding point coords
	 * 
	 * @param point
	 *            point
	 */
    public void enlargeClippingForPoint(GeoPointND point) {
        if (isXREnabled()) {
            if (clippingCubeDrawable.enlargeFor(point.getInhomCoordsInD3())) {
                setViewChangedByZoom();
                setWaitForUpdate();
            }
        }
    }

	/**
	 * enlarge clipping for AR
     */
    public void enlargeClippingWhenAREnabled() {
        if (isXREnabled()) {
            if (updateObjectsBounds(true, true, true)) {
                boolean needsUpdate1 = clippingCubeDrawable.enlargeFor(boundsMin);
                boolean needsUpdate2 = clippingCubeDrawable.enlargeFor(boundsMax);
                if (needsUpdate1 || needsUpdate2) {
                    setViewChangedByZoom();
                    setWaitForUpdate();
                }
            }
        }
    }

	/**
	 * reset view for AR
	 */
	public void resetViewFromAR() {
		resetSettings();
	}

	@Override
    public void resetSettings() {
        super.resetSettings();
        // reset rendering
        reset(true);
    }

	/**
	 * set on touch listener
	 */
	public void setEuclidianPanelOnTouchListener() {
		// overriden in EuclidianView3DA
	}

	/**
	 * 
	 * @return mouse/touch gesture controller
	 */
	public MouseTouchGestureController getEuclidianPanelOnTouchListner() {
		// overriden in EuclidianView3DA
		return null;
	}

	private boolean shouldDrawCursorAtEnd() {
		return euclidianController.isCreatingPointAR();
	}

	/**
	 * queue runnable on GL thread (needs platform-specific implementation)
	 * @param runnable runnable
	 */
	public void queueOnGLThread(Runnable runnable) {
		runnable.run();
	}

    /**
     *
     * @param value value in dip
     * @return value in pixels
     */
    public float dipToPx(float value) {
        return value;
    }

	/**
	 * set ARRatio is shown
	 */
	public void setARRatioIsShown(boolean arRatioIsShown) {
		this.arRatioIsShown = arRatioIsShown;
		XRManagerInterface arManager = renderer.getXRManager();
		if (arManager != null) {
			arManager.setRatioIsShown(arRatioIsShown);
		}
	}

	/**
	 * @return ARRatio is shown
	 */
	public boolean isARRatioShown() {
		return arRatioIsShown;
	}

	/**
	 * set AR Ratio Unit
	 */
	public void setARRatioUnit(String arRatioUnit) {
		this.arRatioUnit = arRatioUnit;
	}

	/**
	 * @return AR Ratio Unit
	 */
	public String getARRatioUnit() {
		return arRatioUnit;
	}

	/**
	 * set AR Ratio Metric System
	 */
	public void setARRatioMetricSystem(int arRatioMetricSystem) {
		this.arRatioMetricSystem = arRatioMetricSystem;
		XRManagerInterface arManager = renderer.getXRManager();
		if (arManager != null) {
			arManager.calculateAndShowRatio();
		}
	}

	/**
	 * @return AR Ratio Metric System
	 */
	public int getARRatioMetricSystem() {
		return arRatioMetricSystem;
	}

	private void set3DCoordSystem(double xzero, double yzero, double zzero, double xscale,
			double yscale, double zscale) {
		if (Double.isNaN(xscale) || (xscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (xscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}
		if (Double.isNaN(yscale) || (yscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (yscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}
		if (Double.isNaN(zscale) || (zscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (zscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}
		this.xZero = xzero;
		this.yZero = yzero;
		this.zZero = zzero;
		EuclidianSettings3D settings = getSettings();
		settings.setXscaleValue(xscale);
		settings.setYscaleValue(yscale);
		settings.setZscaleValue(zscale);
		clippingCubeDrawable.doUpdateMinMax();
	}

	@Override
	protected void setStandardCoordSystem(boolean repaint) {
		if (getSettings() != null && !getSettings().isSetStandardCoordSystem()) {
			return;
		}
		set3DCoordSystem(XZERO_SCENE_STANDARD, YZERO_SCENE_STANDARD, ZZERO_SCENE_STANDARD,
				SCALE_STANDARD, SCALE_STANDARD, SCALE_STANDARD);
		getSettings().setUpdateScaleOrigin(
				getSettings() != null && !getSettings().isSetStandardCoordSystem());
	}

	@Override
	protected void setSizeListeners() {
		super.setSizeListeners();
		if (zminObject != null) {
			((GeoNumeric) zmaxObject).addEVSizeListener(this);
			((GeoNumeric) zminObject).addEVSizeListener(this);
		}
	}

	@Override
	public void resetXYMinMaxObjects() {
		if ((evNo == 1) || (evNo == 2)) {
			EuclidianSettings3D es =
					(EuclidianSettings3D) getApplication().getSettings().getEuclidian(evNo);

			GeoNumeric xmao = new GeoNumeric(kernel.getConstruction(),
					xmaxObject.getNumber().getDouble());
			GeoNumeric xmio = new GeoNumeric(kernel.getConstruction(),
					xminObject.getNumber().getDouble());
			GeoNumeric ymao = new GeoNumeric(kernel.getConstruction(),
					ymaxObject.getNumber().getDouble());
			GeoNumeric ymio = new GeoNumeric(kernel.getConstruction(),
					yminObject.getNumber().getDouble());
			GeoNumeric zmao = new GeoNumeric(kernel.getConstruction(),
					zminObject.getNumber().getDouble());
			GeoNumeric zmio = new GeoNumeric(kernel.getConstruction(),
					zminObject.getNumber().getDouble());
			es.setXmaxObject(xmao, false);
			es.setXminObject(xmio, false);
			es.setYmaxObject(ymao, false);
			es.setYminObject(ymio, false);
			es.setZmaxObject(zmao, false);
			es.setZminObject(zmio, true);
		}
	}

	@Override
	protected void setMinMaxObjects() {
		super.setMinMaxObjects();
		zminObject = new GeoNumeric(kernel.getConstruction());
		zmaxObject = new GeoNumeric(kernel.getConstruction());
	}

	@Override
	public boolean isZoomable() {
		if (super.isZoomable()) {
			if (!GeoNumeric.isChangeable(zminObject)) {
				return false;
			}
			return GeoNumeric.isChangeable(zmaxObject);
		}
		return false;
	}
}
