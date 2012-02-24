package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoInterval;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/**
 * Max[ <Number>, <Number> ]
 */
public class CmdMax extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMax(Kernel kernel) {
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
				GeoElement[] ret = { 
						kernelA.Max(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else if (arg[0].isGeoInterval()) {
				GeoElement[] ret = { 
						kernelA.Max(c.getLabel(),
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
						kernelA.Max(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;
				
			}
			throw argErr(app, c.getName(), getBadArg(ok,arg));

		case 3:		//Max[f,a,b]
			arg=resArgs(c);
			if( (ok[0]=arg[0].isGeoFunction()) &&
			    (ok[1]=arg[1].isNumberValue())     &&
			    (ok[2]=arg[2].isNumberValue())  )
			{
				GeoElement[] ret= {
						kernelA.Max(c.getLabel(),
						(GeoFunction) arg[0],
						(NumberValue) arg[1],
						(NumberValue) arg[2])
				};//array
				return ret;
			}
				throw argErr(app,c.getName(),getBadArg(ok,arg));
			
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
