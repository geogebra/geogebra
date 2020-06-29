package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoElementND;

public interface GeoInline extends GeoElementND, RectangleTransformable {

	/**
	 * @param content editor content; encoding depends on editor type
	 */
	void setContent(String content);
}
