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

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoDummyVariable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyParseError;
import geogebra.common.plugin.Operation;
import geogebra.common.util.StringUtil;

import java.util.HashSet;

/**
 * 
 * @author Markus Hohenwarter
 * 
 */
public class Variable extends ValidExpression {

	private String name;
	private Kernel kernel;

	/** Creates new VarString
	 * @param kernel kernel
	 * @param name variable name 
	 **/
	public Variable(Kernel kernel, String name) {
		this.name = name;
		this.kernel = kernel;
	}

	public ExpressionValue deepCopy(Kernel kernel1) {
		return new Variable(kernel1, name);
	}

	/**
	 * @param tpl string template
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
		return resolve(!kernel.isResolveUnkownVarsAsDummyGeos(),throwError);
	}

	/**
	 * Looks up the name of this variable in the kernel and returns the
	 * according GeoElement object. 
	 * 
	 * @param allowAutoCreateGeoElement true to allow creating new objects
	 * @param throwError when true, error is thrown when geo not found. Otherwise null is returned in such case.
	 * @return GeoElement with same label
	 */
	protected GeoElement resolve(boolean allowAutoCreateGeoElement,boolean throwError) {
		// keep bound CAS variables when resolving a CAS expression
		if (kernel.isResolveUnkownVarsAsDummyGeos()) {
			// resolve unknown variable as dummy geo to keep its name and
			// avoid an "unknown variable" error message
			return new GeoDummyVariable(kernel.getConstruction(), name);
		}

		// lookup variable name, create missing variables automatically if
		// allowed
		GeoElement geo = kernel.lookupLabel(name,
				allowAutoCreateGeoElement);
		if (geo != null || !throwError)
			return geo;

		// if we get here we couldn't resolve this variable name as a GeoElement
		String[] str = { "UndefinedVariable", name };
		throw new MyParseError(kernel.getApplication(), str);
	}

	
	/**
	 * Looks up the name of this variable in the kernel and returns the
	 * according GeoElement object. For absolute spreadsheet reference names
	 * like A$1 or $A$1 a special ExpressionNode wrapper object is returned that
	 * preserves this special name for displaying of the expression.
	 * @param forEquation true to resolve xx as polynomial rather than product of function variables
	 * 
	 * @return GeoElement whose label is name of this variable or ExpressionNode
	 * wrapping spreadsheet reference 
	 */
	final public ExpressionValue resolveAsExpressionValue(boolean forEquation) {
		GeoElement geo = resolve(false);
		if(geo==null){
		
			
			//holds powers of x,y,z: eg {"xxx","y","zzzzz"}
			int[] exponents = new int[]{0,0,0};
			int i;
			ExpressionValue geo2 = null;
			for(i=name.length()-1;i>=0;i--){
				if(name.charAt(i)<'x' || name.charAt(i)>'z')
					break;
				exponents[name.charAt(i)-'x']++;
				
				
					geo2 =kernel.lookupLabel(name.substring(0,i));
				
				if(geo2!=null)
					break;
			}
			if(i>-1 && !(geo2 instanceof GeoElement))
				resolve(true);
			if(geo2==null)
				geo2 = new MyDouble(kernel,1.0);
			//AbstractApplication.printStacktrace(name+":"+forEquation);
			if(forEquation)
				return new Polynomial(kernel,new Term(geo2, StringUtil.repeat('x',exponents[0])+StringUtil.repeat('y',exponents[1])+StringUtil.repeat('z',exponents[2])));
			return new ExpressionNode(kernel,geo2,Operation.MULTIPLY,new ExpressionNode(kernel,new FunctionVariable(kernel,"x")).power(new MyDouble(kernel,exponents[0])).
					multiply(new ExpressionNode(kernel,new FunctionVariable(kernel,"y")).power(new MyDouble(kernel,exponents[1]))).
					multiply(new ExpressionNode(kernel,new FunctionVariable(kernel,"z")).power(new MyDouble(kernel,exponents[2]))));
		}
		
		// spreadsheet dollar sign reference
		if (name.indexOf('$') > -1 && !(geo instanceof GeoCasCell)) {
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

	public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> ret = new HashSet<GeoElement>();
		ret.add(resolve(true));
		return ret;
	}

	public void resolveVariables(boolean forEquation) {
		// this has to be handled in ExpressionNode
	}

	@Override
	public String toString(StringTemplate tpl) {
		return kernel.printVariableName(name,tpl);
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	final public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		return toString(tpl);
	}

	public boolean isNumberValue() {
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isBooleanValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}

	public boolean isTextValue() {
		return false;
	}

	final public boolean isExpressionNode() {
		return false;
	}

	@Override
	final public boolean isVariable() {
		return true;
	}

	public boolean isListValue() {
		return false;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	public boolean isVector3DValue() {
		return false;
	}

	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	public Kernel getKernel() {
		return kernel;
	}

}
