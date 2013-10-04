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

}
