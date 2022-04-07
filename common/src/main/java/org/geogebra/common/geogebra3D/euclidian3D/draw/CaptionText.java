package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.GeoElement;

public interface CaptionText {

	String text();

	GFont font();

	int fontSize();

	boolean isSerifFont();

	boolean isLaTeX();

	GColor foregroundColor();

	GColor backgroundColor();

	void update(String text, GFont font, GColor fgColor);

	String textToDraw();

	GeoElement getGeoElement();

	void createFont(GFont original);

	void register(GeoElement geo);

	boolean hasChanged(String text, GFont font);

	boolean isValid();
}
