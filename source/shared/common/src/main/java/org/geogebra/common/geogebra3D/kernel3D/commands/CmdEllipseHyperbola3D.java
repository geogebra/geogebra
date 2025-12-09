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

package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoConicFociLength3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoEllipseFociLength3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoHyperbolaFociLength3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdEllipseHyperbola;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Ellipse and Hyperbola command processor
 *
 */
public class CmdEllipseHyperbola3D extends CmdEllipseHyperbola {

	/**
	 * @param kernel
	 *            kernel
	 * @param type
	 *            conic type
	 */
	public CmdEllipseHyperbola3D(Kernel kernel, final int type) {
		super(kernel, type);
	}

	@Override
	protected GeoElement ellipse(String label, GeoPointND a, GeoPointND b,
			GeoPointND c) {

		if (a.isGeoElement3D() || b.isGeoElement3D() || c.isGeoElement3D()) {
			return kernel.getManager3D().ellipseHyperbola3D(label, a, b, c,
					type);
		}

		return super.ellipse(label, a, b, c);
	}

	@Override
	protected GeoElement[] process4(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		if ((ok[0] = arg[0].isGeoPoint()) && (ok[1] = arg[1].isGeoPoint())
				&& (ok[2] = arg[2].isGeoPoint())
				&& (ok[3] = arg[3] instanceof GeoDirectionND)) {

			GeoElement[] ret = { kernel.getManager3D().ellipseHyperbola3D(
					c.getLabel(), (GeoPointND) arg[0], (GeoPointND) arg[1],
					(GeoPointND) arg[2], (GeoDirectionND) arg[3], type) };
			return ret;
		}

		if ((ok[0] = arg[0].isGeoPoint()) && (ok[1] = arg[1].isGeoPoint())
				&& (ok[2] = arg[2] instanceof GeoNumberValue)
				&& (ok[3] = arg[3] instanceof GeoDirectionND)) {

			GeoElement[] ret = { ellipseHyperbola(c.getLabel(),
					(GeoPointND) arg[0], (GeoPointND) arg[1],
					(GeoNumberValue) arg[2], (GeoDirectionND) arg[3]) };
			return ret;
		}

		return null;
	}

	private final GeoElement ellipseHyperbola(String label, GeoPointND a,
			GeoPointND b, GeoNumberValue v, GeoDirectionND orientation) {

		// check if all 2D
		if (!a.isGeoElement3D() && !b.isGeoElement3D()
				&& orientation == kernel.getXOYPlane()) {
			return super.ellipseHyperbola(label, a, b, v);
		}

		// use 3D algo with orientation
		AlgoConicFociLength3D algo;
		if (type == GeoConicNDConstants.CONIC_HYPERBOLA) {
			algo = new AlgoHyperbolaFociLength3D(kernel.getConstruction(),
					label, a, b, v, orientation);
		} else { // ellipse
			algo = new AlgoEllipseFociLength3D(kernel.getConstruction(), label,
					a, b, v, orientation);
		}
		return algo.getConic();
	}

	@Override
	protected GeoElement ellipseHyperbola(String label, GeoPointND a,
			GeoPointND b, GeoNumberValue v) {

		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientationNoSpace(kernel, app);
		if (orientation == null) {
			if (a.isGeoElement3D() || b.isGeoElement3D()) {
				orientation = kernel.getXOYPlane();
			} else {
				// use 2D algo
				return super.ellipseHyperbola(label, a, b, v);
			}
		}

		return ellipseHyperbola(label, a, b, v, orientation);
	}
}
