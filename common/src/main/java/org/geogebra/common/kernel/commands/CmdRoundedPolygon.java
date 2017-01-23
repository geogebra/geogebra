package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoRoundedPolygon;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class CmdRoundedPolygon extends CommandProcessor {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdRoundedPolygon(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) {
		GeoElement[] arg = this.resArgs(c, info);
		int n = c.getArgumentNumber();
		GeoPointND[] points = new GeoPointND[n - 1];
		// check arguments
		for (int i = 0; i < n - 1; i++) {
			if (!(arg[i].isGeoPoint())) {
				throw argErr(app, c, arg[i]);
			}
			points[i] = (GeoPointND) arg[i];
		}
		if (!(arg[n - 1].isNumberValue())) {
			throw argErr(app, c, arg[n - 1]);
		}
		AlgoRoundedPolygon algo = new AlgoRoundedPolygon(cons, points,
				(GeoNumberValue) arg[n - 1]);
		algo.getOutput(0).setLabel(c.getLabel());
		return algo.getOutput();

	}

}
