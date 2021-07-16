/* FBoxAtom.java
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

import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.serialize.HasTrueBase;

/**
 * An atom representing a boxed base atom.
 */
public class FBoxAtom extends Atom implements HasTrueBase {

	protected final Atom base;
	protected final Color bg;
	protected final Color line;

	public FBoxAtom(Atom base, Color bg, Color line) {
		this.base = base;
		this.bg = bg;
		this.line = line;
	}

	public FBoxAtom(Atom base) {
		this(base, null, null);
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		final Box bbase = base.createBox(env);
		final double drt = env.lengthSettings().getLength("fboxrule", env);
		final double space = env.lengthSettings().getLength("fboxsep", env);
		if (bg == null) {
			return new FramedBox(bbase, drt, space);
		}

		env.isColored = true;
		return new FramedBox(bbase, drt, space, line, bg);
	}

	public Atom getTrueBase() {
		return base;
	}
}
