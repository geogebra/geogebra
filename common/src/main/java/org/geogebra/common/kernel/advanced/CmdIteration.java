package org.geogebra.common.kernel.advanced;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoIteration;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Iteration[ &lt;function>, &lt;start>, &lt;n> ]
 * 
 * Iteration[ &lt;function>, &lt;var_name>, &lt;var_value>, &lt;n> ]
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
		case 0:
		case 1:
		case 2:
			throw argNumErr(c);
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

			if ((ok[0] = (arg[0].isGeoFunctionNVar()
					&& ((GeoFunctionNVar) arg[0]).isFun2Var()))
					&& (ok[1] = arg[1] instanceof GeoList)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {
				AlgoIteration algo = new AlgoIteration(cons, c.getLabel(),
						(GeoFunctionNVar) arg[0], (GeoList) arg[1],
						(GeoNumberValue) arg[2]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}

			throw argErr(c, getBadArg(ok, arg));
		default:
			if (GeoGebraConstants.CAS_APPCODE.equals(app.getConfig().getAppCode())
					|| GeoGebraConstants.CAS_APPCODE.equals(app.getConfig().getSubAppCode())) {
				throw argNumErr(c);
			}
			GeoElement arg1 = null;
			GeoElement[] vars = new GeoElement[n - 3]; // exp, list and limit
														// not included
			GeoList[] over = new GeoList[1];
			GeoNumeric[] num = new GeoNumeric[1];
			boolean oldval = cons.isSuppressLabelsActive();

			try {
				cons.setSuppressLabelCreation(true);
				arg1 = resArgsForIteration(c, vars, over, num);
			} finally {
				for (GeoElement localVar : vars) {
					if (localVar != null) {
						cons.removeLocalVariable(localVar
								.getLabel(StringTemplate.defaultTemplate));
					}
				}
				cons.setSuppressLabelCreation(oldval);
			}

			AlgoIteration algo = new AlgoIteration(cons, arg1,
					vars, over, num[0]);
			algo.getOutput(0).setLabel(c.getLabel());
			return algo.getOutput();
		}
	}
}