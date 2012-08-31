package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.App;
import geogebra.common.main.MyError;

/**
 *TDistribution
 */
public class CmdTDistribution extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTDistribution(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		
		GeoElement[] arg;
		
		arg = resArgs(c);

		BooleanValue cumulative = null; // default for n=2 (false)
		switch (n) {
		case 3:
			if (!arg[1].isGeoFunction() || !((GeoFunction)arg[1]).toString(StringTemplate.defaultTemplate).equals("x")) {
				throw argErr(app, c.getName(), arg[1]);
			}
			
			if (arg[2].isGeoBoolean()) {
				cumulative = (BooleanValue) arg[2];
			} else
				throw argErr(app, c.getName(), arg[2]);

			// fall through
		case 2:			
			if (arg[0].isNumberValue()) {
				if (arg[1].isGeoFunction() && ((GeoFunction)arg[1]).toString(StringTemplate.defaultTemplate).equals("x")) {

					App.debug("jhgjhgjhg");
					AlgoTDistributionDF algo = new AlgoTDistributionDF(cons, c.getLabel(), (NumberValue)arg[0], cumulative);
					return algo.getGeoElements();

				} else if (arg[1].isNumberValue()) {
					
					AlgoTDistribution algo = new AlgoTDistribution(cons, c.getLabel(),
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
