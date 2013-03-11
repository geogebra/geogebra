package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoConicPartConicParameters;
import geogebra.common.kernel.algos.AlgoConicPartConicPoints;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.main.MyError;

/**
 * Sector[ <GeoConic>, <Number>, <Number> ] Sector[ <GeoConic>, <GeoPoint>,
 * <GeoPoint> ]
 */
public class CmdSector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSector(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				
				AlgoConicPartConicParameters algo = new AlgoConicPartConicParameters(
						cons, c.getLabel(),
						(GeoConic) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2], GeoConicNDConstants.CONIC_PART_SECTOR);

				GeoElement[] ret = { algo.getConicPart() };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				
				AlgoConicPartConicPoints algo = new AlgoConicPartConicPoints(cons,
						c.getLabel(), (GeoConic) arg[0],
						(GeoPoint) arg[1], (GeoPoint) arg[2], GeoConicNDConstants.CONIC_PART_SECTOR);

				GeoElement[] ret = { algo.getConicPart() };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				else
					throw argErr(app, c.getName(), arg[2]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
