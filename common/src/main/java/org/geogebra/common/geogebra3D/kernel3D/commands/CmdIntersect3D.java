package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.CmdIntersectPath;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdIntersect;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.main.MyError;

/**
 * Intersect[ &lt;GeoPlane3D&gt;, &lt;GeoConicND&gt; ]
 * 
 * Intersect[ &lt;GeoLineND&gt;, &lt;GeoQuadric3D&gt; ]
 * 
 * Intersect[ &lt;GeoConicND&gt;, &lt;GeoConicND&gt; ]
 * 
 * Intersect[ &lt;GeoLineND&gt;, &lt;GeoPolygon&gt; ]
 * 
 * Intersect[ &lt;GeoLineND&gt;, &lt;GeoCoordSys2D&gt; ]
 * 
 * Intersect[ &lt;GeoLineND&gt;, &lt;GeoLineND&gt; ]
 * 
 * Intersect[ &lt;GeoLineND&gt;, &lt;GeoConicND&gt;, &lt;GeoNumeric&gt; ]
 * 
 * Intersect[ &lt;GeoLineND&gt;, &lt;GeoQuadric3D&gt;, &lt;GeoNumeric&gt; ]
 */
public class CmdIntersect3D extends CmdIntersect {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdIntersect3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
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
						&& (arg[1] instanceof GeoConicND)) {
					return (GeoElement[]) kernel.getManager3D()
							.intersectLineConic(c.getLabels(),
									(GeoLineND) arg[0], (GeoConicND) arg[1]);
				} else if ((arg[0] instanceof GeoConicND)
						&& (arg[1] instanceof GeoLineND)) {
					return (GeoElement[]) kernel.getManager3D()
							.intersectLineConic(c.getLabels(),
									(GeoLineND) arg[1], (GeoConicND) arg[0]);
				} else if ((arg[0] instanceof GeoPlane3D)
						&& (arg[1] instanceof GeoConicND)) {
					return (GeoElement[]) kernel.getManager3D()
							.intersectPlaneConic(c.getLabels(),
									(GeoCoordSys2D) arg[0],
									(GeoConicND) arg[1]);
				} else if ((arg[0] instanceof GeoConicND)
						&& (arg[1] instanceof GeoPlane3D)) {
					return (GeoElement[]) kernel.getManager3D()
							.intersectPlaneConic(c.getLabels(),
									(GeoCoordSys2D) arg[1],
									(GeoConicND) arg[0]);
				} else if ((arg[0] instanceof GeoPlane3D)
						&& (arg[1] instanceof GeoCurveCartesianND)) {
					return (GeoElement[]) kernel.getManager3D()
							.intersectPlaneCurve(c.getLabels(),
									(GeoCoordSys2D) arg[0],
									(GeoCurveCartesianND) arg[1]);
				} else if ((arg[0] instanceof GeoCurveCartesianND)
						&& (arg[1] instanceof GeoPlane3D)) {
					return (GeoElement[]) kernel.getManager3D()
							.intersectPlaneCurve(c.getLabels(),
									(GeoCoordSys2D) arg[1],
									(GeoCurveCartesianND) arg[0]);
				} else if ((arg[0] instanceof GeoPlane3D)
						&& (arg[1] instanceof GeoPolygon)) {
					return kernel.getManager3D().intersectionPoint(
							c.getLabels(), (GeoPlane3D) arg[0],
							(GeoPolygon) arg[1]);
				} else if ((arg[0] instanceof GeoPolygon)
						&& (arg[1] instanceof GeoPlane3D)) {
					return kernel.getManager3D().intersectionPoint(
							c.getLabels(), (GeoPlane3D) arg[1],
							(GeoPolygon) arg[0]);
				} else if ((ok[0] = (arg[0].isGeoLine()))
						&& (ok[1] = (arg[1] instanceof GeoQuadric3D))) {
					return (GeoElement[]) kernel.getManager3D()
							.intersectLineQuadric(c.getLabels(),
									(GeoLineND) arg[0], (GeoQuadric3D) arg[1]);
				} else if ((ok[0] = (arg[0] instanceof GeoQuadric3D))
						&& (ok[1] = (arg[1].isGeoLine()))) {
					return (GeoElement[]) kernel.getManager3D()
							.intersectLineQuadric(c.getLabels(),
									(GeoLineND) arg[1], (GeoQuadric3D) arg[0]);
				} else if ((arg[0] instanceof GeoConicND) && (arg[1].isGeoConic()
						|| arg[1] instanceof GeoQuadric3D)) {
					return (GeoElement[]) kernel.getManager3D()
							.intersectConics(c.getLabels(), (GeoConicND) arg[0],
									(GeoQuadricND) arg[1]);
				} else if ((arg[1] instanceof GeoConicND)
						&& (arg[0].isGeoConic()
								|| arg[0] instanceof GeoQuadric3D)) {
					return (GeoElement[]) kernel.getManager3D()
							.intersectConics(c.getLabels(), (GeoConicND) arg[1],
									(GeoQuadricND) arg[0]);
				}

				// intersection line/polygon

				else if ((arg[0] instanceof GeoLineND
						&& arg[1] instanceof GeoPolygon)
						|| (arg[1] instanceof GeoLineND
								&& arg[0] instanceof GeoPolygon)) {
					return kernel.getManager3D().intersectionPoint(
							c.getLabels(), (GeoLineND) arg[0],
							(GeoPolygon) arg[1]);
				} else if (arg[0].isGeoPolygon() && arg[1].isGeoPolygon()
						&& (arg[1] instanceof GeoPolygon3D
								|| arg[0] instanceof GeoPolygon3D)) {
					return kernel.getManager3D().intersectionPoint(
							c.getLabels(), (GeoPolygon) arg[0],
							(GeoPolygon) arg[1]);
				} else if (arg[0] instanceof GeoLineND
						&& arg[1] instanceof GeoCoordSys2D) {

					GeoPoint3D point = (GeoPoint3D) kernel.getManager3D()
							.intersect(c.getLabel(), (GeoLineND) arg[0],
									(GeoCoordSys2D) arg[1], false);

					kernel.setStringMode(point);

					return new GeoElement[] { point };

					// intersection line/planar objects
				} else if (arg[1] instanceof GeoLineND
						&& arg[0] instanceof GeoCoordSys2D) {

					GeoPoint3D point = (GeoPoint3D) kernel.getManager3D()
							.intersect(c.getLabel(), (GeoLineND) arg[1],
									(GeoCoordSys2D) arg[0], true);

					kernel.setStringMode(point);

					return new GeoElement[] { point };

					// intersection line/line
				} else if (arg[0] instanceof GeoLineND
						&& arg[1] instanceof GeoLineND) {

					GeoPoint3D point = (GeoPoint3D) kernel.getManager3D()
							.intersect(c.getLabel(), (GeoLineND) arg[0],
									(GeoLineND) arg[1]);

					kernel.setStringMode(point);

					return new GeoElement[] { point };

				}

				// TODO remove this if conflicting another case
				// intersection plane/plane
				if ((arg[0] instanceof GeoPlaneND)
						&& (arg[1] instanceof GeoPlaneND)) {
					GeoElement[] ret = {
							kernel.getManager3D().intersectPlanes(c.getLabel(),
									(GeoPlaneND) arg[0], (GeoPlaneND) arg[1]) };
					return ret;
				}

				// intersection plane/limited quadric
				if ((arg[0] instanceof GeoPlaneND)
						&& (arg[1] instanceof GeoQuadric3DLimited)) {
					GeoElement[] ret = {
							kernel.getManager3D().intersectQuadricLimited(
									c.getLabel(), (GeoPlaneND) arg[0],
									(GeoQuadric3DLimited) arg[1]) };
					return ret;
				} else if ((arg[0] instanceof GeoQuadric3DLimited)
						&& (arg[1] instanceof GeoPlaneND)) {
					GeoElement[] ret = {
							kernel.getManager3D().intersectQuadricLimited(
									c.getLabel(), (GeoPlaneND) arg[1],
									(GeoQuadric3DLimited) arg[0]) };
					return ret;
				}

				// plane / quadric
				if ((arg[0] instanceof GeoPlaneND)
						&& (arg[1] instanceof GeoQuadricND)) {
					GeoElement[] ret = { kernel.getManager3D().intersect(
							c.getLabel(), (GeoPlaneND) arg[0],
							(GeoQuadric3D) arg[1]) };
					return ret;
				} else if ((arg[1] instanceof GeoPlaneND)
						&& (arg[0] instanceof GeoQuadricND)) {
					GeoElement[] ret = { kernel.getManager3D().intersect(
							c.getLabel(), (GeoPlaneND) arg[1],
							(GeoQuadric3D) arg[0]) };
					return ret;
				}

				// between 2 quadrics
				if ((ok[0] = (arg[0] instanceof GeoQuadric3D
						|| arg[0] instanceof GeoQuadric3DLimited))
						&& (ok[1] = (arg[1] instanceof GeoQuadric3D
								|| arg[1] instanceof GeoQuadric3DLimited))) {
					GeoElement[] ret = kernel.getManager3D().intersectAsCircle(
							c.getLabels(), (GeoQuadricND) arg[0],
							(GeoQuadricND) arg[1]);
					return ret;
				}

				// Plane - Polyhedron
				if ((ok[0] = (arg[0].isGeoPlane()))
						&& (ok[1] = (arg[1].isGeoPolyhedron()))) {
					return kernel.getManager3D().intersectRegion(c.getLabels(),
							(GeoPlane3D) arg[0], (GeoPolyhedron) arg[1],
							c.getOutputSizes());
				} else if ((ok[1] = (arg[1].isGeoPlane()))
						&& (ok[0] = (arg[0].isGeoPolyhedron()))) {
					return kernel.getManager3D().intersectRegion(c.getLabels(),
							(GeoPlane3D) arg[1], (GeoPolyhedron) arg[0],
							c.getOutputSizes());
				}

				GeoElement ret = CmdIntersectPath.processPlaneSurface(kernel,
						arg, ok, c.getLabel());
				if (ret != null) {
					return new GeoElement[] { ret };
				}

			}

			return super.process(c, info);

		case 3:
			arg = resArgs(c);
			if ((arg[0].isGeoElement3D()) || (arg[1].isGeoElement3D())
					|| (arg[2].isGeoElement3D())) {

				// Line - Conic
				if ((arg[0].isGeoLine()) && arg[1].isGeoConic()
						&& arg[2] instanceof GeoNumberValue) {
					GeoElement[] ret = { (GeoElement) kernel.getManager3D()
							.intersectLineConicSingle(c.getLabel(),
									(GeoLineND) arg[0], (GeoConicND) arg[1],
									(GeoNumberValue) arg[2]) };
					return ret;
				} else if ((arg[1].isGeoLine()) && arg[0].isGeoConic()
						&& arg[2] instanceof GeoNumberValue) {
					GeoElement[] ret = { (GeoElement) kernel.getManager3D()
							.intersectLineConicSingle(c.getLabel(),
									(GeoLineND) arg[1], (GeoConicND) arg[0],
									(GeoNumberValue) arg[2]) };
					return ret;
				} else if ((arg[0].isGeoLine()) && arg[1].isGeoConic()
						&& arg[2].isGeoPoint()) {
					GeoElement[] ret = { (GeoElement) kernel.getManager3D()
							.intersectLineConicSingle(c.getLabel(),
									(GeoLineND) arg[0], (GeoConicND) arg[1],
									(GeoPointND) arg[2]) };
					return ret;
				} else if ((arg[1].isGeoLine()) && arg[0].isGeoConic()
						&& arg[2].isGeoPoint()) {
					GeoElement[] ret = { (GeoElement) kernel.getManager3D()
							.intersectLineConicSingle(c.getLabel(),
									(GeoLineND) arg[1], (GeoConicND) arg[0],
									(GeoPointND) arg[2]) };
					return ret;
				}
				// Conic - Conic
				else if ((arg[0].isGeoConic())
						&& (arg[1].isGeoConic()
								|| arg[1] instanceof GeoQuadric3D)
						&& arg[2] instanceof GeoNumberValue) {
					GeoElement[] ret = { (GeoElement) kernel.getManager3D()
							.intersectConicsSingle(c.getLabel(),
									(GeoConicND) arg[0], (GeoQuadricND) arg[1],
									(GeoNumberValue) arg[2]) };
					return ret;
				} else if ((arg[0].isGeoConic()) && arg[1].isGeoConic()
						&& arg[2].isGeoPoint()) {
					GeoElement[] ret = { (GeoElement) kernel.getManager3D()
							.intersectConicsSingle(c.getLabel(),
									(GeoConicND) arg[0], (GeoConicND) arg[1],
									(GeoPointND) arg[2]) };
					return ret;
				}
				// Line - Quadric
				else if ((arg[0].isGeoLine()) && arg[1] instanceof GeoQuadric3D
						&& arg[2] instanceof GeoNumberValue) {
					GeoElement[] ret = { (GeoElement) kernel.getManager3D()
							.intersectLineQuadricSingle(c.getLabel(),
									(GeoLineND) arg[0], (GeoQuadric3D) arg[1],
									(GeoNumberValue) arg[2]) };
					return ret;
				} else if ((arg[1].isGeoLine())
						&& arg[0] instanceof GeoQuadric3D
						&& arg[2] instanceof GeoNumberValue) {
					GeoElement[] ret = { (GeoElement) kernel.getManager3D()
							.intersectLineQuadricSingle(c.getLabel(),
									(GeoLineND) arg[1], (GeoQuadric3D) arg[0],
									(GeoNumberValue) arg[2]) };
					return ret;
				} else if ((arg[0].isGeoLine())
						&& arg[1] instanceof GeoQuadric3D
						&& arg[2].isGeoPoint()) {
					GeoElement[] ret = { (GeoElement) kernel.getManager3D()
							.intersectLineQuadricSingle(c.getLabel(),
									(GeoLineND) arg[0], (GeoQuadric3D) arg[1],
									(GeoPointND) arg[2]) };
					return ret;
				} else if ((arg[1].isGeoLine())
						&& arg[0] instanceof GeoQuadric3D
						&& arg[2].isGeoPoint()) {
					GeoElement[] ret = { (GeoElement) kernel.getManager3D()
							.intersectLineQuadricSingle(c.getLabel(),
									(GeoLineND) arg[1], (GeoQuadric3D) arg[0],
									(GeoPointND) arg[2]) };
					return ret;
				}
			}

		default:
			return super.process(c, info);
		// throw argNumErr(c);
		}
	}
}