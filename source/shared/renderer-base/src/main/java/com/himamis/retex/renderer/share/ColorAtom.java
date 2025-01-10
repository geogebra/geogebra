/* ColorAtom.java
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

package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.platform.graphics.Color;

/**
 * An atom representing the foreground and background color of an other atom.
 */
public class ColorAtom extends Atom implements Row {

	// background color
	private final Color background;

	// foreground color
	private final Color color;

	// RowAtom for which the colorsettings apply
	protected final RowAtom elements;

	/**
	 * Creates a new ColorAtom that sets the given colors for the given atom.
	 * Null for a color means: no specific color set for this atom.
	 *
	 * @param atom
	 *            the atom for which the given colors have to be set
	 * @param bg
	 *            the background color
	 * @param c
	 *            the foreground color
	 */
	public ColorAtom(Atom atom, Color bg, Color c) {
		elements = new RowAtom(atom);
		background = bg;
		color = c;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		env.isColored = true;
		TeXEnvironment copy = env.copy();
		if (background != null)
			copy.setBackground(background);
		if (color != null)
			copy.setColor(color);
		Box box = elements.createBox(copy);
		box.setAtom(this);
		return box;
	}

	@Override
	public int getLeftType() {
		return elements.getLeftType();
	}

	@Override
	public int getRightType() {
		return elements.getRightType();
	}

	@Override
	public void setPreviousAtom(Dummy prev) {
		elements.setPreviousAtom(prev);
	}

	protected Color getColor() {
		return color;
	}

	protected Color getBackground() {
		return background;
	}

	protected Color getFg() {
		return color;
	}

	protected Color getBg() {
		return background;
	}

	public RowAtom getElements() {
		return elements;
	}

	public Atom getElement(int i) {
		return elements.getElement(i);
	}
}