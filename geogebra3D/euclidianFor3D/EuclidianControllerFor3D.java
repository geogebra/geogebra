package geogebra3D.euclidianFor3D;

import geogebra.euclidian.EuclidianController;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoPointND;


public class EuclidianControllerFor3D extends EuclidianController {

	public EuclidianControllerFor3D(Kernel kernel) {
		super(kernel);
	}
	
	
	protected GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoPointND C){
		return kernel.getManager3D().Angle3D(null, A, B, C);
	}

}
