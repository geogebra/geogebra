package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.AlgoIntersectFunctionNVarPlane;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.GeoImplicitSurface;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.CmdIntersectPath;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.main.MyError;

/**
 * Processor forIntersectPath command
 *
 */
public class CmdIntersectPath3D extends CmdIntersectPath {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdIntersectPath3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			if (!arg[0].isGeoElement3D() && !arg[1].isGeoElement3D()) {
				return super.process(c);
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
						(GeoPlane3D) arg[0], (GeoPolygon) arg[1]);
			} else if ((ok[1] = (arg[1].isGeoPlane()))
					&& (ok[0] = (arg[0].isGeoPolygon()))) {
				return kernel.getManager3D().intersectPath(c.getLabels(),
						(GeoPlane3D) arg[1], (GeoPolygon) arg[0]);
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
					arg[i] = lineToPlane(arg[i]);
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
			return super.process(c);
		}
	}

	private GeoElement lineToPlane(GeoElement geoElement) {

		GeoLine line = (GeoLine) geoElement;
		GeoPlane3D plane = new GeoPlane3D(cons, line.getX(), line.getY(), 0,
				line.getZ());
		if (line.getDefinition() != null) {
			ExpressionValue eq = geoElement.getDefinition().unwrap();
			if (eq instanceof Equation) {
				plane.setDefinition(eq.wrap());
			}
		}
		return plane;
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
					(GeoPlaneND) arg[0], (GeoImplicitSurface) arg[1])[0];
		} else if ((ok[1] = (arg[1].isGeoPlane()))
				&& (ok[0] = (arg[0].isGeoImplicitSurface()))) {
			result = kernel.getManager3D().intersectPlaneImplicitSurface(
					(GeoPlaneND) arg[1], (GeoImplicitSurface) arg[0])[0];
		}

		else if ((ok[0] = (arg[0].isGeoPlane()))
				&& (ok[1] = (arg[1].isGeoFunctionNVar()))) {
			result = new AlgoIntersectFunctionNVarPlane(cons,
					(GeoFunctionNVar) arg[1], (GeoPlaneND) arg[0])
							.getOutput()[0];
		} else if ((ok[1] = (arg[1].isGeoPlane()))
				&& (ok[0] = (arg[0].isGeoFunctionNVar()))) {
			result = new AlgoIntersectFunctionNVarPlane(cons,
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
			String label, GeoPlaneND plane, GeoQuadric3DLimited quadric) {
		return kernelA.getManager3D().intersectQuadricLimited(label, plane,
				quadric);
	}

}