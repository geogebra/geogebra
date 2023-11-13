package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.DoubleUtil;

/**
 * Algorithm for computation of tangent curve
 *
 */
public class AlgoImplicitPolyTangentLine extends AlgoElement implements
		AlgoTangentHelper {

	private GeoImplicit poly;
	private GeoLineND line;

	private GeoImplicit tangentPoly;

	/**
	 * @param c
	 *            construction
	 * @param poly
	 *            polynomial
	 * @param line
	 *            parallel line
	 */
	public AlgoImplicitPolyTangentLine(Construction c, GeoImplicit poly,
			GeoLineND line) {
		super(c, false);
		this.poly = poly;
		this.line = line;
		tangentPoly = (GeoImplicit) poly.copy();

		// tangentPoly.preventPathCreation();

		setInputOutput();
		compute();
	}

	@Override
	public void compute() {

		/*
		 * calculate tangent curve: dF/dx * x_p + dF/dy * y_p + u_{n-1} +
		 * 2*u_{n-2} + ... + n*u_0 where u_i are the terms of poly with total
		 * degree of i.
		 */
		if (line.isGeoElement3D()) {
			return;
		}
		GeoLine line2D = (GeoLine) this.line;
		tangentPoly.setDefined();
		if (poly instanceof GeoImplicitCurve
				&& poly.getCoeff() == null) {
			GeoImplicitCurve inputCurve = (GeoImplicitCurve) poly;

			// build expression Fx*(x-x0)+Fy*(y-y0)
			ExpressionNode y1 = new ExpressionNode(kernel, -line2D.getX());
			ExpressionNode x1 = new ExpressionNode(kernel, line2D.getY());

			x1 = x1.multiply(inputCurve.getDerivativeX().getExpression());
			y1 = y1.multiply(inputCurve.getDerivativeY().getExpression());

			tangentPoly.fromEquation(new Equation(kernel, x1.plus(y1),
					new MyDouble(kernel, 0)), null);
			return;

		}
		double x = line2D.getY();
		double y = -line2D.getX();
		double[][] coeff = poly.getCoeff();

		double[][] newCoeff = new double[coeff.length][];

		for (int i = 0; i < coeff.length; i++) {
			newCoeff[i] = new double[coeff[i].length];
			for (int j = 0; j < coeff[i].length; j++) {
				newCoeff[i][j] = 0;
				if (i + 1 < coeff.length && j < coeff[i + 1].length) {
					newCoeff[i][j] += x * (i + 1) * coeff[i + 1][j];
				}
				if (j + 1 < coeff[i].length) {
					newCoeff[i][j] += y * (j + 1) * coeff[i][j + 1];
				}
				// helper = helper.plus(vx.wrap().power(i)
				// .multiply(vy.wrap().power(j)).multiply(newCoeff[i][j]));
			}
		}

		tangentPoly.setCoeff(PolynomialUtils.coeffMinDeg(newCoeff));
		tangentPoly.setDefined();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { poly.toGeoElement(), line.toGeoElement() };
		setOnlyOutput(tangentPoly);
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
		return line.toGeoElement();
	}

	@Override
	public boolean vecDefined() {
		return line.isDefined();
	}

	@Override
	public void getTangents(GeoPoint[] ip, OutputHandler<GeoLine> tangents) {
		int n = 0;
		GeoLine line2d = (GeoLine) line;
		for (int i = 0; i < ip.length; i++) {

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
					line2d.getX(),
					line2d.getY(),
					-ip[i].getX() * line2d.getX() - line2d.getY()
									* ip[i].getY());
			ip[i].addIncidence(tangents.getElement(n), false);
			n++;
		}

	}

	@Override
	public GeoPointND getTangentPoint(GeoElement geo, GeoLine l) {
		// TODO Auto-generated method stub
		return null;
	}

}
