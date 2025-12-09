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
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.PolygonFactory;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * RigidPolygon[ &lt;GeoPoint&gt;, ..., &lt;GeoPoint&gt; ]
 * 
 * Extends CmdScripting so that preview is disabled (otherwise it doesn't work)
 */
public class CmdRigidPolygon extends CmdScripting {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRigidPolygon(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		arg = resArgs(c);
		switch (n) {

		case 1:
			if (arg[0].isGeoPolygon()) {

				EuclidianViewInterfaceCommon view = kernel.getApplication()
						.getActiveEuclidianView();

				double offset = view.toRealWorldCoordX(view.getWidth()) / 15;

				return new PolygonFactory(kernel).rigidPolygon(
						(GeoPolygon) arg[0],
						offset, -offset, c.getLabels());
			}

			throw argErr(c, arg[0]);
		case 0:
		case 2:
			throw argNumErr(c);

		case 3:
			if (arg[0].isGeoPolygon() && arg[1] instanceof GeoNumberValue
					&& arg[2] instanceof GeoNumberValue) {

				return new PolygonFactory(kernel).rigidPolygon(
						(GeoPolygon) arg[0], arg[1].evaluateDouble(),
						arg[2].evaluateDouble(), c.getLabels());
			}

			//$FALL-THROUGH$
		default:

			// polygon for given points
			GeoPointND[] points = new GeoPointND[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!arg[i].isGeoPoint()) {
					throw argErr(c, arg[i]);
				}
				points[i] = (GeoPointND) arg[i];
			}

			// everything ok
			return new PolygonFactory(kernel).rigidPolygon(c.getLabels(),
					points);
		}
	}

}
