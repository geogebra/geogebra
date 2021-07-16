/* VRowAtom.java
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

/* Modified by Calixte Denizet */

package com.himamis.retex.renderer.share;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.himamis.retex.renderer.share.TeXConstants.Align;

/**
 * An atom representing a vertical row of other atoms.
 */
public class VRowAtom extends Atom {

	// atoms to be displayed horizontally next to eachother
	protected List<Atom> elements;
	private SpaceAtom raise = new SpaceAtom(Unit.EX, 0, 0, 0);
	protected boolean addInterline = false;
	protected boolean vtop = false;
	protected TeXConstants.Align halign = TeXConstants.Align.NONE;

	private VRowAtom(List<Atom> elements, SpaceAtom raise, boolean addInterline,
			boolean vtop, Align halign) {
		this.elements = elements;
		this.raise = raise;
		this.addInterline = addInterline;
		this.vtop = vtop;
		this.halign = halign;
	}

	public VRowAtom() {
		this.elements = new ArrayList<Atom>();
	}

	public VRowAtom(Atom el) {
		if (el == null) {
			this.elements = new ArrayList<Atom>();
		} else {
			if (el instanceof VRowAtom) {
				this.elements = new ArrayList<Atom>(
						((VRowAtom) el).elements.size());
				// no need to make an mrow the only element of an mrow
				elements.addAll(((VRowAtom) el).elements);
			} else {
				this.elements = new ArrayList<Atom>();
				elements.add(el);
			}
		}
	}

	public VRowAtom(Atom... atoms) {
		this.elements = new ArrayList<Atom>(atoms.length);
		for (Atom a : atoms) {
			elements.add(a);
		}
	}

	public VRowAtom(ArrayList<Atom> atoms) {
		this.elements = atoms;
	}

	public void setAddInterline(boolean addInterline) {
		this.addInterline = addInterline;
	}

	public boolean getAddInterline() {
		return this.addInterline;
	}

	public void setHalign(TeXConstants.Align halign) {
		this.halign = halign;
	}

	public TeXConstants.Align getHalign() {
		return halign;
	}

	public void setVtop(boolean vtop) {
		this.vtop = vtop;
	}

	public boolean getVtop() {
		return vtop;
	}

	public void setRaise(Unit unit, double r) {
		raise = new SpaceAtom(unit, r, 0, 0);
	}

	public Atom getLastAtom() {
		final int s = elements.size();
		if (s != 0) {
			return elements.remove(s - 1);
		}

		return EmptyAtom.get();
	}

	public final void add(Atom el) {
		if (el != null) {
			elements.add(0, el);
		}
	}

	public final void append(Atom el) {
		if (el != null) {
			elements.add(el);
		}
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		VerticalBox vb = new VerticalBox();
		Box interline = new StrutBox(0.,
				env.lengthSettings().getLength("baselineskip", env), 0., 0.);
		if (halign != TeXConstants.Align.NONE) {
			double maxWidth = -Double.POSITIVE_INFINITY;
			ArrayList<Box> boxes = new ArrayList<>();
			for (ListIterator it = elements.listIterator(); it.hasNext();) {
				Box b = ((Atom) it.next()).createBox(env);
				boxes.add(b);
				if (maxWidth < b.getWidth()) {
					maxWidth = b.getWidth();
				}
			}

			// convert atoms to boxes and add to the horizontal box
			for (ListIterator it = boxes.listIterator(); it.hasNext();) {
				Box b = (Box) it.next();
				vb.add(new HorizontalBox(b, maxWidth, halign));
				if (addInterline && it.hasNext()) {
					vb.add(interline);
				}
			}
		} else {
			// convert atoms to boxes and add to the horizontal box
			for (ListIterator it = elements.listIterator(); it.hasNext();) {
				vb.add(((Atom) it.next()).createBox(env));
				if (addInterline && it.hasNext()) {
					vb.add(interline);
				}
			}
		}

		vb.setShift(-raise.createBox(env).getWidth());
		if (vtop) {
			final double t = vb.getSize() == 0 ? 0
					: vb.children.get(0).getHeight();
			vb.setHeight(t);
			vb.setDepth(vb.getDepth() + vb.getHeight() - t);
		} else {
			final int s = vb.children.size();
			final double t = vb.getSize() == 0 ? 0
					: vb.children.get(s - 1).getDepth();
			vb.setHeight(vb.getDepth() + vb.getHeight() - t);
			vb.setDepth(t);
		}

		return vb;
	}

	public Atom getElement(int i) {
		return i < elements.size() ? elements.get(i) : null;
	}
}
