package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoInline;

/**
 * Creates inline-editable elements.
 */
public interface GeoInlineFactory {
	GeoInline newInlineObject(Construction cons, GPoint2D location);
}
