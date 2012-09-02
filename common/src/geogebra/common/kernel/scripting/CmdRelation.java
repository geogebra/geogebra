package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.main.MyError;

/**
 * Relation[ <GeoElement>, <GeoElement> ]
 */
public class CmdRelation extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRelation(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];

		switch (n) {
		case 2:
			arg = resArgs(c);

			// show relation string in a message dialog
			if ((ok[0] = (arg[0].isGeoElement()))
					&& (ok[1] = (arg[1].isGeoElement()))) {
				app.showRelation(arg[0], arg[1]);
				return;
			}

			// syntax error
			throw argErr(app, c.getName(), getBadArg(ok,arg));
			

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
