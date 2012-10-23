package geogebra3D.euclidianForPlane;

import geogebra.common.euclidian.draw.DrawAngle;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPlaneND;
import geogebra.common.main.App;
import geogebra.euclidian.EuclidianControllerD;
import geogebra3D.euclidianFor3D.DrawAngleFor3D;
import geogebra3D.euclidianFor3D.EuclidianViewFor3D;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * 2D view for plane.
 * 
 * @author matthieu
 *
 */
public class EuclidianViewForPlane extends EuclidianViewFor3D {

	private GeoCoordSys2D plane;
	
	private int id;


	/**
	 * 
	 * @param ec controller
	 */
	public EuclidianViewForPlane(EuclidianControllerD ec) {
		super(ec, new boolean[]{ true, true }, true, 1); //TODO ev id
		
		initView(true);
		setShowAxes(false, true);
		showGrid(false);
	}
	
	/**
	 * 
	 * @param ec controller
	 * @param plane plane creating this view
	 */
	public EuclidianViewForPlane(EuclidianControllerD ec, GeoCoordSys2D plane) {
		this(ec);
		
		setPlane(plane);

		updateMatrix();
			
	}
	
	/**
	 * set the plane creator
	 * @param plane plane creator
	 */
	public void setPlane(GeoCoordSys2D plane){
		this.plane = plane;
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
		case ANGLE:
		case ANGLE3D:
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
	//private CoordMatrix4x4 reverseMatrix;
	private CoordMatrix4x4 transform = CoordMatrix4x4.IDENTITY;
	
	/**
	 * update the matrix transformation
	 */
	public void updateMatrix(){
		//planeMatrix = plane.getCoordSys().getMatrixOrthonormal();	
		planeMatrix = plane.getCoordSys().getDrawingMatrix();	
		
		transformedMatrix = planeMatrix.mul(transform);//transform.mul(planeMatrix);	
		inverseTransformedMatrix = transformedMatrix.inverse();
		
	}
	
	/**
	 * set the transform matrix regarding view direction
	 * @param directionView3D 3D view direction
	 * @param toScreenMatrix matrix from real 3D world to screen world
	 */
	public void setTransform(Coords directionView3D, CoordMatrix toScreenMatrix){
		
		//front or back view
		double p = plane.getCoordSys().getNormal().dotproduct(directionView3D);
		double reverse = 1;
		if (p<0)
			transform = CoordMatrix4x4.IDENTITY;
		else if (p>0){
			transform = CoordMatrix4x4.MIRROR_Y;
			reverse = -1;
		}

		//Application.debug("transform=\n"+transform);
		
		//CoordMatrix m = toScreenMatrix.mul(planeMatrix.mul(transform));
		CoordMatrix m = toScreenMatrix.mul(planeMatrix);
		
		//Application.debug("m=\n"+m);
		
		double vXx = m.get(1, 1);
		double vXy = m.get(2, 1);
		double vYx = m.get(1, 2);
		m.get(2, 2);
		
		//Application.debug("vx="+vXx+","+vXy+"\nvy="+vYx+","+vYy);
		
		//if (vXx*vXx+vXy*vXy>vYx*vYx+vYy*vYy){//vX is more important
			if (Math.abs(vXy)>Math.abs(vXx)){			
				if (vYx*reverse>=0)
					transform = CoordMatrix4x4.ROTATION_OZ_90.mul(transform);
				else
					transform = CoordMatrix4x4.ROTATION_OZ_M90.mul(transform);
			}
		/*}else{//vY is more important
			if (Math.abs(vYx)>Math.abs(vYy))
				transform = CoordMatrix4x4.ROTATION_OZ_90.mul(transform);			
		}*/
		
		/*
		if (vx.getX()>=0){
			if (vy.getY()>=0){
				if (vx.getX()<vx.getY())
					transform = TRANSFORM_ROT_M90;
				else if (vx.getX()<-vx.getY())
					transform = TRANSFORM_ROT_90;
				else
					transform = TRANSFORM_IDENTITY;
			}else{
				//if (vx.getX()>-vx.getY())
					transform = TRANSFORM_MIRROR_X;
				//else
				//	transform = TRANSFORM_ROT_90;
			}
		}else{
			if (vy.getY()>=0)
				transform = TRANSFORM_MIRROR_Y;
			else{
				if (-vx.getX()>vx.getY())
					transform = TRANSFORM_MIRROR_O;
				else
					transform = TRANSFORM_ROT_M90;
			}
		}
		*/
		
		updateMatrix();
		
		
		//TODO only if new matrix != old matrix
		updateAllDrawables(true);	
	}
	
	@Override
	public geogebra.common.awt.GAffineTransform getTransform(GeoConicND conic, Coords M, Coords[] ev){

		//use already computed for this view middlepoint M and eigen vecs ev
		AffineTransform transform = new AffineTransform();			
		transform.setTransform(
				ev[0].getX(),
				ev[0].getY(),
				ev[1].getX(),
				ev[1].getY(),
				M.getX(),
				M.getY());

		return new geogebra.awt.GAffineTransformD(transform);
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
			return getApplication().getPlain("PlaneA",((GeoElement) plane).getLabel(StringTemplate.defaultTemplate));
		}
		return getApplication().getPlain("PlaneFromA",((GeoElement) plane).getLabel(StringTemplate.defaultTemplate));
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
	
	/**
	 * set the id of the view
	 * @param id id
	 */
	public void setId(int id){
		this.id=id;
	}
	
	/**
	 * 
	 * @return the id of the view
	 */
	public int getId(){
		return id;
	}
	
}
