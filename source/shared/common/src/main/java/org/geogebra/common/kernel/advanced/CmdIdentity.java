package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * Identity[&lt;Number&gt;]
 */
public class CmdIdentity extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIdentity(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);
		if (n != 1) {
			throw argNumErr(c);
		}
		if (!(arg[0] instanceof GeoNumberValue)) {
			throw argErr(c, arg[0]);
		}

		AlgoIdentity algo = new AlgoIdentity(kernel.getConstruction(),
				c.getLabel(), (GeoNumberValue) arg[0]);
		return new GeoElement[] { algo.getResult() };

	}
}
