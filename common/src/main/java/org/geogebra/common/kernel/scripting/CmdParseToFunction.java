package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDependentGeoCopy;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * ParseToFunction
 */
public class CmdParseToFunction extends CommandProcessor {

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
			if ((ok = (arg[0].isGeoFunction() || arg[0].isGeoFunctionNVar()))
					&& arg[1].isGeoText()) {

				GeoElement fun = arg[0];
				if (!fun.isLabelSet()) {
					AlgoElement algo = fun.getParentAlgorithm();
					if (algo instanceof AlgoDependentGeoCopy) {
						fun = algo.getInput(0);
					}
				}

				String str = ((GeoText) arg[1]).getTextString();

				try {
					GeoElement parsed = arg[0].isGeoFunction() ? kernelA
							.getAlgebraProcessor()
							.evaluateToFunction(str, true) : kernelA
							.getAlgebraProcessor().evaluateToFunctionNVar(str,
									true);
					fun.set(parsed);
					fun.updateCascade();
				} catch (Exception e) {
					// eg ParseToFunction[f, "hello"]
					fun.set(kernelA.getAlgebraProcessor().evaluateToFunction(
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
