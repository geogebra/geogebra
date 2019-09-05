package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.prover.AlgoProveDetails;
import org.geogebra.common.main.MyError;

/**
 * ToolImage
 */
public class CmdProveDetails extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdProveDetails(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {

		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:
			if (arg[1].isGeoBoolean()) {
				return proveDetails(arg[0], ((GeoBoolean) arg[1]).getBoolean(), c);
			}
		case 1:
			return proveDetails(arg[0], false, c);

		default:
			throw argNumErr(c);

		}
	}

	private GeoElement[] proveDetails(GeoElement geoElement, boolean html, Command c) {
		if (geoElement instanceof BooleanValue) {

			AlgoProveDetails algo = new AlgoProveDetails(cons, geoElement, html);
			algo.getGeoList().setLabel(c.getLabel());
			GeoElement[] ret = { algo.getGeoList() };
			return ret;
		}
		throw argErr(c, geoElement);
	}
}