package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Payment[ &lt;Rate&gt;, &lt;Number of Periods&gt;, &lt;Present Value&gt;, &lt;Future
 * Value (optional)&gt;, &lt;Type (optional)&gt; ] Like the Excel Pmt function
 */
public class CmdFinancialPmt extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFinancialPmt(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c, info);

			if ((ok[0] = arg[0].isGeoNumeric())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())) {

				AlgoFinancial algo = new AlgoFinancial(cons, c.getLabel(),
						(GeoNumeric) arg[0], (GeoNumeric) arg[1], null,
						(GeoNumeric) arg[2], null, null,
						AlgoFinancial.CalculationType.PMT);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}

			else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else {
				throw argErr(c, arg[3]);
			}

		case 4:
			arg = resArgs(c, info);

			if ((ok[0] = arg[0].isGeoNumeric())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())) {

				AlgoFinancial algo = new AlgoFinancial(cons, c.getLabel(),
						(GeoNumeric) arg[0], (GeoNumeric) arg[1], null,
						(GeoNumeric) arg[2], (GeoNumeric) arg[3], null,
						AlgoFinancial.CalculationType.PMT);

				GeoElement[] ret = { algo.getResult() };
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
			arg = resArgs(c, info);

			if ((ok[0] = arg[0].isGeoNumeric())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())) {

				AlgoFinancial algo = new AlgoFinancial(cons, c.getLabel(),
						(GeoNumeric) arg[0], (GeoNumeric) arg[1], null,
						(GeoNumeric) arg[2], (GeoNumeric) arg[3],
						(GeoNumeric) arg[4], AlgoFinancial.CalculationType.PMT);

				GeoElement[] ret = { algo.getResult() };
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
}
