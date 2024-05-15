package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.main.MyError;

/**
 * FirstAxis[ &lt;GeoConic&gt; ]
 */
public class CmdAxis extends CommandProcessor {

	/** 0 for major, 1 for minor */
	protected int axisId;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxis(Kernel kernel, int axisId) {
		super(kernel);
		this.axisId = axisId;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {

				AlgoAxis algo = getAlgoAxisFirst(cons, c.getLabel(),
						(GeoConicND) arg[0]);

				GeoElement[] ret = { algo.getAxis().toGeoElement() };
				return ret;
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @param cons1
	 *            construction
	 * @param label
	 *            label
	 * @param geoConicND
	 *            conic
	 * @return axis algo
	 */
	protected AlgoAxis getAlgoAxisFirst(Construction cons1, String label,
			GeoConicND geoConicND) {

		return new AlgoAxis(cons1, label, geoConicND, axisId);
	}
}
