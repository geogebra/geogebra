package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * ZoomIn
 */
public class CmdSetSeed extends CmdScripting {
	/**
	 * Sets new seed for random numbers
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetSeed(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			GeoElement[] arg = resArgs(c);
			if (arg[0].isNumberValue()) {

				app.setRandomSeed((int) arg[0].evaluateDouble());

				return arg;

			}

			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
