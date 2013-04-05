package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoDensityPlot;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.main.MyError;

/**
 * Giuliano Bellucci 05/04/2013
 * 
 * densityplot[2-variables function]
 * densityplot[2-variables function,minx,maxx,miny,maxy] 
 * densityplot[2-variables function,minx,maxx,miny,maxy,quality] quality=2 for web 1 for desktop
 * 
 */

public class CmdDensityPlot extends CommandProcessor {

	private double lowX;
	private double highX;
	private double lowY;
	private double highY;
	private GeoElement[] args;

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
			algo.compute();
			GeoElement[] ret = { algo.getResult() };
			return ret;
		case 5:
			control(c);
			algo = new AlgoDensityPlot(cons, (GeoFunctionNVar) args[0], lowX,
					highX, lowY, highY);
			algo.compute();
			ret = new GeoElement[1];
			ret[0] = algo.getResult();
			return ret;
		case 6:
			control(c);
			// Quality = 1 -> Image size = 280 x 280. 1 pixel for 1 function value
			// Quality = 2 -> Image size = 140 x 140. 4 pixels for 1 function value (for web)
			int quality = (int) c.getArgument(5).evaluateNum().getNumber()
					.getDouble();
			if (Double.isNaN(quality) || (quality != 1 && quality != 2)) {
				throw argErr(app, c.getName(), c.getArgument(5));
			}
			algo = new AlgoDensityPlot(cons, (GeoFunctionNVar) args[0], lowX,
					highX, lowY, highY, quality);
			algo.compute();
			ret = new GeoElement[1];
			ret[0] = algo.getResult();
			return ret;
		case 10:
			control(c);
			quality = (int) c.getArgument(5).evaluateNum().getNumber()
					.getDouble();
			if (Double.isNaN(quality) || (quality != 1 && quality != 2)) {
				throw argErr(app, c.getName(), c.getArgument(5));
			}
			double zeroX=(double) c.getArgument(6).evaluateNum().getNumber().getDouble();
			if (Double.isNaN(zeroX)) {
				throw argErr(app, c.getName(), c.getArgument(6));
			}
			double zeroY=(double) c.getArgument(7).evaluateNum().getNumber().getDouble();
			if (Double.isNaN(zeroY)) {
				throw argErr(app, c.getName(), c.getArgument(7));
			}
			boolean hasAxes=false;
			if (c.getArgument(8).isBooleanValue()){
				hasAxes="true".equals(c.getArgument(8).toValueString(StringTemplate.defaultTemplate));
			}else{
				throw argErr(app, c.getName(), c.getArgument(8));
			}
			boolean hasGrid=false;
			if (c.getArgument(9).isBooleanValue()){
				hasGrid="true".equals(c.getArgument(9).toValueString(StringTemplate.defaultTemplate));
			}else{
				throw argErr(app, c.getName(), c.getArgument(9));
			}
			algo = new AlgoDensityPlot(cons, (GeoFunctionNVar) args[0], lowX,
					highX, lowY, highY, quality,zeroX,zeroY,hasAxes,hasGrid);
			algo.compute();
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
		lowX = c.getArgument(1).evaluateNum().getNumber().getDouble();
		highX = c.getArgument(2).evaluateNum().getNumber().getDouble();
		lowY = c.getArgument(3).evaluateNum().getNumber().getDouble();
		highY = c.getArgument(4).evaluateNum().getNumber().getDouble();
		if (Double.isNaN(lowX) || Double.isNaN(highX) || lowX >= highX) {
			throw argErr(app, c.getName(), c.getArgument(1));
		}
		if (Double.isNaN(lowY) || Double.isNaN(highY) || lowY >= highY) {
			throw argErr(app, c.getName(), c.getArgument(3));
		}
	}
	
}
