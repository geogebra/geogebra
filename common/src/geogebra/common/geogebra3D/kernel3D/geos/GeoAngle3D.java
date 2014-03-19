package geogebra.common.geogebra3D.kernel3D.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
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

	
	/**
	 * create a new GeoAngle3D and set its interval (e.g. between 0° and 180°) as default angle
	 * @param cons construction
	 * @return new GeoAngle
	 */
	static public final GeoAngle3D newAngle3DWithDefaultInterval(Construction cons) {
		GeoAngle3D ret = new GeoAngle3D(cons);
		//set the angle interval
		ret.setAngleStyle(((GeoAngle) cons.getConstructionDefaults().getDefaultGeo(ConstructionDefaults.DEFAULT_ANGLE)).getAngleStyle());
		ret.setHasOrientation(true);
		return ret;
	}
}
