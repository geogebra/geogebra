package geogebra3D.kernel3D.commands;




import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdDistance;
import geogebra.common.kernel.commands.CmdIntersect;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPlaneND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra.common.main.MyError;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoQuadric3D;


/**
 * Intersect[ <GeoPlane3D>, <GeoConicND> ] 
 * Intersect[ <GeoLineND>, <GeoQuadric3D> ] 
 * Intersect[ <GeoConicND>, <GeoConicND> ] 
 * Intersect[ <GeoLineND>, <GeoPolygon> ] 
 * Intersect[ <GeoLineND>, <GeoCoordSys2D> ]
 * Intersect[ <GeoLineND>, <GeoLineND> ] 
 * Intersect[ <GeoLineND>, <GeoConicND>, <GeoNumeric> ] 
 * Intersect[ <GeoLineND>, <GeoQuadric3D>, <GeoNumeric> ]  
 */
public class CmdDistance3D extends CmdDistance {
	
	
	
	public CmdDistance3D(Kernel kernel) {
		super(kernel);
		
		
	}	
	
	
public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
        	arg = resArgs(c);

        	if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D() ){
        		
        		return new GeoElement[] {
        				((Kernel)kernelA).getManager3D().Distance(
        				c.getLabel(),
        				(GeoLineND)arg[0],
        				(GeoLineND)arg[1])        		
        		};
        	}
        	
        
        	return super.process(c);

        default :
            //return super.process(c);
        	throw argNumErr(app, c.getName(), n);
    }
}
}