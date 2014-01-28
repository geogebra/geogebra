package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdMirror;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.Transformable;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPlaneND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;


/**
 * Mirror at 3D point or 3D line
 * @author mathieu
 *
 */
public class CmdMirror3D extends CmdMirror {
	
	
	
	/**
	 * constructor
	 * @param kernel kernel
	 */
	public CmdMirror3D(Kernel kernel) {
		super(kernel);
	}

	
	

	@Override
	public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		GeoElement[] ret = new GeoElement[1];

		switch (n) {
		case 2:
			arg = resArgs(c);

			//check if there is a 3D geo
			if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()){

				// translate object
				if (arg[0] instanceof Transformable){
					if(arg[1].isGeoPoint()) {				
						ret = kernelA.getManager3D().Mirror3D(label, arg[0], (GeoPointND) arg[1]);
						return ret;
					}
					if(arg[1].isGeoLine()) {				
						ret = kernelA.getManager3D().Mirror3D(label, arg[0], (GeoLineND) arg[1]);
						return ret;
					}					
					if(arg[1].isGeoPlane()) {				
						ret = kernelA.getManager3D().Mirror3D(label, arg[0], (GeoPlaneND) arg[1]);
						return ret;
					}					
					ok[1] = false;
				}else{
					ok[0] = false;
				}
			}
			break;
		}
		
	    return super.process(c);
	}
	
}
