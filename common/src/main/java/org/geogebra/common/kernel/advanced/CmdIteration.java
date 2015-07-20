package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoIteration;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Iteration[ <function>, <start>, <n> ]
 */
public class CmdIteration extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIteration(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoFunction())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {

				AlgoIteration algo = new AlgoIteration(cons, c.getLabel(),
						(GeoFunction) arg[0], (GeoNumberValue) arg[1],
						(GeoNumberValue) arg[2]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));
		case 4:
			GeoElement arg1 = null;
			GeoElement[] vars = new GeoElement[(n - 2) / 2];
			GeoList[] over = new GeoList[(n - 2) / 2];
			GeoNumeric[] num = new GeoNumeric[1];
			boolean oldval = cons.isSuppressLabelsActive();

			try {
				cons.setSuppressLabelCreation(true);
				arg1 = resArgsForIteration(c, vars, over, num);
			} finally {
				for (GeoElement localVar : vars) {
					if (localVar != null)
						cons.removeLocalVariable(localVar
								.getLabel(StringTemplate.defaultTemplate));
				}
				cons.setSuppressLabelCreation(oldval);
			}

			AlgoIteration algo = new AlgoIteration(cons, c.getLabel(), arg1,
					vars, over, num[0]);
			return algo.getOutput();
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}