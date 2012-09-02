package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

/**
 *FractionText
 */
public class CmdContinuedFraction extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdContinuedFraction(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		boolean[] ok = new boolean[3];
		switch (n) {
		case 1:

			if (arg[0].isNumberValue()) {
				GeoElement[] ret = { ContinuedFraction(c.getLabel(),
						(NumberValue) arg[0],null,null) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		case 2:
			if ((ok[0]=arg[0].isNumberValue())&&(ok[1]=arg[1].isGeoBoolean())) {
				GeoElement[] ret = { ContinuedFraction(c.getLabel(),
						(NumberValue) arg[0],null,(GeoBoolean) arg[1]) };
				return ret;
			}
			if ((ok[0]=arg[0].isNumberValue())&&(ok[1]=arg[1].isNumberValue())) {
				GeoElement[] ret = { ContinuedFraction(c.getLabel(),
						(NumberValue) arg[0],(NumberValue) arg[1],null) };
				return ret;
			}
			
			throw argErr(app, c.getName(), getBadArg(ok,arg));
		case 3:

			
			if ((ok[0]=arg[0].isNumberValue())&&(ok[1]=arg[1].isNumberValue())&&(ok[2]=arg[2].isGeoBoolean())) {
				GeoElement[] ret = { ContinuedFraction(c.getLabel(),
						(NumberValue) arg[0],(NumberValue) arg[1],(GeoBoolean) arg[2]) };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok,arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
	
	
	final private GeoText ContinuedFraction(String label, NumberValue num,NumberValue level,GeoBoolean shortHand) {
		AlgoContinuedFraction algo = new AlgoContinuedFraction(cons, label, num,level,shortHand);
		GeoText text = algo.getResult();
		return text;
	}
}
