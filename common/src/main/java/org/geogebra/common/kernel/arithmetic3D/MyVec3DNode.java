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

package org.geogebra.common.kernel.arithmetic3D;

import java.util.HashSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ReplaceChildrenByValues;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.arithmetic3D.vector.VectorPrinterMapBuilder3D;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.Geo3DVecInterface;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.vector.VectorNodeStringifier;
import org.geogebra.common.kernel.printing.printer.vector.VectorPrinterMapBuilder;
import org.geogebra.common.kernel.printing.printer.vector.VectorPrintingMode;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.MyParseError;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

import com.google.j2objc.annotations.Weak;

/**
 * 
 * @author Markus + ggb3D
 */
public class MyVec3DNode extends ValidExpression
		implements Vector3DValue, MyVecNDNode, PrintableVector {

	private ExpressionValue x;
	private ExpressionValue y;
	private ExpressionValue z;
	@Weak
	private Kernel kernel;
	private int mode = Kernel.COORD_CARTESIAN_3D;
	private boolean isCASVector;
	private VectorNodeStringifier stringifier;

	/**
	 * Creates new MyVec3D
	 * 
	 * @param kernel
	 *            kernel
	 */
	public MyVec3DNode(Kernel kernel) {
		this.kernel = kernel;
		kernel.getConstruction().addUsedType(GeoClass.POINT3D);
		initStringifier();
	}

	/**
	 * Creates new MyPoint3DNode with coordinates (x,y,z) as ExpresssionNodes.
	 * Both nodes must evaluate to NumberValues.
	 * 
	 * @param kernel
	 *            kernel
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param z
	 *            z coordinate
	 */
	public MyVec3DNode(Kernel kernel, ExpressionValue x, ExpressionValue y,
			ExpressionValue z) {
		this(kernel);
		setCoords(x, y, z);
	}

	private void initStringifier() {
		VectorPrinterMapBuilder builder = new VectorPrinterMapBuilder3D();
		stringifier = new VectorNodeStringifier(this, builder.build(this));
		stringifier.setPrintingMode(VectorPrintingMode.Cartesian);
	}

	@Override
	public MyVec3DNode deepCopy(Kernel kernel1) {
		MyVec3DNode ret = new MyVec3DNode(kernel1, x.deepCopy(kernel1),
				y.deepCopy(kernel1), z.deepCopy(kernel1));
		ret.setMode(mode);
		if (isCASVector()) {
			ret.setupCASVector();
		}

		return ret;
	}

	@Override
	public void resolveVariables(EvalInfo info) {
		x.resolveVariables(info);
		y.resolveVariables(info);
		z.resolveVariables(info);
	}

	/**
	 * @return x coordinate
	 */
	@Override
	public ExpressionValue getX() {
		return x;
	}

	/**
	 * @return y coordinate
	 */
	@Override
	public ExpressionValue getY() {
		return y;
	}

	/**
	 * @return z coordinate
	 */
	@Override
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
			throw new MyParseError(kernel.getLocalization(), Errors.NumberExpected,
					evx.wrap().toString(tpl));
		}
		ExpressionValue evy = y.evaluate(tpl);
		if (!(evy instanceof NumberValue)) {
			throw new MyParseError(kernel.getLocalization(), Errors.NumberExpected,
					evy.wrap().toString(tpl));
		}
		ExpressionValue evz = z.evaluate(tpl);
		if (!(evz instanceof NumberValue)) {
			throw new MyParseError(kernel.getLocalization(), Errors.NumberExpected,
					evz.wrap().toString(tpl));
		}

		if (mode == Kernel.COORD_SPHERICAL) {
			double r = evx.evaluateDouble();
			// allow negative radius for US
			double theta = evy.evaluateDouble();
			double phi = evz.evaluateDouble();
			double[] ret = { r * Math.cos(theta) * Math.cos(phi),
					r * Math.sin(theta) * Math.cos(phi), r * Math.sin(phi) };
			return ret;
		}

		// CARTESIAN 3D
		double[] ret = { evx.evaluateDouble(), evy.evaluateDouble(),
				evz.evaluateDouble() };
		return ret;

	}

	@Override
	final public String toString(StringTemplate tpl) {
		return stringifier.toString(tpl);
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return stringifier.toValueString(tpl);
	}

	@Override
	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return symbolic ? stringifier.toString(tpl) : stringifier.toValueString(tpl);
	}

	/**
	 * interface Point3DValue implementation
	 */
	@Override
	public double[] getPointAsDouble() {
		// Application.debug("myvec");
		return getCoords();
	}

	@Override
	public boolean isConstant() {
		return x.isConstant() && y.isConstant() && z.isConstant();
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	/** returns all GeoElement objects all coordinate subtrees */
	@Override
	public HashSet<GeoElement> getVariables(SymbolicMode symbolicMode) {
		HashSet<GeoElement> temp, varset = x.getVariables(symbolicMode);
		if (varset == null) {
			varset = new HashSet<>();
		}
		temp = y.getVariables(symbolicMode);
		if (temp != null) {
			varset.addAll(temp);
		}
		temp = z.getVariables(symbolicMode);
		if (temp != null) {
			varset.addAll(temp);
		}
		return varset;
	}

	/**
	 * @return true for 3D point values
	 */
	public boolean isPoint3DValue() {
		return true;
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	// could be vector or point?
	@Override
	public boolean evaluatesToVectorNotPoint() {
		return isCASVector; // this.mode != Kernel.COORD_COMPLEX;
	}

	@Override
	public Geo3DVecInterface getVector() {
		double[] coords = getCoords();
		Geo3DVecInterface ret = kernel.getManager3D().newGeo3DVec(coords[0],
				coords[1], coords[2]);
		ret.setMode(mode);
		return ret;
	}

	@Override
	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue ev = t.process(this);
		if (ev != this) {
			return ev;
		}
		x = x.traverse(t);
		y = y.traverse(t);
		z = z.traverse(t);
		return this;
	}

	@Override
	public boolean inspect(Inspecting t) {
		return t.check(this) || x.inspect(t) || y.inspect(t) || z.inspect(t);
	}

	/**
	 * @return kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	@Override
	public boolean hasCoords() {
		return true;
	}

	/**
	 * Sets the spherical coords and changes the coord mode
	 * 
	 * @param r
	 *            radius
	 * @param theta
	 *            argument
	 * @param phi
	 *            alt
	 */
	public void setSphericalPolarCoords(ExpressionValue r,
			ExpressionValue theta, ExpressionValue phi) {
		setCoords(r, theta, phi);
		setMode(Kernel.COORD_SPHERICAL);
	}

	@Override
	public int getToStringMode() {
		return mode;
	}

	@Override
	public void setupCASVector() {
		isCASVector = true;
		setVectorPrintingMode();
	}

	@Override
	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	@Override
	public boolean isCASVector() {
		return isCASVector;
	}

	@Override
	public int getCoordinateSystem() {
		return mode;
	}

	@Override
	public int getDimension() {
		return 3;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.VECTOR3D;
	}

	@Override
	public ExpressionValue getUndefinedCopy(Kernel kernel1) {
		return kernel1.getManager3D().newGeo3DVec(Double.NaN, Double.NaN,
				Double.NaN);
	}

	@Override
	public void setMode(int mode) {
		this.mode = mode;

		if (mode == Kernel.COORD_CARTESIAN_3D) {
			stringifier.setPrintingMode(VectorPrintingMode.Cartesian);
		} else {
			stringifier.setPrintingMode(VectorPrintingMode.Polar);
		}
	}

	@Override
	public void replaceChildrenByValues(GeoElement geo) {
		if (x instanceof ReplaceChildrenByValues) {
			((ReplaceChildrenByValues) x).replaceChildrenByValues(geo);
		}
		if (y instanceof ReplaceChildrenByValues) {
			((ReplaceChildrenByValues) y).replaceChildrenByValues(geo);
		}
		if (z instanceof ReplaceChildrenByValues) {
			((ReplaceChildrenByValues) z).replaceChildrenByValues(geo);
		}
	}

	@Override
	public ExpressionValue evaluate(StringTemplate tpl) {
		// MyNumberPair used for datafunction -- don't simplify
		if (x.evaluatesToList() || y.evaluatesToList() || z.evaluatesToList()) {
			if (x.wrap().containsFreeFunctionVariable(null)
					|| y.wrap().containsFreeFunctionVariable(null)
					|| z.wrap().containsFreeFunctionVariable(null)) {
				return super.evaluate(tpl);
			}
			MyList result = new MyList(kernel);
			ExpressionValue xEval = x.evaluate(tpl);
			ExpressionValue yEval = y.evaluate(tpl);
			ExpressionValue zEval = z.evaluate(tpl);
			int size = 0;
			int maxSize = Integer.MAX_VALUE;
			if (xEval instanceof ListValue) {
				maxSize = size = ((ListValue) xEval).size();
			}
			if (yEval instanceof ListValue) {
				size = Math.min(((ListValue) yEval).size(), maxSize);
			}
			if (zEval instanceof ListValue) {
				size = Math.min(((ListValue) zEval).size(), maxSize);
			}
			for (int idx = 0; idx < size; idx++) {
				MyVec3DNode el = new MyVec3DNode(kernel,
						MyList.get(xEval, idx), MyList.get(yEval, idx),
						MyList.get(zEval, idx));
				el.setMode(mode);
				result.addListElement(el);
			}
			return result;
		}
		return super.evaluate(tpl);
	}

	@Override
	public void setLabel(String label) {
		super.setLabel(label);
		if (isVectorLabel(label)) {
			stringifier.setPrintingMode(VectorPrintingMode.Vector);
		}
	}

	private boolean isVectorLabel(String label) {
		return label != null && StringUtil.isLowerCase(label.charAt(0));
	}

	@Override
	public void setVectorPrintingMode() {
		stringifier.setPrintingMode(VectorPrintingMode.Vector);
	}
}
