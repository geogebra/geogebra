package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoIntersectPathLinePolygon;
import org.geogebra.common.kernel.algos.AlgoIntersectPolyLineConicRegion;
import org.geogebra.common.kernel.algos.AlgoIntersectSegmentConicRegion;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoImplicitSurfaceND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DLimitedInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.main.MyError;

/**
 * IntersectPath[ &lt;GeoLine&gt;, &lt;GeoPolygon&gt; ]
 * 
 * IntersectPath[ &lt;GeoLine&gt;, * &lt;GeoConic&gt; ]
 * 
 * // removed IntersectPath[&lt;GeoSegment&gt;, &lt;GeoConic&gt;]
 */
public class CmdIntersectPath extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIntersectPath(Kernel kernel) {
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

			if (!arg[0].isGeoElement3D() && !arg[1].isGeoElement3D()) {
				return process2D(c, arg);
			}

			// Line - Polygon(as region) in 2D/3D
			if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoPolygon()))) {
				GeoElement[] ret = kernel.getManager3D().intersectPath(
						c.getLabels(), (GeoLineND) arg[0], (GeoPolygon) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPolygon()))
					&& (ok[1] = (arg[1].isGeoLine()))) {
				GeoElement[] ret = kernel.getManager3D().intersectPath(
						c.getLabels(), (GeoLineND) arg[1], (GeoPolygon) arg[0]);
				return ret;
			}

			// Plane - Polygon
			if ((ok[0] = (arg[0].isGeoPlane()))
					&& (ok[1] = (arg[1].isGeoPolygon()))) {
				return kernel.getManager3D().intersectPath(c.getLabels(),
						(GeoPlaneND) arg[0], (GeoPolygon) arg[1]);
			} else if ((ok[1] = (arg[1].isGeoPlane()))
					&& (ok[0] = (arg[0].isGeoPolygon()))) {
				return kernel.getManager3D().intersectPath(c.getLabels(),
						(GeoPlaneND) arg[1], arg[0]);
			}

			// Plane - Polyhedron
			if ((ok[0] = (arg[0].isGeoPlane()))
					&& (ok[1] = (arg[1].isGeoPolyhedron()))) {
				return kernel.getManager3D().intersectRegion(c.getLabels(),
						(GeoPlaneND) arg[0], arg[1],
						c.getOutputSizes());
			} else if ((ok[1] = (arg[1].isGeoPlane()))
					&& (ok[0] = (arg[0].isGeoPolyhedron()))) {
				return kernel.getManager3D().intersectRegion(c.getLabels(),
						(GeoPlaneND) arg[1], arg[0],
						c.getOutputSizes());
			}

			// intersection 3D polygons
			if ((ok[0] = (arg[0].isGeoPolygon()))
					&& (ok[1] = (arg[1].isGeoPolygon()))) {
				GeoElement[] result = kernel.getManager3D().intersectPolygons(
						c.getLabels(), (GeoPolygon) arg[0],
						(GeoPolygon) arg[1]);
				return result;
			}
			// argument x=0 should be a plane, not line
			for (int i = 0; i < 2; i++) {
				if (arg[i] instanceof GeoLine && arg[i].isIndependent()
						&& !arg[i].isLabelSet()) {
					arg[i] = kernel.getManager3D().lineToPlane(arg[i]);
				}
			}

			// intersection plane/plane
			if ((ok[0] = (arg[0].isGeoPlane()))
					&& (ok[0] = (arg[1].isGeoPlane()))) {

				GeoElement[] ret = {
						kernel.getManager3D().intersectPlanes(c.getLabel(),
								(GeoPlaneND) arg[0], (GeoPlaneND) arg[1]) };
				return ret;

			}

			GeoElement ret = processPlaneSurface(kernel, arg, ok,
					c.getLabel());
			if (ret != null) {
				return new GeoElement[] { ret };
			}

			// intersection plane/quadric
			ret = processQuadricPlane(kernel, c, arg, ok);
			if (ret != null) {
				return new GeoElement[] { ret };
			}

			throw argErr(c, getBadArg(ok, arg));
		default:
			throw argNumErr(c);
		}
	}

	private GeoElement[] process2D(Command c, GeoElement[] arg) {
		boolean[] ok = new boolean[2];
		// Line - Polygon(as region) in 2D
		if ((ok[0] = (arg[0].isGeoLine()))
				&& (ok[1] = (arg[1].isGeoPolygon()))) {
			GeoElement[] ret = intersectPathLinePolygon(c.getLabels(),
					(GeoLine) arg[0], (GeoPolygon) arg[1]);
			return ret;
		} else if ((ok[0] = (arg[0].isGeoPolygon()))
				&& (ok[1] = (arg[1].isGeoLine()))) {
			GeoElement[] ret = intersectPathLinePolygon(c.getLabels(),
					(GeoLine) arg[1], (GeoPolygon) arg[0]);
			return ret;
		}

		// Line - Conic(as region) in 2D else if ((ok[0] =
		// if (((arg[0].isGeoLine())) && (ok[1] = (arg[1].isGeoConic())))
		// return
		// intersectLineConicRegion(c.getLabels(), (GeoLine) arg[0],
		// (GeoConic) arg[1]);
		//
		//
		// else if ((ok[0] = (arg[0].isGeoConic())) && (ok[1] =
		// (arg[1].isGeoLine()))) return
		// intersectLineConicRegion(c.getLabels(), (GeoLine) arg[1],
		// (GeoConic) arg[0]);

		// Segment - Conic(as region)
		if ((ok[0] = (arg[0].isGeoSegment()))
				&& (ok[1] = (arg[1].isGeoConic()))) {
			GeoElement[] ret = intersectSegmentConicRegion(c.getLabels(),
					(GeoSegment) arg[0], (GeoConic) arg[1]);
			return ret;
		} else if ((ok[0] = (arg[0].isGeoConic()))
				&& (ok[1] = (arg[1].isGeoSegment()))) {
			GeoElement[] ret = intersectSegmentConicRegion(c.getLabels(),
					(GeoSegment) arg[1], (GeoConic) arg[0]);
			return ret;
		}

		// polyLine - Conic(as region)
		if ((ok[0] = (arg[0].isGeoPolyLine()))
				&& (ok[1] = (arg[1].isGeoConic()))) {
			GeoElement[] ret = intersectPolyConicRegion(c.getLabels(),
					(GeoPoly) arg[0], (GeoConic) arg[1], false);
			return ret;
		} else if ((ok[0] = (arg[0].isGeoConic()))
				&& (ok[1] = (arg[1].isGeoPolyLine()))) {
			GeoElement[] ret = intersectPolyConicRegion(c.getLabels(),
					(GeoPoly) arg[1], (GeoConic) arg[0], false);
			return ret;
		}

		// polygon(as boundary) - Conic(as region)
		if ((ok[0] = (arg[0].isGeoPolygon()))
				&& (ok[1] = (arg[1].isGeoConic()))) {
			GeoElement[] ret = intersectPolyConicRegion(c.getLabels(),
					(GeoPoly) arg[0], (GeoConic) arg[1], true);
			return ret;
		} else if ((ok[0] = (arg[0].isGeoConic()))
				&& (ok[1] = (arg[1].isGeoPolygon()))) {
			GeoElement[] ret = intersectPolyConicRegion(c.getLabels(),
					(GeoPoly) arg[1], (GeoConic) arg[0], true);
			return ret;
		}

		// Polygon(as region) - Polygon(as region) in 2D

		if ((ok[0] = arg[0].isGeoPolygon()) && arg[1].isGeoPolygon()) {
			GeoElement[] ret = getAlgoDispatcher().intersectPolygons(
					c.getLabels(), (GeoPolygon) arg[0], (GeoPolygon) arg[1],
					true);
			return ret;
		}

		throw argErr(c, getBadArg(ok, arg));
	}

	/**
	 * yields intersection segments named label of GeoPoly poly and conic(as
	 * region)
	 */
	final private GeoElement[] intersectPolyConicRegion(String[] labels,
			GeoPoly poly, GeoConic conic, boolean isPolyClosed) {
		AlgoIntersectPolyLineConicRegion algo = new AlgoIntersectPolyLineConicRegion(
				cons, labels, poly, conic, isPolyClosed);
		GeoElement[] ret = algo.getOutput();
		return ret;
	}

	/**
	 * yields intersection segments named label of segment seg and conic(as
	 * region)
	 */
	final private GeoElement[] intersectSegmentConicRegion(String[] labels,
			GeoSegment seg, GeoConic conic) {
		AlgoIntersectSegmentConicRegion algo = new AlgoIntersectSegmentConicRegion(
				cons, labels, seg, conic);
		GeoElement[] ret = algo.getOutput();
		// GeoElement.setLabels(labels, ret);
		return ret;
	}

	/**
	 * yields intersection segments named label of line g and polygon p (as
	 * region)
	 */
	final private GeoElement[] intersectPathLinePolygon(String[] labels,
			GeoLine g, GeoPolygon p) {
		AlgoIntersectPathLinePolygon algo = new AlgoIntersectPathLinePolygon(
				cons, labels, g, p);
		return algo.getOutput();
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param arg
	 *            arguments
	 * @param ok
	 *            feedback array for type check
	 * @param label
	 *            label for output
	 * @return path
	 */
	public static GeoElement processPlaneSurface(Kernel kernel,
			GeoElement[] arg, boolean[] ok, String label) {
		Construction cons = kernel.getConstruction();
		GeoElement result = null;
		if ((ok[0] = (arg[0].isGeoPlane()))
				&& (ok[1] = (arg[1].isGeoImplicitSurface()))) {
			result = kernel.getManager3D().intersectPlaneImplicitSurface(
					(GeoPlaneND) arg[0], (GeoImplicitSurfaceND) arg[1])[0];
		} else if ((ok[1] = (arg[1].isGeoPlane()))
				&& (ok[0] = (arg[0].isGeoImplicitSurface()))) {
			result = kernel.getManager3D().intersectPlaneImplicitSurface(
					(GeoPlaneND) arg[1], (GeoImplicitSurfaceND) arg[0])[0];
		}

		else if ((ok[0] = (arg[0].isGeoPlane()))
				&& (ok[1] = (arg[1].isGeoFunctionNVar()))) {
			result = kernel.getManager3D().intersectFunctionNVarPlane(cons,
					(GeoFunctionNVar) arg[1], (GeoPlaneND) arg[0])
					.getOutput()[0];
		} else if ((ok[1] = (arg[1].isGeoPlane()))
				&& (ok[0] = (arg[0].isGeoFunctionNVar()))) {
			result = kernel.getManager3D().intersectFunctionNVarPlane(cons,
					(GeoFunctionNVar) arg[0], (GeoPlaneND) arg[1])
					.getOutput()[0];
		}
		if (result != null) {
			result.setLabel(label);
		}
		return result;
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
				&& (ok[0] = (arg[1] instanceof GeoQuadric3DLimitedInterface))) {

			return intersectPlaneQuadricLimited(kernelA, c.getLabel(),
					(GeoPlaneND) arg[0], (GeoQuadric3DLimitedInterface) arg[1]);

		} else if ((ok[0] = (arg[0] instanceof GeoQuadric3DLimitedInterface))
				&& (ok[0] = (arg[1] instanceof GeoPlaneND))) {

			return intersectPlaneQuadricLimited(kernelA, c.getLabel(),
					(GeoPlaneND) arg[1], (GeoQuadric3DLimitedInterface) arg[0]);

		}

		// intersection plane/quadric
		if ((ok[0] = (arg[0] instanceof GeoPlaneND))
				&& (ok[1] = (arg[1] instanceof GeoQuadricND))) {
			GeoElement ret =

					kernelA.getManager3D().intersect(c.getLabel(),
							(GeoPlaneND) arg[0], (GeoQuadricND) arg[1]);
			return ret;
		} else if ((arg[0] instanceof GeoQuadricND)
				&& (arg[1] instanceof GeoPlaneND)) {
			GeoElement ret =

					kernelA.getManager3D().intersect(c.getLabel(),
							(GeoPlaneND) arg[1], (GeoQuadricND) arg[0]);
			return ret;
		}

		return null;
	}

	static private final GeoElement intersectPlaneQuadricLimited(Kernel kernelA,
			String label, GeoPlaneND plane, GeoQuadric3DLimitedInterface quadric) {
		return kernelA.getManager3D().intersectQuadricLimited(label, plane,
				(GeoQuadricND) quadric);
	}
}