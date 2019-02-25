package org.geogebra.web.html5.main;

import org.geogebra.common.kernel.geos.GeoElement;

public interface MathEditor {

	void setState(String text, GeoElement geo);

	String getState();

}
