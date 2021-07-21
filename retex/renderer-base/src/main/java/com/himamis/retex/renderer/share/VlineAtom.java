/* VlineAtom.java
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

/**
 * An atom representing a vline in array environment
 */
public class VlineAtom extends Atom {

	private double height;
	private double shift;
	private int n;

	public VlineAtom(double height, double shift, int n) {
		this.height = height;
		this.shift = shift;
		this.n = n;
	}

	public VlineAtom(int n) {
		this.n = n;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setShift(double shift) {
		this.shift = shift;
	}

	public static VlineAtom getEmpty() {
		return new VlineAtom(0);
	}

	public void add(final int n) {
		this.n += n;
	}

	public int getNumber() {
		return n;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		if (n != 0) {
			double drt = env.getTeXFont()
					.getDefaultRuleThickness(env.getStyle());

			return new VlineBox(n, drt, height, shift);
		}

		return StrutBox.getEmpty();
	}

	@Override
	public String toString() {
		if (n == 0) {
			return ".";
		}
		String s = "";
		for (int i = 0; i < n; ++i) {
			s += "|";
		}
		return s;
	}
}
