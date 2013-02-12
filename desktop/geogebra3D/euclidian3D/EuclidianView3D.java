package geogebra3D.euclidian3D;

import geogebra.common.GeoGebraConstants;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.DrawableND;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.Previewable;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.CoordMatrixUtil;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.kernel.kernelND.GeoAxisND;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import geogebra.common.kernel.kernelND.GeoRayND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.Unicode;
import geogebra.euclidian.EuclidianStyleBarD;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.main.AppD;
import geogebra3D.euclidian3D.opengl.PlotterCursor;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoClippingCube3D;
import geogebra3D.kernel3D.GeoConic3D;
import geogebra3D.kernel3D.GeoCurveCartesian3D;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPlane3DConstant;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoQuadric3D;
import geogebra3D.kernel3D.GeoQuadric3DPart;
import geogebra3D.kernel3D.GeoSurfaceCartesian3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class for 3D view
 * @author matthieu
 *
 */
public class EuclidianView3D extends EuclidianViewND implements Printable {

	private static final long serialVersionUID = -8414195993686838278L;
	
	//private Kernel kernel;
	private Kernel3D kernel3D;
	protected AppD app;
	private EuclidianController3D euclidianController3D;
	private Renderer renderer;
	
	
	
	// distances between grid lines
	protected boolean automaticGridDistance = true;
	// since V3.0 this factor is 1, before it was 0.5
	final public static double DEFAULT_GRID_DIST_FACTOR = 1;
	public static double automaticGridDistanceFactor = DEFAULT_GRID_DIST_FACTOR;

	double[] gridDistances = { 2, 2, 2 };
	


	protected boolean[] piAxisUnit = { false, false, false };
	
	
	protected double[] axesNumberingDistances = { 2, 2, 2 };
	protected boolean[] automaticAxesNumberingDistances = { true, true, true };


	protected int[] axesTickStyles = { EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
			EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR, EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR };
	

	private double[] axisCross = {0,0,0};
	private boolean[] positiveAxes = {false, false, false};
	private boolean[] drawBorderAxes = {false,false, false};
	
	//viewing values
	private double XZero = 0;
	private double YZero = 0;
	private double ZZero = -1.5;
	
	private double XZeroOld = 0;
	private double YZeroOld = 0;
	private double ZZeroOld = 0;
	
	//list of 3D objects
	private boolean waitForUpdate = true; //says if it waits for update...
	//public boolean waitForPick = false; //says if it waits for update...
	private Drawable3DLists drawable3DLists;// = new DrawList3D();
	/** list for drawables that will be added on next frame */
	private LinkedList<Drawable3D> drawable3DListToBeAdded;// = new DrawList3D();
	/** list for drawables that will be removed on next frame */
	private LinkedList<Drawable3D> drawable3DListToBeRemoved;// = new DrawList3D();
	/** list for Geos to that will be added on next frame */
	private TreeSet<GeoElement> geosToBeAdded;
	
	
	// Map (geo, drawable) for GeoElements and Drawables
	private TreeMap<GeoElement,Drawable3D> drawable3DMap = new TreeMap<GeoElement,Drawable3D>();
	
	//matrix for changing coordinate system
	private CoordMatrix4x4 m = CoordMatrix4x4.Identity(); 
	private CoordMatrix4x4 mInv = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 mInvTranspose = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 undoRotationMatrix = CoordMatrix4x4.Identity();
	
	
	public final static double ANGLE_ROT_OZ=-60;
	public final static double ANGLE_ROT_XOY=20;
	
	private double a = ANGLE_ROT_OZ;
	private double b = ANGLE_ROT_XOY;//angles (in degrees)
	private double aOld, bOld;
	private double aNew, bNew;


	//picking and hits
	private Hits3D hits = new Hits3D(); //objects picked from openGL
	
	//base vectors for moving a point
	/** origin */
	static public Coords o = new Coords(new double[] {0.0, 0.0, 0.0,  1.0});
	/** vx vector */
	static public Coords vx = new Coords(new double[] {1.0, 0.0, 0.0,  0.0});
	/** vy vector */
	static public Coords vy = new Coords(new double[] {0.0, 1.0, 0.0,  0.0});
	/** vz vector */
	static public Coords vz = new Coords(new double[] {0.0, 0.0, 1.0,  0.0});
	/** vzNeg vector */
	static public Coords vzNeg = new Coords(new double[] {0.0, 0.0, -1.0,  0.0});
	
	/** direction of view */
	private Coords viewDirection = vz.copyVector();
	private Coords eyePosition = new Coords(4);

	
	//axis and xOy plane
	private GeoPlane3D xOyPlane;
	private GeoAxisND[] axis;
	private GeoClippingCube3D clippingCube;
	
	private DrawPlane3D xOyPlaneDrawable;
	private DrawAxis3D[] axisDrawable;
	private DrawClippingCube3D clippingCubeDrawable;
	
	
	/** number of drawables linked to this view (xOy plane, Ox, Oy, Oz axis) */
	static final public int DRAWABLES_NB = 4;
	/** id of z-axis */
	static final int AXIS_Z = 2; //AXIS_X and AXIS_Y already defined in EuclidianViewInterface

	//point decorations	
	private DrawPointDecorations pointDecorations;
	private boolean decorationVisible = false;

	//preview
	private Previewable previewDrawable;
	private GeoPoint3D cursor3D, cursorOnXOYPlane;
	//private boolean cursorOnXOYPlaneVisible;
	public DrawLine3D previewDrawLine3D;
	public DrawConic3D previewDrawConic3D;
	public GeoLine3D previewLine;
	public GeoConic3D previewConic;
	private GeoElement[] cursor3DIntersectionOf = new GeoElement[2]; 
	
	//cursor
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
	private long animatedScaleTimeStart;
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
	private long animatedRotTimeStart;
	
	/** tells if the view is under animation for rotation */
	private boolean animatedRot = false;

	/** says if the view is frozen (see freeze()) */
	private boolean isFrozen = false;
		
	/**  selection rectangle  TODO */
	protected Rectangle selectionRectangle = new Rectangle();
	
	/**
	 * common constructor
	 * @param ec controller on this
	 */
	public EuclidianView3D(EuclidianController3D ec, EuclidianSettings settings){
		
		super(ec, settings);
		
		this.euclidianController3D = ec;
		this.kernel3D = (Kernel3D) ec.getKernel();
		euclidianController3D.setView(this);
		app = ec.getApplication();	
		
		start();
		
		initView(false);
	}
	
	@Override
	protected void initAxesValues(){
		axesNumberFormat = new NumberFormatAdapter[3];
		showAxesNumbers = new boolean[] { true, true, true };
		axesLabels = new String[] { "x", "y", "z" };
		axesLabelsStyle = new int[] { GFont.PLAIN, GFont.PLAIN, GFont.PLAIN };
		axesUnitLabels = new String[] { null, null, null };

	}
		
	@Override
	public AppD getApplication() {
		return app;
	}
	
	private void start(){
		
		drawable3DLists = new Drawable3DLists(this);
		drawable3DListToBeAdded = new LinkedList<Drawable3D>();
		drawable3DListToBeRemoved = new LinkedList<Drawable3D>();
		
		geosToBeAdded = new TreeSet<GeoElement>();
		
		//TODO replace canvas3D with GLDisplay
		App.debug("create gl renderer");
		renderer = new Renderer(this);
		renderer.setDrawable3DLists(drawable3DLists);
		
			
        //JPanel canvas = this;
		
        Component canvas = renderer.canvas;
		getJPanel().setLayout(new BorderLayout());
		getJPanel().add(BorderLayout.CENTER, canvas);
						
		attachView();
		
		// register Listener
		canvas.addMouseMotionListener(euclidianController3D);
		canvas.addMouseListener(euclidianController3D);
		canvas.addMouseWheelListener(euclidianController3D);
		canvas.setFocusable(true);
		
				

		initAxisAndPlane();
		
		//previewables
		//kernel3D.setSilentMode(true);
		cursor3D = new GeoPoint3D(kernel3D.getConstruction());
		cursor3D.setCoords(0,0,0,1);
		cursor3D.setIsPickable(false);
		//cursor3D.setLabelOffset(5, -5);
		//cursor3D.setEuclidianVisible(false);
		cursor3D.setMoveNormalDirection(EuclidianView3D.vz);
		//kernel3D.setSilentMode(false);
		
		cursorOnXOYPlane = new GeoPoint3D(kernel3D.getConstruction());
		cursorOnXOYPlane.setCoords(0,0,0,1);
		cursorOnXOYPlane.setIsPickable(false);
		cursorOnXOYPlane.setMoveNormalDirection(EuclidianView3D.vz);
		cursorOnXOYPlane.setRegion(xOyPlane);
		
		//point decorations
		initPointDecorations();
		
		//tells the renderer if use clipping cube
		updateUseClippingCube();
		
			
	}
	
	/**
	 * init the axis and xOy plane
	 */
	public void initAxisAndPlane(){
		//axis
		axis = new GeoAxisND[3];
		axisDrawable = new DrawAxis3D[3];
		axis[0] = kernel3D.getXAxis3D();
		axis[1] = kernel3D.getYAxis3D();
		axis[2] = kernel3D.getZAxis3D();
				
		for(int i=0;i<3;i++){
			axis[i].setLabelVisible(true);
			axisDrawable[i] = (DrawAxis3D) createDrawable((GeoElement) axis[i]);
		}	
		
		
		//clipping cube
		clippingCube = kernel3D.getClippingCube();
		clippingCube.setEuclidianVisible(true);
		clippingCube.setObjColor(new geogebra.awt.GColorD(0.5f,0.5f,0.5f));
		clippingCube.setLineThickness(1);
		clippingCube.setIsPickable(false);
		clippingCubeDrawable = (DrawClippingCube3D) createDrawable(clippingCube);
		
		//plane	
		xOyPlane = kernel3D.getXOYPlane();
		xOyPlane.setEuclidianVisible(true);
		xOyPlane.setGridVisible(true);
		xOyPlane.setPlateVisible(true);
		//xOyPlane.setFading(0);
		xOyPlaneDrawable = (DrawPlane3D) createDrawable(xOyPlane);		

	}

	// POINT_CAPTURING_STICKY_POINTS locks onto these points
	// not implemented yet in 3D
	@Override
	public ArrayList<GeoPointND> getStickyPointList() {
		return null;
	}
	
	/** return the 3D kernel
	 * @return the 3D kernel
	 */
	@Override
	public Kernel3D getKernel(){
		return kernel3D;
	}

	/**
	 * @return controller
	 */
	@Override
	public EuclidianController3D getEuclidianController(){
		return euclidianController3D;
	}
	
	/**
	 * @return gl renderer
	 */
	public Renderer getRenderer(){
		return renderer;
	}
	
	/**
	 * adds a GeoElement3D to this view
	 */	
	@Override
	public void add(GeoElement geo) {
		
		if (geo.isVisibleInView3D()){
			setWaitForUpdate();
			geosToBeAdded.add(geo);
		}
	}
	
