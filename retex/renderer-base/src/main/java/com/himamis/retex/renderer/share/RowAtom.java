/* RowAtom.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
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

/* Modified by Calixte Denizet to handle the case where several ligatures occure*/

package com.himamis.retex.renderer.share;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Stack;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * An atom representing a horizontal row of other atoms, to be seperated by
 * glue. It's also responsible for inserting kerns and ligatures.
 */
public class RowAtom extends Atom implements Row {

	// atoms to be displayed horizontally next to eachother
	protected ArrayList<Atom> elements;

	public boolean lookAtLastAtom = false;

	// previous atom (for nested Row atoms)
	private Dummy previousAtom = null;

	private boolean shape = false;

	// set of atom types that make a previous bin atom change to ord
	private final static BitSet BIN_SET = new BitSet(16) {
		{
			set(TeXConstants.TYPE_BINARY_OPERATOR);
			set(TeXConstants.TYPE_BIG_OPERATOR);
			set(TeXConstants.TYPE_RELATION);
			set(TeXConstants.TYPE_OPENING);
			set(TeXConstants.TYPE_PUNCTUATION);
		}
	};

	// set of atom types that can possibly need a kern or, together with the
	// previous atom, be replaced by a ligature
	private final static BitSet LIG_KERN_SET = new BitSet(16) {
		{
			set(TeXConstants.TYPE_ORDINARY);
			set(TeXConstants.TYPE_BIG_OPERATOR);
			set(TeXConstants.TYPE_BINARY_OPERATOR);
			set(TeXConstants.TYPE_RELATION);
			set(TeXConstants.TYPE_OPENING);
			set(TeXConstants.TYPE_CLOSING);
			set(TeXConstants.TYPE_PUNCTUATION);
		}
	};

	protected RowAtom() {
		this.elements = new ArrayList<Atom>();
	}

	public RowAtom(List<Atom> elements) {
		this.elements = new ArrayList<>(elements);
	}

	public RowAtom(Atom... atoms) {
		this(Arrays.asList(atoms));
	}

	public RowAtom(final int size) {
		this.elements = new ArrayList<Atom>(size);
	}

	public RowAtom(Atom el) {
		if (el == null) {
			this.elements = new ArrayList<Atom>();
		} else {
			if (el instanceof RowAtom) {
				this.elements = new ArrayList<Atom>(
						((RowAtom) el).elements.size());
				// no need to make an mrow the only element of an mrow
				elements.addAll(((RowAtom) el).elements);
			} else {
				this.elements = new ArrayList<Atom>(1);
				elements.add(el);
			}
		}
	}


	public void append(final RowAtom ra) {
		elements.addAll(ra.elements);
	}

	public void setShape(final boolean shape) {
		this.shape = shape;
	}

	public Atom getLastAtom() {
		final int s = elements.size();
		if (s != 0) {
			return elements.remove(s - 1);
		}

		return EmptyAtom.get();
	}

	public Atom last() {
		final int s = elements.size();
		if (s != 0) {
			return elements.get(s - 1);
		}
		return null;
	}

	public final int size() {
		return elements.size();
	}

	public void substitute(final Substitution subst) {
		final int N = elements.size();
		for (int i = 0; i < N; ++i) {
			elements.set(i, subst.get(elements.get(i)));
		}
	}

	public final void add(Atom... atoms) {
		for (Atom a : atoms) {
			addAtom(a);
		}
	}

	private void addAtom(Atom atom) {
		if (atom == null) {
			return;
		}

		if (atom instanceof RowAtom) {
			addRowAtom((RowAtom) atom);
		} else {
			elements.add(atom);
			if (atom instanceof TypedAtom) {
				addTypedAtom(atom);
			}
		}
	}

	private void addRowAtom(RowAtom row) {
		elements.addAll(row.getElements());
	}

	private void addTypedAtom(Atom a) {
		// TODO: check this stuff (added for back comp)
		final int rtype = a.getRightType();
		if (rtype == TeXConstants.TYPE_BINARY_OPERATOR
				|| rtype == TeXConstants.TYPE_RELATION) {
			elements.add(BreakMarkAtom.get());
		}
	}

