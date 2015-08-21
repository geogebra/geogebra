package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.algos.AlgoPointVector;
import org.geogebra.common.kernel.algos.AlgoPointsFromList;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * Point[ <Path> ] Point[ <Point>, <Vector> ]
 */
public class CmdPoint extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPoint(Kernel kernel) {
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
			// need to check isGeoList first as {1,2} can be a Path but we want
			// Point[{1,2}] to create a point
			if (arg[0].isGeoList()
					&& ((GeoList) arg[0]).getGeoElementForPropertiesDialog()
							.isGeoNumeric()) {
				if ((((GeoList) arg[0]).get(0).isGeoNumeric() && ((GeoList) arg[0]).size() == 2)
						|| (((GeoList) arg[0]).get(0).isGeoList() && ((GeoList) ((GeoList) arg[0]).get(0)).size() == 2)) {
					
					AlgoPointsFromList algo = new AlgoPointsFromList(cons,
						c.getLabels(), !cons.isSuppressLabelsActive(),
						(GeoList) arg[0]);

					GeoElement[] ret = algo.getPoints();

					return ret;
				}
			} else if (arg[0].isPath()) {
				GeoElement[] ret = { getAlgoDispatcher().Point(c.getLabel(),
						(Path) arg[0], null) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isPath()))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))) {
				GeoElement[] ret = { point(c.getLabel(), (Path) arg[0],
						(GeoNumberValue) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoVector()))) {

				GeoElement[] ret = { (GeoElement) point(c.getLabel(),
						(GeoPointND) arg[0], (GeoVectorND) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param path
	 *            path
	 * @param value
	 *            parameter
	 * @return point on path with parameter
	 */
	protected GeoElement point(String label, Path path, GeoNumberValue value) {
		return getAlgoDispatcher().Point(label, path, value);
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param point
	 *            point
	 * @param vector
	 *            vector
	 * @return point + vector
	 */
	protected GeoPointND point(String label, GeoPointND point,
			GeoVectorND vector) {
		AlgoPointVector algo = new AlgoPointVector(cons, label, point, vector);

		return algo.getQ();
	}

}