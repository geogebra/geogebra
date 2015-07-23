package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * SetConstructionStep[ <Number> ]
 * 
 * @author Michael Borcherds
 */
public class CmdSetConstructionStep extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetConstructionStep(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1:
			double newStep = Math.round(arg[0].evaluateDouble());
			int maxStep = cons.steps();
			// eg SetConstructionStep[infinity] to set to end
			if (newStep >= maxStep) {
				newStep = maxStep - 1;
			}

			cons.setStep((int) newStep);
			return arg;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
