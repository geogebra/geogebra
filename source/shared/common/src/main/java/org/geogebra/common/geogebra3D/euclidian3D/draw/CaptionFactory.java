package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.kernel.geos.GeoElement;

public interface CaptionFactory {
	CaptionText createStaticCaption3D(GeoElement geo);
}
