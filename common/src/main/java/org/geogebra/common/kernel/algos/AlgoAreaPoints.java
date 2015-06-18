/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

public class AlgoAreaPoints extends AlgoElement implements
		SymbolicParametersBotanaAlgo {

	protected GeoPointND[] P; // input
	protected GeoNumeric area; // output

	private Variable[] botanaVars;
	private Polynomial[][] botanaPolynomials;

	public AlgoAreaPoints(Construction cons, String label, GeoPointND[] P) {
		this(cons, P);
		area.setLabel(label);
	}

	AlgoAreaPoints(Construction cons, GeoPointND[] P) {
		super(cons);
		this.P = P;
		createOutput(cons);
		setInputOutput(); // for AlgoElement

		// compute
		initCoords();
		compute();
	}

	/**
	 * init Coords values
	 */
	protected void initCoords() {
		// none here
	}

	protected void createOutput(Construction cons) {
		area = new GeoNumeric(cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.Area;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_AREA;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[P.length];
		for (int i = 0; i < P.length; i++) {
			input[i] = (GeoElement) P[i];
		}

		setOutputLength(1);
		setOutput(0, area);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getArea() {
		return area;
	}

	GeoPointND[] getPoints() {
		return P;
	}

	// calc area of polygon P[0], ..., P[n]
	// angle in range [0, pi]
	@Override
	public void compute() {
		area.setValue(Math.abs(AlgoPolygon.calcAreaWithSign(P)));
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		if (botanaVars == null) {
			GeoPoint A = (GeoPoint) P[0];
			GeoPoint B = (GeoPoint) P[1];
			GeoPoint C = (GeoPoint) P[2];
			Variable[] vA = A.getBotanaVars(A);
			Variable[] vB = B.getBotanaVars(B);
			Variable[] vC = C.getBotanaVars(C);
			botanaVars = new Variable[6];
			botanaVars[0] = vA[0];
			botanaVars[1] = vA[1];
			botanaVars[2] = vB[0];
			botanaVars[3] = vB[1];
			botanaVars[4] = vC[0];
			botanaVars[5] = vC[1];
		}
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO Consider locusequability

}
