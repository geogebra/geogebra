/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;

import java.util.HashSet;

/**
 * Helper class to evaluate expressions with GeoText objects in it.
 * 
 * @see ExpressionNode#evaluate(StringTemplate)
 * @author Markus Hohenwarter
 */
public class MyStringBuffer extends ValidExpression implements TextValue {

	private StringBuilder sb;
	private Kernel kernel;

	/**
	 * @param kernel Kernel
	 * @param str text
	 */
	public MyStringBuffer(Kernel kernel, String str) {
		sb = new StringBuilder(str);
		this.kernel = kernel;
	}

	/**
	 * @param str text to append
	 */
	public void append(String str) {
		sb.append(str);
	}

	/**
	 * @param pos position to insert (same as in String#insert)
	 * @param str text to insert
	 */
	public void insert(int pos, String str) {
		sb.insert(pos, str);
	}

	@SuppressWarnings("cast")
	// see http://code.google.com/p/google-web-toolkit/issues/detail?id=4097
	@Override
	public String toString(StringTemplate tpl) {
		StringBuilder temp = new StringBuilder();
		temp.append("\"");
		temp.append((CharSequence)sb);
		temp.append("\"");
		return temp.toString();
	}

	public void resolveVariables(boolean forEquation) {
		//MyStringBuffer naver has variables
	}

	public boolean isConstant() {
		return true;
	}

	public boolean isLeaf() {
		return true;
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
		return true;
	}

	public ExpressionValue deepCopy(Kernel kernelForCopy) {
		return getText();
	}

	public HashSet<GeoElement> getVariables() {
		return null;
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		return sb.toString();
	}

	@SuppressWarnings("cast")
	// see http://code.google.com/p/google-web-toolkit/issues/detail?id=4097
	final public String toOutputValueString(StringTemplate tpl) {
		StringBuffer sb2 = new StringBuffer(sb.length() + 2);
		sb2.append('"');
		sb2.append((CharSequence)sb);
		sb2.append('"');
		return sb2.toString();
	}

	final public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		return sb.toString();
	}

	public MyStringBuffer getText() {
		return new MyStringBuffer(kernel, sb.toString());
	}

	final public boolean isExpressionNode() {
		return false;
	}

	public boolean isListValue() {
		return false;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

	public Kernel getKernel() {
		return kernel;
	}
}
