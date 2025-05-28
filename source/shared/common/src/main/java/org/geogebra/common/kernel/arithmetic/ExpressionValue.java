/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * NumberValue.java
 *
 * Created on 03. October 2001, 10:09
 */

package org.geogebra.common.kernel.arithmetic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.Operation;

/**
 *
 * @author Markus
 *
 */

public interface ExpressionValue extends Iterable<ExpressionValue> {
	/**
	 * @return true if this is does not depend on any labeled or dependent geos
	 */
	public boolean isConstant();

	/**
	 * @return whether this is leaf if it occurs in ExpressionNode
	 */
	public boolean isLeaf();

	/**
	 * @return whether this is instance of NumberValue
	 */
	public boolean isNumberValue();

	/**
	 * @return whether this is instance of VectorValue
	 */
	public boolean evaluatesToNonComplex2DVector();

	/**
	 * @return whether this is a 2D/3D Vector (but not Point/Complex)
	 */
	public boolean evaluatesToVectorNotPoint();

	/**
	 * @return whether this is instance of Vector3DValue
	 */
	public boolean evaluatesTo3DVector();

	/**
	 * @return whether this is instance of ListValue
	 */
	public boolean evaluatesToList();

	/**
	 * @return whether this evaluates to a matrix
	 */
	public int getListDepth();

	/**
	 * @return whether this is instance of TextValue
	 */
	public boolean evaluatesToText();

	/**
	 * @return whether this is instance of ExpressionNode
	 */
	public boolean isExpressionNode();

	/**
	 * @return whether this is instance of GeoElement
	 */
	public boolean isGeoElement();

	/**
	 * @return whether this is instance of Variable
	 */
	public boolean isVariable();

	/**
	 * @return whether this is part of some expression node tree
	 */
	public boolean isInTree();

	/**
	 * @param flag
	 *            whether this is part of some expression node tree
	 */
	public void setInTree(boolean flag);

	/**
	 * @param ev
	 *            expression value
	 * @return whether given value is contained in tree / list of this
	 */
	public boolean contains(ExpressionValue ev);

	/**
	 * @param kernel
	 *            kernel
	 * @return deep copy (duplicates all ExpressionValues used for definition of
	 *         this)
	 */
	public ExpressionValue deepCopy(Kernel kernel);

	/**
	 * @return evaluated value
	 */
	public double evaluateDouble();

	/**
	 * @param tpl
	 *            string template (in case concatenation of strings is involved)
	 * @return evaluated value
	 */
	public ExpressionValue evaluate(StringTemplate tpl);

	/**
	 * @param mode
	 *            symbolic mode
	 * @param variables set of GeoElement variables
	 */
	void getVariables(Set<GeoElement> variables, SymbolicMode mode);

	/**
	 * @param mode
	 *            symbolic mode
	 * @return set of GeoElement variables
	 */
	default Set<GeoElement> getVariables(SymbolicMode mode) {
		HashSet<GeoElement> vars = new HashSet<>();
		getVariables(vars, mode);
		return vars;
	}

	@Override
	@Deprecated
	public String toString();

	/**
	 * Note: this is needed for texts that need to be quoted in lists and as
	 * command arguments.
	 *
	 * @param tpl
	 *            string template
	 * @return value string that can be re-run as GGB command
	 */
	public String toOutputValueString(StringTemplate tpl);

	/**
	 * @param symbolic
	 *            true to keep variable names
	 * @param tpl
	 *            string template
	 * @return LaTeX string
	 */
	public String toLaTeXString(boolean symbolic, StringTemplate tpl);

	/**
	 * Resolve variables
	 *
	 * @param info
	 *            evaluation flags
	 */
	public void resolveVariables(EvalInfo info);

	/**
	 * @param tpl
	 *            string template
	 * @return string representation of this object
	 */
	public String toString(StringTemplate tpl);

