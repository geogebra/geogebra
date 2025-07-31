package org.geogebra.common.kernel.commands;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoLocusStroke;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Polyline[ &lt;GeoPoint&gt;, ..., &lt;GeoPoint&gt; ]
 */
public class CmdPolyLine extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPolyLine(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		switch (n) {
		case 0:
			throw argNumErr(c);
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isGeoList()) {
				return polyLine(c.getLabel(), (GeoList) arg[0]);
			}
			throw argErr(c, arg[0]);
		case 2:
			arg = resArgs(c, info);
			if (arg[0].isGeoList()) {

				if (!arg[1].isGeoBoolean()) {
					throw argErr(c, arg[1]);
				}

				return polyLine(c.getLabel(), (GeoList) arg[0]);
			}
			if (arg[0].isGeoPoint()) {

				if (!arg[1].isGeoPoint() && !(arg[1].isGeoBoolean()
						&& arg[1].evaluateDouble() > 0)) {
					throw argErr(c, arg[1]);
				}

				return genericPolyline(arg[1], arg, c, info);
			}
			throw argErr(c, arg[0]);
		default:
			GeoElement lastArg = resArgSilent(c, n - 1, info.withLabels(false));
			return genericPolyline(lastArg, null, c, info);
		}
	}

	private GeoElement[] genericPolyline(GeoElement lastArg, GeoElement[] arg0,
			Command c, EvalInfo info) {
		boolean penStroke = false;
		int size = c.getArgumentNumber();
		if (lastArg.isGeoBoolean()) {
			// pen stroke
			// last argument is boolean (normally true)
			size = size - 1;
			penStroke = ((GeoBoolean) lastArg).getBoolean();
		}
		if (penStroke) {
			ArrayList<MyPoint> myPoints = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				MyVecNode vec = (MyVecNode) c.getArgument(i).unwrap();
				myPoints.add(new MyPoint(vec.getX().evaluateDouble(),
						vec.getY().evaluateDouble()));
			}
			AlgoLocusStroke algo = new AlgoLocusStroke(cons, myPoints);
			algo.getOutput(0).setLabel(c.getLabel());
			return algo.getOutput();
		}
		GeoElement[] arg = arg0 == null ? resArgs(c, info) : arg0;
		// polygon for given points
		GeoPointND[] points = new GeoPointND[size];
		// check arguments
		boolean is3D = false;
		for (int i = 0; i < size; i++) {
			if (!(arg[i].isGeoPoint())) {
				throw argErr(c, arg[i]);
			}
			points[i] = (GeoPointND) arg[i];
			is3D = checkIs3D(is3D, arg[i]);
		}
		// everything ok
		return polyLine(c.getLabel(), points, is3D);
	}

	/**
	 * @param label
	 *            label
	 * @param pointList
	 *            input points
	 * @return polyline
	 */
	protected GeoElement[] polyLine(String label, GeoList pointList) {
		AlgoPolyLine algo = new AlgoPolyLine(cons, pointList);
		algo.getOutput(0).setLabel(label);
		return algo.getOutput();
	}

	/**
	 * @param is3D
	 *            true if already 3D
	 * @param geo
	 *            geo to check
	 * @return true if is already 3D or geo is 3D
	 */
	protected boolean checkIs3D(boolean is3D, GeoElement geo) {
		return false; // check only in 3D mode
	}

	/**
	 * @param label
	 *            output label
	 * @param points
	 *            polyline
	 * @param is3D
	 *            whether it's a 3D object
	 * @return polyline
	 */
	protected GeoElement[] polyLine(String label, GeoPointND[] points,
			boolean is3D) {
		return kernel.polyLine(label, points);
	}
}