/* CMMIB10_UNCHANGED.java
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

package com.himamis.retex.renderer.share.fonts;

import com.himamis.retex.renderer.share.Configuration;
import com.himamis.retex.renderer.share.FontInfo;

final class CMMIB10_UNCHANGED extends FontInfo {

	CMMIB10_UNCHANGED(final String ttfPath) {
		super(0, ttfPath, 0.444445, 0.0, 1.149994, '\u0000');
	}

	@Override
	protected final void initMetrics() {
		setInfo('\u00AE', // char code: 174
				new double[] { 0.760645, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.031944 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00AF', // char code: 175
				new double[] { 0.659719, 0.694445, 0.194445, 0.034029 }, // metrics:
																			// width,
																			// height,
																			// depth,
																			// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.095833 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B0', // char code: 176
				new double[] { 0.59003, 0.444445, 0.194445, 0.063889 }, // metrics:
																		// width,
																		// height,
																		// depth,
																		// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B1', // char code: 177
				new double[] { 0.522219, 0.694445, 0., 0.038195 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u003B', '\u003A', '\u00C4' }, // kern codes
				new double[] { -0.063889, -0.063889, 0.063889 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B2', // char code: 178
				new double[] { 0.483332, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.063889 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B3', // char code: 179
				new double[] { 0.508331, 0.694445, 0.194445, 0.062154 }, // metrics:
																			// width,
																			// height,
																			// depth,
																			// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.095833 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B4', // char code: 180
				new double[] { 0.599998, 0.444445, 0.194445, 0.037038 }, // metrics:
																			// width,
																			// height,
																			// depth,
																			// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.063889 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B5', // char code: 181
				new double[] { 0.561803, 0.694445, 0., 0.031944 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.095833 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B6', // char code: 182
				new double[] { 0.412036, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.063889 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B7', // char code: 183
				new double[] { 0.66759, 0.444445, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B8', // char code: 184
				new double[] { 0.67083, 0.694445, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B9', // char code: 185
				new double[] { 0.707869, 0.444445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.031944 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BA', // char code: 186
				new double[] { 0.576849, 0.444445, 0., 0.068982 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u003B', '\u003A', '\u00C4' }, // kern codes
				new double[] { -0.063889, -0.063889, 0.031944 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BB', // char code: 187
				new double[] { 0.508331, 0.694445, 0.194445, 0.03021 }, // metrics:
																		// width,
																		// height,
																		// depth,
																		// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.127777 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BC', // char code: 188
				new double[] { 0.682405, 0.444445, 0., 0.037038 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BD', // char code: 189
				new double[] { 0.611804, 0.444445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.095833 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BE', // char code: 190
				new double[] { 0.685877, 0.444445, 0., 0.037038 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u003B', '\u003A' }, // kern codes
				new double[] { -0.063889, -0.063889 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BF', // char code: 191
				new double[] { 0.520831, 0.444445, 0., 0.134723 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u003B', '\u003A', '\u00C4' }, // kern codes
				new double[] { -0.063889, -0.063889, 0.031944 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00C0', // char code: 192
				new double[] { 0.630552, 0.444445, 0., 0.037038 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.031944 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00C1', // char code: 193
				new double[] { 0.712496, 0.694445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.095833 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00C2', // char code: 194
				new double[] { 0.718054, 0.444445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.063889 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00C3', // char code: 195
				new double[] { 0.758329, 0.694445, 0.194445, 0.037038 }, // metrics:
																			// width,
																			// height,
																			// depth,
																			// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.127777 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00C4', // char code: 196
				new double[] { 0.319443, 0.694445, 0., 0.449999 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0021', // char code: 33
				new double[] { 0.717819, 0.444445, 0., 0.037038 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\"', // char code: 34
				new double[] { 0.528818, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.095833 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0023', // char code: 35
				new double[] { 0.691548, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.095833 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0024', // char code: 36
				new double[] { 0.974997, 0.444445, 0., 0.031944 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0025', // char code: 37
				new double[] { 0.611804, 0.444445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.095833 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0026', // char code: 38
				new double[] { 0.423609, 0.444445, 0.097223, 0.079167 }, // metrics:
																			// width,
																			// height,
																			// depth,
																			// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.095833 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\'', // char code: 39
				new double[] { 0.747217, 0.444445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.095833 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0028', // char code: 40
				new double[] { 1.149994, 0.391111, -0.108889, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0029', // char code: 41
				new double[] { 1.149994, 0.391111, -0.108889, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u002A', // char code: 42
				new double[] { 1.149994, 0.391111, -0.108889, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u002B', // char code: 43
				new double[] { 1.149994, 0.391111, -0.108889, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u002C', // char code: 44
				new double[] { 0.319443, 0.502223, 0.002223, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u002D', // char code: 45
				new double[] { 0.319443, 0.502223, 0.002223, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u002E', // char code: 46
				new double[] { 0.574997, 0.472223, -0.027777, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u002F', // char code: 47
				new double[] { 0.574997, 0.472223, -0.027777, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0030', // char code: 48
				new double[] { 0.574997, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0031', // char code: 49
				new double[] { 0.574997, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0032', // char code: 50
				new double[] { 0.574997, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0033', // char code: 51
				new double[] { 0.574997, 0.444445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0034', // char code: 52
				new double[] { 0.574997, 0.444445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0035', // char code: 53
				new double[] { 0.574997, 0.444445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0036', // char code: 54
				new double[] { 0.574997, 0.644444, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0037', // char code: 55
				new double[] { 0.574997, 0.444445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0038', // char code: 56
				new double[] { 0.574997, 0.644444, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0039', // char code: 57
				new double[] { 0.574997, 0.444445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u003A', // char code: 58
				new double[] { 0.319443, 0.155556, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u003B', // char code: 59
				new double[] { 0.319443, 0.155556, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u003C', // char code: 60
				new double[] { 0.89444, 0.585556, 0.085556, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00AB', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u003D', // char code: 61
				new double[] { 0.574997, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00B1', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u003E', // char code: 62
				new double[] { 0.89444, 0.585556, 0.085556, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00AE', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u003F', // char code: 63
				new double[] { 0.574997, 0.472223, -0.027777, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0040', // char code: 64
				new double[] { 0.6284685, 0.694445, 0., 0.063889 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.095833 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u005B', // char code: 91
				new double[] { 0.44722, 0.75, 0., 0. }, // metrics: width,
														// height, depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\\', // char code: 92
				new double[] { 0.44722, 0.694445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u005D', // char code: 93
				new double[] { 0.44722, 0.694445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u005E', // char code: 94
				new double[] { 1.149994, 0.361112, -0.138888, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u005F', // char code: 95
				new double[] { 1.149994, 0.361112, -0.138888, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0060', // char code: 96
				new double[] { 0.473612, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.127777 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0065', // char code: 101
				new double[] { 0.553609, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.063889 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u006F', // char code: 111
				new double[] { 0.584719, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.063889 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007B', // char code: 123
				new double[] { 0.393518, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.031944 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007C', // char code: 124
				new double[] { 0.438889, 0.444445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.095833 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007D', // char code: 125
				new double[] { 0.740274, 0.444445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u00C4' }, // kern codes
				new double[] { 0.127777 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007E', // char code: 126
				new double[] { 0.574997, 0.724444, 0., 0.154861 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
	}
}
