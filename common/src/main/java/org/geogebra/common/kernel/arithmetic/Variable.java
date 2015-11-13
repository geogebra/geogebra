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

package org.geogebra.common.kernel.arithmetic;

import java.util.HashSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
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
	}

	public ExpressionValue deepCopy(Kernel kernel1) {
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

	public boolean isConstant() {
		return false;
	}

	public boolean isLeaf() {
		return true;
	}

	/**
	 * Looks up the name of this variable in the kernel and returns the
	 * according GeoElement object.
	 */
	private GeoElement resolve(boolean throwError) {
		return resolve(!kernel.isResolveUnkownVarsAsDummyGeos(), throwError);
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
	 * @return GeoElement with same label
	 */
	protected GeoElement resolve(boolean allowAutoCreateGeoElement,
			boolean throwError) {
		// keep bound CAS variables when resolving a CAS expression
		if (kernel.isResolveUnkownVarsAsDummyGeos()) {
			// resolve unknown variable as dummy geo to keep its name and
			// avoid an "unknown variable" error message
			return new GeoDummyVariable(kernel.getConstruction(), name);
		}

		// lookup variable name, create missing variables automatically if
		// allowed
		GeoElement geo = kernel.lookupLabel(name, allowAutoCreateGeoElement, kernel.isResolveUnkownVarsAsDummyGeos());
		if (geo != null || !throwError)
			return geo;

		// if we get here we couldn't resolve this variable name as a GeoElement
		String[] str = { "UndefinedVariable", name };
		String[] exists = kernel.getApplication().getGgbApi()
				.getAllObjectNames();
		for (String label : exists) {
			App.debug(label);
		}
		throw new MyParseError(kernel.getApplication().getLocalization(), str);
	}

	/**
	 * Looks up the name of this variable in the kernel and returns the
	 * according GeoElement object. For absolute spreadsheet reference names
	 * like A$1 or $A$1 a special ExpressionNode wrapper object is returned that
	 * preserves this special name for displaying of the expression.
	 * 
	 * @return GeoElement whose label is name of this variable or ExpressionNode
	 *         wrapping spreadsheet reference
	 */
	final public ExpressionValue resolveAsExpressionValue() {
		GeoElement geo = resolve(false);
		if (geo == null) {
			ExpressionValue ret = replacement(kernel, name);
			return ret instanceof Variable ? resolve(true) : ret;
		}

		// spreadsheet dollar sign reference
		// need to avoid CAS cell references, eg $1 (see #3206)
		if (name.indexOf('$') > -1 && !(geo instanceof GeoCasCell)
				&& !(geo instanceof GeoDummyVariable)) {
			// row and/or column dollar sign present?
			boolean col$ = name.indexOf('$') == 0;
			boolean row$ = name.length() > 2 && name.indexOf('$', 1) > -1;
			Operation operation = Operation.NO_OPERATION;
			if (row$ && col$)
				operation = Operation.$VAR_ROW_COL;
			else if (row$)
				operation = Operation.$VAR_ROW;
			else
				// if (col$)
				operation = Operation.$VAR_COL;

			// build an expression node that wraps the resolved geo
			return new ExpressionNode(kernel, geo, operation, null);
		}
		// standard case: no dollar sign
		return geo;
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param name
	 *            variable name
	 * @return interpretation, eg axxx -> a*x*x
	 */
	public static ExpressionValue replacement(Kernel kernel, String name) {
		// holds powers of x,y,z: eg {"xxx","y","zzzzz"}
		int[] exponents = new int[] { 0, 0, 0 };
		int i;
		ExpressionValue geo2 = null;
		for (i = name.length() - 1; i >= 0; i--) {
			if (name.charAt(i) < 'x' || name.charAt(i) > 'z')
				break;
			exponents[name.charAt(i) - 'x']++;
			String nameNoX = name.substring(0, i);
			geo2 = kernel.lookupLabel(nameNoX);
			Operation op = kernel.getApplication().getParserFunctions()
					.get(nameNoX, 1);
			if (op != null && op != Operation.XCOORD && op != Operation.YCOORD
					&& op != Operation.ZCOORD) {
				return new FunctionVariable(kernel, name.charAt(i) + "")
						.wrap()
						.power(new MyDouble(kernel,
								exponents[name.charAt(i) - 'x'])).apply(op);
			}

			if (geo2 != null)
				break;
		}
		if (i > -1 && !(geo2 instanceof GeoElement))
			return new Variable(kernel, name.substring(0, i + 1));
		ExpressionNode powers = new ExpressionNode(kernel,
				new FunctionVariable(kernel, "x"))
				.power(new MyDouble(kernel, exponents[0]))
				.multiplyR(
						new ExpressionNode(kernel, new FunctionVariable(kernel,
								"y")).power(new MyDouble(kernel, exponents[1])))
				.multiplyR(
						new ExpressionNode(kernel, new FunctionVariable(kernel,
								"z")).power(new MyDouble(kernel, exponents[2])));
		if (geo2 == null) {
			return powers;
		}
		return powers.multiply(geo2);
	}

	public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> ret = new HashSet<GeoElement>();
		ret.add(resolve(true));
		return ret;
	}

	public void resolveVariables() {
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

	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(tpl);
	}

	public boolean isNumberValue() {
		return false;
	}

	@Override
	final public boolean isVariable() {
		return true;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

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
		GeoElement ge = kernel.lookupLabel(name, false, true);
		if (ge != null && !(ge instanceof GeoDummyVariable))
			return ge.hasCoords();
		
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
