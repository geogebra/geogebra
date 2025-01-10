package org.geogebra.common.kernel.geos;

/**
 * @author Thomas
 *
 */
public interface HasExtendedAV {

	/**
	 * @return whether checkbox / slider is visible in AV
	 */
	boolean isAVSliderOrCheckboxVisible();

	/**
	 * @param showSliderOrCheckbox
	 *            set whether slider / checkbox should be shown
	 */
	void setAVSliderOrCheckboxVisible(boolean showSliderOrCheckbox);
}
