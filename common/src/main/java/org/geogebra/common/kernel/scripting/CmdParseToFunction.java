package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDependentGeoCopy;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
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
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		if (!info.isScripting()) {
			return new GeoElement[0];
		}
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
						fun = algo.getInput(0).toGeoElement();
					}
				}

				String str = ((GeoText) arg[1]).getTextString();

				try {
					GeoElement parsed = arg[0].isGeoFunction()
							? kernel.getAlgebraProcessor()
									.evaluateToFunction(str, true)
							: kernel.getAlgebraProcessor()
									.evaluateToFunctionNVar(str, true, false);
					fun.set(parsed);
					fun.updateCascade();
				} catch (Exception e) {
					// eg ParseToFunction[f, "hello"]
					fun.set(kernel.getAlgebraProcessor()
							.evaluateToFunction("?", true));
					fun.updateCascade();
				}

				return fun.asArray();
			} else if (!ok) {
				throw argErr(c, arg[0]);
			} else {
				throw argErr(c, arg[1]);
			}

		default:
			throw argNumErr(c);
		}
	}
}
