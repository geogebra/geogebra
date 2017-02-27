/* RotateBox.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2009-2011 DENIZET Calixte
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

import com.himamis.retex.renderer.share.platform.Geom;
import com.himamis.retex.renderer.share.platform.geom.Point2D;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

/**
 * A box representing a rotated box.
 */
public class RotateBox extends Box {

	public static final int BL = 0;
	public static final int BC = 1;
	public static final int BR = 2;
	public static final int TL = 3;
	public static final int TC = 4;
	public static final int TR = 5;
	public static final int BBL = 6;
	public static final int BBR = 7;
	public static final int BBC = 8;
	public static final int CL = 9;
	public static final int CC = 10;
	public static final int CR = 11;

	protected double angle = 0;
	private Box box;
	private double xmax, xmin, ymax, ymin;

	private double shiftX;
	private double shiftY;

	public RotateBox(Box b, double angle, double x, double y) {
		this.box = b;
		this.angle = angle * Math.PI / 180;
		height = b.height;
		depth = b.depth;
		width = b.width;
		double s = Math.sin(this.angle);
		double c = Math.cos(this.angle);
		shiftX = (x * (1 - c) + y * s);
		shiftY = (y * (1 - c) - x * s);
		xmax =  Math.max(-height * s,
				Math.max(depth * s, Math.max(width * c + depth * s, width * c - height * s)))
				+ shiftX;
		xmin =  Math.min(-height * s,
				Math.min(depth * s, Math.min(width * c + depth * s, width * c - height * s)))
				+ shiftX;
		ymax =  Math.max(height * c,
				Math.max(-depth * c, Math.max(width * s - depth * c, width * s + height * c)));
		ymin =  Math.min(height * c,
				Math.min(-depth * c, Math.min(width * s - depth * c, width * s + height * c)));
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

	public static int getOrigin(String option0) {
		if (option0 == null || option0.length() == 0) {
			return BBL;
		}
		String option = option0;
		if (option.length() == 1) {
			option += "c";
		}
		if ("bl".equals(option) || "lb".equals(option)) {
			return BL;
		} else if ("bc".equals(option) || "cb".equals(option)) {
			return BC;
		} else if ("br".equals(option) || "rb".equals(option)) {
			return BR;
		} else if ("cl".equals(option) || "lc".equals(option)) {
			return CL;
		} else if ("cc".equals(option)) {
			return CC;
		} else if ("cr".equals(option) || "cr".equals(option)) {
			return CR;
		} else if ("tl".equals(option) || "lt".equals(option)) {
			return TL;
		} else if ("tc".equals(option) || "ct".equals(option)) {
			return TC;
		} else if ("tr".equals(option) || "rt".equals(option)) {
			return TR;
		} else if ("Bl".equals(option) || "lB".equals(option)) {
			return BBL;
		} else if ("Bc".equals(option) || "cB".equals(option)) {
			return BBC;
		} else if ("Br".equals(option) || "rB".equals(option)) {
			return BBR;
		} else {
			return BBL;
		}
	}

	private static Point2D calculateShift(Box b, int option) {
		Point2D p = new Geom().createPoint2D(0, -b.depth);
		switch (option) {
		case BL:
			p.setX(0);
			p.setY(-b.depth);
			break;
		case BR:
			p.setX(b.width);
			p.setY(-b.depth);
			break;
		case BC:
			p.setX(b.width / 2);
			p.setY(-b.depth);
			break;
		case TL:
			p.setX(0);
			p.setY(b.height);
			break;
		case TR:
			p.setX(b.width);
			p.setY(b.height);
			break;
		case TC:
			p.setX(b.width / 2);
			p.setY(b.height);
			break;
		case BBL:
			p.setX(0);
			p.setY(0);
			break;
		case BBR:
			p.setX(b.width);
			p.setY(0);
			break;
		case BBC:
			p.setX(b.width / 2);
			p.setY(0);
			break;
		case CL:
			p.setX(0);
			p.setY((b.height - b.depth) / 2);
			break;
		case CR:
			p.setX(b.width);
			p.setY((b.height - b.depth) / 2);
			break;
		case CC:
			p.setX(b.width / 2);
			p.setY((b.height - b.depth) / 2);
			break;
		default:
		}

		return p;
	}

	@Override
	public void draw(Graphics2DInterface g2, double x0, double y0) {
		drawDebug(g2, x0, y0);
		box.drawDebug(g2, x0, y0, true);
		double y = y0 - shiftY;
		double x = x0 + shiftX - xmin;
		g2.rotate(-angle, x, y);
		box.draw(g2, x, y);
		box.drawDebug(g2, x, y, true);
		g2.rotate(angle, x, y);
	}

	@Override
	public int getLastFontId() {
		return box.getLastFontId();
	}
}
