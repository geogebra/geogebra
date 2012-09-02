package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * Textfield[],Textfield[object]
 * @author Zbynek
 *
 */

public class CmdTextfield extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTextfield(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		// dummy

		
		
		switch (n) {
		case 1:
			
			arg = resArgs(c);
			if (arg[0].isLabelSet()){				
				return new GeoElement[] {kernelA.textfield(c.getLabel(),arg[0])};
			}
			throw argErr(app, c.getName(), arg[0]);			
		case 0:							
			return new GeoElement[] {kernelA.textfield(c.getLabel(),null)};

		default:
			throw argNumErr(app, c.getName(), n);
		}
		
	}
}
