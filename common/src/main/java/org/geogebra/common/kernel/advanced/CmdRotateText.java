package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * RotateText
 */
public class CmdRotateText extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRotateText(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 3:
		case 2:
			if ((ok[0] = (arg[0].isGeoText()))
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {
				AlgoRotateText algo = new AlgoRotateText(cons,
						(GeoText) arg[0], (GeoNumberValue) arg[1]);
				algo.getResult().setLabel(c.getLabel());
				return new GeoElement[] { algo.getResult() };
			}
			throw argErr(c, ok[0] ? arg[1] : arg[0]);

		default:
			throw argNumErr(c);
		}
	}

}
