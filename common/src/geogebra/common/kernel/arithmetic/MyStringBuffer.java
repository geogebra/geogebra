/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.AbstractKernel;

import java.util.HashSet;

/**
 * Helper class to evaluate expressions with GeoText objects in it.
 * 
 * @see ExpressionNode#evaluate()
 * @author Markus Hohenwarter
 */
public class MyStringBuffer extends ValidExpression implements TextValue {

	private StringBuilder sb;
	private AbstractKernel kernel;

	/**
	 * @param kernel Kernel
	 * @param str text
	 */
	public MyStringBuffer(AbstractKernel kernel, String str) {
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

	@Override
	public String toString() {
		StringBuilder temp = new StringBuilder();
		temp.append("\"");
		temp.append(sb);
		temp.append("\"");
		return temp.toString();
	}

	public void resolveVariables() {//MyStringBuffer naver has variables
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

	public ExpressionValue deepCopy(AbstractKernel kernelForCopy) {
		return getText();
	}

	public ExpressionValue evaluate() {
		return this;
	}

	public HashSet getVariables() {
		return null;
	}

	final public String toValueString() {
		return sb.toString();
	}

	final public String toOutputValueString() {
		StringBuffer sb2 = new StringBuffer(sb.length() + 2);
		sb2.append('"');
		sb2.append(sb);
		sb2.append('"');
		return sb2.toString();
	}

	final public String toLaTeXString(boolean symbolic) {
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

	public AbstractKernel getKernel() {
		return kernel;
	}
}
