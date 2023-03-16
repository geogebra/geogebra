/* TStrokeAtom.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2009 DENIZET Calixte
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

import com.himamis.retex.renderer.share.serialize.HasCharacter;

/**
 * An atom with a stroked T
 */
public class TStrokeAtom extends Atom implements HasCharacter {

	private static final SymbolAtom BAR = SymbolAtom.get("bar");
	private boolean upper;

	public TStrokeAtom(boolean upper) {
		this.upper = upper;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		Char ch = env.getTeXFont().getChar(BAR.getCf(), env.getStyle());
		double italic = ch.getItalic();
		CharBox T = new CharBox(
				env.getTeXFont().getChar(upper ? 'T' : 't', env.getStyle()));
		CharBox B = new CharBox(ch);
		Box y;
		if (Math.abs(italic) > TeXFormula.PREC) {
			HorizontalBox hb = new HorizontalBox(
					new StrutBox(-italic, 0, 0, 0));
			hb.add(B);
			y = hb;
		} else {
			y = B;
		}
		Box b = new HorizontalBox(y, T.getWidth(), TeXConstants.Align.CENTER);
		VerticalBox vb = new VerticalBox();
		vb.add(T);
		vb.add(new StrutBox(0, -0.5 * T.getHeight(), 0, 0));
		vb.add(b);
		return vb;
	}

	@Override
	public String getCharacter() {
		return upper ? "T" : "t";
	}
}
/*
 * if (upper) hb.add(new SpaceAtom(TeXLength.Unit.EM, -0.7, 0,
 * 0).createBox(env)); else hb.add(new SpaceAtom(TeXLength.Unit.EM, -0.3, 0,
 * 0).createBox(env)); hb.add(A); return hb; }
 * 
 * public Box createBox(TeXEnvironment env) { Box b = base.createBox(env);
 * VerticalBox vb = new VerticalBox(); vb.add(b); Char ch =
 * env.getTeXFont().getChar("ogonek", env.getStyle()); double italic =
 * ch.getItalic(); double x = new SpaceAtom(TeXLength.Unit.MU, 1., 0,
 * 0).createBox(env).getWidth(); Box ogonek = new CharBox(ch); Box y; if
 * (Math.abs(italic) > TeXFormula.PREC) { y = new HorizontalBox(new
 * StrutBox(-italic, 0, 0, 0)); y.add(ogonek); } else y = ogonek;
 * 
 * Box og = new HorizontalBox(y, b.getWidth(), TeXConstants.Align.RIGHT);
 * vb.add(new StrutBox(0, -ogonek.getHeight(), 0, 0)); vb.add(og); double f =
 * vb.getHeight() + vb.getDepth(); vb.setHeight(b.getHeight()); vb.setDepth(f -
 * b.getHeight()); return vb; } }
 */
