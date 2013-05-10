/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Parametric;
import geogebra.common.kernel.arithmetic.Polynomial;
import geogebra.common.kernel.arithmetic3D.Vector3DValue;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.commands.CommandDispatcher;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.AlgoLinePointVector3D;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoVector3D;


public class AlgebraProcessor3D extends AlgebraProcessor {
	
	

	public AlgebraProcessor3D(Kernel kernel,CommandDispatcher cd) {
		super(kernel,cd);
	}
	
	
	
	/** creates 3D point or 3D vector
	 * @param n
	 * @param evaluate
	 * @return 3D point or 3D vector
	 */	
	@Override
	protected GeoElement[] processPointVector3D(
			ExpressionNode n,
			ExpressionValue evaluate) {
		String label = n.getLabel();		

		double[] p = ((Vector3DValue) evaluate).getPointAsDouble();

		GeoElement[] ret = new GeoElement[1];
		boolean isIndependent = n.isConstant();

		// make vector, if label begins with lowercase character
		if (label != null) {
			if (!(n.isForcedPoint() || n.isForcedVector())) { // may be set by MyXMLHandler
				if (Character.isLowerCase(label.charAt(0)))
					n.setForceVector();
				else
					n.setForcePoint();
			}
		}
		
		boolean isVector = n.isVectorValue();
		
		
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

		return ret;
	}

	
	@Override
	protected void checkNoTermsInZ(Equation equ){
		if (!equ.getNormalForm().isFreeOf('z'))
			equ.setForcePlane();
	}
	
	@Override
	protected GeoElement[] processLine(Equation equ) {
		
		if (equ.isForcedLine())
			return super.processLine(equ);
		
		//check if the equ is forced plane or if the 3D view has the focus
		if (equ.isForcedPlane() ||
				kernel.getApplication().getActiveEuclidianView() instanceof EuclidianView3D){
			return processPlane(equ);
		}
		return super.processLine(equ);
		
	}

	/**
	 * @param equ equation to process
	 * @return resulting plane
	 */
	protected GeoElement[] processPlane(Equation equ) {
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
			plane = (GeoPlane3D) kernel.getManager3D().Plane3D(label, a, b, c, d);
		} else
			plane = (GeoPlane3D) kernel.getManager3D().DependentPlane3D(label, equ);

		ret[0] = plane;
		return ret;
	}

	@Override
	// eg g: X = (-5, 5, 2) + t (4, -3, -2)
	protected GeoElement[] processParametric(Parametric par)
			throws MyError {

		// point and vector are created silently
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// get point
		ExpressionNode node = par.getP();
		node.setForcePoint();
		GeoElement[] temp = processExpressionNode(node);
		GeoPoint3D P = (GeoPoint3D) temp[0];

		// get vector
		node = par.getv();
		node.setForceVector();
		temp = processExpressionNode(node);
		GeoVector3D v = (GeoVector3D) temp[0];

		// switch back to old mode
		cons.setSuppressLabelCreation(oldMacroMode);

		// Line through P with direction v
		GeoLine3D line = Line3D(par.getLabel(), P, v);

		line.setToParametric(par.getParameter());
		line.updateRepaint();
		GeoElement[] ret = { line };
		return ret;
	}
	
	/**
	 * Line named label through Point P with direction of vector v
	 */
	final public GeoLine3D Line3D(String label, GeoPoint3D P, GeoVector3D v) {
		AlgoLinePointVector3D algo = new AlgoLinePointVector3D(cons, label, P, v);
		GeoLine3D g = algo.getLine();
		return g;
	}


	
	
	
}
