package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdSegment;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;



/*
 * Segment[ <GeoPoint3D>, <GeoPoint3D> ] or CmdSegment
 */
public class CmdSegment3D extends CmdSegment {
	

	public CmdSegment3D(Kernel kernel) {
		super(kernel);
		
	}
	
	
	
	public GeoElement[] process(Command c) throws MyError {	
		
		
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    
	   if (n==2) {
            arg = resArgs(c);
            if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()){
            	
            	GeoElement geo0 = (GeoElement) arg[0];
            	GeoElement geo1 = (GeoElement) arg[1];
            	
            	// segment between two 3D points
            	if ((ok[0] = (geo0.isGeoPoint()))
            			&& (ok[1] = (geo1.isGeoPoint()))) {
            		GeoElement[] ret =
            		{
            				(GeoElement) ((Kernel)kernelA).getManager3D().Segment3D(
            						c.getLabel(),
            						(GeoPointND) geo0,
            						(GeoPointND) geo1)};
            		return ret;
            	}
            }
	    }
		
		return super.process(c);
	}

}
