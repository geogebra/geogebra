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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;

import com.google.j2objc.annotations.Weak;

/**
 * Helper class to evaluate expressions with GeoBoolean objects in it.
 * 
 * @see ExpressionNode#evaluate(StringTemplate)
 * @author Markus Hohenwarter
 */
public class MyBoolean extends ValidExpression
		implements BooleanValue, NumberValue {

	private boolean value;
	@Weak
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

	/**
	 * @param kernel
	 *            kernel
	 * @param value
	 *            true or false
	 * @param defined
	 *            whether it's defined
	 */
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

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	final public boolean isLeaf() {
		return true;
	}

	@Override
	public void resolveVariables(EvalInfo info) {
		// do nothing
	}

	@Override
	final public boolean isNumberValue() {
		return true;
	}

	@Override
	public MyBoolean deepCopy(Kernel kernel1) {
		return new MyBoolean(kernel1, value);
	}

	@Override
	public HashSet<GeoElement> getVariables(SymbolicMode mode) {
		return null;
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	@Override
	final public MyBoolean getMyBoolean() {
		return new MyBoolean(kernel, value);
	}

	@Override
	final public boolean getBoolean() {
		return value;
	}

	/**
	 * Returns 1 for true and 0 for false.
	 */
	@Override
	public double getDouble() {
		return value ? 1 : 0;
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
	public GeoElement toGeoElement(Construction cons) {
		return new GeoBoolean(cons, value);
	}

	@Override
	public MyDouble getNumber() {
		return new MyDouble(kernel, getDouble());
	}

	@Override
	public int getAngleDim() {
		return 0;
	}

	@Override
	public boolean isDefined() {
		return isDefined;
	}

	@Override
	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	/**
	 * @param defined
	 *            whether it's defined or not
	 */
	public void setDefined(boolean defined) {
		this.isDefined = defined;
	}

	@Override
	public String getLabel(StringTemplate tpl) {
		return toValueString(tpl);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.BOOLEAN;
	}
}
