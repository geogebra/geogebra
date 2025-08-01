package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoSVD;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;

/**
 * SVD.Syntax=[ &lt;Matrix&gt; ]
 * 
 * @author csilla
 *
 */
public class CmdSVD extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSVD(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c, info);
		switch (n) {
		case 1:
			if (arg[0].isGeoList()) {
				AlgoSVD algo = new AlgoSVD(cons, c.getLabel(),
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
