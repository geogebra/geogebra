package geogebra.common.geogebra3D.kernel3D.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.plugin.GeoClass;

final public class GeoAngle3D extends GeoAngle {

	public GeoAngle3D(Construction c) {
		super(c);
		hasOrientation = false;
	}
	
	@Override
	final public GeoClass getGeoClassType() {
		return GeoClass.ANGLE3D;
	}
	
	private boolean hasOrientation;
	
	
	@Override
	final public boolean hasOrientation(){
		return hasOrientation; //no specific orientation
	}
	
	/**
	 * set if it has orientation
	 * @param flag flag
	 */
	public void setHasOrientation(boolean flag){
		hasOrientation = flag;
	}

}