	/**
	 *
	 * @param cur
	 *            current atom being processed
	 * @param prev
	 *            previous atom
	 */
	private void changeToOrd(Dummy cur, Dummy prev, Atom next) {
		// TeXBook p. 438
		int type = cur.getLeftType();
		if (type == TeXConstants.TYPE_BINARY_OPERATOR
				&& ((prev == null || BIN_SET.get(prev.getRightType()))
						|| next == null)) {
			cur.setType(TeXConstants.TYPE_ORDINARY);
		} else if (next != null
				&& cur.getRightType() == TeXConstants.TYPE_BINARY_OPERATOR) {
			int nextType = next.getLeftType();
			if (nextType == TeXConstants.TYPE_RELATION
					|| nextType == TeXConstants.TYPE_CLOSING
					|| nextType == TeXConstants.TYPE_PUNCTUATION) {
				cur.setType(TeXConstants.TYPE_ORDINARY);
			}
		}
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		TeXFont tf = env.getTeXFont();

		Stack<HorizontalBox> hBox = new Stack<>();
		hBox.push((HorizontalBox) new HorizontalBox(env.getColor(),
				env.getBackground()).setAtom(this));

		env.resetColors();

		Dummy prevAtom = null;

		ArrayList<Atom> elementsCopy = new ArrayList<>(elements);

		for (int i = 0; i < elementsCopy.size(); ++i) {
			Atom at = elementsCopy.get(i);

			if (at instanceof SelectionAtom) {
				SelectionAtom ca = (SelectionAtom) at;

				hBox.push((HorizontalBox) new HorizontalBox(ca.getColor(),
						ca.getBackground()).setAtom(this));

				elementsCopy.remove(i);
				elementsCopy.addAll(i, ca.elements.elements);
				elementsCopy.add(i + ca.elements.elements.size(), null);

				at = elementsCopy.get(i);
			}

			boolean markAdded = false;
			if (at instanceof BreakMarkAtom) {
				// skip the BreakMarkAtoms
				markAdded = true;
				++i;
				for (; i < elementsCopy.size(); ++i) {
					at = elementsCopy.get(i);
					if (!(at instanceof BreakMarkAtom)) {
						break;
					}
				}
			}

			if (at instanceof CursorAtom) {
				hBox.peek().add(at.createBox(env));
				continue;
			}

			if (at instanceof MathchoiceAtom) {
				at = ((MathchoiceAtom) at).chose(env);
			}

			if (at == null) {
				HorizontalBox box = hBox.pop();
				hBox.peek().add(box);
				continue;
			}

			Dummy curAtom = new Dummy(at);
			Atom nextAtom = null;
			for (int j = i + 1; j < elementsCopy.size(); j++) {
				nextAtom = elementsCopy.get(j);

				if (nextAtom == null || nextAtom instanceof CursorAtom) {
					continue;
				}

				if (nextAtom instanceof SelectionAtom) {
					nextAtom = ((SelectionAtom) nextAtom).getElements().elements.get(0);
				}

				break;
			}

			double kern = 0.;

			changeToOrd(curAtom, prevAtom, nextAtom);

			// it is on the boundary of a color atom, or next to a cursor, in which
			// case it shouldn't apply ligatures, only proper kerning
			boolean onBoundary = false;
			for (int j = i + 1; j < elementsCopy.size(); ++j) {
				nextAtom = elementsCopy.get(j);

				if (nextAtom == null || nextAtom instanceof CursorAtom) {
					onBoundary = true;
					continue;
				}

				if (nextAtom instanceof SelectionAtom) {
					onBoundary = true;
					nextAtom = ((SelectionAtom) nextAtom).getElements().elements.get(0);
				}

				if (curAtom.isCharSymbol()
						&& (nextAtom instanceof CharSymbol)
						&& curAtom.getRightType() == TeXConstants.TYPE_ORDINARY
						&& LIG_KERN_SET.get(nextAtom.getLeftType())) {

					curAtom.markAsTextSymbol();
					final CharFont l = curAtom.getCharFont(tf);
					final CharFont r = ((CharSymbol) nextAtom).getCharFont(tf);
					final CharFont lig = tf.getLigature(l, r);
					if (lig == null || onBoundary) {
						kern = tf.getKern(l, r, env.getStyle());
						break;
					} else {
						i = j;
						curAtom.changeAtom(new FixedCharAtom(lig));
					}
				} else {
					break;
				}
			}

			// insert glue, unless it's the first element of the row
			// OR this element or the next is a Kern.
			if (prevAtom != null && !prevAtom.isKern() && !curAtom.isKern()) {
				final Box glue = Glue.get(prevAtom.getRightType(),
						curAtom.getLeftType(), env);
				if (glue != null) {
					hBox.peek().add(glue);
				}
			}

			if (markAdded || (at instanceof CharAtom
					&& Character.isDigit(((CharAtom) at).getCharacter()))) {
				hBox.peek().addBreakPosition(hBox.peek().children.size());
			}

			final Box b = curAtom.createBox(env);
			hBox.peek().add(b);

			// set last used fontId (for next atom)
			env.setLastFont(b.getLastFont());

			// insert kern
			if (Math.abs(kern) > TeXFormula.PREC) {
				hBox.peek().add(new StrutBox(kern, 0., 0., 0.));
			}

			// kerns do not interfere with the normal glue-rules without kerns
			if (!curAtom.isKern()) {
				prevAtom = curAtom;
			}
		}

		if (shape) {
			return FactoryProvider.getInstance().getBoxDecorator().decorate(hBox.peek());
		}

		return hBox.peek();
	}

	@Override
	public void setPreviousAtom(Dummy prev) {
		previousAtom = prev;
	}

	public boolean lookAtLast() {
		return lookAtLastAtom;
	}

	public void lookAtLast(final boolean b) {
		lookAtLastAtom = b;
	}

	public final Atom simplify() {
		final int s = elements.size();
		if (s == 0) {
			return EmptyAtom.get();
		} else if (s == 1) {
			return elements.get(0);
		}
		return this;
	}

	public final boolean isEmpty() {
		return elements.isEmpty();
	}

	public ArrayList<Atom> getElements() {
		return elements;
	}

	@Override
	public String toString() {
		String s = "RowAtom {";
		for (Atom e : elements) {
			s += e + "; ";
		}
		s += "}";
		return s;
	}

	public Atom getElement(int i) {
		return i < elements.size() ? elements.get(i) : null;
	}
}
