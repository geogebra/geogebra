package geogebra3D.euclidianFor3D;

import geogebra.euclidian.EuclidianController;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;


public class EuclidianControllerFor3D extends EuclidianController {

	public EuclidianControllerFor3D(Kernel kernel) {
		super(kernel);
	}
	
	
	protected GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoPointND C){
		return kernel.getManager3D().Angle3D(null, A, B, C);
	}
	
	protected GeoElement[] translate(GeoElement geo, GeoVectorND vec){
		if (geo.isGeoElement3D() || ((GeoElement) vec).isGeoElement3D())
			return kernel.getManager3D().Translate3D(null, geo, vec);
		else
			return kernel.Translate(null, geo, (GeoVector) vec);
	}

}
