/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVec4D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoCurveCartesian;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

public class AlgebraProcessor3D extends AlgebraProcessor {

	public AlgebraProcessor3D(Kernel kernel, CommandDispatcher cd) {
		super(kernel, cd);
	}

	/**
	 * creates 3D point or 3D vector
	 * 
	 * @param n
	 * @param evaluate
	 * @return 3D point or 3D vector
	 */
	@Override
	protected GeoElement[] processPointVector3D(ExpressionNode n,
			ExpressionValue evaluate) {
		String label = n.getLabel();

		double[] p = ((Vector3DValue) evaluate).getPointAsDouble();
		int mode = ((Vector3DValue) evaluate).getMode();

		GeoElement[] ret = new GeoElement[1];
		boolean isIndependent = n.isConstant();

		// make vector, if label begins with lowercase character
		if (label != null) {
			if (!(n.isForcedPoint() || n.isForcedVector())) { // may be set by
																// MyXMLHandler
				if (Character.isLowerCase(label.charAt(0)))
					n.setForceVector();
				else
					n.setForcePoint();
			}
		}

		boolean isVector = n.shouldEvaluateToGeoVector();

		if (isIndependent) {
			// get coords
			double x = p[0];
			double y = p[1];
			double z = p[2];
			if (isVector)
				ret[0] = kernel.getManager3D().Vector3D(label, x, y, z);
			else
				ret[0] = kernel.getManager3D().Point3D(label, x, y, z, false);
		} else {
			if (isVector)
				ret[0] = kernel.getManager3D().DependentVector3D(label, n);
			else
				ret[0] = kernel.getManager3D().DependentPoint3D(label, n);
		}

		if (mode == Kernel.COORD_SPHERICAL) {
			((GeoVec4D) ret[0]).setMode(Kernel.COORD_SPHERICAL);
			ret[0].updateRepaint();
		}

		return ret;
	}

	@Override
	protected void checkNoTermsInZ(Equation equ) {

		if (equ.containsZ()) {
			switch (equ.degree()) {
			case 1:
				equ.setForcePlane();
				break;
			case 2:
				equ.setForceQuadric();
				break;
			}
		}

	}

	@Override
	protected GeoElement[] processLine(Equation equ) {

		if (equ.isForcedLine())
			return super.processLine(equ);

		// check if the equ is forced plane or if the 3D view has the focus
		if (equ.isForcedPlane() || kernel.isParsingFor3D()) {
			return processPlane(equ);
		}
		return super.processLine(equ);

	}

	@Override
	protected GeoElement[] processConic(Equation equ) {

		if (equ.isForcedConic())
			return super.processConic(equ);

		// check if the equ is forced plane or if the 3D view has the focus
		if (equ.isForcedQuadric()
				|| kernel.getApplication().getActiveEuclidianView()
						.isEuclidianView3D()) {
			return processQuadric(equ);
		}
		return super.processConic(equ);

	}

	private GeoElement[] processQuadric(Equation equ) {
		double xx = 0, yy = 0, zz = 0, xy = 0, xz = 0, yz = 0, x = 0, y = 0, z = 0, c = 0;
		GeoElement[] ret = new GeoElement[1];
		GeoQuadric3D quadric;
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();

		boolean isIndependent = lhs.isConstant();

		if (isIndependent) {
			xx = lhs.getCoeffValue("xx");
			yy = lhs.getCoeffValue("yy");
			zz = lhs.getCoeffValue("zz");
			c = lhs.getCoeffValue("");
			xy = lhs.getCoeffValue("xy") / 2;
			xz = lhs.getCoeffValue("xz") / 2;
			yz = lhs.getCoeffValue("yz") / 2;
			x = lhs.getCoeffValue("x") / 2;
			y = lhs.getCoeffValue("y") / 2;
			z = lhs.getCoeffValue("z") / 2;

			double[] coeffs = { xx, yy, zz, c, xy, xz, yz, x, y, z };
			quadric = new GeoQuadric3D(cons, label, coeffs);
		} else {
			quadric = (GeoQuadric3D) kernel.getManager3D().DependentQuadric3D(
					label, equ);
		}

		ret[0] = quadric;
		return ret;
	}

