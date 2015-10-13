package org.geogebra.common.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.geos.GeoElement;

public interface DropDownList {
	void drawSelected(GeoElement geo, GGraphics2D g2, GColor bgColor, int left,
			int top, int width, int height);
}
