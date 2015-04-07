package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * UnitVector[ <GeoLine> ] UnitVector[ <GeoVector> ]
 */
public class CmdUnitVector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUnitVector(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoLine()) {

				AlgoUnitVector algo = algo(c.getLabel(), (GeoLineND) arg[0]);

				GeoElement[] ret = { (GeoElement) algo.getVector() };
				return ret;
			} else if (arg[0].isGeoVector()) {

				AlgoUnitVector algo = algo(c.getLabel(), (GeoVectorND) arg[0]);

				GeoElement[] ret = { (GeoElement) algo.getVector() };
				return ret;
			} else {
				return processNotLineNotVector(c, arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * process command in case arg is not a line nor a vector
	 * 
	 * @param c
	 * @param arg
	 * @return result
	 * @throws MyError
	 */
	protected GeoElement[] processNotLineNotVector(Command c, GeoElement arg)
			throws MyError {
		throw argErr(app, c.getName(), arg);
	}

	/**
	 * 
	 * @param label
	 *            vector name
	 * @param line
	 *            line
	 * @return algo for this line
	 */
	protected AlgoUnitVector algo(String label, GeoLineND line) {
		return new AlgoUnitVectorLine(cons, label, line);
	}

	/**
	 * 
	 * @param label
	 *            vector name
	 * @param v
	 *            vector
	 * @return algo for this vector
	 */
	protected AlgoUnitVector algo(String label, GeoVectorND v) {
		return new AlgoUnitVectorVector(cons, label, v);
	}
}
