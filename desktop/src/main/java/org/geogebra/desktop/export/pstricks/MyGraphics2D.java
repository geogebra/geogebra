package org.geogebra.desktop.export.pstricks;

import java.io.IOException;
import java.io.OutputStream;

import org.geogebra.desktop.export.epsgraphics.ColorMode;
import org.geogebra.desktop.export.epsgraphics.EpsGraphics;

// Created just for the constructor of MyGraphics.EpsGraphics used to avoid
// having all methods of Graphics2D. None of his methods is used
class MyGraphics2D extends EpsGraphics {

	public MyGraphics2D(String title, OutputStream outputStream, int minX,
			int minY, int maxX, int maxY, ColorMode colorMode)
			throws IOException {
		super(title, outputStream, minX, minY, maxX, maxY, colorMode);
	}
}