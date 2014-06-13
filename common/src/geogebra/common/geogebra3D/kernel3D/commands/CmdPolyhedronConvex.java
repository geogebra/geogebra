package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;


public class CmdPolyhedronConvex extends CommandProcessor {




	public CmdPolyhedronConvex(Kernel kernel) {
		super(kernel);
	}




	public GeoElement[] process(Command c) throws MyError {

		int n = c.getArgumentNumber();
		
		if (n < 4){
			throw argNumErr(app, c.getName(), n);
		}
		
		GeoElement[] arg;


		arg = resArgs(c);

		for (int i = 0 ; i < n ; i++){
			if (!arg[i].isGeoPoint()){
				throw argErr(app, c.getName(), arg[i]);
			}
		}



		return kernelA.getManager3D().PolyhedronConvex(c.getLabels(), arg);




	}

}
