package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPlaneThreePoints;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdCircle;
import org.geogebra.common.kernel.geos.GeoAxis;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Circle command
 *
 */
public class CmdCircle3D extends CmdCircle {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdCircle3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] process2(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		if ((ok[0] = (arg[0] instanceof GeoLineND))
				&& (ok[1] = (arg[1].isGeoPoint()))) {
			GeoElement[] ret = { kernel.getManager3D().circle3D(c.getLabel(),
					(GeoLineND) arg[0], (GeoPointND) arg[1]) };
			return ret;
		}

		return super.process2(c, arg, ok);
	}

	@Override
	protected GeoElement circle(String label, GeoPointND a, GeoNumberValue v) {

		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientation(kernel, app);
		if (orientation == null) {
			if (a.isGeoElement3D()) {
				orientation = kernel.getXOYPlane();
			} else {
				// use 2D algo
				return super.circle(label, a, v);
			}
		}

		return kernel.getManager3D().circle3D(label, a, v, orientation);
	}

	@Override
	protected GeoElement circle(String label, GeoPointND a, GeoPointND b) {

		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientation(kernel, app);
		if (orientation == null) {
			if (a.isGeoElement3D() || b.isGeoElement3D()) {
				orientation = kernel.getXOYPlane();
			} else {
				// use 2D algo
				return super.circle(label, a, b);
			}
		}

		return kernel.getManager3D().circle3D(label, a, b, orientation);
	}

	@Override
	protected GeoElement[] process3(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		if ((ok[0] = (arg[0].isGeoPoint()))
				&& (ok[2] = (arg[2] instanceof GeoDirectionND))) {

			// x=0 is ambiguous so convert eg
			// Circle((0,0,0), 1, x=0)
			// to Circle((0, 0, 0), 1, Plane((0, 0, 0), (0, 0, 1), (1, 0, 0)))
			// Circle((0, 0, 0), 1, 4x + 3y = 0)
			// unambiguous, doesn't need converting
			// Circle((0,0,0), 1, Line((0,0),(0,1)))
			// Circle((0,0,0), 1, xAxis)
			if (arg[2] instanceof GeoLine
					&& arg[2].getParentAlgorithm() == null
					&& !(arg[2] instanceof GeoAxis)) {

				GeoLine line = (GeoLine) arg[2];

				double x = line.x;
				double y = line.y;
				double z = line.z;

				GeoPoint3D pt1 = new GeoPoint3D(cons);
				GeoPoint3D pt2 = new GeoPoint3D(cons);
				GeoPoint3D pt3 = new GeoPoint3D(cons);

				// convert eg 4x+3y=5 to vertical plane
				// perpendicular to 4x+3y=5

				// point on line
				double x1 = -z * x / (x * x + y * y);
				double y1 = -z * y / (x * x + y * y);

				pt1.setCoords(x1, y1, 0, 1);
				pt2.setCoords(x1, y1, 1, 1);
				// second point on line
				pt3.setCoords(x1 + x, y1 + y, 0, 1);

				AlgoPlaneThreePoints algo = new AlgoPlaneThreePoints(cons, pt1,
						pt2, pt3);

				// converted plane
				arg[2] = algo.getOutput(0);

			}

			if (arg[1] instanceof GeoNumberValue) {
				GeoElement[] ret = { kernel.getManager3D().circle3D(
						c.getLabel(), (GeoPointND) arg[0],
						(GeoNumberValue) arg[1], (GeoDirectionND) arg[2]) };
				return ret;
			} else if (arg[1].isGeoPoint()) {
				GeoElement[] ret = { kernel.getManager3D().circle3D(
						c.getLabel(), (GeoPointND) arg[0], (GeoPointND) arg[1],
						(GeoDirectionND) arg[2]) };
				return ret;
			}
			ok[1] = false;
		}

		return super.process3(c, arg, ok);
	}

	@Override
	protected GeoElement circle(String label, GeoPointND a, GeoPointND b,
			GeoPointND c) {

		if (a.isGeoElement3D() || b.isGeoElement3D() || c.isGeoElement3D()) {
			return kernel.getManager3D().circle3D(label, a, b, c);
		}

		return super.circle(label, a, b, c);
	}

}
