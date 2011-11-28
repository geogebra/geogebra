package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;

/**
 * Textfield[],Textfield[object]
 * @author Zbynek
 *
 */

class CmdTextfield extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTextfield(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		// dummy

		
		
		switch (n) {
		case 1:
			
			arg = resArgs(c);
			if (arg[0].isLabelSet()){				
				return new GeoElement[] {kernel.textfield(c.getLabel(),arg[0])};
			}
			else
				throw argErr(app, c.getName(), arg[0]);			
		case 0:							
			return new GeoElement[] {kernel.textfield(c.getLabel(),null)};

		default:
			throw argNumErr(app, c.getName(), n);
		}
		
	}
}
