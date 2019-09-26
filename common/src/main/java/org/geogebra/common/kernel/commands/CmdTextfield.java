package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * InputBox[], InputBox[object]
 * 
 * @author Zbynek
 *
 */

public class CmdTextfield extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTextfield(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		// dummy

		switch (n) {
		case 1:

			arg = resArgs(c);
			if (arg[0].isLabelSet()) {
				return new GeoElement[] {
						getAlgoDispatcher().textfield(c.getLabel(), arg[0]) };
			}
			throw argErr(c, arg[0]);
		case 0:
			return new GeoElement[] {
					getAlgoDispatcher().textfield(c.getLabel(), null) };

		default:
			throw argNumErr(c);
		}

	}
}
