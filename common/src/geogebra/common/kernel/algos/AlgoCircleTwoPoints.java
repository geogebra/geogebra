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

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.elements.EquationCircleTwoPoints;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;

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
		((GeoPoint) getP()).addIncidence(getCircle());
	}

	@Override
	protected GeoQuadricND createSphereND(Construction cons) {
		GeoConic circle = new GeoConic(cons);
		circle.addPointOnConic(getP()); // TODO do this in AlgoSphereNDTwoPoints
		return circle;
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoCircleTwoPoints;
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

	@Override
	final public String toString(StringTemplate tpl) {

		return app.getPlain("CircleThroughAwithCenterB",
				((GeoElement) getP()).getLabel(tpl),
				((GeoElement) getM()).getLabel(tpl));

	}
	
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
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return new EquationCircleTwoPoints(element, scope);
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

}
