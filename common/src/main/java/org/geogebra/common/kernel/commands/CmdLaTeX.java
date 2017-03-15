package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoLaTeX;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * FormulaText[ <GeoElement> ]
 */
public class CmdLaTeX extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLaTeX(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c, info);

		switch (n) {
		case 1:
			AlgoLaTeX algo = new AlgoLaTeX(cons, c.getLabel(), arg[0]);

			GeoElement[] ret = { algo.getGeoText() };
			return ret;

		case 2:
			if (arg[1].isGeoBoolean()) {
				GeoElement[] ret2 = { LaTeX(c.getLabel(), arg[0],
						(GeoBoolean) arg[1], null) };
				return ret2;
			}
			throw argErr(app, c, arg[1]);

		case 3:
			if (arg[1].isGeoBoolean() && arg[2].isGeoBoolean()) {
				GeoElement[] ret2 = { LaTeX(c.getLabel(), arg[0],
						(GeoBoolean) arg[1], (GeoBoolean) arg[2]) };
				return ret2;
			}

			else if (!arg[1].isGeoBoolean()) {
				throw argErr(app, c, arg[1]);
			} else {
				throw argErr(app, c, arg[2]);
			}

		default:
			throw argNumErr(app, c, n);
		}
	}

	/**
	 * LaTeX of geo.
	 */
	final private GeoText LaTeX(String label, GeoElement geo,
			GeoBoolean substituteVars, GeoBoolean showName) {
		AlgoLaTeX algo = new AlgoLaTeX(cons, label, geo, substituteVars,
				showName);
		GeoText t = algo.getGeoText();
		return t;
	}

}
