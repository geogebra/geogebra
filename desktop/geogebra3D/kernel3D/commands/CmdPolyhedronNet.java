package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra3D.kernel3D.GeoPolyhedron;


public class CmdPolyhedronNet extends CommandProcessor {
	
	
	
	
	public CmdPolyhedronNet(Kernel kernel) {
		super(kernel);
	}

	
	

	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    switch (n) {	    
	    case 2 :
	    	arg = resArgs(c);
	    	if (
	    			(ok[0] = (arg[0] instanceof GeoPolyhedron ) )
	    			&& (ok[1] = (arg[1].isNumberValue() ))
	    	) {
	    		return kernelA.getManager3D().PolyhedronNet(
	    						c.getLabels(),
	    						(GeoPolyhedron) arg[0],
	    						(NumberValue) arg[1]);
	    	}
	    	
	    	
			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);

	  

	    default :
	    	throw argNumErr(app, c.getName(), n);
	    }
	    

	}
	
}
