package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.statistics.AlgoInverseLogNormal;
import geogebra.common.main.MyError;

/**
 * InvarseNormal[ <Number>, <Number>,<Number> ]
 * 
 * adapted from CmdMax by Michael Borcherds 2008-01-20
 */
public class CmdInverseLogNormal extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInverseLogNormal(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:			
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue()) &&
				(ok[1] = arg[1].isNumberValue()) &&
				(ok[2] = arg[2].isNumberValue())) 
			{
				AlgoInverseLogNormal algo = new AlgoInverseLogNormal(cons, c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1], (NumberValue) arg[2]);

				GeoElement[] ret = {algo.getResult() };
				return ret;
				
			}
			throw argErr(app, c.getName(),getBadArg(ok,arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
