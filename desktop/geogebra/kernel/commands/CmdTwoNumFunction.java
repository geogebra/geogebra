package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

/*
 * abstract class for Commands with two numberical arguments eg Binomial[ <Number>, <Number> ]
 * Michael Borcherds 2008-04-12
 */
public abstract class CmdTwoNumFunction extends CommandProcessor {

	public CmdTwoNumFunction(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		
		case 2:			
			arg = resArgs(c);
			if ((arg[0].isNumberValue()) &&
				(arg[1].isNumberValue())) 
			{
				GeoElement[] ret = { 
						doCommand(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;
				
			}  else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
	
    abstract protected GeoElement doCommand(String a, NumberValue b, NumberValue c);     
}
