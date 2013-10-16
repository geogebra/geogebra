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

	
	@Override
	public boolean isPolarized(){
		return true;
	}
	
	@Override
	public float getScreenZOffsetFactor(){
		return 1f;
	}
	
	/**
	 * set the coord system regarding 3D mouse move
	 * @param dx relative mouse x move
	 * @param dy relative mouse y move
	 * @param dz relative mouse z move
	 * @param rotX relative mouse rotate around x (screen)
	 * @param rotZ relative mouse rotate around z (view)
	 */
	public void setCoordSystemFromMouse3DMove(double dx, double dy, double dz, double rotX, double rotZ) {	
		
		// translation
		Coords v = new Coords(dx,dy,dz,0);
		toSceneCoords3D(v);
		setXZero(XZeroOld+v.getX());
		setYZero(YZeroOld+v.getY());
		setZZero(ZZeroOld+v.getZ());		
		
		// rotation
		setRotXYinDegrees(aOld+rotX, bOld+rotZ);
		
		
		// update the view
		updateMatrix();
		setViewChangedByTranslate();
		setViewChangedByRotate();
		setWaitForUpdate();
		
		
	}	
	
	/*
	@Override
	public void rememberOrigins(){
		XZeroOld = XZero;
		YZeroOld = YZero;
		ZZeroOld = ZZero;
	}
	*/
}
