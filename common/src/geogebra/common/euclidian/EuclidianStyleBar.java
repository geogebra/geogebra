package geogebra.common.euclidian;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

import java.util.ArrayList;

/**
 * Style bar for Euclidian view
 */
public interface EuclidianStyleBar {

	/**
	 * Apply selected style on given geos geos
	 * @param selectedGeos geos
	 */
	void applyVisualStyle(ArrayList<GeoElement> selectedGeos);

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

	int getPointCaptureSelectedIndex();

}
