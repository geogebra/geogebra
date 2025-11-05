/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.tree;

import org.geogebra.editor.share.catalog.CharacterTemplate;

/**
 * Sequence of math expressions
 */
public class SequenceNode extends InternalNode {

	private boolean keepCommas;

	/**
	 * Use MathFormula.newSequence(...)
	 */
	public SequenceNode() {
		super(0);
		ensureChildren(0);
	}

	@Override
	public void addChild(Node child) {
		if (child != null) {
			child.setParent(this);
		}
		children.add(child);
	}

	@Override
	public void append(CharacterTemplate mathChar) {
		if (children.isEmpty()) {
			super.append(mathChar);
			return;
		}
		Node last = children.get(children.size() - 1);
		if (last instanceof CharacterNode
				&& ((CharacterNode) last).mergeUnicode(mathChar.getUnicodeString())) {
			checkModifier(children.size() - 1);
			return;
		}
		super.append(mathChar);
	}

	private boolean checkModifier(int i) {
		if (i > 0) {
			CharacterNode last = (CharacterNode) children.get(i);
			Node prev = children.get(i - 1);
			if (prev instanceof CharacterNode
					&& ((CharacterNode) prev).mergeUnicode(last.getUnicodeString())) {
				removeChild(i);
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
	public int addChild(int i, CharacterTemplate argument) {
		if (i > 0 && i <= children.size()) {
			Node prev = children.get(i - 1);
			if (prev instanceof CharacterNode
					&& ((CharacterNode) prev).mergeUnicode(argument.getUnicodeString())) {
				return checkModifier(i - 1) ? -1 : 0;
			}
		}
		return addChild(i, new CharacterNode(argument)) ? 1 : 0;
	}

	@Override
	public boolean addChild(int i, Node child) {
		// Korean merging separated from unicode merging, bc we want
		// characters merged on paste, but not merge them in parser
		if (checkKorean(i, child)) {
			return false;
		}
		if (i <= children.size()) {
			if (child != null) {
				child.setParent(this);
			}
			children.add(i, child);
		}

		return true;
	}

	/**
	 * If this has a single element (x), replace it with just x. Flatten nested
	 * sequences.
	 */
	public void removeBrackets() {
		if (size() == 1 && getChild(0) instanceof ArrayNode) {
			ArrayNode arg0 = (ArrayNode) getChild(0);
			if (arg0.size() == 1 && arg0.getChild(0) != null
					&& arg0.getOpenDelimiter().getCharacter() == '(') {
				setChild(0, arg0.getChild(0));
			}
		}
		if (size() == 1 && getChild(0) instanceof SequenceNode) {
			SequenceNode arg0 = (SequenceNode) getChild(0);
			clearChildren();
			for (Node child: arg0) {
				addChild(child);
			}
		}
	}

	public int getArgumentCount() {
		return children.size();
	}

	public boolean isKeepCommas() {
		return keepCommas;
	}

	/**
	 * Prevent commas from being removed.
	 */
	public void setKeepCommas() {
		this.keepCommas = true;
	}
}
