package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * IsInRegion[&lt;Point&gt;,&lt;Region&gt;]
 */
public class CmdIsInRegion extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIsInRegion(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);
		if (n != 2) {
			throw argNumErr(c);
		}
		if (!arg[0].isGeoPoint()) {
			throw argErr(c, arg[0]);
		}
		if (!arg[1].isRegion()) {
			throw argErr(c, arg[1]);
		}

		AlgoIsInRegion algo = new AlgoIsInRegion(cons, c.getLabel(),
				(GeoPointND) arg[0], (Region) arg[1]);

		return new GeoElement[] { algo.getResult() };
	}
}
