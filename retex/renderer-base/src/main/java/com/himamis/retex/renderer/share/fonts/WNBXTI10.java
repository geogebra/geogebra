/* WNBXTI10.java
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

final class WNBXTI10 extends UniFontInfo {

	WNBXTI10(final String ttfPath) {
		super(95, ttfPath, 0.444445, 0.414441, 1.182211, '\u0000');
	}

	@Override
	protected final void initMetrics() {
		setInfo('\u040A', // char code: 1034
				new double[] { 1.198877, 0.686111, 0., 0.032991 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0416', '\u0425', '\u041E', '\u0424', '\u0472',
						'\u0421', '\u0404', '\u0422', '\u042A', '\u0402',
						'\u040B', '\u0462', '\u0427', '\u0423', '\u0474',
						'\u044A', '\u0475', '\u043A', '\u043D', '\u045A',
						'\u043F', '\u0442', '\u044E', '\u0463', '\u0456',
						'\u0438', '\u0439', '\u0446', '\u0448', '\u0449',
						'\u044C', '\u044B', '\u0443' }, // kern
														// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.117777, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445 }, // kern
																		// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0409', // char code: 1033
				new double[] { 1.198877, 0.686111, 0., 0.032991 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0416', '\u0425', '\u041E', '\u0424', '\u0472',
						'\u0421', '\u0404', '\u0422', '\u042A', '\u0402',
						'\u040B', '\u0462', '\u0427', '\u0423', '\u0474',
						'\u044A', '\u0475', '\u043A', '\u043D', '\u045A',
						'\u043F', '\u0442', '\u044E', '\u0463', '\u0456',
						'\u0438', '\u0439', '\u0446', '\u0448', '\u0449',
						'\u044C', '\u044B', '\u0443' }, // kern
														// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.117777, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445 }, // kern
																		// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u040F', // char code: 1039
				new double[] { 0.894992, 0.686111, 0.194445, 0.172084 }, // metrics:
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
				new double[] { 0.826658, 0.686111, 0., 0.090625 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u0416', '\u0425', '\u0423',
						'\u0474', '\u042F' }, // kern
												// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445 }, // kern
															// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0406', // char code: 1030
				new double[] { 0.471664, 0.686111, 0., 0.156807 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0406' }, // kern codes
				new double[] { 0.029445 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0404', // char code: 1028
				new double[] { 0.826658, 0.686111, 0., 0.142084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0402', // char code: 1026
				new double[] { 0.943324, 0.686111, 0., 0.12903 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0416', '\u0425', '\u041E', '\u0424', '\u0472',
						'\u0421', '\u0404', '\u0422', '\u042A', '\u0402',
						'\u040B', '\u0462', '\u0427', '\u0423', '\u0474',
						'\u044A', '\u0475', '\u043A', '\u043D', '\u045A',
						'\u043F', '\u0442', '\u044E', '\u0463', '\u0456',
						'\u0438', '\u0439', '\u0446', '\u0448', '\u0449',
						'\u044C', '\u044B', '\u0443' }, // kern
														// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.117777, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445 }, // kern
																		// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u040B', // char code: 1035
				new double[] { 0.870825, 0.686111, 0., 0.084864 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u045A', // char code: 1114
				new double[] { 0.82666, 0.444445, 0., 0.078611 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u043B', '\u043C', '\u0459', '\u0475', '\u044A',
						'\u0447', '\u043E', '\u0473', '\u0444', '\u0454' }, // kern
																			// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.088333, -0.029445, -0.029445, -0.029445,
						-0.029445 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0459', // char code: 1113
				new double[] { 0.797215, 0.444445, 0., 0.078611 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u043B', '\u043C', '\u0459', '\u0475', '\u044A',
						'\u0447', '\u043E', '\u0473', '\u0444', '\u0454' }, // kern
																			// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.088333, -0.029445, -0.029445, -0.029445,
						-0.029445 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u045F', // char code: 1119
				new double[] { 0.62055, 0.444445, 0.194445, 0.094261 }, // metrics:
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
				new double[] { 0.511606, 0.444445, 0., 0.078611 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0444', '\u0430', '\u043B', '\u043C', '\u0459' }, // kern
																					// codes
				new double[] { -0.058888, -0.058888, -0.029445, -0.029445,
						-0.029445 }, // kern
										// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0456', // char code: 1110
				new double[] { 0.355553, 0.693255, 0., 0.113872 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0454', // char code: 1108
				new double[] { 0.511606, 0.444445, 0., 0.081667 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0452', // char code: 1106
				new double[] { 0.532217, 0.694445, 0.194445, 0.077777 }, // metrics:
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
				new double[] { 0.591105, 0.694445, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u042E', // char code: 1070
				new double[] { 1.236933, 0.686111, 0., 0.090625 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u0416', '\u0425', '\u0423',
						'\u0474', '\u042F' }, // kern
												// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445 }, // kern
															// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0416', // char code: 1046
				new double[] { 1.318319, 0.686111, 0., 0.142084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u041E', '\u0424', '\u0472', '\u0421', '\u0404',
						'\u0447' }, // kern
									// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445 }, // kern
												// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0419', // char code: 1049
				new double[] { 0.894992, 0.894394, 0., 0.172084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0401', // char code: 1025
				new double[] { 0.756659, 0.894394, 0., 0.114306 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0474', // char code: 1140
				new double[] { 0.934436, 0.686111, 0., 0.186251 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0410', '\u042F', '\u041E', '\u0424', '\u0472',
						'\u0421', '\u0404', '\u0430', '\u043E', '\u0473',
						'\u0435', '\u0451', '\u0438', '\u0439', '\u0446',
						'\u0448', '\u0449', '\u044C', '\u044B', '\u0443',
						'\u0475', '\u044A', '\u043B', '\u043C', '\u0459' }, // kern
																			// codes
				new double[] { -0.117777, -0.117777, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.117777, -0.117777,
						-0.117777, -0.117777, -0.117777, -0.117777, -0.117777,
						-0.117777, -0.117777, -0.117777, -0.117777, -0.117777,
						-0.117777 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0472', // char code: 1138
				new double[] { 0.885547, 0.686111, 0., 0.090625 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u0416', '\u0425', '\u0423',
						'\u0474', '\u042F' }, // kern
												// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445 }, // kern
															// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0405', // char code: 1029
				new double[] { 0.649994, 0.686111, 0., 0.11264 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u042F', // char code: 1071
				new double[] { 0.894992, 0.686111, 0., 0.172084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u044E', // char code: 1102
				new double[] { 0.835492, 0.444445, 0., 0.078611 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0444', '\u0430', '\u043B', '\u043C', '\u0459' }, // kern
																					// codes
				new double[] { -0.058888, -0.058888, -0.029445, -0.029445,
						-0.029445 }, // kern
										// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0436', // char code: 1078
				new double[] { 1.209432, 0.444445, 0., 0.052223 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0439', // char code: 1081
				new double[] { 0.649994, 0.652727, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0451', // char code: 1105
				new double[] { 0.511606, 0.686111, 0., 0.085002 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0444', '\u0430', '\u043B', '\u043C', '\u0459' }, // kern
																					// codes
				new double[] { -0.058888, -0.058888, -0.029445, -0.029445,
						-0.029445 }, // kern
										// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0475', // char code: 1141
				new double[] { 0.723051, 0.444445, 0., 0.1258335 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u043B', '\u043C', '\u0459' }, // kern codes
				new double[] { -0.088333, -0.088333, -0.088333 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0473', // char code: 1139
				new double[] { 0.532217, 0.444445, 0., 0.078611 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0444', '\u0430', '\u043B', '\u043C', '\u0459' }, // kern
																					// codes
				new double[] { -0.058888, -0.058888, -0.029445, -0.029445,
						-0.029445 }, // kern
										// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0455', // char code: 1109
				new double[] { 0.486941, 0.444445, 0., 0.081667 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u044F', // char code: 1103
				new double[] { 0.62055, 0.444445, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0308', // char code: 776
				new double[] { 0.591105, 0.686111, 0., 0.112642 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0462', // char code: 1122
				new double[] { 0.934436, 0.75, 0., 0.099202 }, // metrics:
																// width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0416', '\u0425', '\u041E', '\u0424', '\u0472',
						'\u0421', '\u0404', '\u0422', '\u042A', '\u0402',
						'\u040B', '\u0462', '\u0427', '\u0423', '\u0474',
						'\u044A', '\u0475', '\u043A', '\u043D', '\u045A',
						'\u043F', '\u0442', '\u044E', '\u0463', '\u0456',
						'\u0438', '\u0439', '\u0446', '\u0448', '\u0449',
						'\u044C', '\u044B', '\u0443' }, // kern
														// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.117777, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445 }, // kern
																		// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0306', // char code: 774
				new double[] { 0.591105, 0.652727, 0., 0.092905 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0463', // char code: 1123
				new double[] { 0.826658, 0.444445, 0., 0.078611 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u043B', '\u043C', '\u0459', '\u0475', '\u044A',
						'\u0447', '\u043E', '\u0473', '\u0444', '\u0454' }, // kern
																			// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.088333, -0.029445, -0.029445, -0.029445,
						-0.029445 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00AB', // char code: 171
				new double[] { 0.649994, 0.472223, 0., 0.008611 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0131', // char code: 305
				new double[] { 0.355553, 0.444445, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00BB', // char code: 187
				new double[] { 0.649994, 0.472223, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0410', // char code: 1040
				new double[] { 0.865547, 0.686111, 0., 0. }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u041E', '\u0424', '\u0472', '\u0421', '\u0404',
						'\u0422', '\u042A', '\u0402', '\u040B', '\u0462',
						'\u0427', '\u0423', '\u0474', '\u044A', '\u0475',
						'\u043A', '\u043D', '\u045A', '\u043F', '\u0442',
						'\u044E', '\u0463', '\u0456', '\u0438', '\u0439',
						'\u0446', '\u0448', '\u0449', '\u044C', '\u044B',
						'\u0443' }, // kern codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.117777, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445 }, // kern
												// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0411', // char code: 1041
				new double[] { 0.81666, 0.686111, 0., 0.055418 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0426', // char code: 1062
				new double[] { 0.894992, 0.686111, 0.194445, 0.172084 }, // metrics:
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
				new double[] { 0.894992, 0.686111, 0.194445, 0.172084 }, // metrics:
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
				new double[] { 0.756659, 0.686111, 0., 0.114306 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0424', // char code: 1060
				new double[] { 0.944435, 0.686111, 0., 0.090625 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u0416', '\u0425', '\u0423',
						'\u0474', '\u042F' }, // kern
												// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445 }, // kern
															// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0413', // char code: 1043
				new double[] { 0.697771, 0.686111, 0., 0.12903 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u042F', '\u041B', '\u0409',
						'\u0430', '\u043E', '\u0473', '\u0435', '\u0451',
						'\u0441', '\u0454', '\u0444', '\u0438', '\u0439',
						'\u0446', '\u0448', '\u0449', '\u044C', '\u044B',
						'\u0443', '\u0475', '\u044A', '\u043B', '\u043C',
						'\u0459' }, // kern
									// codes
				new double[] { -0.088333, -0.088333, -0.088333, -0.029445,
						-0.029445, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.088333, -0.088333 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0425', // char code: 1061
				new double[] { 0.865547, 0.686111, 0., 0.156807 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u041E', '\u0424', '\u0472', '\u0421', '\u0404',
						'\u0447' }, // kern
									// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445 }, // kern
												// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0418', // char code: 1048
				new double[] { 0.894992, 0.686111, 0., 0.172084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0408', // char code: 1032
				new double[] { 0.61055, 0.686111, 0., 0.145001 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u041A', // char code: 1050
				new double[] { 0.894992, 0.686111, 0., 0.142084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u041E', '\u0424', '\u0472', '\u0421', '\u0404',
						'\u0447' }, // kern
									// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445 }, // kern
												// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u041B', // char code: 1051
				new double[] { 0.894992, 0.686111, 0., 0.172084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u041C', // char code: 1052
				new double[] { 1.072767, 0.686111, 0., 0.172084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u041D', // char code: 1053
				new double[] { 0.894992, 0.686111, 0., 0.172084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u041E', // char code: 1054
				new double[] { 0.854991, 0.686111, 0., 0.090625 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u0416', '\u0425', '\u0423',
						'\u0474', '\u042F' }, // kern
												// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445 }, // kern
															// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u041F', // char code: 1055
				new double[] { 0.894992, 0.686111, 0., 0.172084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0427', // char code: 1063
				new double[] { 0.894992, 0.686111, 0., 0.172084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0420', // char code: 1056
				new double[] { 0.787214, 0.686111, 0., 0.099202 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u041B', '\u0409', '\u042F',
						'\u0430', '\u043E', '\u0473', '\u0435', '\u0451',
						'\u0434', '\u043B', '\u0459' }, // kern codes
				new double[] { -0.088333, -0.088333, -0.088333, -0.088333,
						-0.088333, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.088333, -0.088333, -0.088333 }, // kern
																		// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0421', // char code: 1057
				new double[] { 0.826658, 0.686111, 0., 0.142084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0422', // char code: 1058
				new double[] { 0.796103, 0.686111, 0., 0.12903 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0410', '\u0414', '\u042F', '\u041B', '\u0409',
						'\u0430', '\u043E', '\u0473', '\u0435', '\u0451',
						'\u0441', '\u0454', '\u0444', '\u0438', '\u0439',
						'\u0446', '\u0448', '\u0449', '\u044C', '\u044B',
						'\u0443', '\u0475', '\u044A', '\u043B', '\u043C',
						'\u0459' }, // kern
									// codes
				new double[] { -0.088333, -0.088333, -0.088333, -0.029445,
						-0.029445, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.088333, -0.088333 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0423', // char code: 1059
				new double[] { 0.865547, 0.686111, 0., 0.186251 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0410', '\u042F', '\u0414', '\u041B', '\u0409',
						'\u041E', '\u0424', '\u0472', '\u0421', '\u0404',
						'\u0430', '\u043E', '\u0473', '\u0435', '\u0451',
						'\u0441', '\u0454', '\u0438', '\u0439', '\u0446',
						'\u0448', '\u0449', '\u044C', '\u044B', '\u0443',
						'\u0475', '\u044A', '\u043B', '\u043C', '\u0459' }, // kern
																			// codes
				new double[] { -0.088333, -0.088333, -0.058888, -0.058888,
						-0.058888, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.117777, -0.117777,
						-0.117777, -0.117777, -0.117777, -0.117777, -0.117777,
						-0.117777, -0.117777, -0.117777, -0.117777, -0.117777,
						-0.117777 }, // kern
										// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0412', // char code: 1042
				new double[] { 0.81666, 0.686111, 0., 0.069758 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0429', // char code: 1065
				new double[] { 1.293599, 0.686111, 0.194445, 0.172084 }, // metrics:
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
				new double[] { 1.293599, 0.686111, 0., 0.172084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u042B', // char code: 1067
				new double[] { 1.101102, 0.686111, 0., 0.172084 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0417', // char code: 1047
				new double[] { 0.708882, 0.686111, 0., 0.099202 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u042C', // char code: 1068
				new double[] { 0.81666, 0.686111, 0., 0.032991 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0416', '\u0425', '\u041E', '\u0424', '\u0472',
						'\u0421', '\u0404', '\u0422', '\u042A', '\u0402',
						'\u040B', '\u0462', '\u0427', '\u0423', '\u0474',
						'\u044A', '\u0475', '\u043A', '\u043D', '\u045A',
						'\u043F', '\u0442', '\u044E', '\u0463', '\u0456',
						'\u0438', '\u0439', '\u0446', '\u0448', '\u0449',
						'\u044C', '\u044B', '\u0443' }, // kern
														// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.117777, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445 }, // kern
																		// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u042A', // char code: 1066
				new double[] { 0.988047, 0.686111, 0., 0.032991 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0416', '\u0425', '\u041E', '\u0424', '\u0472',
						'\u0421', '\u0404', '\u0422', '\u042A', '\u0402',
						'\u040B', '\u0462', '\u0427', '\u0423', '\u0474',
						'\u044A', '\u0475', '\u043A', '\u043D', '\u045A',
						'\u043F', '\u0442', '\u044E', '\u0463', '\u0456',
						'\u0438', '\u0439', '\u0446', '\u0448', '\u0449',
						'\u044C', '\u044B', '\u0443' }, // kern
														// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.088333, -0.088333,
						-0.088333, -0.088333, -0.088333, -0.088333, -0.088333,
						-0.117777, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.029445, -0.029445, -0.029445 }, // kern
																		// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0430', // char code: 1072
				new double[] { 0.570494, 0.444445, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0431', // char code: 1073
				new double[] { 0.549883, 0.694445, 0., 0.167501 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0444', '\u0430', '\u043B', '\u043C', '\u0459' }, // kern
																					// codes
				new double[] { -0.058888, -0.058888, -0.029445, -0.029445,
						-0.029445 }, // kern
										// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0446', // char code: 1094
				new double[] { 0.655884, 0.444445, 0.194445, 0.094261 }, // metrics:
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
				new double[] { 0.549883, 0.694445, 0., 0.112694 }, // metrics:
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
				new double[] { 0.511606, 0.444445, 0., 0.085002 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0444', '\u0430', '\u043B', '\u043C', '\u0459' }, // kern
																					// codes
				new double[] { -0.058888, -0.058888, -0.029445, -0.029445,
						-0.029445 }, // kern
										// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0444', // char code: 1092
				new double[] { 0.785437, 0.694445, 0.194445, 0.078611 }, // metrics:
																			// width,
																			// height,
																			// depth,
																			// italic
				null, // ligatures
				new char[] { '\u0444', '\u0430', '\u043B', '\u043C', '\u0459' }, // kern
																					// codes
				new double[] { -0.058888, -0.058888, -0.029445, -0.029445,
						-0.029445 }, // kern
										// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0433', // char code: 1075
				new double[] { 0.488052, 0.444445, 0., 0.085002 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0445', // char code: 1093
				new double[] { 0.648885, 0.444445, 0., 0.1258335 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0438', // char code: 1080
				new double[] { 0.649994, 0.444445, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0458', // char code: 1112
				new double[] { 0.355553, 0.693255, 0.194445, 0.167204 }, // metrics:
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
				new double[] { 0.591105, 0.444445, 0., 0.111112 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u043B', // char code: 1083
				new double[] { 0.62055, 0.444445, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u043B', '\u043C', '\u0459', '\u0475', '\u044A',
						'\u0447' }, // kern
									// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.088333 }, // kern
												// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u043C', // char code: 1084
				new double[] { 0.856104, 0.444445, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u043D', // char code: 1085
				new double[] { 0.649994, 0.444445, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u043E', // char code: 1086
				new double[] { 0.549883, 0.444445, 0., 0.078611 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0444', '\u0430', '\u043B', '\u043C', '\u0459' }, // kern
																					// codes
				new double[] { -0.058888, -0.058888, -0.029445, -0.029445,
						-0.029445 }, // kern
										// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u043F', // char code: 1087
				new double[] { 0.649994, 0.444445, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0447', // char code: 1095
				new double[] { 0.62055, 0.444445, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0440', // char code: 1088
				new double[] { 0.585216, 0.444445, 0.194445, 0.078611 }, // metrics:
																			// width,
																			// height,
																			// depth,
																			// italic
				null, // ligatures
				new char[] { '\u0444', '\u0430', '\u043B', '\u043C', '\u0459' }, // kern
																					// codes
				new double[] { -0.058888, -0.058888, -0.029445, -0.029445,
						-0.029445 }, // kern
										// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0441', // char code: 1089
				new double[] { 0.511606, 0.444445, 0., 0.052223 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u0444', '\u0430', '\u043B', '\u043C', '\u0459' }, // kern
																					// codes
				new double[] { -0.058888, -0.058888, -0.029445, -0.029445,
						-0.029445 }, // kern
										// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0442', // char code: 1090
				new double[] { 0.944435, 0.444445, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0443', // char code: 1091
				new double[] { 0.591105, 0.444445, 0.194445, 0.105001 }, // metrics:
																			// width,
																			// height,
																			// depth,
																			// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0432', // char code: 1074
				new double[] { 0.570494, 0.444445, 0., 0.085002 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0449', // char code: 1097
				new double[] { 0.950325, 0.444445, 0.194445, 0.094261 }, // metrics:
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
				new double[] { 0.944435, 0.444445, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u044B', // char code: 1099
				new double[] { 0.767771, 0.444445, 0., 0.094261 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0437', // char code: 1079
				new double[] { 0.532217, 0.444445, 0., 0.052223 }, // metrics:
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
				new double[] { 0.591105, 0.444445, 0., 0.078611 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u043B', '\u043C', '\u0459', '\u0475', '\u044A',
						'\u0447', '\u043E', '\u0473', '\u0444', '\u0454' }, // kern
																			// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.088333, -0.029445, -0.029445, -0.029445,
						-0.029445 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u044A', // char code: 1098
				new double[] { 0.561663, 0.444445, 0., 0.078611 }, // metrics:
																	// width,
																	// height,
																	// depth,
																	// italic
				null, // ligatures
				new char[] { '\u043B', '\u043C', '\u0459', '\u0475', '\u044A',
						'\u0447', '\u043E', '\u0473', '\u0444', '\u0454' }, // kern
																			// codes
				new double[] { -0.029445, -0.029445, -0.029445, -0.029445,
						-0.029445, -0.088333, -0.029445, -0.029445, -0.029445,
						-0.029445 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
	}
}
