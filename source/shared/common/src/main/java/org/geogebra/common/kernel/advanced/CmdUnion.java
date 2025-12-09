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
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPolygon3DInterface;
import org.geogebra.common.main.MyError;

/**
 * Union
 */
public class CmdUnion extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUnion(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 2:

			if (arg[0].isGeoList() && arg[1].isGeoList()) {

				AlgoUnion algo = new AlgoUnion(cons,
						(GeoList) arg[0], (GeoList) arg[1]);
				algo.getResult().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (arg[0].isGeoPolygon() && arg[1].isGeoPolygon()) {
				if (arg[0] instanceof GeoPolygon3DInterface
						&& arg[1] instanceof GeoPolygon3DInterface) {
					return union3D(c.getLabels(), (GeoPoly) arg[0],
							(GeoPoly) arg[1]);
				}
				return union(c.getLabels(), (GeoPolygon) arg[0],
						(GeoPolygon) arg[1]);
			} else {
				throw argErr(c, arg[0]);
			}

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * returns the output polygon after polygon union operation
	 * 
	 * @param labels
	 *            labels for output
	 * @param poly1
	 *            input polygon 1
	 * @param poly2
	 *            input polygon 2
	 * @return resulting polygons
	 */
	protected GeoElement[] union(String[] labels, GeoPolygon poly1,
			GeoPolygon poly2) {
		return getAlgoDispatcher().union(labels, poly1, poly2);
	}

	/**
	 * returns the output polygon after polygon union operation
	 * 
	 * @param labels
	 *            labels for output
	 * @param poly1
	 *            input polygon3D 1
	 * @param poly2
	 *            input polygon3D 2
	 * @return resulting polygons
	 */
	protected GeoElement[] union3D(String[] labels, GeoPoly poly1,
			GeoPoly poly2) {
		return kernel.getManager3D().unionPolygons(labels, poly1, poly2);
	}
}