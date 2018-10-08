/* FCSROPG.java
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

final class FCSROPG extends UniFontInfo {

	FCSROPG(final String ttfPath) {
		super(204, ttfPath, 0.444, 0.239, 1, '\u0000');
	}

	@Override
	protected final void initMetrics() {
		setInfo('\u0020', // char code: 32
				new double[] { 0.239, 0.0, 0., 0. }, // metrics: width, height,
														// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u00A8', // char code: 168
				new double[] { 0.222, 0.603, 0., 0.17 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0374', // char code: 884
				new double[] { 0.194, 0.695, 0., 0.14 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0375', // char code: 885
				new double[] { 0.194, 0.001, 0.21, 0.003 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u037A', // char code: 890
				new double[] { 0.111, -0.064, 0.238, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0384', // char code: 900
				new double[] { 0.167, 0.695, 0., 0.145 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u039F', '\u03A9', '\u1FBC', '\u1FFC' }, // kern
																					// codes
				new double[] { -0.111, -0.056, -0.028, -0.111, -0.028 }, // kern
																			// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0385', // char code: 901
				new double[] { 0.278, 0.695, 0., 0.117 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0387', // char code: 903
				new double[] { 0.278, 0.431, 0., 0. }, // metrics: width,
														// height, depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0390', // char code: 912
				new double[] { 0.25, 0.695, 0.011, 0.099 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0391', // char code: 913
				new double[] { 0.667, 0.685, 0., 0. }, // metrics: width,
														// height, depth, italic
				null, // ligatures
				new char[] { '\u0398', '\u039F', '\u03A4', '\u03A5', '\u03A6',
						'\u03A8', '\u03AB' }, // kern
												// codes
				new double[] { -0.083, -0.083, -0.083, -0.139, -0.083, -0.122,
						-0.139 }, // kern
									// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0392', // char code: 914
				new double[] { 0.667, 0.685, 0., 0.028 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0393', // char code: 915
				new double[] { 0.542, 0.681, 0., 0.101 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u0394', '\u039B', '\u1FBC' }, // kern
																		// codes
				new double[] { -0.133, -0.111, -0.122, -0.133 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0394', // char code: 916
				new double[] { 0.833, 0.684, 0., 0. }, // metrics: width,
														// height, depth, italic
				null, // ligatures
				new char[] { '\u039F', '\u03A5', '\u03AB' }, // kern codes
				new double[] { -0.033, -0.139, -0.139 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0395', // char code: 917
				new double[] { 0.597, 0.681, 0., 0.088 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0396', // char code: 918
				new double[] { 0.611, 0.684, 0., 0.089 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0397', // char code: 919
				new double[] { 0.708, 0.684, 0., 0.049 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0398', // char code: 920
				new double[] { 0.778, 0.707, 0.022, 0.026 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0391', '\u1FBC' }, // kern codes
				new double[] { -0.083, -0.083 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u0399', // char code: 921
				new double[] { 0.278, 0.684, 0., 0.049 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u039A', // char code: 922
				new double[] { 0.694, 0.684, 0., 0.089 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u039F' }, // kern codes
				new double[] { -0.083 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u039B', // char code: 923
				new double[] { 0.611, 0.684, 0., 0. }, // metrics: width,
														// height, depth, italic
				null, // ligatures
				new char[] { '\u039F', '\u03A5', '\u03AB' }, // kern codes
				new double[] { -0.033, -0.1, -0.1 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u039C', // char code: 924
				new double[] { 0.875, 0.684, 0., 0.044 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u039D', // char code: 925
				new double[] { 0.708, 0.684, 0., 0.047 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u1FBC' }, // kern codes
				new double[] { -0.083, -0.083 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u039E', // char code: 926
				new double[] { 0.667, 0.679, 0., 0.096 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u039F', // char code: 927
				new double[] { 0.736, 0.706, 0.021, 0.026 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u0391', '\u03A3', '\u1FBC' }, // kern codes
				new double[] { -0.083, -0.056, -0.083 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03A0', // char code: 928
				new double[] { 0.708, 0.681, 0., 0.048 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03A1', // char code: 929
				new double[] { 0.639, 0.685, 0., 0.051 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u1FBC' }, // kern codes
				new double[] { -0.167, -0.167 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03A3', // char code: 931
				new double[] { 0.722, 0.684, 0., 0.089 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u1FBC' }, // kern codes
				new double[] { -0.028, -0.028 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03A4', // char code: 932
				new double[] { 0.68, 0.679, 0., 0.107 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u1FBC' }, // kern codes
				new double[] { -0.083, -0.083 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03A5', // char code: 933
				new double[] { 0.778, 0.707, 0., 0.064 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u0394', '\u039B', '\u1FBC' }, // kern
																		// codes
				new double[] { -0.139, -0.139, -0.139, -0.139 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03A6', // char code: 934
				new double[] { 0.722, 0.684, 0., 0.021 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u1FBC' }, // kern codes
				new double[] { -0.083, -0.083 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03A7', // char code: 935
				new double[] { 0.667, 0.684, 0., 0.088 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03A8', // char code: 936
				new double[] { 0.778, 0.684, 0., 0.072 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u1FBC' }, // kern codes
				new double[] { -0.122, -0.122 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03A9', // char code: 937
				new double[] { 0.722, 0.706, 0., 0.046 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03AA', // char code: 938
				new double[] { 0.278, 0.833, 0., 0.168 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03AB', // char code: 939
				new double[] { 0.778, 0.833, 0., 0.064 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u0394', '\u039B', '\u1FBC' }, // kern
																		// codes
				new double[] { -0.139, -0.139, -0.139, -0.139 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03AC', // char code: 940
				new double[] { 0.5, 0.695, 0.011, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03AD', // char code: 941
				new double[] { 0.43, 0.695, 0.022, 0.042 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03AE', // char code: 942
				new double[] { 0.5, 0.695, 0.272, 0.022 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03AF', // char code: 943
				new double[] { 0.25, 0.695, 0.011, 0.099 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03B0', // char code: 944
				new double[] { 0.5, 0.695, 0.011, 0.013 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03B1', // char code: 945
				new double[] { 0.5, 0.443, 0.011, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03B2', // char code: 946
				new double[] { 0.5, 0.706, 0.272, 0.03 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03B3', // char code: 947
				new double[] { 0.555, 0.443, 0.216, 0.044 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03B4', // char code: 948
				new double[] { 0.472, 0.652, 0.01, 0.05 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03B5', // char code: 949
				new double[] { 0.43, 0.453, 0.022, 0.042 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03B6', // char code: 950
				new double[] { 0.472, 0.718, 0.153, 0.089 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03B7', // char code: 951
				new double[] { 0.5, 0.453, 0.272, 0.011 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03B8', // char code: 952
				new double[] { 0.569, 0.707, 0.012, 0.031 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03B9', // char code: 953
				new double[] { 0.25, 0.443, 0.011, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03BA', // char code: 954
				new double[] { 0.528, 0.458, 0.026, 0.05 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03BB', // char code: 955
				new double[] { 0.5, 0.698, 0.011, 0. }, // metrics: width,
														// height, depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03BC', // char code: 956
				new double[] { 0.522, 0.453, 0.267, 0.018 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03BD', // char code: 957
				new double[] { 0.472, 0.443, 0.01, 0.022 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03BE', // char code: 958
				new double[] { 0.472, 0.718, 0.152, 0.088 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03BF', // char code: 959
				new double[] { 0.528, 0.442, 0.011, 0.009 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u03B3', '\u03BB', '\u03BD', '\u03C4', '\u03C7' }, // kern
																					// codes
				new double[] { -0.028, -0.028, -0.022, -0.022, -0.028 }, // kern
																			// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03C0', // char code: 960
				new double[] { 0.514, 0.432, 0.011, 0.048 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03C1', // char code: 961
				new double[] { 0.472, 0.443, 0.266, 0.009 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03C2', // char code: 962
				new double[] { 0.444, 0.443, 0.124, 0.044 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03C3', // char code: 963
				new double[] { 0.569, 0.431, 0.01, 0.061 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03C4', // char code: 964
				new double[] { 0.458, 0.432, 0.011, 0.048 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u03AC', '\u03B1', '\u03BF', '\u03C9', '\u03CC',
						'\u03CE', '\u1F00', '\u1F01', '\u1F04', '\u1F05',
						'\u1F06', '\u1F07', '\u1F40', '\u1F41', '\u1F44',
						'\u1F45', '\u1F60', '\u1F61', '\u1F64', '\u1F65',
						'\u1F66', '\u1F67', '\u1F70', '\u1F78', '\u1F7C',
						'\u1F80', '\u1F81', '\u1F84', '\u1F85', '\u1F86',
						'\u1F87', '\u1FA0', '\u1FA1', '\u1FA4', '\u1FA5',
						'\u1FA6', '\u1FA7', '\u1FB2', '\u1FB4', '\u1FB6',
						'\u1FB7', '\u1FF2', '\u1FF4', '\u1FF6', '\u1FF7' }, // kern
																			// codes
				new double[] { -0.028, -0.028, -0.028, -0.028, -0.028, -0.028,
						-0.028, -0.028, -0.028, -0.028, -0.028, -0.028, -0.028,
						-0.028, -0.028, -0.028, -0.028, -0.028, -0.028, -0.028,
						-0.028, -0.028, -0.028, -0.028, -0.028, -0.028, -0.028,
						-0.028, -0.028, -0.028, -0.028, -0.028, -0.028, -0.028,
						-0.028, -0.028, -0.028, -0.028, -0.028, -0.028, -0.028,
						-0.028, -0.028, -0.028, -0.028 }, // kern
															// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03C5', // char code: 965
				new double[] { 0.5, 0.456, 0.011, 0.013 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03C6', // char code: 966
				new double[] { 0.583, 0.454, 0.272, 0.012 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03C7', // char code: 967
				new double[] { 0.555, 0.445, 0.263, 0.051 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03C8', // char code: 968
				new double[] { 0.583, 0.718, 0.272, 0.021 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03C9', // char code: 969
				new double[] { 0.667, 0.453, 0.011, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03CA', // char code: 970
				new double[] { 0.25, 0.626, 0.011, 0.12 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03CB', // char code: 971
				new double[] { 0.5, 0.626, 0.011, 0.013 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03CC', // char code: 972
				new double[] { 0.528, 0.695, 0.011, 0.009 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u03B3', '\u03BB', '\u03BD', '\u03C4', '\u03C7' }, // kern
																					// codes
				new double[] { -0.028, -0.028, -0.022, -0.022, -0.028 }, // kern
																			// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03CD', // char code: 973
				new double[] { 0.5, 0.695, 0.011, 0.013 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03CE', // char code: 974
				new double[] { 0.667, 0.695, 0.011, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03D8', // char code: 984
				new double[] { 0.5, 0.695, 0., 0.072 }, // metrics: width,
														// height, depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03D9', // char code: 985
				new double[] { 0.5, 0.601, 0.064, 0.042 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03DA', // char code: 986
				new double[] { 0.778, 0.679, 0., 0. }, // metrics: width,
														// height, depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03DB', // char code: 987
				new double[] { 0.5, 0.454, 0., 0.04 }, // metrics: width,
														// height, depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03DC', // char code: 988
				new double[] { 0.569, 0.681, 0., 0.101 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03DD', // char code: 989
				new double[] { 0.5, 0.432, 0.25, 0.028 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03DF', // char code: 991
				new double[] { 0.389, 0.563, 0.106, 0.005 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03E0', // char code: 992
				new double[] { 0.639, 0.683, 0., 0. }, // metrics: width,
														// height, depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u03E1', // char code: 993
				new double[] { 0.722, 0.696, 0., 0. }, // metrics: width,
														// height, depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F00', // char code: 7936
				new double[] { 0.5, 0.696, 0.011, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F01', // char code: 7937
				new double[] { 0.5, 0.696, 0.011, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F02', // char code: 7938
				new double[] { 0.5, 0.696, 0.011, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F03', // char code: 7939
				new double[] { 0.5, 0.696, 0.011, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F04', // char code: 7940
				new double[] { 0.5, 0.696, 0.011, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F05', // char code: 7941
				new double[] { 0.5, 0.696, 0.011, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F06', // char code: 7942
				new double[] { 0.5, 0.695, 0.011, 0.067 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F07', // char code: 7943
				new double[] { 0.5, 0.695, 0.011, 0.067 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F10', // char code: 7952
				new double[] { 0.43, 0.696, 0.022, 0.042 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F11', // char code: 7953
				new double[] { 0.43, 0.696, 0.022, 0.042 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F12', // char code: 7954
				new double[] { 0.43, 0.696, 0.022, 0.048 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F13', // char code: 7955
				new double[] { 0.43, 0.696, 0.022, 0.048 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F14', // char code: 7956
				new double[] { 0.43, 0.696, 0.022, 0.083 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F15', // char code: 7957
				new double[] { 0.43, 0.696, 0.022, 0.083 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F20', // char code: 7968
				new double[] { 0.5, 0.696, 0.272, 0.011 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F21', // char code: 7969
				new double[] { 0.5, 0.696, 0.272, 0.011 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F22', // char code: 7970
				new double[] { 0.5, 0.696, 0.272, 0.029 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F23', // char code: 7971
				new double[] { 0.5, 0.696, 0.272, 0.029 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F24', // char code: 7972
				new double[] { 0.5, 0.696, 0.272, 0.064 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F25', // char code: 7973
				new double[] { 0.5, 0.696, 0.272, 0.064 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F26', // char code: 7974
				new double[] { 0.5, 0.696, 0.272, 0.079 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F27', // char code: 7975
				new double[] { 0.5, 0.696, 0.272, 0.079 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F30', // char code: 7984
				new double[] { 0.25, 0.696, 0.011, 0.071 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F31', // char code: 7985
				new double[] { 0.25, 0.696, 0.011, 0.022 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F32', // char code: 7986
				new double[] { 0.25, 0.696, 0.011, 0.106 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F33', // char code: 7987
				new double[] { 0.25, 0.696, 0.011, 0.106 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F34', // char code: 7988
				new double[] { 0.25, 0.696, 0.011, 0.141 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F35', // char code: 7989
				new double[] { 0.25, 0.696, 0.011, 0.141 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F36', // char code: 7990
				new double[] { 0.25, 0.696, 0.011, 0.155 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F37', // char code: 7991
				new double[] { 0.25, 0.696, 0.011, 0.155 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F40', // char code: 8000
				new double[] { 0.528, 0.696, 0.011, 0.009 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u03B3', '\u03BB', '\u03BD', '\u03C4', '\u03C7' }, // kern
																					// codes
				new double[] { -0.028, -0.028, -0.022, -0.022, -0.028 }, // kern
																			// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F41', // char code: 8001
				new double[] { 0.528, 0.696, 0.011, 0.009 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u03B3', '\u03BB', '\u03BD', '\u03C4', '\u03C7' }, // kern
																					// codes
				new double[] { -0.028, -0.028, -0.022, -0.022, -0.028 }, // kern
																			// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F42', // char code: 8002
				new double[] { 0.528, 0.696, 0.011, 0.009 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F43', // char code: 8003
				new double[] { 0.528, 0.696, 0.011, 0.009 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F44', // char code: 8004
				new double[] { 0.528, 0.696, 0.011, 0.034 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u03B3', '\u03BB', '\u03BD', '\u03C4', '\u03C7' }, // kern
																					// codes
				new double[] { -0.028, -0.028, -0.022, -0.022, -0.028 }, // kern
																			// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F45', // char code: 8005
				new double[] { 0.528, 0.696, 0.011, 0.034 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u03B3', '\u03BB', '\u03BD', '\u03C4', '\u03C7' }, // kern
																					// codes
				new double[] { -0.028, -0.028, -0.022, -0.022, -0.028 }, // kern
																			// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F50', // char code: 8016
				new double[] { 0.5, 0.696, 0.011, 0.013 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F51', // char code: 8017
				new double[] { 0.5, 0.696, 0.011, 0.013 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F52', // char code: 8018
				new double[] { 0.5, 0.696, 0.011, 0.013 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F53', // char code: 8019
				new double[] { 0.5, 0.696, 0.011, 0.013 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F54', // char code: 8020
				new double[] { 0.5, 0.696, 0.011, 0.013 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F55', // char code: 8021
				new double[] { 0.5, 0.696, 0.011, 0.013 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F56', // char code: 8022
				new double[] { 0.5, 0.696, 0.011, 0.026 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F57', // char code: 8023
				new double[] { 0.5, 0.696, 0.011, 0.026 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F60', // char code: 8032
				new double[] { 0.667, 0.696, 0.011, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F61', // char code: 8033
				new double[] { 0.667, 0.696, 0.011, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F62', // char code: 8034
				new double[] { 0.667, 0.696, 0.011, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F63', // char code: 8035
				new double[] { 0.667, 0.696, 0.011, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F64', // char code: 8036
				new double[] { 0.667, 0.696, 0.011, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F65', // char code: 8037
				new double[] { 0.667, 0.696, 0.011, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F66', // char code: 8038
				new double[] { 0.667, 0.695, 0.011, 0.035 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F67', // char code: 8039
				new double[] { 0.667, 0.695, 0.011, 0.035 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F70', // char code: 8048
				new double[] { 0.5, 0.695, 0.011, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F72', // char code: 8050
				new double[] { 0.43, 0.695, 0.022, 0.042 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F74', // char code: 8052
				new double[] { 0.5, 0.695, 0.272, 0.011 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F76', // char code: 8054
				new double[] { 0.25, 0.695, 0.011, 0.011 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F78', // char code: 8056
				new double[] { 0.528, 0.695, 0.011, 0.009 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				new char[] { '\u03B3', '\u03BB', '\u03BD', '\u03C4', '\u03C7' }, // kern
																					// codes
				new double[] { -0.028, -0.028, -0.022, -0.022, -0.028 }, // kern
																			// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F7A', // char code: 8058
				new double[] { 0.5, 0.695, 0.011, 0.013 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F7C', // char code: 8060
				new double[] { 0.667, 0.695, 0.011, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F80', // char code: 8064
				new double[] { 0.5, 0.696, 0.238, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F81', // char code: 8065
				new double[] { 0.5, 0.696, 0.238, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F82', // char code: 8066
				new double[] { 0.5, 0.696, 0.238, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F83', // char code: 8067
				new double[] { 0.5, 0.696, 0.238, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F84', // char code: 8068
				new double[] { 0.5, 0.696, 0.238, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F85', // char code: 8069
				new double[] { 0.5, 0.696, 0.238, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F86', // char code: 8070
				new double[] { 0.5, 0.695, 0.238, 0.067 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F87', // char code: 8071
				new double[] { 0.5, 0.695, 0.238, 0.067 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F90', // char code: 8080
				new double[] { 0.5, 0.696, 0.272, 0.011 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F91', // char code: 8081
				new double[] { 0.5, 0.696, 0.272, 0.011 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F92', // char code: 8082
				new double[] { 0.5, 0.696, 0.272, 0.029 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F93', // char code: 8083
				new double[] { 0.5, 0.696, 0.272, 0.029 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F94', // char code: 8084
				new double[] { 0.5, 0.696, 0.272, 0.064 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F95', // char code: 8085
				new double[] { 0.5, 0.696, 0.272, 0.064 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F96', // char code: 8086
				new double[] { 0.5, 0.696, 0.272, 0.079 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1F97', // char code: 8087
				new double[] { 0.5, 0.696, 0.272, 0.079 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FA0', // char code: 8096
				new double[] { 0.667, 0.696, 0.238, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FA1', // char code: 8097
				new double[] { 0.667, 0.696, 0.238, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FA2', // char code: 8098
				new double[] { 0.667, 0.696, 0.238, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FA3', // char code: 8099
				new double[] { 0.667, 0.696, 0.238, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FA4', // char code: 8100
				new double[] { 0.667, 0.696, 0.238, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FA5', // char code: 8101
				new double[] { 0.667, 0.696, 0.238, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FA6', // char code: 8102
				new double[] { 0.667, 0.695, 0.238, 0.035 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FA7', // char code: 8103
				new double[] { 0.667, 0.695, 0.238, 0.035 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FB2', // char code: 8114
				new double[] { 0.5, 0.695, 0.238, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FB3', // char code: 8115
				new double[] { 0.5, 0.443, 0.238, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FB4', // char code: 8116
				new double[] { 0.5, 0.695, 0.238, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FB6', // char code: 8118
				new double[] { 0.5, 0.627, 0.011, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FB7', // char code: 8119
				new double[] { 0.5, 0.627, 0.238, 0.065 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FBC', // char code: 8124
				new double[] { 0.667, 0.685, 0.238, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0398', '\u039F', '\u03A4', '\u03A5', '\u03A6',
						'\u03A8', '\u03AB' }, // kern
												// codes
				new double[] { -0.083, -0.083, -0.083, -0.139, -0.083, -0.122,
						-0.139 }, // kern
									// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FBE', // char code: 8126
				new double[] { 0.222, 0.158, 0.156, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FBF', // char code: 8127
				new double[] { 0.222, 0.696, 0., 0.117 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u039F', '\u03A9', '\u1FBC', '\u1FFC' }, // kern
																					// codes
				new double[] { -0.139, -0.056, -0.028, -0.139, -0.028 }, // kern
																			// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FC0', // char code: 8128
				new double[] { 0.333, 0.627, 0., 0.132 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u1FBC' }, // kern codes
				new double[] { -0.111, -0.111 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FC1', // char code: 8129
				new double[] { 0.444, 0.695, 0., 0.091 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FC2', // char code: 8130
				new double[] { 0.5, 0.695, 0.272, 0.011 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FC3', // char code: 8131
				new double[] { 0.5, 0.453, 0.272, 0.011 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FC4', // char code: 8132
				new double[] { 0.5, 0.695, 0.272, 0.022 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FC6', // char code: 8134
				new double[] { 0.5, 0.626, 0.272, 0.064 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FC7', // char code: 8135
				new double[] { 0.5, 0.626, 0.272, 0.064 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FCC', // char code: 8140
				new double[] { 0.708, 0.684, 0.238, 0.049 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FCD', // char code: 8141
				new double[] { 0.278, 0.696, 0., 0.124 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u039F', '\u1FBC' }, // kern codes
				new double[] { -0.083, -0.028, -0.083 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FCE', // char code: 8142
				new double[] { 0.278, 0.696, 0., 0.159 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u039F', '\u1FBC' }, // kern codes
				new double[] { -0.083, -0.028, -0.083 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FCF', // char code: 8143
				new double[] { 0.333, 0.695, 0., 0.146 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u1FBC' }, // kern codes
				new double[] { -0.111, -0.111 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FD2', // char code: 8146
				new double[] { 0.25, 0.695, 0.011, 0.08 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FD6', // char code: 8150
				new double[] { 0.25, 0.627, 0.011, 0.141 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FD7', // char code: 8151
				new double[] { 0.25, 0.696, 0.011, 0.155 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u03B3', '\u03B7', '\u03B8', '\u03BD', '\u03BF',
						'\u03C2', '\u03C3', '\u03C4', '\u03C7' }, // kern
																	// codes
				new double[] { -0.028, -0.017, -0.017, -0.056, -0.028, -0.028,
						-0.028, -0.028, -0.028 }, // kern
													// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FDD', // char code: 8157
				new double[] { 0.278, 0.696, 0., 0.124 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u039F', '\u1FBC' }, // kern codes
				new double[] { -0.083, -0.028, -0.083 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FDE', // char code: 8158
				new double[] { 0.278, 0.696, 0., 0.159 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u039F', '\u1FBC' }, // kern codes
				new double[] { -0.083, -0.028, -0.083 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FDF', // char code: 8159
				new double[] { 0.333, 0.695, 0., 0.146 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u1FBC' }, // kern codes
				new double[] { -0.111, -0.111 }, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FE2', // char code: 8162
				new double[] { 0.5, 0.695, 0.011, 0.013 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FE4', // char code: 8164
				new double[] { 0.472, 0.696, 0.266, 0.009 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FE5', // char code: 8165
				new double[] { 0.472, 0.696, 0.266, 0.009 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FE6', // char code: 8166
				new double[] { 0.5, 0.627, 0.011, 0.013 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FE7', // char code: 8167
				new double[] { 0.5, 0.696, 0.011, 0.026 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FED', // char code: 8173
				new double[] { 0.278, 0.695, 0., 0.098 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FEF', // char code: 8175
				new double[] { 0.167, 0.695, 0., 0.112 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u039F', '\u03A9', '\u1FBC', '\u1FFC' }, // kern
																					// codes
				new double[] { -0.111, -0.056, -0.028, -0.111, -0.028 }, // kern
																			// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FF2', // char code: 8178
				new double[] { 0.667, 0.695, 0.238, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FF3', // char code: 8179
				new double[] { 0.667, 0.453, 0.238, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FF4', // char code: 8180
				new double[] { 0.667, 0.695, 0.238, 0.01 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FF6', // char code: 8182
				new double[] { 0.667, 0.627, 0.011, 0.021 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FF7', // char code: 8183
				new double[] { 0.667, 0.627, 0.238, 0.021 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FFC', // char code: 8188
				new double[] { 0.722, 0.706, 0.238, 0.046 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u1FFE', // char code: 8190
				new double[] { 0.222, 0.696, 0., 0.068 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				new char[] { '\u0391', '\u039F', '\u03A9', '\u1FBC', '\u1FFC' }, // kern
																					// codes
				new double[] { -0.167, -0.056, -0.028, -0.167, -0.028 }, // kern
																			// values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u2019', // char code: 8217
				new double[] { 0.278, 0.695, 0., 0.057 }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u2329', // char code: 9001
				new double[] { 0.389, 0.752, 0.249, 0.099 }, // metrics: width,
																// height,
																// depth, italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
		setInfo('\u232A', // char code: 9002
				new double[] { 0.389, 0.751, 0.249, 0. }, // metrics: width,
															// height, depth,
															// italic
				null, // ligatures
				null, // kern codes
				null, // kern values
				null, '\0', // next larger
				null); // extension: top, mid, rep, bot
	}
}
