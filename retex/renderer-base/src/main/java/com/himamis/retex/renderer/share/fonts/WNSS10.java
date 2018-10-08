/* WNSS10.java
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

import com.himamis.retex.renderer.share.UniFontInfo;

final class WNSS10 extends UniFontInfo {

	WNSS10(final String ttfPath) {
		super(95, ttfPath, 0.444445, 0.333334, 1.000003, '\u0000');
	}

	@Override
	protected final void initMetrics() {
		setInfo('\u040A', // char code: 1034
				new double[] { 1.020838, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0416', '\u0425', '\u041E', '\u0424', '\u0472',
						'\u0421', '\u0404', '\u0422', '\u042A', '\u0402',
						'\u040B', '\u0462', '\u0427', '\u0423', '\u0474',
						'\u0442', '\u044A', '\u0463', '\u0447' }, // kern codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779, -0.083334, -0.083334,
						-0.083334, -0.083334, -0.083334, -0.083334, -0.027779,
						-0.083334, -0.027779, -0.027779, -0.027779, -0.083334 }, // kern
																					// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0409', // char code: 1033
				new double[] { 1.037504, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0416', '\u0425', '\u041E', '\u0424', '\u0472',
						'\u0421', '\u0404', '\u0422', '\u042A', '\u0402',
						'\u040B', '\u0462', '\u0427', '\u0423', '\u0474',
						'\u0442', '\u044A', '\u0463', '\u0447' }, // kern codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779, -0.083334, -0.083334,
						-0.083334, -0.083334, -0.083334, -0.083334, -0.027779,
						-0.083334, -0.027779, -0.027779, -0.027779, -0.083334 }, // kern
																					// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u040F', // char code: 1039
				new double[] { 0.694448, 0.694445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u042D', // char code: 1069
				new double[] { 0.638891, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u0416', '\u0425', '\u0423',
						'\u0474' }, // kern
									// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779 }, // kern
												// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0406', // char code: 1030
				new double[] { 0.277781, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0406' }, // kern codes
				new double[] { 0.027779 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0404', // char code: 1028
				new double[] { 0.638891, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0402', // char code: 1026
				new double[] { 0.8194475, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0416', '\u0425', '\u041E', '\u0424', '\u0472',
						'\u0421', '\u0404', '\u0422', '\u042A', '\u0402',
						'\u040B', '\u0462', '\u0427', '\u0423', '\u0474',
						'\u0442', '\u044A', '\u0463', '\u0447' }, // kern codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779, -0.083334, -0.083334,
						-0.083334, -0.083334, -0.083334, -0.083334, -0.027779,
						-0.083334, -0.027779, -0.027779, -0.027779, -0.083334 }, // kern
																					// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u040B', // char code: 1035
				new double[] { 0.763891, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u045A', // char code: 1114
				new double[] { 0.765282, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0443', '\u0475', '\u0442', '\u044A', '\u0463',
						'\u0447', '\u043E', '\u0473', '\u0444', '\u0454' }, // kern
																			// codes
				new double[] { -0.055555, -0.055555, -0.027779, -0.027779,
						-0.027779, -0.083334, -0.027779, -0.027779, -0.027779,
						-0.027779 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0459', // char code: 1113
				new double[] { 0.755559, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0443', '\u0475', '\u0442', '\u044A', '\u0463',
						'\u0447', '\u043E', '\u0473', '\u0444', '\u0454' }, // kern
																			// codes
				new double[] { -0.055555, -0.055555, -0.027779, -0.027779,
						-0.027779, -0.083334, -0.027779, -0.027779, -0.027779,
						-0.027779 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u045F', // char code: 1119
				new double[] { 0.537503, 0.444445, 0.162038, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u044D', // char code: 1101
				new double[] { 0.444446, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0434', '\u0436', '\u0445', '\u044F' }, // kern
																		// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779 }, // kern
																				// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0456', // char code: 1110
				new double[] { 0.23889, 0.679365, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0454', // char code: 1108
				new double[] { 0.43889, 0.444445, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0452', // char code: 1106
				new double[] { 0.488892, 0.694445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u045B', // char code: 1115
				new double[] { 0.516668, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u042E', // char code: 1070
				new double[] { 1.04167, 0.694445, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u0416', '\u0425', '\u0423',
						'\u0474' }, // kern
									// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779 }, // kern
												// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0416', // char code: 1046
				new double[] { 1.111117, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u041E', '\u0424', '\u0472', '\u0421', '\u0404',
						'\u0447', '\u0442', '\u044A', '\u0463' }, // kern
																	// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779, -0.027779, -0.027779 }, // kern
																					// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0419', // char code: 1049
				new double[] { 0.694448, 0.902727, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0401', // char code: 1025
				new double[] { 0.597224, 0.902727, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0474', // char code: 1140
				new double[] { 0.722226, 0.694445, 0., 0.013888 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u041B', '\u0409', '\u041E',
						'\u0424', '\u0472', '\u0421', '\u0404', '\u0430',
						'\u043E', '\u0473', '\u0435', '\u0451', '\u0434',
						'\u043B', '\u0459', '\u044F' }, // kern
														// codes
				new double[] { -0.083334, -0.055555, -0.055555, -0.055555,
						-0.027779, -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779, -0.027779, -0.027779,
						-0.083334, -0.083334, -0.083334, -0.083334 }, // kern
																		// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0472', // char code: 1138
				new double[] { 0.777781, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u0416', '\u0425', '\u0423',
						'\u0474' }, // kern
									// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779 }, // kern
												// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0405', // char code: 1029
				new double[] { 0.555557, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u042F', // char code: 1071
				new double[] { 0.645836, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u044E', // char code: 1102
				new double[] { 0.730558, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0434', '\u0436', '\u0445', '\u044F' }, // kern
																		// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779 }, // kern
																				// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0436', // char code: 1078
				new double[] { 0.7388935, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0430', '\u043E', '\u0473', '\u0441', '\u0454',
						'\u0435', '\u0451' }, // kern
												// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779 }, // kern
															// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0439', // char code: 1081
				new double[] { 0.537503, 0.652727, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0451', // char code: 1105
				new double[] { 0.444446, 0.660319, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0475', // char code: 1141
				new double[] { 0.491667, 0.444445, 0., 0.013888 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0430', '\u043E', '\u0473', '\u0441', '\u0454',
						'\u0435', '\u0451', '\u044F', '\u0434', '\u043B',
						'\u0459' }, // kern codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779, -0.027779, -0.055555,
						-0.055555, -0.055555 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0473', // char code: 1139
				new double[] { 0.500002, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0434', '\u0436', '\u0445', '\u044F' }, // kern
																		// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779 }, // kern
																				// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0455', // char code: 1109
				new double[] { 0.383334, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u044F', // char code: 1103
				new double[] { 0.515279, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0308', // char code: 776
				new double[] { 0.500002, 0.660319, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0462', // char code: 1122
				new double[] { 0.7777815, 0.75, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0416', '\u0425', '\u041E', '\u0424', '\u0472',
						'\u0421', '\u0404', '\u0422', '\u042A', '\u0402',
						'\u040B', '\u0462', '\u0427', '\u0423', '\u0474',
						'\u0442', '\u044A', '\u0463', '\u0447' }, // kern codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779, -0.083334, -0.083334,
						-0.083334, -0.083334, -0.083334, -0.083334, -0.027779,
						-0.083334, -0.027779, -0.027779, -0.027779, -0.083334 }, // kern
																					// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0306', // char code: 774
				new double[] { 0.500002, 0.652727, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0463', // char code: 1123
				new double[] { 0.500002, 0.652727, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0443', '\u0475', '\u0442', '\u044A', '\u0463',
						'\u0447', '\u043E', '\u0473', '\u0444', '\u0454' }, // kern
																			// codes
				new double[] { -0.055555, -0.055555, -0.027779, -0.027779,
						-0.027779, -0.083334, -0.027779, -0.027779, -0.027779,
						-0.027779 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00AB', // char code: 171
				new double[] { 0.666669, 0.438889, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0131', // char code: 305
				new double[] { 0.23889, 0.444445, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BB', // char code: 187
				new double[] { 0.666669, 0.438889, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0410', // char code: 1040
				new double[] { 0.66667, 0.694445, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u041E', '\u0424', '\u0472', '\u0421', '\u0404',
						'\u0422', '\u042A', '\u0402', '\u040B', '\u0462',
						'\u0427', '\u0423', '\u0474', '\u0442', '\u044A',
						'\u0463', '\u0447' }, // kern
												// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.083334, -0.083334, -0.083334, -0.083334,
						-0.083334, -0.083334, -0.027779, -0.083334, -0.027779,
						-0.027779, -0.027779, -0.083334 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0411', // char code: 1041
				new double[] { 0.66667, 0.694445, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0426', // char code: 1062
				new double[] { 0.711116, 0.694445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0414', // char code: 1044
				new double[] { 0.727783, 0.694445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0415', // char code: 1045
				new double[] { 0.597224, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0424', // char code: 1060
				new double[] { 0.833336, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u0416', '\u0425', '\u0423',
						'\u0474' }, // kern
									// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779 }, // kern
												// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0413', // char code: 1043
				new double[] { 0.541669, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u041B', '\u0409', '\u0430',
						'\u043E', '\u0473', '\u0435', '\u0451', '\u0441',
						'\u0454', '\u0444', '\u0434', '\u043B', '\u0459',
						'\u044F' }, // kern
									// codes
				new double[] { -0.083334, -0.083334, -0.055555, -0.055555,
						-0.083334, -0.083334, -0.083334, -0.083334, -0.083334,
						-0.083334, -0.083334, -0.083334, -0.083334, -0.083334,
						-0.083334, -0.083334 }, // kern
												// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0425', // char code: 1061
				new double[] { 0.66667, 0.694445, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u041E', '\u0424', '\u0472', '\u0421', '\u0404',
						'\u0447', '\u0442', '\u044A', '\u0463' }, // kern
																	// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779, -0.027779, -0.027779 }, // kern
																					// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0418', // char code: 1048
				new double[] { 0.694448, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0408', // char code: 1032
				new double[] { 0.472224, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u041A', // char code: 1050
				new double[] { 0.694448, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u041E', '\u0424', '\u0472', '\u0421', '\u0404',
						'\u0447', '\u0442', '\u044A', '\u0463' }, // kern
																	// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779, -0.027779, -0.027779 }, // kern
																					// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u041B', // char code: 1051
				new double[] { 0.711116, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u041C', // char code: 1052
				new double[] { 0.875005, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u041D', // char code: 1053
				new double[] { 0.694448, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u041E', // char code: 1054
				new double[] { 0.736113, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u0416', '\u0425', '\u0423',
						'\u0474' }, // kern
									// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779 }, // kern
												// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u041F', // char code: 1055
				new double[] { 0.694448, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0427', // char code: 1063
				new double[] { 0.694448, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0420', // char code: 1056
				new double[] { 0.638891, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u041B', '\u0409', '\u0430',
						'\u043E', '\u0473', '\u0435', '\u0451', '\u0434',
						'\u043B', '\u0459' }, // kern codes
				new double[] { -0.083334, -0.083334, -0.083334, -0.083334,
						-0.027779, -0.027779, -0.027779, -0.027779, -0.027779,
						-0.083334, -0.083334, -0.083334 }, // kern
															// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0421', // char code: 1057
				new double[] { 0.638891, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0422', // char code: 1058
				new double[] { 0.680557, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u041B', '\u0409', '\u0430',
						'\u043E', '\u0473', '\u0435', '\u0451', '\u0441',
						'\u0454', '\u0444', '\u0434', '\u043B', '\u0459',
						'\u044F' }, // kern
									// codes
				new double[] { -0.083334, -0.083334, -0.055555, -0.055555,
						-0.083334, -0.083334, -0.083334, -0.083334, -0.083334,
						-0.083334, -0.083334, -0.083334, -0.083334, -0.083334,
						-0.083334, -0.083334 }, // kern
												// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0423', // char code: 1059
				new double[] { 0.66667, 0.694445, 0., 0.013888 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u041B', '\u0409', '\u041E',
						'\u0424', '\u0472', '\u0421', '\u0404', '\u0430',
						'\u043E', '\u0473', '\u0435', '\u0451', '\u0441',
						'\u0454', '\u0434', '\u043B', '\u0459', '\u044F' }, // kern
																			// codes
				new double[] { -0.027779, -0.055555, -0.055555, -0.055555,
						-0.027779, -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.083334, -0.083334, -0.083334,
						-0.083334 }, // kern
										// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0412', // char code: 1042
				new double[] { 0.66667, 0.694445, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0429', // char code: 1065
				new double[] { 1.100006, 0.694445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0428', // char code: 1064
				new double[] { 1.083339, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u042B', // char code: 1067
				new double[] { 0.888895, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0417', // char code: 1047
				new double[] { 0.611113, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u042C', // char code: 1068
				new double[] { 0.66667, 0.694445, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0416', '\u0425', '\u041E', '\u0424', '\u0472',
						'\u0421', '\u0404', '\u0422', '\u042A', '\u0402',
						'\u040B', '\u0462', '\u0427', '\u0423', '\u0474',
						'\u0442', '\u044A', '\u0463', '\u0447' }, // kern codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779, -0.083334, -0.083334,
						-0.083334, -0.083334, -0.083334, -0.083334, -0.027779,
						-0.083334, -0.027779, -0.027779, -0.027779, -0.083334 }, // kern
																					// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u042A', // char code: 1066
				new double[] { 0.868059, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0416', '\u0425', '\u041E', '\u0424', '\u0472',
						'\u0421', '\u0404', '\u0422', '\u042A', '\u0402',
						'\u040B', '\u0462', '\u0427', '\u0423', '\u0474',
						'\u0442', '\u044A', '\u0463', '\u0447' }, // kern codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779, -0.083334, -0.083334,
						-0.083334, -0.083334, -0.083334, -0.083334, -0.027779,
						-0.083334, -0.027779, -0.027779, -0.027779, -0.083334 }, // kern
																					// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0430', // char code: 1072
				new double[] { 0.480557, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0447', '\u0443' }, // kern codes
				new double[] { -0.027779, -0.027779 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0431', // char code: 1073
				new double[] { 0.500002, 0.694445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0434', '\u0436', '\u0445', '\u044F' }, // kern
																		// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779 }, // kern
																				// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0446', // char code: 1094
				new double[] { 0.5486145, 0.444445, 0.162038, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0434', // char code: 1076
				new double[] { 0.538892, 0.444445, 0.162038, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0435', // char code: 1077
				new double[] { 0.444446, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0444', // char code: 1092
				new double[] { 0.76667, 0.694445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0434', '\u0436', '\u0445', '\u044F' }, // kern
																		// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779 }, // kern
																				// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0433', // char code: 1075
				new double[] { 0.404167, 0.444445, 0., 0.013888 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0434', '\u043B', '\u0459', '\u044F' }, // kern
																		// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779 }, // kern
																				// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0445', // char code: 1093
				new double[] { 0.461113, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0430', '\u043E', '\u0473', '\u0441', '\u0454',
						'\u0435', '\u0451' }, // kern
												// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779 }, // kern
															// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0438', // char code: 1080
				new double[] { 0.537503, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0458', // char code: 1112
				new double[] { 0.266668, 0.679365, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u043A', // char code: 1082
				new double[] { 0.488892, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0430', '\u043E', '\u0473', '\u0441', '\u0454',
						'\u0435', '\u0451' }, // kern
												// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779 }, // kern
															// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u043B', // char code: 1083
				new double[] { 0.527781, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u043C', // char code: 1084
				new double[] { 0.669447, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u043D', // char code: 1085
				new double[] { 0.516668, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u043E', // char code: 1086
				new double[] { 0.500002, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0434', '\u0436', '\u0445', '\u044F' }, // kern
																		// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779 }, // kern
																				// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u043F', // char code: 1087
				new double[] { 0.516668, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0447', // char code: 1095
				new double[] { 0.537503, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0440', // char code: 1088
				new double[] { 0.516668, 0.444445, 0.194445, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0434', '\u0436', '\u0445', '\u044F' }, // kern
																		// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779 }, // kern
																				// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0441', // char code: 1089
				new double[] { 0.444446, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0434', '\u0436', '\u0445', '\u044F' }, // kern
																		// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779 }, // kern
																				// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0442', // char code: 1090
				new double[] { 0.458334, 0.444445, 0., 0.019444 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0434', '\u043B', '\u0459', '\u044F' }, // kern
																		// codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779 }, // kern
																				// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0443', // char code: 1091
				new double[] { 0.461113, 0.444445, 0.194445, 0.013888 }, // metrics:
																			// width,
																			// height,
																			// depth,
																			// italic
				null, // ligatures
				new char[] { '\u0430', '\u043E', '\u0473', '\u0441', '\u0454',
						'\u0435', '\u0451', '\u044F', '\u0434', '\u043B',
						'\u0459' }, // kern codes
				new double[] { -0.027779, -0.027779, -0.027779, -0.027779,
						-0.027779, -0.027779, -0.027779, -0.027779, -0.055555,
						-0.055555, -0.055555 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0432', // char code: 1074
				new double[] { 0.480557, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0449', // char code: 1097
				new double[] { 0.7777815, 0.444445, 0.162038, 0. }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0448', // char code: 1096
				new double[] { 0.76667, 0.444445, 0., 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u044B', // char code: 1099
				new double[] { 0.683336, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0437', // char code: 1079
				new double[] { 0.444446, 0.444445, 0., 0.002777 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u044C', // char code: 1100
				new double[] { 0.480557, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0443', '\u0475', '\u0442', '\u044A', '\u0463',
						'\u0447', '\u043E', '\u0473', '\u0444', '\u0454' }, // kern
																			// codes
				new double[] { -0.055555, -0.055555, -0.027779, -0.027779,
						-0.027779, -0.083334, -0.027779, -0.027779, -0.027779,
						-0.027779 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u044A', // char code: 1098
				new double[] { 0.590279, 0.444445, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0443', '\u0475', '\u0442', '\u044A', '\u0463',
						'\u0447', '\u043E', '\u0473', '\u0444', '\u0454' }, // kern
																			// codes
				new double[] { -0.055555, -0.055555, -0.027779, -0.027779,
						-0.027779, -0.083334, -0.027779, -0.027779, -0.027779,
						-0.027779 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
	}
}
