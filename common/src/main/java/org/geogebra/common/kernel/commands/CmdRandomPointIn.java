package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoRandomPoint;
import org.geogebra.common.kernel.algos.AlgoRandomPointInConic;
import org.geogebra.common.kernel.algos.AlgoRandomPointInPoints;
import org.geogebra.common.kernel.algos.AlgoRandomPointInPolygon;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

public class CmdRandomPointIn extends CommandProcessor {

	public CmdRandomPointIn(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);
		
		if (n == 1) {
			if (arg[0].isGeoPolygon()) {

				AlgoRandomPointInPolygon algo = new AlgoRandomPointInPolygon(
						cons, c.getLabel(),
						(GeoPolygon) arg[0]);

				GeoElement[] ret = { algo.getRandomPoint() };
				return ret;
			} else if (arg[0].isGeoConic()) {
				AlgoRandomPointInConic algo = new AlgoRandomPointInConic(cons,
						c.getLabel(), (GeoConicND) arg[0]);

				GeoElement[] ret = { algo.getRandomPoint() };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}
		} else if (n == 4 && arg[0].isNumberValue() && arg[1].isNumberValue()
				&& arg[2].isNumberValue() && arg[3].isNumberValue()) {

			return randomPoint(c.getLabel(), (GeoNumeric) arg[0],
					(GeoNumeric) arg[1], (GeoNumeric) arg[2],
					(GeoNumeric) arg[3]);

		} else if (n > 2) {
			arg = resArgs(c);
			GeoPointND[] points = new GeoPointND[n];

			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint())) {
					throw argErr(app, c.getName(), arg[i]);
				}
				points[i] = (GeoPointND) arg[i];
			}
			// everything ok

			AlgoRandomPointInPoints algo = new AlgoRandomPointInPoints(cons, c.getLabel(), points);

			GeoElement[] ret = { algo.getRandomPoint() };
			return ret;
		} else {
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * @param label
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return random point (p,q) where p is between a & b and q is between c &
	 *         d
	 */
	protected GeoElement[] randomPoint(String label, GeoNumeric a,
			GeoNumeric b, GeoNumeric c, GeoNumeric d) {
		AlgoRandomPoint arp = new AlgoRandomPoint(cons, label, a, b, c, d);
		return new GeoElement[] { arp.getOutput(0) };
	}

}
