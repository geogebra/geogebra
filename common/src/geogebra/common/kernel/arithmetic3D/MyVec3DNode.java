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

package geogebra.common.kernel.arithmetic3D;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.ReplaceableValue;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.Geo3DVec;
import geogebra.common.main.MyParseError;

import java.util.HashSet;

/**
 * 
 * @author Markus + ggb3D
 */
public class MyVec3DNode extends ValidExpression implements Vector3DValue,
		ReplaceableValue {

	private ExpressionValue x, y, z;
	// private int mode = Kernel.COORD_CARTESIAN;
	private Kernel kernel;

	/** Creates new MyVec3D 
	 * @param kernel kernel */
	public MyVec3DNode(Kernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * Creates new MyPoint3DNode with coordinates (x,y,z) as ExpresssionNodes.
	 * Both nodes must evaluate to NumberValues.
	 * @param kernel kernel
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public MyVec3DNode(Kernel kernel, ExpressionValue x, ExpressionValue y,
			ExpressionValue z) {
		this(kernel);
		setCoords(x, y, z);
	}

	public ExpressionValue deepCopy(Kernel kernel1) {
		return new MyVec3DNode(kernel1, x.deepCopy(kernel1), y.deepCopy(kernel1),
				z.deepCopy(kernel1));
	}

	public void resolveVariables() {
		x.resolveVariables();
		y.resolveVariables();
		z.resolveVariables();
	}

	/**
	 * @return x coordinate
	 */
	public ExpressionValue getX() {
		return x;
	}

	/**
	 * @return y coordinate
	 */
	public ExpressionValue getY() {
		return y;
	}

	/**
	 * @return z coordinate
	 */
	public ExpressionValue getZ() {
		return z;
	}

	private void setCoords(ExpressionValue x, ExpressionValue y,
			ExpressionValue z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @return coordinates of this point as array of doubles
	 */
	final public double[] getCoords() {
		// check if both ExpressionNodes represent NumberValues
		StringTemplate errTpl = StringTemplate.defaultTemplate;
		ExpressionValue evx = x.evaluate();
		if (!evx.isNumberValue()) {
			String[] str = { "NumberExpected", evx.toString(errTpl) };
			throw new MyParseError(kernel.getApplication(), str);
		}
		ExpressionValue evy = y.evaluate();
		if (!evy.isNumberValue()) {
			String[] str = { "NumberExpected", evy.toString(errTpl) };
			throw new MyParseError(kernel.getApplication(), str);
		}
		ExpressionValue evz = z.evaluate();
		if (!evz.isNumberValue()) {
			String[] str = { "NumberExpected", evz.toString(errTpl) };
			throw new MyParseError(kernel.getApplication(), str);
		}

		double[] ret = { ((NumberValue) evx).getDouble(),
				((NumberValue) evy).getDouble(),
				((NumberValue) evz).getDouble() };
		return ret;

	}

	@Override
	final public String toString(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		switch (tpl.getStringType()) {
		case MPREDUCE:
			sb.append("myvect(");
			sb.append(x.toString(tpl));
			sb.append(", ");
			sb.append(y.toString(tpl));
			sb.append(", ");
			sb.append(z.toString(tpl));
			sb.append(')');
			break;
		
		default:
			sb.append('(');
			sb.append(x.toString(tpl));
			sb.append(", ");
			sb.append(y.toString(tpl));
			sb.append(", ");
			sb.append(z.toString(tpl));
			sb.append(')');
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
	 * interface Point3DValue implementation
	 */
	public double[] getPointAsDouble() {
		// Application.debug("myvec");
		return getCoords();
	}

	public boolean isConstant() {
		return x.isConstant() && y.isConstant() && z.isConstant();
	}

	public boolean isLeaf() {
		return true;
	}

	/** returns all GeoElement objects in the both coordinate subtrees */
	public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> temp, varset = x.getVariables();
		if (varset == null)
			varset = new HashSet<GeoElement>();
		temp = y.getVariables();
		if (temp != null)
			varset.addAll(temp);
		temp = z.getVariables();
		if (temp != null)
			varset.addAll(temp);
		return varset;
	}

	// TODO could be vector or point
	public boolean isVectorValue() {
		return false;
	}

	/**
	 * @return true for 3D point values
	 */
	public boolean isPoint3DValue() {
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

	final public boolean isVector3DValue() {
		return true;
	}

	public Geo3DVec get3DVec() {
		return kernel.getManager3D().newGeo3DVec( ((NumberValue) x.evaluate()).getDouble(),
				((NumberValue) y.evaluate()).getDouble(),
				((NumberValue) z.evaluate()).getDouble());
	}

	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	public ExpressionValue replace(ExpressionValue oldOb, ExpressionValue newOb) {
		if (x == oldOb) {
			x = newOb;
		} else if (x instanceof ReplaceableValue) {
			x = ((ReplaceableValue) x).replace(oldOb, newOb);
		}

		if (y == oldOb) {
			y = newOb;
		} else if (y instanceof ReplaceableValue) {
			y = ((ReplaceableValue) y).replace(oldOb, newOb);
		}

		if (z == oldOb) {
			z = newOb;
		} else if (z instanceof ReplaceableValue) {
			z = ((ReplaceableValue) z).replace(oldOb, newOb);
		}

		return this;
	}

	public Kernel getKernel() {
		return kernel;
	}
}
