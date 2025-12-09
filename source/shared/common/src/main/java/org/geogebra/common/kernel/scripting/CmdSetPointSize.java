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

package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.kernelND.GeoPolyhedronInterface;
import org.geogebra.common.main.MyError;

/**
 * SetPointSize
 */
public class CmdSetPointSize extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetPointSize(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		switch (n) {
		case 2:
			GeoElement[] arg = resArgs(c);

			boolean ok = false;
			if (arg[1].isNumberValue()) {
				ok = true;
				double size = arg[1].evaluateDouble();
				if (arg[0] instanceof PointProperties) {

					if (size > 0) {
						arg[0].setEuclidianVisibleIfNoConditionToShowObject(
								true);
						((PointProperties) arg[0]).setPointSize((int) size);
					} else {
						arg[0].setEuclidianVisibleIfNoConditionToShowObject(
								false);
					}
					arg[0].updateVisualStyleRepaint(GProperty.COMBINED);

					return arg;
				}

				if (arg[0] instanceof GeoPolyhedronInterface) {
					GeoPolyhedronInterface poly = (GeoPolyhedronInterface) arg[0];
					poly.setPointSizeOrVisibility((int) size);
					return arg;
				}

				if (arg[0].isGeoPolygon()) {
					GeoPolygon poly = (GeoPolygon) arg[0];
					poly.setPointSizeOrVisibility((int) size);
					return arg;
				}

				if (arg[0].isGeoNumeric()) {
					GeoNumeric poly = (GeoNumeric) arg[0];
					poly.setSliderBlobSize(size);
					poly.updateVisualStyleRepaint(GProperty.COMBINED);
					return arg;
				}
			}

			if (!ok) {
				throw argErr(c, arg[1]);
			}

			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
