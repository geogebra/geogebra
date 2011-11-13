package geogebra3D.euclidianForPlane;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import geogebra.euclidian.EuclidianController;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Matrix.CoordMatrix;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoPlaneND;

import geogebra3D.euclidianFor3D.EuclidianViewFor3D;

/**
 * 2D view for plane.
 * 
 * @author matthieu
 *
 */
public class EuclidianViewForPlane extends EuclidianViewFor3D {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GeoCoordSys2D plane;

	/**
	 * 
	 * @param ec
	 * @param plane 
	 */
	public EuclidianViewForPlane(EuclidianController ec, GeoCoordSys2D plane) {
		super(ec, new boolean[]{ true, true }, true, 1); //TODO ev id
		
		this.plane = plane;
		updateMatrix();
		
		
		
		//TODO
		initView(true);
		setShowAxes(false, true);
		showGrid(false);
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
		case GeoElement.GEO_CLASS_POINT:
		case GeoElement.GEO_CLASS_POINT3D:
		case GeoElement.GEO_CLASS_SEGMENT:
		case GeoElement.GEO_CLASS_SEGMENT3D:
		case GeoElement.GEO_CLASS_LINE:
		case GeoElement.GEO_CLASS_LINE3D:
		case GeoElement.GEO_CLASS_RAY:
		case GeoElement.GEO_CLASS_RAY3D:
		case GeoElement.GEO_CLASS_VECTOR:
		case GeoElement.GEO_CLASS_VECTOR3D:
		case GeoElement.GEO_CLASS_POLYGON:
		case GeoElement.GEO_CLASS_POLYGON3D:
		case GeoElement.GEO_CLASS_CONIC:
		case GeoElement.GEO_CLASS_CONIC3D:
			break;
		default:
			return false;
		}
		
		//Application.debug(geo+": "+geo.isVisibleInView3D());
		
		return geo.isVisibleInView3D();
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
	
	private CoordMatrix4x4 planeMatrix, reverseMatrix, transformedMatrix;
	private CoordMatrix4x4 transform = CoordMatrix4x4.IDENTITY;
	
	public void updateMatrix(){
		planeMatrix = plane.getCoordSys().getMatrixOrthonormal();	
		
		transformedMatrix = planeMatrix.mul(transform);//transform.mul(planeMatrix);
		
	}
	
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
		double vYy = m.get(2, 2);
		
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
	public AffineTransform getTransform(GeoConicND conic, Coords M, Coords[] ev){

		//use already computed for this view middlepoint M and eigen vecs ev
		AffineTransform transform = new AffineTransform();			
		transform.setTransform(
				ev[0].getX(),
				ev[0].getY(),
				ev[1].getX(),
				ev[1].getY(),
				M.getX(),
				M.getY());

		return transform;
	}
	
	@Override
	public String getFromPlaneString(){
		return ((GeoElement) plane).getLabel();
	}

	@Override
	public String getTranslatedFromPlaneString(){
		if (plane instanceof GeoPlaneND) {
			return app.getPlain("PlaneA",((GeoElement) plane).getLabel());
		} else {
			return app.getPlain("PlaneFromA",((GeoElement) plane).getLabel());
		}
	}
	
	public GeoCoordSys2D getGeoElement(){
		return plane;
	}
	
	@Override
	public GeoPlaneND getPlaneContaining(){
		if (plane instanceof GeoPlaneND) {
			return (GeoPlaneND) plane;
		} else {
			return kernel.getManager3D().Plane3D(plane);
		}
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
		} else {
			return geo.isMoveable();
		}
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
	
}
