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
 * Created on 03. Oktober 2001, 10:09
 */

package org.geogebra.common.kernel.arithmetic;

import java.util.HashSet;

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

public interface ExpressionValue {
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
	 * @return set of GeoElement variables
	 */
	public HashSet<GeoElement> getVariables(SymbolicMode mode);

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
	 * @return kernel
	 */
	// public Kernel getKernel();
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
	 * Similar to traverse, but only gives a boolean answer, the structure is
	 * not changed
	 * 
	 * @param t
	 *            inspecting object
	 * @return true if inspecting object returned true for at least one of the
	 *         sub-objects
	 */
	public boolean inspect(Inspecting t);

	/**
	 * If this is an expression node wrapping some other ExpressionValue, retur
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
	public boolean evaluatesToNDVector();

	boolean isOperation(Operation operation);
}
