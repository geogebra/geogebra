package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdLength;
import geogebra3D.kernel3D.GeoVector3D;

public class CmdLength3D extends CmdLength {
	
	
	
	
	public CmdLength3D(Kernel kernel) {
		super(kernel);
	}

	
	

	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    //boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    switch (n) {
	    case 1 :
	    	arg = resArgs(c);
	    	if (arg[0].isGeoElement3D()){
	    		if (arg[0].isGeoVector()) {
	    			GeoElement[] ret = { kernel.getManager3D().Length(c.getLabel(),
							(GeoVector3D) arg[0]) };
					return ret;
	    		}
	    	}
	    	break;	    
	    }
	    
	    
	    
	    return super.process(c);
	}
	
}
