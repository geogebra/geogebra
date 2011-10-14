package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoVector3D;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Pyramid[ <GeoPoint3D>, <GeoPoint3D>, <GeoPoint3D>, ... ] 
 */
public class CmdCube extends CommandProcessor {
	
	public CmdCube(Kernel kernel) {
		super(kernel);
		
		
	}
	
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
	    case 3 :
	    	arg = resArgs(c);
	    	if ((ok[0] = arg[0].isGeoPoint())
	    			&& (ok[1] = arg[1].isGeoPoint())
	    			&& (ok[2] = (arg[2] instanceof GeoDirectionND))){
	    		
	    			GeoElement[] ret = { kernel.getManager3D().Cube(c.getLabels(), 
	    					(GeoPointND) arg[0], (GeoPointND) arg[1], (GeoDirectionND) arg[2]) };
					return ret;
	    		
	    	}else{
	    		for (int i=0;i<3;i++){
	    			if (!ok[i])
	    				throw argErr(app, c.getName(), arg[i]);
	    		}
	    	}
	    	break;	    
	    }
		
		throw argNumErr(app, "Length", n);

		

	}

}
