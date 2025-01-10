/* CommandSfrac.java
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

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.ScaleAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.Unit;
import com.himamis.retex.renderer.share.VRowAtom;

public class CommandSfrac extends Command2A {

	@Override
	public Atom newI(TeXParser tp, Atom a, Atom b) {
		double scaleX = 0.75;
		double scaleY = 0.75;
		double raise1 = 0.45;
		double shiftL = -0.13;
		double shiftR = -0.065;
		Atom slash = Symbols.SLASH;

		if (!tp.isMathMode()) {
			scaleX = 0.6;
			scaleY = 0.6;
			raise1 = 0.75;
			shiftL = -0.24;
			shiftR = -0.24;
			slash = new VRowAtom(
					new ScaleAtom(Symbols.TEXTFRACTIONSOLIDUS, 1.25, 0.65));
			((VRowAtom) slash).setRaise(Unit.EX, 0.4);
		}

		final VRowAtom snum = new VRowAtom(new ScaleAtom(a, scaleX, scaleY));
		snum.setRaise(Unit.EX, raise1);
		return new RowAtom(snum,
				new SpaceAtom(Unit.EM, shiftL, 0., 0.), slash,
				new SpaceAtom(Unit.EM, shiftR, 0., 0.),
				new ScaleAtom(b, scaleX, scaleY));
	}
}
