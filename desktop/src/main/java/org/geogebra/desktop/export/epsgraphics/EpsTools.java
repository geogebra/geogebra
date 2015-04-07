/**
 * EpsTools.java
 *
 * This file is part of the EPS Graphics Library
 * 
 * The EPS Graphics Library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The EPS Graphics Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the EPS Graphics Library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2009, Thomas Abeel
 *  
 * Project: http://sourceforge.net/projects/epsgraphics/
 */
package org.geogebra.desktop.export.epsgraphics;

import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Contains utility method to create an EPS figure from an object that
 * implements the <code>Drawable</code> interface.
 * 
 * @author Thomas Abeel
 * 
 */
public class EpsTools {
	/* This class should not be instantiated. */
	private EpsTools() {

	}

	/**
	 * Method to export a drawable object to an EPS file.
	 * 
	 * @param d
	 *            the drawable object
	 * @param fileName
	 *            the file name of the EPS file
	 * @param x
	 *            the width of the exported graphic
	 * @param y
	 *            the height of the exported graphic
	 * @param colorMode
	 *            the colormode to be used
	 * @return true when the export is succesful, false in other cases
	 */
	public static boolean createFromDrawable(Drawable d, String fileName,
			int x, int y, ColorMode colorMode) {
		try {
			EpsGraphics g = new EpsGraphics("EpsTools Drawable Export",
					new FileOutputStream(fileName + ".eps"), 0, 0, x, y,
					colorMode);
			d.draw(g, new Rectangle(x, y));
			g.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}
}
