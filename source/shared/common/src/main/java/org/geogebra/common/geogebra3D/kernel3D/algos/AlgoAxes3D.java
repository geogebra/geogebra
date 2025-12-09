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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoAxesQuadricND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author Markus
 */
public class AlgoAxes3D extends AlgoAxesQuadricND {
	private Coords midpoint;

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param c
	 *            quadric
	 */
	public AlgoAxes3D(Construction cons, String[] labels, GeoQuadricND c) {
		super(cons, labels, c);
	}

	@Override
	protected void createInput() {
		int d = c.getDimension();
		axes = new GeoLine3D[d];
		for (int i = 0; i < d; i++) {
			axes[i] = new GeoLine3D(cons);
		}

	}

	// calc axes
	@Override
	public final void compute() {

		midpoint = c.getMidpoint3D();

		super.compute();

	}

	@Override
	protected void setAxisCoords(int i) {
		GeoLine3D axis = (GeoLine3D) axes[i];
		axis.setCoord(midpoint, c.getEigenvec3D(i));

	}

}
