package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdMidpoint;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.main.MyError;



public class CmdMidpoint3D extends CmdMidpoint {
	
	public CmdMidpoint3D(Kernel kernel) {
		super(kernel);
		
		
	}
	
	
	@Override
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;



		switch (n) {
		
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoElement3D()){
				if (arg[0].isGeoSegment()) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D().Midpoint(c.getLabel(),
							(GeoSegmentND) arg[0]) };
					return ret;
				} 
			}
			break;
			
		case 2:
			arg = resArgs(c);
			if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D() ){
				if ((ok[0] = (arg[0].isGeoPoint()))
						&& (ok[1] = (arg[1].isGeoPoint()))) {
					GeoElement[] ret = { (GeoElement) kernelA.getManager3D().Midpoint(c.getLabel(),
							(GeoPointND) arg[0], (GeoPointND) arg[1]) };
					return ret;
				} 
			}
			break;

		default :

		}


		return super.process(c);
	}

}
