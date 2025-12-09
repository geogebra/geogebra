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
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.MyError;

/**
 * LineBisector[ &lt;GeoPoint&gt;, &lt;GeoPoint&gt; ]
 * 
 * LineBisector[ &lt;GeoSegment&gt; ]
 */
public class CmdLineBisector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLineBisector(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1: // segment
			arg = resArgs(c, info);
			// line through point orthogonal to segment
			if (arg[0].isGeoSegment()) {
				GeoElement[] ret = {
						lineBisector(c.getLabel(), (GeoSegmentND) arg[0]) };
				return ret;
			}

			// syntax error
			throw argErr(c, arg[0]);

		case 2: // two points
			arg = resArgs(c, info);

			return process2(c, arg, ok);

		case 3:
			arg = resArgs(c, info);

			GeoElement[] ret = process3(c, arg, ok);

			return ret;

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * process line bisector when 2 arguments
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            arguments
	 * @param ok
	 *            ok array
	 * @return result (if one)
	 * @throws MyError
	 *             arg error
	 */
	protected GeoElement[] process2(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		// line through point orthogonal to vector
		if ((ok[0] = arg[0].isGeoPoint())
				&& (ok[1] = arg[1].isGeoPoint())) {
			GeoElement[] ret = { lineBisector(c.getLabel(), (GeoPointND) arg[0],
					(GeoPointND) arg[1]) };
			return ret;
		}

		// syntax error
		throw argErr(c, getBadArg(ok, arg));
	}

	/**
	 * process line bisector when 3 arguments
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            arguments
	 * @param ok
	 *            ok array
	 * @return result (if one)
	 * @throws MyError
	 *             in 2D, not possible with 3 args
	 */
	protected GeoElement[] process3(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {
		throw argNumErr(c);
	}

	/**
	 * @param label
	 *            label
	 * @param segment
	 *            segment
	 * @return perpendicular bisector
	 */
	protected GeoElement lineBisector(String label, GeoSegmentND segment) {
		return getAlgoDispatcher().lineBisector(label, (GeoSegment) segment);
	}

	/**
	 * @param label
	 *            label
	 * @param a
	 *            first point
	 * @param b
	 *            second point
	 * @return perpendicular bisector
	 */
	protected GeoElement lineBisector(String label, GeoPointND a,
			GeoPointND b) {
		return getAlgoDispatcher().lineBisector(label, (GeoPoint) a,
				(GeoPoint) b);
	}
}