	/**
	 * @param equ
	 *            equation to process
	 * @return resulting plane
	 */
	private GeoElement[] processPlane(Equation equ) {
		double a = 0, b = 0, c = 0, d = 0;
		GeoPlane3D plane = null;
		GeoElement[] ret = new GeoElement[1];
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();

		boolean isIndependent = lhs.isConstant();

		if (isIndependent) {
			// get coefficients
			a = lhs.getCoeffValue("x");
			b = lhs.getCoeffValue("y");
			c = lhs.getCoeffValue("z");
			d = lhs.getCoeffValue("");
			plane = (GeoPlane3D) kernel.getManager3D().Plane3D(label, a, b, c,
					d);
		} else
			plane = (GeoPlane3D) kernel.getManager3D().DependentPlane3D(label,
					equ);

		ret[0] = plane;
		return ret;
	}

	protected GeoElement[] processParametricFunction(ExpressionNode exp,
			ExpressionValue ev, FunctionVariable fv, String label) {
		if (ev instanceof Vector3DValue) {
			GeoNumeric loc = new GeoNumeric(cons);
			loc.setLocalVariableLabel(fv.getSetVarString());
			exp.replace(fv, loc);

			ExpressionNode cx = computeCoord(exp, 0);
			ExpressionNode cy = computeCoord(exp, 1);
			ExpressionNode cz = computeCoord(exp, 2);

			ExpressionValue[] coefX = new ExpressionValue[5];
			ExpressionValue[] coefY = new ExpressionValue[5];
			ExpressionValue[] coefZ = new ExpressionValue[5];
			for (int i = 0; i < coefX.length; i++) {
				coefX[i] = new ExpressionNode(kernel, 0);
				coefY[i] = new ExpressionNode(kernel, 0);
				coefZ[i] = new ExpressionNode(kernel, 0);
			}
			int degX = getPolyCoeffs(cx, coefX, new ExpressionNode(kernel, 1),
					loc);
			int degY = getPolyCoeffs(cy, coefY, new ExpressionNode(kernel, 1),
					loc);
			int degZ = getPolyCoeffs(cz, coefZ, new ExpressionNode(kernel, 1),
					loc);

			if ((degX >= 0 && degY >= 0 && degZ >= 0)
					&& (degX < 2 && degY < 2 && degZ < 2)) {
				/*
				 * if (P.isGeoElement3D() || v.isGeoElement3D()) { if
				 * (isConstant) { line = new GeoLine3D(cons); ((GeoLine3D)
				 * line).setCoord(P.getCoordsInD3(),v.getCoordsInD3());
				 * line.setLabel(par.getLabel()); }else{ line =
				 * kernel.getManager3D().Line3D(par.getLabel(), P, v); } } else
				 * { line = Line(par, (GeoPoint) P, (GeoVector) v, isConstant);
				 * }
				 */
				GeoLine3D line = new GeoLine3D(cons);
				if (coefX[0].isConstant() && coefY[0].isConstant()
						&& coefZ[0].isConstant() && coefX[1].isConstant()
						&& coefY[1].isConstant() && coefZ[1].isConstant()) {

					Coords start = new Coords(new double[] {
							coefX[0].evaluateDouble(),
							coefY[0].evaluateDouble(),
							coefZ[0].evaluateDouble() });
					Coords v = new Coords(new double[] {
							coefX[1].evaluateDouble(),
							coefY[1].evaluateDouble(),
							coefZ[1].evaluateDouble() });
					line.setCoord(start, v);
					line.setToParametric(fv.getSetVarString());
					line.setLabel(label);
				} else {
					line = (GeoLine3D) kernel.getManager3D().Line3D(label,
							coefX, coefY, coefZ);

				}
				line.setToParametric(fv.getSetVarString());
				return new GeoElement[] { line };

			}
			AlgoDependentNumber nx = new AlgoDependentNumber(cons, cx, false);
			cons.removeFromConstructionList(nx);
			AlgoDependentNumber ny = new AlgoDependentNumber(cons, cy, false);
			cons.removeFromConstructionList(ny);
			AlgoDependentNumber nz = new AlgoDependentNumber(cons, cz, false);
			cons.removeFromConstructionList(nz);
			GeoNumeric from = new GeoNumeric(cons, -10);
			GeoNumeric to = new GeoNumeric(cons, 10);
			AlgoCurveCartesian ac = new AlgoCurveCartesian3D(cons, label,
					new NumberValue[] { nx.getNumber(), ny.getNumber(),
							nz.getNumber() }, loc, from, to);
			return ac.getOutput();
		}
		return super.processParametricFunction(exp, ev, fv, label);

	}

}
