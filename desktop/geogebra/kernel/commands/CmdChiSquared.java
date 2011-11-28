package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoBoolean;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunction;

/**
 *Chi Squared Distribution
 */
class CmdChiSquared extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdChiSquared(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		
		arg = resArgs(c);

		boolean cumulative = false; // default for n=2
		switch (n) {
		case 3:
			
			if (!arg[1].isGeoFunction() || !((GeoFunction)arg[1]).toString().equals("x")) {
				throw argErr(app, c.getName(), arg[1]);
			}
			
			if (arg[2].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[2]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[2]);

			// fall through
		case 2:			
			if ((ok[0] = arg[0].isNumberValue()) ) {
				if (arg[1].isGeoFunction() && ((GeoFunction)arg[1]).toString().equals("x")) {

					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String k = arg[0].getLabel();
					String command = null;
					kernel.restorePrintAccuracy();
					
					if (cumulative) {
						command = "If[x<0,0,gamma(("+k+")/2,x/2)/gamma(("+k+")/2)]";
					} else {
						command = "If[x<0,0,(x^(("+k+")/2-1)exp(-x/2))/(2^(("+k+")/2)gamma(("+k+")/2))]";
					}
					
					
					GeoElement[] ret = (GeoElement[])kernel.getAlgebraProcessor().processAlgebraCommand(command, true);
					return ret;


				} else if (arg[1].isNumberValue()) {
					GeoElement[] ret = { kernel.ChiSquared(c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1]) };
					return ret;
				} else
					throw argErr(app, c.getName(), arg[1]);

				} else 
					throw argErr(app, c.getName(), arg[0]);
				

			default:
				throw argNumErr(app, c.getName(), n);
			}
		}
	}
