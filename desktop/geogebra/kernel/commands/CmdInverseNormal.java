package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.MyError;

/*
 * , (NumberValue) arg[1][ <Number>, <Number>,<Number> ]
 * 
 * adapted from CmdMax by Michael Borcherds 2008-01-20
 */
public class CmdInverseNormal extends CommandProcessor {

	public CmdInverseNormal(Kernel kernel) {
		super(kernel);
	}

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
				GeoElement[] ret = { 
						kernel.InverseNormal(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1], (NumberValue) arg[2]) };
				return ret;
				
			}  else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
