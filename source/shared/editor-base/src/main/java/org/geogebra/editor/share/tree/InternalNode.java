/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.editor.share.tree;

import java.util.ArrayList;
import java.util.Iterator;

import org.geogebra.editor.share.catalog.CharacterTemplate;
import org.geogebra.editor.share.tree.inspect.Inspecting;
import org.geogebra.editor.share.tree.traverse.Traversing;

/**
 * This class represents abstract model element.
 */
abstract public class InternalNode extends Node implements Iterable<Node> {

	/**
	 * List of child nodes
	 */
	protected ArrayList<Node> children = null;

	private boolean isProtected;

	/**
	 * @param size number of children
	 */
	InternalNode(int size) {
		if (size > 0) {
			ensureChildren(size);
		}
	}

	/**
	 * checks previous character to see if it combines with ch
	 * @param i index
	 * @param comp new character
	 * @return true if it's been combined (so ch doesn't need adding)
	 */
	protected boolean checkKorean(int i, Node comp) {

		if (comp != null && i > 0 && children.size() > 0 && i - 1 < children.size()) {

			Node compLast = children.get(i - 1);
			if (!(compLast instanceof CharacterNode)) {
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
				CharacterTemplate characterComponent = new CharacterTemplate(
						ret[0] + "", ret[0], CharacterTemplate.TYPE_CHARACTER);

				CharacterNode mathChar = (CharacterNode) compLast;
				mathChar.setChar(characterComponent);

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

			CharacterNode mathChar = (CharacterNode) comp;
			mathChar.setChar(new CharacterTemplate(newNewChar + "",
					newNewChar, CharacterTemplate.TYPE_CHARACTER));

			// make sure comp is still inserted
			return false;

		}

		return false;

	}

	/**
	 * Extend arguments array to given size
	 * @param size number of children
	 */
	protected void ensureChildren(int size) {
		if (children == null) {
			children = new ArrayList<>(size);
		} else {
			children.ensureCapacity(size);
		}
		while (children.size() < size) {
			children.add(null);
		}
	}

	/**
	 * Returns i-th argument.
	 * @param i index
	 * @return argument
	 */
	public Node getChild(int i) {
		return children != null && children.size() > i && i >= 0
				? children.get(i) : null;
	}

	/**
	 * Sets i-th child.
	 * @param i index
	 * @param child child
	 */
	public void setChild(int i, Node child) {
		if (children == null) {
			children = new ArrayList<>(i + 1);
		}
		if (child != null) {
			child.setParent(this);
		}
		children.set(i, child);
	}

	/**
	 * Remove given argument
	 * @param i index
	 */
	public void removeChild(int i) {
		if (children == null) {
			children = new ArrayList<>(i + 1);
		}

		if (i >= children.size()) {
			return;
		}

		if (children.get(i) != null) {
			children.get(i).setParent(null);
		}
		children.remove(i);
	}

	/**
	 * Remove all arguments.
	 */
	public void clearChildren() {
		if (children == null) {
			children = new ArrayList<>();
		}
		for (int i = children.size() - 1; i > -1; i--) {
			removeChild(i);
		}
	}

	/**
	 * Add child to the end.
	 * @param child new child
	 */
	public void addChild(Node child) {
		if (children == null) {
			children = new ArrayList<>(1);
		}
		if (child != null) {
			child.setParent(this);
		}
		children.add(child);
	}

	/**
	 * Like addArgument, but with unicode magic
	 * @param mathChar added character
	 */
	public void append(CharacterTemplate mathChar) {
		addChild(new CharacterNode(mathChar));
	}

	/**
	 * Add argument to the end.
	 * @param index index
	 * @param child new argument
	 * @return true
	 */
	public boolean addChild(int index, Node child) {
		if (children == null) {
			children = new ArrayList<>(index + 1);
		}
		if (child != null) {
			child.setParent(this);
		}
		children.add(index, child);

		return true;
	}

	/**
	 * @return number of children.
	 */
	public int size() {
		return children != null ? children.size() : 0;
	}

	/**
	 * Get index of the first child.
	 * @return index of the first child.
	 */
	public int first() {
		return getNext(-1);
	}

	/**
	 * Get index of the last argument.
	 * @return index of the last argument.
	 */
	public int last() {
		return getPrevious(size());
	}

	/**
	 * Returns true if there is a next child
	 * @param current current index
	 * @return whether there is a container after
	 */
	public boolean hasNext(int current) {
		return getNext(current) != -1;
	}

	/**
	 * Get the index of the next child.
	 * @param current current index
	 * @return next container index or -1 if there is no next internal node
	 */
	public int getNext(int current) {
		for (int i = current + 1; i < size(); i++) {
			if (getChild(i) instanceof InternalNode) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns true if there is a previous child.
	 * @param current current index
	 * @return whether there is an internal node before
	 */
	public boolean hasPrevious(int current) {
		return getPrevious(current) != -1;
	}

	/**
	 * Get index of previous child.
	 * @param current current index
	 * @return index of previous internal node, or -1 if there is none
	 */
	public int getPrevious(int current) {
		for (int i = current - 1; i >= 0; i--) {
			if (getChild(i) instanceof InternalNode) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Are there any children?
	 * @return whether this contains containers
	 */
	public boolean hasChildren() {
		if (children == null) {
			return false;
		}
		return hasNext(-1);
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
	public Node traverse(Traversing traversing) {
		Node node = traversing.process(this);
		if (node != this) {
			return node;
		}
		for (int i = 0; i < size(); i++) {
			Node argument = getChild(i);
			if (argument != null) {
				setChild(i, argument.traverse(traversing));
			}
		}
		return this;
	}

	@Override
	public boolean inspect(Inspecting inspecting) {
		if (inspecting.check(this)) {
			return true;
		}
		for (int i = 0; i < size(); i++) {
			Node argument = getChild(i);
			if (inspecting.check(argument)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param child argument
	 * @return index of the argument, -1 if not found
	 */
	public int indexOf(Node child) {
		return children.indexOf(child);
	}

	/**
	 * Remove an argument without resetting parent
	 * @param index index
	 */
	public void deleteChild(int index) {
		if (index >= 0 && index < children.size()) {
			Node removed = children.remove(index);
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
			sb.append(getChild(i));
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
	 * @param i index
	 * @return whether given argument is a sub/super-script
	 */
	public boolean isScript(int i) {
		return i >= 0 && i < size() && FunctionNode.isScript(getChild(i));
	}

	/**
	 * @return the child math container, if the current container is protected
	 */
	public InternalNode extractLocked() {
		if (size() == 1 && isProtected) {
			Node argument = getChild(0);
			if (argument instanceof InternalNode) {
				return (InternalNode) argument;
			}
		}

		return this;
	}

	/**
	 * Mark as protected from deletion.
	 */
	public void setProtected() {
		isProtected = true;
	}

	/**
	 * @return true if sequence is protected from deletion
	 */
	public boolean isProtected() {
		return isProtected;
	}

	/**
	 * Inside of protected array all commas are considered protected
	 * @param index index
	 * @return whether argument with given index is a protected comma
	 */
	public boolean isChildProtected(int index) {
		if (index < 0 || index >= children.size()) {
			return false;
		}

		return getParent() != null && getParent().getParent() != null
				&& getParent().getParent().isProtected
				&& children.get(index).isFieldSeparator();
	}

	/**
	 * Check if the i-th position of this container is a comma
	 * @param i position to check
	 * @return whether it is a separator (comma or vertical line)
	 */
	public boolean isFieldSeparator(int i) {
		if (i < 0 || i >= children.size()) {
			return false;
		}

		return children.get(i).isFieldSeparator();
	}

	@Override
	public Iterator<Node> iterator() {
		return children.iterator();
	}

	/**
	 * Replace several arguments with single one
	 * @param start index of first argument
	 * @param end index of last argument
	 * @param array replacement
	 * @return list of removed elements
	 */
	public ArrayList<Node> replaceChildren(int start, int end, Node array) {
		ArrayList<Node> removed = new ArrayList<>();
		for (int i = end; i >= start; i--) {
			removed.add(getChild(i));
			removeChild(i);
		}
		addChild(start, array);
		return removed;
	}

	/**
	 * @return whether template has highlighted boxes
	 */
	public boolean isRenderingOwnPlaceholders() {
		return false;
	}
}
