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
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Ellipse[ &lt;GeoPoint&gt;, &lt;GeoPoint&gt;, &lt;NumberValue&gt; ]
 */
public class CmdEllipseHyperbola extends CommandProcessor {
	/** ellipse or hyperbola, eg GeoConicNDConstants.CONIC_HYPERBOLA */
	final protected int type;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 * @param type
	 *            ellipse/hyperbola
	 */
	public CmdEllipseHyperbola(Kernel kernel, final int type) {
		super(kernel);
		this.type = type;
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {
				return new GeoElement[] {
						ellipseHyperbola(c.getLabel(), (GeoPointND) arg[0],
								(GeoPointND) arg[1], (GeoNumberValue) arg[2]) };
			} else if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2].isGeoPoint())) {
				GeoElement[] ret = { ellipse(c.getLabel(), (GeoPointND) arg[0],
						(GeoPointND) arg[1], (GeoPointND) arg[2]) };
				return ret;
			} else {
				throw argErr(c, getBadArg(ok, arg));
			}

		case 4:
			arg = resArgs(c, info);

			GeoElement[] ret = process4(c, arg, ok);

			return ret;

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @param label
	 *            label
	 * @param a
	 *            first focus
	 * @param b
	 *            second focus
	 * @param c
	 *            point on ellipse
	 * @return ellipse
	 */
	protected GeoElement ellipse(String label, GeoPointND a, GeoPointND b,
			GeoPointND c) {
		return getAlgoDispatcher().ellipseHyperbola(label, a, b, c, type);
	}

	/**
	 * process when 4 arguments
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            arguments
	 * @param ok
	 *            ok array
	 * @return result (if one)
	 * @throws MyError
	 *             in 2D, not possible with 4 args
	 */
	protected GeoElement[] process4(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {
		throw argNumErr(c);
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param a
	 *            first focus
	 * @param b
	 *            second focus
	 * @param v
	 *            value
	 * @return ellipse/hypebola
	 */
	protected GeoElement ellipseHyperbola(String label, GeoPointND a,
			GeoPointND b, GeoNumberValue v) {
		if (type == GeoConicNDConstants.CONIC_HYPERBOLA) {
			return getAlgoDispatcher().hyperbola(label, a, b, v);
		}
		return getAlgoDispatcher().ellipse(label, a, b, v);
	}
}
