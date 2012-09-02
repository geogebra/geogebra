package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoTextToUnicode;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

/**
 * TextToUnicode[text]
 */
public class CmdTextToUnicode extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdTextToUnicode(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:

			if (arg[0].isGeoText()) {
				
				AlgoTextToUnicode algo = new AlgoTextToUnicode(cons, c.getLabel(),
						(GeoText) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
