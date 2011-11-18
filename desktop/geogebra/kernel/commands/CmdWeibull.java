package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoBoolean;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunction;
import geogebra.main.MyError;

/**
 *Weibull Distribution
 */
class CmdWeibull extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdWeibull(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		
		arg = resArgs(c);

		boolean cumulative = false; // default for n=3
		switch (n) {
		case 4:
			if (!arg[2].isGeoFunction() || !((GeoFunction)arg[2]).toString().equals("x")) {
				throw argErr(app, c.getName(), arg[2]);
			}
			
			if (arg[3].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[3]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[3]);

			// fall through
		case 3:			
			if ((ok[0] = arg[0].isNumberValue()) && (ok[1] = arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString().equals("x")) {

					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String k = arg[0].getLabel();
					String l = arg[1].getLabel();
					kernel.restorePrintAccuracy();
					String command;
					
					if (cumulative) {
						command = "If[x<0,0,1-exp(-(x/("+l+"))^("+k+"))]";
					} else {
						command = "If[x<0,0,("+k+")/("+l+")(x/("+l+"))^("+k+"-1)exp(-(x/("+l+"))^("+k+"))]";
					}
					
					
					GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand(command, true);
					return ret;


				} else if (arg[2].isNumberValue()) {
					GeoElement[] ret = { kernel.Weibull(c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1],
							(NumberValue) arg[2]) };
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
