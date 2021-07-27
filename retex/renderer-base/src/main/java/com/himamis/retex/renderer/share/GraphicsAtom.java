/* GraphicsAtom.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2009-2018 DENIZET Calixte
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

import java.util.Map;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.RenderingHints;

/**
 * An atom representing an atom containing a graphic.
 */
public class GraphicsAtom extends Atom {

	private Image bimage;
	private Atom base;
	private boolean first = true;
	private int interp = -1;

	public GraphicsAtom(final String path, final Map<String, String> option) {
		bimage = FactoryProvider.getInstance().getGraphicsFactory()
				.createImage(path);
		buildAtom(option);
	}

	protected void buildAtom(final Map<String, String> options) {
		base = this;
		final boolean hasWidth = options.containsKey("width");
		final boolean hasHeight = options.containsKey("height");
		if (hasWidth || hasHeight) {
			TeXLength width = null;
			TeXLength height = null;
			final TeXParser tp = new TeXParser();
			if (hasWidth) {
				tp.setParseString(options.get("width"));
				width = tp.getLength();
			}
			if (hasHeight) {
				tp.setParseString(options.get("height"));
				height = tp.getLength();
			}

			base = new ResizeAtom(base, width, height,
					options.containsKey("keepaspectratio"));
		}
		if (options.containsKey("scale")) {
			final double scl = Double.parseDouble(options.get("scale"));
			if (!Double.isNaN(scl)) {
				base = new ScaleAtom(base, scl, scl);
			}
		}
		if (options.containsKey("angle")) {
			final double angle = Double.parseDouble(options.get("angle"));
			if (!Double.isNaN(angle)) {
				base = new RotateAtom(base, angle, options);
			}
		}
		if (options.containsKey("interpolation")) {
			final String meth = options.get("interpolation");
			if (meth.equalsIgnoreCase("bilinear")) {
				interp = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
			} else if (meth.equalsIgnoreCase("bicubic")) {
				interp = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
			} else if (meth.equalsIgnoreCase("nearest_neighbor")) {
				interp = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
			}
		}
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		if (bimage != null) {
			if (first) {
				first = false;
				return base.createBox(env);
			}
			env.isColored = true;
			final double width = bimage.getWidth()
					* Unit.PIXEL.getFactor(env);
			final double height = bimage.getHeight()
					* Unit.PIXEL.getFactor(env);
			return new GraphicsBox(bimage, width, height, env.getSize(),
					interp);
		}

		return TeXParser.getAtomForLatinStr("No such image file", false)
				.createBox(env);
	}
}
