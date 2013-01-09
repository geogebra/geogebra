package geogebra.common.kernel.geos;

import geogebra.common.kernel.kernelND.GeoElementND;
/**
 * Interface for single variable inequality or list of those
 */
public interface InequalityProperties extends GeoElementND{
	/**
	 * For inequalities.
	 * 
	 * @param showOnAxis
	 *            true iff should be drawn on x-Axis only
	 */
	public void setShowOnAxis(boolean showOnAxis);
	
	/**
	 * For inequalities.
	 * 
	 * @return true iff should be drawn on x-Axis only
	 */
	public boolean showOnAxis();
}
