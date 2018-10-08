/**
 * This file is part of the ReTeX library - https://github.com/himamis/ReTeX
 *
 * Copyright (C) 2015 Balazs Bencze
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
package com.himamis.retex.renderer.web.graphics;

import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.himamis.retex.renderer.share.platform.graphics.BasicStroke;

public class BasicStrokeW implements BasicStroke {

	private double width;
	private int cap;
	private int join;
	private double miterLimit;
	private float[] dashes;

	public BasicStrokeW(double width, int cap, int join, double miterLimit) {
		this.width = width;
		this.cap = cap;
		this.join = join;
		this.miterLimit = miterLimit;
	}

	public BasicStrokeW(double width, String cap, String join,
			double miterLimit) {
		this.width = width;
		this.cap = getLineCap(LineCap.valueOf(cap.toUpperCase()));
		this.join = getLineJoin(LineJoin.valueOf(join.toUpperCase()));
		this.miterLimit = miterLimit;
	}

	public BasicStrokeW(double width2, float[] dashes2) {
		this.width = width2;
		this.dashes = dashes2;
		this.cap = CAP_BUTT;
		this.join = JOIN_MITER;
		this.miterLimit = 10;

	}

	public double getWidth() {
		return width;
	}

	public int getCap() {
		return cap;
	}

	public int getJoin() {
		return join;
	}

	public double getMiterLimit() {
		return miterLimit;
	}

	public LineCap getJSLineCap() {
		switch (cap) {
		case CAP_BUTT:
			return LineCap.BUTT;
		case CAP_ROUND:
			return LineCap.ROUND;
		case CAP_SQUARE:
			return LineCap.SQUARE;
		default:
			return LineCap.BUTT;
		}
	}

	public LineJoin getJSLineJoin() {
		switch (join) {
		case JOIN_BEVEL:
			return LineJoin.BEVEL;
		case JOIN_MITER:
			return LineJoin.MITER;
		case JOIN_ROUND:
			return LineJoin.ROUND;
		default:
			return LineJoin.BEVEL;
		}
	}

	private static int getLineJoin(LineJoin lineJoin) {
		switch (lineJoin) {
		case BEVEL:
			return JOIN_BEVEL;
		case MITER:
			return JOIN_MITER;
		case ROUND:
			return JOIN_ROUND;
		default:
			return JOIN_BEVEL;
		}
	}

	private static int getLineCap(LineCap lineCap) {
		switch (lineCap) {
		case BUTT:
			return CAP_BUTT;
		case ROUND:
			return CAP_ROUND;
		case SQUARE:
			return CAP_SQUARE;
		default:
			return CAP_BUTT;
		}
	}

	public float[] getDash() {
		return dashes;
	}
}
