/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.arithmetic;

import geogebra.common.euclidian.EuclidianStyleConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.main.MyError;

/**
 * stores left and right hand side of an inequality as Expressions
 */
public class Inequality {

	/** can be used e.g. by PointIn, but cannot be drawn */
	public static final int INEQUALITY_INVALID = 0;

	/** x > f(y) */
	public static final int INEQUALITY_PARAMETRIC_X = 1;
	/** y > f(x) */
	public static final int INEQUALITY_PARAMETRIC_Y = 2;
	/** f(x,y) >0, degree of f greater than 2 */
	public static final int INEQUALITY_IMPLICIT = 3;
	/** f(x,y) >0, f is quadratic */
	public static final int INEQUALITY_CONIC = 4;

	/** inequality with one variable */
	public static final int INEQUALITY_1VAR_X = 5;
	/** inequality with one variable, called y */
	public static final int INEQUALITY_1VAR_Y = 6;

	private Operation op = Operation.LESS;
	private int type;
	/*private GeoImplicitPoly impBorder;*/
	private GeoConic conicBorder;
	private GeoFunction funBorder;
	private GeoElement border;
	private AbstractKernel kernel;
	private boolean isAboveBorder;
	private ExpressionNode normal;
	private FunctionVariable[] fv;
	private MyDouble coef;
	private GeoPoint2[] zeros;	
	// if variable x or y appears with 0 coef, we want to replace the 
	// variable by 0 itself to avoid errors on computation
	private MyDouble zeroDummy0,zeroDummy1;

	/**
	 * check whether ExpressionNodes are evaluable to instances of Polynomial or
	 * NumberValue and build an Inequality out of them
	 * 
	 * @param kernel Kernel
	 * @param lhs left hand side of the equation
	 * @param rhs right hand side of the equation
	 * @param op operation
	 * @param fv variable
	 * @param function function to which this ineq belongs 
	 */
	public Inequality(AbstractKernel kernel, ExpressionValue lhs, ExpressionValue rhs,
			Operation op, FunctionVariable[] fv, FunctionalNVar function) {

		this.op = op;
		this.kernel = kernel;
		this.fv = fv;		
		
		if (op.equals(Operation.GREATER) || op.equals(Operation.GREATER_EQUAL)) {
			normal = new ExpressionNode(kernel, lhs, Operation.MINUS, rhs);

		} else {
			normal = new ExpressionNode(kernel, rhs, Operation.MINUS, lhs);
		}
		update();
	}

