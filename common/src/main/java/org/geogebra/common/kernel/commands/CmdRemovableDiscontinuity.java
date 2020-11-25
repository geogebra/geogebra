package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoRemovableDiscontinuity;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.MyError;

public class CmdRemovableDiscontinuity extends CommandProcessor implements UsesCAS {

	/**
	 * @param kernel kernel
	 */
	public CmdRemovableDiscontinuity(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		switch (n) {
		case 1:
			GeoElement[] arg = resArgs(c);
			GeoElement element = arg[0];
			if (element.isGeoFunction()) {
				return removableDiscontinuity((GeoFunction) element, c);
			}
			throw argErr(c, element);
		default:
			throw argNumErr(c);
		}
	}

	private GeoElement[] removableDiscontinuity(GeoFunction function, Command cmd) {
		AlgoRemovableDiscontinuity algo
				= new AlgoRemovableDiscontinuity(cons, function, cmd.getLabels());
		return algo.getOutput();
	}
}
