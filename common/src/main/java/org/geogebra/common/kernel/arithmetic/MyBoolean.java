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
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Helper class to evaluate expressions with GeoBoolean objects in it.
 * 
 * @see ExpressionNode#evaluate(StringTemplate)
 * @author Markus Hohenwarter
 */
public class MyBoolean extends ValidExpression implements BooleanValue,
		NumberValue {

	private boolean value;
	private Kernel kernel;

	// #5223
	private boolean isDefined = true;

	/**
	 * Creates new boolean
	 * 
	 * @param kernel
	 *            kernel
	 * @param value
	 *            boolean value
	 */
	public MyBoolean(Kernel kernel, boolean value) {
		this.value = value;
		this.kernel = kernel;
	}

	public MyBoolean(Kernel kernel, boolean value, boolean defined) {
		this(kernel, value);
		this.isDefined = defined;

	}

	/**
	 * Sets value of this boolean
	 * 
	 * @param value
	 *            new value
	 */
	final public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return value ? "true" : "false";
	}

	public boolean isConstant() {
		return true;
	}

	final public boolean isLeaf() {
		return true;
	}

	public void resolveVariables() {
		// do nothing
	}

	final public boolean isNumberValue() {
		return true;
	}

	public ExpressionValue deepCopy(Kernel kernel1) {
		return new MyBoolean(kernel1, value);
	}

	public HashSet<GeoElement> getVariables() {
		return null;
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(tpl);
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	final public MyBoolean getMyBoolean() {
		return new MyBoolean(kernel, value);
	}

	final public boolean getBoolean() {
		return value;
	}

	/**
	 * Returns 1 for true and 0 for false.
	 */
	public double getDouble() {
		return value ? 1 : 0;
	}

	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	public Kernel getKernel() {
		return kernel;
	}

	public GeoElement toGeoElement() {
		return new GeoBoolean(kernel.getConstruction(), value);
	}

	public MyDouble getNumber() {
		return new MyDouble(kernel, getDouble());
	}

	public boolean isAngle() {
		return false;
	}

	public boolean isDefined() {
		return isDefined;
	}

	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	public void setDefined(boolean defined) {
		this.isDefined = defined;
	}
}