	/**
	 * @param tpl
	 *            string template
	 * @return string representation of value of this object
	 */
	public String toValueString(StringTemplate tpl);

	/**
	 * Lets the traversing object go through the structure of this
	 * ExpressionValue and return changed value. This method may change content
	 * of this value, so you might need to use copy first.
	 *
	 * @param t
	 *            traversing object
	 * @return changed value
	 */
	public ExpressionValue traverse(Traversing t);

	/**
	 * Traverses the expression tree and returns true
	 * if any of the nodes evaluates to true
	 *
	 * @param inspecting predicate testing each node
	 * @return true if inspecting object returned true for at least one of the nodes
	 */
	default boolean any(Inspecting inspecting) {
		for (ExpressionValue value: this) {
			if (inspecting.check(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Traverses the expression tree and returns {@code true}
	 * if none of the nodes evaluates to {@code true}.
	 * <p> Identical to negating {@link ExpressionValue#any(Inspecting)}.</p>
	 *
	 * @param inspecting predicate testing each node
	 * @return {@code true} if inspecting object returned true for none of the nodes
	 */
	default boolean none(Inspecting inspecting) {
		return !any(inspecting);
	}

	/**
	 * Returns the number of direct children of this expression value.
	 * @return a positive number
	 */
	default int getChildCount() {
		return 0;
	}

	/**
	 * Returns the direct child of this expression value at the given index.
	 * @param index index of the child, must be less than {@link ExpressionValue#getChildCount()}
	 * and greater than 0.
	 * @return a child of this expression value at index.
	 * @throws IndexOutOfBoundsException when the preconditions of the parameter index are not met.
	 */
	default ExpressionValue getChild(int index) {
		throw new IndexOutOfBoundsException();
	}

	/**
	 * If this is an expression node wrapping some other ExpressionValue, return
	 * its content, otherwise return this.
	 *
	 * @return unwrapped content
	 */
	public ExpressionValue unwrap();

	/**
	 * Wraps this value in ExpressionNode if it's not already one.
	 *
	 * @return wrapped value
	 */
	public ExpressionNode wrap();

	/**
	 *
	 * @return whether x(this) makes sense
	 */
	public boolean hasCoords();

	/**
	 * @param fv
	 *            variable with respect to which the derivative is computed
	 * @param kernel
	 *            kernel
	 * @return derivative
	 */
	public ExpressionValue derivative(FunctionVariable fv, Kernel kernel);

	/**
	 * @param fv
	 *            variable with respect to which the integral is computed
	 * @param kernel
	 *            kernel
	 * @return integral
	 */
	ExpressionValue integral(FunctionVariable fv, Kernel kernel);

	/**
	 * @param def
	 *            what to return if we cannot determine the outcome
	 * @return whether this evaluates to number
	 */
	boolean evaluatesToNumber(boolean def);

	/**
	 * @return : for equations, := by default
	 */
	public String getAssignmentOperator();

	/**
	 * @return type of this value after evaluation
	 */
	public ValueType getValueType();

	/**
	 * @param kernel
	 *            kernel
	 * @return undefined object of the same value type
	 */
	public ExpressionValue getUndefinedCopy(Kernel kernel);

	/**
	 * @return converts to valid expression, GeoText -&gt; MyTextBuffer,
	 *         GeoNumeric -&gt; MyDouble etc.
	 *
	 */
	public ExpressionValue toValidExpression();

	/**
	 * @return whether this evaluates to 3D vector on non-complex 2D
	 */
	boolean evaluatesToNDVector();

	/**
	 *
	 * @param operation operation
	 * @return whether this is an expression node with given operation on top level
	 */
	boolean isOperation(Operation operation);

	/**
	 *
	 * @return whether this evaluates to RecurringDecimal
	 */
	boolean isRecurringDecimal();

	@Override
	default @Nonnull Iterator<ExpressionValue> iterator() {
		return new ExpressionValueTreeIterator(this);
	}
}
