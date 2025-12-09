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
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;

/**
 * Computes the area of a conic section
 * 
 * @author Markus Hohenwarter
 */
public class AlgoAreaConic extends AlgoElement {

	private GeoConicND conic; // input
	private GeoNumeric area; // output

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param c
	 *            conic
	 */
	public AlgoAreaConic(Construction cons, String label, GeoConicND c) {
		super(cons);
		this.conic = c;
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
		input[0] = conic;

		setOnlyOutput(area);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return area of the conic
	 */
	public GeoNumeric getArea() {
		return area;
	}

	// calc area of conic c
	@Override
	public final void compute() {
		if (!conic.isDefined()) {
			area.setUndefined();
			return;
		}

		// area of sector
		if (conic.isGeoConicPart()) {
			GeoConicPartND conicPart = (GeoConicPartND) conic;

			// added area for arcs, so just call getArea
			area.setValue(conicPart.getArea());
			/*
			 * int partType = conicPart.getConicPartType();
			 * 
			 * if (partType == GeoConicPart.CONIC_PART_SECTOR) { //
			 * circumference of sector area.setValue(conicPart.getValue()); }
			 * else { // area of arc is undefined area.setUndefined(); }
			 */

			return;
		}

		// standard case: area of conic
		int type = conic.getType();
		switch (type) {
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
		case GeoConicNDConstants.CONIC_CIRCLE:
			// r is length of one of the half axes
			double r = conic.getHalfAxis(0);
			area.setValue(r * r * Math.PI);
			break;

		case GeoConicNDConstants.CONIC_ELLIPSE:
			// lengths of the half axes
			double a = conic.getHalfAxis(0);
			double b = conic.getHalfAxis(1);
			area.setValue(a * b * Math.PI);
			break;

		default:
			area.setUndefined();
		}
	}

}
