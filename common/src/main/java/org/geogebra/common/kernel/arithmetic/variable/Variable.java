/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * VarString.java
 *
 * Created on 18. November 2001, 14:49
 */

package org.geogebra.common.kernel.arithmetic.variable;

import java.util.HashSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.MyParseError;
import org.geogebra.common.plugin.Operation;

/**
 * 
 * @author Markus Hohenwarter
 * 
 */
public class Variable extends ValidExpression {

	private String name;
	private Kernel kernel;
	private VariableReplacerAlgorithm variableReplacerAlgorithm;

	/**
	 * Creates new VarString
	 * 
	 * @param kernel
	 *            kernel
	 * @param name
	 *            variable name
	 **/
	public Variable(Kernel kernel, String name) {
		this.name = name;
		this.kernel = kernel;
		variableReplacerAlgorithm = new VariableReplacerAlgorithm(kernel);
	}

	@Override
	public Variable deepCopy(Kernel kernel1) {
		return new Variable(kernel1, name);
	}

	/**
	 * @param tpl
	 *            string template
	 * @return variable name
	 */
	public String getName(StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	/**
	 * Looks up the name of this variable in the kernel and returns the
	 * according GeoElement object.
	 */
	private GeoElement resolve(boolean throwError, SymbolicMode mode) {
		return resolve(
				mode == SymbolicMode.NONE, throwError, mode);
	}

	/**
	 * Looks up the name of this variable in the kernel and returns the
	 * according GeoElement object.
	 * 
	 * @param allowAutoCreateGeoElement
	 *            true to allow creating new objects
	 * @param throwError
	 *            when true, error is thrown when geo not found. Otherwise null
	 *            is returned in such case.
	 * @param mode
	 *            symbolic mode
	 * @return GeoElement with same label
	 */
	public GeoElement resolve(boolean allowAutoCreateGeoElement, boolean throwError,
							  SymbolicMode mode) {
		switch (mode) {
			case SYMBOLIC:
				return newGeoDummyVariable();
			case SYMBOLIC_AV:
				return lookupLabel(allowAutoCreateGeoElement, mode);
			case NONE:
				GeoElement resolvedElement = lookupLabel(allowAutoCreateGeoElement, mode);
				if (resolvedElement != null || !throwError) {
					return resolvedElement;
				}
		}

		Localization localization = kernel.getApplication().getLocalization();
		throw new MyParseError(localization, Errors.UndefinedVariable, name);
	}

	private GeoElement newGeoDummyVariable() {
		return new GeoDummyVariable(kernel.getConstruction(), name);
	}

	private GeoElement lookupLabel(boolean allowAutoCreateGeoElement, SymbolicMode symbolicMode) {
		return kernel.lookupLabel(name, allowAutoCreateGeoElement, symbolicMode);
	}

	/**
	 * Looks up the name of this variable in the kernel and returns the
	 * according GeoElement object. For absolute spreadsheet reference names
	 * like A$1 or $A$1 a special ExpressionNode wrapper object is returned that
	 * preserves this special name for displaying of the expression.
	 * 
	 * @param mode
	 *            symbolic mode
	 * 
	 * @return GeoElement whose label is name of this variable or ExpressionNode
	 *         wrapping spreadsheet reference
	 */
	final public ExpressionValue resolveAsExpressionValue(SymbolicMode mode) {
		GeoElement geo = resolve(false, mode);
		if (geo == null) {
			if (kernel.getConstruction().isRegistredFunctionVariable(name)) {
				return new FunctionVariable(kernel, name);
			}
			ExpressionValue replacement = replacement(name);
			if (isReplacementValid(replacement)) {
				return replacement;
			}
			if (mode == SymbolicMode.SYMBOLIC_AV) {
				return new GeoDummyVariable(kernel.getConstruction(), name);
			}
			return resolve(true, mode);
		}

		// spreadsheet dollar sign reference
		// need to avoid CAS cell references, eg $1 (see #3206)
		if (name.indexOf('$') > -1 && !(geo instanceof GeoCasCell)
				&& !(geo instanceof GeoDummyVariable)) {
			// row and/or column dollar sign present?
			boolean colDollar = name.indexOf('$') == 0;
			boolean rowDollar = name.length() > 2 && name.indexOf('$', 1) > -1;
			Operation operation;
			if (rowDollar && colDollar) {
				operation = Operation.DOLLAR_VAR_ROW_COL;
			} else if (rowDollar) {
				operation = Operation.DOLLAR_VAR_ROW;
			} else {
				// if (col$)
				operation = Operation.DOLLAR_VAR_COL;
			}

			// build an expression node that wraps the resolved geo
			return new ExpressionNode(kernel, geo, operation, null);
		}

		// standard case: no dollar sign
		return geo;
	}

	private boolean isReplacementValid(ExpressionValue replacement) {
		return !(replacement instanceof Variable);
	}

	/**
	 * @param name
	 *            variable name
	 * @return interpretation, eg axxx -> a*x*x
	 */
	public ExpressionValue replacement(String name) {
		return variableReplacerAlgorithm.replace(name);
	}

	@Override
	public HashSet<GeoElement> getVariables(SymbolicMode mode) {
		HashSet<GeoElement> ret = new HashSet<>();
		ret.add(resolve(true, mode));
		return ret;
	}

	@Override
	public void resolveVariables(EvalInfo info) {
		// this has to be handled in ExpressionNode
	}

	@Override
	public String toString(StringTemplate tpl) {
		return tpl.printVariableName(name);
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	final public boolean isVariable() {
		return true;
	}

	@Override
	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	@Override
	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	/**
	 * @return kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	@Override
	public boolean hasCoords() {
		GeoElement ge = kernel.lookupLabel(name, false, SymbolicMode.NONE);
		if (ge != null && !(ge instanceof GeoDummyVariable)) {
			return ge.hasCoords();
		}

		return false;
	}

	/**
	 * force the name to s, used by RelativeCopy
	 * 
	 * @param s
	 *            new name
	 */
	public void setName(String s) {
		name = s;
	}

	/**
	 * @return variable name
	 */
	public String getName() {
		return name;
	}

	@Override
	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.UNKNOWN;
	}

}
