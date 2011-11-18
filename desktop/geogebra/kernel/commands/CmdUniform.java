package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoBoolean;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunction;
import geogebra.main.MyError;

	class CmdUniform extends CommandProcessor {

		public CmdUniform(Kernel kernel) {
			super(kernel);
		}

		public GeoElement[] process(Command c) throws MyError {
			int n = c.getArgumentNumber();
			boolean ok;
			GeoElement[] arg;

			boolean cumulative = false; // default for n=3
			arg = resArgs(c);
			
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
				if ((ok = arg[0].isNumberValue()) && (arg[1].isNumberValue())) {
					if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString().equals("x")) {
										
						// needed for eg Normal[1, 0.001, x] 
						kernel.setTemporaryPrintFigures(15);
						String a = arg[0].getLabel();
						String b = arg[1].getLabel();
						kernel.restorePrintAccuracy();
						
						if (cumulative) {
							GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "If[x<Min["+a+","+b+"],0,If[x>Max["+a+","+b+"],1,(x-Min["+a+","+b+"])/abs("+b+"-("+a+"))]]", true );
							
							return ret;
							
						} else {
							GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "If[x<Min["+a+","+b+"],0,If[x>Max["+a+","+b+"],0,1/abs("+b+"-("+a+"))]]", true );
							
							return ret;
						}
						
					} else if (arg[2].isNumberValue()) 
					{
						// needed for eg Normal[1, 0.001, x] 
						kernel.setTemporaryPrintFigures(15);
						String a = arg[0].getLabel();
						String b = arg[1].getLabel();
						String x = arg[2].getLabel();
						kernel.restorePrintAccuracy();
						GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "If["+x+"<Min["+a+","+b+"],0,If["+x+">Max["+a+","+b+"],1,("+x+"-Min["+a+","+b+"])/abs("+b+"-("+a+"))]]", true );
						return ret;
						
					}  else
						throw argErr(app, c.getName(), arg[2]);
			} else throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

			default:
				throw argNumErr(app, c.getName(), n);
			}
		}

	}
