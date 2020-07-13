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

import com.himamis.retex.editor.share.meta.Tag;

/**
 * Sequence of math expressions
 */
public class MathSequence extends MathContainer {

	private static final long serialVersionUID = 1L;

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
	public boolean addArgument(int i, MathComponent argument) {
		if (checkKorean(i, argument)) {
			return false;
		}
		// argument = checkKorean(i, argument);

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
	 * Is i'th argument script.
	 * 
	 * @param i
	 *            index
	 * @return whether given argument is a sub/super-script
	 */
	public boolean isScript(int i) {
		return i >= 0 && i < size() && MathFunction.isScript(getArgument(i));
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
			MathArray arg0 = ((MathArray) getArgument(0));
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
	public MathComponent wrap() {
		return this;
	}

	/**
	 * Extract the matrix if sequence contains one only.
	 *
	 * @return the matrix if any, the component unchanged
	 * 		   otherwise.
	 */
	public MathContainer extractMatrix() {
		if (size() == 1) {
			MathComponent argument = getArgument(0);
			if (MathArray.isMatrix(argument)) {
				return (MathContainer) argument;
			}
		}

		return this;
	}

	/**
	 *
	 * @return true if sequence is a matrix.
	 */
	public boolean isMatrix() {
		return extractMatrix() != this;
	}
}
