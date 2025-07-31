package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoBezierCurve;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

public class CmdBezierCurve extends CommandProcessor {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdBezierCurve(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		int n = c.getArgumentNumber();
		if (n != 4) {
			throw argNumErr(c);
		}
		GeoElement[] arg = resArgs(c, info);

		for (GeoElement geo: arg) {
			if (!geo.isGeoPoint()) {
				throw argErr(geo, c);
			}
		}
		AlgoBezierCurve algo = new AlgoBezierCurve(cons, (GeoPointND) arg[0],
				(GeoPointND) arg[1], (GeoPointND) arg[2], (GeoPointND) arg[3]);
		algo.getOutput(0).setLabel(c.getLabel());
		return algo.getOutput();
	}
}
