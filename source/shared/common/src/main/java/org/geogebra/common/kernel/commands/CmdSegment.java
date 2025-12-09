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

package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Segment[ &lt;GeoPoint&gt;, &lt;GeoPoint&gt; ]
 * 
 * Segment[ &lt;GeoPoint&gt;, &lt;Number&gt; ]
 */
public class CmdSegment extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSegment(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c, info);

			// segment between two points
			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoPoint())) {
				GeoElement[] ret = { segment(c.getLabel(), (GeoPointND) arg[0],
						(GeoPointND) arg[1]) };
				return ret;
			}

			// segment from point with given length
			else if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {
				return getAlgoDispatcher().segment(c.getLabels(),
						(GeoPointND) arg[0], (GeoNumberValue) arg[1]);
			} else {
				if (!ok[0]) {
					throw argErr(c, arg[0]);
				}
				throw argErr(c, arg[1]);
			}

		case 3: // special case for Segment[A,B,poly1] -> do nothing!
			arg = resArgs(c, info);

			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2].isGeoPolygon())) {
				GeoElement[] ret = {};
				return ret;
			}
			throw argNumErr(c);

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @param label
	 *            label
	 * @param a
	 *            first point
	 * @param b
	 *            second point
	 * @return segment [ab]
	 */
	protected GeoElement segment(String label, GeoPointND a, GeoPointND b) {
		if (app.isWhiteboardActive()) {
			AlgoPolyLine algo = new AlgoPolyLine(cons, new GeoPointND[]{a, b});
			algo.getPoly().setLabel(label);
			return algo.getPoly();
		}
		return getAlgoDispatcher().segment(label, (GeoPoint) a, (GeoPoint) b);
	}
}
