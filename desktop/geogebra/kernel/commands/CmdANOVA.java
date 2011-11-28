package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

/**
 * ANOVA test 
 */
class CmdANOVA extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdANOVA(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1: // list of lists, result of XML conversion
			if ((ok[0] = (arg[0].isGeoList()))) {
				GeoList list = (GeoList) arg[0];

				if (list.size() == 0)
					throw argErr(app, c.getName(), arg[0]);

				if (list.get(0).isGeoList()) { 
					GeoElement[] ret = { kernel.ANOVATest(c.getLabel(),
							(GeoList) arg[0]) };
					return ret;

				} else {
					throw argErr(app, c.getName(), arg[0]);
				}
			}

		default:
			GeoList list = wrapInList(kernel, arg, arg.length, GeoClass.LIST);
			if (list != null) {
				GeoElement[] ret = { kernel.ANOVATest(c.getLabel(), list) };
				return ret;
			}
			else{
				// null ret should mean that an arg is not a GeoList
				// so find the bad one
				for(int i = 0; i <= n; i++){
					if(!arg[i].isGeoList())
						throw argErr(app, c.getName(), arg[i]);
				}
				// throw error for any other reason ... 
				throw argErr(app, c.getName(), arg[0]);
			}
		}
	}
}
