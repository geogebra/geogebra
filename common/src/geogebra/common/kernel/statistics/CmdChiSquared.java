package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;

/**
 *Chi Squared Distribution
 */
public class CmdChiSquared extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdChiSquared(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		
		GeoElement[] arg;
		
		arg = resArgs(c);

		BooleanValue cumulative = null; // default for n=2
		switch (n) {
		case 3:
			
			if (!arg[1].isGeoFunction() || !((GeoFunction)arg[1]).toString(StringTemplate.defaultTemplate).equals("x")) {
				throw argErr(app, c.getName(), arg[1]);
			}
			
			if (arg[2].isGeoBoolean()) {
				cumulative = (BooleanValue)arg[2];
			} else
				throw argErr(app, c.getName(), arg[2]);

			// fall through
		case 2:			
			if (arg[0].isNumberValue() ) {
				if (arg[1].isGeoFunction() && ((GeoFunction)arg[1]).toString(StringTemplate.defaultTemplate).equals("x")) {

					AlgoChiSquaredDF algo = new AlgoChiSquaredDF(cons, c.getLabel(), (NumberValue)arg[0], cumulative);
					return algo.getGeoElements();



				} else if (arg[1].isNumberValue()) {
					
					AlgoChiSquared algo = new AlgoChiSquared(cons, c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1]);
					GeoElement[] ret = { algo.getResult() };
					return ret;
				} else
					throw argErr(app, c.getName(), arg[1]);

				}
			throw argErr(app, c.getName(), arg[0]);
				

			default:
				throw argNumErr(app, c.getName(), n);
			}
		}
	}
