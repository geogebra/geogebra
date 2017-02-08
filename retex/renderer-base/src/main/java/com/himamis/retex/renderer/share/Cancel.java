/* Cancel.java
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

import com.himamis.retex.renderer.share.platform.geom.Line2D;
import com.himamis.retex.renderer.share.platform.graphics.BasicStroke;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;

/**
 * adapted from FramedBox
 * 
 * implements \cancel, \bcancel, \xcancel
 * 
 * @author Michael Borcherds
 */
public class Cancel extends Box {

	private Box box;
	private double thickness;
	private double space;

	private Line2D diagonalLine;
	private Type cancelType;

	/**
	 * type of cancelling
	 *
	 */
	enum Type {
		/** line bottom-left to top-right */
		SLASH,

		/** line top-left to bottom-right */
		BACKSLASH,

		/** two diagonal lines */
		CROSS
	}

	/**
	 * @param box
	 *            box
	 * @param thickness
	 *            line thickness
	 * @param cancelType
	 *            type of cancel
	 */
	public Cancel(Box box, double thickness, Type cancelType) {
		this.box = box;
		this.width = box.width;
		this.height = box.height;
		this.depth = box.depth;
		this.shift = box.shift;
		this.thickness = thickness;

		this.cancelType = cancelType;

		diagonalLine = geom.createLine2D();
	}

	@Override
	public void draw(Graphics2DInterface g2, double x, double y) {
		box.draw(g2, x + space + thickness, y);
		Stroke st = g2.getStroke();
		g2.setStroke(graphics.createBasicStroke(0.1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER));
		double th = thickness / 2;

		if (cancelType == Type.BACKSLASH || cancelType == Type.CROSS) {
			diagonalLine.setLine(x + th, y - height + th,
					x + th + width - thickness,
					y - height + th + height + depth - thickness);
			g2.draw(diagonalLine);
		}

		if (cancelType == Type.SLASH || cancelType == Type.CROSS) {
			diagonalLine.setLine(x + th,
					y - height + th + height + depth - thickness,
					x + th + width - thickness, y - height + th);
			g2.draw(diagonalLine);
		}

		g2.setStroke(st);

	}

	@Override
	public int getLastFontId() {
		return box.getLastFontId();
	}
}
