package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoUnicodeToLetter;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * UnicodeToLetter[ &lt;Number&gt; ]
 */
public class CmdUnicodeToLetter extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUnicodeToLetter(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);
			if (arg[0] instanceof GeoNumberValue) {
				AlgoUnicodeToLetter algo = new AlgoUnicodeToLetter(cons,
						c.getLabel(), (GeoNumberValue) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}

}
