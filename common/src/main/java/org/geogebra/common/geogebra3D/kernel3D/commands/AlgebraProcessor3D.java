/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVec4D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.ParametricProcessor;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * 3D expression processor
 *
 */
public class AlgebraProcessor3D extends AlgebraProcessor {

	/**
	 * @param kernel
	 *            kernel
	 * @param cd
	 *            commands dispatcher
	 */
	public AlgebraProcessor3D(Kernel kernel, CommandDispatcher cd) {
		super(kernel, cd);
	}

	/**
	 * creates 3D point or 3D vector
	 * 
	 * @param n
	 *            point expression
	 * @param evaluate
	 *            evaluated expression
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
	protected GeoElement[] processLine(Equation equ, ExpressionNode def) {

		if (equ.isForcedLine())
			return super.processLine(equ, def);

		// check if the equ is forced plane or if the 3D view has the focus
		if (equ.isForcedPlane() || kernel.isParsingFor3D()) {
			return processPlane(equ, def);
		}
		return super.processLine(equ, def);

	}

	@Override
	protected GeoElement[] processConic(Equation equ, ExpressionNode def) {

		if (equ.isForcedConic())
			return super.processConic(equ, def);

		// check if the equ is forced plane or if the 3D view has the focus
		if (equ.isForcedQuadric()
				|| kernel.getApplication().getActiveEuclidianView()
						.isEuclidianView3D()) {
			return processQuadric(equ, def);
		}
		return super.processConic(equ, def);

	}

	private GeoElement[] processQuadric(Equation equ, ExpressionNode def) {
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
		quadric.setDefinition(def);
		ret[0] = quadric;
		return ret;
	}

	/**
	 * @param equ
	 *            equation to process
	 * @return resulting plane
	 */
	private GeoElement[] processPlane(Equation equ, ExpressionNode def) {
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
			plane.setDefinition(def);
		} else
			plane = (GeoPlane3D) kernel.getManager3D().DependentPlane3D(label,
					equ);

		ret[0] = plane;
		return ret;
	}

	@Override
	public ParametricProcessor getParamProcessor() {
		if (this.paramProcessor == null) {
			paramProcessor = new ParametricProcessor3D(kernel, this);
		}
		return paramProcessor;
	}



}
