/* MathContainer.java
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

import java.util.ArrayList;

import com.himamis.retex.editor.share.meta.MetaCharacter;
import com.himamis.retex.editor.share.model.inspect.Inspecting;
import com.himamis.retex.editor.share.model.traverse.Traversing;

/**
 * This class represents abstract model element.
 *
 * @author Bea Petrovicova
 */
abstract public class MathContainer extends MathComponent {

	private static final long serialVersionUID = 1L;

	/**
	 * List of arguments
	 */
	protected ArrayList<MathComponent> arguments = null;

	private boolean isProtected;

	/**
	 * @param size
	 *            number of children
	 */
	MathContainer(int size) {
		if (size > 0) {
			ensureArguments(size);
		}
	}

	/**
	 * checks previous character to see if it combines with ch
	 * 
	 * @param i
	 *            index
	 * @param comp
	 *            new character
	 * @return true if it's been combined (so ch doesn't need adding)
	 */
	protected boolean checkKorean(int i, MathComponent comp) {

		if (comp != null && i > 0 && arguments.size() > 0 && i - 1 < arguments.size()) {

			MathComponent compLast = arguments.get(i - 1);
			if (!(compLast instanceof MathCharacter)) {
				return false;
			}
			String s = compLast.toString();

			char newChar = comp.toString().charAt(0);

			if (!Korean.isSingleKoreanChar(newChar)) {
				return false;
			}

			char lastChar = 0;

			if (s.length() == 1) {
				lastChar = s.charAt(0);
			} else {
				return false;
			}

			char[] ret = Korean.checkMerge(lastChar, newChar);

			// check if "previous" char needs changing
			if (ret[0] != lastChar) {
				MetaCharacter metaChar = new MetaCharacter(ret[0] + "",
						ret[0] + "", ret[0], MetaCharacter.CHARACTER);

				MathCharacter mathChar = (MathCharacter) compLast;
				mathChar.setChar(metaChar);

			}

			char newNewChar = ret[1];

			if (newNewChar == newChar) {
				// doesn't need updating
				return false;
			}

			// '0' means doesn't need updating
			if (newNewChar == 0) {
				return true;
			}

			MathCharacter mathChar = (MathCharacter) comp;
			mathChar.setChar(new MetaCharacter(newNewChar + "", newNewChar + "",
					newNewChar, MetaCharacter.CHARACTER));

			// make sure comp is still inserted
			return false;

		}

		return false;

	}

	/**
	 * Extend arguments array to given size
	 * 
	 * @param size
	 *            number of children
	 */
	protected void ensureArguments(int size) {
		if (arguments == null) {
			arguments = new ArrayList<>(size);
		} else {
			arguments.ensureCapacity(size);
		}
		while (arguments.size() < size) {
			arguments.add(null);
		}
	}

	/**
	 * Returns i-th argument.
	 * 
	 * @param i
	 *            index
	 * @return argument
	 */
	public MathComponent getArgument(int i) {
		return (arguments != null && arguments.size() > i && i >= 0
				? arguments.get(i) : null);
	}

	/**
	 * Sets i-th argument.
	 * 
	 * @param i
	 *            index
	 * @param argument
	 *            argument
	 */
	public void setArgument(int i, MathComponent argument) {
		if (arguments == null) {
			arguments = new ArrayList<>(i + 1);
		}
		if (argument != null) {
			argument.setParent(this);
		}
		arguments.set(i, argument);
	}

	/**
	 * Remove given argument
	 * 
	 * @param i
	 *            index
	 */
	public void removeArgument(int i) {
		if (arguments == null) {
			arguments = new ArrayList<>(i + 1);
		}

		if (i >= arguments.size()) {
			return;
		}

		if (arguments.get(i) != null) {
			arguments.get(i).setParent(null);
		}
		arguments.remove(i);
	}

	/**
	 * Remove all arguments.
	 */
	public void clearArguments() {
		if (arguments == null) {
			arguments = new ArrayList<>();
		}
		for (int i = arguments.size() - 1; i > -1; i--) {
			removeArgument(i);
		}
	}

	/**
	 * Add argument to the end.
	 * 
	 * @param argument
	 *            new argument
	 */
	public void addArgument(MathComponent argument) {
		if (arguments == null) {
			arguments = new ArrayList<>(1);
		}
		if (argument != null) {
			argument.setParent(this);
		}
		arguments.add(argument);
	}

