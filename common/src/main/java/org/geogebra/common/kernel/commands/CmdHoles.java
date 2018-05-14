package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoHolesPolynomial;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;

/**
 * Created by kh on 18.01.2018.
 */
public class CmdHoles extends CommandProcessor {

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
				return new GeoElement[] {
						holes(c, (GeoFunction) arg[0]).toGeoElement() };
			}
			throw argErr(c, arg[0]);
		default:
			throw argNumErr(c);
		}
	}

	final private GeoList holes(Command c, GeoFunction gf) {

		AlgoHolesPolynomial algo = new AlgoHolesPolynomial(cons, c.getLabel(),
				gf);
		GeoList g = algo.getHolePoints();
		return g;
	}
}
