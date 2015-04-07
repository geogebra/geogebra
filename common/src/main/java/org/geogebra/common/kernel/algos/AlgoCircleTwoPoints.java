/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoCircleTwoPoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoCircleTwoPoints extends AlgoSphereNDTwoPoints implements
		SymbolicParametersBotanaAlgo {

	private Variable[] botanaVars;

	public AlgoCircleTwoPoints(Construction cons, GeoPoint M, GeoPoint P) {
		super(cons, M, P);
		setIncidence();
	}

	public AlgoCircleTwoPoints(Construction cons, String label, GeoPoint M,
			GeoPoint P) {
		super(cons, label, M, P);
		setIncidence();
	}

	private void setIncidence() {
		((GeoPoint) getP()).addIncidence(getCircle(), false);
	}

	@Override
	protected GeoQuadricND createSphereND(Construction cons) {
		GeoConic circle = new GeoConic(cons);
		circle.addPointOnConic(getP()); // TODO do this in AlgoSphereNDTwoPoints
		return circle;
	}

	@Override
	public Commands getClassName() {
		return Commands.Circle;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_CIRCLE_TWO_POINTS;
	}

	public GeoConic getCircle() {
		return (GeoConic) getSphereND();
	}

	/*
	 * GeoPoint getM() { return M; } GeoPoint getP() { return P; }
	 */

	// compute circle with midpoint M and radius r
	/*
	 * public final void compute() { circle.setCircle(M, P); }
	 */

	public Variable[] getBotanaVars(GeoElement geo) {
		if (botanaVars == null) {
			Variable[] circle1vars = new Variable[2];
			Variable[] centerVars = new Variable[2];

			GeoElement P = (GeoElement) getP();
			GeoElement M = (GeoElement) getM();
			circle1vars = ((SymbolicParametersBotanaAlgo) P).getBotanaVars(P);
			centerVars = ((SymbolicParametersBotanaAlgo) M).getBotanaVars(M);

			botanaVars = new Variable[4];
			// Center:
			botanaVars[0] = centerVars[0];
			botanaVars[1] = centerVars[1];
			// Point on the circle:
			botanaVars[2] = circle1vars[0];
			botanaVars[3] = circle1vars[1];
		}
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {
		// It's OK to return null here since no constraint must be set:
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnCircleTwoPoints(geo, this, scope);
	}

	// ///////////////////////////////
	// TRICKS FOR XOY PLANE
	// ///////////////////////////////

	@Override
	protected int getInputLengthForXML() {
		return getInputLengthForXMLMayNeedXOYPlane();
	}

	@Override
	protected int getInputLengthForCommandDescription() {
		return getInputLengthForCommandDescriptionMayNeedXOYPlane();
	}

	@Override
	public GeoElement getInput(int i) {
		return getInputMaybeXOYPlane(i);
	}

	@Override
	final public String toString(StringTemplate tpl) {

		return getLoc().getPlain("CircleThroughAwithCenterB",
				((GeoElement) getP()).getLabel(tpl),
				((GeoElement) getM()).getLabel(tpl));
	}
}
