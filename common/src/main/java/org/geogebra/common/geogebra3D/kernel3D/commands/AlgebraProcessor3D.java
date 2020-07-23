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
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.AlgoDependentImplicitSurface;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.GeoImplicitSurface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.commands.ParametricProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.StringUtil;

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
		int mode = ((Vector3DValue) evaluate).getToStringMode();
		if (evaluate instanceof MyVecNDNode) {
			// force vector for CAS vectors GGB-1492
			if (((MyVecNDNode) evaluate).isCASVector()) {
				n.setForceVector();
			}
		}
		GeoElement[] ret = new GeoElement[1];
		boolean isIndependent = n.isConstant();

		// make vector, if label begins with lowercase character
		if (label != null) {
			if (!(n.isForcedPoint() || n.isForcedVector())) { // may be set by
																// MyXMLHandler
				if (StringUtil.isLowerCase(label.charAt(0))) {
					n.setForceVector();
				} else {
					n.setForcePoint();
				}
			}
		}

		boolean isVector = n.shouldEvaluateToGeoVector();

		if (isIndependent) {
			// get coords
			double x = p[0];
			double y = p[1];
			double z = p[2];
			if (isVector) {
				ret[0] = kernel.getManager3D().vector3D(x, y, z);
			} else {
				ret[0] = kernel.getManager3D().point3D(x, y, z, false)
						.toGeoElement();
			}
			ret[0].setDefinition(n);

		} else {
			if (isVector) {
				ret[0] = kernel.getManager3D().dependentVector3D(n);
			} else {
				ret[0] = kernel.getManager3D().dependentPoint3D(n, true)
						.toGeoElement();
			}

		}

		if (mode == Kernel.COORD_SPHERICAL) {
			((GeoVec4D) ret[0]).setMode(Kernel.COORD_SPHERICAL);
		}
		ret[0].setLabel(label);
		return ret;
	}

	@Override
	protected void checkNoTermsInZ(Equation equ) {

		if (equ.containsZ()) {
			switch (equ.degree()) {
			case 0:
			case 1:
				equ.setForcePlane();
				break;
			case 2:
				equ.setForceQuadric();
				break;
			default:
				equ.setForceSurface();
				break;
			}
		}

	}

	@Override
	protected GeoElement[] processLine(Equation equ, ExpressionNode def,
			EvalInfo info) {

		if (equ.isForcedLine() && !equ.containsFreeFunctionVariable("z")) {
			return super.processLine(equ, def, info);
		}

		// check if the equ is forced plane or if the 3D view has the focus
		if (equ.isForcedPlane() || kernel.isParsingFor3D()) {
			return processPlane(equ, def, info);
		}
		return super.processLine(equ, def, info);

	}

	@Override
	public GeoElement[] processConic(Equation equ, ExpressionNode def,
			EvalInfo info) {

		if (equ.isForcedConic()) {
			return super.processConic(equ, def, info);
		}

		// check if the equ is forced plane or if the 3D view has the focus
		if (equ.isForcedQuadric() || kernel.getApplication()
				.getActiveEuclidianView().isEuclidianView3D()) {
			return processQuadric(equ, def, info);
		}
		return super.processConic(equ, def, info);

	}

	private GeoElement[] processQuadric(Equation equ, ExpressionNode def,
			EvalInfo info) {
		double xx = 0, yy = 0, zz = 0, xy = 0, xz = 0, yz = 0, x = 0, y = 0,
				z = 0, c = 0;
		GeoElement[] ret = new GeoElement[1];
		GeoQuadric3D quadric;
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();

		boolean isIndependent = lhs.isConstant(info);

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
			quadric = new GeoQuadric3D(cons, coeffs);
		} else {
			quadric = (GeoQuadric3D) kernel.getManager3D()
					.dependentQuadric3D(equ);
		}
		quadric.setDefinition(def);
		quadric.showUndefinedInAlgebraView(true);
		if (quadric.getType() == GeoQuadricNDConstants.QUADRIC_SPHERE) {
			quadric.setToSpecific();
		} else {
			quadric.setToImplicit();
		}
		setEquationLabelAndVisualStyle(quadric, label, info);
		ret[0] = quadric;
		return ret;
	}

	/**
	 * @param equ
	 *            equation to process
	 * @return resulting plane
	 */
	private GeoElement[] processPlane(Equation equ, ExpressionNode def,
			EvalInfo info) {
		double a = 0, b = 0, c = 0, d = 0;
		GeoPlane3D plane = null;
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();

		boolean isIndependent = lhs.isConstant(info);

		if (isIndependent) {
			// get coefficients
			a = lhs.getCoeffValue("x");
			b = lhs.getCoeffValue("y");
			c = lhs.getCoeffValue("z");
			d = lhs.getCoeffValue("");
			plane = (GeoPlane3D) kernel.getManager3D().plane3D(a, b, c, d);
			plane.setDefinition(def);
		} else {
			plane = (GeoPlane3D) kernel.getManager3D().dependentPlane3D(equ);
		}
		setEquationLabelAndVisualStyle(plane, label, info);

		return array(plane);
	}

	@Override
	public ParametricProcessor getParamProcessor() {
		if (this.paramProcessor == null) {
			paramProcessor = new ParametricProcessor3D(kernel, this);
		}
		return paramProcessor;
	}

	@Override
	public GeoElement[] processImplicitPoly(Equation equ,
			ExpressionNode definition, EvalInfo info) {

		if (app.has(Feature.IMPLICIT_SURFACES) || equ.isForcedQuadric() || equ.isForcedPlane()) {
			Polynomial lhs = equ.getNormalForm();
			boolean isIndependent = !equ.isFunctionDependent()
					&& lhs.isConstant(info) && !equ.hasVariableDegree();

			if (kernel.getApplication().getActiveEuclidianView()
					.isEuclidianView3D() || equ.isForcedSurface()
					|| equ.isForcedPlane() || equ.isForcedQuadric()) {
				GeoElement geo = null;
				if (isIndependent) {
					geo = new GeoImplicitSurface(cons, equ);
				} else {
					AlgoElement surfaceAlgo = new AlgoDependentImplicitSurface(
							cons, equ);
					geo = surfaceAlgo.getOutput(0);
				}
				geo.setDefinition(definition);
				geo.setLabel(equ.getLabel());
				return new GeoElement[] { geo };
			}
		}

		return super.processImplicitPoly(equ, definition, info);
	}

}
