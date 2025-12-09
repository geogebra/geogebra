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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdCircleArcSector;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Conic arc commands
 *
 */
public class CmdCircleArcSector3D extends CmdCircleArcSector {
	/**
	 * @param kernel
	 *            Kernel
	 * @param type
	 *            arc type
	 */
	public CmdCircleArcSector3D(Kernel kernel, int type) {
		super(kernel, type);
	}

	@Override
	protected GeoElement circleArcSector(String label, GeoPointND center,
			GeoPointND startPoint, GeoPointND endPoint) {

		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientation(kernel, app);
		if (orientation != null) {
			return (GeoElement) kernel.getManager3D().circleArcSector3D(label,
					center, startPoint, endPoint, orientation, type);
		}

		if (center.isGeoElement3D() || startPoint.isGeoElement3D()
				|| endPoint.isGeoElement3D()) {
			return (GeoElement) kernel.getManager3D().circleArcSector3D(label,
					center, startPoint, endPoint, type);
		}

		return super.circleArcSector(label, center, startPoint, endPoint);
	}

	@Override
	protected GeoElement[] process4(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		// arc center-two points, oriented
		if ((ok[0] = arg[0].isGeoPoint()) && (ok[1] = arg[1].isGeoPoint())
				&& (ok[2] = arg[2].isGeoPoint())
				&& (ok[3] = arg[3] instanceof GeoDirectionND)) {

			GeoElement[] ret = { (GeoElement) kernel.getManager3D()
					.circleArcSector3D(c.getLabel(), (GeoPointND) arg[0],
							(GeoPointND) arg[1], (GeoPointND) arg[2],
							(GeoDirectionND) arg[3], type) };
			return ret;
		}

		return null;
	}
}
