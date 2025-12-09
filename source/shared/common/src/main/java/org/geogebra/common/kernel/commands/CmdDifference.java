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

package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.main.MyError;

/**
 * Difference[&lt;polygon&gt;, &lt;polygon&gt;]
 * 
 * @author thilina
 *
 */
public class CmdDifference extends CommandProcessor {

	/**
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDifference(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		int argumentNo = c.getArgumentNumber();
		boolean[] ok = { false, false, false };
		GeoElement[] arg = resArgs(c, info);

		switch (argumentNo) {
		case 2:
			if ((ok[0] = arg[0] instanceof GeoPolygon)
					&& (ok[1] = arg[1] instanceof GeoPolygon)) {
				if (arg[0].isGeoElement3D() && arg[1].isGeoElement3D()) {
					return difference3D(c.getLabels(), (GeoPolygon) arg[0], (GeoPolygon) arg[1]);
				}
				return difference(c.getLabels(), (GeoPolygon) arg[0],
						(GeoPolygon) arg[1]);
			}
			throw argErr(c, getBadArg(ok, arg));
		case 3:
			if ((ok[0] = arg[0] instanceof GeoPolygon)
					&& (ok[1] = arg[1] instanceof GeoPolygon)
					&& (ok[2] = arg[2] instanceof GeoBoolean)) {
				if (arg[0].isGeoElement3D() && arg[1].isGeoElement3D()) {
					return difference3D(c.getLabels(), (GeoPolygon) arg[0], (GeoPolygon) arg[1],
							(GeoBoolean) arg[2]);
				}
				return difference(c.getLabels(), (GeoPolygon) arg[0],
						(GeoPolygon) arg[1], (GeoBoolean) arg[2]);
			}

			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}

	}

	/**
	 * returns the output polygon after polygon difference operation
	 * 
	 * @param labels
	 *            labels for output
	 * @param poly1
	 *            input polygon 1
	 * @param poly2
	 *            input polygon 2
	 * @return resulting polygons
	 */
	protected GeoElement[] difference(String[] labels, GeoPolygon poly1,
			GeoPolygon poly2) {
		return getAlgoDispatcher().difference(labels, poly1, poly2);
	}

	/**
	 * returns the output polygon after polygon difference operation
	 * 
	 * @param labels
	 *            labels for output
	 * @param poly1
	 *            input polygon3D 1
	 * @param poly2
	 *            input polygon3D 2
	 * @return resulting polygons
	 */
	protected GeoElement[] difference3D(String[] labels, GeoPolygon poly1, GeoPolygon poly2) {
		return kernel.getManager3D().differencePolygons(labels, poly1, poly2);
	}

	/**
	 * returns the output polygon after polygon exclusive OR/difference (XOR)
	 * operation
	 * 
	 * @param labels
	 *            labels for output
	 * @param poly1
	 *            input polygon 1
	 * @param poly2
	 *            input polygon 2
	 * @param exclusive
	 *            input GeoBoolean indicating XOR or not
	 * @return resulting polygons
	 */
	protected GeoElement[] difference(String[] labels, GeoPolygon poly1,
			GeoPolygon poly2, GeoBoolean exclusive) {
		return getAlgoDispatcher().difference(labels, poly1, poly2, exclusive);
	}

	/**
	 * returns the output polygon after polygon exclusive OR/difference (XOR)
	 * operation
	 * 
	 * @param labels
	 *            labels for output
	 * @param poly1
	 *            input polygon3D 1
	 * @param poly2
	 *            input polygon3D 2
	 * @param exclusive
	 *            input GeoBoolean indicating exclusive difference or not
	 * @return resulting polygons
	 */
	protected GeoElement[] difference3D(String[] labels, GeoPolygon poly1, GeoPolygon poly2,
			GeoBoolean exclusive) {
		return kernel.getManager3D().differencePolygons(labels, poly1, poly2,
				exclusive);
	}

}