/* CMEX10.java
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

final class CMEX10 extends FontInfo {

	CMEX10(final String ttfPath) {
		super(0, ttfPath, 0.430555, 0.0, 1.000003, '\u0000');
	}

	@Override
	protected final void initMetrics() {
		setInfo('\u00A1', // char code: 161
				new double[] { 0.458336, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00B3', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00A2', // char code: 162
				new double[] { 0.458336, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00B4', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00A3', // char code: 163
				new double[] { 0.416669, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0068', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00A4', // char code: 164
				new double[] { 0.416669, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0069', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00A5', // char code: 165
				new double[] { 0.472224, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u006A', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00A6', // char code: 166
				new double[] { 0.472224, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u006B', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00A7', // char code: 167
				new double[] { 0.472224, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u006C', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00A8', // char code: 168
				new double[] { 0.472224, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u006D', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00A9', // char code: 169
				new double[] { 0.583336, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u006E', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00AA', // char code: 170
				new double[] { 0.583336, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u006F', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00AB', // char code: 171
				new double[] { 0.472224, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0044', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00AE', // char code: 174
				new double[] { 0.472224, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0045', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00AF', // char code: 175
				new double[] { 0.333334, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { TeXFont.NONE, TeXFont.NONE, '\u00AF',
						TeXFont.NONE }); // extension:
											// top,
											// mid,
											// rep,
											// bot
		setInfo('\u00B0', // char code: 176
				new double[] { 0.555557, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { TeXFont.NONE, TeXFont.NONE, '\u00B0',
						TeXFont.NONE }); // extension:
											// top,
											// mid,
											// rep,
											// bot
		setInfo('\u00B1', // char code: 177
				new double[] { 0.577779, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u002E', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B2', // char code: 178
				new double[] { 0.577779, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u002F', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B3', // char code: 179
				new double[] { 0.597224, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00B5', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B4', // char code: 180
				new double[] { 0.597224, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00B6', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B5', // char code: 181
				new double[] { 0.7361145, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00C3', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B6', // char code: 182
				new double[] { 0.7361145, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0021', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B7', // char code: 183
				new double[] { 0.527781, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\"', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B8', // char code: 184
				new double[] { 0.527781, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0023', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00B9', // char code: 185
				new double[] { 0.583336, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0024', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BA', // char code: 186
				new double[] { 0.583336, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0025', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BB', // char code: 187
				new double[] { 0.583336, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0026', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BC', // char code: 188
				new double[] { 0.583336, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\'', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BD', // char code: 189
				new double[] { 0.750002, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0028', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BE', // char code: 190
				new double[] { 0.750002, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0029', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BF', // char code: 191
				new double[] { 0.750002, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u002A', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00C0', // char code: 192
				new double[] { 0.750002, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u002B', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00C1', // char code: 193
				new double[] { 1.044447, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u002C', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00C2', // char code: 194
				new double[] { 1.044447, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u002D', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00C3', // char code: 195
				new double[] { 0.79167, 0.039999, 2.9600315, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0030', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0021', // char code: 33
				new double[] { 0.79167, 0.039999, 2.9600315, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0031', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\"', // char code: 34
				new double[] { 0.583336, 0.039999, 2.9600315, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0032', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0023', // char code: 35
				new double[] { 0.583336, 0.039999, 2.9600315, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0033', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0024', // char code: 36
				new double[] { 0.638891, 0.039999, 2.9600315, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0034', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0025', // char code: 37
				new double[] { 0.638891, 0.039999, 2.9600315, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0035', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0026', // char code: 38
				new double[] { 0.638891, 0.039999, 2.9600315, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0036', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\'', // char code: 39
				new double[] { 0.638891, 0.039999, 2.9600315, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0037', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0028', // char code: 40
				new double[] { 0.80556, 0.039999, 2.9600315, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0038', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0029', // char code: 41
				new double[] { 0.80556, 0.039999, 2.9600315, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0039', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u002A', // char code: 42
				new double[] { 0.80556, 0.039999, 2.9600315, 0. }, // metrics:
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
				new double[] { 0.80556, 0.039999, 2.9600315, 0. }, // metrics:
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
				new double[] { 1.2777815, 0.039999, 2.9600315, 0. }, // metrics:
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
				new double[] { 1.2777815, 0.039999, 2.9600315, 0. }, // metrics:
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
				new double[] { 0.811114, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00C1', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u002F', // char code: 47
				new double[] { 0.811114, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00C2', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0030', // char code: 48
				new double[] { 0.875003, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0030', TeXFont.NONE, '\u0042', '\u0040' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
		setInfo('\u0031', // char code: 49
				new double[] { 0.875003, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0031', TeXFont.NONE, '\u0043', '\u0041' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
		setInfo('\u0032', // char code: 50
				new double[] { 0.666669, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0032', TeXFont.NONE, '\u0036', '\u0034' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
		setInfo('\u0033', // char code: 51
				new double[] { 0.666669, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0033', TeXFont.NONE, '\u0037', '\u0035' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
		setInfo('\u0034', // char code: 52
				new double[] { 0.666669, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { TeXFont.NONE, TeXFont.NONE, '\u0036', '\u0034' }); // extension:
																				// top,
																				// mid,
																				// rep,
																				// bot
		setInfo('\u0035', // char code: 53
				new double[] { 0.666669, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { TeXFont.NONE, TeXFont.NONE, '\u0037', '\u0035' }); // extension:
																				// top,
																				// mid,
																				// rep,
																				// bot
		setInfo('\u0036', // char code: 54
				new double[] { 0.666669, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0032', TeXFont.NONE, '\u0036', TeXFont.NONE }); // extension:
																				// top,
																				// mid,
																				// rep,
																				// bot
		setInfo('\u0037', // char code: 55
				new double[] { 0.666669, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0033', TeXFont.NONE, '\u0037', TeXFont.NONE }); // extension:
																				// top,
																				// mid,
																				// rep,
																				// bot
		setInfo('\u0038', // char code: 56
				new double[] { 0.888891, 0., 0.900009, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0038', '\u003C', '\u003E', '\u003A' }); // extension:
																		// top,
																		// mid,
																		// rep,
																		// bot
		setInfo('\u0039', // char code: 57
				new double[] { 0.888891, 0., 0.900009, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0039', '\u003D', '\u003E', '\u003B' }); // extension:
																		// top,
																		// mid,
																		// rep,
																		// bot
		setInfo('\u003A', // char code: 58
				new double[] { 0.888891, 0., 0.900009, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0038', TeXFont.NONE, '\u003E', '\u003A' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
		setInfo('\u003B', // char code: 59
				new double[] { 0.888891, 0., 0.900009, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0039', TeXFont.NONE, '\u003E', '\u003B' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
		setInfo('\u003C', // char code: 60
				new double[] { 0.888891, 0., 1.800018, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { TeXFont.NONE, TeXFont.NONE, '\u003F',
						TeXFont.NONE }); // extension:
											// top,
											// mid,
											// rep,
											// bot
		setInfo('\u003D', // char code: 61
				new double[] { 0.888891, 0., 1.800018, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { TeXFont.NONE, TeXFont.NONE, '\u0077',
						TeXFont.NONE }); // extension:
											// top,
											// mid,
											// rep,
											// bot
		setInfo('\u003E', // char code: 62
				new double[] { 0.888891, 0., 0.300003, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { TeXFont.NONE, TeXFont.NONE, '\u003E',
						TeXFont.NONE }); // extension:
											// top,
											// mid,
											// rep,
											// bot
		setInfo('\u003F', // char code: 63
				new double[] { 0.666669, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0078', TeXFont.NONE, '\u003F', '\u0079' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
		setInfo('\u0040', // char code: 64
				new double[] { 0.875003, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0038', TeXFont.NONE, '\u003E', '\u003B' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
		setInfo('\u0041', // char code: 65
				new double[] { 0.875003, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0039', TeXFont.NONE, '\u003E', '\u003A' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
		setInfo('\u0042', // char code: 66
				new double[] { 0.875003, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { TeXFont.NONE, TeXFont.NONE, '\u0042',
						TeXFont.NONE }); // extension:
											// top,
											// mid,
											// rep,
											// bot
		setInfo('\u0043', // char code: 67
				new double[] { 0.875003, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { TeXFont.NONE, TeXFont.NONE, '\u0043',
						TeXFont.NONE }); // extension:
											// top,
											// mid,
											// rep,
											// bot
		setInfo('\u0044', // char code: 68
				new double[] { 0.611113, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00BF', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0045', // char code: 69
				new double[] { 0.611113, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00C0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0046', // char code: 70
				new double[] { 0.833336, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0047', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0047', // char code: 71
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
		setInfo('\u0048', // char code: 72
				new double[] { 0.472223, 0., 1.111122, 0.194446 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0049', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0049', // char code: 73
				new double[] { 0.555557, 0., 2.222246, 0.444446 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u004A', // char code: 74
				new double[] { 1.1111145, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u004B', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u004B', // char code: 75
				new double[] { 1.511116, 0.100001, 1.500012, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u004C', // char code: 76
				new double[] { 1.1111145, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u004D', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u004D', // char code: 77
				new double[] { 1.511116, 0.100001, 1.500012, 0. }, // metrics:
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
				new double[] { 1.1111145, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u004F', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u004F', // char code: 79
				new double[] { 1.511116, 0.100001, 1.500012, 0. }, // metrics:
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
				new double[] { 1.055559, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0058', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0051', // char code: 81
				new double[] { 0.9444475, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0059', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0052', // char code: 82
				new double[] { 0.472223, 0., 1.111122, 0.194446 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u005A', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0053', // char code: 83
				new double[] { 0.833336, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u005B', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0054', // char code: 84
				new double[] { 0.833336, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\\', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0055', // char code: 85
				new double[] { 0.833336, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u005D', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0056', // char code: 86
				new double[] { 0.833336, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u005E', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0057', // char code: 87
				new double[] { 0.833336, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u005F', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0058', // char code: 88
				new double[] { 1.444448, 0.100001, 1.500012, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0059', // char code: 89
				new double[] { 1.2777815, 0.100001, 1.500012, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u005A', // char code: 90
				new double[] { 0.555557, 0., 2.222246, 0.444446 }, // metrics:
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
		setInfo('\\', // char code: 92
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
		setInfo('\u005D', // char code: 93
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
		setInfo('\u005E', // char code: 94
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
		setInfo('\u005F', // char code: 95
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
		setInfo('\u0060', // char code: 96
				new double[] { 0.9444475, 0., 1.000013, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0061', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0061', // char code: 97
				new double[] { 1.2777815, 0.100001, 1.500012, 0. }, // metrics:
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
				new double[] { 0.555557, 0.722223, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0063', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0063', // char code: 99
				new double[] { 1.000003, 0.75, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0064', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0064', // char code: 100
				new double[] { 1.444448, 0.75, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().msbm10, '\u005B', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0065', // char code: 101
				new double[] { 0.555557, 0.722223, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0066', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0066', // char code: 102
				new double[] { 1.000003, 0.75, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0067', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0067', // char code: 103
				new double[] { 1.444448, 0.75, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().msbm10, '\u005D', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0068', // char code: 104
				new double[] { 0.472224, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00B7', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0069', // char code: 105
				new double[] { 0.472224, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00B8', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u006A', // char code: 106
				new double[] { 0.527781, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00B9', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u006B', // char code: 107
				new double[] { 0.527781, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00BA', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u006C', // char code: 108
				new double[] { 0.527781, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00BB', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u006D', // char code: 109
				new double[] { 0.527781, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00BC', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u006E', // char code: 110
				new double[] { 0.666669, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00BD', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u006F', // char code: 111
				new double[] { 0.666669, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u00BE', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0070', // char code: 112
				new double[] { 1.000003, 0.039999, 1.160013, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0071', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0071', // char code: 113
				new double[] { 1.000003, 0.039999, 1.760019, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0072', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0072', // char code: 114
				new double[] { 1.000003, 0.039999, 2.360025, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0073', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0073', // char code: 115
				new double[] { 1.000003, 0.039999, 2.9600315, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				Configuration.getFonts().cmex10, '\u0074', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0074', // char code: 116
				new double[] { 1.055559, 0., 1.800018, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0076', TeXFont.NONE, '\u0075', '\u0074' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
		setInfo('\u0075', // char code: 117
				new double[] { 1.055559, 0., 0.585006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0076', // char code: 118
				new double[] { 1.055559, 0.039999, 0.550007, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0077', // char code: 119
				new double[] { 0.777781, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u007E', TeXFont.NONE, '\u0077', '\u00C4' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
		setInfo('\u0078', // char code: 120
				new double[] { 0.666669, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0078', TeXFont.NONE, '\u003F', TeXFont.NONE }); // extension:
																				// top,
																				// mid,
																				// rep,
																				// bot
		setInfo('\u0079', // char code: 121
				new double[] { 0.666669, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { TeXFont.NONE, TeXFont.NONE, '\u003F', '\u0079' }); // extension:
																				// top,
																				// mid,
																				// rep,
																				// bot
		setInfo('\u007A', // char code: 122
				new double[] { 0.450005, 0.119998, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007B', // char code: 123
				new double[] { 0.450005, 0.119998, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007C', // char code: 124
				new double[] { 0.450005, 0.119998, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007D', // char code: 125
				new double[] { 0.450005, 0.119998, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u007E', // char code: 126
				new double[] { 0.777781, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u007E', TeXFont.NONE, '\u0077', TeXFont.NONE }); // extension:
																				// top,
																				// mid,
																				// rep,
																				// bot
		setInfo('\u00C4', // char code: 196
				new double[] { 0.777781, 0., 0.600006, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { TeXFont.NONE, TeXFont.NONE, '\u0077', '\u00C4' }); // extension:
																				// top,
																				// mid,
																				// rep,
																				// bot
	}
}
