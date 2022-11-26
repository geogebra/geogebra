/* MathSequence.java
 * =========================================================================
 * This file is part of the Mirai Math TN - http://mirai.sourceforge.net
 *
 * Copyright (C) 2008-2009 Bea Petrovicova
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
 */

package com.himamis.retex.editor.share.model;

import com.himamis.retex.editor.share.meta.MetaCharacter;
import com.himamis.retex.editor.share.meta.Tag;

/**
 * Sequence of math expressions
 */
public class MathSequence extends MathContainer {

	private static final long serialVersionUID = 1L;
	private boolean keepCommas;

	/**
	 * Use MathFormula.newSequence(...)
	 */
	public MathSequence() {
		super(0);
		ensureArguments(0);
	}

	@Override
	public void addArgument(MathComponent argument) {
		if (argument != null) {
			argument.setParent(this);
		}
		arguments.add(argument);
	}

	@Override
	public void append(MetaCharacter mathChar) {
		if (arguments.isEmpty()) {
			super.append(mathChar);
			return;
		}
		MathComponent last = arguments.get(arguments.size() - 1);
		if (last instanceof MathCharacter
				&& ((MathCharacter) last).mergeUnicode(mathChar.getUnicodeString())) {
			checkModifier(arguments.size() - 1);
			return;
		}
		super.append(mathChar);
	}

	private boolean checkModifier(int i) {
		if (i > 0) {
			MathCharacter last = (MathCharacter) arguments.get(i);
			MathComponent prev = arguments.get(i - 1);
			if (prev instanceof MathCharacter
					&& ((MathCharacter) prev).mergeUnicode(last.getUnicodeString())) {
				removeArgument(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Add argument, consider unicode surogates
	 * @param i index
	 * @param argument argument
	 * @return sequence size change (may be negative after unicode merge)
	 */
	public int addArgument(int i, MetaCharacter argument) {
		if (i > 0 && i <= arguments.size()) {
			MathComponent prev = arguments.get(i - 1);
			if (prev instanceof MathCharacter
					&& ((MathCharacter) prev).mergeUnicode(argument.getUnicodeString())) {
				return checkModifier(i - 1) ? -1 : 0;
			}
		}
		return addArgument(i, new MathCharacter(argument)) ? 1 : 0;
	}

	@Override
	public boolean addArgument(int i, MathComponent argument) {
		// Korean merging separated from unicode merging, bc we want
		// characters merged on paste, but not merge them in parser
		if (checkKorean(i, argument)) {
			return false;
		}
		if (i <= arguments.size()) {
			if (argument != null) {
				argument.setParent(this);
			}
			arguments.add(i, argument);
		}

		return true;
	}

	@Override
	public MathSequence copy() {
		MathSequence sequence = new MathSequence();
		for (int i = 0; i < arguments.size(); i++) {
			MathComponent component = getArgument(i);
			MathComponent newComponent = component.copy();
			sequence.addArgument(i, newComponent);
		}
		return sequence;
	}

	/**
	 * @return whether sequence contains operator (including power).
	 */
	public boolean hasOperator() {
		for (int i = 0; i < size(); i++) {
			if (isOperator(i)) {
				return true;
			} else if (getArgument(i) instanceof MathFunction
					&& getArgument(i).hasTag(Tag.SUPERSCRIPT)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Is i'th argument operator.
	 * 
	 * @param i
	 *            index
	 * @return whether given argument is an operator
	 */
	public boolean isOperator(int i) {
		return i >= 0 && i < size() && getArgument(i) instanceof MathCharacter
				&& ((MathCharacter) getArgument(i)).isOperator();
	}

	/**
	 * If this has a single element (x), replace it with just x. Flatten nested
	 * sequences.
	 */
	public void removeBrackets() {
		if (size() == 1 && getArgument(0) instanceof MathArray) {
			MathArray arg0 = (MathArray) getArgument(0);
			if (arg0.size() == 1 && arg0.getArgument(0) != null
					&& arg0.getOpenKey() == '(') {
				setArgument(0, arg0.getArgument(0));
			}
		}
		if (size() == 1 && getArgument(0) instanceof MathSequence) {
			MathSequence arg0 = (MathSequence) getArgument(0);
			clearArguments();
			for (int i = 0; i < arg0.size(); i++) {
				addArgument(arg0.getArgument(i));
			}
		}
	}

	@Override
	public MathSequence wrap() {
		return this;
	}

	public int getArgumentCount() {
		return arguments.size();
	}

	public boolean isKeepCommas() {
		return keepCommas;
	}

	public void setKeepCommas() {
		this.keepCommas = true;
	}
}
