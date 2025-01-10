package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoAxesQuadricND;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.main.MyError;

/**
 * Axes[ &lt;GeoConic&gt; ]
 */
public class CmdAxes extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxes(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic/quadric
			if (arg[0] instanceof GeoQuadricND) {
				AlgoAxesQuadricND algo = kernel.getAlgoDispatcher().axesConic((GeoQuadricND) arg[0],
						c.getLabels());
				return (GeoElement[]) algo.getAxes();

			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
