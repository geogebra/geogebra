/* MOUSTACHE.java
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

import com.himamis.retex.renderer.share.FontInfo;
import com.himamis.retex.renderer.share.TeXFont;

final class MOUSTACHE extends FontInfo {

	MOUSTACHE(final String ttfPath) {
		super(0, ttfPath, 0.430555, 0.0, 1.000003, '\u0000');
	}

	@Override
	protected final void initMetrics() {
		setInfo('\u0038', // char code: 56
				new double[] { 0.888891, 0., 0.900009, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0039', // char code: 57
				new double[] { 0.888891, 0., 0.900009, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u003A', // char code: 58
				new double[] { 0.888891, 0., 0.900009, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u003B', // char code: 59
				new double[] { 0.888891, 0., 0.900009, 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
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
		setInfo('\u0040', // char code: 64
				new double[] { 0.458336, 0, 0, 0. }, // metrics: width, height,
														// depth, italic
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
				new double[] { 0.875003, 0, 0, 0. }, // metrics: width, height,
														// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				new char[] { '\u0039', TeXFont.NONE, '\u003E', '\u003A' }); // extension:
																			// top,
																			// mid,
																			// rep,
																			// bot
	}
}
