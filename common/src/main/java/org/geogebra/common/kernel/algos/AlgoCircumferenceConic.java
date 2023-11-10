/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;

/**
 * Algorithm to compute the circumference of a
 * {@link org.geogebra.common.kernel.geos.GeoConic GeoConic}.
 * 
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 * @author Markus Hohenwarter
 */
public class AlgoCircumferenceConic extends AlgoElement {

	// Take a conic as input
	private GeoConicND conic;

	// Output is a GeoNumeric (= a number)
	private GeoNumeric circum;

	// Helper to calculate circumference for ellipse

	/**
	 * @param cons
	 *            construction
	 * @param conic
	 *            conic
	 */
	public AlgoCircumferenceConic(Construction cons, GeoConicND conic) {
		super(cons);
		this.conic = conic;

		circum = new GeoNumeric(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Circumference;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = conic;

		setOnlyOutput(circum);
		setDependencies();
	}

	/**
	 * Compute circumference. In order to do so we have to distinguishe between
	 * the following cases:
	 * 
	 * <pre>
	 *    a) conic is a circle
	 *    b) conic is an ellipse
	 * </pre>
	 * 
	 * For all other cases circumference is undefined.
	 */
	@Override
	public final void compute() {
		if (!conic.isDefined()) {
			circum.setUndefined();
		}

		// conic type
		int type = conic.getType();

		// circumference of sector
		if (conic.isGeoConicPart()) {
			GeoConicPartND conicPart = (GeoConicPartND) conic;
			int partType = conicPart.getConicPartType();
			if (type == GeoConicNDConstants.CONIC_CIRCLE
					&& partType == GeoConicNDConstants.CONIC_PART_SECTOR) {
				/*
				 * value of sector is area: area = r*r * paramExtent / 2;
				 * arclength = r * paramExtent; arclength = area * 2/r;
				 */
				double area = conicPart.getParameters().getValue();
				double r = conic.getHalfAxis(0);
				double arclength = area * 2.0 / r;

				// circumference of sector
				circum.setValue(arclength + 2 * r);
			} else if (type == GeoConicNDConstants.CONIC_CIRCLE
					&& partType == GeoConicNDConstants.CONIC_PART_ARC) {
				// value of arc is curved length
				double arclength = conicPart.getParameters().getValue();
				double r = conic.getHalfAxis(0);
				double angle = conicPart.getParameterExtent();

				// return circumference of **segment**
				// ie curved + straight
				circum.setValue(arclength + 2.0 * r * Math.sin(angle / 2));
			} else {
				// circumference of ellipse sector is undefined
				// note: circumference of ellipse sector is simply not
				// implemented yet
				circum.setUndefined();
			}

			return;
		}

		// standard case: conic
		switch (type) {
		case GeoConicNDConstants.CONIC_CIRCLE:
			// r is length of one of the half axes
			double r = conic.getHalfAxis(0);
			circum.setValue(2 * r * Math.PI);
			break;

		case GeoConicNDConstants.CONIC_ELLIPSE:

			// new, more accurate method
			// https://jira.geogebra.org/browse/GGB-692
			circum.setValue(conic.getEllipseCircumference());

			break;

		default:
			circum.setUndefined();
		}
	}

	/**
	 * Get the GeoConics's circumference.
	 * 
	 * @return circumference
	 */
	public GeoNumeric getCircumference() {
		return circum;
	}

}
