package org.geogebra.common.geogebra3D.euclidian3D;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
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
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoTextField;
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
import org.geogebra.common.plugin.GeoClass;
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
		EuclidianView3DInterface {

	// private Kernel kernel;
	private Kernel3D kernel3D;
	protected Renderer renderer;

	// distances between grid lines
	protected boolean automaticGridDistance = true;
	// since V3.0 this factor is 1, before it was 0.5
	final public static double DEFAULT_GRID_DIST_FACTOR = 1;
	public static double automaticGridDistanceFactor = DEFAULT_GRID_DIST_FACTOR;

	/** default scene x-coord of origin */
	public static final double XZERO_SCENE_STANDARD = 0;
	/** default scene y-coord of origin */
	public static final double YZERO_SCENE_STANDARD = 0;
	/** default scene z-coord of origin */
	public static final double ZZERO_SCENE_STANDARD = -1.5;

	// viewing values
	protected double XZero = XZERO_SCENE_STANDARD;
	protected double YZero = YZERO_SCENE_STANDARD;
	protected double ZZero = ZZERO_SCENE_STANDARD;

	protected double XZeroOld = 0;
	protected double YZeroOld = 0;
	protected double ZZeroOld = 0;

	// list of 3D objects
	private boolean waitForUpdate = true; // says if it waits for update...
	// public boolean waitForPick = false; //says if it waits for update...
	private Drawable3DListsForView drawable3DLists;// = new DrawList3D();
	/** list for drawables that will be added on next frame */
	private LinkedList<Drawable3D> drawable3DListToBeAdded;// = new
															// DrawList3D();
	/** list for drawables that will be removed on next frame */
	private LinkedList<Drawable3D> drawable3DListToBeRemoved;// = new
																// DrawList3D();
	/** list for Geos to that will be added on next frame */
	private TreeSet<GeoElement> geosToBeAdded;

	// Map (geo, drawable) for GeoElements and Drawables
	private TreeMap<GeoElement, Drawable3D> drawable3DMap = new TreeMap<GeoElement, Drawable3D>();

	// matrix for changing coordinate system
	private CoordMatrix4x4 m = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 mInv = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 mInvTranspose = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 undoRotationMatrix = CoordMatrix4x4.Identity();

	public final static double ANGLE_ROT_OZ = -60;
	public final static double ANGLE_ROT_XOY = 20;

	private double a = ANGLE_ROT_OZ;
	private double b = ANGLE_ROT_XOY;// angles (in degrees)
	protected double aOld, bOld;
	private double aNew, bNew;

	// picking and hits
	protected Hits3D hits = new Hits3D(); // objects picked from openGL

	/** direction of view */
	private Coords viewDirection = Coords.VZ.copyVector();
	private Coords eyePosition = new Coords(4);

	// axis and xOy plane
	private GeoPlane3D xOyPlane;
	private GeoAxisND[] axis;
	private GeoClippingCube3D clippingCube;

	private DrawPlane3D xOyPlaneDrawable;
	private DrawAxis3D[] axisDrawable;
	protected DrawClippingCube3D clippingCubeDrawable;

	/** number of drawables linked to this view (xOy plane, Ox, Oy, Oz axis) */
	static final public int DRAWABLES_NB = 4;
	/** id of z-axis */
	static final int AXIS_Z = 2; // AXIS_X and AXIS_Y already defined in
									// EuclidianViewInterface

	// point decorations
	private DrawPointDecorations pointDecorations;
	private boolean decorationVisible = false;

	// preview
	private Previewable previewDrawable;
	private GeoPoint3D cursor3D, cursorOnXOYPlane;
	// private boolean cursorOnXOYPlaneVisible;
	// private GeoElement[] cursor3DIntersectionOf = new GeoElement[2];

	// cursor
	/** no point under the cursor */
	public static final int PREVIEW_POINT_NONE = 0;
	/** free point under the cursor */
	public static final int PREVIEW_POINT_FREE = 1;
	/** path point under the cursor */
	public static final int PREVIEW_POINT_PATH = 2;
	/** region point under the cursor */
	public static final int PREVIEW_POINT_REGION = 3;
	/** dependent point under the cursor */
	public static final int PREVIEW_POINT_DEPENDENT = 4;
	/** already existing point under the cursor */
	public static final int PREVIEW_POINT_ALREADY = 5;
	/** region as path (e.g. quadric as line) point under the cursor */
	public static final int PREVIEW_POINT_REGION_AS_PATH = 6;

	private int cursor3DType = PREVIEW_POINT_NONE;

	private static final int CURSOR_DEFAULT = 0;
	private static final int CURSOR_DRAG = 1;
	private static final int CURSOR_MOVE = 2;
	private static final int CURSOR_HIT = 3;
	private int cursor = CURSOR_DEFAULT;

	// animation
	/** tells if the view is under animation for scale */
	private boolean animatedScale = false;
	/** starting and ending scales */
	private double animatedScaleStart, animatedScaleEnd;
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

	/** tells if the view is under continue animation for rotation */
	private boolean animatedContinueRot = false;
	/** speed for animated rotation */
	private double animatedRotSpeed;
	/** starting time for animated rotation */
	private double animatedRotTimeStart;

	/** tells if the view is under animation for rotation */
	private boolean animatedRot = false;

	/** says if the view is frozen (see freeze()) */
	private boolean isFrozen = false;

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

		this.kernel3D = (Kernel3D) ec.getKernel();
		euclidianController.setView(this);

		start();

	}

	@Override
	protected void initAxesValues() {
		axesNumberFormat = new NumberFormatAdapter[3];
		showAxesNumbers = new boolean[] { true, true, true };
		axesLabels = new String[] { null, null, null };
		axesLabelsStyle = new int[] { GFont.PLAIN, GFont.PLAIN, GFont.PLAIN };
		axesUnitLabels = new String[] { null, null, null };
		axesTickStyles = new int[] {
				EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
				EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
				EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR };
		automaticAxesNumberingDistances = new boolean[] { true, true, true };
		axesNumberingDistances = new double[] { 2, 2, 2 };
		drawBorderAxes = new boolean[] { false, false, false };
		axisCross = new double[] { 0, 0, 0 };
		positiveAxes = new boolean[] { false, false, false };
		piAxisUnit = new boolean[] { false, false, false };
		gridDistances = new double[] { 2, 2, Math.PI / 6 };
		AxesTickInterval = new double[] { 1, 1, 1 };
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

		App.debug("create gl renderer");
		renderer = createRenderer();

		renderer.setDrawable3DLists(drawable3DLists);

		createPanel();

		attachView();

		initAxisAndPlane();
		boolean was3d = kernel3D.getConstruction().usedGeos
				.contains(GeoClass.POINT3D);
		// previewables
		// kernel3D.setSilentMode(true);
		cursor3D = new GeoPoint3D(kernel3D.getConstruction());
		cursor3D.setCoords(0, 0, 0, 1);
		cursor3D.setIsPickable(false);
		// cursor3D.setLabelOffset(5, -5);
		// cursor3D.setEuclidianVisible(false);
		cursor3D.setMoveNormalDirection(Coords.VZ);
		// kernel3D.setSilentMode(false);

		cursorOnXOYPlane = new GeoPoint3D(kernel3D.getConstruction());
		cursorOnXOYPlane.setCoords(0, 0, 0, 1);
		cursorOnXOYPlane.setIsPickable(false);
		cursorOnXOYPlane.setMoveNormalDirection(Coords.VZ);
		cursorOnXOYPlane.setRegion(xOyPlane);
		cursorOnXOYPlane.setMoveMode(GeoPointND.MOVE_MODE_XY);
		if (!was3d) {
			kernel3D.getConstruction().usedGeos.remove(GeoClass.POINT3D);
		}
		// point decorations
		initPointDecorations();

		// tells the renderer if use clipping cube
		updateUseClippingCube();

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
			repaintView();
		}
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
			case CURVE_CARTESIAN:
			case CURVE_CARTESIAN3D:
				d = new DrawCurve3D(this, (CurveEvaluable) geo);
				break;

			case LOCUS:
				d = new DrawLocus3D(this, (GeoLocusND) geo);
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
		if (app.useShaders() && app.has(Feature.GL_ELEMENTS)) {
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
		changeCoords(mInv, vInOut);
	}

	/**
	 * converts the vector to screen coords
	 * 
	 * @param vInOut
	 *            vector
	 */
	final public void toScreenCoords3D(Coords vInOut) {
		changeCoords(m, vInOut);
	}

	final private static void changeCoords(CoordMatrix mat, Coords vInOut) {
		Coords v1 = vInOut.getCoordsLast1();
		vInOut.set(mat.mul(v1));
	}

	/**
	 * return the matrix : screen coords -> scene coords.
	 * 
	 * @return the matrix : screen coords -> scene coords.
	 */
	final public CoordMatrix4x4 getToSceneMatrix() {

		return mInv;
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

		return m;
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

	
	public boolean getUseLight(){
		return getSettings().getUseLight();
	}
	
	private void updateRotationMatrix() {

		CoordMatrix m1, m2;

		if (getYAxisVertical()) { // y axis taken for up-down direction
			m1 = CoordMatrix.Rotation3DMatrix(CoordMatrix.X_AXIS, (this.b)
					* EuclidianController3D.ANGLE_TO_DEGREES);
			m2 = CoordMatrix.Rotation3DMatrix(CoordMatrix.Y_AXIS,
					(-this.a - 90) * EuclidianController3D.ANGLE_TO_DEGREES);
		} else { // z axis taken for up-down direction
			m1 = CoordMatrix.Rotation3DMatrix(CoordMatrix.X_AXIS, (this.b - 90)
					* EuclidianController3D.ANGLE_TO_DEGREES);
			m2 = CoordMatrix.Rotation3DMatrix(CoordMatrix.Z_AXIS,
					(-this.a - 90) * EuclidianController3D.ANGLE_TO_DEGREES);
		}

		rotationMatrix = m1.mul(m2);
	}

	private void updateScaleMatrix() {
		scaleMatrix.set(1, 1, getXscale());
		scaleMatrix.set(2, 2, getYscale());
		scaleMatrix.set(3, 3, getZscale());
	}

	protected void updateTranslationMatrix() {
		translationMatrix.set(1, 4, getXZero());
		translationMatrix.set(2, 4, getYZero());
		translationMatrix.set(3, 4, getZZero());
	}

	private CoordMatrix4x4 scaleMatrix = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 undoScaleMatrix = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 translationMatrix = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 undoTranslationMatrix = CoordMatrix4x4.Identity();
	private CoordMatrix rotationMatrix;
	protected CoordMatrix rotationAndScaleMatrix;

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
	 * 
	 * @return current rotation matrix
	 */
	public CoordMatrix getRotationMatrix() {
		return rotationMatrix;
	}

	protected void setGlobalMatrices() {

		m.set(rotationAndScaleMatrix.mul(translationMatrix));

		/*
		 * //TO TEST PROJECTION m.set(CoordMatrix.Identity(4)); scale = 1;
		 */

		// mInv.set(m.inverse());
		mInv.set(undoTranslationMatrix.mul(undoScaleMatrix
				.mul(undoRotationMatrix)));

		mInvTranspose.set(mInv.transposeCopy());

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
	 * 
	 * @return eye position
	 */
	public Coords getEyePosition() {
		return eyePosition;
	}

	public void shiftRotAboutZ(double da) {
		setRotXYinDegrees(aOld + da, bOld);

		updateRotationAndScaleMatrices();

		setGlobalMatrices();

		setViewChangedByRotate();
		setWaitForUpdate();
	}

	public void setRotXYinDegrees(double a, double b) {

		// App.debug("setRotXY: "+a+","+b);
		if (Double.isNaN(a) || Double.isNaN(b)) {
			App.printStacktrace("NaN values for setRotXYinDegrees");
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

	/** Sets coord system from mouse move */
	@Override
	final public void translateCoordSystemInPixels(int dx, int dy, int dz,
			int mode) {
		setXZero(XZeroOld + dx / getSettings().getXscale());
		setYZero(YZeroOld - dy / getSettings().getYscale());
		setZZero(ZZeroOld + dz / getSettings().getZscale());

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

	/** Sets coord system from mouse move */
	@Override
	final public void setCoordSystemFromMouseMove(int dx, int dy, int mode) {
		switch (mode) {
		case EuclidianController.MOVE_ROTATE_VIEW:
			setRotXYinDegrees(aOld - dx, bOld + dy);
			updateMatrix();
			setViewChangedByRotate();
			setWaitForUpdate();
			break;
		case EuclidianController.MOVE_VIEW:
			Coords v = new Coords(dx, -dy, 0, 0);
			toSceneCoords3D(v);

			if (cursorOnXOYPlane.getRealMoveMode() == GeoPointND.MOVE_MODE_XY) {
				v.projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY,
						getViewDirection(), tmpCoords1);
				setXZero(XZeroOld + tmpCoords1.getX());
				setYZero(YZeroOld + tmpCoords1.getY());
			} else {
				v.projectPlaneInPlaneCoords(CoordMatrix4x4.IDENTITY, tmpCoords1);
				setZZero(ZZeroOld + tmpCoords1.getZ());
			}
			getSettings().updateOriginFromView(getXZero(), getYZero(),
					getZZero());
			updateMatrix();
			setViewChangedByTranslate();
			setWaitForUpdate();
			break;
		}
	}

	/*
	 * TODO interaction - note : methods are called by
	 * EuclidianRenderer3D.viewOrtho() to re-center the scene
	 */
	@Override
	public double getXZero() {
		return XZero;
	}

	@Override
	public double getYZero() {
		return YZero;
	}

	/** @return the z-coord of the origin */
	public double getZZero() {
		return ZZero;
	}

	/**
	 * set the x-coord of the origin
	 * 
	 * @param val
	 */
	public void setXZero(double val) {
		XZero = val;
	}

	/**
	 * set the y-coord of the origin
	 * 
	 * @param val
	 */
	public void setYZero(double val) {
		YZero = val;
	}

	/**
	 * set the z-coord of the origin
	 * 
	 * @param val
	 */
	public void setZZero(double val) {
		ZZero = val;
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
			CoordMatrix matrix = ((mRS.inverse()).mul(translationMatrix)
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

	// TODO specific scaling for each direction
	// private double scale = 50;

	@Override
	public double getXscale() {
		return getSettings().getXscale();
	}

	@Override
	public double getYscale() {
		return getSettings().getYscale();
	}

	/** @return the z-scale */
	public double getZscale() {
		return getSettings().getZscale();
	}

	@Override
	public double getScale(int i) {
		return getSettings().getXscale();
	}

	@Override
	protected void setAxesIntervals(double scale, int axis) {
		super.setAxesIntervals(scale, axis);
		axisDrawable[axis].setLabelWaitForUpdate();
		setWaitForUpdate();
	}

	/**
	 * set the all-axis scale
	 * 
	 * @param val
	 */
	public void setScale(double val) {
		getSettings().setScaleNoCallToSettingsChanged(val);
		setViewChangedByZoom();
	}

	/**
	 * @return the all-axis scale
	 */
	public double getScale() {
		return getSettings().getXscale();
	}

	/** remembers the origins values (xzero, ...) */
	@Override
	public void rememberOrigins() {
		aOld = a;
		bOld = b;
		XZeroOld = XZero;
		YZeroOld = YZero;
		ZZeroOld = ZZero;
	}

	// ////////////////////////////////////
	// update

	public void updateAnimation() {
		if (isAnimated()) {
			animate();
			setWaitForUpdate();
		}
	}

	/** update the drawables for 3D view */
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

	// ////////////////////////////////////
	// picking

	protected Coords pickPoint = new Coords(0, 0, 0, 1);
	private Coords viewDirectionPersp = new Coords(4);

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
	 * 
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
	 * 
	 * @param p
	 *            3D point in scene coords
	 * @return (x,y) point aligned with p
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

	@Override
	public void repaintView() {

		// Application.debug("repaint View3D");
		// super.repaintView();
	}

	/**
	 * says we want a new repaint after current repaint
	 */
	public void waitForNewRepaint() {
		// nothing done here, see EuclidianView3DW
	}

	@Override
	public void reset() {

		// Application.debug("reset View3D");
		resetAllDrawables();
		// updateAllDrawables();
		viewChangedOwnDrawables();
		setViewChanged();
		setWaitForUpdate();

		// update();
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
	public void updateVisualStyle(GeoElement geo) {
		// Application.debug(geo);
		if (geo.hasDrawable3D()) {
			Drawable3D d = drawable3DMap.get(geo);
			if (d != null) {
				d.setWaitForUpdateVisualStyle();
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

	// ////////////////////////////////////////////
	// EuclidianViewInterface

	@Override
	public DrawableND getDrawableND(GeoElement geo) {
		if (geo.hasDrawable3D()) {

			return drawable3DMap.get(geo);
		}

		return null;
	}

	@Override
	public GeoElement getLabelHit(org.geogebra.common.awt.GPoint p,
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

	public void setShowPlane(boolean flag) {
		getxOyPlane().setEuclidianVisible(flag);
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

	// ////////////////////////////////////////////////
	// ANIMATION
	// ////////////////////////////////////////////////

	/** tells if the view is under animation */
	public boolean isAnimated() {
		return animatedScale || isRotAnimated();
	}

	/**
	 * tells if the view is under rot animation
	 * 
	 * @return true if there is a rotation animation
	 */
	public boolean isRotAnimated() {
		return animatedContinueRot || animatedRot;
	}

	/**
	 * @return true if there is a continue rotation animation
	 */
	public boolean isRotAnimatedContinue() {
		return animatedContinueRot;
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
	 * 
	 * @return true if use of clipping cube
	 */
	public boolean useClippingCube() {
		return getSettings().useClippingCube();
	}

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
	 * 
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
	 * sets the reduction of the clipping box
	 * 
	 * @param value
	 *            reduction
	 */
	public void setClippingReduction(int value) {
		clippingCube.setReduction(value);
		setViewChanged();
		setWaitForUpdate();
	}

	/**
	 * 
	 * @return the reduction of the clipping box
	 */
	public int getClippingReduction() {
		return clippingCube.getReduction();
	}

	@Override
	public void setAnimatedCoordSystem(double x0, double y0, int steps,
			boolean storeUndo) {

		setAnimatedCoordSystem(XZERO_SCENE_STANDARD, YZERO_SCENE_STANDARD,
				ZZERO_SCENE_STANDARD, SCALE_STANDARD, steps);
	}

	private void setAnimatedCoordSystem(double x, double y, double z,
			double newScale, int steps) {

		animatedScaleStartX = getXZero();
		animatedScaleStartY = getYZero();
		animatedScaleStartZ = getZZero();

		animatedScaleEndX = x;
		animatedScaleEndY = y;
		animatedScaleEndZ = z;

		animatedScaleStart = getScale();
		animatedScaleTimeStart = app.getMillisecondTime();
		animatedScaleEnd = newScale;
		animatedScale = true;

		animatedScaleTimeFactor = 0.0003 * steps;

	}

	@Override
	public void setAnimatedCoordSystem(double ox, double oy, double f,
			double newScale, int steps, boolean storeUndo) {

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
			// App.debug("\n"+v);
			if (!v.isDefined()) {
				v = new Coords(-animatedScaleStartX, -animatedScaleStartY,
						-animatedScaleStartZ, 1); // takes center of the scene
													// for fixed point
			}
		}

		// Application.debug(v);

		double factor = getScale() / newScale;

		animatedScaleEndX = -v.getX() + (animatedScaleStartX + v.getX())
				* factor;
		animatedScaleEndY = -v.getY() + (animatedScaleStartY + v.getY())
				* factor;
		animatedScaleEndZ = -v.getZ() + (animatedScaleStartZ + v.getZ())
				* factor;

		// Application.debug("mouse = ("+ox+","+oy+")"+"\nscale end = ("+animatedScaleEndX+","+animatedScaleEndY+")"+"\nZero = ("+animatedScaleStartX+","+animatedScaleStartY+")");

		animatedScaleStart = getScale();
		animatedScaleTimeStart = app.getMillisecondTime();
		animatedScaleEnd = newScale;
		animatedScale = true;

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
		// Application.debug("delay="+delay+", rotSpeed="+rotSpeed);

		if (Double.isNaN(rotSpeed)) {
			App.printStacktrace("NaN values for setRotContinueAnimation");
			stopRotAnimation();
			return;
		}

		double rotSpeed2 = rotSpeed;
		// if last drag occured more than 200ms ago, then no animation
		if (delay > 200)
			return;

		// if speed is too small, no animation
		if (Math.abs(rotSpeed2) < 0.01) {
			stopRotAnimation();
			return;
		}

		// if speed is too large, use max speed
		if (rotSpeed2 > 0.1)
			rotSpeed2 = 0.1;
		else if (rotSpeed2 < -0.1)
			rotSpeed2 = -0.1;
		this.getSettings().setRotSpeed(0);
		animatedContinueRot = true;
		animatedRot = false;
		animatedRotSpeed = -rotSpeed2;
		animatedRotTimeStart = app.getMillisecondTime() - delay;
		bOld = b;
		aOld = a;
	}

	private Coords tmpCoordsLength3 = new Coords(3);

	/**
	 * start a rotation animation to be in the vector direction
	 * 
	 * @param vn
	 */
	public void setRotAnimation(Coords vn) {
		CoordMatrixUtil.sphericalCoords(vn, tmpCoordsLength3);
		setRotAnimation(tmpCoordsLength3.get(2) * 180 / Math.PI,
				tmpCoordsLength3.get(3) * 180 / Math.PI, true);
	}

	public void setClosestRotAnimation(Coords v) {
		if (v.dotproduct(getViewDirection()) > 0)
			setRotAnimation(v.mul(-1));
		else
			setRotAnimation(v);
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
	 * @param checkSameValues
	 *            if true, check new values are same than old, in this case
	 *            revert the view
	 */
	public void setRotAnimation(double aN, double bN, boolean checkSameValues) {

		if (Double.isNaN(aN) || Double.isNaN(bN)) {
			App.printStacktrace("NaN values for setRotAnimation");
			return;
		}

		// app.storeUndoInfo();

		animatedRot = true;
		animatedContinueRot = false;
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
		if (aOld - aNew > 180)
			aOld -= 360;
		else if (aOld - aNew < -180)
			aOld += 360;

		else if (checkSameValues)
			if (Kernel.isEqual(aOld, aNew, Kernel.STANDARD_PRECISION))
				if (Kernel.isEqual(bOld, bNew, Kernel.STANDARD_PRECISION)) {
					if (!Kernel.isEqual(Math.abs(bNew), 90,
							Kernel.STANDARD_PRECISION))
						aNew += 180;
					bNew *= -1;
					// Application.debug("ici");
				}
		if (bOld > 180)
			bOld -= 360;

		animatedRotTimeStart = app.getMillisecondTime();

	}

	/**
	 * stops the rotation animation
	 */
	public void stopRotAnimation() {
		animatedContinueRot = false;
		animatedRot = false;

	}

	/** animate the view for changing scale, orientation, etc. */
	private void animate() {
		if (animatedScale) {
			double t = (app.getMillisecondTime() - animatedScaleTimeStart)
					* animatedScaleTimeFactor;
			t += 0.2; // starting at 1/4

			if (t >= 1) {
				t = 1;
				animatedScale = false;
			}

			// Application.debug("t="+t+"\nscale="+(startScale*(1-t)+endScale*t));

			setScale(animatedScaleStart * (1 - t) + animatedScaleEnd * t);
			setXZero(animatedScaleStartX * (1 - t) + animatedScaleEndX * t);
			setYZero(animatedScaleStartY * (1 - t) + animatedScaleEndY * t);
			setZZero(animatedScaleStartZ * (1 - t) + animatedScaleEndZ * t);
			getSettings().updateOriginFromView(getXZero(), getYZero(),
					getZZero());

			updateMatrix();
			setViewChangedByZoom();
			setViewChangedByTranslate();

			// euclidianController3D.setFlagMouseMoved();

		}

		if (animatedContinueRot) {
			double da = (app.getMillisecondTime() - animatedRotTimeStart)
					* animatedRotSpeed;

			shiftRotAboutZ(da);
		}

		if (animatedRot) {
			double t = (app.getMillisecondTime() - animatedRotTimeStart) * 0.001;
			t *= t;
			// t+=0.2; //starting at 1/4

			if (t >= 1) {
				t = 1;
				animatedRot = false;
			}

			setRotXYinDegrees(aOld * (1 - t) + aNew * t, bOld * (1 - t) + bNew
					* t);

			updateMatrix();
			setViewChangedByRotate();
		}

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
	public void setHits(org.geogebra.common.awt.GPoint p, PointerEventType type) {
		// empty method : setHits3D() used instead
		// OR comment setHits3D() for shaders

		if (renderer.useLogicalPicking()) {
			renderer.setHits(p, getCapturingThreshold(type));
			if (type == PointerEventType.TOUCH && this.hits.size() == 0) {
				renderer.setHits(p, getCapturingThreshold(type) * 3);
			}

			hasMouse = true;
			updateCursor3D();
		}

	}

	public int getCapturingThreshold(PointerEventType type) {
		return app.getCapturingThreshold(type);
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

	public DrawAxis3D getAxisDrawable(int i) {
		return axisDrawable[i];
	}

	public DrawPlane3D getPlaneDrawable() {
		return xOyPlaneDrawable;
	}

	/**
	 * init the hits for this view
	 * 
	 * @param hits
	 */
	public void setHits(Hits3D hits) {
		this.hits = hits;
	}

	public Hits3D getHits3D() {
		return hits;
	}

	@Override
	public Hits getHits() {
		return hits.clone();
	}

	@Override
	public void updateCursor(GeoPointND point) {
		hits.init();
		hits.add((GeoElement) point);
		updateCursor3D();
	}

	@Override
	public void setSelectionRectangle(
			org.geogebra.common.awt.GRectangle selectionRectangle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setShowAxesRatio(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setShowMouseCoords(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void zoom(double px, double py, double zoomFactor, int steps,
			boolean storeUndo) {

		setScale(getXscale() * zoomFactor);
		updateMatrix();
		setWaitForUpdate();

	}

	// ///////////////////////////////////////
	// previewables

	/**
	 * return the point used for 3D cursor
	 * 
	 * @return the point used for 3D cursor
	 */
	public GeoPoint3D getCursor3D() {
		return cursor3D;
	}

	/**
	 * sets the type of the cursor
	 * 
	 * @param v
	 */
	public void setCursor3DType(int v) {
		cursor3DType = v;
		// App.debug(""+v);
	}

	/**
	 * @return the type of the cursor
	 */
	public int getCursor3DType() {
		return cursor3DType;
	}

	private int intersectionThickness;

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

	private GeoPointND intersectionPoint;

	public void setIntersectionPoint(GeoPointND point) {
		intersectionPoint = point;
	}

	public GeoPointND getIntersectionPoint() {
		return intersectionPoint;
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
	@SuppressWarnings("rawtypes")
	public Previewable createPreviewSphere(ArrayList selectedPoints) {
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
			getPreviewDrawable().updatePreview();
		}
	}

	@Override
	public void updatePreviewableForProcessMode() {
		updatePreviewable();
	}

	/**
	 * update the 3D cursor with current hits
	 * 
	 * @param hits
	 */
	public void updateCursor3D(Hits hits) {
		if (hasMouse()) {
			getEuclidianController().updateNewPoint(true, hits, true, true,
					true, // TODO doSingleHighlighting = false ?
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
		cursorOnXOYPlane.getDrawingMatrix().setDiag(1 / getScale());

		// Application.debug(cursorOnXOYPlane.getCoords());
		// Application.debug(cursorOnXOYPlane.getDrawingMatrix());
	}

	public void switchMoveCursor() {

		if (moveCursorIsVisible())
			cursorOnXOYPlane.switchMoveMode(getMode());

	}

	private boolean moveCursorIsVisible() {
		return cursor == CURSOR_MOVE
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

	/**
	 * update cursor3D matrix
	 */
	public void updateMatrixForCursor3D() {
		double t;

		Coords v;
		if (getEuclidianController().getMode() == EuclidianConstants.MODE_VIEW_IN_FRONT_OF) {

			switch (getCursor3DType()) {

			case PREVIEW_POINT_REGION:
				// use region drawing directions for the arrow
				t = 1 / getScale();
				v = getCursor3D().getMoveNormalDirection();
				if (v.dotproduct(getViewDirection()) > 0)
					v = v.mul(-1);

				CoordMatrix4x4.createOrthoToDirection(getCursor3D()
						.getDrawingMatrix().getOrigin(), v, CoordMatrix4x4.VZ,
						tmpCoords1, tmpCoords2, tmpMatrix4x4);
				tmpMatrix4x4.mulAllButOrigin(t);
				getCursor3D().setDrawingMatrix(tmpMatrix4x4);

				break;
			case PREVIEW_POINT_PATH:
				// use path drawing directions for the arrow
				t = 1 / getScale();
				v = ((GeoElement) getCursor3D().getPath()).getMainDirection()
						.normalized();
				if (v.dotproduct(getViewDirection()) > 0)
					v = v.mul(-1);

				CoordMatrix4x4.createOrthoToDirection(getCursor3D()
						.getDrawingMatrix().getOrigin(), v, CoordMatrix4x4.VZ,
						tmpCoords1, tmpCoords2, tmpMatrix4x4);
				tmpMatrix4x4.mulAllButOrigin(t);
				getCursor3D().setDrawingMatrix(tmpMatrix4x4);

				break;

			}
		} else
			switch (getCursor3DType()) {

			case PREVIEW_POINT_FREE:
				// use default directions for the cross
				t = 1 / getScale();
				getCursor3D().getDrawingMatrix().setVx(Coords.VX.mul(t));
				getCursor3D().getDrawingMatrix().setVy(Coords.VY.mul(t));
				getCursor3D().getDrawingMatrix().setVz(Coords.VZ.mul(t));
				break;
			case PREVIEW_POINT_REGION:

				// use region drawing directions for the cross
				t = 1 / getScale();

				v = getCursor3D().getMoveNormalDirection();

				CoordMatrix4x4.createOrthoToDirection(getCursor3D()
						.getDrawingMatrix().getOrigin(), v, CoordMatrix4x4.VZ,
						tmpCoords1, tmpCoords2, tmpMatrix4x4);
				tmpMatrix4x4.mulAllButOrigin(t);
				getCursor3D().setDrawingMatrix(tmpMatrix4x4);

				break;
			case PREVIEW_POINT_PATH:
			case PREVIEW_POINT_REGION_AS_PATH:
				// use path drawing directions for the cross
				t = 1 / getScale();

				GeoElement path = getCursorPath();
				v = path.getMainDirection();
				CoordMatrix4x4.completeOrtho(v, tmpCoords1, tmpCoords2,
						tmpMatrix4x4_2);

				getCursor3D().getDrawingMatrix().setVx(
						tmpMatrix4x4_2.getVx().normalized().mul(t));
				t *= (10 + path.getLineThickness());
				getCursor3D().getDrawingMatrix().setVy(
						tmpMatrix4x4_2.getVy().mul(t));
				getCursor3D().getDrawingMatrix().setVz(
						tmpMatrix4x4_2.getVz().mul(t));

				break;
			case PREVIEW_POINT_DEPENDENT:
				// use size of intersection
				t = getIntersectionThickness() / getScale();
				getCursor3D().getDrawingMatrix().setVx(Coords.VX.mul(t));
				getCursor3D().getDrawingMatrix().setVy(Coords.VY.mul(t));
				getCursor3D().getDrawingMatrix().setVz(Coords.VZ.mul(t));
				break;
			case PREVIEW_POINT_ALREADY:
				// use size of point
				t = Math.max(1, getCursor3D().getPointSize() / 6.0 + 0.5)
						/ getScale();

				if (getCursor3D().hasPath()) {
					v = ((GeoElement) getCursor3D().getPath())
							.getMainDirection();

					CoordMatrix4x4.completeOrtho(v, tmpCoords1, tmpCoords2,
							tmpMatrix4x4);

					tmpMatrix4x4_2.setVx(tmpMatrix4x4.getVy());
					tmpMatrix4x4_2.setVy(tmpMatrix4x4.getVz());
					tmpMatrix4x4_2.setVz(tmpMatrix4x4.getVx());
					tmpMatrix4x4_2.setOrigin(tmpMatrix4x4.getOrigin());

				} else if (getCursor3D().hasRegion()) {

					v = getCursor3D().getMoveNormalDirection();

					CoordMatrix4x4.createOrthoToDirection(getCursor3D()
							.getCoordsInD3(), v, CoordMatrix4x4.VZ, tmpCoords1,
							tmpCoords2, tmpMatrix4x4_2);
				} else {
					CoordMatrix4x4.Identity(tmpMatrix4x4_2);
				}

				getCursor3D().getDrawingMatrix().setVx(
						tmpMatrix4x4_2.getVx().normalized().mul(t));
				getCursor3D().getDrawingMatrix().setVy(
						tmpMatrix4x4_2.getVy().mul(t));
				getCursor3D().getDrawingMatrix().setVz(
						tmpMatrix4x4_2.getVz().mul(t));
				break;
			}

		// Application.debug("getCursor3DType()="+getCursor3DType());

	}

	private GeoElement getCursorPath() {
		if (getCursor3DType() == PREVIEW_POINT_PATH) {
			return (GeoElement) getCursor3D().getPath();
		}

		// PREVIEW_POINT_REGION_AS_PATH
		return (GeoElement) getCursor3D().getRegion();
	}

	@Override
	public void setPreview(Previewable previewDrawable) {

		// App.debug(""+previewDrawable);

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

	// ///////////////////////////////////////////////////
	//
	// POINT DECORATION
	//
	// ///////////////////////////////////////////////////

	private void initPointDecorations() {
		// Application.debug("hop");
		pointDecorations = new DrawPointDecorations(this);
	}

	/**
	 * update decorations for localizing point in the space if point==null, no
	 * decoration will be drawn
	 * 
	 * @param point
	 */
	public void updatePointDecorations(GeoPoint3D point) {

		if (point == null)
			decorationVisible = false;
		else {
			decorationVisible = true;
			pointDecorations.setPoint(point);
		}

		// Application.debug("point :\n"+point.getDrawingMatrix()+"\ndecorations :\n"+decorationMatrix);

	}

	// ///////////////////////////////////////////////////
	//
	// CURSOR
	//
	// ///////////////////////////////////////////////////

	/**
	 * draws the mouse cursor (for glasses)
	 * 
	 * @param renderer1
	 *            renderer
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
			// App.debug("\n"+eye);
			double eyeSep = 0;
			if (getProjection() == PROJECTION_GLASSES) {
				eyeSep = renderer1.getEyeSep(); // TODO eye lateralization
			}

			double x = mouseLoc.x + renderer1.getLeft() + eyeSep;
			double y = -mouseLoc.y + renderer1.getTop();
			double dz = eye.getZ() - z;
			double coeff = dz / eye.getZ();

			v = new Coords(x * coeff - eyeSep, y * coeff, z, 1);
		}

		drawMouseCursor(renderer1, v);

	}

	private CoordMatrix4x4 tmpMatrix4x4 = new CoordMatrix4x4();
	private CoordMatrix4x4 tmpMatrix4x4_2 = CoordMatrix4x4.Identity();
	protected CoordMatrix4x4 tmpMatrix4x4_3 = CoordMatrix4x4.Identity();

	private Coords tmpCoords1 = new Coords(4), tmpCoords2 = new Coords(4);

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

	/**
	 * draws the cursor
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawCursor(Renderer renderer1) {

		// App.debug("\nhasMouse="
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
				renderer1.setMatrix(cursorOnXOYPlane.getDrawingMatrix());
				drawPointAlready(cursorOnXOYPlane.getRealMoveMode());
				renderer1.drawCursor(PlotterCursor.TYPE_CUBE);
			} else if (!getEuclidianController().mouseIsOverLabel()
					&& ((EuclidianController3D) getEuclidianController())
							.cursor3DVisibleForCurrentMode(getCursor3DType())) {
				renderer1.setMatrix(getCursor3D().getDrawingMatrix());

				switch (cursor) {
				case CURSOR_DEFAULT:
					switch (getCursor3DType()) {
					case PREVIEW_POINT_FREE:
						drawFreeCursor(renderer1);
						break;
					case PREVIEW_POINT_ALREADY: // showing arrows directions
						drawPointAlready(getCursor3D());
						break;
					case PREVIEW_POINT_NONE:
						// App.debug("ici");
						break;
					}
					break;
				case CURSOR_HIT:
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

	private void drawPointAlready(int mode) {

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

	public void setMoveCursor() {

		// 3D cursor
		cursor = CURSOR_MOVE;

		// Application.printStacktrace("");
		// Application.debug("ici");

	}

	public void setCursor(int cursor) {
		switch (cursor) {
		case CURSOR_DRAG:
			setDragCursor();
			break;
		case CURSOR_MOVE:
			setMoveCursor();
			break;
		case CURSOR_HIT:
			setHitCursor();
			break;
		case CURSOR_DEFAULT:
		default:
			setDefaultCursor();
			break;
		}
	}

	public int getCursor() {
		return cursor;
	}

	private boolean defaultCursorWillBeHitCursor = false;

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
		cursor = CURSOR_DRAG;
		// Application.printStacktrace("setDragCursor");

	}

	/**
	 * 
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
			setTransparentCursor(); // use own 3D cursor (for depth)
			// setDefault2DCursor();
		} else {
			setDefault2DCursor();
		}

		// 3D cursor
		cursor = CURSOR_DEFAULT;
	}

	/**
	 * set 2D cursor to default
	 */
	abstract protected void setDefault2DCursor();

	public void setHitCursor() {

		if (getShiftDown()) // do nothing
			return;

		// App.printStacktrace("setHitCursor");
		cursor = CURSOR_HIT;
	}

	/**
	 * returns settings in XML format, read by xml handlers
	 * 
	 * @see org.geogebra.common.io.MyXMLHandler
	 * @see org.geogebra.common.geogebra3D.io.MyXMLHandler3D
	 * @return the XML description of 3D view settings
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
		if (projectionObliqueAngle != EuclidianSettings3D.PROJECTION_OBLIQUE_ANGLE_DEFAULT){
			sb.append("\" obliqueAngle=\"");
			sb.append(projectionObliqueAngle);
		}
		if (projectionObliqueFactor != EuclidianSettings3D.PROJECTION_OBLIQUE_FACTOR_DEFAULT){
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

	// ///////////////////////////////////////////////////
	//
	// EUCLIDIANVIEW DRAWABLES (AXIS AND PLANE)
	//
	// ///////////////////////////////////////////////////

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

	/**
	 * 
	 * @return true if show xOy plane
	 */
	public boolean getShowPlane() {
		return xOyPlane.isPlateVisible();
	}

	/**
	 * toggle the visibility of xOy grid
	 */
	public void toggleGrid() {

		getSettings().showGrid(!getShowGrid());

	}

	public GeoPlane3D getxOyPlane() {

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
		return decorationVisible;
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
		xOyPlaneDrawable.setWaitForUpdateVisualStyle();

		for (int i = 0; i < 3; i++) {
			axisDrawable[i].setWaitForUpdateVisualStyle();
		}

		pointDecorations.setWaitForUpdateVisualStyle();

		// other drawables
		drawable3DLists.resetAllVisualStyles();

	}

	/**
	 * 
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

	private double[] parameters = new double[2];

	private void intervalUnionOutside(double[] minmax, Coords o, Coords v,
			Coords p1, Coords p2) {
		p1.projectLine(o, v, tmpCoords1, parameters);
		double t1 = parameters[0];
		p2.projectLine(o, v, tmpCoords1, parameters);
		double t2 = parameters[0];
		intervalUnion(minmax, t1, t2);
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

		// App.debug(v1+","+v2);

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

	public void updateBounds() {
		((Kernel3D) kernel).setEuclidianView3DBounds(evNo, getXmin(),
				getXmax(), getYmin(), getYmax(), getZmin(), getZmax(),
				getXscale(), getYscale(), getZscale());
		calcPrintingScale();
	}

	@Override
	public void updateBounds(boolean updateDrawables, boolean updateSettings) {
		updateBounds();
	}

	private void viewChangedOwnDrawables() {

		// update, but not in case where view changed by rotation
		if (!viewChanged() || viewChangedByTranslate() || viewChangedByZoom()) {
			// update clipping cube
			double[][] minMax = clippingCubeDrawable.updateMinMax();
			clippingCubeDrawable.setWaitForUpdate();

			// update e.g. Corner[]
			kernel.notifyEuclidianViewCE();

			// xOy plane wait for update
			xOyPlaneDrawable.setWaitForUpdate();

			// update decorations and wait for update
			for (int i = 0; i < 3; i++) {
				axisDrawable[i].setDrawMinMaxImmediatly(minMax);
				axisDrawable[i].updateDecorations();
				setAxesIntervals(getScale(i), i);

				axisDrawable[i].setWaitForUpdate();

			}
		} else {
			// we need to update renderer clip planes, since they are in screen
			// coordinates
			clippingCubeDrawable.updateRendererClipPlanes();
			// we need to update axis numbers locations
			for (int i = 0; i < 3; i++) {
				axisDrawable[i].updateDecorations();
				axisDrawable[i].setLabelWaitForUpdate();
			}
			
			// update e.g. Corner[]
			kernel.notifyEuclidianViewCE();

		}

	}

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

	// ////////////////////////////////////////////////////
	// AXES
	// ////////////////////////////////////////////////////

	/**
	 * 
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
	 * 
	 * @param i
	 *            index
	 * @return if i-th axis shows numbers
	 */
	public boolean getShowAxisNumbers(int i) {
		return showAxesNumbers[i];
	}

	// ///////////////////////////
	// OPTIONS
	// //////////////////////////

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
		// TODO Auto-generated method stub
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

	// //////////////////////////////////////
	// ALGEBRA VIEW
	// //////////////////////////////////////

	@Override
	public int getMode() {
		return getEuclidianController().getMode();
	}

	protected Hits3D tempArrayList = new Hits3D();

	public void setResizeXAxisCursor() {
		// TODO Auto-generated method stub

	}

	public void setResizeYAxisCursor() {
		// TODO Auto-generated method stub

	}

	// ///////////////////////////////////////////////
	// UPDATE VIEW : ZOOM, TRANSLATE, ROTATE
	// ///////////////////////////////////////////////

	private boolean viewChangedByZoom = true;
	private boolean viewChangedByTranslate = true;
	private boolean viewChangedByRotate = true;

	private int pointStyle;

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
		viewChangedByZoom = true;
		viewChangedByTranslate = true;
		viewChangedByRotate = true;
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
		return app.getPlain("space");
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

	// ///////////////////////////////////////////////
	// PROJECTION (ORTHO/PERSPECTIVE/...)
	// ///////////////////////////////////////////////

	final static public int PROJECTION_ORTHOGRAPHIC = 0;
	final static public int PROJECTION_PERSPECTIVE = 1;
	final static public int PROJECTION_GLASSES = 2;
	final static public int PROJECTION_OBLIQUE = 3;

	private int projection = PROJECTION_ORTHOGRAPHIC;

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

	public void setProjectionOrthographic() {
		renderer.setWaitForDisableStencilLines();
		renderer.updateOrthoValues();
		setProjectionValues(PROJECTION_ORTHOGRAPHIC);
		setDefault2DCursor();
	}

	private double[] projectionPerspectiveEyeDistance = 
		{PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT, PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT};
	
	private static final int PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT = 2500;
	
	

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
				&& projection != PROJECTION_GLASSES)
			projection = PROJECTION_PERSPECTIVE;
		updateProjectionPerspectiveEyeDistance();
		if (projection == PROJECTION_GLASSES) { // also update eyes separation
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
		setTransparentCursor();
	}

	private boolean isGlassesGrayScaled = true;

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

	private boolean isGlassesShutDownGreen = false;

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

	private double[] eyeX = {-100, 100}, eyeY = {0 , 0};

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
	
	
	public double getEyeX(int i){
		return eyeX[i];
	}

	public double getEyeY(int i){
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

	private double projectionObliqueAngle = 30;
	private double projectionObliqueFactor = 0.5;

	public void setProjectionOblique() {
		renderer.updateProjectionObliqueValues();
		renderer.setWaitForDisableStencilLines();
		setProjectionValues(PROJECTION_OBLIQUE);
		setDefault2DCursor();
	}

	public void setProjectionObliqueAngle(double angle) {
		projectionObliqueAngle = angle;
		renderer.updateProjectionObliqueValues();
	}

	public double getProjectionObliqueAngle() {
		return projectionObliqueAngle;
	}

	public void setProjectionObliqueFactor(double factor) {
		projectionObliqueFactor = factor;
		renderer.updateProjectionObliqueValues();
	}

	public double getProjectionObliqueFactor() {
		return projectionObliqueFactor;
	}

	// ////////////////////////////////////////////////////
	//
	// ////////////////////////////////////////////////////

	@Override
	public boolean getShowAxis(int axisNo) {
		return this.axis[axisNo].isEuclidianVisible();
	}

	@Override
	public void replaceBoundObject(GeoNumeric num, GeoNumeric geoNumeric) {

	}

	protected GColor bgColor;

	public GColor getBackground() {
		return bgColor;
	}

	@Override
	final public GColor getBackgroundCommon() {
		return getBackground();
	}

	/*
	 * @Override public geogebra.common.awt.GColor getBackgroundCommon() {
	 * return new geogebra.awt.GColorD(getBackground());
	 * 
	 * }
	 */

	// ////////////////////////////////////
	// PICKING

	public void addOneGeoToPick() {
		renderer.addOneGeoToPick();
	}

	public void removeOneGeoToPick() {
		renderer.removeOneGeoToPick();
	}

	// ////////////////////////////////////
	// SOME LINKS WITH 2D VIEW

	@Override
	public int getFontSize() {

		return app.getFontSize();
	}

	@Override
	public int getEuclidianViewNo() {
		return getViewID();
	}

	// ////////////////////////////////////////
	// ABSTRACTEUCLIDIANVIEW
	// ////////////////////////////////////////

	// ////////////////////////////////////////
	// EUCLIDIANVIEWND
	// ////////////////////////////////////////

	public Drawable newDrawButton(GeoButton geo) {
		return null;
	}

	public Drawable newDrawTextField(GeoTextField geo) {
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
	public void setEraserCursor() {
		Log.warn("unimplemented");

	}

	@Override
	public org.geogebra.common.awt.GGraphics2D getGraphicsForPen() {
		return null;
	}

	@Override
	protected void doDrawPoints(GeoImage gi,
			List<org.geogebra.common.awt.GPoint> penPoints2,
			org.geogebra.common.awt.GColor penColor, int penLineStyle, int penSize) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void drawResetIcon(GGraphics2D g) {
		App.debug("unimplemented");

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
	 * 
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
		eyeX[0] = - evs.getEyeSep()/2;
		eyeX[1] = - eyeX[0];
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

	private Coords boundsMin, boundsMax;

	@Override
	public final void setViewShowAllObjects(boolean storeUndo) {

		if (boundsMin == null) {
			boundsMin = new Coords(3);
			boundsMax = new Coords(3);
		}

		boundsMin.setPositiveInfinity();
		boundsMax.setNegativeInfinity();

		drawable3DLists.enlargeBounds(boundsMin, boundsMax);

		// App.debug("\nmin=\n"+boundsMin+"\nmax=\n"+boundsMax);

		// no object
		if (Double.isInfinite(boundsMin.getX())) {
			return;
		}

		zoomRW(boundsMin, boundsMax);

	}

	public void zoomRW(Coords boundsMin2, Coords boundsMax2) {
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
		scale *= getScale();
		if (Double.isNaN(scale) || Kernel.isZero(scale)) {
			scale = SCALE_STANDARD;
		} else {
			// let the view a bit greater than the scene
			scale *= 0.94;
		}

		double x = -(boundsMin2.getX() + boundsMax2.getX()) / 2;
		double y = -(boundsMin2.getY() + boundsMax2.getY()) / 2;
		double z = -(boundsMin2.getZ() + boundsMax2.getZ()) / 2;

		setAnimatedCoordSystem(x, y, z, scale, 15);

	}

	@Override
	public void setEuclidianViewNo(int evNo) {
		this.evNo = evNo;
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
	
	// maximum angle between two line segments
	private static final double MAX_ANGLE_SPEED_SURFACE = 20; // degrees
	private static final double MAX_BEND_SPEED_SURFACE = Math.tan(MAX_ANGLE_SPEED_SURFACE * Kernel.PI_180);
	

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

	private double fontScale = 1;

	public double getFontScale() {
		return fontScale;
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
}
