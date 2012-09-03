package geogebra.common.kernel.cas;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.advanced.AlgoTextLength;
import geogebra.common.kernel.algos.AlgoArcLength;
import geogebra.common.kernel.algos.AlgoLengthSegment;
import geogebra.common.kernel.algos.AlgoLengthVector;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.main.MyError;

/**
 * Length[ <GeoVector> ] Length[ <GeoPoint> ] Length[ <GeoList> ] Victor Franco
 * 18-04-2007: add Length[ <Function>, <Number>, <Number> ] add Length[
 * <Function>, <Point>, <Point> ] add Length[ <Curve>, <Number>, <Number> ] add
 * Length[ <Curve>, <Point>, <Point> ]
 */
public class CmdLength extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLength(Kernel kernel) {
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
			if (arg[0].isGeoVector() || arg[0].isGeoPoint()) {
				
				AlgoLengthVector algo = new AlgoLengthVector(cons, c.getLabel(),
						(GeoVec3D) arg[0]);

				GeoElement[] ret = { algo.getLength() };
				return ret;
			} else if (arg[0].isGeoList()) {
				GeoElement[] ret = { getAlgoDispatcher().Length(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else if (arg[0].isGeoText()) {
				
				AlgoTextLength algo = new AlgoTextLength(cons, c.getLabel(),
						(GeoText) arg[0]);

				GeoElement[] ret = { algo.getLength() };
				return ret;
			} else if (arg[0].isGeoLocus()) {
				GeoElement[] ret = { getAlgoDispatcher().Length(c.getLabel(),
						(GeoLocus) arg[0]) };
				return ret;
			} else if (arg[0].isGeoSegment()) {
				
				AlgoLengthSegment algo = new AlgoLengthSegment(cons, c.getLabel(),
						(GeoSegmentND) arg[0]);

				GeoElement[] ret = { algo.getLength() };
				return ret;
			
			} else if (arg[0].isGeoConicPart()) {
				// Arc length
				
				AlgoArcLength algo = new AlgoArcLength(cons, c.getLabel(),
						(GeoConicPart) arg[0]);

				GeoElement[] ret = { algo.getArcLength() };
				return ret;
			
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

			// Victor Franco 18-04-2007
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoNumeric()))
					&& (ok[2] = (arg[2].isGeoNumeric()))) {
				
				AlgoLengthFunction algo = new AlgoLengthFunction(cons, c.getLabel(),
						(GeoFunction) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2]);

				GeoElement[] ret = { algo.getLength() };
				return ret;
			}

			else if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				
				AlgoLengthFunction2Points algo = new AlgoLengthFunction2Points(cons,
						c.getLabel(),
						(GeoFunction) arg[0], (GeoPoint) arg[1],
						(GeoPoint) arg[2]);

				GeoElement[] ret = { algo.getLength() };
				return ret;
			}

			else if ((ok[0] = (arg[0].isGeoCurveCartesian()))
					&& (ok[1] = (arg[1].isGeoNumeric()))
					&& (ok[2] = (arg[2].isGeoNumeric()))) {
				
				AlgoLengthCurve algo = new AlgoLengthCurve(cons, c.getLabel(),
						(GeoCurveCartesian) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2]);

				GeoElement[] ret = { algo.getLength() };
				return ret;

			}

			else if ((ok[0] = (arg[0].isGeoCurveCartesian()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				
				AlgoLengthCurve2Points algo = new AlgoLengthCurve2Points(cons, c.getLabel(),
						(GeoCurveCartesian) arg[0], (GeoPoint) arg[1],
						(GeoPoint) arg[2]);

				GeoElement[] ret = { algo.getLength() };
				return ret;
			}

			else {

				throw argErr(app, c.getName(), getBadArg(ok,arg));
			}

			// Victor Franco 18-04-2007 (end)
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
