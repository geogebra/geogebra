package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;

/**
 *Weibull Distribution
 */
public class CmdWeibull extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdWeibull(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		
		arg = resArgs(c);

		boolean cumulative = false; // default for n=3
		switch (n) {
		case 4:
			if (!arg[2].isGeoFunction() || !((GeoFunction)arg[2]).toString(StringTemplate.defaultTemplate).equals("x")) {
				throw argErr(app, c.getName(), arg[2]);
			}
			
			if (arg[3].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[3]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[3]);

			// fall through
		case 3:			
			if ((ok[0] = arg[0].isNumberValue()) && (ok[1] = arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString(StringTemplate.defaultTemplate).equals("x")) {

					// needed for eg Normal[1, 0.001, x] 
					StringTemplate highPrecision = StringTemplate.maxPrecision;
					String k = arg[0].getLabel(highPrecision);
					String l = arg[1].getLabel(highPrecision);
					
					String command;
					
					if (cumulative) {
						command = "If[x<0,0,1-exp(-(x/("+l+"))^("+k+"))]";
					} else {
						command = "If[x<0,0,("+k+")/("+l+")(x/("+l+"))^("+k+"-1)exp(-(x/("+l+"))^("+k+"))]";
					}
					
					
					GeoElement[] ret = kernelA.getAlgebraProcessor().processAlgebraCommand(command, true);
					return ret;


				} else if (arg[2].isNumberValue()) {
					GeoElement[] ret = { kernelA.Weibull(c.getLabel(),
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
