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

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoDependentVector3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;

/**
 * Plane command
 *
 */
public class CmdPlane extends CommandProcessor {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdPlane(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			if (c.getArgument(0).unwrap() instanceof Equation) {
				((Equation) c.getArgument(0).unwrap()).setForcePlane();
			}
			arg = resArgs(c, info);
			if (arg[0] instanceof GeoCoordSys2D) {
				GeoElement[] ret = { (GeoElement) kernel.getManager3D()
						.plane3D(c.getLabel(), (GeoCoordSys2D) arg[0]) };
				return ret;
			}

			throw argErr(c, arg[0]);
		case 2:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1] instanceof GeoLineND)) {
				GeoElement[] ret = { (GeoElement) kernel.getManager3D()
						.plane3D(c.getLabel(), (GeoPointND) arg[0],
								(GeoLineND) arg[1]) };
				return ret;
			} else if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1] instanceof GeoCoordSys2D)) {
				GeoElement[] ret = { (GeoElement) kernel.getManager3D()
						.plane3D(c.getLabel(), (GeoPointND) arg[0],
								(GeoCoordSys2D) arg[1]) };
				return ret;
			} else if ((ok[0] = arg[0].isGeoLine())
					&& (ok[1] = arg[1].isGeoLine())) {
				GeoElement[] ret = { kernel.getManager3D().plane3D(
						c.getLabel(), (GeoLineND) arg[0], (GeoLineND) arg[1]) };
				return ret;

			} else {
				if (!ok[0]) {
					throw argErr(c, arg[0]);
				}
				throw argErr(c, arg[1]);
			}

		case 3:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2].isGeoPoint())) {
				GeoElement[] ret = { kernel.getManager3D().plane3D(
						c.getLabel(), (GeoPointND) arg[0], (GeoPointND) arg[1],
						(GeoPointND) arg[2]) };
				return ret;
			}

			// implement Plane[(1,2,3),Vector[(3,-3,1)],Vector[(2,2,-1)]] as
			// shortcut/macro for
			// PerpendicularPlane[(1,2,3),Vector[(3,-3,1)]\u2297Vector[(2,2,-1)]]
			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoVector())
					&& (ok[2] = arg[2].isGeoVector())) {
				ExpressionNode cross = new ExpressionNode(kernel, arg[1],
						Operation.VECTORPRODUCT, arg[2]);

				AlgoDependentVector3D algo = new AlgoDependentVector3D(cons,
						cross);

				return new GeoElement[] { (GeoElement) kernel.getManager3D()
						.orthogonalPlane3D(c.getLabel(), (GeoPointND) arg[0],
								algo.getVector3D()) };
			}

			if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else {
				throw argErr(c, arg[2]);
			}

		default:
			throw argNumErr(c);
		}

	}

}
