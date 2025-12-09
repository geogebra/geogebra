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
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * Frequency
 */
public class CmdFrequency extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFrequency(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		GeoElement frequency = processFrequency(c, info);
		frequency.setLabel(c.getLabel());
		return new GeoElement[]{frequency};
	}

	private GeoElement processFrequency(Command c, EvalInfo info) {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {

		case 1:
			if (arg[0].isGeoList()) {
				return frequency((GeoList) arg[0]);
			}
			throw argErr(c, arg[0]);

		case 2:

			// arg[0] = is cumulative, arg[1] = data list,
			if (arg[0].isGeoBoolean() && arg[1].isGeoList()) {
				return frequency((GeoBoolean) arg[0],
						(GeoList) arg[1]);
			}

			// arg[0] = class list, arg[1] = data list
			else if (arg[0].isGeoList() && arg[1].isGeoList()) {
				if (arg[1].isGeoList()) {
					return frequency((GeoList) arg[0],
							(GeoList) arg[1]);
				}

			} else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else {
				throw argErr(c, arg[1]);
			}

		case 3:

			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list
			if ((ok[0] = arg[0].isGeoBoolean()) && (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoList())) {
				return frequency((GeoBoolean) arg[0],
						(GeoList) arg[1], (GeoList) arg[2]);
			}
			// arg[0] = class list, arg[1] = data list, arg[2] = useDensity
			else if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				return frequency((GeoList) arg[0],
						(GeoList) arg[1], (GeoBoolean) arg[2]);
			} else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else {
				throw argErr(c, arg[2]);
			}

		case 4:
			arg = resArgs(c, info);
			// arg[0] = class list, arg[2] = data list, arg[2] = useDensity,
			// arg[3]= density scale factor
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoBoolean())
					&& (ok[3] = arg[3].isGeoNumeric())) {
				return frequency((GeoList) arg[0],
						(GeoList) arg[1], (GeoBoolean) arg[2],
						(GeoNumeric) arg[3]);
			}

			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list,
			// arg[3] = useDensity
			else if ((ok[0] = arg[0].isGeoBoolean())
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoList())
					&& (ok[3] = arg[3].isGeoBoolean())) {
				return frequency((GeoBoolean) arg[0],
						(GeoList) arg[1], (GeoList) arg[2], (GeoBoolean) arg[3]);
				
			}

			else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else if (!ok[2]) {
				throw argErr(c, arg[2]);
			} else {
				throw argErr(c, arg[3]);
			}

		case 5:
			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list,
			// arg[3] = useDensity, arg[4] = density scale factor,
			if ((ok[0] = arg[0].isGeoBoolean()) && (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoList())
					&& (ok[3] = arg[3].isGeoBoolean())
					&& (ok[4] = arg[4].isGeoNumeric())) {
				return frequency((GeoBoolean) arg[0], (GeoList) arg[1],
						(GeoList) arg[2],
						(GeoBoolean) arg[3], (GeoNumeric) arg[4]);
			}

			else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else if (!ok[2]) {
				throw argErr(c, arg[2]);
			} else if (!ok[3]) {
				throw argErr(c, arg[3]);
			} else {
				throw argErr(c, arg[4]);
			}

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * Frequency[dataList] G. Sturr
	 */
	final private GeoList frequency(GeoList dataList) {
		AlgoFrequency algo = new AlgoFrequency(cons, null, null,
				dataList);
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[isCumulative, dataList] G. Sturr
	 */
	final private GeoList frequency(GeoBoolean isCumulative,
			GeoList dataList) {
		AlgoFrequency algo = new AlgoFrequency(cons, isCumulative, null,
				dataList);
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[classList, dataList] G. Sturr
	 */
	final private GeoList frequency(GeoList classList,
			GeoList dataList) {
		AlgoFrequency algo;

		if (classList.getElementType() == GeoClass.TEXT) {
			algo = new AlgoFrequency(cons, classList, dataList, true);
		} else {
			algo = new AlgoFrequency(cons, null, classList, dataList);
		}
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[classList, dataList, useDensity] G. Sturr
	 */
	final private GeoList frequency(GeoList classList,
			GeoList dataList, GeoBoolean useDensity) {
		AlgoFrequency algo = new AlgoFrequency(cons, null, classList,
				dataList, useDensity, null);
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[classList, dataList, useDensity, scaleFactor] G. Sturr
	 */
	final private GeoList frequency(GeoList classList,
			GeoList dataList, GeoBoolean useDensity, GeoNumeric scaleFactor) {
		AlgoFrequency algo = new AlgoFrequency(cons, null, classList,
				dataList, useDensity, scaleFactor);
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[isCumulative, classList, dataList] G. Sturr
	 */
	final private GeoList frequency(GeoBoolean isCumulative,
			GeoList classList, GeoList dataList) {
		AlgoFrequency algo = new AlgoFrequency(cons, isCumulative,
				classList, dataList, null, null);
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[isCumulative, classList, dataList, useDensity] G. Sturr
	 */
	final private GeoList frequency(GeoBoolean isCumulative,
			GeoList classList, GeoList dataList, GeoBoolean useDensity) {
		AlgoFrequency algo = new AlgoFrequency(cons, isCumulative,
				classList, dataList, useDensity, null);
		GeoList list = algo.getResult();
		return list;
	}

	/**
	 * Frequency[isCumulative, classList, dataList, useDensity, scaleFactor] G.
	 * Sturr
	 */
	final private GeoList frequency(GeoBoolean isCumulative,
			GeoList classList, GeoList dataList, GeoBoolean useDensity,
			GeoNumeric scaleFactor) {
		AlgoFrequency algo = new AlgoFrequency(cons, isCumulative,
				classList, dataList, useDensity, scaleFactor);
		GeoList list = algo.getResult();
		return list;
	}
}
