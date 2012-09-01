/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoRootsPolynomial;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.Operation;

/**
 * stores left and right hand side of an inequality as Expressions
 */
public class Inequality {

	/**
	 * Inequality type
	 */
	public enum IneqType {
	/** can be used e.g. by PointIn, but cannot be drawn */
	INEQUALITY_INVALID,

	/** x > f(y) */
	INEQUALITY_PARAMETRIC_X,
	/** y > f(x) */
	INEQUALITY_PARAMETRIC_Y,
	
	/** f(x,y) >0, f is linear */
	INEQUALITY_LINEAR,
	/** f(x,y) >0, f is quadratic */
	INEQUALITY_CONIC,
	/** f(x,y) >0, degree of f greater than 2 */
	INEQUALITY_IMPLICIT,
	
	/** inequality with one variable */
	INEQUALITY_1VAR_X,
	/** inequality with one variable, called y */
	INEQUALITY_1VAR_Y
	}
	private Operation op = Operation.LESS;
	private IneqType type;
	/*private GeoImplicitPoly impBorder;*/
	private GeoConic conicBorder;
	private GeoLine lineBorder;
	private GeoFunction funBorder;
	private GeoElement border;
	private Kernel kernel;
	private boolean isAboveBorder;
	private ExpressionNode normal;
	private FunctionVariable[] fv;
	private MyDouble coef;
	private GeoPoint[] zeros;	
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
	public Inequality(Kernel kernel, ExpressionValue lhs, ExpressionValue rhs,
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
				type = IneqType.INEQUALITY_INVALID;
			}
			else if (fv[0].toString(StringTemplate.defaultTemplate).equals("y")) {
				type = IneqType.INEQUALITY_1VAR_Y;
			} else
				type = IneqType.INEQUALITY_1VAR_X;

