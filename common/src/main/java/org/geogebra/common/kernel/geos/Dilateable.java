package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;
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
