package geogebra.common.kernel.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoIntervalMidpoint;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoInterval;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.main.MyError;


/**
 * Midpoint[ <GeoConic> ] Midpoint[ <GeoPoint>, <GeoPoint> ]
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
			return process1(c,arg[0]);

		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				return twoPoints(c.getLabel(),
						(GeoPointND) arg[0], (GeoPointND) arg[1]) ;
			}
			throw argErr(app, c.getName(), getBadArg(ok,arg));
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
	
	/**
	 * 
	 * @param label
	 * @param segment
	 * @return midpoint for segment
	 */
	protected GeoElement[] segment(String label, GeoSegmentND segment){
		GeoElement[] ret = { getAlgoDispatcher().Midpoint(label, (GeoSegment) segment) };
		return ret;
	}
	

	/**
	 * process when 1 arg
	 * @param c
	 * @param arg
	 * @return result
	 * @throws MyError
	 */
	protected GeoElement[] process1(Command c, GeoElement arg) throws MyError {
		if (arg.isGeoConic()) {
			return conic(c.getLabel(), (GeoConicND) arg);
		} else if (arg.isGeoSegment()) {
			return segment(c.getLabel(), (GeoSegmentND) arg);
		} else if (arg.isGeoInterval()) {
			AlgoIntervalMidpoint algo = new AlgoIntervalMidpoint(cons, c.getLabel(),
					(GeoInterval) arg);

			GeoElement[] ret = { algo.getResult() };
			return ret;
		} else
			throw argErr(app, c.getName(), arg);
	}
	
	/**
	 * 
	 * @param label
	 * @param conic
	 * @return midpoint for conic
	 */
	protected GeoElement[] conic(String label, GeoConicND conic){
		GeoElement[] ret = { (GeoElement) getAlgoDispatcher().Center(label, conic) };
		return ret;
	}
	
	/**
	 * 
	 * @param label
	 * @param p1
	 * @param p2
	 * @return midpoint for two points
	 */
	protected GeoElement[] twoPoints(String label, GeoPointND p1, GeoPointND p2){
		GeoElement[] ret = { getAlgoDispatcher().Midpoint(label, (GeoPoint) p1, (GeoPoint) p2) };
		return ret;
	}
}