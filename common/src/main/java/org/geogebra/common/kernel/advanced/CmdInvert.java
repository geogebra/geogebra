package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;

/**
 * Invert[ &lt;Function&gt; ]
 * 
 * Invert[ &lt;Matrix&gt; ]
 */
public class CmdInvert extends CommandProcessor {

	private boolean numeric;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 * @param numeric
	 *            whether to use NInvert
	 */
	public CmdInvert(Kernel kernel, boolean numeric) {
		super(kernel);
		this.numeric = numeric;
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			if (arg[0].isGeoFunction()) {

				AlgoFunctionInvert algo = new AlgoFunctionInvert(cons,
						(GeoFunction) arg[0], numeric);
				algo.getResult().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else if (arg[0].isGeoList() && !"NInvert".equals(c.getName())) {

				AlgoInvert algo = new AlgoInvert(cons, c.getLabel(),
						(GeoList) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
