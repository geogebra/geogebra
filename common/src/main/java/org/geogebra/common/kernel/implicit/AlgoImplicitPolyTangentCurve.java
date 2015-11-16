package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.Operation;

/**
 * Algorithm for computation of tangent curve
 *
 */
public class AlgoImplicitPolyTangentCurve extends AlgoElement {
	
	private GeoImplicit poly;
	private GeoPointND point;
	
	private GeoImplicitCurve tangentPoly;

	/**
	 * @param c construction
	 * @param poly polynomial
	 * @param point point
	 */
	public AlgoImplicitPolyTangentCurve(Construction c, GeoImplicit poly,
			GeoPointND point) {
		super(c, false);
		this.poly=poly;
		this.point=point;
		tangentPoly = new GeoImplicitCurve(c);

		tangentPoly.preventPathCreation();

		setInputOutput();
		compute();
		// tangentPoly.setLabel("tgt");
	}
	

	@Override
	public void compute() {
		
		/*
		 *  calculate tangent curve:
		 *  dF/dx * x_p + dF/dy * y_p + u_{n-1} + 2*u_{n-2} + ... + n*u_0
		 *  where u_i are the terms of poly with total degree of i.
		 */
		

		double x = point.getInhomX();
		double y = point.getInhomY();
		tangentPoly.setDefined();
		if (poly instanceof GeoImplicitCurve && poly.getCoeff() == null) {
			GeoImplicitCurve inputCurve = ((GeoImplicitCurve) poly);
			FunctionNVar f1 = inputCurve.getExpression();

			FunctionVariable vx = f1.getFunctionVariables()[0];
			FunctionVariable vy = f1.getFunctionVariables()[1];

			// build expression
			ExpressionNode x1 = new ExpressionNode(kernel, vx, Operation.MINUS,
					new MyDouble(kernel, point.getInhomX()));
			ExpressionNode y1 = new ExpressionNode(kernel, vy, Operation.MINUS,
					new MyDouble(kernel, point.getInhomY()));

			x1 = x1.multiply(inputCurve.getDerivativeX().getExpression());
			y1 = y1.multiply(inputCurve.getDerivativeY().getExpression());

			FunctionNVar f2 = new FunctionNVar(x1.plus(y1),
					new FunctionVariable[] { vx, vy });
			tangentPoly.fromEquation(new Equation(kernel, f2, new MyDouble(
					kernel, 0)));
			return;

		}


		double[][] coeff = poly.getCoeff();
	
		double [][] newCoeff = new double[coeff.length][];
		
		int maxDeg = poly.getDeg();
		// ExpressionNode helper = new ExpressionNode(kernel, 0);
		// GeoImplicitCurve inputCurve = ((GeoImplicitCurve) poly);
		// FunctionNVar f1 = inputCurve.getExpression();

		// FunctionVariable vx = f1.getFunctionVariables()[0];
		// FunctionVariable vy = f1.getFunctionVariables()[1];
		for (int i=0;i<coeff.length;i++){
			newCoeff[i]=new double[coeff[i].length];
			for (int j=0;j<coeff[i].length;j++){
				newCoeff[i][j]=(maxDeg-(i+j))*coeff[i][j];
				if (i+1<coeff.length&&j<coeff[i+1].length){
					newCoeff[i][j]+=x*(i+1)*coeff[i+1][j];
				}
				if (j+1<coeff[i].length){
					newCoeff[i][j]+=y*(j+1)*coeff[i][j+1];
				}
				// helper = helper.plus(vx.wrap().power(i)
				// .multiply(vy.wrap().power(j)).multiply(newCoeff[i][j]));
			}
		}
		// tangentPoly.fromEquation(new Equation(kernel, new
		// FunctionNVar(helper,
		// new FunctionVariable[] { vx, vy }), new MyDouble(kernel, 0)));
		tangentPoly.setCoeff(PolynomialUtils.coeffMinDeg(newCoeff));
		tangentPoly.setDefined();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { poly.toGeoElement(), (GeoElement) point };
		setOutputLength(1);
		setOutput(0, tangentPoly.toGeoElement());
		setDependencies();
	}

	@Override
	public Commands getClassName() {
		return Commands.Tangent;
	}
	/**
	 * @return resulting tangent curve
	 */
	public GeoImplicit getTangentCurve() {
		return tangentPoly;
	}

	// TODO Consider locusequability

}
