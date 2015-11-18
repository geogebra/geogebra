package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoIterationList;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * IterationList[ <function>, <start>, <n> ]
 * 
 * IterationList[ <function>, <var_name>, <var_value>, <n> ]
 */
public class CmdIterationList extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIterationList(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 0:
		case 1:
		case 2:
			throw argNumErr(app, c.getName(), n);
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoFunction())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {
				AlgoIterationList algo = new AlgoIterationList(cons,
						c.getLabel(), (GeoFunction) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));
		default:
			GeoElement arg1 = null;
			GeoElement[] vars = new GeoElement[n - 3];
			GeoList[] over = new GeoList[1];
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

			AlgoIterationList algo = new AlgoIterationList(cons,
					arg1, vars, over, num[0]);
			algo.getOutput(0).setLabel(c.getLabel());
			return algo.getOutput();


		}
	}
}