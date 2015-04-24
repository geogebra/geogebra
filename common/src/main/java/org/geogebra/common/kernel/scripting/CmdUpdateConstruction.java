package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * UpdateConstruction
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

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:
			app.getKernel().updateConstruction();
			app.setUnsaved();

			return new GeoElement[0];

		case 1:
			GeoElement[] arg = resArgs(c);
			if (arg[0] instanceof NumberValue) {
				double val = ((NumberValue) arg[0]).getDouble();
				if (Kernel.isInteger(val)) {
					app.getKernel().updateConstruction((int) val);
					app.setUnsaved();
					return arg;
				}
			}

			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
