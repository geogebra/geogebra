package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdLine;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/*
 * Line[ <GeoPoint3D>, <GeoPoint3D> ] or CmdLine
 */
public class CmdLine3D extends CmdLine {

	public CmdLine3D(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {

		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		if (n == 2) {
			arg = resArgs(c);
			if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()) {

				GeoElement geo0 = arg[0];
				GeoElement geo1 = arg[1];

				if ((ok[0] = (geo0.isGeoPoint()))
						&& (ok[1] = (geo1.isGeoPoint()))) { // line between two
															// 3D points
					GeoElement[] ret = { kernelA.getManager3D().Line3D(
							c.getLabel(), (GeoPointND) geo0, (GeoPointND) geo1) };
					return ret;
				} else if ((ok[0] = (geo0.isGeoPoint()))
						&& (ok[1] = (geo1.isGeoVector()))) { // line directed
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.Line3D(c.getLabel(), (GeoPointND) geo0,
									(GeoVectorND) geo1) };
					return ret;

				} else if ((ok[0] = (geo0.isGeoPoint()))
						&& (ok[1] = (geo1 instanceof GeoLineND))) { // line
																	// parallel
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.Line3D(c.getLabel(), (GeoPointND) geo0,
									(GeoLineND) geo1) };
					return ret;
				}
			}
		}

		return super.process(c);
	}

}
