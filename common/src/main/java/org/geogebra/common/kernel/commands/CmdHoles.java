package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoHolesPolynomial;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.MyError;

/**
 * Created by kh on 18.01.2018.
 */
public class CmdHoles extends CommandProcessor implements UsesCAS {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdHoles(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1: // Holes[f]
			arg = resArgs(c);
			ok[0] = arg[0].isGeoFunction();
			if (ok[0]) {
				return holes((GeoFunction) arg[0], c);
			}
			throw argErr(c, arg[0]);
		default:
			throw argNumErr(c);
		}
	}

	private GeoElement[] holes(GeoFunction gf, Command cmd) {
		AlgoHolesPolynomial algo = new AlgoHolesPolynomial(cons, gf, cmd.getLabels());
		return algo.getOutput();
	}
}
