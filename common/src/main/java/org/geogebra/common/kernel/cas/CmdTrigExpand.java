package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.MyError;

/**
 * TrigExpand[<Function>] TrigExpand[<Function>, <Target Function>]
 * 
 * @author Zbynek Konecny
 */
public class CmdTrigExpand extends CommandProcessor {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdTrigExpand(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if ((arg[0].isCasEvaluableObject())) {
				AlgoTrigExpand algo = new AlgoTrigExpand(
						kernelA.getConstruction(), c.getLabel(),
						(CasEvaluableFunction) arg[0], null);
				return new GeoElement[] { algo.getResult() };
			}
		case 2:
			if ((arg[0].isCasEvaluableObject())
					&& (arg[1] instanceof GeoFunction)) {
				AlgoTrigExpand algo = new AlgoTrigExpand(
						kernelA.getConstruction(), c.getLabel(),
						(CasEvaluableFunction) arg[0], (GeoFunction) arg[1]);
				return new GeoElement[] { algo.getResult() };
			}
			throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
