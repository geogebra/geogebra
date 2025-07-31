package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.MyError;

/**
 * TrigExpand[&lt;Function&gt;]
 * 
 * TrigExpand[&lt;Function&gt;, &lt;Target Function&gt;]
 * 
 * @author Zbynek Konecny
 */
public class CmdTrigExpand extends CommandProcessor implements UsesCAS {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdTrigExpand(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 1:
			if (arg[0].isCasEvaluableObject()) {
				AlgoTrigExpand algo = new AlgoTrigExpand(
						kernel.getConstruction(), c.getLabel(),
						(CasEvaluableFunction) arg[0], null, info);
				return new GeoElement[] { algo.getResult() };
			}
		case 2:
			if ((arg[0].isCasEvaluableObject())
					&& (arg[1] instanceof GeoFunction)) {
				AlgoTrigExpand algo = new AlgoTrigExpand(
						kernel.getConstruction(), c.getLabel(),
						(CasEvaluableFunction) arg[0], (GeoFunction) arg[1],
						info);
				return new GeoElement[] { algo.getResult() };
			}
			throw argErr(c, arg[0]);

			// more than one argument
		default:
			throw argNumErr(c);
		}
	}

}
