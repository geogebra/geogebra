/* OoalignBox.java
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

import java.util.List;

import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

/**
 * A box representing a rotated box.
 */
public class OoalignBox extends Box {

	private final List<Box> boxes;

	public OoalignBox(List<Box> boxes) {
		this.boxes = boxes;
		this.width = Double.NEGATIVE_INFINITY;
		this.height = Double.NEGATIVE_INFINITY;
		this.depth = Double.NEGATIVE_INFINITY;
		for (final Box b : boxes) {
			width = Math.max(width, b.getWidth());
			height = Math.max(height, b.getHeight());
			depth = Math.max(depth, b.getDepth());
		}
	}

	@Override
	public void draw(Graphics2DInterface g2, double x, double y) {
		startDraw(g2, x, y);
		for (final Box b : boxes) {
			b.draw(g2, x + b.getShift(), y);
		}
		endDraw(g2);
	}

	@Override
	public FontInfo getLastFont() {
		return boxes.get(boxes.size() - 1).getLastFont();
	}

	@Override
	public void inspect(BoxConsumer handler, BoxPosition position) {
		super.inspect(handler, position);
		for (Box box : boxes) {
			box.inspect(handler, position);
		}
	}
}
