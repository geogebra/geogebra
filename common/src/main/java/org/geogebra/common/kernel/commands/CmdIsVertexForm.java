package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.AlgoIsVertexForm;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.main.MyError;

public class CmdIsVertexForm extends CommandProcessor {
	public CmdIsVertexForm(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);

		if (n == 1) {
			if (arg[0] instanceof GeoFunctionable) {
				AlgoIsVertexForm algo = new AlgoIsVertexForm(cons, (GeoFunctionable) arg[0]);
				algo.getOutput(0).setLabel(c.getLabel());
				return new GeoElement[]{algo.getOutput(0)};
			}
			throw argErr(c, arg[0]);
		}
		throw argNumErr(c);
	}
}
