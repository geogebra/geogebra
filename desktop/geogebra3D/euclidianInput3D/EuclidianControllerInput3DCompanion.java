package geogebra3D.euclidianInput3D;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3DCompanion;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.Matrix.Quaternion;
import geogebra.common.main.App;
import geogebra3D.euclidianInput3D.EuclidianViewInput3D.StationaryCoords;

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
		
		if (((EuclidianControllerInput3D) ec).input3D.currentlyUseMouse2D()){
			return super.createNewFreePoint(complex);
		}
		
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
		
		if (((EuclidianControllerInput3D) ec).input3D.currentlyUseMouse2D()){
			super.movePoint(repaint, event);
		}else{
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

			if (((EuclidianControllerInput3D) ec).input3D.getLeftButton()){
				long time = System.currentTimeMillis();
				StationaryCoords stationaryCoords = ((EuclidianViewInput3D) ec.view).getStationaryCoords();
				stationaryCoords.setCoords(ec.movedGeoPoint.getInhomCoordsInD3(), time);
				if (stationaryCoords.hasLongDelay(time)){
					((EuclidianControllerInput3D) ec).input3D.setLeftButtonPressed(false);
				}
			}
		}

	}
	
	
	
	
	
	private StationaryQuaternion stationaryQuaternion = new StationaryQuaternion();
	
	private class StationaryQuaternion {
		
		private Quaternion startCoords = new Quaternion();
		private long startTime;
		
		public StationaryQuaternion(){
			startCoords.setUndefined();
		}
		
		public void setQuaternion(Quaternion q, long time){
			
			if (startCoords.isDefined()){
				double distance = startCoords.distance(q);
				//App.debug("\n -- "+(distance * ((EuclidianView3D) ec.view).getScale()));
				if (distance > 0.05){ // angle < 25.8°
					startCoords.set(q);
					startTime = time;
					//App.debug("\n -- startCoords =\n"+startCoords);
				}else{
					//App.debug("\n -- same coords "+(time-startTime));
				}
			}else{
				startCoords.set(q);
				startTime = time;
				//App.debug("\n -- startCoords =\n"+startCoords);
			}
		}
		
		
		/**
		 * 
		 * @param time current time
		 * @return true if hit was long enough to process left release
		 */
		public boolean hasLongDelay(long time){
			
			if (startCoords.isDefined()){
				int delay = (int) ((time-startTime) /100);
				String s = "";
				for (int i = 0 ; i < 10 - delay ; i++){
					s+="=";
				}
				for (int i = 10 - delay ; i <= 10 ; i++){
					s+=" ";
				}
				s+="|";
				App.error("\n rot delay : "+s);
				if ((time-startTime) > 1000){
					startCoords.setUndefined(); // consume event
					return true;
				}
			}
			
			return false;
		}
	}
	
	
	
	@Override
	protected void movePlane(boolean repaint, AbstractEvent event) {

		if (((EuclidianControllerInput3D) ec).input3D.currentlyUseMouse2D()){
			super.movePlane(repaint, event);
		}else{
			Coords v = new Coords(4);
			v.set(((EuclidianControllerInput3D) ec).mouse3DPosition.sub(((EuclidianControllerInput3D) ec).startMouse3DPosition));
			((EuclidianView3D) ec.view).toSceneCoords3D(v);

			((EuclidianControllerInput3D) ec).movedGeoPlane.setCoordSys(((EuclidianControllerInput3D) ec).movedGeoPlaneStartCoordSys);

			((EuclidianControllerInput3D) ec).calcCurrentRot();
			((EuclidianControllerInput3D) ec).movedGeoPlane.rotate(
					((EuclidianControllerInput3D) ec).getCurrentRotMatrix(),
					((EuclidianControllerInput3D) ec).movedGeoPointStartCoords
					);

			((EuclidianControllerInput3D) ec).movedGeoPlane.translate(v);

			((EuclidianControllerInput3D) ec).movedGeoPlane.updateCascade();
			
			/*
			if (((EuclidianControllerInput3D) ec).input3D.getLeftButton()){
				long time = System.currentTimeMillis();
				stationaryCoords.setCoords(v, time);
				stationaryQuaternion.setQuaternion(
						((EuclidianControllerInput3D) ec).getCurrentRotQuaternion(), 
						time);
				if (stationaryCoords.hasLongDelay(time) && stationaryQuaternion.hasLongDelay(time)){
					((EuclidianControllerInput3D) ec).input3D.setLeftButtonPressed(false);
				}
			}
			*/
		}
	}
	
	
	
	

}
