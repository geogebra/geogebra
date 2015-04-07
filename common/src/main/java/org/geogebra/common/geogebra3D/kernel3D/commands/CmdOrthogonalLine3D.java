package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSpace;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdOrthogonalLine;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/*
 * Orthogonal[ <GeoPoint3D>, <GeoCoordSys> ]
 */
public class CmdOrthogonalLine3D extends CmdOrthogonalLine {

	public CmdOrthogonalLine3D(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1] instanceof GeoCoordSys2D))) {
				GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
						.OrthogonalLine3D(c.getLabel(), (GeoPointND) arg[0],
								(GeoCoordSys2D) arg[1]) };
				return ret;
			} else if (((ok[0] = (arg[0].isGeoPoint())) && (ok[1] = (arg[1] instanceof GeoLineND)))) {

				// check if there is an active view with orientation
				GeoDirectionND orientation = CommandProcessor3D
						.getCurrentViewOrientation(kernelA, app);
				if (orientation != null) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.OrthogonalLine3D(c.getLabel(),
									(GeoPointND) arg[0], (GeoLineND) arg[1],
									orientation) };
					return ret;
				}

				// check if there is a 3D geo: then use 3D algo
				if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.OrthogonalLine3D(c.getLabel(),
									(GeoPointND) arg[0], (GeoLineND) arg[1]) };
					return ret;
				}

				// else use 2D algo
				GeoElement[] ret = { getAlgoDispatcher().OrthogonalLine(
						c.getLabel(), (GeoPoint) arg[0], (GeoLine) arg[1]) };
				return ret;

			} else if (((ok[0] = (arg[0].isGeoPoint())) && (ok[1] = (arg[1]
					.isGeoVector())))) {

				// check if there is an active view with orientation
				GeoDirectionND orientation = CommandProcessor3D
						.getCurrentViewOrientation(kernelA, app);
				if (orientation == null || orientation == kernelA.getSpace()) {
					orientation = kernelA.getXOYPlane();
				}

				if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()
						|| orientation != kernelA.getXOYPlane()) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.OrthogonalLine3D(c.getLabel(),
									(GeoPointND) arg[0], (GeoVectorND) arg[1],
									orientation) };
					return ret;
				}

				// 2D geos and xOy plane for direction : use 2D algo
				GeoElement[] ret = { getAlgoDispatcher().OrthogonalLine(
						c.getLabel(), (GeoPoint) arg[0], (GeoVector) arg[1]) };
				return ret;

			} else if (((ok[0] = (arg[0] instanceof GeoLineND)) && (ok[1] = (arg[1] instanceof GeoLineND)))) {
				GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
						.OrthogonalLine3D(c.getLabel(), (GeoLineND) arg[0],
								(GeoLineND) arg[1]) };
				return ret;
			}
			break;

		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1] instanceof GeoDirectionND))
					&& (ok[2] = (arg[2] instanceof GeoDirectionND)
					// "space" not allowed as 2nd arg
							&& !(arg[1] instanceof GeoSpace)
							// check if it's not 2 planes (or plane-"space")
							&& !((arg[1] instanceof GeoCoordSys2D) && (arg[2] instanceof GeoCoordSys2D || arg[2] instanceof GeoSpace)))) {
				GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
						.OrthogonalLine3D(c.getLabel(), (GeoPointND) arg[0],
								(GeoDirectionND) arg[1],
								(GeoDirectionND) arg[2]) };
				return ret;
			}

			break;
		}

		return super.process(c);
	}

}
