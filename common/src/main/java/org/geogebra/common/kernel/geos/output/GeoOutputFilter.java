package org.geogebra.common.kernel.geos.output;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * This class is responsible for filtering text outputs, like captions,
 * so that these don't contain sensitive information,
 * such as the equation of a function created by a tool.
 */
public interface GeoOutputFilter {

	/**
	 * Decides whether the caption of the parameter should be filtered or not.
	 *
	 * @param element the element whose caption should maybe be filtered
	 * @return true if the user shouldn't see sensitive info in the caption, otherwise false
	 */
	boolean shouldFilterCaption(GeoElement element);

	/**
	 * Removes all sensitive info from the caption.
	 *
	 * @param element the element whose caption is filtered
	 * @return the caption text without any sensitive info
	 */
	String filterCaption(GeoElement element);
}
