/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * Computes the area of a polygon
 * 
 * @author mathieu
 */
public class AlgoAreaPolygon extends AlgoElement
		implements SymbolicParametersBotanaAlgo {

	private GeoPolygon polygon; // input
	private GeoNumeric area; // output

	private PVariable[] botanaVars;

	/**
	 * @param cons
	 *            construction
	 * @param polygon
	 *            polygon
	 */
	public AlgoAreaPolygon(Construction cons, GeoPolygon polygon) {
		super(cons);
		this.polygon = polygon;
		area = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
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

		setOnlyOutput(area);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return output area
	 */
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

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo)
			throws NoSymbolicParametersException {
		GeoPointND[] pointsOfPolygon = polygon.getPoints();
		if (botanaVars == null) {
			botanaVars = new PVariable[pointsOfPolygon.length * 2];
			for (int i = 0; i < pointsOfPolygon.length; i++) {
				PVariable[] currentPointBotanavars = ((GeoPoint) pointsOfPolygon[i])
						.getBotanaVars(pointsOfPolygon[i]);
				botanaVars[2 * i] = currentPointBotanavars[0];
				botanaVars[2 * i + 1] = currentPointBotanavars[1];
			}
		}
		return botanaVars;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		return null;
	}

}
