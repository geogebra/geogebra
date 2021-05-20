/* VerticalBox.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
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

import java.util.ArrayList;
import java.util.ListIterator;

import com.himamis.retex.renderer.share.platform.geom.Area;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

/**
 * A box composed of other boxes, put one above the other.
 */
class VerticalBox extends Box {

	private double leftMostPos = Double.MAX_VALUE;
	private double rightMostPos = -Double.MAX_VALUE;
	protected final ArrayList<Box> children = new ArrayList<Box>();

	public VerticalBox() {
	}

	public VerticalBox(Box b) {
		add(b);
	}

	public VerticalBox(Box b, double rest, TeXConstants.Align alignment) {
		this();
		add(b);
		if (alignment == TeXConstants.Align.CENTER) {
			final StrutBox s = new StrutBox(0., rest / 2., 0., 0.);
			justAdd(0, s);
			height += rest / 2.;
			depth += rest / 2.;
			justAdd(s);
		} else if (alignment == TeXConstants.Align.TOP) {
			depth += rest;
			justAdd(new StrutBox(0., rest, 0., 0.));
		} else if (alignment == TeXConstants.Align.BOTTOM) {
			height += rest;
			justAdd(0, new StrutBox(0., rest, 0., 0.));
		}
	}

	private final void justAdd(Box b) {
		children.add(b);
	}

	public final void add(Box b) {
		justAdd(b);
		if (children.size() == 1) {
			height = b.height;
			depth = b.depth;
		} else {
			depth += b.height + b.depth;
		}
		recalculateWidth(b);
	}

	public final void add(Box b, double interline) {
		if (children.size() >= 1) {
			add(new StrutBox(0., interline, 0., 0.));
		}
		add(b);
	}

	private void recalculateWidth(Box b) {
		leftMostPos = Math.min(leftMostPos, b.shift);
		rightMostPos = Math.max(rightMostPos,
				b.shift + (b.width > 0 ? b.width : 0));
		width = rightMostPos - leftMostPos;
	}

	private final void justAdd(int pos, Box b) {
		children.add(pos, b);
	}

	public void add(int pos, Box b) {
		justAdd(pos, b);
		if (pos == 0) {
			depth += b.depth + height;
			height = b.height;
		} else {
			depth += b.height + b.depth;
		}
		recalculateWidth(b);
	}

	@Override
	public void draw(Graphics2DInterface g2, double x, double y) {
		startDraw(g2, x, y);
		double yPos = y - height;
		for (final Box box : children) {
			if (box instanceof HVruleBox) {
				((HVruleBox) box).setWHD(width, height, depth);
			}
			yPos += box.getHeight();
			box.draw(g2, x + box.getShift() - leftMostPos, yPos);
			yPos += box.getDepth();
		}
		endDraw(g2);
	}

	@Override
	public Area getArea() {
		// final Area area = new Area();
		final Area area = geom.createArea();
		// final AffineTransform af = AffineTransform.getTranslateInstance(0.,
		// -height);
		final double afX = 0;
		double afY = -height;

		for (final Box b : children) {
			if (b instanceof StrutBox) {
				// af.translate(0., b.getHeight() + b.getDepth());
				afY += b.getHeight() + b.getDepth();
			} else {
				final Area a = b.getArea();
				if (a == null) {
					return null;
				}
				// af.translate(0., b.getHeight());
				afY += b.getHeight();
				// a.transform(af);
				a.translate(afX, afY);
				area.add(a);
				// af.translate(0., b.getDepth());
				afY += b.getDepth();
			}
		}
		return area;
	}

	public int getSize() {
		return children.size();
	}

	@Override
	public FontInfo getLastFont() {
		// iterate from the last child box (the lowest) to the first (the
		// highest)
		// untill a font id is found that's not equal to NO_FONT
		FontInfo fontId = null;
		for (ListIterator it = children
				.listIterator(children.size()); fontId == null
						&& it.hasPrevious();) {
			fontId = ((Box) it.previous()).getLastFont();
		}

		return fontId;
	}

	@Override
	public void inspect(BoxConsumer handler, BoxPosition position) {
		super.inspect(handler, position);

		double yPos = position.y - height;
		double baseline = position.baseline - height;
		for (Box box : children) {
			yPos += box.getHeight();
			baseline += box.getHeight();
			BoxPosition current = new BoxPosition(position.x + box.getShift() - leftMostPos,
					yPos, position.scale, baseline);
			box.inspect(handler, current);
			yPos += box.getDepth();
			baseline += box.getDepth();
		}
	}
}
