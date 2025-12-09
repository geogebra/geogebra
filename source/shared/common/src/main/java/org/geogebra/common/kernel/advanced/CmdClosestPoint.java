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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.algos.AlgoClosestPoint;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * ClosestPoint[Point,Path] ClosestPoint[Path,Point]
 */
public class CmdClosestPoint extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdClosestPoint(Kernel kernel) {
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
			if (!arg[0].isGeoElement3D() && !arg[1].isGeoElement3D()) {
				return process2D(c, arg);
			}

			if (arg[0].isPath() && arg[1].isGeoPoint()) {
				return new GeoElement[] { kernel.getManager3D().closestPoint(
						c.getLabel(), (Path) arg[0], (GeoPointND) arg[1]) };
			}

			if (arg[1].isPath() && arg[0].isGeoPoint()) {
				return new GeoElement[] { kernel.getManager3D().closestPoint(
						c.getLabel(), (Path) arg[1], (GeoPointND) arg[0]) };
			}

			if (arg[0].isGeoLine() || arg[1].isGeoLine()) {

				return new GeoElement[] { kernel.getManager3D().closestPoint(
						c.getLabel(), (GeoLineND) arg[0], (GeoLineND) arg[1]) };
			}

			if ((ok[0] = arg[0].isRegion()) && (ok[1] = arg[1].isGeoPoint())) {
				return new GeoElement[] { (GeoElement) kernel.getManager3D()
						.closestPoint(c.getLabel(), (Region) arg[0],
								(GeoPointND) arg[1]) };
			}

			if ((ok[1] = arg[1].isRegion()) && (ok[0] = arg[0].isGeoPoint())) {
				return new GeoElement[] { (GeoElement) kernel.getManager3D()
						.closestPoint(c.getLabel(), (Region) arg[1],
								(GeoPointND) arg[0]) };
			}

			// syntax error
			else {
				throw argErr(c, getBadArg(ok, arg));
			}

		default:
			throw argNumErr(c);
		}
	}

	private GeoElement[] process2D(Command c, GeoElement[] arg) {
		// distance between two points
		boolean[] ok = new boolean[2];
		if ((ok[0] = arg[0] instanceof Path)
				&& (ok[1] = arg[1].isGeoPoint())) {
			GeoElement[] ret = { closestPoint(c.getLabel(), (Path) arg[0],
					(GeoPointND) arg[1]) };
			return ret;
		}

		// distance between point and line
		else if ((ok[1] = arg[1] instanceof Path)
				&& (ok[0] = arg[0].isGeoPoint())) {
			GeoElement[] ret = { closestPoint(c.getLabel(), (Path) arg[1],
					(GeoPointND) arg[0]) };
			return ret;
		}

		else if ((ok[1] = arg[1] instanceof GeoLine)
				&& (ok[0] = arg[0] instanceof GeoLine)) {
			GeoElement[] ret = {
					new AlgoClosestPointLines(kernel.getConstruction(),
							c.getLabel(), (GeoLine) arg[1],
							(GeoLine) arg[0]).getOutput(0) };
			return ret;
		}
		throw argErr(c, getBadArg(ok, arg));
	}

	/** Point anywhere on path with */
	private GeoPoint closestPoint(String label, Path path, GeoPointND p) {
		AlgoClosestPoint algo = new AlgoClosestPoint(cons, path, p);
		algo.getP().setLabel(label);
		return (GeoPoint) algo.getP();
	}
}
