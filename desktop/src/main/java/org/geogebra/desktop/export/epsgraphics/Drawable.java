/**
 * Drawable.java
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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * An interface for an object that can draw itself within an rectangle using a
 * Graphics2D context.
 * 
 * @author Thomas Abeel
 */
public interface Drawable {

	/**
	 * Draws the object in the rectangle using the provide graphics context.
	 * 
	 * @param g2
	 *            the graphics device.
	 * @param area
	 *            the area inside which the object should be drawn.
	 */
	public void draw(Graphics2D g2, Rectangle2D area);
}
