/* CellBox.java
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

import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

/**
 * A box representing a rotated box.
 */
public class CellBox extends Box {

	private Box box;
	private double left;

	public CellBox(Box box, double height, double depth, double left,
			double right, double colW, TeXConstants.Align alignment) {
		this.width = left + colW + right;
		this.height = height;
		this.depth = depth;
		this.box = box;
		switch (alignment) {
		case LEFT:
			this.left = left;
			break;
		case RIGHT:
			this.left = left + colW - box.width;
			break;
		case CENTER:
			this.left = left + (colW - box.width) / 2.;
			break;
		default:
			this.left = left;
		}
	}

	@Override
	public void draw(Graphics2DInterface g2, double x, double y) {
		startDraw(g2, x, y);
		box.draw(g2, x + left, y);
		endDraw(g2);
	}

	@Override
	public FontInfo getLastFont() {
		return box.getLastFont();
	}

	@Override
	public void inspect(BoxConsumer handler, BoxPosition position) {
		super.inspect(handler, position);
		box.inspect(handler, position.withX(position.x + left));
	}
}
