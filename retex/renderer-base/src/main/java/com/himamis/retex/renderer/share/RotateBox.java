/* RotateBox.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2009-2018 DENIZET Calixte
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

import java.util.HashMap;
import java.util.Map;

import com.himamis.retex.renderer.share.platform.Geom;
import com.himamis.retex.renderer.share.platform.geom.Point2D;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

/**
 * A box representing a rotated box.
 */
public class RotateBox extends Box {

	private static final int BL = 0;
	private static final int BC = 1;
	private static final int BR = 2;
	private static final int TL = 3;
	private static final int TC = 4;
	private static final int TR = 5;
	private static final int BBL = 6;
	private static final int BBR = 7;
	private static final int BBC = 8;
	private static final int CL = 9;
	private static final int CC = 10;
	private static final int CR = 11;

	private static final Map<String, Integer> map = new HashMap<String, Integer>() {
		{
			put("bl", BL);
			put("lb", BL);
			put("bc", BC);
			put("cb", BC);
			put("br", BR);
			put("rb", BR);
			put("cl", CL);
			put("lc", CL);
			put("cc", CC);
			put("cr", CR);
			put("rc", CR);
			put("tl", TL);
			put("lt", TL);
			put("tc", TC);
			put("ct", TC);
			put("tr", TR);
			put("rt", TR);
			put("Bl", BBL);
			put("lB", BBL);
			put("Bc", BBC);
			put("cB", BBC);
			put("Br", BBR);
			put("rB", BBR);
		}
	};

	protected double angle = 0.;
	private Box box;
	private double xmax, xmin, ymax, ymin;
	private int option;

	private double shiftX;
	private double shiftY;

	public RotateBox(Box b, double angle, double x, double y) {
		this.box = b;
		this.angle = angle * Math.PI / 180.;
		height = b.height;
		depth = b.depth;
		width = b.width;

		final double s = Math.sin(this.angle);
		final double c = Math.cos(this.angle);
		shiftX = x * (1 - c) + y * s;
		shiftY = y * (1 - c) - x * s;
		xmax = Math
				.max(-height * s, Math.max(depth * s, Math
						.max(width * c + depth * s, width * c - height * s)))
				+ shiftX;
		xmin = Math
				.min(-height * s, Math.min(depth * s, Math
						.min(width * c + depth * s, width * c - height * s)))
				+ shiftX;
		ymax = Math.max(height * c, Math.max(-depth * c,
				Math.max(width * s - depth * c, width * s + height * c)));
		ymin = Math.min(height * c, Math.min(-depth * c,
				Math.min(width * s - depth * c, width * s + height * c)));
		width = xmax - xmin;
		height = ymax + shiftY;
		depth = -ymin - shiftY;
	}

	public RotateBox(Box b, double angle, Point2D origin) {
		this(b, angle, origin.getX(), origin.getY());
	}

	public RotateBox(Box b, double angle, int option) {
		this(b, angle, calculateShift(b, option));
	}

	public static int getOrigin(final String option) {
		if (option == null || option.isEmpty() || option.length() >= 3) {
			return BBL;
		}

		if (option.length() == 1) {
			switch (option.charAt(0)) {
			case 'b':
				return BC;
			case 'c':
				return CC;
			case 'l':
				return CL;
			case 'r':
				return CR;
			case 't':
				return TC;
			case 'B':
				return BBC;
			default:
				return BBL;
			}
		}

		final Integer v = map.get(option);
		if (v != null) {
			return v.intValue();
		}
		return BBL;
	}

	private static Point2D calculateShift(Box b, int option) {
		Point2D p = new Geom().createPoint2D(0, -b.depth);

		switch (option) {
		case BL:
			p.setX(0.);
			p.setY(-b.depth);
			break;
		case BR:
			p.setX(b.width);
			p.setY(-b.depth);
			break;
		case BC:
			p.setX(b.width / 2.);
			p.setY(-b.depth);
			break;
		case TL:
			p.setX(0.);
			p.setY(b.height);
			break;
		case TR:
			p.setX(b.width);
			p.setY(b.height);
			break;
		case TC:
			p.setX(b.width / 2.);
			p.setY(b.height);
			break;
		case BBL:
			p.setX(0.);
			p.setY(0.);
			break;
		case BBR:
			p.setX(b.width);
			p.setY(0.);
			break;
		case BBC:
			p.setX(b.width / 2.);
			p.setY(0.);
			break;
		case CL:
			p.setX(0.);
			p.setY((b.height - b.depth) / 2.);
			break;
		case CR:
			p.setX(b.width);
			p.setY((b.height - b.depth) / 2.);
			break;
		case CC:
			p.setX(b.width / 2.);
			p.setY((b.height - b.depth) / 2.);
			break;
		default:
		}

		return p;
	}

	public void draw(Graphics2DInterface g2, double x, double y) {
		startDraw(g2, x, y);
		box.drawDebug(g2, x, y, true);
		y -= shiftY;
		x += shiftX - xmin;
		g2.rotate(-angle, x, y);
		box.draw(g2, x, y);
		box.drawDebug(g2, x, y, true);
		g2.rotate(angle, x, y);
		endDraw(g2);
	}

	public FontInfo getLastFont() {
		return box.getLastFont();
	}
}