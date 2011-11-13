package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Pyramid[ <GeoPoint3D>, <GeoPoint3D>, <GeoPoint3D>, ... ] 
 */
public class CmdPyramid extends CommandProcessor {
	
	public CmdPyramid(Kernel kernel) {
		super(kernel);
		
		
	}
	
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		
		// polygon for given points
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
        return kernel.getManager3D().Pyramid(c.getLabels(), points);
		
		

	}

}
