/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/**
 * Used as internal return type in Parser.
 * Stores a label. 
 */
package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.StringTemplate;

import java.util.Set;
import java.util.Vector;

/**
 * Common class for objects obtained from the parser that are not yet processed
 * to GeoElements. They may also persist in ExpressionNodes of functions
 * 
 * @author Markus
 * 
 */
public abstract class ValidExpression implements ExpressionValue {

	private Vector<String> labels;
	private boolean inTree; // used by ExpressionNode
	private boolean keepInputUsed; // flag used by GeoGebraCAS
	private AssignmentType assignmentType = AssignmentType.NONE;

	public String toRealString(StringTemplate tpl) {
		return toString(tpl);
	}

	/**
	 * @param assignmentType
	 *            the {@link AssignmentType} to set
	 */
	public void setAssignmentType(AssignmentType assignmentType) {
		this.assignmentType = assignmentType;
	}

	/**
	 * @return the current {@link AssignmentType}
	 */
	public AssignmentType getAssignmentType() {
		return assignmentType;
	}

	/**
	 * @param label
	 *            label to be added
	 */
	public void addLabel(String label) {
		initLabels();
		labels.add(label);
	}

	private void initLabels() {
		if (labels == null)
			labels = new Vector<String>();
	}

	/**
	 * @param labellist
	 *            list of labels to be added
	 */
	public void addLabel(Vector<String> labellist) {
		initLabels();
		labels.addAll(labellist);
	}

	/**
	 * @return count of labels
	 */
	public int labelCount() {
		if (labels == null) {
			return 0;
		}
		return labels.size();
	}

	/**
	 * @param index
	 *            index
	 * @return label
	 */
	public String getLabel(int index) {
		if (index < 0 || index >= labelCount()) {
			return null;
		}
		return labels.get(index);
	}

	/**
	 * @return array of all labels
	 */
	public String[] getLabels() {
		int size = labelCount();
		if (size == 0) {
			return null;
		}

		String[] ret = new String[size];
		for (int i = 0; i < size; i++) {
			ret[i] = labels.get(i);
		}
		return ret;
	}

	/**
	 * @return label
	 */
	public String getLabel() {
		return getLabel(0);
	}

	/**
	 * @param label
	 *            sets given label
	 */
	public void setLabel(String label) {
		initLabels();
		labels.clear();
		labels.add(label);
	}

	/**
	 * @param str
	 *            sets all labels
	 */
	public void setLabels(String[] str) {
		initLabels();
		labels.clear();
		if (str == null)
			return;
		for (int i = 0; i < str.length; i++) {
			labels.add(str[i]);
		}
	}

	public boolean isVariable() {
		return false;
	}

	final public boolean isInTree() {
		return inTree;
	}

	final public void setInTree(boolean flag) {
		inTree = flag;
	}

	final public boolean isGeoElement() {
		return false;
	}

	/**
	 * @return true if this is command and it is on top level
	 */
	public boolean isTopLevelCommand() {
		return false;
	}

	/**
	 * @return top level command of this expression
	 */
	public Command getTopLevelCommand() {
		return null;
	}

	/**
	 * @return label
	 */
	public String getLabelForAssignment() {
		return getLabel();
	}

	/**
	 * Includes the label and assignment operator. E.g. while toString() would
	 * return x^2, this method would return f(x) := x^2
	 * 
	 * @param tpl
	 *            String template
	 * @return assignment in the form L:=R
	 */
	public String toAssignmentString(StringTemplate tpl) {
		if (labels == null) {
			return toString(tpl);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(getLabelForAssignment());

		switch (assignmentType) {
		case DEFAULT:
			sb.append(getAssignmentOperator());
			break;
		case DELAYED:
			sb.append(getDelayedAssignmentOperator());
			break;
		}

		sb.append(toString(tpl));
		return sb.toString();
	}

	/**
	 * 
	 * @param tpl
	 *            string template
	 * @param delayedAssignment
	 *            specifies if the assignment is a direct assignment (false) or
	 *            a delayed assignment (true)
	 * @return assignment in LaTeX
	 */
	public final String toAssignmentLaTeXString(StringTemplate tpl) {
		if (labels == null) {
			return toLaTeXString(true, tpl);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(getLabelForAssignment());
		
		switch (assignmentType) {
		case DEFAULT:
			sb.append(getAssignmentOperatorLaTeX());
			break;
		case DELAYED:
			sb.append(getDelayedAssignmentOperatorLaTeX());
			break;
		}
		
		sb.append(toLaTeXString(true, tpl));
		return sb.toString();
	}

	/**
	 * @return operator for non-delayed assignment
	 */
	public String getAssignmentOperator() {
		return ":=";
	}

	/**
	 * @return operator for delayed assignment
	 */
	public String getDelayedAssignmentOperator() {
		return "::=";
	}

	/**
	 * @return operator for non-delayed assignment in LaTeX form
	 */
	public String getAssignmentOperatorLaTeX() {
		return " \\, :=  \\, ";
	}

	/**
	 * @return operator for delayed assignment in LaTeX form
	 */
	public String getDelayedAssignmentOperatorLaTeX() {
		return " \\, ::= \\, ";
	}

	/**
	 * @param cmds
	 *            commands
	 */
	public final void addCommands(Set<Command> cmds) {
		// do nothing, see Command, ExpressionNode classes
	}

	/**
	 * @return whether KeepInput command is part of this expression
	 */
	public boolean isKeepInputUsed() {
		return keepInputUsed;
	}

	/**
	 * @param keepInputUsed
	 *            true if KeepInput command is part of this expression
	 */
	public void setKeepInputUsed(boolean keepInputUsed) {
		this.keepInputUsed = keepInputUsed;
	}

	public ExpressionValue evaluate(StringTemplate tpl) {
		return this;
	}

	/**
	 * Evaluates to number (if not numeric, returns undefined MyDouble)
	 * 
	 * @return number or undefined double
	 */
	public NumberValue evaluateNum() {
		ExpressionValue ev = evaluate(StringTemplate.defaultTemplate);
		if (ev instanceof NumberValue)
			return (NumberValue) ev;
		return new MyDouble(getKernel(), Double.NaN);
	}

	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public final String toString() {
		return toString(StringTemplate.defaultTemplate);
	}

	public abstract String toString(StringTemplate tpl);

	public abstract String toValueString(StringTemplate tpl);

	public final ExpressionValue traverse(final Traversing t) {
		return t.process(this);
	}

	/**
	 * @param s
	 *            expression
	 * @return string for debugging (revealing structure)
	 */
	public static String debugString(ExpressionValue s) {
		if (s == null)
			return "<null>";
		if (s instanceof ExpressionNode)
			return "ExNode(" + debugString(((ExpressionNode) s).getLeft())
					+ "," + ((ExpressionNode) s).getOperation() + ","
					+ debugString(((ExpressionNode) s).getRight()) + ")";
		return s.getClass().getName()
				.replaceAll("geogebra.common.kernel.arithmetic.", "")
				+ "(" + s.toString(StringTemplate.defaultTemplate) + ")";
	}

	public ExpressionValue unwrap() {
		return this;
	}

	public ExpressionNode wrap() {
		return new ExpressionNode(getKernel(), this);
	}

}