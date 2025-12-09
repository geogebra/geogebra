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

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoLocusable;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;

public class AlgoFirstLocus extends AlgoFirst {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputLocus
	 *            locus
	 * @param n
	 *            number of points
	 */
	public AlgoFirstLocus(Construction cons, String label,
			GeoLocusable inputLocus,
			GeoNumeric n) {
		super(cons, label, inputLocus.toGeoElement(), n);
	}

	@Override
	public Commands getClassName() {
		return Commands.First;
	}

	@Override
	public final void compute() {

		ArrayList<? extends MyPoint> points = ((GeoLocusable) inputList)
				.getPoints();

		size = points.size();
		int outsize = n == null ? 1 : (int) n.getDouble();

		if (!inputList.isDefined() || size == 0 || outsize < 0
				|| outsize > size) {
			outputList.setUndefined();
			return;
		}

		outputList.setDefined(true);

		if (outsize == 0) {
			outputList.clear();
			return; // return empty list
		}

		int outputListSize = outputList.size();

		// remove extra elements
		if (outputList.size() > outsize) {
			for (int i = outputListSize - 1; i >= outsize; i--) {
				outputList.remove(i);
			}
		}

		// avoid label creation

		for (int i = 0; i < outsize; i++) {
			MyPoint mp = points.get(i);

			if (i < outputList.size()) {
				// recycle existing GeoPoint
				// important to avoid memory problems
				// (and quicker?)
				GeoPoint p = (GeoPoint) outputList.get(i);
				p.setCoords(mp.x, mp.y, 1.0);
				p.updateRepaint();
			} else {
				outputList.addPoint(mp.x, mp.y, 1.0, null);
			}
		}

	}

}
