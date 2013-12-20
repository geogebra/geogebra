package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;

/**
 * Net for a polyhedron
 * @author Vincent
 *
 */
public class GeoPolyhedronNet extends GeoPolyhedron {

	/**
	 * @param c  construction
	 */
	public GeoPolyhedronNet(Construction c) {
		super(c);
	}

	@Override
	public String getTypeString() {
		return "Net";
	}
	
	@Override
	public boolean isGeoPolyhedron() {
		return false;
	}
	
}


