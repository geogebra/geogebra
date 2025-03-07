/* FramedBox.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 * 
 * Copyright (C) 2009 DENIZET Calixte
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 * 
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * Linking this library statically or dynamically with other modules 
 * is making a combined work based on this library. Thus, the terms 
 * and conditions of the GNU General Public License cover the whole 
 * combination.
 * 
 * As a special exception, the copyright holders of this library give you 
 * permission to link this library with independent modules to produce 
 * an executable, regardless of the license terms of these independent 
 * modules, and to copy and distribute the resulting executable under terms 
 * of your choice, provided that you also meet, for each linked independent 
 * module, the terms and conditions of the license of that module. 
 * An independent module is a module which is not derived from or based 
 * on this library. If you modify this library, you may extend this exception 
 * to your version of the library, but you are not obliged to do so. 
 * If you do not wish to do so, delete this exception statement from your 
 * version.
 * 
 */

package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.graphics.BasicStroke;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;

/**
 * A box representing a framed box.
 */
public class FramedBox extends Box {

	protected Box box;
	protected double thickness;
	protected double space;
	protected double dashlength;
	protected double dashdash;
	protected Color line;
	protected Color bg;

	private final Rectangle2D rectangle;

	/**
	 * @param box wrapped box
	 * @param thickness border thickness (relative to font size)
	 * @param space padding (relative to font size)
	 * @param line border color
	 * @param bg background color
	 * @param dashLength total length of dash pattern
	 * @param dashVisibleLength visible part of dash pattern
	 */
	public FramedBox(Box box, double thickness, double space, Color line,
			Color bg, double dashLength, double dashVisibleLength) {
		this.box = box;
		this.width = box.width + 2 * thickness + 2 * space;
		this.height = box.height + thickness + space;
		this.depth = box.depth + thickness + space;
		this.shift = box.shift;
		this.thickness = thickness;
		this.space = space;
		this.line = line;
		this.bg = bg;
		this.dashlength = dashLength;
		this.dashdash = dashVisibleLength;

		rectangle = geom.createRectangle2D(0, 0, 0, 0);
	}

	public FramedBox(Box box, double thickness, double space) {
		this(box, thickness, space, null, null, Double.NaN, Double.NaN);
	}

	public FramedBox(Box box, double thickness, double space, double dashLength,
			double dashVisibleLength) {
		this(box, thickness, space, null, null, dashLength, dashVisibleLength);
	}

	public FramedBox(Box box, double thickness, double space, Color line,
			Color bg) {
		this(box, thickness, space, line, bg, Double.NaN, Double.NaN);
	}

	@Override
	public void draw(Graphics2DInterface g2, double x, double y) {
		Stroke st = g2.getStroke();
		// g2.setStroke(graphics.createBasicStroke(thickness,
		// BasicStroke.CAP_BUTT,
		// BasicStroke.JOIN_MITER));
		if (Double.isNaN(dashlength) || Double.isNaN(dashdash)) {
			g2.setStroke(graphics.createBasicStroke(thickness,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		} else {
			double[] dashes = new double[] { (float) dashdash,
					(float) (dashlength - dashdash) };
			g2.setStroke(graphics.createBasicStroke(thickness, dashes));
		}
		double th = thickness / 2;
		rectangle.setRectangle(x + th, y - height + th, width - thickness,
				height + depth - thickness);
		fillAndDraw(g2);
		drawDebug(g2, x, y);
		g2.setStroke(st);
		box.draw(g2, x + space + thickness, y);
	}

	protected void fillAndDraw(Graphics2DInterface g2) {
		if (bg != null) {
			Color prev = g2.getColor();
			g2.setColor(bg);
			fillShape(g2);
			g2.setColor(prev);
		}
		if (line != null) {
			Color prev = g2.getColor();
			g2.setColor(line);
			drawShape(g2);
			g2.setColor(prev);
		} else {
			drawShape(g2);
		}
	}

	protected void drawShape(Graphics2DInterface g2) {
		g2.draw(rectangle);
	}

	protected void fillShape(Graphics2DInterface g2) {
		g2.fill(rectangle);
	}

	@Override
	public FontInfo getLastFont() {
		return box.getLastFont();
	}

	@Override
	public void inspect(BoxConsumer handler, BoxPosition position) {
		super.inspect(handler, position);
		box.inspect(handler, position.withPosition(position.x + space, position.y + space));
	}

	public void setColor(Color line) {
		this.line = line;
	}

}