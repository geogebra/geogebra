package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Filter for algebra output formats.
 */
public interface AlgebraOutputFormatFilter {
	/**
	 * Determines whether the specific format is allowed for the given element by this filter.
	 * @param geoElement the element for which the format is evaluated
	 * @param outputFormat the format to be evaluated
	 * @return {@code true} if the given format is allowed
	 * for the given element, {@code false} otherwise
	 */
	boolean isAllowed(GeoElement geoElement, AlgebraOutputFormat outputFormat);
}
