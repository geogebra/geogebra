package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;
/**
 * Triangular[min,max,mode,value]
 * Triangular[min,max,mode,value,cumulative]
 * Triangular[min,max,mode,x] 
 */
public class CmdTriangular extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdTriangular(Kernel kernel) {
		super(kernel);
	}

	@Override
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
				if (arg[3].isGeoFunction() && ((GeoFunction)arg[3]).toString(StringTemplate.defaultTemplate).equals("x")) {
									
					// needed for eg Normal[1, 0.001, x] 
					StringTemplate highPrecision = StringTemplate.maxPrecision;
					String a = arg[0].getLabel(highPrecision);
					String b = arg[1].getLabel(highPrecision);
					String c = arg[2].getLabel(highPrecision);
					
					if (cumulative) {
						GeoElement[] ret = kernelA.getAlgebraProcessor().processAlgebraCommand( "If[x < "+a+", 0, If[x < "+c+", (x - ("+a+"))^2 / ("+b+" - ("+a+")) / ("+c+" - ("+a+")), If[x < "+b+", 1 + (x - ("+b+"))^2 / ("+b+" - ("+a+")) / ("+c+" - ("+b+")), 1]]]", true);
						return ret;
						
					} 
						GeoElement[] ret = kernelA.getAlgebraProcessor().processAlgebraCommand( "If[x < "+a+", 0, If[x < "+c+", 2(x - ("+a+")) / ("+b+" - ("+a+")) / ("+c+" - ("+a+")), If[x < "+b+", 2(x - ("+b+")) / ("+b+" - ("+a+")) / ("+c+" - ("+b+")), 0]]]", true );
						
						return ret;
					
					
				} else if (arg[3].isNumberValue()) 
				{
					// needed for eg Normal[1, 0.001, x] 
					StringTemplate highPrecision = StringTemplate.maxPrecision;
					String a = arg[0].getLabel(highPrecision);
					String b = arg[1].getLabel(highPrecision);
					String c = arg[2].getLabel(highPrecision);
					String x = arg[3].getLabel(highPrecision);
					
					GeoElement[] ret = kernelA.getAlgebraProcessor().processAlgebraCommand( "If["+x+" < "+a+", 0, If["+x+" < "+c+", ("+x+" - ("+a+"))^2 / ("+b+" - ("+a+")) / ("+c+" - ("+a+")), If["+x+" < "+b+", 1 + ("+x+" - ("+b+"))^2 / ("+b+" - ("+a+")) / ("+c+" - ("+b+")), 1]]]", true );
					return ret;
					
				}  else
					throw argErr(app, c2.getName(), arg[2]);
		}
			throw argErr(app, c2.getName(), !ok ? arg[0] : (ok2 ? arg[2] : arg[0]));

		default:
			throw argNumErr(app, c2.getName(), n);
		}
	}

}
