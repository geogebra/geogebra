package geogebra3D.euclidianForPlane;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.draw.DrawAngle;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPlaneND;
import geogebra.common.kernel.kernelND.ViewCreator;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.euclidian.EuclidianControllerD;
import geogebra.euclidian.EuclidianStyleBarD;
import geogebra.gui.layout.LayoutD;
import geogebra3D.App3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.euclidianFor3D.DrawAngleFor3D;
import geogebra3D.euclidianFor3D.EuclidianViewFor3D;
import geogebra3D.gui.layout.panels.EuclidianDockPanelForPlane;
import geogebra3D.settings.EuclidianSettingsForPlane;

import java.util.ArrayList;

/**
 * 2D view for plane.
 * 
 * @author matthieu
 *
 */
public class EuclidianViewForPlane extends EuclidianViewFor3D {

	private ViewCreator plane;
	
	private int id;

	private CoordMatrix4x4  transform;// = CoordMatrix4x4.IDENTITY;

	
	/**
	 * 
	 * @param ec controller
	 * @param plane plane creating this view
	 * @param settings euclidian settings
	 */
	public EuclidianViewForPlane(EuclidianControllerD ec, ViewCreator plane, EuclidianSettings settings) {
		super(ec, new boolean[]{ false, false }, false, 0, settings); //TODO euclidian settings
		
		//initView(true);
		setShowAxes(false, false);
		//showGrid(false);
		
		setPlane(plane);
		
		updateMatrix();
		updateCenterAndOrientationRegardingView();
	}
	
	@Override
	protected void setXYMinMaxForUpdateSize() {
		
		//keep center of the view at center of the frame
		
		double c = (xmin+xmax)/2;
		double l = getWidth() * getInvXscale()/2;
		xmin = c-l;
		xmax = c+l;
		
		c = (ymin+ymax)/2;
		l = getHeight() * getInvYscale()/2;
		ymax = c+l;
		ymin = c-l;
	}
	
	/**
	 * set the plane creator
	 * @param plane plane creator
	 */
	public void setPlane(ViewCreator plane){
		this.plane = plane;
	}
	
	/**
	 * @return creator of the view
	 * 
	 */
	public ViewCreator getPlane(){
		return plane;
	}
	
	
	@Override
	protected DrawAngle newDrawAngle(GeoAngle geo){
		return new DrawAngleFor3D(this, geo);
	}
	
	@Override
	public boolean isDefault2D(){
		return false;
	}
	
	@Override
	public void updateForPlane(){
		updateMatrix();
		updateAllDrawables(true);
	}
	
	@Override
	public boolean isVisibleInThisView(GeoElement geo){

		// prevent not implemented type to be displayed (TODO remove)
		switch (geo.getGeoClassType()){
		case POINT:
		case POINT3D:
		case SEGMENT:
		case SEGMENT3D:
		case LINE:
		case LINE3D:
		case RAY:
		case RAY3D:
		case VECTOR:
		case VECTOR3D:
		case POLYGON:
		case POLYGON3D:
		case CONIC:
		case CONIC3D:
		case CONICSECTION:
		case ANGLE:
		case ANGLE3D:
		case TEXT:
			return geo.isVisibleInView3D();
		default:
			return false;
		}
		
	}
	
	@Override
	public void attachView() {
		kernel.attach(this);
	}

	/**
	 * add all existing geos to this view
	 */
	public void addExistingGeos(){
		kernel.notifyAddAll(this);
	}	
	
	@Override
	public Coords getCoordsForView(Coords coords){
		return coords.projectPlane(getMatrix())[1];
	}
	
	/**
	 * @param coords in view plane
	 * @return coords in 3D world
	 */
	public Coords getCoordsFromView(Coords coords){
		return getMatrix().mul(coords);
	}
	
	/**
	 * @param x x coord in view plane
	 * @param y y coord in view plane
	 * @return coords in 3D world
	 */
	public Coords getCoordsFromView(double x, double y){
		return getCoordsFromView(new Coords(x,y,0,1));
	}
	
	@Override
	public CoordMatrix getMatrix(){
		
		return transformedMatrix;
		
		/*
		if (reverse==1)
			return planeMatrix;
		else
			return reverseMatrix;
			*/
		
		
		//return plane.getCoordSys().getMatrixOrthonormal();
		//return plane.getCoordSys().getDrawingMatrix();
	}
	
	@Override
	public CoordMatrix getInverseMatrix() {
		return inverseTransformedMatrix;
	}
	
