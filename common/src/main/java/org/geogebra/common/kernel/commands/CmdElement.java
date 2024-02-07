package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.AlgoTextElement;
import org.geogebra.common.kernel.algos.AlgoListElement;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * Element[ &lt;list>, &lt;n> ]
 * 
 * Element[ &lt;point>, &lt;n> ]
 */
public class CmdElement extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdElement(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 0:
		case 1:
			throw argNumErr(c);
		case 2:
			arg = resArgs(c);
			// list
			if ((ok[0] = arg[0].isGeoList() || arg[0] instanceof GeoList)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {

				AlgoListElement algo = new AlgoListElement(cons,
						(GeoList) arg[0], (GeoNumberValue) arg[1], info.isLabelOutput());
				algo.getElement().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getElement() };
				return ret;
			}
			if ((ok[0] = arg[0].isGeoText())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {

				AlgoTextElement algo = new AlgoTextElement(cons, c.getLabel(),
						(GeoText) arg[0], (GeoNumberValue) arg[1]);

				GeoElement[] ret = { algo.getText() };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			arg = resArgs(c);
			// list
			GeoNumberValue[] nvs = new GeoNumberValue[n - 1];
			if (!arg[0].isGeoList()) {
				throw argErr(c, arg[0]);
			}
			for (int i = 1; i < n; i++) {
				if (arg[i] instanceof GeoNumberValue) {
					nvs[i - 1] = (GeoNumberValue) arg[i];
				} else {
					throw argErr(c, arg[i]);
				}
			}

			AlgoListElement algo = new AlgoListElement(cons,
					(GeoList) arg[0], nvs, info.isLabelOutput());
			algo.getElement().setLabel(c.getLabel());
			GeoElement[] ret = { algo.getElement() };
			return ret;
		}

	}
}
