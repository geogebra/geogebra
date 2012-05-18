package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoLogisticDF;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;

/**
 * Logistic Distribution
 */
public class CmdLogistic extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLogistic(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;

		BooleanValue cumulative = null; // default for n=3
		arg = resArgs(c);
		
		switch (n) {
		case 4:
			if (!arg[2].isGeoFunction() || !((GeoFunction)arg[2]).toString(StringTemplate.defaultTemplate).equals("x")) {
				throw argErr(app, c.getName(), arg[2]);
			}
			
			if (arg[3].isGeoBoolean()) {
				cumulative = (BooleanValue)arg[3];
			} else
				throw argErr(app, c.getName(), arg[3]);
			
			// fall through
		case 3:			
			if ((ok = arg[0].isNumberValue()) && (arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString(StringTemplate.defaultTemplate).equals("x")) {
									
					AlgoLogisticDF algo = new AlgoLogisticDF(cons, c.getLabel(), (NumberValue)arg[0], (NumberValue)arg[1], cumulative);
					return algo.getGeoElements();
					
					
				} else if (arg[2].isNumberValue()) 
				{
					// needed for eg Normal[1, 0.001, x] 
					StringTemplate highPrecision = StringTemplate.maxPrecision;
					String m = arg[0].getLabel(highPrecision);
					String s = arg[1].getLabel(highPrecision);
					String x = arg[2].getLabel(highPrecision);
					
					GeoElement[] ret = kernelA.getAlgebraProcessor().processAlgebraCommand( "1/(1+exp(-("+x+"-("+m+"))/abs("+s+")))", true );
					return ret;
					
				}  else
					throw argErr(app, c.getName(), arg[2]);
		} 
		throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
