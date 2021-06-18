/* CancelBox.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2018 DENIZET Calixte
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

import com.himamis.retex.renderer.share.platform.geom.Line2D;
import com.himamis.retex.renderer.share.platform.graphics.BasicStroke;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;

/**
 * A box representing a rotated box.
 */
public class CancelBox extends Box {

	private final Box b;
	private final CancelAtom.Type ctype;
	private final double thickness;
	private final double extra;
	private final Color color;

	public CancelBox(final Box b, final CancelAtom.Type ctype,
			final double thickness, final double extra, Color color) {
		this.b = b;
		this.ctype = ctype;
		this.thickness = thickness;
		this.extra = extra;
		this.color = color;
		this.width = b.width;
		this.height = b.height;
		this.depth = b.depth;
		this.shift = b.shift;
	}

	@Override
	public void draw(Graphics2DInterface g2, double x, double y) {
		b.draw(g2, x, y);
		startDraw(g2, x, y);
		final Stroke oldStroke = g2.getStroke();
		g2.setStroke(graphics.createBasicStroke(thickness, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER));
		final double th = thickness / 2.;
		final double hyp = Math.hypot(width, height + depth);
		final double a = extra * width / hyp;
		final double b = extra * height / hyp;
		Color oldColor = null;
		if (color != null) {
			oldColor = g2.getColor();
			g2.setColor(color);
		}

		Line2D line = geom.createLine2D();

		switch (ctype) {
		case SLASH:
			line.setLine(x + th - a, y + depth - th + b, x + width - th + a,
					y - height + th - b);
			g2.draw(line);
			break;
		case BACKSLASH:
			line.setLine(x + th - a, y - height + th - b, x + width - th + a,
					y + depth - th + b);
			g2.draw(line);
			break;
		case X:
			line.setLine(x + th - a, y - height + th - b, x + width - th + a,
					y + depth - th + b);
			g2.draw(line);
			line.setLine(x + th - a, y + depth - th + b, x + width - th + a,
					y - height + th - b);
			g2.draw(line);
			break;
		}
		g2.setStroke(oldStroke);
		if (color != null) {
			g2.setColor(oldColor);
		}

		endDraw(g2);
	}

	@Override
	public FontInfo getLastFont() {
		return b.getLastFont();
	}

	@Override
	public void inspect(BoxConsumer handler, BoxPosition position) {
		super.inspect(handler, position);
		b.inspect(handler, position);
	}
}
