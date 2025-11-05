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

import java.util.ArrayList;
import java.util.List;

import org.geogebra.editor.share.catalog.FunctionTemplate;
import org.geogebra.editor.share.catalog.Tag;

/**
 * Function. This class is part of model.
 * <p>
 * function(arguments)
 */
public class FunctionNode extends InternalNode {

	private final FunctionTemplate template;
	private final List<String> placeholders = new ArrayList<>();
	private String commandForSyntax;
	private boolean preventNestedFractions;

	/**
	 * Use MathFormula.newFunction(...)
	 * @param template function template
	 */
	public FunctionNode(FunctionTemplate template) {
		super(template.getArgumentCount());
		this.template = template;
	}

	@Override
	public SequenceNode getChild(int i) {
		return (SequenceNode) super.getChild(i);
	}

	/**
	 * @return Uid name.
	 */
	public Tag getName() {
		return template.getTag();
	}

	/**
	 * @return TeX name.
	 */
	public String getTexName() {
		return template.getTexName();
	}

	/**
	 * Insert Index
	 */
	@Override
	public int getInsertIndex() {
		return template.getInsertIndex();
	}

	/**
	 * Initial Index
	 */
	@Override
	public int getInitialIndex() {
		if (getName() == Tag.FRAC) {
			return getChild(0).size() == 0 ? 0 : 1;
		} else if (getName() == Tag.LOG) {
			return 1;
		} else if (getName() == Tag.ATOMIC_PRE) {
			return 2;
		}
		return 0;
	}

	/**
	 * Up Index for n-th argument
	 * @param n index
	 * @return index to jump with "up" key
	 */
	public int getUpIndex(int n) {
		return template.getUpIndex(n);
	}

	/**
	 * Down Index for n-th argument
	 * @param n index
	 * @return index to jump with "down" key
	 */
	public int getDownIndex(int n) {
		return template.getDownIndex(n);
	}

	/**
	 * @return opening bracket
	 */
	public char getOpeningBracket() {
		return template.getOpeningBracket();
	}

	/**
	 * @return closing bracket
	 */
	public char getClosingBracket() {
		return template.getClosingBracket();
	}

	@Override
	protected String getSimpleName() {
		return "Fn" + template.getTag();
	}

	@Override
	public boolean hasTag(Tag tag) {
		return template.getTag() == tag;
	}

	/**
	 * @param argument part of expression
	 * @return whether argument is either subscript or superscript
	 */
	public static boolean isScript(Node argument) {
		return argument instanceof FunctionNode
				&& (argument.hasTag(Tag.SUPERSCRIPT)
				|| argument.hasTag(Tag.SUBSCRIPT));
	}

	public List<String> getPlaceholders() {
		return placeholders;
	}

	public void setCommandForSyntax(String command) {
		this.commandForSyntax = command;
	}

	public String getCommandForSyntax() {
		return this.commandForSyntax;
	}

	public boolean isPreventingNestedFractions() {
		return this.preventNestedFractions;
	}

	public void setPreventingNestedFractions(boolean prevent) {
		this.preventNestedFractions = prevent;
	}

	@Override
	public boolean isRenderingOwnPlaceholders() {
		return template.getTag().isRenderingOwnPlaceholders();
	}
}
