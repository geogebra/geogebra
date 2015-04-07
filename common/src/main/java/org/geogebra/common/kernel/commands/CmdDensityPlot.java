package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDensityPlot;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.main.MyError;

/**
 * Giuliano Bellucci 05/04/2013
 * 
 * densityplot[2-variables function] densityplot[2-variables
 * function,minx,maxx,miny,maxy] densityplot[2-variables
 * function,minx,maxx,miny,maxy,quality] quality=2 for web 1 for desktop
 * 
 */

public class CmdDensityPlot extends CommandProcessor {

	private double lowX;
	private double highX;
	private double lowY;
	private double highY;
	private GeoElement[] args;

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDensityPlot(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {

		int n = c.getArgumentNumber();

		switch (n) {

		case 1:
			args = resArgs(c);
			if (!args[0].isGeoFunctionNVar()
					|| ((GeoFunctionNVar) args[0]).getVarNumber() != 2) {
				throw argErr(app, c.getName(), args[0]);
			}
			AlgoDensityPlot algo = new AlgoDensityPlot(cons,
					(GeoFunctionNVar) args[0]);
			GeoElement[] ret = { algo.getResult() };
			return ret;
		case 5:
			control(c);
			algo = new AlgoDensityPlot(cons, (GeoFunctionNVar) args[0], lowX,
					highX, lowY, highY, true);
			ret = new GeoElement[1];
			ret[0] = algo.getResult();
			return ret;
		case 6:
			control(c);
			algo = new AlgoDensityPlot(cons, (GeoFunctionNVar) args[0], lowX,
					highX, lowY, highY, ((GeoBoolean) args[5]).getBoolean());
			ret = new GeoElement[1];
			ret[0] = algo.getResult();
			return ret;
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	private void control(Command c) throws MyError {
		args = resArgs(c);
		if (!args[0].isGeoFunctionNVar()
				|| ((GeoFunctionNVar) args[0]).getVarNumber() != 2) {
			throw argErr(app, c.getName(), args[0]);
		}
		lowX = c.getArgument(1).evaluateDouble();
		highX = c.getArgument(2).evaluateDouble();
		lowY = c.getArgument(3).evaluateDouble();
		highY = c.getArgument(4).evaluateDouble();
		if (Double.isNaN(lowX) || Double.isNaN(highX) || lowX >= highX) {
			throw argErr(app, c.getName(), c.getArgument(1));
		}
		if (Double.isNaN(lowY) || Double.isNaN(highY) || lowY >= highY) {
			throw argErr(app, c.getName(), c.getArgument(3));
		}
		if (args.length == 6 && !args[5].isGeoBoolean()) {
			throw argErr(app, c.getName(), args[5]);
		}
	}

}
