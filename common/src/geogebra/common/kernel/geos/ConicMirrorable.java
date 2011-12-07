package geogebra.common.kernel.geos;

import geogebra.common.kernel.geos.GeoConicInterface;


/**
 * Represents geos that can be mirrored atline or point
 * 
 */
public interface ConicMirrorable {
	/**
	 * Miror at circle
	 * @param c mirror circle
	 */
	public void mirror(GeoConicInterface c);
	
}
