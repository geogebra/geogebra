/* CommandDefinecolor.java
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

package com.himamis.retex.renderer.share.commands;

import java.util.HashMap;
import java.util.Map;

import com.himamis.retex.renderer.share.Colors;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.exception.ParseException;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Color;

public class CommandDefinecolor extends Command {

	private static abstract class Converter {
		final double[] doubles = new double[4];
		final int[] ints = new int[4];

		abstract Color to(TeXParser tp);

		void clampf(final int l) {
			for (int i = 0; i < l; ++i) {
				doubles[i] = Colors.clamp(doubles[i]);
			}
		}

		void clampi(final int l) {
			for (int i = 0; i < l; ++i) {
				ints[i] = Colors.clamp(ints[i]);
			}
		}
	}

	private static Map<String, Converter> converters = new HashMap<String, Converter>(
			11) {
		{
			put("gray", new Converter() {
				@Override
				public Color to(TeXParser tp) {
					final double gray = Colors.clamp(tp.getArgAsDecimal());
					return FactoryProvider.getInstance().getGraphicsFactory()
							.createColor(gray, gray, gray);
				}
			});
			put("wave", new Converter() {
				@Override
				public Color to(TeXParser tp) {
					final double waveLen = tp.getArgAsDecimal();
					return Colors.convWave(waveLen);
				}
			});
			put("rgb", new Converter() {
				@Override
				public Color to(TeXParser tp) {
					tp.getArgAsDecimals(doubles, 3);
					clampf(3);
					return FactoryProvider.getInstance().getGraphicsFactory()
							.createColor(doubles[0], doubles[1], doubles[2]);
				}
			});
			put("RGB", new Converter() {
				@Override
				public Color to(TeXParser tp) {
					tp.getArgAsPositiveIntegers(ints, 3);
					clampi(3);
					return FactoryProvider.getInstance().getGraphicsFactory()
							.createColor(ints[0], ints[1], ints[2]);
				}
			});
			put("rgba", new Converter() {
				@Override
				public Color to(TeXParser tp) {
					tp.getArgAsDecimals(doubles, 4);
					clampf(4);
					return FactoryProvider.getInstance().getGraphicsFactory()
							.createColor(doubles[0], doubles[1], doubles[2],
									doubles[3]);
				}
			});
			put("RGBA", new Converter() {
				@Override
				public Color to(TeXParser tp) {
					tp.getArgAsPositiveIntegers(ints, 4);
					clampi(4);
					return FactoryProvider.getInstance().getGraphicsFactory()
							.createColor(ints[0], ints[1], ints[2], ints[3]);
				}
			});
			put("cmyk", new Converter() {
				@Override
				public Color to(TeXParser tp) {
					tp.getArgAsDecimals(doubles, 4);
					clampf(4);
					return Colors.conv(doubles[0], doubles[1], doubles[2],
							doubles[3]);
				}
			});
			put("hsl", new Converter() {
				@Override
				public Color to(TeXParser tp) {
					tp.getArgAsDecimals(doubles, 3);
					doubles[1] = Colors.clamp(doubles[1]);
					doubles[2] = Colors.clamp(doubles[2]);
					return Colors.convHSL(doubles[0], doubles[1], doubles[2]);
				}
			});
			put("hsla", new Converter() {
				@Override
				public Color to(TeXParser tp) {
					tp.getArgAsDecimals(doubles, 3);
					doubles[1] = Colors.clamp(doubles[1]);
					doubles[2] = Colors.clamp(doubles[2]);
					doubles[3] = Colors.clamp(doubles[3]);
					return Colors.convHSL(doubles[0], doubles[1], doubles[2],
							doubles[3]);
				}
			});
			put("hsb", new Converter() {
				@Override
				public Color to(TeXParser tp) {
					tp.getArgAsDecimals(doubles, 3);
					doubles[1] = Colors.clamp(doubles[1]);
					doubles[2] = Colors.clamp(doubles[2]);
					return Colors.convHSB(doubles[0], doubles[1], doubles[2]);
				}
			});
			put("HTML", new Converter() {
				@Override
				public Color to(TeXParser tp) {
					final int c = tp.getArgAsHexNumber(6);
					return FactoryProvider.getInstance().getGraphicsFactory()
							.createColor(c);
				}
			});
		}
	};

	public static Color getColor(TeXParser tp) {
		final String model = tp.getOptionAsString().trim();
		if (model.isEmpty()) {
			return tp.getArgAsColor();
		}
		final Converter conv = converters.get(model);
		if (conv != null) {
			return conv.to(tp);
		}
		throw new ParseException(tp, "Invalid color model: " + model);
	}

	@Override
	public boolean init(TeXParser tp) {
		final String name = tp.getArgAsString().trim();
		if (!name.isEmpty()) {
			final String model = tp.getArgAsString().trim();
			final Converter conv = converters.get(model);
			if (conv != null) {
				final Color color = conv.to(tp);
				Colors.add(name, color);
			} else {
				throw new ParseException(tp, "Invalid color model: " + model);
			}
		} else {
			throw new ParseException(tp, "Color name must not be empty");
		}

		return false;
	}
}