package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

public class CmdAngle3D extends CmdAngle {
	
	
	
	
	public CmdAngle3D(Kernel kernel) {
		super(kernel);
	}

	
	

	@Override
	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    switch (n) {
	    
	    
	    case 3 :
	    	arg = resArgs(c);
	    	if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D() || arg[2].isGeoElement3D()){
	    		if ((ok[0] = (arg[0] .isGeoPoint()))
	    				&& (ok[1] = (arg[1] .isGeoPoint()))
	    				&& (ok[2] = (arg[2] .isGeoPoint()))) {
	    			GeoElement[] ret =
	    			{
	    					kernelA.getManager3D().Angle3D(
	    							c.getLabel(),
	    							(GeoPointND) arg[0],
	    							(GeoPointND) arg[1],
	    							(GeoPointND) arg[2])};
	    			return ret;
	    		}
	    	
	    	}
	    	
	    	
	    	break;
	    }
	    
	    return super.process(c);
	}
	
}
