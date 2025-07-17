package org.geogebra.web.full.gui.util;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;

public interface ElementPropertySetter {

	/**
	 * Update property from the current UI state.
	 * @param elements elements to be changed
	 * @return whether the state of any elements changed
	 */
	boolean apply(List<GeoElement> elements);
}
