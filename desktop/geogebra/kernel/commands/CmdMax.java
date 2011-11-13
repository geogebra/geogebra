package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoInterval;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.MyError;

/*
 * Max[ <Number>, <Number> ]
 */
public class CmdMax extends CommandProcessor {

	public CmdMax(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						kernel.Max(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else if (arg[0].isGeoInterval()) {
				GeoElement[] ret = { 
						kernel.Max(c.getLabel(),
						(GeoInterval) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		case 2:			
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue()) &&
				(ok[1] = arg[1].isNumberValue())) 
			{
				GeoElement[] ret = { 
						kernel.Max(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;
				
			}  else
				throw argErr(app, c.getName(), arg[0]);

		case 3:		//Max[f,a,b]
			arg=resArgs(c);
			if( (ok[0]=arg[0].isGeoFunction()) &&
			    (ok[1]=arg[1].isNumberValue())     &&
			    (ok[2]=arg[2].isNumberValue())  )
			{
				GeoElement[] ret= {
						kernel.Max(c.getLabel(),
						(GeoFunction) arg[0],
						(NumberValue) arg[1],
						(NumberValue) arg[2])
				};//array
				return ret;
			}else{
				throw argErr(app,c.getName(),arg[0]);
			}//if
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
