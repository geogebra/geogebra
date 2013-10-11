package geogebra3D.euclidianInput3D;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.euclidian3D.opengl.Renderer;

/**
 * EuclidianView3D with controller using 3D input
 * @author mathieu
 *
 */
public class EuclidianViewInput3D extends EuclidianView3D{

	/**
	 * constructor
	 * @param ec euclidian controller
	 * @param settings settings
	 */
	public EuclidianViewInput3D(EuclidianController3D ec,
			EuclidianSettings settings) {
		super(ec, settings);
	}
	
	
	@Override
	public void drawMouseCursor(Renderer renderer1){
		
		//use a 3D mouse position
		Coords v = ((EuclidianControllerInput3D) getEuclidianController()).getMouse3DPosition();
		
		drawMouseCursor(renderer1, v);
		
	}

	public void setCoordSystemFromMouse3DMove(double dx, double dy, double dz, double rotX, double rotZ) {	
		/*
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
		*/
		
		
		Coords v = new Coords(dx,dy,dz,0);
		toSceneCoords3D(v);
		setXZero(XZeroOld+v.getX());
		setYZero(YZeroOld+v.getY());
		setZZero(ZZeroOld+v.getZ());
		
		//setRotXYinDegrees(rotX, rotZ);
		
		updateMatrix();
		setViewChangedByTranslate();
		setWaitForUpdate();
		
	}	
	
	@Override
	public void rememberOrigins(){
		XZeroOld = XZero;
		YZeroOld = YZero;
		ZZeroOld = ZZero;
	}
}
