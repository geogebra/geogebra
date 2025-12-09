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

import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.DoubleUtil;

/**
 * ZoomOut
 */
public class CmdZoomOut extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdZoomOut(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			GeoElement[] arg = resArgs(c);
			if (arg[0].isGeoNumeric()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];

				EuclidianViewInterfaceSlim ev = app.getActiveEuclidianView();
				double px = ev.getWidth() / 2.0; // mouseLoc.x;
				double py = ev.getHeight() / 2.0; // mouseLoc.y;

				double factor = numGeo.getDouble();

				if (DoubleUtil.isZero(factor)) {
					throw argErr(c, arg[0]);
				}

				ev.zoom(px, py, 1 / factor, 4, true);

				app.setUnsaved();

				return arg;

			}
			throw argErr(c, arg[0]);

		case 2:
			arg = resArgs(c);
			return CmdZoomIn.zoomIn2(arg, c,
					1 / arg[0].evaluateDouble(), this);

		default:
			throw argNumErr(c);
		}
	}
}
