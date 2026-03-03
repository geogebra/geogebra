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
	boolean isConstant();

	/**
	 * @return whether this is leaf if it occurs in ExpressionNode
	 */
	boolean isLeaf();

	/**
	 * @return whether this is instance of NumberValue
	 */
	boolean isNumberValue();

	/**
	 * @return whether this is instance of VectorValue
	 */
	boolean evaluatesToNonComplex2DVector();

	/**
	 * @return whether this is a 2D/3D Vector (but not Point/Complex)
	 */
	boolean evaluatesToVectorNotPoint();

	/**
	 * @return whether this is instance of Vector3DValue
	 */
	boolean evaluatesTo3DVector();

	/**
	 * @return whether this is instance of ListValue
	 */
	boolean evaluatesToList();

	/**
	 * @return whether this evaluates to a matrix
	 */
	int getListDepth();

	/**
	 * @return whether this is instance of TextValue
	 */
	boolean evaluatesToText();

	/**
	 * @return whether this is instance of ExpressionNode
	 */
	boolean isExpressionNode();

	/**
	 * @return whether this is instance of GeoElement
	 */
	boolean isGeoElement();

	/**
	 * @return whether this is instance of Variable
	 */
	boolean isVariable();

	/**
	 * @return whether this is part of some expression node tree
	 */
	boolean isInTree();

	/**
	 * @param flag
	 *            whether this is part of some expression node tree
	 */
	void setInTree(boolean flag);

	/**
	 * @param ev
	 *            expression value
	 * @return whether given value is contained in tree / list of this
	 */
	boolean contains(ExpressionValue ev);

	/**
	 * @param kernel
	 *            kernel
	 * @return deep copy (duplicates all ExpressionValues used for definition of
	 *         this)
	 */
	ExpressionValue deepCopy(Kernel kernel);

	/**
	 * @return evaluated value
	 */
	double evaluateDouble();

	/**
	 * @param tpl
	 *            string template (in case concatenation of strings is involved)
	 * @return evaluated value
	 */
	ExpressionValue evaluate(StringTemplate tpl);

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
	String toString();

	/**
	 * Note: this is needed for texts that need to be quoted in lists and as
	 * command arguments.
	 *
	 * @param tpl
	 *            string template
	 * @return value string that can be re-run as GGB command
	 */
	String toOutputValueString(StringTemplate tpl);

	/**
	 * @param symbolic
	 *            true to keep variable names
	 * @param tpl
	 *            string template
	 * @return LaTeX string
	 */
	String toLaTeXString(boolean symbolic, StringTemplate tpl);

	/**
	 * Resolve variables
	 *
	 * @param info
	 *            evaluation flags
	 */
	void resolveVariables(EvalInfo info);

	/**
	 * @param tpl
	 *            string template
	 * @return string representation of this object
	 */
	String toString(StringTemplate tpl);

	/**
	 * @param tpl
	 *            string template
	 * @return string representation of value of this object
	 */
	String toValueString(StringTemplate tpl);

	/**
	 * Lets the traversing object go through the structure of this
	 * ExpressionValue and return changed value. This method may change content
	 * of this value, so you might need to use copy first.
	 *
	 * @param t
	 *            traversing object
	 * @return changed value
	 */
	ExpressionValue traverse(Traversing t);

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
	ExpressionValue unwrap();

	/**
	 * Wraps this value in ExpressionNode if it's not already one.
	 *
	 * @return wrapped value
	 */
	ExpressionNode wrap();

	/**
	 *
	 * @return whether x(this) makes sense
	 */
	boolean hasCoords();

	/**
	 * @param fv
	 *            variable with respect to which the derivative is computed
	 * @param kernel
	 *            kernel
	 * @return derivative
	 */
	ExpressionValue derivative(FunctionVariable fv, Kernel kernel);

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
	String getAssignmentOperator();

	/**
	 * @return type of this value after evaluation
	 */
	ExpressionValueType getValueType();

	/**
	 * @param kernel
	 *            kernel
	 * @return undefined object of the same value type
	 */
	ExpressionValue getUndefinedCopy(Kernel kernel);

	/**
	 * @return converts to valid expression, GeoText -&gt; MyTextBuffer,
	 *         GeoNumeric -&gt; MyDouble etc.
	 *
	 */
	ExpressionValue toValidExpression();

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

	/**
	 * Angle dimension describes the power of angle unit used to define this value,
	 * it may be {@code null} if mixed powers are used.
	 * <ul>
	 * <li> 1 deg -> dimension 1</li>
	 * <li> (1 deg) * (5 deg) -> dimension 2</li>
	 * <li> 1/(1 deg) -> dimension -1</li>
	 * <li> 1deg + 7 -> dimension null</li>
	 * <li> 1deg/(2deg) -> dimension 0</li>
	 * <li> sin(1deg) -> dimension 0</li>
	 * </ul>
	 * @implNote for {@link GeoElement} this method returns 0 for all numbers that are not angles.
	 * @return angle dimension
	 */
	default Integer getAngleDimension() {
		return null;
	}
}
