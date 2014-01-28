package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdPolyLine;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;



/*
 * Polygon[ <GeoPoint3D>, <GeoPoint3D>, ... ] or CmdPolygon
 */
public class CmdPolyLine3D extends CmdPolyLine {
	

	public CmdPolyLine3D(Kernel kernel) {
		super(kernel);
				
	}
	
	
	public GeoElement[] process(Command c) throws MyError {	
		
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		//check if one of arguments is 3D 
		boolean ok3D = false;
		for(int i=0;i<n;i++)
			ok3D = ok3D || (arg[i].isGeoElement3D());
		
		
		
		if (ok3D){
		    switch (n) {
			    case 0 :
			    	throw argNumErr(app, c.getName(), n);
		    	
				case 1:
				if (arg[0].isGeoList())
					return ((Kernel)kernelA).getManager3D().PolyLine3D(c.getLabels(), (GeoList) arg[0]);
				
				
		       default:
					
			        GeoPointND[] points = new GeoPointND[n];
			        // check arguments
			        for (int i = 0; i < n; i++) {
			            if (!(arg[i].isGeoPoint()))
							throw argErr(app, c.getName(), arg[i]);
						else {
			                points[i] = (GeoPointND) arg[i];
			            }
			        }
			        // everything ok
			        return ((Kernel)kernelA).getManager3D().PolyLine3D(c.getLabels(), points);
				}	
		}
 
		return super.process(c);
	}

}
