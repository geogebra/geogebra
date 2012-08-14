package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoGammaDF;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.statistics.AlgoGamma;
import geogebra.common.main.MyError;

/**
 *Gamma Distribution
 */
public class CmdGamma extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdGamma(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		
		arg = resArgs(c);

		BooleanValue cumulative = null; // default for n=3 (false)
		switch (n) {
		case 4:
			if (!arg[2].isGeoFunction() || !((GeoFunction)arg[2]).toString(StringTemplate.defaultTemplate).equals("x")) {
				throw argErr(app, c.getName(), arg[2]);
			}
			
			if (arg[3].isGeoBoolean()) {
				cumulative = (BooleanValue) arg[3];
			} else
				throw argErr(app, c.getName(), arg[3]);

			// fall through
		case 3:			
			if ((ok[0] = arg[0].isNumberValue()) && (ok[1] = arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString(StringTemplate.defaultTemplate).equals("x")) {

					AlgoGammaDF algo = new AlgoGammaDF(cons, c.getLabel(), (NumberValue)arg[0], (NumberValue)arg[1], cumulative);
					return algo.getGeoElements();



				} else if (arg[2].isNumberValue()) {
					
					AlgoGamma algo = new AlgoGamma(cons, c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1],
							(NumberValue) arg[2]);

					GeoElement[] ret = { algo.getResult() };
					return ret;
				} else
					throw argErr(app, c.getName(), arg[2]);

				} else if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				

			default:
				throw argNumErr(app, c.getName(), n);
			}
		}
	}
