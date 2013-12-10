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
import geogebra.common.kernel.arithmetic.Inspecting;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.Traversing;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.Geo3DVec;
import geogebra.common.main.MyParseError;

import java.util.HashSet;

/**
 * 
 * @author Markus + ggb3D
 */
public class MyVec3DNode extends ValidExpression implements Vector3DValue {

	private ExpressionValue x, y, z;
	// private int mode = Kernel.COORD_CARTESIAN;
	private Kernel kernel;
	
	private int mode = Kernel.COORD_CARTESIAN_3D;

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
		MyVec3DNode ret =  new MyVec3DNode(kernel1, x.deepCopy(kernel1), y.deepCopy(kernel1),
				z.deepCopy(kernel1));
		ret.mode = mode;
		return ret;
	}

	public void resolveVariables(boolean forEquation) {
		x.resolveVariables(forEquation);
		y.resolveVariables(forEquation);
		z.resolveVariables(forEquation);
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
		StringTemplate tpl = StringTemplate.defaultTemplate;
		ExpressionValue evx = x.evaluate(tpl);
		if (!(evx instanceof NumberValue)) {
			String[] str = { "NumberExpected", evx.toString(tpl) };
			throw new MyParseError(kernel.getLocalization(), str);
		}
		ExpressionValue evy = y.evaluate(tpl);
		if (!(evy instanceof NumberValue)) {
			String[] str = { "NumberExpected", evy.toString(tpl) };
			throw new MyParseError(kernel.getLocalization(), str);
		}
		ExpressionValue evz = z.evaluate(tpl);
		if (!(evz instanceof NumberValue)) {
			String[] str = { "NumberExpected", evz.toString(tpl) };
			throw new MyParseError(kernel.getLocalization(), str);
		}

		
		if (mode == Kernel.COORD_SPHERICAL) {
			double r = ((NumberValue) evx).getDouble();
			// allow negative radius for US
			double theta = ((NumberValue) evy).getDouble();
			double phi = ((NumberValue) evz).getDouble();
			double[] ret = { 
					r * Math.cos(theta) * Math.cos(phi), 
					r * Math.sin(theta) * Math.cos(phi),
					r * Math.sin(phi)};
			return ret;
		}
		
		// CARTESIAN 3D
		double[] ret = { ((NumberValue) evx).getDouble(),
				((NumberValue) evy).getDouble(),
				((NumberValue) evz).getDouble() };
		return ret;

	}
	
	
	@Override
	final public String toString(StringTemplate tpl) {
		return toString(tpl, false);
	}
	
	private String toString(StringTemplate tpl, boolean values) {
		StringBuilder sb = new StringBuilder();
		switch (tpl.getStringType()) {
		case GIAC:			
			switch (mode) {
			case Kernel.COORD_SPHERICAL:
				sb.append("point((");
				sb.append(print(x, values, tpl));
				sb.append(")*cos(");
				sb.append(print(y, values, tpl));
				sb.append(")*cos(");
				sb.append(print(z, values, tpl));
				sb.append("),(");
				sb.append(print(x, values, tpl));
				sb.append(")*sin(");
				sb.append(print(y, values, tpl));
				sb.append(")*cos(");
				sb.append(print(z, values, tpl));
				sb.append("),(");
				sb.append(print(x, values, tpl));
				sb.append(")*sin(");
				sb.append(print(z, values, tpl));
				sb.append("))");
				break;
				
			case Kernel.COORD_CARTESIAN_3D:
				sb.append("point(");
				sb.append(print(x, values, tpl));
				sb.append(',');
				sb.append(print(y, values, tpl));
				sb.append(',');
				sb.append(print(z, values, tpl));
				sb.append(")");
				break;
				
			case Kernel.COORD_COMPLEX:
				sb.append("(");
				sb.append(print(x, values, tpl));
				sb.append("+i*(");
				sb.append(print(y, values, tpl));
				sb.append("))");
				break;
				
			default:
			case Kernel.COORD_CARTESIAN:
				sb.append("point(");
				sb.append(print(x, values, tpl));
				sb.append(',');
				sb.append(print(y, values, tpl));
				sb.append(")");
				break;
			}
			break;
		default:
			sb.append(tpl.leftBracket());
			sb.append(print(x,values,tpl));
			appendSeparator(sb);
			sb.append(print(y,values,tpl));
			appendSeparator(sb);
			sb.append(print(z,values,tpl));
			sb.append(tpl.rightBracket());
		}
		return sb.toString();
	}
	
	private void appendSeparator(StringBuilder sb){
		if (mode == Kernel.COORD_CARTESIAN_3D)
			sb.append(", ");
		else
			sb.append("; ");
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl, true);
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

	/**
	 * @return true for 3D point values
	 */
	public boolean isPoint3DValue() {
		return true;
	}

	public boolean isNumberValue() {
		return false;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	@Override
	final public boolean evaluatesTo3DVector() {
		return true;
	}

	public Geo3DVec get3DVec() {
		Geo3DVec ret = kernel.getManager3D().newGeo3DVec( x.evaluateDouble(),
				y.evaluateDouble(),
				z.evaluateDouble());
		return ret;
	}

	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue ev = t.process(this);
		if(ev!=this)
			return ev;
		x = x.traverse(t);
		y = y.traverse(t);
		z = z.traverse(t);
		return this;
	}
	
	
	@Override
	public boolean inspect(Inspecting t){
		return t.check(this) || x.inspect(t) || y.inspect(t) || z.inspect(t);
	}

	public Kernel getKernel() {
		return kernel;
	}
	
	@Override
	public boolean hasCoords() {
		return true;
	}

	public void setSphericalPolarCoords(ExpressionValue r, ExpressionValue theta, ExpressionValue phi) {
			setCoords(r, theta, phi);
			mode = Kernel.COORD_SPHERICAL;
		}
	
	public int getMode(){
		return mode;
	}
}
