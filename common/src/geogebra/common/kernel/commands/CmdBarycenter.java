package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.barycentric.AlgoBarycenter;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/**
 * Barycenter[ &lt;List of points>, &lt; list of weights> ]
 * @author Darko
 *
 */
public class CmdBarycenter extends CommandProcessor 
{
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdBarycenter(Kernel kernel) 
	{
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoList()) &&
					(ok[1] = arg[1].isGeoList())) {
				
				AlgoBarycenter algo = new AlgoBarycenter(cons, c.getLabel(),
						(GeoList)arg[0], (GeoList)arg[1]);

				GeoElement[] ret = { algo.getResult() } ;
				return ret;
				
			}
			if(!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
