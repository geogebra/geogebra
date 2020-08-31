package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Geo caption can be dynamic,
 * ie GeoText if this interface is implemented.
 *
 * @author laszlo
 */
public interface HasDynamicCaption extends GeoElementND {

	/**
	 *
	 * @return if dynamic caption is set (as GeoText).
	 */
	boolean hasDynamicCaption();

	/**
	 *
	 * @return the GeoText as the dynamic caption
	 */
	GeoText getDynamicCaption();

	/**
	 * Sets GeoText as dynamic caption.
	 * @param caption to set.
	 */
	void setDynamicCaption(GeoText caption);

	/**
	 * Clears dynamic caption but does not disable it.
	 */
	void clearDynamicCaption();

	/**
	 * Removes dynamic capiton completely.
	 */
	void removeDynamicCaption();
}
