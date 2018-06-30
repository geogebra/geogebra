package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * SetDecoration
 */
public class CmdSetDecoration extends CmdScripting {

	/**
	 * Create new command processor
	 *
	 * @param kernel
	 *            kernel
	 */
	public CmdSetDecoration(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 2:
			GeoElement[] arg = resArgs(c);
			if (!arg[1].isNumberValue()) {
				throw argErr(c, arg[1]);
			}

			int style = (int) arg[1].evaluateDouble();

			// For invalid number we assume it's 0
			// We do this also for SetPointStyle

			if (style < 0) {
				style = 0;
			}

			arg[0].setDecorationType(style);
			arg[0].updateRepaint();

			return arg;

		default:
			throw argNumErr(c);
		}
	}

}
