package org.geogebra.common.kernel.geos;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.euclidian.draw.HasTextFormat;

/**
 * Construction element that has a text formatter.
 */
public interface HasTextFormatter {

	@MissingDoc
	HasTextFormat getFormatter();
}
