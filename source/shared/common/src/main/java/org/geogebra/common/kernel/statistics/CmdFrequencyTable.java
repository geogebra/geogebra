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
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * FrequencyTable[ &lt;List of Raw Data L&gt; ]
 * FrequencyTable[ &lt;Boolean Cumulative C&gt;, &lt;List of Raw Data L&gt;]
 * FrequencyTable[&lt;List of Class Boundaries C&gt;, &lt;List of Raw Data L&gt; ]
 * FrequencyTable[ &lt;Boolean Cumulative&gt;,&lt;List of Class Boundaries C&gt;,
 *   &lt;List of Raw Data L&gt;]
 * FrequencyTable[&lt;List of Class Boundaries&gt;, &lt;List of Raw Data&gt;, &lt;Use
 * Density&gt; , &lt;Density Scale Factor&gt; (optional) ]
 * FrequencyTable[ &lt;Boolean Cumulative&gt;, &lt;List of Class Boundaries&gt;,
 *   &lt;List of Raw Data&gt;, &lt;Use Density&gt; , &lt;Density Scale Factor&gt; (optional) ]
 *
 */
public class CmdFrequencyTable extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFrequencyTable(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 1:
			// raw data
			if (arg[0].isGeoList()) {
				GeoElement[] ret = {
						frequencyTable(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			}

			// chart
			else if (arg[0].isGeoNumeric()) {
				GeoElement[] ret = {
						frequencyTable(c.getLabel(), (GeoNumeric) arg[0]) };
				return ret;

			} else {
				throw argErr(c, arg[0]);
			}

		case 2:
			// arg[0] = is cumulative, arg[1] = data list,
			if ((ok[0] = arg[0].isGeoBoolean())
					&& (ok[1] = arg[1].isGeoList())) {
				GeoElement[] ret = { frequencyTable(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1]) };
				return ret;
			}

			// arg[0] = class list, arg[1] = data list
			else if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoList())) {
				GeoElement[] ret = { frequencyTable(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]) };
				return ret;

			}

			// arg[0] = data list, arg[1] = scale factor,
			else if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoNumeric())) {
				GeoElement[] ret = { frequencyTable(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1]) };
				return ret;

			}

			else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else {
				throw argErr(c, arg[1]);
			}

		case 3:
			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list
			if ((ok[0] = arg[0].isGeoBoolean()) && (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoList())) {
				GeoElement[] ret = {
						frequencyTable(c.getLabel(), (GeoBoolean) arg[0],
								(GeoList) arg[1], (GeoList) arg[2]) };
				return ret;

			}
			// arg[0] = class list, arg[1] = data list, arg[2] = useDensity
			else if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = {
						frequencyTable(c.getLabel(), (GeoList) arg[0],
								(GeoList) arg[1], (GeoBoolean) arg[2]) };
				return ret;

			}

			// arg[0] = isCumulative, arg[1] = data list, arg[2] = scale factor,
			else if ((ok[0] = arg[0].isGeoBoolean())
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoNumeric())) {
				GeoElement[] ret = {
						frequencyTable(c.getLabel(), (GeoBoolean) arg[0],
								(GeoList) arg[1], (GeoNumeric) arg[2]) };
				return ret;

			}

			else if (!ok[0]) {
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
				GeoElement[] ret = { frequencyTable(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1], (GeoBoolean) arg[2],
						(GeoNumeric) arg[3]) };
				return ret;
			}

			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list,
			// arg[3] = useDensity
			else if ((ok[0] = arg[0].isGeoBoolean())
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoList())
					&& (ok[3] = arg[3].isGeoBoolean())) {
				GeoElement[] ret = { frequencyTable(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1], (GeoList) arg[2],
						(GeoBoolean) arg[3]) };
				return ret;
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
				GeoElement[] ret = { frequencyTable(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1], (GeoList) arg[2],
						(GeoBoolean) arg[3], (GeoNumeric) arg[4]) };
				return ret;
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
	 * FrequencyTable[dataList]
	 */
	final private GeoText frequencyTable(String label, GeoList dataList) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, null,
				null, dataList);
		return output(algo, label);
	}

	/**
	 * FrequencyTable[isCumulative, dataList]
	 */
	final private GeoText frequencyTable(String label, GeoBoolean isCumulative,
			GeoList dataList) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons,
				isCumulative, null, dataList);
		return output(algo, label);
	}

	/**
	 * FrequencyTable[dataList, scale factor]
	 */
	final private GeoText frequencyTable(String label, GeoList dataList,
			GeoNumeric scale) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, null,
				null, dataList, scale);
		return output(algo, label);
	}

	/**
	 * FrequencyTable[isCumulative, dataList, scale factor]
	 */
	final private GeoText frequencyTable(String label, GeoBoolean isCumulative,
			GeoList dataList, GeoNumeric scale) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons,
				isCumulative, null, dataList, scale);
		return output(algo, label);
	}

	/**
	 * FrequencyTable[classList, dataList]
	 */
	final private GeoText frequencyTable(String label, GeoList classList,
			GeoList dataList) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, null,
				classList, dataList);
		return output(algo, label);
	}

	/**
	 * FrequencyTable[classList, dataList, useDensity]
	 */
	final private GeoText frequencyTable(String label, GeoList classList,
			GeoList dataList, GeoBoolean useDensity) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, null,
				classList, dataList, useDensity, null);
		return output(algo, label);
	}

	/**
	 * FrequencyTable[classList, dataList, useDensity, scaleFactor]
	 */
	final private GeoText frequencyTable(String label, GeoList classList,
			GeoList dataList, GeoBoolean useDensity, GeoNumeric scaleFactor) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, null,
				classList, dataList, useDensity, scaleFactor);
		return output(algo, label);
	}

	/**
	 * FrequencyTable[isCumulative, classList, dataList]
	 */
	final private GeoText frequencyTable(String label, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons,
				isCumulative, classList, dataList, null, null);
		return output(algo, label);
	}

	/**
	 * FrequencyTable[isCumulative, classList, dataList, useDensity]
	 */
	final private GeoText frequencyTable(String label, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList, GeoBoolean useDensity) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons,
				isCumulative, classList, dataList, useDensity, null);
		return output(algo, label);
	}

	/**
	 * FrequencyTable[isCumulative, classList, dataList, useDensity,
	 * scaleFactor]
	 */
	final private GeoText frequencyTable(String label, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList, GeoBoolean useDensity,
			GeoNumeric scaleFactor) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons,
				isCumulative, classList, dataList, useDensity, scaleFactor);
		return output(algo, label);
	}

	/**
	 * FrequencyTable[chart (Histogram or BarChart)]
	 */
	final private GeoText frequencyTable(String label, GeoNumeric chart) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, chart);
		return output(algo, label);
	}

	private static GeoText output(AlgoFrequencyTable algo, String label) {
		algo.getResult().setLabel(label);
		return algo.getResult();
	}
}
