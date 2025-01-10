package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * Cylinder command processor (for finite cylinder)
 *
 */
public class CmdCylinder extends CommandProcessor {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdCylinder(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {

		case 2:
			arg = resArgs(c);

			if ((ok[0] = (arg[0] instanceof GeoConicND))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))) {
				return kernel.getManager3D().cylinderLimited(c.getLabels(),
						(GeoConicND) arg[0], (GeoNumberValue) arg[1]);
			} else if ((ok[0] = (arg[0] instanceof GeoLineND))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))) {
				GeoElement[] ret = {
						kernel.getManager3D().cylinder(c.getLabel(),
								(GeoLineND) arg[0], (GeoNumberValue) arg[1]) };
				return ret;
			}

			if (!ok[0]) {
				throw argErr(arg[0], c);
			}
			throw argErr(arg[1], c);

		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoVector()))
					&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
				GeoElement[] ret = { kernel.getManager3D().cylinder(
						c.getLabel(), (GeoPointND) arg[0], (GeoVectorND) arg[1],
						(GeoNumberValue) arg[2]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
				return cylinderPointPointRadius(c, (GeoPointND) arg[0],
						(GeoPointND) arg[1], (GeoNumberValue) arg[2]);
			} else {
				if (!ok[0]) {
					throw argErr(arg[0], c);
				} else if (!ok[1]) {
					throw argErr(arg[1], c);
				} else {
					throw argErr(arg[2], c);
				}
			}

		default:
			throw argNumErr(c);
		}

	}

	// overridden by CmdCylinderInfinite

	/**
	 * @param c
	 *            command
	 * @param p1
	 *            bottom center
	 * @param p2
	 *            top center
	 * @param r
	 *            radius
	 * @return cylinder
	 */
	protected GeoElement[] cylinderPointPointRadius(Command c, GeoPointND p1,
			GeoPointND p2, GeoNumberValue r) {
		return kernel.getManager3D().cylinderLimited(c.getLabels(), p1, p2, r);
	}

}
