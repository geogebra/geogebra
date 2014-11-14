package geogebra3D.euclidianInput3D;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3DCompanion;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import geogebra.common.kernel.Matrix.Coords;

/**
 * Euclidian controller creator for 3D controller with 3D input
 * @author mathieu
 *
 */
public class EuclidianControllerInput3DCompanion extends EuclidianController3DCompanion {

	/**
	 * constructor
	 * @param ec controller
	 */
	public EuclidianControllerInput3DCompanion(EuclidianController ec) {
		super(ec);
	}
	
	
	@Override
	protected GeoPoint3D createNewFreePoint(boolean complex){
		GeoPoint3D point3D = ((EuclidianView3D) ec.view).getCursor3D();	
		point3D.setPath(null);
		point3D.setRegion(null);
		
		Coords coords = ((EuclidianView3D) ec.view).getPickPoint(ec.mouseLoc).copyVector();
		((EuclidianView3D) ec.view).toSceneCoords3D(coords);
		checkPointCapturingXYThenZ(coords);
		point3D.setCoords(coords);
		
		return point3D;
	}
	

	@Override
	protected void movePoint(boolean repaint, AbstractEvent event){
		
		
		Coords v = new Coords(4);
		v.set(((EuclidianControllerInput3D) ec).mouse3DPosition.sub(((EuclidianControllerInput3D) ec).startMouse3DPosition));
		((EuclidianView3D) ec.view).toSceneCoords3D(v);
		
		
		Coords coords = ((EuclidianControllerInput3D) ec).movedGeoPointStartCoords.add(v);
		checkPointCapturingXYThenZ(coords);
		ec.movedGeoPoint.setCoords(coords, true);
		ec.movedGeoPoint.updateCascade();


		if (ec.movedGeoPoint.isGeoElement3D() && !ec.movedGeoPoint.hasPath() && !ec.movedGeoPoint.hasRegion()){
			//update point decorations
			((EuclidianView3D) ec.view).updatePointDecorations((GeoPoint3D) ec.movedGeoPoint);
		}

	}
	
	
	@Override
	protected void movePlane(boolean repaint, AbstractEvent event) {
		
		Coords v = new Coords(4);
		v.set(((EuclidianControllerInput3D) ec).mouse3DPosition.sub(((EuclidianControllerInput3D) ec).startMouse3DPosition));
		((EuclidianView3D) ec.view).toSceneCoords3D(v);
		
		((EuclidianControllerInput3D) ec).movedGeoPlane.setCoordSys(((EuclidianControllerInput3D) ec).movedGeoPlaneStartCoordSys);
		
		((EuclidianControllerInput3D) ec).movedGeoPlane.translate(v);
		
		((EuclidianControllerInput3D) ec).movedGeoPlane.rotate(((EuclidianControllerInput3D) ec).getCurrentRotMatrix());
		
		((EuclidianControllerInput3D) ec).movedGeoPlane.updateCascade();
		
//		Coords coords = ((EuclidianControllerInput3D) ec).movedGeoPointStartCoords.add(v);
//		checkPointCapturingXYThenZ(coords);
//		ec.movedGeoPoint.setCoords(coords, true);
//		ec.movedGeoPoint.updateCascade();
	}
	
	
	
	

}
