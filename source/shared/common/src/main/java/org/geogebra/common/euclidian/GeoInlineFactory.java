package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoInline;

/**
 * Creates inline-editable elements.
 */
public interface GeoInlineFactory {
	/**
	 * Create an element at given position.
	 * @param cons construction
	 * @param location location
	 * @return inline-editable element
	 */
	GeoInline newInlineObject(Construction cons, GPoint2D location);
}
