package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoAngle;

final public class GeoAngle3D extends GeoAngle {

	public GeoAngle3D(Construction c) {
		super(c);
		//setAngleStyle(ANGLE_ISNOTREFLEX);
	}
	

	final public int getGeoClassType() {
		return GEO_CLASS_ANGLE3D;
	}

	
	final public boolean hasOrientation(){
		return false; //no specific orientation
	}
	
	
	
	

}