	private void update() {		
		if (fv.length == 1) {
			init1varFunction(0);
			if(!funBorder.isPolynomialFunction(false)){
				type = INEQUALITY_INVALID;
			}
			else if (fv[0].toString().equals("y")) {
				type = INEQUALITY_1VAR_Y;
			} else
				type = INEQUALITY_1VAR_X;

			return;
		}
		if(zeroDummy0!=null)
			normal.replaceAndWrap(zeroDummy0, fv[0]);
		if(zeroDummy1!=null)
			normal.replaceAndWrap(zeroDummy1, fv[1]);
		Double coefY = normal.getCoefficient(fv[1]);
		Double coefX = normal.getCoefficient(fv[0]);
		Function fun = null;
		if (coefY != null && !AbstractKernel.isZero(coefY) && !Double.isNaN(coefY)
				&& (coefX == null || Math.abs(coefX) < Math.abs(coefY))) {
			coef = new MyDouble(kernel, -coefY);
			isAboveBorder = coefY > 0;
			ExpressionNode m = new ExpressionNode(kernel, new ExpressionNode(
					kernel, normal, Operation.DIVIDE, coef),
					Operation.PLUS, fv[1]);
			m.simplifyLeafs();
			fun = new Function(m, fv[0]);
			type = INEQUALITY_PARAMETRIC_Y;
		} else if (coefX != null && !AbstractKernel.isZero(coefX)
				&& !Double.isNaN(coefX)) {
			coef = new MyDouble(kernel, -coefX);
			isAboveBorder = coefX > 0;
			ExpressionNode m = new ExpressionNode(kernel, new ExpressionNode(
					kernel, normal, Operation.DIVIDE, coef),
					Operation.PLUS, fv[0]);
			m.simplifyLeafs();
			fun = new Function(m, fv[1]);
			type = INEQUALITY_PARAMETRIC_X;
		} else if (coefX != null && AbstractKernel.isZero(coefX) && coefY == null) {
			zeroDummy0 = new MyDouble(kernel, 0);
			normal.replaceAndWrap(fv[0], zeroDummy0 );
			init1varFunction(1);
			type = funBorder.isPolynomialFunction(false) ?				
				INEQUALITY_1VAR_Y:INEQUALITY_INVALID;
		} else if (coefY != null && AbstractKernel.isZero(coefY) && coefX == null) {
			zeroDummy1 = new MyDouble(kernel, 0);
			normal.replaceAndWrap(fv[1], zeroDummy1);
			init1varFunction(0);
			type = funBorder.isPolynomialFunction(false) ?				
					INEQUALITY_1VAR_X:INEQUALITY_INVALID;
		}

		else {						
			Polynomial xVar=new Polynomial(kernel,"x");
			Polynomial yVar=new Polynomial(kernel,"y");
			ExpressionNode replaced = 
					((ExpressionNode) normal.deepCopy(kernel)).replaceAndWrap(fv[0], xVar).
					replaceAndWrap(fv[1], yVar);
			Equation equ=new Equation(kernel,replaced,new MyDouble(kernel,0));				
			try{
				equ.initEquation();			
			}
			catch(Throwable t){
				type = INEQUALITY_INVALID;
				return;
			}
			Polynomial newBorder =  equ.getNormalForm();			
			if(newBorder.degree()<3){
				if (conicBorder == null)
					conicBorder = new GeoConic(kernel.getConstruction());
				//conicBorder.setLabel("res");
				conicBorder.setCoeffs(equ.getNormalForm().getCoeff());
				type = INEQUALITY_CONIC;
				border = (GeoElement)conicBorder;						
				setAboveBorderFromConic();	
			}
			else{
				throw new MyError(kernel.getApplication(), "InvalidEquation");
			}
			//TODO implicit ineq	
			/*if (newBorder.isGeoLine()) {
				type = INEQUALITY_CONIC;
				if (conicBorder == null)
					conicBorder = new GeoConic(kernel.getConstruction());				
				border = conicBorder;
			}}*/
		}
		if (type == INEQUALITY_PARAMETRIC_X || type == INEQUALITY_PARAMETRIC_Y) {
			funBorder = new GeoFunction(kernel.getConstruction());
			funBorder.setFunction(fun);
			if (type == INEQUALITY_PARAMETRIC_X) {
				funBorder.swapEval();
			}
		}
		if (funBorder != null)
			border = funBorder;
		if (isStrict()) {
			border.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		} else
			border.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);
	}

	private void setAboveBorderFromConic() {
		if (conicBorder.getType() == GeoConicNDConstants.CONIC_INTERSECTING_LINES ||
				conicBorder.getType() == GeoConicNDConstants.CONIC_EMPTY ||
				conicBorder.getType() == GeoConicNDConstants.CONIC_LINE) {
			   		isAboveBorder = true;
			   		return;
		}
		GeoVec2D midpoint = conicBorder.getTranslationVector();
		ExpressionNode normalCopy = (ExpressionNode) normal
				.deepCopy(kernel);
		double midX, midY;	
		if (conicBorder.getType() == GeoConicNDConstants.CONIC_PARABOLA){
			//TODO: replace with Eigenvec once GeoVec2D is ported
			midX = midpoint.getX()+conicBorder.getP()*conicBorder.getEigenvec(0).getX();
			midY = midpoint.getY()+conicBorder.getP()*conicBorder.getEigenvec(0).getY();
		} else {					
			midX = midpoint.getY();
			midY = midpoint.getX();
		} 
		normalCopy.replaceAndWrap(fv[0], new MyDouble(kernel, midX));
		normalCopy.replaceAndWrap(fv[1], new MyDouble(kernel, midY));
		double valAtCenter = ((NumberValue) normalCopy.evaluate())
				.getDouble();
		isAboveBorder = (valAtCenter < 0)
				^ (conicBorder.getType() == GeoConicNDConstants.CONIC_HYPERBOLA);		
	}
	
	private void init1varFunction(int varIndex) {
		Construction cons = kernel.getConstruction();
		boolean supress = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		funBorder = new GeoFunction(cons);
		funBorder.setFunction(new Function(normal, fv[varIndex]));		
		zeros = kernel.RootMultiple(null, funBorder);
		/*for(int i=0;i<zeros.length;i++){
			Application.debug(zeros[i]);
		}*/
		cons.setSuppressLabelCreation(supress);
		border = funBorder;
		if (isStrict()) {
			border.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		} else
			border.setLineType(EuclidianStyleConstants.LINE_TYPE_FULL);

	}

	/**
	 * Updates the coefficient k in y<k*f(x) for parametric, for implicit runs
	 * full update.
	 */
	public void updateCoef() {
		Double coefVal = null, otherVal = null;
		if (type == INEQUALITY_PARAMETRIC_Y) {
			coefVal = normal.getCoefficient(fv[1]);
			otherVal = normal.getCoefficient(fv[0]);

		} else if (type == INEQUALITY_PARAMETRIC_X) {
			coefVal = normal.getCoefficient(fv[0]);
			otherVal = normal.getCoefficient(fv[1]);
		}
		if (coefVal == null || coefVal == 0
				|| (otherVal != null && Math.abs(otherVal) > Math.abs(coefVal)))
			update();		
		else {
			isAboveBorder = coefVal > 0;
			coef.set(-coefVal);
		}

	}

	//TODO remove?
	/**
	 * @return implicit border
	 */
	/* public GeoImplicitPoly getImpBorder() {
		return impBorder;
	} */ 

	@Override
	final public String toString() {
		return "inequality";
	}

	/**
	 * @return true if strict
	 */
	public boolean isStrict() {
		return (op.equals(Operation.GREATER) || op.equals(Operation.LESS));
	}

	/**
	 * @return border for parametric equations
	 */
	public GeoFunction getFunBorder() {
		return funBorder;
	}

	/**
	 * Returns true for parametric ineqs like y>border(x), false for y<border(x)
	 * (for PARAMETRIC_X vars are swapped)
	 * 
	 * @return true for parametric ineqs like y>border(x), false for y<border(x)
	 * 
	 */
	public boolean isAboveBorder() {
		return isAboveBorder;
	}

	/**
	 * Returns border, which can be function, conic or implicit polynomial
	 * 
	 * @return border
	 */
	public GeoElement getBorder() {
		return border;
	}

	/**
	 * Returns type of ineq
	 * 
	 * @return one of {@link #INEQUALITY_IMPLICIT}
	 *         {@link #INEQUALITY_PARAMETRIC_X} {@link #INEQUALITY_PARAMETRIC_Y}
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the conicBorder
	 */
	public GeoConic getConicBorder() {
		return conicBorder;
	}

	/**
	 * @return zero points for 1var ineqs
	 */
	public GeoPoint2[] getZeros() {
		return zeros;
	}

} // end of class Equation
