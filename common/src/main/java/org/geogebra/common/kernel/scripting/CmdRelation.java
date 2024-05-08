package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;

/**
 * Relation[ &lt;GeoElement&gt;, &lt;GeoElement&gt; ]
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
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok;

		GeoElement[] arg = resArgs(c);
		switch (n) {
		case 2:
			ok = new boolean[2];
			// show relation string in a message dialog
			if ((ok[0] = (arg[0].isGeoElement()))
					&& (ok[1] = (arg[1].isGeoElement()))) {
				app.showRelation(arg[0], arg[1], null, null);
				return arg;
			}

			// syntax error
			throw argErr(c, getBadArg(ok, arg));

		case 1:
			if (arg[0] instanceof GeoList) {
				GeoElement list = arg[0];
				GeoElement[] ge = list.getParentAlgorithm().getInput();
				int size = ge.length;

				ok = new boolean[size];
				if (ge.length == 2) {
					if ((ok[0] = (ge[0].isGeoElement()))
							&& (ok[1] = (ge[1].isGeoElement()))) {
						app.showRelation(ge[0], ge[1], null, null);
						return ge;
					}
					// syntax error
					throw argErr(c, getBadArg(ok, ge));
				}
				if (ge.length == 3) {
					if ((ok[0] = (ge[0].isGeoElement()))
							&& (ok[1] = (ge[1].isGeoElement()))
							&& (ok[2] = (ge[2].isGeoElement()))) {
						app.showRelation(ge[0], ge[1], ge[2], null);
						return ge;
					}
					// syntax error
					throw argErr(c, getBadArg(ok, ge));
				}
				if (ge.length == 4) {
					if ((ok[0] = (ge[0].isGeoElement()))
							&& (ok[1] = (ge[1].isGeoElement()))
							&& (ok[2] = (ge[2].isGeoElement()))
							&& (ok[3] = (ge[3].isGeoElement()))) {
						app.showRelation(ge[0], ge[1], ge[2], ge[3]);
						return ge;
					}
					// syntax error
					throw argErr(c, getBadArg(ok, ge));
				}
			}

			ok = new boolean[1];
			ok[0] = false;
			// syntax error
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}
}
