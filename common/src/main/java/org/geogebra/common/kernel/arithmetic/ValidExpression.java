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
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.HasDebugString;
import org.geogebra.common.util.debug.Log;

/**
 * Common class for objects obtained from the parser that are not yet processed
 * to GeoElements. They may also persist in ExpressionNodes of functions
 * 
 * @author Markus
 * 
 */
public abstract class ValidExpression
		implements ExpressionValue, HasDebugString {

	private Vector<String> labels;
	private boolean inTree; // used by ExpressionNode
	private boolean isRoot = false;

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
		if (labels == null) {
			labels = new Vector<>();
		}
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
		if (str == null) {
			return;
		}
		for (int i = 0; i < str.length; i++) {
			labels.add(str[i]);
		}
	}

	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	final public boolean isInTree() {
		return inTree;
	}

	@Override
	final public void setInTree(boolean flag) {
		inTree = flag;
	}

	@Override
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
	public String toAssignmentString(StringTemplate tpl,
			AssignmentType assignmentType) {
		return toAssignmentString(toString(tpl), assignmentType);
	}

	/**
	 * @param rhs
	 *            assignment RHS
	 * @param assignmentType
	 *            assignment type
	 * @return rhs with prepended label + assignment operator
	 */
	public String toAssignmentString(String rhs,
			AssignmentType assignmentType) {
		if (labels == null) {
			return rhs;
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

		sb.append(rhs);
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
	public final String toAssignmentLaTeXString(StringTemplate tpl,
			AssignmentType assignmentType) {
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
	@Override
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

	@Override
	public ExpressionValue evaluate(StringTemplate tpl) {
		return this;
	}

	/**
	 * Evaluates to number (if not numeric, returns undefined MyDouble)
	 * 
	 * @return number or undefined double
	 */
	@Override
	public double evaluateDouble() {
		ExpressionValue ev;
		try {
			ev = evaluate(StringTemplate.defaultTemplate);
		} catch (Error | Exception e) {
			return Double.NaN;
		}
		if (ev instanceof NumberValue) {
			return ((NumberValue) ev).getDouble();
		}
		return Double.NaN;
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public final String toString() {
		return toString(StringTemplate.defaultTemplate);
	}

	@Override
	public abstract String toString(StringTemplate tpl);

	@Override
	public abstract String toValueString(StringTemplate tpl);

	@Override
	public ExpressionValue traverse(final Traversing t) {
		return t.process(this);
	}

	@Override
	public boolean inspect(Inspecting t) {
		return t.check(this);
	}

	@Override
	public String getDebugString() {
		return debugString(this);
	}

	/**
	 * @param s
	 *            expression
	 * @return string for debugging (revealing structure)
	 */
	public static String debugString(ExpressionValue s) {
		if (s == null) {
			return "<null>";
		}
		if (s instanceof ExpressionNode) {
			return "ExNode(" + debugString(((ExpressionNode) s).getLeft()) + ","
					+ ((ExpressionNode) s).getOperation() + ","
					+ debugString(((ExpressionNode) s).getRight()) + ")";
		}
		if (s instanceof Equation) {
			return "Eq(" + debugString(((Equation) s).getLHS()) + ",=,"
					+ debugString(((Equation) s).getRHS()) + ")";
		}
		if (s instanceof MyList) {
			StringBuilder sb = new StringBuilder("MyList(");
			for (int i = 0; i < ((MyList) s).size(); i++) {
				if (i > 0) {
					sb.append(",");
				}
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
				if (i > 0) {
					sb.append(",");
				}
				sb.append(debugString(((Command) s).getArgument(i).unwrap()));
			}
			sb.append(')');
			return sb.toString();
		}
		if (s.isGeoElement()) {
			return (((GeoElement) s)
					.getConstruction() instanceof MacroConstruction ? "Macro"
							: "")
					+ s.getClass().getName()
							.replaceAll("org.geogebra.common.kernel.geos.Geo",
									"G")
							.replaceAll(
									"org.geogebra.common.geogebra3D.kernel3D.geos.Geo",
									"G")
					+ "(" + s.toString(StringTemplate.defaultTemplate) + ")";
		}
		return s.getClass().getName()
				.replaceAll("org.geogebra.common.kernel.arithmetic.", "") + "("
				+ s.toString(StringTemplate.defaultTemplate) + ")";
	}

	@Override
	public ExpressionValue unwrap() {
		return this;
	}

	@Override
	public abstract ExpressionNode wrap();

	@Override
	public boolean hasCoords() {
		return false;
	}

	@Override
	public ExpressionValue derivative(FunctionVariable fv, Kernel kernel) {
		Log.debug("derivative from " + this.getValueType());
		return new ExpressionNode(kernel, Double.NaN);
	}

	@Override
	public ExpressionValue integral(FunctionVariable fv, Kernel kernel) {
		Log.debug("integral from " + this.getValueType());
		return null;
	}

	@Override
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
			return x2.toValueString(tpl);
		}
		return x2.isGeoElement() ? ((GeoElement) x2).getLabel(tpl)
				: x2.toString(tpl);
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
				return v instanceof FunctionVariable && (name == null || name
						.equals(((FunctionVariable) v).getSetVarString()));
			}
		});
	}

	/**
	 * @param vars
	 *            list of acceptable variables
	 * @return whether some other variable is included
	 */
	public final boolean containsFunctionVariableOtherThan(
			final FunctionVariable[] vars) {
		return this.inspect(new Inspecting() {

			@Override
			public boolean check(ExpressionValue v) {
				return v instanceof FunctionVariable
						&& ExpressionNode.doesNotInclude(vars, v);
			}
		});
	}

	/**
	 * Here we just check for number values, overridden in ExpressionNode
	 */
	@Override
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

			@Override
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

	@Override
	public final boolean evaluatesToNonComplex2DVector() {
		return getValueType() == ValueType.NONCOMPLEX2D;
	}

	@Override
	public boolean evaluatesToVectorNotPoint() {
		return false;
	}

	@Override
	public final boolean evaluatesTo3DVector() {
		return getValueType() == ValueType.VECTOR3D;
	}

	@Override
	public final boolean evaluatesToList() {
		return getValueType() == ValueType.LIST;
	}

	@Override
	public int getListDepth() {
		return 0;
	}

	@Override
	public boolean evaluatesToText() {
		return getValueType() == ValueType.TEXT;
	}

	@Override
	public abstract ValueType getValueType();

	@Override
	public ExpressionValue getUndefinedCopy(Kernel kernel) {
		return new MyDouble(kernel, Double.NaN);
	}

	@Override
	public ExpressionValue toValidExpression() {
		return this;
	}

	@Override
	public boolean evaluatesToNDVector() {
		ValueType vt = getValueType();
		return vt == ValueType.NONCOMPLEX2D || vt == ValueType.VECTOR3D;
	}

	@Override
	public abstract ValidExpression deepCopy(Kernel kernel);

	/**
	 * @param string
	 *            command name
	 * @return whether top level is a command and name is equal to string
	 */
	public boolean isTopLevelCommand(String string) {
		return false;
	}

	public boolean isRootNode() {
		return isRoot;
	}

	public void setAsRootNode() {
		this.isRoot = true;
	}

	@Override
	public boolean isOperation(Operation operation) {
		return false;
	}
}