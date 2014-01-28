package geogebra.common.geogebra3D.kernel3D.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.plugin.GeoClass;

final public class GeoAngle3D extends GeoAngle {

	public GeoAngle3D(Construction c) {
		super(c);
		//setAngleStyle(ANGLE_ISNOTREFLEX);
	}
	
	@Override
	final public GeoClass getGeoClassType() {
		return GeoClass.ANGLE3D;
	}
	
	@Override
	final public boolean hasOrientation(){
		return false; //no specific orientation
	}

}
