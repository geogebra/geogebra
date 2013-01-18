package geogebra3D.kernel3D.commands;



import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.advanced.CmdIntersectRegion;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPolyhedron;

public class CmdIntersectRegion3D extends CmdIntersectRegion {

	public CmdIntersectRegion3D(Kernel kernel) {
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

			// Plane - Polyhedron

			if (
					(ok[0] = (arg[0] .isGeoPlane()))
					&& (ok[1] = (arg[1] instanceof GeoPolyhedron)))
				return kernelA.getManager3D().IntersectRegion(
						c.getLabels(),
						(GeoPlane3D) arg[0],
						(GeoPolyhedron) arg[1], 
						c.getOutputSizes());
			else if (
					(ok[1] = (arg[1] .isGeoPlane()))
					&& (ok[0] = (arg[0] instanceof GeoPolyhedron)))
				return kernelA.getManager3D().IntersectRegion(
						c.getLabels(),
						(GeoPlane3D) arg[1],
						(GeoPolyhedron) arg[0],
						c.getOutputSizes());




			//intersection plane/quadric
			/*
		if ((arg[0] instanceof GeoPlaneND) && (arg[1] instanceof GeoQuadricND)){
			GeoElement[] ret =
			{
					kernelA.getManager3D().Intersect(
							c.getLabel(),
							(GeoPlaneND) arg[0],
							(GeoQuadric3D) arg[1])};
			return ret;
		}else if ((arg[0] instanceof GeoQuadricND) && (arg[1] instanceof GeoPlaneND)){
			GeoElement[] ret =
			{
					kernelA.getManager3D().Intersect(
							c.getLabel(),
							(GeoPlaneND) arg[1],
							(GeoQuadric3D) arg[0])};
			return ret;
		}
			 */

			return super.process(c);

		default :
			return super.process(c);
		}
	}
	

}