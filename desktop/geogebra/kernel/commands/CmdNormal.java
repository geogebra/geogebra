package geogebra.kernel.commands;

import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import geogebra.main.MyError;

/*
 * , (NumberValue) arg[1][ <Number>, <Number>,<Number> ]
 * 
 * adapted from CmdMax by Michael Borcherds 2008-01-20
 */
public class CmdNormal extends CommandProcessor {

	public CmdNormal(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;

		boolean cumulative = false; // default for n=3
		arg = resArgs(c);
		
		switch (n) {
		case 4:
			if (arg[3].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[3]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[3]);
			
			// fall through
		case 3:			
			if ((ok = arg[0].isNumberValue()) && (arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString().equals("x")) {
									
					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					
					String mean = arg[0].getLabel();
					String sd = arg[1].getLabel();
					
					kernel.restorePrintAccuracy();
					
					if (cumulative) {
						GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "(erf((x-("+mean+"))/abs("+sd+")) + 1)/2", true );
						
						return ret;
						
					} else {
						GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "exp(-((x-("+mean+"))/("+sd+"))^2/2)/(sqrt(2*pi)*abs("+sd+"))", true );
						
						return ret;
					}
					
				} else if (arg[2].isNumberValue()) 
				{
					GeoElement[] ret = { 
							kernel.Normal(c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1], (NumberValue) arg[2]) };
					return ret;
					
				}  else
					throw argErr(app, c.getName(), arg[2]);
		} else throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
