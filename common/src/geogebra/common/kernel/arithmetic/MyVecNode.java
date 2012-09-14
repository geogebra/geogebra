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

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.main.MyParseError;

import java.util.HashSet;

/**
 * 
 * @author Markus
 */
public class MyVecNode extends ValidExpression implements VectorValue {

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

	/** Creates new MyVec2D 
	 * @param kernel kernel
	 */
	public MyVecNode(Kernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * Creates new MyVec2D with coordinates (x,y) as ExpresssionNodes. Both
	 * nodes must evaluate to NumberValues.
	 * @param kernel kernel
	 * @param x x-coord
	 * @param y y-coord
	 */
	public MyVecNode(Kernel kernel, ExpressionValue x, ExpressionValue y) {
		this(kernel);
		setCoords(x, y);
	}

	public ExpressionValue deepCopy(Kernel kernel1) {
		return new MyVecNode(kernel1, x.deepCopy(kernel1), y.deepCopy(kernel1));
	}

	public void resolveVariables(boolean forEquation) {
		x.resolveVariables(forEquation);
		y.resolveVariables(forEquation);
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
	 * @param r radius
	 * @param phi phase
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
		if (!evx.isNumberValue()) {
			String[] str = { "NumberExpected", evx.toString(tpl) };
			throw new MyParseError(kernel.getApplication(), str);
		}
		ExpressionValue evy = y.evaluate(tpl);
		if (!evy.isNumberValue()) {
			String[] str = { "NumberExpected", evy.toString(tpl) };
			throw new MyParseError(kernel.getApplication(), str);
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
		StringBuilder sb = new StringBuilder();
		double[] coords;
		
		switch (tpl.getStringType()) {
		case MATH_PIPER:
			coords = getCoords();
			sb.append("{");
			sb.append(coords[0]);
			sb.append(", ");
			sb.append(coords[1]);
			sb.append("}");
			break;

		case MAXIMA:
			coords = getCoords();
			sb.append("[");
			sb.append(coords[0]);
			sb.append(", ");
			sb.append(coords[1]);
			sb.append("]");
			break;

		case MPREDUCE:
			/*
			 * coords = getCoords(); sb.append("list("); sb.append(coords[0]);
			 * sb.append(", "); sb.append(coords[1]); sb.append(")"); break;
			 */
			
			if (mode == Kernel.COORD_POLAR) {
				sb.append("polartopoint!\u00a7(");
				sb.append(x.toString(tpl));
				sb.append(", ");
				sb.append(y.toString(tpl));
				sb.append(')');
			} else {
				sb.append("myvect(");
				sb.append(x.toString(tpl));
				sb.append(", ");
				sb.append(y.toString(tpl));
				sb.append(')');
			}
			break;
			
		default: // continue below
			sb.append(tpl.getStringType().equals(StringType.LATEX) ? " \\left( " : "(");
			sb.append(x.isGeoElement() ? ((GeoElement) x).getLabel(tpl) : x
					.toString(tpl));
			if (mode == Kernel.COORD_CARTESIAN)
				sb.append(", ");
			else
				sb.append("; ");
			sb.append(y.isGeoElement() ? ((GeoElement) y).getLabel(tpl) : y
					.toString(tpl));
			sb.append(tpl.getStringType().equals(StringType.LATEX) ? " \\right) " : ")");
			break;
		}

		return sb.toString();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	final public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		return toString(tpl);
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
	public boolean isVectorValue() {
		return true;
	}

	public boolean isNumberValue() {
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

	public boolean isListValue() {
		return false;
	}

	final public boolean isExpressionNode() {
		return false;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
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
		if(v!=this)
			return v;
		x = x.traverse(t);
		y = y.traverse(t);
		return this;
	}
	
}
