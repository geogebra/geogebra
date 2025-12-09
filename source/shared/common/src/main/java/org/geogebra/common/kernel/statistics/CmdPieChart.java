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

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;

public class CmdPieChart extends CommandProcessor {
	public CmdPieChart(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) {
		if (c.getArgumentNumber() > 3 || c.getArgumentNumber() < 1) {
			throw argNumErr(c);
		}
		GeoElement[] args = resArgs(c, info);
		GeoNumberValue radius = null;
		GeoPoint center = null;
		if (args.length > 2) {
			radius = (GeoNumberValue) validate(args[2], args[2].isNumberValue(), c);
		}
		if (args.length > 1) {
			center = (GeoPoint) validate(args[1], args[1] instanceof GeoPoint, c);
		}
		GeoList data = (GeoList) validate(args[0], args[0].isGeoList(), c);
		AlgoPieChart algo = new AlgoPieChart(cons, data, center, radius);
		algo.getChart().setLabel(c.getLabel());
		return new GeoElement[] {algo.getChart()};
	}

}
