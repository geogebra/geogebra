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
package org.geogebra.common.kernel.arithmetic;

import java.util.Set;
import java.util.Vector;

import org.geogebra.common.kernel.GTemplate;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MacroConstruction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

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
	

	/**
	 * @param label
	 *            label to be added
	 */
	public void addLabel(String label) {
		initLabels();
		// App.printStacktrace(label+":"+(label==null));
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
	 * @param assignmentType
	 *            assignment type
	 * @return assignment in the form L:=R
	 */
	public String toAssignmentString(StringTemplate tpl, AssignmentType assignmentType) {
		if (labels == null) {
			return toString(tpl);
		}

		StringBuilder sb = new StringBuilder();
		// make sure we do not prepend null when
		// assignment type is none.
		switch (assignmentType) {
		case DEFAULT:
			sb.append(getLabelForAssignment());
			sb.append(unwrap().getAssignmentOperator());
			break;
		case DELAYED:
			sb.append(getLabelForAssignment());
			sb.append(getDelayedAssignmentOperator());
			break;
		case NONE:
			break;
		}

		sb.append(toString(tpl));
		return sb.toString();
	}

	/**
	 * 
	 * @param tpl
	 *            string template
	 * @param assignmentType
	 *            assignment type
	 * @return assignment in LaTeX
	 */
	public final String toAssignmentLaTeXString(StringTemplate tpl, AssignmentType assignmentType) {
		if (labels == null) {
			return toLaTeXString(true, tpl);
		}

		StringBuilder sb = new StringBuilder();
		switch (assignmentType) {
		case DEFAULT:
			sb.append(tpl.printVariableName(getLabelForAssignment()));
			sb.append(getAssignmentOperatorLaTeX());
			break;
		case DELAYED:
			sb.append(tpl.printVariableName(getLabelForAssignment()));
			sb.append(getDelayedAssignmentOperatorLaTeX());
			break;
		case NONE:
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

	

	public ExpressionValue evaluate(StringTemplate tpl) {
		return this;
	}

	/**
	 * Evaluates to number (if not numeric, returns undefined MyDouble)
	 * 
	 * @return number or undefined double
	 */
	public double evaluateDouble() {
		ExpressionValue ev = evaluate(StringTemplate.defaultTemplate);
		if (ev instanceof NumberValue)
			return ((NumberValue) ev).getDouble();
		return Double.NaN;
	}

	/**
	 * Evaluates like function, a complex expression
	 * 
	 * @return function
	 */
	public Function evaluateComplex() {
		ExpressionValue ev = evaluate(StringTemplate.defaultTemplate);
		return (Function) ev;
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public final String toString() {
		return toString(StringTemplate.defaultTemplate);
	}

	public abstract String toString(StringTemplate tpl);

	public abstract String toValueString(StringTemplate tpl);

	public ExpressionValue traverse(final Traversing t) {
		return t.process(this);
	}

	public boolean inspect(Inspecting t) {
		return t.check(this);
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
		if (s instanceof Equation)
			return "Eq(" + debugString(((Equation) s).getLHS()) + ",=,"
					+ debugString(((Equation) s).getRHS()) + ")";
		if (s instanceof MyList) {
			StringBuilder sb = new StringBuilder("MyList(");
			for (int i = 0; i < ((MyList) s).size(); i++) {
				if (i > 0)
					sb.append(",");
				sb.append(debugString(((MyList) s).getListElement(i)));
			}
			sb.append(')');
			return sb.toString();
		}
		if (s instanceof Command) {
			StringBuilder sb = new StringBuilder("Cmd:");
			sb.append(((Command) s).getName());
			sb.append("(");
			for (int i = 0; i < ((Command) s).getArgumentNumber(); i++) {
				if (i > 0)
					sb.append(",");
				sb.append(debugString(((Command) s).getArgument(i).unwrap()));
			}
			sb.append(')');
			return sb.toString();
		}
		if (s.isGeoElement()) {
			return (((GeoElement) s).getConstruction() instanceof MacroConstruction ? "Macro"
					: "")
					+ s.getClass()
							.getName()
							.replaceAll("org.geogebra.common.kernel.geos.Geo",
									"G")
					+ "("
					+ s.toString(StringTemplate.defaultTemplate) + ")";
		}
		return s.getClass().getName()
				.replaceAll("org.geogebra.common.kernel.arithmetic.", "")
				+ "(" + s.toString(StringTemplate.defaultTemplate) + ")";
	}

	public ExpressionValue unwrap() {
		return this;
	}

	public abstract ExpressionNode wrap();

	public boolean hasCoords() {
		return false;
	}

	public ExpressionValue derivative(FunctionVariable fv, Kernel kernel) {
		App.debug("derivative from " + this.getClass());
		return null;
	}

	public ExpressionValue integral(FunctionVariable fv, Kernel kernel) {
		App.debug("integral from " + this.getClass());
		return null;
	}

	public boolean isExpressionNode() {
		return false;
	}



	/**
	 * print expression as value or geo label
	 * 
	 * @param x2
	 *            expression
	 * @param values
	 *            value or label
	 * @param tpl
	 *            template
	 * @return value or geo
	 */
	protected static String print(ExpressionValue x2, boolean values,
			StringTemplate tpl) {
		if (values) {
			Log.debug(x2);
			Log.debug(x2.toValueString(tpl));
			return x2.toValueString(tpl);
		}
		return x2.isGeoElement() ? ((GeoElement) x2).getLabel(tpl) : x2
				.toString(tpl);
	}

	/**
	 * @return deep check for function variable
	 */
	public final boolean containsFunctionVariable() {
		return this.inspect(new Inspecting() {

			@Override
			public boolean check(ExpressionValue v) {
				return v instanceof FunctionVariable;
			}
		});
	}
	
	/**
	 * @param name
	 *            variable name
	 * @return deep check for function variable with given name
	 */
	public final boolean containsFunctionVariable(final String name) {
		return this.inspect(new Inspecting() {

			@Override
			public boolean check(ExpressionValue v) {
				return v instanceof FunctionVariable
						&& (name == null || name.equals(((FunctionVariable) v)
								.getSetVarString()));
			}
		});
	}

	/**
	 * Here we just check for number values, overridden in ExpressionNode
	 */
	public final boolean evaluatesToNumber(boolean def) {
		return getValueType() == ValueType.NUMBER
				|| getValueType() == ValueType.BOOLEAN
				|| (def && getValueType() == ValueType.UNKNOWN);
	}

	/**
	 * Unlike contains does not stop on lists and equations
	 * 
	 * @param needle
	 *            subexpression
	 * @return whether expression is included
	 */
	public boolean containsDeep(final ExpressionValue needle) {
		return inspect(new Inspecting() {

			public boolean check(ExpressionValue v) {
				return v == needle;
			}
		});
	}

	/**
	 * @param tpl
	 *            template
	 * @return string representation of this node
	 */
	public final String toString(GTemplate tpl) {
		return toString(tpl.getTemplate());
	}

	public final boolean evaluatesToNonComplex2DVector() {
		return getValueType() == ValueType.NONCOMPLEX2D;
	}

	public boolean evaluatesToVectorNotPoint() {
		return false;
	}

	public final boolean evaluatesTo3DVector() {
		return getValueType() == ValueType.VECTOR3D;
	}

	public final boolean evaluatesToList() {
		return getValueType() == ValueType.LIST;
	}

	public int getListDepth() {
		return 0;
	}

	public boolean evaluatesToText() {
		return getValueType() == ValueType.TEXT;
	}

	public abstract ValueType getValueType();
}