	/**
	 * add the geo now
	 * @param geo
	 */
	private void addNow(GeoElement geo){
		
		//check if geo has been already added
		if (getDrawableND(geo)!=null)
			return;
		
		//create the drawable
		Drawable3D d = null;
		d = createDrawable(geo);
		if (d != null) {
			drawable3DLists.add(d);
		}
	}
		
	/**
	 * add the drawable to the lists of drawables
	 * @param d
	 */
	public void addToDrawable3DLists(Drawable3D d){	
		/*
		if (d.getGeoElement().getLabel().equals("a")){
			Application.debug("d="+d);
		}
		*/
		
		setWaitForUpdate();
		drawable3DListToBeAdded.add(d);
	}

	/**
	 * Create a {@link Drawable3D} linked to the {@link GeoElement3D}
	 * 
	 * <h3> Exemple:</h3>
	  
	  For a GeoElement3D called "GeoNew3D", add in the switch the following code:
	    <p>
	    <code>
	    case GeoElement3D.GEO_CLASS_NEW3D: <br> &nbsp;&nbsp;                   
           d = new DrawNew3D(this, (GeoNew3D) geo); <br> &nbsp;&nbsp;
           break; <br> 
        }
        </code>

	 * 
	 * @param geo GeoElement for which the drawable is created
	 * @return the drawable
	 */
	//protected Drawable3D newDrawable(GeoElement geo) {
	@Override
	protected Drawable3D createDrawable(GeoElement geo) {
		Drawable3D d=null;
		if (geo.hasDrawable3D()){

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
							axisDrawable[AXIS_X],axisDrawable[AXIS_Y]);
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
				d = new DrawConicPart3D(this, (GeoConicPart) geo);
				break;	

			case AXIS:	
			case AXIS3D:	
				d = new DrawAxis3D(this, (GeoAxisND) geo);	
				break;	

			case CURVECARTESIAN3D:	
				d = new DrawCurve3D(this, (GeoCurveCartesian3D) geo);	
				break;				
				
			case ANGLE:
			case ANGLE3D:
				d = new DrawAngle3D(this, (GeoAngle) geo);
				break;

			case QUADRIC:					
				d = new DrawQuadric3D(this, (GeoQuadric3D) geo);
				break;									

			case QUADRIC_PART:					
				d = new DrawQuadric3DPart(this, (GeoQuadric3DPart) geo);
				break;	

			case FUNCTION_NVAR:
				GeoFunctionNVar geoFun = (GeoFunctionNVar) geo;
				switch(geoFun.getVarNumber()){
				case 2:
					d = new DrawSurface3D(this, geoFun);
					break;
				/*
				case 3:
					d = new DrawImplicitFunction3Var(this, geoFun);
					break;
				*/
				}
				break;	
								
			case SURFACECARTESIAN3D:	
				d = new DrawSurface3D(this, (GeoSurfaceCartesian3D) geo);
				break;	
				
			case TEXT:
				d = new DrawText3D(this,(GeoText) geo);
				break;
				
			case CLIPPINGCUBE3D:
				d = new DrawClippingCube3D(this, (GeoClippingCube3D) geo);
				break;
			}
		}		
		
		

		if (d != null) 			
			drawable3DMap.put(geo, d);
		
		return d;
	}
	

	
	/**
	 * converts the vector to scene coords
	 * @param vInOut vector
	 */
	final public void toSceneCoords3D(Coords vInOut) {	
		changeCoords(mInv,vInOut);		
	}
	
	/**
	 * converts the vector to screen coords
	 * @param vInOut vector
	 */
	final public void toScreenCoords3D(Coords vInOut) {	
		changeCoords(m,vInOut);		
	}

	
	final private static void changeCoords(CoordMatrix mat, Coords vInOut){
		Coords v1 = vInOut.getCoordsLast1();
		vInOut.set(mat.mul(v1));		
	}
	
	/** return the matrix : screen coords -> scene coords.
	 * @return the matrix : screen coords -> scene coords.
	 */
	final public CoordMatrix4x4 getToSceneMatrix(){
		
		return mInv;
	}
	
	final public CoordMatrix4x4 getToSceneMatrixTranspose(){
		
		return mInvTranspose;
	}
	
	/** return the matrix : scene coords -> screen coords.
	 * @return the matrix : scene coords -> screen coords.
	 */
	final public CoordMatrix4x4 getToScreenMatrix(){
		
		return m;
	}	
	
	/** return the matrix undoing the rotation : scene coords -> screen coords.
	 * @return the matrix undoing the rotation : scene coords -> screen coords.
	 */
	final public CoordMatrix4x4 getUndoRotationMatrix(){
		
		return undoRotationMatrix;
	}
	
	private boolean yAxisIsUp = false;
	
	/**
	 * 
	 * @return true if y axis is up (and not z axis)
	 */
	public boolean getYAxisIsUp(){
		return yAxisIsUp;
	}
	
	/**
	 * set if y axis is up (and not z axis)
	 * @param flag flag
	 */
	public void setYAxisIsUp(boolean flag){
		yAxisIsUp = flag;
	}

	
	private CoordMatrix getRotationMatrix(){
		
		CoordMatrix m1, m2;

		if (yAxisIsUp){ //y axis taken for up-down direction
			m1 = CoordMatrix.Rotation3DMatrix(CoordMatrix.X_AXIS, (this.b)*EuclidianController3D.ANGLE_TO_DEGREES);
			m2 = CoordMatrix.Rotation3DMatrix(CoordMatrix.Y_AXIS, (-this.a-90)*EuclidianController3D.ANGLE_TO_DEGREES);		
		}else{ //z axis taken for up-down direction
			m1 = CoordMatrix.Rotation3DMatrix(CoordMatrix.X_AXIS, (this.b-90)*EuclidianController3D.ANGLE_TO_DEGREES);
			m2 = CoordMatrix.Rotation3DMatrix(CoordMatrix.Z_AXIS, (-this.a-90)*EuclidianController3D.ANGLE_TO_DEGREES);
		}
		
		return m1.mul(m2);
	}
	
	private CoordMatrix getScaleMatrix(){
		return CoordMatrix.ScaleMatrix(new double[] {getXscale(),getYscale(),getZscale()});				
	}
	
	private CoordMatrix getTranslationMatrix(){
		return CoordMatrix.TranslationMatrix(new double[] {getXZero(),getYZero(),getZZero()});		
	}
	
	/**
	 * set Matrix for view3D
	 */	
	public void updateMatrix(){
		
		//TODO use Ggb3DMatrix4x4
		//Application.printStacktrace("");
		
		//rotations
		CoordMatrix mRot = getRotationMatrix();

		undoRotationMatrix.set(mRot.inverse());

		//scaling
		CoordMatrix mScale = getScaleMatrix();
		
		//translation
		CoordMatrix mTrans = getTranslationMatrix();
		
		//m.set(m5.mul(m3.mul(m4)));	
		//m.set(m3.mul(m5.mul(m4)));	
		m.set(mRot.mul(mScale.mul(mTrans)));	
		
		mInv.set(m.inverse());
		mInvTranspose.set(mInv.transposeCopy());
		
		updateEye();
			
		//Application.debug("Zero = ("+getXZero()+","+getYZero()+","+getZZero()+")");	
	}
	
	private void updateEye(){
		
		//update view direction
		if (projection==PROJECTION_CAV)
			viewDirection=renderer.getCavOrthoDirection().copyVector();
		else
			viewDirection = vzNeg.copyVector();
		toSceneCoords3D(viewDirection);	
		viewDirection.normalize();
		
		//update eye position
		if (projection==PROJECTION_ORTHOGRAPHIC || projection==PROJECTION_CAV)
			eyePosition=viewDirection;
		else{
			eyePosition=renderer.getPerspEye().copyVector();
			toSceneCoords3D(eyePosition);	
		}
	}
	
	/**
	 * 
	 * @return ortho direction of the eye
	 */
	public Coords getViewDirection(){
		if (projection==PROJECTION_ORTHOGRAPHIC || projection==PROJECTION_CAV)
			return viewDirection;
		else
			return viewDirectionPersp;
	}

	/**
	 * 
	 * @return eye position
	 */
	public Coords getEyePosition(){
		return eyePosition;
	}
	
	/**
	 * sets the rotation matrix
	 * @param a
	 * @param b
	 */
	public void setRotXYinDegrees(double a, double b){
		
		//Application.debug("setRotXY: "+a+","+b);
		
		this.a = a;
		this.b = b;
		
		if (this.b>EuclidianController3D.ANGLE_MAX)
			this.b=EuclidianController3D.ANGLE_MAX;
		else if (this.b<-EuclidianController3D.ANGLE_MAX)
			this.b=-EuclidianController3D.ANGLE_MAX;
		

	}
		
	/** Sets coord system from mouse move */
	@Override
	final public void setCoordSystemFromMouseMove(int dx, int dy, int mode) {	
		switch(mode){
		case EuclidianController.MOVE_ROTATE_VIEW:
			setRotXYinDegrees(aOld - dx, bOld + dy);
			updateMatrix();
			setViewChangedByRotate();
			setWaitForUpdate();	
			break;
		case EuclidianController.MOVE_VIEW:			
			Coords v = new Coords(dx,-dy,0,0);
			toSceneCoords3D(v);

			if (cursorOnXOYPlane.getRealMoveMode()==GeoPointND.MOVE_MODE_XY){
				v=v.projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY, getViewDirection())[0];
				setXZero(XZeroOld+v.getX());
				setYZero(YZeroOld+v.getY());
			}else{
				v=v.projectPlane(CoordMatrix4x4.IDENTITY)[1];
				setZZero(ZZeroOld+v.getZ());
			}
			
			updateMatrix();
			setViewChangedByTranslate();
			setWaitForUpdate();
			break;
		}
	}	

	/* TODO interaction - note : methods are called by EuclidianRenderer3D.viewOrtho() 
	 * to re-center the scene */
	@Override
	public double getXZero() { return XZero; }
	@Override
	public double getYZero() { return YZero; }
	/** @return the z-coord of the origin */
	public double getZZero() { return ZZero; }

	/** set the x-coord of the origin 
	 * @param val */
	public void setXZero(double val) { 
		XZero=val; 
	}
	
	/** set the y-coord of the origin 
	 * @param val */
	public void setYZero(double val) { 
		YZero=val; 
	}
	
	/** set the z-coord of the origin 
	 * @param val */
	public void setZZero(double val) { 
		ZZero=val; 
	}
	
	/**
	 * sets the origin
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	public void setZeroFromXML(double x, double y, double z){
		
		if (GeoGebraConstants.IS_PRE_RELEASE){
			if (app.fileVersionBefore(App.getSubValues("4.9.14.0"))){
				//new matrix multiplication (since 4.9.14)
				CoordMatrix mRot = getRotationMatrix();
				CoordMatrix mScale = getScaleMatrix();
				setXZero(x);setYZero(y);setZZero(z);
				CoordMatrix mTrans = getTranslationMatrix();
				CoordMatrix mRS = mRot.mul(mScale);
				CoordMatrix matrix = ((mRS.inverse()).mul(mTrans).mul(mRS));
				Coords origin = matrix.getOrigin();				
				setXZero(origin.getX());setYZero(origin.getY());setZZero(origin.getZ());
				updateMatrix();
				return;
			}
		}
		
		setXZero(x);setYZero(y);setZZero(z);
	}
		
	public double getXRot(){ return a;}
	public double getZRot(){ return b;}
	

	/**  @return min-max value for x-axis (linked to grid)*/
	public double[] getXMinMax(){ return axisDrawable[AXIS_X].getDrawMinMax(); }
	/**  @return min value for y-axis (linked to grid)*/
	public double[] getYMinMax(){ return axisDrawable[AXIS_Y].getDrawMinMax(); }
	/**  @return min value for z-axis */
	public double[] getZMinMax(){ 
		return axisDrawable[AXIS_Z].getDrawMinMax(); 
	}

	
	//TODO specific scaling for each direction
	private double scale = 50; 


	@Override
	public double getXscale() { return scale; }
	@Override
	public double getYscale() { return scale; }
	
	/** @return the z-scale */
	public double getZscale() { return scale; }
	
	/**
	 * set the all-axis scale
	 * @param val
	 */
	public void setScale(double val){
		scale = val;
		setViewChangedByZoom();
	}
	
	/**
	 * @return the all-axis scale
	 */
	public double getScale(){
		return scale;
	}

	/** remembers the origins values (xzero, ...) */
	@Override
	public void rememberOrigins(){
		aOld = a;
		bOld = b;
		XZeroOld = XZero;
		YZeroOld = YZero;
		ZZeroOld = ZZero;
	}
	
	
	//////////////////////////////////////
	// update
	
	/** update the drawables for 3D view */
	public void update(){
		
		if (isAnimated()){
			animate();
			setWaitForUpdate();
		}
		
		if (waitForUpdate){
			//drawList3D.updateAll();

			// I've placed remove() before add(), otherwise when the two lists
			// contains the same element, the element will NOT be added. ---Tam, 2011/7/15			
			drawable3DLists.remove(drawable3DListToBeRemoved);
			drawable3DListToBeRemoved.clear();
			
			
			
			//add drawables (for preview)
			drawable3DLists.add(drawable3DListToBeAdded);	
			drawable3DListToBeAdded.clear();
			
			//add geos
			for (GeoElement geo : geosToBeAdded)
				addNow(geo);
			geosToBeAdded.clear();
			
			viewChangedOwnDrawables();
			setWaitForUpdateOwnDrawables();
						
			waitForUpdate = false;
		}

		// update decorations
		pointDecorations.update();
	}	
	
	/** 
	 * tell the view that it has to be updated
	 * 
	 */
	public void setWaitForUpdate(){
		waitForUpdate = true;
	}
	
	/*
	public void paint(Graphics g){
				
		if (!isStarted){
			//Application.debug("ici");
			isStarted = true;
		}
		
		
		//update();
		//setWaitForUpdate();
		if (isFrozen)
			super.paint(g);
	}	
	*/
	
	//////////////////////////////////////
	// toolbar and euclidianController3D
	
	/** sets EuclidianController3D mode */
	@Override
	public void setMode(int mode){
		if (mode == euclidianController3D.getMode()) return;
		euclidianController3D.setMode(mode);
		getStyleBar().setMode(mode);
	}
	
	//////////////////////////////////////
	// picking

	private Coords pickPoint = new Coords(0, 0, 0, 1);
	private Coords viewDirectionPersp = new Coords(4);

	/**
	 * (x,y) 2D screen coords -> 3D physical coords
	 * 
	 * @param x
	 * @param y
	 * @return 3D physical coords of the picking point
	 */
	public Coords getPickPoint(int x, int y) {

		pickPoint.setX(x + renderer.getLeft());
		pickPoint.setY(-y + renderer.getTop());

		if (projection == PROJECTION_PERSPECTIVE
				|| projection == PROJECTION_ANAGLYPH) {
			viewDirectionPersp = pickPoint.sub(renderer.getPerspEye());
			toSceneCoords3D(viewDirectionPersp);
			viewDirectionPersp.normalize();
		}

		return pickPoint.copyVector();
	}
	
	/**
	 * 
	 * @param p 3D point in scene coords
	 * @return (x,y) point aligned with p
	 */
	public Coords projectOnScreen(Coords p){
		Coords p1 = getToScreenMatrix().mul(p);//.getInhomCoords();
		if (projection == PROJECTION_PERSPECTIVE
				|| projection == PROJECTION_ANAGLYPH) {
			Coords eye = renderer.getPerspEye();
			Coords v = p1.sub(eye);
			return new Coords(eye.getX()-eye.getZ()*v.getX()/v.getZ(),eye.getY()-eye.getZ()*v.getY()/v.getZ());
		}
		return new Coords(p1.getX(),p1.getY());
	}

	/** p scene coords, (dx,dy) 2D mouse move -> 3D physical coords 
	 * @param p 
	 * @param dx 
	 * @param dy 
	 * @return 3D physical coords  */
	public Coords getPickFromScenePoint(Coords p, int dx, int dy){
		
		Coords point = getToScreenMatrix().mul(p);

		pickPoint.setX(point.get(1)+dx);
		pickPoint.setY(point.get(2)-dy);
		
		if (projection==PROJECTION_PERSPECTIVE||projection==PROJECTION_ANAGLYPH){
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
	
	
	@Override
	public void clearView() {
		
		//clear lists
		drawable3DLists.clear();
		geosToBeAdded.clear();
		drawable3DListToBeAdded.clear();
		drawable3DMap.clear();
		
		initView(false);
	}
	
	@Override
	protected void initView(boolean repaint) {
		super.initView(repaint);
		setBackground(Color.white);
		updateMatrix();
	}

	/**
	 * remove a GeoElement3D from this view
	 */	
	@Override
	public void remove(GeoElement geo) {
		
		if (geo.hasDrawable3D()){
			//Drawable3D d = ((GeoElement3DInterface) geo).getDrawable3D();
			Drawable3D d = drawable3DMap.get(geo);
			//drawable3DLists.remove(d);
			remove(d);
			
			//for GeoList : remove all 3D drawables linked to it
			if (geo.isGeoList()){
				if (d!=null) {
					removeDrawList3D((DrawList3D) d);
				}
			}
		}
		
		drawable3DMap.remove(geo);
		geosToBeAdded.remove(geo);
	}
	


	private void removeDrawList3D(DrawList3D d){
		for (DrawableND d1 : d.getDrawables3D()){
			if (d1.createdByDrawList()){
				remove((Drawable3D) d1);
				removeFromDrawList(d1);
			}
		}
	}
	
	private void removeFromDrawList(DrawableND d){
		if (d instanceof DrawList3D){
			removeDrawList3D((DrawList3D) d);
		}
	}
	
	/**
	 * remove the drawable d
	 * @param d
	 */
	public void remove(Drawable3D d) {
		setWaitForUpdate();
		drawable3DListToBeRemoved.add(d);		
	}

	@Override
	public void rename(GeoElement geo) {
		// TODO Raccord de méthode auto-généré		
	}

	@Override
	public void repaintView() {
		
		//Application.debug("repaint View3D");		
		//super.repaintView();
	}

	@Override
	public void reset() {
		
		//Application.debug("reset View3D");
		resetAllDrawables();
		//updateAllDrawables();
		viewChangedOwnDrawables();
		setViewChanged();
		setWaitForUpdate();
		
		//update();		
	}

	@Override
	public void update(GeoElement geo) {
		
		//String s = geo.toString(); if (s.startsWith("F")) Application.debug(s);
		
		if (geo.hasDrawable3D()){
			Drawable3D d = drawable3DMap.get(geo);
				//((GeoElement3DInterface) geo).getDrawable3D();
			//Application.debug(d);
			if (d!=null){
				update(d);
				//update(((GeoElement3DInterface) geo).getDrawable3D());
			}
		}
	}
	
	@Override
	public void updateVisualStyle(GeoElement geo) {
		//Application.debug(geo);
		if (geo.hasDrawable3D()){
			Drawable3D d = drawable3DMap.get(geo);
			if (d!=null){
				d.setWaitForUpdateVisualStyle();
			}
		}
	}
	
	private void updateAllDrawables(){
		for (Drawable3D d:drawable3DMap.values())
			update(d);
		setWaitForUpdateOwnDrawables();		
	}
	
	/**
	 * says this drawable to be updated
	 * @param d
	 */
	public void update(Drawable3D d){
		d.setWaitForUpdate();
	}



	//////////////////////////////////////////////
	// EuclidianViewInterface


	@Override
	public DrawableND getDrawableND(GeoElement geo) {
		if (geo.hasDrawable3D()){

			return drawable3DMap.get(geo);
		}
		
		return null;
	}



	@Override
	public GeoElement getLabelHit(geogebra.common.awt.GPoint p) {
		
		return hits.getLabelHit();
	}

	@Override
	public Previewable getPreviewDrawable() {
		
		return previewDrawable;
	}

	@Override
	public geogebra.common.awt.GRectangle getSelectionRectangle() {
		return new geogebra.awt.GRectangleD(selectionRectangle);
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
	public void setShowAxis(int axis, boolean flag, boolean update){
		this.axis[axis].setEuclidianVisible(flag);
	}

	@Override
	public void setShowAxes(boolean flag, boolean update){
		setShowAxis(AXIS_X, flag, false);
		setShowAxis(AXIS_Y, flag, false);
		setShowAxis(AXIS_Z, flag, true);
	}

	/** sets the visibility of xOy plane
	 * @param flag
	 */
	public void setShowPlane(boolean flag){
		getxOyPlane().setEuclidianVisible(flag);
	}
		
	/** sets the visibility of xOy plane plate
	 * @param flag
	 */
	public void setShowPlate(boolean flag){
		getxOyPlane().setPlateVisible(flag);
	}

	/** sets the visibility of xOy plane grid
	 * @param flag
	 */
	public void setShowGrid(boolean flag){
		getxOyPlane().setGridVisible(flag);
	}

	@Override
	public int getViewHeight() {
		return getHeight();
	}


	@Override
	public int getViewWidth() {
		return getWidth();
	}

	public boolean hitAnimationButton(MouseEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean hitAnimationButton(AbstractEvent e) {
		return false;
	}
	
	
	@Override
	public void repaintEuclidianView() {
		//Application.debug("repaintEuclidianView");
		//super.repaintEuclidianView();
	}

	@Override
	public void resetMode() {
		// TODO Auto-generated method stub
		
	}


	//////////////////////////////////////////////////
	// ANIMATION
	//////////////////////////////////////////////////


	/** tells if the view is under animation */
	private boolean isAnimated(){
		return animatedScale || isRotAnimated();
	}
	
	/** tells if the view is under rot animation 
	 * @return true if there is a rotation animation*/
	public boolean isRotAnimated(){
		return  animatedContinueRot || animatedRot;
	}
	
	/** 
	 * @return true if there is a continue rotation animation*/
	public boolean isRotAnimatedContinue(){
		return  animatedContinueRot;
	}
	
	/**
	 * 
	 * @param p point
	 * @return true if the point is between min-max coords values
	 */
	public boolean isInside(Coords p){
		double[] minmax = getXMinMax();
		double val = p.getX();
		if (val<minmax[0] || val>minmax[1])
			return false;
		minmax = getYMinMax();
		val = p.getY();
		if (val<minmax[0] || val>minmax[1])
			return false;
		minmax = getZMinMax();
		val = p.getZ();
		if (val<minmax[0] || val>minmax[1])
			return false;
		return true;		
	}
	
	
	private boolean useClippingCube = true;
	private boolean showClippingCube = true;
	
	/**
	 * 
	 * @return true if use of clipping cube
	 */
	public boolean useClippingCube(){
		return useClippingCube;
	}
	
	/**
	 * sets the use of the clipping cube
	 * @param flag flag
	 */
	public void setUseClippingCube(boolean flag){

		useClippingCube = flag;
		updateUseClippingCube();
		
	}
	
	private void updateUseClippingCube(){
		renderer.setEnableClipPlanes(useClippingCube);
		setViewChanged();
		setWaitForUpdate();
	}
	
	/**
	 * 
	 * @return true if clipping cube is shown
	 */
	public boolean showClippingCube(){
		return showClippingCube;
	}
	
	/**
	 * sets if the clipping cube is shown
	 * @param flag flag
	 */
	public void setShowClippingCube(boolean flag){
		showClippingCube=flag;
		setWaitForUpdate();
	}
	
	/**
	 *  toggle show/hide clipping
	 */
	public void toggleShowAndUseClippingCube(){
		boolean flag = showClippingCube || useClippingCube;
		setShowClippingCube(!flag);
		setUseClippingCube(!flag);
	}
	
	
	/**
	 * sets the reduction of the clipping box
	 * @param value reduction
	 */
	public void setClippingReduction(int value){
		clippingCube.setReduction(value);
		setWaitForUpdate();
	}
	
	/**
	 * 
	 * @return the reduction of the clipping box
	 */
	public int getClippingReduction(){
		return clippingCube.getReduction();
	}
	
	
	@Override
	public void setAnimatedCoordSystem(double ox, double oy, double f, double newScale,
			int steps, boolean storeUndo) {
		
		animatedScaleStartX=getXZero();
		animatedScaleStartY=getYZero();
		animatedScaleStartZ=getZZero();

		/*
		double centerX = ox+renderer.getLeft();
		double centerY = -oy+renderer.getTop();
		Coords v = new Coords(centerX,centerY,0,1);
		toSceneCoords3D(v);
		v=v.projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY, getViewDirection())[0];
		*/
		Coords v=cursor3D.getInhomCoords();
		
		if (!isInside(v)){//takes center of the scene for fixed point
			v = new Coords(-animatedScaleStartX,-animatedScaleStartY,-animatedScaleStartZ,1);
		}
		
		//Application.debug(v);
		
		double factor=getScale()/newScale;
		animatedScaleEndX=-v.getX()+(animatedScaleStartX+v.getX())*factor;
		animatedScaleEndY=-v.getY()+(animatedScaleStartY+v.getY())*factor;
		animatedScaleEndZ=-v.getZ()+(animatedScaleStartZ+v.getZ())*factor;

		
		
		//Application.debug("mouse = ("+ox+","+oy+")"+"\nscale end = ("+animatedScaleEndX+","+animatedScaleEndY+")"+"\nZero = ("+animatedScaleStartX+","+animatedScaleStartY+")");
		
		animatedScaleStart = getScale();
		animatedScaleTimeStart = System.currentTimeMillis();
		animatedScaleEnd = newScale;
		animatedScale = true;
		
		animatedScaleTimeFactor = 0.005; //it will take about 1/2s to achieve it
		
		//this.storeUndo = storeUndo;		
	}	
	
	/** sets a continued animation for rotation
	 * if delay is too long, no animation
	 * if speed is too small, no animation
	 * @param delay delay since last drag
	 * @param rotSpeed speed of rotation
	 */
	public void setRotContinueAnimation(long delay, double rotSpeed){
		//Application.debug("delay="+delay+", rotSpeed="+rotSpeed);

		//if last drag occured more than 200ms ago, then no animation
		if (delay>200)
			return;
		
		//if speed is too small, no animation
		if (Math.abs(rotSpeed)<0.01){
			stopRotAnimation();
			return;
		}
		
		//if speed is too large, use max speed
		if (rotSpeed>0.1)
			rotSpeed=0.1;
		else if (rotSpeed<-0.1)
			rotSpeed=-0.1;
					
		animatedContinueRot = true;
		animatedRot = false;
		animatedRotSpeed = -rotSpeed;
		animatedRotTimeStart = System.currentTimeMillis() - delay;
		bOld = b;
		aOld = a;
	}	
	
	/**
	 * start a rotation animation to be in the vector direction
	 * @param vn
	 */
	public void setRotAnimation(Coords vn){
		Coords spheric = CoordMatrixUtil.sphericalCoords(vn);		
		setRotAnimation(spheric.get(2)*180/Math.PI,spheric.get(3)*180/Math.PI,true);
	}
		

	/**
	 * start a rotation animation to go to the new values
	 * @param aN
	 * @param bN
	 * @param checkSameValues if true, check new values are same than old, 
	 * in this case revert the view
	 */
	public void setRotAnimation(double aN, double bN, boolean checkSameValues){


		//app.storeUndoInfo();
		
		animatedRot = true;
		animatedContinueRot = false;
		aOld = this.a % 360;
		bOld = this.b % 360;
		
		aNew = aN;
		bNew = bN;
		
		
		//if (aNew,bNew)=(0°,90°), then change it to (90°,90°) to have correct xOy orientation
		if (Kernel.isEqual(aNew, 0, Kernel.STANDARD_PRECISION) &&
				Kernel.isEqual(Math.abs(bNew), 90, Kernel.STANDARD_PRECISION))
			aNew=-90;
		
		
		//looking for the smallest path
		if (aOld-aNew>180)
			aOld-=360;
		else if (aOld-aNew<-180)
			aOld+=360;
			

		else if (checkSameValues) 
			if (Kernel.isEqual(aOld, aNew, Kernel.STANDARD_PRECISION))
				if (Kernel.isEqual(bOld, bNew, Kernel.STANDARD_PRECISION)){
					if (!Kernel.isEqual(Math.abs(bNew), 90, Kernel.STANDARD_PRECISION))
						aNew+=180;
					bNew*=-1;
					//Application.debug("ici");
				}
		if (bOld>180)
			bOld-=360;

		animatedRotTimeStart = System.currentTimeMillis();
		
	}
	
	
	/**
	 * stops the rotation animation
	 */
	public void stopRotAnimation(){
		animatedContinueRot = false;
		animatedRot = false;
		
		
	}


	/** animate the view for changing scale, orientation, etc. */
	private void animate(){
		if (animatedScale){
			double t = (System.currentTimeMillis()-animatedScaleTimeStart)*animatedScaleTimeFactor;
			t+=0.2; //starting at 1/4
			
			if (t>=1){
				t=1;
				animatedScale = false;
			}
			
			//Application.debug("t="+t+"\nscale="+(startScale*(1-t)+endScale*t));
			
			setScale(animatedScaleStart*(1-t)+animatedScaleEnd*t);
			setXZero(animatedScaleStartX*(1-t)+animatedScaleEndX*t);
			setYZero(animatedScaleStartY*(1-t)+animatedScaleEndY*t);
			setZZero(animatedScaleStartZ*(1-t)+animatedScaleEndZ*t);
			
			updateMatrix();
			setViewChangedByZoom();
			setViewChangedByTranslate();
			
		}
		
		if (animatedContinueRot){
			double da = (System.currentTimeMillis()-animatedRotTimeStart)*animatedRotSpeed;			
			setRotXYinDegrees(aOld + da, bOld);
			
			updateMatrix();
			setViewChangedByRotate();
		}
		
		if (animatedRot){
			double t = (System.currentTimeMillis()-animatedRotTimeStart)*0.001;
			t*=t;
			//t+=0.2; //starting at 1/4
			
			if (t>=1){
				t=1;
				animatedRot = false;
			}
			
			setRotXYinDegrees(aOld*(1-t)+aNew*t, bOld*(1-t)+bNew*t);

			updateMatrix();
			setViewChangedByRotate();
		}

			
		
		
	}



	/*
	Point pOld = null;


	public void setHits(Point p) {
		
		
		
		if (p.equals(pOld)){
			//Application.printStacktrace("");
			return;
		}
		
		
		pOld = p;
		
		//sets the flag and mouse location for openGL picking
		renderer.setMouseLoc(p.x,p.y,Renderer.PICKING_MODE_LABELS);

		//calc immediately the hits
		renderer.display();
		

	}
	
	*/
	
	// empty method : setHits3D() used instead
	@Override
	public void setHits(geogebra.common.awt.GPoint p) {
		
	}
	
	
	/** sets the 3D hits regarding point location
	 * @param p point location
	 */
	public void setHits3D(Point p) {
		
		//Application.debug(p.x+","+p.y);
		
		//sets the flag and mouse location for openGL picking
		renderer.setMouseLoc(p.x,p.y,Renderer.PICKING_MODE_LABELS);

		//calc immediately the hits
		//renderer.display();
		

	}
	



	/** add a drawable to the current hits
	 * (used when a new object is created)
	 * @param d drawable to add
	 */
	public void addToHits3D(Drawable3D d){
		hits.addDrawable3D(d, false);
		hits.sort();
	}
	
	

	
	/** init the hits for this view
	 * @param hits
	 */
	public void setHits(Hits3D hits){
		this.hits = hits;
	}



	public Hits3D getHits3D(){
		return hits;
	}

	@Override
	public Hits getHits() {
		return hits.clone();
	}



	public void setHits(Rectangle rect) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setSelectionRectangle(geogebra.common.awt.GRectangle selectionRectangle) {
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
						
		setScale(getXscale()*zoomFactor);
		updateMatrix();
		setWaitForUpdate();
		
		
	}
	
	/////////////////////////////////////////
	// previewables
	
	

	
	/** return the point used for 3D cursor
	 * @return the point used for 3D cursor
	 */
	public GeoPoint3D getCursor3D(){
		return cursor3D;
	}
	
	

	
	
	/**
	 * sets the type of the cursor
	 * @param v
	 */
	public void setCursor3DType(int v){
		cursor3DType = v;
		//App.debug(""+v);
	}
	

	/**
	 * @return the type of the cursor
	 */
	public int getCursor3DType(){
		return cursor3DType;
	}
	
	
	
	
	private int intersectionThickness;
	
	public void setIntersectionThickness(GeoElement a, GeoElement b){
		int t1 = a.getLineThickness();
		int t2 = b.getLineThickness();
		if (t2>t1)
			intersectionThickness=t2;
		else
			intersectionThickness=t1;
		intersectionThickness+=6;
	}
	
	public int getIntersectionThickness(){
		return intersectionThickness;
	}
	
	
	
	private GeoPointND intersectionPoint;
	
	public void setIntersectionPoint(GeoPointND point){
		intersectionPoint=point;
	}

	public GeoPointND getIntersectionPoint(){
		return intersectionPoint;
	}
	
	
	
	/**
	 * @return the list of 3D drawables
	 */
	public Drawable3DLists getDrawList3D(){
		return drawable3DLists;
	}
	
	
	@Override
	@SuppressWarnings("rawtypes")
	public Previewable createPreviewLine(ArrayList selectedPoints){

		previewDrawLine3D = new DrawLine3D(this, selectedPoints);
		return previewDrawLine3D;
		
	}

	public Previewable createPreviewLine(){
		if (previewDrawLine3D==null) {
			previewLine = new GeoLine3D(getKernel().getConstruction());
			previewLine.setObjColor(new geogebra.awt.GColorD(Color.YELLOW));
			previewLine.setIsPickable(false);
			previewDrawLine3D = new DrawLine3D(this, previewLine);
		}
		return previewDrawLine3D;
		
	}
	

	
	public Previewable createPreviewConic(){
		if (previewDrawConic3D==null) {
			previewConic = new GeoConic3D(getKernel().getConstruction());
			previewConic.setObjColor(new geogebra.awt.GColorD(Color.YELLOW));
			previewConic.setIsPickable(false);
			previewDrawConic3D = new DrawConic3D(this, previewConic);
		}
		return previewDrawConic3D;
		
	}
	
	@Override
	public Previewable createPreviewSegment(ArrayList<GeoPointND> selectedPoints){
		return new DrawSegment3D(this, selectedPoints);
	}	
	
	@Override
	public Previewable createPreviewRay(ArrayList<GeoPointND> selectedPoints){
		return new DrawRay3D(this, selectedPoints);
	}	
	

	@Override
	public Previewable createPreviewVector(ArrayList<GeoPointND> selectedPoints){
		return new DrawVector3D(this, selectedPoints);
	}
	
	@Override
	public Previewable createPreviewPolygon(ArrayList<GeoPointND> selectedPoints){
		return new DrawPolygon3D(this, selectedPoints);
	}
	
	public Previewable createPreviewPyramid(ArrayList<GeoPointND> selectedPoints){
		return new DrawPolygon3D(this, selectedPoints);
	}
	
	
	
	@Override
	public Previewable createPreviewConic(int mode, ArrayList<GeoPointND> selectedPoints){
		return null;
	}

	/**
	 * @param selectedPoints
	 * @return a preview sphere (center-point)
	 */
	@SuppressWarnings("rawtypes")
	public Previewable createPreviewSphere(ArrayList selectedPoints){
		return new DrawQuadric3D(this, selectedPoints, GeoQuadricNDConstants.QUADRIC_SPHERE);
	}	

	/**
	 * @return a preview right prism/cylinder (basis and height)
	 */
	public Previewable createPreviewExtrusion(
			ArrayList<GeoPolygon> selectedPolygons,
			ArrayList<GeoConicND> selectedConics
			){
		return new DrawExtrusion3D(this, selectedPolygons, selectedConics);
	}	
	
	/**
	 * @return a preview pyramid/cone (basis and height)
	 */
	public Previewable createPreviewConify(
			ArrayList<GeoPolygon> selectedPolygons,
			ArrayList<GeoConicND> selectedConics
			){
		return new DrawConify3D(this, selectedPolygons, selectedConics);
	}	
	
	
	
	@Override
	public void updatePreviewable(){
		
		if (getCursor3DType()!=PREVIEW_POINT_NONE){
			getPreviewDrawable().updatePreview();
		}
	}
	
	@Override
	public void updatePreviewableForProcessMode(){
		updatePreviewable();
	}
	
	
	/**
	 * update the 3D cursor with current hits
	 * @param hits 
	 */
	public void updateCursor3D(Hits hits){
		if (hasMouse){
			getEuclidianController().updateNewPoint(true, 
				hits, 
				true, true, true,  //TODO doSingleHighlighting = false ? 
				false, false);
			

			updateCursorOnXOYPlane();
			
			updateMatrixForCursor3D();
		}
		
	}
	
	private void updateCursorOnXOYPlane(){
		cursorOnXOYPlane.setWillingCoords(getCursor3D().getCoords());
		cursorOnXOYPlane.setWillingDirection(getViewDirection());
		cursorOnXOYPlane.doRegion();
		
		//cursorOnXOYPlaneVisible = isInside(cursorOnXOYPlane.getInhomCoords());
		
		//if (cursorOnXOYPlaneVisible)
			cursorOnXOYPlane.getDrawingMatrix().setDiag(1/getScale());
			
		//Application.debug(cursorOnXOYPlane.getCoords());
		//Application.debug(cursorOnXOYPlane.getDrawingMatrix());
	}
	

	public void switchMoveCursor() {
		
		if (moveCursorIsVisible())
			cursorOnXOYPlane.switchMoveMode();
		
		
	}

	private boolean moveCursorIsVisible(){
		return cursor==CURSOR_MOVE || euclidianController3D.getMode()==EuclidianConstants.MODE_TRANSLATEVIEW;
	}
	
	/**
	 * update the 3D cursor with current hits
	 */
	public void updateCursor3D(){
		//updateCursor3D(getHits().getTopHits()); 
		
	
		//we also want to see different pick orders in preview, e.g. line/plane intersection
		//For now we follow the practice of EView2D: we reserve only points if there are any, 
		//and return the clone if there are no points.
		//TODO: define this behavior better
		if (getHits().containsGeoPoint())
			updateCursor3D(getHits().getTopHits());
		else
			updateCursor3D(getHits());
		
	}

	/**
	 * update cursor3D matrix
	 */
	public void updateMatrixForCursor3D(){		
		double t;

		CoordMatrix4x4 matrix;
		CoordMatrix4x4 m2;
		Coords v;
		CoordMatrix m;
		if (getEuclidianController().getMode()==EuclidianConstants.MODE_VIEW_IN_FRONT_OF){

			switch(getCursor3DType()){

			case PREVIEW_POINT_REGION:
				// use region drawing directions for the arrow
				t = 1/getScale();
				v = getCursor3D().getMoveNormalDirection();		
				if (v.dotproduct(getViewDirection())>0)
					v=v.mul(-1);

				matrix = new CoordMatrix4x4(getCursor3D().getDrawingMatrix().getOrigin(),v,CoordMatrix4x4.VZ);
				matrix.mulAllButOrigin(t);
				getCursor3D().setDrawingMatrix(matrix);
				
				break;
			case PREVIEW_POINT_PATH:
				// use path drawing directions for the arrow
				t = 1/getScale();
				v = ((GeoElement)getCursor3D().getPath()).getMainDirection().normalized();
				if (v.dotproduct(getViewDirection())>0)
					v=v.mul(-1);
				
				matrix = new CoordMatrix4x4(getCursor3D().getDrawingMatrix().getOrigin(),v,CoordMatrix4x4.VZ);
				matrix.mulAllButOrigin(t);
				getCursor3D().setDrawingMatrix(matrix);

				break;
		

			}
		}else
			switch(getCursor3DType()){

			case PREVIEW_POINT_FREE:
				// use default directions for the cross
				t = 1/getScale();
				getCursor3D().getDrawingMatrix().setVx(vx.mul(t));
				getCursor3D().getDrawingMatrix().setVy(vy.mul(t));
				getCursor3D().getDrawingMatrix().setVz(vz.mul(t));
				break;
			case PREVIEW_POINT_REGION:
				
				
				// use region drawing directions for the cross
				t = 1/getScale();

				v = getCursor3D().getMoveNormalDirection();	
				
				matrix = new CoordMatrix4x4(getCursor3D().getDrawingMatrix().getOrigin(),v,CoordMatrix4x4.VZ);
				matrix.mulAllButOrigin(t);
				getCursor3D().setDrawingMatrix(matrix);

				break;
			case PREVIEW_POINT_PATH:
				// use path drawing directions for the cross
				t = 1/getScale();

				v = ((GeoElement)getCursor3D().getPath()).getMainDirection();
				m = new CoordMatrix(4, 2);
				m.set(v, 1);
				m.set(4, 2, 1);
				matrix = new CoordMatrix4x4(m);


				getCursor3D().getDrawingMatrix().setVx(
						matrix.getVx().normalized().mul(t));
				t *= (10+((GeoElement) getCursor3D().getPath()).getLineThickness());
				getCursor3D().getDrawingMatrix().setVy(
						matrix.getVy().mul(t));
				getCursor3D().getDrawingMatrix().setVz(
						matrix.getVz().mul(t));


				break;
			case PREVIEW_POINT_DEPENDENT:
				//use size of intersection
				t = getIntersectionThickness()/getScale();
				getCursor3D().getDrawingMatrix().setVx(vx.mul(t));
				getCursor3D().getDrawingMatrix().setVy(vy.mul(t));
				getCursor3D().getDrawingMatrix().setVz(vz.mul(t));
				break;			
			case PREVIEW_POINT_ALREADY:
				//use size of point
				t = 1/getScale();//(getCursor3D().getPointSize()/6+2)/getScale();

				if (getCursor3D().hasPath()){
					v = ((GeoElement)getCursor3D().getPath()).getMainDirection();
					m = new CoordMatrix(4, 2);
					m.set(v, 1);
					m.set(4, 2, 1);
					m2 = new CoordMatrix4x4(m);

					matrix = new CoordMatrix4x4();
					matrix.setVx(m2.getVy());
					matrix.setVy(m2.getVz());
					matrix.setVz(m2.getVx());
					matrix.setOrigin(m2.getOrigin());


				}else if (getCursor3D().hasRegion()){
					
					v = getCursor3D().getMoveNormalDirection();	

					matrix = new CoordMatrix4x4(getCursor3D().getCoordsInD(3), v, CoordMatrix4x4.VZ);
				}else
					matrix = CoordMatrix4x4.Identity();

				getCursor3D().getDrawingMatrix().setVx(
						matrix.getVx().normalized().mul(t));
				getCursor3D().getDrawingMatrix().setVy(
						matrix.getVy().mul(t));
				getCursor3D().getDrawingMatrix().setVz(
						matrix.getVz().mul(t));
				break;
			}





		//Application.debug("getCursor3DType()="+getCursor3DType());

		
	}
	



	@Override
	public void setPreview(Previewable previewDrawable) {
		
		if (this.previewDrawable!=null)
			this.previewDrawable.disposePreview();
		
		if (previewDrawable!=null){
			if (((Drawable3D) previewDrawable).getGeoElement()!=null)
				addToDrawable3DLists((Drawable3D) previewDrawable);
			//drawable3DLists.add((Drawable3D) previewDrawable);
		}
		
		//Application.debug("drawList3D :\n"+drawList3D);
			
		
			
		//setCursor3DType(PREVIEW_POINT_NONE);
		
		this.previewDrawable = previewDrawable;
		
		
		
	}
	

	
	
	
	
	
	
	
	/////////////////////////////////////////////////////
	// 
    // POINT DECORATION 
	//
	/////////////////////////////////////////////////////
	
	private void initPointDecorations(){
		//Application.debug("hop");
		pointDecorations = new DrawPointDecorations(this);
	}
	
	
	/** update decorations for localizing point in the space
	 *  if point==null, no decoration will be drawn
	 * @param point
	 */
	public void updatePointDecorations(GeoPoint3D point){
		
		if (point==null)
			decorationVisible = false;
		else{
			decorationVisible = true;
			pointDecorations.setPoint(point);
		}
		
		//Application.debug("point :\n"+point.getDrawingMatrix()+"\ndecorations :\n"+decorationMatrix);
		
		
	}
	
	

	

	/////////////////////////////////////////////////////
	// 
	// CURSOR
	//
	/////////////////////////////////////////////////////
	
	/**
	 * draws the mouse cursor (for anaglyph)
	 * @param renderer renderer
	 */
	public void drawMouseCursor(Renderer renderer){
		if (!hasMouse)
			return;
		
		if (getProjection() != PROJECTION_ANAGLYPH ) //&& getProjection() != PROJECTION_PERSPECTIVE)
			return;
		
		GPoint mouseLoc = euclidianController.getMouseLoc();
		if (mouseLoc == null)
			return;
		
		Coords v;
		
		if (getCursor3DType()==CURSOR_DEFAULT){
			//if mouse is over nothing, use mouse coords and screen for depth
			v = new Coords(mouseLoc.x + renderer.getLeft(),-mouseLoc.y + renderer.getTop(), 0, 1);
		}else{
			//if mouse is over an object, use its depth and mouse coords
			Coords eye = renderer.getPerspEye();
			double z = getToScreenMatrix().mul(getCursor3D().getCoords()).getZ()
					+20; //to be over
			//App.debug("\n"+eye);
			double eyeSep = 0;
			if (getProjection() == PROJECTION_ANAGLYPH)
				eyeSep = renderer.getEyeSep(); //TODO eye lateralization
			
			double x = mouseLoc.x + renderer.getLeft() + eyeSep;
			double y = -mouseLoc.y + renderer.getTop();
			double dz = eye.getZ() - z;
			double coeff = dz/eye.getZ();
			
			v = new Coords(x*coeff - eyeSep, y*coeff, z, 1);
		}
		
		CoordMatrix4x4 matrix = CoordMatrix4x4.Identity();
		matrix.setOrigin(v);
		renderer.setMatrix(matrix);
		renderer.drawMouseCursor();
		
	
	}	
	
	/** 
	 * draws the cursor
	 * @param renderer renderer
	 */
	public void drawCursor(Renderer renderer){

		
		//App.debug("\nhasMouse="+hasMouse+"\n!getEuclidianController().mouseIsOverLabel() "+!getEuclidianController().mouseIsOverLabel() +"\ngetEuclidianController().cursor3DVisibleForCurrentMode(getCursor3DType())" + getEuclidianController().cursor3DVisibleForCurrentMode(getCursor3DType())+"\ncursor="+cursor+"\ngetCursor3DType()="+getCursor3DType());		

		if (hasMouse){			
			if (moveCursorIsVisible()){
				renderer.setMatrix(cursorOnXOYPlane.getDrawingMatrix());
				drawPointAlready(cursorOnXOYPlane.getRealMoveMode());	
				renderer.drawCursor(PlotterCursor.TYPE_CUBE);
			}else if(!getEuclidianController().mouseIsOverLabel() 
					&& getEuclidianController().cursor3DVisibleForCurrentMode(getCursor3DType())
					){
				renderer.setMatrix(getCursor3D().getDrawingMatrix());

				switch(cursor){
				case CURSOR_DEFAULT:
					switch(getCursor3DType()){
					case PREVIEW_POINT_FREE: //free point on xOy plane
						renderer.drawCursor(PlotterCursor.TYPE_CROSS2D);					
						break;
					case PREVIEW_POINT_ALREADY: //showing arrows directions
						drawPointAlready(getCursor3D().getMoveMode());				
						break;		
					case PREVIEW_POINT_NONE:
						//App.debug("ici");
						break;
					}
					break;
					/*
			case CURSOR_DRAG:
				if(getCursor3DType()==PREVIEW_POINT_ALREADY)
					drawPointAlready();
				break;
					 */
				case CURSOR_HIT:									
					switch(getCursor3DType()){
					case PREVIEW_POINT_FREE:
						renderer.drawCursor(PlotterCursor.TYPE_CROSS2D);
						break;
					case PREVIEW_POINT_REGION:
						if (getEuclidianController().getMode()==EuclidianConstants.MODE_VIEW_IN_FRONT_OF)
							renderer.drawViewInFrontOf();
						else
							renderer.drawCursor(PlotterCursor.TYPE_CROSS2D);
						break;
					case PREVIEW_POINT_PATH:
						if (getEuclidianController().getMode()==EuclidianConstants.MODE_VIEW_IN_FRONT_OF)
							renderer.drawViewInFrontOf();
						else
							renderer.drawCursor(PlotterCursor.TYPE_CYLINDER);
						break;
					case PREVIEW_POINT_DEPENDENT:
						renderer.drawCursor(PlotterCursor.TYPE_DIAMOND);
						break;

					case PREVIEW_POINT_ALREADY:
						drawPointAlready(getCursor3D().getMoveMode());
						break;
					}
					break;
				}
			}
		}
	}

	
	private void drawPointAlready(int mode){
		
		//Application.debug(mode);
		
		switch (mode){
		case GeoPointND.MOVE_MODE_XY:
			renderer.drawCursor(PlotterCursor.TYPE_ALREADY_XY);
			break;
		case GeoPointND.MOVE_MODE_Z:
			renderer.drawCursor(PlotterCursor.TYPE_ALREADY_Z);
			break;
		}
	}
	

	
	
	public void setMoveCursor(){
		
		// 3D cursor
		cursor = CURSOR_MOVE;
		
		//Application.printStacktrace("");
		//Application.debug("ici");
		
	}

	public void setCursor(int cursor){
		switch(cursor){
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
	
	public int getCursor(){
		return cursor;
	}
	
	private boolean defaultCursorWillBeHitCursor = false;
	
	/**
	 * next call to setDefaultCursor() will call setHitCursor() instead
	 */
	public void setDefaultCursorWillBeHitCursor(){
		defaultCursorWillBeHitCursor = true;
	}
	
	public void setDragCursor(){

		// 2D cursor is invisible
		//setCursor(app.getTransparentCursor());

		// 3D cursor
		cursor = CURSOR_DRAG;
		//Application.printStacktrace("setDragCursor");
		
	}
	
	public void setDefaultCursor(){
		//App.printStacktrace("setDefaultCursor:"+defaultCursorWillBeHitCursor);
		
		
		if (app.getShiftDown()) //do nothing
			return;
		
		if (defaultCursorWillBeHitCursor){
			defaultCursorWillBeHitCursor=false;
			setHitCursor();
			return;
		}
		

		// 2D cursor
		if (getProjection()==PROJECTION_ANAGLYPH)
			setTransparentCursor(); //use own 3D cursor (for depth)
		else
			setDefault2DCursor();
		
		
		// 3D cursor
		cursor = CURSOR_DEFAULT;
	}
	
	
	private void setDefault2DCursor(){
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	public void setHitCursor(){
		
		if (app.getShiftDown()) //do nothing
			return;
		
		//Application.printStacktrace("setHitCursor");
		cursor = CURSOR_HIT;
	}
	
	/**
	 * returns settings in XML format, read by xml handlers
	 * @see geogebra.common.io.MyXMLHandler
	 * @see geogebra3D.io.MyXMLHandler3D
	 * @return the XML description of 3D view settings
	 */
	@Override
	public void getXML(StringBuilder sb,boolean asPreference) {
		
		//Application.debug("getXML: "+a+","+b);
		
		//if (true)	return "";
		
		
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
		
		
		
		
		// axis settings
		for (int i = 0; i < 3; i++) {
			sb.append("\t<axis id=\"");
			sb.append(i);
			sb.append("\" show=\"");
			sb.append(axis[i].isEuclidianVisible());			
			sb.append("\" label=\"");
			sb.append(axis[i].getAxisLabel());
			sb.append("\" unitLabel=\"");
			sb.append(axis[i].getUnitLabel());
			sb.append("\" tickStyle=\"");
			sb.append(axis[i].getTickStyle());
			sb.append("\" showNumbers=\"");
			sb.append(axis[i].getShowNumbers());

			// the tick distance should only be saved if
			// it isn't calculated automatically
			/*
			if (!automaticAxesNumberingDistances[i]) {
				sb.append("\" tickDistance=\"");
				sb.append(axesNumberingDistances[i]);
			}
			*/

			sb.append("\"/>\n");
		}
		
		
		// xOy plane settings
		sb.append("\t<plate show=\"");
		sb.append(getxOyPlane().isPlateVisible());		
		sb.append("\"/>\n");

		sb.append("\t<grid show=\"");
		sb.append(getxOyPlane().isGridVisible());		
		sb.append("\"/>\n");
		
		
		// background color
		sb.append("\t<bgColor r=\"");
		sb.append(bgColor.getRed());
		sb.append("\" g=\"");
		sb.append(bgColor.getGreen());
		sb.append("\" b=\"");
		sb.append(bgColor.getBlue());
		sb.append("\"/>\n");
		
		
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
		sb.append("\"/>\n");	
		
		
		//end
		sb.append("</euclidianView3D>\n");
		
		
		
		
	}
	
	
	
	/////////////////////////////////////////////////////
	// 
	// EUCLIDIANVIEW DRAWABLES (AXIS AND PLANE)
	//
	/////////////////////////////////////////////////////
	
	
	/**
	 * toggle the visibility of axes 
	 */
	public void toggleAxis(){
		
		boolean flag = axesAreAllVisible();
		
		for(int i=0;i<3;i++)
			axis[i].setEuclidianVisible(!flag);
		
	}
	

	/** says if all axes are visible
	 * @return true if all axes are visible
	 */
	public boolean axesAreAllVisible(){
		boolean flag = true;

		for(int i=0;i<3;i++)
			flag = (flag && axis[i].isEuclidianVisible());

		return flag;
	}
	
	
	/**
	 * toggle the visibility of xOy plane
	 */
	public void togglePlane(){
		
		boolean flag = xOyPlane.isPlateVisible();
		xOyPlane.setPlateVisible(!flag);
		
	}	
	
	/**
	 * 
	 * @return true if show xOy plane
	 */
	public boolean getShowPlane(){
		return xOyPlane.isPlateVisible();
	}
	
	/**
	 * toggle the visibility of xOy grid
	 */
	public void toggleGrid(){
		
		boolean flag = xOyPlane.isGridVisible();
		xOyPlane.setGridVisible(!flag);
		
	}
	
	
	/**
	 * @return the xOy plane
	 */
	public GeoPlane3D getxOyPlane()  {

		return xOyPlane;
		
	}
	
	
	/**
	 * says if this geo is owned by the view (xOy plane, ...)
	 * @param geo
	 * @return if this geo is owned by the view (xOy plane, ...)
	 */
	public boolean owns(GeoElement geo){
		
		boolean ret = (geo == xOyPlane);
		
		for(int i=0;(!ret)&&(i<3);i++)
			ret = (geo == axis[i]);
		
		return ret;
		
	}
	
	
	
	
	
	
	/** draw transparent parts of view's drawables (xOy plane)
	 * @param renderer
	 */
	public void drawTransp(Renderer renderer){
		
		
		if (xOyPlane.isPlateVisible())
			xOyPlaneDrawable.drawTransp(renderer);
				
	}
	
	
	/** draw hiding parts of view's drawables (xOy plane)
	 * @param renderer
	 */
	public void drawHiding(Renderer renderer){
		xOyPlaneDrawable.drawHiding(renderer);
	}
	
	/** draw not hidden parts of view's drawables (axis)
	 * @param renderer
	 */
	public void draw(Renderer renderer){
		for(int i=0;i<3;i++)
			axisDrawable[i].drawOutline(renderer);
		
		if (showClippingCube())
			clippingCubeDrawable.drawOutline(renderer);
		
	}

	
	
	
	
	
	
	/** draw hidden parts of view's drawables (axis)
	 * @param renderer
	 */
	public void drawHidden(Renderer renderer){
		for(int i=0;i<3;i++)
			axisDrawable[i].drawHidden(renderer);
		
		xOyPlaneDrawable.drawHidden(renderer);
		
		if (showClippingCube())
			clippingCubeDrawable.drawHidden(renderer);
		
		if (decorationVisible)
			pointDecorations.drawHidden(renderer);
			
		
	}
	
	
	/** draw for picking view's drawables (plane and axis)
	 * @param renderer
	 */
	public void drawForPicking(Renderer renderer){
		renderer.pick(xOyPlaneDrawable);
		for(int i=0;i<3;i++)
			renderer.pick(axisDrawable[i]);
	}
	
	
	
	/** draw ticks on axis
	 * @param renderer
	 */
	public void drawLabel(Renderer renderer){
		
		for(int i=0;i<3;i++)
			axisDrawable[i].drawLabel(renderer);
		

	}
	
	
	
	
	
	/**
	 * says all drawables owned by the view that the view has changed
	 */
	/*
	public void viewChangedOwnDrawables(){
		
		//xOyPlaneDrawable.viewChanged();
		xOyPlaneDrawable.setWaitForUpdate();
		
		for(int i=0;i<3;i++)
			axisDrawable[i].viewChanged();
		
		
	}
	*/
	
	/**
	 * tell all drawables owned by the view to be udpated
	 */
	public void setWaitForUpdateOwnDrawables(){
		
		xOyPlaneDrawable.setWaitForUpdate();
		
		for(int i=0;i<3;i++)
			axisDrawable[i].setWaitForUpdate();
		
		clippingCubeDrawable.setWaitForUpdate();
		
		
		
	}
	
	/**
	 * says all labels owned by the view that the view has changed
	 */
	public void resetOwnDrawables(){
		
		xOyPlaneDrawable.setWaitForReset();
		
		for(int i=0;i<3;i++){
			axisDrawable[i].setWaitForReset();
		}
				
		pointDecorations.setWaitForReset();
		
		clippingCubeDrawable.setWaitForReset();
	}
	

	
	/**
	 * says all labels to be recomputed
	 */
	public void resetAllDrawables(){
		
		resetOwnDrawables();
		drawable3DLists.resetAllDrawables();
		
	}
	
	/**
	 * reset all drawables visual styles
	 */
	public void resetAllVisualStyles(){
		
		// own drawables
		xOyPlaneDrawable.setWaitForUpdateVisualStyle();
		
		for(int i=0;i<3;i++){
			axisDrawable[i].setWaitForUpdateVisualStyle();
		}
				
		pointDecorations.setWaitForUpdateVisualStyle();
		
		// other drawables
		drawable3DLists.resetAllVisualStyles();
		
	}
	
	/**
	 * 
	 * @param i index
	 * @return i-th vertex of the clipping cube
	 */
	public Coords getClippingVertex(int i){
		return clippingCubeDrawable.getVertex(i);
	}
	
	private void viewChangedOwnDrawables(){

		//if (useClippingCube()){
			//update clipping cube
			double[][] minMax = clippingCubeDrawable.updateMinMax();
			clippingCubeDrawable.setWaitForUpdate();
			//update decorations and wait for update
			for(int i=0;i<3;i++){
				axisDrawable[i].setDrawMinMaxImmediatly(minMax);
				axisDrawable[i].updateDecorations();
				axisDrawable[i].setWaitForUpdate();
			}
		/*
	    }else{
			// calc draw min/max for x and y axis
			for(int i=0;i<2;i++){
				axisDrawable[i].updateDrawMinMax();
			}

			//for z axis, use bottom to top min/max
			double zmin = (renderer.getBottom()-getYZero())/getScale();
			double zmax = (renderer.getTop()-getYZero())/getScale();
			axisDrawable[AXIS_Z].setDrawMinMax(zmin, zmax);

			//update decorations and wait for update
			for(int i=0;i<3;i++){
				axisDrawable[i].updateDecorations();
				axisDrawable[i].setWaitForUpdate();
			}
		}
		*/
		
	
		
	}
	
	
	/**
	 * update all drawables now
	 */
	public void updateOwnDrawablesNow(){
		
		for(int i=0;i<3;i++){
			axisDrawable[i].update();
		}
		
		// update xOyPlane
		xOyPlaneDrawable.update();
		
		clippingCubeDrawable.update();
		

		
		// update intersection curves in controller
		getEuclidianController().updateOwnDrawablesNow();

		
	}
	
	
	
	
	//////////////////////////////////////////////////////
	// AXES
	//////////////////////////////////////////////////////

	@Override
	public String[] getAxesLabels(boolean addBoldItalicTags) { 
		String[] ret = new String[3]; 
		ret[0] = axesLabels[0]; 
		ret[1] = axesLabels[1]; 
		ret[2] = axesLabels[2]; 

		if (addBoldItalicTags) { 
			for (int axis = 0 ; axis <=2 ; axis++) { 
				if (axesLabels[axis] != null) { 
					ret[axis] = axisLabelForXML(axis); 
				} 
			} 
		} 

		return ret; 
	}
	
	@Override
	public void setAxesLabels(String[] axesLabels){
		this.axesLabels = axesLabels;
		for (int i = 0; i < 3; i++) {
			if (axesLabels[i] != null && axesLabels[i].length() == 0) {
				axesLabels[i] = null;
			}
		}
	}
	
	@Override
	public void setAxisLabel(int axis, String axisLabel){
		if (axisLabel != null && axisLabel.length() == 0) 
			axesLabels[axis] = null;
		else
			axesLabels[axis] = axisLabel;
	}
	
	@Override
	public String[] getAxesUnitLabels(){
		return axesUnitLabels;
	}
	@Override
	public void setShowAxesNumbers(boolean[] showAxesNumbers){
		this.showAxesNumbers = showAxesNumbers;
	}
	
	@Override
	public void setAxesUnitLabels(String[] axesUnitLabels){
		this.axesUnitLabels = axesUnitLabels;

		// check if pi is an axis unit
		for (int i = 0; i < 3; i++) {
			piAxisUnit[i] = axesUnitLabels[i] != null
					&& axesUnitLabels[i].equals(Unicode.PI_STRING);
		}
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
		setAxesIntervals(getZscale(), 2);
	}
	
	@Override
	public boolean[] getShowAxesNumbers(){
		return showAxesNumbers;
	}
	
	@Override
	public void setShowAxisNumbers(int axis, boolean showAxisNumbers){
		showAxesNumbers[axis]=showAxisNumbers;
	}
	
	@Override
	public void setAxesNumberingDistance(double dist, int axis){
		axesNumberingDistances[axis] = dist;
		setAutomaticAxesNumberingDistance(false, axis);
	}
	
	@Override
	public int[] getAxesTickStyles(){
		return axesTickStyles;
	}

	@Override
	public void setAxisTickStyle(int axis, int tickStyle){
		axesTickStyles[axis]=tickStyle;
	}

	
	/////////////////////////////
	// OPTIONS
	////////////////////////////



	@Override
	public Previewable createPreviewParallelLine(ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Previewable createPreviewPerpendicularLine(ArrayList<GeoPointND> selectedPoints,
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
	public Previewable createPreviewAngleBisector(ArrayList<GeoPointND> selectedPoints) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Previewable createPreviewPolyLine(ArrayList<GeoPointND> selectedPoints) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAxisCross(int axis, double cross) {
		axisCross[axis] = cross;
	}

	@Override
	public void setPositiveAxis(int axis, boolean isPositiveAxis) {
		positiveAxes[axis] = isPositiveAxis;
	}

	@Override
	public double[] getAxesCross() {
		return axisCross;
	}

	@Override
	public void setAxesCross(double[] axisCross) {
		this.axisCross = axisCross;
	}

	@Override
	public boolean[] getPositiveAxes() {
		return positiveAxes;
	}

	@Override
	public void setPositiveAxes(boolean[] positiveAxis) {
		this.positiveAxes = positiveAxis;
	}


	


	@Override
	public boolean getShowGrid() {
		return xOyPlane.isGridVisible();
	}


	@Override
	public boolean getGridIsBold() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean getAllowShowMouseCoords() {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public int getAxesLineStyle() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getGridLineStyle() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean isAutomaticGridDistance() {
		return automaticGridDistance;
	}


	@Override
	public double[] getGridDistances() {
		return gridDistances;
	}


	@Override
	public void setAxesColor(geogebra.common.awt.GColor showColorChooser) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setGridColor(geogebra.common.awt.GColor showColorChooser) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void showGrid(boolean selected) {
		xOyPlane.setGridVisible(selected);
		
	}


	@Override
	public void setGridIsBold(boolean selected) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setAllowShowMouseCoords(boolean selected) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setGridType(int selectedIndex) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setAxesLineStyle(int selectedIndex) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setGridLineStyle(int type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAutomaticGridDistance(boolean flag) {
		automaticGridDistance = flag;
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
		setAxesIntervals(getZscale(), 1);	
	}



	@Override
	public void setGridDistances(double[] dist) {
		gridDistances = dist;
		setAutomaticGridDistance(false);
	}

	@Override
	public void setAutomaticAxesNumberingDistance(boolean b, int axis) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void setAxesTickStyles(int[] styles) {
		// TODO Auto-generated method stub		
	}

	@Override
	public boolean[] getDrawBorderAxes() {
		return drawBorderAxes;
	}

	@Override
	public void setDrawBorderAxes(boolean[] drawBorderAxes) {
		this.drawBorderAxes = drawBorderAxes;		
	}

	@Override
	public boolean[] isAutomaticAxesNumberingDistance() {
		return automaticAxesNumberingDistances;
	}

	@Override
	public double[] getAxesNumberingDistances() {
		return axesNumberingDistances;
	}

	
	////////////////////////////////////////
	// ALGEBRA VIEW
	////////////////////////////////////////
	

	@Override
	public int getMode() {
		return euclidianController3D.getMode();
	}

	protected Hits3D tempArrayList = new Hits3D();



	public void setResizeXAxisCursor() {
		// TODO Auto-generated method stub
		
	}


	public void setResizeYAxisCursor() {
		// TODO Auto-generated method stub
		
	}
	
	
	/////////////////////////////////////////////////
	// UPDATE VIEW : ZOOM, TRANSLATE, ROTATE
	/////////////////////////////////////////////////
	
	private boolean viewChangedByZoom = true;
	private boolean viewChangedByTranslate = true;
	private boolean viewChangedByRotate = true;

	private int pointCapturingMode;

	private int pointStyle;
	
	private void setViewChangedByZoom(){viewChangedByZoom = true;}
	private void setViewChangedByTranslate(){viewChangedByTranslate = true;}
	private void setViewChangedByRotate(){viewChangedByRotate = true;}
	public void setViewChanged(){
		viewChangedByZoom = true;
		viewChangedByTranslate = true;
		viewChangedByRotate = true;
	}
	
	public boolean viewChangedByZoom(){return viewChangedByZoom;}
	public boolean viewChangedByTranslate(){return viewChangedByTranslate;}
	public boolean viewChangedByRotate(){return viewChangedByRotate;}
	public boolean viewChanged(){
		return viewChangedByZoom || viewChangedByTranslate || viewChangedByRotate;
	}
	
	public void resetViewChanged(){
		viewChangedByZoom = false;
		viewChangedByTranslate = false;
		viewChangedByRotate = false;
	}
	
	/**
	 * Returns point capturing mode.
	 */
	@Override
	final public int getPointCapturingMode() {
		return pointCapturingMode;
	}

	/**
	 * Set capturing of points to the grid.
	 */
	@Override
	public void setPointCapturing(int mode) {
		pointCapturingMode = mode;
	}
	
	final public int getPointStyle() {
		return pointStyle;
	}
		

	@Override
	public String getFromPlaneString(){
		return "space";
	}
	
	@Override
	public String getTranslatedFromPlaneString(){
		return app.getPlain("space");
	}


	@Override
	public Previewable createPreviewAngle(ArrayList<GeoPointND> selectedPoints) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDefault2D(){
		return false;
	}
	

	@Override
	public boolean hasForParent(GeoElement geo){
		return false;
	}
	

	@Override
	public boolean isMoveable(GeoElement geo){
		return geo.isMoveable();
	}
	

	@Override
	public ArrayList<GeoPoint> getFreeInputPoints(AlgoElement algoParent){
		return algoParent.getFreeInputPoints();
	}
	
	
	/////////////////////////////////////////////////
	// PROJECTION (ORTHO/PERSPECTIVE/...)
	/////////////////////////////////////////////////
	
	
	final static public int PROJECTION_ORTHOGRAPHIC = 0;
	final static public int PROJECTION_PERSPECTIVE = 1;
	final static public int PROJECTION_ANAGLYPH = 2;
	final static public int PROJECTION_CAV = 3;
	
	private int projection = PROJECTION_ORTHOGRAPHIC;
	
	public void setProjection(int projection){
		switch(projection){
		case PROJECTION_ORTHOGRAPHIC:
			setProjectionOrthographic();
			break;
		case PROJECTION_PERSPECTIVE:
			setProjectionPerspective();
			break;
		case PROJECTION_ANAGLYPH:
			setAnaglyph();
			break;
		case PROJECTION_CAV:
			setCav();
			break;
			
		}
		
	}
	
	
	private void setProjectionValues(int projection){
		if(this.projection!=projection){
			this.projection=projection;
			updateEye();
			setViewChanged();
			setWaitForUpdate();
			//resetAllDrawables();
			resetAllVisualStyles();
			renderer.setWaitForUpdateClearColor();
		}
	}
	
	public int getProjection(){
		return projection;
	}
	
	
	public void setProjectionOrthographic(){
		renderer.updateOrthoValues();
		setProjectionValues(PROJECTION_ORTHOGRAPHIC);
		setDefault2DCursor();
	}
	
	
	private double projectionPerspectiveValue = 1500;
	

	public void setProjectionPerspective(){
		updateProjectionPerspectiveValue();
		setProjectionValues(PROJECTION_PERSPECTIVE);
		setDefault2DCursor();
		//setTransparentCursor();
	}
	
	
	/**
	 * set the near distance regarding the angle (in degrees)
	 * @param angle
	 */
	public void setProjectionPerspectiveValue(double angle){
		projectionPerspectiveValue = angle;
		if (projection!=PROJECTION_PERSPECTIVE && projection!=PROJECTION_ANAGLYPH)
			projection=PROJECTION_PERSPECTIVE;
		updateProjectionPerspectiveValue();
	}
	
	private void updateProjectionPerspectiveValue(){
		/*
		if (projectionPerspectiveValue<0)
			renderer.setNear(0);
		else
		*/
		renderer.setNear(projectionPerspectiveValue);
	}
	
	/**
	 * 
	 * @return angle for perspective projection
	 */
	public double getProjectionPerspectiveValue(){
		return projectionPerspectiveValue;
	}
	
	
	public void setAnaglyph(){
		updateProjectionPerspectiveValue();
		renderer.updateAnaglyphValues();
		setProjectionValues(PROJECTION_ANAGLYPH);
		setTransparentCursor();
	}
	
	private boolean isAnaglyphGrayScaled = true;
	
	public boolean isAnaglyphGrayScaled(){
		return isAnaglyphGrayScaled;
	}
	
	public void setAnaglyphGrayScaled(boolean flag){
		
		if (isAnaglyphGrayScaled==flag)
			return;
		
		isAnaglyphGrayScaled=flag;
		resetAllDrawables();
	}
	
	public boolean isGrayScaled(){
		return projection==PROJECTION_ANAGLYPH && isAnaglyphGrayScaled();
	}
	
	private boolean isAnaglyphShutDownGreen = false;
	
	public boolean isAnaglyphShutDownGreen(){
		return isAnaglyphShutDownGreen;
	}

	public void setAnaglyphShutDownGreen(boolean flag){

		if (isAnaglyphShutDownGreen==flag)
			return;

		isAnaglyphShutDownGreen=flag;
		renderer.setWaitForUpdateClearColor();
	}
	
	public boolean isShutDownGreen(){
		return projection==PROJECTION_ANAGLYPH && isAnaglyphShutDownGreen();
	}
	
	private double eyeSepFactor = 0.03;

	public void setEyeSepFactor(double val){
		eyeSepFactor = val;
		renderer.updateAnaglyphValues();
	}
	
	public double getEyeSepFactor(){
		return eyeSepFactor;
	}


	public boolean isUnitAxesRatio() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int getViewID() {
		return App.VIEW_EUCLIDIAN3D;
	}
	
	private double cavAngle = 30;
	private double cavFactor = 0.5;
	
	public void setCav(){
		renderer.updateCavValues();
		setProjectionValues(PROJECTION_CAV);
		setDefault2DCursor();
	}
	
	public void setCavAngle(double angle){
		cavAngle = angle;
		renderer.updateCavValues();
	}
	
	public double getCavAngle(){
		return cavAngle;
	}
	
	public void setCavFactor(double factor){
		cavFactor = factor;
		renderer.updateCavValues();
	}
	
	public double getCavFactor(){
		return cavFactor;
	}

	

	//////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////






	@Override
	public boolean getShowAxis(int axis) {
		return this.axis[axis].isEuclidianVisible();
	}
	

	@Override
	public void replaceBoundObject(GeoNumeric num, GeoNumeric geoNumeric){
		
	}
	
	
	protected Color bgColor;
	
	public Color getBackground() {
		return bgColor;
	}

	public void setBackground(Color bgColor) {
		if (bgColor != null){
			this.bgColor = bgColor;
			if (renderer!=null)
				renderer.setWaitForUpdateClearColor();
		}
	}
	
	
	
	//////////////////////////////////////
	// PICKING
	
	
	public void addOneGeoToPick(){
		renderer.addOneGeoToPick();
	}
	
	public void removeOneGeoToPick(){
		renderer.removeOneGeoToPick();
	}
	
	
	//////////////////////////////////////
	// SOME LINKS WITH 2D VIEW



	@Override
	public int getFontSize() {
		
		return app.getFontSize();
	}

	

	
	@Override
	public void setBackground(geogebra.common.awt.GColor color) {
		setBackground(geogebra.awt.GColorD.getAwtColor(color));
		
	}
	@Override
	public geogebra.common.awt.GColor getBackgroundCommon() {
		return new geogebra.awt.GColorD(getBackground());
		
	}
	

	
	
	@Override
	public int getEuclidianViewNo() {
		// TODO Auto-generated method stub
		return App.VIEW_EUCLIDIAN3D;
	}

	
	

	
	//////////////////////////////////////////
	// ABSTRACTEUCLIDIANVIEW
	//////////////////////////////////////////
	@Override
	protected void drawAxes(geogebra.common.awt.GGraphics2D g2) {
		
		
	}
	
	//////////////////////////////////////////
	// EUCLIDIANVIEWND
	//////////////////////////////////////////
	
	@Override
	protected EuclidianStyleBarD newEuclidianStyleBar(){
		return new EuclidianStyleBar3D(this);
	}
	
	


	public Drawable newDrawButton( GeoButton geo) {
		return null;
	}
	
	public Drawable newDrawTextField(GeoTextField geo) {
		return null;
	}
	
	@Override
	protected void initCursor() {
		
	}
	
	
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		return NO_SUCH_PAGE;
	}

	@Override
	public void setShowAxis(boolean show) {
		setShowAxis(0,  show, false);
		setShowAxis(1,  show, false);
		setShowAxis(2,  show, true);
	}


	@Override
	public void setTransparentCursor() {

		setCursor(getApplication().getTransparentCursor());

	}


	@Override
	public void setEraserCursor() {
		App.warn("unimplemented");
		
	}

	@Override
	public geogebra.common.awt.GGraphics2D getGraphicsForPen() {
		return null;
	}

	@Override
	protected void doDrawPoints(GeoImage gi,
			List<geogebra.common.awt.GPoint> penPoints2,
			geogebra.common.awt.GColor penColor, int penLineStyle, int penSize) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void drawResetIcon(GGraphics2D g) {
		App.debug("unimplemented");
		
	}
	
}

