package geogebra3D.kernel3D.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.main.MyError;
import geogebra3D.kernel3D.AlgoOrientation;



/**
 * Vector[ <GeoPoint>, <GeoPoint> ] Vector[ <GeoPoint> ]
 */
public class CmdOrientation extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdOrientation(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		if (n == 1) {
			arg = resArgs(c);
			if (arg[0] instanceof GeoDirectionND) {
				AlgoOrientation algo = new AlgoOrientation(cons, c.getLabel(), (GeoDirectionND) arg[0]);
				GeoElement[] ret = { algo.getVector()};
				return ret;
			}
			
			throw argErr(app, c.getName(), arg[0]);
		}
			
		throw argNumErr(app, c.getName(), n);
		
	}
	
}