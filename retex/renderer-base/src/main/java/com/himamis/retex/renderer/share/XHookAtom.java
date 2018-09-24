/* XHookAtom.java
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
 * An atom representing an extensible left or right arrow to handle xleftarrow
 * and xrightarrow commands in LaTeX.
 */
public class XHookAtom extends XAtom {

	private final boolean left;

	@Override
	public Atom duplicate() {
		return setFields(new XHookAtom(over, under, left));
	}

	public XHookAtom(Atom over, Atom under, boolean left) {
		super(over, under);
		this.left = left;
	}

	@Override
	public Box createExtension(TeXEnvironment env, double width) {
		if (left) {
			final CharBox left = (CharBox) Symbols.LEFTARROW.createBox(env);
			final double leftLB = 0.057;
			final double leftW = 0.853;
			final double bodyLW = 0.769;
			final double roundLW = 0.033;
			final CharBox right = (CharBox) Symbols.RHOOK.createBox(env);
			final double rightLB = 0.055;
			final double rightW = 0.142;
			final double bodyRW = 0.;
			final double roundRW = 0.026;

			return XFactory.createExtension(env, left, leftLB, leftW, bodyLW,
					roundLW, right, rightLB, rightW, bodyRW, roundRW, width);
		} else {
			final CharBox left = (CharBox) Symbols.LHOOK.createBox(env);
			final double leftLB = 0.055;
			final double leftW = 0.142;
			final double bodyLW = 0.;
			final double roundLW = 0.026;
			final CharBox right = (CharBox) Symbols.RIGHTARROW.createBox(env);
			final double rightLB = 0.057;
			final double rightW = 0.853;
			final double bodyRW = 0.769;
			final double roundRW = 0.033;

			return XFactory.createExtension(env, left, leftLB, leftW, bodyLW,
					roundLW, right, rightLB, rightW, bodyRW, roundRW, width);
		}
	}

}
