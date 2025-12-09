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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoElement3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3DConstant;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSpace;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.commands.CmdOrthogonalLine;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
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

/**
 * Orthogonal[ &lt;GeoPoint3D&gt;, &lt;GeoCoordSys&gt; ]
 */
public class CmdOrthogonalLine3D extends CmdOrthogonalLine {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdOrthogonalLine3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process2(Command c, GeoElement[] arg) throws MyError {
		boolean[] ok = new boolean[2];
		if ((ok[0] = arg[0].isGeoPoint())
				&& (ok[1] = arg[1] instanceof GeoCoordSys2D)) {
			GeoElement[] ret = { (GeoElement) kernel.getManager3D()
					.orthogonalLine3D(c.getLabel(), (GeoPointND) arg[0],
							(GeoCoordSys2D) arg[1]) };
			return ret;
		} else if ((ok[0] = arg[0].isGeoPoint())
				&& (ok[1] = arg[1] instanceof GeoLineND)) {

			// check if there is an active view with orientation
			GeoDirectionND orientation = CommandProcessor3D
					.getCurrentViewOrientation(kernel, app);
			if (orientation != null) {
				GeoElement[] ret = { (GeoElement) kernel.getManager3D()
						.orthogonalLine3D(c.getLabel(), (GeoPointND) arg[0],
								(GeoLineND) arg[1], orientation) };
				return ret;
			}

			// check if there is a 3D geo: then use 3D algo
			if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()) {
				GeoElement[] ret = { (GeoElement) kernel.getManager3D()
						.orthogonalLine3D(c.getLabel(), (GeoPointND) arg[0],
								(GeoLineND) arg[1]) };
				return ret;
			}

			// else use 2D algo
			GeoElement[] ret = { getAlgoDispatcher().orthogonalLine(
					c.getLabel(), (GeoPoint) arg[0], (GeoLine) arg[1]) };
			return ret;

		} else if ((ok[0] = arg[0].isGeoPoint())
				&& (ok[1] = arg[1].isGeoVector())) {

			// check if there is an active view with orientation
			GeoDirectionND orientation = CommandProcessor3D
					.getCurrentViewOrientation(kernel, app);
			if (orientation == null || orientation == kernel.getSpace()) {
				orientation = kernel.getXOYPlane();
			}

			if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()
					|| orientation != kernel.getXOYPlane()) {
				GeoElement[] ret = { (GeoElement) kernel.getManager3D()
						.orthogonalLine3D(c.getLabel(), (GeoPointND) arg[0],
								(GeoVectorND) arg[1], orientation) };
				return ret;
			}

			// 2D geos and xOy plane for direction : use 2D algo
			GeoElement[] ret = { getAlgoDispatcher().orthogonalLine(
					c.getLabel(), (GeoPoint) arg[0], (GeoVector) arg[1]) };
			return ret;

		} else if ((ok[0] = arg[0] instanceof GeoLineND)
				&& (ok[1] = arg[1] instanceof GeoLineND)) {
			GeoElement[] ret = { (GeoElement) kernel.getManager3D()
					.orthogonalLine3D(c.getLabel(), (GeoLineND) arg[0],
							(GeoLineND) arg[1]) };
			return ret;
		}

		return super.process2(c, arg);
	}

	@Override
	public GeoElement[] process3(Command c, EvalInfo info) throws MyError {

		ExpressionValue arg0 = c.getArgument(0).unwrap();
		ExpressionValue arg1 = c.getArgument(1).unwrap();
		ExpressionValue arg2 = c.getArgument(2).unwrap();

		boolean threeD = arg0 instanceof GeoElement3D
				|| arg1 instanceof GeoElement3D;

		// if arg2 = xOyPlane then we can just handle as a 2D command
		if (!threeD && arg0 instanceof GeoPlane3DConstant) {
			c.setArgument(2,
					new GeoDummyVariable(cons,
							arg2.toString(StringTemplate.defaultTemplate))
									.wrap());
			return process2(c, resArgs(c, info));
		}

		boolean[] ok = new boolean[3];
		GeoElement[] arg;

		arg = resArgs(c, info);
		if ((ok[0] = arg[0].isGeoPoint())
				&& (ok[1] = arg[1] instanceof GeoDirectionND)
				&& (ok[2] = (arg[2] instanceof GeoDirectionND)
						// "space" not allowed as 2nd arg
						&& !(arg[1] instanceof GeoSpace)
						// check if it's not 2 planes (or plane-"space")
						&& !((arg[1] instanceof GeoCoordSys2D)
								&& (arg[2] instanceof GeoCoordSys2D
										|| arg[2] instanceof GeoSpace)))) {
			GeoElement[] ret = { (GeoElement) kernel.getManager3D()
					.orthogonalLine3D(c.getLabel(), (GeoPointND) arg[0],
							(GeoDirectionND) arg[1], (GeoDirectionND) arg[2]) };
			return ret;
		}

		throw argErr(c, getBadArg(ok, arg));
	}

}
