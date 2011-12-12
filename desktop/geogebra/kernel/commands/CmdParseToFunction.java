package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 * ParseToFunction
 */
class CmdParseToFunction extends CommandProcessorDesktop {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdParseToFunction(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		boolean ok;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok = arg[0].isGeoFunction()) && arg[1].isGeoText()) {

				GeoFunction fun = (GeoFunction) arg[0];
				String str = ((GeoText) arg[1]).getTextString();

				try {
					fun.set(kernel.getAlgebraProcessor().evaluateToFunction(
							str, true));
					fun.updateCascade();
				} catch (Exception e) {
					// eg ParseToFunction[f, "hello"]
					fun.set(kernel.getAlgebraProcessor().evaluateToFunction(
							"?", true));
					fun.updateCascade();
				}

				GeoElement[] ret = { fun };
				return ret;
			} else if (!ok) {
				throw argErr(app, c.getName(), arg[0]);
			} else {
				throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
