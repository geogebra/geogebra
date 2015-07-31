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
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 * Computes the area of a polygon
 * 
 * @author mathieu
 * @version
 */
public class AlgoAreaPolygon extends AlgoElement implements SymbolicParametersBotanaAlgo {

	private GeoPolygon polygon; // input
	private GeoNumeric area; // output
	
	private Variable[] botanaVars;
	private Polynomial[] botanaPolynomials;

	public AlgoAreaPolygon(Construction cons, String label, GeoPolygon polygon) {
		super(cons);
		this.polygon = polygon;
		area = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();

		area.setLabel(label);
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
		input = new GeoElement[1];
		input[0] = polygon;

		setOutputLength(1);
		setOutput(0, area);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getArea() {
		return area;
	}

	// calc area of conic c
	@Override
	public final void compute() {
		/*
		 * if (!polygon.isDefined()) { area.setUndefined(); return; }
		 */

		area.setValue(polygon.getArea());
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		GeoPointND[] pointsOfPolygon = polygon.getPoints();
		if (botanaVars == null) {
			botanaVars = new Variable[pointsOfPolygon.length * 2];
			for (int i = 0; i < pointsOfPolygon.length; i++) {
				Variable[] currentPointBotanavars = ((GeoPoint) pointsOfPolygon[i])
						.getBotanaVars((GeoPoint) pointsOfPolygon[i]);
				botanaVars[2 * i] = currentPointBotanavars[0];
				botanaVars[2 * i + 1] = currentPointBotanavars[1];
			}
		}
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {
			return null;
	}
	

	// TODO Consider locusequability

}
