package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;;

/**
 *UpdateConstruction
 */
public class CmdUpdateConstruction extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUpdateConstruction(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:
			app.getKernel().updateConstruction();
			app.setUnsaved();
			
			return;
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
