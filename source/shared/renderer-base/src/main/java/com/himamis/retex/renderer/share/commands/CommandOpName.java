/* CommandOpName.java
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
import com.himamis.retex.renderer.share.RomanAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;

public class CommandOpName extends Command {

	private final String name;
	private final String post;
	private final boolean limits;

	public CommandOpName(final String name, final String post,
			final boolean limits) {
		this.name = name;
		this.post = post;
		this.limits = limits;
	}

	public CommandOpName(final String name, final boolean limits) {
		this(name, null, limits);
	}

	@Override
	public boolean init(TeXParser tp) {
		tp.addToConsumer(createOperation(name, post, limits));
		return false;
	}

	public static Atom createOperation(String name, String post, boolean limits) {
		Atom a;
		if (post == null) {
			a = new RomanAtom(TeXParser.getAtomForLatinStr(name, true));
		} else {
			final RowAtom ra = TeXParser.getAtomForLatinStr(name,
					new RowAtom(name.length() + 1 + post.length()), true);
			ra.add(new SpaceAtom(TeXConstants.Muskip.THIN));
			a = new RomanAtom(TeXParser.getAtomForLatinStr(post, ra, true));
		}
		a = a.changeType(TeXConstants.TYPE_BIG_OPERATOR);
		a.type_limits = limits ? TeXConstants.SCRIPT_LIMITS
				: TeXConstants.SCRIPT_NOLIMITS;
		return a;
	}
}
