package geogebra.common.geogebra3D.kernel3D.commands;



import geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.advanced.CmdIntersectPath;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPlaneND;
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra.common.main.MyError;

public class CmdIntersectPath3D extends CmdIntersectPath {

	public CmdIntersectPath3D(Kernel kernel) {
		super(kernel);
	}

	public  GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2 :
			arg = resArgs(c);

			if (!arg[0].isGeoElement3D() && !arg[1].isGeoElement3D())
				return super.process(c);

			// Line - Polygon(as region) in 2D/3D
			if ((ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] .isGeoPolygon()))) {
				GeoElement[] ret =
						kernelA.getManager3D().IntersectPath(
								c.getLabels(),
								(GeoLineND) arg[0],
								(GeoPolygon) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0] .isGeoPolygon()))
					&& (ok[1] = (arg[1] .isGeoLine()))) {
				GeoElement[] ret =
						kernelA.getManager3D().IntersectPath(
								c.getLabels(),
								(GeoLineND) arg[1],
								(GeoPolygon) arg[0]);
				return ret;
			}


			// Plane - Polygon
			if (
					(ok[0] = (arg[0] .isGeoPlane()))
					&& (ok[1] = (arg[1] .isGeoPolygon())))
				return kernelA.getManager3D().IntersectPath(
						c.getLabels(),
						(GeoPlane3D) arg[0],
						(GeoPolygon) arg[1]);
			else if (
					(ok[1] = (arg[1] .isGeoPlane()))
					&& (ok[0] = (arg[0] .isGeoPolygon())))
				return kernelA.getManager3D().IntersectPath(
						c.getLabels(),
						(GeoPlane3D) arg[1],
						(GeoPolygon) arg[0]);

			// Plane - Polyhedron
			if (
					(ok[0] = (arg[0] .isGeoPlane()))
					&& (ok[1] = (arg[1] .isGeoPolyhedron())))
				return kernelA.getManager3D().IntersectRegion(
						c.getLabels(),
						(GeoPlane3D) arg[0],
						(GeoPolyhedron) arg[1], 
						c.getOutputSizes());
			else if (
					(ok[1] = (arg[1] .isGeoPlane()))
					&& (ok[0] = (arg[0] .isGeoPolyhedron())))
				return kernelA.getManager3D().IntersectRegion(
						c.getLabels(),
						(GeoPlane3D) arg[1],
						(GeoPolyhedron) arg[0],
						c.getOutputSizes());


			//intersection plane/plane
			if ((ok[0] = (arg[0].isGeoPlane())) && (ok[0] = (arg[1].isGeoPlane()))){

				GeoElement[] ret =
					{
						kernelA.getManager3D().IntersectPlanes(
								c.getLabel(),
								(GeoCoordSys2D) arg[0],
								(GeoCoordSys2D) arg[1])};
				return ret;

			}

			
			//intersection plane/limited quadric
			if ((ok[0] = (arg[0] instanceof GeoPlaneND)) && (ok[0] = (arg[1] instanceof GeoQuadric3DLimited))){
				GeoElement[] ret =
					{
						kernelA.getManager3D().IntersectQuadricLimited(
								c.getLabel(),
								(GeoPlaneND) arg[0],
								(GeoQuadric3DLimited) arg[1])};
				return ret;
			}else if ((ok[0] = (arg[0] instanceof GeoQuadric3DLimited)) && (ok[0] = (arg[1] instanceof GeoPlaneND))){
				GeoElement[] ret =
					{
						kernelA.getManager3D().IntersectQuadricLimited(
								c.getLabel(),
								(GeoPlaneND) arg[1],
								(GeoQuadric3DLimited) arg[0])};
				return ret;
			}
			

			//intersection plane/quadric
			if ((ok[0] = (arg[0] instanceof GeoPlaneND)) && (ok[1] = (arg[1] instanceof GeoQuadricND))){
				GeoElement[] ret =
					{
						kernelA.getManager3D().Intersect(
								c.getLabel(),
								(GeoPlaneND) arg[0],
								(GeoQuadricND) arg[1])};
				return ret;
			}else if ((arg[0] instanceof GeoQuadricND) && (arg[1] instanceof GeoPlaneND)){
				GeoElement[] ret =
					{
						kernelA.getManager3D().Intersect(
								c.getLabel(),
								(GeoPlaneND) arg[1],
								(GeoQuadricND) arg[0])};
				return ret;
			}
			
			throw argErr(app, c.getName(), getBadArg(ok,arg));

		default :
			return super.process(c);
		}
	}
}