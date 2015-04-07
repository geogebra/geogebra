package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoScriptAction;
import org.geogebra.common.main.MyError;

/**
 * Repeat[ <Number>, <Scripting Command>, <Scripting Command>, ... ]
 */
public class CmdRepeat extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRepeat(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];


		if (n < 2) {
			throw argNumErr(app, c.getName(), n);
		}

		arg = resArgs(c);
		if (arg[0] instanceof GeoNumberValue) {

			int loopMax = (int) arg[0].evaluateDouble();
			if (loopMax < 1) {
				throw argErr(app, c.getName(), arg[0]);
			}

			for (int loop = 0; loop < loopMax; loop++) {

				for (int object = 1; object < n; object++) {

					if (arg[object] instanceof GeoScriptAction) {
						GeoScriptAction script = (GeoScriptAction) arg[object];
						script.perform();
					} else {
						throw argErr(app, c.getName(), arg[object]);
					}
				}
			}

			return;
		}
		throw argErr(app, c.getName(), arg[0]);

	}
}
