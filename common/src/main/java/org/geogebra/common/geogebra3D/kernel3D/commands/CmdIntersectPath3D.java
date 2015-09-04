package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.CmdIntersectPath;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.main.MyError;

public class CmdIntersectPath3D extends CmdIntersectPath {

	public CmdIntersectPath3D(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			if (!arg[0].isGeoElement3D() && !arg[1].isGeoElement3D())
				return super.process(c);

			// Line - Polygon(as region) in 2D/3D
			if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoPolygon()))) {
				GeoElement[] ret = kernelA.getManager3D().IntersectPath(
						c.getLabels(), (GeoLineND) arg[0], (GeoPolygon) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPolygon()))
					&& (ok[1] = (arg[1].isGeoLine()))) {
				GeoElement[] ret = kernelA.getManager3D().IntersectPath(
						c.getLabels(), (GeoLineND) arg[1], (GeoPolygon) arg[0]);
				return ret;
			}

			// Plane - Polygon
			if ((ok[0] = (arg[0].isGeoPlane()))
					&& (ok[1] = (arg[1].isGeoPolygon())))
				return kernelA.getManager3D().IntersectPath(c.getLabels(),
						(GeoPlane3D) arg[0], (GeoPolygon) arg[1]);
			else if ((ok[1] = (arg[1].isGeoPlane()))
					&& (ok[0] = (arg[0].isGeoPolygon())))
				return kernelA.getManager3D().IntersectPath(c.getLabels(),
						(GeoPlane3D) arg[1], (GeoPolygon) arg[0]);

			// Plane - Polyhedron
			if ((ok[0] = (arg[0].isGeoPlane()))
					&& (ok[1] = (arg[1].isGeoPolyhedron())))
				return kernelA.getManager3D().IntersectRegion(c.getLabels(),
						(GeoPlane3D) arg[0], (GeoPolyhedron) arg[1],
						c.getOutputSizes());
			else if ((ok[1] = (arg[1].isGeoPlane()))
					&& (ok[0] = (arg[0].isGeoPolyhedron())))
				return kernelA.getManager3D().IntersectRegion(c.getLabels(),
						(GeoPlane3D) arg[1], (GeoPolyhedron) arg[0],
						c.getOutputSizes());

			// intersection plane/plane
			if ((ok[0] = (arg[0].isGeoPlane()))
					&& (ok[0] = (arg[1].isGeoPlane()))) {

				GeoElement[] ret = { kernelA.getManager3D().IntersectPlanes(
						c.getLabel(), (GeoPlaneND) arg[0],
						(GeoPlaneND) arg[1]) };
				return ret;

			}

			// intersection 3D polygons
			if ((ok[0] = (arg[0].isGeoPolygon()))
					&& (ok[1] = (arg[1].isGeoPolygon()))) {
				GeoElement[] result = kernelA.getManager3D().IntersectPolygons(
						c.getLabels(), (GeoPolygon3D) arg[0],
						(GeoPolygon3D) arg[1]);
				return result;
			}

			// intersection plane/quadric
			GeoElement ret = processQuadricPlane(kernelA, c, arg, ok);
			if (ret != null) {
				return new GeoElement[] { ret };
			}

			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			return super.process(c);
		}
	}

	/**
	 * (try to) process for plane / quadric (or limited quadric)
	 * 
	 * @param kernelA
	 *            kernel
	 * @param c
	 *            command
	 * @param arg
	 *            arguments
	 * @param ok
	 *            check
	 * @return intersection
	 */
	static public final GeoElement processQuadricPlane(Kernel kernelA,
			Command c, GeoElement[] arg, boolean[] ok) {
		// intersection plane/limited quadric
		if ((ok[0] = (arg[0] instanceof GeoPlaneND))
				&& (ok[0] = (arg[1] instanceof GeoQuadric3DLimited))) {

			return intersectPlaneQuadricLimited(kernelA, c.getLabel(),
					(GeoPlaneND) arg[0], (GeoQuadric3DLimited) arg[1]);

		} else if ((ok[0] = (arg[0] instanceof GeoQuadric3DLimited))
				&& (ok[0] = (arg[1] instanceof GeoPlaneND))) {

			return intersectPlaneQuadricLimited(kernelA, c.getLabel(),
					(GeoPlaneND) arg[1], (GeoQuadric3DLimited) arg[0]);

		}

		// intersection plane/quadric
		if ((ok[0] = (arg[0] instanceof GeoPlaneND))
				&& (ok[1] = (arg[1] instanceof GeoQuadricND))) {
			GeoElement ret =

			kernelA.getManager3D().Intersect(c.getLabel(), (GeoPlaneND) arg[0],
					(GeoQuadricND) arg[1]);
			return ret;
		} else if ((arg[0] instanceof GeoQuadricND)
				&& (arg[1] instanceof GeoPlaneND)) {
			GeoElement ret =

			kernelA.getManager3D().Intersect(c.getLabel(), (GeoPlaneND) arg[1],
					(GeoQuadricND) arg[0]);
			return ret;
		}

		return null;
	}

	static private final GeoElement intersectPlaneQuadricLimited(
			Kernel kernelA, String label, GeoPlaneND plane,
			GeoQuadric3DLimited quadric) {
		return kernelA.getManager3D().IntersectQuadricLimited(label, plane,
				quadric);
	}

}