package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 * DotPlot[ <List of Numeric> ] G.Sturr 2010-8-10
 */
public class CmdDotPlot extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDotPlot(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						doCommand(c.getLabel(),
								(GeoList) arg[0]) };
				return ret;
			} 
			throw argErr(app, c.getName(), arg[0]);
			
		case 2:
			if (arg[0].isGeoList() && arg[1].isGeoNumeric()) {
				AlgoDotPlotScale algo = new AlgoDotPlotScale(cons, c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1]);
				GeoElement[] ret = {algo.getResult() };
				return ret;
			} 
			throw argErr(app, c.getName(), arg[0]);
	
		     // more than one argument
        default :     	
        	throw argNumErr(app, c.getName(), n);
		}
	}

	final private GeoElement doCommand(String a, GeoList b) {
		AlgoDotPlot algo = new AlgoDotPlot(cons, a, b);
		return algo.getResult();
	}

}
