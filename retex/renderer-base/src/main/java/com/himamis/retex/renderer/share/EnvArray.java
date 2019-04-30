/* EnvArray.java
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

import com.himamis.retex.renderer.share.commands.Command;
import com.himamis.retex.renderer.share.exception.ParseException;
import com.himamis.retex.renderer.share.platform.graphics.Color;

public class EnvArray {

	public static final class ColSep extends EmptyAtom {

		private ColSep() {
		}

		public static ColSep get() {
			return new ColSep();
		}

		@Override
		public Atom duplicate() {
			return setFields(new ColSep());
		}
	}

	public static final class RowSep extends EmptyAtom {

		private RowSep() {
		}

		public static RowSep get() {
			return new RowSep();
		}

		@Override
		public Atom duplicate() {
			return setFields(new RowSep());
		}

	}

	public static final class CellColor extends EmptyAtom {
		final Color c;

		public CellColor(final Color c) {
			this.c = c;
		}

		public Color getColor() {
			return c;
		}

		@Override
		public Atom duplicate() {
			return setFields(new CellColor(c));
		}
	}

	public static final class RowColor extends EmptyAtom {
		final Color c;

		public RowColor(final Color c) {
			this.c = c;
		}

		public Color getColor() {
			return c;
		}

		@Override
		public Atom duplicate() {
			return setFields(new RowColor(c));
		}
	}

	public static class Begin extends Command {

		ArrayTypes type;
		ArrayOptions opt;
		ArrayOfAtoms aoa;
		int n;

		public Begin(ArrayTypes type) {
			this.type = type;
			this.opt = null;
		}

		public Begin(ArrayTypes type, ArrayOptions opt) {
			this.type = type;
			this.opt = opt;
		}

		@Override
		final public boolean init(TeXParser tp) {
			if (type == ArrayTypes.ALIGNEDAT || type == ArrayTypes.ALIGNAT) {
				n = tp.getArgAsPositiveInteger();
				if (n <= 0) {
					throw new ParseException(tp,
							"Invalid argument in " + type.toString() + " environment");
				}

				aoa = new ArrayOfAtoms();
				tp.addConsumer(this);
				tp.addConsumer(aoa);
				return false;
			}

			if (opt == null) {
				opt = tp.getArrayOptions();
			}

			aoa = new ArrayOfAtoms();
			tp.addConsumer(this);
			tp.addConsumer(aoa);

			switch (type) {
			case MULTILINE:
			case SUBARRAY:
			case GATHER:
			case GATHERED:
				aoa.setOneColumn(true);
				return false;

			default:
				return false;
			}

		}

		public final ArrayTypes getType() {
			return type;
		}

		public ArrayOptions getOptions() {
			return opt;
		}

		public ArrayOfAtoms getAOA() {
			return aoa;
		}
	}

	public static class End extends Command {

		final ArrayTypes type;
		final String op;
		final String cl;

		public End(ArrayTypes type, String op, String cl) {
			this.type = type;
			this.op = op;
			this.cl = cl;
		}

		public End(ArrayTypes type, String op) {
			this(type, op, null);
		}

		public End(ArrayTypes type) {
			this(type, null, null);
		}

		@Override
		final public boolean init(TeXParser tp) {
			tp.close();
			final AtomConsumer ac = tp.pop();
			if (ac instanceof ArrayOfAtoms) {
				final AtomConsumer c = tp.pop();
				if (c instanceof Begin) {
					final Begin beg = (Begin) c;
					if (type != beg.getType()) {
						throw new ParseException(tp,
								"Close a " + beg.getType().toString() + " with a " + type.toString());
					}
					beg.aoa.checkDimensions();
					if (op == null) {
						tp.addToConsumer(newI(tp, beg));
					} else {
						tp.addToConsumer(newFenced(beg));
					}
				} else {
					throw new ParseException(tp,
							"Close something which is not a " + type.toString());
				}
			} else {
				throw new ParseException(tp,
						"Close something which is not a " + type.toString());
			}

			return false;
		}

		public Atom newFenced(Begin beg) {
			final SymbolAtom op = SymbolAtom.get(this.op);
			final SymbolAtom cl = this.cl == null ? op
					: SymbolAtom.get(this.cl);
			final Atom mat = new SMatrixAtom(beg.aoa, false);
			return new FencedAtom(mat, op, cl);
		}

		final public Atom newI(TeXParser tp, Begin beg) {
			switch (type) {
			case ALIGN:
				return new AlignAtom(beg.aoa, false);
			case CASES:
				return new FencedAtom(new ArrayAtom(beg.aoa, beg.opt, true),
						Symbols.LBRACE, null, null);

			case MATRIX:
				return new SMatrixAtom(beg.aoa, false);

			case SMALLMATRIX:
				return new SMatrixAtom(beg.aoa, true);

			case ALIGNED:
				return new AlignAtom(beg.aoa, true);

			case FLALIGN:
				return new FlalignAtom(beg.aoa);

			case ALIGNAT:
				if (2 * beg.n != beg.aoa.col) {
					throw new ParseException(tp,
							"Bad number of equations in alignat environment !");
				}
				return new AlignAtAtom(beg.aoa, false);

			case ALIGNEDAT:
				if (2 * beg.n != beg.aoa.col) {
					throw new ParseException(tp,
							"Bad number of equations in alignedat environment !");
				}
				return new AlignAtAtom(beg.aoa, true);

			case MULTILINE:
				if (beg.aoa.col == 0) {
					return EmptyAtom.get();
				}
				return new MultlineAtom(beg.aoa, MultlineAtom.MULTLINE);

			case SUBARRAY:
				if (beg.aoa.col == 0) {
					return EmptyAtom.get();
				}
				return new SubarrayAtom(beg.getAOA(), beg.getOptions());

			case GATHER:
				if (beg.aoa.col == 0) {
					return EmptyAtom.get();
				}
				return new MultlineAtom(beg.aoa, MultlineAtom.GATHER);

			case GATHERED:
				if (beg.aoa.col == 0) {
					return EmptyAtom.get();
				}
				return new MultlineAtom(beg.aoa, MultlineAtom.GATHERED);

			default:
				return new ArrayAtom(beg.aoa, beg.opt, true);
			}
		}
	}
}