	private CoordMatrix4x4 planeMatrix, transformedMatrix;
	private CoordMatrix inverseTransformedMatrix;
	
	/**
	 * update the matrix transformation
	 */
	public void updateMatrix(){
		
		if (!plane.isDefined()){
			//force plane matrix for Drawables creation
			planeMatrix = CoordMatrix4x4.IDENTITY;
			transformedMatrix = CoordMatrix4x4.IDENTITY;
			inverseTransformedMatrix = CoordMatrix4x4.IDENTITY;
			return;
		}
		
		if(transform==null) //transform has not already been set
			transform = CoordMatrix4x4.IDENTITY;
		
		//planeMatrix = plane.getCoordSys().getMatrixOrthonormal();	
		planeMatrix = plane.getCoordSys().getDrawingMatrix();	

		transformedMatrix = planeMatrix.mul(transform);//transform.mul(planeMatrix);	
		inverseTransformedMatrix = transformedMatrix.inverse();
		
	}
	
	
	


	/**
	 * update orientation of the view regarding 3D view
	 */
	public void updateCenterAndOrientationRegardingView(){
		
		setTransformRegardingView();
		updateMatrix();
				
		
		EuclidianView3D view3D = ((App3D) app).getEuclidianView3D();
		
		// coords of the bounding box center in the 3D view
		Coords c = new Coords(-view3D.getXZero(),-view3D.getYZero(),-view3D.getZZero(),1);
		
		// project it in this view coord sys
		Coords p = c.projectPlane(getMatrix())[1];
		
		// take this projection for center
		int x = toScreenCoordX(p.getX());
		int y = toScreenCoordY(p.getY());


		/*
		App.debug(getXscale()+","+((App3D) app).getEuclidianView3D().getXscale());
		double scale = ((App3D) app).getEuclidianView3D().getXscale();
		setCoordSystem(getWidth()/2-x+getxZero(), getHeight()/2-y+getyZero(), scale, scale);
		*/
		
		setCoordSystem(getWidth()/2-x+getxZero(), getHeight()/2-y+getyZero(), getXscale(), getYscale());
	
	
	}

	
	private int transformMirror;
	private int transformRotate;

	
	/**
	 * set the transform matrix regarding view direction
	 */
	public void setTransformRegardingView(){
		
		Coords directionView3D = ((App3D) app).getEuclidianView3D().getViewDirection();
		CoordMatrix toScreenMatrix = ((App3D) app).getEuclidianView3D().getToScreenMatrix();
		
		//front or back view
		double p = plane.getCoordSys().getNormal().dotproduct(directionView3D);
		if (p<=0){
			transform = CoordMatrix4x4.IDENTITY;
			transformMirror = 1;
		}else{
			transform = CoordMatrix4x4.MIRROR_Y;
			transformMirror = -1;
		}

		//Application.debug("transform=\n"+transform);
		
		//CoordMatrix m = toScreenMatrix.mul(planeMatrix.mul(transform));
		CoordMatrix m = toScreenMatrix.mul(planeMatrix);
		
		//App.debug("m=\n"+m);
		
		double vXx = m.get(1, 1);
		double vXy = m.get(2, 1);
		double vYx = m.get(1, 2);
		double vYy = m.get(2, 2);

		transformRotate = 0;
		//is vX vertical and vY horizontal ?
		if (Math.abs(vXy)>Math.abs(vXx) && Math.abs(vYx)>Math.abs(vYy)){			
			if (vYx*transformMirror>=0){
				transform = CoordMatrix4x4.ROTATION_OZ_90.mul(transform);
				transformRotate = 90;
			}else{
				transform = CoordMatrix4x4.ROTATION_OZ_M90.mul(transform);
				transformRotate = -90;
			}
		//check vX direction
		}else if (vXx*transformMirror<0){
			transform = CoordMatrix4x4.MIRROR_O.mul(transform);
			transformRotate = 180;
		}


		updateMatrix();
		
		
		//TODO only if new matrix != old matrix
		updateAllDrawables(true);	
	}
	
	/**
	 * set transform from values
	 */
	public void setTransform(){

		if (transformMirror==1)
			transform = CoordMatrix4x4.IDENTITY;
		else
			transform = CoordMatrix4x4.MIRROR_Y;
		


		if (transformRotate == 90)
			transform = CoordMatrix4x4.ROTATION_OZ_90.mul(transform);
		else if (transformRotate == -90)
			transform = CoordMatrix4x4.ROTATION_OZ_M90.mul(transform);
		else if (transformRotate == 180)
			transform = CoordMatrix4x4.MIRROR_O.mul(transform);

	}
	
	
	@Override
	public String getFromPlaneString(){
		if (plane==null)
			return "";
		return plane.toGeoElement().getLabel(StringTemplate.defaultTemplate);
	}

