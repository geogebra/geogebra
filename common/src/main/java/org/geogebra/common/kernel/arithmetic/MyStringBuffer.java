/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.arithmetic;

import java.util.HashSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;

import com.google.j2objc.annotations.Weak;

/**
 * Helper class to evaluate expressions with GeoText objects in it.
 * 
 * @see ExpressionNode#evaluate(StringTemplate)
 * @author Markus Hohenwarter
 */
public class MyStringBuffer extends ValidExpression implements TextValue {

	private StringBuilder sb;
	@Weak
	private Kernel kernel;

	/**
	 * @param kernel
	 *            Kernel
	 * @param str
	 *            text
	 */
	public MyStringBuffer(Kernel kernel, String str) {
		sb = new StringBuilder(str);
		this.kernel = kernel;
	}

	/**
	 * @param str
	 *            text to append
	 */
	public void append(String str) {
		sb.append(str);
	}

	/**
	 * @param pos
	 *            position to insert (same as in String#insert)
	 * @param str
	 *            text to insert
	 */
	public void insert(int pos, String str) {
		sb.insert(pos, str);
	}

	@Override
	public String toString(StringTemplate tpl) {
		StringBuilder temp = new StringBuilder();

		temp.append("\"");
		temp.append(sb);
		temp.append("\"");

		return temp.toString();
	}

	@Override
	public void resolveVariables(EvalInfo info) {
		// MyStringBuffer never has variables
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	public boolean evaluatesToText() {
		return true;
	}

	@Override
	public MyStringBuffer deepCopy(Kernel kernelForCopy) {
		return getText();
	}

	@Override
	public HashSet<GeoElement> getVariables(SymbolicMode mode) {
		return null;
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		return sb.toString();
	}

	@Override
	final public String toOutputValueString(StringTemplate tpl) {
		StringBuffer sb2 = new StringBuffer(sb.length() + 2);

		sb2.append('"');
		sb2.append(sb);
		sb2.append('"');

		return sb2.toString();
	}

	@Override
	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return sb.toString();
	}

	@Override
	public MyStringBuffer getText() {
		return new MyStringBuffer(kernel, sb.toString());
	}

	@Override
	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	/**
	 * @return kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	@Override
	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.TEXT;
	}

	@Override
	public String getTextString() {
		return sb.toString();
	}
}
