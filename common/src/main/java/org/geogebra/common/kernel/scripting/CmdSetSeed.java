package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
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
	protected final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0] instanceof NumberValue) {

				app.setRandomSeed((int) ((NumberValue) arg[0]).getDouble());

				return;

			}

			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
