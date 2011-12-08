package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoText;

/**
 *ParseToNumber
 */
class CmdParseToNumber extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdParseToNumber(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		boolean ok;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (ok = arg[0].isGeoNumeric() && arg[1].isGeoText()) {

				GeoNumeric num = (GeoNumeric) arg[0];
				String str = ((GeoText) arg[1]).getTextString();

				try {
					num.setValue(kernel.getAlgebraProcessor()
							.evaluateToNumeric(str, true).getDouble());
					num.updateCascade();
				} catch (Exception e) {
					num.setUndefined();
					num.updateCascade();
				}

				GeoElement[] ret = { num };
				return ret;
			} else if (!ok)
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
