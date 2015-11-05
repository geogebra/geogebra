package org.geogebra.common.kernel.geos;

/**
 * @author Thomas
 *
 */
public interface HasExtendedAV {
	/**
	 * @return whether checkbox / slider is visible in AV
	 */
	public abstract boolean isShowingExtendedAV();

	/**
	 * @param showExtendedAV
	 *            set whether slider / checkbox should be shown
	 */
	public abstract void setShowExtendedAV(boolean showExtendedAV);
}
