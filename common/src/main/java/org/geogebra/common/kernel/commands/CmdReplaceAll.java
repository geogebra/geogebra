package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.AlgoIsVertexForm;
import org.geogebra.common.kernel.advanced.AlgoReplaceAll;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

public class CmdReplaceAll extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdReplaceAll(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);

		if (n == 3) {
			if (arg[0] instanceof GeoText &&
					arg[1] instanceof GeoText &&
					arg[2] instanceof GeoText) {
				AlgoReplaceAll algo = new AlgoReplaceAll(cons, (GeoText) arg[0],
						(GeoText) arg[1], (GeoText) arg[2]);
				algo.getOutput(0).setLabel(c.getLabel());
				return new GeoElement[]{algo.getOutput(0)};
			}
			throw argErr(c, arg[0]);
		}
		throw argNumErr(c);
	}
}
