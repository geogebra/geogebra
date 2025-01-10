/* CommandMathStyles.java
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
import com.himamis.retex.renderer.share.AtomConsumer;
import com.himamis.retex.renderer.share.MathAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.exception.ParseException;

public class CommandMathStyles {

	public static class OpenBracket extends Command {

		private final TeXConstants.Opener opener;
		private RowAtom ra;

		public OpenBracket(final TeXConstants.Opener opener) {
			this.opener = opener;
		}

		@Override
		public final boolean init(TeXParser tp) {
			tp.pushMode(TeXParser.MATH_MODE);
			ra = new RowAtom();
			return true;
		}

		@Override
		public void add(TeXParser tp, Atom a) {
			ra.add(a);
		}

		@Override
		public RowAtom steal(TeXParser tp) {
			final RowAtom _ra = ra;
			ra = new RowAtom();
			return _ra;
		}

		@Override
		public Atom getLastAtom() {
			return ra.getLastAtom();
		}

	}

	public static class CloseBracket extends Command {

		private final TeXConstants.Opener opener;
		private final int style;
		private final String err;

		public CloseBracket(final TeXConstants.Opener opener, final int style,
				final String err) {
			this.opener = opener;
			this.style = style;
			this.err = err;
		}

		@Override
		public final boolean init(TeXParser tp) {
			tp.close();
			final AtomConsumer ac = tp.peek();
			if (ac instanceof OpenBracket) {
				final OpenBracket ob = (OpenBracket) ac;
				if (ob.opener == opener) {
					tp.popMode();
					tp.closeConsumer(new MathAtom(ob.ra.simplify(), style));
					return false;
				}
			}
			throw new ParseException(tp, err);
		}
	}
}
