package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.kernel.algos.AlgoFractionTextPoint;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * FractionText
 */
public class CmdFractionText extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFractionText(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 1:

			if (arg[0] instanceof GeoNumberValue) {

				AlgoFractionText algo = new AlgoFractionText(cons,
						(GeoNumberValue) arg[0], null);
				algo.getResult().setLabel(c.getLabel());
				return new GeoElement[]{ algo.getResult() };
			} else if (arg[0].isGeoPoint()) {
				AlgoFractionTextPoint algo = new AlgoFractionTextPoint(cons,
						(GeoPointND) arg[0]);
				algo.getResult().setLabel(c.getLabel());
				return new GeoElement[]{ algo.getResult() };
			}
			throw argErr(c, arg[0]);
		case 2:
			if (!(arg[0] instanceof GeoNumberValue)) {
				throw argErr(c, arg[0]);
			}
			if (!(arg[1] instanceof GeoBoolean)) {
				throw argErr(c, arg[1]);
			}
			AlgoFractionText algo = new AlgoFractionText(cons,
					(GeoNumberValue) arg[0], (GeoBoolean) arg[1]);
			algo.getResult().setLabel(c.getLabel());
			return new GeoElement[]{ algo.getResult() };
		default:
			throw argNumErr(c);
		}
	}
}
