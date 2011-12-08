package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

	/**
	 *Erlang Distribution
	 */
	class CmdErlang extends CommandProcessor {

		/**
		 * Create new command processor
		 * 
		 * @param kernel
		 *            kernel
		 */
		public CmdErlang(Kernel kernel) {
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
					throw argErr(app, c.getName(), arg[1]);
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
							command = "If[x<0,0,gamma("+k+",("+l+")x)/("+k+"-1)!]";
						} else {
							command = "If[x<0,0,(("+l+")^("+k+")x^("+k+"-1)exp(-("+l+")x))/("+k+"-1)!]";
						}						
						
						GeoElement[] ret = (GeoElement[])kernel.getAlgebraProcessor().processAlgebraCommand(command, true);
						return ret;


					} else if (arg[2].isNumberValue()) {
						// needed for eg Normal[1, 0.001, x] 
						kernel.setTemporaryPrintFigures(15);
						String k = arg[0].getLabel();
						String l = arg[1].getLabel();
						String x = arg[2].getLabel();
						kernel.restorePrintAccuracy();
						GeoElement[] ret = (GeoElement[])kernel.getAlgebraProcessor().processAlgebraCommand("If[x<0,0,(("+l+")^("+k+")("+x+")^("+k+"-1)exp(-("+l+")("+x+")))/("+k+"-1)!]", true);
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
