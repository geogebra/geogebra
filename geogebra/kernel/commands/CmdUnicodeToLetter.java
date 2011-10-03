package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.MyError;

/*
 * Max[ <Number>, <Number> ]
 */
public class CmdUnicodeToLetter extends CommandProcessor {

	public CmdUnicodeToLetter(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue()) ) 
			{
				GeoElement[] ret = { 
						kernel.UnicodeToLetter(c.getLabel(),
						(NumberValue) arg[0] ) };
				return ret;
						
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
