package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoLast;
import org.geogebra.common.kernel.algos.AlgoLastFunction;
import org.geogebra.common.kernel.algos.AlgoLastString;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * Last[ &lt;List&gt;,n ]
 * 
 * @author Michael Borcherds
 * @version 2008-03-04
 */
public class CmdLast extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLast(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			if (arg[0].isGeoList()) {
				GeoElement[] ret = {
						last(c.getLabel(), (GeoList) arg[0], null) };
				return ret;
			} else if (arg[0].isGeoText()) {
				GeoElement[] ret = {
						last(c.getLabel(), (GeoText) arg[0], null) };
				return ret;
			} else if (arg[0].isGeoFunction()) {
				AlgoLastFunction algo = new AlgoLastFunction(cons, c.getLabel(),
						(GeoFunction) arg[0]);
				return new GeoElement[] { algo.getResult() };
			}
			throw argErr(c, arg[0]);

		case 2:
			boolean list = arg[0].isGeoList();
			boolean text = arg[0].isGeoText();
			if (list && arg[1].isGeoNumeric()) {
				GeoElement[] ret = { last(c.getLabel(), (GeoList) arg[0],
						(GeoNumeric) arg[1]) };
				return ret;
			} else if (text && arg[1].isGeoNumeric()) {
				GeoElement[] ret = { last(c.getLabel(), (GeoText) arg[0],
						(GeoNumeric) arg[1]) };
				return ret;
			} else {
				throw argErr(c,
						(list && text) ? arg[1] : arg[0]);
			}

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * Last[string,n] Michael Borcherds
	 */
	final private GeoText last(String label, GeoText list, GeoNumeric n) {
		AlgoLastString algo = new AlgoLastString(cons, label, list, n);
		GeoText list2 = algo.getResult();
		return list2;
	}

	/**
	 * Last[list,n] Michael Borcherds
	 */
	final private GeoList last(String label, GeoList list, GeoNumeric n) {
		AlgoLast algo = new AlgoLast(cons, label, list, n);
		GeoList list2 = algo.getResult();
		return list2;
	}

}
