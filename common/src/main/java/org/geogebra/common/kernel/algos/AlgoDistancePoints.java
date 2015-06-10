/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDistancePoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoDistancePoints extends AlgoElement implements DistanceAlgo,
		SymbolicParametersBotanaAlgo {

	private GeoPointND P, Q; // input
	private GeoNumeric dist; // output

	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;

	public AlgoDistancePoints(Construction cons, GeoPointND P, GeoPointND Q) {
		super(cons);
		this.P = P;
		this.Q = Q;
		dist = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		// compute length
		compute();
	}

	public AlgoDistancePoints(Construction cons, String label, GeoPointND P,
			GeoPointND Q) {
		this(cons, P, Q);
		dist.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Distance;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_DISTANCE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) P;
		input[1] = (GeoElement) Q;

		super.setOutputLength(1);
		super.setOutput(0, dist);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getDistance() {
		return dist;
	}

	/*
	 * GeoPoint getP() { return P; } GeoPoint getQ() { return Q; }
	 */

	// calc length of vector v
	@Override
	public final void compute() {
		dist.setValue(P.distance(Q));
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("DistanceOfAandB", P.getLabel(tpl),
				Q.getLabel(tpl));

	}

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {

		GeoPoint P1 = (GeoPoint) P;
		GeoPoint P2 = (GeoPoint) Q;
		
		if (P1 != null && P2 != null) {
			
			Variable[] vP1 = P1.getBotanaVars(P1);
			Variable[] vP2 = P2.getBotanaVars(P2);
			
			if (botanaVars == null) {
				botanaVars = new Variable[4];
				botanaVars[0] = vP1[0];
				botanaVars[1] = vP1[1];
				botanaVars[2] = vP2[0];
				botanaVars[3] = vP2[1];

			}

			return null;
		}
		throw new NoSymbolicParametersException();

	}

	// TODO Consider locusequability
}
