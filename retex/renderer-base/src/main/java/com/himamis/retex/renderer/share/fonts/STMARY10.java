/* STMARY10.java
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
import com.himamis.retex.renderer.share.TeXFont;

final class STMARY10 extends FontInfo {

	STMARY10(final String ttfPath) {
		super(0, ttfPath, 0.430555, 0.0, 1.000003, '\u0000');
	}

	@Override
	protected final void initMetrics() {
		setInfo('\u0020', // char code: 32
				new double[] { 0.777781, 0.366875, -0.133125, 0. }, // metrics:
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
				new double[] { 0.777781, 0.366875, -0.133125, 0. }, // metrics:
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
				new double[] { 0.500002, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0023', // char code: 35
				new double[] { 0.500002, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0024', // char code: 36
				new double[] { 0.555557, 0.583334, 0.033438, 0. }, // metrics:
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
				new double[] { 0.555557, 0.527779, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0026', // char code: 38
				new double[] { 0.611113, 0.527779, 0.033438, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\'', // char code: 39
				new double[] { 0.611113, 0.527779, 0.033438, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0028', // char code: 40
				new double[] { 0.666669, 0.694445, 0.187327, 0. }, // metrics:
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
				new double[] { 0.666669, 0.694445, 0.187327, 0. }, // metrics:
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
				new double[] { 0.777781, 0.527779, 0.033438, 0. }, // metrics:
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
				new double[] { 0.555557, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.666669, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u002D', // char code: 45
				new double[] { 0.666669, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u002E', // char code: 46
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0031', // char code: 49
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0032', // char code: 50
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0033', // char code: 51
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0037', // char code: 55
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0039', // char code: 57
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u003B', // char code: 59
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u003D', // char code: 61
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u003E', // char code: 62
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u003F', // char code: 63
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.611113, 0.694445, 0.187327, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0041', // char code: 65
				new double[] { 0.888891, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0042', // char code: 66
				new double[] { 0.555557, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0043', // char code: 67
				new double[] { 0.333334, 0.5625, 0.187327, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0044', // char code: 68
				new double[] { 0.611113, 0.694445, 0.187327, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0045', // char code: 69
				new double[] { 0.611113, 0.694445, 0.187327, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0046', // char code: 70
				new double[] { 0.666669, 0.694445, 0.187327, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0047', // char code: 71
				new double[] { 0.666669, 0.694445, 0.187327, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0048', // char code: 72
				new double[] { 0.722224, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0049', // char code: 73
				new double[] { 0.722224, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u004A', // char code: 74
				new double[] { 0.444446, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u004B', // char code: 75
				new double[] { 0.444446, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u004C', // char code: 76
				new double[] { 1.000003, 0.694445, 0.187327, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u004D', // char code: 77
				new double[] { 0.777781, 0.680211, 0.187327, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u004E', // char code: 78
				new double[] { 0.666669, 0.694445, 0.187327, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u004F', // char code: 79
				new double[] { 0.666669, 0.694445, 0.187327, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0050', // char code: 80
				new double[] { 0.611113, 0.694445, 0.187327, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0051', // char code: 81
				new double[] { 0.611113, 0.694445, 0.187327, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0052', // char code: 82
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0053', // char code: 83
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0054', // char code: 84
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0055', // char code: 85
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0056', // char code: 86
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0057', // char code: 87
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0058', // char code: 88
				new double[] { 0.500002, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0059', // char code: 89
				new double[] { 0.6944475, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u005A', // char code: 90
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u005B', // char code: 91
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\\', // char code: 92
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
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
				new double[] { 0.666669, 0.555557, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0061', // char code: 97
				new double[] { 0.666669, 0.539098, 0.033438, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0062', // char code: 98
				new double[] { 0.666669, 0.539098, 0.033438, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0063', // char code: 99
				new double[] { 0.666669, 0.555557, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0064', // char code: 100
				new double[] { 0.777781, 0.539098, 0.033438, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0065', // char code: 101
				new double[] { 0.777781, 0.539098, 0.033438, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0066', // char code: 102
				new double[] { 0.777781, 0.635971, 0.135971, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0067', // char code: 103
				new double[] { 0.777781, 0.635971, 0.135971, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0068', // char code: 104
				new double[] { 0.444446, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0069', // char code: 105
				new double[] { 0.444446, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u006A', // char code: 106
				new double[] { 0.402779, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u0091', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u006B', // char code: 107
				new double[] { 0.402779, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u0099', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u006C', // char code: 108
				new double[] { 0.38889, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u006D', // char code: 109
				new double[] { 0.38889, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u006E', // char code: 110
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u006F', // char code: 111
				new double[] { 0.777781, 0.583334, 0.083334, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0070', // char code: 112
				new double[] { 0.777781, 0.635971, 0.135971, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0071', // char code: 113
				new double[] { 0.777781, 0.635971, 0.135971, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0072', // char code: 114
				new double[] { 0.777781, 0.802638, 0.302638, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0073', // char code: 115
				new double[] { 0.777781, 0.802638, 0.302638, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0074', // char code: 116
				new double[] { 0.555557, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0075', // char code: 117
				new double[] { 0.555557, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0076', // char code: 118
				new double[] { 0.555557, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0077', // char code: 119
				new double[] { 0.555557, 0.75, 0.25, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0078', // char code: 120
				new double[] { 0.0, 0.366875, -0.133125, 0. }, // metrics:
																// width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0079', // char code: 121
				new double[] { 0.0, 0.366875, -0.133125, 0. }, // metrics:
																// width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007A', // char code: 122
				new double[] { 0.0, 0.366875, -0.133125, 0. }, // metrics:
																// width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007B', // char code: 123
				new double[] { 0.0, 0.366875, -0.133125, 0. }, // metrics:
																// width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007C', // char code: 124
				new double[] { 0.0, 0.366875, -0.133125, 0. }, // metrics:
																// width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007D', // char code: 125
				new double[] { 1.000003, 0.472223, -0.027777, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007E', // char code: 126
				new double[] { 1.000003, 0.472223, -0.027777, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007F', // char code: 127
				new double[] { 1.000003, 0.472223, -0.027777, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0080', // char code: 128
				new double[] { 0.833336, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u0088', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0081', // char code: 129
				new double[] { 0.833336, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u0089', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0082', // char code: 130
				new double[] { 0.833336, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u008A', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0083', // char code: 131
				new double[] { 0.833336, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u008B', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0084', // char code: 132
				new double[] { 0.833336, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u008C', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0085', // char code: 133
				new double[] { 0.833336, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u008D', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0086', // char code: 134
				new double[] { 0.444446, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u008E', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0087', // char code: 135
				new double[] { 0.611113, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u008F', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0088', // char code: 136
				new double[] { 1.1111145, 0.100001, 1.500012, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0089', // char code: 137
				new double[] { 1.1111145, 0.100001, 1.500012, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u008A', // char code: 138
				new double[] { 1.1111145, 0.100001, 1.500012, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u008B', // char code: 139
				new double[] { 1.1111145, 0.100001, 1.500012, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u008C', // char code: 140
				new double[] { 1.1111145, 0.100001, 1.500012, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u008D', // char code: 141
				new double[] { 1.1111145, 0.100001, 1.500012, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u008E', // char code: 142
				new double[] { 0.555557, 0.100001, 1.500012, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u008F', // char code: 143
				new double[] { 0.833336, 0.100001, 1.500012, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0090', // char code: 144
				new double[] { 0.833336, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u0098', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0091', // char code: 145
				new double[] { 0.527781, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u0092', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0092', // char code: 146
				new double[] { 0.583336, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u0093', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0093', // char code: 147
				new double[] { 0.638891, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u0094', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0094', // char code: 148
				new double[] { 0.6944475, 0.039999, 2.9600315, 0. }, // metrics:
																		// width,
																		// height,
																		// depth,
																		// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u0095', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0095', // char code: 149
				new double[] { 0.777781, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0075', TeXFont.NONE, '\u0097', '\u0076' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
		setInfo('\u0096', // char code: 150
				new double[] { 0.777781, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0097', // char code: 151
				new double[] { 0.777781, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0098', // char code: 152
				new double[] { 1.1111145, 0.100001, 1.500012, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0099', // char code: 153
				new double[] { 0.527781, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u009A', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u009A', // char code: 154
				new double[] { 0.583336, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u009B', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u009B', // char code: 155
				new double[] { 0.638891, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u009C', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u009C', // char code: 156
				new double[] { 0.6944475, 0.039999, 2.9600315, 0. }, // metrics:
																		// width,
																		// height,
																		// depth,
																		// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().stmary10, '\u009D', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u009D', // char code: 157
				new double[] { 0.777781, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u007D', TeXFont.NONE, '\u009F', '\u007E' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
		setInfo('\u009E', // char code: 158
				new double[] { 0.777781, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u009F', // char code: 159
				new double[] { 0.777781, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
	}
}