	@Override
	public String getTranslatedFromPlaneString(){
		if (plane == null)
			return "";
		
		if (plane instanceof GeoPlaneND) {
			return getApplication().getLocalization().getPlain("PlaneA",((GeoElement) plane).getLabel(StringTemplate.defaultTemplate));
		}
		return getApplication().getLocalization().getPlain("PlaneFromA",((GeoElement) plane).getLabel(StringTemplate.defaultTemplate));
	}
	
	public GeoCoordSys2D getGeoElement(){
		return plane;
	}
	
	@Override
	public GeoPlaneND getPlaneContaining(){
		if (plane instanceof GeoPlaneND) {
			return (GeoPlaneND) plane;
		} 
		return kernel.getManager3D().Plane3D(plane);
		
	}
	
	@Override
	public GeoDirectionND getDirection(){
		return plane;
	}
	
	@Override
	public boolean hasForParent(GeoElement geo){
		return geo.isParentOf((GeoElement) plane);
	}

	@Override
	public boolean isMoveable(GeoElement geo){
		if (hasForParent(geo)) {
			return false;
		}
		return geo.isMoveable();
	}	

	@Override
	public ArrayList<GeoPoint> getFreeInputPoints(AlgoElement algoParent){
		ArrayList<GeoPoint> list = algoParent.getFreeInputPoints();
		ArrayList<GeoPoint> ret = new ArrayList<GeoPoint>();	
		for (GeoPoint p : list)
			if (!hasForParent(p))
				ret.add(p);
		return ret;
	}	
	
	private EuclidianDockPanelForPlane panel;
	
	/**
	 * set the dock panel of the view
	 * @param panel dock panel containing
	 */
	public void setDockPanel(EuclidianDockPanelForPlane panel){
		this.panel=panel;
		this.id=panel.getViewId();
	}
	
	/**
	 * 
	 * @return the id of the view
	 */
	public int getId(){
		return id;
	}
	
	
	/**
	 * remove the view (when creator is removed)
	 */
	public void doRemove(){
		removeFromGuiAndKernel();
		((App3D) app).removeEuclidianViewForPlaneFromList(this);
	}
	
	/**
	 * remove panel from gui and view from kernel
	 */
	public void removeFromGuiAndKernel(){
		panel.closePanel();
		((LayoutD) app.getGuiManager().getLayout()).getDockManager().unRegisterPanel(panel);
		kernel.detach(this);
	}
	
	
	@Override
	protected void getXMLid(StringBuilder sbxml){

		sbxml.append("\t<viewId ");
		sbxml.append("plane=\"");
		sbxml.append(((GeoElement) plane).getLabelSimple());
		sbxml.append("\"");
		sbxml.append("/>\n");

	}
	
	
	@Override
	public void getXML(StringBuilder sbxml, boolean asPreference) {
		startXML(sbxml, asPreference);
		
		// transform
		sbxml.append("\t<transformForPlane ");
		sbxml.append("mirror=\"");
		sbxml.append(transformMirror==-1);
		sbxml.append("\"");
		sbxml.append(" rotate=\"");
		sbxml.append(transformRotate);
		sbxml.append("\"");		
		sbxml.append("/>\n");
		
		endXML(sbxml);
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		super.settingsChanged(settings);
		
		EuclidianSettingsForPlane evs = (EuclidianSettingsForPlane) settings;
		
		//transform
		transformMirror = 1;		
		if(evs.getMirror())
			transformMirror=-1;
				

		transformRotate = evs.getRotate();

		setTransform();
		
	}
	
	
	@Override
	protected EuclidianStyleBarD newEuclidianStyleBar(){
		return new EuclidianStyleBarForPlane(this);
	}
	
	
	@Override
	public void paint(geogebra.common.awt.GGraphics2D g2) {		
		if (!plane.isDefined()){
			//draws the view in gray
			g2.setColor(GColor.LIGHT_GRAY);
			g2.fillRect(0, 0, getWidth(), getHeight());
			return;
		}
		
		
		super.paint(g2);
	}
	
	@Override
	public void showGrid(boolean show) {
		EuclidianSettings settings = app.getSettings().getEuclidianForPlane(getFromPlaneString());
		if (settings!=null)
			settings.setShowGridSetting(show);
		super.showGrid(show);
	}
	
}
