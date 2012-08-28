package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.statistics.AlgoNormal;
import geogebra.common.kernel.statistics.AlgoNormalDF;
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

		BooleanValue cumulative = null; // default for n=3 (false)
		arg = resArgs(c);

		switch (n) {
		case 4:
			if (arg[3].isBooleanValue()) {
				cumulative = (BooleanValue) arg[3];
			} else
				throw argErr(app, c.getName(), arg[3]);

			// fall through
		case 3:			
			if ((ok = arg[0].isNumberValue()) && (arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString(StringTemplate.defaultTemplate).equals("x")) {

					AlgoNormalDF algo = new AlgoNormalDF(cons, c.getLabel(), (NumberValue)arg[0], (NumberValue)arg[1], cumulative);
					return algo.getGeoElements();


				} else if (arg[2].isNumberValue()) {
					
					AlgoNormal algo = new AlgoNormal(cons, c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1], (NumberValue) arg[2]);
					
					GeoElement[] ret = { algo.getResult() };
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
