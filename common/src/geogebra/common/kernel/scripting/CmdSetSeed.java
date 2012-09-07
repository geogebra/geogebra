package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.main.MyError;


/**
 *ZoomIn
 */
public class CmdSetSeed extends CmdScripting {
	/**
	 * Sets new seed for random numbers
	 * @param kernel kernel
	 */
	public CmdSetSeed(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isNumberValue()) {
				
				app.setRandomSeed((int) ((NumberValue)arg[0]).getDouble());

				return;

			} 

			throw argErr(app, c.getName(), arg[0]);


		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
