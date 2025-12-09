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

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Pan
 */
public class CmdPan extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPan(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;

		switch (n) {
		case 2:
			GeoElement[] arg = resArgs(c);
			if ((ok = arg[0].isGeoNumeric()) && arg[1].isGeoNumeric()) {

				GeoNumeric x = (GeoNumeric) arg[0];
				GeoNumeric y = (GeoNumeric) arg[1];
				EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
				ev.rememberOrigins();
				ev.translateCoordSystemInPixels((int) x.getDouble(),
						-(int) y.getDouble(), 0);

				return arg;
			} else if (!ok) {
				throw argErr(c, arg[0]);
			} else {
				throw argErr(c, arg[1]);
			}
		case 3:
			arg = resArgs(c);
			if ((ok = arg[0].isGeoNumeric()) && arg[1].isGeoNumeric()
					&& arg[2].isGeoNumeric()) {

				GeoNumeric x = (GeoNumeric) arg[0];
				GeoNumeric y = (GeoNumeric) arg[1];
				GeoNumeric z = (GeoNumeric) arg[2];
				EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
				ev.rememberOrigins();
				ev.translateCoordSystemInPixels((int) x.getDouble(),
						-(int) y.getDouble(), (int) z.getDouble());

				return arg;
			} else if (!ok) {
				throw argErr(c, arg[0]);
			} else {
				throw argErr(c, arg[1]);
			}

		default:
			throw argNumErr(c);
		}
	}
}
