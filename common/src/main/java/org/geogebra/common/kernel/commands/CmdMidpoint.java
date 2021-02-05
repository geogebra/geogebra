package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoIntervalMidpoint;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.MyError;

/**
 * Midpoint[ &lt;GeoConic> ]
 * 
 * Midpoint[ &lt;GeoPoint>, &lt;GeoPoint> ]
 */
public class CmdMidpoint extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMidpoint(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			return process1(c, arg[0]);

		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				return twoPoints(c.getLabel(), (GeoPointND) arg[0],
						(GeoPointND) arg[1]);
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param segment
	 *            segment
	 * @return midpoint for segment
	 */
	protected GeoElement[] segment(String label, GeoSegmentND segment) {
		GeoElement mp = getAlgoDispatcher().midpoint((GeoSegment) segment);
		mp.setLabel(label);
		GeoElement[] ret = { mp };
		return ret;
	}

	/**
	 * process when 1 arg
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            single argument
	 * @return result
	 * @throws MyError
	 *             when argument type is wrong
	 */
	protected GeoElement[] process1(Command c, GeoElement arg) throws MyError {
		if (arg.isGeoConic()) {
			return conic(c.getLabel(), (GeoConicND) arg);
		} else if (arg.isGeoSegment()) {
			return segment(c.getLabel(), (GeoSegmentND) arg);
		} else if (arg.isGeoFunctionBoolean()) {
			AlgoIntervalMidpoint algo = new AlgoIntervalMidpoint(cons, (GeoFunction) arg);
			algo.getResult().setLabel(c.getLabel());
			GeoElement[] ret = { algo.getResult() };
			return ret;
		} else {
			throw argErr(c, arg);
		}
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param conic
	 *            conic
	 * @return midpoint for conic
	 */
	protected GeoElement[] conic(String label, GeoConicND conic) {
		GeoElement[] ret = {
				(GeoElement) getAlgoDispatcher().center(label, conic) };
		return ret;
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @return midpoint for two points
	 */
	protected GeoElement[] twoPoints(String label, GeoPointND p1,
			GeoPointND p2) {
		GeoElement[] ret = { getAlgoDispatcher().midpoint(label, (GeoPoint) p1,
				(GeoPoint) p2) };
		return ret;
	}
}