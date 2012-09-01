package geogebra.common.kernel.barycentric;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;

/**
 * TriangleCenter[<Point>,<Point>,<Point>,<Index>]
 * @author Zbynek Konecny
 *
 */
public class CmdKimberling extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdKimberling(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoPoint()) &&
					(ok[1] = arg[1].isGeoPoint()) &&
					(ok[2] = arg[2].isGeoPoint()) &&
					(ok[3] = arg[3].isNumberValue())) {
				
				AlgoKimberling algo = new AlgoKimberling(cons, c.getLabel(),
						(GeoPoint)arg[0], (GeoPoint)arg[1], (GeoPoint)arg[2],
						(NumberValue) arg[3]);

				GeoElement[] ret = { algo.getResult() } ;
				return ret;
				
			} 
			throw argErr(app,c.getName(),getBadArg(ok,arg));
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