			return;
		}
		if(zeroDummy0!=null)
			normal.replace(zeroDummy0, fv[0]).wrap();
		if(zeroDummy1!=null)
			normal.replace(zeroDummy1, fv[1]).wrap();
		Double coefY = normal.getCoefficient(fv[1]);
		Double coefX = normal.getCoefficient(fv[0]);
		Function fun = null;
		if (coefY != null && !Kernel.isZero(coefY) && !Double.isNaN(coefY)
				&& coefX == null) {
			coef = new MyDouble(kernel, -coefY);
			isAboveBorder = coefY > 0;
			ExpressionNode m = new ExpressionNode(kernel, new ExpressionNode(
					kernel, normal, Operation.DIVIDE, coef),
					Operation.PLUS, fv[1]);
			m.simplifyLeafs();
			fun = new Function(m, fv[0]);
			type = IneqType.INEQUALITY_PARAMETRIC_Y;
		} else if (coefX != null && !Kernel.isZero(coefX)
				&& !Double.isNaN(coefX) && coefY == null) {
			coef = new MyDouble(kernel, -coefX);
			isAboveBorder = coefX > 0;
			ExpressionNode m = new ExpressionNode(kernel, new ExpressionNode(
					kernel, normal, Operation.DIVIDE, coef),
					Operation.PLUS, fv[0]);
			m.simplifyLeafs();
			fun = new Function(m, fv[1]);
			type = IneqType.INEQUALITY_PARAMETRIC_X;
		} else if (coefX != null && Kernel.isZero(coefX) && coefY == null) {
			zeroDummy0 = new MyDouble(kernel, 0);
			normal.replace(fv[0], zeroDummy0 ).wrap();
			init1varFunction(1);
			type = funBorder.isPolynomialFunction(false) ?				
				IneqType.INEQUALITY_1VAR_Y:IneqType.INEQUALITY_INVALID;
		} else if (coefY != null && Kernel.isZero(coefY) && coefX == null) {
			zeroDummy1 = new MyDouble(kernel, 0);
			normal.replace(fv[1], zeroDummy1).wrap();
			init1varFunction(0);
			type = funBorder.isPolynomialFunction(false) ?				
					IneqType.INEQUALITY_1VAR_X:IneqType.INEQUALITY_INVALID;
		}
		else {						
			Polynomial xVar=new Polynomial(kernel,"x");
			Polynomial yVar=new Polynomial(kernel,"y");
			ExpressionNode replaced = 
					((ExpressionNode) normal.deepCopy(kernel)).replace(fv[0], xVar).wrap().
					replace(fv[1], yVar).wrap();
			Equation equ=new Equation(kernel,replaced,new MyDouble(kernel,0));				
			try{
				equ.initEquation();			
			}
			catch(Throwable t){
				type = IneqType.INEQUALITY_INVALID;
				return;
			}
			Polynomial newBorder =  equ.getNormalForm();
			if(newBorder.degree()<2){
				if (lineBorder == null)
					lineBorder = new GeoLine(kernel.getConstruction());
				//conicBorder.setLabel("res");
				ExpressionValue[][] evs = equ.getNormalForm().getCoeff();
				lineBorder.setCoords(GeoConic.evalCoeff(evs, 1, 0),GeoConic.evalCoeff(evs, 0, 1),GeoConic.evalCoeff(evs, 0, 0));
				type = IneqType.INEQUALITY_LINEAR;
				border = lineBorder;						
				isAboveBorder = coefY < 0 || coefY==0.0 && coefX>0;
			}
			else if(newBorder.degree()==2){
				if (conicBorder == null)
					conicBorder = new GeoConic(kernel.getConstruction());
				//conicBorder.setLabel("res");
				conicBorder.setCoeffs(equ.getNormalForm().getCoeff());
				type = IneqType.INEQUALITY_CONIC;
				border = conicBorder;						
				setAboveBorderFromConic();	
			}
			else{
				type = IneqType.INEQUALITY_INVALID;
				return;
			}
			//TODO implicit ineq	
			/*if (newBorder.isGeoLine()) {
				type = IneqType.INEQUALITY_CONIC;
				if (conicBorder == null)
					conicBorder = new GeoConic(kernel.getConstruction());				
				border = conicBorder;
			}}*/
		}
		App.debug(type+":"+coefX+","+coefY);
		if (type == IneqType.INEQUALITY_PARAMETRIC_X || type == IneqType.INEQUALITY_PARAMETRIC_Y) {
			funBorder = new GeoFunction(kernel.getConstruction());
			funBorder.setFunction(fun);
			if (type == IneqType.INEQUALITY_PARAMETRIC_X) {
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
		isAboveBorder = conicBorder.evaluateInSignificantPoint()<0;		
	}
	
	private void init1varFunction(int varIndex) {
		Construction cons = kernel.getConstruction();
		boolean supress = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		//funBorder for inequality f(x)>g(x) is function f(x)-g(x)
		funBorder = new GeoFunction(cons);
		funBorder.setFunction(new Function(normal, fv[varIndex]));		
		zeros = RootMultiple(funBorder);
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

	final private static GeoPoint[] RootMultiple(GeoFunction f) {
		// allow functions that can be simplified to factors of polynomials
		if (!f.isPolynomialFunction(true))
			return null;

		AlgoRootsPolynomial algo = new AlgoRootsPolynomial(f);
		GeoPoint[] g = algo.getRootPoints();
		return g;
	}


	/**
	 * Updates the coefficient k in y<k*f(x) for parametric, for implicit runs
	 * full update.
	 */
	public void updateCoef() {
		Double coefVal = null, otherVal = null;
		if (type == IneqType.INEQUALITY_PARAMETRIC_Y) {
			coefVal = normal.getCoefficient(fv[1]);
			otherVal = normal.getCoefficient(fv[0]);

		} else if (type == IneqType.INEQUALITY_PARAMETRIC_X) {
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
	 * @return inequality type
	 */
	public IneqType getType() {
		return type;
	}

	/**
	 * @return the conicBorder
	 */
	public GeoConic getConicBorder() {
		return conicBorder;
	}
	
	/**
	 * @return the lineBorder
	 */
	public GeoLine getLineBorder() {
		return lineBorder;
	}

	/**
	 * @return zero points for 1var ineqs
	 */
	public GeoPoint[] getZeros() {
		return zeros;
	}

} // end of class Equation
