package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
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

		boolean cumulative = false; // default for n=2
		switch (n) {
		case 3:
			
			if (!arg[1].isGeoFunction() || !((GeoFunction)arg[1]).toString(StringTemplate.defaultTemplate).equals("x")) {
				throw argErr(app, c.getName(), arg[1]);
			}
			
			if (arg[2].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[2]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[2]);

			// fall through
		case 2:			
			if (arg[0].isNumberValue() ) {
				if (arg[1].isGeoFunction() && ((GeoFunction)arg[1]).toString(StringTemplate.defaultTemplate).equals("x")) {

					// needed for eg Normal[1, 0.001, x] 
					StringTemplate highPrecision = StringTemplate.maxPrecision;
					String k = arg[0].getLabel(highPrecision);
					String command = null;

					
					if (cumulative) {
						command = "If[x<0,0,gamma(("+k+")/2,x/2)/gamma(("+k+")/2)]";
					} else {
						command = "If[x<0,0,(x^(("+k+")/2-1)exp(-x/2))/(2^(("+k+")/2)gamma(("+k+")/2))]";
					}
					
					
					GeoElement[] ret = kernelA.getAlgebraProcessor().processAlgebraCommand(command, true);
					return ret;


				} else if (arg[1].isNumberValue()) {
					GeoElement[] ret = { kernelA.ChiSquared(c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1]) };
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
