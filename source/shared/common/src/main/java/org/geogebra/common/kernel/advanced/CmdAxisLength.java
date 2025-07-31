package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.main.MyError;

/**
 * FirstAxisLength[ &lt;GeoConic&gt; ]
 */
public class CmdAxisLength extends CommandProcessor {

	private int axisId;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxisLength(Kernel kernel, int axisId) {
		super(kernel);
		this.axisId = axisId;
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {

				AlgoAxisLength algo = new AlgoAxisLength(cons,
						c.getLabel(), (GeoConicND) arg[0], axisId);

				GeoElement[] ret = { algo.getLength() };
				return ret;
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
