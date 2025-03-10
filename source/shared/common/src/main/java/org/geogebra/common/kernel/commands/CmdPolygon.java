package org.geogebra.common.kernel.commands;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Polygon[ &lt;GeoPoint&gt;, ..., &lt;GeoPoint&gt; ] Polygon[ &lt;GeoPoint&gt;,
 * &lt;GeoPoint&gt;, &lt;Number&gt;] for regular polygon
 */
public class CmdPolygon extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPolygon(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		arg = resArgs(c);

		return process(c, n, arg);
	}

	/**
	 * 
	 * @param c
	 *            command to process
	 * @param n
	 *            number of args
	 * @param arg
	 *            args already resolved
	 * @return list of resulting geos
	 * @throws MyError
	 *             error if problem occurs
	 */
	protected GeoElement[] process(Command c, int n, GeoElement[] arg)
			throws MyError {

		switch (n) {
		case 0:
			throw argNumErr(c);
		case 1:
			GeoList l = null;
			if (arg[0].isGeoList()) {
				l = (GeoList) arg[0];
			}
			if (arg[0].isGeoPoint()) {
				ArrayList<GeoElement> els = new ArrayList<>(1);
				els.add(arg[0]);
				AlgoDependentList adl = new AlgoDependentList(cons, els, false);
				l = adl.getGeoList();
			}
			if (l != null) {
				return getAlgoDispatcher().polygon(c.getLabels(), l);
			}
			throw argErr(arg[0], c);
		case 3:
			// regular polygon
			if (arg[0].isGeoPoint() && arg[1].isGeoPoint()
					&& arg[2] instanceof GeoNumberValue) {
				return regularPolygon(c.getLabels(), (GeoPointND) arg[0],
						(GeoPointND) arg[1], (GeoNumberValue) arg[2]);
			}

		default:
			// polygon for given points
			GeoPointND[] points = new GeoPointND[n];
			// check arguments
			boolean is3D = false;
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint())) {
					throw argErr(c, arg[i]);
				}
				points[i] = (GeoPointND) arg[i];
				is3D = checkIs3D(is3D, arg[i]);
			}
			// everything ok
			return polygon(c.getLabels(), points, is3D);
		}
	}

	/**
	 * 
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
	 * 
	 * @param labels
	 *            labels
	 * @param points
	 *            points
	 * @param is3D
	 *            if in 3D mode
	 * @return polygon for points
	 */
	protected GeoElement[] polygon(String[] labels, GeoPointND[] points,
			boolean is3D) {
		return kernel.polygon(labels, points);
	}

	/**
	 * 
	 * @param labels
	 *            labels
	 * @param A
	 *            first vertex
	 * @param B
	 *            second vertex
	 * @param n
	 *            number of vertices
	 * @return regular polygon
	 */
	protected GeoElement[] regularPolygon(String[] labels, GeoPointND A,
			GeoPointND B, GeoNumberValue n) {
		return getAlgoDispatcher().regularPolygon(labels, A, B, n);
	}
}
