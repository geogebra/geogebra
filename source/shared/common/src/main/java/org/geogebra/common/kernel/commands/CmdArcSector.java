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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoConicPartConicParameters;
import org.geogebra.common.kernel.algos.AlgoConicPartConicPoints;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Arc[ &lt;GeoConic&gt;, &lt;Number&gt;, &lt;Number&gt; ]
 * 
 * Arc[ &lt;GeoConic&gt;, &lt;GeoPoint&gt;, &lt;GeoPoint&gt; ]
 */
public class CmdArcSector extends CommandProcessor {

	/**
	 * arc/sector
	 */
	protected int type;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 * @param type
	 *            arc/sector
	 */
	public CmdArcSector(Kernel kernel, int type) {
		super(kernel);
		this.type = type;
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoConic())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {

				GeoElement[] ret = { arcSector(c.getLabel(),
						(GeoConicND) arg[0], (GeoNumberValue) arg[1],
						(GeoNumberValue) arg[2]) };

				return ret;
			} else if ((ok[0] = arg[0].isGeoConic())
					&& (ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2].isGeoPoint())) {

				GeoElement[] ret = {
						arcSector(c.getLabel(), (GeoConicND) arg[0],
								(GeoPointND) arg[1], (GeoPointND) arg[2]) };

				return ret;
			} else {
				if (!ok[0]) {
					throw argErr(c, arg[0]);
				} else if (!ok[1]) {
					throw argErr(c, arg[1]);
				} else {
					throw argErr(c, arg[2]);
				}
			}

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param conic
	 *            conic
	 * @param start
	 *            start value
	 * @param end
	 *            end value
	 * @return conic part
	 */
	protected GeoElement arcSector(String label, GeoConicND conic,
			GeoNumberValue start, GeoNumberValue end) {
		AlgoConicPartConicParameters algo = new AlgoConicPartConicParameters(
				cons, label, conic, start, end, type);

		return algo.getConicPart();
	}

	/**
	 * @param label
	 *            label
	 * @param conic
	 *            conic
	 * @param start
	 *            start point
	 * @param end
	 *            end point
	 * @return conic part
	 */
	protected GeoElement arcSector(String label, GeoConicND conic,
			GeoPointND start, GeoPointND end) {
		AlgoConicPartConicPoints algo = new AlgoConicPartConicPoints(cons,
				label, conic, start, end, type);

		return algo.getConicPart();
	}

}
