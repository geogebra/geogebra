package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.plugin.GeoClass;

public class GeoSpline extends GeoList {

	public GeoSpline(Construction c) {
		super(c);
	}

	@Override
	public GeoClass getGeoClassType(){
		return GeoClass.SPLINE;
	}

	public boolean isGeoList(){
		return false;
	}
}
