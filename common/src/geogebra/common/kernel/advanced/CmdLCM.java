package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/**
 * LCM[ <Number>, <Number> ]
 * LCM[list]
 * adapted from CmdMax by Michael Borcherds 2008-01-03
 */
public class CmdLCM extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLCM(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {
				
				AlgoListLCM algo = new AlgoListLCM(cons, c.getLabel(),
						(GeoList) arg[0]);

				GeoElement[] ret = { algo.getLCM() };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		
		case 2:			
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue()) &&
				(ok[1] = arg[1].isNumberValue())) 
			{
				
				AlgoLCM algo = new AlgoLCM(cons, c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
				
			}
			throw argErr(app, c.getName(), getBadArg(ok,arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
