package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdIntersect;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.main.MyError;

/**
 * Intersect[ <GeoPlane3D>, <GeoConicND> ] Intersect[ <GeoLineND>,
 * <GeoQuadric3D> ] Intersect[ <GeoConicND>, <GeoConicND> ] Intersect[
 * <GeoLineND>, <GeoPolygon> ] Intersect[ <GeoLineND>, <GeoCoordSys2D> ]
 * Intersect[ <GeoLineND>, <GeoLineND> ] Intersect[ <GeoLineND>, <GeoConicND>,
 * <GeoNumeric> ] Intersect[ <GeoLineND>, <GeoQuadric3D>, <GeoNumeric> ]
 */
public class CmdIntersect3D extends CmdIntersect {

	public CmdIntersect3D(Kernel kernel) {
		super(kernel);

	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()) {

				// POINTS
				// intersection line/conic
				if ((arg[0] instanceof GeoLineND)
						&& (arg[1] instanceof GeoConicND))
					return (GeoElement[]) kernelA.getManager3D()
							.IntersectLineConic(c.getLabels(),
									(GeoLineND) arg[0], (GeoConicND) arg[1]);
				else if ((arg[0] instanceof GeoConicND)
						&& (arg[1] instanceof GeoLineND))
					return (GeoElement[]) kernelA.getManager3D()
							.IntersectLineConic(c.getLabels(),
									(GeoLineND) arg[1], (GeoConicND) arg[0]);
				// intersection plane/conic
				else if ((arg[0] instanceof GeoPlane3D)
						&& (arg[1] instanceof GeoConicND))
					return (GeoElement[]) kernelA
							.getManager3D()
							.IntersectPlaneConic(c.getLabels(),
									(GeoCoordSys2D) arg[0], (GeoConicND) arg[1]);
				else if ((arg[0] instanceof GeoConicND)
						&& (arg[1] instanceof GeoPlane3D))
					return (GeoElement[]) kernelA
							.getManager3D()
							.IntersectPlaneConic(c.getLabels(),
									(GeoCoordSys2D) arg[1], (GeoConicND) arg[0]);

				// intersection plane/polygon
				else if ((arg[0] instanceof GeoPlane3D)
						&& (arg[1] instanceof GeoPolygon))
					return kernelA.getManager3D().IntersectionPoint(
							c.getLabels(), (GeoPlane3D) arg[0],
							(GeoPolygon) arg[1]);
				else if ((arg[0] instanceof GeoPolygon)
						&& (arg[1] instanceof GeoPlane3D))
					return kernelA.getManager3D().IntersectionPoint(
							c.getLabels(), (GeoPlane3D) arg[1],
							(GeoPolygon) arg[0]);

				// intersection plane/polyhedron
				/*
				 * else if ( (arg[0] instanceof GeoPlane3D) &&
				 * (arg[1].isGeoPolyhedron())) return
				 * kernelA.getManager3D().IntersectionPoint( c.getLabels(),
				 * (GeoPlane3D) arg[0], (GeoPolyhedron) arg[1]); else if (
				 * (arg[0].isGeoPolyhedron()) && (arg[1] instanceof GeoPlane3D))
				 * return kernelA.getManager3D().IntersectionPoint(
				 * c.getLabels(), (GeoPlane3D) arg[1], (GeoPolyhedron) arg[0]);
				 */

				// Line - Quadric
				else if ((ok[0] = (arg[0].isGeoLine()))
						&& (ok[1] = (arg[1] instanceof GeoQuadric3D)))
					return (GeoElement[]) kernelA.getManager3D()
							.IntersectLineQuadric(c.getLabels(),
									(GeoLineND) arg[0], (GeoQuadric3D) arg[1]);
				else if ((ok[0] = (arg[0] instanceof GeoQuadric3D))
						&& (ok[1] = (arg[1].isGeoLine())))
					return (GeoElement[]) kernelA.getManager3D()
							.IntersectLineQuadric(c.getLabels(),
									(GeoLineND) arg[1], (GeoQuadric3D) arg[0]);

				// intersection conic/conic
				else if ((arg[0] instanceof GeoConicND)
						&& (arg[1] instanceof GeoConicND))
					return (GeoElement[]) kernelA.getManager3D()
							.IntersectConics(c.getLabels(),
									(GeoConicND) arg[0], (GeoConicND) arg[1]);

				// intersection line/polygon

				else if ((arg[0] instanceof GeoLineND && arg[1] instanceof GeoPolygon)
						|| (arg[1] instanceof GeoLineND && arg[0] instanceof GeoPolygon))

					return kernelA.getManager3D().IntersectionPoint(
							c.getLabels(), (GeoLineND) arg[0],
							(GeoPolygon) arg[1]);

				// intersection line/planar objects
				else if ((arg[0] instanceof GeoLineND && arg[1] instanceof GeoCoordSys2D)
						|| (arg[1] instanceof GeoLineND && arg[0] instanceof GeoCoordSys2D)) {

					GeoPoint3D point = (GeoPoint3D) kernelA.getManager3D()
							.Intersect(c.getLabel(), arg[0], arg[1]);

					kernelA.setStringMode(point);

					return new GeoElement[] { point };

					// intersection line/line
				} else if (arg[0] instanceof GeoLineND
						&& arg[1] instanceof GeoLineND) {

					GeoPoint3D point = (GeoPoint3D) kernelA.getManager3D()
							.Intersect(c.getLabel(), arg[0], arg[1]);

					kernelA.setStringMode(point);

					return new GeoElement[] { point };

				}

				// TODO remove this if conflicting another case
				if ((arg[0] instanceof GeoPlaneND)
						&& (arg[1] instanceof GeoPlaneND)) {
					GeoElement[] ret = { kernelA.getManager3D()
							.IntersectPlanes(c.getLabel(),
									(GeoPlaneND) arg[0],
									(GeoPlaneND) arg[1]) };
					return ret;
				}

				// intersection plane/limited quadric
				if ((arg[0] instanceof GeoPlaneND)
						&& (arg[1] instanceof GeoQuadric3DLimited)) {
					GeoElement[] ret = { kernelA.getManager3D()
							.IntersectQuadricLimited(c.getLabel(),
									(GeoPlaneND) arg[0],
									(GeoQuadric3DLimited) arg[1]) };
					return ret;
				} else if ((arg[0] instanceof GeoQuadric3DLimited)
						&& (arg[1] instanceof GeoPlaneND)) {
					GeoElement[] ret = { kernelA.getManager3D()
							.IntersectQuadricLimited(c.getLabel(),
									(GeoPlaneND) arg[1],
									(GeoQuadric3DLimited) arg[0]) };
					return ret;
				}

				// plane / quadric
				if ((arg[0] instanceof GeoPlaneND)
						&& (arg[1] instanceof GeoQuadricND)) {
					GeoElement[] ret = { kernelA.getManager3D().Intersect(
							c.getLabel(), (GeoPlaneND) arg[0],
							(GeoQuadric3D) arg[1]) };
					return ret;
				} else if ((arg[1] instanceof GeoPlaneND)
						&& (arg[0] instanceof GeoQuadricND)) {
					GeoElement[] ret = { kernelA.getManager3D().Intersect(
							c.getLabel(), (GeoPlaneND) arg[1],
							(GeoQuadric3D) arg[0]) };
					return ret;
				}

				// between 2 quadrics
				if ((ok[0] = (arg[0] instanceof GeoQuadric3D || arg[0] instanceof GeoQuadric3DLimited))
						&& (ok[1] = (arg[1] instanceof GeoQuadric3D || arg[1] instanceof GeoQuadric3DLimited))) {
					GeoElement[] ret = kernelA.getManager3D()
							.IntersectAsCircle(c.getLabels(),
									(GeoQuadricND) arg[0],
									(GeoQuadricND) arg[1]);
					return ret;
				}

				// Plane - Polyhedron
				if ((ok[0] = (arg[0].isGeoPlane()))
						&& (ok[1] = (arg[1].isGeoPolyhedron())))
					return kernelA.getManager3D().IntersectRegion(
							c.getLabels(), (GeoPlane3D) arg[0],
							(GeoPolyhedron) arg[1], c.getOutputSizes());
				else if ((ok[1] = (arg[1].isGeoPlane()))
						&& (ok[0] = (arg[0].isGeoPolyhedron())))
					return kernelA.getManager3D().IntersectRegion(
							c.getLabels(), (GeoPlane3D) arg[1],
							(GeoPolyhedron) arg[0], c.getOutputSizes());

			}

