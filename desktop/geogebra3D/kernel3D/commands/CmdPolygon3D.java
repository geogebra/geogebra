package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdPolygon;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Polygon[ <GeoPoint3D>, <GeoPoint3D>, ... ] or CmdPolygon
 */
public class CmdPolygon3D extends CmdPolygon {
	

	public CmdPolygon3D(Kernel kernel) {
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
			// polygon for given points
			// check if a normal direction is given
			/*
			boolean hasNormal = false;
			GeoDirectionND normal = null;
			if (arg[n-1] instanceof GeoDirectionND){
				hasNormal = true;
				normal = (GeoDirectionND) arg[n-1];
				n=n-1; //one point less
			}
			*/
			//points
			GeoPointND[] points = new GeoPointND[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint()))
					throw argErr(app, c.getName(), arg[i]);
				else {
					points[i] = (GeoPointND) arg[i];
				}
			}

			/*
			if (hasNormal)
				return kernel.getManager3D().Polygon3D(c.getLabels(), points, normal);
			else*/
				return kernel.getManager3D().Polygon3D(c.getLabels(), points);
		}
 
		return super.process(c);
	}

}
