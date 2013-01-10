package geogebra3D.kernel3D.commands;



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
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoQuadric3D;

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


    	// Plane - Polygon(as region)
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

    	
    	//intersection plane/plane
    	if (arg[0].isGeoPlane() && arg[1].isGeoPlane()){

    		GeoElement[] ret =
    			{
    				kernelA.getManager3D().IntersectPlanes(
    						c.getLabel(),
    						(GeoCoordSys2D) arg[0],
    						(GeoCoordSys2D) arg[1])};
    		return ret;

    	}
    	
    	
    	//intersection plane/quadric
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
		
		return super.process(c);

        default :
        	return super.process(c);
    }
}
}