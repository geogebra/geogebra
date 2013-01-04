package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * abstract class for Commands with two numberical arguments eg Binomial[ <Number>, <Number> ]
 * Michael Borcherds 2008-04-12
 */
public abstract class CmdTwoNumFunction extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdTwoNumFunction(Kernel kernel) {
		super(kernel);
	}

	@Override
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
				
			}
			throw argErr(app, c.getName(), arg[0]);

		case 3:		// return list of results	
			arg = resArgs(c);
			if ((arg[0].isNumberValue()) &&
				(arg[1].isNumberValue()) &&
				(arg[2].isNumberValue())) 
			{
				GeoElement[] ret = { 
						doCommand2(c.getLabel(), (NumberValue) arg[0], (NumberValue) arg[1], (NumberValue) arg[2]) };
				return ret;
				
			}
			throw argErr(app, c.getName(), arg[0]);
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
	
	/**
	 * Perform the actual command
	 * @param b first number
	 * @param c second number
	 * @param a label
	 * @return resulting element
	 */
    abstract protected GeoElement doCommand(String a, NumberValue b, NumberValue c);     
    
    /**
	 * Perform the actual command
	 * @param b first number
	 * @param c second number
	 * @param a label
     * @param d length of result list
	 * @return resulting element
	 */
     protected GeoElement doCommand2(String a, NumberValue b, NumberValue c, NumberValue d){
    	 return null;
     }
}
