/* Env.java
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

package com.himamis.retex.renderer.share;

import java.util.ArrayList;

public class Env {

	private final Macro before;
	private final Macro after;

	public static class Begin implements AtomConsumer {

		private final String name;
		private RowAtom base;
		private final ArrayList<String> args;

		public Begin(final String name, final ArrayList<String> args) {
			this.name = name;
			base = new RowAtom();
			this.args = args;
		}

		@Override
		public Atom getLastAtom() {
			return base.getLastAtom();
		}

		@Override
		public boolean init(TeXParser tp) {
			return false;
		}

		@Override
		public void add(TeXParser tp, Atom a) {
			base.add(a);
		}

		@Override
		public boolean close(TeXParser tp) {
			return false;
		}

		@Override
		public boolean isClosable() {
			return false;
		}

		public ArrayList<String> getArgs() {
			return args;
		}

		public String getName() {
			return name;
		}

		@Override
		public RowAtom steal(TeXParser tp) {
			final RowAtom ra = base;
			base = new RowAtom();
			return ra;
		}

		public Atom getBase() {
			return base.simplify();
		}

		@Override
		public boolean isArray() {
			return false;
		}

		@Override
		public boolean isAmpersandAllowed() {
			return false;
		}

		@Override
		public boolean isHandlingArg() {
			return false;
		}

		@Override
		public void lbrace(TeXParser tp) {
		}

		@Override
		public void rbrace(TeXParser tp) {
		}
	}

	public Env(final String before, final String after, final int nargs) {
		this.before = new Macro(before, nargs);
		this.after = new Macro(after, nargs);
	}

	public int getNArgs() {
		return before.getNArgs();
	}

	public String getBefore(final TeXParser tp, final ArrayList<String> args) {
		return before.get(tp, args);
	}

	public String getAfter(final TeXParser tp, final ArrayList<String> args) {
		return after.get(tp, args);
	}
}
