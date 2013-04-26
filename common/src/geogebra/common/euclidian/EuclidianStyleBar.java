package geogebra.common.euclidian;

import geogebra.common.kernel.geos.GeoElement;


/**
 * Style bar for Euclidian view
 */
public interface EuclidianStyleBar {
	

	/** tooltip x location for buttons */
	public static final int TOOLTIP_LOCATION_X = 0;
	/** tooltip y location for buttons */
	public static final int TOOLTIP_LOCATION_Y = -25;

	/**
	 * @param mode euclidian view mode
	 */
	void setMode(int mode);

	/**
	 * Update tooltips
	 */
	void setLabels();

	/**
	 * Restore default properties
	 */
	void restoreDefaultGeo();

	/**
	 * Updates the state of the stylebar buttons and the defaultGeo field.
	 */
	void updateStyleBar();
	
	/**
	 * update the style bar if the geo is part of the active geo list
	 * @param geo geo
	 */
	public void updateVisualStyle(GeoElement geo);

}
