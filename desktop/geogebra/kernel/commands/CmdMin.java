package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoInterval;
import geogebra.kernel.geos.GeoList;

/*
 * Min[ <Number>, <Number> ]
 */
public class CmdMin extends CommandProcessor {

	public CmdMin(Kernel kernel) {
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
						kernel.Min(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else if (arg[0].isGeoInterval()) {
				GeoElement[] ret = { 
						kernel.Min(c.getLabel(),
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
						kernel.Min(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;
				
			}  else
				throw argErr(app, c.getName(), arg[0]);
		
		case 3:		//Min[f,a,b]
			arg=resArgs(c);
			if( (ok[0]=arg[0].isGeoFunction()) &&
			    (ok[1]=arg[1].isNumberValue())     &&
			    (ok[2]=arg[2].isNumberValue())  )
			{
				GeoElement[] ret= {
						kernel.Min(c.getLabel(),
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
