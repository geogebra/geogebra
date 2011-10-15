package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;

final public class GeoAngle3D extends GeoAngle {

	public GeoAngle3D(Construction c) {
		super(c);
	}
	

	final public int getGeoClassType() {
		return GEO_CLASS_ANGLE_3D;
	}

	
	final public boolean hasOrientation(){
		return false; //no specific orientation
	}
	
	

}
