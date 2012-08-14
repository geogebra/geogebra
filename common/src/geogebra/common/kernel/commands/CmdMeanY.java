package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoListMeanY;
import geogebra.common.main.MyError;
/**
 * MeanX[ &lt;Number of points> ]
 */
public class CmdMeanY extends CmdOneOrTwoListsFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMeanY(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						doCommand(c.getLabel(),
								(GeoList) arg[0]) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{

		AlgoListMeanY algo = new AlgoListMeanY(cons, a, b);
		return algo.getResult();
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return null;//kernel.MeanY(a, b, c);
	}


}
