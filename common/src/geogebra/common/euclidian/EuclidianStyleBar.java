package geogebra.common.euclidian;

import geogebra.common.kernel.geos.GeoElement;


/**
 * Style bar for Euclidian view
 */
public interface EuclidianStyleBar {

	/**
	 * Update capture button
	 * @param mode euclidian view mode
	 */
	public void updateButtonPointCapture(int mode);

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

	/**
	 * @return index of selected point capturing mode
	 */
	int getPointCaptureSelectedIndex();

}
