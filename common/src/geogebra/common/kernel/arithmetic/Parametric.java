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
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.geos.GeoElement;

import java.util.HashSet;

/**
 * A Parametric is a ValidExpression that represents a Line in parametric form
 * (px, py) + t (vx, vy)
 */
public class Parametric extends ValidExpression {
	private ExpressionNode P, v;
	private String parameter;
	private Kernel kernel;
	private String lhs;

	/**
	 * Creates new Parametric P + parameter * v. (X = P + parameter * v)
	 * 
	 * @param kernel
	 *            kernel
	 * @param P
	 *            start point
	 * @param v
	 *            direction vector
	 * @param parameter
	 *            parameter name
	 * @param lhs
	 *            the left hand side of the parametric equation (i.e. X | (x,y))
	 */
	public Parametric(Kernel kernel, ExpressionValue P, ExpressionValue v,
			String parameter, String lhs) {
		if (P.isExpressionNode())
			this.P = (ExpressionNode) P;
		else
			this.P = new ExpressionNode(kernel, P);

		if (v.isExpressionNode())
			this.v = (ExpressionNode) v;
		else
			this.v = new ExpressionNode(kernel, v);

		this.parameter = parameter;
		this.lhs = lhs;
		this.kernel = kernel;
	}

	/**
	 * @return start point
	 */
	public ExpressionNode getP() {
		return P;
	}

	/**
	 * @return direction vector
	 */
	public ExpressionNode getv() {
		return v;
	}

	/**
	 * @return parameter name
	 */
	public String getParameter() {
		return parameter;
	}

	@Override
	public String toString(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		if (tpl.getStringType().equals(StringType.GIAC)) {
			MyVecNode a = (MyVecNode) P.unwrap();
			MyVecNode b = (MyVecNode) v.unwrap();
			sb.append("{x=");
			sb.append(a.x.toString(tpl) + "+" + parameter + "*" + b.x.toString(tpl) + ",");
			sb.append("y=");
			sb.append(a.y.toString(tpl) + "+" + parameter + "*" + b.y.toString(tpl) + "}");
			return sb.toString();
		}
		sb.append((lhs == null) ? "" : lhs + " = " + P.toString(tpl) + " + " + parameter + " "
				+ v.toString(tpl));
		return sb.toString();
	}

	public boolean contains(ExpressionValue ev) {
		return P.contains(ev) || v.contains(ev);
	}

	public ExpressionValue deepCopy(Kernel kernel1) {
		return new Parametric(kernel1, P.deepCopy(kernel1),
				v.deepCopy(kernel1), parameter, lhs);
	}

	public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> vars = new HashSet<GeoElement>();
		vars.addAll(P.getVariables());
		vars.addAll(v.getVariables());
		return vars;
	}

	public boolean isConstant() {
		return P.isConstant() && v.isConstant();
	}

	public boolean isLeaf() {
		return false;
	}

	public boolean isNumberValue() {
		return false;
	}

	public void resolveVariables(boolean forEquation) {
		P.resolveVariables(forEquation);
		v.resolveVariables(forEquation);
	}

	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	public boolean isVector3DValue() {
		return false;
	}

	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	public Kernel getKernel() {
		return kernel;
	}

	@Override
	public String getAssignmentOperator() {
		return ": ";
	}

	@Override
	public String getAssignmentOperatorLaTeX() {
		return ": \\, ";
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue ev = t.process(this);
		P = P.traverse(t).wrap();
		v = v.traverse(t).wrap();
		return ev;
	}

	@Override
	public boolean inspect(Inspecting t) {
		return t.check(this) || P.inspect(t) || v.inspect(t);
	}

}
