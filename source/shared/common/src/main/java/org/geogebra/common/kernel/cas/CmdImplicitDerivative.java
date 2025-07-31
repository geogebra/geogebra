package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * ImplicitDerivative[f(x,y)]
 */
public class CmdImplicitDerivative extends CommandProcessor implements UsesCAS {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdImplicitDerivative(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		GeoElement[] args = resArgs(c, info);
		if (args.length != 1) {
			throw argNumErr(c);
		}
		if (!(args[0] instanceof FunctionalNVar)) {
			throw argErr(c, args[0]);
		}

		AlgoImplicitDerivative algo = new AlgoImplicitDerivative(cons,
				c.getLabel(), (FunctionalNVar) args[0]);

		return new GeoElement[] { algo.getResult() };
	}

}
