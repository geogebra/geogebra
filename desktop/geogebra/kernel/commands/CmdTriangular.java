package geogebra.kernel.commands;

import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

public class CmdTriangular extends CommandProcessor {

	public CmdTriangular(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c2) throws MyError {
		int n = c2.getArgumentNumber();
		boolean ok, ok2 = true;
		GeoElement[] arg;

		boolean cumulative = false; // default for n=3
		arg = resArgs(c2);
		
		switch (n) {
		case 5:
			if (arg[4].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[4]).getBoolean();
			} else
				throw argErr(app, c2.getName(), arg[4]);
			
			// fall through
		case 4:			
			if ((ok = arg[0].isNumberValue()) && (ok2 = arg[1].isNumberValue()) && (arg[2].isNumberValue())) {
				if (arg[3].isGeoFunction() && ((GeoFunction)arg[3]).toString().equals("x")) {
									
					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String a = arg[0].getLabel();
					String b = arg[1].getLabel();
					String c = arg[2].getLabel();
					kernel.restorePrintAccuracy();
					
					if (cumulative) {
						GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "If[x < "+a+", 0, If[x < "+c+", (x - ("+a+"))� / ("+b+" - ("+a+")) / ("+c+" - ("+a+")), If[x < "+b+", 1 + (x - ("+b+"))� / ("+b+" - ("+a+")) / ("+c+" - ("+b+")), 1]]]", true);
						return ret;
						
					} else {
						GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "If[x < "+a+", 0, If[x < "+c+", 2(x - ("+a+")) / ("+b+" - ("+a+")) / ("+c+" - ("+a+")), If[x < "+b+", 2(x - ("+b+")) / ("+b+" - ("+a+")) / ("+c+" - ("+b+")), 0]]]", true );
						
						return ret;
					}
					
				} else if (arg[3].isNumberValue()) 
				{
					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String a = arg[0].getLabel();
					String b = arg[1].getLabel();
					String c = arg[2].getLabel();
					String x = arg[3].getLabel();
					kernel.restorePrintAccuracy();
					GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "If["+x+" < "+a+", 0, If["+x+" < "+c+", ("+x+" - ("+a+"))� / ("+b+" - ("+a+")) / ("+c+" - ("+a+")), If["+x+" < "+b+", 1 + ("+x+" - ("+b+"))� / ("+b+" - ("+a+")) / ("+c+" - ("+b+")), 1]]]", true );
					return ret;
					
				}  else
					throw argErr(app, c2.getName(), arg[2]);
		} else throw argErr(app, c2.getName(), !ok ? arg[0] : (ok2 ? arg[2] : arg[0]));

		default:
			throw argNumErr(app, c2.getName(), n);
		}
	}

}
