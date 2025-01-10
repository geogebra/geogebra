/* LongdivAtom.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2017-2018 DENIZET Calixte
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

import java.util.ArrayList;
import java.util.List;

/**
 * An atom representing a long division.
 */
public class LongdivAtom extends VRowAtom {

	public LongdivAtom(long divisor, long dividend, TeXParser tp) {
		setHalign(TeXConstants.Align.RIGHT);
		setVtop(true);
		String[] res = makeResults(divisor, dividend);
		Atom rule = new RuleAtom(new TeXLength(Unit.EX, 0.),
				new TeXLength(Unit.EX, 2.6),
				new TeXLength(Unit.EX, 0.5));
		for (int i = 0; i < res.length; ++i) {
			if (i % 2 == 0) {
				final RowAtom ra = TeXParser.getAtomForLatinStr(res[i],
						new RowAtom(), tp.isMathMode());
				ra.add(rule);
				if (i == 0) {
					append(ra);
				} else {
					append(new UnderlinedAtom(ra));
				}
			} else if (i == 1) {
				String div = Long.toString(divisor);
				SymbolAtom rparen = Symbols.RBRACK;
				Atom big = new BigDelimiterAtom(rparen, 1);
				Atom ph = new PhantomAtom(big, false, true, true);
				RowAtom ra = new RowAtom(ph);
				Atom raised = new RaiseAtom(big,
						new TeXLength(Unit.X8, 3.5),
						TeXLength.getZero(), TeXLength.getZero());
				ra.add(new SmashedAtom(raised));
				ra.add(TeXParser.getAtomForLatinStr(res[i], tp.isMathMode()));
				Atom a = new OverlinedAtom(ra);
				RowAtom ra1 = TeXParser.getAtomForLatinStr(div, new RowAtom(),
						tp.isMathMode());
				ra1.add(new SpaceAtom(TeXConstants.Muskip.THIN));
				ra1.add(a);
				append(ra1);
			} else {
				final RowAtom ra = TeXParser.getAtomForLatinStr(res[i],
						new RowAtom(), tp.isMathMode());
				ra.add(rule);
				append(ra);
			}
		}
	}

	private String[] makeResults(long divisor, long dividend) {
		List<String> vec = new ArrayList<>();
		long q = dividend / divisor;
		final long r = dividend % divisor;
		vec.add(Long.toString(q));
		vec.add(Long.toString(dividend));

		while (q != 0) {
			final double p = Math.floor(Math.log10(q));
			final double p10 = Math.pow(10., p);
			final long d = (long) (Math.floor((q) / p10) * p10);
			final long dd = d * divisor;
			vec.add(Long.toString(dd));
			dividend -= dd;
			vec.add(Long.toString(dividend));
			q -= d;
		}

		String[] res = new String[vec.size()];
		return vec.toArray(res);
	}
}
