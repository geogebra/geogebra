package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;

/**
 * Normal[mean,SD,val]
 * Normal[mean,SD,x]
 * Normal[mean,SD,x, cumulative]
 * 
 * adapted from CmdMax by Michael Borcherds 2008-01-20
 */
public class CmdNormal extends CommandProcessor {

	/**
	 * Creates new processor for Normal command
	 * @param kernel Kernel
	 */
	public CmdNormal(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;

		boolean cumulative = false; // default for n=3
		arg = resArgs(c);
		
		switch (n) {
		case 4:
			if (arg[3].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[3]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[3]);
			
			// fall through
		case 3:			
			if ((ok = arg[0].isNumberValue()) && (arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString(StringTemplate.defaultTemplate).equals("x")) {
									
					// needed for eg Normal[1, 0.001, x] 
					StringTemplate highPrecision = StringTemplate.maxPrecision;
					
					String mean = arg[0].getLabel(highPrecision);
					String sd = arg[1].getLabel(highPrecision);
					
					if (cumulative) {
						return kernelA.getAlgebraProcessor().processAlgebraCommand( "(erf((x-("+mean+"))/(sqrt(2)*abs("+sd+"))) + 1)/2", true );
					}
					return kernelA.getAlgebraProcessor().processAlgebraCommand( "exp(-((x-("+mean+"))/("+sd+"))^2/2)/(sqrt(2*pi)*abs("+sd+"))", true );					
				} else if (arg[2].isNumberValue()) 
				{
					GeoElement[] ret = { 
							kernelA.Normal(c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1], (NumberValue) arg[2]) };
					return ret;
					
				}  else
					throw argErr(app, c.getName(), arg[2]);
		} 
			throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
