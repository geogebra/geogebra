package org.geogebra.common.kernel.geos;

/**
 * @author Thomas
 *
 */
public interface HasExtendedAV {

	/**
	 * @return whether checkbox / slider is visible in AV
	 */
	boolean isShowingExtendedAV();

	/**
	 * @param showExtendedAV
	 *            set whether slider / checkbox should be shown
	 */
	void setShowExtendedAV(boolean showExtendedAV);
}
