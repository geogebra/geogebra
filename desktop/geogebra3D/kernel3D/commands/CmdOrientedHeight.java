package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.HasHeight;
import geogebra.common.main.MyError;

/**
 * OrientedHeight[ Cone ], etc.
 * @author mathieu
 *
 */
public class CmdOrientedHeight extends CommandProcessor {
	
	
	
	
	public CmdOrientedHeight(Kernel kernel) {
		super(kernel);
	}

	
	

	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    GeoElement[] arg;

	    switch (n) {	
	    case 1 :
	    	arg = resArgs(c);

	    	if (arg[0] instanceof HasHeight){
	    		return new GeoElement[] {kernelA.getManager3D().OrientedHeight(
	    				c.getLabel(),(HasHeight) arg[0])};
	    	}

	    	throw argErr(app,"OrientedHeight",arg[0]);

	    
	    default :
	    	throw argNumErr(app,"OrientedHeight",n);
	    }
	    

	}
	
	
	
}
