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
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.MyParseError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;

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
		Log.debug(kernel.getClass());
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
		if (name.endsWith("'")) {

			ExpressionValue ret = asDerivative(kernel, name);
			if (ret != null) {
				return ret;
			}

		}
		int[] exponents = new int[] { 0, 0, 0, 0 };
		int i;
		ExpressionValue geo2 = null;
		String nameNoX = name;
		int degPower = 0;
		while (nameNoX.length() > 0 && !(geo2 instanceof GeoElement)
				&& nameNoX.endsWith("deg")) {
			int length = nameNoX.length();
			degPower++;
			nameNoX = nameNoX.substring(0, length - 3);
			if (length > 3) {
				geo2 = kernel.lookupLabel(nameNoX);
			}

		}
		for (i = nameNoX.length() - 1; i >= 0; i--) {
			char c = name.charAt(i);
			if ((c < 'x' || c > 'z') && c != Unicode.theta)
				break;
			exponents[c == Unicode.theta ? 3 : c - 'x']++;
			nameNoX = name.substring(0, i);
			geo2 = kernel.lookupLabel(nameNoX);
			Operation op = kernel.getApplication().getParserFunctions()
					.get(nameNoX, 1);
			if (op != null && op != Operation.XCOORD && op != Operation.YCOORD
					&& op != Operation.ZCOORD) {
				return xyzPowers(kernel, exponents).apply(op);
			}

			if (geo2 != null)
				break;
		}
		int piPower = 0;
		while (nameNoX.length() > 0
				&& !(geo2 instanceof GeoElement)
				&& (nameNoX.startsWith("pi") || nameNoX.charAt(0) == Unicode.pi)) {
			int chop = nameNoX.charAt(0) == Unicode.pi ? 1 : 2;
			piPower++;
			nameNoX = nameNoX.substring(chop);
			if (i + 1 >= chop) {
				geo2 = kernel.lookupLabel(nameNoX);
			}
			if (geo2 != null) {
				break;
			}
		}
		if (nameNoX.length() > 0 && !(geo2 instanceof GeoElement)) {
			return new Variable(kernel, nameNoX);
		}
		ExpressionNode powers = xyzPowers(kernel, exponents);
		if (geo2 == null) {
			return piPower == 0 && degPower == 0 ? powers : powers
					.multiply(piDegTo(piPower, degPower, kernel));
		}
		return piPower == 0 && degPower == 0 ? powers.multiply(geo2) : powers
				.multiply(geo2)
.multiply(piDegTo(piPower, degPower, kernel));
	}

	private static ExpressionValue asDerivative(Kernel kernel, String name) {
		GeoElement fn = kernel
				.lookupLabel(name.substring(0, name.length() - 1));
		if (fn instanceof GeoFunction) {
			return new ExpressionNode(kernel,
					new ExpressionNode(kernel, fn, Operation.DERIVATIVE,
							new MyDouble(kernel, 1)),
					Operation.FUNCTION, new FunctionVariable(kernel));
		}
		return null;
	}

	private static ExpressionNode xyzPowers(Kernel kernel, int[] exponents) {
		return new ExpressionNode(kernel,
				new FunctionVariable(kernel, "x"))
				.power(new MyDouble(kernel, exponents[0]))
				.multiplyR(
						new ExpressionNode(kernel, new FunctionVariable(kernel,
								"y")).power(new MyDouble(kernel, exponents[1])))
				.multiplyR(
						new ExpressionNode(kernel, new FunctionVariable(kernel,
								"z")).power(new MyDouble(kernel, exponents[2])))
				.multiplyR(
						new ExpressionNode(kernel, new FunctionVariable(kernel,
								Unicode.thetaStr)).power(new MyDouble(kernel,
								exponents[3])));
	}

	private static ExpressionNode piDegTo(int piPower, int degPower,
			Kernel kernel2) {
		ExpressionNode piExp = piPower > 0 ? new MySpecialDouble(kernel2, Math.PI, Unicode.PI_STRING).wrap()
		.power(piPower) : null;
		ExpressionNode degExp = degPower > 0 ?
 new MyDouble(kernel2, MyMath.DEG)
				.setAngle()
.wrap().power(degPower) : null;
		return degExp == null ? piExp : (piExp == null ? degExp : piExp
				.multiply(degExp));
	}

	public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> ret = new HashSet<GeoElement>();
		ret.add(resolve(true));
		return ret;
	}

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
