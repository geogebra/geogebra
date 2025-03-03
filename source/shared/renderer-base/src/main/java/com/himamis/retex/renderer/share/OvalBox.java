/* OvalBox.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2011 DENIZET Calixte
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

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.geom.RoundRectangle2D;
import com.himamis.retex.renderer.share.platform.graphics.BasicStroke;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;

/**
 * A box representing a rotated box.
 */
public class OvalBox extends FramedBox {

	private RoundRectangle2D roundRectangle = geom.createRoundRectangle2D(0, 0,
			0, 0, 0, 0);

	final double cornersize;

	public OvalBox(Box bbase, double drt, double space, Color line, Color bg,
			double cornersize, double minHeight) {
		super(bbase, drt, space, line, bg);
		this.height = Math.max(height, minHeight);
		this.cornersize = cornersize;
	}

	@Override
	public void draw(Graphics2DInterface g2, double x, double y) {

		Stroke st = g2.getStroke();
		g2.setStroke(graphics.createBasicStroke(thickness, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER));
		double th = thickness / 2;
		double r = cornersize * Math.min(width - 2 * thickness,
				height + depth - 2 * thickness);
		roundRectangle.setRoundRectangle(x + th, y - height + th,
				width - thickness, height + depth - thickness, r, r);
		fillAndDraw(g2);

		drawDebug(g2, x, y);
		box.draw(g2, x + space + thickness, y);
		g2.setStroke(st);
	}

	protected void drawShape(Graphics2DInterface g2) {
		g2.draw(roundRectangle);
	}

	protected void fillShape(Graphics2DInterface g2) {
		g2.fill(roundRectangle);
	}

	@Override
	public FontInfo getLastFont() {
		return box.getLastFont();
	}
}