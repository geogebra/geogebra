/* ArrayOptions.java
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
import java.util.List;

public final class ArrayOptions {

	public static final class Option {

		private final TeXConstants.Align alignment;
		private final TeXConstants.Align vertAlignment;
		private final TeXLength minWidth;
		private List<Atom> separators;

		Option(final TeXConstants.Align alignment, final TeXConstants.Align vertAlignment,
				final TeXLength minWidth) {
			this.alignment = alignment;
			this.vertAlignment = vertAlignment;
			this.separators = new ArrayList<>();
			this.minWidth = minWidth;
		}

		public TeXConstants.Align getAlignment() {
			return alignment;
		}

		public boolean isVline() {
			return separators.size() == 1
					&& separators.get(0) instanceof VlineAtom;
		}

		public boolean isAlignment() {
			return !isVline();
		}

		public List<Atom> getSeparators() {
			return separators;
		}

		@Override
		public String toString() {
			String a = "";
			switch (alignment) {
			case LEFT:
				a = "left";
				break;
			case RIGHT:
				a = "right";
				break;
			case CENTER:
				a = "center";
				break;
			case NONE:
				a = "none";
				break;
			case INVALID:
				a = "first";
				break;
			}

			a += ":";
			for (Atom separator : separators) {
				a += separator.toString();
			}

			return a;
		}

		public TeXLength getMinWidth() {
			return minWidth;
		}

		public TeXConstants.Align getVertAlignment() {
			return vertAlignment;
		}
	}

	private final List<Option> options;
	private final static ArrayOptions empty = new ArrayOptions(0);

	public ArrayOptions() {
		options = new ArrayList<Option>();
	}

	public ArrayOptions(final int size) {
		options = new ArrayList<Option>(size);
	}

	public static ArrayOptions getEmpty() {
		return empty;
	}

	public List<List<Atom>> getSeparators() {
		List<List<Atom>> atoms = new ArrayList<>();
		for (final Option opt : options) {
			atoms.add(opt.getSeparators());
		}

		return atoms;
	}

	public List<Box> getSeparatorBoxes(TeXEnvironment env) {
		List<Box> boxes = new ArrayList<>();
		for (Option opt : options) {
			boxes.add(new RowAtom(opt.separators).createBox(env));
		}

		return boxes;
	}

	public ArrayOptions close() {
		if (!options.isEmpty() && last().isAlignment()) {
			addSeparator(VlineAtom.getEmpty());
		}
		return this;
	}

	public Option last() {
		return options.get(options.size() - 1);
	}

	public ArrayOptions complete(final int n) {
		final int s = options.size();
		for (int i = 0; i < n - s; ++i) {
			addAlignment(TeXConstants.Align.CENTER);
		}
		return this;
	}

	public TeXConstants.Align getAlignment(final int i) {
		return options.get(i + 1).getAlignment();
	}

	public boolean hasAlignment() {
		return options.size() >= 1;
	}

	public ArrayOptions addAlignment(final TeXConstants.Align alignment) {
		return addAlignment(alignment, TeXConstants.Align.TOP, null);
	}

	public ArrayOptions addAlignment(final TeXConstants.Align alignment,
			TeXConstants.Align vertAlignment, TeXLength minWidth) {
		if (options.isEmpty() || last().isAlignment()) {
			addSeparator(VlineAtom.getEmpty());
		}
		options.add(new Option(alignment, vertAlignment, minWidth));
		return this;
	}

	public void addVline(final int n) {
		addSeparator(new VlineAtom(n));
	}

	public ArrayOptions addSeparator(final Atom a) {
		final int s = options.size();
		if (s == 0) {
			final Option o = new Option(TeXConstants.Align.INVALID, TeXConstants.Align.TOP, null);
			o.separators.add(a);
			options.add(o);
		} else {
			final Option lastOption = options.get(s - 1);
			final List<Atom> separators = lastOption.separators;

			if (!separators.isEmpty()) {
				Atom lastSeparator = separators.get(separators.size() - 1);

				if (lastSeparator instanceof VlineAtom && a instanceof VlineAtom) {
					((VlineAtom) lastSeparator).add(((VlineAtom) a).getNumber());
					return this;
				}
			}

			separators.add(a);
		}

		return this;
	}

	@Override
	public String toString() {
		String s = "";
		boolean first = true;
		for (Option o : options) {
			if (first) {
				s = o.toString();
				first = false;
			} else {
				s += ", " + o.toString();
			}
		}
		return s + " size:" + options.size();
	}

	public TeXLength getMinWidth(int j) {
		return options.get(j + 1).getMinWidth();
	}

	public TeXConstants.Align getVertAlignment(int j) {
		return options.get(j + 1).getVertAlignment();
	}
}
