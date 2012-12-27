package geogebra.common.kernel.geos;

import geogebra.common.kernel.kernelND.GeoElementND;

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
