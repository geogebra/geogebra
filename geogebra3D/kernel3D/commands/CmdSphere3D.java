package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.MyError;

public class CmdSphere3D extends CommandProcessor {
	
	
	
	public CmdSphere3D(Kernel kernel) {
		super(kernel);
	}

	
	

	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    switch (n) {
	    case 2 :
	    	arg = resArgs(c);
	    	if ((ok[0] = ( arg[0] .isGeoPoint() ))
	    			&& (ok[1] = ( arg[1] .isNumberValue() ))) {
	    		GeoElement[] ret =
	    		{
	    				kernel.getManager3D().Sphere(
	    						c.getLabel(),
	    						(GeoPointND) arg[0],
	    						(NumberValue) arg[1])};
	    		return ret;
	    	} else if ((ok[0] = ( arg[0] .isGeoPoint() ))
	    			&& (ok[1] = ( arg[1] .isGeoPoint() ))) {
	    		GeoElement[] ret =
	    		{
	    				kernel.getManager3D().Sphere(
	    						c.getLabel(),
	    						(GeoPointND) arg[0],
	    						(GeoPointND) arg[1])};
	    		return ret;	    		
	    	} else {
	    		if (!ok[0])
	    			throw argErr(app, "Sphere", arg[0]);
	    		else
	    			throw argErr(app, "Sphere", arg[1]);
	    	}
	    	
	    	
	    default :
	    	throw argNumErr(app, "Sphere", n);

	    }
	    
	    
	}
	
}
