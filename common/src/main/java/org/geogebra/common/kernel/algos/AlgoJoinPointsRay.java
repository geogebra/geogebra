/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoJoinPoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 *
 * @author Markus
 */
public class AlgoJoinPointsRay extends AlgoElement
		implements SymbolicParametersBotanaAlgo {

	private GeoPoint P; // input
	private GeoPoint Q; // input
	private GeoRay ray; // output

	private PVariable[] botanaVars;

	/** Creates new AlgoJoinPoints */
	public AlgoJoinPointsRay(Construction cons, String label, GeoPoint P,
			GeoPoint Q) {
		super(cons);
		this.P = P;
		this.Q = Q;
		ray = new GeoRay(cons, P);
		ray.setEndPoint(Q);

		setInputOutput(); // for AlgoElement

		// compute line through P, Q
		compute();

		addIncidence();

		// note: GeoRay's equation form is initialized from construction defaults
		EquationBehaviour equationBehaviour = kernel.getEquationBehaviour();
		if (equationBehaviour != null) {
			ray.setEquationForm(equationBehaviour.getRayCommandEquationForm());
		}

		ray.setLabel(label);
	}

	/**
	 * @author Tam
	 * 
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	private void addIncidence() {
		P.addIncidence(ray, true);
		Q.addIncidence(ray, true);
	}

	@Override
	public Commands getClassName() {
		return Commands.Ray;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_RAY;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = P;
		input[1] = Q;

		setOnlyOutput(ray);
		setDependencies(); // done by AlgoElement
	}

	public GeoRay getRay() {
		return ray;
	}

	public GeoPoint getP() {
		return P;
	}

	public GeoPoint getQ() {
		return Q;
	}

	// calc the line g through P and Q
	@Override
	public final void compute() {
		// g = P v Q <=> g_n : n = P x Q
		// g = cross(P, Q)
		GeoVec3D.lineThroughPoints(P, Q, ray);
	}

	@Override
	final public String toString(StringTemplate tpl) {

		// Michael Borcherds 2008-03-31
		// simplified to allow better translation
		return getLoc().getPlainDefault("RayThroughAB", "Ray through %0, %1",
				P.getLabel(tpl), Q.getLabel(tpl));

	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (botanaVars != null) {
			return botanaVars;
		}
		botanaVars = SymbolicParameters.addBotanaVarsJoinPoints(input);
		return botanaVars;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		return null;
	}
}
