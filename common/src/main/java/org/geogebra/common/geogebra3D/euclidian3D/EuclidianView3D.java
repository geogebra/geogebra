package org.geogebra.common.geogebra3D.euclidian3D;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
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
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawImplicitSurface3D;
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
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawText3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawVector3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3DLists;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3DListsForView;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.ScalerXYZ;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterCursor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.geogebra3D.kernel3D.Kernel3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicSection;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3DConstant;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSurfaceCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.GeoImplicitSurface;
import org.geogebra.common.kernel.EVProperty;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.debug.Log;

/**
 * Class for 3D view
 * 
 * @author mathieu
 * 
 */
@SuppressWarnings("javadoc")
public abstract class EuclidianView3D extends EuclidianView implements
		EuclidianView3DInterface, ScalerXYZ {

	// since V3.0 this factor is 1, before it was 0.5
	final public static double DEFAULT_GRID_DIST_FACTOR = 1;
	/** default scene x-coord of origin */
	public static final double XZERO_SCENE_STANDARD = 0;
	/** default scene y-coord of origin */
	public static final double YZERO_SCENE_STANDARD = 0;
	/** default scene z-coord of origin */
	public static final double ZZERO_SCENE_STANDARD = -1.5;
	public final static double ANGLE_ROT_OZ = -60;
	public final static double ANGLE_ROT_XOY = 20;
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
	final static public int PROJECTION_ORTHOGRAPHIC = 0;
	final static public int PROJECTION_PERSPECTIVE = 1;
	final static public int PROJECTION_GLASSES = 2;
															// DrawList3D();
															final static public int PROJECTION_OBLIQUE = 3;
	// DrawList3D();
	final static public int PROJECTION_EQUIRECTANGULAR = 4;
	protected static final int CURSOR_DEFAULT = 0;
	/**
	 * id of z-axis
	 */
	static final int AXIS_Z = 2; // AXIS_X and AXIS_Y already defined in
	private static final int CURSOR_DRAG = 1;
	private static final int CURSOR_MOVE = 2;
	private static final int CURSOR_HIT = 3;
	private static final int PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT = 2500;
	// maximum angle between two line segments
	private static final double MAX_ANGLE_SPEED_SURFACE = 20; // degrees
	private static final double MAX_BEND_SPEED_SURFACE = Math.tan(MAX_ANGLE_SPEED_SURFACE * Kernel.PI_180);
	public static double automaticGridDistanceFactor = DEFAULT_GRID_DIST_FACTOR;
	protected Renderer renderer;
	// distances between grid lines
	protected boolean automaticGridDistance = true;
	// viewing values
	protected double zZero;
	protected double zZeroOld = 0;
	protected double aOld, bOld;
	// picking and hits
	protected Hits3D hits = new Hits3D(); // objects picked from openGL
	protected DrawClippingCube3D clippingCubeDrawable;
	protected GeoPoint3D cursorOnXOYPlane;
	protected CoordMatrix rotationAndScaleMatrix;
	// EuclidianViewInterface
	protected Coords pickPoint = new Coords(0, 0, 0, 1);
	protected CoordMatrix4x4 tmpMatrix4x4_3 = CoordMatrix4x4.Identity();
	protected Coords tmpCoords1 = new Coords(4), tmpCoords2 = new Coords(4);
	protected Hits3D tempArrayList = new Hits3D();
	protected GColor bgColor, bgApplyedColor;

	// cursor
	// private Kernel kernel;
	private Kernel3D kernel3D;
	// list of 3D objects
	private boolean waitForUpdate = true; // says if it waits for update...
	// public boolean waitForPick = false; //says if it waits for update...
	private Drawable3DListsForView drawable3DLists;// = new DrawList3D();
	/**
	 * list for drawables that will be added on next frame
	 */
	private LinkedList<Drawable3D> drawable3DListToBeAdded;// = new
	/**
	 * list for drawables that will be removed on next frame
	 */
	private LinkedList<Drawable3D> drawable3DListToBeRemoved;// = new
	/**
	 * list for Geos to that will be added on next frame
	 */
	private TreeSet<GeoElement> geosToBeAdded;
	// Map (geo, drawable) for GeoElements and Drawables
	private TreeMap<GeoElement, Drawable3D> drawable3DMap = new TreeMap<GeoElement, Drawable3D>();
	// matrix for changing coordinate system
	private CoordMatrix4x4 mWithoutScale = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 mWithScale = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 mInvWithUnscale = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 mInvTranspose = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 undoRotationMatrix = CoordMatrix4x4.Identity();
	private double a = ANGLE_ROT_OZ;
	private double b = ANGLE_ROT_XOY;// angles (in degrees)

	// animation
	private double aNew, bNew;
	/**
	 * direction of view
	 */
	private Coords viewDirection = Coords.VZ.copyVector();
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
	private GeoPoint3D cursor3D;
	private int cursor3DType = PREVIEW_POINT_NONE;
	private EuclidianCursor cursor = EuclidianCursor.DEFAULT;
	/** starting and ending scales */
	private double xScaleEnd, yScaleEnd, zScaleStart, zScaleEnd;
	/** velocity of animated scaling */
	private double animatedScaleTimeFactor;
	/** starting time for animated scale */
	private double animatedScaleTimeStart;
	/** x start of animated scale */
	private double animatedScaleStartX;
	/** y start of animated scale */
	private double animatedScaleStartY;
	/** z start of animated scale */
	private double animatedScaleStartZ;
	/** x end of animated scale */
	private double animatedScaleEndX;
	/** y end of animated scale */
	private double animatedScaleEndY;
	/** z end of animated scale */
	private double animatedScaleEndZ;
	/** speed for animated rotation */
	private double animatedRotSpeed;
	/** starting time for animated rotation */
	private double animatedRotTimeStart;
	/** says if the view is frozen (see freeze()) */
	private boolean isFrozen = false;
	private CoordMatrix4x4 scaleMatrix = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 undoScaleMatrix = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 translationMatrixWithScale = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 translationMatrixWithoutScale = CoordMatrix4x4
			.Identity();
	private CoordMatrix4x4 undoTranslationMatrix = CoordMatrix4x4.Identity();
	private CoordMatrix rotationMatrix;
	private Coords viewDirectionPersp = new Coords(4);
	private Coords tmpCoordsLength3 = new Coords(3);
	private int intersectionThickness;
	private GeoPointND intersectionPoint;
	private CoordMatrix4x4 tmpMatrix4x4 = CoordMatrix4x4.Identity();
	private boolean defaultCursorWillBeHitCursor = false;
	private double[] parameters = new double[2];
	private boolean viewChangedByZoom = true;
	private boolean viewChangedByTranslate = true;
	private boolean viewChangedByRotate = true;
	private int pointStyle;
	private int projection = PROJECTION_ORTHOGRAPHIC;
	private double[] projectionPerspectiveEyeDistance =
			{PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT, PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT};
	private boolean isGlassesGrayScaled = true;
	private boolean isGlassesShutDownGreen = false;
	private double[] eyeX = {-100, 100}, eyeY = {0, 0};
	private double projectionObliqueAngle = 30;
	private double projectionObliqueFactor = 0.5;
	private Coords boundsMin, boundsMax;
	private double fontScale = 1;

	/**
	 * common constructor
	 *
	 * @param ec
	 *            controller on this
	 */
	public EuclidianView3D(EuclidianController3D ec, EuclidianSettings settings) {

		super(ec, EVNO_3D, settings);
		// don't remove, it's important we pick up when this class is created by
		// mistake
		Log.error(
				"******************************************************************************");
		Log.error(
				"******************* 3D View being initialized ********************************");
		Log.error(
				"******************************************************************************");
		Log.printStacktrace("");
		this.kernel3D = (Kernel3D) ec.getKernel();
		euclidianController.setView(this);

		start();

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
	 * @return intersection interval
	 */
	private static void intervalUnion(double[] minmax, double v1, double v2) {

		// Log.debug(v1+","+v2);

		if (Double.isNaN(v2)) {
			return;
		}

		if (v1 > v2) {
			double v = v1;
			v1 = v2;
			v2 = v;
		}

		if (v1 < minmax[0] && !Double.isInfinite(v1))
			minmax[0] = v1;

		if (v2 > minmax[1] && !Double.isInfinite(v2))
			minmax[1] = v2;

	}

	@Override
	protected void initAxesValues() {
		axesNumberFormat = new NumberFormatAdapter[3];
		showAxesNumbers = new boolean[]{true, true, true};
		axesLabels = new String[]{null, null, null};
		axesLabelsStyle = new int[]{GFont.PLAIN, GFont.PLAIN, GFont.PLAIN};
		axesUnitLabels = new String[]{null, null, null};
		axesTickStyles = new int[]{
				EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
				EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
				EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR};
		automaticAxesNumberingDistances = new boolean[]{true, true, true};
		axesNumberingDistances = new double[]{2, 2, 2};
		axesDistanceObjects = new GeoNumberValue[] { null, null, null };
		drawBorderAxes = new boolean[]{false, false, false};
		axisCross = new double[]{0, 0, 0};
		positiveAxes = new boolean[]{false, false, false};
		piAxisUnit = new boolean[]{false, false, false};
		gridDistances = new double[]{2, 2, Math.PI / 6};
		axesTickInterval = new double[]{ 1, 1, 1};
	}

	public int getAxisTickStyle(int i) {
		return axesTickStyles[i];
	}

	/**
	 * create the panel
	 */
	abstract protected void createPanel();

	abstract protected Renderer createRenderer();

	protected void start() {

		drawable3DLists = new Drawable3DListsForView(this);
		drawable3DListToBeAdded = new LinkedList<Drawable3D>();
		drawable3DListToBeRemoved = new LinkedList<Drawable3D>();

		geosToBeAdded = new TreeSet<GeoElement>();

		Log.debug("create gl renderer");
		renderer = createRenderer();
		if (renderer == null) {
			return;
		}
		renderer.setDrawable3DLists(drawable3DLists);

		createExportToPrinter3D();

		createPanel();

		attachView();

		initAxisAndPlane();
		kernel3D.getConstruction().setIgnoringNewTypes(true);
		// previewables
		cursor3D = new GeoPoint3D(kernel3D.getConstruction());
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

	protected void createExportToPrinter3D() {
		// not implemented here
	}

	/**
	 * init the axis and xOy plane
	 */
	public void initAxisAndPlane() {
		// axis
		axis = new GeoAxisND[3];
		axisDrawable = new DrawAxis3D[3];
		axis[0] = kernel3D.getXAxis3D();
		axis[1] = kernel3D.getYAxis3D();
		axis[2] = kernel3D.getZAxis3D();

		for (int i = 0; i < 3; i++) {
			axis[i].setLabelVisible(true);
			axisDrawable[i] = (DrawAxis3D) createDrawable((GeoElement) axis[i]);
		}

		// clipping cube
		clippingCube = kernel3D.getClippingCube();
		clippingCube.setEuclidianVisible(true);
		clippingCube.setObjColor(GColor.GRAY);
		clippingCube.setLineThickness(1);
		clippingCube.setIsPickable(false);
		clippingCubeDrawable = (DrawClippingCube3D) createDrawable(clippingCube);

		// plane
		xOyPlane = kernel3D.getXOYPlane();
		xOyPlane.setEuclidianVisible(true);
		xOyPlane.setGridVisible(true);
		xOyPlane.setPlateVisible(true);
		// xOyPlane.setFading(0);
		xOyPlaneDrawable = (DrawPlane3D) createDrawable(xOyPlane);

	}

	// POINT_CAPTURING_STICKY_POINTS locks onto these points
	// not implemented yet in 3D
	@Override
	public ArrayList<GeoPointND> getStickyPointList() {
		return new ArrayList<GeoPointND>();
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
	public Renderer getRenderer() {
		return renderer;
	}

	/**
	 * adds a GeoElement3D to this view
	 */
	@Override
	public void add(GeoElement geo) {

		if (geo.isVisibleInView3D()) {
			setWaitForUpdate();
			geosToBeAdded.add(geo);
		}
	}

	@Override
	protected boolean createAndAddDrawable(GeoElement geo) {
		geosToBeAdded.add(geo);
		return true;
	}

	@Override
	protected void repaintForPreviewFromInputBar() {
		setWaitForUpdate();
		repaintView();
	}

	/**
	 * add the geo now
	 *
	 * @param geo
	 */
	private void addNow(GeoElement geo) {

		// check if geo has been already added
		if (getDrawableND(geo) != null)
			return;

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
	 */
	public void addToDrawable3DLists(Drawable3D d) {
		/*
		 * if (d.getGeoElement().getLabel().equals("a")){
		 * Application.debug("d="+d); }
		 */

		drawable3DListToBeAdded.add(d);
	}

	@Override
	public Drawable3D newDrawable(GeoElement geo) {

		Drawable3D d = null;
		if (geo.hasDrawable3D()) {

			switch (geo.getGeoClassType()) {

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
					if (geo instanceof GeoPlane3DConstant)
						d = new DrawPlaneConstant3D(this, (GeoPlane3D) geo,
								axisDrawable[AXIS_X], axisDrawable[AXIS_Y]);
					else
						d = new DrawPlane3D(this, (GeoPlane3D) geo);

					break;

				case POLYGON:
				case POLYGON3D:
					d = new DrawPolygon3D(this, (GeoPolygon) geo);
					break;

				case PENSTROKE:
				case POLYLINE:
				case POLYLINE3D:
					d = new DrawPolyLine3D(this, geo);
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
				d = new DrawLocus3D(this, (GeoLocusND) geo, geo,
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
						case 2:
							d = newDrawSurface3D(geoFun);
							break;
				/*
				 * case 3: d = new DrawImplicitFunction3Var(this, geoFun);
				 * break;
				 */
					}
					break;

				case SURFACECARTESIAN3D:
					d = newDrawSurface3D((GeoSurfaceCartesian3D) geo);
					break;

				case TEXT:
					d = new DrawText3D(this, (GeoText) geo);
					break;

				case CLIPPINGCUBE3D:
					d = new DrawClippingCube3D(this, (GeoClippingCube3D) geo);
					break;
				case IMPLICIT_SURFACE_3D:
					d = new DrawImplicitSurface3D(this, (GeoImplicitSurface) geo);
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
		if (d != null)
			drawable3DMap.put(geo, d);

		return d;
	}

	/**
	 * converts the vector to scene coords
	 *
	 * @param vInOut
	 *            vector
	 */
	final public void toSceneCoords3D(Coords vInOut) {
		changeCoords(mInvWithUnscale, vInOut);
	}

	/**
	 * converts the vector to screen coords
	 *
	 * @param vInOut vector
	 */
	final public void toScreenCoords3D(Coords vInOut) {
		changeCoords(mWithScale, vInOut);
	}

	/**
	 * return the matrix : screen coords -> scene coords.
	 *
	 * @return the matrix : screen coords -> scene coords.
	 */
	final public CoordMatrix4x4 getToSceneMatrix() {
		return mInvWithUnscale;
	}

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

	final public CoordMatrix4x4 getToScreenMatrixForGL() {
		if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
			return mWithoutScale;
		}
		return mWithScale;
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

	public void setYAxisVertical(boolean flag) {
		getSettings().setYAxisVertical(flag);

	}

	public boolean getUseLight() {
		return getSettings().getUseLight();
	}

	private void updateRotationMatrix() {

		CoordMatrix m1, m2;

		if (getYAxisVertical()) { // y axis taken for up-down direction
			m1 = CoordMatrix.rotation3DMatrix(CoordMatrix.X_AXIS, (this.b)
					* EuclidianController3D.ANGLE_TO_DEGREES);
			m2 = CoordMatrix.rotation3DMatrix(CoordMatrix.Y_AXIS,
					(-this.a - 90) * EuclidianController3D.ANGLE_TO_DEGREES);
		} else { // z axis taken for up-down direction
			m1 = CoordMatrix.rotation3DMatrix(CoordMatrix.X_AXIS, (this.b - 90)
					* EuclidianController3D.ANGLE_TO_DEGREES);
			m2 = CoordMatrix.rotation3DMatrix(CoordMatrix.Z_AXIS,
					(-this.a - 90) * EuclidianController3D.ANGLE_TO_DEGREES);
		}

		rotationMatrix = m1.mul(m2);
	}

	// TODO specific scaling for each direction
	// private double scale = 50;

	private void updateScaleMatrix() {
		scaleMatrix.set(1, 1, getXscale());
		scaleMatrix.set(2, 2, getYscale());
		scaleMatrix.set(3, 3, getZscale());
	}

	protected void updateTranslationMatrix() {
		if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
			translationMatrixWithScale.set(1, 4, getXZero() * getXscale());
			translationMatrixWithScale.set(2, 4, getYZero() * getYscale());
			translationMatrixWithScale.set(3, 4, getZZero() * getZscale());
		}
		translationMatrixWithoutScale.set(1, 4, getXZero());
		translationMatrixWithoutScale.set(2, 4, getYZero());
		translationMatrixWithoutScale.set(3, 4, getZZero());
		
	}

	protected void updateRotationAndScaleMatrices() {

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

	protected void setGlobalMatrices() {

		if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
			mWithoutScale.setMul(rotationMatrix, translationMatrixWithScale);
		}
		mWithScale
				.setMul(rotationAndScaleMatrix, translationMatrixWithoutScale);


		mInvWithUnscale.setMul(undoTranslationMatrix,
				tmpMatrix4x4.setMul(undoScaleMatrix, undoRotationMatrix));

		mInvTranspose.setTranspose(mInvWithUnscale);

		updateEye();

	}

	public void updateMatrix() {

		// rotations and scaling
		updateRotationAndScaleMatrices();

		// translation
		updateTranslationMatrix();
		updateUndoTranslationMatrix();

		// set global matrix and inverse, and eye position
		setGlobalMatrices();

	}

	protected void updateUndoTranslationMatrix() {
		undoTranslationMatrix.set(1, 4, -getXZero());
		undoTranslationMatrix.set(2, 4, -getYZero());
		undoTranslationMatrix.set(3, 4, -getZZero());
	}

	private void updateEye() {

		// update view direction
		if (projection == PROJECTION_OBLIQUE)
			viewDirection = renderer.getObliqueOrthoDirection().copyVector();
		else
			viewDirection = Coords.VZm.copyVector();
		toSceneCoords3D(viewDirection);
		viewDirection.normalize();

		// update eye position
		if (projection == PROJECTION_ORTHOGRAPHIC
				|| projection == PROJECTION_OBLIQUE)
			eyePosition = viewDirection;
		else {
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
				|| projection == PROJECTION_OBLIQUE)
			return viewDirection;

		return viewDirectionPersp;
	}

	/**
	 *
	 * @return direction for hitting
	 */
	public Coords getHittingDirection() {
		return getViewDirection();
	}

	/**
	 * @return eye position
	 */
	public Coords getEyePosition() {
		return eyePosition;
	}

	// ////////////////////////////////////
	// picking

	public void shiftRotAboutZ(double da) {
		setRotXYinDegrees(aOld + da, bOld);

		updateRotationAndScaleMatrices();

		setGlobalMatrices();

		setViewChangedByRotate();
		setWaitForUpdate();
	}

	public void setRotXYinDegrees(double a, double b) {

		// Log.debug("setRotXY: "+a+","+b);
		if (Double.isNaN(a) || Double.isNaN(b)) {
			Log.error("NaN values for setRotXYinDegrees");
			return;
		}

		this.a = a;
		this.b = b;

		if (this.b > EuclidianController3D.ANGLE_MAX)
			this.b = EuclidianController3D.ANGLE_MAX;
		else if (this.b < -EuclidianController3D.ANGLE_MAX)
			this.b = -EuclidianController3D.ANGLE_MAX;
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
	final public void translateCoordSystemInPixels(int dx, int dy, int dz,
												   int mode) {
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
		translateCoordSystemInPixels(0, 0, height / 100,
				EuclidianController.MOVE_VIEW);
	}

	private int mouseMoveDX, mouseMoveDY, mouseMoveMode;

	/**
	 * Sets coord system from mouse move
	 */
	@Override
	final public void setCoordSystemFromMouseMove(int dx, int dy, int mode) {
		mouseMoveDX = dx;
		mouseMoveDY = dy;
		mouseMoveMode = mode;
		animationType = AnimationType.MOUSE_MOVE;
	}

	final private void processSetCoordSystemFromMouseMove() {
		switch (mouseMoveMode) {
			case EuclidianController.MOVE_ROTATE_VIEW:
				setRotXYinDegrees(aOld - mouseMoveDX, bOld + mouseMoveDY);
				updateMatrix();
				setViewChangedByRotate();
				setWaitForUpdate();
				break;
			case EuclidianController.MOVE_VIEW:
				Coords v = new Coords(mouseMoveDX, -mouseMoveDY, 0, 0);
				toSceneCoords3D(v);

				if (cursorOnXOYPlane.getRealMoveMode() == GeoPointND.MOVE_MODE_XY) {
					v.projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY,
							getViewDirection(), tmpCoords1);
					setXZero(xZeroOld + tmpCoords1.getX());
					setYZero(yZeroOld + tmpCoords1.getY());
				} else {
					v.projectPlaneInPlaneCoords(CoordMatrix4x4.IDENTITY, tmpCoords1);
					setZZero(zZeroOld + tmpCoords1.getZ());
				}
				getSettings().updateOriginFromView(getXZero(), getYZero(),
						getZZero());
				updateMatrix();
				setViewChangedByTranslate();
				setWaitForUpdate();
				break;
		}
	}

	private double axisScaleFactor, axisScaleOld;
	private int axisScaleMode;

	final public void setCoordSystemFromAxisScale(double factor,
			double scaleOld, int mode) {
		axisScaleFactor = factor;
		axisScaleOld = scaleOld;
		axisScaleMode = mode;
		animationType = AnimationType.AXIS_SCALE;
	}

	final private void processSetCoordSystemFromAxisScale() {

		switch (axisScaleMode) {
		case EuclidianController.MOVE_X_AXIS:
			setXZero(xZeroOld / axisScaleFactor);
			getSettings().setXscaleValue(axisScaleFactor * axisScaleOld);
			break;
		case EuclidianController.MOVE_Y_AXIS:
			setYZero(yZeroOld / axisScaleFactor);
			getSettings().setYscaleValue(axisScaleFactor * axisScaleOld);
			break;
		case EuclidianController.MOVE_Z_AXIS:
			setZZero(zZeroOld / axisScaleFactor);
			getSettings().setZscaleValue(axisScaleFactor * axisScaleOld);
			break;

		}

		getSettings().updateOriginFromView(getXZero(), getYZero(), getZZero());


		updateMatrix();
		setViewChangedByTranslate();
		setViewChangedByZoom();
		setWaitForUpdate();

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
	 */
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
	 */
	public void setYZero(double val) {
		yZero = val;
	}

	/**
	 * @return the z-coord of the origin
	 */
	public double getZZero() {
		return zZero;
	}

	/**
	 * set the z-coord of the origin
	 *
	 * @param val
	 */
	public void setZZero(double val) {
		zZero = val;
	}

	public void setZeroFromXML(double x, double y, double z) {

		if (app.fileVersionBefore(App.getSubValues("4.9.14.0"))) {
			// new matrix multiplication (since 4.9.14)
			updateRotationMatrix();
			updateScaleMatrix();
			setXZero(x);
			setYZero(y);
			setZZero(z);
			getSettings().updateOriginFromView(x, y, z);
			updateTranslationMatrix();
			CoordMatrix mRS = rotationMatrix.mul(scaleMatrix);
			CoordMatrix matrix = ((mRS.inverse())
					.mul(translationMatrixWithoutScale)
					.mul(mRS));
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

	public double getZmin() {
		return clippingCubeDrawable.getMinMax()[2][0];
	}

	// ////////////////////////////////////////////
	// EuclidianViewInterface

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
	public double getZscale() {
		return getSettings().getZscale();
	}

	@Override
	public double getScale(int i) {
		switch (i) {
		case 0:
		default:
			return getSettings().getXscale();
		case 1:
			return getSettings().getYscale();
		case 2:
			return getSettings().getZscale();
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

	public double getScaledDistance(Coords p1, Coords p2) {
		tmpCoordsLength3.setSub(p1, p2);
		scaleXYZ(tmpCoordsLength3);
		tmpCoordsLength3.calcNorm();
		return tmpCoordsLength3.getNorm();
	}

	/**
	 * set the all-axis scale
	 */
	final private void setScale(double xscale, double yscale, double zscale) {
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
		zScaleStart = getZscale();
	}

	public void updateAnimation() {
		if (isAnimated()) {
			animate();
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
			for (GeoElement geo : geosToBeAdded)
				addNow(geo);
			geosToBeAdded.clear();

			viewChangedOwnDrawables();
			// setWaitForUpdateOwnDrawables();

			waitForUpdate = false;
		}

		// update decorations
		pointDecorations.update();
	}

	public void setWaitForUpdate() {
		waitForUpdate = true;
	}

	// ////////////////////////////////////////////////
	// ANIMATION
	// ////////////////////////////////////////////////

	/**
	 * (x,y) 2D screen coords -> 3D physical coords
	 *
	 * @param x
	 * @param y
	 * @return 3D physical coords of the picking point
	 */
	public Coords getPickPoint(GPoint mouse) {

		setPickPointFromMouse(mouse);

		if (projection == PROJECTION_PERSPECTIVE
				|| projection == PROJECTION_GLASSES) {
			viewDirectionPersp = pickPoint.sub(renderer.getPerspEye());
			toSceneCoords3D(viewDirectionPersp);
			viewDirectionPersp.normalize();
		}

		return pickPoint.copyVector();
	}

	public Coords getHittingOrigin(GPoint mouse) {
		Coords origin = getPickPoint(mouse);
		if (getProjection() == EuclidianView3D.PROJECTION_PERSPECTIVE
				|| getProjection() == EuclidianView3D.PROJECTION_GLASSES) {
			origin = getRenderer().getPerspEye().copyVector();
		}
		toSceneCoords3D(origin);

		return origin;
	}

	/**
	 * @param mouse  mouse position
	 * @param result mouse position with (0,0) on window center
	 */
	public void setCenteredPosition(GPoint mouse, GPoint result) {
		result.x = mouse.getX() + renderer.getLeft();
		result.y = -mouse.getY() + renderer.getTop();
	}

	protected void setPickPointFromMouse(GPoint mouse) {
		pickPoint.setX(mouse.getX() + renderer.getLeft());
		pickPoint.setY(-mouse.getY() + renderer.getTop());
		if (projection == PROJECTION_PERSPECTIVE
				|| projection == PROJECTION_GLASSES) {
			pickPoint.setZ(0);
		} else {
			pickPoint.setZ(renderer.getVisibleDepth());
			if (projection == PROJECTION_OBLIQUE) {
				pickPoint.setX(pickPoint.getX() - pickPoint.getZ()
						* renderer.getObliqueX());
				pickPoint.setY(pickPoint.getY() - pickPoint.getZ()
						* renderer.getObliqueY());
			}
		}
	}

	/**
	 * @param p 3D point in scene coords
	 * @return (x, y) point aligned with p
	 */
	public Coords projectOnScreen(Coords p) {
		Coords p1 = getToScreenMatrix().mul(p);// .getInhomCoords();
		if (projection == PROJECTION_PERSPECTIVE
				|| projection == PROJECTION_GLASSES) {
			Coords eye = renderer.getPerspEye();
			Coords v = p1.sub(eye);
			return new Coords(eye.getX() - eye.getZ() * v.getX() / v.getZ(),
					eye.getY() - eye.getZ() * v.getY() / v.getZ());
		}
		return new Coords(p1.getX(), p1.getY());
	}

	/**
	 * p scene coords, (dx,dy) 2D mouse move -> 3D physical coords
	 *
	 * @param p
	 * @param dx
	 * @param dy
	 * @return 3D physical coords
	 */
	public Coords getPickFromScenePoint(Coords p, int dx, int dy) {

		Coords point = getToScreenMatrix().mul(p);

		pickPoint.setX(point.get(1) + dx);
		pickPoint.setY(point.get(2) - dy);

		if (projection == PROJECTION_PERSPECTIVE
				|| projection == PROJECTION_GLASSES) {
			viewDirectionPersp = pickPoint.sub(renderer.getPerspEye());
			toSceneCoords3D(viewDirectionPersp);
			viewDirectionPersp.normalize();
		}

		return pickPoint.copyVector();
	}

	/**
	 * attach the view to the kernel
	 */
	@Override
	public void attachView() {
		kernel3D.notifyAddAll(this);
		kernel3D.attach(this);
	}

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
	 */
	public void remove(Drawable3D d) {
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
	public void reset() {

		resetAllDrawables();
		setViewChanged();
		viewChangedOwnDrawables();
		setWaitForUpdate();

	}

	@Override
	public void update(GeoElement geo) {

		// String s = geo.toString(); if (s.startsWith("F"))
		// Application.debug(s);

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

		if (styleBar != null)
			styleBar.updateVisualStyle(geo);
	}

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
	public GeoElement getLabelHit(GPoint p,
								  PointerEventType type) {
		if (type == PointerEventType.TOUCH) {
			return null;
		}
		return renderer.getLabelHit(p);
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

	/*
	 * Point pOld = null;
	 * 
	 * 
	 * public void setHits(Point p) {
	 * 
	 * 
	 * 
	 * if (p.equals(pOld)){ //Application.printStacktrace(""); return; }
	 * 
	 * 
	 * pOld = p;
	 * 
	 * //sets the flag and mouse location for openGL picking
	 * renderer.setMouseLoc(p.x,p.y,Renderer.PICKING_MODE_LABELS);
	 * 
	 * //calc immediately the hits renderer.display();
	 * 
	 * 
	 * }
	 */

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

	public void setShowPlate(boolean flag) {
		getxOyPlane().setPlateVisible(flag);
	}

	/**
	 * sets the visibility of xOy plane grid
	 *
	 * @param flag
	 */
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

	public boolean hitAnimationButton(int x, int y) {
		return false;
	}

	@Override
	public void resetMode() {
		// TODO Auto-generated method stub

	}

	/** tells if the view is under animation */
	public boolean isAnimated() {
		return animationType != AnimationType.OFF;
	}

	/**
	 * tells if the view is under rot animation
	 *
	 * @return true if there is a rotation animation
	 */
	public boolean isRotAnimated() {
		return isRotAnimatedContinue()
				|| animationType == AnimationType.ROTATION;
	}

	/**
	 * @return true if there is a continue rotation animation
	 */
	public boolean isRotAnimatedContinue() {
		return animationType == AnimationType.CONTINUE_ROTATION;
	}

	/**
	 *
	 * @param p
	 *            point
	 * @return true if the point is between min-max coords values
	 */
	public boolean isInside(Coords p) {

		double val = p.getX();
		if (val < getXmin() || val > getXmax())
			return false;

		val = p.getY();
		if (val < getYmin() || val > getYmax())
			return false;

		val = p.getZ();
		if (val < getZmin() || val > getZmax())
			return false;

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
	 * @param flag flag
	 */
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
	 * @param value reduction
	 */
	public void setClippingReduction(int value) {
		clippingCube.setReduction(value);
		setViewChanged();
		setWaitForUpdate();
	}

	@Override
	public void setAnimatedCoordSystem(double x0, double y0, int steps,
									   boolean storeUndo) {

		setAnimatedCoordSystem(XZERO_SCENE_STANDARD, YZERO_SCENE_STANDARD,
				ZZERO_SCENE_STANDARD, SCALE_STANDARD, steps);
	}

	private void setAnimatedCoordSystem(double x, double y, double z,
										double newScale, int steps) {


		rememberOrigins();
		animatedScaleStartX = getXZero();
		animatedScaleStartY = getYZero();
		animatedScaleStartZ = getZZero();

		animatedScaleEndX = x;
		animatedScaleEndY = y;
		animatedScaleEndZ = z;

		animatedScaleTimeStart = app.getMillisecondTime();
		xScaleEnd = newScale;
		yScaleEnd = newScale;
		zScaleEnd = newScale;
		animationType = AnimationType.ANIMATED_SCALE;

		animatedScaleTimeFactor = 0.0003 * steps;

	}

	@Override
	public void setAnimatedCoordSystem(double ox, double oy, double f,
									   double newScale, int steps, boolean storeUndo) {

		rememberOrigins();
		animatedScaleStartX = getXZero();
		animatedScaleStartY = getYZero();
		animatedScaleStartZ = getZZero();

		Coords v;
		if (getCursor3DType() == PREVIEW_POINT_NONE) { // use cursor only if on
			// point/path/region or
			// xOy plane
			v = new Coords(-animatedScaleStartX, -animatedScaleStartY,
					-animatedScaleStartZ, 1); // takes center of the scene for
			// fixed point
		} else {
			v = cursor3D.getInhomCoords();
			// Log.debug("\n"+v);
			if (!v.isDefined()) {
				v = new Coords(-animatedScaleStartX, -animatedScaleStartY,
						-animatedScaleStartZ, 1); // takes center of the scene
				// for fixed point
			}
		}

		// Application.debug(v);

		double factor = getXscale() / newScale;

		animatedScaleEndX = -v.getX() + (animatedScaleStartX + v.getX())
				* factor;
		animatedScaleEndY = -v.getY() + (animatedScaleStartY + v.getY())
				* factor;
		animatedScaleEndZ = -v.getZ() + (animatedScaleStartZ + v.getZ())
				* factor;

		// Application.debug("mouse = ("+ox+","+oy+")"+"\nscale end = ("+animatedScaleEndX+","+animatedScaleEndY+")"+"\nZero = ("+animatedScaleStartX+","+animatedScaleStartY+")");

		animatedScaleTimeStart = app.getMillisecondTime();
		xScaleEnd = xScaleStart / factor;
		yScaleEnd = yScaleStart / factor;
		zScaleEnd = zScaleStart / factor;
		animationType = AnimationType.ANIMATED_SCALE;

		animatedScaleTimeFactor = 0.005; // it will take about 1/2s to achieve
		// it

		// this.storeUndo = storeUndo;
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
		// Log.debug("delay=" + delay + ", rotSpeed=" + rotSpeed);

		if (Double.isNaN(rotSpeed)) {
			Log.error("NaN values for setRotContinueAnimation");
			stopAnimation();
			return;
		}

		double rotSpeed2 = rotSpeed;
		// if last drag occured more than 200ms ago, then no animation
		if (delay > 200)
			return;

		// if speed is too small, no animation
		if (Math.abs(rotSpeed2) < 0.01) {
			stopAnimation();
			return;
		}

		// if speed is too large, use max speed
		if (rotSpeed2 > 0.1)
			rotSpeed2 = 0.1;
		else if (rotSpeed2 < -0.1)
			rotSpeed2 = -0.1;
		this.getSettings().setRotSpeed(0);
		animationType = AnimationType.CONTINUE_ROTATION;
		animatedRotSpeed = -rotSpeed2;
		animatedRotTimeStart = app.getMillisecondTime() - delay;
		bOld = b;
		aOld = a;
	}

	public void setRotAnimation(Coords vn, boolean checkSameValues,
			boolean animated) {
		CoordMatrixUtil.sphericalCoords(vn, tmpCoordsLength3);
		setRotAnimation(tmpCoordsLength3.get(2) * 180 / Math.PI,
				tmpCoordsLength3.get(3) * 180 / Math.PI, checkSameValues,
				animated);
	}

	public void setRotAnimation(double rotOz, boolean checkSameValues,
			boolean animated) {
		setRotAnimation(rotOz * 180 / Math.PI, this.b, checkSameValues,
				animated);
	}

	/**
	 * start a rotation animation to be in the vector direction
	 *
	 * @param vn
	 */
	public void setRotAnimation(Coords vn) {
		setRotAnimation(vn, true, true);
	}

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
	public void setDefaultRotAnimation() {
		setRotAnimation(EuclidianView3D.ANGLE_ROT_OZ,
				EuclidianView3D.ANGLE_ROT_XOY, false);
	}

	/**
	 * start a rotation animation to go to the new values
	 *
	 * @param aN
	 * @param bN
	 * @param checkSameValues if true, check new values are same than old, in this case
	 *                        revert the view
	 */
	public void setRotAnimation(double aN, double bN, boolean checkSameValues) {
		setRotAnimation(aN, bN, checkSameValues, true);
	}

	public void setRotAnimation(double aN, double bN, boolean checkSameValues,
			boolean animated) {

		if (Double.isNaN(aN) || Double.isNaN(bN)) {
			Log.error("NaN values for setRotAnimation");
			return;
		}

		// app.storeUndoInfo();

		animationType = animated ? AnimationType.ROTATION
				: AnimationType.ROTATION_NO_ANIMATION;
		aOld = this.a % 360;
		bOld = this.b % 360;

		aNew = aN;
		bNew = bN;

		// if (aNew,bNew)=(0degrees,90degrees), then change it to (90degrees,90degrees) to have correct
		// xOy orientation
		if (Kernel.isEqual(aNew, 0, Kernel.STANDARD_PRECISION)
				&& Kernel
				.isEqual(Math.abs(bNew), 90, Kernel.STANDARD_PRECISION))
			aNew = -90;

		// looking for the smallest path
		if (aOld - aNew > 180) {
			aOld -= 360;
		} else if (aOld - aNew < -180) {
			aOld += 360;
		}

		if (checkSameValues) {
			if (Kernel.isEqual(aOld, aNew, Kernel.STANDARD_PRECISION))
				if (Kernel.isEqual(bOld, bNew, Kernel.STANDARD_PRECISION)) {
					if (!Kernel.isEqual(Math.abs(bNew), 90,
							Kernel.STANDARD_PRECISION)) {
						aNew += 180;
					}
					bNew *= -1;
					// Application.debug("ici");
				}
		}

		if (bOld > 180) {
			bOld -= 360;
		}

		animatedRotTimeStart = app.getMillisecondTime();

	}

	/**
	 * stops the animations
	 */
	public void stopAnimation() {
		animationType = AnimationType.OFF;
	}

	private enum AnimationType {
		OFF, ANIMATED_SCALE, SCALE, CONTINUE_ROTATION, ROTATION, ROTATION_NO_ANIMATION, SCREEN_TRANSLATE_AND_SCALE, MOUSE_MOVE, AXIS_SCALE
	}

	private AnimationType animationType = AnimationType.OFF;

	/**
	 * animate the view for changing scale, orientation, etc.
	 */
	private void animate() {

		switch (animationType) {
		case SCALE:
			setScale(animatedScaleEndX, animatedScaleEndY, animatedScaleEndZ);
			updateMatrix();
			stopAnimation();
			break;
		case ANIMATED_SCALE:
			double t;
			if (animatedScaleTimeFactor == 0){
				t = 1;
				stopAnimation();
			}else {
				t = (app.getMillisecondTime() - animatedScaleTimeStart)
						* animatedScaleTimeFactor;
				t += 0.2; // starting at 1/4

				if (t >= 1) {
					t = 1;
					stopAnimation();
				}
			}

			// Application.debug("t="+t+"\nscale="+(startScale*(1-t)+endScale*t));

			setScale(xScaleStart * (1 - t) + xScaleEnd * t,
					yScaleStart * (1 - t) + yScaleEnd * t,
					zScaleStart * (1 - t) + zScaleEnd * t);
			setXZero(animatedScaleStartX * (1 - t) + animatedScaleEndX * t);
			setYZero(animatedScaleStartY * (1 - t) + animatedScaleEndY * t);
			setZZero(animatedScaleStartZ * (1 - t) + animatedScaleEndZ * t);
			getSettings().updateOriginFromView(getXZero(), getYZero(),
					getZZero());

			updateMatrix();
			setViewChangedByZoom();
			setViewChangedByTranslate();

			// euclidianController3D.setFlagMouseMoved();
			break;

		case CONTINUE_ROTATION:
			double da = (app.getMillisecondTime() - animatedRotTimeStart)
					* animatedRotSpeed;

			shiftRotAboutZ(da);
			break;
			
		case ROTATION:
			t = (app.getMillisecondTime() - animatedRotTimeStart) * 0.001;
			t *= t;
			// t+=0.2; //starting at 1/4

			if (t >= 1) {
				t = 1;
				stopAnimation();
			}

			setRotXYinDegrees(aOld * (1 - t) + aNew * t, bOld * (1 - t) + bNew
					* t);

			updateMatrix();
			setViewChangedByRotate();
			break;

		case ROTATION_NO_ANIMATION:
			stopAnimation();
			setRotXYinDegrees(aNew, bNew);
			updateMatrix();
			setViewChangedByRotate();
			break;

		case SCREEN_TRANSLATE_AND_SCALE:
			setXZero(xZeroOld + screenTranslateAndScaleDX);
			setYZero(yZeroOld + screenTranslateAndScaleDY);
			setZZero(zZeroOld + screenTranslateAndScaleDZ);
			getSettings().updateOriginFromView(getXZero(), getYZero(),
					getZZero());
			setScale(xScaleEnd, yScaleEnd, zScaleEnd);
			updateMatrix();
			setViewChangedByZoom();
			setViewChangedByTranslate();
			setWaitForUpdate();

			stopAnimation();
			break;

		case MOUSE_MOVE:
			processSetCoordSystemFromMouseMove();
			stopAnimation();
			break;

		case AXIS_SCALE:
			processSetCoordSystemFromAxisScale();
			stopAnimation();
			break;
		}

	}

	@Override
	public void setHits(GPoint p, PointerEventType type) {
		// empty method : setHits3D() used instead
		// OR comment setHits3D() for shaders

		if (renderer.useLogicalPicking()) {
			renderer.setHits(p, getCapturingThreshold(type));
			if (type == PointerEventType.TOUCH && hitsEmptyOrOnlyContainsXOYPlane()) {
				renderer.setHits(p, getCapturingThresholdForTouch(type));
			}

			hasMouse = true;
			updateCursor3D();
		}

	}

	private boolean hitsEmptyOrOnlyContainsXOYPlane(){
		if (hits.size() == 0){
			return true;
		}

		if (hits.size() == 1){
			return hits.get(0) == getxOyPlane();
		}

		return false;
	}

	public int getCapturingThreshold(PointerEventType type) {
		return app.getCapturingThreshold(type);
	}

	public int getCapturingThresholdForTouch(PointerEventType type) {
		return app.getCapturingThreshold(type) * 3;
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

	/**
	 * sets the 3D hits regarding point location
	 *
	 * @param p
	 *            point location
	 */
	public void setHits3D(GPoint p) {
		if (!renderer.useLogicalPicking()) {
			renderer.setHits(p, 0);
		}
	}

	@Override
	public Hits getHits() {
		return hits.cloneHits();
	}

	/**
	 * init the hits for this view
	 *
	 * @param hits
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
	public void setSelectionRectangle(
GRectangle selectionRectangle) {
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

		animatedScaleEndX = getXscale() * zoomFactor;
		animatedScaleEndY = getYscale() * zoomFactor;
		animatedScaleEndZ = getZscale() * zoomFactor;
		animationType = AnimationType.SCALE;

	}

	/**
	 * return the point used for 3D cursor
	 *
	 * @return the point used for 3D cursor
	 */
	public GeoPoint3D getCursor3D() {
		return cursor3D;
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
	 */
	public void setCursor3DType(int v) {
		cursor3DType = v;
		// Log.debug(""+v);
	}

	public void setIntersectionThickness(GeoElement a, GeoElement b) {
		int t1 = a.getLineThickness();
		int t2 = b.getLineThickness();
		if (t2 > t1)
			intersectionThickness = t2;
		else
			intersectionThickness = t1;
		intersectionThickness += 6;
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
	public Drawable3DLists getDrawList3D() {
		return drawable3DLists;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Previewable createPreviewLine(ArrayList selectedPoints) {

		return new DrawLine3D(this, selectedPoints);

	}

	@Override
	public Previewable createPreviewSegment(ArrayList<GeoPointND> selectedPoints) {
		return new DrawSegment3D(this, selectedPoints);
	}

	@Override
	public Previewable createPreviewRay(ArrayList<GeoPointND> selectedPoints) {
		return new DrawRay3D(this, selectedPoints);
	}

	@Override
	public Previewable createPreviewVector(ArrayList<GeoPointND> selectedPoints) {
		return new DrawVector3D(this, selectedPoints);
	}

	@Override
	public Previewable createPreviewPolygon(ArrayList<GeoPointND> selectedPoints) {
		return new DrawPolygon3D(this, selectedPoints);
	}

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
	 * @return a preview sphere (center-point)
	 */
	public Previewable createPreviewSphere(ArrayList<GeoPointND> selectedPoints) {
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

		if (getCursor3DType() != PREVIEW_POINT_NONE) {
			previewDrawable.updatePreview();
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
	 * @param hits
	 */
	public void updateCursor3D(Hits hits) {
		if (hasMouse()) {
			getEuclidianController().updateNewPoint(true, hits, true, true,
					getMode() != EuclidianConstants.MODE_MOVE, // TODO
																// doSingleHighlighting
																// = false ?
					false, false);

			updateCursorOnXOYPlane();

			updateMatrixForCursor3D();
		}

	}

	private void updateCursorOnXOYPlane() {
		cursorOnXOYPlane.setWillingCoords(getCursor3D().getCoords());
		cursorOnXOYPlane.setWillingDirection(getHittingDirection());
		cursorOnXOYPlane.doRegion();

		// cursorOnXOYPlaneVisible =
		// isInside(cursorOnXOYPlane.getInhomCoords());

		// if (cursorOnXOYPlaneVisible)
		if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
			cursorOnXOYPlane.getDrawingMatrix().setDiag(1);
			scaleXYZ(cursorOnXOYPlane.getDrawingMatrix().getOrigin());
		} else {
			cursorOnXOYPlane.getDrawingMatrix().setDiag(1 / getScale());
		}

		// Application.debug(cursorOnXOYPlane.getCoords());
		// Application.debug(cursorOnXOYPlane.getDrawingMatrix());
	}

	public void switchMoveCursor() {

		if (moveCursorIsVisible())
			cursorOnXOYPlane.switchMoveMode(getMode());

	}

	protected boolean moveCursorIsVisible() {
		return cursorIsTranslateViewCursor()
				|| getEuclidianController().getMode() == EuclidianConstants.MODE_TRANSLATEVIEW;
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
		if (getHits().containsGeoPoint())
			updateCursor3D(getHits().getTopHits());
		else
			updateCursor3D(getHits());

	}

	private CoordMatrix4x4 cursorMatrix = new CoordMatrix4x4();
	private Coords cursorNormal = new Coords(3);

	/**
	 * update cursor3D matrix
	 */
	public void updateMatrixForCursor3D() {
		double t;

		Coords v;
		if (getEuclidianController()
				.getMode() == EuclidianConstants.MODE_VIEW_IN_FRONT_OF) {

			switch (getCursor3DType()) {

			case PREVIEW_POINT_REGION:
				// use region drawing directions for the cross
				cursorNormal.set3(getCursor3D().getMoveNormalDirection());
				if (cursorNormal.dotproduct(getViewDirection()) > 0) {
					cursorNormal.mulInside(-1);
				}
				if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
					scaleNormalXYZ(cursorNormal);
					cursorNormal.normalize();
				}
				CoordMatrix4x4.createOrthoToDirection(getCursor3D()
								.getDrawingMatrix().getOrigin(), cursorNormal,
								CoordMatrix4x4.VZ,
						tmpCoords1, tmpCoords2, cursorMatrix);
				if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
					scaleXYZ(cursorMatrix.getOrigin());
				} else {
					// use region drawing directions for the arrow
					t = 1 / getScale();
					cursorMatrix.mulAllButOrigin(t);
				}

				break;
			case PREVIEW_POINT_PATH:
				// use path drawing directions for the arrow
				if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
					cursorMatrix.setOrigin(getCursor3D().getDrawingMatrix()
							.getOrigin());
					scaleXYZ(cursorMatrix.getOrigin());
					cursorNormal.set3(((GeoElement) getCursor3D().getPath())
							.getMainDirection());
					if (cursorNormal.dotproduct(getViewDirection()) > 0) {
						cursorNormal.mulInside(-1);
					}
					scaleXYZ(cursorNormal);
					cursorNormal.normalize();
					CoordMatrix4x4.createOrthoToDirection(getCursor3D()
							.getDrawingMatrix().getOrigin(), cursorNormal,
							CoordMatrix4x4.VZ, tmpCoords1, tmpCoords2,
							cursorMatrix);
					scaleXYZ(cursorMatrix.getOrigin());
				} else {
					t = 1 / getScale();
					cursorNormal.set3(((GeoElement) getCursor3D().getPath())
							.getMainDirection());
					if (cursorNormal.dotproduct(getViewDirection()) > 0) {
						cursorNormal.mulInside(-1);
					}
					cursorNormal.normalize();

					CoordMatrix4x4.createOrthoToDirection(getCursor3D()
							.getDrawingMatrix().getOrigin(), cursorNormal,
							CoordMatrix4x4.VZ, tmpCoords1, tmpCoords2,
							cursorMatrix);
					cursorMatrix.mulAllButOrigin(t);
				}
				break;

			}
		} else if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)
				&& moveCursorIsVisible()) {

			if (cursor != EuclidianCursor.MOVE) {
				cursorMatrix.setOrigin(
						getCursor3D().getDrawingMatrix().getOrigin());
				scaleXYZ(cursorMatrix.getOrigin());
				switch (cursor) {
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
			switch (getCursor3DType()) {

			case PREVIEW_POINT_FREE:
				// use default directions for the cross
				if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
					cursorMatrix.setDiagonal3(1);
					cursorMatrix.setOrigin(getCursor3D().getDrawingMatrix()
							.getOrigin());
					scaleXYZ(cursorMatrix.getOrigin());
				} else {
					cursorMatrix.setOrigin(getCursor3D().getDrawingMatrix()
							.getOrigin());
					t = 1 / getScale();
					cursorMatrix.setVx(Coords.VX.mul(t));
					cursorMatrix.setVy(Coords.VY.mul(t));
					cursorMatrix.setVz(Coords.VZ.mul(t));
				}
				break;
			case PREVIEW_POINT_REGION:
				// use region drawing directions for the cross
				cursorNormal.set3(getCursor3D().getMoveNormalDirection());
				if (cursorNormal.dotproduct(getViewDirection()) > 0) {
					cursorNormal.mulInside(-1);
				}
				if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
					scaleNormalXYZ(cursorNormal);
					cursorNormal.normalize();
					CoordMatrix4x4.createOrthoToDirection(getCursor3D()
							.getDrawingMatrix().getOrigin(), cursorNormal,
							CoordMatrix4x4.VZ, tmpCoords1, tmpCoords2,
							cursorMatrix);
					scaleXYZ(cursorMatrix.getOrigin());
				} else {
					// use region drawing directions for the arrow
					CoordMatrix4x4.createOrthoToDirection(getCursor3D()
							.getDrawingMatrix().getOrigin(), cursorNormal,
							CoordMatrix4x4.VZ, tmpCoords1, tmpCoords2,
							cursorMatrix);
					t = 1 / getScale();
					cursorMatrix.mulAllButOrigin(t);
				}
				break;
			case PREVIEW_POINT_PATH:
			case PREVIEW_POINT_REGION_AS_PATH:
				// use path drawing directions for the cross
				if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
					cursorMatrix.setOrigin(getCursor3D().getDrawingMatrix()
							.getOrigin());
					scaleXYZ(cursorMatrix.getOrigin());
					GeoElement path = getCursorPath();
					cursorNormal.set3(path.getMainDirection());
					scaleXYZ(cursorNormal);
					cursorNormal.normalize();
					CoordMatrix4x4.completeOrtho(cursorNormal, tmpCoords1,
							tmpCoords2,
							cursorMatrix);
					t = 10 + path.getLineThickness();
					cursorMatrix.getVy().mulInside3(t);
					cursorMatrix.getVz().mulInside3(t);
				} else {
					cursorMatrix.setOrigin(getCursor3D().getDrawingMatrix()
							.getOrigin());

					t = 1 / getScale();

					GeoElement path = getCursorPath();
					v = path.getMainDirection();
					CoordMatrix4x4.completeOrtho(v, tmpCoords1, tmpCoords2,
							cursorMatrix);

					cursorMatrix
							.setVx(cursorMatrix.getVx().normalized().mul(t));
					t *= (10 + path.getLineThickness());
					cursorMatrix.setVy(cursorMatrix.getVy().mul(t));
					cursorMatrix.setVz(cursorMatrix.getVz().mul(t));

				}
				break;
			case PREVIEW_POINT_DEPENDENT:
				// use size of intersection
				cursorMatrix.setOrigin(getCursor3D().getDrawingMatrix()
						.getOrigin());
				if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
					scaleXYZ(cursorMatrix.getOrigin());
				}
				t = unscale(getIntersectionThickness());
				cursorMatrix.getVx().setMul(Coords.VX, t);
				cursorMatrix.getVy().setMul(Coords.VY, t);
				cursorMatrix.getVz().setMul(Coords.VZ, t);
				break;
			case PREVIEW_POINT_ALREADY:

				if (getCursor3D().hasPath()) {
					cursorNormal.set3(((GeoElement) getCursor3D().getPath())
							.getMainDirection());
					if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
						scaleXYZ(cursorNormal);
						cursorNormal.normalize();
					}

					CoordMatrix4x4.completeOrtho(cursorNormal, tmpCoords1,
							tmpCoords2,
							tmpMatrix4x4);

					cursorMatrix.setVx(tmpMatrix4x4.getVy());
					cursorMatrix.setVy(tmpMatrix4x4.getVz());
					cursorMatrix.setVz(tmpMatrix4x4.getVx());
					cursorMatrix.setOrigin(tmpMatrix4x4.getOrigin());

				} else if (getCursor3D().hasRegion()) {
					cursorNormal.set3(getCursor3D().getMoveNormalDirection());
					if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
						scaleNormalXYZ(cursorNormal);
						cursorNormal.normalize();
					}
					CoordMatrix4x4.createOrthoToDirection(getCursor3D()
							.getCoordsInD3(), cursorNormal, CoordMatrix4x4.VZ,
							tmpCoords1,
							tmpCoords2, cursorMatrix);
				} else {
					CoordMatrix4x4.Identity(cursorMatrix);
				}

				cursorMatrix.setOrigin(getCursor3D().getDrawingMatrix()
						.getOrigin());
				if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
					scaleXYZ(cursorMatrix.getOrigin());
				}

				cursorMatrix.getVx().normalize();
				// use size of point
				t = unscale(Math.max(1,
						getCursor3D().getPointSize() / 6.0 + 0.5));
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

	private GeoElement getCursorPath() {
		if (getCursor3DType() == PREVIEW_POINT_PATH) {
			return (GeoElement) getCursor3D().getPath();
		}

		// PREVIEW_POINT_REGION_AS_PATH
		return (GeoElement) getCursor3D().getRegion();
	}

	@Override
	public void setPreview(Previewable previewDrawable) {

		// Log.debug(""+previewDrawable);

		if (this.previewDrawable == previewDrawable) {
			return;
		}

		if (this.previewDrawable != null) {
			this.previewDrawable.disposePreview();
		}

		if (previewDrawable != null && previewDrawable instanceof Drawable3D) {
			if (((Drawable3D) previewDrawable).getGeoElement() != null)
				addToDrawable3DLists((Drawable3D) previewDrawable);
			// drawable3DLists.add((Drawable3D) previewDrawable);
		}

		// Application.debug("drawList3D :\n"+drawList3D);

		// setCursor3DType(PREVIEW_POINT_NONE);

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
	 */
	public void setPointDecorations(GeoPointND point) {

		pointDecorations.setPoint(point);

	}

	/**
	 * draws the mouse cursor (for glasses)
	 *
	 * @param renderer1 renderer
	 */
	public void drawMouseCursor(Renderer renderer1) {
		if (!hasMouse())
			return;

		if (getProjection() != PROJECTION_GLASSES) // && getProjection() !=
			// PROJECTION_PERSPECTIVE)
			return;

		GPoint mouseLoc = euclidianController.getMouseLoc();
		if (mouseLoc == null)
			return;

		Coords v;

		if (getCursor3DType() == CURSOR_DEFAULT) {
			// if mouse is over nothing, use mouse coords and screen for depth
			v = new Coords(mouseLoc.x + renderer1.getLeft(), -mouseLoc.y
					+ renderer1.getTop(), 0, 1);
		} else {
			// if mouse is over an object, use its depth and mouse coords
			Coords eye = renderer1.getPerspEye();
			double z = getToScreenMatrix().mul(getCursor3D().getCoords())
					.getZ() + 20; // to be over
			double eyeSep = renderer1.getEyeSep(); // TODO eye lateralization

			double x = mouseLoc.x + renderer1.getLeft() + eyeSep - eye.getX();
			double y = -mouseLoc.y + renderer1.getTop() - eye.getY();
			double dz = eye.getZ() - z;
			double coeff = dz / eye.getZ();

			v = new Coords(x * coeff - eyeSep + eye.getX(), y * coeff
					+ eye.getY(), z, 1);
		}

		drawMouseCursor(renderer1, v);

	}

	/**
	 * draw mouse cursor for location v
	 *
	 * @param renderer1
	 *            renderer
	 * @param v
	 *            location
	 */
	protected void drawMouseCursor(Renderer renderer1, Coords v) {

		CoordMatrix4x4.Identity(tmpMatrix4x4_3);

		tmpMatrix4x4_3.setOrigin(v);
		renderer1.setMatrix(tmpMatrix4x4_3);
		renderer1.drawMouseCursor();

	}

	protected void drawFreeCursor(Renderer renderer1) {
		// free point on xOy plane
		renderer1.drawCursor(PlotterCursor.TYPE_CROSS2D);
	}

	protected void drawTranslateViewCursor(Renderer renderer1) {

		if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
			switch (cursor) {
			case MOVE:
				renderer1.setMatrix(cursorOnXOYPlane.getDrawingMatrix());
				drawPointAlready(cursorOnXOYPlane.getRealMoveMode());
				renderer1.drawCursor(PlotterCursor.TYPE_CUBE);
				break;
			case RESIZE_X:
			case RESIZE_Y:
			case RESIZE_Z:
				renderer1.setMatrix(cursorMatrix);
				renderer.drawCursor(PlotterCursor.TYPE_ALREADY_Z);
				renderer1.drawCursor(PlotterCursor.TYPE_CUBE);
				break;
			}
		} else {
			renderer1.setMatrix(cursorOnXOYPlane.getDrawingMatrix());
			drawPointAlready(cursorOnXOYPlane.getRealMoveMode());
			renderer1.drawCursor(PlotterCursor.TYPE_CUBE);
		}

	}

	/**
	 * draws the cursor
	 *
	 * @param renderer1 renderer
	 */
	public void drawCursor(Renderer renderer1) {

		// Log.debug("\nhasMouse="
		// + hasMouse
		// + "\n!getEuclidianController().mouseIsOverLabel() "
		// + !getEuclidianController().mouseIsOverLabel()
		// +
		// "\ngetEuclidianController().cursor3DVisibleForCurrentMode(getCursor3DType())"
		// + ((EuclidianController3D) getEuclidianController())
		// .cursor3DVisibleForCurrentMode(getCursor3DType())
		// + "\ncursor=" + cursor + "\ngetCursor3DType()="
		// + getCursor3DType());

		if (hasMouse()) {

			// mouse cursor
			if (moveCursorIsVisible()) {
				drawTranslateViewCursor(renderer1);
			} else if (!getEuclidianController().mouseIsOverLabel()
					&& ((EuclidianController3D) getEuclidianController())
					.cursor3DVisibleForCurrentMode(getCursor3DType())) {
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
							case PREVIEW_POINT_NONE:
								// Log.debug("ici");
								break;
						}
						break;
				case HIT:
						switch (getCursor3DType()) {
							case PREVIEW_POINT_FREE:
								if (drawCrossForFreePoint()) {
									renderer1.drawCursor(PlotterCursor.TYPE_CROSS2D);
								}
								break;
							case PREVIEW_POINT_REGION:
								if (getEuclidianController().getMode() == EuclidianConstants.MODE_VIEW_IN_FRONT_OF) {
									renderer1.drawViewInFrontOf();
								} else {
									renderer1.drawCursor(PlotterCursor.TYPE_CROSS2D);
								}
								break;
							case PREVIEW_POINT_PATH:
							case PREVIEW_POINT_REGION_AS_PATH:
								if (getEuclidianController().getMode() == EuclidianConstants.MODE_VIEW_IN_FRONT_OF)
									renderer1.drawViewInFrontOf();
								else
									renderer1.drawCursor(PlotterCursor.TYPE_CYLINDER);
								break;
							case PREVIEW_POINT_DEPENDENT:
								renderer1.drawCursor(PlotterCursor.TYPE_DIAMOND);
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

	/**
	 *
	 * @return true if it has to draw 2D/1D arrows to move free point
	 */
	protected boolean drawCrossForFreePoint() {
		return true;
	}

	/**
	 * @param point moved point
	 * @return true if it has to draw 2D/1D arrows to move this point
	 */
	protected boolean drawCrossForPoint(GeoPoint3D point) {
		return true;
	}

	protected void drawPointAlready(GeoPoint3D point) {
		drawPointAlready(point.getMoveMode());
	}

	protected void drawPointAlready(int mode) {

		// Application.debug(mode);

		int pointMoveMode = mode;
		if (pointMoveMode == GeoPointND.MOVE_MODE_TOOL_DEFAULT) {
			pointMoveMode = ((EuclidianController3D) euclidianController)
					.getPointMoveMode();
		}

		switch (pointMoveMode) {
		case GeoPointND.MOVE_MODE_XY:
			renderer.drawCursor(PlotterCursor.TYPE_ALREADY_XY);
			break;
			case GeoPointND.MOVE_MODE_Z:
				renderer.drawCursor(PlotterCursor.TYPE_ALREADY_Z);
				break;
		}
	}

	/**
	 * says all drawables owned by the view that the view has changed
	 */
	/*
	 * public void viewChangedOwnDrawables(){
	 * 
	 * //xOyPlaneDrawable.viewChanged(); xOyPlaneDrawable.setWaitForUpdate();
	 * 
	 * for(int i=0;i<3;i++) axisDrawable[i].viewChanged();
	 * 
	 * 
	 * }
	 */
	public void setMoveCursor() {

		// 3D cursor
		cursor = EuclidianCursor.MOVE;

		// Application.printStacktrace("");
		// Application.debug("ici");

	}

	public EuclidianCursor getCursor() {
		return cursor;
	}

	final private boolean cursorIsTranslateViewCursor() {
		return cursor == EuclidianCursor.MOVE
				|| cursor == EuclidianCursor.RESIZE_X
				|| cursor == EuclidianCursor.RESIZE_Y
				|| cursor == EuclidianCursor.RESIZE_Z;
	}

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

	public void setDragCursor() {

		// 2D cursor is invisible
		// setCursor(app.getTransparentCursor());

		// 3D cursor
		cursor = EuclidianCursor.DRAG;
		// Application.printStacktrace("setDragCursor");

	}

	/**
	 * @return true if shift key is down
	 */
	abstract protected boolean getShiftDown();

	public void setDefaultCursor() {
		// App.printStacktrace("setDefaultCursor:"+defaultCursorWillBeHitCursor);

		if (getShiftDown()) // do nothing
			return;

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

	public void setHitCursor() {

		if (getShiftDown()) // do nothing
			return;

		// App.printStacktrace("setHitCursor");
		cursor = EuclidianCursor.HIT;
	}

	/**
	 * returns settings in XML format, read by xml handlers
	 *
	 * @return the XML description of 3D view settings
	 * @see org.geogebra.common.io.MyXMLHandler
	 * @see org.geogebra.common.geogebra3D.io.MyXMLHandler3D
	 */
	@Override
	public void getXML(StringBuilder sb, boolean asPreference) {

		// Application.debug("getXML: "+a+","+b);

		// if (true) return "";

		sb.append("<euclidianView3D>\n");

		// coord system
		sb.append("\t<coordSystem");

		sb.append(" xZero=\"");
		sb.append(getXZero());
		sb.append("\"");
		sb.append(" yZero=\"");
		sb.append(getYZero());
		sb.append("\"");
		sb.append(" zZero=\"");
		sb.append(getZZero());
		sb.append("\"");

		sb.append(" scale=\"");
		sb.append(getXscale());
		sb.append("\"");

		if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)
				&& !getSettings().hasSameScales()) {
			sb.append(" yscale=\"");
			sb.append(getYscale());
			sb.append("\"");

			sb.append(" zscale=\"");
			sb.append(getZscale());
			sb.append("\"");
		}

		sb.append(" xAngle=\"");
		sb.append(b);
		sb.append("\"");
		sb.append(" zAngle=\"");
		sb.append(a);
		sb.append("\"");

		sb.append("/>\n");

		// ev settings
		sb.append("\t<evSettings axes=\"");
		sb.append(getShowAxis(0) || getShowAxis(1) || getShowAxis(2));

		sb.append("\" grid=\"");
		sb.append(getShowGrid());
		sb.append("\" gridIsBold=\""); //
		sb.append(gridIsBold); // Michael Borcherds 2008-04-11
		sb.append("\" pointCapturing=\"");

		// make sure POINT_CAPTURING_STICKY_POINTS isn't written to XML
		sb.append(getPointCapturingMode() > EuclidianStyleConstants.POINT_CAPTURING_XML_MAX ? EuclidianStyleConstants.POINT_CAPTURING_DEFAULT
				: getPointCapturingMode());

		sb.append("\" rightAngleStyle=\"");
		sb.append(getApplication().rightAngleStyle);
		// if (asPreference) {
		// sb.append("\" allowShowMouseCoords=\"");
		// sb.append(getAllowShowMouseCoords());
		//
		// sb.append("\" allowToolTips=\"");
		// sb.append(getAllowToolTips());
		//
		// sb.append("\" deleteToolSize=\"");
		// sb.append(getEuclidianController().getDeleteToolSize());
		// }

		// sb.append("\" checkboxSize=\"");
		// sb.append(app.getCheckboxSize()); // Michael Borcherds
		// 2008-05-12

		sb.append("\" gridType=\"");
		sb.append(getGridType()); // cartesian/isometric/polar

		// if (lockedAxesRatio != null) {
		// sb.append("\" lockedAxesRatio=\"");
		// sb.append(lockedAxesRatio);
		// }

		sb.append("\"/>\n");
		// end ev settings

		// axis settings
		for (int i = 0; i < 3; i++) {
			// sb.append("\t<axis id=\"");
			// sb.append(i);
			// sb.append("\" show=\"");
			// sb.append(axis[i].isEuclidianVisible());
			// sb.append("\" label=\"");
			// sb.append(axis[i].getAxisLabel());
			// sb.append("\" unitLabel=\"");
			// sb.append(axis[i].getUnitLabel());
			// sb.append("\" tickStyle=\"");
			// sb.append(axis[i].getTickStyle());
			// sb.append("\" showNumbers=\"");
			// sb.append(axis[i].getShowNumbers());
			//
			// // the tick distance should only be saved if
			// // it isn't calculated automatically
			// /*
			// * if (!automaticAxesNumberingDistances[i]) {
			// * sb.append("\" tickDistance=\"");
			// * sb.append(axesNumberingDistances[i]); }
			// */
			//
			// sb.append("\"/>\n");
			this.getSettings().addAxisXML(i, sb);

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
		sb.append("\t<bgColor r=\"");
		sb.append(bgColor.getRed());
		sb.append("\" g=\"");
		sb.append(bgColor.getGreen());
		sb.append("\" b=\"");
		sb.append(bgColor.getBlue());
		sb.append("\"/>\n");

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
		sb.append(getProjection());
		getXMLForStereo(sb);
		if (!Kernel.isEqual(projectionObliqueAngle,
				EuclidianSettings3D.PROJECTION_OBLIQUE_ANGLE_DEFAULT)) {
			sb.append("\" obliqueAngle=\"");
			sb.append(projectionObliqueAngle);
		}
		if (!Kernel.isEqual(projectionObliqueFactor,
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

	protected void getXMLForStereo(StringBuilder sb) {
		int eyeDistance = (int) projectionPerspectiveEyeDistance[0];
		if (eyeDistance != EuclidianSettings3D.PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT) {
			sb.append("\" distance=\"");
			sb.append(eyeDistance);
		}
		int sep = (int) getEyeSep();
		if (sep != EuclidianSettings3D.EYE_SEP_DEFAULT) {
			sb.append("\" separation=\"");
			sb.append(sep);
		}
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

		for (int i = 0; i < 3; i++)
			flag = (flag && axis[i].isEuclidianVisible());

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

	public void setShowPlane(boolean flag) {
		getxOyPlane().setEuclidianVisible(flag);
	}

	/**
	 * toggle the visibility of xOy grid
	 */
	public void toggleGrid() {

		getSettings().showGrid(!getShowGrid());

	}

	public GeoPlane3DConstant getxOyPlane() {

		return xOyPlane;

	}

	/**
	 * says if this geo is owned by the view (xOy plane, ...)
	 *
	 * @param geo
	 * @return if this geo is owned by the view (xOy plane, ...)
	 */
	public boolean owns(GeoElement geo) {

		boolean ret = (geo == xOyPlane);

		for (int i = 0; (!ret) && (i < 3); i++)
			ret = (geo == axis[i]);

		return ret;

	}

	/**
	 * draw transparent parts of view's drawables (xOy plane)
	 *
	 * @param renderer
	 */
	public void drawTransp(Renderer renderer1) {

		if (xOyPlane.isPlateVisible())
			xOyPlaneDrawable.drawTransp(renderer1);

	}

	/**
	 * draw hiding parts of view's drawables (xOy plane)
	 *
	 * @param renderer
	 */
	public void drawHiding(Renderer renderer1) {
		xOyPlaneDrawable.drawHiding(renderer1);
	}

	/**
	 * draw not hidden parts of view's drawables (axis)
	 *
	 * @param renderer1
	 */
	public void draw(Renderer renderer1) {
		for (int i = 0; i < 3; i++)
			axisDrawable[i].drawOutline(renderer1);

		if (showClippingCube())
			clippingCubeDrawable.drawOutline(renderer1);

	}

	// ///////////////////////////
	// OPTIONS
	// //////////////////////////

	/**
	 * draw hidden parts of view's drawables (axis)
	 *
	 * @param renderer1
	 */
	public void drawHidden(Renderer renderer1) {
		for (int i = 0; i < 3; i++)
			axisDrawable[i].drawHidden(renderer1);

		xOyPlaneDrawable.drawHidden(renderer1);

		if (showClippingCube())
			clippingCubeDrawable.drawHidden(renderer1);

		if (decorationVisible())
			pointDecorations.drawHidden(renderer1);

	}

	protected boolean decorationVisible() {
		return pointDecorations.shouldBeDrawn();
	}

	/**
	 * draw for picking view's drawables (plane and axis)
	 *
	 * @param renderer1
	 */
	public void drawForPicking(Renderer renderer1) {
		renderer1.pick(xOyPlaneDrawable, PickingType.SURFACE);
		for (int i = 0; i < 3; i++)
			renderer1.pick(axisDrawable[i], PickingType.POINT_OR_CURVE);
	}

	/**
	 * draw ticks on axis
	 *
	 * @param renderer1
	 */
	public void drawLabel(Renderer renderer1) {

		for (int i = 0; i < 3; i++)
			axisDrawable[i].drawLabel(renderer1);

	}

	/**
	 * tell all drawables owned by the view to be udpated
	 */
	private void setWaitForUpdateOwnDrawables() {

		xOyPlaneDrawable.setWaitForUpdate();

		for (int i = 0; i < 3; i++)
			axisDrawable[i].setWaitForUpdate();

		clippingCubeDrawable.setWaitForUpdate();

	}

	/**
	 * says all labels owned by the view that the view has changed
	 */
	public void resetOwnDrawables() {

		xOyPlaneDrawable.setWaitForReset();

		for (int i = 0; i < 3; i++) {
			axisDrawable[i].setWaitForReset();
		}

		pointDecorations.setWaitForReset();

		clippingCubeDrawable.setWaitForReset();
	}

	/**
	 * says all labels to be recomputed
	 */
	public void resetAllDrawables() {

		resetOwnDrawables();
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

	}

	/**
	 * @param i index
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
	 * @param minmax initial and returned min/max values
	 * @param o      line origin
	 * @param v      line direction
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

	public void updateBounds() {
		((Kernel3D) kernel).setEuclidianView3DBounds(evNo, getXmin(),
				getXmax(), getYmin(), getYmax(), getZmin(), getZmax(),
				getXscale(), getYscale(), getZscale());
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
		updateBounds();
	}

	private void viewChangedOwnDrawables() {

		// update, but not in case where view changed by rotation
		if (viewChangedByTranslate() || viewChangedByZoom()) {
			// update clipping cube
			double[][] minMax = clippingCubeDrawable.updateMinMax();
			clippingCubeDrawable.setWaitForUpdate();

			// update e.g. Corner[]
			kernel.notifyEuclidianViewCE(EVProperty.ZOOM);

			// xOy plane wait for update
			xOyPlaneDrawable.setWaitForUpdate();

			// update decorations and wait for update
			for (int i = 0; i < 3; i++) {
				axisDrawable[i].setDrawMinMaxImmediatly(minMax);
				axisDrawable[i].updateDecorations();
				setAxesIntervals(getScale(i), i);

				axisDrawable[i].setWaitForUpdate();

			}
		}

		if (viewChangedByRotate()) {
			// we need to update renderer clip planes, since they are in screen
			// coordinates
			clippingCubeDrawable.updateRendererClipPlanes();

			// we need to update axis numbers locations
			for (int i = 0; i < 3; i++) {
				axisDrawable[i].updateDecorations();
				axisDrawable[i].setLabelWaitForUpdate();
			}

			// update e.g. Corner[]
			kernel.notifyEuclidianViewCE(EVProperty.ROTATION);

		}

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

	public void updateDrawables(Drawable3DListsForView drawable3DLists) {
		drawable3DLists.updateAll();
	}

	public void updateOtherDrawables() {
		updateDrawables(drawable3DLists);
	}

	/**
	 * @param i index
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
	 * @param i index
	 * @return if i-th axis shows numbers
	 */
	public boolean getShowAxisNumbers(int i) {
		return showAxesNumbers[i];
	}

	@Override
	public Previewable createPreviewParallelLine(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Previewable createPreviewPerpendicularLine(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines) {
		// TODO Auto-generated method stub
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


	protected void setViewChangedByZoom() {
		viewChangedByZoom = true;
	}

	protected void setViewChangedByTranslate() {
		viewChangedByTranslate = true;
	}

	protected void setViewChangedByRotate() {
		viewChangedByRotate = true;
	}

	public void setViewChanged() {
		setViewChangedByZoom();
		setViewChangedByTranslate();
		setViewChangedByRotate();
	}

	public boolean viewChangedByZoom() {
		return viewChangedByZoom;
	}

	public boolean viewChangedByTranslate() {
		return viewChangedByTranslate;
	}

	public boolean viewChangedByRotate() {
		return viewChangedByRotate;
	}

	public boolean viewChanged() {
		return viewChangedByZoom || viewChangedByTranslate
				|| viewChangedByRotate;
	}

	public void resetViewChanged() {
		viewChangedByZoom = false;
		viewChangedByTranslate = false;
		viewChangedByRotate = false;
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
	public Previewable createPreviewAngle(ArrayList<GeoPointND> selectedPoints) {
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
	public boolean isMoveable(GeoElement geo) {
		return geo.isMoveable();
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

	public void setProjection(int projection) {
		switch (projection) {
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

	public void setProjectionOrthographic() {
		renderer.setWaitForDisableStencilLines();
		renderer.updateOrthoValues();
		setProjectionValues(PROJECTION_ORTHOGRAPHIC);
		setDefault2DCursor();
	}

	public void setProjectionPerspective() {
		renderer.setWaitForDisableStencilLines();
		updateProjectionPerspectiveEyeDistance();
		setProjectionValues(PROJECTION_PERSPECTIVE);
		setDefault2DCursor();
		// setTransparentCursor();
	}

	/**
	 * set the near distance regarding eye distance to the screen for
	 * perspective (in pixels)
	 *
	 * @param distance
	 */
	public void setProjectionPerspectiveEyeDistance(double distanceLeft, double distanceRight) {
		projectionPerspectiveEyeDistance[0] = distanceLeft;
		projectionPerspectiveEyeDistance[1] = distanceRight;
		if (projection != PROJECTION_PERSPECTIVE
				&& projection != PROJECTION_GLASSES
				&& projection != PROJECTION_EQUIRECTANGULAR)
			projection = PROJECTION_PERSPECTIVE;
		updateProjectionPerspectiveEyeDistance();
		if (projection == PROJECTION_GLASSES
				|| projection == PROJECTION_EQUIRECTANGULAR) { // also update
			// eyes
			// separation
			renderer.updateGlassesValues();
		}
	}

	final private void updateProjectionPerspectiveEyeDistance() {

		renderer.setNear(projectionPerspectiveEyeDistance[0], projectionPerspectiveEyeDistance[1]);
	}

	/**
	 *
	 * @return eye distance to the screen for perspective
	 */
	public double getProjectionPerspectiveEyeDistance() {
		return projectionPerspectiveEyeDistance[0];
	}

	public void setProjectionGlasses() {
		updateProjectionPerspectiveEyeDistance();
		renderer.updateGlassesValues();
		if (isPolarized()) {
			renderer.setWaitForSetStencilLines();
		} else {
			renderer.setWaitForDisableStencilLines();
		}
		setProjectionValues(PROJECTION_GLASSES);
		setCursor(EuclidianCursor.TRANSPARENT);
		;
	}

	public void setProjectionEquirectangular() {

		// updateProjectionPerspectiveEyeDistance();
		double d = renderer.getVisibleDepth() / 2 + 100;
		renderer.setNear(d, d);

		eyeX[1] = 10;
		eyeX[0] = -eyeX[1];

		renderer.updateGlassesValues();


		if (isPolarized()) {
			renderer.setWaitForSetStencilLines();
		} else {
			renderer.setWaitForDisableStencilLines();
		}
		setProjectionValues(PROJECTION_EQUIRECTANGULAR);
		setCursor(EuclidianCursor.TRANSPARENT);

		// set view origin
		setXZero(0);
		setYZero(0);
		setZZero(0);

		// no horizontal angle
		b = 0;

		// update
		updateMatrix();
		setViewChangedByTranslate();
		setWaitForUpdate();

	}

	public void setEquirectangularAngle(double angle) {

		// change angle
		a = angle;

		// update
		updateMatrix();
		setViewChangedByRotate();
		setWaitForUpdate();
	}

	public boolean isGlassesGrayScaled() {
		return isGlassesGrayScaled;
	}

	public void setGlassesGrayScaled(boolean flag) {

		if (isGlassesGrayScaled == flag)
			return;

		isGlassesGrayScaled = flag;
		resetAllDrawables();
	}

	public boolean isPolarized() {
		return false;
	}

	public boolean isStereoBuffered() {
		return false;
	}

	public boolean wantsStereo() {
		return false;
	}

	public double getScreenZOffset() {
		return 0;
	}

	public boolean isGrayScaled() {
		return projection == PROJECTION_GLASSES && !isPolarized() && !isStereoBuffered()
				&& isGlassesGrayScaled();
	}

	public boolean isGlassesShutDownGreen() {
		return isGlassesShutDownGreen;
	}

	public void setGlassesShutDownGreen(boolean flag) {

		if (isGlassesShutDownGreen == flag)
			return;

		isGlassesShutDownGreen = flag;
		renderer.setWaitForUpdateClearColor();
	}

	public boolean isShutDownGreen() {
		return projection == PROJECTION_GLASSES && isGlassesShutDownGreen();
	}

	public void setEyes(double leftX, double leftY, double rightX, double rightY) {
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

	public boolean isUnitAxesRatio() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getViewID() {
		return App.VIEW_EUCLIDIAN3D - evNo - 1;
	}

	// ////////////////////////////////////////////////////
	//
	// ////////////////////////////////////////////////////

	public void setProjectionOblique() {
		renderer.updateProjectionObliqueValues();
		renderer.setWaitForDisableStencilLines();
		setProjectionValues(PROJECTION_OBLIQUE);
		setDefault2DCursor();
	}

	public double getProjectionObliqueAngle() {
		return projectionObliqueAngle;
	}

	public void setProjectionObliqueAngle(double angle) {
		projectionObliqueAngle = angle;
		renderer.updateProjectionObliqueValues();
	}

	public double getProjectionObliqueFactor() {
		return projectionObliqueFactor;
	}

	public void setProjectionObliqueFactor(double factor) {
		projectionObliqueFactor = factor;
		renderer.updateProjectionObliqueValues();
	}

	@Override
	public boolean getShowAxis(int axisNo) {
		return this.axis[axisNo].isEuclidianVisible();
	}

	/*
	 * @Override public geogebra.common.awt.GColor getBackgroundCommon() {
	 * return new geogebra.awt.GColorD(getBackground());
	 * 
	 * }
	 */

	// ////////////////////////////////////
	// PICKING

	@Override
	public void replaceBoundObject(GeoNumeric num, GeoNumeric geoNumeric) {

	}

	public GColor getBackground() {
		return bgColor;
	}

	// ////////////////////////////////////
	// SOME LINKS WITH 2D VIEW

	@Override
	public void setBackground(GColor color) {
		if (color != null) {
			setBackground(color, color);
		}

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

	public void addOneGeoToPick() {
		renderer.addOneGeoToPick();
	}

	public void removeOneGeoToPick() {
		renderer.removeOneGeoToPick();
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

	public Drawable newDrawButton(GeoButton geo) {
		return null;
	}

	public Drawable newDrawTextField(GeoInputBox geo) {
		return null;
	}

	@Override
	protected void initCursor() {

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
	protected void doDrawPoints(GeoImage gi, List<GPoint> penPoints2,
			GColor penColor, int penLineStyle,
			int penSize) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void drawResetIcon(GGraphics2D g) {
		Log.debug("unimplemented");

	}

	public double[] getIntervalClippedLarge(double[] minmax, Coords o, Coords v) {
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

		if (Kernel.isGreater(getZmin(), z1, tolerance)
				&& Kernel.isGreater(getZmin(), z2, tolerance))
			return true;

		if (Kernel.isGreater(z1, getZmax(), tolerance)
				&& Kernel.isGreater(z2, getZmax(), tolerance))
			return true;

		// close to screen
		return false;
	}

	@Override
	protected boolean drawPlayButtonInThisView() {
		// TODO Auto-generated method stub
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
	 * @param axis
	 *            axis
	 * @return distance between two number ticks on the axis
	 */
	public double getAxisNumberingDistance(int i) {
		return axesNumberingDistances[i];
	}

	@Override
	public double getGridDistances(int i) {

		if (i == AXIS_Z) { // no grid along z axis
			return getAxisNumberingDistance(AXIS_Z);
		}

		return super.getGridDistances(i);
	}

	public NumberFormatAdapter getAxisNumberFormat(int i) {
		return axesNumberFormat[i];
	}

	public EuclidianController getEuclidianController() {
		return euclidianController;
	}

	/**
	 * @return mouse pick width for openGL picking
	 */
	public int getMousePickWidth() {
		return 3;
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


		updateMatrix();
		setViewChanged();
		setWaitForUpdate();
		if (evs.getRotSpeed() > 0) {
			this.setRotContinueAnimation(0, evs.getRotSpeed());
		}

		if (styleBar != null) {
			styleBar.updateGUI();
		}
	}

	@Override
	public final void setViewShowAllObjects(boolean storeUndo, boolean keepRatio) {
		setViewShowAllObjects(storeUndo, keepRatio, 15);
	}

	public final void setViewShowAllObjects(boolean storeUndo, boolean keepRatio, int steps) {

		if (boundsMin == null) {
			boundsMin = new Coords(3);
			boundsMax = new Coords(3);
		}

		boundsMin.setPositiveInfinity();
		boundsMax.setNegativeInfinity();

		drawable3DLists.enlargeBounds(boundsMin, boundsMax);

		// Log.debug("\nmin=\n"+boundsMin+"\nmax=\n"+boundsMax);

		// no object
		if (Double.isInfinite(boundsMin.getX())) {
			return;
		}

		zoomRW(boundsMin, boundsMax, steps);

	}

	public void zoomRW(Coords boundsMin2, Coords boundsMax2) {
		zoomRW(boundsMin2, boundsMax2, 15);
	}

	public void zoomRW(Coords boundsMin2, Coords boundsMax2, int steps) {
		double dx0 = getXmax() - getXmin();
		double dy0 = getYmax() - getYmin();
		double dz0 = getZmax() - getZmin();

		double dx = boundsMax2.getX() - boundsMin2.getX();
		double dy = boundsMax2.getY() - boundsMin2.getY();
		double dz = boundsMax2.getZ() - boundsMin2.getZ();

		double scale = Double.POSITIVE_INFINITY;
		if (!Kernel.isZero(dx)) {
			scale = dx0 / dx;
		}
		if (!Kernel.isZero(dy)) {
			double v = dy0 / dy;
			if (scale > v) {
				scale = v;
			}
		}
		if (!Kernel.isZero(dz)) {
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

		if (Kernel.isZero(scale)) {
			return;
		}

		scale *= getScale();

		// let the view a bit greater than the scene
		scale *= 0.94;


		double x = -(boundsMin2.getX() + boundsMax2.getX()) / 2;
		double y = -(boundsMin2.getY() + boundsMax2.getY()) / 2;
		double z = -(boundsMin2.getZ() + boundsMax2.getZ()) / 2;

		setAnimatedCoordSystem(x, y, z, scale, steps);

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

		Coords p = point.getInhomCoordsInD3();

		setXZero(-p.getX());
		setYZero(-p.getY());
		setZZero(-p.getZ());
		getSettings().updateOriginFromView(getXZero(), getYZero(), getZZero());

		// update the view
		updateTranslationMatrix();
		updateUndoTranslationMatrix();
		setGlobalMatrices();

		setViewChangedByTranslate();
		setWaitForUpdate();
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
		if (!Kernel.isEqual(scale, fontScale)) {
			fontScale = scale;
			updateDrawableFontSize();
		}

	}

	/**
	 * set zNear nearest value
	 *
	 * @param zNear
	 */
	public void setZNearest(double zNear) {
		// used for some input3D
	}

	/**
	 *
	 * @return true if currently uses hand grabbing (3D input)
	 */
	public boolean useHandGrabbing() {
		return false;
	}

	/**
	 *
	 * @return true if consumes space key hitted
	 */
	public boolean handleSpaceKey() {
		return false;
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
	protected void setBackground(GColor updatedColor, GColor applyedColor) {
		this.bgColor = updatedColor;
		this.bgApplyedColor = applyedColor;
		if (renderer != null) {
			renderer.setWaitForUpdateClearColor();
		}
	}

	@Override
	final public void paintBackground(GGraphics2D g2) {
		// not used in 3D
	}

	private double screenTranslateAndScaleDX;
	private double screenTranslateAndScaleDY;
	private double screenTranslateAndScaleDZ;

	public void screenTranslateAndScale(double dx, double dy, double scaleFactor) {

		// dx and dy are translation in screen coords
		// dx moves along "visible left-right" axis on xOy plane
		// dy moves along "visible front-back" axis on xOy plane
		//    or z-axis if this one "visibly" more than sqrt(2)*front-back axis
		tmpCoords1.set(Coords.VX);
		toSceneCoords3D(tmpCoords1);
		screenTranslateAndScaleDX = tmpCoords1.getX() * dx;
		screenTranslateAndScaleDY = tmpCoords1.getY() * dx;

		tmpCoords1.set(Coords.VY);
		toSceneCoords3D(tmpCoords1);
		double z = tmpCoords1.getZ() * getScale();
		if (z > 0.85) {
			screenTranslateAndScaleDZ = tmpCoords1.getZ() * (-dy);
		} else if (z < 0.45) {
			screenTranslateAndScaleDX += tmpCoords1.getX() * (-dy);
			screenTranslateAndScaleDY += tmpCoords1.getY() * (-dy);
			screenTranslateAndScaleDZ = 0;
		} else {
			screenTranslateAndScaleDZ = 0;
		}

		xScaleEnd = xScaleStart * scaleFactor;
		yScaleEnd = yScaleStart * scaleFactor;
		zScaleEnd = zScaleStart * scaleFactor;
		animationType = AnimationType.SCREEN_TRANSLATE_AND_SCALE;
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
		if (animationType == AnimationType.SCREEN_TRANSLATE_AND_SCALE){
			animationType = AnimationType.OFF;
		}
		
	}

	public void scaleXYZ(Coords coords) {
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
	 * TODO remove this (not needed after Feature.DIFFERENT_AXIS_RATIO_3D)
	 * 
	 * @param value
	 * @return value/scale
	 */
	public double unscale(double value) {
		if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
			return value;
		}
		return value / getScale();
	}

	/**
	 * TODO remove this (not needed after Feature.DIFFERENT_AXIS_RATIO_3D)
	 * 
	 * @param value
	 * @return value/scale
	 */
	public float unscale(float value) {
		if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
			return value;
		}
		return (float) (value / getScale());
	}

	/**
	 * 
	 * @return true if we want to show a preview for MODE_PYRAMID and MODE_PRISM
	 */
	public boolean showPyramidAndPrismPreviews() {
		return true;
	}

	@Override
	public GBufferedImage getExportImage(double scale, boolean transparency) {
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

}
