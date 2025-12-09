/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

/**
 * Algorithm for computation of tangent curve
 *
 */
public class AlgoImplicitPolyTangentCurve extends AlgoElement implements
		AlgoTangentHelper {

	private GeoImplicit poly;
	private GeoPointND point;

	private GeoImplicit tangentPoly;
	private boolean pointOnPath;

	/**
	 * @param c
	 *            construction
	 * @param poly
	 *            polynomial
	 * @param point
	 *            point
	 * @param pointOnPath
	 *            whether point is on path by definition
	 */
	public AlgoImplicitPolyTangentCurve(Construction c, GeoImplicit poly,
			GeoPointND point, boolean pointOnPath) {
		super(c, false);
		this.poly = poly;
		this.point = point;
		tangentPoly = (GeoImplicit) poly.copy();
		tangentPoly.preventPathCreation();
		this.pointOnPath = pointOnPath;

		setInputOutput();
		compute();
		// tangentPoly.setLabel("tgt");
	}

	@Override
	public void compute() {

		/*
		 * calculate tangent curve: dF/dx * x_p + dF/dy * y_p + u_{n-1} +
		 * 2*u_{n-2} + ... + n*u_0 where u_i are the terms of poly with total
		 * degree of i.
		 */
		double x = point.getInhomX();
		double y = point.getInhomY();

		tangentPoly.setDefined();
		if (poly instanceof GeoImplicitCurve
				&& poly.getCoeff() == null) {
			GeoImplicitCurve inputCurve = (GeoImplicitCurve) poly;
			FunctionNVar f1 = inputCurve.getExpression();

			FunctionVariable vx = f1.getFunctionVariables()[0];
			FunctionVariable vy = f1.getFunctionVariables()[1];

			// build expression Fx(x0, y0)*(x-x0)+Fy(x0, y0)*(y-y0)
			ExpressionNode x1 = new ExpressionNode(kernel, vx, Operation.MINUS,
					new MyDouble(kernel, x));
			ExpressionNode y1 = new ExpressionNode(kernel, vy, Operation.MINUS,
					new MyDouble(kernel, y));

			x1 = x1.multiply(inputCurve.getDerivativeX().evaluate(x, y));
			y1 = y1.multiply(inputCurve.getDerivativeY().evaluate(x, y));

			tangentPoly.fromEquation(new Equation(kernel, x1.plus(y1),
					new MyDouble(kernel, 0)), null);
			((GeoImplicitCurve) tangentPoly).updatePath();
			return;

		}

		double[][] coeff = poly.getCoeff();

		double[][] newCoeff = new double[coeff.length][];

		int maxDeg = poly.getDeg();

		for (int i = 0; i < coeff.length; i++) {
			newCoeff[i] = new double[coeff[i].length];
			for (int j = 0; j < coeff[i].length; j++) {
				newCoeff[i][j] = (maxDeg - (i + j)) * coeff[i][j];
				if (i + 1 < coeff.length && j < coeff[i + 1].length) {
					newCoeff[i][j] += x * (i + 1) * coeff[i + 1][j];
				}
				if (j + 1 < coeff[i].length) {
					newCoeff[i][j] += y * (j + 1) * coeff[i][j + 1];
				}
			}
		}

		tangentPoly.setCoeff(PolynomialUtils.coeffMinDeg(newCoeff));
		tangentPoly.setDefined();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { poly.toGeoElement(), (GeoElement) point };
		setOnlyOutput(tangentPoly.toGeoElement());
		setDependencies();
	}

	@Override
	public Commands getClassName() {
		return Commands.Tangent;
	}

	/**
	 * @return resulting tangent curve
	 */
	@Override
	public GeoImplicit getTangentCurve() {
		return tangentPoly;
	}

	@Override
	public GeoElement getVec() {
		return point.toGeoElement();
	}

	@Override
	public boolean vecDefined() {
		return point.isDefined() && DoubleUtil.isZero(point.getInhomZ());
	}

	@Override
	public void getTangents(GeoPoint[] ip, OutputHandler<GeoLine> tangents) {
		int n = 0;
		if (point != null && poly.isOnPath(point, Kernel.STANDARD_PRECISION)) {
			tangents.adjustOutputSize(n + 1);
			double dfdx = this.poly.derivativeX(point.getInhomX(),
					point.getInhomY());
			double dfdy = this.poly.derivativeY(point.getInhomX(),
					point.getInhomY());
			if (!DoubleUtil.isEqual(dfdx, 0, 1E-5)
					|| !DoubleUtil.isEqual(dfdy, 0, 1E-5)) {
				tangents.getElement(n).setCoords(dfdx, dfdy,
						-dfdx * point.getInhomX() - dfdy * point.getInhomY());
				n++;
			}
		}
		if (pointOnPath) {
			return;
		}
		for (int i = 0; i < ip.length; i++) {

			if (DoubleUtil.isEqual(ip[i].inhomX, point.getInhomX(), 1E-2)
					&& DoubleUtil.isEqual(ip[i].inhomY, point.getInhomY(), 1E-2)) {
				continue;
			}

			// normal vector does not exist, therefore tangent is not defined
			// We need to check if F1 :=dF/dx and F2 :=dF/dy are both zero when
			// eval at ip[i]
			// The error of F1 is dF1/dx * err(x) + dF1/dy * err(y), where
			// err(x) and err(y) satisfies
			// | (dF/dx) err(x) + (dF/dy) err(y) | < EPSILON
			// So |dF/dx|<= |dF1/dx * err(x) + dF1/dy * err(y)| <= Max(dF1/dx /
			// dF/dx, dF1/dy / dF/dy) * EPSILON
			// A convenient necessary condition of this is (dF/dx)^2 <= |dF1/dx|
			// * EPSILON.
			// Not very reasonably, now we use (dF/dx)^2 <= EPSILON only, to
			// avoid evaluation of dF1/dx
			// TODO: have a more reasonable choice; also we use standard
			// precision rather than working precision (might not be a problem)
			if (DoubleUtil.isEqual(0,
					this.poly.derivativeX(ip[i].inhomX, ip[i].inhomY),
					Kernel.STANDARD_PRECISION_SQRT)
					&& DoubleUtil.isEqual(0,
							this.poly.derivativeY(ip[i].inhomX, ip[i].inhomY),
							Kernel.STANDARD_PRECISION_SQRT)) {
				continue;
			}

			tangents.adjustOutputSize(n + 1);
			tangents.getElement(n).setCoords(
					ip[i].getY() - this.point.getInhomY(),
					this.point.getInhomX() - ip[i].getX(),
					ip[i].getX() * this.point.getInhomY()
							- this.point.getInhomX() * ip[i].getY());
			ip[i].addIncidence(tangents.getElement(n), false);
			n++;
		}

	}

	@Override
	public GeoPointND getTangentPoint(GeoElement geo, GeoLine line) {
		if (geo == poly && pointOnPath) {
			return point;

		}
		// for (int i = 0; i < this.tangents.size(); i++) {
		// if (tangents.getElement(i) == line) {
		// return R;
		// }
		// }
		return null;
	}

}
