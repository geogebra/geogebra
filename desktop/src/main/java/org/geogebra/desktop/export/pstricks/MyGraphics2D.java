package org.geogebra.desktop.export.pstricks;

import org.geogebra.desktop.export.epsgraphics.ColorMode;
import org.geogebra.desktop.export.epsgraphics.EpsGraphics;

// Created just for the constructor of MyGraphics.EpsGraphics used to avoid
// having all methods of Graphics2D. None of his methods is used
class MyGraphics2D extends EpsGraphics {

	public MyGraphics2D(String title, StringBuilder sb, int minX,
			int minY, int maxX, int maxY, ColorMode colorMode) {
		super(title, sb, minX, minY, maxX, maxY, colorMode);
	}
}