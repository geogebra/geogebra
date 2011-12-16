package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 * Relation[ <GeoElement>, <GeoElement> ]
 */
class CmdRelation extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRelation(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// show relation string in a message dialog
			if ((ok[0] = (arg[0].isGeoElement()))
					&& (ok[1] = (arg[1].isGeoElement()))) {
				app.showRelation((GeoElement) arg[0], (GeoElement) arg[1]);
				return;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