	/**
	 * Add argument to the end.
	 * 
	 * @param index
	 *            index
	 * 
	 * @param argument
	 *            new argument
	 * @return true
	 */
	public boolean addArgument(int index, MathComponent argument) {
		if (arguments == null) {
			arguments = new ArrayList<>(index + 1);
		}
		if (argument != null) {
			argument.setParent(this);
		}
		arguments.add(index, argument);

		return true;
	}

	/**
	 * @return number of arguments.
	 */
	public int size() {
		return arguments != null ? arguments.size() : 0;
	}

	/**
	 * Get index of the first argument.
	 * 
	 * @return index of the first argument.
	 */
	public int first() {
		// strange but correct
		return next(-1);
	}

	/**
	 * Get index of the last argument.
	 * 
	 * @return index of the last argument.
	 */
	public int last() {
		return prev(arguments != null ? arguments.size() : 0);
	}

	/**
	 * Is there a next argument?
	 * 
	 * @param current
	 *            current index
	 * @return whether there is a container after
	 */
	public boolean hasNext(int current) {
		for (int i = current + 1; i < (arguments != null ? arguments.size()
				: 0); i++) {
			if (getArgument(i) instanceof MathContainer) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get index of next argument.
	 * 
	 * @param current
	 *            current index
	 * @return next container index
	 */
	public int next(int current) {
		for (int i = current + 1; i < (arguments != null ? arguments.size()
				: 0); i++) {
			if (getArgument(i) instanceof MathContainer) {
				return i;
			}
		}
		throw new ArrayIndexOutOfBoundsException("Index out of array bounds.");
	}

	/**
	 * Is there previous argument?
	 * 
	 * @param current
	 *            current index
	 * @return whether there is a container before
	 */
	public boolean hasPrev(int current) {
		for (int i = current - 1; i >= 0; i--) {
			if (getArgument(i) instanceof MathContainer) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get index of previous argument.
	 * 
	 * @param current
	 *            current index
	 * @return index of previous container
	 */
	public int prev(int current) {
		for (int i = current - 1; i >= 0; i--) {
			if (getArgument(i) instanceof MathContainer) {
				return i;
			}
		}
		throw new ArrayIndexOutOfBoundsException("Index out of array bounds.");
	}

	/**
	 * Are there any arguments?
	 * 
	 * @return whether this contains containers
	 */
	public boolean hasChildren() {
		for (int i = 0; i < (arguments != null ? arguments.size() : 0); i++) {
			if (getArgument(i) instanceof MathContainer) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return insert index
	 */
	public int getInsertIndex() {
		return 0;
	}

	/**
	 * @return initial index
	 */
	public int getInitialIndex() {
		return 0;
	}

	@Override
	public MathComponent traverse(Traversing traversing) {
		MathComponent component = traversing.process(this);
		if (component != this) {
			return component;
		}
		for (int i = 0; i < size(); i++) {
			MathComponent argument = getArgument(i);
			setArgument(i, argument.traverse(traversing));
		}
		return this;
	}

	@Override
	public boolean inspect(Inspecting inspecting) {
		if (inspecting.check(this)) {
			return true;
		}
		for (int i = 0; i < size(); i++) {
			MathComponent argument = getArgument(i);
			if (inspecting.check(argument)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public abstract MathContainer copy();

	/**
	 * @param argument
	 *            argument
	 * @return index of the argument, -1 if not found
	 */
	public int indexOf(MathComponent argument) {
		return arguments.indexOf(argument);
	}

	/**
	 * Remove an argument without reseting parent
	 * 
	 * @param i
	 *            index
	 */
	public void delArgument(int i) {
		if (i >= 0 && i < arguments.size()) {
			MathComponent removed = arguments.remove(i);
			if (removed != null) {
				removed.setParent(null);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getSimpleName());
		sb.append('[');
		for (int i = 0; i < size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(getArgument(i));
		}
		sb.append(']');
		return sb.toString();
	}

	/**
	 * @return representation in debug string
	 */
	protected String getSimpleName() {
		return getClass().getSimpleName();
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
	 * @return the child math container, if the current container is protected
	 */
	public MathContainer extractLocked() {
		if (size() == 1 && isProtected) {
			MathComponent argument = getArgument(0);
			if (argument instanceof MathContainer) {
				return (MathContainer) argument;
			}
		}

		return this;
	}

	public void setProtected() {
		isProtected = true;
	}

	/**
	 * @return true if sequence is protected from deletion
	 */
	public boolean isProtected() {
		return isProtected;
	}
}