			return super.process(c);

		case 3:
			arg = resArgs(c);
			if ((arg[0].isGeoElement3D()) || (arg[1].isGeoElement3D())
					|| (arg[2].isGeoElement3D())) {

				// Line - Conic
				if ((arg[0].isGeoLine()) && arg[1].isGeoConic()
						&& arg[2] instanceof GeoNumberValue) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.IntersectLineConicSingle(c.getLabel(),
									(GeoLineND) arg[0], (GeoConicND) arg[1],
									(GeoNumberValue) arg[2]) };
					return ret;
				} else if ((arg[1].isGeoLine()) && arg[0].isGeoConic()
						&& arg[2] instanceof GeoNumberValue) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.IntersectLineConicSingle(c.getLabel(),
									(GeoLineND) arg[1], (GeoConicND) arg[0],
									(GeoNumberValue) arg[2]) };
					return ret;
				} else if ((arg[0].isGeoLine()) && arg[1].isGeoConic()
						&& arg[2].isGeoPoint()) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.IntersectLineConicSingle(c.getLabel(),
									(GeoLineND) arg[0], (GeoConicND) arg[1],
									(GeoPointND) arg[2]) };
					return ret;
				} else if ((arg[1].isGeoLine()) && arg[0].isGeoConic()
						&& arg[2].isGeoPoint()) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.IntersectLineConicSingle(c.getLabel(),
									(GeoLineND) arg[1], (GeoConicND) arg[0],
									(GeoPointND) arg[2]) };
					return ret;
				}
				// Conic - Conic
				else if ((arg[0].isGeoConic()) && arg[1].isGeoConic()
						&& arg[2] instanceof GeoNumberValue) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.IntersectConicsSingle(c.getLabel(),
									(GeoConicND) arg[0], (GeoConicND) arg[1],
									(GeoNumberValue) arg[2]) };
					return ret;
				} else if ((arg[0].isGeoConic()) && arg[1].isGeoConic()
						&& arg[2].isGeoPoint()) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.IntersectConicsSingle(c.getLabel(),
									(GeoConicND) arg[0], (GeoConicND) arg[1],
									(GeoPointND) arg[2]) };
					return ret;
				}
				// Line - Quadric
				else if ((arg[0].isGeoLine()) && arg[1] instanceof GeoQuadric3D
						&& arg[2] instanceof GeoNumberValue) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.IntersectLineQuadricSingle(c.getLabel(),
									(GeoLineND) arg[0], (GeoQuadric3D) arg[1],
									(GeoNumberValue) arg[2]) };
					return ret;
				} else if ((arg[1].isGeoLine())
						&& arg[0] instanceof GeoQuadric3D
						&& arg[2] instanceof GeoNumberValue) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.IntersectLineQuadricSingle(c.getLabel(),
									(GeoLineND) arg[1], (GeoQuadric3D) arg[0],
									(GeoNumberValue) arg[2]) };
					return ret;
				} else if ((arg[0].isGeoLine())
						&& arg[1] instanceof GeoQuadric3D
						&& arg[2].isGeoPoint()) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.IntersectLineQuadricSingle(c.getLabel(),
									(GeoLineND) arg[0], (GeoQuadric3D) arg[1],
									(GeoPointND) arg[2]) };
					return ret;
				} else if ((arg[1].isGeoLine())
						&& arg[0] instanceof GeoQuadric3D
						&& arg[2].isGeoPoint()) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
							.IntersectLineQuadricSingle(c.getLabel(),
									(GeoLineND) arg[1], (GeoQuadric3D) arg[0],
									(GeoPointND) arg[2]) };
					return ret;
				}
			}

		default:
			return super.process(c);
			// throw argNumErr(app, c.getName(), n);
		}
	}
}