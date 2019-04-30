/* SubSupCom.java
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

import com.himamis.retex.renderer.share.exception.ParseException;

public class SubSupCom implements AtomConsumer {

	private enum State {
		SUB_WAIT,

		SUP_WAIT,

		OK
	}

	private Atom base;
	private Atom sub;
	private Atom sup;
	private State state;

	public SubSupCom(final char c) {
		state = c == '^' ? State.SUP_WAIT : State.SUB_WAIT;
	}

	public void setSub(Atom sub) {
		this.sub = sub;
	}

	public void setBase(Atom base) {
		this.base = base;
	}

	public void setState(TeXParser tp, final char c) {
		switch (state) {
		case SUB_WAIT:
		case SUP_WAIT:
			throw new ParseException(tp, "Invalid " + c);
		case OK:
			if (c == '^') {
				state = State.SUP_WAIT;
			} else {
				state = State.SUB_WAIT;
			}
		}
	}

	@Override
	public boolean init(TeXParser tp) {
		this.base = SubSupCom.getBase(tp);
		return false;
	}

	@Override
	public void add(TeXParser tp, Atom a) {
		switch (state) {
		case SUB_WAIT:
			addToSub(a);
			state = State.OK;
			break;
		case SUP_WAIT:
			addToSup(a);
			state = State.OK;
			break;
		case OK:
			tp.closeConsumer(get());
			tp.addToConsumer(a);
			break;
		}
	}

	private void addToSup(Atom a) {
		if (sup != null) {
			if (sup instanceof RowAtom) {
				((RowAtom) sup).add(a);
			} else {
				sup = new RowAtom(sup, a);
			}
		} else {
			sup = a;
		}
	}

	private void addToSub(Atom a) {
		if (sub != null) {
			if (sub instanceof RowAtom) {
				((RowAtom) sub).add(a);
			} else {
				sub = new RowAtom(sub, a);
			}
		} else {
			sub = a;
		}
	}

	@Override
	public Atom getLastAtom() {
		return null;
	}

	@Override
	public boolean close(TeXParser tp) {
		tp.closeConsumer(get());
		return true;
	}

	@Override
	public boolean isClosable() {
		return true;
	}

	@Override
	public RowAtom steal(TeXParser tp) {
		close(tp);
		return tp.steal();
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

	public static Atom get(Atom base, Atom sub, Atom sup) {
		if (base.getRightType() == TeXConstants.TYPE_BIG_OPERATOR) {
			return new BigOperatorAtom(base, sub, sup);
		} else if (base instanceof OverUnderDelimiter) {
			if (((OverUnderDelimiter) base).isOver()) {
				if (sup != null) {
					((OverUnderDelimiter) base).addScript(sup);
					return new ScriptsAtom(base, sub, null);
				}
			} else if (sub != null) {
				((OverUnderDelimiter) base).addScript(sub);
				return new ScriptsAtom(base, null, sup);
			}
		}
		return new ScriptsAtom(base, sub, sup);
	}

	public static Atom getBase(TeXParser tp) {
		final Atom a = tp.getLastAtom();
		if (a != null) {
			return a;
		}
		return MHeightAtom.get();
	}

	private Atom get() {
		return SubSupCom.get(base, sub, sup);
	}
}
