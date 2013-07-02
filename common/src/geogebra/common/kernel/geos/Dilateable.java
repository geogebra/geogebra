package geogebra.common.kernel.geos;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.kernelND.GeoElementND;
/**
 * Interface for geos which can be dilated from point
 *
 */
public interface Dilateable extends GeoElementND{
	/**
	 * Dilates the element
	 * @param r ratio
	 * @param S point
	 */
	public void dilate(NumberValue r, Coords S);
}
