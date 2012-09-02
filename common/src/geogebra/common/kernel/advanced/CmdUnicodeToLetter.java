package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * UnicodeToLetter[ <Number> ]
 */
public class CmdUnicodeToLetter extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdUnicodeToLetter(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if ( arg[0].isNumberValue()) 
			{
				AlgoUnicodeToLetter algo = new AlgoUnicodeToLetter(cons, c.getLabel(),
						(NumberValue) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
						
			}
			throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
