package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdCorner;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra3D.kernel3D.GeoConicSection;

/**
 * 
 * Corner[ <Conic section> ]
 * 
 * @author mathieu
 *
 */
public class CmdCorner3D extends CmdCorner {
	
	
	
	
	public CmdCorner3D(Kernel kernel) {
		super(kernel);
	}

	
	

	@Override
	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    GeoElement[] arg;
	    
	    if (n==1){
	    	arg = resArgs(c);
			if (arg[0] instanceof GeoConicSection) {
				
				return kernelA.getManager3D().Corner(c.getLabels(), (GeoConicSection) arg[0]);
			}
	    }

	    return super.process(c);
	}
	
}
