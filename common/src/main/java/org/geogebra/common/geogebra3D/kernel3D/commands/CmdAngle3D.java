package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoAngleConic3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoAngleElement3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoAnglePoint3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoAngleVector3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

public class CmdAngle3D extends CmdAngle {

	public CmdAngle3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] process(Command c, int n, boolean ok[])
			throws MyError {

		if (n == 4) {
			GeoElement[] arg = resArgs(c);

			// angle between three points
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))
					&& (ok[3] = (arg[3] instanceof GeoDirectionND))) {

				if (!arg[0].isGeoElement3D() && !arg[1].isGeoElement3D()
						&& !arg[2].isGeoElement3D()
						&& arg[3] == kernelA.getXOYPlane()) { // ignore xOy
																// plane to
																// orient 2D
					return angle(c.getLabel(), (GeoPointND) arg[0],
							(GeoPointND) arg[1], (GeoPointND) arg[2]);
				}

				GeoElement[] ret = { kernelA.getManager3D().Angle3D(
						c.getLabel(), (GeoPointND) arg[0], (GeoPointND) arg[1],
						(GeoPointND) arg[2], (GeoDirectionND) arg[3]) };
				return ret;
			}

			throw argErr(app, c.getName(), getBadArg(ok, arg));
		}

		return super.process(c, n, ok);

	}

	@Override
	protected GeoElement[] process2(Command c, GeoElement[] arg, boolean[] ok) {

		// angle between line and plane
		if ((ok[0] = (arg[0].isGeoLine())) && (ok[1] = (arg[1].isGeoPlane()))) {
			GeoElement[] ret = { kernelA.getManager3D().Angle3D(c.getLabel(),
					(GeoLineND) arg[0], (GeoPlane3D) arg[1]) };
			return ret;
		}
		if ((ok[1] = (arg[1].isGeoLine())) && (ok[0] = (arg[0].isGeoPlane()))) {
			GeoElement[] ret = { kernelA.getManager3D().Angle3D(c.getLabel(),
					(GeoLineND) arg[1], (GeoPlane3D) arg[0]) };
			return ret;
		}

		// angle between planes
		if ((ok[0] = (arg[0].isGeoPlane())) && (ok[1] = (arg[1].isGeoPlane()))) {
			GeoElement[] ret = { kernelA.getManager3D().Angle3D(c.getLabel(),
					(GeoPlane3D) arg[0], (GeoPlane3D) arg[1]) };
			return ret;
		}

		// angle of polygon, oriented
		if ((ok[0] = (arg[0].isGeoPolygon()))
				&& (ok[1] = (arg[1] instanceof GeoDirectionND))) {

			if (!arg[0].isGeoElement3D() && arg[1] == kernelA.getXOYPlane()) { // ignore
																				// xOy
																				// plane
																				// to
																				// orient
																				// 2D
																				// polygon
				return super.angle(c.getLabels(), (GeoPolygon) arg[0]);
			}

			GeoElement[] ret = kernelA.getManager3D().Angles3D(c.getLabels(),
					(GeoPolygon) arg[0], (GeoDirectionND) arg[1]);
			return ret;
		}

		return super.process2(c, arg, ok);
	}

	@Override
	protected GeoElement[] process3(Command c, GeoElement[] arg, boolean[] ok) {

		// angle between lines, oriented
		if ((ok[0] = (arg[0].isGeoLine())) && (ok[1] = (arg[1].isGeoLine()))
				&& (ok[2] = (arg[2] instanceof GeoDirectionND))) {

			if (!arg[0].isGeoElement3D() && !arg[1].isGeoElement3D()
					&& arg[2] == kernelA.getXOYPlane()) { // ignore xOy plane
															// for 2D
				return super.angle(c.getLabel(), (GeoLineND) arg[0],
						(GeoLineND) arg[1]);
			}

			GeoElement[] ret = { kernelA.getManager3D().Angle3D(c.getLabel(),
					(GeoLineND) arg[0], (GeoLineND) arg[1],
					(GeoDirectionND) arg[2]) };
			return ret;
		}

		// angle between vectors, oriented
		if ((ok[0] = (arg[0].isGeoVector()))
				&& (ok[1] = (arg[1].isGeoVector()))
				&& (ok[2] = (arg[2] instanceof GeoDirectionND))) {

			if (!arg[0].isGeoElement3D() && !arg[1].isGeoElement3D()
					&& arg[2] == kernelA.getXOYPlane()) { // ignore xOy plane
															// for 2D
				return angle(c.getLabel(), (GeoVectorND) arg[0],
						(GeoVectorND) arg[1]);
			}

			GeoElement[] ret = { kernelA.getManager3D().Angle3D(c.getLabel(),
					(GeoVectorND) arg[0], (GeoVectorND) arg[1],
					(GeoDirectionND) arg[2]) };
			return ret;
		}

		return super.process3(c, arg, ok);
	}

	@Override
	protected GeoElement[] angle(String label, GeoPointND p1, GeoPointND p2,
			GeoPointND p3) {
		if (p1.isGeoElement3D() || p2.isGeoElement3D() || p3.isGeoElement3D()) {
			GeoElement[] ret = { kernelA.getManager3D().Angle3D(label, p1, p2,
					p3) };
			return ret;
		}

		return super.angle(label, p1, p2, p3);
	}

	@Override
	protected GeoElement[] angle(String label, GeoLineND g, GeoLineND h) {

		if (g.isGeoElement3D() || h.isGeoElement3D()) {
			GeoElement[] ret = { kernelA.getManager3D().Angle3D(label, g, h) };
			return ret;
		}

		return super.angle(label, g, h);
	}

	@Override
	protected GeoElement[] angle(String label, GeoVectorND v, GeoVectorND w) {

		if (v.isGeoElement3D() || w.isGeoElement3D()) {
			GeoElement[] ret = { kernelA.getManager3D().Angle3D(label, v, w) };
			return ret;
		}

		return super.angle(label, v, w);

	}

	@Override
	protected GeoElement[] anglePointOrVector(String label, GeoElement v) {

		if (v.isGeoElement3D()) {
			AlgoAngleElement3D algo;
			if (v.isGeoVector()) {
				algo = new AlgoAngleVector3D(cons, label, (GeoVector3D) v);
			} else {
				algo = new AlgoAnglePoint3D(cons, label, (GeoPoint3D) v);
			}

			GeoElement[] ret = { algo.getAngle() };
			return ret;
		}

		return super.anglePointOrVector(label, v);
	}

	@Override
	protected GeoElement[] angle(String label, GeoConicND c) {

		if (c.isGeoElement3D()) {
			AlgoAngleElement3D algo = new AlgoAngleConic3D(cons, label,
					(GeoConic3D) c);
			GeoElement[] ret = { algo.getAngle() };
			return ret;
		}

		return super.angle(label, c);
	}

	@Override
	protected GeoElement[] angle(String[] labels, GeoPolygon p) {

		if (p.isGeoElement3D()) {
			return kernelA.getManager3D().Angles3D(labels, (GeoPolygon3D) p);
		}

		return super.angle(labels, p);
	}

	@Override
	protected GeoElement[] angle(String[] labels, GeoPointND p1, GeoPointND p2,
			GeoNumberValue a) {

		GeoDirectionND direction = kernelA.getApplication()
				.getActiveEuclidianView().getDirection();

		if (direction == kernelA.getSpace()) { // TODO: should create point on
												// circle
			return new GeoElement[] {};
		}

		if (direction == kernelA.getXOYPlane() /*
												 * || direction ==
												 * kernelA.getSpace()
												 */) { // use xOy plane
			if (p1.isGeoElement3D() || p2.isGeoElement3D()) {
				return kernelA.getManager3D().Angle(labels, p1, p2, a,
						kernelA.getXOYPlane(), true);
			}

			return super.angle(labels, p1, p2, a);
		}

		return kernelA.getManager3D().Angle(labels, p1, p2, a, direction, true);
	}
}
