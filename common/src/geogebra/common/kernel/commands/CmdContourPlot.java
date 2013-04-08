package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoContourPlot;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.main.MyError;

/**
 * ContourPlot[ <Function> ]
 */
public class CmdContourPlot extends CommandProcessor {

	/**
	 * Create new command processor
	 * @param kernel
	 * 		kernel
	 */
	public CmdContourPlot(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError
	{
		int n = c.getArgumentNumber();
		if (n!=1) {
			throw argNumErr(app, c.getName(), n);
		}
		GeoElement[] arg;			
		arg = resArgs(c);
		if (arg[0] instanceof GeoFunctionNVar){
			AlgoContourPlot algo = new AlgoContourPlot(cons, c.getLabel(), (GeoFunctionNVar)arg[0],
					app.getActiveEuclidianView().getXmin(), app.getActiveEuclidianView().getXmax(),
					app.getActiveEuclidianView().getYmin(), app.getActiveEuclidianView().getYmax());
			return algo.getOutput();
		}
		throw new MyError(app.getLocalization(), "InvalidEquation");
		
	}

}
