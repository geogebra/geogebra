/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoVec2D.java
 *
 * Created on 31. August 2001, 11:34
 */

package org.geogebra.common.kernel.arithmetic;

import java.util.HashSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.main.MyParseError;

/**
 * 
 * @author Markus
 */
public class MyVecNode extends ValidExpression implements VectorValue,
		MyVecNDNode {

	/**
	 * x coordinate
	 */
	protected ExpressionValue x;
	/**
	 * y coordinate
	 */
	protected ExpressionValue y;
	private int mode = Kernel.COORD_CARTESIAN;
	private Kernel kernel;
	private boolean isCASVector = false;

	/**
	 * Creates new MyVec2D
	 * 
	 * @param kernel
	 *            kernel
	 */
	public MyVecNode(Kernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * Creates new MyVec2D with coordinates (x,y) as ExpresssionNodes. Both
	 * nodes must evaluate to NumberValues.
	 * 
	 * @param kernel
	 *            kernel
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	public MyVecNode(Kernel kernel, ExpressionValue x, ExpressionValue y) {
		this(kernel);
		setCoords(x, y);
	}

	public ExpressionValue deepCopy(Kernel kernel1) {
		MyVecNode ret = new MyVecNode(kernel1, x.deepCopy(kernel1),
				y.deepCopy(kernel1));
		ret.mode = mode;
		if (isCASVector()) {
			ret.setCASVector();
		}
		return ret;
	}

	public void resolveVariables() {
		x.resolveVariables();
		y.resolveVariables();
	}

	/**
	 * @return x-coord
	 */
	public ExpressionValue getX() {
		return x;
	}

	/**
	 * @return y-coord
	 */
	public ExpressionValue getY() {
		return y;
	}

	/**
	 * @param r
	 *            radius
	 * @param phi
	 *            phase
	 */
	public void setPolarCoords(ExpressionValue r, ExpressionValue phi) {
		setCoords(r, phi);
		mode = Kernel.COORD_POLAR;
	}

	/**
	 * @return true if uses polar coordinates
	 */
	public boolean hasPolarCoords() {
		return mode == Kernel.COORD_POLAR;
	}

	private void setCoords(ExpressionValue x, ExpressionValue y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return array of coordinates
	 */
	final public double[] getCoords() {
		StringTemplate tpl = StringTemplate.defaultTemplate;
		// check if both ExpressionNodes represent NumberValues
		ExpressionValue evx = x.evaluate(tpl);
		if (!(evx instanceof NumberValue)) {
			String[] str = { "NumberExpected", evx.toString(tpl) };
			throw new MyParseError(kernel.getApplication().getLocalization(),
					str);
		}
		ExpressionValue evy = y.evaluate(tpl);
		if (!(evy instanceof NumberValue)) {
			String[] str = { "NumberExpected", evy.toString(tpl) };
			throw new MyParseError(kernel.getApplication().getLocalization(),
					str);
		}

		if (mode == Kernel.COORD_POLAR) {
			double r = ((NumberValue) evx).getDouble();
			// allow negative radius for US
			double phi = ((NumberValue) evy).getDouble();
			double[] ret = { r * Math.cos(phi), r * Math.sin(phi) };
			return ret;
		}
		// CARTESIAN
		double[] ret = { ((NumberValue) evx).getDouble(),
				((NumberValue) evy).getDouble() };
		return ret;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return toString(tpl, false);
	}

	private String toString(StringTemplate tpl, boolean values) {
		StringBuilder sb = new StringBuilder();
		switch (tpl.getStringType()) {
		case GIAC:
			if (mode == Kernel.COORD_POLAR) {
				sb.append("point((");
				sb.append(print(x, values, tpl));
				sb.append(")*cos(");
				sb.append(print(y, values, tpl));
				sb.append("),(");
				sb.append(print(x, values, tpl));
				sb.append(")*sin(");
				sb.append(print(y, values, tpl));
				sb.append("))");
			} else {
				sb.append(isCASVector() ? "ggbvect[" : "point(");
				sb.append(print(x, values, tpl));
				sb.append(',');
				sb.append(print(y, values, tpl));
				sb.append(isCASVector() ? "]" : ")");
			}
			break;

		default: // continue below

			if (isCASVector && tpl.getStringType().equals(StringType.LATEX)) {

				sb.append(" \\binom{");
				sb.append(print(x, values, tpl));
				sb.append("}{");
				sb.append(print(y, values, tpl));
				sb.append("}");

			} else {

				sb.append(tpl.leftBracket());
				sb.append(print(x, values, tpl));
				if (mode == Kernel.COORD_CARTESIAN) {
					sb.append(", ");
				} else {
					sb.append("; ");
				}
				sb.append(print(y, values, tpl));
				sb.append(tpl.rightBracket());
			}
			break;
		}

		return sb.toString();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl, true);
	}

	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(tpl, !symbolic);
	}

	/**
	 * interface VectorValue implementation
	 */
	public GeoVec2D getVector() {
		GeoVec2D ret = new GeoVec2D(kernel, getCoords());
		ret.setMode(mode);
		return ret;
	}

	public boolean isConstant() {
		return x.isConstant() && y.isConstant();
	}

	public boolean isLeaf() {
		return true;
	}

	/** POLAR or CARTESIAN */
	public int getMode() {
		return mode;
	}

	/** returns all GeoElement objects in the both coordinate subtrees */
	public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> temp, varset = x.getVariables();
		if (varset == null) {
			varset = new HashSet<GeoElement>();
		}
		temp = y.getVariables();
		if (temp != null)
			varset.addAll(temp);

		return varset;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	// could be vector or point
	@Override
	public boolean evaluatesToNonComplex2DVector() {
		return this.mode != Kernel.COORD_COMPLEX;
	}

	// could be vector or point
	@Override
	public boolean evaluatesToVectorNotPoint() {
		return isCASVector;// this.mode != Kernel.COORD_COMPLEX;
	}

	public boolean isNumberValue() {
		return false;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	public Kernel getKernel() {
		return kernel;
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue v = t.process(this);
		if (v != this)
			return v;
		x = x.traverse(t);
		y = y.traverse(t);
		return this;
	}

	@Override
	public boolean inspect(Inspecting t) {
		return t.check(this) || x.inspect(t) || y.inspect(t);
	}

	@Override
	public boolean hasCoords() {
		return true;
	}

	/**
	 * LaTeX form needs to be different in CAS
	 */
	public void setCASVector() {
		isCASVector = true;
	}

	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	public boolean isCASVector() {
		return isCASVector;
	}

	public int getDimension() {
		return 2;
	}

}
