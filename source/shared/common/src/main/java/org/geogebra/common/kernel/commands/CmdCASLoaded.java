package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoCasLoaded;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * CASLoaded[]
 * Returns if CAS subsystem is loaded or not.
 */
public class CmdCASLoaded extends CommandProcessor {

	/**
	 * Create new command processor
	 *
	 * @param kernel
	 *            kernel
	 */
	CmdCASLoaded(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();

		if (n == 0) {
			return isCasLoaded(c);
		}
		throw argNumErr(c);
	}

	private GeoElement[] isCasLoaded(Command c) {
		AlgoCasLoaded algo = new AlgoCasLoaded(cons);
		GeoBoolean result = algo.getResult();
		result.setLabel(c.getLabel());
		return new GeoElement[]{result};
	}

}
