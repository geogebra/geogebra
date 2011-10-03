package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.CmdPolygon;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Prism[ <GeoPoint3D>, <GeoPoint3D>, <GeoPoint3D>, ... ] 
 */
public class CmdPrism extends CommandProcessor {
	

	public CmdPrism(Kernel kernel) {
		super(kernel);
		
		
	}
	
	
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);


		if(n==2){		
			if ((ok[0] = (arg[0] .isGeoPolygon()))
					&& (ok[1] = (arg[1] .isGeoPoint()))) {
				return kernel.getManager3D().Prism(
								c.getLabels(),
								(GeoPolygon) arg[0],
								(GeoPointND) arg[1]);
			} else if ((ok[0] = (arg[0] .isGeoPolygon()))
					&& (ok[1] = (arg[1] .isNumberValue()))) {
				return kernel.getManager3D().Prism(
								c.getLabels(),
								(GeoPolygon) arg[0],
								(NumberValue) arg[1]);		
			} else {
                if (!ok[0])
                    throw argErr(app, "Prism", arg[0]);
                else
                    throw argErr(app, "Prism", arg[1]);
            }

		}else if (n>2){

			// polygon for given points
			GeoPointND[] points = new GeoPointND[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint()))
					throw argErr(app, c.getName(), arg[i]);
				else {
					points[i] = (GeoPointND) arg[i];
				}
			}
			// everything ok
			return kernel.getManager3D().Prism(c.getLabels(), points);

		}else{
			throw argNumErr(app, "Prism", n);
		}

	}

}
