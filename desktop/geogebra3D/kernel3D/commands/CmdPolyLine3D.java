package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdPolyLine;
import geogebra.kernel.commands.CmdPolygon;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;



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
					return kernel.getManager3D().PolyLine3D(c.getLabels(), (GeoList) arg[0]);
				
				
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
			        return kernel.getManager3D().PolyLine3D(c.getLabels(), points);
				}	
		}
 
		return super.process(c);
	}

}
