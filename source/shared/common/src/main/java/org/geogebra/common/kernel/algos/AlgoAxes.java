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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 *
 * @author Markus
 */
public class AlgoAxes extends AlgoAxesQuadricND {

	private GeoPoint P;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 */
	AlgoAxes(Construction cons, String label, GeoConic c) {
		super(cons, label, c);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param c
	 *            conic
	 */
	public AlgoAxes(Construction cons, String[] labels, GeoConic c) {
		super(cons, labels, c);
	}

	@Override
	protected void createInput() {
		axes = new GeoLine[2];
		axes[0] = new GeoLine(cons);
		axes[1] = new GeoLine(cons);

		P = new GeoPoint(cons);
		((GeoLine) axes[0]).setStartPoint(P);
		((GeoLine) axes[1]).setStartPoint(P);
	}

	// calc axes
	@Override
	public final void compute() {

		super.compute();

		P.setCoords(((GeoConic) c).getB().getX(), ((GeoConic) c).getB().getY(), 1.0);
	}

	@Override
	protected void setAxisCoords(int i) {
		GeoLine axis = (GeoLine) axes[i];
		axis.x = -((GeoConic) c).eigenvec[i].getY();
		axis.y = ((GeoConic) c).eigenvec[i].getX();
		axis.z = -(axis.x * ((GeoConic) c).getB().getX()
				+ axis.y * ((GeoConic) c).getB().getY());

	}

}
