package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.HasVolume;
import geogebra.common.main.MyError;

/**
 * Volume[ Sphere ], etc.
 * @author mathieu
 *
 */
public class CmdVolume extends CommandProcessor {
	
	
	
	
	public CmdVolume(Kernel kernel) {
		super(kernel);
	}

	
	

	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    GeoElement[] arg;

	    switch (n) {	
	    case 1 :
	    	arg = resArgs(c);

	    	if (arg[0] instanceof HasVolume){
	    		return new GeoElement[] {kernelA.getManager3D().Volume(
	    				c.getLabel(),(HasVolume) arg[0])};
	    	}

	    	throw argErr(app,"Volume",arg[0]);

	    
	    default :
	    	throw argNumErr(app,"Volume",n);
	    }
	    

	}
	
	
	
}
