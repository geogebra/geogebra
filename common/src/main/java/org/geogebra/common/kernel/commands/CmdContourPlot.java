package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoContourPlot;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * ContourPlot[ <Function> ]
 */
public class CmdContourPlot extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdContourPlot(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		if (n != 1 && n != 2) {
			throw argNumErr(app, c.getName(), n);
		}
		GeoElement[] arg;
		arg = resArgs(c);
		if (arg[0] instanceof GeoFunctionNVar) {
			AlgoContourPlot algo = null;
			if (n == 1) {
				algo = new AlgoContourPlot(cons, c.getLabel(),
						(GeoFunctionNVar) arg[0], app.getActiveEuclidianView()
								.getXmin(), app.getActiveEuclidianView()
								.getXmax(), app.getActiveEuclidianView()
								.getYmin(), app.getActiveEuclidianView()
								.getYmax());
			} else if (n == 2 && arg[1] instanceof GeoNumeric) {
				algo = new AlgoContourPlot(cons, c.getLabel(),
						(GeoFunctionNVar) arg[0], app.getActiveEuclidianView()
								.getXmin(), app.getActiveEuclidianView()
								.getXmax(), app.getActiveEuclidianView()
								.getYmin(), app.getActiveEuclidianView()
								.getYmax(), arg[1].evaluateDouble());
			} else {
				throw this.argErr(app, c.getName(), arg[1]);
			}
			return algo.getOutput();
		}
		throw this.argErr(app, c.getName(), arg[0]);

	}

}
